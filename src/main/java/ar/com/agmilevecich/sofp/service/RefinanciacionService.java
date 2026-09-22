package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.EstadoObligacion;
import ar.com.agmilevecich.sofp.domain.Obligacion;
import ar.com.agmilevecich.sofp.domain.Refinanciacion;
import ar.com.agmilevecich.sofp.domain.TipoCuenta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class RefinanciacionService {

    private final EntityManager entityManager;

    public RefinanciacionService(EntityManager entityManager) {
        this.entityManager = Objects.requireNonNull(entityManager, "El EntityManager es obligatorio");
    }

    public Refinanciacion crear(Long obligacionId,
                                Long usuarioId,
                                LocalDate fechaInicio,
                                int cantidadCuotas,
                                BigDecimal interesInicial,
                                BigDecimal cargosIniciales,
                                BigDecimal tasaAnual) {
        Objects.requireNonNull(obligacionId, "El id de la obligación es obligatorio");
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");
        Objects.requireNonNull(fechaInicio, "La fecha de inicio es obligatoria");

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Obligacion obligacion = entityManager.find(Obligacion.class, obligacionId);
            if (obligacion == null) throw new IllegalArgumentException("La obligación no existe");
            Cuenta cuenta = obligacion.getMovimientoOrigen().getCuenta();
            if (!Objects.equals(cuenta.getPerfilFinanciero().getUsuario().getId(), usuarioId)) {
                throw new IllegalArgumentException("La obligación no pertenece al usuario autorizado");
            }
            if (cuenta.getTipoCuenta() != TipoCuenta.TARJETA_CREDITO) {
                throw new IllegalArgumentException("La obligación no pertenece a una tarjeta de crédito");
            }
            if (fechaInicio.isBefore(obligacion.getFechaOrigen().toLocalDate())) {
                throw new IllegalArgumentException("La fecha de inicio de la refinanciación no puede ser anterior al origen de la obligación");
            }
            if (obligacion.getEstado() == EstadoObligacion.PAGADA
                    || obligacion.getEstado() == EstadoObligacion.REFINANCIADA) {
                throw new IllegalStateException("La obligación no tiene saldo refinanciable");
            }

            BigDecimal capital = obligacion.getDeudaParaPagoMinimo();
            Refinanciacion refinanciacion = new Refinanciacion(
                    obligacion,
                    cuenta.getMoneda(),
                    fechaInicio,
                    capital,
                    interesInicial,
                    cargosIniciales,
                    tasaAnual,
                    cantidadCuotas
            );
            refinanciacion.generarCuotas();
            entityManager.persist(refinanciacion);
            obligacion.marcarRefinanciada();
            entityManager.flush();
            transaction.commit();
            return refinanciacion;
        } catch (RuntimeException e) {
            if (transaction.isActive()) transaction.rollback();
            throw e;
        }
    }


}
