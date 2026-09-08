package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.EstadoObligacion;
import ar.com.agmilevecich.sofp.domain.FormaPago;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.Movimiento;
import ar.com.agmilevecich.sofp.domain.Obligacion;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.TipoCuenta;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.domain.TipoMovimiento;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import ar.com.agmilevecich.sofp.persistence.ObligacionRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GastoServiceTest {

    private EntityManager entityManager;
    private GastoService gastoService;
    private MovimientoService movimientoService;
    private ObligacionService obligacionService;
    private MovimientoRepository movimientoRepository;
    private Usuario usuario;
    private Cuenta cuenta;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();
        movimientoRepository = new MovimientoRepository(entityManager);
        movimientoService = new MovimientoService(
                entityManager,
                movimientoRepository
        );
        obligacionService = new ObligacionService(
                entityManager,
                new ObligacionRepository(entityManager)
        );
        gastoService = new GastoService(movimientoService, obligacionService);

        usuario = new Usuario(
                "Juan",
                "Pérez",
                "juan." + System.nanoTime() + "@test.com",
                "hash"
        );
        PerfilFinanciero perfilFinanciero = new PerfilFinanciero(
                "Perfil principal",
                usuario
        );
        usuario.agregarPerfilFinanciero(perfilFinanciero);

        InstitucionFinanciera institucionFinanciera = new InstitucionFinanciera(
                "Banco de Prueba",
                TipoInstitucionFinanciera.BANCO
        );
        Moneda moneda = new Moneda(
                "ARS",
                "Peso argentino",
                2,
                TipoMoneda.FIAT
        );
        cuenta = new Cuenta(
                "Cuenta principal",
                TipoCuenta.CAJA_AHORRO,
                perfilFinanciero,
                institucionFinanciera,
                moneda
        );
        categoria = new Categoria("Alimentos", perfilFinanciero);

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
        if (entityManager != null && entityManager.isOpen()) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            entityManager.close();
        }
        JpaTestManager.close();
    }

    @Test
    void deberiaRegistrarUnGastoConFormaDePago() {
        movimientoService.registrar(
                cuenta,
                categoria,
                TipoMovimiento.INGRESO,
                new BigDecimal("10000.00"),
                LocalDateTime.of(2026, 9, 7, 10, 0),
                "Saldo inicial",
                usuario.getId()
        );

        Movimiento movimiento = gastoService.registrar(
                cuenta,
                categoria,
                new BigDecimal("2500.00"),
                LocalDateTime.of(2026, 9, 7, 11, 0),
                "Compra de alimentos",
                FormaPago.EFECTIVO,
                usuario.getId()
        );

        assertNotNull(movimiento);
        assertNotNull(movimiento.getId());
        assertEquals(TipoMovimiento.EGRESO, movimiento.getTipoMovimiento());
        assertEquals(new BigDecimal("2500.00"), movimiento.getImporte());
        assertEquals(FormaPago.EFECTIVO, movimiento.getFormaPago());
        assertEquals("Compra de alimentos", movimiento.getDescripcion());
    }

    @Test
    void deberiaRechazarFormaDePagoNula() {
        assertThrows(
                NullPointerException.class,
                () -> gastoService.registrar(
                        cuenta,
                        categoria,
                        new BigDecimal("1000.00"),
                        LocalDateTime.of(2026, 9, 7, 11, 30),
                        "Gasto",
                        null,
                        usuario.getId()
                )
        );
    }

    @Test
    void deberiaRegistrarCompraConTarjetaDeCreditoYCrearObligacionSinConsumirSaldo() {
        movimientoService.registrar(
                cuenta,
                categoria,
                TipoMovimiento.INGRESO,
                new BigDecimal("100.00"),
                LocalDateTime.of(2026, 9, 7, 10, 0),
                "Saldo inicial",
                usuario.getId()
        );

        Movimiento movimiento = gastoService.registrar(
                cuenta,
                categoria,
                new BigDecimal("15000.00"),
                LocalDateTime.of(2026, 9, 7, 12, 0),
                "Compra con tarjeta",
                FormaPago.TARJETA_CREDITO,
                usuario.getId()
        );

        Obligacion obligacion = obligacionService
                .buscarPorMovimientoOrigen(movimiento.getId())
                .orElseThrow();

        assertNotNull(movimiento.getId());
        assertEquals(TipoMovimiento.EGRESO, movimiento.getTipoMovimiento());
        assertEquals(FormaPago.TARJETA_CREDITO, movimiento.getFormaPago());
        assertEquals(new BigDecimal("15000.00"), obligacion.getImporteOriginal());
        assertEquals(new BigDecimal("15000.00"), obligacion.getSaldoPendiente());
        assertEquals(EstadoObligacion.PENDIENTE, obligacion.getEstado());
        assertEquals(movimiento.getId(), obligacion.getMovimientoOrigen().getId());

        assertEquals(2, movimientoRepository.listarPorCuenta(cuenta.getId()).size());
        assertEquals(new BigDecimal("100.00"),
                movimientoRepository.listarPorCuenta(cuenta.getId()).stream()
                        .filter(m -> m.getTipoMovimiento() == TipoMovimiento.INGRESO)
                        .map(Movimiento::getImporte)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    @Test
    void deberiaRechazarDependenciaNula() {
        assertThrows(
                NullPointerException.class,
                () -> new GastoService(movimientoService, null)
        );
    }
}
