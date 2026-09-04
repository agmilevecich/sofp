package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.persistence.CategoriaRepository;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CategoriaService {

    private final EntityManager entityManager;
    private final CategoriaRepository categoriaRepository;
    private final MovimientoRepository movimientoRepository;

    public CategoriaService(EntityManager entityManager, CategoriaRepository categoriaRepository) {
        this(
                entityManager,
                categoriaRepository,
                new MovimientoRepository(entityManager)
        );
    }

    public CategoriaService(EntityManager entityManager,
                            CategoriaRepository categoriaRepository,
                            MovimientoRepository movimientoRepository) {
        this.entityManager = Objects.requireNonNull(entityManager, "El EntityManager es obligatorio");
        this.categoriaRepository = Objects.requireNonNull(categoriaRepository, "El CategoriaRepository es obligatorio");
        this.movimientoRepository = Objects.requireNonNull(movimientoRepository, "El MovimientoRepository es obligatorio");
    }

    public Categoria registrar(Categoria categoria, Long usuarioId) {
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");
        Objects.requireNonNull(categoria, "La categoría es obligatoria");
        validarPropietario(usuarioId, categoria);
        EntityTransaction transaction = entityManager.getTransaction();
        boolean transactionIniciadaPorElServicio = !transaction.isActive();
        try {
            if (transactionIniciadaPorElServicio) {
                transaction.begin();
            }
            Categoria registrada = categoriaRepository.guardar(categoria);
            entityManager.flush();
            if (transactionIniciadaPorElServicio) {
                transaction.commit();
            }
            return registrada;
        } catch (RuntimeException e) {
            if (transactionIniciadaPorElServicio && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    public Optional<Categoria> buscarPorId(Long id, Long usuarioId) {
        Objects.requireNonNull(id, "El id de la categoría es obligatorio");
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");
        return Optional.of(obtenerCategoriaAutorizada(id, usuarioId));
    }

    public List<Categoria> listarPorPerfilFinanciero(Long perfilFinancieroId, Long usuarioId) {
        Objects.requireNonNull(perfilFinancieroId, "El id del perfil financiero es obligatorio");
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");
        Long propietarioId = entityManager.createQuery(
                "SELECT p.usuario.id FROM PerfilFinanciero p WHERE p.id = :perfilId", Long.class)
                .setParameter("perfilId", perfilFinancieroId)
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe el perfil financiero con id " + perfilFinancieroId));
        if (!Objects.equals(propietarioId, usuarioId)) {
            throw new IllegalArgumentException("El usuario no es propietario del perfil financiero");
        }
        return categoriaRepository.listarPorPerfilFinanciero(perfilFinancieroId);
    }

    /* API interna de compatibilidad para tests y coordinación interna del paquete. */
    Categoria registrar(Categoria categoria) {
        Objects.requireNonNull(categoria, "La categoría es obligatoria");
        return categoriaRepository.guardar(categoria);
    }

    Optional<Categoria> buscarPorId(Long id) {
        Objects.requireNonNull(id, "El id de la categoría es obligatorio");
        return categoriaRepository.buscarPorId(id);
    }

    List<Categoria> listarTodas() {
        return categoriaRepository.listarTodas();
    }

    List<Categoria> listarPorPerfilFinanciero(Long perfilFinancieroId) {
        Objects.requireNonNull(perfilFinancieroId, "El id del perfil financiero es obligatorio");
        return categoriaRepository.listarPorPerfilFinanciero(perfilFinancieroId);
    }

    public Categoria modificarNombre(Long categoriaId, Long usuarioId, String nuevoNombre) {
        validarIds(categoriaId, usuarioId);
        Objects.requireNonNull(nuevoNombre, "El nuevo nombre es obligatorio");
        Categoria categoria = obtenerCategoriaAutorizada(categoriaId, usuarioId);
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            categoria.renombrar(nuevoNombre);
            entityManager.flush();
            transaction.commit();
            return categoria;
        } catch (RuntimeException e) {
            if (transaction.isActive()) transaction.rollback();
            throw e;
        }
    }

    public Categoria modificarDescripcion(Long categoriaId, Long usuarioId, String descripcion) {
        validarIds(categoriaId, usuarioId);
        Categoria categoria = obtenerCategoriaAutorizada(categoriaId, usuarioId);
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            categoria.cambiarDescripcion(descripcion);
            entityManager.flush();
            transaction.commit();
            return categoria;
        } catch (RuntimeException e) {
            if (transaction.isActive()) transaction.rollback();
            throw e;
        }
    }

    public Categoria activar(Long categoriaId, Long usuarioId) {
        validarIds(categoriaId, usuarioId);
        Categoria categoria = obtenerCategoriaAutorizada(categoriaId, usuarioId);
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            categoria.activar();
            entityManager.flush();
            transaction.commit();
            return categoria;
        } catch (RuntimeException e) {
            if (transaction.isActive()) transaction.rollback();
            throw e;
        }
    }

    public Categoria desactivar(Long categoriaId, Long usuarioId) {
        validarIds(categoriaId, usuarioId);
        Categoria categoria = obtenerCategoriaAutorizada(categoriaId, usuarioId);
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            categoria.desactivar();
            entityManager.flush();
            transaction.commit();
            return categoria;
        } catch (RuntimeException e) {
            if (transaction.isActive()) transaction.rollback();
            throw e;
        }
    }

    private Categoria obtenerCategoria(Long categoriaId) {
        Objects.requireNonNull(categoriaId, "El id de la categoría es obligatorio");
        return categoriaRepository.buscarPorId(categoriaId).orElseThrow(
                () -> new IllegalArgumentException("No existe la categoría con id: " + categoriaId));
    }

    private Categoria obtenerCategoriaAutorizada(Long categoriaId, Long usuarioId) {
        Categoria categoria = obtenerCategoria(categoriaId);
        validarPropietario(usuarioId, categoria);
        return categoria;
    }

    private void validarPropietario(Long usuarioId, Categoria categoria) {
        Long propietarioId = categoria.getPerfilFinanciero().getUsuario().getId();
        if (!Objects.equals(propietarioId, usuarioId)) {
            throw new IllegalArgumentException("El usuario no es propietario de la categoría");
        }
    }

    private void validarIds(Long categoriaId, Long usuarioId) {
        Objects.requireNonNull(categoriaId, "El id de la categoría es obligatorio");
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");
    }

    public boolean eliminar(Long categoriaId, Long usuarioId) {
        validarIds(categoriaId, usuarioId);
        Categoria categoria = obtenerCategoriaAutorizada(categoriaId, usuarioId);
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            boolean eliminada = movimientoRepository.listarPorCategoria(categoriaId).isEmpty();
            if (eliminada) {
                categoriaRepository.eliminar(categoria);
            } else {
                categoria.desactivar();
            }
            entityManager.flush();
            transaction.commit();
            return eliminada;
        } catch (RuntimeException e) {
            if (transaction.isActive()) transaction.rollback();
            throw e;
        }
    }
}
