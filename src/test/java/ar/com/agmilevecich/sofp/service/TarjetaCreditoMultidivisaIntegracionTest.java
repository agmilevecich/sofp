package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.EstadoObligacion;
import ar.com.agmilevecich.sofp.domain.FormaPago;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.Movimiento;
import ar.com.agmilevecich.sofp.domain.Obligacion;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.TipoCambio;
import ar.com.agmilevecich.sofp.domain.TipoCuenta;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.domain.TipoMovimiento;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.CuentaRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

class TarjetaCreditoMultidivisaIntegracionTest {

    private EntityManager entityManager;
    private Cuenta tarjeta;
    private Cuenta cuentaUsd;
    private Cuenta cuentaArs;
    private Categoria categoria;
    private Moneda ars;
    private Moneda usd;
    private Usuario usuario;
    private MovimientoService movimientoService;
    private ObligacionService obligacionService;
    private GastoService gastoService;
    private CuentaService cuentaService;
    private PagoTarjetaService pagoTarjetaService;
    private TipoCambioRepository tipoCambioRepository;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();

        MovimientoRepository movimientoRepository = new MovimientoRepository(entityManager);
        ObligacionRepository obligacionRepository = new ObligacionRepository(entityManager);
        tipoCambioRepository = new TipoCambioRepository(entityManager);
        movimientoService = new MovimientoService(
                entityManager,
                movimientoRepository,
                obligacionRepository
        );
        obligacionService = new ObligacionService(
                entityManager,
                obligacionRepository,
                tipoCambioRepository
        );
        gastoService = new GastoService(movimientoService, obligacionService);
        cuentaService = new CuentaService(
                new CuentaRepository(entityManager),
                movimientoRepository,
                obligacionRepository,
                entityManager
        );
        pagoTarjetaService = new PagoTarjetaService(
                entityManager,
                movimientoRepository,
                obligacionRepository
        );

        usuario = new Usuario(
                "Juan",
                "Pérez",
                "multidivisa." + System.nanoTime() + "@test.com",
                "hash"
        );
        PerfilFinanciero perfil = new PerfilFinanciero("Perfil principal", usuario);
        usuario.agregarPerfilFinanciero(perfil);

        InstitucionFinanciera institucion = new InstitucionFinanciera(
                "Banco de Prueba",
                TipoInstitucionFinanciera.BANCO
        );
        ars = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        usd = new Moneda("USD", "Dólar estadounidense", 2, TipoMoneda.FIAT);
        tarjeta = new Cuenta(
                "Visa",
                perfil,
                institucion,
                ars,
                new BigDecimal("500000.00"),
                10,
                25
        );
        cuentaUsd = new Cuenta(
                "Caja USD",
                TipoCuenta.CAJA_AHORRO,
                perfil,
                institucion,
                usd
        );
        cuentaArs = new Cuenta(
                "Caja ARS",
                TipoCuenta.CAJA_AHORRO,
                perfil,
                institucion,
                ars
        );
        categoria = new Categoria("Compras", perfil);

        entityManager.getTransaction().begin();
        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(institucion);
        entityManager.persist(ars);
        entityManager.persist(usd);
        entityManager.persist(tarjeta);
        entityManager.persist(cuentaUsd);
        entityManager.persist(cuentaArs);
        entityManager.persist(categoria);
        entityManager.getTransaction().commit();

        registrarSaldoInicial(cuentaUsd, usd, new BigDecimal("40.00"), LocalDateTime.of(2026, 9, 9, 9, 0));
        registrarSaldoInicial(cuentaArs, ars, new BigDecimal("96000.00"), LocalDateTime.of(2026, 9, 9, 9, 0));
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
    void noDeberiaConvertirImplicitamenteUnaFinanciacionMultidivisaSinValorizacion() {
        Movimiento consumo = gastoService.registrar(
                tarjeta,
                categoria,
                usd,
                new BigDecimal("100.00"),
                LocalDateTime.of(2026, 9, 9, 10, 0),
                "Compra USD financiada",
                FormaPago.TARJETA_CREDITO,
                usuario.getId()
        );
        Obligacion obligacion = obligacionService.buscarPorMovimientoOrigen(consumo.getId()).orElseThrow();

        entityManager.getTransaction().begin();
        obligacion.crearFinanciacion(
                LocalDate.of(2026, 9, 10),
                new BigDecimal("100.00"),
                usd,
                null,
                false,
                true
        );
        entityManager.flush();
        entityManager.getTransaction().commit();

        assertEquals(
                0,
                cuentaService.calcularCreditoDisponible(tarjeta.getId(), usuario.getId())
                        .compareTo(new BigDecimal("500000.00"))
        );
    }

