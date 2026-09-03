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
import ar.com.agmilevecich.sofp.domain.TipoMovimiento;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.CategoriaRepository;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import ar.com.agmilevecich.sofp.service.CategoriaService;
import ar.com.agmilevecich.sofp.service.MovimientoService;
import com.github.lgooddatepicker.components.DateTimePicker;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;
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
        assertNotNull(panel.getFechaHoraPicker());
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
    void deberiaRegistrarMovimientoYNotificarAlContenedor() throws Exception {
        Usuario usuario = new Usuario(
                "Ariel",
                "Test",
                "ariel.registro.movimiento.persistencia." + System.nanoTime(),
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
        Categoria categoria = new Categoria("Supermercado", perfil);

        entityManager.getTransaction().begin();
        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(institucion);
        entityManager.persist(moneda);
        entityManager.persist(cuenta);
        entityManager.persist(categoria);
        entityManager.getTransaction().commit();

        AtomicBoolean notificado = new AtomicBoolean(false);
        AtomicReference<RegistrarMovimientoPanel> panelRef = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> {
            RegistrarMovimientoPanel panel = new RegistrarMovimientoPanel(
                    movimientoService,
                    categoriaService,
                    cuenta,
                    usuario.getId(),
                    () -> notificado.set(true)
            );
            panel.getCategoriaComboBox().setSelectedItem(categoria);
            panel.getTipoMovimientoComboBox().setSelectedItem(TipoMovimiento.INGRESO);
            panel.getImporteField().setText("150000");
            panel.getFechaHoraPicker().setDateTimeStrict(LocalDateTime.parse("2026-09-02T12:30"));
            panel.getDescripcionField().setText("Sueldo");
            panelRef.set(panel);
        });

        SwingUtilities.invokeAndWait(() -> panelRef.get().registrarMovimiento());

        assertTrue(notificado.get());
        assertEquals(1, movimientoService.listarPorCuenta(cuenta.getId(), usuario.getId()).size());
        assertEquals(
                "Sueldo",
                movimientoService.listarPorCuenta(cuenta.getId(), usuario.getId())
                        .get(0)
                        .getDescripcion()
        );
        assertEquals(
                new BigDecimal("150000"),
                movimientoService.listarPorCuenta(cuenta.getId(), usuario.getId())
                        .get(0)
                        .getImporte()
        );
        assertEquals(
                LocalDateTime.parse("2026-09-02T12:30"),
                movimientoService.listarPorCuenta(cuenta.getId(), usuario.getId())
                        .get(0)
                        .getFechaHora()
        );
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
