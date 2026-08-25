package ar.com.agmilevecich.sofp.persistence;

import ar.com.agmilevecich.sofp.domain.Bono;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class BonoRepository {

    private final EntityManager entityManager;

    public BonoRepository(EntityManager entityManager) {
        this.entityManager =
                Objects.requireNonNull(
                        entityManager,
                        "El EntityManager es obligatorio"
                );
    }

    public Bono guardar(Bono bono) {

        Objects.requireNonNull(
                bono,
                "El bono es obligatorio"
        );

        if (bono.getId() == null) {
            entityManager.persist(bono);
            return bono;
        }

        return entityManager.merge(bono);
    }

    public Optional<Bono> buscarPorId(Long id) {

        Objects.requireNonNull(
                id,
                "El id es obligatorio"
        );

        return Optional.ofNullable(
                entityManager.find(
                        Bono.class,
                        id
                )
        );
    }

    public List<Bono> listarTodas() {

        return entityManager.createQuery(
                """
                SELECT b
                FROM Bono b
                ORDER BY b.id
                """,
                Bono.class
        ).getResultList();
    }
}