    @Test
    void deberiaCompletarCicloMultidivisaYLiberarCreditoAlPagarLaLiquidacion() {
        registrarCotizacion("1500.00", LocalDateTime.of(2026, 9, 10, 18, 0));
        registrarCotizacion("1600.00", LocalDateTime.of(2026, 9, 17, 15, 0));

        Movimiento consumo = gastoService.registrar(
                tarjeta,
                categoria,
                usd,
                new BigDecimal("100.00"),
                LocalDateTime.of(2026, 9, 9, 10, 0),
                "Compra USD con tarjeta",
                FormaPago.TARJETA_CREDITO,
                usuario.getId()
        );
        Obligacion obligacion = obligacionService.buscarPorMovimientoOrigen(consumo.getId()).orElseThrow();

        assertEquals(new BigDecimal("500000.00"),
                cuentaService.calcularCreditoDisponible(tarjeta.getId(), usuario.getId()));

        obligacionService.cerrarCiclo(tarjeta.getId(), LocalDate.of(2026, 9, 10));

        assertEquals(new BigDecimal("150000.00"), obligacion.getCuotas().get(0).getImporteValorizacionCierre());
        assertEquals(0, cuentaService.calcularCreditoDisponible(tarjeta.getId(), usuario.getId())
                .compareTo(new BigDecimal("350000.00")));

        pagoTarjetaService.registrarPago(
                obligacion.getId(),
                cuentaUsd,
                categoria,
                new BigDecimal("40.00"),
                LocalDateTime.of(2026, 9, 17, 14, 0),
                "Pago parcial USD",
                usuario.getId()
        );

        assertEquals(new BigDecimal("60.00"), obligacion.getSaldoPendiente());
        assertEquals(0, cuentaService.calcularCreditoDisponible(tarjeta.getId(), usuario.getId())
                .compareTo(new BigDecimal("410000.00")));

        obligacionService.liquidar(
                obligacion.getId(),
                LocalDateTime.of(2026, 9, 17, 16, 0),
                usuario.getId()
        );

        assertEquals(new BigDecimal("96000.00"), obligacion.getImporteLiquidacion());
        assertEquals(new BigDecimal("96000.00"), obligacion.getSaldoLiquidacion());
        assertEquals(0, cuentaService.calcularCreditoDisponible(tarjeta.getId(), usuario.getId())
                .compareTo(new BigDecimal("404000.00")));

        pagoTarjetaService.registrarPago(
                obligacion.getId(),
                cuentaArs,
                categoria,
                new BigDecimal("96000.00"),
                LocalDateTime.of(2026, 9, 17, 17, 0),
                "Pago liquidación ARS",
                usuario.getId()
        );

        assertEquals(0, obligacion.getSaldoLiquidacion().compareTo(BigDecimal.ZERO));
        assertEquals(EstadoObligacion.PAGADA, obligacion.getEstado());
        assertEquals(0, cuentaService.calcularCreditoDisponible(tarjeta.getId(), usuario.getId())
                .compareTo(new BigDecimal("500000.00")));
    }

    private void registrarCotizacion(String cotizacion, LocalDateTime fechaHora) {
        entityManager.getTransaction().begin();
        tipoCambioRepository.guardar(new TipoCambio(
                usd,
                ars,
                new BigDecimal(cotizacion),
                fechaHora,
                "Test"
        ));
        entityManager.getTransaction().commit();
    }

    private void registrarSaldoInicial(Cuenta cuenta, Moneda moneda, BigDecimal importe, LocalDateTime fechaHora) {
        entityManager.getTransaction().begin();
        entityManager.persist(new Movimiento(
                cuenta,
                categoria,
                moneda,
                TipoMovimiento.INGRESO,
                importe,
                fechaHora,
                "Saldo inicial",
                FormaPago.TRANSFERENCIA
        ));
        entityManager.getTransaction().commit();
    }
}
