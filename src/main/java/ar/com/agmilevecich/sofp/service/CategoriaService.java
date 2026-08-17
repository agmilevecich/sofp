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
            String nuevoNombre) {

        Categoria categoria =
                obtenerCategoria(categoriaId);

        Objects.requireNonNull(
                nuevoNombre,
                "El nuevo nombre es obligatorio"
        );

        entityManager.getTransaction().begin();

        try {

            categoria.renombrar(
                    nuevoNombre
            );

            entityManager.flush();

            entityManager.getTransaction().commit();

            return categoria;

        } catch (RuntimeException e) {

            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }

            throw e;
        }
    }

    public Categoria modificarDescripcion(
            Long categoriaId,
            String descripcion) {

        Categoria categoria =
                obtenerCategoria(categoriaId);

        entityManager.getTransaction().begin();

        try {

            categoria.cambiarDescripcion(
                    descripcion
            );

            entityManager.flush();

            entityManager.getTransaction().commit();

            return categoria;

        } catch (RuntimeException e) {

            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }

            throw e;
        }
    }

    public Categoria activar(
            Long categoriaId) {

        Categoria categoria =
                obtenerCategoria(categoriaId);

        entityManager.getTransaction().begin();

        try {

            categoria.activar();

            entityManager.flush();

            entityManager.getTransaction().commit();

            return categoria;

        } catch (RuntimeException e) {

            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }

            throw e;
        }
    }

    public Categoria desactivar(
            Long categoriaId) {

        Categoria categoria =
                obtenerCategoria(categoriaId);

        entityManager.getTransaction().begin();

        try {

            categoria.desactivar();

            entityManager.flush();

            entityManager.getTransaction().commit();

            return categoria;

        } catch (RuntimeException e) {

            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
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

    public void eliminar(Long categoriaId) {

        Objects.requireNonNull(
                categoriaId,
                "El id de la categoría es obligatorio"
        );

        Categoria categoria =
                obtenerCategoria(categoriaId);

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