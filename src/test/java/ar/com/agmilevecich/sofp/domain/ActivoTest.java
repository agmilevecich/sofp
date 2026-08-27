package ar.com.agmilevecich.sofp.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActivoTest {

    @Test
    void deberiaCrearActivo() {

        Moneda moneda =
                new Moneda(
                        "ARS",
                        "Peso argentino",
                        2,
                        TipoMoneda.FIAT
                );

        Activo activo =
                new Activo(
                        "Bono GD30",
                        moneda
                );

        assertEquals(
                "Bono GD30",
                activo.getNombre()
        );

        assertEquals(
                moneda,
                activo.getMoneda()
        );
    }


    @Test
    void deberiaCrearActivoConMonedaCrypto() {

        Moneda moneda =
                new Moneda(
                        "USDT",
                        "Tether",
                        6,
                        TipoMoneda.CRYPTO
                );

        Activo activo =
                new Activo(
                        "Tether USD",
                        moneda
                );

        assertEquals(
                "Tether USD",
                activo.getNombre()
        );

        assertEquals(
                moneda,
                activo.getMoneda()
        );
    }


    @Test
    void deberiaCambiarNombre() {

        Moneda moneda =
                new Moneda(
                        "ARS",
                        "Peso argentino",
                        2,
                        TipoMoneda.FIAT
                );

        Activo activo =
                new Activo(
                        "Bono GD30",
                        moneda
                );

        activo.cambiarNombre("Bono GD30 2030");

        assertEquals(
                "Bono GD30 2030",
                activo.getNombre()
        );
    }


    @Test
    void deberiaCambiarMoneda() {

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

        Activo activo =
                new Activo(
                        "Bono GD30",
                        monedaInicial
                );

        activo.cambiarMoneda(nuevaMoneda);

        assertEquals(
                nuevaMoneda,
                activo.getMoneda()
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
                () -> new Activo(
                        null,
                        moneda
                )
        );
    }


    @Test
    void deberiaRechazarMonedaNula() {

        assertThrows(
                NullPointerException.class,
                () -> new Activo(
                        "Bono GD30",
                        null
                )
        );
    }


    @Test
    void deberiaRechazarNombreNuloAlCambiarlo() {

        Moneda moneda =
                new Moneda(
                        "ARS",
                        "Peso argentino",
                        2,
                        TipoMoneda.FIAT
                );

        Activo activo =
                new Activo(
                        "Bono GD30",
                        moneda
                );

        assertThrows(
                NullPointerException.class,
                () -> activo.cambiarNombre(null)
        );
    }


    @Test
    void deberiaRechazarMonedaNulaAlCambiarla() {

        Moneda moneda =
                new Moneda(
                        "ARS",
                        "Peso argentino",
                        2,
                        TipoMoneda.FIAT
                );

        Activo activo =
                new Activo(
                        "Bono GD30",
                        moneda
                );

        assertThrows(
                NullPointerException.class,
                () -> activo.cambiarMoneda(null)
        );
    }
}
