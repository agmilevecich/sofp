package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Activo;
import ar.com.agmilevecich.sofp.domain.Bono;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.Movimiento;
import ar.com.agmilevecich.sofp.domain.MovimientoActivo;
import ar.com.agmilevecich.sofp.domain.OperacionFinanciera;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.TipoCuenta;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.domain.TipoMovimiento;
import ar.com.agmilevecich.sofp.domain.TipoMovimientoActivo;
import ar.com.agmilevecich.sofp.domain.TipoOperacionFinanciera;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import ar.com.agmilevecich.sofp.persistence.OperacionFinancieraRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OperacionFinancieraVentaServiceTest {

    private EntityManager entityManager;
    private OperacionFinancieraService operacionFinancieraService;
    private Cuenta cuentaDestino;
    private Categoria categoriaDestino;
    private Activo activo;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();
        MovimientoRepository movimientoRepository = new MovimientoRepository(entityManager);
        OperacionFinancieraRepository operacionFinancieraRepository = new OperacionFinancieraRepository(entityManager);
        operacionFinancieraService = new OperacionFinancieraService(entityManager, movimientoRepository, operacionFinancieraRepository);

        Usuario usuario = new Usuario("Ariel", "Milevecich", "operacion.venta." + System.nanoTime() + "@test.com", "hash");
        PerfilFinanciero perfil = new PerfilFinanciero("Perfil principal", usuario);
        InstitucionFinanciera banco = new InstitucionFinanciera("Banco de Prueba", TipoInstitucionFinanciera.BANCO);
        Moneda moneda = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        cuentaDestino = new Cuenta("Cuenta destino", TipoCuenta.CAJA_AHORRO, perfil, banco, moneda);
        categoriaDestino = new Categoria("Ventas de inversiones", perfil);
        activo = new Bono("Bono GD30", "GD30", moneda);

        entityManager.getTransaction().begin();
        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(banco);
        entityManager.persist(moneda);
        entityManager.persist(cuentaDestino);
        entityManager.persist(categoriaDestino);
        entityManager.persist(activo);
        entityManager.getTransaction().commit();
    }

    @AfterEach
    void tearDown() {
        if (entityManager != null && entityManager.isOpen()) {
            if (entityManager.getTransaction().isActive()) entityManager.getTransaction().rollback();
            entityManager.close();
        }
        JpaTestManager.close();
    }

    @Test
    void deberiaVenderUnActivo() {
        LocalDateTime fechaHora = LocalDateTime.of(2026, 8, 27, 14, 0);
        BigDecimal cantidad = new BigDecimal("100");
        BigDecimal precioUnitario = new BigDecimal("125");
        BigDecimal importeEsperado = new BigDecimal("12500");

        OperacionFinanciera operacion = operacionFinancieraService.venderActivo(
                cuentaDestino, categoriaDestino, activo, cantidad, precioUnitario,
                fechaHora, "Venta Bono GD30");

        assertNotNull(operacion);
        assertNotNull(operacion.getId());
        assertEquals(TipoOperacionFinanciera.VENTA, operacion.getTipoOperacion());
        assertEquals(cuentaDestino.getId(), operacion.getCuentaDestino().getId());
        assertEquals(importeEsperado, operacion.getImporte());
        assertEquals(1, operacion.getMovimientos().size());

        Movimiento movimiento = operacion.getMovimientos().get(0);
        assertEquals(TipoMovimiento.INGRESO, movimiento.getTipoMovimiento());
        assertEquals(importeEsperado, movimiento.getImporte());
        assertEquals(cuentaDestino.getId(), movimiento.getCuenta().getId());

        assertEquals(1, operacion.getMovimientosActivos().size());
        MovimientoActivo movimientoActivo = operacion.getMovimientosActivos().get(0);
        assertEquals(activo.getId(), movimientoActivo.getActivo().getId());
        assertEquals(TipoMovimientoActivo.VENTA, movimientoActivo.getTipoMovimiento());
        assertEquals(cantidad, movimientoActivo.getCantidad());
        assertEquals(precioUnitario, movimientoActivo.getPrecioUnitario());
    }

    @Test
    void deberiaPersistirYRecuperarVentaDeActivo() {
        BigDecimal cantidad = new BigDecimal("100");
        BigDecimal precioUnitario = new BigDecimal("125");
        BigDecimal importeEsperado = new BigDecimal("12500");

        OperacionFinanciera operacion = operacionFinancieraService.venderActivo(
                cuentaDestino, categoriaDestino, activo, cantidad, precioUnitario,
                LocalDateTime.of(2026, 8, 27, 14, 0), "Venta Bono GD30");

        Long id = operacion.getId();
        entityManager.clear();
        OperacionFinanciera recuperada = new OperacionFinancieraRepository(entityManager)
                .buscarPorId(id).orElseThrow();

        assertEquals(TipoOperacionFinanciera.VENTA, recuperada.getTipoOperacion());
        assertEquals(0, importeEsperado.compareTo(recuperada.getImporte()));
        assertEquals(cuentaDestino.getId(), recuperada.getCuentaDestino().getId());
        assertEquals(1, recuperada.getMovimientos().size());
        assertEquals(1, recuperada.getMovimientosActivos().size());

        Movimiento movimiento = recuperada.getMovimientos().get(0);
        assertEquals(TipoMovimiento.INGRESO, movimiento.getTipoMovimiento());
        assertEquals(0, importeEsperado.compareTo(movimiento.getImporte()));
        assertEquals(id, movimiento.getOperacionFinanciera().getId());
        assertEquals(cuentaDestino.getId(), movimiento.getCuenta().getId());

        MovimientoActivo movimientoActivo = recuperada.getMovimientosActivos().get(0);
        assertEquals(id, movimientoActivo.getOperacionFinanciera().getId());
        assertEquals(activo.getId(), movimientoActivo.getActivo().getId());
        assertEquals(TipoMovimientoActivo.VENTA, movimientoActivo.getTipoMovimiento());
        assertEquals(0, cantidad.compareTo(movimientoActivo.getCantidad()));
        assertEquals(0, precioUnitario.compareTo(movimientoActivo.getPrecioUnitario()));
    }

    @Test
    void deberiaRechazarCuentaDestinoNula() {
        assertThrows(NullPointerException.class, () -> operacionFinancieraService.venderActivo(
                null, categoriaDestino, activo, new BigDecimal("100"), new BigDecimal("125"),
                LocalDateTime.now(), "Venta"));
    }

    @Test
    void deberiaRechazarCategoriaDestinoNula() {
        assertThrows(NullPointerException.class, () -> operacionFinancieraService.venderActivo(
                cuentaDestino, null, activo, new BigDecimal("100"), new BigDecimal("125"),
                LocalDateTime.now(), "Venta"));
    }

    @Test
    void deberiaRechazarActivoNulo() {
        assertThrows(NullPointerException.class, () -> operacionFinancieraService.venderActivo(
                cuentaDestino, categoriaDestino, null, new BigDecimal("100"), new BigDecimal("125"),
                LocalDateTime.now(), "Venta"));
    }

    @Test
    void deberiaRechazarCantidadNula() {
        assertThrows(NullPointerException.class, () -> operacionFinancieraService.venderActivo(
                cuentaDestino, categoriaDestino, activo, null, new BigDecimal("125"),
                LocalDateTime.now(), "Venta"));
    }

    @Test
    void deberiaRechazarPrecioUnitarioNulo() {
        assertThrows(NullPointerException.class, () -> operacionFinancieraService.venderActivo(
                cuentaDestino, categoriaDestino, activo, new BigDecimal("100"), null,
                LocalDateTime.now(), "Venta"));
    }

    @Test
    void deberiaRechazarCantidadCero() {
        assertThrows(IllegalArgumentException.class, () -> operacionFinancieraService.venderActivo(
                cuentaDestino, categoriaDestino, activo, BigDecimal.ZERO, new BigDecimal("125"),
                LocalDateTime.now(), "Venta"));
    }

    @Test
    void deberiaRechazarCantidadNegativa() {
        assertThrows(IllegalArgumentException.class, () -> operacionFinancieraService.venderActivo(
                cuentaDestino, categoriaDestino, activo, new BigDecimal("-1"), new BigDecimal("125"),
                LocalDateTime.now(), "Venta"));
    }

    @Test
    void deberiaRechazarPrecioUnitarioCero() {
        assertThrows(IllegalArgumentException.class, () -> operacionFinancieraService.venderActivo(
                cuentaDestino, categoriaDestino, activo, new BigDecimal("100"), BigDecimal.ZERO,
                LocalDateTime.now(), "Venta"));
    }

    @Test
    void deberiaRechazarPrecioUnitarioNegativo() {
        assertThrows(IllegalArgumentException.class, () -> operacionFinancieraService.venderActivo(
                cuentaDestino, categoriaDestino, activo, new BigDecimal("100"), new BigDecimal("-125"),
                LocalDateTime.now(), "Venta"));
    }

    @Test
    void deberiaRechazarCuentaDestinoDesactivada() {
        cuentaDestino.desactivar();
        assertThrows(IllegalArgumentException.class, () -> operacionFinancieraService.venderActivo(
                cuentaDestino, categoriaDestino, activo, new BigDecimal("100"), new BigDecimal("125"),
                LocalDateTime.now(), "Venta"));
    }

    @Test
    void deberiaRechazarCategoriaDeOtroPerfil() {
        Usuario otroUsuario = new Usuario("Otro", "Usuario", "operacion.venta.otro." + System.nanoTime() + "@test.com", "hash");
        PerfilFinanciero otroPerfil = new PerfilFinanciero("Otro perfil", otroUsuario);
        Categoria otraCategoria = new Categoria("Ventas de otro perfil", otroPerfil);

        assertThrows(IllegalArgumentException.class, () -> operacionFinancieraService.venderActivo(
                cuentaDestino, otraCategoria, activo, new BigDecimal("100"), new BigDecimal("125"),
                LocalDateTime.now(), "Venta"));
    }
}
