package ar.com.agmilevecich.sofp.persistence;

import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.Obligacion;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
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
}
