package ar.com.agmilevecich.sofp.domain;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TipoCambioJpaTest {

    @Test
    void deberiaPersistirTipoCambioConSusMonedas() {
        EntityManager em = JpaTestManager.createEntityManager();

        Moneda ars = new Moneda(
                "ARS",
                "Peso Argentino",
                2,
                TipoMoneda.FIAT
        );

        Moneda usd = new Moneda(
                "USD",
                "Dólar Estadounidense",
                2,
                TipoMoneda.FIAT
        );

        LocalDateTime fechaHora = LocalDateTime.of(2026, 9, 15, 12, 0);

        TipoCambio tipoCambio = new TipoCambio(
                usd,
                ars,
                new BigDecimal("1500.00"),
                fechaHora,
                "Cotización manual"
        );

        em.getTransaction().begin();
        em.persist(ars);
        em.persist(usd);
        em.persist(tipoCambio);
        em.getTransaction().commit();

        Long id = tipoCambio.getId();

        em.clear();

        TipoCambio recuperado = em.find(TipoCambio.class, id);

        assertNotNull(recuperado);
        assertEquals(usd.getId(), recuperado.getMonedaOrigen().getId());
        assertEquals(ars.getId(), recuperado.getMonedaDestino().getId());
        assertEquals(new BigDecimal("1500.00"), recuperado.getCotizacion());
        assertEquals(fechaHora, recuperado.getFechaHora());
        assertEquals("Cotización manual", recuperado.getFuente());
        assertEquals(new BigDecimal("150000.00"), recuperado.convertir(new BigDecimal("100.00")));

        em.close();
        JpaTestManager.close();
    }
}
