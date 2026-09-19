package ar.com.agmilevecich.sofp.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ObligacionTest {

    @Test
    void deberiaCrearObligacionDesdeMovimientoConTarjetaDeCredito() {

        Movimiento movimiento = crearMovimiento(FormaPago.TARJETA_CREDITO);

        Obligacion obligacion = new Obligacion(movimiento);

        assertEquals(new BigDecimal("15000.50"), obligacion.getImporteOriginal());
        assertEquals(new BigDecimal("15000.50"), obligacion.getSaldoPendiente());
        assertEquals(EstadoObligacion.PENDIENTE, obligacion.getEstado());
        assertEquals(movimiento, obligacion.getMovimientoOrigen());
        assertEquals(movimiento.getFechaHora(), obligacion.getFechaOrigen());
        assertEquals(movimiento.getMoneda(), obligacion.getMonedaOriginal());
        assertEquals(movimiento.getCuenta().getMoneda(), obligacion.getMonedaLiquidacion());
        assertNull(obligacion.getImporteLiquidacion());
    }

    @Test
    void deberiaSepararMonedaOriginalDeMonedaDeLiquidacionEnConsumoMultidivisa() {
        Movimiento movimiento = crearMovimientoMultidivisa(FormaPago.TARJETA_CREDITO);

        Obligacion obligacion = new Obligacion(movimiento);

        assertEquals(new BigDecimal("100.00"), obligacion.getImporteOriginal());
        assertEquals("USD", obligacion.getMonedaOriginal().getCodigo());
        assertEquals("ARS", obligacion.getMonedaLiquidacion().getCodigo());
        assertEquals(new BigDecimal("100.00"), obligacion.getSaldoPendiente());
        assertNull(obligacion.getImporteLiquidacion());
    }

    @Test
    void deberiaSepararFinanciacionOriginalDeLiquidacionPosteriorMultidivisa() {
        Obligacion obligacion = new Obligacion(crearMovimientoMultidivisa(FormaPago.TARJETA_CREDITO));
        TipoCambio cierre = new TipoCambio(
                obligacion.getMonedaOriginal(),
                obligacion.getMonedaLiquidacion(),
                new BigDecimal("1500.00"),
                LocalDateTime.of(2026, 9, 15, 18, 0),
                "CIERRE"
        );
        TipoCambio liquidacion = new TipoCambio(
                obligacion.getMonedaOriginal(),
                obligacion.getMonedaLiquidacion(),
                new BigDecimal("1600.00"),
                LocalDateTime.of(2026, 9, 26, 10, 0),
                "LIQUIDACION"
        );

        Financiacion financiacion = obligacion.crearFinanciacion(
                LocalDate.of(2026, 9, 26),
                new BigDecimal("40.00"),
                obligacion.getMonedaOriginal(),
                cierre,
                false
        );
        obligacion.liquidar(liquidacion);

        assertEquals(new BigDecimal("40.00"), financiacion.getSaldoCapital());
        assertEquals(new BigDecimal("60000.00"), financiacion.getSaldoValorizacion());
        assertEquals(new BigDecimal("96000.00"), obligacion.getSaldoLiquidacion());

        obligacion.registrarPagoFinanciacion(financiacion, new BigDecimal("40.00"));
        assertEquals(EstadoObligacion.PARCIAL, obligacion.getEstado());
        obligacion.registrarPagoLiquidacion(new BigDecimal("96000.00"));
        assertEquals(EstadoObligacion.PAGADA, obligacion.getEstado());
    }

    @Test
    void deberiaPermitirFinanciarSaldoEnMonedaDeLiquidacionDespuesDeLiquidar() {
        Obligacion obligacion = new Obligacion(crearMovimientoMultidivisa(FormaPago.TARJETA_CREDITO));
        TipoCambio liquidacion = new TipoCambio(
                obligacion.getMonedaOriginal(),
                obligacion.getMonedaLiquidacion(),
                new BigDecimal("1600.00"),
                LocalDateTime.of(2026, 9, 26, 10, 0),
                "LIQUIDACION"
        );
        obligacion.liquidar(liquidacion);

        Financiacion financiacion = obligacion.crearFinanciacion(
                LocalDate.of(2026, 9, 26),
                new BigDecimal("160000.00"),
                obligacion.getMonedaLiquidacion(),
                null,
                true
        );

        assertEquals(new BigDecimal("160000.00"), financiacion.getSaldoCapital());
        assertEquals(new BigDecimal("0.00"), obligacion.getSaldoLiquidacion());
        obligacion.registrarPagoFinanciacion(financiacion, new BigDecimal("160000.00"));
        assertEquals(EstadoObligacion.PAGADA, obligacion.getEstado());
    }

    @Test
    void deberiaRechazarMovimientoDeIngreso() {

        Movimiento movimiento = crearMovimiento(TipoMovimiento.INGRESO, FormaPago.TARJETA_CREDITO);

        assertThrows(
                IllegalArgumentException.class,
                () -> new Obligacion(movimiento)
        );
    }

    @Test
    void deberiaRechazarMovimientoSinTarjetaDeCredito() {

        Movimiento movimiento = crearMovimiento(FormaPago.TARJETA_DEBITO);

        assertThrows(
                IllegalArgumentException.class,
                () -> new Obligacion(movimiento)
        );
    }

    @Test
    void deberiaRechazarMovimientoNulo() {

        assertThrows(
                NullPointerException.class,
                () -> new Obligacion(null)
        );
    }

    @Test
    void deberiaCalcularPagoMinimoDeLaObligacion() {
        Obligacion obligacion = new Obligacion(crearMovimiento(FormaPago.TARJETA_CREDITO));
        obligacion.getMovimientoOrigen().getCuenta().configurarDatosCredito(
                new BigDecimal("500000.00"), 15, 10, 0,
                new BigDecimal("20.00"), new BigDecimal("1500.00")
        );

        assertEquals(new BigDecimal("3000.10"), obligacion.calcularPagoMinimo());
    }

    @Test
    void deberiaDeterminarSiElPagoCumpleElMinimo() {
        Obligacion obligacion = new Obligacion(crearMovimiento(FormaPago.TARJETA_CREDITO));
        obligacion.getMovimientoOrigen().getCuenta().configurarDatosCredito(
                new BigDecimal("500000.00"), 15, 10, 0,
                new BigDecimal("20.00"), new BigDecimal("1500.00")
        );

        assertFalse(obligacion.cumplePagoMinimo(new BigDecimal("3000.00")));
        assertTrue(obligacion.cumplePagoMinimo(new BigDecimal("3000.10")));
        assertTrue(obligacion.cumplePagoMinimo(new BigDecimal("5000.00")));
    }

    @Test
    void deberiaAnularObligacionSinBorrarSuOrigen() {
        Movimiento movimiento = crearMovimiento(FormaPago.TARJETA_CREDITO);
        Obligacion obligacion = new Obligacion(movimiento);

        obligacion.anular();

        assertEquals(EstadoObligacion.ANULADA, obligacion.getEstado());
        assertEquals(new BigDecimal("0.00"), obligacion.getSaldoPendiente());
        assertEquals(movimiento, obligacion.getMovimientoOrigen());
    }

    @Test
    void noDeberiaAnularObligacionDespuesDeUnPago() {
        Obligacion obligacion = new Obligacion(crearMovimiento(FormaPago.TARJETA_CREDITO));
        obligacion.registrarPago(new BigDecimal("100.00"));

        assertThrows(IllegalStateException.class, obligacion::anular);
    }

    @Test
    void deberiaRegistrarPagoParcial() {

        Obligacion obligacion = new Obligacion(
                crearMovimiento(FormaPago.TARJETA_CREDITO)
        );

        obligacion.registrarPago(new BigDecimal("5000.50"));

        assertEquals(new BigDecimal("10000.00"), obligacion.getSaldoPendiente());
        assertEquals(EstadoObligacion.PARCIAL, obligacion.getEstado());
    }

    @Test
    void deberiaMarcarComoPagadaAlCancelarSaldoCompleto() {

        Obligacion obligacion = new Obligacion(
                crearMovimiento(FormaPago.TARJETA_CREDITO)
        );

        obligacion.registrarPago(new BigDecimal("15000.50"));

        assertEquals(0, obligacion.getSaldoPendiente().compareTo(BigDecimal.ZERO));
        assertEquals(EstadoObligacion.PAGADA, obligacion.getEstado());
    }

    @Test
    void deberiaPermitirVariosPagosParciales() {

        Obligacion obligacion = new Obligacion(
                crearMovimiento(FormaPago.TARJETA_CREDITO)
        );

        obligacion.registrarPago(new BigDecimal("5000.00"));
        obligacion.registrarPago(new BigDecimal("4000.00"));

        assertEquals(new BigDecimal("6000.50"), obligacion.getSaldoPendiente());
        assertEquals(EstadoObligacion.PARCIAL, obligacion.getEstado());
    }

    @Test
    void deveriaRechazarPagoMayorAlSaldoPendiente() {

        Obligacion obligacion = new Obligacion(
                crearMovimiento(FormaPago.TARJETA_CREDITO)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> obligacion.registrarPago(new BigDecimal("15000.51"))
        );

        assertEquals(new BigDecimal("15000.50"), obligacion.getSaldoPendiente());
        assertEquals(EstadoObligacion.PENDIENTE, obligacion.getEstado());
    }

    @Test
    void deberiaRechazarPagoNulo() {

        Obligacion obligacion = new Obligacion(
                crearMovimiento(FormaPago.TARJETA_CREDITO)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> obligacion.registrarPago(null)
        );
    }

    @Test
    void deberiaRechazarPagoCero() {

        Obligacion obligacion = new Obligacion(
                crearMovimiento(FormaPago.TARJETA_CREDITO)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> obligacion.registrarPago(BigDecimal.ZERO)
        );
    }

    @Test
    void deberiaRechazarPagoSobreObligacionPagada() {

        Obligacion obligacion = new Obligacion(
                crearMovimiento(FormaPago.TARJETA_CREDITO)
        );

        obligacion.registrarPago(new BigDecimal("15000.50"));

        assertThrows(
                IllegalStateException.class,
                () -> obligacion.registrarPago(BigDecimal.ONE)
        );
    }

    private Movimiento crearMovimiento(FormaPago formaPago) {
        return crearMovimiento(TipoMovimiento.EGRESO, formaPago);
    }

    private Movimiento crearMovimiento(
            TipoMovimiento tipoMovimiento,
            FormaPago formaPago) {

        Usuario usuario = new Usuario(
                "Ariel",
                "Milevecich",
                "ariel.obligacion." + System.nanoTime() + "@test.com",
                "hash"
        );

        PerfilFinanciero perfil = new PerfilFinanciero(
                "Personal",
                usuario
        );

        InstitucionFinanciera banco = new InstitucionFinanciera(
                "Banco Santander",
                TipoInstitucionFinanciera.BANCO
        );

        Moneda moneda = new Moneda(
                "ARS",
                "Peso Argentino",
                2,
                TipoMoneda.FIAT
        );

        Cuenta cuenta = new Cuenta(
                "Tarjeta",
                perfil,
                banco,
                moneda,
                new BigDecimal("500000.00"),
                15,
                10
        );

        Categoria categoria = new Categoria(
                "Supermercado",
                perfil
        );

        return new Movimiento(
                cuenta,
                categoria,
                tipoMovimiento,
                new BigDecimal("15000.50"),
                LocalDateTime.of(2026, 9, 4, 12, 0),
                "Compra con tarjeta",
                formaPago
        );
    }

    private Movimiento crearMovimientoMultidivisa(FormaPago formaPago) {
        Usuario usuario = new Usuario(
                "Ariel",
                "Milevecich",
                "ariel.obligacion.multidivisa." + System.nanoTime() + "@test.com",
                "hash"
        );

        PerfilFinanciero perfil = new PerfilFinanciero("Personal", usuario);
        InstitucionFinanciera banco = new InstitucionFinanciera("Banco Santander", TipoInstitucionFinanciera.BANCO);
        Moneda ars = new Moneda("ARS", "Peso Argentino", 2, TipoMoneda.FIAT);
        Moneda usd = new Moneda("USD", "Dólar estadounidense", 2, TipoMoneda.FIAT);
        Cuenta cuenta = new Cuenta("Tarjeta ARS", perfil, banco, ars, new BigDecimal("500000.00"), 15, 10);
        Categoria categoria = new Categoria("Supermercado", perfil);

        return new Movimiento(
                cuenta,
                categoria,
                usd,
                TipoMovimiento.EGRESO,
                new BigDecimal("100.00"),
                LocalDateTime.of(2026, 9, 4, 12, 0),
                "Compra en USD",
                formaPago
        );
    }
}
