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

    @Column(name = "saldo_pendiente", nullable = false, precision = 19, scale = 2)
    private BigDecimal saldoPendiente;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    protected CuotaRefinanciacion() {}

    public CuotaRefinanciacion(Refinanciacion refinanciacion,
                               int numero,
                               BigDecimal importeOriginal,
                               LocalDate fechaVencimiento) {
        this.refinanciacion = Objects.requireNonNull(refinanciacion, "La refinanciación es obligatoria");
        if (numero < 1) throw new IllegalArgumentException("El número de cuota debe ser positivo");
        this.numero = numero;
        this.importeOriginal = Validaciones.importePositivo(importeOriginal, "El importe de la cuota es obligatorio");
        this.saldoPendiente = this.importeOriginal;
        this.fechaVencimiento = Objects.requireNonNull(fechaVencimiento, "La fecha de vencimiento es obligatoria");
    }

    public Refinanciacion getRefinanciacion() { return refinanciacion; }
    public int getNumero() { return numero; }
    public BigDecimal getImporteOriginal() { return importeOriginal; }
    public BigDecimal getSaldoPendiente() { return saldoPendiente; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }

    public void registrarPago(BigDecimal importe) {
        BigDecimal pago = Validaciones.importePositivo(importe, "El importe del pago es obligatorio");
        if (pago.compareTo(saldoPendiente) > 0) {
            throw new IllegalArgumentException("El pago no puede superar el saldo de la cuota");
        }
        saldoPendiente = saldoPendiente.subtract(pago);
    }
}
