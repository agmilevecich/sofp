package ar.com.agmilevecich.sofp.domain;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Representa el detalle de composición de una posición valorizada dentro de una cartera.
 *
 * No es una entidad persistente. El detalle se deriva de una valorización
 * y de la participación de esa posición sobre el valor total de la cartera.
 */
public class DetalleComposicionCarteraActivo {

    private final ValorizacionPosicionActivo valorizacion;
    private final BigDecimal participacionPorcentual;

    public DetalleComposicionCarteraActivo(
            ValorizacionPosicionActivo valorizacion,
            BigDecimal participacionPorcentual) {
        this.valorizacion = Objects.requireNonNull(
                valorizacion, "La valorización no puede ser nula");
        this.participacionPorcentual = Objects.requireNonNull(
                participacionPorcentual,
                "La participación porcentual no puede ser nula");

        if (participacionPorcentual.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "La participación porcentual no puede ser negativa");
        }
    }

    public ValorizacionPosicionActivo getValorizacion() {
        return valorizacion;
    }

    public BigDecimal getParticipacionPorcentual() {
        return participacionPorcentual;
    }
}
