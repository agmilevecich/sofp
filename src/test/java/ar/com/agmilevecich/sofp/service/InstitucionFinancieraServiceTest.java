package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.persistence.InstitucionFinancieraRepository;
import ar.com.agmilevecich.sofp.config.JpaTestManager;
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
        entityManager = JpaTestManager.createEntityManager();

        InstitucionFinancieraRepository repository =
                new InstitucionFinancieraRepository(entityManager);

        service = new InstitucionFinancieraService(repository);
    }

    @AfterEach
    void tearDown() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
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
                service.buscarPorNombre("Banco Galicia")
                        .orElseThrow();

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

        assertTrue(
                instituciones.stream()
                        .anyMatch(i -> i.getId().equals(primera.getId()))
        );

        assertTrue(
                instituciones.stream()
                        .anyMatch(i -> i.getId().equals(segunda.getId()))
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
                service.buscarPorId(institucion.getId())
                        .orElseThrow();

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
                service.buscarPorId(institucion.getId())
                        .orElseThrow();

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
                service.buscarPorId(institucion.getId())
                        .orElseThrow();

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

        service.desactivar(institucion.getId());

        entityManager.getTransaction().commit();

        entityManager.clear();

        InstitucionFinanciera actualizada =
                service.buscarPorId(institucion.getId())
                        .orElseThrow();

        assertFalse(actualizada.isActiva());
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

        service.desactivar(institucion.getId());

        entityManager.getTransaction().commit();

        entityManager.clear();

        entityManager.getTransaction().begin();

        service.activar(institucion.getId());

        entityManager.getTransaction().commit();

        entityManager.clear();

        InstitucionFinanciera actualizada =
                service.buscarPorId(institucion.getId())
                        .orElseThrow();

        assertTrue(actualizada.isActiva());
    }
}
