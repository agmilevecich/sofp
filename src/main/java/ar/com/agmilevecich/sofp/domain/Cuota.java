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

    public Obligacion getObligacion() {
        return obligacion;
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
