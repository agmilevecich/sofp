package ar.com.agmilevecich.sofp.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReporteCarteraActivoTest {

    @Test
    void deberiaConsolidarCostoValorGananciaYRendimientoDeLaCartera() {
        PosicionActivo gd30 = crearPosicion("GD30", "100", "100");
        PosicionActivo al30 = crearPosicion("AL30", "50", "100");

        ValorizacionPosicionActivo valorizacionGd30 = new ValorizacionPosicionActivo(
                gd30,
                new BigDecimal("120")
        );
        ValorizacionPosicionActivo valorizacionAl30 = new ValorizacionPosicionActivo(
                al30,
                new BigDecimal("80")
        );

        ReporteCarteraActivo reporte = new ReporteCarteraActivo(
                List.of(valorizacionGd30, valorizacionAl30)
        );

        assertEquals(2, reporte.getValorizaciones().size());
        assertEquals(0, reporte.getCostoTotal().compareTo(new BigDecimal("15000")));
        assertEquals(0, reporte.getValorActualTotal().compareTo(new BigDecimal("16000")));
        assertEquals(0, reporte.getGananciaPerdidaTotal().compareTo(new BigDecimal("1000")));
        assertEquals(
                0,
                reporte.getRendimientoPorcentualTotal()
                        .compareTo(new BigDecimal("6.666666666666666666666666666666667"))
        );
    }

    @Test
    void deberiaCalcularComposicionDetalladaPorValorActual() {
        PosicionActivo gd30 = crearPosicion("GD30", "100", "100");
        PosicionActivo al30 = crearPosicion("AL30", "50", "100");

        ValorizacionPosicionActivo valorizacionGd30 = new ValorizacionPosicionActivo(
                gd30,
                new BigDecimal("120")
        );
        ValorizacionPosicionActivo valorizacionAl30 = new ValorizacionPosicionActivo(
                al30,
                new BigDecimal("80")
        );

        ReporteCarteraActivo reporte = new ReporteCarteraActivo(
                List.of(valorizacionGd30, valorizacionAl30)
        );

        List<DetalleComposicionCarteraActivo> composicion = reporte.getComposicion();

        assertEquals(2, composicion.size());
        assertEquals(
                0,
                composicion.get(0).getParticipacionPorcentual()
                        .compareTo(new BigDecimal("75"))
        );
        assertEquals(
                0,
                composicion.get(1).getParticipacionPorcentual()
                        .compareTo(new BigDecimal("25"))
        );
        assertEquals(
                valorizacionGd30,
                composicion.get(0).getValorizacion()
        );
    }

    @Test
    void deberiaDevolverComposicionEnCeroParaCarteraSinValorActual() {
        PosicionActivo gd30 = crearPosicion("GD30", "100", "100");
        ValorizacionPosicionActivo valorizacion = new ValorizacionPosicionActivo(
                gd30,
                BigDecimal.ZERO
        );

        ReporteCarteraActivo reporte = new ReporteCarteraActivo(List.of(valorizacion));

        assertEquals(1, reporte.getComposicion().size());
        assertEquals(
                0,
                reporte.getComposicion().get(0).getParticipacionPorcentual()
                        .compareTo(BigDecimal.ZERO)
        );
    }

    @Test
    void deberiaDevolverTotalesEnCeroParaUnaCarteraVacia() {
        ReporteCarteraActivo reporte = new ReporteCarteraActivo(List.of());

        assertTrue(reporte.getValorizaciones().isEmpty());
        assertEquals(0, reporte.getCostoTotal().compareTo(BigDecimal.ZERO));
        assertEquals(0, reporte.getValorActualTotal().compareTo(BigDecimal.ZERO));
        assertEquals(0, reporte.getGananciaPerdidaTotal().compareTo(BigDecimal.ZERO));
        assertEquals(0, reporte.getRendimientoPorcentualTotal().compareTo(BigDecimal.ZERO));
        assertTrue(reporte.getComposicion().isEmpty());
    }

    @Test
    void deberiaRechazarListaNula() {
        assertThrows(
                NullPointerException.class,
                () -> new ReporteCarteraActivo(null)
        );
    }

    @Test
    void deberiaRechazarValorizacionNulaDentroDeLaLista() {
        assertThrows(
                NullPointerException.class,
                () -> new ReporteCarteraActivo(List.of((ValorizacionPosicionActivo) null))
        );
    }

    @Test
    void deberiaExponerListaInmodificable() {
        PosicionActivo posicion = crearPosicion("GD30", "100", "100");
        ValorizacionPosicionActivo valorizacion = new ValorizacionPosicionActivo(
                posicion,
                new BigDecimal("120")
        );

        ReporteCarteraActivo reporte = new ReporteCarteraActivo(List.of(valorizacion));

        assertThrows(
                UnsupportedOperationException.class,
                () -> reporte.getValorizaciones().clear()
        );
        assertThrows(
                UnsupportedOperationException.class,
                () -> reporte.getComposicion().clear()
        );
    }

    private PosicionActivo crearPosicion(String simbolo, String cantidad, String precio) {
        Moneda moneda = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        Activo activo = new Activo("Bono " + simbolo, simbolo, moneda);
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
}
