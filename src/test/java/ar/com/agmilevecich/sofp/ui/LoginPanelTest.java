package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.UsuarioRepository;
import ar.com.agmilevecich.sofp.service.PasswordService;
import ar.com.agmilevecich.sofp.service.UsuarioService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class LoginPanelTest {

    private EntityManager entityManager;
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();
        usuarioService = new UsuarioService(new UsuarioRepository(entityManager));
    }

    @AfterEach
    void tearDown() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
        JpaTestManager.close();
    }

    @Test
    void deberiaConstruirFormularioBaseDeshabilitado() {
        LoginPanel panel = new LoginPanel();

        assertNotNull(panel.getEmailField());
        assertNotNull(panel.getPasswordField());
        assertNotNull(panel.getIngresarButton());
        assertFalse(panel.getIngresarButton().isEnabled());
    }

    @Test
    void deberiaAutenticarYNotificarUsuario() {
        Usuario usuario = new Usuario(
                "Ariel",
                "Usuario",
                "ariel@example.com",
                PasswordService.hash("secreto")
        );
        entityManager.getTransaction().begin();
        entityManager.persist(usuario);
        entityManager.getTransaction().commit();

        AtomicReference<Usuario> usuarioAutenticado = new AtomicReference<>();
        LoginPanel panel = new LoginPanel(usuarioService, usuarioAutenticado::set);
        panel.getEmailField().setText("ariel@example.com");
        panel.getPasswordField().setText("secreto");

        panel.autenticar();

        assertNotNull(usuarioAutenticado.get());
        assertEquals(usuario.getId(), usuarioAutenticado.get().getId());
    }

    @Test
    void deberiaRechazarCredencialesInvalidas() {
        Usuario usuario = new Usuario(
                "Ariel",
                "Usuario",
                "ariel@example.com",
                PasswordService.hash("secreto")
        );
        entityManager.getTransaction().begin();
        entityManager.persist(usuario);
        entityManager.getTransaction().commit();

        LoginPanel panel = new LoginPanel(usuarioService, ignored -> fail("No debería autenticar"));
        panel.getEmailField().setText("ariel@example.com");
        panel.getPasswordField().setText("incorrecta");

        assertThrows(IllegalArgumentException.class, panel::autenticar);
    }

    @Test
    void deberiaRechazarUsuarioInexistente() {
        LoginPanel panel = new LoginPanel(usuarioService, ignored -> fail("No debería autenticar"));
        panel.getEmailField().setText("inexistente@example.com");
        panel.getPasswordField().setText("secreto");

        assertThrows(IllegalArgumentException.class, panel::autenticar);
    }

    @Test
    void deberiaRequerirServicioDeUsuarios() {
        LoginPanel panel = new LoginPanel();

        assertThrows(NullPointerException.class, panel::autenticar);
    }
}
