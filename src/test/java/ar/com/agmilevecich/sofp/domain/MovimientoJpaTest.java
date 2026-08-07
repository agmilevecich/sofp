package ar.com.agmilevecich.sofp.domain;

import ar.com.agmilevecich.sofp.config.JpaManager;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MovimientoJpaTest {

    @Test
    void deberiaPersistirMovimiento() {

        EntityManager em = JpaManager.createEntityManager();

        Usuario usuario = new Usuario(
                "Ariel",
                "Milevecich",
                "ariel.movimiento@test.com",
                "hash"
        );

        PerfilFinanciero perfil = new PerfilFinanciero(
                "Personal",
                usuario
        );

        usuario.agregarPerfilFinanciero(perfil);

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
                new BigDecimal("15000.50"),
                LocalDateTime.now(),
                "Compra Carrefour"
        );

        em.getTransaction().begin();

        em.persist(usuario);
        em.persist(perfil);
        em.persist(banco);
        em.persist(moneda);
        em.persist(cuenta);
        em.persist(categoria);
        em.persist(movimiento);

        em.getTransaction().commit();

        Movimiento recuperado =
                em.find(Movimiento.class, movimiento.getId());

        assertNotNull(recuperado);

        assertEquals(
                "Compra Carrefour",
                recuperado.getDescripcion()
        );

        assertEquals(
                new BigDecimal("15000.50"),
                recuperado.getImporte()
        );

        assertEquals(
                TipoMovimiento.EGRESO,
                recuperado.getTipoMovimiento()
        );

        assertEquals(
                cuenta.getId(),
                recuperado.getCuenta().getId()
        );

        assertEquals(
                categoria.getId(),
                recuperado.getCategoria().getId()
        );

        em.close();
    }
}