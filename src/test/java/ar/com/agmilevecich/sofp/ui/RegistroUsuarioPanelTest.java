package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.persistence.UsuarioRepository;
import ar.com.agmilevecich.sofp.service.UsuarioService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class RegistroUsuarioPanelTest {

    private EntityManager entityManager;
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();
        usuarioService = new UsuarioService(
                new UsuarioRepository(entityManager),
                entityManager
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
    void deberiaRegistrarUsuarioYPerfil() {
        AtomicReference<ar.com.agmilevecich.sofp.domain.Usuario> registrado =
                new AtomicReference<>();

        RegistroUsuarioPanel panel =
                new RegistroUsuarioPanel(usuarioService, registrado::set);

        panel.getNombreField().setText("Ariel");
        panel.getApellidoField().setText("Milevecich");
        panel.getEmailField().setText("registro." + System.nanoTime() + "@test.com");
        panel.getPasswordField().setText("secreto");
        panel.getPerfilField().setText("Perfil principal");

        panel.registrar();

        assertNotNull(registrado.get());
        assertTrue(usuarioService.buscarPorEmail(
                panel.getEmailField().getText()
        ).isPresent());

        var usuario = registrado.get();
        entityManager.clear();

        var perfiles = new ar.com.agmilevecich.sofp.persistence.PerfilFinancieroRepository(entityManager)
                .listarPorUsuario(usuario.getId());

        assertEquals(1, perfiles.size());
        assertEquals("Perfil principal", perfiles.get(0).getNombre());
    }

    @Test
    void deberiaRechazarEmailDuplicado() {
        String email = "duplicado." + System.nanoTime() + "@test.com";

        usuarioService.registrar(
                "Ariel",
                "Milevecich",
                email,
                "secreto",
                "Perfil principal"
        );

        RegistroUsuarioPanel panel =
                new RegistroUsuarioPanel(usuarioService, ignored -> fail("No debería registrar"));

        panel.getNombreField().setText("Otro");
        panel.getApellidoField().setText("Usuario");
        panel.getEmailField().setText(email);
        panel.getPasswordField().setText("secreto");
        panel.getPerfilField().setText("Perfil");

        assertThrows(IllegalArgumentException.class, panel::registrar);
    }

    @Test
    void deberiaRechazarDatosObligatoriosVacios() {
        RegistroUsuarioPanel panel =
                new RegistroUsuarioPanel(usuarioService, null);

        panel.getNombreField().setText("");
        panel.getApellidoField().setText("Usuario");
        panel.getEmailField().setText("usuario@test.com");
        panel.getPasswordField().setText("secreto");
        panel.getPerfilField().setText("Perfil");

        assertThrows(IllegalArgumentException.class, panel::registrar);
    }
}
