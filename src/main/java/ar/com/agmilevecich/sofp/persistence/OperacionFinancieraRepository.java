package ar.com.agmilevecich.sofp.persistence;

import ar.com.agmilevecich.sofp.domain.OperacionFinanciera;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class OperacionFinancieraRepository {

    private final EntityManager entityManager;

    public OperacionFinancieraRepository(EntityManager entityManager) {
        this.entityManager =
                Objects.requireNonNull(
                        entityManager,
                        "El EntityManager es obligatorio"
                );
    }

    public OperacionFinanciera guardar(
            OperacionFinanciera operacionFinanciera) {

        Objects.requireNonNull(
                operacionFinanciera,
                "La operación financiera es obligatoria"
        );

        if (operacionFinanciera.getId() == null) {
            entityManager.persist(operacionFinanciera);
            return operacionFinanciera;
        }

        return entityManager.merge(operacionFinanciera);
    }

    public Optional<OperacionFinanciera> buscarPorId(Long id) {

        Objects.requireNonNull(
                id,
                "El id es obligatorio"
        );

        return Optional.ofNullable(
                entityManager.find(
                        OperacionFinanciera.class,
                        id
                )
        );
    }

    public List<OperacionFinanciera> listarTodas() {

        return entityManager.createQuery(
                """
                SELECT o
                FROM OperacionFinanciera o
                ORDER BY o.id
                """,
                OperacionFinanciera.class
        ).getResultList();
    }

    public List<OperacionFinanciera> listarPorCuentaOrigen(
            Long cuentaOrigenId) {

        Objects.requireNonNull(
                cuentaOrigenId,
                "El id de la cuenta de origen es obligatorio"
        );

        return entityManager.createQuery(
                        """
                        SELECT o
                        FROM OperacionFinanciera o
                        WHERE o.cuentaOrigen.id = :cuentaOrigenId
                        ORDER BY o.id
                        """,
                        OperacionFinanciera.class
                )
                .setParameter(
                        "cuentaOrigenId",
                        cuentaOrigenId
                )
                .getResultList();
    }

    public List<OperacionFinanciera> listarPorCuentaDestino(
            Long cuentaDestinoId) {

        Objects.requireNonNull(
                cuentaDestinoId,
                "El id de la cuenta de destino es obligatorio"
        );

        return entityManager.createQuery(
                        """
                        SELECT o
                        FROM OperacionFinanciera o
                        WHERE o.cuentaDestino.id = :cuentaDestinoId
                        ORDER BY o.id
                        """,
                        OperacionFinanciera.class
                )
                .setParameter(
                        "cuentaDestinoId",
                        cuentaDestinoId
                )
                .getResultList();
    }
}
