package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.persistence.CategoriaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CategoriaService {

    private final EntityManager entityManager;
    private final CategoriaRepository categoriaRepository;

    public CategoriaService(
            EntityManager entityManager,
            CategoriaRepository categoriaRepository) {

        this.entityManager =
                Objects.requireNonNull(
                        entityManager,
                        "El EntityManager es obligatorio"
                );

        this.categoriaRepository =
                Objects.requireNonNull(
                        categoriaRepository,
                        "El CategoriaRepository es obligatorio"
                );
    }

    public Categoria registrar(
            Categoria categoria) {

        Objects.requireNonNull(
                categoria,
                "La categoría es obligatoria"
        );

        return categoriaRepository.guardar(categoria);
    }

    public Optional<Categoria> buscarPorId(Long id) {

        Objects.requireNonNull(
                id,
                "El id de la categoría es obligatorio"
        );

        return categoriaRepository.buscarPorId(id);
    }

    public List<Categoria> listarTodas() {

        return categoriaRepository.listarTodas();
    }

    public List<Categoria> listarPorPerfilFinanciero(
            Long perfilFinancieroId) {

        Objects.requireNonNull(
                perfilFinancieroId,
                "El id del perfil financiero es obligatorio"
        );

        return categoriaRepository.listarPorPerfilFinanciero(
                perfilFinancieroId
        );
    }

    public Categoria modificarNombre(
            Long categoriaId,
            Long usuarioId,
            String nuevoNombre) {

        validarIds(categoriaId, usuarioId);

        Objects.requireNonNull(
                nuevoNombre,
                "El nuevo nombre es obligatorio"
        );

        Categoria categoria =
                obtenerCategoriaAutorizada(
                        categoriaId,
                        usuarioId
                );

        EntityTransaction transaction =
                entityManager.getTransaction();

        try {
            transaction.begin();

            categoria.renombrar(
                    nuevoNombre
            );

            entityManager.flush();

            transaction.commit();

            return categoria;

        } catch (RuntimeException e) {

            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        }
    }

    public Categoria modificarDescripcion(
            Long categoriaId,
            Long usuarioId,
            String descripcion) {

        validarIds(categoriaId, usuarioId);

        Categoria categoria =
                obtenerCategoriaAutorizada(
                        categoriaId,
                        usuarioId
                );

        EntityTransaction transaction =
                entityManager.getTransaction();

        try {
            transaction.begin();

            categoria.cambiarDescripcion(
                    descripcion
            );

            entityManager.flush();

            transaction.commit();

            return categoria;

        } catch (RuntimeException e) {

            if (transaction.isActive()) {
                transaction.getRollbackOnly();
            }

            throw e;
        }
    }

    public Categoria activar(
            Long categoriaId,
            Long usuarioId) {

        validarIds(categoriaId, usuarioId);

        Categoria categoria =
                obtenerCategoriaAutorizada(
                        categoriaId,
                        usuarioId
                );

        EntityTransaction transaction =
                entityManager.getTransaction();

        try {
            transaction.begin();

            categoria.activar();

            entityManager.flush();

            transaction.commit();

            return categoria;

        } catch (RuntimeException e) {

            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        }
    }

    public Categoria desactivar(
            Long categoriaId,
            Long usuarioId) {

        validarIds(categoriaId, usuarioId);

        Categoria categoria =
                obtenerCategoriaAutorizada(
                        categoriaId,
                        usuarioId
                );

        EntityTransaction transaction =
                entityManager.getTransaction();

        try {
            transaction.begin();

            categoria.desactivar();

            entityManager.flush();

            transaction.commit();

            return categoria;

        } catch (RuntimeException e) {

            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        }
    }

    private Categoria obtenerCategoria(
            Long categoriaId) {

        Objects.requireNonNull(
                categoriaId,
                "El id de la categoría es obligatorio"
        );

        return categoriaRepository
                .buscarPorId(categoriaId)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "No existe la categoría con id: "
                                        + categoriaId
                        )
                );
    }

    private Categoria obtenerCategoriaAutorizada(
            Long categoriaId,
            Long usuarioId) {

        Categoria categoria =
                obtenerCategoria(categoriaId);

        if (!categoria.getPerfilFinanciero()
                .getUsuario()
                .getId()
                .equals(usuarioId)) {

            throw new IllegalArgumentException(
                    "El usuario no es propietario de la categoría"
            );
        }

        return categoria;
    }

    private void validarIds(
            Long categoriaId,
            Long usuarioId) {

        Objects.requireNonNull(
                categoriaId,
                "El id de la categoría es obligatorio"
        );

        Objects.requireNonNull(
                usuarioId,
                "El id del usuario es obligatorio"
        );
    }

    public void eliminar(
            Long categoriaId,
            Long usuarioId) {

        validarIds(categoriaId, usuarioId);

        Categoria categoria =
                obtenerCategoriaAutorizada(
                        categoriaId,
                        usuarioId
                );

        EntityTransaction transaction =
                entityManager.getTransaction();

        try {
            transaction.begin();

            categoriaRepository.eliminar(categoria);

            entityManager.flush();

            transaction.commit();

        } catch (RuntimeException e) {

            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        }
    }
}
