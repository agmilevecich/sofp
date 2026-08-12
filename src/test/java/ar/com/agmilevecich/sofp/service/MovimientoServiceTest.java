package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.Movimiento;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.TipoCuenta;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.domain.TipoMovimiento;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MovimientoServiceTest {

    private EntityManager entityManager;
    private MovimientoService movimientoService;

    private Usuario usuario;
    private PerfilFinanciero perfilFinanciero;
    private InstitucionFinanciera institucionFinanciera;
    private Moneda moneda;
    private Cuenta cuenta;
    private Categoria categoria;

    @BeforeEach
    void setUp() {

        entityManager = JpaTestManager.createEntityManager();

        MovimientoRepository movimientoRepository =
                new MovimientoRepository(entityManager);

        movimientoService =
                new MovimientoService(
                        entityManager,
                        movimientoRepository
                );

        usuario = new Usuario(
                "Juan",
                "Pérez",
                "juan." + System.nanoTime() + "@test.com",
                "hash"
        );

        perfilFinanciero =
                new PerfilFinanciero(
                        "Perfil principal",
                        usuario
                );

        usuario.agregarPerfilFinanciero(perfilFinanciero);

        institucionFinanciera =
                new InstitucionFinanciera(
                        "Banco de Prueba",
                        TipoInstitucionFinanciera.BANCO
                );

        moneda =
                new Moneda(
                        "ARS",
                        "Peso argentino",
                        2,
                        TipoMoneda.FIAT
                );

        cuenta =
                new Cuenta(
                        "Cuenta principal",
                        TipoCuenta.CAJA_AHORRO,
                        perfilFinanciero,
                        institucionFinanciera,
                        moneda
                );

        categoria =
                new Categoria(
                        "Alimentación",
                        perfilFinanciero
                );

        entityManager.getTransaction().begin();

        entityManager.persist(usuario);
        entityManager.persist(perfilFinanciero);
        entityManager.persist(institucionFinanciera);
        entityManager.persist(moneda);
        entityManager.persist(cuenta);
        entityManager.persist(categoria);

        entityManager.getTransaction().commit();
    }

    @AfterEach
    void tearDown() {

        if (entityManager != null
                && entityManager.isOpen()) {

            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }

            entityManager.close();
        }

        JpaTestManager.close();
    }

    @Test
    void deberiaRegistrarUnIngreso() {

        Movimiento movimiento =
                movimientoService.registrar(
                        cuenta,
                        categoria,
                        TipoMovimiento.INGRESO,
                        new BigDecimal("150000.00"),
                        LocalDateTime.of(
                                2026,
                                8,
                                11,
                                10,
                                0
                        ),
                        "Sueldo"
                );

        assertNotNull(movimiento);
        assertNotNull(movimiento.getId());
        assertEquals(
                new BigDecimal("150000.00"),
                movimiento.getImporte()
        );
        assertEquals(
                TipoMovimiento.INGRESO,
                movimiento.getTipoMovimiento()
        );
        assertEquals(
                "Sueldo",
                movimiento.getDescripcion()
        );
    }

    @Test
    void deberiaRegistrarUnEgreso() {

        Movimiento movimiento =
                movimientoService.registrar(
                        cuenta,
                        categoria,
                        TipoMovimiento.EGRESO,
                        new BigDecimal("25000.00"),
                        LocalDateTime.of(
                                2026,
                                8,
                                11,
                                12,
                                0
                        ),
                        "Compra supermercado"
                );

        assertNotNull(movimiento);
        assertNotNull(movimiento.getId());
        assertEquals(
                new BigDecimal("25000.00"),
                movimiento.getImporte()
        );
        assertEquals(
                TipoMovimiento.EGRESO,
                movimiento.getTipoMovimiento()
        );
        assertEquals(
                "Compra supermercado",
                movimiento.getDescripcion()
        );
    }

    @Test
    void deberiaListarMovimientosPorCuenta() {

        movimientoService.registrar(
                cuenta,
                categoria,
                TipoMovimiento.INGRESO,
                new BigDecimal("100000.00"),
                LocalDateTime.of(
                        2026,
                        8,
                        11,
                        9,
                        0
                ),
                "Sueldo"
        );

        movimientoService.registrar(
                cuenta,
                categoria,
                TipoMovimiento.EGRESO,
                new BigDecimal("15000.00"),
                LocalDateTime.of(
                        2026,
                        8,
                        11,
                        10,
                        0
                ),
                "Supermercado"
        );

        var movimientos =
                movimientoService.listarPorCuenta(
                        cuenta.getId()
                );

        assertEquals(2, movimientos.size());

        assertEquals(
                TipoMovimiento.INGRESO,
                movimientos.get(0).getTipoMovimiento()
        );

        assertEquals(
                TipoMovimiento.EGRESO,
                movimientos.get(1).getTipoMovimiento()
        );
    }

    @Test
    void deberiaListarMovimientosPorCategoria() {

        movimientoService.registrar(
                cuenta,
                categoria,
                TipoMovimiento.INGRESO,
                new BigDecimal("80000.00"),
                LocalDateTime.of(
                        2026,
                        8,
                        12,
                        9,
                        0
                ),
                "Sueldo"
        );

        movimientoService.registrar(
                cuenta,
                categoria,
                TipoMovimiento.EGRESO,
                new BigDecimal("12000.00"),
                LocalDateTime.of(
                        2026,
                        8,
                        12,
                        10,
                        0
                ),
                "Supermercado"
        );

        var movimientos =
                movimientoService.listarPorCategoria(
                        categoria.getId()
                );

        assertEquals(2, movimientos.size());

        assertEquals(
                TipoMovimiento.INGRESO,
                movimientos.get(0).getTipoMovimiento()
        );

        assertEquals(
                TipoMovimiento.EGRESO,
                movimientos.get(1).getTipoMovimiento()
        );
    }

    @Test
    void deberiaListarTodosLosMovimientos() {

        movimientoService.registrar(
                cuenta,
                categoria,
                TipoMovimiento.INGRESO,
                new BigDecimal("120000.00"),
                LocalDateTime.of(
                        2026,
                        8,
                        12,
                        8,
                        0
                ),
                "Sueldo"
        );

        movimientoService.registrar(
                cuenta,
                categoria,
                TipoMovimiento.EGRESO,
                new BigDecimal("20000.00"),
                LocalDateTime.of(
                        2026,
                        8,
                        12,
                        9,
                        0
                ),
                "Supermercado"
        );

        var movimientos =
                movimientoService.listarTodos();

        assertEquals(2, movimientos.size());

        assertEquals(
                TipoMovimiento.INGRESO,
                movimientos.get(0).getTipoMovimiento()
        );

        assertEquals(
                TipoMovimiento.EGRESO,
                movimientos.get(1).getTipoMovimiento()
        );
    }

    @Test
    void deberiaBuscarMovimientoPorId() {

        Movimiento movimientoRegistrado =
                movimientoService.registrar(
                        cuenta,
                        categoria,
                        TipoMovimiento.INGRESO,
                        new BigDecimal("95000.00"),
                        LocalDateTime.of(
                                2026,
                                8,
                                12,
                                11,
                                0
                        ),
                        "Transferencia recibida"
                );

        var resultado =
                movimientoService.buscarPorId(
                        movimientoRegistrado.getId()
                );

        assertEquals(true, resultado.isPresent());

        Movimiento movimientoEncontrado =
                resultado.orElseThrow();

        assertEquals(
                movimientoRegistrado.getId(),
                movimientoEncontrado.getId()
        );

        assertEquals(
                new BigDecimal("95000.00"),
                movimientoEncontrado.getImporte()
        );

        assertEquals(
                TipoMovimiento.INGRESO,
                movimientoEncontrado.getTipoMovimiento()
        );

        assertEquals(
                "Transferencia recibida",
                movimientoEncontrado.getDescripcion()
        );
    }
}