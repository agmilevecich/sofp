package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.InstitucionFinancieraRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InstitucionFinancieraMultiUsuarioTest {

    private EntityManager entityManager;
    private InstitucionFinancieraService service;
    private Usuario usuarioA;
    private Usuario usuarioB;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();
        service = new InstitucionFinancieraService(
                new InstitucionFinancieraRepository(entityManager),
                entityManager
        );

        usuarioA = new Usuario("Usuario", "A", "instituciones.a." + System.nanoTime() + "@test.local", "hash");
        usuarioB = new Usuario("Usuario", "B", "instituciones.b." + System.nanoTime() + "@test.local", "hash");

        entityManager.getTransaction().begin();
        entityManager.persist(usuarioA);
        entityManager.persist(usuarioB);
        entityManager.getTransaction().commit();
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
    void cadaUsuarioDebeVerSoloSusInstituciones() {
        InstitucionFinanciera institucionA =
                service.registrar(new InstitucionFinanciera("Banco A", TipoInstitucionFinanciera.BANCO), usuarioA.getId());
        InstitucionFinanciera institucionB =
                service.registrar(new InstitucionFinanciera("Banco B", TipoInstitucionFinanciera.FINTECH), usuarioB.getId());

        assertEquals(1, service.listarPorUsuario(usuarioA.getId()).size());
        assertEquals("Banco A", service.listarPorUsuario(usuarioA.getId()).get(0).getNombre());

        assertEquals(1, service.listarPorUsuario(usuarioB.getId()).size());
        assertEquals("Banco B", service.listarPorUsuario(usuarioB.getId()).get(0).getNombre());

        assertTrue(service.buscarPorId(institucionA.getId(), usuarioB.getId()).isEmpty());
        assertTrue(service.buscarPorId(institucionB.getId(), usuarioA.getId()).isEmpty());
        assertTrue(service.buscarPorNombre("Banco A", usuarioB.getId()).isEmpty());
        assertTrue(service.buscarPorNombre("Banco B", usuarioA.getId()).isEmpty());
    }

    @Test
    void unUsuarioNoDebePoderModificarInstitucionDeOtro() {
        InstitucionFinanciera institucionA =
                service.registrar(new InstitucionFinanciera("Banco Privado A", TipoInstitucionFinanciera.BANCO), usuarioA.getId());

        assertThrows(
                IllegalArgumentException.class,
                () -> service.renombrar(institucionA.getId(), usuarioB.getId(), "Banco Modificado")
        );

        assertEquals(
                "Banco Privado A",
                service.buscarPorId(institucionA.getId()).orElseThrow().getNombre()
        );
    }

    @Test
    void registrarDebeRechazarUsuarioInexistente() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrar(
                        new InstitucionFinanciera("Banco Inexistente", TipoInstitucionFinanciera.BANCO),
                        999999L
                )
        );
    }

    @Test
    void listarPorUsuarioDebeRechazarUsuarioNulo() {
        assertThrows(
                NullPointerException.class,
                () -> service.listarPorUsuario(null)
        );
    }
}
