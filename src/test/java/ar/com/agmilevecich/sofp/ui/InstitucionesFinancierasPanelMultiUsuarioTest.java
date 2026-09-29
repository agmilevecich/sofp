package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.InstitucionFinancieraRepository;
import ar.com.agmilevecich.sofp.service.InstitucionFinancieraService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.SwingUtilities;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InstitucionesFinancierasPanelMultiUsuarioTest {

    private EntityManager entityManager;
    private InstitucionFinancieraService service;
    private Usuario usuarioA;
    private Usuario usuarioB;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();
        service = new InstitucionFinancieraService(
                new InstitucionFinancieraRepository(entityManager),
                entityManager
        );

        usuarioA = new Usuario("Usuario", "A", "panel.a." + System.nanoTime() + "@test.local", "hash");
        usuarioB = new Usuario("Usuario", "B", "panel.b." + System.nanoTime() + "@test.local", "hash");

        entityManager.getTransaction().begin();
        entityManager.persist(usuarioA);
        entityManager.persist(usuarioB);
        entityManager.getTransaction().commit();

        service.registrar(
                new InstitucionFinanciera("Banco A", TipoInstitucionFinanciera.BANCO),
                usuarioA.getId()
        );
        service.registrar(
                new InstitucionFinanciera("Banco B", TipoInstitucionFinanciera.BANCO),
                usuarioB.getId()
        );
    }

    @AfterEach
    void tearDown() {
        if (entityManager != null && entityManager.isOpen()) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            entityManager.close();
        }
        JpaTestManager.close();
    }

    @Test
    void cadaPanelDebeMostrarSoloLasInstitucionesDeSuUsuario() throws Exception {
        AtomicReference<InstitucionesFinancierasPanel> panelA = new AtomicReference<>();
        AtomicReference<InstitucionesFinancierasPanel> panelB = new AtomicReference<>();

        SwingUtilities.invokeAndWait(() ->
                panelA.set(new InstitucionesFinancierasPanel(service, usuarioA.getId())));
        SwingUtilities.invokeAndWait(() ->
                panelB.set(new InstitucionesFinancierasPanel(service, usuarioB.getId())));

        assertEquals(1, panelA.get().getListaInstituciones().getModel().getSize());
        assertEquals(
                "Banco A — BANCO — Activa",
                panelA.get().getListaInstituciones().getModel().getElementAt(0)
        );

        assertEquals(1, panelB.get().getListaInstituciones().getModel().getSize());
        assertEquals(
                "Banco B — BANCO — Activa",
                panelB.get().getListaInstituciones().getModel().getElementAt(0)
        );
    }
}
