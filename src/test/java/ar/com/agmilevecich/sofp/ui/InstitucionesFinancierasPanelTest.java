package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.persistence.InstitucionFinancieraRepository;
import ar.com.agmilevecich.sofp.service.InstitucionFinancieraService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InstitucionesFinancierasPanelTest {

    private EntityManager entityManager;
    private InstitucionFinancieraService service;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();
        service = new InstitucionFinancieraService(
                new InstitucionFinancieraRepository(entityManager),
                entityManager
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
    void deberiaMostrarInstitucionesYPermitirRegistrarUnaNueva() throws Exception {
        service.registrar(new InstitucionFinanciera(
                "Banco Existente",
                TipoInstitucionFinanciera.BANCO
        ));

        AtomicReference<InstitucionesFinancierasPanel> panelRef = new AtomicReference<>();

        SwingUtilities.invokeAndWait(() -> panelRef.set(
                new InstitucionesFinancierasPanel(service)
        ));

        InstitucionesFinancierasPanel panel = panelRef.get();
        assertNotNull(panel);
        assertEquals(1, panel.getListaInstituciones().getModel().getSize());
        assertEquals(
                "Banco Existente — BANCO — Activa",
                panel.getListaInstituciones().getModel().getElementAt(0)
        );

        panel.getNombreField().setText("Mercado Pago");
        panel.getTipoComboBox().setSelectedItem(TipoInstitucionFinanciera.FINTECH);

        SwingUtilities.invokeAndWait(panel::registrarInstitucion);

        JList<String> lista = panel.getListaInstituciones();
        assertEquals(2, lista.getModel().getSize());
        assertEquals(
                "Mercado Pago — FINTECH — Activa",
                lista.getModel().getElementAt(1)
        );
        assertEquals(2, service.listarTodas().size());
    }

    @Test
    void deberiaMostrarElBotonRegistrar() throws Exception {
        AtomicReference<InstitucionesFinancierasPanel> panelRef = new AtomicReference<>();

        SwingUtilities.invokeAndWait(() -> panelRef.set(
                new InstitucionesFinancierasPanel(service)
        ));

        JButton boton = buscarBoton(panelRef.get(), "Registrar");
        assertNotNull(boton);
    }

    @Test
    void deberiaIniciarElComboConSeleccione() throws Exception {
        AtomicReference<InstitucionesFinancierasPanel> panelRef = new AtomicReference<>();

        SwingUtilities.invokeAndWait(() -> panelRef.set(
                new InstitucionesFinancierasPanel(service)
        ));

        InstitucionesFinancierasPanel panel = panelRef.get();

        assertNull(panel.getTipoComboBox().getSelectedItem());
        Component renderer = panel.getTipoComboBox().getRenderer()
                .getListCellRendererComponent(
                        new JList<>(),
                        null,
                        0,
                        false,
                        false
                );
        assertEquals("Seleccione...", ((javax.swing.JLabel) renderer).getText());
    }

    private JButton buscarBoton(Container container, String texto) {
        for (Component component : container.getComponents()) {
            if (component instanceof JButton boton && texto.equals(boton.getText())) {
                return boton;
            }
            if (component instanceof Container hijo) {
                JButton encontrado = buscarBoton(hijo, texto);
                if (encontrado != null) {
                    return encontrado;
                }
            }
        }
        return null;
    }
}
