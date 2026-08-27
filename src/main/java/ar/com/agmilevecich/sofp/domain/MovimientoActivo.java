package ar.com.agmilevecich.sofp.domain;

import ar.com.agmilevecich.sofp.util.Validaciones;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "movimientos_activos")
public class MovimientoActivo extends EntidadAuditable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "activo_id", nullable = false)
    private Activo activo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoMovimientoActivo tipoMovimiento;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal cantidad;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal precioUnitario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operacion_financiera_id")
    private OperacionFinanciera operacionFinanciera;

    protected MovimientoActivo() {
    }

    public MovimientoActivo(
            Activo activo,
            TipoMovimientoActivo tipoMovimiento,
            BigDecimal cantidad,
            BigDecimal precioUnitario) {

        this.activo = Objects.requireNonNull(
                activo,
                "El activo es obligatorio"
        );

        this.tipoMovimiento = Objects.requireNonNull(
                tipoMovimiento,
                "El tipo de movimiento es obligatorio"
        );

        this.cantidad = Validaciones.importePositivo(
                cantidad,
                "La cantidad es obligatoria"
        );

        this.precioUnitario = Validaciones.importePositivo(
                precioUnitario,
                "El precio unitario es obligatorio"
        );
    }

    public Activo getActivo() {
        return activo;
    }

    public TipoMovimientoActivo getTipoMovimiento() {
        return tipoMovimiento;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public OperacionFinanciera getOperacionFinanciera() {
        return operacionFinanciera;
    }

    public BigDecimal getVariacionCantidad() {
        if (tipoMovimiento == TipoMovimientoActivo.VENTA) {
            return cantidad.negate();
        }
        return cantidad;
    }

    public void asociarOperacionFinanciera(
            OperacionFinanciera operacionFinanciera) {

        Objects.requireNonNull(
                operacionFinanciera,
                "La operación financiera es obligatoria"
        );

        if (this.operacionFinanciera != null
                && this.operacionFinanciera != operacionFinanciera) {
            throw new IllegalStateException(
                    "El movimiento de activo ya pertenece a otra operación financiera"
            );
        }

        this.operacionFinanciera = operacionFinanciera;
    }

    public void cambiarTipoMovimiento(
            TipoMovimientoActivo tipoMovimiento) {

        this.tipoMovimiento = Objects.requireNonNull(
                tipoMovimiento,
                "El tipo de movimiento es obligatorio"
        );
    }

    public void cambiarCantidad(BigDecimal cantidad) {

        this.cantidad = Validaciones.importePositivo(
                cantidad,
                "La cantidad es obligatoria"
        );
    }

    public void cambiarPrecioUnitario(BigDecimal precioUnitario) {

        this.precioUnitario = Validaciones.importePositivo(
                precioUnitario,
                "El precio unitario es obligatorio"
        );
    }
}
