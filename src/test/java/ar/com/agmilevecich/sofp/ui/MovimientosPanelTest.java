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
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import ar.com.agmilevecich.sofp.service.MovimientoService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.JList;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MovimientosPanelTest {

    private EntityManager entityManager;
    private MovimientoService movimientoService;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();
        movimientoService = new MovimientoService(
                entityManager,
                new MovimientoRepository(entityManager)
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
    void deberiaMostrarLosMovimientosDeLaCuentaDelUsuario() throws Exception {
        Usuario usuario = new Usuario(
                "Ariel",
                "Test",
                "ariel.movimientos.panel." + System.nanoTime(),
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

        AtomicReference<MovimientosPanel> panelRef =
                new AtomicReference<>();

        SwingUtilities.invokeAndWait(() -> panelRef.set(
                new MovimientosPanel(
                        movimientoService,
                        cuenta.getId(),
                        usuario.getId()
                )
        ));

        MovimientosPanel panel = panelRef.get();
        JList<?> lista = buscarLista(panel);

        assertNotNull(lista);
        assertEquals(2, lista.getModel().getSize());
        assertEquals(
                "INGRESO - Sueldo - 500000",
                lista.getModel().getElementAt(0)
        );
        assertEquals(
                "EGRESO - Alquiler - 200000",
                lista.getModel().getElementAt(1)
        );
    }

    @Test
    void deberiaRechazarUnaCuentaDeOtroUsuario() {
        Usuario usuarioPropietario = new Usuario(
                "Propietario",
                "Test",
                "ariel.movimientos.panel.propietario."
                        + System.nanoTime(),
                "hash"
        );
        Usuario otroUsuario = new Usuario(
                "Otro",
                "Test",
                "ariel.movimientos.panel.otro."
                        + System.nanoTime(),
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
        Cuenta cuentaAjena = new Cuenta(
                "Cuenta ajena",
                TipoCuenta.CAJA_AHORRO,
                perfilAjeno,
                institucion,
                moneda
        );

        entityManager.getTransaction().begin();
        entityManager.persist(usuarioPropietario);
        entityManager.persist(otroUsuario);
        entityManager.persist(perfilPropietario);
        entityManager.persist(perfilAjeno);
        entityManager.persist(institucion);
        entityManager.persist(moneda);
        entityManager.persist(cuentaAjena);
        entityManager.getTransaction().commit();

        assertThrows(
                IllegalArgumentException.class,
                () -> new MovimientosPanel(
                        movimientoService,
                        cuentaAjena.getId(),
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
}
