package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.UsuarioRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioServiceTest {

    private EntityManager entityManager;
    private UsuarioRepository usuarioRepository;
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {

        JpaTestManager.close();

        entityManager =
                JpaTestManager.createEntityManager();

        usuarioRepository =
                new UsuarioRepository(entityManager);

        usuarioService =
                new UsuarioService(usuarioRepository);
    }

    @AfterEach
    void tearDown() {

        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }

        JpaTestManager.close();
    }

    @Test
    void deberiaGuardarYBuscarUsuarioPorId() {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Milevecich",
                        "ariel.usuario.id." + System.nanoTime() + "@test.com",
                        "hash-test"
                );

        entityManager.getTransaction().begin();

        usuarioService.guardar(usuario);

        entityManager.getTransaction().commit();

        Optional<Usuario> resultado =
                usuarioService.buscarPorId(
                        usuario.getId()
                );

        assertTrue(resultado.isPresent());

        assertEquals(
                usuario.getId(),
                resultado.get().getId()
        );

        assertEquals(
                "Ariel",
                resultado.get().getNombre()
        );

        assertEquals(
                "Milevecich",
                resultado.get().getApellido()
        );

        assertEquals(
                "ariel.usuario.id." + usuario.getEmail()
                        .substring(
                                "ariel.usuario.id.".length()
                        ),
                resultado.get().getEmail()
        );

        assertTrue(
                resultado.get().isActivo()
        );
    }

    @Test
    void deberiaBuscarUsuarioPorEmail() {

        String email =
                "ariel.usuario.email."
                        + System.nanoTime()
                        + "@test.com";

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Milevecich",
                        email,
                        "hash-test"
                );

        entityManager.getTransaction().begin();

        usuarioService.guardar(usuario);

        entityManager.getTransaction().commit();

        Optional<Usuario> resultado =
                usuarioService.buscarPorEmail(email);

        assertTrue(resultado.isPresent());

        assertEquals(
                usuario.getId(),
                resultado.get().getId()
        );

        assertEquals(
                email,
                resultado.get().getEmail()
        );
    }

    @Test
    void deberiaListarTodosLosUsuarios() {

        Usuario usuario1 =
                new Usuario(
                        "Ariel",
                        "Milevecich",
                        "ariel.usuario.lista1."
                                + System.nanoTime()
                                + "@test.com",
                        "hash-test"
                );

        Usuario usuario2 =
                new Usuario(
                        "Juan",
                        "Perez",
                        "juan.usuario.lista2."
                                + System.nanoTime()
                                + "@test.com",
                        "hash-test"
                );

        entityManager.getTransaction().begin();

        usuarioService.guardar(usuario1);
        usuarioService.guardar(usuario2);

        entityManager.getTransaction().commit();

        List<Usuario> usuarios =
                usuarioService.listarTodos();

        assertEquals(
                2,
                usuarios.size()
        );

        assertEquals(
                "Ariel",
                usuarios.get(0).getNombre()
        );

        assertEquals(
                "Juan",
                usuarios.get(1).getNombre()
        );
    }

    @Test
    void deberiaActivarUsuario() {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Milevecich",
                        "ariel.usuario.activar."
                                + System.nanoTime()
                                + "@test.com",
                        "hash-test"
                );

        entityManager.getTransaction().begin();

        usuarioService.guardar(usuario);

        entityManager.getTransaction().commit();

        entityManager.clear();

        entityManager.getTransaction().begin();

        Usuario usuarioDesactivado =
                usuarioService.desactivar(
                        usuario.getId()
                );

        entityManager.getTransaction().commit();

        assertFalse(
                usuarioDesactivado.isActivo()
        );

        entityManager.clear();

        entityManager.getTransaction().begin();

        Usuario usuarioActivado =
                usuarioService.activar(
                        usuario.getId()
                );

        entityManager.getTransaction().commit();

        assertTrue(
                usuarioActivado.isActivo()
        );
    }

    @Test
    void deberiaDesactivarUsuario() {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Milevecich",
                        "ariel.usuario.desactivar."
                                + System.nanoTime()
                                + "@test.com",
                        "hash-test"
                );

        entityManager.getTransaction().begin();

        usuarioService.guardar(usuario);

        entityManager.getTransaction().commit();

        assertTrue(
                usuario.isActivo()
        );

        entityManager.clear();

        entityManager.getTransaction().begin();

        Usuario usuarioDesactivado =
                usuarioService.desactivar(
                        usuario.getId()
                );

        entityManager.getTransaction().commit();

        assertFalse(
                usuarioDesactivado.isActivo()
        );

        entityManager.clear();

        Optional<Usuario> resultado =
                usuarioService.buscarPorId(
                        usuario.getId()
                );

        assertTrue(resultado.isPresent());

        assertFalse(
                resultado.get().isActivo()
        );
    }
}