package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.TipoCuenta;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.CuentaRepository;
import ar.com.agmilevecich.sofp.persistence.InstitucionFinancieraRepository;
import ar.com.agmilevecich.sofp.persistence.MonedaRepository;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import ar.com.agmilevecich.sofp.service.CuentaService;
import ar.com.agmilevecich.sofp.service.InstitucionFinancieraService;
import ar.com.agmilevecich.sofp.service.MonedaService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistrarCuentaPanelTest {

    private EntityManager entityManager;
    private CuentaService cuentaService;
    private InstitucionFinancieraService institucionFinancieraService;
    private MonedaService monedaService;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();
        cuentaService = new CuentaService(
                new CuentaRepository(entityManager),
                new MovimientoRepository(entityManager),
                entityManager
        );
        institucionFinancieraService = new InstitucionFinancieraService(
                new InstitucionFinancieraRepository(entityManager)
        );
        monedaService = new MonedaService(
                new MonedaRepository(entityManager)
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
    void deberiaConstruirElFormularioBaseConElRegistroDeshabilitado() {
        RegistrarCuentaPanel panel = new RegistrarCuentaPanel();

        assertNotNull(panel.getNombreField());
        assertNotNull(panel.getTipoCuentaComboBox());
        assertNotNull(panel.getInstitucionComboBox());
        assertNotNull(panel.getMonedaComboBox());
        assertEquals(0, panel.getTipoCuentaComboBox().getSelectedIndex());
        assertEquals(0, panel.getInstitucionComboBox().getSelectedIndex());
        assertEquals(0, panel.getMonedaComboBox().getSelectedIndex());
        JLabel renderer = (JLabel) panel.getTipoCuentaComboBox().getRenderer()
                .getListCellRendererComponent(
                        new javax.swing.JList<>(), null, 0, false, false
                );
        assertEquals("Seleccione...", renderer.getText());
        assertNotNull(panel.getIdentificadorExternoField());
        assertFalse(panel.getRegistrarButton().isEnabled());
    }

    @Test
    void deberiaMantenerElRegistroDeshabilitadoMientrasElNombreEsteVacio() throws Exception {
        Usuario usuario = crearUsuario();
        PerfilFinanciero perfil = new PerfilFinanciero("Perfil principal", usuario);
        usuario.agregarPerfilFinanciero(perfil);
        InstitucionFinanciera institucion = new InstitucionFinanciera(
                "Banco Activo",
                TipoInstitucionFinanciera.BANCO
        );
        Moneda moneda = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);

        persistir(usuario, perfil, institucion, moneda);

        AtomicReference<RegistrarCuentaPanel> panelRef = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> panelRef.set(
                new RegistrarCuentaPanel(
                        cuentaService,
                        institucionFinancieraService,
                        monedaService,
                        perfil,
                        usuario.getId()
                )
        ));

        RegistrarCuentaPanel panel = panelRef.get();
        assertFalse(panel.getRegistrarButton().isEnabled());

        SwingUtilities.invokeAndWait(() -> panel.getNombreField().setText("Cuenta principal"));

        assertTrue(panel.getRegistrarButton().isEnabled());

        SwingUtilities.invokeAndWait(() -> panel.getNombreField().setText("   "));

        assertFalse(panel.getRegistrarButton().isEnabled());
    }

    @Test
    void deberiaMostrarSoloInstitucionesActivasYLasMonedasDisponibles() throws Exception {
        Usuario usuario = crearUsuario();
        PerfilFinanciero perfil = new PerfilFinanciero("Perfil principal", usuario);
        usuario.agregarPerfilFinanciero(perfil);

        InstitucionFinanciera activa = new InstitucionFinanciera(
                "Banco Activo",
                TipoInstitucionFinanciera.BANCO
        );
        InstitucionFinanciera inactiva = new InstitucionFinanciera(
                "Banco Inactivo",
                TipoInstitucionFinanciera.BANCO
        );
        inactiva.desactivar();
        Moneda ars = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        Moneda usd = new Moneda("USD", "Dólar estadounidense", 2, TipoMoneda.FIAT);

        persistir(usuario, perfil, activa, inactiva, ars, usd);

        AtomicReference<RegistrarCuentaPanel> panelRef = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> panelRef.set(
                new RegistrarCuentaPanel(
                        cuentaService,
                        institucionFinancieraService,
                        monedaService,
                        perfil,
                        usuario.getId()
                )
        ));

        RegistrarCuentaPanel panel = panelRef.get();
        assertEquals(2, panel.getInstitucionComboBox().getItemCount());
        assertNull(panel.getInstitucionComboBox().getItemAt(0));
        assertEquals(activa, panel.getInstitucionComboBox().getItemAt(1));
        assertEquals(3, panel.getMonedaComboBox().getItemCount());
        assertNull(panel.getMonedaComboBox().getItemAt(0));
        assertFalse(panel.getRegistrarButton().isEnabled());
    }

    @Test
    void deberiaRegistrarLaCuentaYNotificarElCallback() throws Exception {
        Usuario usuario = crearUsuario();
        PerfilFinanciero perfil = new PerfilFinanciero("Perfil principal", usuario);
        usuario.agregarPerfilFinanciero(perfil);
        InstitucionFinanciera institucion = new InstitucionFinanciera(
                "Banco Test",
                TipoInstitucionFinanciera.BANCO
        );
        Moneda moneda = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        persistir(usuario, perfil, institucion, moneda);

        AtomicBoolean callbackEjecutado = new AtomicBoolean(false);
        AtomicReference<RegistrarCuentaPanel> panelRef = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> panelRef.set(
                new RegistrarCuentaPanel(
                        cuentaService,
                        institucionFinancieraService,
                        monedaService,
                        perfil,
                        usuario.getId(),
                        () -> callbackEjecutado.set(true)
                )
        ));

        RegistrarCuentaPanel panel = panelRef.get();
        SwingUtilities.invokeAndWait(() -> {
            panel.getNombreField().setText("Cuenta principal");
            panel.getTipoCuentaComboBox().setSelectedItem(TipoCuenta.CAJA_AHORRO);
            panel.getInstitucionComboBox().setSelectedItem(institucion);
            panel.getMonedaComboBox().setSelectedItem(moneda);
            panel.getIdentificadorExternoField().setText("CBU-123");
            panel.registrarCuenta();
        });

        assertTrue(callbackEjecutado.get());

        entityManager.clear();
        Cuenta cuenta = entityManager.createQuery(
                        "select c from Cuenta c where c.nombre = :nombre",
                        Cuenta.class
                )
                .setParameter("nombre", "Cuenta principal")
                .getSingleResult();

        assertEquals(TipoCuenta.CAJA_AHORRO, cuenta.getTipoCuenta());
        assertEquals("CBU-123", cuenta.getIdentificadorExterno());
        assertTrue(cuenta.isActiva());
        assertEquals(usuario.getId(), cuenta.getPerfilFinanciero().getUsuario().getId());
    }

    @Test
    void deberiaRegistrarSinIdentificadorExternoCuandoSeDejaVacio() throws Exception {
        Usuario usuario = crearUsuario();
        PerfilFinanciero perfil = new PerfilFinanciero("Perfil principal", usuario);
        usuario.agregarPerfilFinanciero(perfil);
        InstitucionFinanciera institucion = new InstitucionFinanciera(
                "Banco Test",
                TipoInstitucionFinanciera.BANCO
        );
        Moneda moneda = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        persistir(usuario, perfil, institucion, moneda);

        RegistrarCuentaPanel panel = new RegistrarCuentaPanel(
                cuentaService,
                institucionFinancieraService,
                monedaService,
                perfil,
                usuario.getId()
        );
        panel.getNombreField().setText("Cuenta sin identificador");
        panel.getTipoCuentaComboBox().setSelectedItem(TipoCuenta.CAJA_AHORRO);
        panel.getInstitucionComboBox().setSelectedItem(institucion);
        panel.getMonedaComboBox().setSelectedItem(moneda);
        panel.registrarCuenta();

        entityManager.clear();
        Cuenta cuenta = entityManager.createQuery(
                        "select c from Cuenta c where c.nombre = :nombre",
                        Cuenta.class
                )
                .setParameter("nombre", "Cuenta sin identificador")
                .getSingleResult();

        assertNull(cuenta.getIdentificadorExterno());
    }

    @Test
    void deberiaRechazarDependenciasObligatoriasNulas() {
        assertThrows(
                NullPointerException.class,
                () -> new RegistrarCuentaPanel(
                        null,
                        institucionFinancieraService,
                        monedaService,
                        null,
                        1L
                )
        );
        assertThrows(
                NullPointerException.class,
                () -> new RegistrarCuentaPanel(
                        cuentaService,
                        null,
                        monedaService,
                        null,
                        1L
                )
        );
        assertThrows(
                NullPointerException.class,
                () -> new RegistrarCuentaPanel(
                        cuentaService,
                        institucionFinancieraService,
                        null,
                        null,
                        1L
                )
        );
    }

    private Usuario crearUsuario() {
        return new Usuario(
                "Ariel",
                "Test",
                "ariel.registrar.cuenta." + System.nanoTime(),
                "hash"
        );
    }

    private void persistir(Object... entidades) {
        entityManager.getTransaction().begin();
        for (Object entidad : entidades) {
            entityManager.persist(entidad);
        }
        entityManager.getTransaction().commit();
    }
}
