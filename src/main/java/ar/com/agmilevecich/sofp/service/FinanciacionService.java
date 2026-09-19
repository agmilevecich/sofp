package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.domain.CargoFinanciero;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.Financiacion;
import ar.com.agmilevecich.sofp.domain.TasaInteres;
import ar.com.agmilevecich.sofp.domain.TipoCuenta;
import ar.com.agmilevecich.sofp.domain.TipoTasaInteres;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class FinanciacionService {

    private final EntityManager entityManager;

    public FinanciacionService(EntityManager entityManager) {
        this.entityManager = Objects.requireNonNull(entityManager, "El EntityManager es obligatorio");
    }

    public TasaInteres registrarTna(Long cuentaId,
                                    Long usuarioId,
                                    TipoTasaInteres tipo,
                                    LocalDate fechaDesde,
                                    LocalDate fechaHasta,
                                    BigDecimal tasaAnual,
                                    String fuente) {
        Objects.requireNonNull(cuentaId, "El id de la cuenta es obligatorio");
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");
        Cuenta cuenta = entityManager.find(Cuenta.class, cuentaId);
        if (cuenta == null) {
            throw new IllegalArgumentException("La cuenta no existe");
        }
        if (!Objects.equals(cuenta.getPerfilFinanciero().getUsuario().getId(), usuarioId)) {
            throw new IllegalArgumentException("La cuenta no pertenece al usuario autorizado");
        }
        if (cuenta.getTipoCuenta() != TipoCuenta.TARJETA_CREDITO) {
            throw new IllegalArgumentException("La cuenta no es una tarjeta de crédito");
        }

        TasaInteres tasa = new TasaInteres(
                cuenta, tipo, fechaDesde, fechaHasta, tasaAnual, fuente
        );
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            long superposiciones = entityManager.createQuery(
                    """
                    SELECT COUNT(t)
                    FROM TasaInteres t
                    WHERE t.cuenta = :cuenta
                      AND t.tipo = :tipo
                      AND (t.fechaHasta IS NULL OR t.fechaHasta >= :fechaDesde)
                      AND (:fechaHasta IS NULL OR t.fechaDesde <= :fechaHasta)
                    """,
                    Long.class
            )
            .setParameter("cuenta", cuenta)
            .setParameter("tipo", tipo)
            .setParameter("fechaDesde", fechaDesde)
            .setParameter("fechaHasta", fechaHasta)
            .getSingleResult();
            if (superposiciones > 0) {
                throw new IllegalArgumentException("La tasa se superpone con una vigencia existente");
            }
            entityManager.persist(tasa);
            entityManager.flush();
            transaction.commit();
            return tasa;
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    public Optional<TasaInteres> buscarTasaVigente(Long cuentaId,
                                                   TipoTasaInteres tipo,
                                                   LocalDate fecha) {
        Objects.requireNonNull(cuentaId, "El id de la cuenta es obligatorio");
        Objects.requireNonNull(tipo, "El tipo de tasa es obligatorio");
        Objects.requireNonNull(fecha, "La fecha es obligatoria");
        return entityManager.createQuery(
                """
                SELECT t
                FROM TasaInteres t
                WHERE t.cuenta.id = :cuentaId
                  AND t.tipo = :tipo
                  AND t.fechaDesde <= :fecha
                  AND (t.fechaHasta IS NULL OR t.fechaHasta >= :fecha)
                ORDER BY t.fechaDesde DESC, t.id DESC
                """,
                TasaInteres.class
        )
        .setParameter("cuentaId", cuentaId)
        .setParameter("tipo", tipo)
        .setParameter("fecha", fecha)
        .setMaxResults(1)
        .getResultStream()
        .findFirst();
    }

    public CargoFinanciero calcularInteres(Long financiacionId,
                                           LocalDate fechaHasta,
                                           Long usuarioId) {
        Objects.requireNonNull(financiacionId, "El id de la financiación es obligatorio");
        Objects.requireNonNull(fechaHasta, "La fecha de cálculo es obligatoria");
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Financiacion financiacion = entityManager.find(Financiacion.class, financiacionId);
            if (financiacion == null) {
                throw new IllegalArgumentException("La financiación no existe");
            }
            validarPropietario(usuarioId, financiacion);

            LocalDate fechaDesde = financiacion.getFechaUltimoCalculoInteres();
            if (!fechaHasta.isAfter(fechaDesde) || !financiacion.estaPendiente()) {
                throw new IllegalArgumentException("No existe un período de interés pendiente");
            }

            BigDecimal capital = financiacion.getSaldoCapital();
            List<TasaInteres> tasas = entityManager.createQuery(
                    """
                    SELECT t
                    FROM TasaInteres t
                    WHERE t.cuenta = :cuenta
                      AND t.tipo = :tipo
                      AND t.fechaDesde <= :fechaHasta
                      AND (t.fechaHasta IS NULL OR t.fechaHasta >= :fechaDesde)
                    ORDER BY t.fechaDesde ASC, t.id ASC
                    """,
                    TasaInteres.class
            )
            .setParameter("cuenta", financiacion.getObligacion().getMovimientoOrigen().getCuenta())
            .setParameter("tipo", TipoTasaInteres.TNA_FINANCIERA)
            .setParameter("fechaDesde", fechaDesde)
            .setParameter("fechaHasta", fechaHasta)
            .getResultList();

            if (tasas.isEmpty()) {
                throw new IllegalArgumentException("No existe TNA vigente para el período de interés");
            }

            BigDecimal interes = BigDecimal.ZERO;
            LocalDate dia = fechaDesde;
            int diasCalculados = 0;
            while (dia.isBefore(fechaHasta)) {
                LocalDate fechaDia = dia;
                TasaInteres tasa = tasas.stream()
                        .filter(t -> t.vigenteEn(fechaDia))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException(
                                "No existe TNA vigente para " + fechaDia
                        ));
                interes = interes.add(
                        capital.multiply(tasa.getTasaAnual())
                                .divide(new BigDecimal("100"), 12, RoundingMode.HALF_UP)
                                .divide(new BigDecimal("365"), 12, RoundingMode.HALF_UP)
                );
                diasCalculados++;
                dia = dia.plusDays(1);
            }

            interes = interes.setScale(2, RoundingMode.HALF_UP);
            TasaInteres tasaReferencia = tasas.stream()
                    .filter(t -> t.vigenteEn(fechaHasta.minusDays(1)))
                    .findFirst()
                    .orElse(tasas.get(0));

            CargoFinanciero cargo = financiacion.registrarInteres(
                    interes,
                    fechaHasta,
                    capital,
                    tasaReferencia.getTasaAnual(),
                    diasCalculados
            );
            entityManager.flush();
            transaction.commit();
            return cargo;
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    public CargoFinanciero calcularPunitorio(Long financiacionId,
                                             LocalDate fechaHasta,
                                             Long usuarioId) {
        Objects.requireNonNull(financiacionId, "El id de la financiación es obligatorio");
        Objects.requireNonNull(fechaHasta, "La fecha de cálculo es obligatoria");
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Financiacion financiacion = entityManager.find(Financiacion.class, financiacionId);
            if (financiacion == null) {
                throw new IllegalArgumentException("La financiación no existe");
            }
            validarPropietario(usuarioId, financiacion);

            LocalDate fechaDesde = financiacion.getFechaUltimoCalculoPunitorio();
            if (!fechaHasta.isAfter(fechaDesde) || !financiacion.estaPendiente()) {
                throw new IllegalArgumentException("No existe un período de punitorio pendiente");
            }

            BigDecimal capital = financiacion.getSaldoCapital();
            List<TasaInteres> tasas = entityManager.createQuery(
                    """
                    SELECT t
                    FROM TasaInteres t
                    WHERE t.cuenta = :cuenta
                      AND t.tipo = :tipo
                      AND t.fechaDesde <= :fechaHasta
                      AND (t.fechaHasta IS NULL OR t.fechaHasta >= :fechaDesde)
                    ORDER BY t.fechaDesde ASC, t.id ASC
                    """,
                    TasaInteres.class
            )
            .setParameter("cuenta", financiacion.getObligacion().getMovimientoOrigen().getCuenta())
            .setParameter("tipo", TipoTasaInteres.TNA_PUNITORIA)
            .setParameter("fechaDesde", fechaDesde)
            .setParameter("fechaHasta", fechaHasta)
            .getResultList();

            if (tasas.isEmpty()) {
                throw new IllegalArgumentException("No existe TNA punitoria vigente para el período");
            }

            BigDecimal punitorio = BigDecimal.ZERO;
            LocalDate dia = fechaDesde;
            int diasCalculados = 0;
            while (dia.isBefore(fechaHasta)) {
                LocalDate fechaDia = dia;
                TasaInteres tasa = tasas.stream()
                        .filter(t -> t.vigenteEn(fechaDia))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException(
                                "No existe TNA punitoria vigente para " + fechaDia
                        ));
                punitorio = punitorio.add(
                        capital.multiply(tasa.getTasaAnual())
                                .divide(new BigDecimal("100"), 12, RoundingMode.HALF_UP)
                                .divide(new BigDecimal("365"), 12, RoundingMode.HALF_UP)
                );
                diasCalculados++;
                dia = dia.plusDays(1);
            }

            punitorio = punitorio.setScale(2, RoundingMode.HALF_UP);
            TasaInteres tasaReferencia = tasas.stream()
                    .filter(t -> t.vigenteEn(fechaHasta.minusDays(1)))
                    .findFirst()
                    .orElse(tasas.get(0));

            CargoFinanciero cargo = financiacion.registrarPunitorio(
                    punitorio,
                    fechaHasta,
                    capital,
                    tasaReferencia.getTasaAnual(),
                    diasCalculados
            );
            entityManager.flush();
            transaction.commit();
            return cargo;
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    private void validarPropietario(Long usuarioId, Financiacion financiacion) {
        Long propietarioId = financiacion.getObligacion()
                .getMovimientoOrigen()
                .getCuenta()
                .getPerfilFinanciero()
                .getUsuario()
                .getId();
        if (!Objects.equals(propietarioId, usuarioId)) {
            throw new IllegalArgumentException("La financiación no pertenece al usuario autorizado");
        }
    }
}
