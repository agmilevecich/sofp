package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Bono;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.TipoCuenta;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.CuentaRepository;
import ar.com.agmilevecich.sofp.persistence.MovimientoActivoRepository;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import ar.com.agmilevecich.sofp.persistence.OperacionFinancieraRepository;
import ar.com.agmilevecich.sofp.service.CarteraActivoService;
import ar.com.agmilevecich.sofp.service.CuentaService;
import ar.com.agmilevecich.sofp.service.OperacionFinancieraService;
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

class MainFrameReportesTest {

    private EntityManager entityManager;
    private CuentaService cuentaService;
    private CarteraActivoService carteraActivoService;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();
        cuentaService = new CuentaService(
                new CuentaRepository(entityManager),
                new MovimientoRepository(entityManager),
                entityManager
        );
        carteraActivoService = new CarteraActivoService(
                new MovimientoActivoRepository(entityManager)
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
    void deberiaMostrarReporteDeMovimientosAlNavegarDesdeMainFrame() throws Exception {
        Moneda moneda = crearMonedaPersistida();
        Bono bono = crearBonoPersistido(moneda);
        Contexto contexto = crearContexto(moneda);
        registrarCompra(contexto, bono);

        AtomicReference<MainFrame> frameRef = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> frameRef.set(new MainFrame(
                cuentaService,
                null,
                carteraActivoService,
                contexto.perfil,
                contexto.usuario.getId()
        )));

        MainFrame mainFrame = frameRef.get();
        assertNotNull(mainFrame);

        SwingUtilities.invokeAndWait(() -> {
            JButton botonReportes = buscarBoton(mainFrame.getContentPane(), "Reportes");
            assertNotNull(botonReportes);
            botonReportes.doClick();
        });

        JList<?> lista = buscarListaConValor(mainFrame.getContentPane(), "COMPRA - GD30 - 100 - 12500");
        assertNotNull(lista);
        assertEquals(1, lista.getModel().getSize());
        assertEquals("COMPRA - GD30 - 100 - 12500", lista.getModel().getElementAt(0));

        mainFrame.dispose();
    }

    private Moneda crearMonedaPersistida() {
        Moneda moneda = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        entityManager.getTransaction().begin();
        entityManager.persist(moneda);
        entityManager.getTransaction().commit();
        return moneda;
    }

    private Bono crearBonoPersistido(Moneda moneda) {
        Bono bono = new Bono("Bono GD30", "GD30", moneda);
        entityManager.getTransaction().begin();
        entityManager.persist(bono);
        entityManager.getTransaction().commit();
        return bono;
    }

    private Contexto crearContexto(Moneda moneda) {
        Usuario usuario = new Usuario("Ariel", "Test", "ariel.mainframe.reportes." + System.nanoTime(), "hash");
        PerfilFinanciero perfil = new PerfilFinanciero("Perfil reportes", usuario);
        usuario.agregarPerfilFinanciero(perfil);
        InstitucionFinanciera institucion = new InstitucionFinanciera(
                "Banco Test " + System.nanoTime(),
                TipoInstitucionFinanciera.BANCO
        );
        Cuenta cuenta = new Cuenta(
                "Cuenta reportes " + System.nanoTime(),
                TipoCuenta.CAJA_AHORRO,
                perfil,
                institucion,
                moneda
        );
        Categoria categoria = new Categoria("Inversiones " + System.nanoTime(), perfil);

        entityManager.getTransaction().begin();
        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(institucion);
        entityManager.persist(cuenta);
        entityManager.persist(categoria);
        entityManager.getTransaction().commit();

        return new Contexto(usuario, perfil, cuenta, categoria);
    }

    private void registrarCompra(Contexto contexto, Bono bono) {
        OperacionFinancieraService service = new OperacionFinancieraService(
                entityManager,
                new MovimientoRepository(entityManager),
                new OperacionFinancieraRepository(entityManager)
        );
        service.comprarActivo(
                contexto.usuario.getId(),
                contexto.cuenta,
                contexto.categoria,
                bono,
                new BigDecimal("100"),
                new BigDecimal("125"),
                LocalDateTime.of(2026, 8, 27, 10, 0),
                "Compra Bono GD30"
        );
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

    private JList<?> buscarListaConValor(Container container, String valor) {
        for (Component component : container.getComponents()) {
            if (component instanceof JList<?> lista) {
                for (int i = 0; i < lista.getModel().getSize(); i++) {
                    if (valor.equals(lista.getModel().getElementAt(i))) {
                        return lista;
                    }
                }
            }
            if (component instanceof Container hijo) {
                JList<?> encontrada = buscarListaConValor(hijo, valor);
                if (encontrada != null) {
                    return encontrada;
                }
            }
        }
        return null;
    }

    private record Contexto(
            Usuario usuario,
            PerfilFinanciero perfil,
            Cuenta cuenta,
            Categoria categoria) {
    }
}
