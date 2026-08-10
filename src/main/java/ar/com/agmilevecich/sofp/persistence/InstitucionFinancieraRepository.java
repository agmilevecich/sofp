package ar.com.agmilevecich.sofp.persistence;

import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class InstitucionFinancieraRepository {

    private final EntityManager entityManager;

    public InstitucionFinancieraRepository(EntityManager entityManager) {
        this.entityManager =
                Objects.requireNonNull(
                        entityManager,
                        "El EntityManager es obligatorio"
                );
    }

    public InstitucionFinanciera guardar(
            InstitucionFinanciera institucion) {

        Objects.requireNonNull(
                institucion,
                "La institución financiera es obligatoria"
        );

        if (institucion.getId() == null) {
            entityManager.persist(institucion);
            return institucion;
        }

        return entityManager.merge(institucion);
    }

    public Optional<InstitucionFinanciera> buscarPorId(Long id) {

        Objects.requireNonNull(
                id,
                "El id es obligatorio"
        );

        return Optional.ofNullable(
                entityManager.find(
                        InstitucionFinanciera.class,
                        id
                )
        );
    }

    public Optional<InstitucionFinanciera> buscarPorNombre(
            String nombre) {

        Objects.requireNonNull(
                nombre,
                "El nombre es obligatorio"
        );

        List<InstitucionFinanciera> instituciones =
                entityManager.createQuery(
                                """
                                SELECT i
                                FROM InstitucionFinanciera i
                                WHERE i.nombre = :nombre
                                """,
                                InstitucionFinanciera.class
                        )
                        .setParameter("nombre", nombre)
                        .getResultList();

        return instituciones.stream().findFirst();
    }

    public List<InstitucionFinanciera> listarTodas() {

        return entityManager.createQuery(
                """
                SELECT i
                FROM InstitucionFinanciera i
                ORDER BY i.id
                """,
                InstitucionFinanciera.class
        ).getResultList();
    }
}
