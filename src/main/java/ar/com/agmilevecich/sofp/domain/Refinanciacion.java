package ar.com.agmilevecich.sofp.domain;

import ar.com.agmilevecich.sofp.util.Validaciones;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "refinanciaciones")
public class Refinanciacion extends EntidadAuditable {

    private static final int ESCALA_CALCULO = 12;
    private static final BigDecimal PERIODOS_ANUALES = new BigDecimal("12");

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "obligacion_origen_id", nullable = false)
    private Obligacion obligacionOrigen;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "moneda_id", nullable = false)
    private Moneda moneda;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "capital_original", nullable = false, precision = 19, scale = 2)
    private BigDecimal capitalOriginal;

    @Column(name = "interes_inicial", nullable = false, precision = 19, scale = 2)
    private BigDecimal interesInicial;

    @Column(name = "cargos_iniciales", nullable = false, precision = 19, scale = 2)
    private BigDecimal cargosIniciales;

    @Column(name = "total_plan", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalPlan;

    @Column(name = "saldo_plan", nullable = false, precision = 19, scale = 2)
    private BigDecimal saldoPlan;

    @Column(name = "tasa_anual", precision = 9, scale = 4)
    private BigDecimal tasaAnual;

    @Column(name = "cantidad_cuotas", nullable = false)
    private int cantidadCuotas;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoRefinanciacion estado;

    @OneToMany(mappedBy = "refinanciacion", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("numero ASC")
    private List<CuotaRefinanciacion> cuotas = new ArrayList<>();

    protected Refinanciacion() {}

    public Refinanciacion(Obligacion obligacionOrigen,
                          Moneda moneda,
                          LocalDate fechaInicio,
                          BigDecimal capitalOriginal,
                          BigDecimal interesInicial,
                          BigDecimal cargosIniciales,
                          BigDecimal tasaAnual,
                          int cantidadCuotas) {
        this.obligacionOrigen = Objects.requireNonNull(obligacionOrigen, "La obligación de origen es obligatoria");
        this.moneda = Objects.requireNonNull(moneda, "La moneda es obligatoria");
        this.fechaInicio = Objects.requireNonNull(fechaInicio, "La fecha de inicio es obligatoria");
        this.capitalOriginal = Validaciones.importePositivo(capitalOriginal, "El capital original es obligatorio");
        this.interesInicial = validarNoNegativo(interesInicial, "El interés inicial");
        this.cargosIniciales = validarNoNegativo(cargosIniciales, "Los cargos iniciales");
        this.totalPlan = this.capitalOriginal.add(this.interesInicial).add(this.cargosIniciales).setScale(2);
        this.saldoPlan = this.totalPlan;
        this.tasaAnual = validarTasaAnual(tasaAnual);
        if (cantidadCuotas < 1) {
            throw new IllegalArgumentException("La cantidad de cuotas debe ser positiva");
        }
        this.cantidadCuotas = cantidadCuotas;
        this.estado = EstadoRefinanciacion.ACTIVA;
    }

    public Obligacion getObligacionOrigen() { return obligacionOrigen; }
    public Moneda getMoneda() { return moneda; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public BigDecimal getCapitalOriginal() { return capitalOriginal; }
    public BigDecimal getInteresInicial() { return interesInicial; }
    public BigDecimal getCargosIniciales() { return cargosIniciales; }
    public BigDecimal getTotalPlan() { return totalPlan; }
    public BigDecimal getSaldoPlan() { return saldoPlan; }
    public BigDecimal getTasaAnual() { return tasaAnual; }
    public int getCantidadCuotas() { return cantidadCuotas; }
    public EstadoRefinanciacion getEstado() { return estado; }
    public List<CuotaRefinanciacion> getCuotas() { return Collections.unmodifiableList(cuotas); }

    public void generarCuotas() {
        if (!cuotas.isEmpty()) {
            throw new IllegalStateException("La refinanciación ya tiene cuotas");
        }
        if (tasaAnual == null) {
            throw new IllegalStateException("La TNA es obligatoria para generar cuotas");
        }

        BigDecimal tasaMensual = tasaAnual
                .divide(PERIODOS_ANUALES, ESCALA_CALCULO, RoundingMode.HALF_UP)
                .divide(new BigDecimal("100"), ESCALA_CALCULO, RoundingMode.HALF_UP);

        BigDecimal importeTeorico = calcularImporteCuota(totalPlan, tasaMensual, cantidadCuotas);
        BigDecimal importeCuota = importeTeorico.setScale(2, RoundingMode.DOWN);

        BigDecimal saldoCapital = totalPlan;
        for (int i = 1; i <= cantidadCuotas; i++) {
            BigDecimal interes = saldoCapital
                    .multiply(tasaMensual)
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal capitalAmortizado;
            BigDecimal importe;

            if (i == cantidadCuotas) {
                capitalAmortizado = saldoCapital;
                importe = capitalAmortizado.add(interes).setScale(2, RoundingMode.HALF_UP);
            } else {
                importe = importeCuota;
                capitalAmortizado = importe.subtract(interes).setScale(2, RoundingMode.HALF_UP);
            }

            LocalDate vencimiento = fechaInicio.plusMonths(i);
            cuotas.add(new CuotaRefinanciacion(
                    this,
                    i,
                    importe,
                    interes,
                    capitalAmortizado,
                    vencimiento
            ));

            saldoCapital = saldoCapital.subtract(capitalAmortizado).setScale(2, RoundingMode.HALF_UP);
        }

        // Una vez generadas las cuotas, el saldo del plan representa el importe
        // contractual pendiente de las cuotas, incluyendo el interés programado.
        saldoPlan = cuotas.stream()
                .map(CuotaRefinanciacion::getImporteOriginal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularImporteCuota(BigDecimal capital,
                                             BigDecimal tasaMensual,
                                             int cantidadCuotas) {
        if (tasaMensual.signum() == 0) {
            return capital.divide(
                    BigDecimal.valueOf(cantidadCuotas),
                    12,
                    RoundingMode.HALF_UP
            );
        }

        BigDecimal unoMasTasa = BigDecimal.ONE.add(tasaMensual);
        BigDecimal factor = unoMasTasa.pow(cantidadCuotas);
        return capital
                .multiply(tasaMensual)
                .divide(
                        BigDecimal.ONE.subtract(BigDecimal.ONE.divide(
                                factor,
                                ESCALA_CALCULO,
                                RoundingMode.HALF_UP
                        )),
                        ESCALA_CALCULO,
                        RoundingMode.HALF_UP
                );
    }

    public void registrarPago(BigDecimal importe) {
        validarCuotasGeneradas();
        if (estado == EstadoRefinanciacion.CANCELADA) {
            throw new IllegalStateException("La refinanciación ya está cancelada");
        }
        BigDecimal pago = Validaciones.importePositivo(importe, "El importe del pago es obligatorio");
        if (pago.compareTo(saldoPlan) > 0) {
            throw new IllegalArgumentException("El pago no puede superar el saldo de la refinanciación");
        }
        BigDecimal restante = pago;
        for (CuotaRefinanciacion cuota : cuotas) {
            if (restante.signum() == 0) break;
            BigDecimal pagoCuota = restante.min(cuota.getSaldoPendiente());
            if (pagoCuota.signum() > 0) {
                cuota.registrarPago(pagoCuota);
                restante = restante.subtract(pagoCuota);
            }
        }
        saldoPlan = saldoPlan.subtract(pago);
        if (saldoPlan.signum() == 0) {
            estado = EstadoRefinanciacion.CANCELADA;
        }
    }

    public void revertirPago(BigDecimal importe) {
        validarCuotasGeneradas();
        BigDecimal monto = Validaciones.importePositivo(importe, "El importe de la reversión es obligatorio");
        BigDecimal importePlanContractual = cuotas.stream()
                .map(CuotaRefinanciacion::getImporteOriginal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal pagado = importePlanContractual.subtract(saldoPlan);
        if (monto.compareTo(pagado) > 0) {
            throw new IllegalArgumentException("La reversión supera los pagos de la refinanciación");
        }

        BigDecimal restante = monto;
        for (int i = cuotas.size() - 1; i >= 0 && restante.signum() > 0; i--) {
            CuotaRefinanciacion cuota = cuotas.get(i);
            BigDecimal pagadoCuota = cuota.getImporteOriginal().subtract(cuota.getSaldoPendiente());
            BigDecimal restaurar = restante.min(pagadoCuota);
            if (restaurar.signum() > 0) {
                cuota.revertirPago(restaurar);
                restante = restante.subtract(restaurar);
            }
        }

        saldoPlan = saldoPlan.add(monto);
        if (estado == EstadoRefinanciacion.CANCELADA) {
            estado = EstadoRefinanciacion.ACTIVA;
        }
    }

    private void validarCuotasGeneradas() {
        if (cuotas.isEmpty()) {
            throw new IllegalStateException("La refinanciación no tiene cuotas generadas");
        }
    }

    private BigDecimal validarTasaAnual(BigDecimal tasa) {
        if (tasa == null) {
            return null;
        }
        if (tasa.signum() < 0) {
            throw new IllegalArgumentException("La tasa anual no puede ser negativa");
        }
        return tasa.setScale(4);
    }

    private BigDecimal validarNoNegativo(BigDecimal importe, String mensaje) {
        Objects.requireNonNull(importe, mensaje + " es obligatorio");
        if (importe.signum() < 0) {
            throw new IllegalArgumentException(mensaje + " no puede ser negativo");
        }
        return importe.setScale(2);
    }
}
