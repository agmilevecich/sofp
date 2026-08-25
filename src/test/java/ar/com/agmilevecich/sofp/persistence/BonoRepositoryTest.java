package ar.com.agmilevecich.sofp.persistence;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Bono;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BonoRepositoryTest {

    @Test
    void deberiaGuardarYBuscarBonoPorId() {

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

            Bono bono =
                    new Bono(
                            "Bono GD30",
                            moneda
                    );

            BonoRepository repository =
                    new BonoRepository(em);

            em.getTransaction().begin();

            em.persist(moneda);
            repository.guardar(bono);

            em.getTransaction().commit();

            Long id = bono.getId();

            em.clear();

            Optional<Bono> resultado =
                    repository.buscarPorId(id);

            assertTrue(resultado.isPresent());
            assertEquals(id, resultado.get().getId());
            assertEquals("Bono GD30", resultado.get().getNombre());
            assertEquals(
                    moneda.getId(),
                    resultado.get().getMoneda().getId()
            );

        } finally {
            em.close();
        }
    }

    @Test
    void deberiaRetornarOptionalVacioCuandoNoExisteBono() {

        JpaTestManager.close();

        EntityManager em =
                JpaTestManager.createEntityManager();

        try {
            BonoRepository repository =
                    new BonoRepository(em);

            Optional<Bono> resultado =
                    repository.buscarPorId(999999L);

            assertTrue(resultado.isEmpty());

        } finally {
            em.close();
        }
    }

    @Test
    void deberiaListarTodosLosBonos() {

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

            Bono primero =
                    new Bono("Bono GD30", moneda);

            Bono segundo =
                    new Bono("Bono AL30", moneda);

            BonoRepository repository =
                    new BonoRepository(em);

            em.getTransaction().begin();

            em.persist(moneda);
            repository.guardar(primero);
            repository.guardar(segundo);

            em.getTransaction().commit();

            List<Bono> bonos =
                    repository.listarTodas();

            assertEquals(2, bonos.size());
            assertEquals("Bono GD30", bonos.get(0).getNombre());
            assertEquals("Bono AL30", bonos.get(1).getNombre());

        } finally {
            em.close();
        }
    }

    @Test
    void deberiaActualizarBonoExistente() {

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

            Bono bono =
                    new Bono("Bono GD30", moneda);

            BonoRepository repository =
                    new BonoRepository(em);

            em.getTransaction().begin();

            em.persist(moneda);
            repository.guardar(bono);

            em.getTransaction().commit();

            Long id = bono.getId();

            em.clear();

            Bono existente =
                    repository.buscarPorId(id)
                            .orElseThrow();

            existente.cambiarNombre("Bono GD30 actualizado");

            em.getTransaction().begin();
            repository.guardar(existente);
            em.getTransaction().commit();

            em.clear();

            Bono actualizado =
                    repository.buscarPorId(id)
                            .orElseThrow();

            assertEquals(
                    "Bono GD30 actualizado",
                    actualizado.getNombre()
            );
            assertEquals(id, actualizado.getId());

        } finally {
            em.close();
        }
    }

    @Test
    void deberiaRechazarBonoNuloAlGuardar() {

        JpaTestManager.close();

        EntityManager em =
                JpaTestManager.createEntityManager();

        try {
            BonoRepository repository =
                    new BonoRepository(em);

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
            BonoRepository repository =
                    new BonoRepository(em);

            assertThrows(
                    NullPointerException.class,
                    () -> repository.buscarPorId(null)
            );

        } finally {
            em.close();
        }
    }
}
