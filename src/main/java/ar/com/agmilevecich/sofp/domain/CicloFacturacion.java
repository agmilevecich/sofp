package ar.com.agmilevecich.sofp.domain;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Representa un ciclo de facturación calculado para una tarjeta de crédito.
 * No es una entidad persistente: se obtiene a partir de la configuración de
 * la tarjeta y de la fecha del consumo.
 */
public final class CicloFacturacion {

    private final LocalDate fechaInicio;
    private final LocalDate fechaCierre;
    private final LocalDate fechaVencimiento;

    public CicloFacturacion(
            LocalDate fechaInicio,
            LocalDate fechaCierre,
            LocalDate fechaVencimiento) {
        this.fechaInicio = Objects.requireNonNull(fechaInicio, "La fecha de inicio es obligatoria");
        this.fechaCierre = Objects.requireNonNull(fechaCierre, "La fecha de cierre es obligatoria");
        this.fechaVencimiento = Objects.requireNonNull(fechaVencimiento, "La fecha de vencimiento es obligatoria");

        if (fechaInicio.isAfter(fechaCierre)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior al cierre");
        }

        if (!fechaVencimiento.isAfter(fechaCierre)) {
            throw new IllegalArgumentException("La fecha de vencimiento debe ser posterior al cierre");
        }
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaCierre() {
        return fechaCierre;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }
}
