package ar.com.agmilevecich.sofp.domain;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Representa un movimiento de activo dentro del reporte de movimientos de una cartera.
 *
 * No es una entidad persistente. El importe se deriva de la cantidad y del precio
 * unitario registrados en el movimiento.
 */
public class DetalleMovimientoCarteraActivo {

    private final MovimientoActivo movimiento;
    private final BigDecimal importe;

    public DetalleMovimientoCarteraActivo(MovimientoActivo movimiento) {
        this.movimiento = Objects.requireNonNull(
                movimiento,
                "El movimiento de activo no puede ser nulo"
        );
        this.importe = movimiento.getCantidad()
                .multiply(movimiento.getPrecioUnitario());
    }

    public MovimientoActivo getMovimiento() {
        return movimiento;
    }

    public Activo getActivo() {
        return movimiento.getActivo();
    }

    public TipoMovimientoActivo getTipoMovimiento() {
        return movimiento.getTipoMovimiento();
    }

    public BigDecimal getCantidad() {
        return movimiento.getCantidad();
    }

    public BigDecimal getPrecioUnitario() {
        return movimiento.getPrecioUnitario();
    }

    public OperacionFinanciera getOperacionFinanciera() {
        return movimiento.getOperacionFinanciera();
    }

    public BigDecimal getImporte() {
        return importe;
    }
}
