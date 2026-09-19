package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.OperacionFinanciera;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.TipoCuenta;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.domain.TipoMovimiento;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.CategoriaRepository;
import ar.com.agmilevecich.sofp.persistence.CuentaRepository;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import ar.com.agmilevecich.sofp.persistence.OperacionFinancieraRepository;
import ar.com.agmilevecich.sofp.service.CategoriaService;
import ar.com.agmilevecich.sofp.service.CuentaService;
import ar.com.agmilevecich.sofp.service.MovimientoService;
import ar.com.agmilevecich.sofp.service.OperacionFinancieraService;
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

class TransferenciasPanelTest {

    private EntityManager entityManager;
    private MovimientoService movimientoService;
    private CategoriaService categoriaService;
    private CuentaService cuentaService;
    private OperacionFinancieraService operacionFinancieraService;
    private OperacionFinancieraRepository operacionFinancieraRepository;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();
        MovimientoRepository movimientoRepository = new MovimientoRepository(entityManager);
        movimientoService = new MovimientoService(entityManager, movimientoRepository);
        categoriaService = new CategoriaService(entityManager, new CategoriaRepository(entityManager), movimientoRepository);
        cuentaService = new CuentaService(new CuentaRepository(entityManager), movimientoRepository, entityManager);
        operacionFinancieraRepository = new OperacionFinancieraRepository(entityManager);
        operacionFinancieraService = new OperacionFinancieraService(
                entityManager, movimientoRepository, operacionFinancieraRepository);
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
        TransferenciasPanel panel = new TransferenciasPanel();

        assertNotNull(panel.getCuentaOrigenComboBox());
        assertNotNull(panel.getCuentaDestinoComboBox());
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
        Categoria activaCategoria = new Categoria("Transferencias", perfil);
        Categoria inactivaCategoria = new Categoria("Antigua", perfil);
        inactivaCategoria.desactivar();

        persistir(usuario, perfil, institucion, moneda, activa, inactiva, activaCategoria, inactivaCategoria);

        TransferenciasPanel panel = new TransferenciasPanel(
                operacionFinancieraService, cuentaService, categoriaService,
                perfil.getId(), usuario.getId());

        assertEquals(1, panel.getCuentaOrigenComboBox().getItemCount());
        assertEquals(activa, panel.getCuentaOrigenComboBox().getItemAt(0));
        assertEquals(1, panel.getCuentaDestinoComboBox().getItemCount());
        assertEquals(activa, panel.getCuentaDestinoComboBox().getItemAt(0));
        assertEquals(1, panel.getCategoriaComboBox().getItemCount());
        assertEquals(activaCategoria, panel.getCategoriaComboBox().getItemAt(0));
        assertTrue(panel.getRegistrarButton().isEnabled());
    }

    @Test
    void deberiaRegistrarLaTransferenciaComoUnaOperacionConDosMovimientos() {
        Usuario usuario = crearUsuario();
        PerfilFinanciero perfil = new PerfilFinanciero("Perfil principal", usuario);
        usuario.agregarPerfilFinanciero(perfil);
        InstitucionFinanciera institucion = new InstitucionFinanciera("Banco Test", TipoInstitucionFinanciera.BANCO);
        Moneda moneda = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        Cuenta origen = new Cuenta("Cuenta origen", TipoCuenta.CAJA_AHORRO, perfil, institucion, moneda);
        Cuenta destino = new Cuenta("Cuenta destino", TipoCuenta.CAJA_AHORRO, perfil, institucion, moneda);
        Categoria categoria = new Categoria("Transferencias", perfil);
        persistir(usuario, perfil, institucion, moneda, origen, destino, categoria);

        TransferenciasPanel panel = new TransferenciasPanel(
                operacionFinancieraService, cuentaService, categoriaService,
                perfil.getId(), usuario.getId());
        panel.getCuentaOrigenComboBox().setSelectedItem(origen);
        panel.getCuentaDestinoComboBox().setSelectedItem(destino);
        panel.getCategoriaComboBox().setSelectedItem(categoria);
        panel.getImporteField().setText("50000");
        panel.getFechaField().setDate(LocalDate.of(2026, 9, 8));
        panel.getDescripcionField().setText("Transferencia entre cuentas");

        panel.registrarTransferencia();

        var operaciones = operacionFinancieraRepository.listarTodas();
        assertEquals(1, operaciones.size());
        OperacionFinanciera operacion = operaciones.get(0);
        assertEquals(origen, operacion.getCuentaOrigen());
        assertEquals(destino, operacion.getCuentaDestino());
        assertEquals(new BigDecimal("50000"), operacion.getImporte());
        assertEquals(2, operacion.getMovimientos().size());
        assertEquals(TipoMovimiento.EGRESO, operacion.getMovimientos().get(0).getTipoMovimiento());
        assertEquals(TipoMovimiento.INGRESO, operacion.getMovimientos().get(1).getTipoMovimiento());
        assertEquals(LocalDate.of(2026, 9, 8), operacion.getMovimientos().get(0).getFechaHora().toLocalDate());
        assertEquals("Transferencia entre cuentas", operacion.getMovimientos().get(0).getDescripcion());
    }

    @Test
    void deberiaRechazarDependenciasObligatorias() {
        assertThrows(NullPointerException.class, () -> new TransferenciasPanel(
                null, cuentaService, categoriaService, 1L, 1L));
        assertThrows(NullPointerException.class, () -> new TransferenciasPanel(
                operacionFinancieraService, null, categoriaService, 1L, 1L));
        assertThrows(NullPointerException.class, () -> new TransferenciasPanel(
                operacionFinancieraService, cuentaService, null, 1L, 1L));
    }

    private Usuario crearUsuario() {
        return new Usuario("Ariel", "Test", "ariel.transferencias." + System.nanoTime(), "hash");
    }

    private void persistir(Object... entidades) {
        entityManager.getTransaction().begin();
        for (Object entidad : entidades) {
            entityManager.persist(entidad);
        }
        entityManager.getTransaction().commit();
    }
}
