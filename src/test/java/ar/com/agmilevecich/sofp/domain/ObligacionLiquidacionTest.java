package ar.com.agmilevecich.sofp.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ObligacionLiquidacionTest {

    @Test
    void deberiaLiquidarObligacionMultidivisaConTipoCambioHistorico() {
        Moneda ars = new Moneda("ARS", "Peso Argentino", 2, TipoMoneda.FIAT);
        Moneda usd = new Moneda("USD", "Dólar Estadounidense", 2, TipoMoneda.FIAT);
        Obligacion obligacion = new Obligacion(crearMovimiento(usd, ars));
        TipoCambio tipoCambio = new TipoCambio(
                usd,
                ars,
                new BigDecimal("1500.00"),
                LocalDateTime.of(2026, 9, 15, 12, 0),
                "Cotización manual"
        );

        obligacion.liquidar(tipoCambio);

        assertEquals(new BigDecimal("150000.00"), obligacion.getImporteLiquidacion());
        assertEquals(new BigDecimal("150000.00"), obligacion.getSaldoLiquidacion());
        assertSame(tipoCambio, obligacion.getTipoCambioLiquidacion());
        assertEquals(new BigDecimal("100.00"), obligacion.getImporteOriginal());
        assertEquals(new BigDecimal("100.00"), obligacion.getSaldoPendiente());
    }

    @Test
    void deberiaRegistrarPagoParcialSobreSaldoDeLiquidacion() {
        Obligacion obligacion = liquidarObligacionMultidivisa();

        obligacion.registrarPagoLiquidacion(new BigDecimal("50000.00"));

        assertEquals(new BigDecimal("100000.00"), obligacion.getSaldoLiquidacion());
        assertEquals(new BigDecimal("100.00"), obligacion.getSaldoPendiente());
        assertEquals(EstadoObligacion.PARCIAL, obligacion.getEstado());
    }

    @Test
    void deberiaMarcarComoPagadaAlCancelarSaldoDeLiquidacion() {
        Obligacion obligacion = liquidarObligacionMultidivisa();

        obligacion.registrarPagoLiquidacion(new BigDecimal("150000.00"));

        assertEquals(new BigDecimal("0.00"), obligacion.getSaldoLiquidacion());
        assertEquals(new BigDecimal("100.00"), obligacion.getSaldoPendiente());
        assertEquals(EstadoObligacion.PAGADA, obligacion.getEstado());
    }

    @Test
    void deberiaRechazarPagoMayorAlSaldoDeLiquidacion() {
        Obligacion obligacion = liquidarObligacionMultidivisa();

        assertThrows(IllegalArgumentException.class, () -> obligacion.registrarPagoLiquidacion(new BigDecimal("150000.01")));
        assertEquals(new BigDecimal("150000.00"), obligacion.getSaldoLiquidacion());
        assertEquals(EstadoObligacion.PENDIENTE, obligacion.getEstado());
    }

    @Test
    void deberiaRechazarPagoDeLiquidacionSinLiquidar() {
        Obligacion obligacion = new Obligacion(crearMovimiento());

        assertThrows(IllegalStateException.class, () -> obligacion.registrarPagoLiquidacion(new BigDecimal("100.00")));
    }

    @Test
    void deberiaRechazarTipoCambioNulo() {
        Obligacion obligacion = new Obligacion(crearMovimiento());

        assertThrows(NullPointerException.class, () -> obligacion.liquidar(null));
        assertNull(obligacion.getImporteLiquidacion());
        assertNull(obligacion.getSaldoLiquidacion());
        assertNull(obligacion.getTipoCambioLiquidacion());
    }

    @Test
    void deberiaRechazarTipoCambioConMonedaOrigenIncorrecta() {
        Moneda ars = new Moneda("ARS", "Peso Argentino", 2, TipoMoneda.FIAT);
        Moneda usd = new Moneda("USD", "Dólar Estadounidense", 2, TipoMoneda.FIAT);
        Moneda eur = new Moneda("EUR", "Euro", 2, TipoMoneda.FIAT);
        Obligacion obligacion = new Obligacion(crearMovimiento(usd, ars));
        TipoCambio tipoCambio = new TipoCambio(
                eur,
                ars,
                new BigDecimal("1600.00"),
                LocalDateTime.of(2026, 9, 15, 12, 0),
                "Cotización manual"
        );

        assertThrows(IllegalArgumentException.class, () -> obligacion.liquidar(tipoCambio));
        assertNull(obligacion.getImporteLiquidacion());
        assertNull(obligacion.getSaldoLiquidacion());
        assertNull(obligacion.getTipoCambioLiquidacion());
    }

    @Test
    void deberiaRechazarTipoCambioConMonedaDestinoIncorrecta() {
        Moneda ars = new Moneda("ARS", "Peso Argentino", 2, TipoMoneda.FIAT);
        Moneda usd = new Moneda("USD", "Dólar Estadounidense", 2, TipoMoneda.FIAT);
        Moneda eur = new Moneda("EUR", "Euro", 2, TipoMoneda.FIAT);
        Obligacion obligacion = new Obligacion(crearMovimiento(usd, ars));
        TipoCambio tipoCambio = new TipoCambio(
                usd,
                eur,
                new BigDecimal("1.20"),
                LocalDateTime.of(2026, 9, 15, 12, 0),
                "Cotización manual"
        );

        assertThrows(IllegalArgumentException.class, () -> obligacion.liquidar(tipoCambio));
        assertNull(obligacion.getImporteLiquidacion());
        assertNull(obligacion.getSaldoLiquidacion());
        assertNull(obligacion.getTipoCambioLiquidacion());
    }

    @Test
    void deberiaRechazarSegundaLiquidacion() {
        Moneda ars = new Moneda("ARS", "Peso Argentino", 2, TipoMoneda.FIAT);
        Moneda usd = new Moneda("USD", "Dólar Estadounidense", 2, TipoMoneda.FIAT);
        Obligacion obligacion = new Obligacion(crearMovimiento(usd, ars));
        TipoCambio primero = new TipoCambio(
                usd,
                ars,
                new BigDecimal("1500.00"),
                LocalDateTime.of(2026, 9, 15, 12, 0),
                "Cotización manual"
        );
        TipoCambio segundo = new TipoCambio(
                usd,
                ars,
                new BigDecimal("1600.00"),
                LocalDateTime.of(2026, 9, 16, 12, 0),
                "Cotización manual"
        );

        obligacion.liquidar(primero);

        assertThrows(IllegalStateException.class, () -> obligacion.liquidar(segundo));
        assertEquals(new BigDecimal("150000.00"), obligacion.getImporteLiquidacion());
        assertEquals(new BigDecimal("150000.00"), obligacion.getSaldoLiquidacion());
        assertSame(primero, obligacion.getTipoCambioLiquidacion());
    }

    private Obligacion liquidarObligacionMultidivisa() {
        Moneda ars = new Moneda("ARS", "Peso Argentino", 2, TipoMoneda.FIAT);
        Moneda usd = new Moneda("USD", "Dólar Estadounidense", 2, TipoMoneda.FIAT);
        Obligacion obligacion = new Obligacion(crearMovimiento(usd, ars));
        obligacion.liquidar(new TipoCambio(
                usd,
                ars,
                new BigDecimal("1500.00"),
                LocalDateTime.of(2026, 9, 15, 12, 0),
                "Cotización manual"
        ));
        return obligacion;
    }

    private Movimiento crearMovimiento() {
        Moneda ars = new Moneda("ARS", "Peso Argentino", 2, TipoMoneda.FIAT);
        return crearMovimiento(ars, ars);
    }

    private Movimiento crearMovimiento(Moneda monedaMovimiento, Moneda monedaCuenta) {
        Usuario usuario = new Usuario(
                "Ariel",
                "Milevecich",
                "ariel.obligacion.liquidacion." + System.nanoTime() + "@test.com",
                "hash"
        );
        PerfilFinanciero perfil = new PerfilFinanciero("Personal", usuario);
        InstitucionFinanciera banco = new InstitucionFinanciera("Banco Santander", TipoInstitucionFinanciera.BANCO);
        Cuenta cuenta = new Cuenta("Tarjeta", perfil, banco, monedaCuenta, new BigDecimal("500000.00"), 15, 10);
        Categoria categoria = new Categoria("Compra", perfil);

        return new Movimiento(
                cuenta,
                categoria,
                monedaMovimiento,
                TipoMovimiento.EGRESO,
                new BigDecimal("100.00"),
                LocalDateTime.of(2026, 9, 15, 10, 0),
                "Compra",
                FormaPago.TARJETA_CREDITO
        );
    }
}
