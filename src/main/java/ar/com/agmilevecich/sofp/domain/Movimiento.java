package ar.com.agmilevecich.sofp.domain;

import ar.com.agmilevecich.sofp.util.Validaciones;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "movimientos")
public class Movimiento extends EntidadAuditable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cuenta_id", nullable = false)
    private Cuenta cuenta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "moneda_id", nullable = false)
    private Moneda moneda;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoMovimiento tipoMovimiento;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private FormaPago formaPago;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal importe;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false, length = 250)
    private String descripcion;

    @Column(length = 1000)
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operacion_financiera_id")
    private OperacionFinanciera operacionFinanciera;

    protected Movimiento() {
    }

    public Movimiento(
            Cuenta cuenta,
            Categoria categoria,
            TipoMovimiento tipoMovimiento,
            BigDecimal importe,
            LocalDateTime fechaHora,
            String descripcion) {
        this(cuenta, categoria, cuenta.getMoneda(), tipoMovimiento, importe, fechaHora, descripcion, null);
    }

    public Movimiento(
            Cuenta cuenta,
            Categoria categoria,
            TipoMovimiento tipoMovimiento,
            BigDecimal importe,
            LocalDateTime fechaHora,
            String descripcion,
            FormaPago formaPago) {
        this(cuenta, categoria, cuenta.getMoneda(), tipoMovimiento, importe, fechaHora, descripcion, formaPago);
    }

    /**
     * Constructor que permite indicar la moneda económica propia del movimiento.
     * Es necesario, entre otros casos, para compras en moneda extranjera con tarjeta.
     */
    public Movimiento(
            Cuenta cuenta,
            Categoria categoria,
            Moneda moneda,
            TipoMovimiento tipoMovimiento,
            BigDecimal importe,
            LocalDateTime fechaHora,
            String descripcion) {
        this(cuenta, categoria, moneda, tipoMovimiento, importe, fechaHora, descripcion, null);
    }

    public Movimiento(
            Cuenta cuenta,
            Categoria categoria,
            Moneda moneda,
            TipoMovimiento tipoMovimiento,
            BigDecimal importe,
            LocalDateTime fechaHora,
            String descripcion,
            FormaPago formaPago) {

        this.cuenta = Objects.requireNonNull(cuenta, "La cuenta es obligatoria");
        this.categoria = Objects.requireNonNull(categoria, "La categoría es obligatoria");
        this.moneda = Objects.requireNonNull(moneda, "La moneda es obligatoria");
        this.tipoMovimiento = Objects.requireNonNull(tipoMovimiento, "El tipo de movimiento es obligatorio");
        this.importe = Validaciones.importePositivo(importe, "El importe es obligatorio");
        this.fechaHora = Objects.requireNonNull(fechaHora, "La fecha y hora son obligatorias");
        this.descripcion = Validaciones.textoObligatorio(descripcion, "La descripción es obligatoria");
        this.formaPago = formaPago;
    }

    public Cuenta getCuenta() {
        return cuenta;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public Moneda getMoneda() {
        return moneda;
    }

    public TipoMovimiento getTipoMovimiento() {
        return tipoMovimiento;
    }

    public FormaPago getFormaPago() {
        return formaPago;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public OperacionFinanciera getOperacionFinanciera() {
        return operacionFinanciera;
    }

    public void asociarOperacionFinanciera(OperacionFinanciera operacionFinanciera) {
        Objects.requireNonNull(operacionFinanciera, "La operación financiera es obligatoria");
        if (this.operacionFinanciera != null && this.operacionFinanciera != operacionFinanciera) {
            throw new IllegalStateException("El movimiento ya pertenece a otra operación financiera");
        }
        this.operacionFinanciera = operacionFinanciera;
    }

    public void modificarTipoMovimiento(TipoMovimiento tipoMovimiento) {
        this.tipoMovimiento = Objects.requireNonNull(tipoMovimiento, "El tipo de movimiento es obligatorio");
    }

    public void cambiarImporte(BigDecimal importe) {
        this.importe = Validaciones.importePositivo(importe, "El importe es obligatorio");
    }

    public void cambiarFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = Objects.requireNonNull(fechaHora, "La fecha y hora son obligatorias");
    }

    public void cambiarDescripcion(String descripcion) {
        this.descripcion = Validaciones.textoObligatorio(descripcion, "La descripción es obligatoria");
    }

    public void cambiarObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public void cambiarCategoria(Categoria categoria) {
        this.categoria = Objects.requireNonNull(categoria, "La categoría es obligatoria");
    }

    public void cambiarFormaPago(FormaPago formaPago) {
        this.formaPago = formaPago;
    }
}