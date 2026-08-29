package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.PerfilFinancieroRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PerfilFinancieroServiceTest {

    private EntityManager entityManager;
    private PerfilFinancieroRepository perfilFinancieroRepository;
    private PerfilFinancieroService perfilFinancieroService;

    @BeforeEach
    void setUp() {

        entityManager = JpaTestManager.createEntityManager();

        perfilFinancieroRepository =
                new PerfilFinancieroRepository(entityManager);

        perfilFinancieroService =
                new PerfilFinancieroService(
                        perfilFinancieroRepository
                );
    }

    @AfterEach
    void tearDown() {

        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }

        JpaTestManager.close();
    }

    @Test
    void deberiaGuardarYBuscarPerfilPorId() {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Test",
                        "ariel.perfil." + System.nanoTime()
                                + "@example.com",
                        "hash"
                );

        PerfilFinanciero perfil =
                new PerfilFinanciero(
                        "Perfil principal",
                        usuario
                );

        entityManager.getTransaction().begin();

        entityManager.persist(usuario);

        perfilFinancieroService.guardar(perfil);

        entityManager.getTransaction().commit();

        entityManager.clear();

        Optional<PerfilFinanciero> resultado =
                perfilFinancieroService.buscarPorId(
                        perfil.getId()
                );

        assertTrue(resultado.isPresent());

        assertEquals(
                "Perfil principal",
                resultado.get().getNombre()
        );

        assertTrue(
                resultado.get().isActivo()
        );
    }

    @Test
    void deberiaListarTodosLosPerfiles() {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Test",
                        "ariel.lista." + System.nanoTime()
                                + "@example.com",
                        "hash"
                );

        PerfilFinanciero perfil1 =
                new PerfilFinanciero(
                        "Perfil principal",
                        usuario
                );

        PerfilFinanciero perfil2 =
                new PerfilFinanciero(
                        "Perfil secundario",
                        usuario
                );

        entityManager.getTransaction().begin();

        entityManager.persist(usuario);

        perfilFinancieroService.guardar(perfil1);
        perfilFinancieroService.guardar(perfil2);

        entityManager.getTransaction().commit();

        entityManager.clear();

        List<PerfilFinanciero> perfiles =
                perfilFinancieroService.listarTodos();

        assertEquals(
                2,
                perfiles.size()
        );

        assertEquals(
                "Perfil principal",
                perfiles.get(0).getNombre()
        );

        assertEquals(
                "Perfil secundario",
                perfiles.get(1).getNombre()
        );
    }

    @Test
    void deberiaListarPerfilesPorUsuario() {

        Usuario usuario1 =
                new Usuario(
                        "Ariel",
                        "Usuario Uno",
                        "ariel.usuario1." + System.nanoTime()
                                + "@example.com",
                        "hash"
                );

        Usuario usuario2 =
                new Usuario(
                        "Ariel",
                        "Usuario Dos",
                        "ariel.usuario2." + System.nanoTime()
                                + "@example.com",
                        "hash"
                );

        PerfilFinanciero perfilUsuario1 =
                new PerfilFinanciero(
                        "Perfil usuario uno",
                        usuario1
                );

        PerfilFinanciero perfilUsuario2 =
                new PerfilFinanciero(
                        "Perfil usuario dos",
                        usuario2
                );

        entityManager.getTransaction().begin();

        entityManager.persist(usuario1);
        entityManager.persist(usuario2);

        perfilFinancieroService.guardar(
                perfilUsuario1
        );

        perfilFinancieroService.guardar(
                perfilUsuario2
        );

        entityManager.getTransaction().commit();

        entityManager.clear();

        List<PerfilFinanciero> perfiles =
                perfilFinancieroService.listarPorUsuario(
                        usuario1.getId()
                );

        assertEquals(
                1,
                perfiles.size()
        );

        assertEquals(
                "Perfil usuario uno",
                perfiles.get(0).getNombre()
        );

        assertEquals(
                usuario1.getId(),
                perfiles.get(0)
                        .getUsuario()
                        .getId()
        );
    }

    @Test
    void deberiaCambiarDescripcion() {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Test",
                        "ariel.descripcion." + System.nanoTime()
                                + "@example.com",
                        "hash"
                );

        PerfilFinanciero perfil =
                new PerfilFinanciero(
                        "Perfil principal",
                        usuario
                );

        entityManager.getTransaction().begin();

        entityManager.persist(usuario);

        perfilFinancieroService.guardar(perfil);

        entityManager.getTransaction().commit();

        Long perfilId = perfil.getId();
        Long usuarioId = usuario.getId();

        entityManager.clear();

        entityManager.getTransaction().begin();

        perfilFinancieroService.cambiarDescripcion(
                perfilId,
                usuarioId,
                "Perfil destinado a las finanzas personales"
        );

        entityManager.getTransaction().commit();

        entityManager.clear();

        PerfilFinanciero verificado =
                perfilFinancieroService.buscarPorId(
                        perfilId
                ).orElseThrow();

        assertEquals(
                "Perfil destinado a las finanzas personales",
                verificado.getDescripcion()
        );
    }

    @Test
    void deberiaDesactivarPerfil() {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Test",
                        "ariel.desactivar." + System.nanoTime()
                                + "@example.com",
                        "hash"
                );

        PerfilFinanciero perfil =
                new PerfilFinanciero(
                        "Perfil principal",
                        usuario
                );

        entityManager.getTransaction().begin();

        entityManager.persist(usuario);

        perfilFinancieroService.guardar(perfil);

        entityManager.getTransaction().commit();

        Long perfilId = perfil.getId();
        Long usuarioId = usuario.getId();

        entityManager.clear();

        entityManager.getTransaction().begin();

        perfilFinancieroService.desactivar(
                perfilId,
                usuarioId
        );

        entityManager.getTransaction().commit();

        entityManager.clear();

        PerfilFinanciero verificado =
                perfilFinancieroService.buscarPorId(
                        perfilId
                ).orElseThrow();

        assertFalse(
                verificado.isActivo()
        );
    }

    @Test
    void deberiaActivarPerfil() {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Test",
                        "ariel.activar." + System.nanoTime()
                                + "@example.com",
                        "hash"
                );

        PerfilFinanciero perfil =
                new PerfilFinanciero(
                        "Perfil principal",
                        usuario
                );

        perfil.desactivar();

        entityManager.getTransaction().begin();

        entityManager.persist(usuario);

        perfilFinancieroService.guardar(perfil);

        entityManager.getTransaction().commit();

        Long perfilId = perfil.getId();
        Long usuarioId = usuario.getId();

        entityManager.clear();

        entityManager.getTransaction().begin();

        perfilFinancieroService.activar(
                perfilId,
                usuarioId
        );

        entityManager.getTransaction().commit();

        entityManager.clear();

        PerfilFinanciero verificado =
                perfilFinancieroService.buscarPorId(
                        perfilId
                ).orElseThrow();

        assertTrue(
                verificado.isActivo()
        );
    }

    @Test
    void debeRechazarCambioDeDescripcionSiElUsuarioNoEsPropietario() {

        Usuario propietario =
                new Usuario(
                        "Ariel",
                        "Propietario",
                        "ariel.propietario." + System.nanoTime()
                                + "@example.com",
                        "hash"
                );

        Usuario otroUsuario =
                new Usuario(
                        "Ariel",
                        "Otro Usuario",
                        "ariel.otro." + System.nanoTime()
                                + "@example.com",
                        "hash"
                );

        PerfilFinanciero perfil =
                new PerfilFinanciero(
                        "Perfil protegido",
                        propietario
                );

        entityManager.getTransaction().begin();

        entityManager.persist(propietario);
        entityManager.persist(otroUsuario);
        perfilFinancieroService.guardar(perfil);

        entityManager.getTransaction().commit();

        Long perfilId = perfil.getId();
        Long otroUsuarioId = otroUsuario.getId();

        entityManager.clear();

        entityManager.getTransaction().begin();

        assertThrows(
                IllegalArgumentException.class,
                () -> perfilFinancieroService.cambiarDescripcion(
                        perfilId,
                        otroUsuarioId,
                        "Intento no autorizado"
                )
        );

        entityManager.getTransaction().rollback();
    }

    @Test
    void debeRechazarActivacionSiElUsuarioNoEsPropietario() {

        Usuario propietario =
                new Usuario(
                        "Ariel",
                        "Propietario",
                        "ariel.propietario.activar." + System.nanoTime()
                                + "@example.com",
                        "hash"
                );

        Usuario otroUsuario =
                new Usuario(
                        "Ariel",
                        "Otro Usuario",
                        "ariel.otro.activar." + System.nanoTime()
                                + "@example.com",
                        "hash"
                );

        PerfilFinanciero perfil =
                new PerfilFinanciero(
                        "Perfil protegido",
                        propietario
                );

        perfil.desactivar();

        entityManager.getTransaction().begin();

        entityManager.persist(propietario);
        entityManager.persist(otroUsuario);
        perfilFinancieroService.guardar(perfil);

        entityManager.getTransaction().commit();

        Long perfilId = perfil.getId();
        Long otroUsuarioId = otroUsuario.getId();

        entityManager.clear();

        entityManager.getTransaction().begin();

        assertThrows(
                IllegalArgumentException.class,
                () -> perfilFinancieroService.activar(
                        perfilId,
                        otroUsuarioId
                )
        );

        entityManager.getTransaction().rollback();
    }

    @Test
    void debeRechazarDesactivacionSiElUsuarioNoEsPropietario() {

        Usuario propietario =
                new Usuario(
                        "Ariel",
                        "Propietario",
                        "ariel.propietario.desactivar." + System.nanoTime()
                                + "@example.com",
                        "hash"
                );

        Usuario otroUsuario =
                new Usuario(
                        "Ariel",
                        "Otro Usuario",
                        "ariel.otro.desactivar." + System.nanoTime()
                                + "@example.com",
                        "hash"
                );

        PerfilFinanciero perfil =
                new PerfilFinanciero(
                        "Perfil protegido",
                        propietario
                );

        entityManager.getTransaction().begin();

        entityManager.persist(propietario);
        entityManager.persist(otroUsuario);
        perfilFinancieroService.guardar(perfil);

        entityManager.getTransaction().commit();

        Long perfilId = perfil.getId();
        Long otroUsuarioId = otroUsuario.getId();

        entityManager.clear();

        entityManager.getTransaction().begin();

        assertThrows(
                IllegalArgumentException.class,
                () -> perfilFinancieroService.desactivar(
                        perfilId,
                        otroUsuarioId
                )
        );

        entityManager.getTransaction().rollback();
    }

    @Test
    void debeLanzarExcepcionAlGuardarPerfilNulo() {

        assertThrows(
                NullPointerException.class,
                () -> perfilFinancieroService.guardar(null)
        );
    }

    @Test
    void debeLanzarExcepcionAlBuscarPorIdNulo() {

        assertThrows(
                NullPointerException.class,
                () -> perfilFinancieroService.buscarPorId(null)
        );
    }

    @Test
    void debeDevolverVacioAlBuscarPorIdInexistente() {

        assertTrue(
                perfilFinancieroService.buscarPorId(999999L).isEmpty()
        );
    }

    @Test
    void debeLanzarExcepcionAlListarPorUsuarioConIdNulo() {

        assertThrows(
                NullPointerException.class,
                () -> perfilFinancieroService.listarPorUsuario(null)
        );
    }

    @Test
    void debeLanzarExcepcionAlCambiarDescripcionDePerfilInexistente() {

        assertThrows(
                IllegalArgumentException.class,
                () -> perfilFinancieroService.cambiarDescripcion(
                        999999L,
                        999999L,
                        "Descripción inexistente"
                )
        );
    }

    @Test
    void debeLanzarExcepcionAlActivarPerfilInexistente() {

        assertThrows(
                IllegalArgumentException.class,
                () -> perfilFinancieroService.activar(
                        999999L,
                        999999L
                )
        );
    }

    @Test
    void debeLanzarExcepcionAlDesactivarPerfilInexistente() {

        assertThrows(
                IllegalArgumentException.class,
                () -> perfilFinancieroService.desactivar(
                        999999L,
                        999999L
                )
        );
    }

    @Test
    void debeLanzarExcepcionAlCambiarDescripcionConUsuarioIdNulo() {

        assertThrows(
                NullPointerException.class,
                () -> perfilFinancieroService.cambiarDescripcion(
                        999999L,
                        null,
                        "Descripción"
                )
        );
    }

    @Test
    void debeLanzarExcepcionAlActivarConUsuarioIdNulo() {

        assertThrows(
                NullPointerException.class,
                () -> perfilFinancieroService.activar(
                        999999L,
                        null
                )
        );
    }

    @Test
    void debeLanzarExcepcionAlDesactivarConUsuarioIdNulo() {

        assertThrows(
                NullPointerException.class,
                () -> perfilFinancieroService.desactivar(
                        999999L,
                        null
                )
        );
    }
}