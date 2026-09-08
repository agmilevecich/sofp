package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.domain.Movimiento;
import ar.com.agmilevecich.sofp.domain.Obligacion;
import ar.com.agmilevecich.sofp.persistence.ObligacionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Coordina la persistencia de obligaciones originadas por compras con tarjeta de crédito. */
public class ObligacionService {

    private final EntityManager entityManager;
    private final ObligacionRepository obligacionRepository;

    public ObligacionService(EntityManager entityManager,
                             ObligacionRepository obligacionRepository) {
        this.entityManager = Objects.requireNonNull(
                entityManager,
                "El EntityManager es obligatorio"
        );
        this.obligacionRepository = Objects.requireNonNull(
                obligacionRepository,
                "El ObligacionRepository es obligatorio"
        );
    }

    public Obligacion registrar(Movimiento movimientoOrigen) {
        Obligacion obligacion = new Obligacion(movimientoOrigen);
        return guardar(obligacion);
    }

    public Obligacion registrarPago(Long obligacionId, BigDecimal importe) {
        Objects.requireNonNull(obligacionId, "El id de la obligación es obligatorio");

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            Obligacion obligacion = obligacionRepository.buscarPorId(obligacionId)
                    .orElseThrow(() -> new IllegalArgumentException("La obligación no existe"));

            obligacion.registrarPago(importe);
            entityManager.flush();
            transaction.commit();
            return obligacion;
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    public Optional<Obligacion> buscarPorId(Long id) {
        return obligacionRepository.buscarPorId(id);
    }

    public Optional<Obligacion> buscarPorMovimientoOrigen(Long movimientoId) {
        return obligacionRepository.buscarPorMovimientoOrigen(movimientoId);
    }

    public List<Obligacion> listarTodas() {
        return obligacionRepository.listarTodas();
    }

    private Obligacion guardar(Obligacion obligacion) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Obligacion guardada = obligacionRepository.guardar(obligacion);
            entityManager.flush();
            transaction.commit();
            return guardada;
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }
}
