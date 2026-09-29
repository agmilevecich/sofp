package ar.com.agmilevecich.sofp.config;

import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.persistence.MonedaRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatosInicialesSistemaTest {

    private EntityManager entityManager;
    private MonedaRepository monedaRepository;

    @BeforeEach
    void setUp() {
        JpaTestManager.close();
        entityManager = JpaTestManager.createEntityManager();
        monedaRepository = new MonedaRepository(entityManager);
    }

    @AfterEach
    void tearDown() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
        JpaTestManager.close();
    }

    @Test
    void deberiaCrearMonedasBaseEnUnaBaseNueva() {
        DatosInicialesSistema.crearSiNoExisten(entityManager);

        Moneda ars = monedaRepository.buscarPorCodigo("ARS").orElseThrow();
        Moneda usd = monedaRepository.buscarPorCodigo("USD").orElseThrow();

        assertEquals("Peso argentino", ars.getNombre());
        assertEquals(2, ars.getCantidadDecimales());
        assertEquals("Dólar estadounidense", usd.getNombre());
        assertEquals(2, usd.getCantidadDecimales());
    }

    @Test
    void noDeberiaDuplicarMonedasBase() {
        DatosInicialesSistema.crearSiNoExisten(entityManager);
        DatosInicialesSistema.crearSiNoExisten(entityManager);

        assertEquals(
                1,
                monedaRepository.listarTodas().stream()
                        .filter(moneda -> "ARS".equals(moneda.getCodigo()))
                        .count()
        );
        assertEquals(
                1,
                monedaRepository.listarTodas().stream()
                        .filter(moneda -> "USD".equals(moneda.getCodigo()))
                        .count()
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoEntityManagerEsNulo() {
        assertThrows(
                NullPointerException.class,
                () -> DatosInicialesSistema.crearSiNoExisten(null)
        );
    }
}
