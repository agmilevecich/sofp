package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.FormaPago;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.TipoCuenta;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.CategoriaRepository;
import ar.com.agmilevecich.sofp.persistence.CuentaRepository;
import ar.com.agmilevecich.sofp.persistence.InstitucionFinancieraRepository;
import ar.com.agmilevecich.sofp.persistence.MonedaRepository;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import ar.com.agmilevecich.sofp.persistence.ObligacionRepository;
import ar.com.agmilevecich.sofp.service.CategoriaService;
import ar.com.agmilevecich.sofp.service.CuentaService;
import ar.com.agmilevecich.sofp.service.GastoService;
import ar.com.agmilevecich.sofp.service.InstitucionFinancieraService;
import ar.com.agmilevecich.sofp.service.MonedaService;
import ar.com.agmilevecich.sofp.service.MovimientoService;
import ar.com.agmilevecich.sofp.service.ObligacionService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MainFrameObligacionesTest {

    private EntityManager entityManager;
    private CuentaService cuentaService;
    private MovimientoService movimientoService;
    private CategoriaService categoriaService;
    private InstitucionFinancieraService institucionFinancieraService;
    private MonedaService monedaService;
    private ObligacionService obligacionService;
    private GastoService gastoService;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();
        cuentaService = new CuentaService(
                new CuentaRepository(entityManager),
                new MovimientoRepository(entityManager),
                entityManager
        );
        movimientoService = new MovimientoService(
                entityManager,
                new MovimientoRepository(entityManager)
        );
        categoriaService = new CategoriaService(
                entityManager,
                new CategoriaRepository(entityManager)
        );
        institucionFinancieraService = new InstitucionFinancieraService(
                new InstitucionFinancieraRepository(entityManager)
        );
        monedaService = new MonedaService(new MonedaRepository(entityManager));
        obligacionService = new ObligacionService(
                entityManager,
                new ObligacionRepository(entityManager)
        );
        gastoService = new GastoService(movimientoService, obligacionService);
    }

    @AfterEach
    void tearDown() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
        JpaTestManager.close();
    }

    @Test
    void deberiaNavegarAObligacionesYMostrarLasDelUsuario() throws Exception {
        Usuario usuario = new Usuario(
                "Ariel",
                "Test",
                "ariel.mainframe.obligaciones." + System.nanoTime(),
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

        gastoService.registrar(
                cuenta,
                categoria,
                new BigDecimal("12000.00"),
                LocalDateTime.of(2026, 9, 8, 12, 0),
                "Compra con tarjeta",
                FormaPago.TARJETA_CREDITO,
                usuario.getId()
        );

        AtomicReference<MainFrame> frameRef = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> frameRef.set(
                new MainFrame(
                        cuentaService,
                        movimientoService,
                        categoriaService,
                        institucionFinancieraService,
                        monedaService,
                        null,
                        perfil,
                        usuario.getId(),
                        obligacionService
                )
        ));

        MainFrame mainFrame = frameRef.get();
        assertNotNull(mainFrame);

        SwingUtilities.invokeAndWait(() -> {
            JButton botonObligaciones = buscarBoton(
                    mainFrame.getContentPane(),
                    "Obligaciones"
            );
            assertNotNull(botonObligaciones);
            botonObligaciones.doClick();
        });

        ObligacionesPanel panel = buscarPanel(mainFrame.getContentPane());
        assertNotNull(panel);
        assertEquals(1, panel.getObligacionesList().getModel().getSize());
        assertEquals(
                new BigDecimal("12000.00"),
                panel.getObligacionesList().getModel().getElementAt(0).getSaldoPendiente()
        );

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

    private ObligacionesPanel buscarPanel(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof ObligacionesPanel panel) {
                return panel;
            }
            if (component instanceof Container hijo) {
                ObligacionesPanel encontrado = buscarPanel(hijo);
                if (encontrado != null) {
                    return encontrado;
                }
            }
        }
        return null;
    }
}
