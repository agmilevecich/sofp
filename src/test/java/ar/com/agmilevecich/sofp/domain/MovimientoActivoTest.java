package ar.com.agmilevecich.sofp.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MovimientoActivoTest {

    @Test
    void deberiaCrearMovimientoActivoDeCompra() {

        Activo activo = crearActivo();

        MovimientoActivo movimiento =
                new MovimientoActivo(
                        activo,
                        TipoMovimientoActivo.COMPRA,
                        new BigDecimal("100"),
                        new BigDecimal("105.50")
                );

        assertEquals(activo, movimiento.getActivo());
        assertEquals(TipoMovimientoActivo.COMPRA, movimiento.getTipoMovimiento());
        assertEquals(new BigDecimal("100"), movimiento.getCantidad());
        assertEquals(new BigDecimal("105.50"), movimiento.getPrecioUnitario());
        assertEquals(new BigDecimal("100"), movimiento.getVariacionCantidad());
    }

    @Test
    void deberiaCrearMovimientoActivoDeVenta() {

        MovimientoActivo movimiento =
                new MovimientoActivo(
                        crearActivo(),
                        TipoMovimientoActivo.VENTA,
                        new BigDecimal("30"),
                        new BigDecimal("110.25")
                );

        assertEquals(new BigDecimal("30"), movimiento.getCantidad());
        assertEquals(new BigDecimal("-30"), movimiento.getVariacionCantidad());
    }

    @Test
    void deberiaRechazarActivoNulo() {

        assertThrows(
                NullPointerException.class,
                () -> new MovimientoActivo(
                        null,
                        TipoMovimientoActivo.COMPRA,
                        new BigDecimal("100"),
                        new BigDecimal("100")
                )
        );
    }

    @Test
    void deberiaRechazarTipoNulo() {

        assertThrows(
                NullPointerException.class,
                () -> new MovimientoActivo(
                        crearActivo(),
                        null,
                        new BigDecimal("100"),
                        new BigDecimal("100")
                )
        );
    }

    @Test
    void deberiaRechazarCantidadNula() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new MovimientoActivo(
                        crearActivo(),
                        TipoMovimientoActivo.COMPRA,
                        null,
                        new BigDecimal("100")
                )
        );
    }

    @Test
    void deberiaRechazarCantidadCero() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new MovimientoActivo(
                        crearActivo(),
                        TipoMovimientoActivo.COMPRA,
                        BigDecimal.ZERO,
                        new BigDecimal("100")
                )
        );
    }

    @Test
    void deberiaRechazarCantidadNegativa() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new MovimientoActivo(
                        crearActivo(),
                        TipoMovimientoActivo.COMPRA,
                        new BigDecimal("-1"),
                        new BigDecimal("100")
                )
        );
    }

    @Test
    void deberiaRechazarPrecioUnitarioNulo() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new MovimientoActivo(
                        crearActivo(),
                        TipoMovimientoActivo.COMPRA,
                        new BigDecimal("100"),
                        null
                )
        );
    }

    @Test
    void deberiaRechazarPrecioUnitarioCero() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new MovimientoActivo(
                        crearActivo(),
                        TipoMovimientoActivo.COMPRA,
                        new BigDecimal("100"),
                        BigDecimal.ZERO
                )
        );
    }

    @Test
    void deberiaRechazarPrecioUnitarioNegativo() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new MovimientoActivo(
                        crearActivo(),
                        TipoMovimientoActivo.COMPRA,
                        new BigDecimal("100"),
                        new BigDecimal("-100")
                )
        );
    }

    @Test
    void deberiaCambiarTipoCantidadYPrecio() {

        MovimientoActivo movimiento =
                new MovimientoActivo(
                        crearActivo(),
                        TipoMovimientoActivo.COMPRA,
                        new BigDecimal("100"),
                        new BigDecimal("100")
                );

        movimiento.cambiarTipoMovimiento(TipoMovimientoActivo.VENTA);
        movimiento.cambiarCantidad(new BigDecimal("25"));
        movimiento.cambiarPrecioUnitario(new BigDecimal("120.50"));

        assertEquals(TipoMovimientoActivo.VENTA, movimiento.getTipoMovimiento());
        assertEquals(new BigDecimal("25"), movimiento.getCantidad());
        assertEquals(new BigDecimal("120.50"), movimiento.getPrecioUnitario());
        assertEquals(new BigDecimal("-25"), movimiento.getVariacionCantidad());
    }

    private Activo crearActivo() {

        Moneda moneda =
                new Moneda(
                        "ARS",
                        "Peso argentino",
                        2,
                        TipoMoneda.FIAT
                );

        return new Bono("Bono GD30", "GD30", moneda);
    }
}
