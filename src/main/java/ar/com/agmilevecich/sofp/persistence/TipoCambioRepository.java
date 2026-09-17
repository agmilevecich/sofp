package ar.com.agmilevecich.sofp.persistence;

import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.TipoCambio;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class TipoCambioRepository {

    private final EntityManager entityManager;

    public TipoCambioRepository(EntityManager entityManager) {
        this.entityManager = Objects.requireNonNull(
                entityManager,
                "El EntityManager es obligatorio"
        );
    }

    public TipoCambio guardar(TipoCambio tipoCambio) {
        Objects.requireNonNull(tipoCambio, "El tipo de cambio es obligatorio");
        entityManager.persist(tipoCambio);
        return tipoCambio;
    }

    public Optional<TipoCambio> buscarPorMonedasYFecha(
            Moneda monedaOrigen,
            Moneda monedaDestino,
            LocalDate fecha
    ) {
        Objects.requireNonNull(
                monedaOrigen,
                "La moneda de origen es obligatoria"
        );
        Objects.requireNonNull(
                monedaDestino,
                "La moneda de destino es obligatoria"
        );
        Objects.requireNonNull(
                fecha,
                "La fecha es obligatoria"
        );

        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.plusDays(1).atStartOfDay();

        return entityManager.createQuery(
                        """
                        SELECT tc
                        FROM TipoCambio tc
                        WHERE tc.monedaOrigen = :monedaOrigen
                          AND tc.monedaDestino = :monedaDestino
                          AND tc.fechaHora >= :inicio
                          AND tc.fechaHora < :fin
                        ORDER BY tc.fechaHora DESC, tc.id DESC
                        """,
                        TipoCambio.class
                )
                .setParameter("monedaOrigen", monedaOrigen)
                .setParameter("monedaDestino", monedaDestino)
                .setParameter("inicio", inicio)
                .setParameter("fin", fin)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    /**
     * Busca la última cotización disponible hasta el instante indicado.
     * Permite representar la cotización vigente al momento de la cancelación,
     * incluyendo la última cotización del día hábil anterior cuando no hubo
     * una cotización posterior.
     */
    public Optional<TipoCambio> buscarPorMonedasYFechaHora(
            Moneda monedaOrigen,
            Moneda monedaDestino,
            LocalDateTime fechaHora
    ) {
        Objects.requireNonNull(
                monedaOrigen,
                "La moneda de origen es obligatoria"
        );
        Objects.requireNonNull(
                monedaDestino,
                "La moneda de destino es obligatoria"
        );
        Objects.requireNonNull(
                fechaHora,
                "La fecha y hora son obligatorias"
        );

        return entityManager.createQuery(
                        """
                        SELECT tc
                        FROM TipoCambio tc
                        WHERE tc.monedaOrigen = :monedaOrigen
                          AND tc.monedaDestino = :monedaDestino
                          AND tc.fechaHora <= :fechaHora
                        ORDER BY tc.fechaHora DESC, tc.id DESC
                        """,
                        TipoCambio.class
                )
                .setParameter("monedaOrigen", monedaOrigen)
                .setParameter("monedaDestino", monedaDestino)
                .setParameter("fechaHora", fechaHora)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }
}
