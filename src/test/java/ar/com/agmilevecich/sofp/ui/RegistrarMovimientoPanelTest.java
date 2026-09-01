package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.TipoCuenta;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.CategoriaRepository;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import ar.com.agmilevecich.sofp.service.CategoriaService;
import ar.com.agmilevecich.sofp.service.MovimientoService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistrarMovimientoPanelTest {

    private EntityManager entityManager;
    private MovimientoService movimientoService;
    private CategoriaService categoriaService;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();
        movimientoService = new MovimientoService(
                entityManager,
                new MovimientoRepository(entityManager)
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
    void deberiaConstruirElFormularioDelShellSinContexto() {
        RegistrarMovimientoPanel panel = new RegistrarMovimientoPanel();

        assertNotNull(panel.getCategoriaComboBox());
        assertNotNull(panel.getTipoMovimientoComboBox());
        assertNotNull(panel.getImporteField());
        assertNotNull(panel.getFechaHoraField());
        assertNotNull(panel.getDescripcionField());
        assertFalse(panel.getRegistrarButton().isEnabled());
    }

    @Test
    void deberiaMostrarSoloCategoriasActivasDelPerfilAutorizado() throws Exception {
        Usuario usuario = new Usuario(
                "Ariel",
                "Test",
                "ariel.registro.movimiento." + System.nanoTime(),
                "hash"
        );
        PerfilFinanciero perfil = new PerfilFinanciero("Perfil principal", usuario);
        usuario.agregarPerfilFinanciero(perfil);

        InstitucionFinanciera institucion = new InstitucionFinanciera(
                "Banco Test",
                TipoInstitucionFinanciera.BANCO
        );
        Moneda moneda = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        Cuenta cuenta = new Cuenta(
                "Cuenta principal",
                TipoCuenta.CAJA_AHORRO,
                perfil,
                institucion,
                moneda
        );
        Categoria activa = new Categoria("Supermercado", perfil);
        Categoria inactiva = new Categoria("Antigua", perfil);
        inactiva.desactivar();

        entityManager.getTransaction().begin();
        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(institucion);
        entityManager.persist(moneda);
        entityManager.persist(cuenta);
        entityManager.persist(activa);
        entityManager.persist(inactiva);
        entityManager.getTransaction().commit();

        AtomicReference<RegistrarMovimientoPanel> panelRef = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> panelRef.set(
                new RegistrarMovimientoPanel(
                        movimientoService,
                        categoriaService,
                        cuenta,
                        usuario.getId()
                )
        ));

        RegistrarMovimientoPanel panel = panelRef.get();
        JComboBox<?> categorias = panel.getCategoriaComboBox();
        JButton registrar = panel.getRegistrarButton();

        assertEquals(1, categorias.getItemCount());
        assertEquals(activa, categorias.getItemAt(0));
        assertTrue(registrar.isEnabled());
    }

    @Test
    void deberiaRechazarDependenciasObligatoriasNulas() {
        assertThrows(
                NullPointerException.class,
                () -> new RegistrarMovimientoPanel(
                        null,
                        categoriaService,
                        null,
                        1L
                )
        );
    }
}
