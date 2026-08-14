package ar.com.agmilevecich.sofp.persistence;

import ar.com.agmilevecich.sofp.domain.Movimiento;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MovimientoRepository {

    private final EntityManager entityManager;

    public MovimientoRepository(EntityManager entityManager) {
        this.entityManager =
                Objects.requireNonNull(
                        entityManager,
                        "El EntityManager es obligatorio"
                );
    }

    public Movimiento guardar(Movimiento movimiento) {

        Objects.requireNonNull(
                movimiento,
                "El movimiento es obligatorio"
        );

        if (movimiento.getId() == null) {
            entityManager.persist(movimiento);
            return movimiento;
        }

        return entityManager.merge(movimiento);
    }

    public Optional<Movimiento> buscarPorId(Long id) {

        Objects.requireNonNull(
                id,
                "El id es obligatorio"
        );

        return Optional.ofNullable(
                entityManager.find(
                        Movimiento.class,
                        id
                )
        );
    }

    public List<Movimiento> listarTodos() {

        return entityManager.createQuery(
                """
                SELECT m
                FROM Movimiento m
                ORDER BY m.fechaHora, m.id
                """,
                Movimiento.class
        ).getResultList();
    }

    public List<Movimiento> listarPorCuenta(Long cuentaId) {

        Objects.requireNonNull(
                cuentaId,
                "El id de la cuenta es obligatorio"
        );

        return entityManager.createQuery(
                        """
                        SELECT m
                        FROM Movimiento m
                        WHERE m.cuenta.id = :cuentaId
                        ORDER BY m.fechaHora, m.id
                        """,
                        Movimiento.class
                )
                .setParameter("cuentaId", cuentaId)
                .getResultList();
    }

    public List<Movimiento> listarPorCategoria(Long categoriaId) {

        Objects.requireNonNull(
                categoriaId,
                "El id de la categoría es obligatorio"
        );

        return entityManager.createQuery(
                        """
                        SELECT m
                        FROM Movimiento m
                        WHERE m.categoria.id = :categoriaId
                        ORDER BY m.fechaHora, m.id
                        """,
                        Movimiento.class
                )
                .setParameter("categoriaId", categoriaId)
                .getResultList();
    }

    public void eliminar(Movimiento movimiento) {

        Objects.requireNonNull(
                movimiento,
                "El movimiento es obligatorio"
        );

        Movimiento movimientoGestionado =
                movimiento;

        if (!entityManager.contains(movimientoGestionado)) {
            movimientoGestionado =
                    entityManager.merge(movimientoGestionado);
        }

        entityManager.remove(movimientoGestionado);
    }
}
