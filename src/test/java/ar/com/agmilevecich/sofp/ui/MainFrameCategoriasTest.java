package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.CategoriaRepository;
import ar.com.agmilevecich.sofp.persistence.CuentaRepository;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import ar.com.agmilevecich.sofp.service.CategoriaService;
import ar.com.agmilevecich.sofp.service.CuentaService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.JButton;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MainFrameCategoriasTest {

    private EntityManager entityManager;
    private CuentaService cuentaService;
    private CategoriaService categoriaService;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();
        cuentaService = new CuentaService(
                new CuentaRepository(entityManager),
                new MovimientoRepository(entityManager),
                entityManager
        );
        categoriaService = new CategoriaService(
                entityManager,
                new CategoriaRepository(entityManager)
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
    void deberiaMostrarElPanelDeCategoriasDesdeLaNavegacion() throws Exception {
        Usuario usuario = new Usuario(
                "Ariel",
                "Test",
                "ariel.mainframe.categorias." + System.nanoTime(),
                "hash"
        );
        PerfilFinanciero perfil = new PerfilFinanciero("Perfil principal", usuario);
        usuario.agregarPerfilFinanciero(perfil);
        Categoria categoria = new Categoria("Supermercado", perfil);

        entityManager.getTransaction().begin();
        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(categoria);
        entityManager.getTransaction().commit();

        AtomicReference<MainFrame> frameRef = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> frameRef.set(
                new MainFrame(
                        cuentaService,
                        null,
                        categoriaService,
                        null,
                        null,
                        null,
                        perfil,
                        usuario.getId()
                )
        ));

        MainFrame mainFrame = frameRef.get();
        assertNotNull(mainFrame);

        SwingUtilities.invokeAndWait(() -> {
            JButton botonCategorias = buscarBoton(
                    mainFrame.getContentPane(),
                    "Categorías"
            );
            assertNotNull(botonCategorias);
            botonCategorias.doClick();
        });

        CategoriasPanel panel = buscarPanel(mainFrame.getContentPane());
        assertNotNull(panel);
        assertEquals(1, panel.getListaCategorias().getModel().getSize());
        assertEquals("Supermercado (activa)",
                panel.getListaCategorias().getModel().getElementAt(0));

        mainFrame.dispose();
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

    private CategoriasPanel buscarPanel(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof CategoriasPanel panel) {
                return panel;
            }
            if (component instanceof Container hijo) {
                CategoriasPanel encontrado = buscarPanel(hijo);
                if (encontrado != null) {
                    return encontrado;
                }
            }
        }
        return null;
    }
}
