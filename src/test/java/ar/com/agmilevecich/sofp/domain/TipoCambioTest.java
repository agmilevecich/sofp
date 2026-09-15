package ar.com.agmilevecich.sofp.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TipoCambioTest {

    private final Moneda ars = new Moneda("ARS", "Peso Argentino", 2, TipoMoneda.FIAT);
    private final Moneda usd = new Moneda("USD", "Dólar Estadounidense", 2, TipoMoneda.FIAT);

    @Test
    void deberiaCrearTipoCambioValido() {
        LocalDateTime fechaHora = LocalDateTime.of(2026, 9, 15, 12, 0);

        TipoCambio tipoCambio = new TipoCambio(
                usd,
                ars,
                new BigDecimal("1500.00"),
                fechaHora,
                "Cotización manual"
        );

        assertSame(usd, tipoCambio.getMonedaOrigen());
        assertSame(ars, tipoCambio.getMonedaDestino());
        assertEquals(new BigDecimal("1500.00"), tipoCambio.getCotizacion());
        assertEquals(fechaHora, tipoCambio.getFechaHora());
        assertEquals("Cotización manual", tipoCambio.getFuente());
    }

    @Test
    void deberiaConvertirImporteConLosDecimalesDeLaMonedaDestino() {
        TipoCambio tipoCambio = new TipoCambio(
                usd,
                ars,
                new BigDecimal("1500.00"),
                LocalDateTime.of(2026, 9, 15, 12, 0),
                "Cotización manual"
        );

        assertEquals(new BigDecimal("150000.00"), tipoCambio.convertir(new BigDecimal("100.00")));
    }

    @Test
    void deberiaRedondearLaConversionSegunLaMonedaDestino() {
        TipoCambio tipoCambio = new TipoCambio(
                usd,
                ars,
                new BigDecimal("1500.123456"),
                LocalDateTime.of(2026, 9, 15, 12, 0),
                "Cotización manual"
        );

        assertEquals(new BigDecimal("150012.35"), tipoCambio.convertir(new BigDecimal("100.00")));
    }

    @Test
    void deberiaRechazarMonedaOrigenNula() {
        assertThrows(NullPointerException.class, () -> new TipoCambio(
                null,
                ars,
                new BigDecimal("1500.00"),
                LocalDateTime.now(),
                "Manual"
        ));
    }

    @Test
    void deberiaRechazarMonedaDestinoNula() {
        assertThrows(NullPointerException.class, () -> new TipoCambio(
                usd,
                null,
                new BigDecimal("1500.00"),
                LocalDateTime.now(),
                "Manual"
        ));
    }

    @Test
    void deberiaRechazarMismaMonedaComoOrigenYDestino() {
        assertThrows(IllegalArgumentException.class, () -> new TipoCambio(
                usd,
                usd,
                new BigDecimal("1.00"),
                LocalDateTime.now(),
                "Manual"
        ));
    }

    @Test
    void deberiaRechazarCotizacionNulaOCeroONegativa() {
        assertThrows(NullPointerException.class, () -> new TipoCambio(
                usd, ars, null, LocalDateTime.now(), "Manual"
        ));
        assertThrows(IllegalArgumentException.class, () -> new TipoCambio(
                usd, ars, BigDecimal.ZERO, LocalDateTime.now(), "Manual"
        ));
        assertThrows(IllegalArgumentException.class, () -> new TipoCambio(
                usd, ars, new BigDecimal("-1.00"), LocalDateTime.now(), "Manual"
        ));
    }

    @Test
    void deberiaRechazarFechaHoraNula() {
        assertThrows(NullPointerException.class, () -> new TipoCambio(
                usd, ars, new BigDecimal("1500.00"), null, "Manual"
        ));
    }

    @Test
    void deberiaRechazarFuenteNulaOVacia() {
        assertThrows(NullPointerException.class, () -> new TipoCambio(
                usd, ars, new BigDecimal("1500.00"), LocalDateTime.now(), null
        ));
        assertThrows(IllegalArgumentException.class, () -> new TipoCambio(
                usd, ars, new BigDecimal("1500.00"), LocalDateTime.now(), "   "
        ));
    }

    @Test
    void deberiaRechazarImporteDeConversionNuloCeroONegativo() {
        TipoCambio tipoCambio = new TipoCambio(
                usd,
                ars,
                new BigDecimal("1500.00"),
                LocalDateTime.now(),
                "Manual"
        );

        assertThrows(NullPointerException.class, () -> tipoCambio.convertir(null));
        assertThrows(IllegalArgumentException.class, () -> tipoCambio.convertir(BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, () -> tipoCambio.convertir(new BigDecimal("-1.00")));
    }
}
