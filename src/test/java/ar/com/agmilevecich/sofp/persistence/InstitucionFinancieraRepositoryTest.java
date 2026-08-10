package ar.com.agmilevecich.sofp.persistence;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InstitucionFinancieraRepositoryTest {

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
    void deberiaGuardarYBuscarInstitucionPorId() {

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Banco Santander",
                        TipoInstitucionFinanciera.BANCO
                );

        InstitucionFinancieraRepository repository =
                new InstitucionFinancieraRepository(em);

        em.getTransaction().begin();

        repository.guardar(institucion);

        em.getTransaction().commit();

        Optional<InstitucionFinanciera> resultado =
                repository.buscarPorId(institucion.getId());

        assertTrue(resultado.isPresent());

        assertEquals(
                "Banco Santander",
                resultado.get().getNombre()
        );

        assertEquals(
                TipoInstitucionFinanciera.BANCO,
                resultado.get().getTipo()
        );

        assertTrue(resultado.get().isActiva());
    }

    @Test
    void deberiaBuscarInstitucionPorNombre() {

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Banco Santander",
                        TipoInstitucionFinanciera.BANCO
                );

        InstitucionFinancieraRepository repository =
                new InstitucionFinancieraRepository(em);

        em.getTransaction().begin();

        repository.guardar(institucion);

        em.getTransaction().commit();

        Optional<InstitucionFinanciera> resultado =
                repository.buscarPorNombre(
                        "Banco Santander"
                );

        assertTrue(resultado.isPresent());

        assertEquals(
                institucion.getId(),
                resultado.get().getId()
        );
    }

    @Test
    void deberiaListarTodasLasInstituciones() {

        InstitucionFinanciera banco =
                new InstitucionFinanciera(
                        "Banco Santander",
                        TipoInstitucionFinanciera.BANCO
                );

        InstitucionFinanciera broker =
                new InstitucionFinanciera(
                        "Broker Demo",
                        TipoInstitucionFinanciera.BROKER
                );

        InstitucionFinancieraRepository repository =
                new InstitucionFinancieraRepository(em);

        em.getTransaction().begin();

        repository.guardar(banco);
        repository.guardar(broker);

        em.getTransaction().commit();

        List<InstitucionFinanciera> instituciones =
                repository.listarTodas();

        assertEquals(
                2,
                instituciones.size()
        );

        assertEquals(
                "Banco Santander",
                instituciones.get(0).getNombre()
        );

        assertEquals(
                "Broker Demo",
                instituciones.get(1).getNombre()
        );
    }

    @Test
    void deberiaActualizarInstitucionExistente() {

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Banco Santander",
                        TipoInstitucionFinanciera.BANCO
                );

        InstitucionFinancieraRepository repository =
                new InstitucionFinancieraRepository(em);

        em.getTransaction().begin();

        repository.guardar(institucion);

        em.getTransaction().commit();

        Long id = institucion.getId();

        em.clear();

        InstitucionFinanciera institucionModificada =
                repository.buscarPorId(id)
                        .orElseThrow();

        em.getTransaction().begin();

        institucionModificada.renombrar(
                "Banco Santander Argentina"
        );

        institucionModificada.actualizarSitioWeb(
                "https://www.santander.com.ar"
        );

        institucionModificada.actualizarDescripcion(
                "Institución financiera actualizada"
        );

        repository.guardar(institucionModificada);

        em.getTransaction().commit();

        em.clear();

        InstitucionFinanciera verificada =
                repository.buscarPorId(id)
                        .orElseThrow();

        assertEquals(
                id,
                verificada.getId()
        );

        assertEquals(
                "Banco Santander Argentina",
                verificada.getNombre()
        );

        assertEquals(
                "https://www.santander.com.ar",
                verificada.getSitioWeb()
        );

        assertEquals(
                "Institución financiera actualizada",
                verificada.getDescripcion()
        );
    }
}
