package ar.com.agmilevecich.sofp.config;

import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.InstitucionFinancieraRepository;
import ar.com.agmilevecich.sofp.persistence.MonedaRepository;
import ar.com.agmilevecich.sofp.persistence.PerfilFinancieroRepository;
import ar.com.agmilevecich.sofp.persistence.UsuarioRepository;
import ar.com.agmilevecich.sofp.service.PasswordService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DatosInicialesDesarrolloTest {

    private EntityManager entityManager;
    private UsuarioRepository usuarioRepository;
    private PerfilFinancieroRepository perfilFinancieroRepository;
    private InstitucionFinancieraRepository institucionRepository;
    private MonedaRepository monedaRepository;

    @BeforeEach
    void setUp() {

        JpaTestManager.close();

        entityManager =
                JpaTestManager.createEntityManager();

        usuarioRepository =
                new UsuarioRepository(entityManager);

        perfilFinancieroRepository =
                new PerfilFinancieroRepository(entityManager);

        institucionRepository =
                new InstitucionFinancieraRepository(entityManager);

        monedaRepository =
                new MonedaRepository(entityManager);
    }

    @AfterEach
    void tearDown() {

        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }

        JpaTestManager.close();
    }

    @Test
    void deberiaCrearUsuarioDemoAunqueExistaOtroUsuario() {

        Usuario usuarioExistente =
                new Usuario(
                        "Usuario",
                        "Existente",
                        "usuario.existente." + System.nanoTime() + "@test.com",
                        PasswordService.hash("password")
                );

        entityManager.getTransaction().begin();
        usuarioRepository.guardar(usuarioExistente);
        entityManager.getTransaction().commit();

        DatosInicialesDesarrollo.crearSiNoExisten(entityManager);

        Optional<Usuario> usuarioDemo =
                usuarioRepository.buscarPorEmail(
                        DatosInicialesDesarrollo.EMAIL
                );

        assertTrue(usuarioDemo.isPresent());
        assertNotEquals(usuarioExistente.getId(), usuarioDemo.get().getId());
        assertEquals("Usuario", usuarioDemo.get().getNombre());
        assertEquals("Demo", usuarioDemo.get().getApellido());
        assertTrue(usuarioDemo.get().isActivo());
        assertTrue(
                PasswordService.matches(
                        DatosInicialesDesarrollo.PASSWORD,
                        usuarioDemo.get().getPasswordHash()
                )
        );

        List<PerfilFinanciero> perfiles =
                perfilFinancieroRepository.listarPorUsuario(
                        usuarioDemo.get().getId()
                );

        assertEquals(1, perfiles.size());
        assertEquals("Perfil de desarrollo", perfiles.get(0).getNombre());

        assertTrue(
                institucionRepository.buscarPorNombre(
                        "Institución de desarrollo"
                ).isPresent()
        );
        assertTrue(monedaRepository.buscarPorCodigo("ARS").isPresent());
        assertTrue(monedaRepository.buscarPorCodigo("USD").isPresent());
    }

    @Test
    void noDeberiaDuplicarUsuarioDemoNiPerfilNiDatosDeReferencia() {

        DatosInicialesDesarrollo.crearSiNoExisten(entityManager);

        Usuario usuarioDemo =
                usuarioRepository.buscarPorEmail(
                        DatosInicialesDesarrollo.EMAIL
                ).orElseThrow();

        Long usuarioDemoId = usuarioDemo.getId();

        DatosInicialesDesarrollo.crearSiNoExisten(entityManager);

        List<Usuario> usuariosDemo =
                usuarioRepository.listarTodos()
                        .stream()
                        .filter(usuario ->
                                DatosInicialesDesarrollo.EMAIL.equals(
                                        usuario.getEmail()
                                )
                        )
                        .toList();

        assertEquals(1, usuariosDemo.size());
        assertEquals(usuarioDemoId, usuariosDemo.get(0).getId());

        List<PerfilFinanciero> perfiles =
                perfilFinancieroRepository.listarPorUsuario(usuarioDemoId);

        assertEquals(1, perfiles.size());
        assertEquals("Perfil de desarrollo", perfiles.get(0).getNombre());

        assertEquals(
                1,
                institucionRepository.listarTodas()
                        .stream()
                        .filter(institucion ->
                                "Institución de desarrollo".equals(
                                        institucion.getNombre()
                                )
                        )
                        .count()
        );
        assertEquals(
                1,
                monedaRepository.listarTodas()
                        .stream()
                        .filter(moneda -> "ARS".equals(moneda.getCodigo()))
                        .count()
        );
        assertEquals(
                1,
                monedaRepository.listarTodas()
                        .stream()
                        .filter(moneda -> "USD".equals(moneda.getCodigo()))
                        .count()
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoEntityManagerEsNulo() {

        assertThrows(
                NullPointerException.class,
                () -> DatosInicialesDesarrollo.crearSiNoExisten(null)
        );
    }
}
