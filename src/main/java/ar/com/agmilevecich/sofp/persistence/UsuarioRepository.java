package ar.com.agmilevecich.sofp.persistence;

import ar.com.agmilevecich.sofp.domain.Usuario;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class UsuarioRepository {

    private final EntityManager entityManager;

    public UsuarioRepository(EntityManager entityManager) {
        this.entityManager =
                Objects.requireNonNull(
                        entityManager,
                        "El EntityManager es obligatorio"
                );
    }

    public Usuario guardar(Usuario usuario) {

        Objects.requireNonNull(
                usuario,
                "El usuario es obligatorio"
        );

        if (usuario.getId() == null) {
            entityManager.persist(usuario);
            return usuario;
        }

        return entityManager.merge(usuario);
    }

    public Optional<Usuario> buscarPorId(Long id) {

        Objects.requireNonNull(
                id,
                "El id es obligatorio"
        );

        return Optional.ofNullable(
                entityManager.find(
                        Usuario.class,
                        id
                )
        );
    }

    public Optional<Usuario> buscarPorEmail(String email) {

        Objects.requireNonNull(
                email,
                "El email es obligatorio"
        );

        List<Usuario> usuarios =
                entityManager.createQuery(
                                """
                                SELECT u
                                FROM Usuario u
                                WHERE u.email = :email
                                """,
                                Usuario.class
                        )
                        .setParameter("email", email)
                        .getResultList();

        return usuarios.stream().findFirst();
    }

    public List<Usuario> listarTodos() {

        return entityManager.createQuery(
                """
                SELECT u
                FROM Usuario u
                ORDER BY u.id
                """,
                Usuario.class
        ).getResultList();
    }
}