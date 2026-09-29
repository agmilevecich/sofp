package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.InstitucionFinancieraRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class InstitucionFinancieraService {

    private final InstitucionFinancieraRepository institucionFinancieraRepository;
    private final EntityManager entityManager;

    public InstitucionFinancieraService(InstitucionFinancieraRepository institucionFinancieraRepository) {
        this(institucionFinancieraRepository, null);
    }

    public InstitucionFinancieraService(
            InstitucionFinancieraRepository institucionFinancieraRepository,
            EntityManager entityManager) {
        this.institucionFinancieraRepository = Objects.requireNonNull(
                institucionFinancieraRepository,
                "El repositorio de instituciones financieras es obligatorio");
        this.entityManager = entityManager;
    }

    public InstitucionFinanciera guardar(InstitucionFinanciera institucion) {
        Objects.requireNonNull(institucion, "La institución financiera es obligatoria");
        return institucionFinancieraRepository.guardar(institucion);
    }

    public InstitucionFinanciera registrar(InstitucionFinanciera institucion) {
        return registrarInterno(institucion, null);
    }

    public InstitucionFinanciera registrar(InstitucionFinanciera institucion, Long usuarioId) {
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");
        return registrarInterno(institucion, usuarioId);
    }

    private InstitucionFinanciera registrarInterno(
            InstitucionFinanciera institucion,
            Long usuarioId) {
        Objects.requireNonNull(institucion, "La institución financiera es obligatoria");
        if (entityManager == null) {
            throw new IllegalStateException(
                    "El EntityManager es obligatorio para registrar una institución financiera");
        }

        EntityTransaction transaction = entityManager.getTransaction();
        boolean transactionIniciadaPorElServicio = !transaction.isActive();

        try {
            if (transactionIniciadaPorElServicio) {
                transaction.begin();
            }

            if (usuarioId != null) {
                Usuario usuario = entityManager.find(Usuario.class, usuarioId);
                if (usuario == null) {
                    throw new IllegalArgumentException("No existe el usuario con id: " + usuarioId);
                }
                institucion.asignarUsuario(usuario);
            }

            InstitucionFinanciera registrada = institucionFinancieraRepository.guardar(institucion);
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

    public Optional<InstitucionFinanciera> buscarPorId(Long id) {
        Objects.requireNonNull(id, "El id de la institución financiera es obligatorio");
        return institucionFinancieraRepository.buscarPorId(id);
    }

    public Optional<InstitucionFinanciera> buscarPorId(Long id, Long usuarioId) {
        validarIds(id, usuarioId);
        return institucionFinancieraRepository.buscarPorId(id, usuarioId);
    }

    public Optional<InstitucionFinanciera> buscarPorNombre(String nombre) {
        Objects.requireNonNull(nombre, "El nombre de la institución financiera es obligatorio");
        return institucionFinancieraRepository.buscarPorNombre(nombre);
    }

    public Optional<InstitucionFinanciera> buscarPorNombre(String nombre, Long usuarioId) {
        Objects.requireNonNull(nombre, "El nombre de la institución financiera es obligatorio");
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");
        return institucionFinancieraRepository.buscarPorNombre(nombre, usuarioId);
    }

    public List<InstitucionFinanciera> listarTodas() {
        return institucionFinancieraRepository.listarTodas();
    }

    public List<InstitucionFinanciera> listarPorUsuario(Long usuarioId) {
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");
        return institucionFinancieraRepository.listarPorUsuario(usuarioId);
    }

    public InstitucionFinanciera renombrar(Long institucionId, String nuevoNombre) {
        InstitucionFinanciera institucion = obtenerPorId(institucionId);
        institucion.renombrar(nuevoNombre);
        return institucionFinancieraRepository.guardar(institucion);
    }

    public InstitucionFinanciera renombrar(Long institucionId, Long usuarioId, String nuevoNombre) {
        InstitucionFinanciera institucion = obtenerPorIdAutorizada(institucionId, usuarioId);
        institucion.renombrar(nuevoNombre);
        return institucionFinancieraRepository.guardar(institucion);
    }

    public InstitucionFinanciera actualizarSitioWeb(Long institucionId, String sitioWeb) {
        InstitucionFinanciera institucion = obtenerPorId(institucionId);
        institucion.actualizarSitioWeb(sitioWeb);
        return institucionFinancieraRepository.guardar(institucion);
    }

    public InstitucionFinanciera actualizarSitioWeb(Long institucionId, Long usuarioId, String sitioWeb) {
        InstitucionFinanciera institucion = obtenerPorIdAutorizada(institucionId, usuarioId);
        institucion.actualizarSitioWeb(sitioWeb);
        return institucionFinancieraRepository.guardar(institucion);
    }

    public InstitucionFinanciera actualizarDescripcion(Long institucionId, String descripcion) {
        InstitucionFinanciera institucion = obtenerPorId(institucionId);
        institucion.actualizarDescripcion(descripcion);
        return institucionFinancieraRepository.guardar(institucion);
    }

    public InstitucionFinanciera actualizarDescripcion(Long institucionId, Long usuarioId, String descripcion) {
        InstitucionFinanciera institucion = obtenerPorIdAutorizada(institucionId, usuarioId);
        institucion.actualizarDescripcion(descripcion);
        return institucionFinancieraRepository.guardar(institucion);
    }

    public InstitucionFinanciera activar(Long institucionId) {
        InstitucionFinanciera institucion = obtenerPorId(institucionId);
        institucion.activar();
        return institucionFinancieraRepository.guardar(institucion);
    }

    public InstitucionFinanciera activar(Long institucionId, Long usuarioId) {
        InstitucionFinanciera institucion = obtenerPorIdAutorizada(institucionId, usuarioId);
        institucion.activar();
        return institucionFinancieraRepository.guardar(institucion);
    }

    public InstitucionFinanciera desactivar(Long institucionId) {
        InstitucionFinanciera institucion = obtenerPorId(institucionId);
        institucion.desactivar();
        return institucionFinancieraRepository.guardar(institucion);
    }

    public InstitucionFinanciera desactivar(Long institucionId, Long usuarioId) {
        InstitucionFinanciera institucion = obtenerPorIdAutorizada(institucionId, usuarioId);
        institucion.desactivar();
        return institucionFinancieraRepository.guardar(institucion);
    }

    private InstitucionFinanciera obtenerPorId(Long institucionId) {
        Objects.requireNonNull(institucionId, "El id de la institución financiera es obligatorio");
        return institucionFinancieraRepository.buscarPorId(institucionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe la institución financiera con id: " + institucionId));
    }

    private InstitucionFinanciera obtenerPorIdAutorizada(Long institucionId, Long usuarioId) {
        validarIds(institucionId, usuarioId);
        return institucionFinancieraRepository.buscarPorId(institucionId, usuarioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe una institución financiera del usuario con id: " + institucionId));
    }

    private void validarIds(Long institucionId, Long usuarioId) {
        Objects.requireNonNull(institucionId, "El id de la institución financiera es obligatorio");
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");
    }
}
