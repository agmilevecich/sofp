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
}
