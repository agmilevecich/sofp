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

import static org.junit.jupiter.api.Assertions.*;

class TipoCambioServiceTest {

    private EntityManager entityManager;
    private TipoCambioService tipoCambioService;
    private Moneda ars;
    private Moneda usd;

    @BeforeEach
    void setUp() {
        JpaTestManager.close();
        entityManager = JpaTestManager.createEntityManager();
        tipoCambioService = new TipoCambioService(
                new TipoCambioRepository(entityManager)
        );

        ars = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        usd = new Moneda("USD", "Dólar estadounidense", 2, TipoMoneda.FIAT);

        entityManager.getTransaction().begin();
        entityManager.persist(ars);
        entityManager.persist(usd);
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
    void deberiaRegistrarCotizacion() {
        TipoCambio tipoCambio = new TipoCambio(
                usd,
                ars,
                new BigDecimal("1500.0000000000"),
                LocalDateTime.of(2026, 9, 15, 17, 30),
                "Fuente test"
        );

        TipoCambio registrado = tipoCambioService.registrar(tipoCambio);

        entityManager.getTransaction().commit();

        assertNotNull(registrado.getId());
        assertEquals(new BigDecimal("1500.0000000000"), registrado.getCotizacion());
    }

    @Test
    void deberiaRechazarTipoCambioNulo() {
        assertThrows(
                NullPointerException.class,
                () -> tipoCambioService.registrar(null)
        );
    }

    @Test
    void deberiaPersistirLaCotizacionParaQueSeaEncontradaPorFecha() {
        TipoCambio tipoCambio = new TipoCambio(
                usd,
                ars,
                new BigDecimal("1500.0000000000"),
                LocalDateTime.of(2026, 9, 15, 17, 30),
                "Fuente test"
        );

        tipoCambioService.registrar(tipoCambio);
        entityManager.getTransaction().commit();
        entityManager.clear();

        TipoCambio encontrada = new TipoCambioRepository(entityManager)
                .buscarPorMonedasYFecha(
                        usd,
                        ars,
                        java.time.LocalDate.of(2026, 9, 15)
                )
                .orElseThrow();

        assertEquals(tipoCambio.getId(), encontrada.getId());
        assertEquals(new BigDecimal("1500.0000000000"), encontrada.getCotizacion());
    }
}
