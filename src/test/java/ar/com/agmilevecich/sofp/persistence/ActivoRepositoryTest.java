package ar.com.agmilevecich.sofp.persistence;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Activo;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ActivoRepositoryTest {

    @Test
    void deberiaGuardarYBuscarActivoPorId() {

        JpaTestManager.close();

        EntityManager em =
                JpaTestManager.createEntityManager();

        try {
            Moneda moneda =
                    new Moneda(
                            "ARS",
                            "Peso Argentino",
                            2,
                            TipoMoneda.FIAT
                    );

            Activo activo =
                    new Activo(
                            "Bitcoin",
                            "BTC",
                            moneda
                    );

            ActivoRepository repository =
                    new ActivoRepository(em);

            em.getTransaction().begin();

            em.persist(moneda);
            repository.guardar(activo);

            em.getTransaction().commit();

            Long id = activo.getId();

            em.clear();

            Optional<Activo> resultado =
                    repository.buscarPorId(id);

            assertTrue(resultado.isPresent());
            assertEquals(id, resultado.get().getId());
            assertEquals("Bitcoin", resultado.get().getNombre());
            assertEquals("BTC", resultado.get().getSimbolo());
            assertEquals(
                    moneda.getId(),
                    resultado.get().getMoneda().getId()
            );

        } finally {
            em.close();
        }
    }

    @Test
    void deberiaBuscarActivoPorSimbolo() {

        JpaTestManager.close();

        EntityManager em =
                JpaTestManager.createEntityManager();

        try {
            Moneda moneda =
                    new Moneda(
                            "ARS",
                            "Peso Argentino",
                            2,
                            TipoMoneda.FIAT
                    );

            Activo activo =
                    new Activo(
                            "Bitcoin",
                            "BTC",
                            moneda
                    );

            ActivoRepository repository =
                    new ActivoRepository(em);

            em.getTransaction().begin();

            em.persist(moneda);
            repository.guardar(activo);

            em.getTransaction().commit();

            Optional<Activo> resultado =
                    repository.buscarPorSimbolo("BTC");

            assertTrue(resultado.isPresent());
            assertEquals("Bitcoin", resultado.get().getNombre());
            assertEquals("BTC", resultado.get().getSimbolo());

        } finally {
            em.close();
        }
    }

    @Test
    void deberiaRetornarOptionalVacioCuandoNoExisteActivo() {

        JpaTestManager.close();

        EntityManager em =
                JpaTestManager.createEntityManager();

        try {
            ActivoRepository repository =
                    new ActivoRepository(em);

            Optional<Activo> resultado =
                    repository.buscarPorId(999999L);

            assertTrue(resultado.isEmpty());

        } finally {
            em.close();
        }
    }

    @Test
    void deberiaRetornarOptionalVacioCuandoNoExisteActivoPorSimbolo() {

        JpaTestManager.close();

        EntityManager em =
                JpaTestManager.createEntityManager();

        try {
            ActivoRepository repository =
                    new ActivoRepository(em);

            Optional<Activo> resultado =
                    repository.buscarPorSimbolo("NOEXISTE");

            assertTrue(resultado.isEmpty());

        } finally {
            em.close();
        }
    }

    @Test
    void deberiaListarTodosLosActivos() {

        JpaTestManager.close();

        EntityManager em =
                JpaTestManager.createEntityManager();

        try {
            Moneda moneda =
                    new Moneda(
                            "ARS",
                            "Peso Argentino",
                            2,
                            TipoMoneda.FIAT
                    );

            Activo primero =
                    new Activo("Bitcoin", "BTC", moneda);

            Activo segundo =
                    new Activo("Ethereum", "ETH", moneda);

            ActivoRepository repository =
                    new ActivoRepository(em);

            em.getTransaction().begin();

            em.persist(moneda);
            repository.guardar(primero);
            repository.guardar(segundo);

            em.getTransaction().commit();

            List<Activo> activos =
                    repository.listarTodas();

            assertEquals(2, activos.size());
            assertEquals("Bitcoin", activos.get(0).getNombre());
            assertEquals("BTC", activos.get(0).getSimbolo());
            assertEquals("Ethereum", activos.get(1).getNombre());
            assertEquals("ETH", activos.get(1).getSimbolo());

        } finally {
            em.close();
        }
    }

    @Test
    void deberiaActualizarActivoExistente() {

        JpaTestManager.close();

        EntityManager em =
                JpaTestManager.createEntityManager();

        try {
            Moneda moneda =
                    new Moneda(
                            "ARS",
                            "Peso Argentino",
                            2,
                            TipoMoneda.FIAT
                    );

            Activo activo =
                    new Activo("Bitcoin", "BTC", moneda);

            ActivoRepository repository =
                    new ActivoRepository(em);

            em.getTransaction().begin();

            em.persist(moneda);
            repository.guardar(activo);

            em.getTransaction().commit();

            Long id = activo.getId();

            em.clear();

            Activo existente =
                    repository.buscarPorId(id)
                            .orElseThrow();

            existente.cambiarNombre("Bitcoin actualizado");

            em.getTransaction().begin();
            repository.guardar(existente);
            em.getTransaction().commit();

            em.clear();

            Activo actualizado =
                    repository.buscarPorId(id)
                            .orElseThrow();

            assertEquals(
                    "Bitcoin actualizado",
                    actualizado.getNombre()
            );
            assertEquals("BTC", actualizado.getSimbolo());
            assertEquals(id, actualizado.getId());

        } finally {
            em.close();
        }
    }

    @Test
    void deberiaRechazarActivoNuloAlGuardar() {

        JpaTestManager.close();

        EntityManager em =
                JpaTestManager.createEntityManager();

        try {
            ActivoRepository repository =
                    new ActivoRepository(em);

            assertThrows(
                    NullPointerException.class,
                    () -> repository.guardar(null)
            );

        } finally {
            em.close();
        }
    }

    @Test
    void deberiaRechazarIdNuloAlBuscar() {

        JpaTestManager.close();

        EntityManager em =
                JpaTestManager.createEntityManager();

        try {
            ActivoRepository repository =
                    new ActivoRepository(em);

            assertThrows(
                    NullPointerException.class,
                    () -> repository.buscarPorId(null)
            );

        } finally {
            em.close();
        }
    }

    @Test
    void deberiaRechazarSimboloNuloAlBuscar() {

        JpaTestManager.close();

        EntityManager em =
                JpaTestManager.createEntityManager();

        try {
            ActivoRepository repository =
                    new ActivoRepository(em);

            assertThrows(
                    NullPointerException.class,
                    () -> repository.buscarPorSimbolo(null)
            );

        } finally {
            em.close();
        }
    }
}
