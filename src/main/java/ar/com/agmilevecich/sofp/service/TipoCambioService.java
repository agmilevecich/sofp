package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.domain.TipoCambio;
import ar.com.agmilevecich.sofp.persistence.TipoCambioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.Objects;

public class TipoCambioService {

    private final EntityManager entityManager;
    private final TipoCambioRepository tipoCambioRepository;

    public TipoCambioService(TipoCambioRepository tipoCambioRepository) {
        this(null, tipoCambioRepository);
    }

    public TipoCambioService(EntityManager entityManager,
                             TipoCambioRepository tipoCambioRepository) {
        this.entityManager = entityManager;
        this.tipoCambioRepository = Objects.requireNonNull(
                tipoCambioRepository,
                "El TipoCambioRepository es obligatorio"
        );
    }

    public TipoCambio registrar(TipoCambio tipoCambio) {
        Objects.requireNonNull(
                tipoCambio,
                "El tipo de cambio es obligatorio"
        );

        if (entityManager == null) {
            return tipoCambioRepository.guardar(tipoCambio);
        }

        EntityTransaction transaction = entityManager.getTransaction();
        boolean transactionIniciadaPorElServicio = !transaction.isActive();
        try {
            if (transactionIniciadaPorElServicio) {
                transaction.begin();
            }
            TipoCambio registrado = tipoCambioRepository.guardar(tipoCambio);
            entityManager.flush();
            if (transactionIniciadaPorElServicio) {
                transaction.commit();
            }
            return registrado;
        } catch (RuntimeException e) {
            if (transactionIniciadaPorElServicio && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }
}
