package ar.com.agmilevecich.sofp.persistence;

import ar.com.agmilevecich.sofp.domain.Activo;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ActivoRepository {

    private final EntityManager entityManager;

    public ActivoRepository(EntityManager entityManager) {
        this.entityManager =
                Objects.requireNonNull(
                        entityManager,
                        "El EntityManager es obligatorio"
                );
    }

    public Activo guardar(Activo activo) {

        Objects.requireNonNull(
                activo,
                "El activo es obligatorio"
        );

        if (activo.getId() == null) {
            entityManager.persist(activo);
            return activo;
        }

        return entityManager.merge(activo);
    }

    public Optional<Activo> buscarPorId(Long id) {

        Objects.requireNonNull(
                id,
                "El id es obligatorio"
        );

        return Optional.ofNullable(
                entityManager.find(
                        Activo.class,
                        id
                )
        );
    }

    public Optional<Activo> buscarPorSimbolo(String simbolo) {

        Objects.requireNonNull(
                simbolo,
                "El símbolo es obligatorio"
        );

        List<Activo> activos =
                entityManager.createQuery(
                                """
                                SELECT a
                                FROM Activo a
                                WHERE a.simbolo = :simbolo
                                """,
                                Activo.class
                        )
                        .setParameter("simbolo", simbolo)
                        .getResultList();

        return activos.stream().findFirst();
    }

    public List<Activo> listarTodas() {

        return entityManager.createQuery(
                """
                SELECT a
                FROM Activo a
                ORDER BY a.id
                """,
                Activo.class
        ).getResultList();
    }
}
