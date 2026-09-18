package ar.com.agmilevecich.sofp.domain;

import ar.com.agmilevecich.sofp.util.Validaciones;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "financiaciones")
public class Financiacion extends EntidadAuditable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "obligacion_id", nullable = false)
    private Obligacion obligacion;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "capital_original", nullable = false, precision = 19, scale = 2)
    private BigDecimal capitalOriginal;

    @Column(name = "saldo_capital", nullable = false, precision = 19, scale = 2)
    private BigDecimal saldoCapital;

    protected Financiacion() {}

    public Financiacion(Obligacion obligacion, LocalDate fechaInicio, BigDecimal capitalOriginal) {
        this.obligacion = Objects.requireNonNull(obligacion, "La obligación es obligatoria");
        this.fechaInicio = Objects.requireNonNull(fechaInicio, "La fecha de inicio es obligatoria");
        this.capitalOriginal = Validaciones.importePositivo(capitalOriginal, "El capital original es obligatorio");
        this.saldoCapital = this.capitalOriginal;
    }

    public Obligacion getObligacion() {
        return obligacion;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public BigDecimal getCapitalOriginal() {
        return capitalOriginal;
    }

    public BigDecimal getSaldoCapital() {
        return saldoCapital;
    }

    public void registrarPago(BigDecimal importe) {
        BigDecimal pago = Validaciones.importePositivo(importe, "El importe del pago es obligatorio");
        if (pago.compareTo(saldoCapital) > 0) {
            throw new IllegalArgumentException("El pago no puede superar el saldo de capital");
        }
        saldoCapital = saldoCapital.subtract(pago);
    }
}
