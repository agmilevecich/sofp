package ar.com.agmilevecich.sofp.persistence;

import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PerfilFinancieroRepository {

    private final EntityManager entityManager;

    public PerfilFinancieroRepository(EntityManager entityManager) {
        this.entityManager =
                Objects.requireNonNull(
                        entityManager,
                        "El EntityManager es obligatorio"
                );
    }

    public PerfilFinanciero guardar(PerfilFinanciero perfil) {

        Objects.requireNonNull(
                perfil,
                "El perfil financiero es obligatorio"
        );

        if (perfil.getId() == null) {
            entityManager.persist(perfil);
            return perfil;
        }

        return entityManager.merge(perfil);
    }

    public Optional<PerfilFinanciero> buscarPorId(Long id) {

        Objects.requireNonNull(
                id,
                "El id es obligatorio"
        );

        return Optional.ofNullable(
                entityManager.find(
                        PerfilFinanciero.class,
                        id
                )
        );
    }

    public List<PerfilFinanciero> listarTodos() {

        return entityManager.createQuery(
                """
                SELECT p
                FROM PerfilFinanciero p
                ORDER BY p.id
                """,
                PerfilFinanciero.class
        ).getResultList();
    }

    public List<PerfilFinanciero> listarPorUsuario(Long usuarioId) {

        Objects.requireNonNull(
                usuarioId,
                "El id del usuario es obligatorio"
        );

        return entityManager.createQuery(
                        """
                        SELECT p
                        FROM PerfilFinanciero p
                        WHERE p.usuario.id = :usuarioId
                        ORDER BY p.id
                        """,
                        PerfilFinanciero.class
                )
                .setParameter("usuarioId", usuarioId)
                .getResultList();
    }
}