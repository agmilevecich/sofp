package ar.com.agmilevecich.sofp.persistence;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Usuario;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioRepositoryTest {

    @Test
    void deberiaGuardarYBuscarUsuarioPorId() {

        EntityManager em = JpaTestManager.createEntityManager();

        try {
            Usuario usuario = new Usuario(
                    "Ariel",
                    "Milevecich",
                    "usuario.id@test.com",
                    "hash123"
            );

            em.getTransaction().begin();

            UsuarioRepository repository =
                    new UsuarioRepository(em);

            repository.guardar(usuario);

            em.getTransaction().commit();

            Optional<Usuario> resultado =
                    repository.buscarPorId(usuario.getId());

            assertTrue(resultado.isPresent());
            assertEquals(
                    "Ariel",
                    resultado.get().getNombre()
            );
            assertEquals(
                    "usuario.id@test.com",
                    resultado.get().getEmail()
            );

        } finally {
            em.close();
        }
    }

    @Test
    void deberiaBuscarUsuarioPorEmail() {

        EntityManager em = JpaTestManager.createEntityManager();

        try {
            Usuario usuario = new Usuario(
                    "Ariel",
                    "Milevecich",
                    "usuario.email@test.com",
                    "hash123"
            );

            em.getTransaction().begin();

            UsuarioRepository repository =
                    new UsuarioRepository(em);

            repository.guardar(usuario);

            em.getTransaction().commit();

            Optional<Usuario> resultado =
                    repository.buscarPorEmail(
                            "usuario.email@test.com"
                    );

            assertTrue(resultado.isPresent());
            assertEquals(
                    usuario.getId(),
                    resultado.get().getId()
            );

        } finally {
            em.close();
        }
    }

    @Test
    void deberiaListarTodosLosUsuarios() {

        EntityManager em = JpaTestManager.createEntityManager();

        try {
            Usuario usuario1 = new Usuario(
                    "Ariel",
                    "Milevecich",
                    "usuario1@test.com",
                    "hash123"
            );

            Usuario usuario2 = new Usuario(
                    "Juan",
                    "Perez",
                    "usuario2@test.com",
                    "hash456"
            );

            em.getTransaction().begin();

            UsuarioRepository repository =
                    new UsuarioRepository(em);

            repository.guardar(usuario1);
            repository.guardar(usuario2);

            em.getTransaction().commit();

            List<Usuario> usuarios =
                    repository.listarTodos();

            assertEquals(2, usuarios.size());
            assertEquals(
                    "Ariel",
                    usuarios.get(0).getNombre()
            );
            assertEquals(
                    "Juan",
                    usuarios.get(1).getNombre()
            );

        } finally {
            em.close();
        }
    }

    @Test
    void deberiaActualizarUsuarioExistente() {

        EntityManager em = JpaTestManager.createEntityManager();

        try {
            Usuario usuario = new Usuario(
                    "Ariel",
                    "Milevecich",
                    "usuario.update@test.com",
                    "hash123"
            );

            UsuarioRepository repository =
                    new UsuarioRepository(em);

            em.getTransaction().begin();

            repository.guardar(usuario);

            em.getTransaction().commit();

            Long id = usuario.getId();

            em.clear();

            Usuario usuarioModificado =
                    repository.buscarPorId(id)
                            .orElseThrow();

            em.getTransaction().begin();

            usuarioModificado.desactivar();

            Usuario resultado =
                    repository.guardar(usuarioModificado);

            em.getTransaction().commit();

            em.clear();

            Usuario usuarioVerificado =
                    repository.buscarPorId(id)
                            .orElseThrow();

            assertEquals(
                    id,
                    usuarioVerificado.getId()
            );

            assertFalse(
                    usuarioVerificado.isActivo()
            );

        } finally {
            em.close();
        }
    }
}