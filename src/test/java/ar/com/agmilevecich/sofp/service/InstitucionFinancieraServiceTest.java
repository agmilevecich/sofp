package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.persistence.InstitucionFinancieraRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InstitucionFinancieraServiceTest {

    private EntityManager entityManager;
    private InstitucionFinancieraService service;

    @BeforeEach
    void setUp() {

        entityManager =
                JpaTestManager.createEntityManager();

        InstitucionFinancieraRepository repository =
                new InstitucionFinancieraRepository(
                        entityManager
                );

        service =
                new InstitucionFinancieraService(
                        repository
                );
    }

    @AfterEach
    void tearDown() {

        if (entityManager != null
                && entityManager.isOpen()) {

            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }

            entityManager.close();
        }

        JpaTestManager.close();
    }

    @Test
    void debeGuardarYBuscarInstitucionPorId() {

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Banco Santander",
                        TipoInstitucionFinanciera.BANCO
                );

        entityManager.getTransaction().begin();

        service.guardar(institucion);

        entityManager.getTransaction().commit();

        assertNotNull(institucion.getId());

        entityManager.clear();

        InstitucionFinanciera encontrada =
                service.buscarPorId(institucion.getId())
                        .orElseThrow();

        assertEquals(
                "Banco Santander",
                encontrada.getNombre()
        );

        assertEquals(
                TipoInstitucionFinanciera.BANCO,
                encontrada.getTipo()
        );
    }

    @Test
    void debeBuscarInstitucionPorNombre() {

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Banco Galicia",
                        TipoInstitucionFinanciera.BANCO
                );

        entityManager.getTransaction().begin();

        service.guardar(institucion);

        entityManager.getTransaction().commit();

        entityManager.clear();

        InstitucionFinanciera encontrada =
                service.buscarPorNombre(
                        "Banco Galicia"
                ).orElseThrow();

        assertEquals(
                institucion.getId(),
                encontrada.getId()
        );
    }

    @Test
    void debeListarTodasLasInstituciones() {

        InstitucionFinanciera primera =
                new InstitucionFinanciera(
                        "Banco Nación",
                        TipoInstitucionFinanciera.BANCO
                );

        InstitucionFinanciera segunda =
                new InstitucionFinanciera(
                        "Banco Macro",
                        TipoInstitucionFinanciera.BANCO
                );

        entityManager.getTransaction().begin();

        service.guardar(primera);
        service.guardar(segunda);

        entityManager.getTransaction().commit();

        List<InstitucionFinanciera> instituciones =
                service.listarTodas();

        assertEquals(
                2,
                instituciones.size()
        );

        assertTrue(
                instituciones.stream()
                        .anyMatch(
                                i -> i.getId().equals(
                                        primera.getId()
                                )
                        )
        );

        assertTrue(
                instituciones.stream()
                        .anyMatch(
                                i -> i.getId().equals(
                                        segunda.getId()
                                )
                        )
        );
    }

    @Test
    void debeRenombrarInstitucion() {

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Nombre Original",
                        TipoInstitucionFinanciera.BANCO
                );

        entityManager.getTransaction().begin();

        service.guardar(institucion);

        entityManager.getTransaction().commit();

        entityManager.clear();

        entityManager.getTransaction().begin();

        service.renombrar(
                institucion.getId(),
                "Nuevo Nombre"
        );

        entityManager.getTransaction().commit();

        entityManager.clear();

        InstitucionFinanciera actualizada =
                service.buscarPorId(
                        institucion.getId()
                ).orElseThrow();

        assertEquals(
                "Nuevo Nombre",
                actualizada.getNombre()
        );
    }

    @Test
    void debeActualizarSitioWeb() {

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Banco Provincia",
                        TipoInstitucionFinanciera.BANCO
                );

        entityManager.getTransaction().begin();

        service.guardar(institucion);

        entityManager.getTransaction().commit();

        entityManager.clear();

        entityManager.getTransaction().begin();

        service.actualizarSitioWeb(
                institucion.getId(),
                "https://www.bancoprovincia.com.ar"
        );

        entityManager.getTransaction().commit();

        entityManager.clear();

        InstitucionFinanciera actualizada =
                service.buscarPorId(
                        institucion.getId()
                ).orElseThrow();

        assertEquals(
                "https://www.bancoprovincia.com.ar",
                actualizada.getSitioWeb()
        );
    }

    @Test
    void debeActualizarDescripcion() {

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Banco BBVA",
                        TipoInstitucionFinanciera.BANCO
                );

        entityManager.getTransaction().begin();

        service.guardar(institucion);

        entityManager.getTransaction().commit();

        entityManager.clear();

        entityManager.getTransaction().begin();

        service.actualizarDescripcion(
                institucion.getId(),
                "Institución financiera de prueba"
        );

        entityManager.getTransaction().commit();

        entityManager.clear();

        InstitucionFinanciera actualizada =
                service.buscarPorId(
                        institucion.getId()
                ).orElseThrow();

        assertEquals(
                "Institución financiera de prueba",
                actualizada.getDescripcion()
        );
    }

    @Test
    void debeDesactivarInstitucion() {

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Banco HSBC",
                        TipoInstitucionFinanciera.BANCO
                );

        entityManager.getTransaction().begin();

        service.guardar(institucion);

        entityManager.getTransaction().commit();

        entityManager.clear();

        entityManager.getTransaction().begin();

        service.desactivar(
                institucion.getId()
        );

        entityManager.getTransaction().commit();

        entityManager.clear();

        InstitucionFinanciera actualizada =
                service.buscarPorId(
                        institucion.getId()
                ).orElseThrow();

        assertFalse(
                actualizada.isActiva()
        );
    }

    @Test
    void debeActivarInstitucion() {

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Banco ICBC",
                        TipoInstitucionFinanciera.BANCO
                );

        entityManager.getTransaction().begin();

        service.guardar(institucion);

        entityManager.getTransaction().commit();

        entityManager.clear();

        entityManager.getTransaction().begin();

        service.desactivar(
                institucion.getId()
        );

        entityManager.getTransaction().commit();

        entityManager.clear();

        entityManager.getTransaction().begin();

        service.activar(
                institucion.getId()
        );

        entityManager.getTransaction().commit();

        entityManager.clear();

        InstitucionFinanciera actualizada =
                service.buscarPorId(
                        institucion.getId()
                ).orElseThrow();

        assertTrue(
                actualizada.isActiva()
        );
    }

    @Test
    void debeLanzarExcepcionAlGuardarInstitucionNula() {

        assertThrows(
                NullPointerException.class,
                () -> service.guardar(null)
        );
    }

    @Test
    void debeLanzarExcepcionAlBuscarPorIdNulo() {

        assertThrows(
                NullPointerException.class,
                () -> service.buscarPorId(null)
        );
    }

    @Test
    void debeDevolverVacioAlBuscarPorIdInexistente() {

        assertTrue(
                service.buscarPorId(999999L).isEmpty()
        );
    }

    @Test
    void debeLanzarExcepcionAlBuscarPorNombreNulo() {

        assertThrows(
                NullPointerException.class,
                () -> service.buscarPorNombre(null)
        );
    }

    @Test
    void debeDevolverVacioAlBuscarPorNombreInexistente() {

        assertTrue(
                service.buscarPorNombre(
                        "Institución inexistente"
                ).isEmpty()
        );
    }

    @Test
    void debeLanzarExcepcionAlRenombrarConIdNulo() {

        assertThrows(
                NullPointerException.class,
                () -> service.renombrar(
                        null,
                        "Nuevo Nombre"
                )
        );
    }

    @Test
    void debeLanzarExcepcionAlRenombrarConNombreNulo() {

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Banco Test Nombre Nulo",
                        TipoInstitucionFinanciera.BANCO
                );

        entityManager.getTransaction().begin();

        service.guardar(institucion);

        entityManager.getTransaction().commit();

        entityManager.clear();

        assertThrows(
                NullPointerException.class,
                () -> service.renombrar(
                        institucion.getId(),
                        null
                )
        );
    }

    @Test
    void debeLanzarExcepcionAlRenombrarInstitucionInexistente() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.renombrar(
                        999999L,
                        "Nuevo Nombre"
                )
        );
    }

    @Test
    void debeLanzarExcepcionAlActualizarSitioWebConIdNulo() {

        assertThrows(
                NullPointerException.class,
                () -> service.actualizarSitioWeb(
                        null,
                        "https://www.test.com"
                )
        );
    }


    @Test
    void debeLanzarExcepcionAlActualizarSitioWebDeInstitucionInexistente() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.actualizarSitioWeb(
                        999999L,
                        "https://www.test.com"
                )
        );
    }

    @Test
    void debeLanzarExcepcionAlActualizarDescripcionConIdNulo() {

        assertThrows(
                NullPointerException.class,
                () -> service.actualizarDescripcion(
                        null,
                        "Descripción de prueba"
                )
        );
    }

    @Test
    void debeLanzarExcepcionAlActualizarDescripcionDeInstitucionInexistente() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.actualizarDescripcion(
                        999999L,
                        "Descripción de prueba"
                )
        );
    }

    @Test
    void debeLanzarExcepcionAlActivarConIdNulo() {

        assertThrows(
                NullPointerException.class,
                () -> service.activar(null)
        );
    }

    @Test
    void debeLanzarExcepcionAlActivarInstitucionInexistente() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.activar(999999L)
        );
    }

    @Test
    void debeLanzarExcepcionAlDesactivarConIdNulo() {

        assertThrows(
                NullPointerException.class,
                () -> service.desactivar(null)
        );
    }

    @Test
    void debeLanzarExcepcionAlDesactivarInstitucionInexistente() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.desactivar(999999L)
        );
    }

    @Test
    void debeLanzarExcepcionAlActualizarSitioWebConSitioWebNulo() {

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Banco Test Sitio Web Nulo",
                        TipoInstitucionFinanciera.BANCO
                );

        entityManager.getTransaction().begin();

        service.guardar(institucion);

        entityManager.getTransaction().commit();

        entityManager.clear();

        assertThrows(
                NullPointerException.class,
                () -> service.actualizarSitioWeb(
                        institucion.getId(),
                        null
                )
        );
    }

    @Test
    void debeLanzarExcepcionAlActualizarDescripcionConDescripcionNula() {

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Banco Test Descripcion Nula",
                        TipoInstitucionFinanciera.BANCO
                );

        entityManager.getTransaction().begin();

        service.guardar(institucion);

        entityManager.getTransaction().commit();

        entityManager.clear();

        assertThrows(
                NullPointerException.class,
                () -> service.actualizarDescripcion(
                        institucion.getId(),
                        null
                )
        );
    }
}