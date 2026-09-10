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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistrarTarjetaCreditoPanelTest {

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
        monedaService = new MonedaService(new MonedaRepository(entityManager));
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
        RegistrarTarjetaCreditoPanel panel = new RegistrarTarjetaCreditoPanel();

        assertEquals(0, panel.getInstitucionComboBox().getSelectedIndex());
        assertEquals(0, panel.getMonedaComboBox().getSelectedIndex());
        assertFalse(panel.getRegistrarButton().isEnabled());
        assertEquals("", panel.getLimiteCreditoField().getText());
        assertEquals("", panel.getDiaCierreField().getText());
        assertEquals("", panel.getDiaVencimientoField().getText());
    }

    @Test
    void deberiaMostrarSoloInstitucionesActivas() {
        Usuario usuario = crearUsuario();
        PerfilFinanciero perfil = crearPerfil(usuario);
        InstitucionFinanciera activa = new InstitucionFinanciera(
                "Banco Activo", TipoInstitucionFinanciera.BANCO);
        InstitucionFinanciera inactiva = new InstitucionFinanciera(
                "Banco Inactivo", TipoInstitucionFinanciera.BANCO);
        inactiva.desactivar();
        Moneda ars = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        persistir(usuario, perfil, activa, inactiva, ars);

        RegistrarTarjetaCreditoPanel panel = new RegistrarTarjetaCreditoPanel(
                cuentaService, institucionFinancieraService, monedaService,
                perfil, usuario.getId());

        assertEquals(2, panel.getInstitucionComboBox().getItemCount());
        assertNull(panel.getInstitucionComboBox().getItemAt(0));
        assertEquals(activa, panel.getInstitucionComboBox().getItemAt(1));
        assertEquals(2, panel.getMonedaComboBox().getItemCount());
        assertNull(panel.getMonedaComboBox().getItemAt(0));
    }

    @Test
    void deberiaRegistrarUnaTarjetaDeCreditoYNotificarElCallback() {
        Usuario usuario = crearUsuario();
        PerfilFinanciero perfil = crearPerfil(usuario);
        InstitucionFinanciera institucion = new InstitucionFinanciera(
                "Banco Test", TipoInstitucionFinanciera.BANCO);
        Moneda moneda = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        persistir(usuario, perfil, institucion, moneda);

        boolean[] callback = {false};
        RegistrarTarjetaCreditoPanel panel = new RegistrarTarjetaCreditoPanel(
                cuentaService, institucionFinancieraService, monedaService,
                perfil, usuario.getId(), () -> callback[0] = true);
        panel.getNombreField().setText("Visa Test");
        panel.getInstitucionComboBox().setSelectedItem(institucion);
        panel.getMonedaComboBox().setSelectedItem(moneda);
        panel.getLimiteCreditoField().setText("500000");
        panel.getDiaCierreField().setText("15");
        panel.getDiaVencimientoField().setText("10");
        panel.getIdentificadorExternoField().setText("****1234");

        assertTrue(panel.getRegistrarButton().isEnabled());
        panel.registrarTarjetaCredito();

        assertTrue(callback[0]);
        entityManager.clear();
        Cuenta tarjeta = entityManager.createQuery(
                        "select c from Cuenta c where c.nombre = :nombre", Cuenta.class)
                .setParameter("nombre", "Visa Test")
                .getSingleResult();

        assertEquals(TipoCuenta.TARJETA_CREDITO, tarjeta.getTipoCuenta());
        assertEquals(0, new java.math.BigDecimal("500000").compareTo(tarjeta.getLimiteCredito()));
        assertEquals(15, tarjeta.getDiaCierre());
        assertEquals(10, tarjeta.getDiaVencimiento());
        assertEquals("****1234", tarjeta.getIdentificadorExterno());
        assertTrue(tarjeta.isActiva());
    }

    @Test
    void deberiaRechazarDatosDeCreditoInvalidos() {
        Usuario usuario = crearUsuario();
        PerfilFinanciero perfil = crearPerfil(usuario);
        InstitucionFinanciera institucion = new InstitucionFinanciera(
                "Banco Test", TipoInstitucionFinanciera.BANCO);
        Moneda moneda = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        persistir(usuario, perfil, institucion, moneda);

        RegistrarTarjetaCreditoPanel panel = new RegistrarTarjetaCreditoPanel(
                cuentaService, institucionFinancieraService, monedaService,
                perfil, usuario.getId());
        panel.getNombreField().setText("Visa Test");
        panel.getInstitucionComboBox().setSelectedItem(institucion);
        panel.getMonedaComboBox().setSelectedItem(moneda);
        panel.getLimiteCreditoField().setText("0");
        panel.getDiaCierreField().setText("15");
        panel.getDiaVencimientoField().setText("10");

        assertThrows(IllegalArgumentException.class, panel::registrarTarjetaCredito);
    }

    @Test
    void deberiaRechazarDependenciasObligatoriasNulas() {
        assertThrows(NullPointerException.class, () -> new RegistrarTarjetaCreditoPanel(
                null, institucionFinancieraService, monedaService, null, 1L));
        assertThrows(NullPointerException.class, () -> new RegistrarTarjetaCreditoPanel(
                cuentaService, null, monedaService, null, 1L));
        assertThrows(NullPointerException.class, () -> new RegistrarTarjetaCreditoPanel(
                cuentaService, institucionFinancieraService, null, null, 1L));
    }

    private Usuario crearUsuario() {
        return new Usuario("Ariel", "Test", "ariel.registrar.tarjeta." + System.nanoTime(), "hash");
    }

    private PerfilFinanciero crearPerfil(Usuario usuario) {
        PerfilFinanciero perfil = new PerfilFinanciero("Perfil principal", usuario);
        usuario.agregarPerfilFinanciero(perfil);
        return perfil;
    }

    private void persistir(Object... entidades) {
        entityManager.getTransaction().begin();
        for (Object entidad : entidades) {
            entityManager.persist(entidad);
        }
        entityManager.getTransaction().commit();
    }
}
