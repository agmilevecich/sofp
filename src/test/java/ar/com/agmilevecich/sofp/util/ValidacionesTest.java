package ar.com.agmilevecich.sofp.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidacionesTest {

    @Test
    void deberiaAceptarTextoValido() {

        String texto = Validaciones.textoObligatorio(
                "Santander",
                "Error");

        assertEquals("Santander", texto);
    }

    @Test
    void deberiaLanzarExcepcionSiEsNull() {

        assertThrows(
                IllegalArgumentException.class,
                () -> Validaciones.textoObligatorio(
                        null,
                        "Error")
        );
    }

    @Test
    void deberiaLanzarExcepcionSiEstaVacio() {

        assertThrows(
                IllegalArgumentException.class,
                () -> Validaciones.textoObligatorio(
                        "",
                        "Error")
        );
    }

    @Test
    void deberiaLanzarExcepcionSiSoloTieneEspacios() {

        assertThrows(
                IllegalArgumentException.class,
                () -> Validaciones.textoObligatorio(
                        "     ",
                        "Error")
        );
    }

}