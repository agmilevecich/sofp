package ar.com.agmilevecich.sofp.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DetalleMovimientoCarteraActivoTest {

    @Test
    void deberiaExponerDatosDelMovimientoEImporteCalculado() {
        Moneda moneda = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        Activo activo = new Activo("Bono GD30", "GD30", moneda);
        MovimientoActivo movimiento = new MovimientoActivo(
                activo,
                TipoMovimientoActivo.COMPRA,
                new BigDecimal("100"),
                new BigDecimal("120")
        );

        DetalleMovimientoCarteraActivo detalle = new DetalleMovimientoCarteraActivo(movimiento);

        assertSame(movimiento, detalle.getMovimiento());
        assertSame(activo, detalle.getActivo());
        assertEquals(TipoMovimientoActivo.COMPRA, detalle.getTipoMovimiento());
        assertEquals(0, detalle.getCantidad().compareTo(new BigDecimal("100")));
        assertEquals(0, detalle.getPrecioUnitario().compareTo(new BigDecimal("120")));
        assertEquals(0, detalle.getImporte().compareTo(new BigDecimal("12000")));
        assertNull(detalle.getOperacionFinanciera());
    }

    @Test
    void deberiaRechazarMovimientoNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new DetalleMovimientoCarteraActivo(null)
        );
    }
}
