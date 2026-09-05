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
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import ar.com.agmilevecich.sofp.service.CategoriaService;
import ar.com.agmilevecich.sofp.service.CuentaService;
import ar.com.agmilevecich.sofp.service.GastoService;
import ar.com.agmilevecich.sofp.service.MovimientoService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GastosPanelTest {

    private EntityManager entityManager;
    private MovimientoService movimientoService;
    private CategoriaService categoriaService;
    private CuentaService cuentaService;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();
        MovimientoRepository movimientoRepository = new MovimientoRepository(entityManager);
        movimientoService = new MovimientoService(entityManager, movimientoRepository);
        categoriaService = new CategoriaService(entityManager, new CategoriaRepository(entityManager), movimientoRepository);
        cuentaService = new CuentaService(
                new ar.com.agmilevecich.sofp.persistence.CuentaRepository(entityManager),
                movimientoRepository,
                entityManager
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
    void deberiaConstruirElFormularioDelShellSinContexto() {
        GastosPanel panel = new GastosPanel();

        assertNotNull(panel.getCuentaComboBox());
        assertNotNull(panel.getCategoriaComboBox());
        assertNotNull(panel.getImporteField());
        assertNotNull(panel.getFechaField());
        assertEquals(LocalDate.now(), panel.getFechaField().getDate());
        assertFalse(panel.getRegistrarButton().isEnabled());
    }

    @Test
    void deberiaMostrarSoloCuentasYCategoriasActivasDelPerfil() {
        Usuario usuario = crearUsuario();
        PerfilFinanciero perfil = new PerfilFinanciero("Perfil principal", usuario);
        usuario.agregarPerfilFinanciero(perfil);
        InstitucionFinanciera institucion = new InstitucionFinanciera("Banco Test", TipoInstitucionFinanciera.BANCO);
        Moneda moneda = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        Cuenta activa = new Cuenta("Cuenta activa", TipoCuenta.CAJA_AHORRO, perfil, institucion, moneda);
        Cuenta inactiva = new Cuenta("Cuenta inactiva", TipoCuenta.CAJA_AHORRO, perfil, institucion, moneda);
        inactiva.desactivar();
        Categoria activaCategoria = new Categoria("Supermercado", perfil);
        Categoria inactivaCategoria = new Categoria("Antigua", perfil);
        inactivaCategoria.desactivar();

        persistir(usuario, perfil, institucion, moneda, activa, inactiva, activaCategoria, inactivaCategoria);

        GastosPanel panel = new GastosPanel(
                new GastoService(movimientoService),
                cuentaService,
                categoriaService,
                perfil.getId(),
                usuario.getId()
        );

        assertEquals(1, panel.getCuentaComboBox().getItemCount());
        assertEquals(activa, panel.getCuentaComboBox().getItemAt(0));
        assertEquals(1, panel.getCategoriaComboBox().getItemCount());
        assertEquals(activaCategoria, panel.getCategoriaComboBox().getItemAt(0));
        assertTrue(panel.getRegistrarButton().isEnabled());
    }

    @Test
    void deberiaRegistrarElGastoComoEgreso() {
        Usuario usuario = crearUsuario();
        PerfilFinanciero perfil = new PerfilFinanciero("Perfil principal", usuario);
        usuario.agregarPerfilFinanciero(perfil);
        InstitucionFinanciera institucion = new InstitucionFinanciera("Banco Test", TipoInstitucionFinanciera.BANCO);
        Moneda moneda = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        Cuenta cuenta = new Cuenta("Cuenta principal", TipoCuenta.CAJA_AHORRO, perfil, institucion, moneda);
        Categoria categoria = new Categoria("Supermercado", perfil);

        persistir(usuario, perfil, institucion, moneda, cuenta, categoria);

        GastosPanel panel = new GastosPanel(
                new GastoService(movimientoService),
                cuentaService,
                categoriaService,
                perfil.getId(),
                usuario.getId()
        );
        panel.getCuentaComboBox().setSelectedItem(cuenta);
        panel.getCategoriaComboBox().setSelectedItem(categoria);
        panel.getImporteField().setText("100");
        panel.getFechaField().setDate(LocalDate.of(2026, 9, 4));
        panel.getDescripcionField().setText("Compra supermercado");

        panel.registrarGasto();

        var movimientos = movimientoService.listarPorCuenta(cuenta.getId(), usuario.getId());
        assertEquals(1, movimientos.size());
        assertEquals(TipoMovimiento.EGRESO, movimientos.get(0).getTipoMovimiento());
        assertEquals(new BigDecimal("100"), movimientos.get(0).getImporte());
        assertEquals("Compra supermercado", movimientos.get(0).getDescripcion());
        assertEquals(LocalDate.of(2026, 9, 4), movimientos.get(0).getFechaHora().toLocalDate());
    }

    @Test
    void deberiaRechazarDependenciasObligatorias() {
        assertThrows(NullPointerException.class, () -> new GastosPanel(
                null,
                cuentaService,
                categoriaService,
                1L,
                1L
        ));
        assertThrows(NullPointerException.class, () -> new GastosPanel(
                new GastoService(movimientoService),
                null,
                categoriaService,
                1L,
                1L
        ));
        assertThrows(NullPointerException.class, () -> new GastosPanel(
                new GastoService(movimientoService),
                cuentaService,
                null,
                1L,
                1L
        ));
    }

    private Usuario crearUsuario() {
        return new Usuario(
                "Ariel",
                "Test",
                "ariel.gastos." + System.nanoTime(),
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
