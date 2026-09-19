package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.FormaPago;
import ar.com.agmilevecich.sofp.domain.Financiacion;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.PagoTarjeta;
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
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PagoTarjetaServiceTest {

    private EntityManager entityManager;
    private Cuenta tarjeta;
    private Cuenta cuentaPagadora;
    private Cuenta cuentaPagadoraUsd;
    private Categoria categoriaCompras;
    private Categoria categoriaPago;
    private Moneda ars;
    private Moneda usd;
    private Usuario usuario;
    private GastoService gastoService;
    private ObligacionService obligacionService;
    private CuentaService cuentaService;
    private PagoTarjetaService pagoTarjetaService;
    private RefinanciacionService refinanciacionService;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();
        MovimientoRepository movimientoRepository = new MovimientoRepository(entityManager);
        ObligacionRepository obligacionRepository = new ObligacionRepository(entityManager);
        MovimientoService movimientoService = new MovimientoService(entityManager, movimientoRepository, obligacionRepository);
        obligacionService = new ObligacionService(entityManager, obligacionRepository);
        gastoService = new GastoService(movimientoService, obligacionService);
        cuentaService = new CuentaService(new CuentaRepository(entityManager), movimientoRepository, obligacionRepository, entityManager);
        pagoTarjetaService = new PagoTarjetaService(entityManager, movimientoRepository, obligacionRepository);
        refinanciacionService = new RefinanciacionService(entityManager);
        usuario = new Usuario("Juan", "Pérez", "pago.tarjeta." + System.nanoTime() + "@test.com", "hash");
        PerfilFinanciero perfil = new PerfilFinanciero("Perfil principal", usuario);
        usuario.agregarPerfilFinanciero(perfil);
        InstitucionFinanciera institucion = new InstitucionFinanciera("Banco de Prueba", TipoInstitucionFinanciera.BANCO);
        ars = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        usd = new Moneda("USD", "Dólar estadounidense", 2, TipoMoneda.FIAT);
        tarjeta = new Cuenta("Visa", perfil, institucion, ars, new BigDecimal("500000.00"), 10, 25);
        cuentaPagadora = new Cuenta("Caja de ahorro", TipoCuenta.CAJA_AHORRO, perfil, institucion, ars);
        cuentaPagadoraUsd = new Cuenta("Caja de ahorro USD", TipoCuenta.CAJA_AHORRO, perfil, institucion, usd);
        categoriaCompras = new Categoria("Compras", perfil);
        categoriaPago = new Categoria("Pago tarjeta", perfil);
        entityManager.getTransaction().begin();
        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(institucion);
        entityManager.persist(ars);
        entityManager.persist(usd);
        entityManager.persist(tarjeta);
        entityManager.persist(cuentaPagadora);
        entityManager.persist(cuentaPagadoraUsd);
        entityManager.persist(categoriaCompras);
        entityManager.persist(categoriaPago);
        entityManager.persist(new Movimiento(cuentaPagadora, categoriaPago, TipoMovimiento.INGRESO, new BigDecimal("200000.00"), LocalDateTime.of(2026, 9, 1, 9, 0), "Saldo inicial"));
        entityManager.persist(new Movimiento(cuentaPagadoraUsd, categoriaPago, TipoMovimiento.INGRESO, new BigDecimal("100.00"), LocalDateTime.of(2026, 9, 1, 9, 0), "Saldo inicial USD"));
        entityManager.getTransaction().commit();
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
    void deberiaRegistrarYRevertirElUltimoPagoConMovimientoCompensatorio() {
        Obligacion obligacion = registrarGasto("120000.00", 1);
        pagoTarjetaService.registrarPago(
                obligacion.getId(), cuentaPagadora, categoriaPago,
                new BigDecimal("50000.00"),
                LocalDateTime.of(2026, 9, 10, 10, 0),
                "Pago tarjeta", usuario.getId()
        );

        assertEquals(new BigDecimal("70000.00"), obligacion.getSaldoPendiente());
        assertEquals(new BigDecimal("150000.00"), cuentaService.calcularSaldo(cuentaPagadora.getId(), usuario.getId()));

        PagoTarjeta pago = pagoTarjetaService.revertirUltimoPago(
                obligacion.getId(), usuario.getId(),
                LocalDateTime.of(2026, 9, 11, 10, 0)
        );

        assertEquals("REVERSADO", pago.getEstado().name());
        assertEquals(new BigDecimal("120000.00"), obligacion.getSaldoPendiente());
        assertEquals(new BigDecimal("200000.00"), cuentaService.calcularSaldo(cuentaPagadora.getId(), usuario.getId()));
    }

    @Test
    void deberiaRegistrarPagoParcialYDescontarloDeLaCuentaPagadora() {
        Obligacion obligacion = registrarGasto("120000.00");
        pagoTarjetaService.registrarPago(obligacion.getId(), cuentaPagadora, categoriaPago, new BigDecimal("50000.00"), LocalDateTime.of(2026, 9, 10, 10, 0), "Pago tarjeta", usuario.getId());
        assertEquals(new BigDecimal("70000.00"), obligacion.getSaldoPendiente());
        assertEquals(new BigDecimal("150000.00"), cuentaService.calcularSaldo(cuentaPagadora.getId(), usuario.getId()));
        assertEquals(new BigDecimal("430000.00"), cuentaService.calcularCreditoDisponible(tarjeta.getId(), usuario.getId()));
    }

    @Test
    void deberiaRegistrarPagoParcialEnMonedaOriginalAntesDeLiquidarMultidivisa() {
        Obligacion obligacion = registrarGastoMultidivisaSinLiquidar();
        pagoTarjetaService.registrarPago(obligacion.getId(), cuentaPagadoraUsd, categoriaPago, new BigDecimal("40.00"), LocalDateTime.of(2026, 9, 10, 10, 0), "Pago USD antes de liquidar", usuario.getId());

        assertEquals(new BigDecimal("60.00"), obligacion.getSaldoPendiente());
        assertEquals("PARCIAL", obligacion.getEstado().name());
        assertEquals(new BigDecimal("60.00"), cuentaService.calcularSaldo(cuentaPagadoraUsd.getId(), usuario.getId()));
    }

    @Test
    void deberiaLiquidarSoloElSaldoOriginalRestanteDespuesDePagoMultidivisa() {
        Obligacion obligacion = registrarGastoMultidivisaSinLiquidar();
        pagoTarjetaService.registrarPago(obligacion.getId(), cuentaPagadoraUsd, categoriaPago, new BigDecimal("40.00"), LocalDateTime.of(2026, 9, 10, 10, 0), "Pago USD antes de liquidar", usuario.getId());

        TipoCambio tipoCambio = new TipoCambio(usd, ars, new BigDecimal("1600.00"), LocalDateTime.of(2026, 9, 15, 12, 0), "Cotización manual");
        entityManager.getTransaction().begin();
        entityManager.persist(tipoCambio);
        obligacion.liquidar(tipoCambio);
        entityManager.getTransaction().commit();

        assertEquals(new BigDecimal("60.00"), obligacion.getSaldoPendiente());
        assertEquals(new BigDecimal("96000.00"), obligacion.getImporteLiquidacion());
        assertEquals(new BigDecimal("96000.00"), obligacion.getSaldoLiquidacion());
    }

    @Test
    void deberiaRechazarPagoEnMonedaDeLiquidacionAntesDeLiquidarUnaObligacionMultidivisa() {
        Obligacion obligacion = registrarGastoMultidivisaSinLiquidar();
        assertThrows(IllegalArgumentException.class, () -> pagoTarjetaService.registrarPago(obligacion.getId(), cuentaPagadora, categoriaPago, new BigDecimal("50000.00"), LocalDateTime.of(2026, 9, 10, 10, 0), "Pago ARS antes de liquidar", usuario.getId()));
        assertEquals(new BigDecimal("100.00"), obligacion.getSaldoPendiente());
    }

    @Test
    void deberiaRegistrarPagoParcialSobreSaldoDeLiquidacionMultidivisa() {
        Obligacion obligacion = registrarGastoMultidivisa();
        pagoTarjetaService.registrarPago(obligacion.getId(), cuentaPagadora, categoriaPago, new BigDecimal("50000.00"), LocalDateTime.of(2026, 9, 10, 10, 0), "Pago tarjeta multidivisa", usuario.getId());

        assertEquals(new BigDecimal("100000.00"), obligacion.getSaldoLiquidacion());
        assertEquals(new BigDecimal("100.00"), obligacion.getSaldoPendiente());
        assertEquals("PARCIAL", obligacion.getEstado().name());
        assertEquals(new BigDecimal("150000.00"), cuentaService.calcularSaldo(cuentaPagadora.getId(), usuario.getId()));
    }

    @Test
    void deberiaRegistrarPagoCompletoSobreSaldoDeLiquidacionMultidivisa() {
        Obligacion obligacion = registrarGastoMultidivisa();
        pagoTarjetaService.registrarPago(obligacion.getId(), cuentaPagadora, categoriaPago, new BigDecimal("150000.00"), LocalDateTime.of(2026, 9, 10, 10, 0), "Pago tarjeta multidivisa", usuario.getId());

        assertEquals(new BigDecimal("0.00"), obligacion.getSaldoLiquidacion());
        assertEquals(new BigDecimal("100.00"), obligacion.getSaldoPendiente());
        assertEquals("PAGADA", obligacion.getEstado().name());
        assertEquals(new BigDecimal("50000.00"), cuentaService.calcularSaldo(cuentaPagadora.getId(), usuario.getId()));
    }

    @Test
    void deberiaRegistrarPagoDeRefinanciacionYReducirCredito() {
        Obligacion obligacion = registrarGasto("120000.00");
        var refinanciacion = refinanciacionService.crear(
                obligacion.getId(),
                usuario.getId(),
                java.time.LocalDate.of(2026, 9, 10),
                3,
                new BigDecimal("6000.00"),
                BigDecimal.ZERO,
                new BigDecimal("24.0000")
        );

        pagoTarjetaService.registrarPago(
                obligacion.getId(),
                cuentaPagadora,
                categoriaPago,
                new BigDecimal("50000.00"),
                LocalDateTime.of(2026, 9, 10, 12, 0),
                "Pago refinanciacion",
                usuario.getId()
        );

        PagoTarjeta pago = entityManager.createQuery(
                "SELECT p FROM PagoTarjeta p WHERE p.obligacion.id = :obligacionId ORDER BY p.id DESC",
                PagoTarjeta.class
        ).setParameter("obligacionId", obligacion.getId()).setMaxResults(1).getSingleResult();
        assertEquals(refinanciacion.getId(), pago.getRefinanciacion().getId());
        assertEquals(new BigDecimal("76000.00"), refinanciacion.getSaldoPlan());
        assertEquals(new BigDecimal("424000.00"), cuentaService.calcularCreditoDisponible(tarjeta.getId(), usuario.getId()));
        assertEquals(new BigDecimal("150000.00"), cuentaService.calcularSaldo(cuentaPagadora.getId(), usuario.getId()));
    }

    @Test
    void deberiaRevertirPagoDeRefinanciacionYRestaurarCredito() {
        Obligacion obligacion = registrarGasto("120000.00");
        refinanciacionService.crear(
                obligacion.getId(),
                usuario.getId(),
                java.time.LocalDate.of(2026, 9, 10),
                3,
                new BigDecimal("6000.00"),
                BigDecimal.ZERO,
                new BigDecimal("24.0000")
        );

        pagoTarjetaService.registrarPago(
                obligacion.getId(),
                cuentaPagadora,
                categoriaPago,
                new BigDecimal("50000.00"),
                LocalDateTime.of(2026, 9, 10, 12, 0),
                "Pago refinanciacion",
                usuario.getId()
        );

        PagoTarjeta pago = pagoTarjetaService.revertirUltimoPago(
                obligacion.getId(),
                usuario.getId(),
                LocalDateTime.of(2026, 9, 11, 12, 0)
        );

        assertEquals("REVERSADO", pago.getEstado().name());
        assertEquals(new BigDecimal("126000.00"), pago.getRefinanciacion().getSaldoPlan());
        assertEquals(new BigDecimal("374000.00"), cuentaService.calcularCreditoDisponible(tarjeta.getId(), usuario.getId()));
        assertEquals(new BigDecimal("200000.00"), cuentaService.calcularSaldo(cuentaPagadora.getId(), usuario.getId()));
    }

    @Test
    void noDeberiaPermitirRevertirDosVecesElMismoPagoDeRefinanciacion() {
        Obligacion obligacion = registrarGasto("120000.00");
        refinanciacionService.crear(
                obligacion.getId(),
                usuario.getId(),
                java.time.LocalDate.of(2026, 9, 10),
                3,
                new BigDecimal("6000.00"),
                BigDecimal.ZERO,
                new BigDecimal("24.0000")
        );

        pagoTarjetaService.registrarPago(
                obligacion.getId(),
                cuentaPagadora,
                categoriaPago,
                new BigDecimal("50000.00"),
                LocalDateTime.of(2026, 9, 10, 12, 0),
                "Pago refinanciacion",
                usuario.getId()
        );

        pagoTarjetaService.revertirUltimoPago(
                obligacion.getId(),
                usuario.getId(),
                LocalDateTime.of(2026, 9, 11, 12, 0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> pagoTarjetaService.revertirUltimoPago(
                        obligacion.getId(),
                        usuario.getId(),
                        LocalDateTime.of(2026, 9, 12, 12, 0)
                )
        );
    }

    @Test
    void deberiaAplicarPagoPosteriorAlVencimientoSobreLaFinanciacion() {
        Obligacion obligacion = registrarGasto("120000.00", LocalDateTime.of(2026, 8, 9, 10, 0));
        pagoTarjetaService.registrarPago(
                obligacion.getId(), cuentaPagadora, categoriaPago,
                new BigDecimal("40000.00"),
                LocalDateTime.of(2026, 9, 10, 10, 0),
                "Pago parcial", usuario.getId()
        );

        LocalDateTime fechaFinanciacion = obligacion.getFechaLimitePago().plusDays(1).atTime(10, 0);
        obligacionService.financiarSaldoImpago(
                obligacion.getId(), fechaFinanciacion.toLocalDate(), usuario.getId()
        ).orElseThrow();

        pagoTarjetaService.registrarPago(
                obligacion.getId(), cuentaPagadora, categoriaPago,
                new BigDecimal("30000.00"),
                fechaFinanciacion,
                "Pago financiacion", usuario.getId()
        );

        assertEquals(new BigDecimal("50000.00"), obligacion.getSaldoPendiente());
        assertEquals(new BigDecimal("50000.00"), obligacion.getCuotas().get(0).getSaldoPendiente());
        assertEquals(new BigDecimal("50000.00"), obligacion.getFinanciaciones().get(0).getSaldoCapital());
    }

    @Test
    void deberiaAplicarElExcedenteDelPagoDeFinanciacionSobreLaSiguienteCuota() {
        Obligacion obligacion = registrarGasto("120000.00", 3, LocalDateTime.of(2026, 8, 9, 10, 0));
        pagoTarjetaService.registrarPago(
                obligacion.getId(), cuentaPagadora, categoriaPago,
                new BigDecimal("20000.00"),
                LocalDateTime.of(2026, 9, 10, 10, 0),
                "Pago parcial", usuario.getId()
        );

        LocalDateTime fechaFinanciacion = obligacion.getCuotas().get(0).getFechaVencimiento()
                .plusDays(1).atTime(10, 0);
        obligacionService.financiarSaldoImpago(
                obligacion.getId(), fechaFinanciacion.toLocalDate(), usuario.getId()
        ).orElseThrow();

        pagoTarjetaService.registrarPago(
                obligacion.getId(), cuentaPagadora, categoriaPago,
                new BigDecimal("40000.00"),
                fechaFinanciacion,
                "Pago financiacion y cuota siguiente", usuario.getId()
        );

        assertEquals(new BigDecimal("0.00"), obligacion.getFinanciaciones().get(0).getSaldoCapital());
        assertEquals(new BigDecimal("0.00"), obligacion.getCuotas().get(0).getSaldoPendiente());
        assertEquals(new BigDecimal("20000.00"), obligacion.getCuotas().get(1).getSaldoPendiente());
        assertEquals(new BigDecimal("60000.00"), obligacion.getSaldoPendiente());
    }

    @Test
    void deberiaRegistrarPagoCompletoYDejarLaObligacionPagada() {
        Obligacion obligacion = registrarGasto("120000.00");
        pagoTarjetaService.registrarPago(obligacion.getId(), cuentaPagadora, categoriaPago, new BigDecimal("120000.00"), LocalDateTime.of(2026, 9, 10, 10, 0), "Pago tarjeta", usuario.getId());
        assertEquals(new BigDecimal("0.00"), obligacion.getSaldoPendiente());
        assertEquals("PAGADA", obligacion.getEstado().name());
        assertEquals(new BigDecimal("80000.00"), cuentaService.calcularSaldo(cuentaPagadora.getId(), usuario.getId()));
        assertEquals(new BigDecimal("500000.00"), cuentaService.calcularCreditoDisponible(tarjeta.getId(), usuario.getId()));
    }

    @Test
    void deberiaRechazarPagoQueSupereLosFondosDeLaCuentaPagadora() {
        Obligacion obligacion = registrarGasto("120000.00");
        assertThrows(IllegalArgumentException.class, () -> pagoTarjetaService.registrarPago(obligacion.getId(), cuentaPagadora, categoriaPago, new BigDecimal("200001.00"), LocalDateTime.of(2026, 9, 10, 10, 0), "Pago tarjeta", usuario.getId()));
        assertEquals(new BigDecimal("120000.00"), obligacion.getSaldoPendiente());
        assertEquals(new BigDecimal("200000.00"), cuentaService.calcularSaldo(cuentaPagadora.getId(), usuario.getId()));
    }

    @Test
    void deberiaAplicarPagoDeTarjetaSobreLasCuotasEnOrden() {
        Obligacion obligacion = registrarGasto("120000.00", 3);
        pagoTarjetaService.registrarPago(obligacion.getId(), cuentaPagadora, categoriaPago, new BigDecimal("50000.00"), LocalDateTime.of(2026, 9, 10, 10, 0), "Pago tarjeta", usuario.getId());
        assertEquals(3, obligacion.getCuotas().size());
        assertEquals(new BigDecimal("0.00"), obligacion.getCuotas().get(0).getSaldoPendiente());
        assertEquals("PAGADA", obligacion.getCuotas().get(0).getEstado().name());
        assertEquals(new BigDecimal("30000.00"), obligacion.getCuotas().get(1).getSaldoPendiente());
        assertEquals("PARCIAL", obligacion.getCuotas().get(1).getEstado().name());
        assertEquals(new BigDecimal("40000.00"), obligacion.getCuotas().get(2).getSaldoPendiente());
        assertEquals("PENDIENTE", obligacion.getCuotas().get(2).getEstado().name());
        assertEquals(new BigDecimal("70000.00"), obligacion.getSaldoPendiente());
        assertEquals("PARCIAL", obligacion.getEstado().name());
        assertEquals(new BigDecimal("150000.00"), cuentaService.calcularSaldo(cuentaPagadora.getId(), usuario.getId()));
        assertEquals(new BigDecimal("430000.00"), cuentaService.calcularCreditoDisponible(tarjeta.getId(), usuario.getId()));
    }

    @Test
    void noDeberiaPermitirPagoAnteriorAlConsumo() {
        Obligacion obligacion = registrarGasto("120000.00");
        assertThrows(IllegalArgumentException.class, () -> pagoTarjetaService.registrarPago(obligacion.getId(), cuentaPagadora, categoriaPago, new BigDecimal("1000.00"), LocalDateTime.of(2026, 9, 8, 23, 59), "Pago anticipado", usuario.getId()));
    }

    @Test
    void noDeberiaPermitirPagoFuturo() {
        Obligacion obligacion = registrarGasto("120000.00");
        assertThrows(IllegalArgumentException.class, () -> pagoTarjetaService.registrarPago(obligacion.getId(), cuentaPagadora, categoriaPago, new BigDecimal("1000.00"), LocalDateTime.now().plusDays(1), "Pago futuro", usuario.getId()));
    }

    @Test
    void deberiaConservarElCicloHistoricoAunqueCambieLaConfiguracionDeLaTarjeta() {
        Obligacion obligacion = registrarGasto("120000.00");
        var cicloOriginal = obligacion.getCicloFacturacion();
        tarjeta.configurarDatosCredito(new BigDecimal("500000.00"), 20, 5);
        var cicloActual = obligacion.getCicloFacturacion();
        assertEquals(cicloOriginal.getFechaInicio(), cicloActual.getFechaInicio());
        assertEquals(cicloOriginal.getFechaCierre(), cicloActual.getFechaCierre());
        assertEquals(cicloOriginal.getFechaVencimiento(), cicloActual.getFechaVencimiento());
    }

    @Test
    void deberiaConsiderarLaGraciaAlEvaluarMora() {
        tarjeta.configurarDatosCredito(new BigDecimal("500000.00"), 10, 25, 3);
        Obligacion obligacion = registrarGasto("120000.00");
        assertFalse(obligacion.estaEnMora(obligacion.getFechaLimitePago()));
        assertTrue(obligacion.estaEnMora(obligacion.getFechaLimitePago().plusDays(1)));
    }

    private Obligacion registrarGasto(String importe) { return registrarGasto(importe, 1); }

    private Obligacion registrarGasto(String importe, int cantidadCuotas) {
        return registrarGasto(importe, cantidadCuotas, LocalDateTime.of(2026, 9, 9, 10, 0));
    }

    private Obligacion registrarGasto(String importe, LocalDateTime fechaHora) {
        return registrarGasto(importe, 1, fechaHora);
    }

    private Obligacion registrarGasto(String importe, int cantidadCuotas, LocalDateTime fechaHora) {
        Movimiento movimiento = gastoService.registrar(tarjeta, categoriaCompras, ars, new BigDecimal(importe), fechaHora, "Compra con tarjeta", FormaPago.TARJETA_CREDITO, usuario.getId(), cantidadCuotas);
        return obligacionService.buscarPorMovimientoOrigen(movimiento.getId()).orElseThrow();
    }

    private Obligacion registrarGastoMultidivisaSinLiquidar() {
        Movimiento movimiento = gastoService.registrar(
                tarjeta,
                categoriaCompras,
                usd,
                new BigDecimal("100.00"),
                LocalDateTime.of(2026, 9, 9, 10, 0),
                "Compra en USD",
                FormaPago.TARJETA_CREDITO,
                usuario.getId(),
                1
        );
        return obligacionService.buscarPorMovimientoOrigen(movimiento.getId()).orElseThrow();
    }

    private Obligacion registrarGastoMultidivisa() {
        Obligacion obligacion = registrarGastoMultidivisaSinLiquidar();
        TipoCambio tipoCambio = new TipoCambio(
                usd,
                ars,
                new BigDecimal("1500.00"),
                LocalDateTime.of(2026, 9, 15, 12, 0),
                "Cotización manual"
        );
        entityManager.getTransaction().begin();
        entityManager.persist(tipoCambio);
        obligacion.liquidar(tipoCambio);
        entityManager.getTransaction().commit();
        return obligacion;
    }
}
