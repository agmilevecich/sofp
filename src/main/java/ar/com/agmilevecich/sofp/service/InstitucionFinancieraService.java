package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.persistence.InstitucionFinancieraRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class InstitucionFinancieraService {

    private final InstitucionFinancieraRepository institucionFinancieraRepository;
    private final EntityManager entityManager;

    public InstitucionFinancieraService(
            InstitucionFinancieraRepository institucionFinancieraRepository) {
        this(institucionFinancieraRepository, null);
    }

    public InstitucionFinancieraService(
            InstitucionFinancieraRepository institucionFinancieraRepository,
            EntityManager entityManager) {

        this.institucionFinancieraRepository =
                Objects.requireNonNull(
                        institucionFinancieraRepository,
                        "El repositorio de instituciones financieras es obligatorio"
                );
        this.entityManager = entityManager;
    }

    public InstitucionFinanciera guardar(
            InstitucionFinanciera institucion) {

        Objects.requireNonNull(
                institucion,
                "La institución financiera es obligatoria"
        );

        return institucionFinancieraRepository.guardar(institucion);
    }

    public InstitucionFinanciera registrar(
            InstitucionFinanciera institucion) {

        Objects.requireNonNull(
                institucion,
                "La institución financiera es obligatoria"
        );
        if (entityManager == null) {
            throw new IllegalStateException(
                    "El EntityManager es obligatorio para registrar una institución financiera"
            );
        }

        EntityTransaction transaction = entityManager.getTransaction();
        boolean transactionIniciadaPorElServicio = !transaction.isActive();

        try {
            if (transactionIniciadaPorElServicio) {
                transaction.begin();
            }

            InstitucionFinanciera registrada =
                    institucionFinancieraRepository.guardar(institucion);
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

        Objects.requireNonNull(
                id,
                "El id de la institución financiera es obligatorio"
        );

        return institucionFinancieraRepository.buscarPorId(id);
    }

    public Optional<InstitucionFinanciera> buscarPorNombre(
            String nombre) {

        Objects.requireNonNull(
                nombre,
                "El nombre de la institución financiera es obligatorio"
        );

        return institucionFinancieraRepository.buscarPorNombre(nombre);
    }

    public List<InstitucionFinanciera> listarTodas() {

        return institucionFinancieraRepository.listarTodas();
    }

    public InstitucionFinanciera renombrar(
            Long institucionId,
            String nuevoNombre) {

        InstitucionFinanciera institucion =
                obtenerPorId(institucionId);

        institucion.renombrar(nuevoNombre);

        return institucionFinancieraRepository.guardar(institucion);
    }

    public InstitucionFinanciera actualizarSitioWeb(
            Long institucionId,
            String sitioWeb) {

        InstitucionFinanciera institucion =
                obtenerPorId(institucionId);

        institucion.actualizarSitioWeb(sitioWeb);

        return institucionFinancieraRepository.guardar(institucion);
    }

    public InstitucionFinanciera actualizarDescripcion(
            Long institucionId,
            String descripcion) {

        InstitucionFinanciera institucion =
                obtenerPorId(institucionId);

        institucion.actualizarDescripcion(descripcion);

        return institucionFinancieraRepository.guardar(institucion);
    }

    public InstitucionFinanciera activar(Long institucionId) {

        InstitucionFinanciera institucion =
                obtenerPorId(institucionId);

        institucion.activar();

        return institucionFinancieraRepository.guardar(institucion);
    }

    public InstitucionFinanciera desactivar(Long institucionId) {

        InstitucionFinanciera institucion =
                obtenerPorId(institucionId);

        institucion.desactivar();

        return institucionFinancieraRepository.guardar(institucion);
    }

    private InstitucionFinanciera obtenerPorId(Long institucionId) {

        Objects.requireNonNull(
                institucionId,
                "El id de la institución financiera es obligatorio"
        );

        return institucionFinancieraRepository.buscarPorId(institucionId)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "No existe la institución financiera con id: "
                                        + institucionId
                        )
                );
    }
}
