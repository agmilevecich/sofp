package ar.com.agmilevecich.sofp.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MonedaTest {

    @Test
    void deberiaCrearMonedaFiat() {

        Moneda moneda =
                new Moneda(
                        "ARS",
                        "Peso argentino",
                        2,
                        TipoMoneda.FIAT
                );

        assertEquals("ARS", moneda.getCodigo());
        assertEquals("Peso argentino", moneda.getNombre());
        assertEquals(2, moneda.getCantidadDecimales());
        assertEquals(TipoMoneda.FIAT, moneda.getTipo());
    }


    @Test
    void deberiaCrearMonedaCrypto() {

        Moneda moneda =
                new Moneda(
                        "BTC",
                        "Bitcoin",
                        8,
                        TipoMoneda.CRYPTO
                );

        assertEquals("BTC", moneda.getCodigo());
        assertEquals("Bitcoin", moneda.getNombre());
        assertEquals(8, moneda.getCantidadDecimales());
        assertEquals(TipoMoneda.CRYPTO, moneda.getTipo());
    }


    @Test
    void deberiaCambiarNombre() {

        Moneda moneda =
                new Moneda(
                        "USD",
                        "Dolar",
                        2,
                        TipoMoneda.FIAT
                );

        moneda.cambiarNombre("Dólar estadounidense");

        assertEquals(
                "Dólar estadounidense",
                moneda.getNombre()
        );
    }


    @Test
    void deberiaCambiarCantidadDecimales() {

        Moneda moneda =
                new Moneda(
                        "BTC",
                        "Bitcoin",
                        8,
                        TipoMoneda.CRYPTO
                );

        moneda.cambiarCantidadDecimales(6);

        assertEquals(
                6,
                moneda.getCantidadDecimales()
        );
    }
}