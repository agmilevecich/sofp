package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Activo;
import ar.com.agmilevecich.sofp.domain.Bono;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.MovimientoActivo;
import ar.com.agmilevecich.sofp.domain.OperacionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMovimientoActivo;
import ar.com.agmilevecich.sofp.domain.TipoOperacionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoCambio;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.FormaPago;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.ResumenPatrimonial;
import ar.com.agmilevecich.sofp.domain.Refinanciacion;
import ar.com.agmilevecich.sofp.domain.TipoCuenta;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.domain.TipoMovimiento;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.CuentaRepository;
import ar.com.agmilevecich.sofp.persistence.MonedaRepository;
import ar.com.agmilevecich.sofp.persistence.MovimientoActivoRepository;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import ar.com.agmilevecich.sofp.persistence.ObligacionRepository;
import ar.com.agmilevecich.sofp.persistence.TipoCambioRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PatrimonioFinancieroServiceTest {

    private EntityManager entityManager;
    private Usuario usuario;
    private PerfilFinanciero perfil;
    private Cuenta cuenta;
    private Categoria categoria;
    private Moneda ars;
    private MovimientoService movimientoService;
    private GastoService gastoService;
    private ObligacionService obligacionService;
    private PatrimonioFinancieroService patrimonioService;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();

        movimientoService = new MovimientoService(
                entityManager,
                new MovimientoRepository(entityManager)
        );
        obligacionService = new ObligacionService(
                entityManager,
                new ObligacionRepository(entityManager)
        );
        gastoService = new GastoService(movimientoService, obligacionService);

        patrimonioService = new PatrimonioFinancieroService(
                new CuentaService(
                        new CuentaRepository(entityManager),
                        new MovimientoRepository(entityManager),
                        entityManager
                ),
                new CarteraActivoService(
                        new MovimientoActivoRepository(entityManager)
                ),
                new ObligacionRepository(entityManager),
                new TipoCambioRepository(entityManager),
                new MonedaRepository(entityManager)
        );

        usuario = new Usuario(
                "Ariel",
                "Patrimonio",
                "patrimonio." + System.nanoTime() + "@test.com",
                "hash"
        );
        perfil = new PerfilFinanciero("Perfil patrimonial", usuario);
        usuario.agregarPerfilFinanciero(perfil);

        InstitucionFinanciera institucion = new InstitucionFinanciera(
                "Banco Patrimonio",
                TipoInstitucionFinanciera.BANCO
        );

        ars = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        cuenta = new Cuenta(
                "Cuenta principal",
                TipoCuenta.CAJA_AHORRO,
                perfil,
                institucion,
                ars
        );
        categoria = new Categoria("General", perfil);

        entityManager.getTransaction().begin();
        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(institucion);
        entityManager.persist(ars);
        entityManager.persist(cuenta);
        entityManager.persist(categoria);
        entityManager.getTransaction().commit();
    }

    @AfterEach
    void tearDown() {
        if (entityManager != null && entityManager.isOpen()) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            entityManager.close();
        }
        JpaTestManager.close();
    }

    @Test
    void deberiaCalcularPatrimonioConSaldoMonetario() {
        movimientoService.registrar(
                cuenta,
                categoria,
                TipoMovimiento.INGRESO,
                new BigDecimal("100000.00"),
                LocalDateTime.of(2026, 9, 25, 10, 0),
                "Saldo inicial",
                usuario.getId()
        );

        ResumenPatrimonial resumen = patrimonioService.calcular(
                perfil,
                usuario.getId(),
                Map.of()
        );

        assertEquals(new BigDecimal("100000.00"), resumen.getActivosMonetarios());
        assertEquals(new BigDecimal("100000.00"), resumen.getActivosTotales());
        assertEquals(BigDecimal.ZERO, resumen.getPasivosTotales());
        assertEquals(new BigDecimal("100000.00"), resumen.getPatrimonioNeto());
    }

    @Test
    void deberiaDescontarDeudaDeTarjetaSinContarLaTarjetaComoActivo() {
        movimientoService.registrar(
                cuenta,
                categoria,
                TipoMovimiento.INGRESO,
                new BigDecimal("100000.00"),
                LocalDateTime.of(2026, 9, 25, 10, 0),
                "Saldo inicial",
                usuario.getId()
        );

        Cuenta tarjeta = new Cuenta(
                "Visa Patrimonio",
                perfil,
                entityManager.createQuery(
                        "SELECT i FROM InstitucionFinanciera i",
                        InstitucionFinanciera.class
                ).setMaxResults(1).getSingleResult(),
                ars,
                new BigDecimal("500000.00"),
                10,
                25
        );

        entityManager.getTransaction().begin();
        entityManager.persist(tarjeta);
        entityManager.getTransaction().commit();

        movimientoService.registrar(
                tarjeta,
                categoria,
                TipoMovimiento.EGRESO,
                new BigDecimal("30000.00"),
                LocalDateTime.of(2026, 9, 25, 11, 0),
                "Compra con tarjeta",
                FormaPago.TARJETA_CREDITO,
                usuario.getId()
        );

        ResumenPatrimonial resumen = patrimonioService.calcular(
                perfil,
                usuario.getId(),
                Map.of()
        );

        assertEquals(new BigDecimal("100000.00"), resumen.getActivosMonetarios());
        assertEquals(new BigDecimal("30000.00"), resumen.getPasivosTarjetas());
        assertEquals(new BigDecimal("100000.00"), resumen.getActivosTotales());
        assertEquals(new BigDecimal("70000.00"), resumen.getPatrimonioNeto());
    }

    @Test
    void deberiaConvertirElTotalDeInversionesAntesDeRedondear() {
        Moneda usd = new Moneda("USD", "Dólar estadounidense", 2, TipoMoneda.FIAT);
        InstitucionFinanciera institucion = new InstitucionFinanciera(
                "Broker Patrimonio",
                TipoInstitucionFinanciera.BANCO
        );
        Bono activo1 = new Bono("Bono USD 1", "USD1", usd);
        Bono activo2 = new Bono("Bono USD 2", "USD2", usd);
        TipoCambio cambio = new TipoCambio(
                usd,
                ars,
                new BigDecimal("100.0049"),
                LocalDateTime.of(2026, 9, 25, 12, 0),
                "Test"
        );

        Cuenta cuentaBroker = new Cuenta(
                "Cuenta broker",
                TipoCuenta.CAJA_AHORRO,
                perfil,
                institucion,
                usd
        );

        MovimientoActivo compra1 = new MovimientoActivo(
                activo1,
                TipoMovimientoActivo.COMPRA,
                BigDecimal.ONE,
                BigDecimal.ONE
        );
        MovimientoActivo compra2 = new MovimientoActivo(
                activo2,
                TipoMovimientoActivo.COMPRA,
                BigDecimal.ONE,
                BigDecimal.ONE
        );

        OperacionFinanciera operacion1 = new OperacionFinanciera(
                cuentaBroker,
                null,
                BigDecimal.ONE,
                TipoOperacionFinanciera.COMPRA
        );
        OperacionFinanciera operacion2 = new OperacionFinanciera(
                cuentaBroker,
                null,
                BigDecimal.ONE,
                TipoOperacionFinanciera.COMPRA
        );
        operacion1.agregarMovimientoActivo(compra1);
        operacion2.agregarMovimientoActivo(compra2);

        Categoria categoriaBroker = new Categoria("Inversiones USD", perfil);

        entityManager.getTransaction().begin();
        entityManager.persist(usd);
        entityManager.persist(institucion);
        entityManager.persist(activo1);
        entityManager.persist(activo2);
        entityManager.persist(cuentaBroker);
        entityManager.persist(categoriaBroker);
        entityManager.persist(cambio);
        entityManager.persist(operacion1);
        entityManager.persist(compra1);
        entityManager.persist(operacion2);
        entityManager.persist(compra2);
        entityManager.getTransaction().commit();

        movimientoService.registrar(
                cuentaBroker,
                categoriaBroker,
                TipoMovimiento.INGRESO,
                BigDecimal.ONE,
                LocalDateTime.of(2026, 9, 25, 12, 30),
                "Saldo para prueba de valorización",
                usuario.getId()
        );

        ResumenPatrimonial resumen = patrimonioService.calcular(
                perfil,
                usuario.getId(),
                Map.of(activo1, BigDecimal.ONE, activo2, BigDecimal.ONE)
        );

        assertEquals(new BigDecimal("200.01"), resumen.getActivosInversiones());
    }

    @Test
    void deberiaCalcularPatrimonioSinDatos() {
        ResumenPatrimonial resumen = patrimonioService.calcular(
                perfil,
                usuario.getId(),
                Map.of()
        );

        assertEquals(BigDecimal.ZERO, resumen.getActivosMonetarios());
        assertEquals(BigDecimal.ZERO, resumen.getActivosInversiones());
        assertEquals(BigDecimal.ZERO, resumen.getActivosTotales());
        assertEquals(BigDecimal.ZERO, resumen.getPasivosTarjetas());
        assertEquals(BigDecimal.ZERO, resumen.getPasivosTotales());
        assertEquals(BigDecimal.ZERO, resumen.getPatrimonioNeto());
    }

    @Test
    void deberiaValorarActivosMonetariosMultidivisaEnMonedaDePresentacion() {
        Moneda usd = new Moneda("USD", "Dólar estadounidense", 2, TipoMoneda.FIAT);
        InstitucionFinanciera institucionUsd = new InstitucionFinanciera(
                "Banco USD",
                TipoInstitucionFinanciera.BANCO
        );
        Cuenta cuentaUsd = new Cuenta(
                "Cuenta USD",
                TipoCuenta.CAJA_AHORRO,
                perfil,
                institucionUsd,
                usd
        );
        Categoria categoriaUsd = new Categoria("General USD", perfil);
        TipoCambio cambio = new TipoCambio(
                usd,
                ars,
                new BigDecimal("100.00"),
                LocalDateTime.of(2026, 9, 25, 13, 0),
                "Test multidivisa"
        );

        entityManager.getTransaction().begin();
        entityManager.persist(usd);
        entityManager.persist(institucionUsd);
        entityManager.persist(cuentaUsd);
        entityManager.persist(categoriaUsd);
        entityManager.persist(cambio);
        entityManager.getTransaction().commit();

        movimientoService.registrar(
                cuentaUsd,
                categoriaUsd,
                TipoMovimiento.INGRESO,
                new BigDecimal("100.00"),
                LocalDateTime.of(2026, 9, 25, 13, 30),
                "Saldo USD",
                usuario.getId()
        );

        ResumenPatrimonial resumen = patrimonioService.calcular(
                perfil,
                usuario.getId(),
                Map.of()
        );

        assertEquals(new BigDecimal("10000.00"), resumen.getActivosMonetarios());
        assertEquals(new BigDecimal("10000.00"), resumen.getActivosTotales());
        assertEquals(new BigDecimal("10000.00"), resumen.getPatrimonioNeto());
    }

    @Test
    void deberiaIncluirRefinanciacionComoPasivoSinContarLaObligacionOrigen() {
        Cuenta tarjeta = new Cuenta(
                "Visa Refinanciada",
                perfil,
                entityManager.createQuery(
                        "SELECT i FROM InstitucionFinanciera i",
                        InstitucionFinanciera.class
                ).setMaxResults(1).getSingleResult(),
                ars,
                new BigDecimal("500000.00"),
                10,
                25
        );

        Categoria categoriaTarjeta = new Categoria("Refinanciacion", perfil);
        entityManager.getTransaction().begin();
        entityManager.persist(tarjeta);
        entityManager.persist(categoriaTarjeta);
        entityManager.getTransaction().commit();

        var movimiento = gastoService.registrar(
                tarjeta,
                categoriaTarjeta,
                ars,
                new BigDecimal("120000.00"),
                LocalDateTime.of(2026, 9, 25, 14, 0),
                "Consumo refinanciado",
                FormaPago.TARJETA_CREDITO,
                usuario.getId()
        );

        var obligacion = obligacionService.buscarPorMovimientoOrigen(movimiento.getId())
                .orElseThrow();

        Refinanciacion refinanciacion = new RefinanciacionService(entityManager).crear(
                obligacion.getId(),
                usuario.getId(),
                LocalDate.of(2026, 9, 26),
                3,
                new BigDecimal("6000.00"),
                new BigDecimal("1000.00"),
                new BigDecimal("24.0000")
        );

        ResumenPatrimonial resumen = patrimonioService.calcular(
                perfil,
                usuario.getId(),
                Map.of()
        );

        assertEquals(refinanciacion.getSaldoPlan(), resumen.getPasivosTarjetas());
        assertEquals(refinanciacion.getSaldoPlan().negate(), resumen.getPatrimonioNeto());
    }

    @Test
    void deberiaRechazarValorizacionMultidivisaSinCotizacion() {
        Moneda usd = new Moneda("USD", "Dólar estadounidense", 2, TipoMoneda.FIAT);
        InstitucionFinanciera institucionUsd = new InstitucionFinanciera(
                "Banco sin cotizacion",
                TipoInstitucionFinanciera.BANCO
        );
        Cuenta cuentaUsd = new Cuenta(
                "Cuenta USD sin cotizacion",
                TipoCuenta.CAJA_AHORRO,
                perfil,
                institucionUsd,
                usd
        );
        Categoria categoriaUsd = new Categoria("Sin cotizacion", perfil);

        entityManager.getTransaction().begin();
        entityManager.persist(usd);
        entityManager.persist(institucionUsd);
        entityManager.persist(cuentaUsd);
        entityManager.persist(categoriaUsd);
        entityManager.getTransaction().commit();

        movimientoService.registrar(
                cuentaUsd,
                categoriaUsd,
                TipoMovimiento.INGRESO,
                new BigDecimal("1.00"),
                LocalDateTime.of(2026, 9, 25, 15, 0),
                "Saldo USD sin cotizacion",
                usuario.getId()
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> patrimonioService.calcular(perfil, usuario.getId(), Map.of())
        );
    }

    @Test
    void deberiaRechazarPerfilDeOtroUsuario() {
        Usuario otroUsuario = new Usuario(
                "Otro",
                "Usuario",
                "otro." + System.nanoTime() + "@test.com",
                "hash"
        );
        PerfilFinanciero otroPerfil = new PerfilFinanciero(
                "Otro perfil",
                otroUsuario
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> patrimonioService.calcular(
                        otroPerfil,
                        usuario.getId(),
                        Map.of()
                )
        );
    }
}
