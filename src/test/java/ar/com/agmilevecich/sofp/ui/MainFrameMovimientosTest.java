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
import ar.com.agmilevecich.sofp.persistence.CuentaRepository;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import ar.com.agmilevecich.sofp.service.CategoriaService;
import ar.com.agmilevecich.sofp.service.CuentaService;
import ar.com.agmilevecich.sofp.service.MovimientoService;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainFrameMovimientosTest {

    private EntityManager entityManager;
    private CuentaService cuentaService;
    private MovimientoService movimientoService;
    private CategoriaService categoriaService;

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
    }

    @AfterEach
    void tearDown() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
        JpaTestManager.close();
    }

    @Test
    void deberiaCargarLosMovimientosDeLaCuentaSeleccionada() throws Exception {
        Usuario usuario = new Usuario(
                "Ariel",
                "Test",
                "ariel.mainframe.movimientos." + System.nanoTime(),
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
        Cuenta cuenta = new Cuenta(
                "Cuenta principal",
                TipoCuenta.CAJA_AHORRO,
                perfil,
                institucion,
                moneda
        );
        Categoria categoria = new Categoria(
                "Supermercado",
                perfil
        );

        entityManager.getTransaction().begin();
        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(institucion);
        entityManager.persist(moneda);
        entityManager.persist(cuenta);
        entityManager.persist(categoria);
        entityManager.getTransaction().commit();

        movimientoService.registrar(
                cuenta,
                categoria,
                TipoMovimiento.INGRESO,
                new BigDecimal("500000"),
                LocalDateTime.now(),
                "Sueldo",
                usuario.getId()
        );
        movimientoService.registrar(
                cuenta,
                categoria,
                TipoMovimiento.EGRESO,
                new BigDecimal("200000"),
                LocalDateTime.now(),
                "Alquiler",
                usuario.getId()
        );

        AtomicReference<MainFrame> frameRef = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> frameRef.set(
                new MainFrame(
                        cuentaService,
                        movimientoService,
                        perfil.getId(),
                        usuario.getId()
                )
        ));

        MainFrame mainFrame = frameRef.get();
        assertNotNull(mainFrame);

        SwingUtilities.invokeAndWait(() -> {
            JList<?> listaCuentas = buscarLista(mainFrame.getContentPane(), 1);
            assertNotNull(listaCuentas);
            listaCuentas.setSelectedIndex(0);

            JButton botonMovimientos = buscarBoton(
                    mainFrame.getContentPane(),
                    "Movimientos"
            );
            assertNotNull(botonMovimientos);
            botonMovimientos.doClick();
        });

        JList<?> listaMovimientos = buscarLista(mainFrame.getContentPane(), 2);
        assertNotNull(listaMovimientos);
        assertEquals(2, listaMovimientos.getModel().getSize());
        assertEquals(
                "INGRESO - Sueldo - 500000",
                listaMovimientos.getModel().getElementAt(0)
        );
        assertEquals(
                "EGRESO - Alquiler - 200000",
                listaMovimientos.getModel().getElementAt(1)
        );

        mainFrame.dispose();
    }

    @Test
    void deberiaMostrarElFormularioDeAltaParaLaCuentaSeleccionada() throws Exception {
        Usuario usuario = new Usuario(
                "Ariel",
                "Test",
                "ariel.mainframe.formulario." + System.nanoTime(),
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
        Cuenta cuenta = new Cuenta(
                "Cuenta principal",
                TipoCuenta.CAJA_AHORRO,
                perfil,
                institucion,
                moneda
        );
        Categoria categoria = new Categoria(
                "Supermercado",
                perfil
        );

        entityManager.getTransaction().begin();
        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(institucion);
        entityManager.persist(moneda);
        entityManager.persist(cuenta);
        entityManager.persist(categoria);
        entityManager.getTransaction().commit();

        AtomicReference<MainFrame> frameRef = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> frameRef.set(
                new MainFrame(
                        cuentaService,
                        movimientoService,
                        categoriaService,
                        null,
                        perfil,
                        usuario.getId()
                )
        ));

        MainFrame mainFrame = frameRef.get();
        assertNotNull(mainFrame);

        SwingUtilities.invokeAndWait(() -> {
            JList<?> listaCuentas = buscarLista(mainFrame.getContentPane(), 1);
            assertNotNull(listaCuentas);
            listaCuentas.setSelectedIndex(0);

            JButton botonMovimientos = buscarBoton(
                    mainFrame.getContentPane(),
                    "Movimientos"
            );
            assertNotNull(botonMovimientos);
            botonMovimientos.doClick();
        });

        RegistrarMovimientoPanel formulario =
                buscarFormulario(mainFrame.getContentPane());
        assertNotNull(formulario);
        assertEquals(1, formulario.getCategoriaComboBox().getItemCount());
        assertTrue(formulario.getRegistrarButton().isEnabled());

        mainFrame.dispose();
    }

    private JList<?> buscarLista(Container container, int minimoElementos) {
        for (Component component : container.getComponents()) {
            if (component instanceof JList<?> lista
                    && lista.getModel().getSize() >= minimoElementos) {
                return lista;
            }

            if (component instanceof Container hijo) {
                JList<?> encontrada = buscarLista(hijo, minimoElementos);
                if (encontrada != null) {
                    return encontrada;
                }
            }
        }

        return null;
    }

    private JButton buscarBoton(Container container, String texto) {
        for (Component component : container.getComponents()) {
            if (component instanceof JButton boton
                    && texto.equals(boton.getText())) {
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

    private RegistrarMovimientoPanel buscarFormulario(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof RegistrarMovimientoPanel formulario) {
                return formulario;
            }

            if (component instanceof Container hijo) {
                RegistrarMovimientoPanel encontrado = buscarFormulario(hijo);
                if (encontrado != null) {
                    return encontrado;
                }
            }
        }

        return null;
    }
}
