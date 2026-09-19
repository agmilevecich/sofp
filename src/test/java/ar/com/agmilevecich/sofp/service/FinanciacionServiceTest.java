package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.CargoFinanciero;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.FormaPago;
import ar.com.agmilevecich.sofp.domain.Financiacion;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.Movimiento;
import ar.com.agmilevecich.sofp.domain.Obligacion;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.domain.TipoMovimiento;
import ar.com.agmilevecich.sofp.domain.TipoTasaInteres;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FinanciacionServiceTest {

    private EntityManager entityManager;
    private FinanciacionService service;
    private Cuenta tarjeta;
    private long usuarioId;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();
        service = new FinanciacionService(entityManager);

        var usuario = new ar.com.agmilevecich.sofp.domain.Usuario(
                "Ariel", "Milevecich",
                "financiacion.service." + System.nanoTime() + "@test.com", "hash"
        );
        PerfilFinanciero perfil = new PerfilFinanciero("Personal", usuario);
        usuario.agregarPerfilFinanciero(perfil);
        InstitucionFinanciera banco = new InstitucionFinanciera(
                "Banco de Prueba", TipoInstitucionFinanciera.BANCO
        );
        Moneda ars = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        tarjeta = new Cuenta(
                "Tarjeta", perfil, banco, ars,
                new BigDecimal("500000.00"), 15, 5
        );
        Categoria categoria = new Categoria("Compras", perfil);

        entityManager.getTransaction().begin();
        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(banco);
        entityManager.persist(ars);
        entityManager.persist(tarjeta);
        entityManager.persist(categoria);

        Movimiento movimiento = new Movimiento(
                tarjeta, categoria, TipoMovimiento.EGRESO,
                new BigDecimal("100000.00"),
                LocalDateTime.of(2026, 9, 9, 10, 0),
                "Compra", FormaPago.TARJETA_CREDITO
        );
        entityManager.persist(movimiento);
        Obligacion obligacion = new Obligacion(movimiento);
        entityManager.persist(obligacion);
        Financiacion financiacion = obligacion.crearFinanciacion(
                LocalDate.of(2026, 9, 10),
                new BigDecimal("100000.00")
        );
        entityManager.persist(financiacion);
        entityManager.getTransaction().commit();
        usuarioId = usuario.getId();
    }

    @AfterEach
    void tearDown() {
        if (entityManager != null && entityManager.isOpen()) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            entityManager.close();
        }
    }

    @Test
    void deberiaCalcularInteresSimpleSobreCapitalPendiente() {
        Financiacion financiacion = entityManager.createQuery(
                "SELECT f FROM Financiacion f", Financiacion.class
        ).getSingleResult();

        service.registrarTna(
                tarjeta.getId(), usuarioId,
                TipoTasaInteres.TNA_FINANCIERA,
                LocalDate.of(2026, 9, 1), null,
                new BigDecimal("36.5000"), "TEST"
        );

        CargoFinanciero cargo = service.calcularInteres(
                financiacion.getId(), LocalDate.of(2026, 9, 12), usuarioId
        );

        assertEquals(new BigDecimal("200.00"), cargo.getImporteOriginal());
        assertEquals(2, cargo.getDiasCalculo());
        assertEquals(new BigDecimal("100000.00"), cargo.getCapitalBase());
        assertEquals(new BigDecimal("36.5000"), cargo.getTasaAnual());
        assertEquals(new BigDecimal("200.00"), financiacion.getSaldoCargosPendiente());
    }

    @Test
    void deberiaCalcularPunitorioSeparadoDelInteresFinanciero() {
        Financiacion financiacion = entityManager.createQuery(
                "SELECT f FROM Financiacion f", Financiacion.class
        ).getSingleResult();

        service.registrarTna(
                tarjeta.getId(), usuarioId,
                TipoTasaInteres.TNA_PUNITORIA,
                LocalDate.of(2026, 9, 1), null,
                new BigDecimal("18.2500"), "TEST PUNITORIO"
        );

        CargoFinanciero cargo = service.calcularPunitorio(
                financiacion.getId(), LocalDate.of(2026, 9, 12), usuarioId
        );

        assertEquals(new BigDecimal("100.00"), cargo.getImporteOriginal());
        assertEquals("INTERES_PUNITORIO", cargo.getTipo().name());
        assertEquals(2, cargo.getDiasCalculo());
        assertEquals(new BigDecimal("100000.00"), cargo.getCapitalBase());
    }

    @Test
    void deberiaCancelarAnticipadamenteSinGenerarInteresFuturo() {
        Financiacion financiacion = entityManager.createQuery(
                "SELECT f FROM Financiacion f", Financiacion.class
        ).getSingleResult();

        service.registrarTna(
                tarjeta.getId(), usuarioId,
                TipoTasaInteres.TNA_FINANCIERA,
                LocalDate.of(2026, 9, 1), null,
                new BigDecimal("36.5000"), "TEST"
        );
        service.calcularInteres(financiacion.getId(), LocalDate.of(2026, 9, 12), usuarioId);

        service.cancelarAnticipadamente(financiacion.getId(), usuarioId);

        assertEquals(new BigDecimal("0.00"), financiacion.getSaldoCapital());
        assertEquals(new BigDecimal("0.00"), financiacion.getSaldoCargosPendiente());
        assertEquals(new BigDecimal("0.00"), financiacion.getSaldoTotalPendiente());
    }

    @Test
    void noDeberiaCalcularInteresSinTnaVigente() {
        Financiacion financiacion = entityManager.createQuery(
                "SELECT f FROM Financiacion f", Financiacion.class
        ).getSingleResult();

        assertThrows(
                IllegalArgumentException.class,
                () -> service.calcularInteres(
                        financiacion.getId(),
                        LocalDate.of(2026, 9, 12),
                        usuarioId
                )
        );
    }
}
