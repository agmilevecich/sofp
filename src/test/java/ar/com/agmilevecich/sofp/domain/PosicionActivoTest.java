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
        assertEquals(0, posicion.getCostoAdquisicion().compareTo(BigDecimal.ZERO));
        assertEquals(0, posicion.getPrecioPromedio().compareTo(BigDecimal.ZERO));
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
        assertEquals(0, posicion.getCostoAdquisicion().compareTo(new BigDecimal("12500")));
        assertEquals(0, posicion.getPrecioPromedio().compareTo(new BigDecimal("125")));
    }

    @Test
    void deberiaCalcularPrecioPromedioConComprasADistintosPrecios() {
        Activo activo = crearActivo("Bono GD30");
        PosicionActivo posicion = new PosicionActivo(activo);

        posicion.aplicarMovimiento(crearMovimiento(activo, TipoMovimientoActivo.COMPRA, "100", "100"));
        posicion.aplicarMovimiento(crearMovimiento(activo, TipoMovimientoActivo.COMPRA, "50", "160"));

        assertEquals(0, posicion.getCantidad().compareTo(new BigDecimal("150")));
        assertEquals(0, posicion.getCostoAdquisicion().compareTo(new BigDecimal("18000")));
        assertEquals(0, posicion.getPrecioPromedio().compareTo(new BigDecimal("120")));
    }

    @Test
    void deberiaAplicarCompraYVentaManteniendoCostoDeAdquisicionRemanente() {
        Activo activo = crearActivo("Bono GD30");
        PosicionActivo posicion = new PosicionActivo(activo);

        posicion.aplicarMovimiento(crearMovimiento(activo, TipoMovimientoActivo.COMPRA, "100", "100"));
        posicion.aplicarMovimiento(crearMovimiento(activo, TipoMovimientoActivo.COMPRA, "50", "160"));
        posicion.aplicarMovimiento(crearMovimiento(activo, TipoMovimientoActivo.VENTA, "50", "150"));

        assertEquals(0, posicion.getCantidad().compareTo(new BigDecimal("100")));
        assertEquals(0, posicion.getCostoAdquisicion().compareTo(new BigDecimal("12000")));
        assertEquals(0, posicion.getPrecioPromedio().compareTo(new BigDecimal("120")));
    }

    @Test
    void deberiaPermitirCerrarLaPosicionYReiniciarCosto() {
        Activo activo = crearActivo("Bono GD30");
        PosicionActivo posicion = new PosicionActivo(activo);

        posicion.aplicarMovimiento(crearMovimiento(activo, TipoMovimientoActivo.COMPRA, "100", "125"));
        posicion.aplicarMovimiento(crearMovimiento(activo, TipoMovimientoActivo.VENTA, "100", "130"));

        assertEquals(0, posicion.getCantidad().compareTo(BigDecimal.ZERO));
        assertEquals(0, posicion.getCostoAdquisicion().compareTo(BigDecimal.ZERO));
        assertEquals(0, posicion.getPrecioPromedio().compareTo(BigDecimal.ZERO));
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
        assertEquals(0, posicion.getCostoAdquisicion().compareTo(new BigDecimal("12500")));
    }

    private Activo crearActivo(String nombre) {
        Moneda moneda = new Moneda(
                "ARS",
                "Peso argentino",
                2,
                TipoMoneda.FIAT
        );

        String simbolo = nombre.equals("Bono GD30") ? "GD30" : "AL30";
        return new Activo(nombre, simbolo, moneda);
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
