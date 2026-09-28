package ar.com.agmilevecich.sofp.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CotizacionActivoTest {

    @Test
    void deberiaCrearCotizacion() {
        Moneda moneda = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        Activo activo = new Activo("Bono GD30", "GD30", moneda);
        LocalDate fecha = LocalDate.of(2026, 9, 28);

        CotizacionActivo cotizacion = new CotizacionActivo(
                activo, fecha, new BigDecimal("150.25"));

        assertEquals(activo, cotizacion.getActivo());
        assertEquals(fecha, cotizacion.getFecha());
        assertEquals(new BigDecimal("150.25"), cotizacion.getPrecio());
    }

    @Test
    void deberiaCambiarPrecio() {
        Activo activo = activo();

        CotizacionActivo cotizacion = new CotizacionActivo(
                activo, LocalDate.of(2026, 9, 28), new BigDecimal("150"));

        cotizacion.cambiarPrecio(new BigDecimal("155"));

        assertEquals(new BigDecimal("155"), cotizacion.getPrecio());
    }

    @Test
    void deberiaRechazarActivoNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new CotizacionActivo(
                        null, LocalDate.now(), new BigDecimal("150")));
    }

    @Test
    void deberiaRechazarFechaNula() {
        assertThrows(
                NullPointerException.class,
                () -> new CotizacionActivo(
                        activo(), null, new BigDecimal("150")));
    }

    @Test
    void deberiaRechazarPrecioNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new CotizacionActivo(
                        activo(), LocalDate.now(), null));
    }

    @Test
    void deberiaRechazarPrecioCero() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new CotizacionActivo(
                        activo(), LocalDate.now(), BigDecimal.ZERO));
    }

    @Test
    void deberiaRechazarPrecioNegativoAlCambiarlo() {
        CotizacionActivo cotizacion = new CotizacionActivo(
                activo(), LocalDate.now(), new BigDecimal("150"));

        assertThrows(
                IllegalArgumentException.class,
                () -> cotizacion.cambiarPrecio(new BigDecimal("-1")));
    }

    private Activo activo() {
        return new Activo(
                "Bono GD30",
                "GD30",
                new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT));
    }
}
