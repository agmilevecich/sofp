package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.persistence.MonedaRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MonedaServiceTest {

    private EntityManager entityManager;
    private MonedaService monedaService;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();

        MonedaRepository monedaRepository =
                new MonedaRepository(entityManager);

        monedaService =
                new MonedaService(monedaRepository);

        entityManager.getTransaction().begin();
    }

    @AfterEach
    void tearDown() {
        if (entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().rollback();
        }

        entityManager.close();
        JpaTestManager.close();
    }

    @Test
    void debeGuardarYBuscarPorId() {
        Moneda moneda =
                new Moneda(
                        "ARS",
                        "Peso argentino",
                        2,
                        TipoMoneda.FIAT
                );

        Moneda guardada = monedaService.guardar(moneda);

        entityManager.getTransaction().commit();

        Optional<Moneda> encontrada =
                monedaService.buscarPorId(guardada.getId());

        assertTrue(encontrada.isPresent());
        assertEquals("ARS", encontrada.get().getCodigo());
        assertEquals("Peso argentino", encontrada.get().getNombre());
    }

    @Test
    void debeBuscarPorCodigo() {
        Moneda moneda =
                new Moneda(
                        "USD",
                        "Dólar estadounidense",
                        2,
                        TipoMoneda.FIAT
                );

        monedaService.guardar(moneda);

        entityManager.getTransaction().commit();

        Optional<Moneda> encontrada =
                monedaService.buscarPorCodigo("USD");

        assertTrue(encontrada.isPresent());
        assertEquals("Dólar estadounidense",
                encontrada.get().getNombre());
    }

    @Test
    void debeListarTodasLasMonedas() {
        Moneda ars =
                new Moneda(
                        "ARS",
                        "Peso argentino",
                        2,
                        TipoMoneda.FIAT
                );

        Moneda usd =
                new Moneda(
                        "USD",
                        "Dólar estadounidense",
                        2,
                        TipoMoneda.FIAT
                );

        monedaService.guardar(ars);
        monedaService.guardar(usd);

        entityManager.getTransaction().commit();

        List<Moneda> monedas =
                monedaService.listarTodas();

        assertEquals(2, monedas.size());
    }

    @Test
    void debeCambiarNombre() {
        Moneda moneda =
                new Moneda(
                        "ARS",
                        "Peso argentino",
                        2,
                        TipoMoneda.FIAT
                );

        monedaService.guardar(moneda);

        entityManager.getTransaction().commit();

        Moneda actualizada =
                monedaService.cambiarNombre(
                        moneda.getId(),
                        "Peso argentino actualizado"
                );

        assertEquals(
                "Peso argentino actualizado",
                actualizada.getNombre()
        );
    }

    @Test
    void debeCambiarCantidadDecimales() {
        Moneda moneda =
                new Moneda(
                        "ARS",
                        "Peso argentino",
                        2,
                        TipoMoneda.FIAT
                );

        monedaService.guardar(moneda);

        entityManager.getTransaction().commit();

        Moneda actualizada =
                monedaService.cambiarCantidadDecimales(
                        moneda.getId(),
                        4
                );

        assertEquals(
                4,
                actualizada.getCantidadDecimales()
        );
    }

    @Test
    void debeLanzarExcepcionAlCambiarNombreDeMonedaInexistente() {
        assertThrows(
                IllegalArgumentException.class,
                () -> monedaService.cambiarNombre(
                        999999L,
                        "Nombre inexistente"
                )
        );
    }

    @Test
    void debeLanzarExcepcionAlCambiarDecimalesDeMonedaInexistente() {
        assertThrows(
                IllegalArgumentException.class,
                () -> monedaService.cambiarCantidadDecimales(
                        999999L,
                        4
                )
        );
    }

    @Test
    void debeCrearMonedaActivaComoComportamientoDelDominio() {
        Moneda moneda =
                new Moneda(
                        "EUR",
                        "Euro",
                        2,
                        TipoMoneda.FIAT
                );

        Moneda guardada = monedaService.guardar(moneda);

        assertNotNull(guardada);
        assertEquals("EUR", guardada.getCodigo());
        assertEquals("Euro", guardada.getNombre());
    }

    @Test
    void debeLanzarExcepcionAlGuardarMonedaNula() {

        assertThrows(
                NullPointerException.class,
                () -> monedaService.guardar(null)
        );
    }

    @Test
    void debeLanzarExcepcionAlBuscarPorIdNulo() {

        assertThrows(
                NullPointerException.class,
                () -> monedaService.buscarPorId(null)
        );
    }

    @Test
    void debeDevolverVacioAlBuscarPorIdInexistente() {

        assertTrue(
                monedaService.buscarPorId(999999L).isEmpty()
        );
    }

    @Test
    void debeLanzarExcepcionAlBuscarPorCodigoNulo() {

        assertThrows(
                NullPointerException.class,
                () -> monedaService.buscarPorCodigo(null)
        );
    }

    @Test
    void debeDevolverVacioAlBuscarPorCodigoInexistente() {

        assertTrue(
                monedaService.buscarPorCodigo("XXX").isEmpty()
        );
    }

    @Test
    void debeLanzarExcepcionAlCambiarNombreConIdNulo() {

        assertThrows(
                NullPointerException.class,
                () -> monedaService.cambiarNombre(
                        null,
                        "Nuevo nombre"
                )
        );
    }

    @Test
    void debeLanzarExcepcionAlCambiarNombreConNombreNulo() {

        Moneda moneda =
                new Moneda(
                        "ARS",
                        "Peso argentino",
                        2,
                        TipoMoneda.FIAT
                );

        monedaService.guardar(moneda);

        entityManager.getTransaction().commit();

        assertThrows(
                NullPointerException.class,
                () -> monedaService.cambiarNombre(
                        moneda.getId(),
                        null
                )
        );
    }

    @Test
    void debeLanzarExcepcionAlCambiarDecimalesConIdNulo() {

        assertThrows(
                NullPointerException.class,
                () -> monedaService.cambiarCantidadDecimales(
                        null,
                        4
                )
        );
    }

    @Test
    void debeLanzarExcepcionAlCambiarDecimalesConCantidadNula() {

        Moneda moneda =
                new Moneda(
                        "ARS",
                        "Peso argentino",
                        2,
                        TipoMoneda.FIAT
                );

        monedaService.guardar(moneda);

        entityManager.getTransaction().commit();

        assertThrows(
                NullPointerException.class,
                () -> monedaService.cambiarCantidadDecimales(
                        moneda.getId(),
                        null
                )
        );
    }
}