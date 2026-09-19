package ar.com.agmilevecich.sofp.domain;

import ar.com.agmilevecich.sofp.util.Validaciones;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Objects;

@Entity
@Table(name = "cuentas")
public class Cuenta extends EntidadAuditable {
    @Column(nullable = false, length = 100) private String nombre;
    @Column(name = "identificador_externo", length = 150) private String identificadorExterno;
    @Enumerated(EnumType.STRING) @Column(name = "tipo_cuenta", nullable = false, length = 30) private TipoCuenta tipoCuenta;
    @Column(nullable = false) private boolean activa;
    @Column(name = "limite_credito", precision = 19, scale = 2) private BigDecimal limiteCredito;
    @Column(name = "dia_cierre") private Integer diaCierre;
    @Column(name = "dia_vencimiento") private Integer diaVencimiento;
    @Column(name = "dias_gracia") private Integer diasGracia = 0;
    @Column(name = "porcentaje_pago_minimo", precision = 5, scale = 2) private BigDecimal porcentajePagoMinimo = BigDecimal.ZERO;
    @Column(name = "importe_minimo_pago", precision = 19, scale = 2) private BigDecimal importeMinimoPago = BigDecimal.ZERO;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "perfil_financiero_id", nullable = false) private PerfilFinanciero perfilFinanciero;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "institucion_financiera_id", nullable = false) private InstitucionFinanciera institucionFinanciera;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "moneda_id", nullable = false) private Moneda moneda;

    protected Cuenta() {}

    public Cuenta(String nombre, TipoCuenta tipoCuenta, PerfilFinanciero perfilFinanciero, InstitucionFinanciera institucionFinanciera, Moneda moneda) {
        this.nombre = Validaciones.textoObligatorio(nombre, "El nombre es obligatorio");
        this.tipoCuenta = Objects.requireNonNull(tipoCuenta, "El tipo de cuenta es obligatorio");
        this.perfilFinanciero = Objects.requireNonNull(perfilFinanciero, "El perfil financiero es obligatorio");
        this.institucionFinanciera = Objects.requireNonNull(institucionFinanciera, "La institución financiera es obligatoria");
        this.moneda = Objects.requireNonNull(moneda, "La moneda es obligatoria");
        this.activa = true;
    }

    public Cuenta(String nombre, PerfilFinanciero perfilFinanciero, InstitucionFinanciera institucionFinanciera, Moneda moneda, BigDecimal limiteCredito, Integer diaCierre, Integer diaVencimiento) {
        this(nombre, TipoCuenta.TARJETA_CREDITO, perfilFinanciero, institucionFinanciera, moneda);
        configurarDatosCredito(limiteCredito, diaCierre, diaVencimiento);
    }

    public String getNombre() { return nombre; }
    public String getIdentificadorExterno() { return identificadorExterno; }
    public TipoCuenta getTipoCuenta() { return tipoCuenta; }
    public boolean isActiva() { return activa; }
    public BigDecimal getLimiteCredito() { return limiteCredito; }
    public Integer getDiaCierre() { return diaCierre; }
    public Integer getDiaVencimiento() { return diaVencimiento; }
    public Integer getDiasGracia() { return diasGracia == null ? 0 : diasGracia; }
    public BigDecimal getPorcentajePagoMinimo() { return porcentajePagoMinimo == null ? BigDecimal.ZERO : porcentajePagoMinimo; }
    public BigDecimal getImporteMinimoPago() { return importeMinimoPago == null ? BigDecimal.ZERO.setScale(2) : importeMinimoPago; }
    public PerfilFinanciero getPerfilFinanciero() { return perfilFinanciero; }
    public InstitucionFinanciera getInstitucionFinanciera() { return institucionFinanciera; }
    public Moneda getMoneda() { return moneda; }

    public void renombrar(String nuevoNombre) { this.nombre = Validaciones.textoObligatorio(nuevoNombre, "El nombre es obligatorio"); }
    public void cambiarIdentificadorExterno(String identificadorExterno) { this.identificadorExterno = identificadorExterno; }
    public void cambiarTipoCuenta(TipoCuenta tipoCuenta) { this.tipoCuenta = Objects.requireNonNull(tipoCuenta, "El tipo de cuenta es obligatoria"); }
    public void cambiarInstitucionFinanciera(InstitucionFinanciera institucionFinanciera) { this.institucionFinanciera = Objects.requireNonNull(institucionFinanciera, "La institución financiera es obligatoria"); }
    public void cambiarMoneda(Moneda moneda) { this.moneda = Objects.requireNonNull(moneda, "La moneda es obligatoria"); }

    public void configurarDatosCredito(BigDecimal limiteCredito, Integer diaCierre, Integer diaVencimiento) {
        configurarDatosCredito(limiteCredito, diaCierre, diaVencimiento, 0, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public void configurarDatosCredito(BigDecimal limiteCredito,
                                       Integer diaCierre,
                                       Integer diaVencimiento,
                                       Integer diasGracia) {
        configurarDatosCredito(
                limiteCredito, diaCierre, diaVencimiento, diasGracia,
                BigDecimal.ZERO, BigDecimal.ZERO
        );
    }

    public void configurarDatosCredito(BigDecimal limiteCredito,
                                       Integer diaCierre,
                                       Integer diaVencimiento,
                                       Integer diasGracia,
                                       BigDecimal porcentajePagoMinimo,
                                       BigDecimal importeMinimoPago) {
        this.limiteCredito = Objects.requireNonNull(limiteCredito, "El límite de crédito es obligatorio");
        if (limiteCredito.signum() <= 0) throw new IllegalArgumentException("El límite de crédito debe ser positivo");
        this.diaCierre = validarDia(diaCierre, "El día de cierre es obligatorio");
        this.diaVencimiento = validarDia(diaVencimiento, "El día de vencimiento es obligatorio");
        this.diasGracia = validarDiasGracia(diasGracia);
        this.porcentajePagoMinimo = validarPorcentajePagoMinimo(porcentajePagoMinimo);
        this.importeMinimoPago = validarImporteMinimoPago(importeMinimoPago);
    }

    public BigDecimal calcularPagoMinimo(BigDecimal deuda) {
        Objects.requireNonNull(deuda, "La deuda es obligatoria");
        if (deuda.signum() < 0) {
            throw new IllegalArgumentException("La deuda no puede ser negativa");
        }
        if (deuda.signum() == 0) {
            return BigDecimal.ZERO.setScale(2);
        }
        BigDecimal porcentaje = deuda
                .multiply(getPorcentajePagoMinimo())
                .divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
        return porcentaje.max(getImporteMinimoPago()).min(deuda).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public CicloFacturacion calcularCicloFacturacion(LocalDate fechaConsumo) {
        validarConfiguracionCredito();
        Objects.requireNonNull(fechaConsumo, "La fecha de consumo es obligatoria");
        LocalDate cierreMesActual = fechaCierre(fechaConsumo.getYear(), fechaConsumo.getMonthValue());
        LocalDate fechaCierre;
        LocalDate fechaInicio;
        if (!fechaConsumo.isAfter(cierreMesActual)) {
            fechaCierre = cierreMesActual;
            LocalDate anterior = fechaConsumo.minusMonths(1);
            fechaInicio = fechaCierre(anterior.getYear(), anterior.getMonthValue()).plusDays(1);
        } else {
            LocalDate siguiente = fechaConsumo.plusMonths(1);
            fechaCierre = fechaCierre(siguiente.getYear(), siguiente.getMonthValue());
            fechaInicio = cierreMesActual.plusDays(1);
        }
        return new CicloFacturacion(fechaInicio, fechaCierre, calcularFechaVencimiento(fechaCierre));
    }

    private LocalDate calcularFechaVencimiento(LocalDate fechaCierre) {
        YearMonth mes = YearMonth.from(fechaCierre);
        if (diaVencimiento <= diaCierre || diaVencimiento > mes.lengthOfMonth()) mes = mes.plusMonths(1);
        LocalDate vencimiento = fechaDelMes(mes, diaVencimiento);
        if (vencimiento.getDayOfWeek() == DayOfWeek.SATURDAY) return vencimiento.plusDays(2);
        if (vencimiento.getDayOfWeek() == DayOfWeek.SUNDAY) return vencimiento.plusDays(1);
        return vencimiento;
    }

    private LocalDate fechaCierre(int year, int month) { return fechaDelMes(YearMonth.of(year, month), diaCierre); }
    private LocalDate fechaDelMes(YearMonth mes, int dia) { return mes.atDay(Math.min(dia, mes.lengthOfMonth())); }

    private void validarConfiguracionCredito() {
        Objects.requireNonNull(limiteCredito, "El límite de crédito es obligatorio");
        Objects.requireNonNull(diaCierre, "El día de cierre es obligatorio");
        Objects.requireNonNull(diaVencimiento, "El día de vencimiento es obligatorio");
    }

    public BigDecimal calcularCreditoDisponible(BigDecimal creditoUtilizado) {
        Objects.requireNonNull(limiteCredito, "El límite de crédito es obligatorio");
        Objects.requireNonNull(creditoUtilizado, "El crédito utilizado es obligatorio");
        if (creditoUtilizado.signum() < 0) throw new IllegalArgumentException("El crédito utilizado no puede ser negativo");
        return limiteCredito.subtract(creditoUtilizado);
    }

    private int validarDia(Integer dia, String mensaje) {
        Objects.requireNonNull(dia, mensaje);
        if (dia < 1 || dia > 31) throw new IllegalArgumentException("El día debe estar entre 1 y 31");
        return dia;
    }

    private BigDecimal validarPorcentajePagoMinimo(BigDecimal porcentaje) {
        Objects.requireNonNull(porcentaje, "El porcentaje de pago mínimo es obligatorio");
        if (porcentaje.signum() < 0 || porcentaje.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("El porcentaje de pago mínimo debe estar entre 0 y 100");
        }
        return porcentaje.setScale(2);
    }

    private BigDecimal validarImporteMinimoPago(BigDecimal importe) {
        Objects.requireNonNull(importe, "El importe mínimo de pago es obligatorio");
        if (importe.signum() < 0) {
            throw new IllegalArgumentException("El importe mínimo de pago no puede ser negativo");
        }
        return importe.setScale(2);
    }

    private int validarDiasGracia(Integer diasGracia) {
        Objects.requireNonNull(diasGracia, "Los días de gracia son obligatorios");
        if (diasGracia < 0) throw new IllegalArgumentException("Los días de gracia no pueden ser negativos");
        return diasGracia;
    }

    public void activar() { this.activa = true; }
    public void desactivar() { this.activa = false; }
}
