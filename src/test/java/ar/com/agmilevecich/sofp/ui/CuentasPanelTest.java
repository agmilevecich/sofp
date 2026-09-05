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

import javax.swing.JList;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CuentasPanelTest {

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
    void deberiaMostrarLasCuentasDelPerfilDelUsuario() throws Exception {
        Usuario usuario = new Usuario(
                "Ariel",
                "Test",
                "ariel.cuentas.panel." + System.nanoTime(),
                "hash"
        );
        PerfilFinanciero perfil = new PerfilFinanciero(
                "Perfil principal",
                usuario
        );
        usuario.agregarPerfilFinanciero(perfil);

        InstitucionFinanciera institucion = new InstitucionFinanciera(
                "Banco Test",
                TipoInstitucionFinanciera.BANCO
        );
        Moneda moneda = new Moneda(
                "ARS",
                "Peso argentino",
                2,
                TipoMoneda.FIAT
        );

        Cuenta cuenta1 = new Cuenta(
                "Cuenta principal",
                TipoCuenta.CAJA_AHORRO,
                perfil,
                institucion,
                moneda
        );
        Cuenta cuenta2 = new Cuenta(
                "Cuenta secundaria",
                TipoCuenta.CUENTA_CORRIENTE,
                perfil,
                institucion,
                moneda
        );

        entityManager.getTransaction().begin();
        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(institucion);
        entityManager.persist(moneda);
        cuentaService.registrar(cuenta1, usuario.getId());
        cuentaService.registrar(cuenta2, usuario.getId());
        entityManager.getTransaction().commit();

        AtomicReference<CuentasPanel> panelRef = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> panelRef.set(
                new CuentasPanel(
                        cuentaService,
                        perfil.getId(),
                        usuario.getId()
                )
        ));

        CuentasPanel panel = panelRef.get();
        JList<?> lista = buscarLista(panel);

        assertNotNull(lista);
        assertEquals(2, lista.getModel().getSize());
        assertEquals("Cuenta principal", lista.getModel().getElementAt(0));
        assertEquals("Cuenta secundaria", lista.getModel().getElementAt(1));
    }

    @Test
    void deberiaMostrarElFormularioYActualizarLaListaDespuesDeRegistrar() throws Exception {
        Usuario usuario = new Usuario(
                "Ariel",
                "Test",
                "ariel.cuentas.panel.alta." + System.nanoTime(),
                "hash"
        );
        PerfilFinanciero perfil = new PerfilFinanciero(
                "Perfil principal",
                usuario
        );
        usuario.agregarPerfilFinanciero(perfil);
        InstitucionFinanciera institucion = new InstitucionFinanciera(
                "Banco Test",
                TipoInstitucionFinanciera.BANCO
        );
        Moneda moneda = new Moneda(
                "ARS",
                "Peso argentino",
                2,
                TipoMoneda.FIAT
        );

        entityManager.getTransaction().begin();
        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(institucion);
        entityManager.persist(moneda);
        entityManager.getTransaction().commit();

        AtomicReference<CuentasPanel> panelRef = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> panelRef.set(
                new CuentasPanel(
                        cuentaService,
                        institucionFinancieraService,
                        monedaService,
                        perfil,
                        usuario.getId()
                )
        ));

        CuentasPanel panel = panelRef.get();
        RegistrarCuentaPanel formulario = buscarFormulario(panel);

        assertNotNull(formulario);
        assertEquals(2, formulario.getInstitucionComboBox().getItemCount());
        assertEquals(2, formulario.getMonedaComboBox().getItemCount());
        assertEquals(0, buscarLista(panel).getModel().getSize());

        formulario.getNombreField().setText("Cuenta nueva");
        SwingUtilities.invokeAndWait(formulario::registrarCuenta);

        JList<?> lista = buscarLista(panel);
        assertEquals(1, lista.getModel().getSize());
        assertEquals("Cuenta nueva", lista.getModel().getElementAt(0));
        assertEquals(1, cuentaService.listarPorPerfilFinanciero(
                perfil.getId(),
                usuario.getId()
        ).size());
    }

    @Test
    void deberiaRechazarUnPerfilDeOtroUsuario() {
        Usuario usuarioPropietario = new Usuario(
                "Propietario",
                "Test",
                "ariel.cuentas.panel.propietario." + System.nanoTime(),
                "hash"
        );
        Usuario otroUsuario = new Usuario(
                "Otro",
                "Test",
                "ariel.cuentas.panel.otro." + System.nanoTime(),
                "hash"
        );
        PerfilFinanciero perfilPropietario = new PerfilFinanciero(
                "Perfil propietario",
                usuarioPropietario
        );
        PerfilFinanciero perfilAjeno = new PerfilFinanciero(
                "Perfil ajeno",
                otroUsuario
        );
        usuarioPropietario.agregarPerfilFinanciero(perfilPropietario);
        otroUsuario.agregarPerfilFinanciero(perfilAjeno);

        entityManager.getTransaction().begin();
        entityManager.persist(usuarioPropietario);
        entityManager.persist(otroUsuario);
        entityManager.persist(perfilPropietario);
        entityManager.persist(perfilAjeno);
        entityManager.getTransaction().commit();

        assertThrows(
                IllegalArgumentException.class,
                () -> new CuentasPanel(
                        cuentaService,
                        perfilAjeno.getId(),
                        usuarioPropietario.getId()
                )
        );
    }

    private JList<?> buscarLista(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof JList<?> lista) {
                return lista;
            }

            if (component instanceof Container hijo) {
                JList<?> encontrada = buscarLista(hijo);
                if (encontrada != null) {
                    return encontrada;
                }
            }
        }

        return null;
    }

    private RegistrarCuentaPanel buscarFormulario(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof RegistrarCuentaPanel formulario) {
                return formulario;
            }

            if (component instanceof Container hijo) {
                RegistrarCuentaPanel encontrado = buscarFormulario(hijo);
                if (encontrado != null) {
                    return encontrado;
                }
            }
        }

        return null;
    }
}
