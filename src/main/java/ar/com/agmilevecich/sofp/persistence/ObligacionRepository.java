package ar.com.agmilevecich.sofp.persistence;

import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.Obligacion;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ObligacionRepository {

    private final EntityManager entityManager;

    public ObligacionRepository(EntityManager entityManager) {
        this.entityManager = Objects.requireNonNull(
                entityManager,
                "El EntityManager es obligatorio"
        );
    }

    public Obligacion guardar(Obligacion obligacion) {
        Objects.requireNonNull(
                obligacion,
                "La obligación es obligatoria"
        );

        if (obligacion.getId() == null) {
            entityManager.persist(obligacion);
            return obligacion;
        }

        return entityManager.merge(obligacion);
    }

    public Optional<Obligacion> buscarPorId(Long id) {
        Objects.requireNonNull(
                id,
                "El id es obligatorio"
        );

        return Optional.ofNullable(
                entityManager.find(Obligacion.class, id)
        );
    }

    public Optional<Obligacion> buscarPorMovimientoOrigen(Long movimientoId) {
        Objects.requireNonNull(
                movimientoId,
                "El id del movimiento es obligatorio"
        );

        return entityManager.createQuery(
                        """
                        SELECT o
                        FROM Obligacion o
                        WHERE o.movimientoOrigen.id = :movimientoId
                        """,
                        Obligacion.class
                )
                .setParameter("movimientoId", movimientoId)
                .getResultStream()
                .findFirst();
    }

    public List<Obligacion> listarTodas() {
        return entityManager.createQuery(
                        """
                        SELECT o
                        FROM Obligacion o
                        ORDER BY o.movimientoOrigen.fechaHora, o.id
                        """,
                        Obligacion.class
                )
                .getResultList();
    }

    public List<Obligacion> listarPorUsuario(Long usuarioId) {
        Objects.requireNonNull(
                usuarioId,
                "El id del usuario es obligatorio"
        );

        return entityManager.createQuery(
                        """
                        SELECT o
                        FROM Obligacion o
                        WHERE o.movimientoOrigen.cuenta.perfilFinanciero.usuario.id = :usuarioId
                        ORDER BY o.movimientoOrigen.fechaHora, o.id
                        """,
                        Obligacion.class
                )
                .setParameter("usuarioId", usuarioId)
                .getResultList();
    }

    public List<Obligacion> listarPorCuentaYCierreCiclo(
            Long cuentaId,
            LocalDate fechaCierre
    ) {
        Objects.requireNonNull(
                cuentaId,
                "El id de la cuenta es obligatorio"
        );
        Objects.requireNonNull(
                fechaCierre,
                "La fecha de cierre es obligatoria"
        );

        return entityManager.createQuery(
                        """
                        SELECT o
                        FROM Obligacion o
                        WHERE o.movimientoOrigen.cuenta.id = :cuentaId
                          AND (
                              (
                                  o.cuotas IS EMPTY
                                  AND o.fechaCierreCiclo = :fechaCierre
                                  AND o.saldoPendiente > 0
                              )
                              OR EXISTS (
                                  SELECT c.id
                                  FROM Cuota c
                                  WHERE c.obligacion = o
                                    AND c.fechaCierreCiclo = :fechaCierre
                                    AND c.saldoPendiente > 0
                              )
                          )
                        ORDER BY o.movimientoOrigen.fechaHora, o.id
                        """,
                        Obligacion.class
                )
                .setParameter("cuentaId", cuentaId)
                .setParameter("fechaCierre", fechaCierre)
                .getResultList();
    }

    /** Suma el crédito pendiente y los consumos de tarjeta todavía no materializados como obligación. */
    public BigDecimal sumarSaldoPendientePorCuentaYMoneda(Long cuentaId, Moneda moneda) {
        Objects.requireNonNull(cuentaId, "El id de la cuenta es obligatorio");
        Objects.requireNonNull(moneda, "La moneda es obligatoria");

        BigDecimal saldoPendiente = entityManager.createQuery(
                        """
                        SELECT COALESCE(SUM(o.saldoPendiente), 0)
                        FROM Obligacion o
                        WHERE o.movimientoOrigen.cuenta.id = :cuentaId
                          AND o.movimientoOrigen.moneda = :moneda
                          AND o.saldoPendiente > 0
                        """,
                        BigDecimal.class
                )
                .setParameter("cuentaId", cuentaId)
                .setParameter("moneda", moneda)
                .getSingleResult();

        BigDecimal consumosSinObligacion = entityManager.createQuery(
                        """
                        SELECT COALESCE(SUM(m.importe), 0)
                        FROM Movimiento m
                        WHERE m.cuenta.id = :cuentaId
                          AND m.moneda = :moneda
                          AND m.tipoMovimiento = ar.com.agmilevecich.sofp.domain.TipoMovimiento.EGRESO
                          AND m.formaPago = ar.com.agmilevecich.sofp.domain.FormaPago.TARJETA_CREDITO
                          AND NOT EXISTS (
                              SELECT o.id
                              FROM Obligacion o
                              WHERE o.movimientoOrigen.id = m.id
                          )
                        """,
                        BigDecimal.class
                )
                .setParameter("cuentaId", cuentaId)
                .setParameter("moneda", moneda)
                .getSingleResult();

        return saldoPendiente.add(consumosSinObligacion);
    }

    /**
     * Calcula el crédito utilizado en la moneda de la tarjeta.
     * Las obligaciones ya liquidadas usan el saldo de liquidación;
     * las obligaciones todavía no liquidadas usan su saldo pendiente,
     * valorizado al cierre cuando corresponda.
     */
    public BigDecimal sumarCreditoUtilizadoPorCuenta(Long cuentaId, Moneda moneda) {
        Objects.requireNonNull(cuentaId, "El id de la cuenta es obligatorio");
        Objects.requireNonNull(moneda, "La moneda es obligatoria");

        BigDecimal obligacionesLiquidadas = entityManager.createQuery(
                        """
                        SELECT COALESCE(SUM(o.saldoLiquidacion), 0)
                        FROM Obligacion o
                        WHERE o.movimientoOrigen.cuenta.id = :cuentaId
                          AND o.saldoLiquidacion IS NOT NULL
                          AND o.saldoLiquidacion > 0
                        """,
                        BigDecimal.class
                )
                .setParameter("cuentaId", cuentaId)
                .getSingleResult();

        BigDecimal obligacionesSinCuotas = entityManager.createQuery(
                        """
                        SELECT COALESCE(SUM(
                            CASE
                                WHEN o.movimientoOrigen.moneda = :moneda
                                    THEN o.saldoPendiente
                                WHEN o.importeValorizacionCierre IS NOT NULL
                                    THEN CASE
                                        WHEN o.saldoPendiente = o.importeOriginal
                                            THEN o.importeValorizacionCierre
                                        ELSE FUNCTION('ROUND',
                                            o.importeValorizacionCierre * o.saldoPendiente / o.importeOriginal,
                                            2
                                        )
                                    END
                                ELSE 0
                            END
                        ), 0)
                        FROM Obligacion o
                        WHERE o.movimientoOrigen.cuenta.id = :cuentaId
                          AND o.saldoLiquidacion IS NULL
                          AND o.saldoPendiente > 0
                          AND o.cuotas IS EMPTY
                        """,
                        BigDecimal.class
                )
                .setParameter("cuentaId", cuentaId)
                .setParameter("moneda", moneda)
                .getSingleResult();

        BigDecimal cuotas = entityManager.createQuery(
                        """
                        SELECT COALESCE(SUM(
                            CASE
                                WHEN o.movimientoOrigen.moneda = :moneda
                                    THEN c.saldoPendiente
                                WHEN c.importeValorizacionCierre IS NOT NULL
                                    THEN CASE
                                        WHEN c.saldoPendiente = c.importeOriginal
                                            THEN c.importeValorizacionCierre
                                        ELSE FUNCTION('ROUND',
                                            c.importeValorizacionCierre * c.saldoPendiente / c.importeOriginal,
                                            2
                                        )
                                    END
                                ELSE 0
                            END
                        ), 0)
                        FROM Obligacion o
                        JOIN o.cuotas c
                        WHERE o.movimientoOrigen.cuenta.id = :cuentaId
                          AND o.saldoLiquidacion IS NULL
                          AND c.saldoPendiente > 0
                        """,
                        BigDecimal.class
                )
                .setParameter("cuentaId", cuentaId)
                .setParameter("moneda", moneda)
                .getSingleResult();

        BigDecimal obligaciones = obligacionesLiquidadas
                .add(obligacionesSinCuotas)
                .add(cuotas);

        BigDecimal consumosSinObligacion = entityManager.createQuery(
                        """
                        SELECT COALESCE(SUM(m.importe), 0)
                        FROM Movimiento m
                        WHERE m.cuenta.id = :cuentaId
                          AND m.moneda = :moneda
                          AND m.tipoMovimiento = ar.com.agmilevecich.sofp.domain.TipoMovimiento.EGRESO
                          AND m.formaPago = ar.com.agmilevecich.sofp.domain.FormaPago.TARJETA_CREDITO
                          AND NOT EXISTS (
                              SELECT o.id
                              FROM Obligacion o
                              WHERE o.movimientoOrigen.id = m.id
                          )
                        """,
                        BigDecimal.class
                )
                .setParameter("cuentaId", cuentaId)
                .setParameter("moneda", moneda)
                .getSingleResult();

        return obligaciones.add(consumosSinObligacion);
    }
}
