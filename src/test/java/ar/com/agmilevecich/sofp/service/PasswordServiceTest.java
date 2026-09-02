package ar.com.agmilevecich.sofp.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordServiceTest {

    @Test
    void deberiaGenerarHashYVerificarContrasena() {

        String hash = PasswordService.hash("secreto");

        assertNotEquals("secreto", hash);
        assertTrue(PasswordService.matches("secreto", hash));
        assertFalse(PasswordService.matches("otra-clave", hash));
    }

    @Test
    void deberiaGenerarHashesDiferentesParaLaMismaContrasena() {

        String primerHash = PasswordService.hash("secreto");
        String segundoHash = PasswordService.hash("secreto");

        assertNotEquals(primerHash, segundoHash);
        assertTrue(PasswordService.matches("secreto", primerHash));
        assertTrue(PasswordService.matches("secreto", segundoHash));
    }

    @Test
    void deberiaRechazarHashConFormatoInvalido() {

        assertFalse(
                PasswordService.matches(
                        "secreto",
                        "hash-invalido"
                )
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoLaContrasenaEsNula() {

        assertThrows(
                NullPointerException.class,
                () -> PasswordService.hash(null)
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoElHashEsNulo() {

        assertThrows(
                NullPointerException.class,
                () -> PasswordService.matches("secreto", null)
        );
    }
}
