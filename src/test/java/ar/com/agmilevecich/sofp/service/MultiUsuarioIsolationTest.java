package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Movimiento;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.ResumenPatrimonial;
import ar.com.agmilevecich.sofp.domain.ResumenResultadoFinanciero;
import ar.com.agmilevecich.sofp.domain.TipoCuenta;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMovimiento;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.CategoriaRepository;
import ar.com.agmilevecich.sofp.persistence.CuentaRepository;
import ar.com.agmilevecich.sofp.persistence.MonedaRepository;
import ar.com.agmilevecich.sofp.persistence.MovimientoActivoRepository;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import ar.com.agmilevecich.sofp.persistence.ObligacionRepository;
import ar.com.agmilevecich.sofp.persistence.PerfilFinancieroRepository;
import ar.com.agmilevecich.sofp.persistence.TipoCambioRepository;
import ar.com.agmilevecich.sofp.persistence.UsuarioRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MultiUsuarioIsolationTest {

    private EntityManager entityManager;
    private UsuarioService usuarioService;
    private PerfilFinancieroService perfilService;
    private CuentaService cuentaService;
    private CategoriaService categoriaService;
    private MovimientoService movimientoService;
    private ObligacionService obligacionService;
    private PatrimonioFinancieroService patrimonioService;
    private ResultadoFinancieroService resultadoService;

    private Usuario usuario1;
    private Usuario usuario2;
    private PerfilFinanciero perfil1;
    private PerfilFinanciero perfil2;
    private Cuenta cuenta1;
    private Cuenta cuenta2;
    private Categoria categoria1;
    private Categoria categoria2;
    private Moneda ars;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();

        usuarioService = new UsuarioService(new UsuarioRepository(entityManager), entityManager);
        perfilService = new PerfilFinancieroService(new PerfilFinancieroRepository(entityManager));

        usuario1 = usuarioService.registrar(
                "Usuario", "Uno", "multiuno." + System.nanoTime() + "@test.com",
                "secreto", "Perfil uno"
        );
        usuario2 = usuarioService.registrar(
                "Usuario", "Dos", "multidos." + System.nanoTime() + "@test.com",
                "secreto", "Perfil dos"
        );

        perfil1 = perfilService.listarPorUsuario(usuario1.getId()).get(0);
        perfil2 = perfilService.listarPorUsuario(usuario2.getId()).get(0);

        ars = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        InstitucionFinanciera institucion = new InstitucionFinanciera(
                "Banco Multiusuario " + System.nanoTime(),
                TipoInstitucionFinanciera.BANCO
        );

        entityManager.getTransaction().begin();
        entityManager.persist(ars);
        entityManager.persist(institucion);
        entityManager.getTransaction().commit();

        cuentaService = new CuentaService(
                new CuentaRepository(entityManager),
                new MovimientoRepository(entityManager),
                entityManager
        );
        categoriaService = new CategoriaService(
                entityManager,
                new CategoriaRepository(entityManager)
        );
        movimientoService = new MovimientoService(
                entityManager,
                new MovimientoRepository(entityManager)
        );
        obligacionService = new ObligacionService(
                entityManager,
                new ObligacionRepository(entityManager)
        );

        cuenta1 = cuentaService.registrar(
                new Cuenta("Cuenta usuario 1", TipoCuenta.CAJA_AHORRO, perfil1, institucion, ars),
                usuario1.getId()
        );
        cuenta2 = cuentaService.registrar(
                new Cuenta("Cuenta usuario 2", TipoCuenta.CAJA_AHORRO, perfil2, institucion, ars),
                usuario2.getId()
        );

        categoria1 = categoriaService.registrar(
                new Categoria("Ingresos usuario 1", perfil1),
                usuario1.getId()
        );
        categoria2 = categoriaService.registrar(
                new Categoria("Ingresos usuario 2", perfil2),
                usuario2.getId()
        );

        patrimonioService = new PatrimonioFinancieroService(
                cuentaService,
                new CarteraActivoService(new MovimientoActivoRepository(entityManager)),
                new ObligacionRepository(entityManager),
                new TipoCambioRepository(entityManager),
                new MonedaRepository(entityManager)
        );

        resultadoService = new ResultadoFinancieroService(
                entityManager,
                new MovimientoRepository(entityManager)
        );
    }

    @AfterEach
    void tearDown() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
        JpaTestManager.close();
    }

    @Test
    void deberiaAislarCuentasYMovimientosEntreUsuarios() {
        LocalDateTime fecha = LocalDateTime.now().minusMinutes(1);

        Movimiento movimiento1 = movimientoService.registrar(
                cuenta1, categoria1, TipoMovimiento.INGRESO,
                new BigDecimal("100.00"), fecha, "Ingreso uno", usuario1.getId()
        );
        Movimiento movimiento2 = movimientoService.registrar(
                cuenta2, categoria2, TipoMovimiento.INGRESO,
                new BigDecimal("200.00"), fecha, "Ingreso dos", usuario2.getId()
        );

        assertEquals(List.of(cuenta1.getId()),
                cuentaService.listarPorPerfilFinanciero(perfil1.getId(), usuario1.getId())
                        .stream().map(Cuenta::getId).toList());
        assertEquals(List.of(cuenta2.getId()),
                cuentaService.listarPorPerfilFinanciero(perfil2.getId(), usuario2.getId())
                        .stream().map(Cuenta::getId).toList());

        assertEquals(1, movimientoService.listarPorCuenta(cuenta1.getId(), usuario1.getId()).size());
        assertEquals(1, movimientoService.listarPorCuenta(cuenta2.getId(), usuario2.getId()).size());

        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.buscarPorId(cuenta2.getId(), usuario1.getId()));
        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.listarPorPerfilFinanciero(perfil2.getId(), usuario1.getId()));
        assertThrows(IllegalArgumentException.class,
                () -> movimientoService.buscarPorId(movimiento2.getId(), usuario1.getId()));
        assertThrows(IllegalArgumentException.class,
                () -> movimientoService.listarPorCuenta(cuenta2.getId(), usuario1.getId()));
        assertThrows(IllegalArgumentException.class,
                () -> movimientoService.buscarPorId(movimiento1.getId(), usuario2.getId()));
    }

    @Test
    void deberiaAislarObligacionesEntreUsuarios() {
        Cuenta tarjeta1 = new Cuenta(
                "Tarjeta usuario 1", perfil1, cuenta1.getInstitucionFinanciera(),
                ars, new BigDecimal("1000.00"), 20, 5
        );
        Categoria categoriaTarjeta1 = categoriaService.registrar(
                new Categoria("Consumos usuario 1", perfil1), usuario1.getId()
        );
        tarjeta1 = cuentaService.registrar(tarjeta1, usuario1.getId());

        Movimiento consumo = movimientoService.registrar(
                tarjeta1, categoriaTarjeta1, ars, TipoMovimiento.EGRESO,
                new BigDecimal("150.00"), LocalDateTime.now().minusMinutes(1),
                "Consumo usuario 1", ar.com.agmilevecich.sofp.domain.FormaPago.TARJETA_CREDITO,
                usuario1.getId()
        );
        var obligacion = obligacionService.registrar(consumo, 1);

        assertEquals(1, obligacionService.listarPorUsuario(usuario1.getId()).size());
        assertTrue(obligacionService.listarPorUsuario(usuario2.getId()).isEmpty());

        assertThrows(IllegalArgumentException.class,
                () -> obligacionService.registrarPago(
                        obligacion.getId(), new BigDecimal("10.00"), usuario2.getId()
                ));
        assertThrows(IllegalArgumentException.class,
                () -> obligacionService.anular(obligacion.getId(), usuario2.getId()));
    }

    @Test
    void deberiaAislarReportesPatrimonialesYDeResultado() {
        LocalDateTime fecha = LocalDateTime.now().minusMinutes(1);

        movimientoService.registrar(
                cuenta1, categoria1, TipoMovimiento.INGRESO,
                new BigDecimal("300.00"), fecha, "Ingreso patrimonio uno", usuario1.getId()
        );
        movimientoService.registrar(
                cuenta2, categoria2, TipoMovimiento.INGRESO,
                new BigDecimal("700.00"), fecha, "Ingreso patrimonio dos", usuario2.getId()
        );

        ResumenPatrimonial patrimonio1 = patrimonioService.calcular(perfil1, usuario1.getId());
        ResumenPatrimonial patrimonio2 = patrimonioService.calcular(perfil2, usuario2.getId());

        assertEquals(new BigDecimal("300.00"), patrimonio1.getActivosMonetarios());
        assertEquals(new BigDecimal("700.00"), patrimonio2.getActivosMonetarios());
        assertEquals(new BigDecimal("300.00"), patrimonio1.getPatrimonioNeto());
        assertEquals(new BigDecimal("700.00"), patrimonio2.getPatrimonioNeto());

        LocalDate hoy = fecha.toLocalDate();
        ResumenResultadoFinanciero resultado1 =
                resultadoService.calcular(perfil1, usuario1.getId(), hoy, hoy);
        ResumenResultadoFinanciero resultado2 =
                resultadoService.calcular(perfil2, usuario2.getId(), hoy, hoy);

        assertEquals(new BigDecimal("300.00"), resultado1.getIngresos(ars));
        assertEquals(new BigDecimal("700.00"), resultado2.getIngresos(ars));
        assertThrows(IllegalArgumentException.class,
                () -> patrimonioService.calcular(perfil2, usuario1.getId()));
        assertThrows(IllegalArgumentException.class,
                () -> resultadoService.calcular(perfil2, usuario1.getId(), hoy, hoy));
    }
}
