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
        Objects.requireNonNull(monedaOrigen, "La moneda de origen es obligatoria");
        Objects.requireNonNull(monedaDestino, "La moneda de destino es obligatoria");
        Objects.requireNonNull(fecha, "La fecha es obligatoria");

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

    public Optional<TipoCambio> buscarUltimaPorMonedas(
            Moneda monedaOrigen,
            Moneda monedaDestino) {
        Objects.requireNonNull(monedaOrigen, "La moneda de origen es obligatoria");
        Objects.requireNonNull(monedaDestino, "La moneda de destino es obligatoria");

        return entityManager.createQuery(
                        """
                        SELECT tc
                        FROM TipoCambio tc
                        WHERE tc.monedaOrigen = :monedaOrigen
                          AND tc.monedaDestino = :monedaDestino
                        ORDER BY tc.fechaHora DESC, tc.id DESC
                        """,
                        TipoCambio.class
                )
                .setParameter("monedaOrigen", monedaOrigen)
                .setParameter("monedaDestino", monedaDestino)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    /**
     * Busca la cotización aplicable a una cancelación de consumos en moneda
     * extranjera según la regla temporal del BCRA: en día hábil se toma una
     * cotización del mismo día hasta el momento de cancelación; en sábado o
     * domingo se toma la última cotización del viernes anterior.
     *
     * Los feriados no se infieren: para tratarlos como día inhábil se deberá
     * incorporar un calendario bancario explícito al dominio.
     */
    public Optional<TipoCambio> buscarPorMonedasYFechaHora(
            Moneda monedaOrigen,
            Moneda monedaDestino,
            LocalDateTime fechaHora
    ) {
        Objects.requireNonNull(monedaOrigen, "La moneda de origen es obligatoria");
        Objects.requireNonNull(monedaDestino, "La moneda de destino es obligatoria");
        Objects.requireNonNull(fechaHora, "La fecha y hora son obligatorias");

        LocalDate fecha = fechaHora.toLocalDate();
        LocalDate fechaCotizacion = switch (fecha.getDayOfWeek()) {
            case SATURDAY -> fecha.minusDays(1);
            case SUNDAY -> fecha.minusDays(2);
            default -> fecha;
        };

        LocalDateTime inicio = fechaCotizacion.atStartOfDay();
        LocalDateTime fin = fechaCotizacion.plusDays(1).atStartOfDay();
        LocalDateTime limite = fechaCotizacion.equals(fecha) ? fechaHora : fin;

        return entityManager.createQuery(
                        """
                        SELECT tc
                        FROM TipoCambio tc
                        WHERE tc.monedaOrigen = :monedaOrigen
                          AND tc.monedaDestino = :monedaDestino
                          AND tc.fechaHora >= :inicio
                          AND tc.fechaHora < :fin
                          AND tc.fechaHora <= :limite
                        ORDER BY tc.fechaHora DESC, tc.id DESC
                        """,
                        TipoCambio.class
                )
                .setParameter("monedaOrigen", monedaOrigen)
                .setParameter("monedaDestino", monedaDestino)
                .setParameter("inicio", inicio)
                .setParameter("fin", fin)
                .setParameter("limite", limite)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }
}
