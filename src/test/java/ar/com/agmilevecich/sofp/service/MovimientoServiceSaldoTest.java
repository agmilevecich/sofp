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
import static org.junit.jupiter.api.Assertions.assertThrows;

class MovimientoServiceSaldoTest {

    private EntityManager entityManager;
    private MovimientoService movimientoService;

    private Usuario usuario;
    private PerfilFinanciero perfilFinanciero;
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
                "juan.saldo." + System.nanoTime() + "@test.com",
                "hash"
        );

        perfilFinanciero =
                new PerfilFinanciero(
                        "Perfil principal",
                        usuario
                );

        usuario.agregarPerfilFinanciero(perfilFinanciero);

        InstitucionFinanciera institucionFinanciera =
                new InstitucionFinanciera(
                        "Banco de Prueba",
                        TipoInstitucionFinanciera.BANCO
                );

        Moneda moneda =
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
    void deberiaRechazarEgresoCuandoSuperaElSaldoDisponible() {

        movimientoService.registrar(
                cuenta,
                categoria,
                TipoMovimiento.INGRESO,
                new BigDecimal("100.00"),
                LocalDateTime.of(2026, 9, 7, 10, 0),
                "Saldo inicial"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> movimientoService.registrar(
                        cuenta,
                        categoria,
                        TipoMovimiento.EGRESO,
                        new BigDecimal("100.01"),
                        LocalDateTime.of(2026, 9, 7, 11, 0),
                        "Egreso sin saldo suficiente"
                )
        );
    }

    @Test
    void deberiaPermitirEgresoExactamenteIgualAlSaldoDisponible() {

        movimientoService.registrar(
                cuenta,
                categoria,
                TipoMovimiento.INGRESO,
                new BigDecimal("100.00"),
                LocalDateTime.of(2026, 9, 7, 10, 0),
                "Saldo inicial"
        );

        Movimiento egreso =
                movimientoService.registrar(
                        cuenta,
                        categoria,
                        TipoMovimiento.EGRESO,
                        new BigDecimal("100.00"),
                        LocalDateTime.of(2026, 9, 7, 11, 0),
                        "Egreso por saldo exacto"
                );

        assertNotNull(egreso);
        assertEquals(
                new BigDecimal("100.00"),
                egreso.getImporte()
        );
        assertEquals(
                TipoMovimiento.EGRESO,
                egreso.getTipoMovimiento()
        );
    }

    @Test
    void deberiaPermitirAumentarImporteDeEgresoHastaElSaldoDisponible() {

        movimientoService.registrar(
                cuenta,
                categoria,
                TipoMovimiento.INGRESO,
                new BigDecimal("100.00"),
                LocalDateTime.of(2026, 9, 7, 10, 0),
                "Saldo inicial"
        );

        Movimiento egreso =
                movimientoService.registrar(
                        cuenta,
                        categoria,
                        TipoMovimiento.EGRESO,
                        new BigDecimal("40.00"),
                        LocalDateTime.of(2026, 9, 7, 11, 0),
                        "Egreso original"
                );

        Movimiento actualizado =
                movimientoService.modificarImporte(
                        egreso.getId(),
                        usuario.getId(),
                        new BigDecimal("100.00")
                );

        assertNotNull(actualizado);
        assertEquals(
                new BigDecimal("100.00"),
                actualizado.getImporte()
        );
    }
}
