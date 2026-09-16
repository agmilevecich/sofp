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
}
