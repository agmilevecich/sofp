package ar.com.agmilevecich.sofp.persistence;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.TipoCambio;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TipoCambioRepositoryTest {

    private EntityManager em;

    @BeforeEach
    void iniciarBaseDeDatos() {
        JpaTestManager.close();
        em = JpaTestManager.createEntityManager();
    }

    @AfterEach
    void cerrarBaseDeDatos() {
        if (em != null && em.isOpen()) {
            em.close();
        }

        JpaTestManager.close();
    }

    @Test
    void deberiaBuscarLaUltimaCotizacionDelDiaPorMonedas() {
        Moneda ars = new Moneda(
                "ARS", "Peso argentino", 2, TipoMoneda.FIAT
        );
        Moneda usd = new Moneda(
                "USD", "Dólar estadounidense", 2, TipoMoneda.FIAT
        );

        TipoCambio cotizacionAnterior = new TipoCambio(
                usd,
                ars,
                new BigDecimal("1490.0000000000"),
                LocalDateTime.of(2026, 9, 15, 10, 0),
                "Fuente test"
        );
        TipoCambio cotizacionUltima = new TipoCambio(
                usd,
                ars,
                new BigDecimal("1500.0000000000"),
                LocalDateTime.of(2026, 9, 15, 17, 30),
                "Fuente test"
        );
        TipoCambio cotizacionSiguienteDia = new TipoCambio(
                usd,
                ars,
                new BigDecimal("1510.0000000000"),
                LocalDateTime.of(2026, 9, 16, 10, 0),
                "Fuente test"
        );

        em.getTransaction().begin();
        em.persist(ars);
        em.persist(usd);
        em.persist(cotizacionAnterior);
        em.persist(cotizacionUltima);
        em.persist(cotizacionSiguienteDia);
        em.getTransaction().commit();

        TipoCambioRepository repository = new TipoCambioRepository(em);

        Optional<TipoCambio> resultado = repository.buscarPorMonedasYFecha(
                usd,
                ars,
                LocalDate.of(2026, 9, 15)
        );

        assertTrue(resultado.isPresent());
        assertEquals(cotizacionUltima.getId(), resultado.get().getId());
        assertEquals(new BigDecimal("1500.0000000000"), resultado.get().getCotizacion());
        assertEquals(LocalDateTime.of(2026, 9, 15, 17, 30), resultado.get().getFechaHora());
    }

    @Test
    void deberiaDevolverOptionalVacioSiNoHayCotizacion() {
        Moneda ars = new Moneda(
                "ARS", "Peso argentino", 2, TipoMoneda.FIAT
        );
        Moneda usd = new Moneda(
                "USD", "Dólar estadounidense", 2, TipoMoneda.FIAT
        );

        em.getTransaction().begin();
        em.persist(ars);
        em.persist(usd);
        em.getTransaction().commit();

        TipoCambioRepository repository = new TipoCambioRepository(em);

        Optional<TipoCambio> resultado = repository.buscarPorMonedasYFecha(
                usd,
                ars,
                LocalDate.of(2026, 9, 15)
        );

        assertTrue(resultado.isEmpty());
    }

    @Test
    void deberiaNoConfundirParDeMonedasNiFecha() {
        Moneda ars = new Moneda(
                "ARS", "Peso argentino", 2, TipoMoneda.FIAT
        );
        Moneda usd = new Moneda(
                "USD", "Dólar estadounidense", 2, TipoMoneda.FIAT
        );
        Moneda eur = new Moneda(
                "EUR", "Euro", 2, TipoMoneda.FIAT
        );

        TipoCambio usdArs = new TipoCambio(
                usd,
                ars,
                new BigDecimal("1500.0000000000"),
                LocalDateTime.of(2026, 9, 15, 17, 30),
                "Fuente test"
        );

        em.getTransaction().begin();
        em.persist(ars);
        em.persist(usd);
        em.persist(eur);
        em.persist(usdArs);
        em.getTransaction().commit();

        TipoCambioRepository repository = new TipoCambioRepository(em);

        assertTrue(repository.buscarPorMonedasYFecha(
                ars, usd, LocalDate.of(2026, 9, 15)
        ).isEmpty());
        assertTrue(repository.buscarPorMonedasYFecha(
                usd, ars, LocalDate.of(2026, 9, 16)
        ).isEmpty());
        assertTrue(repository.buscarPorMonedasYFecha(
                usd, eur, LocalDate.of(2026, 9, 15)
        ).isEmpty());
    }

    @Test
    void deberiaRechazarMonedaOrigenNula() {
        Moneda ars = new Moneda(
                "ARS", "Peso argentino", 2, TipoMoneda.FIAT
        );
        TipoCambioRepository repository = new TipoCambioRepository(em);

        assertThrows(
                NullPointerException.class,
                () -> repository.buscarPorMonedasYFecha(
                        null, ars, LocalDate.of(2026, 9, 15)
                )
        );
    }

    @Test
    void deberiaRechazarMonedaDestinoNula() {
        Moneda usd = new Moneda(
                "USD", "Dólar estadounidense", 2, TipoMoneda.FIAT
        );
        TipoCambioRepository repository = new TipoCambioRepository(em);

        assertThrows(
                NullPointerException.class,
                () -> repository.buscarPorMonedasYFecha(
                        usd, null, LocalDate.of(2026, 9, 15)
                )
        );
    }

    @Test
    void deberiaRechazarFechaNula() {
        Moneda ars = new Moneda(
                "ARS", "Peso argentino", 2, TipoMoneda.FIAT
        );
        Moneda usd = new Moneda(
                "USD", "Dólar estadounidense", 2, TipoMoneda.FIAT
        );
        TipoCambioRepository repository = new TipoCambioRepository(em);

        assertThrows(
                NullPointerException.class,
                () -> repository.buscarPorMonedasYFecha(
                        usd, ars, null
                )
        );
    }
}
