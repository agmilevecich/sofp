package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.FormaPago;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.Obligacion;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.TipoCuenta;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import ar.com.agmilevecich.sofp.persistence.ObligacionRepository;
import ar.com.agmilevecich.sofp.service.GastoService;
import ar.com.agmilevecich.sofp.service.MovimientoService;
import ar.com.agmilevecich.sofp.service.ObligacionService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.SwingUtilities;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ObligacionesPanelTest {

    private EntityManager entityManager;
    private ObligacionService obligacionService;
    private GastoService gastoService;
    private Usuario usuario;
    private Cuenta cuenta;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();
        MovimientoService movimientoService = new MovimientoService(
                entityManager,
                new MovimientoRepository(entityManager)
        );
        obligacionService = new ObligacionService(
                entityManager,
                new ObligacionRepository(entityManager)
        );
        gastoService = new GastoService(movimientoService, obligacionService);

        usuario = new Usuario(
                "Juan",
                "Pérez",
                "juan.panel." + System.nanoTime() + "@test.com",
                "hash"
        );
        PerfilFinanciero perfil = new PerfilFinanciero("Perfil principal", usuario);
        usuario.agregarPerfilFinanciero(perfil);

        InstitucionFinanciera institucion = new InstitucionFinanciera(
                "Banco de Prueba",
                TipoInstitucionFinanciera.BANCO
        );
        Moneda moneda = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        cuenta = new Cuenta(
                "Cuenta principal",
                TipoCuenta.CAJA_AHORRO,
                perfil,
                institucion,
                moneda
        );
        categoria = new Categoria("Alimentos", perfil);

        entityManager.getTransaction().begin();
        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(institucion);
        entityManager.persist(moneda);
        entityManager.persist(cuenta);
        entityManager.persist(categoria);
        entityManager.getTransaction().commit();
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
    void deberiaMostrarSoloLasObligacionesDelUsuario() {
        Obligacion obligacion = crearObligacion(new BigDecimal("15000.00"));

        ObligacionesPanel panel = new ObligacionesPanel(obligacionService, usuario.getId());

        assertEquals(1, panel.getObligacionesList().getModel().getSize());
        assertEquals(obligacion.getId(), panel.getObligacionesList().getModel().getElementAt(0).getId());
    }

    @Test
    void deberiaRegistrarPagoYRefrescarSaldo() throws Exception {
        Obligacion obligacion = crearObligacion(new BigDecimal("15000.00"));
        ObligacionesPanel panel = new ObligacionesPanel(obligacionService, usuario.getId());

        SwingUtilities.invokeAndWait(() -> {
            panel.getObligacionesList().setSelectedIndex(0);
            panel.getImportePagoField().setText("5000.00");
        });

        panel.registrarPagoSeleccionado();

        Obligacion actualizada = panel.getObligacionesList().getModel().getElementAt(0);
        assertEquals(new BigDecimal("10000.00"), actualizada.getSaldoPendiente());
        assertTrue(panel.getRegistrarPagoButton().isEnabled());
    }

    @Test
    void deberiaDeshabilitarPagoParaObligacionPagada() throws Exception {
        crearObligacion(new BigDecimal("15000.00"));
        Obligacion obligacion = obligacionService.listarPorUsuario(usuario.getId()).get(0);
        obligacionService.registrarPago(obligacion.getId(), new BigDecimal("15000.00"), usuario.getId());

        ObligacionesPanel panel = new ObligacionesPanel(obligacionService, usuario.getId());
        SwingUtilities.invokeAndWait(() -> panel.getObligacionesList().setSelectedIndex(0));

        assertFalse(panel.getRegistrarPagoButton().isEnabled());
    }

    private Obligacion crearObligacion(BigDecimal importe) {
        var movimiento = gastoService.registrar(
                cuenta,
                categoria,
                importe,
                LocalDateTime.of(2026, 9, 8, 10, 0),
                "Compra con tarjeta",
                FormaPago.TARJETA_CREDITO,
                usuario.getId()
        );
        return obligacionService.buscarPorMovimientoOrigen(movimiento.getId()).orElseThrow();
    }
}
