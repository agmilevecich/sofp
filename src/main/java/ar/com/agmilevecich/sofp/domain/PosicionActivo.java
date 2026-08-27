package ar.com.agmilevecich.sofp.domain;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Representa la cantidad de un activo que resulta de acumular
 * los movimientos de activo.
 *
 * No es una entidad persistente. La posición se deriva de los
 * movimientos registrados.
 */
public class PosicionActivo {

    private final Activo activo;
    private BigDecimal cantidad;

    public PosicionActivo(Activo activo) {
        this.activo = Objects.requireNonNull(activo, "El activo no puede ser nulo");
        this.cantidad = BigDecimal.ZERO;
    }

    public Activo getActivo() {
        return activo;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void aplicarMovimiento(MovimientoActivo movimiento) {
        Objects.requireNonNull(movimiento, "El movimiento no puede ser nulo");

        if (!activo.equals(movimiento.getActivo())) {
            throw new IllegalArgumentException("El movimiento corresponde a otro activo");
        }

        BigDecimal nuevaCantidad = cantidad.add(movimiento.getVariacionCantidad());

        if (nuevaCantidad.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("La posición no puede ser negativa");
        }

        cantidad = nuevaCantidad;
    }
}
