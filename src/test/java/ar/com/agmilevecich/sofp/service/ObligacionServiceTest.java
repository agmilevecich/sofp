package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

class ObligacionServiceTest {

    private EntityManager entityManager;
    private ObligacionService obligacionService;
    private GastoService gastoService;
    private Cuenta cuenta;
    private Categoria categoria;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();

        MovimientoRepository movimientoRepository =
                new MovimientoRepository(entityManager);
        MovimientoService movimientoService =
                new MovimientoService(entityManager, movimientoRepository);

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
    void deberiaRegistrarPagoParcialYPersistirSaldoPendiente() {
        Obligacion obligacion = crearObligacion();

        Obligacion actualizada = obligacionService.registrarPago(
                obligacion.getId(),
                new BigDecimal("5000.00")
        );

        assertEquals(new BigDecimal("10000.00"), actualizada.getSaldoPendiente());
        assertEquals(ar.com.agmilevecich.sofp.domain.EstadoObligacion.PARCIAL,
                actualizada.getEstado());

        entityManager.clear();

        Obligacion recargada = obligacionService.buscarPorId(obligacion.getId())
                .orElseThrow();

        assertEquals(new BigDecimal("10000.00"), recargada.getSaldoPendiente());
        assertEquals(ar.com.agmilevecich.sofp.domain.EstadoObligacion.PARCIAL,
                recargada.getEstado());
    }

    @Test
    void deberiaRegistrarPagoCompletoYPersistirObligacionPagada() {
        Obligacion obligacion = crearObligacion();

        Obligacion actualizada = obligacionService.registrarPago(
                obligacion.getId(),
                new BigDecimal("15000.00")
        );

        assertEquals(BigDecimal.ZERO.setScale(2), actualizada.getSaldoPendiente());
        assertEquals(ar.com.agmilevecich.sofp.domain.EstadoObligacion.PAGADA,
                actualizada.getEstado());

        entityManager.clear();

        Obligacion recargada = obligacionService.buscarPorId(obligacion.getId())
                .orElseThrow();

        assertEquals(BigDecimal.ZERO.setScale(2), recargada.getSaldoPendiente());
        assertEquals(ar.com.agmilevecich.sofp.domain.EstadoObligacion.PAGADA,
                recargada.getEstado());
    }

    @Test
    void deberiaRechazarPagoSuperiorAlSaldoYSostenerEstadoOriginal() {
        Obligacion obligacion = crearObligacion();

        assertThrows(
                IllegalArgumentException.class,
                () -> obligacionService.registrarPago(
                        obligacion.getId(),
                        new BigDecimal("16000.00")
                )
        );

        entityManager.clear();

        Obligacion recargada = obligacionService.buscarPorId(obligacion.getId())
                .orElseThrow();

        assertEquals(new BigDecimal("15000.00"), recargada.getSaldoPendiente());
        assertEquals(ar.com.agmilevecich.sofp.domain.EstadoObligacion.PENDIENTE,
                recargada.getEstado());
    }

    @Test
    void deberiaRechazarObligacionInexistente() {
        assertThrows(
                IllegalArgumentException.class,
                () -> obligacionService.registrarPago(
                        999999L,
                        new BigDecimal("1000.00")
                )
        );
    }

    @Test
    void deberiaRechazarIdNulo() {
        assertThrows(
                NullPointerException.class,
                () -> obligacionService.registrarPago(
                        null,
                        new BigDecimal("1000.00")
                )
        );
    }

    private Obligacion crearObligacion() {
        Movimiento movimiento = gastoService.registrar(
                cuenta,
                categoria,
                new BigDecimal("15000.00"),
                LocalDateTime.of(2026, 9, 8, 10, 0),
                "Compra con tarjeta",
                FormaPago.TARJETA_CREDITO,
                usuario.getId()
        );

        return obligacionService.buscarPorMovimientoOrigen(movimiento.getId())
                .orElseThrow();
    }
}
