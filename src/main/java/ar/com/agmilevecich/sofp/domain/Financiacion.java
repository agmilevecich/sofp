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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moneda_id")
    private Moneda moneda;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_cambio_valorizacion_id")
    private TipoCambio tipoCambioValorizacion;

    @Column(name = "importe_valorizacion", precision = 19, scale = 2)
    private BigDecimal importeValorizacion;

    @Column(name = "saldo_valorizacion", precision = 19, scale = 2)
    private BigDecimal saldoValorizacion;

    @Column(name = "origen_liquidacion")
    private Boolean origenLiquidacion;

    protected Financiacion() {}

    public Financiacion(Obligacion obligacion, LocalDate fechaInicio, BigDecimal capitalOriginal) {
        this(obligacion, fechaInicio, capitalOriginal, obligacion.getMonedaOriginal(), null, false);
    }

    public Financiacion(Obligacion obligacion,
                        LocalDate fechaInicio,
                        BigDecimal capitalOriginal,
                        Moneda moneda,
                        TipoCambio tipoCambioValorizacion,
                        boolean origenLiquidacion) {
        this.obligacion = Objects.requireNonNull(obligacion, "La obligación es obligatoria");
        this.fechaInicio = Objects.requireNonNull(fechaInicio, "La fecha de inicio es obligatoria");
        this.capitalOriginal = Validaciones.importePositivo(capitalOriginal, "El capital original es obligatorio");
        this.moneda = Objects.requireNonNull(moneda, "La moneda de la financiación es obligatoria");
        this.origenLiquidacion = origenLiquidacion;
        Moneda monedaReferencia = origenLiquidacion ? obligacion.getMonedaLiquidacion() : obligacion.getMonedaOriginal();
        if (!moneda.equals(monedaReferencia) && tipoCambioValorizacion == null) {
            throw new IllegalArgumentException("La financiación multidivisa requiere una cotización de valorización");
        }
        if (tipoCambioValorizacion != null) {
            if (!moneda.equals(tipoCambioValorizacion.getMonedaOrigen())
                    || !obligacion.getMonedaLiquidacion().equals(tipoCambioValorizacion.getMonedaDestino())) {
                throw new IllegalArgumentException("La cotización no corresponde a la financiación");
            }
        }
        this.tipoCambioValorizacion = tipoCambioValorizacion;
        this.importeValorizacion = valorizar(capitalOriginal);
        this.saldoValorizacion = this.importeValorizacion;
        this.saldoCapital = this.capitalOriginal;
    }

    private BigDecimal valorizar(BigDecimal capital) {
        if (tipoCambioValorizacion == null) {
            return capital.setScale(2);
        }
        return tipoCambioValorizacion.convertir(capital);
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

    public Moneda getMoneda() {
        return moneda != null ? moneda : obligacion.getMonedaOriginal();
    }

    public TipoCambio getTipoCambioValorizacion() {
        return tipoCambioValorizacion;
    }

    public BigDecimal getImporteValorizacion() {
        return importeValorizacion != null ? importeValorizacion : capitalOriginal;
    }

    public BigDecimal getSaldoValorizacion() {
        return saldoValorizacion != null ? saldoValorizacion : saldoCapital;
    }

    public boolean esSobreLiquidacion() {
        return Boolean.TRUE.equals(origenLiquidacion);
    }

    public boolean estaPendiente() {
        return saldoCapital.signum() > 0;
    }

    public boolean estaCancelada() {
        return saldoCapital.signum() == 0;
    }

    public void registrarPago(BigDecimal importe) {
        BigDecimal pago = Validaciones.importePositivo(importe, "El importe del pago es obligatorio");
        if (pago.compareTo(saldoCapital) > 0) {
            throw new IllegalArgumentException("El pago no puede superar el saldo de capital");
        }
        BigDecimal saldoAnterior = saldoCapital;
        saldoCapital = saldoCapital.subtract(pago);
        BigDecimal valorizacionPago = getSaldoValorizacion()
                .multiply(pago)
                .divide(saldoAnterior, 2, java.math.RoundingMode.HALF_UP);
        saldoValorizacion = getSaldoValorizacion().subtract(valorizacionPago);
        if (saldoCapital.signum() == 0) {
            saldoValorizacion = BigDecimal.ZERO.setScale(2);
        }
    }
}
