package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.FormaPago;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.Obligacion;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.TipoCambio;
import ar.com.agmilevecich.sofp.domain.TipoCuenta;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.domain.TipoMovimiento;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.CategoriaRepository;
import ar.com.agmilevecich.sofp.persistence.CuentaRepository;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import ar.com.agmilevecich.sofp.persistence.ObligacionRepository;
import ar.com.agmilevecich.sofp.service.CategoriaService;
import ar.com.agmilevecich.sofp.service.CuentaService;
import ar.com.agmilevecich.sofp.service.GastoService;
import ar.com.agmilevecich.sofp.service.MovimientoService;
import ar.com.agmilevecich.sofp.service.ObligacionService;
import ar.com.agmilevecich.sofp.service.PagoTarjetaService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ObligacionesPanelTest {

    private EntityManager entityManager;
    private MovimientoService movimientoService;
    private ObligacionService obligacionService;
    private GastoService gastoService;
    private CuentaService cuentaService;
    private CategoriaService categoriaService;
    private PagoTarjetaService pagoTarjetaService;
    private Usuario usuario;
    private PerfilFinanciero perfil;
    private Cuenta cuenta;
    private Cuenta cuentaPagadora;
    private Categoria categoria;
    private Moneda monedaUsd;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();
        MovimientoRepository movimientoRepository = new MovimientoRepository(entityManager);
        ObligacionRepository obligacionRepository = new ObligacionRepository(entityManager);
        movimientoService = new MovimientoService(entityManager, movimientoRepository);
        obligacionService = new ObligacionService(entityManager, obligacionRepository);
        cuentaService = new CuentaService(
                new CuentaRepository(entityManager),
                movimientoRepository,
                entityManager
        );
        categoriaService = new CategoriaService(
                entityManager,
                new CategoriaRepository(entityManager)
        );
        pagoTarjetaService = new PagoTarjetaService(
                entityManager,
                movimientoRepository,
                obligacionRepository
        );
        gastoService = new GastoService(movimientoService, obligacionService);

        usuario = new Usuario(
                "Juan",
                "Pérez",
                "juan.panel." + System.nanoTime() + "@test.com",
                "hash"
        );
        perfil = new PerfilFinanciero("Perfil principal", usuario);
        usuario.agregarPerfilFinanciero(perfil);

        InstitucionFinanciera institucion = new InstitucionFinanciera(
                "Banco de Prueba",
                TipoInstitucionFinanciera.BANCO
        );
        Moneda moneda = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        monedaUsd = new Moneda("USD", "Dólar estadounidense", 2, TipoMoneda.FIAT);
        cuenta = new Cuenta(
                "Tarjeta principal",
                perfil,
                institucion,
                moneda,
                new BigDecimal("500000.00"),
                15,
                10
        );
        cuentaPagadora = new Cuenta(
                "Caja de ahorro",
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
        entityManager.persist(monedaUsd);
        entityManager.persist(cuenta);
        entityManager.persist(cuentaPagadora);
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

        ObligacionesPanel panel = crearPanel();

        assertEquals(1, panel.getObligacionesList().getModel().getSize());
        assertEquals(obligacion.getId(), panel.getObligacionesList().getModel().getElementAt(0).getId());
    }

    @Test
    void deberiaMostrarLaMonedaDeLaObligacion() {
        var movimiento = gastoService.registrar(
                cuenta,
                categoria,
                monedaUsd,
                new BigDecimal("120.50"),
                LocalDateTime.of(2026, 9, 8, 11, 0),
                "Compra en dólares con tarjeta",
                FormaPago.TARJETA_CREDITO,
                usuario.getId()
        );

        Obligacion obligacion = obligacionService.buscarPorMovimientoOrigen(movimiento.getId()).orElseThrow();
        ObligacionesPanel panel = crearPanel();

        Component renderer = panel.getObligacionesList().getCellRenderer().getListCellRendererComponent(
                panel.getObligacionesList(),
                obligacion,
                0,
                false,
                false
        );

        assertTrue(renderer instanceof JLabel);
        assertTrue(((JLabel) renderer).getText().contains("120.50 USD"));
        assertTrue(((JLabel) renderer).getText().contains("pendiente 120.50 USD"));
    }

    @Test
    void deberiaRegistrarPagoYRefrescarSaldo() throws Exception {
        movimientoServiceRegistrarIngresoParaPago();
        Obligacion obligacion = crearObligacion(new BigDecimal("15000.00"));
        ObligacionesPanel panel = crearPanel();

        SwingUtilities.invokeAndWait(() -> {
            panel.getObligacionesList().setSelectedIndex(0);
            panel.getCuentaPagadoraCombo().setSelectedItem(cuentaPagadora);
            panel.getCategoriaCombo().setSelectedItem(categoria);
            panel.getImportePagoField().setText("5000.00");
        });

        panel.registrarPagoSeleccionado();

        Obligacion actualizada = panel.getObligacionesList().getModel().getElementAt(0);
        assertEquals(new BigDecimal("10000.00"), actualizada.getSaldoPendiente());
        assertTrue(panel.getRegistrarPagoButton().isEnabled());
        assertEquals(new BigDecimal("15000.00"), cuentaPagadoraSaldo());
        assertEquals(obligacion.getId(), actualizada.getId());
    }

    @Test
    void deberiaDeshabilitarPagoParaObligacionPagada() throws Exception {
        crearObligacion(new BigDecimal("15000.00"));
        Obligacion obligacion = obligacionService.listarPorUsuario(usuario.getId()).get(0);
        obligacionService.registrarPago(obligacion.getId(), new BigDecimal("15000.00"), usuario.getId());

        ObligacionesPanel panel = crearPanel();
        SwingUtilities.invokeAndWait(() -> panel.getObligacionesList().setSelectedIndex(0));

        assertFalse(panel.getRegistrarPagoButton().isEnabled());
    }

    @Test
    void deberiaCerrarDesdeElPanelYValorarObligacionMultidivisa() throws Exception {
        var movimiento = gastoService.registrar(
                cuenta,
                categoria,
                monedaUsd,
                new BigDecimal("100.00"),
                LocalDateTime.of(2026, 9, 8, 11, 0),
                "Compra USD con tarjeta",
                FormaPago.TARJETA_CREDITO,
                usuario.getId()
        );
        Obligacion obligacion = obligacionService.buscarPorMovimientoOrigen(movimiento.getId()).orElseThrow();

        entityManager.getTransaction().begin();
        entityManager.persist(new TipoCambio(
                monedaUsd,
                cuenta.getMoneda(),
                new BigDecimal("1500.00"),
                LocalDateTime.of(2026, 9, 15, 12, 0),
                "Test"
        ));
        entityManager.getTransaction().commit();

        ObligacionesPanel panel = crearPanel();
        SwingUtilities.invokeAndWait(() -> panel.getObligacionesList().setSelectedIndex(0));

        assertTrue(panel.getCerrarCicloButton().isEnabled());
        panel.cerrarCicloSeleccionado();

        Obligacion actualizada = obligacionService.buscarPorId(obligacion.getId()).orElseThrow();
        assertEquals(new BigDecimal("150000.00"), actualizada.getCuotas().get(0).getImporteValorizacionCierre());
        assertEquals(new BigDecimal("1500.00"), actualizada.getCuotas().get(0).getTipoCambioCierre().getCotizacion());
    }

    @Test
    void deberiaFallarDesdeElPanelSiFaltaCotizacionDeCierre() throws Exception {
        var movimiento = gastoService.registrar(
                cuenta,
                categoria,
                monedaUsd,
                new BigDecimal("100.00"),
                LocalDateTime.of(2026, 9, 8, 11, 0),
                "Compra USD sin cotización",
                FormaPago.TARJETA_CREDITO,
                usuario.getId()
        );
        Obligacion obligacion = obligacionService.buscarPorMovimientoOrigen(movimiento.getId()).orElseThrow();

        ObligacionesPanel panel = crearPanel();
        SwingUtilities.invokeAndWait(() -> panel.getObligacionesList().setSelectedIndex(0));

        assertThrows(IllegalArgumentException.class, panel::cerrarCicloSeleccionado);

        entityManager.clear();
        Obligacion actualizada = obligacionService.buscarPorId(obligacion.getId()).orElseThrow();
        assertNull(actualizada.getImporteValorizacionCierre());
        assertNull(actualizada.getTipoCambioCierre());
    }

    private ObligacionesPanel crearPanel() {
        return new ObligacionesPanel(
                obligacionService,
                pagoTarjetaService,
                cuentaService,
                categoriaService,
                perfil.getId(),
                usuario.getId()
        );
    }

    private void movimientoServiceRegistrarIngresoParaPago() {
        movimientoService.registrar(
                cuentaPagadora,
                categoria,
                TipoMovimiento.INGRESO,
                new BigDecimal("20000.00"),
                LocalDateTime.of(2026, 9, 8, 9, 0),
                "Fondos para pago",
                FormaPago.TRANSFERENCIA,
                usuario.getId()
        );
    }

    private BigDecimal cuentaPagadoraSaldo() {
        return cuentaService.calcularSaldo(cuentaPagadora.getId(), usuario.getId());
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
