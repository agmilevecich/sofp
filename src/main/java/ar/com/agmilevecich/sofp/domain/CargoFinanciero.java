package ar.com.agmilevecich.sofp.domain;

import ar.com.agmilevecich.sofp.util.Validaciones;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "cargos_financieros")
public class CargoFinanciero extends EntidadAuditable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "obligacion_id", nullable = false)
    private Obligacion obligacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financiacion_id")
    private Financiacion financiacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "moneda_id", nullable = false)
    private Moneda moneda;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 30)
    private TipoCargoFinanciero tipo;

    @Column(name = "importe_original", nullable = false, precision = 19, scale = 2)
    private BigDecimal importeOriginal;

    @Column(name = "saldo_pendiente", nullable = false, precision = 19, scale = 2)
    private BigDecimal saldoPendiente;

    @Column(name = "fecha_generacion", nullable = false)
    private LocalDate fechaGeneracion;

    @Column(name = "capital_base", precision = 19, scale = 2)
    private BigDecimal capitalBase;

    @Column(name = "tasa_anual", precision = 9, scale = 4)
    private BigDecimal tasaAnual;

    @Column(name = "dias_calculo")
    private Integer diasCalculo;

    @Column(name = "descripcion", nullable = false, length = 250)
    private String descripcion;

    protected CargoFinanciero() {}

    public CargoFinanciero(Obligacion obligacion,
                           Financiacion financiacion,
                           Moneda moneda,
                           TipoCargoFinanciero tipo,
                           BigDecimal importe,
                           LocalDate fechaGeneracion,
                           BigDecimal capitalBase,
                           BigDecimal tasaAnual,
                           Integer diasCalculo,
                           String descripcion) {
        this.obligacion = Objects.requireNonNull(obligacion, "La obligación es obligatoria");
        this.financiacion = financiacion;
        this.moneda = Objects.requireNonNull(moneda, "La moneda es obligatoria");
        this.tipo = Objects.requireNonNull(tipo, "El tipo de cargo es obligatorio");
        this.importeOriginal = Validaciones.importePositivo(importe, "El importe del cargo es obligatorio");
        this.saldoPendiente = this.importeOriginal;
        this.fechaGeneracion = Objects.requireNonNull(fechaGeneracion, "La fecha de generación es obligatoria");
        this.capitalBase = capitalBase;
        this.tasaAnual = tasaAnual;
        this.diasCalculo = diasCalculo;
        this.descripcion = Objects.requireNonNull(descripcion, "La descripción es obligatoria");
        if (descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripción es obligatoria");
        }
    }

    public Obligacion getObligacion() { return obligacion; }
    public Financiacion getFinanciacion() { return financiacion; }
    public Moneda getMoneda() { return moneda; }
    public TipoCargoFinanciero getTipo() { return tipo; }
    public BigDecimal getImporteOriginal() { return importeOriginal; }
    public BigDecimal getSaldoPendiente() { return saldoPendiente; }
    public LocalDate getFechaGeneracion() { return fechaGeneracion; }
    public BigDecimal getCapitalBase() { return capitalBase; }
    public BigDecimal getTasaAnual() { return tasaAnual; }
    public Integer getDiasCalculo() { return diasCalculo; }
    public String getDescripcion() { return descripcion; }

    public boolean estaPendiente() { return saldoPendiente.signum() > 0; }

    public void registrarPago(BigDecimal importe) {
        BigDecimal pago = Validaciones.importePositivo(importe, "El importe del pago es obligatorio");
        if (pago.compareTo(saldoPendiente) > 0) {
            throw new IllegalArgumentException("El pago no puede superar el saldo del cargo");
        }
        saldoPendiente = saldoPendiente.subtract(pago);
    }
}
