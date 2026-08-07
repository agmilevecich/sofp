package ar.com.agmilevecich.sofp.domain;

import ar.com.agmilevecich.sofp.config.JpaManager;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioJpaTest {

    @Test
    void deberiaPersistirUsuarioConPerfilFinanciero() {

        EntityManager em = JpaManager.createEntityManager();

        em.getTransaction().begin();

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Usuario",
                        "ariel@sofp.com",
                        "hash123"
                );

        PerfilFinanciero perfil =
                new PerfilFinanciero(
                        "Finanzas personales",
                        usuario
                );

        usuario.agregarPerfilFinanciero(perfil);

        em.persist(usuario);
        em.persist(perfil);

        em.getTransaction().commit();

        em.clear();

        Usuario usuarioGuardado =
                em.find(
                        Usuario.class,
                        usuario.getId()
                );

        assertNotNull(usuarioGuardado);
        assertEquals(
                "Ariel",
                usuarioGuardado.getNombre()
        );

        em.close();
    }
}