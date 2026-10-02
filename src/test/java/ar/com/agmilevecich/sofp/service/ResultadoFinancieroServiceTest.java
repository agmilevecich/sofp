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
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ResultadoFinancieroServiceTest {

    private EntityManager entityManager;
    private UsuarioContexto contexto;
    private Categoria categoria;
    private Cuenta cuenta;
    private Cuenta cuentaDestino;
    private Cuenta tarjeta;
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
        tarjeta = contexto.tarjeta;
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
    void deberiaMantenerSeparadosLosResultadosDeDistintasMonedas() {
        Moneda usd = new Moneda(
                "USD", "Dólar estadounidense", 2, TipoMoneda.FIAT
        );
        Cuenta cuentaUsd = new Cuenta(
                "Caja USD",
                TipoCuenta.CAJA_AHORRO,
                contexto.perfil,
                contexto.institucion,
                usd
        );
        Categoria categoriaUsd = new Categoria("General USD", contexto.perfil);

        entityManager.getTransaction().begin();
        entityManager.persist(usd);
        entityManager.persist(cuentaUsd);
        entityManager.persist(categoriaUsd);
        entityManager.persist(new Movimiento(
                cuentaUsd,
                categoriaUsd,
                TipoMovimiento.INGRESO,
                new BigDecimal("100.00"),
                LocalDateTime.of(2026, 9, 10, 11, 0),
                "Ingreso USD"
        ));
        entityManager.persist(new Movimiento(
                cuentaUsd,
                categoriaUsd,
                TipoMovimiento.EGRESO,
                new BigDecimal("25.00"),
                LocalDateTime.of(2026, 9, 11, 11, 0),
                "Gasto USD"
        ));
        entityManager.getTransaction().commit();

        persistirMovimiento(cuenta, TipoMovimiento.INGRESO, "100000.00", "Ingreso ARS");

        ResumenResultadoFinanciero resumen = resultadoService.calcular(
                contexto.perfil,
                contexto.usuario.getId(),
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30)
        );

        assertEquals(new BigDecimal("100000.00"), resumen.getIngresos(contexto.ars));
        assertEquals(new BigDecimal("100000.00"), resumen.getResultado(contexto.ars));
        assertEquals(new BigDecimal("100.00"), resumen.getIngresos(usd));
        assertEquals(new BigDecimal("25.00"), resumen.getEgresos(usd));
        assertEquals(new BigDecimal("75.00"), resumen.getResultado(usd));
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
        Movimiento compra = gastoService.registrar(
                tarjeta,
                categoria,
                contexto.ars,
                new BigDecimal("50000.00"),
                LocalDateTime.of(2026, 9, 9, 10, 0),
                "Compra con tarjeta",
                FormaPago.TARJETA_CREDITO,
                contexto.usuario.getId(),
                1
        );
        Obligacion obligacion = new ObligacionService(entityManager, new ObligacionRepository(entityManager))
                .buscarPorMovimientoOrigen(compra.getId())
                .orElseThrow();

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
    void deberiaIncluirLosLimitesDelPeriodoYExcluirMovimientosExternos() {
        entityManager.getTransaction().begin();
        entityManager.persist(new Movimiento(
                cuenta, categoria, TipoMovimiento.INGRESO, new BigDecimal("1000.00"),
                LocalDateTime.of(2026, 9, 1, 0, 0), "Inicio"
        ));
        entityManager.persist(new Movimiento(
                cuenta, categoria, TipoMovimiento.EGRESO, new BigDecimal("200.00"),
                LocalDateTime.of(2026, 9, 30, 23, 59), "Fin"
        ));
        entityManager.persist(new Movimiento(
                cuenta, categoria, TipoMovimiento.INGRESO, new BigDecimal("9000.00"),
                LocalDateTime.of(2026, 8, 31, 23, 59), "Antes"
        ));
        entityManager.persist(new Movimiento(
                cuenta, categoria, TipoMovimiento.INGRESO, new BigDecimal("8000.00"),
                LocalDateTime.of(2026, 10, 1, 0, 0), "Después"
        ));
        entityManager.getTransaction().commit();

        ResumenResultadoFinanciero resumen = resultadoService.calcular(
                contexto.perfil,
                contexto.usuario.getId(),
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30)
        );

        assertEquals(new BigDecimal("1000.00"), resumen.getIngresos(contexto.ars));
        assertEquals(new BigDecimal("200.00"), resumen.getEgresos(contexto.ars));
        assertEquals(new BigDecimal("800.00"), resumen.getResultado(contexto.ars));
    }

    @Test
    void deberiaExcluirMovimientosDeOtroUsuario() {
        var institucionOtro = new InstitucionFinanciera(
                "Banco Otro",
                TipoInstitucionFinanciera.BANCO,
                entityManager.find(ar.com.agmilevecich.sofp.domain.Usuario.class, contexto.otroUsuarioId)
        );
        var cuentaOtro = new Cuenta(
                "Caja Otro",
                TipoCuenta.CAJA_AHORRO,
                entityManager.createQuery("SELECT p FROM PerfilFinanciero p WHERE p.usuario.id = :usuarioId", PerfilFinanciero.class)
                        .setParameter("usuarioId", contexto.otroUsuarioId)
                        .getSingleResult(),
                institucionOtro,
                contexto.ars
        );
        var categoriaOtro = new Categoria("General Otro", cuentaOtro.getPerfilFinanciero());

        entityManager.getTransaction().begin();
        entityManager.persist(institucionOtro);
        entityManager.persist(cuentaOtro);
        entityManager.persist(categoriaOtro);
        entityManager.persist(new Movimiento(
                cuentaOtro, categoriaOtro, TipoMovimiento.INGRESO,
                new BigDecimal("99999.00"),
                LocalDateTime.of(2026, 9, 15, 10, 0),
                "Ingreso de otro usuario"
        ));
        entityManager.getTransaction().commit();

        ResumenResultadoFinanciero resumen = resultadoService.calcular(
                contexto.perfil,
                contexto.usuario.getId(),
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30)
        );

        assertEquals(new BigDecimal("0.00"), resumen.getIngresos(contexto.ars));
        assertEquals(new BigDecimal("0.00"), resumen.getEgresos(contexto.ars));
        assertEquals(new BigDecimal("0.00"), resumen.getResultado(contexto.ars));
    }

    @Test
    void deberiaExcluirPagoDeTarjetaRealizadoEnOtroPeriodo() {
        Movimiento compra = gastoService.registrar(
                tarjeta,
                categoria,
                contexto.ars,
                new BigDecimal("50000.00"),
                LocalDateTime.of(2026, 9, 30, 10, 0),
                "Compra con tarjeta",
                FormaPago.TARJETA_CREDITO,
                contexto.usuario.getId(),
                1
        );
        Obligacion obligacion = new ObligacionService(entityManager, new ObligacionRepository(entityManager))
                .buscarPorMovimientoOrigen(compra.getId())
                .orElseThrow();

        pagoTarjetaService.registrarPago(
                obligacion.getId(),
                cuenta,
                categoria,
                new BigDecimal("20000.00"),
                LocalDateTime.of(2026, 10, 1, 10, 0),
                "Pago tarjeta en octubre",
                contexto.usuario.getId()
        );

        ResumenResultadoFinanciero septiembre = resultadoService.calcular(
                contexto.perfil,
                contexto.usuario.getId(),
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30)
        );
        ResumenResultadoFinanciero octubre = resultadoService.calcular(
                contexto.perfil,
                contexto.usuario.getId(),
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 31)
        );

        assertEquals(new BigDecimal("50000.00"), septiembre.getEgresos(contexto.ars));
        assertEquals(new BigDecimal("-50000.00"), septiembre.getResultado(contexto.ars));
        assertEquals(new BigDecimal("0.00"), octubre.getIngresos(contexto.ars));
        assertEquals(new BigDecimal("0.00"), octubre.getEgresos(contexto.ars));
        assertEquals(new BigDecimal("0.00"), octubre.getResultado(contexto.ars));
    }

    @Test
    void deberiaExcluirReversionDePagoCuandoCaeEnOtroPeriodo() {
        Movimiento compra = gastoService.registrar(
                tarjeta,
                categoria,
                contexto.ars,
                new BigDecimal("50000.00"),
                LocalDateTime.of(2026, 9, 30, 10, 0),
                "Compra con tarjeta",
                FormaPago.TARJETA_CREDITO,
                contexto.usuario.getId(),
                1
        );
        Obligacion obligacion = new ObligacionService(entityManager, new ObligacionRepository(entityManager))
                .buscarPorMovimientoOrigen(compra.getId())
                .orElseThrow();

        pagoTarjetaService.registrarPago(
                obligacion.getId(),
                cuenta,
                categoria,
                new BigDecimal("20000.00"),
                LocalDateTime.of(2026, 9, 30, 11, 0),
                "Pago tarjeta",
                contexto.usuario.getId()
        );
        pagoTarjetaService.revertirUltimoPago(
                obligacion.getId(),
                contexto.usuario.getId(),
                LocalDateTime.of(2026, 10, 1, 10, 0)
        );

        ResumenResultadoFinanciero septiembre = resultadoService.calcular(
                contexto.perfil,
                contexto.usuario.getId(),
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30)
        );
        ResumenResultadoFinanciero octubre = resultadoService.calcular(
                contexto.perfil,
                contexto.usuario.getId(),
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 31)
        );

        assertEquals(new BigDecimal("50000.00"), septiembre.getEgresos(contexto.ars));
        assertEquals(new BigDecimal("-50000.00"), septiembre.getResultado(contexto.ars));
        assertEquals(new BigDecimal("0.00"), octubre.getIngresos(contexto.ars));
        assertEquals(new BigDecimal("0.00"), octubre.getEgresos(contexto.ars));
        assertEquals(new BigDecimal("0.00"), octubre.getResultado(contexto.ars));
    }

    @Test
    void deberiaRechazarArgumentosObligatoriosNulos() {
        LocalDate desde = LocalDate.of(2026, 9, 1);
        LocalDate hasta = LocalDate.of(2026, 9, 30);

        assertThrows(NullPointerException.class, () -> resultadoService.calcular(null, contexto.usuario.getId(), desde, hasta));
        assertThrows(NullPointerException.class, () -> resultadoService.calcular(contexto.perfil, null, desde, hasta));
        assertThrows(NullPointerException.class, () -> resultadoService.calcular(contexto.perfil, contexto.usuario.getId(), null, hasta));
        assertThrows(NullPointerException.class, () -> resultadoService.calcular(contexto.perfil, contexto.usuario.getId(), desde, null));
    }

    @Test
    void deberiaDevolverCerosCuandoElPeriodoNoTieneMovimientos() {
        ResumenResultadoFinanciero resumen = resultadoService.calcular(
                contexto.perfil,
                contexto.usuario.getId(),
                LocalDate.of(2027, 1, 1),
                LocalDate.of(2027, 1, 31)
        );

        assertEquals(new BigDecimal("0.00"), resumen.getIngresos(contexto.ars));
        assertEquals(new BigDecimal("0.00"), resumen.getEgresos(contexto.ars));
        assertEquals(new BigDecimal("0.00"), resumen.getResultado(contexto.ars));
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

    @Test
    void deberiaMantenerInmutablesLosMapasDelResumen() {
        Map<Moneda, BigDecimal> ingresos = new LinkedHashMap<>();
        Map<Moneda, BigDecimal> egresos = new LinkedHashMap<>();
        ingresos.put(contexto.ars, new BigDecimal("100.00"));
        egresos.put(contexto.ars, new BigDecimal("25.00"));

        ResumenResultadoFinanciero resumen = new ResumenResultadoFinanciero(
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30),
                ingresos,
                egresos
        );

        assertThrows(
                UnsupportedOperationException.class,
                () -> resumen.getIngresos().put(contexto.ars, new BigDecimal("999.00"))
        );
        assertThrows(
                UnsupportedOperationException.class,
                () -> resumen.getEgresos().put(contexto.ars, new BigDecimal("999.00"))
        );
    }

    @Test
    void deberiaCopiarLosMapasRecibidosPorElResumen() {
        Map<Moneda, BigDecimal> ingresos = new LinkedHashMap<>();
        Map<Moneda, BigDecimal> egresos = new LinkedHashMap<>();
        ingresos.put(contexto.ars, new BigDecimal("100.00"));
        egresos.put(contexto.ars, new BigDecimal("25.00"));

        ResumenResultadoFinanciero resumen = new ResumenResultadoFinanciero(
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30),
                ingresos,
                egresos
        );

        ingresos.put(contexto.ars, new BigDecimal("999.00"));
        egresos.clear();

        assertEquals(new BigDecimal("100.00"), resumen.getIngresos(contexto.ars));
        assertEquals(new BigDecimal("25.00"), resumen.getEgresos(contexto.ars));
    }

    @Test
    void deberiaRechazarDatosInvalidosEnElResumen() {
        Map<Moneda, BigDecimal> ingresos = new LinkedHashMap<>();
        Map<Moneda, BigDecimal> egresos = new LinkedHashMap<>();

        assertThrows(
                NullPointerException.class,
                () -> new ResumenResultadoFinanciero(
                        null,
                        LocalDate.of(2026, 9, 30),
                        ingresos,
                        egresos
                )
        );
        assertThrows(
                NullPointerException.class,
                () -> new ResumenResultadoFinanciero(
                        LocalDate.of(2026, 9, 1),
                        LocalDate.of(2026, 9, 30),
                        null,
                        egresos
                )
        );
        assertThrows(
                NullPointerException.class,
                () -> new ResumenResultadoFinanciero(
                        LocalDate.of(2026, 9, 1),
                        LocalDate.of(2026, 9, 30),
                        ingresos,
                        null
                )
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> new ResumenResultadoFinanciero(
                        LocalDate.of(2026, 9, 30),
                        LocalDate.of(2026, 9, 1),
                        ingresos,
                        egresos
                )
        );

        ingresos.put(null, new BigDecimal("10.00"));
        assertThrows(
                NullPointerException.class,
                () -> new ResumenResultadoFinanciero(
                        LocalDate.of(2026, 9, 1),
                        LocalDate.of(2026, 9, 30),
                        ingresos,
                        egresos
                )
        );
    }

    @Test
    void deberiaDevolverCeroConLaEscalaDeLaMonedaCuandoNoHayMovimiento() {
        ResumenResultadoFinanciero resumen = new ResumenResultadoFinanciero(
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30),
                Map.of(),
                Map.of()
        );

        assertEquals(new BigDecimal("0.00"), resumen.getIngresos(contexto.ars));
        assertEquals(new BigDecimal("0.00"), resumen.getEgresos(contexto.ars));
        assertEquals(new BigDecimal("0.00"), resumen.getResultado(contexto.ars));
        assertThrows(
                NullPointerException.class,
                () -> resumen.getIngresos(null)
        );
        assertThrows(
                NullPointerException.class,
                () -> resumen.getEgresos(null)
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
        var institucion = new InstitucionFinanciera("Banco Test", TipoInstitucionFinanciera.BANCO, usuario);
        var ars = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        var cuenta = new Cuenta("Caja", TipoCuenta.CAJA_AHORRO, perfil, institucion, ars);
        var cuentaDestino = new Cuenta("Caja destino", TipoCuenta.CAJA_AHORRO, perfil, institucion, ars);
        var tarjeta = new Cuenta("Visa", perfil, institucion, ars, new BigDecimal("500000.00"), 10, 25);
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
                LocalDateTime.of(2026, 8, 31, 9, 0), "Saldo inicial"
        ));
        entityManager.getTransaction().commit();

        return new UsuarioContexto(usuario, perfil, cuenta, cuentaDestino, tarjeta, categoria, ars, institucion, otroUsuario.getId());
    }

    private record UsuarioContexto(
            ar.com.agmilevecich.sofp.domain.Usuario usuario,
            PerfilFinanciero perfil,
            Cuenta cuenta,
            Cuenta cuentaDestino,
            Cuenta tarjeta,
            Categoria categoria,
            Moneda ars,
            InstitucionFinanciera institucion,
            Long otroUsuarioId
    ) {}
}
