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
import ar.com.agmilevecich.sofp.persistence.CuentaRepository;
import ar.com.agmilevecich.sofp.persistence.InstitucionFinancieraRepository;
import ar.com.agmilevecich.sofp.persistence.MonedaRepository;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import ar.com.agmilevecich.sofp.persistence.ObligacionRepository;
import ar.com.agmilevecich.sofp.service.CategoriaService;
import ar.com.agmilevecich.sofp.service.CuentaService;
import ar.com.agmilevecich.sofp.service.ObligacionService;
import ar.com.agmilevecich.sofp.service.PagoTarjetaService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import java.lang.reflect.Field;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TarjetasCreditoPanelTest {

    private EntityManager entityManager;
    private Usuario usuario;
    private PerfilFinanciero perfil;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();
        usuario = new Usuario("Ariel", "Test", "panel.tarjeta." + System.nanoTime() + "@test.com", "hash");
        perfil = new PerfilFinanciero("Perfil principal", usuario);
        usuario.agregarPerfilFinanciero(perfil);
    }

    @AfterEach
    void tearDown() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
        JpaTestManager.close();
    }

    @Test
    void deberiaConstruirElPanelDeTarjetasSinContexto() {
        TarjetasCreditoPanel panel = new TarjetasCreditoPanel();

        assertNotNull(panel);
        assertNotNull(encontrarEtiqueta(panel, "Tarjetas"));
    }

    @Test
    void deberiaCargarTarjetaFiltrarCuentaPagadoraYMostrarCreditoDisponible() throws Exception {
        InstitucionFinanciera institucion = new InstitucionFinanciera(
                "Banco Test", TipoInstitucionFinanciera.BANCO);
        Moneda ars = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        Cuenta tarjeta = new Cuenta(
                "Visa Test", perfil, institucion, ars,
                new BigDecimal("500000.00"), 10, 25);
        Cuenta cajaAhorro = new Cuenta(
                "Caja de ahorro", TipoCuenta.CAJA_AHORRO,
                perfil, institucion, ars);
        Categoria categoria = new Categoria("Pago tarjeta", perfil);

        persistir(usuario, perfil, institucion, ars, tarjeta, cajaAhorro, categoria);

        MovimientoRepository movimientoRepository = new MovimientoRepository(entityManager);
        CuentaService cuentaService = new CuentaService(
                new CuentaRepository(entityManager),
                movimientoRepository,
                new ObligacionRepository(entityManager),
                entityManager);
        ObligacionService obligacionService = new ObligacionService(
                entityManager, new ObligacionRepository(entityManager));
        PagoTarjetaService pagoTarjetaService = new PagoTarjetaService(
                entityManager, movimientoRepository, new ObligacionRepository(entityManager));
        CategoriaService categoriaService = new CategoriaService(
                entityManager, new CategoriaRepository(entityManager));

        TarjetasCreditoPanel panel = new TarjetasCreditoPanel(
                cuentaService,
                obligacionService,
                pagoTarjetaService,
                categoriaService,
                perfil.getId(),
                usuario.getId());

        JComboBox<Cuenta> tarjetasCombo = obtenerCampo(panel, "tarjetasCombo", JComboBox.class);
        JComboBox<Cuenta> cuentaPagadoraCombo = obtenerCampo(panel, "cuentaPagadoraCombo", JComboBox.class);
        JComboBox<Categoria> categoriaCombo = obtenerCampo(panel, "categoriaCombo", JComboBox.class);

        assertEquals(1, tarjetasCombo.getItemCount());
        assertEquals(tarjeta, tarjetasCombo.getItemAt(0));

        assertEquals(1, cuentaPagadoraCombo.getItemCount());
        assertEquals(cajaAhorro, cuentaPagadoraCombo.getItemAt(0));

        assertEquals(1, categoriaCombo.getItemCount());
        assertEquals(categoria, categoriaCombo.getItemAt(0));

        assertEquals("500000.00 ARS", obtenerCampo(panel, "limiteLabel", JLabel.class).getText());
        assertEquals("500000.00 ARS", obtenerCampo(panel, "disponibleLabel", JLabel.class).getText());
        assertEquals("0.00 ARS", obtenerCampo(panel, "consumidoLabel", JLabel.class).getText());
        assertEquals("-", obtenerCampo(panel, "cicloLabel", JLabel.class).getText());
        assertEquals("-", obtenerCampo(panel, "vencimientoLabel", JLabel.class).getText());
        assertEquals("SIN DEUDA", obtenerCampo(panel, "estadoLabel", JLabel.class).getText());
    }

    @Test
    void deberiaRechazarDependenciasObligatoriasNulas() {
        assertThrows(NullPointerException.class, () -> new TarjetasCreditoPanel(
                null, null, null, null, 1L, 1L));
    }

    private void persistir(Object... entidades) {
        entityManager.getTransaction().begin();
        for (Object entidad : entidades) {
            entityManager.persist(entidad);
        }
        entityManager.getTransaction().commit();
    }

    @SuppressWarnings("unchecked")
    private <T> T obtenerCampo(Object objeto, String nombre, Class<T> tipo) throws Exception {
        Field field = TarjetasCreditoPanel.class.getDeclaredField(nombre);
        field.setAccessible(true);
        return (T) field.get(objeto);
    }

    private JLabel encontrarEtiqueta(java.awt.Container container, String texto) {
        for (java.awt.Component component : container.getComponents()) {
            if (component instanceof JLabel label && texto.equals(label.getText())) {
                return label;
            }
            if (component instanceof java.awt.Container hijo) {
                JLabel encontrada = encontrarEtiqueta(hijo, texto);
                if (encontrada != null) {
                    return encontrada;
                }
            }
        }
        return null;
    }
}
