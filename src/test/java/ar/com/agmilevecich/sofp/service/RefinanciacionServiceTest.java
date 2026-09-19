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
import ar.com.agmilevecich.sofp.domain.Refinanciacion;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMovimiento;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RefinanciacionServiceTest {

    private EntityManager entityManager;
    private RefinanciacionService service;
    private long usuarioId;
    private Obligacion obligacion;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();
        service = new RefinanciacionService(entityManager);

        var usuario = new ar.com.agmilevecich.sofp.domain.Usuario(
                "Ariel", "Milevecich",
                "refinanciacion." + System.nanoTime() + "@test.com", "hash"
        );
        PerfilFinanciero perfil = new PerfilFinanciero("Personal", usuario);
        usuario.agregarPerfilFinanciero(perfil);
        InstitucionFinanciera banco = new InstitucionFinanciera(
                "Banco de Prueba", TipoInstitucionFinanciera.BANCO
        );
        Moneda ars = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        Cuenta tarjeta = new Cuenta(
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
                new BigDecimal("120000.00"),
                LocalDateTime.of(2026, 9, 9, 10, 0),
                "Compra", FormaPago.TARJETA_CREDITO
        );
        entityManager.persist(movimiento);
        obligacion = new Obligacion(movimiento);
        entityManager.persist(obligacion);
        entityManager.getTransaction().commit();
        usuarioId = usuario.getId();
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
    void deberiaCrearPlanYConservarDeudaOrigenAuditable() {
        Refinanciacion refinanciacion = service.crear(
                obligacion.getId(), usuarioId,
                LocalDate.of(2026, 9, 26),
                3,
                new BigDecimal("6000.00"),
                new BigDecimal("1000.00"),
                new BigDecimal("24.0000")
        );

        assertEquals(EstadoObligacion.REFINANCIADA, obligacion.getEstado());
        assertEquals(new BigDecimal("127000.00"), refinanciacion.getTotalPlan());
        assertEquals(3, refinanciacion.getCuotas().size());
        assertEquals(new BigDecimal("42333.33"), refinanciacion.getCuotas().get(0).getImporteOriginal());
        assertEquals(new BigDecimal("42333.34"), refinanciacion.getCuotas().get(2).getImporteOriginal());
        assertEquals(obligacion.getId(), refinanciacion.getObligacionOrigen().getId());
    }

    @Test
    void deberiaAplicarPagoParcialAlPlanRefinanciado() {
        Refinanciacion refinanciacion = service.crear(
                obligacion.getId(), usuarioId,
                LocalDate.of(2026, 9, 26),
                3,
                new BigDecimal("6000.00"),
                new BigDecimal("1000.00"),
                new BigDecimal("24.0000")
        );

        service.registrarPago(refinanciacion.getId(), usuarioId, new BigDecimal("50000.00"));

        assertEquals(new BigDecimal("77000.00"), refinanciacion.getSaldoPlan());
        assertEquals(new BigDecimal("0.00"), refinanciacion.getCuotas().get(0).getSaldoPendiente());
        assertEquals(new BigDecimal("34666.66"), refinanciacion.getCuotas().get(1).getSaldoPendiente());
    }

    @Test
    void noDeberiaRefinanciarUnaObligacionPagada() {
        entityManager.getTransaction().begin();
        obligacion.registrarPago(new BigDecimal("120000.00"));
        entityManager.getTransaction().commit();

        assertThrows(
                IllegalStateException.class,
                () -> service.crear(
                        obligacion.getId(), usuarioId,
                        LocalDate.of(2026, 9, 26), 3,
                        BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("24.0000")
                )
        );
    }
}
