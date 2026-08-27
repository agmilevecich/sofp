package ar.com.agmilevecich.sofp.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BonoTest {

    @Test
    void deberiaCrearBono() {

        Moneda moneda =
                new Moneda(
                        "ARS",
                        "Peso argentino",
                        2,
                        TipoMoneda.FIAT
                );

        Bono bono =
                new Bono(
                        "Bono GD30",
                        "GD30",
                        moneda
                );

        assertEquals(
                "Bono GD30",
                bono.getNombre()
        );

        assertEquals(
                "GD30",
                bono.getSimbolo()
        );

        assertEquals(
                moneda,
                bono.getMoneda()
        );
    }

    @Test
    void deberiaHeredarElComportamientoDeActivo() {

        Moneda monedaInicial =
                new Moneda(
                        "ARS",
                        "Peso argentino",
                        2,
                        TipoMoneda.FIAT
                );

        Moneda nuevaMoneda =
                new Moneda(
                        "USD",
                        "Dolar estadounidense",
                        2,
                        TipoMoneda.FIAT
                );

        Bono bono =
                new Bono(
                        "Bono GD30",
                        "GD30",
                        monedaInicial
                );

        bono.cambiarNombre("Bono GD30 2030");
        bono.cambiarMoneda(nuevaMoneda);

        assertEquals(
                "Bono GD30 2030",
                bono.getNombre()
        );
        assertEquals(
                "GD30",
                bono.getSimbolo()
        );
        assertEquals(
                nuevaMoneda,
                bono.getMoneda()
        );
    }

    @Test
    void deberiaRechazarNombreNulo() {

        Moneda moneda =
                new Moneda(
                        "ARS",
                        "Peso argentino",
                        2,
                        TipoMoneda.FIAT
                );

        assertThrows(
                NullPointerException.class,
                () -> new Bono(null, "GD30", moneda)
        );
    }

    @Test
    void deberiaRechazarSimboloNulo() {

        Moneda moneda =
                new Moneda(
                        "ARS",
                        "Peso argentino",
                        2,
                        TipoMoneda.FIAT
                );

        assertThrows(
                NullPointerException.class,
                () -> new Bono("Bono GD30", null, moneda)
        );
    }

    @Test
    void deberiaRechazarMonedaNula() {

        assertThrows(
                NullPointerException.class,
                () -> new Bono("Bono GD30", "GD30", null)
        );
    }

    @Test
    void deberiaSerUnaEspecializacionDeActivo() {

        Moneda moneda =
                new Moneda(
                        "ARS",
                        "Peso argentino",
                        2,
                        TipoMoneda.FIAT
                );

        Bono bono =
                new Bono(
                        "Bono GD30",
                        "GD30",
                        moneda
                );

        assertInstanceOf(Activo.class, bono);
    }
}
