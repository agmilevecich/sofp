package ar.com.agmilevecich.sofp.domain;

import ar.com.agmilevecich.sofp.util.Validaciones;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "cuotas_refinanciacion")
public class CuotaRefinanciacion extends EntidadAuditable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "refinanciacion_id", nullable = false)
    private Refinanciacion refinanciacion;

    @Column(nullable = false)
    private int numero;

    @Column(name = "importe_original", nullable = false, precision = 19, scale = 2)
    private BigDecimal importeOriginal;

    @Column(name = "interes", nullable = false, precision = 19, scale = 2)
    private BigDecimal interes;

    @Column(name = "capital_amortizado", nullable = false, precision = 19, scale = 2)
    private BigDecimal capitalAmortizado;

    @Column(name = "saldo_pendiente", nullable = false, precision = 19, scale = 2)
    private BigDecimal saldoPendiente;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    protected CuotaRefinanciacion() {}

    public CuotaRefinanciacion(Refinanciacion refinanciacion,
                               int numero,
                               BigDecimal importeOriginal,
                               BigDecimal interes,
                               BigDecimal capitalAmortizado,
                               LocalDate fechaVencimiento) {
        this.refinanciacion = Objects.requireNonNull(refinanciacion, "La refinanciación es obligatoria");
        if (numero < 1) throw new IllegalArgumentException("El número de cuota debe ser positivo");
        this.numero = numero;
        this.importeOriginal = Validaciones.importePositivo(importeOriginal, "El importe de la cuota es obligatorio");
        this.interes = validarNoNegativo(interes, "El interés de la cuota");
        this.capitalAmortizado = validarNoNegativo(capitalAmortizado, "El capital amortizado de la cuota");
        if (this.interes.add(this.capitalAmortizado).compareTo(this.importeOriginal) != 0) {
            throw new IllegalArgumentException("El interés y el capital amortizado deben coincidir con el importe de la cuota");
        }
        this.saldoPendiente = this.importeOriginal;
        this.fechaVencimiento = Objects.requireNonNull(fechaVencimiento, "La fecha de vencimiento es obligatoria");
    }

    public Refinanciacion getRefinanciacion() { return refinanciacion; }
    public int getNumero() { return numero; }
    public BigDecimal getImporteOriginal() { return importeOriginal; }
    public BigDecimal getInteres() { return interes; }
    public BigDecimal getCapitalAmortizado() { return capitalAmortizado; }
    public BigDecimal getSaldoPendiente() { return saldoPendiente; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }

    public void registrarPago(BigDecimal importe) {
        BigDecimal pago = Validaciones.importePositivo(importe, "El importe del pago es obligatorio");
        if (pago.compareTo(saldoPendiente) > 0) {
            throw new IllegalArgumentException("El pago no puede superar el saldo de la cuota");
        }
        saldoPendiente = saldoPendiente.subtract(pago);
    }

    public void revertirPago(BigDecimal importe) {
        BigDecimal monto = Validaciones.importePositivo(importe, "El importe de la reversión es obligatorio");
        BigDecimal pagado = importeOriginal.subtract(saldoPendiente);
        if (monto.compareTo(pagado) > 0) {
            throw new IllegalArgumentException("La reversión supera los pagos de la cuota");
        }
        saldoPendiente = saldoPendiente.add(monto);
    }

    private BigDecimal validarNoNegativo(BigDecimal importe, String mensaje) {
        Objects.requireNonNull(importe, mensaje + " es obligatorio");
        if (importe.signum() < 0) {
            throw new IllegalArgumentException(mensaje + " no puede ser negativo");
        }
        return importe.setScale(2);
    }
}
