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

    @Column(name = "interes_pagado", nullable = false, precision = 19, scale = 2, columnDefinition = "DECIMAL(19,2) DEFAULT 0")
    private BigDecimal interesPagado;

    @Column(name = "capital_pagado", nullable = false, precision = 19, scale = 2, columnDefinition = "DECIMAL(19,2) DEFAULT 0")
    private BigDecimal capitalPagado;

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
        this.interesPagado = BigDecimal.ZERO.setScale(2);
        this.capitalPagado = BigDecimal.ZERO.setScale(2);
        this.fechaVencimiento = Objects.requireNonNull(fechaVencimiento, "La fecha de vencimiento es obligatoria");
    }

    public Refinanciacion getRefinanciacion() { return refinanciacion; }
    public int getNumero() { return numero; }
    public BigDecimal getImporteOriginal() { return importeOriginal; }
    public BigDecimal getInteres() { return interes; }
    public BigDecimal getCapitalAmortizado() { return capitalAmortizado; }
    public BigDecimal getSaldoPendiente() { return saldoPendiente; }
    public BigDecimal getInteresPagado() { return interesPagado; }
    public BigDecimal getCapitalPagado() { return capitalPagado; }
    public BigDecimal getInteresPendiente() { return interes.subtract(interesPagado).setScale(2); }
    public BigDecimal getCapitalPendiente() { return capitalAmortizado.subtract(capitalPagado).setScale(2); }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }

    public void registrarPago(BigDecimal importe) {
        BigDecimal pago = Validaciones.importePositivo(importe, "El importe del pago es obligatorio");
        if (pago.compareTo(saldoPendiente) > 0) {
            throw new IllegalArgumentException("El pago no puede superar el saldo de la cuota");
        }

        BigDecimal interesPendiente = getInteresPendiente();
        BigDecimal aplicarInteres = pago.min(interesPendiente);
        BigDecimal restante = pago.subtract(aplicarInteres);
        BigDecimal aplicarCapital = restante.min(getCapitalPendiente());

        interesPagado = interesPagado.add(aplicarInteres).setScale(2);
        capitalPagado = capitalPagado.add(aplicarCapital).setScale(2);
        saldoPendiente = saldoPendiente.subtract(pago).setScale(2);
    }

    public void revertirPago(BigDecimal importe) {
        BigDecimal monto = Validaciones.importePositivo(importe, "El importe de la reversión es obligatorio");
        BigDecimal pagado = importeOriginal.subtract(saldoPendiente);
        if (monto.compareTo(pagado) > 0) {
            throw new IllegalArgumentException("La reversión supera los pagos de la cuota");
        }

        BigDecimal restaurarCapital = monto.min(capitalPagado);
        BigDecimal restaurarInteres = monto.subtract(restaurarCapital);

        capitalPagado = capitalPagado.subtract(restaurarCapital).setScale(2);
        interesPagado = interesPagado.subtract(restaurarInteres).setScale(2);
        saldoPendiente = saldoPendiente.add(monto).setScale(2);
    }

    private BigDecimal validarNoNegativo(BigDecimal importe, String mensaje) {
        Objects.requireNonNull(importe, mensaje + " es obligatorio");
        if (importe.signum() < 0) {
            throw new IllegalArgumentException(mensaje + " no puede ser negativo");
        }
        return importe.setScale(2);
    }
}
