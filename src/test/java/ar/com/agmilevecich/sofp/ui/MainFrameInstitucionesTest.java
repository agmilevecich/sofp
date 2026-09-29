package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.CuentaRepository;
import ar.com.agmilevecich.sofp.persistence.InstitucionFinancieraRepository;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import ar.com.agmilevecich.sofp.persistence.MonedaRepository;
import ar.com.agmilevecich.sofp.service.CuentaService;
import ar.com.agmilevecich.sofp.service.InstitucionFinancieraService;
import ar.com.agmilevecich.sofp.service.MonedaService;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MainFrameInstitucionesTest {

    private EntityManager entityManager;
    private CuentaService cuentaService;
    private InstitucionFinancieraService institucionService;
    private MonedaService monedaService;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();
        cuentaService = new CuentaService(
                new CuentaRepository(entityManager),
                new MovimientoRepository(entityManager),
                entityManager
        );
        institucionService = new InstitucionFinancieraService(
                new InstitucionFinancieraRepository(entityManager),
                entityManager
        );
        monedaService = new MonedaService(new MonedaRepository(entityManager));
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
    void deberiaMostrarInstitucionesEnElMenuPrincipal() throws Exception {
        Usuario usuario = new Usuario(
                "Ariel",
                "Test",
                "ariel.mainframe.instituciones." + System.nanoTime(),
                "hash"
        );
        PerfilFinanciero perfil = new PerfilFinanciero(
                "Perfil instituciones",
                usuario
        );
        usuario.agregarPerfilFinanciero(perfil);

        entityManager.getTransaction().begin();
        entityManager.persist(usuario);
        entityManager.persist(perfil);
        institucionService.registrar(new InstitucionFinanciera(
                "Banco Test",
                TipoInstitucionFinanciera.BANCO
        ));
        entityManager.getTransaction().commit();

        AtomicReference<MainFrame> frameRef = new AtomicReference<>();

        SwingUtilities.invokeAndWait(() -> frameRef.set(new MainFrame(
                cuentaService,
                null,
                null,
                institucionService,
                monedaService,
                null,
                perfil,
                usuario.getId()
        )));

        MainFrame frame = frameRef.get();
        assertNotNull(frame);

        SwingUtilities.invokeAndWait(() -> {
            JButton boton = buscarBoton(frame.getContentPane(), "Instituciones");
            assertNotNull(boton);
            boton.doClick();
        });

        JList<?> lista = buscarListaConValor(
                frame.getContentPane(),
                "Banco Test — BANCO — Activa"
        );
        assertNotNull(lista);
        assertEquals(1, lista.getModel().getSize());

        frame.dispose();
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

    private JList<?> buscarListaConValor(Container container, String valor) {
        for (Component component : container.getComponents()) {
            if (component instanceof JList<?> lista) {
                for (int i = 0; i < lista.getModel().getSize(); i++) {
                    if (valor.equals(lista.getModel().getElementAt(i))) {
                        return lista;
                    }
                }
            }
            if (component instanceof Container hijo) {
                JList<?> encontrada = buscarListaConValor(hijo, valor);
                if (encontrada != null) {
                    return encontrada;
                }
            }
        }
        return null;
    }
}
