package ar.com.agmilevecich.sofp.domain;

import ar.com.agmilevecich.sofp.config.JpaManager;
import ar.com.agmilevecich.sofp.config.JpaTestManager;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoriaJpaTest {

    @Test
    void deberiaPersistirCategoria() {

        EntityManager em = JpaTestManager.createEntityManager();

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

        usuario.agregarPerfilFinanciero(perfil);

        Categoria categoria = new Categoria(
                "Supermercado",
                perfil
        );

        em.getTransaction().begin();

        em.persist(usuario);
        em.persist(perfil);
        em.persist(categoria);

        em.getTransaction().commit();

        Categoria recuperada =
                em.find(Categoria.class, categoria.getId());

        assertNotNull(recuperada);

        assertEquals(
                "Supermercado",
                recuperada.getNombre()
        );

        assertEquals(
                perfil.getId(),
                recuperada.getPerfilFinanciero().getId()
        );

        em.close();
        JpaTestManager.close();
    }
}