package ar.com.agmilevecich.sofp.domain;

import java.math.BigDecimal;
import java.math.MathContext;
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
    private BigDecimal costoAdquisicion;

    public PosicionActivo(Activo activo) {
        this.activo = Objects.requireNonNull(activo, "El activo no puede ser nulo");
        this.cantidad = BigDecimal.ZERO;
        this.costoAdquisicion = BigDecimal.ZERO;
    }

    public Activo getActivo() {
        return activo;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public BigDecimal getCostoAdquisicion() {
        return costoAdquisicion;
    }

    public BigDecimal getPrecioPromedio() {
        if (cantidad.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return costoAdquisicion.divide(cantidad, MathContext.DECIMAL128);
    }

    public void aplicarMovimiento(MovimientoActivo movimiento) {
        Objects.requireNonNull(movimiento, "El movimiento no puede ser nulo");

        if (!activo.equals(movimiento.getActivo())) {
            throw new IllegalArgumentException("El movimiento corresponde a otro activo");
        }

        BigDecimal variacionCantidad = movimiento.getVariacionCantidad();
        BigDecimal nuevaCantidad = cantidad.add(variacionCantidad);

        if (nuevaCantidad.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("La posición no puede ser negativa");
        }

        if (movimiento.getTipoMovimiento() == TipoMovimientoActivo.COMPRA) {
            costoAdquisicion = costoAdquisicion.add(
                    movimiento.getCantidad().multiply(movimiento.getPrecioUnitario())
            );
        } else {
            BigDecimal costoVenta = movimiento.getCantidad().multiply(getPrecioPromedio());
            costoAdquisicion = costoAdquisicion.subtract(costoVenta);
        }

        cantidad = nuevaCantidad;

        if (cantidad.compareTo(BigDecimal.ZERO) == 0) {
            costoAdquisicion = BigDecimal.ZERO;
        }
    }
}
