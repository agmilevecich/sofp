package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.Movimiento;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.TipoCuenta;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.domain.TipoMovimiento;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.CategoriaRepository;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import ar.com.agmilevecich.sofp.service.CategoriaService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.SwingUtilities;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoriasPanelTest {

    private EntityManager entityManager;
    private CategoriaService categoriaService;
    private Usuario usuario;
    private PerfilFinanciero perfil;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();
        categoriaService = new CategoriaService(
                entityManager,
                new CategoriaRepository(entityManager)
        );

        usuario = new Usuario(
                "Ariel",
                "Test",
                "ariel.categorias.panel." + System.nanoTime(),
                "hash"
        );
        perfil = new PerfilFinanciero("Perfil principal", usuario);
        usuario.agregarPerfilFinanciero(perfil);

        entityManager.getTransaction().begin();
        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.getTransaction().commit();
    }

    @AfterEach
    void tearDown() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
        JpaTestManager.close();
    }

    @Test
    void deberiaMostrarCategoriasYRegistrarUnaNueva() throws Exception {
        Categoria existente = new Categoria("Supermercado", perfil);
        existente.cambiarDescripcion("Compras del hogar");

        entityManager.getTransaction().begin();
        categoriaService.registrar(existente, usuario.getId());
        entityManager.getTransaction().commit();

        AtomicReference<CategoriasPanel> panelRef = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> panelRef.set(
                new CategoriasPanel(categoriaService, perfil, usuario.getId())
        ));

        CategoriasPanel panel = panelRef.get();
        assertNotNull(panel);
        assertEquals(1, panel.getListaCategorias().getModel().getSize());

        SwingUtilities.invokeAndWait(() -> {
            panel.getNombreField().setText("Servicios");
            panel.getDescripcionArea().setText("Luz, gas e internet");
            panel.getRegistrarButton().doClick();
        });

        assertEquals(2, panel.getListaCategorias().getModel().getSize());
        assertEquals(2, categoriaService.listarPorPerfilFinanciero(
                perfil.getId(),
                usuario.getId()
        ).size());
    }

    @Test
    void deberiaRechazarRegistroSinNombre() throws Exception {
        AtomicReference<CategoriasPanel> panelRef = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> panelRef.set(
                new CategoriasPanel(categoriaService, perfil, usuario.getId())
        ));
        CategoriasPanel panel = panelRef.get();

        assertThrows(
                IllegalArgumentException.class,
                panel::registrarCategoria
        );

        assertEquals(0, panel.getListaCategorias().getModel().getSize());
    }

    @Test
    void deberiaModificarCambiarEstadoYEliminarLaCategoriaSeleccionada() throws Exception {
        Categoria categoria = new Categoria("Transporte", perfil);
        categoria.cambiarDescripcion("Viajes");

        entityManager.getTransaction().begin();
        categoriaService.registrar(categoria, usuario.getId());
        entityManager.getTransaction().commit();

        AtomicReference<CategoriasPanel> panelRef = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> panelRef.set(
                new CategoriasPanel(categoriaService, perfil, usuario.getId())
        ));
        CategoriasPanel panel = panelRef.get();

        SwingUtilities.invokeAndWait(() -> {
            panel.getListaCategorias().setSelectedIndex(0);
            panel.getNombreField().setText("Movilidad");
            panel.getDescripcionArea().setText("Transporte personal");
            panel.getModificarButton().doClick();
        });

        Categoria modificada = categoriaService
                .listarPorPerfilFinanciero(perfil.getId(), usuario.getId())
                .get(0);
        assertEquals("Movilidad", modificada.getNombre());
        assertEquals("Transporte personal", modificada.getDescripcion());
        assertTrue(modificada.isActiva());

        SwingUtilities.invokeAndWait(() -> {
            panel.getListaCategorias().setSelectedIndex(0);
            panel.getEstadoButton().doClick();
        });

        Categoria inactiva = categoriaService
                .listarPorPerfilFinanciero(perfil.getId(), usuario.getId())
                .get(0);
        assertFalse(inactiva.isActiva());

        SwingUtilities.invokeAndWait(() -> {
            panel.getListaCategorias().setSelectedIndex(0);
            panel.getEliminarButton().doClick();
        });

        assertTrue(categoriaService.listarPorPerfilFinanciero(
                perfil.getId(), usuario.getId()
        ).isEmpty());
        assertEquals(0, panel.getListaCategorias().getModel().getSize());
    }

    @Test
    void deberiaDesactivarCategoriaConMovimientosDesdeLaInterfazYConservarElHistorial() throws Exception {
        InstitucionFinanciera institucion = new InstitucionFinanciera(
                "Banco de Prueba",
                TipoInstitucionFinanciera.BANCO
        );
        Moneda moneda = new Moneda(
                "ARS",
                "Peso argentino",
                2,
                TipoMoneda.FIAT
        );
        Cuenta cuenta = new Cuenta(
                "Cuenta principal",
                TipoCuenta.CAJA_AHORRO,
                perfil,
                institucion,
                moneda
        );
        Categoria categoria = new Categoria("Alimentación", perfil);
        Movimiento movimiento = new Movimiento(
                cuenta,
                categoria,
                TipoMovimiento.INGRESO,
                new BigDecimal("1000.00"),
                LocalDateTime.of(2026, 9, 4, 10, 0),
                "Movimiento histórico"
        );

        entityManager.getTransaction().begin();
        entityManager.persist(institucion);
        entityManager.persist(moneda);
        entityManager.persist(cuenta);
        entityManager.persist(categoria);
        entityManager.persist(movimiento);
        entityManager.getTransaction().commit();

        AtomicReference<CategoriasPanel> panelRef = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> panelRef.set(
                new CategoriasPanel(categoriaService, perfil, usuario.getId())
        ));
        CategoriasPanel panel = panelRef.get();

        assertEquals(1, panel.getListaCategorias().getModel().getSize());

        SwingUtilities.invokeAndWait(() -> {
            panel.getListaCategorias().setSelectedIndex(0);
            panel.getEliminarButton().doClick();
        });

        List<Categoria> categorias = categoriaService.listarPorPerfilFinanciero(
                perfil.getId(), usuario.getId()
        );
        assertEquals(1, categorias.size());
        assertFalse(categorias.get(0).isActiva());
        assertEquals("Alimentación (inactiva)", panel.getListaCategorias().getModel().getElementAt(0));

        entityManager.clear();
        Categoria conservada = categoriaService
                .listarPorPerfilFinanciero(perfil.getId(), usuario.getId())
                .get(0);
        assertEquals(categoria.getId(), conservada.getId());
        assertFalse(conservada.isActiva());

        List<Movimiento> movimientos = new MovimientoRepository(entityManager)
                .listarPorCategoria(categoria.getId());
        assertEquals(1, movimientos.size());
        assertEquals(movimiento.getId(), movimientos.get(0).getId());
    }
}
