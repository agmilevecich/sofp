package ar.com.agmilevecich.sofp.persistence;

import ar.com.agmilevecich.sofp.domain.Categoria;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CategoriaRepository {

    private final EntityManager entityManager;

    public CategoriaRepository(EntityManager entityManager) {
        this.entityManager =
                Objects.requireNonNull(
                        entityManager,
                        "El EntityManager es obligatorio"
                );
    }

    public Categoria guardar(Categoria categoria) {

        Objects.requireNonNull(
                categoria,
                "La categoría es obligatoria"
        );

        if (categoria.getId() == null) {
            entityManager.persist(categoria);
            return categoria;
        }

        return entityManager.merge(categoria);
    }

    public Optional<Categoria> buscarPorId(Long id) {

        Objects.requireNonNull(
                id,
                "El id es obligatorio"
        );

        return Optional.ofNullable(
                entityManager.find(
                        Categoria.class,
                        id
                )
        );
    }

    public List<Categoria> listarTodas() {

        return entityManager.createQuery(
                """
                SELECT c
                FROM Categoria c
                ORDER BY c.id
                """,
                Categoria.class
        ).getResultList();
    }

    public List<Categoria> listarPorPerfilFinanciero(
            Long perfilFinancieroId) {

        Objects.requireNonNull(
                perfilFinancieroId,
                "El id del perfil financiero es obligatorio"
        );

        return entityManager.createQuery(
                        """
                        SELECT c
                        FROM Categoria c
                        WHERE c.perfilFinanciero.id = :perfilFinancieroId
                        ORDER BY c.id
                        """,
                        Categoria.class
                )
                .setParameter(
                        "perfilFinancieroId",
                        perfilFinancieroId
                )
                .getResultList();
    }

    public void eliminar(Categoria categoria) {

        Objects.requireNonNull(
                categoria,
                "La categoría es obligatoria"
        );

        Categoria gestionada =
                entityManager.contains(categoria)
                        ? categoria
                        : entityManager.merge(categoria);

        entityManager.remove(gestionada);
    }
}
