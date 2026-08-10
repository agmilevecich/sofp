package ar.com.agmilevecich.sofp.persistence;

import ar.com.agmilevecich.sofp.domain.Moneda;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MonedaRepository {

    private final EntityManager entityManager;

    public MonedaRepository(EntityManager entityManager) {
        this.entityManager =
                Objects.requireNonNull(
                        entityManager,
                        "El EntityManager es obligatorio"
                );
    }

    public Moneda guardar(Moneda moneda) {

        Objects.requireNonNull(
                moneda,
                "La moneda es obligatoria"
        );

        if (moneda.getId() == null) {
            entityManager.persist(moneda);
            return moneda;
        }

        return entityManager.merge(moneda);
    }

    public Optional<Moneda> buscarPorId(Long id) {

        Objects.requireNonNull(
                id,
                "El id es obligatorio"
        );

        return Optional.ofNullable(
                entityManager.find(
                        Moneda.class,
                        id
                )
        );
    }

    public Optional<Moneda> buscarPorCodigo(String codigo) {

        Objects.requireNonNull(
                codigo,
                "El código es obligatorio"
        );

        List<Moneda> monedas =
                entityManager.createQuery(
                                """
                                SELECT m
                                FROM Moneda m
                                WHERE m.codigo = :codigo
                                """,
                                Moneda.class
                        )
                        .setParameter("codigo", codigo)
                        .getResultList();

        return monedas.stream().findFirst();
    }

    public List<Moneda> listarTodas() {

        return entityManager.createQuery(
                """
                SELECT m
                FROM Moneda m
                ORDER BY m.id
                """,
                Moneda.class
        ).getResultList();
    }
}