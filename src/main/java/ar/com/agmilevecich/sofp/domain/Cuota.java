package ar.com.agmilevecich.sofp.domain;

import ar.com.agmilevecich.sofp.util.Validaciones;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "cuotas")
public class Cuota extends EntidadAuditable {

    @Column(nullable = false)
    private int numero;

    @Column(name = "importe_original", nullable = false, precision = 19, scale = 2)
    private BigDecimal importeOriginal;

    @Column(name = "importe_valorizacion_cierre", precision = 19, scale = 2)
    private BigDecimal importeValorizacionCierre;

    @Column(name = "saldo_pendiente", nullable = false, precision = 19, scale = 2)
    private BigDecimal saldoPendiente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoObligacion estado;

    @Column(name = "fecha_inicio_ciclo", nullable = false)
    private LocalDate fechaInicioCiclo;

    @Column(name = "fecha_cierre_ciclo", nullable = false)
    private LocalDate fechaCierreCiclo;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_cambio_cierre_id")
    private TipoCambio tipoCambioCierre;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "obligacion_id", nullable = false)
    private Obligacion obligacion;

    protected Cuota() {
    }

    Cuota(Obligacion obligacion, int numero, BigDecimal importe,
          CicloFacturacion cicloFacturacion) {
        this.obligacion = Objects.requireNonNull(obligacion, "La obligación es obligatoria");
        if (numero < 1) {
            throw new IllegalArgumentException("El número de cuota debe ser positivo");
        }
        this.numero = numero;
        this.importeOriginal = Validaciones.importePositivo(importe, "El importe de la cuota es obligatorio");
        this.saldoPendiente = this.importeOriginal;
        this.estado = EstadoObligacion.PENDIENTE;
        this.fechaInicioCiclo = Objects.requireNonNull(cicloFacturacion, "El ciclo de facturación es obligatorio").getFechaInicio();
        this.fechaCierreCiclo = cicloFacturacion.getFechaCierre();
        this.fechaVencimiento = cicloFacturacion.getFechaVencimiento();
    }

    public int getNumero() {
        return numero;
    }

    public BigDecimal getImporteOriginal() {
        return importeOriginal;
    }

    public BigDecimal getImporteValorizacionCierre() {
        return importeValorizacionCierre;
    }

    public BigDecimal getSaldoPendiente() {
        return saldoPendiente;
    }

    public EstadoObligacion getEstado() {
        return estado;
    }

    public LocalDate getFechaInicioCiclo() {
        return fechaInicioCiclo;
    }

    public LocalDate getFechaCierreCiclo() {
        return fechaCierreCiclo;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public TipoCambio getTipoCambioCierre() {
        return tipoCambioCierre;
    }

    public Obligacion getObligacion() {
        return obligacion;
    }

    public void valorarCierre(TipoCambio tipoCambio) {
        Objects.requireNonNull(tipoCambio, "El tipo de cambio es obligatorio");
        if (importeValorizacionCierre != null) {
            throw new IllegalStateException("La cuota ya tiene una valorización de cierre");
        }
        if (obligacion.getMonedaOriginal().equals(obligacion.getMonedaLiquidacion())) {
            throw new IllegalArgumentException("La cuota no requiere valorización de cierre mediante tipo de cambio");
        }
        if (!obligacion.getMonedaOriginal().equals(tipoCambio.getMonedaOrigen())) {
            throw new IllegalArgumentException("La moneda de origen del tipo de cambio no coincide con la obligación");
        }
        if (!obligacion.getMonedaLiquidacion().equals(tipoCambio.getMonedaDestino())) {
            throw new IllegalArgumentException("La moneda de destino del tipo de cambio no coincide con la obligación");
        }
        this.tipoCambioCierre = tipoCambio;
        this.importeValorizacionCierre = tipoCambio.convertir(importeOriginal);
    }

    public void revertirPago(BigDecimal importe) {
        BigDecimal monto = Validaciones.importePositivo(importe, "El importe de la reversión es obligatorio");
        BigDecimal pagado = importeOriginal.subtract(saldoPendiente);
        if (monto.compareTo(pagado) > 0) {
            throw new IllegalArgumentException("La reversión supera el importe pagado de la cuota");
        }
        saldoPendiente = saldoPendiente.add(monto);
        estado = saldoPendiente.compareTo(importeOriginal) == 0
                ? EstadoObligacion.PENDIENTE
                : EstadoObligacion.PARCIAL;
    }

    public void registrarPago(BigDecimal importe) {
        if (estado == EstadoObligacion.PAGADA) {
            throw new IllegalStateException("La cuota ya está pagada");
        }

        BigDecimal pago = Validaciones.importePositivo(importe, "El importe del pago es obligatorio");
        if (pago.compareTo(saldoPendiente) > 0) {
            throw new IllegalArgumentException("El pago no puede superar el saldo pendiente de la cuota");
        }

        saldoPendiente = saldoPendiente.subtract(pago);
        estado = saldoPendiente.signum() == 0 ? EstadoObligacion.PAGADA : EstadoObligacion.PARCIAL;
    }
}
