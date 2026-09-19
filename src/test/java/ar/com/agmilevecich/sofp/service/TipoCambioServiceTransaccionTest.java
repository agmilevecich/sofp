package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.TipoCambio;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.persistence.TipoCambioRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TipoCambioServiceTransaccionTest {

    private EntityManager entityManager;
    private Moneda ars;
    private Moneda usd;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();
        ars = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        usd = new Moneda("USD", "Dólar estadounidense", 2, TipoMoneda.FIAT);

        entityManager.getTransaction().begin();
        entityManager.persist(ars);
        entityManager.persist(usd);
        entityManager.getTransaction().commit();
        entityManager.clear();
    }

    @AfterEach
    void tearDown() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
        JpaTestManager.close();
    }

    @Test
    void deberiaRegistrarYConfirmarLaCotizacionSinTransaccionExterna() {
        TipoCambioService service = new TipoCambioService(
                entityManager,
                new TipoCambioRepository(entityManager)
        );
        TipoCambio tipoCambio = new TipoCambio(
                usd,
                ars,
                new BigDecimal("1500.0000000000"),
                LocalDateTime.of(2026, 9, 15, 17, 30),
                "Fuente test"
        );

        service.registrar(tipoCambio);
        entityManager.clear();

        TipoCambio encontrada = service.buscarPorMonedasYFecha(
                usd,
                ars,
                java.time.LocalDate.of(2026, 9, 15)
        ).orElseThrow();

        assertEquals(new BigDecimal("1500.0000000000"), encontrada.getCotizacion());
        assertTrue(!entityManager.getTransaction().isActive());
    }
}
