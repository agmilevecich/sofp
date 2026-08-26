package ar.com.agmilevecich.sofp.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CalculadorPosicionActivoTest {

    @Test
    void deberiaCalcularPosicionA partirDeMovimientos() {
        Activo activo = crearActivo("Bono GD30");

        PosicionActivo posicion = CalculadorPosicionActivo.calcular(
                activo,
                List.of(
                        crearMovimiento(activo, TipoMovimientoActivo.COMPRA, "100", "125"),
                        crearMovimiento(activo, TipoMovimientoActivo.COMPRA, "50", "130"),
                        crearMovimiento(activo, TipoMovimientoActivo.VENTA, "30", "135")
                )
        );

        assertEquals(activo, posicion.getActivo());
        assertEquals(0, posicion.getCantidad().compareTo(new BigDecimal("120")));
    }

    @Test
    void deberiaCalcularPosicionCeroSinMovimientos() {
        Activo activo = crearActivo("Bono GD30");

        PosicionActivo posicion = CalculadorPosicionActivo.calcular(
                activo,
                List.of()
        );

        assertEquals(0, posicion.getCantidad().compareTo(BigDecimal.ZERO));
    }

    @Test
    void deberiaRechazarActivoNulo() {
        assertThrows(
                NullPointerException.class,
                () -> CalculadorPosicionActivo.calcular(null, List.of())
        );
    }

    @Test
    void deberiaRechazarMovimientosNulos() {
        Activo activo = crearActivo("Bono GD30");

        assertThrows(
                NullPointerException.class,
                () -> CalculadorPosicionActivo.calcular(activo, null)
        );
    }

    @Test
    void deberiaRechazarMovimientoDeOtroActivo() {
        Activo activo = crearActivo("Bono GD30");
        Activo otroActivo = crearActivo("Bono AL30");

        assertThrows(
                IllegalArgumentException.class,
                () -> CalculadorPosicionActivo.calcular(
                        activo,
                        List.of(
                                crearMovimiento(activo, TipoMovimientoActivo.COMPRA, "100", "125"),
                                crearMovimiento(otroActivo, TipoMovimientoActivo.COMPRA, "20", "130")
                        )
                )
        );
    }

    @Test
    void deberiaRechazarVentaQueDejaPosicionNegativa() {
        Activo activo = crearActivo("Bono GD30");

        assertThrows(
                IllegalArgumentException.class,
                () -> CalculadorPosicionActivo.calcular(
                        activo,
                        List.of(
                                crearMovimiento(activo, TipoMovimientoActivo.COMPRA, "100", "125"),
                                crearMovimiento(activo, TipoMovimientoActivo.VENTA, "101", "130")
                        )
                )
        );
    }

    @Test
    void deberiaRespetarElOrdenDeLosMovimientos() {
        Activo activo = crearActivo("Bono GD30");

        assertThrows(
                IllegalArgumentException.class,
                () -> CalculadorPosicionActivo.calcular(
                        activo,
                        List.of(
                                crearMovimiento(activo, TipoMovimientoActivo.VENTA, "50", "130"),
                                crearMovimiento(activo, TipoMovimientoActivo.COMPRA, "100", "125")
                        )
                )
        );
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
