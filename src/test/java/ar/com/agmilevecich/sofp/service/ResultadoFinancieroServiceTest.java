package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Activo;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.FormaPago;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.Movimiento;
import ar.com.agmilevecich.sofp.domain.Obligacion;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.ResumenResultadoFinanciero;
import ar.com.agmilevecich.sofp.domain.TipoCuenta;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMovimiento;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.persistence.MovimientoActivoRepository;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import ar.com.agmilevecich.sofp.persistence.ObligacionRepository;
import ar.com.agmilevecich.sofp.persistence.OperacionFinancieraRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ResultadoFinancieroServiceTest {

    private EntityManager entityManager;
    private UsuarioContexto contexto;
    private Categoria categoria;
    private Cuenta cuenta;
    private Cuenta cuentaDestino;
    private ResultadoFinancieroService resultadoService;
    private GastoService gastoService;
    private PagoTarjetaService pagoTarjetaService;
    private CuentaService cuentaService;
    private OperacionFinancieraService operacionFinancieraService;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();
        MovimientoRepository movimientoRepository = new MovimientoRepository(entityManager);
        ObligacionRepository obligacionRepository = new ObligacionRepository(entityManager);
        resultadoService = new ResultadoFinancieroService(entityManager, movimientoRepository);
        MovimientoService movimientoService = new MovimientoService(entityManager, movimientoRepository, obligacionRepository);
        gastoService = new GastoService(movimientoService, new ObligacionService(entityManager, obligacionRepository));
        pagoTarjetaService = new PagoTarjetaService(entityManager, movimientoRepository, obligacionRepository);
        cuentaService = new CuentaService(
                new ar.com.agmilevecich.sofp.persistence.CuentaRepository(entityManager),
                movimientoRepository,
                obligacionRepository,
                entityManager
        );
        operacionFinancieraService = new OperacionFinancieraService(
                entityManager,
                movimientoRepository,
                new MovimientoActivoRepository(entityManager),
                new OperacionFinancieraRepository(entityManager)
        );

        contexto = crearContexto();
        categoria = contexto.categoria;
        cuenta = contexto.cuenta;
        cuentaDestino = contexto.cuentaDestino;
    }

    @AfterEach
    void tearDown() {
        if (entityManager != null && entityManager.isOpen()) {
            if (entityManager.getTransaction().isActive()) entityManager.getTransaction().rollback();
            entityManager.close();
        }
        JpaTestManager.close();
    }

    @Test
    void deberiaCalcularIngresosEgresosYResultadoPorMoneda() {
        persistirMovimiento(cuenta, TipoMovimiento.INGRESO, "100000.00", "Ingreso");
        persistirMovimiento(cuenta, TipoMovimiento.EGRESO, "35000.00", "Gasto");

        ResumenResultadoFinanciero resumen = resultadoService.calcular(
                contexto.perfil,
                contexto.usuario.getId(),
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30)
        );

        assertEquals(new BigDecimal("100000.00"), resumen.getIngresos(contexto.ars));
        assertEquals(new BigDecimal("35000.00"), resumen.getEgresos(contexto.ars));
        assertEquals(new BigDecimal("65000.00"), resumen.getResultado(contexto.ars));
    }

    @Test
    void deberiaExcluirTransferenciasYComprasDeActivosDelResultado() {
        persistirMovimiento(cuenta, TipoMovimiento.INGRESO, "100000.00", "Ingreso");

        operacionFinancieraService.transferir(
                contexto.usuario.getId(), cuenta, cuentaDestino,
                categoria, categoria, new BigDecimal("20000.00"),
                LocalDateTime.of(2026, 9, 10, 10, 0), "Transferencia"
        );

        Activo activo = new Activo("Bono test", "BONO-RESULTADO", contexto.ars);
        entityManager.getTransaction().begin();
        entityManager.persist(activo);
        entityManager.getTransaction().commit();

        operacionFinancieraService.comprarActivo(
                contexto.usuario.getId(), cuenta, categoria, activo,
                new BigDecimal("10"), new BigDecimal("1000.00"),
                LocalDateTime.of(2026, 9, 11, 10, 0), "Compra activo"
        );

        ResumenResultadoFinanciero resumen = resultadoService.calcular(
                contexto.perfil,
                contexto.usuario.getId(),
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30)
        );

        assertEquals(new BigDecimal("100000.00"), resumen.getIngresos(contexto.ars));
        assertEquals(new BigDecimal("0.00"), resumen.getEgresos(contexto.ars));
        assertEquals(new BigDecimal("100000.00"), resumen.getResultado(contexto.ars));
    }

    @Test
    void deberiaIncluirCompraConTarjetaPeroExcluirPagoYReversion() {
        Obligacion obligacion = gastoService.registrar(
                cuentaDestino,
                categoria,
                contexto.ars,
                new BigDecimal("50000.00"),
                LocalDateTime.of(2026, 9, 9, 10, 0),
                "Compra con tarjeta",
                FormaPago.TARJETA_CREDITO,
                contexto.usuario.getId(),
                1
        ).getOperacionFinanciera() == null
                ? new ObligacionService(entityManager, new ObligacionRepository(entityManager))
                    .buscarPorMovimientoOrigen(ultimoMovimientoId())
                    .orElseThrow()
                : null;

        pagoTarjetaService.registrarPago(
                obligacion.getId(),
                cuenta,
                categoria,
                new BigDecimal("20000.00"),
                LocalDateTime.of(2026, 9, 10, 10, 0),
                "Pago tarjeta",
                contexto.usuario.getId()
        );

        pagoTarjetaService.revertirUltimoPago(
                obligacion.getId(),
                contexto.usuario.getId(),
                LocalDateTime.of(2026, 9, 11, 10, 0)
        );

        ResumenResultadoFinanciero resumen = resultadoService.calcular(
                contexto.perfil,
                contexto.usuario.getId(),
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30)
        );

        assertEquals(new BigDecimal("0.00"), resumen.getIngresos(contexto.ars));
        assertEquals(new BigDecimal("50000.00"), resumen.getEgresos(contexto.ars));
        assertEquals(new BigDecimal("-50000.00"), resumen.getResultado(contexto.ars));
        assertEquals(new BigDecimal("200000.00"), cuentaService.calcularSaldo(cuenta.getId(), contexto.usuario.getId()));
    }

    @Test
    void deberiaRechazarPerfilDeOtroUsuario() {
        assertThrows(
                IllegalArgumentException.class,
                () -> resultadoService.calcular(
                        contexto.perfil,
                        contexto.otroUsuarioId,
                        LocalDate.of(2026, 9, 1),
                        LocalDate.of(2026, 9, 30)
                )
        );
    }

    @Test
    void deberiaRechazarRangoInvertido() {
        assertThrows(
                IllegalArgumentException.class,
                () -> resultadoService.calcular(
                        contexto.perfil,
                        contexto.usuario.getId(),
                        LocalDate.of(2026, 9, 30),
                        LocalDate.of(2026, 9, 1)
                )
        );
    }

    private void persistirMovimiento(Cuenta cuenta, TipoMovimiento tipo, String importe, String descripcion) {
        entityManager.getTransaction().begin();
        entityManager.persist(new Movimiento(
                cuenta, categoria, tipo, new BigDecimal(importe),
                LocalDateTime.of(2026, 9, 10, 10, 0), descripcion
        ));
        entityManager.getTransaction().commit();
    }

    private Long ultimoMovimientoId() {
        return entityManager.createQuery(
                "SELECT m.id FROM Movimiento m ORDER BY m.id DESC", Long.class
        ).setMaxResults(1).getSingleResult();
    }

    private UsuarioContexto crearContexto() {
        var usuario = new ar.com.agmilevecich.sofp.domain.Usuario(
                "Juan", "Pérez", "resultado." + System.nanoTime() + "@test.com", "hash"
        );
        var otroUsuario = new ar.com.agmilevecich.sofp.domain.Usuario(
                "Ana", "Gómez", "resultado.otro." + System.nanoTime() + "@test.com", "hash"
        );
        var perfil = new PerfilFinanciero("Perfil principal", usuario);
        usuario.agregarPerfilFinanciero(perfil);
        var otroPerfil = new PerfilFinanciero("Otro perfil", otroUsuario);
        otroUsuario.agregarPerfilFinanciero(otroPerfil);
        var institucion = new InstitucionFinanciera("Banco Test", TipoInstitucionFinanciera.BANCO);
        var ars = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        var cuenta = new Cuenta("Caja", TipoCuenta.CAJA_AHORRO, perfil, institucion, ars);
        var cuentaDestino = new Cuenta("Caja destino", TipoCuenta.CAJA_AHORRO, perfil, institucion, ars);
        var tarjeta = new Cuenta("Visa", TipoCuenta.TARJETA_CREDITO, perfil, institucion, ars, new BigDecimal("500000.00"), 10, 25);
        var categoria = new Categoria("General", perfil);
        var categoriaTarjeta = new Categoria("Tarjeta", perfil);

        entityManager.getTransaction().begin();
        entityManager.persist(usuario);
        entityManager.persist(otroUsuario);
        entityManager.persist(perfil);
        entityManager.persist(otroPerfil);
        entityManager.persist(institucion);
        entityManager.persist(ars);
        entityManager.persist(cuenta);
        entityManager.persist(cuentaDestino);
        entityManager.persist(tarjeta);
        entityManager.persist(categoria);
        entityManager.persist(categoriaTarjeta);
        entityManager.persist(new Movimiento(
                cuenta, categoria, TipoMovimiento.INGRESO, new BigDecimal("200000.00"),
                LocalDateTime.of(2026, 9, 1, 9, 0), "Saldo inicial"
        ));
        entityManager.getTransaction().commit();

        return new UsuarioContexto(usuario, perfil, cuenta, cuentaDestino, categoria, ars, otroUsuario.getId());
    }

    private record UsuarioContexto(
            ar.com.agmilevecich.sofp.domain.Usuario usuario,
            PerfilFinanciero perfil,
            Cuenta cuenta,
            Cuenta cuentaDestino,
            Categoria categoria,
            Moneda ars,
            Long otroUsuarioId
    ) {}
}
