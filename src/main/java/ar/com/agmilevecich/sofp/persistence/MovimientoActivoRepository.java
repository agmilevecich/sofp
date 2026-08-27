package ar.com.agmilevecich.sofp.persistence;

import ar.com.agmilevecich.sofp.domain.MovimientoActivo;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MovimientoActivoRepository {

    private final EntityManager entityManager;

    public MovimientoActivoRepository(EntityManager entityManager) {
        this.entityManager = Objects.requireNonNull(
                entityManager,
                "El EntityManager es obligatorio"
        );
    }

    public MovimientoActivo guardar(MovimientoActivo movimientoActivo) {

        Objects.requireNonNull(
                movimientoActivo,
                "El movimiento de activo es obligatorio"
        );

        if (movimientoActivo.getId() == null) {
            entityManager.persist(movimientoActivo);
            return movimientoActivo;
        }

        return entityManager.merge(movimientoActivo);
    }

    public Optional<MovimientoActivo> buscarPorId(Long id) {

        Objects.requireNonNull(
                id,
                "El id es obligatorio"
        );

        return Optional.ofNullable(
                entityManager.find(
                        MovimientoActivo.class,
                        id
                )
        );
    }

    public List<MovimientoActivo> listarTodas() {

        return entityManager.createQuery(
                """
                SELECT m
                FROM MovimientoActivo m
                ORDER BY m.id
                """,
                MovimientoActivo.class
        ).getResultList();
    }

    public List<MovimientoActivo> listarPorActivo(Long activoId) {

        Objects.requireNonNull(
                activoId,
                "El id del activo es obligatorio"
        );

        return entityManager.createQuery(
                """
                SELECT m
                FROM MovimientoActivo m
                WHERE m.activo.id = :activoId
                ORDER BY m.id
                """,
                MovimientoActivo.class
        ).setParameter("activoId", activoId)
         .getResultList();
    }
}
