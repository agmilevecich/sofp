package ar.com.agmilevecich.sofp.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ValorizacionPosicionActivoTest {

    @Test
    void deberiaCalcularValorActualGananciaYRendimiento() {
        PosicionActivo posicion = crearPosicionConCosto("100", "120");

        ValorizacionPosicionActivo valorizacion = new ValorizacionPosicionActivo(
                posicion,
                new BigDecimal("150")
        );

        assertEquals(0, valorizacion.getValorActual().compareTo(new BigDecimal("15000")));
        assertEquals(0, valorizacion.getGananciaPerdida().compareTo(new BigDecimal("3000")));
        assertEquals(0, valorizacion.getRendimientoPorcentual().compareTo(new BigDecimal("25")));
    }

    @Test
    void deberiaCalcularPerdida() {
        PosicionActivo posicion = crearPosicionConCosto("100", "120");

        ValorizacionPosicionActivo valorizacion = new ValorizacionPosicionActivo(
                posicion,
                new BigDecimal("100")
        );

        assertEquals(0, valorizacion.getValorActual().compareTo(new BigDecimal("10000")));
        assertEquals(0, valorizacion.getGananciaPerdida().compareTo(new BigDecimal("-2000")));
        assertEquals(0, valorizacion.getRendimientoPorcentual().compareTo(new BigDecimal("-16.66666666666666666666666666666667")));
    }

    @Test
    void deberiaDevolverCeroCuandoLaPosicionEstaCerrada() {
        Activo activo = crearActivo();
        PosicionActivo posicion = new PosicionActivo(activo);

        ValorizacionPosicionActivo valorizacion = new ValorizacionPosicionActivo(
                posicion,
                new BigDecimal("150")
        );

        assertEquals(0, valorizacion.getValorActual().compareTo(BigDecimal.ZERO));
        assertEquals(0, valorizacion.getGananciaPerdida().compareTo(BigDecimal.ZERO));
        assertEquals(0, valorizacion.getRendimientoPorcentual().compareTo(BigDecimal.ZERO));
    }

    @Test
    void deberiaAceptarPrecioActualCero() {
        PosicionActivo posicion = crearPosicionConCosto("100", "120");

        ValorizacionPosicionActivo valorizacion = new ValorizacionPosicionActivo(
                posicion,
                BigDecimal.ZERO
        );

        assertEquals(0, valorizacion.getValorActual().compareTo(BigDecimal.ZERO));
        assertEquals(0, valorizacion.getGananciaPerdida().compareTo(new BigDecimal("-12000")));
        assertEquals(0, valorizacion.getRendimientoPorcentual().compareTo(new BigDecimal("-100")));
    }

    @Test
    void deberiaRechazarPosicionNula() {
        assertThrows(
                NullPointerException.class,
                () -> new ValorizacionPosicionActivo(null, new BigDecimal("100"))
        );
    }

    @Test
    void deberiaRechazarPrecioActualNulo() {
        PosicionActivo posicion = crearPosicionConCosto("100", "120");

        assertThrows(
                NullPointerException.class,
                () -> new ValorizacionPosicionActivo(posicion, null)
        );
    }

    @Test
    void deberiaRechazarPrecioActualNegativo() {
        PosicionActivo posicion = crearPosicionConCosto("100", "120");

        assertThrows(
                IllegalArgumentException.class,
                () -> new ValorizacionPosicionActivo(posicion, new BigDecimal("-1"))
        );
    }

    @Test
    void deberiaDevolverRendimientoCeroCuandoNoHayCosto() {
        Activo activo = crearActivo();
        PosicionActivo posicion = new PosicionActivo(activo);

        ValorizacionPosicionActivo valorizacion = new ValorizacionPosicionActivo(
                posicion,
                new BigDecimal("100")
        );

        assertEquals(0, valorizacion.getRendimientoPorcentual().compareTo(BigDecimal.ZERO));
    }

    private PosicionActivo crearPosicionConCosto(String cantidad, String precio) {
        Activo activo = crearActivo();
        PosicionActivo posicion = new PosicionActivo(activo);
        posicion.aplicarMovimiento(
                new MovimientoActivo(
                        activo,
                        TipoMovimientoActivo.COMPRA,
                        new BigDecimal(cantidad),
                        new BigDecimal(precio)
                )
        );
        return posicion;
    }

    private Activo crearActivo() {
        Moneda moneda = new Moneda(
                "ARS",
                "Peso argentino",
                2,
                TipoMoneda.FIAT
        );
        return new Activo("Bono GD30", "GD30", moneda);
    }
}
