package ar.com.agmilevecich.sofp.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    @Test
    void deberiaCrearUsuarioActivo() {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Usuario",
                        "ariel@test.com",
                        "hash123"
                );

        assertEquals("Ariel", usuario.getNombre());
        assertEquals("Usuario", usuario.getApellido());
        assertEquals("ariel@test.com", usuario.getEmail());
        assertTrue(usuario.isActivo());
        assertNotNull(usuario.getPerfilesFinancieros());
    }

    @Test
    void deberiaDesactivarUsuario() {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Usuario",
                        "ariel@test.com",
                        "hash123"
                );

        usuario.desactivar();

        assertFalse(usuario.isActivo());
    }

    @Test
    void deberiaActivarUsuario() {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Usuario",
                        "ariel@test.com",
                        "hash123"
                );

        usuario.desactivar();
        usuario.activar();

        assertTrue(usuario.isActivo());
    }
}
