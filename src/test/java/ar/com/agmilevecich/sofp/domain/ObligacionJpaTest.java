package ar.com.agmilevecich.sofp.domain;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ObligacionJpaTest {

    @Test
    void deberiaPersistirObligacionConMovimientoDeOrigen() {

        EntityManager em = JpaTestManager.createEntityManager();

        Usuario usuario = new Usuario(
                "Ariel",
                "Milevecich",
                "ariel.obligacion.jpa@test.com",
                "hash"
        );

        PerfilFinanciero perfil = new PerfilFinanciero(
                "Personal",
                usuario
        );

        InstitucionFinanciera banco = new InstitucionFinanciera(
                "Banco Santander",
                TipoInstitucionFinanciera.BANCO
        );

        Moneda moneda = new Moneda(
                "ARS",
                "Peso Argentino",
                2,
                TipoMoneda.FIAT
        );

        Cuenta cuenta = new Cuenta(
                "Caja de Ahorro",
                TipoCuenta.CAJA_AHORRO,
                perfil,
                banco,
                moneda
        );

        Categoria categoria = new Categoria(
                "Supermercado",
                perfil
        );

        Movimiento movimiento = new Movimiento(
                cuenta,
                categoria,
                TipoMovimiento.EGRESO,
                new BigDecimal("10000.00"),
                LocalDateTime.of(2026, 9, 4, 12, 0),
                "Compra con tarjeta",
                FormaPago.TARJETA_CREDITO
        );

        Obligacion obligacion = new Obligacion(movimiento);

        em.getTransaction().begin();
        em.persist(usuario);
        em.persist(perfil);
        em.persist(banco);
        em.persist(moneda);
        em.persist(cuenta);
        em.persist(categoria);
        em.persist(movimiento);
        em.persist(obligacion);
        em.getTransaction().commit();

        Long id = obligacion.getId();

        em.clear();

        Obligacion recuperada = em.find(Obligacion.class, id);

        assertNotNull(recuperada);
        assertEquals(new BigDecimal("10000.00"), recuperada.getImporteOriginal());
        assertEquals(new BigDecimal("10000.00"), recuperada.getSaldoPendiente());
        assertEquals(EstadoObligacion.PENDIENTE, recuperada.getEstado());
        assertEquals(movimiento.getId(), recuperada.getMovimientoOrigen().getId());

        em.close();
        JpaTestManager.close();
    }

    @Test
    void deberiaPersistirObligacionConSusCuotas() {

        EntityManager em = JpaTestManager.createEntityManager();

        Usuario usuario = new Usuario(
                "Ariel",
                "Milevecich",
                "ariel.obligacion.cuotas.jpa@test.com",
                "hash"
        );

        PerfilFinanciero perfil = new PerfilFinanciero(
                "Personal",
                usuario
        );

        InstitucionFinanciera banco = new InstitucionFinanciera(
                "Banco Santander",
                TipoInstitucionFinanciera.BANCO
        );

        Moneda moneda = new Moneda(
                "ARS",
                "Peso Argentino",
                2,
                TipoMoneda.FIAT
        );

        Cuenta tarjeta = new Cuenta(
                "Visa",
                perfil,
                banco,
                moneda,
                new BigDecimal("500000.00"),
                15,
                10
        );

        Categoria categoria = new Categoria(
                "Supermercado",
                perfil
        );

        Movimiento movimiento = new Movimiento(
                tarjeta,
                categoria,
                TipoMovimiento.EGRESO,
                new BigDecimal("120000.00"),
                LocalDateTime.of(2026, 9, 10, 12, 0),
                "Compra en cuotas",
                FormaPago.TARJETA_CREDITO
        );

        Obligacion obligacion = new Obligacion(movimiento);
        obligacion.generarCuotas(3);

        em.getTransaction().begin();
        em.persist(usuario);
        em.persist(perfil);
        em.persist(banco);
        em.persist(moneda);
        em.persist(tarjeta);
        em.persist(categoria);
        em.persist(movimiento);
        em.persist(obligacion);
        em.getTransaction().commit();

        Long id = obligacion.getId();

        em.clear();

        Obligacion recuperada = em.find(Obligacion.class, id);

        assertNotNull(recuperada);
        assertEquals(3, recuperada.getCuotas().size());
        assertEquals(1, recuperada.getCuotas().get(0).getNumero());
        assertEquals(new BigDecimal("40000.00"), recuperada.getCuotas().get(0).getImporteOriginal());
        assertEquals(new BigDecimal("40000.00"), recuperada.getCuotas().get(0).getSaldoPendiente());
        assertEquals(EstadoObligacion.PENDIENTE, recuperada.getCuotas().get(0).getEstado());
        assertEquals(2, recuperada.getCuotas().get(1).getNumero());
        assertEquals(3, recuperada.getCuotas().get(2).getNumero());
        assertEquals(LocalDateTime.of(2026, 9, 10, 12, 0).toLocalDate(), recuperada.getCuotas().get(0).getFechaInicioCiclo().isAfter(LocalDateTime.of(2026, 9, 10, 12, 0).toLocalDate()) ? null : LocalDateTime.of(2026, 9, 10, 12, 0).toLocalDate());
        assertEquals(new BigDecimal("120000.00"), recuperada.getCuotas().stream()
                .map(Cuota::getImporteOriginal)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        assertSame(recuperada, recuperada.getCuotas().get(0).getObligacion());

        em.close();
        JpaTestManager.close();
    }
}
