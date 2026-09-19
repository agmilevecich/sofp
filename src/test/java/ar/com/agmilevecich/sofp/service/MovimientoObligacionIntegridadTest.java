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
import static org.junit.jupiter.api.Assertions.assertTrue;

class MovimientoObligacionIntegridadTest {

    private EntityManager entityManager;
    private MovimientoService movimientoService;
    private ObligacionService obligacionService;
    private MovimientoRepository movimientoRepository;
    private ObligacionRepository obligacionRepository;

    private Usuario usuario;
    private PerfilFinanciero perfilFinanciero;
    private Cuenta cuenta;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();
        movimientoRepository = new MovimientoRepository(entityManager);
        obligacionRepository = new ObligacionRepository(entityManager);
        movimientoService = new MovimientoService(entityManager, movimientoRepository, obligacionRepository);
        obligacionService = new ObligacionService(entityManager, obligacionRepository);

        usuario = new Usuario(
                "Juan",
                "Pérez",
                "juan.obligacion." + System.nanoTime() + "@test.com",
                "hash"
        );
        perfilFinanciero = new PerfilFinanciero("Perfil principal", usuario);
        usuario.agregarPerfilFinanciero(perfilFinanciero);

        InstitucionFinanciera institucionFinanciera = new InstitucionFinanciera(
                "Banco de Prueba",
                TipoInstitucionFinanciera.BANCO
        );
        Moneda moneda = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);

        cuenta = new Cuenta(
                "Tarjeta principal",
                perfilFinanciero,
                institucionFinanciera,
                moneda,
                new BigDecimal("500000.00"),
                10,
                20
        );
        categoria = new Categoria("Alimentación", perfilFinanciero);

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
    void noDeberiaModificarImporteDeMovimientoOrigenDeObligacion() {
        Movimiento movimiento = crearMovimientoOrigen();
        Obligacion obligacion = crearObligacion(movimiento);

        assertThrows(
                IllegalArgumentException.class,
                () -> movimientoService.modificarImporte(
                        movimiento.getId(),
                        usuario.getId(),
                        new BigDecimal("120000.00")
                )
        );

        verificarIntegridad(movimiento.getId(), obligacion.getId(), new BigDecimal("100000.00"), movimiento.getFechaHora(), TipoMovimiento.EGRESO);
    }

    @Test
    void noDeberiaModificarFechaHoraDeMovimientoOrigenDeObligacion() {
        Movimiento movimiento = crearMovimientoOrigen();
        Obligacion obligacion = crearObligacion(movimiento);
        LocalDateTime fechaOriginal = movimiento.getFechaHora();

        assertThrows(
                IllegalArgumentException.class,
                () -> movimientoService.modificarFechaHora(
                        movimiento.getId(),
                        usuario.getId(),
                        fechaOriginal.plusDays(2)
                )
        );

        verificarIntegridad(movimiento.getId(), obligacion.getId(), new BigDecimal("100000.00"), fechaOriginal, TipoMovimiento.EGRESO);
    }

    @Test
    void noDeberiaModificarTipoDeMovimientoOrigenDeObligacion() {
        Movimiento movimiento = crearMovimientoOrigen();
        Obligacion obligacion = crearObligacion(movimiento);

        assertThrows(
                IllegalArgumentException.class,
                () -> movimientoService.modificarTipoMovimiento(
                        movimiento.getId(),
                        usuario.getId(),
                        TipoMovimiento.INGRESO
                )
        );

        verificarIntegridad(movimiento.getId(), obligacion.getId(), new BigDecimal("100000.00"), movimiento.getFechaHora(), TipoMovimiento.EGRESO);
    }

    @Test
    void noDeberiaEliminarMovimientoOrigenDeObligacion() {
        Movimiento movimiento = crearMovimientoOrigen();
        Obligacion obligacion = crearObligacion(movimiento);

        assertThrows(
                IllegalArgumentException.class,
                () -> movimientoService.eliminar(movimiento.getId(), usuario.getId())
        );

        verificarIntegridad(movimiento.getId(), obligacion.getId(), new BigDecimal("100000.00"), movimiento.getFechaHora(), TipoMovimiento.EGRESO);
    }

    @Test
    void deberiaPermitirModificarDescripcionYObservacionesDelMovimientoOrigen() {
        Movimiento movimiento = crearMovimientoOrigen();
        Obligacion obligacion = crearObligacion(movimiento);

        movimientoService.modificarDescripcion(movimiento.getId(), usuario.getId(), "Compra actualizada");
        movimientoService.modificarObservaciones(movimiento.getId(), usuario.getId(), "Observación actualizada");

        entityManager.clear();

        Movimiento movimientoPersistido = movimientoRepository.buscarPorId(movimiento.getId()).orElseThrow();
        Obligacion obligacionPersistida = obligacionRepository.buscarPorId(obligacion.getId()).orElseThrow();

        assertEquals("Compra actualizada", movimientoPersistido.getDescripcion());
        assertEquals("Observación actualizada", movimientoPersistido.getObservaciones());
        assertEquals(new BigDecimal("100000.00"), movimientoPersistido.getImporte());
        assertEquals(new BigDecimal("100000.00"), obligacionPersistida.getImporteOriginal());
        assertEquals(new BigDecimal("100000.00"), obligacionPersistida.getSaldoPendiente());
    }

    private Movimiento crearMovimientoOrigen() {
        return movimientoService.registrar(
                cuenta,
                categoria,
                TipoMovimiento.EGRESO,
                new BigDecimal("100000.00"),
                LocalDateTime.of(2026, 9, 12, 12, 0),
                "Compra original",
                FormaPago.TARJETA_CREDITO,
                usuario.getId()
        );
    }

    private Obligacion crearObligacion(Movimiento movimiento) {
        return obligacionService.registrar(movimiento);
    }

    private void verificarIntegridad(
            Long movimientoId,
            Long obligacionId,
            BigDecimal importeOriginal,
            LocalDateTime fechaOriginal,
            TipoMovimiento tipoOriginal
    ) {
        entityManager.clear();

        Movimiento movimientoPersistido = movimientoRepository.buscarPorId(movimientoId).orElseThrow();
        Obligacion obligacionPersistida = obligacionRepository.buscarPorId(obligacionId).orElseThrow();

        assertTrue(movimientoPersistido.getId().equals(movimientoId));
        assertTrue(obligacionPersistida.getId().equals(obligacionId));
        assertEquals(importeOriginal, movimientoPersistido.getImporte());
        assertEquals(fechaOriginal, movimientoPersistido.getFechaHora());
        assertEquals(tipoOriginal, movimientoPersistido.getTipoMovimiento());
        assertEquals(importeOriginal, obligacionPersistida.getImporteOriginal());
        assertEquals(importeOriginal, obligacionPersistida.getSaldoPendiente());
        assertEquals(movimientoId, obligacionPersistida.getMovimientoOrigen().getId());
    }
}
