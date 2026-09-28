package ar.com.agmilevecich.sofp.persistence;

import ar.com.agmilevecich.sofp.domain.CotizacionActivo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CotizacionActivoRepository {

    private final EntityManager entityManager;

    public CotizacionActivoRepository(EntityManager entityManager) {
        this.entityManager = Objects.requireNonNull(
                entityManager,
                "El EntityManager es obligatorio"
        );
    }

    public CotizacionActivo guardar(CotizacionActivo cotizacion) {
        Objects.requireNonNull(cotizacion, "La cotización es obligatoria");

        EntityTransaction transaction = entityManager.getTransaction();
        boolean transaccionPropia = !transaction.isActive();

        try {
            if (transaccionPropia) {
                transaction.begin();
            }

            CotizacionActivo resultado;
            if (cotizacion.getId() == null) {
                entityManager.persist(cotizacion);
                resultado = cotizacion;
            } else {
                resultado = entityManager.merge(cotizacion);
            }

            if (transaccionPropia) {
                transaction.commit();
            }

            return resultado;
        } catch (RuntimeException e) {
            if (transaccionPropia && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    public Optional<CotizacionActivo> buscarPorActivoYFecha(Long activoId, LocalDate fecha) {
        Objects.requireNonNull(activoId, "El id del activo es obligatorio");
        Objects.requireNonNull(fecha, "La fecha es obligatoria");

        List<CotizacionActivo> cotizaciones = entityManager.createQuery(
                """
                SELECT c
                FROM CotizacionActivo c
                WHERE c.activo.id = :activoId
                  AND c.fecha = :fecha
                ORDER BY c.id DESC
                """,
                CotizacionActivo.class
        ).setParameter("activoId", activoId)
         .setParameter("fecha", fecha)
         .getResultList();

        return cotizaciones.stream().findFirst();
    }

    public Optional<CotizacionActivo> buscarUltimaPorActivo(Long activoId) {
        Objects.requireNonNull(activoId, "El id del activo es obligatorio");

        List<CotizacionActivo> cotizaciones = entityManager.createQuery(
                """
                SELECT c
                FROM CotizacionActivo c
                WHERE c.activo.id = :activoId
                ORDER BY c.fecha DESC, c.id DESC
                """,
                CotizacionActivo.class
        ).setParameter("activoId", activoId)
         .setMaxResults(1)
         .getResultList();

        return cotizaciones.stream().findFirst();
    }

    public List<CotizacionActivo> listarPorActivo(Long activoId) {
        Objects.requireNonNull(activoId, "El id del activo es obligatorio");

        return entityManager.createQuery(
                """
                SELECT c
                FROM CotizacionActivo c
                WHERE c.activo.id = :activoId
                ORDER BY c.fecha DESC, c.id DESC
                """,
                CotizacionActivo.class
        ).setParameter("activoId", activoId)
         .getResultList();
    }
}
