package ar.com.agmilevecich.sofp.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PosicionActivoTest {

    @Test
    void deberiaCrearPosicionConCantidadCero() {
        Activo activo = crearActivo("Bono GD30");

        PosicionActivo posicion = new PosicionActivo(activo);

        assertEquals(activo, posicion.getActivo());
        assertEquals(0, posicion.getCantidad().compareTo(BigDecimal.ZERO));
    }

    @Test
    void deberiaRechazarActivoNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new PosicionActivo(null)
        );
    }

    @Test
    void deberiaAplicarCompra() {
        Activo activo = crearActivo("Bono GD30");
        PosicionActivo posicion = new PosicionActivo(activo);

        posicion.aplicarMovimiento(crearMovimiento(activo, TipoMovimientoActivo.COMPRA, "100", "125"));

        assertEquals(0, posicion.getCantidad().compareTo(new BigDecimal("100")));
    }

    @Test
    void deberiaAplicarCompraYVenta() {
        Activo activo = crearActivo("Bono GD30");
        PosicionActivo posicion = new PosicionActivo(activo);

        posicion.aplicarMovimiento(crearMovimiento(activo, TipoMovimientoActivo.COMPRA, "100", "125"));
        posicion.aplicarMovimiento(crearMovimiento(activo, TipoMovimientoActivo.VENTA, "30", "130"));

        assertEquals(0, posicion.getCantidad().compareTo(new BigDecimal("70")));
    }

    @Test
    void deberiaPermitirCerrarLaPosicion() {
        Activo activo = crearActivo("Bono GD30");
        PosicionActivo posicion = new PosicionActivo(activo);

        posicion.aplicarMovimiento(crearMovimiento(activo, TipoMovimientoActivo.COMPRA, "100", "125"));
        posicion.aplicarMovimiento(crearMovimiento(activo, TipoMovimientoActivo.VENTA, "100", "130"));

        assertEquals(0, posicion.getCantidad().compareTo(BigDecimal.ZERO));
    }

    @Test
    void deberiaRechazarMovimientoNulo() {
        Activo activo = crearActivo("Bono GD30");
        PosicionActivo posicion = new PosicionActivo(activo);

        assertThrows(
                NullPointerException.class,
                () -> posicion.aplicarMovimiento(null)
        );
    }

    @Test
    void deberiaRechazarMovimientoDeOtroActivo() {
        Activo activo = crearActivo("Bono GD30");
        Activo otroActivo = crearActivo("Bono AL30");
        PosicionActivo posicion = new PosicionActivo(activo);

        MovimientoActivo movimiento = crearMovimiento(
                otroActivo,
                TipoMovimientoActivo.COMPRA,
                "100",
                "125"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> posicion.aplicarMovimiento(movimiento)
        );
    }

    @Test
    void deberiaRechazarVentaMayorALaPosicion() {
        Activo activo = crearActivo("Bono GD30");
        PosicionActivo posicion = new PosicionActivo(activo);

        posicion.aplicarMovimiento(crearMovimiento(activo, TipoMovimientoActivo.COMPRA, "100", "125"));

        assertThrows(
                IllegalArgumentException.class,
                () -> posicion.aplicarMovimiento(
                        crearMovimiento(activo, TipoMovimientoActivo.VENTA, "101", "130")
                )
        );

        assertEquals(0, posicion.getCantidad().compareTo(new BigDecimal("100")));
    }

    private Activo crearActivo(String nombre) {
        Moneda moneda = new Moneda(
                "ARS",
                "Peso argentino",
                2,
                TipoMoneda.FIAT
        );

        return new Activo(nombre, moneda);
    }

    private MovimientoActivo crearMovimiento(
            Activo activo,
            TipoMovimientoActivo tipo,
            String cantidad,
            String precioUnitario) {

        return new MovimientoActivo(
                activo,
                tipo,
                new BigDecimal(cantidad),
                new BigDecimal(precioUnitario)
        );
    }
}
