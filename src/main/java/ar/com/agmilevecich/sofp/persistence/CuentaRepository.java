package ar.com.agmilevecich.sofp.persistence;

import ar.com.agmilevecich.sofp.domain.Cuenta;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CuentaRepository {

    private final EntityManager entityManager;

    public CuentaRepository(EntityManager entityManager) {
        this.entityManager =
                Objects.requireNonNull(
                        entityManager,
                        "El EntityManager es obligatorio"
                );
    }

    public Cuenta guardar(Cuenta cuenta) {

        Objects.requireNonNull(
                cuenta,
                "La cuenta es obligatoria"
        );

        if (cuenta.getId() == null) {
            entityManager.persist(cuenta);
            return cuenta;
        }

        return entityManager.merge(cuenta);
    }

    public Optional<Cuenta> buscarPorId(Long id) {

        Objects.requireNonNull(
                id,
                "El id es obligatorio"
        );

        return Optional.ofNullable(
                entityManager.find(
                        Cuenta.class,
                        id
                )
        );
    }

    public List<Cuenta> listarTodas() {

        return entityManager.createQuery(
                """
                SELECT c
                FROM Cuenta c
                ORDER BY c.id
                """,
                Cuenta.class
        ).getResultList();
    }

    public List<Cuenta> listarPorPerfilFinanciero(
            Long perfilFinancieroId) {

        Objects.requireNonNull(
                perfilFinancieroId,
                "El id del perfil financiero es obligatorio"
        );

        return entityManager.createQuery(
                        """
                        SELECT c
                        FROM Cuenta c
                        WHERE c.perfilFinanciero.id = :perfilFinancieroId
                        ORDER BY c.id
                        """,
                        Cuenta.class
                )
                .setParameter(
                        "perfilFinancieroId",
                        perfilFinancieroId
                )
                .getResultList();
    }
}
