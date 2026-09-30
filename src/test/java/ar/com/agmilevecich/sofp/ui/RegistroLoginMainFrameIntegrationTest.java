package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.TipoCuenta;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.CuentaRepository;
import ar.com.agmilevecich.sofp.persistence.InstitucionFinancieraRepository;
import ar.com.agmilevecich.sofp.persistence.MonedaRepository;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import ar.com.agmilevecich.sofp.persistence.PerfilFinancieroRepository;
import ar.com.agmilevecich.sofp.persistence.UsuarioRepository;
import ar.com.agmilevecich.sofp.service.CuentaService;
import ar.com.agmilevecich.sofp.service.PerfilFinancieroService;
import ar.com.agmilevecich.sofp.service.UsuarioService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.JList;
import java.awt.Component;
import java.awt.Container;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class RegistroLoginMainFrameIntegrationTest {

    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();
    }

    @AfterEach
    void tearDown() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
        JpaTestManager.close();
    }

    @Test
    void deberiaCompletarRegistroLoginSeleccionDePerfilYAccesoAlMainFrame() throws Exception {
        UsuarioService usuarioService = new UsuarioService(
                new UsuarioRepository(entityManager),
                entityManager
        );
        PerfilFinancieroService perfilService = new PerfilFinancieroService(
                new PerfilFinancieroRepository(entityManager)
        );

        String email = "e2e." + System.nanoTime() + "@test.com";
        usuarioService.registrar(
                "Ariel",
                "Usuario",
                email,
                "secreto",
                "Perfil principal"
        );

        AtomicReference<Usuario> autenticado = new AtomicReference<>();
        LoginPanel loginPanel = new LoginPanel(usuarioService, autenticado::set);
        loginPanel.getEmailField().setText(email);
        loginPanel.getPasswordField().setText("secreto");
        loginPanel.autenticar();

        assertNotNull(autenticado.get());

        List<PerfilFinanciero> perfiles =
                perfilService.listarPorUsuario(autenticado.get().getId());

        assertEquals(1, perfiles.size());
        PerfilFinanciero perfil = perfiles.get(0);
        assertEquals("Perfil principal", perfil.getNombre());

        Moneda moneda = new Moneda(
                "ARS",
                "Peso argentino",
                2,
                TipoMoneda.FIAT
        );
        InstitucionFinanciera institucion = new InstitucionFinanciera(
                "Banco E2E",
                TipoInstitucionFinanciera.BANCO,
                autenticado.get()
        );

        entityManager.getTransaction().begin();
        entityManager.persist(moneda);
        entityManager.persist(institucion);
        entityManager.getTransaction().commit();

        CuentaService cuentaService = new CuentaService(
                new CuentaRepository(entityManager),
                new MovimientoRepository(entityManager),
                entityManager
        );
        Cuenta cuenta = cuentaService.registrar(
                new Cuenta(
                        "Cuenta principal",
                        TipoCuenta.CAJA_AHORRO,
                        perfil,
                        institucion,
                        moneda
                ),
                autenticado.get().getId()
        );

        AtomicReference<MainFrame> frameRef = new AtomicReference<>();
        javax.swing.SwingUtilities.invokeAndWait(() -> frameRef.set(
                new MainFrame(
                        cuentaService,
                        perfil.getId(),
                        autenticado.get().getId()
                )
        ));

        MainFrame frame = frameRef.get();
        assertNotNull(frame);
        JList<?> lista = buscarLista(frame.getContentPane());
        assertNotNull(lista);
        assertEquals(1, lista.getModel().getSize());
        assertEquals(cuenta.getNombre(), lista.getModel().getElementAt(0));

        frame.dispose();
    }

    private JList<?> buscarLista(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof JList<?> lista) {
                return lista;
            }
            if (component instanceof Container hijo) {
                JList<?> encontrada = buscarLista(hijo);
                if (encontrada != null) {
                    return encontrada;
                }
            }
        }
        return null;
    }
}
