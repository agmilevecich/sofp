package ar.com.agmilevecich.sofp.domain;

import org.junit.jupiter.api.Test;
import static ar.com.agmilevecich.sofp.domain.TipoMovimiento.EGRESO;
import static ar.com.agmilevecich.sofp.domain.TipoMovimiento.INGRESO;

import static org.junit.jupiter.api.Assertions.*;

class CategoriaTest {

    @Test
    void deberiaCrearCategoria() {

        Usuario usuario = new Usuario(
                "Ariel",
                "Milevecich",
                "ariel@test.com",
                "hash"
        );

        PerfilFinanciero perfil = new PerfilFinanciero(
                "Personal",
                usuario
        );

        Categoria categoria = new Categoria(
                "Supermercado",
                perfil
        );

        assertEquals("Supermercado", categoria.getNombre());
        assertEquals("Supermercado", categoria.toString());
        assertTrue(categoria.isActiva());
        assertEquals(perfil, categoria.getPerfilFinanciero());
    }

    @Test
    void deberiaRenombrarCategoria() {

        Usuario usuario = new Usuario(
                "Ariel",
                "Milevecich",
                "ariel@test.com",
                "hash"
        );

        PerfilFinanciero perfil = new PerfilFinanciero(
                "Personal",
                usuario
        );

        Categoria categoria = new Categoria(
                "Supermercado",
                perfil
        );

        categoria.renombrar("Alimentación");

        assertEquals("Alimentación", categoria.getNombre());
    }

    @Test
    void deberiaCambiarDescripcion() {

        Usuario usuario = new Usuario(
                "Ariel",
                "Milevecich",
                "ariel@test.com",
                "hash"
        );

        PerfilFinanciero perfil = new PerfilFinanciero(
                "Personal",
                usuario
        );

        Categoria categoria = new Categoria(
                "Supermercado",
                perfil
        );

        categoria.cambiarDescripcion("Compras de alimentos");

        assertEquals(
                "Compras de alimentos",
                categoria.getDescripcion()
        );
    }

    @Test
    void deberiaDesactivarCategoria() {

        Usuario usuario = new Usuario(
                "Ariel",
                "Milevecich",
                "ariel@test.com",
                "hash"
        );

        PerfilFinanciero perfil = new PerfilFinanciero(
                "Personal",
                usuario
        );

        Categoria categoria = new Categoria(
                "Supermercado",
                perfil
        );

        categoria.desactivar();

        assertFalse(categoria.isActiva());

        categoria.activar();

        assertTrue(categoria.isActiva());
    }
    @Test
    void deberiaCrearCategoriaConTipoMovimiento() {
        Usuario usuario = new Usuario("Ariel", "Milevecich", "ariel-tipo@test.com", "hash");
        PerfilFinanciero perfil = new PerfilFinanciero("Personal", usuario);
        Categoria categoria = new Categoria("Sueldo", perfil, INGRESO);

        assertEquals(INGRESO, categoria.getTipoMovimiento());

        categoria.cambiarTipoMovimiento(EGRESO);
        assertEquals(EGRESO, categoria.getTipoMovimiento());
    }

}