package ar.com.agmilevecich.sofp.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PerfilFinancieroTest {

    @Test
    void deberiaCrearPerfilFinancieroActivo() {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Usuario",
                        "ariel@test.com",
                        "hash123"
                );

        PerfilFinanciero perfil =
                new PerfilFinanciero(
                        "Finanzas personales",
                        usuario
                );

        assertEquals(
                "Finanzas personales",
                perfil.getNombre()
        );

        assertEquals(
                usuario,
                perfil.getUsuario()
        );

        assertTrue(perfil.isActivo());
    }


    @Test
    void deberiaAgregarPerfilAlUsuario() {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Usuario",
                        "ariel@test.com",
                        "hash123"
                );

        PerfilFinanciero perfil =
                new PerfilFinanciero(
                        "Viaje Rio 2027",
                        usuario
                );

        usuario.agregarPerfilFinanciero(perfil);

        assertEquals(
                1,
                usuario.getPerfilesFinancieros().size()
        );

        assertEquals(
                usuario,
                usuario.getPerfilesFinancieros()
                        .get(0)
                        .getUsuario()
        );
    }


    @Test
    void deberiaDesactivarPerfil() {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Usuario",
                        "ariel@test.com",
                        "hash123"
                );

        PerfilFinanciero perfil =
                new PerfilFinanciero(
                        "Inversiones",
                        usuario
                );

        perfil.desactivar();

        assertFalse(perfil.isActivo());
    }


    @Test
    void deberiaCambiarDescripcion() {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Usuario",
                        "ariel@test.com",
                        "hash123"
                );

        PerfilFinanciero perfil =
                new PerfilFinanciero(
                        "Viaje Rio 2027",
                        usuario
                );

        perfil.cambiarDescripcion(
                "Fondo destinado al viaje"
        );

        assertEquals(
                "Fondo destinado al viaje",
                perfil.getDescripcion()
        );
    }
}
