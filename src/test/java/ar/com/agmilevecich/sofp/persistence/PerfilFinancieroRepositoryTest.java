package ar.com.agmilevecich.sofp.persistence;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.Usuario;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PerfilFinancieroRepositoryTest {

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
    void deberiaGuardarYBuscarPerfilPorId() {

            Usuario usuario = new Usuario(
                    "Ariel",
                    "Milevecich",
                    "perfil.id@test.com",
                    "hash123"
            );

            PerfilFinanciero perfil =
                    new PerfilFinanciero(
                            "Finanzas personales",
                            usuario
                    );

            usuario.agregarPerfilFinanciero(perfil);

            PerfilFinancieroRepository repository =
                    new PerfilFinancieroRepository(em);

            em.getTransaction().begin();

            em.persist(usuario);
            repository.guardar(perfil);

            em.getTransaction().commit();

            Optional<PerfilFinanciero> resultado =
                    repository.buscarPorId(perfil.getId());

            assertTrue(resultado.isPresent());

            assertEquals(
                    "Finanzas personales",
                    resultado.get().getNombre()
            );

            assertEquals(
                    usuario.getId(),
                    resultado.get().getUsuario().getId()
            );
    }

    @Test
    void deberiaListarTodosLosPerfiles() {
            Usuario usuario = new Usuario(
                    "Ariel",
                    "Milevecich",
                    "perfil.lista@test.com",
                    "hash123"
            );

            PerfilFinanciero perfil1 =
                    new PerfilFinanciero(
                            "Personal",
                            usuario
                    );

            PerfilFinanciero perfil2 =
                    new PerfilFinanciero(
                            "Viaje Rio 2027",
                            usuario
                    );

            usuario.agregarPerfilFinanciero(perfil1);
            usuario.agregarPerfilFinanciero(perfil2);

            PerfilFinancieroRepository repository =
                    new PerfilFinancieroRepository(em);

            em.getTransaction().begin();

            em.persist(usuario);
            repository.guardar(perfil1);
            repository.guardar(perfil2);

            em.getTransaction().commit();

            List<PerfilFinanciero> perfiles =
                    repository.listarTodos();

            assertEquals(2, perfiles.size());

            assertEquals(
                    "Personal",
                    perfiles.get(0).getNombre()
            );

            assertEquals(
                    "Viaje Rio 2027",
                    perfiles.get(1).getNombre()
            );
    }

    @Test
    void deberiaListarPerfilesDeUnUsuario() {

            Usuario usuario1 = new Usuario(
                    "Ariel",
                    "Milevecich",
                    "usuario1.perfil@test.com",
                    "hash123"
            );

            Usuario usuario2 = new Usuario(
                    "Juan",
                    "Perez",
                    "usuario2.perfil@test.com",
                    "hash456"
            );

            PerfilFinanciero perfilAriel =
                    new PerfilFinanciero(
                            "Finanzas personales",
                            usuario1
                    );

            PerfilFinanciero perfilViaje =
                    new PerfilFinanciero(
                            "Viaje Rio 2027",
                            usuario1
                    );

            PerfilFinanciero perfilJuan =
                    new PerfilFinanciero(
                            "Finanzas Juan",
                            usuario2
                    );

            usuario1.agregarPerfilFinanciero(perfilAriel);
            usuario1.agregarPerfilFinanciero(perfilViaje);
            usuario2.agregarPerfilFinanciero(perfilJuan);

            PerfilFinancieroRepository repository =
                    new PerfilFinancieroRepository(em);

            em.getTransaction().begin();

            em.persist(usuario1);
            em.persist(usuario2);

            repository.guardar(perfilAriel);
            repository.guardar(perfilViaje);
            repository.guardar(perfilJuan);

            em.getTransaction().commit();

            List<PerfilFinanciero> perfiles =
                    repository.listarPorUsuario(
                            usuario1.getId()
                    );

            assertEquals(2, perfiles.size());

            assertEquals(
                    "Finanzas personales",
                    perfiles.get(0).getNombre()
            );

            assertEquals(
                    "Viaje Rio 2027",
                    perfiles.get(1).getNombre()
            );

            assertTrue(
                    perfiles.stream()
                            .allMatch(
                                    perfil ->
                                            perfil.getUsuario()
                                                    .getId()
                                                    .equals(usuario1.getId())
                            )
            );
    }

    @Test
    void deberiaActualizarPerfilExistente() {

            Usuario usuario = new Usuario(
                    "Ariel",
                    "Milevecich",
                    "perfil.update@test.com",
                    "hash123"
            );

            PerfilFinanciero perfil =
                    new PerfilFinanciero(
                            "Viaje Rio 2027",
                            usuario
                    );

            usuario.agregarPerfilFinanciero(perfil);

            PerfilFinancieroRepository repository =
                    new PerfilFinancieroRepository(em);

            em.getTransaction().begin();

            em.persist(usuario);
            repository.guardar(perfil);

            em.getTransaction().commit();

            Long id = perfil.getId();

            em.clear();

            PerfilFinanciero perfilModificado =
                    repository.buscarPorId(id)
                            .orElseThrow();

            em.getTransaction().begin();

            perfilModificado.cambiarDescripcion(
                    "Fondo destinado al viaje"
            );

            PerfilFinanciero resultado =
                    repository.guardar(perfilModificado);

            em.getTransaction().commit();

            em.clear();

            PerfilFinanciero perfilVerificado =
                    repository.buscarPorId(id)
                            .orElseThrow();

            assertEquals(
                    id,
                    perfilVerificado.getId()
            );

            assertEquals(
                    "Fondo destinado al viaje",
                    perfilVerificado.getDescripcion()
            );
    }
}