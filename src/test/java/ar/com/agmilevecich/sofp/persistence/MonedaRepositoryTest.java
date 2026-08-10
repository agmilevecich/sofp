package ar.com.agmilevecich.sofp.persistence;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MonedaRepositoryTest {

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
    void deberiaGuardarYBuscarMonedaPorId() {

        Moneda moneda =
                new Moneda(
                        "ARS",
                        "Peso argentino",
                        2,
                        TipoMoneda.FIAT
                );

        MonedaRepository repository =
                new MonedaRepository(em);

        em.getTransaction().begin();

        repository.guardar(moneda);

        em.getTransaction().commit();

        Optional<Moneda> resultado =
                repository.buscarPorId(moneda.getId());

        assertTrue(resultado.isPresent());

        assertEquals(
                "ARS",
                resultado.get().getCodigo()
        );

        assertEquals(
                "Peso argentino",
                resultado.get().getNombre()
        );

        assertEquals(
                TipoMoneda.FIAT,
                resultado.get().getTipo()
        );
    }

    @Test
    void deberiaBuscarMonedaPorCodigo() {

        Moneda moneda =
                new Moneda(
                        "USD",
                        "Dólar estadounidense",
                        2,
                        TipoMoneda.FIAT
                );

        MonedaRepository repository =
                new MonedaRepository(em);

        em.getTransaction().begin();

        repository.guardar(moneda);

        em.getTransaction().commit();

        Optional<Moneda> resultado =
                repository.buscarPorCodigo("USD");

        assertTrue(resultado.isPresent());

        assertEquals(
                "Dólar estadounidense",
                resultado.get().getNombre()
        );
    }

    @Test
    void deberiaListarTodasLasMonedas() {

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

        MonedaRepository repository =
                new MonedaRepository(em);

        em.getTransaction().begin();

        repository.guardar(ars);
        repository.guardar(usd);

        em.getTransaction().commit();

        List<Moneda> monedas =
                repository.listarTodas();

        assertEquals(2, monedas.size());

        assertEquals(
                "ARS",
                monedas.get(0).getCodigo()
        );

        assertEquals(
                "USD",
                monedas.get(1).getCodigo()
        );
    }

    @Test
    void deberiaActualizarMonedaExistente() {

        Moneda moneda =
                new Moneda(
                        "ARS",
                        "Peso argentino",
                        2,
                        TipoMoneda.FIAT
                );

        MonedaRepository repository =
                new MonedaRepository(em);

        em.getTransaction().begin();

        repository.guardar(moneda);

        em.getTransaction().commit();

        Long id = moneda.getId();

        em.clear();

        Moneda monedaModificada =
                repository.buscarPorId(id)
                        .orElseThrow();

        em.getTransaction().begin();

        monedaModificada.cambiarNombre(
                "Peso argentino actualizado"
        );

        monedaModificada.cambiarCantidadDecimales(2);

        repository.guardar(monedaModificada);

        em.getTransaction().commit();

        em.clear();

        Moneda monedaVerificada =
                repository.buscarPorId(id)
                        .orElseThrow();

        assertEquals(
                id,
                monedaVerificada.getId()
        );

        assertEquals(
                "Peso argentino actualizado",
                monedaVerificada.getNombre()
        );

        assertEquals(
                2,
                monedaVerificada.getCantidadDecimales()
        );
    }
}
