package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.UsuarioRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioServiceAutenticacionTest {

    private EntityManager entityManager;
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {

        JpaTestManager.close();
        entityManager = JpaTestManager.createEntityManager();
        usuarioService = new UsuarioService(
                new UsuarioRepository(entityManager)
        );
    }

    @AfterEach
    void tearDown() {

        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }

        JpaTestManager.close();
    }

    @Test
    void deberiaAutenticarUsuarioActivoConContrasenaCorrecta() {

        String email = "autenticacion.correcta." + System.nanoTime() + "@test.com";
        Usuario usuario = new Usuario(
                "Ariel",
                "Milevecich",
                email,
                PasswordService.hash("secreto")
        );

        entityManager.getTransaction().begin();
        usuarioService.guardar(usuario);
        entityManager.getTransaction().commit();

        Optional<Usuario> resultado =
                usuarioService.autenticar(email, "secreto");

        assertTrue(resultado.isPresent());
        assertEquals(usuario.getId(), resultado.get().getId());
    }

    @Test
    void deberiaRechazarContrasenaIncorrecta() {

        String email = "autenticacion.incorrecta." + System.nanoTime() + "@test.com";
        Usuario usuario = new Usuario(
                "Ariel",
                "Milevecich",
                email,
                PasswordService.hash("secreto")
        );

        entityManager.getTransaction().begin();
        usuarioService.guardar(usuario);
        entityManager.getTransaction().commit();

        assertTrue(
                usuarioService.autenticar(email, "otra-clave").isEmpty()
        );
    }

    @Test
    void deberiaRechazarUsuarioInexistente() {

        assertTrue(
                usuarioService.autenticar(
                        "usuario.inexistente@test.com",
                        "secreto"
                ).isEmpty()
        );
    }

    @Test
    void deberiaRechazarUsuarioInactivo() {

        String email = "autenticacion.inactivo." + System.nanoTime() + "@test.com";
        Usuario usuario = new Usuario(
                "Ariel",
                "Milevecich",
                email,
                PasswordService.hash("secreto")
        );
        usuario.desactivar();

        entityManager.getTransaction().begin();
        usuarioService.guardar(usuario);
        entityManager.getTransaction().commit();

        assertTrue(
                usuarioService.autenticar(email, "secreto").isEmpty()
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoEmailEsNulo() {

        assertThrows(
                NullPointerException.class,
                () -> usuarioService.autenticar(null, "secreto")
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoContrasenaEsNula() {

        assertThrows(
                NullPointerException.class,
                () -> usuarioService.autenticar(
                        "usuario@test.com",
                        null
                )
        );
    }
}
