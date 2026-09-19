package ar.com.agmilevecich.sofp.domain;

import ar.com.agmilevecich.sofp.util.Validaciones;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    @Column(name = "fecha_ultimo_calculo_interes")
    private LocalDate fechaUltimoCalculoInteres;

    @Column(name = "fecha_ultimo_calculo_punitorio")
    private LocalDate fechaUltimoCalculoPunitorio;

    @OneToMany(mappedBy = "financiacion", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("fechaGeneracion ASC, id ASC")
    private List<CargoFinanciero> cargos = new ArrayList<>();

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
        this.fechaUltimoCalculoInteres = this.fechaInicio;
        this.fechaUltimoCalculoPunitorio = this.fechaInicio;
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

    public List<CargoFinanciero> getCargos() {
        return Collections.unmodifiableList(cargos);
    }

    public void agregarCargo(CargoFinanciero cargo) {
        Objects.requireNonNull(cargo, "El cargo es obligatorio");
        if (cargo.getFinanciacion() != this) {
            throw new IllegalArgumentException("El cargo debe pertenecer a esta financiación");
        }
        cargos.add(cargo);
    }

    public LocalDate getFechaUltimoCalculoInteres() {
        return fechaUltimoCalculoInteres != null ? fechaUltimoCalculoInteres : fechaInicio;
    }

    public LocalDate getFechaUltimoCalculoPunitorio() {
        return fechaUltimoCalculoPunitorio != null ? fechaUltimoCalculoPunitorio : fechaInicio;
    }

    public CargoFinanciero registrarPunitorio(BigDecimal importe,
                                              LocalDate fechaHasta,
                                              BigDecimal capitalBase,
                                              BigDecimal tasaAnual,
                                              int dias) {
        Objects.requireNonNull(fechaHasta, "La fecha hasta es obligatoria");
        if (fechaHasta.isBefore(getFechaUltimoCalculoPunitorio())) {
            throw new IllegalArgumentException("La fecha del punitorio no puede ser anterior al último cálculo");
        }
        CargoFinanciero cargo = new CargoFinanciero(
                obligacion,
                this,
                getMoneda(),
                TipoCargoFinanciero.INTERES_PUNITORIO,
                importe,
                fechaHasta,
                capitalBase,
                tasaAnual,
                dias,
                "Interés punitorio de financiación"
        );
        agregarCargo(cargo);
        fechaUltimoCalculoPunitorio = fechaHasta;
        return cargo;
    }

    public CargoFinanciero registrarInteres(BigDecimal importe,
                                            LocalDate fechaHasta,
                                            BigDecimal capitalBase,
                                            BigDecimal tasaAnual,
                                            int dias) {
        Objects.requireNonNull(fechaHasta, "La fecha hasta es obligatoria");
        if (fechaHasta.isBefore(getFechaUltimoCalculoInteres())) {
            throw new IllegalArgumentException("La fecha del interés no puede ser anterior al último cálculo");
        }
        CargoFinanciero cargo = new CargoFinanciero(
                obligacion,
                this,
                getMoneda(),
                TipoCargoFinanciero.INTERES_FINANCIERO,
                importe,
                fechaHasta,
                capitalBase,
                tasaAnual,
                dias,
                "Interés financiero de financiación"
        );
        agregarCargo(cargo);
        fechaUltimoCalculoInteres = fechaHasta;
        return cargo;
    }

    public BigDecimal getSaldoCargosPendiente() {
        return cargos.stream()
                .filter(CargoFinanciero::estaPendiente)
                .map(CargoFinanciero::getSaldoPendiente)
                .reduce(BigDecimal.ZERO.setScale(2), BigDecimal::add);
    }

    public boolean estaPendiente() {
        return saldoCapital.signum() > 0 || getSaldoCargosPendiente().signum() > 0;
    }

    public boolean estaCancelada() {
        return saldoCapital.signum() == 0 && getSaldoCargosPendiente().signum() == 0;
    }

    public BigDecimal registrarPago(BigDecimal importe) {
        BigDecimal pago = Validaciones.importePositivo(importe, "El importe del pago es obligatorio");
        BigDecimal saldoTotal = getSaldoTotalPendiente();
        if (pago.compareTo(saldoTotal) > 0) {
            throw new IllegalArgumentException("El pago no puede superar el saldo total de la financiación");
        }

        BigDecimal restante = pago;
        for (CargoFinanciero cargo : cargos) {
            if (restante.signum() == 0) {
                break;
            }
            BigDecimal pagoCargo = restante.min(cargo.getSaldoPendiente());
            if (pagoCargo.signum() > 0) {
                cargo.registrarPago(pagoCargo);
                restante = restante.subtract(pagoCargo);
            }
        }

        if (restante.signum() == 0) {
            return BigDecimal.ZERO.setScale(2);
        }

        BigDecimal saldoAnterior = saldoCapital;
        BigDecimal pagoCapital = restante.min(saldoCapital);
        saldoCapital = saldoCapital.subtract(pagoCapital);
        BigDecimal valorizacionPago = getSaldoValorizacion()
                .multiply(pagoCapital)
                .divide(saldoAnterior, 2, java.math.RoundingMode.HALF_UP);
        saldoValorizacion = getSaldoValorizacion().subtract(valorizacionPago);
        if (saldoCapital.signum() == 0) {
            saldoValorizacion = BigDecimal.ZERO.setScale(2);
        }
        return pagoCapital;
    }

    public void revertirPago(BigDecimal importe) {
        BigDecimal monto = Validaciones.importePositivo(importe, "El importe de la reversión es obligatorio");
        BigDecimal capitalPagado = capitalOriginal.subtract(saldoCapital);
        BigDecimal cargosPagados = cargos.stream()
                .map(cargo -> cargo.getImporteOriginal().subtract(cargo.getSaldoPendiente()))
                .reduce(BigDecimal.ZERO.setScale(2), BigDecimal::add);
        if (monto.compareTo(capitalPagado.add(cargosPagados)) > 0) {
            throw new IllegalArgumentException("La reversión supera los pagos de la financiación");
        }

        BigDecimal restante = monto.min(capitalPagado);
        if (restante.signum() > 0) {
            saldoCapital = saldoCapital.add(restante);
        }
        saldoValorizacion = valorizar(saldoCapital);

        restante = monto.subtract(restante);
        for (int i = cargos.size() - 1; i >= 0 && restante.signum() > 0; i--) {
            CargoFinanciero cargo = cargos.get(i);
            BigDecimal pagadoCargo = cargo.getImporteOriginal().subtract(cargo.getSaldoPendiente());
            BigDecimal restaurar = restante.min(pagadoCargo);
            if (restaurar.signum() > 0) {
                cargo.registrarReversion(restaurar);
                restante = restante.subtract(restaurar);
            }
        }
    }

    public BigDecimal getSaldoTotalPendiente() {
        return saldoCapital.add(getSaldoCargosPendiente());
    }
}
