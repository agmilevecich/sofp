package ar.com.agmilevecich.sofp.domain;

import ar.com.agmilevecich.sofp.util.Validaciones;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "obligaciones")
public class Obligacion extends EntidadAuditable {
    @Column(name = "importe_original", nullable = false, precision = 19, scale = 2) private BigDecimal importeOriginal;
    @Column(name = "importe_valorizacion_cierre", precision = 19, scale = 2) private BigDecimal importeValorizacionCierre;
    @Column(name = "importe_liquidacion", precision = 19, scale = 2) private BigDecimal importeLiquidacion;
    @Column(name = "saldo_liquidacion", precision = 19, scale = 2) private BigDecimal saldoLiquidacion;
    @Column(name = "saldo_pendiente", nullable = false, precision = 19, scale = 2) private BigDecimal saldoPendiente;
    @Column(nullable = false, length = 20) @Enumerated(EnumType.STRING) private EstadoObligacion estado;
    @Column(name = "fecha_inicio_ciclo") private LocalDate fechaInicioCiclo;
    @Column(name = "fecha_cierre_ciclo") private LocalDate fechaCierreCiclo;
    @Column(name = "fecha_vencimiento") private LocalDate fechaVencimiento;
    @Column(name = "dias_gracia") private Integer diasGracia;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "moneda_original_id") private Moneda monedaOriginal;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "moneda_liquidacion_id") private Moneda monedaLiquidacion;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "tipo_cambio_cierre_id") private TipoCambio tipoCambioCierre;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "tipo_cambio_liquidacion_id") private TipoCambio tipoCambioLiquidacion;
    @OneToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "movimiento_origen_id", nullable = false, unique = true) private Movimiento movimientoOrigen;
    @OneToMany(mappedBy = "obligacion", cascade = CascadeType.ALL, orphanRemoval = true) @OrderBy("numero ASC") private List<Cuota> cuotas = new ArrayList<>();
    @OneToMany(mappedBy = "obligacion", cascade = CascadeType.ALL, orphanRemoval = true) @OrderBy("fechaInicio ASC") private List<Financiacion> financiaciones = new ArrayList<>();

    protected Obligacion() {}

    public Obligacion(Movimiento movimientoOrigen) {
        this.movimientoOrigen = Objects.requireNonNull(movimientoOrigen, "El movimiento de origen es obligatorio");
        if (movimientoOrigen.getTipoMovimiento() != TipoMovimiento.EGRESO) throw new IllegalArgumentException("El movimiento de origen debe ser un egreso");
        if (movimientoOrigen.getFormaPago() != FormaPago.TARJETA_CREDITO) throw new IllegalArgumentException("El movimiento de origen debe utilizar tarjeta de crédito");
        this.importeOriginal = Validaciones.importePositivo(movimientoOrigen.getImporte(), "El importe original es obligatorio");
        this.monedaOriginal = Objects.requireNonNull(movimientoOrigen.getMoneda(), "La moneda original es obligatoria");
        this.monedaLiquidacion = Objects.requireNonNull(movimientoOrigen.getCuenta().getMoneda(), "La moneda de liquidación es obligatoria");
        CicloFacturacion ciclo = movimientoOrigen.getCuenta().calcularCicloFacturacion(movimientoOrigen.getFechaHora().toLocalDate());
        this.fechaInicioCiclo = ciclo.getFechaInicio();
        this.fechaCierreCiclo = ciclo.getFechaCierre();
        this.fechaVencimiento = ciclo.getFechaVencimiento();
        this.diasGracia = movimientoOrigen.getCuenta().getDiasGracia();
        this.saldoPendiente = this.importeOriginal;
        this.estado = EstadoObligacion.PENDIENTE;
    }

    public BigDecimal getImporteOriginal() { return importeOriginal; }
    public BigDecimal getImporteValorizacionCierre() { return importeValorizacionCierre; }
    public BigDecimal getImporteLiquidacion() { return importeLiquidacion; }
    public BigDecimal getSaldoLiquidacion() { return saldoLiquidacion; }
    public BigDecimal getSaldoPendiente() { return saldoPendiente; }
    public EstadoObligacion getEstado() { return estado; }
    public Movimiento getMovimientoOrigen() { return movimientoOrigen; }
    public List<Cuota> getCuotas() { return Collections.unmodifiableList(cuotas); }
    public List<Financiacion> getFinanciaciones() { return Collections.unmodifiableList(financiaciones); }

    public void agregarFinanciacion(Financiacion financiacion) {
        Objects.requireNonNull(financiacion, "La financiación es obligatoria");
        if (financiacion.getObligacion() != this) throw new IllegalArgumentException("La financiación debe pertenecer a esta obligación");
        financiaciones.add(financiacion);
    }

    public Financiacion crearFinanciacion(LocalDate fechaInicio, BigDecimal capital) {
        return crearFinanciacion(fechaInicio, capital, getMonedaOriginal(), null, false);
    }

    public Financiacion crearFinanciacion(LocalDate fechaInicio,
                                          BigDecimal capital,
                                          Moneda moneda,
                                          TipoCambio tipoCambioValorizacion,
                                          boolean origenLiquidacion) {
        if (origenLiquidacion) {
            if (saldoLiquidacion == null) {
                throw new IllegalStateException("La financiación sobre liquidación requiere una liquidación");
            }
            if (capital.compareTo(saldoLiquidacion) > 0) {
                throw new IllegalArgumentException("El capital financiado no puede superar el saldo de liquidación");
            }
        }
        Financiacion financiacion = new Financiacion(
                this, fechaInicio, capital, moneda, tipoCambioValorizacion, origenLiquidacion
        );
        if (origenLiquidacion) {
            saldoLiquidacion = saldoLiquidacion.subtract(capital);
        }
        agregarFinanciacion(financiacion);
        return financiacion;
    }

    public BigDecimal getSaldoFinanciadoPendiente() {
        return financiaciones.stream()
                .filter(Financiacion::estaPendiente)
                .map(Financiacion::getSaldoCapital)
                .reduce(BigDecimal.ZERO.setScale(2), BigDecimal::add);
    }

    public BigDecimal getSaldoNoFinanciadoParaLiquidacion() {
        BigDecimal saldo = saldoPendiente.subtract(getSaldoFinanciadoPendiente());
        return saldo.signum() < 0 ? BigDecimal.ZERO.setScale(2) : saldo;
    }

    private boolean estaCompletamentePagada() {
        if (financiaciones.stream().anyMatch(Financiacion::estaPendiente)) {
            return false;
        }
        return saldoLiquidacion != null
                ? saldoLiquidacion.signum() == 0
                : saldoPendiente.signum() == 0;
    }

    public BigDecimal getSaldoPendienteDelCiclo(LocalDate fechaCierre) {
        Objects.requireNonNull(fechaCierre, "La fecha de cierre es obligatoria");
        if (cuotas.isEmpty()) {
            return fechaCierre.equals(fechaCierreCiclo) ? saldoPendiente : BigDecimal.ZERO.setScale(2);
        }
        return cuotas.stream()
                .filter(cuota -> fechaCierre.equals(cuota.getFechaCierreCiclo()))
                .map(Cuota::getSaldoPendiente)
                .reduce(BigDecimal.ZERO.setScale(2), BigDecimal::add);
    }

    public LocalDate getFechaInicioFinanciacion(LocalDate fechaCierre) {
        Objects.requireNonNull(fechaCierre, "La fecha de cierre es obligatoria");
        if (cuotas.isEmpty()) {
            if (!fechaCierre.equals(fechaCierreCiclo)) {
                throw new IllegalArgumentException("La fecha de cierre no corresponde al ciclo de la obligación");
            }
            return fechaVencimiento.plusDays(1);
        }
        return cuotas.stream()
                .filter(cuota -> fechaCierre.equals(cuota.getFechaCierreCiclo()))
                .findFirst()
                .map(cuota -> cuota.getFechaVencimiento().plusDays(1))
                .orElseThrow(() -> new IllegalArgumentException("La fecha de cierre no corresponde a una cuota de la obligación"));
    }

    public Moneda getMonedaOriginal() { return monedaOriginal != null ? monedaOriginal : movimientoOrigen.getMoneda(); }
    public Moneda getMonedaLiquidacion() { return monedaLiquidacion != null ? monedaLiquidacion : movimientoOrigen.getCuenta().getMoneda(); }
    public TipoCambio getTipoCambioCierre() { return tipoCambioCierre; }
    public TipoCambio getTipoCambioLiquidacion() { return tipoCambioLiquidacion; }
    public Moneda getMoneda() { return getMonedaOriginal(); }
    public LocalDateTime getFechaOrigen() { return movimientoOrigen.getFechaHora(); }

    public CicloFacturacion getCicloFacturacion() {
        if (fechaInicioCiclo == null || fechaCierreCiclo == null || fechaVencimiento == null) {
            return movimientoOrigen.getCuenta().calcularCicloFacturacion(movimientoOrigen.getFechaHora().toLocalDate());
        }
        return new CicloFacturacion(fechaInicioCiclo, fechaCierreCiclo, fechaVencimiento);
    }

    public LocalDate getFechaLimitePago() {
        int gracia = diasGracia == null ? 0 : diasGracia;
        LocalDate vencimiento = !cuotas.isEmpty()
                ? cuotas.stream().filter(c -> c.getSaldoPendiente().signum() > 0).findFirst().map(Cuota::getFechaVencimiento).orElse(getCicloFacturacion().getFechaVencimiento())
                : getCicloFacturacion().getFechaVencimiento();
        return vencimiento.plusDays(gracia);
    }

    public BigDecimal getDeudaParaPagoMinimo() {
        if (saldoLiquidacion != null) {
            BigDecimal deuda = saldoLiquidacion;
            for (Financiacion financiacion : financiaciones) {
                if (financiacion.estaPendiente()) {
                    deuda = deuda.add(financiacion.getSaldoValorizacion());
                }
            }
            return deuda.setScale(2, RoundingMode.HALF_UP);
        }

        if (getMonedaOriginal().equals(getMonedaLiquidacion())) {
            return saldoPendiente.setScale(2, RoundingMode.HALF_UP);
        }

        if (cuotas.isEmpty()) {
            if (importeValorizacionCierre == null) {
                throw new IllegalStateException("No existe valorización de cierre para calcular el pago mínimo multidivisa");
            }
            return importeValorizacionCierre
                    .multiply(saldoPendiente)
                    .divide(importeOriginal, 2, RoundingMode.HALF_UP);
        }

        return cuotas.stream()
                .filter(cuota -> cuota.getSaldoPendiente().signum() > 0)
                .map(cuota -> {
                    if (cuota.getImporteValorizacionCierre() == null) {
                        throw new IllegalStateException("No existe valorización de cierre para calcular el pago mínimo multidivisa");
                    }
                    return cuota.getImporteValorizacionCierre()
                            .multiply(cuota.getSaldoPendiente())
                            .divide(cuota.getImporteOriginal(), 2, RoundingMode.HALF_UP);
                })
                .reduce(BigDecimal.ZERO.setScale(2), BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularPagoMinimo() {
        return movimientoOrigen.getCuenta().calcularPagoMinimo(getDeudaParaPagoMinimo());
    }

    public boolean cumplePagoMinimo(BigDecimal importePagado) {
        Objects.requireNonNull(importePagado, "El importe pagado es obligatorio");
        if (importePagado.signum() < 0) {
            throw new IllegalArgumentException("El importe pagado no puede ser negativo");
        }
        return importePagado.compareTo(calcularPagoMinimo()) >= 0;
    }

    public void marcarRefinanciada() {
        if (estado == EstadoObligacion.PAGADA) {
            throw new IllegalStateException("La obligación ya está pagada");
        }
        if (estado == EstadoObligacion.REFINANCIADA) {
            throw new IllegalStateException("La obligación ya está refinanciada");
        }
        estado = EstadoObligacion.REFINANCIADA;
    }

    public void anular() {
        if (estado == EstadoObligacion.ANULADA) {
            throw new IllegalStateException("La obligación ya está anulada");
        }
        if (saldoPendiente.compareTo(importeOriginal) != 0
                || (saldoLiquidacion != null && saldoLiquidacion.compareTo(importeLiquidacion) != 0)
                || !financiaciones.isEmpty()) {
            throw new IllegalStateException(
                    "La obligación ya tiene pagos, liquidación o financiación y requiere una reversión compensatoria"
            );
        }
        estado = EstadoObligacion.ANULADA;
        saldoPendiente = BigDecimal.ZERO.setScale(2);
        if (saldoLiquidacion != null) {
            saldoLiquidacion = BigDecimal.ZERO.setScale(2);
        }
    }

    public boolean estaEnMora(LocalDate fechaPago) {
        Objects.requireNonNull(fechaPago, "La fecha de pago es obligatoria");
        return fechaPago.isAfter(getFechaLimitePago());
    }

    public void generarCuotas(int cantidad) {
        if (cantidad < 1) throw new IllegalArgumentException("La cantidad de cuotas debe ser positiva");
        if (!cuotas.isEmpty()) throw new IllegalStateException("La obligación ya tiene cuotas generadas");
        BigDecimal importeBase = importeOriginal.divide(BigDecimal.valueOf(cantidad), 2, RoundingMode.DOWN);
        BigDecimal importeAcumulado = BigDecimal.ZERO;
        CicloFacturacion ciclo = getCicloFacturacion();
        for (int numero = 1; numero <= cantidad; numero++) {
            BigDecimal importeCuota = numero == cantidad ? importeOriginal.subtract(importeAcumulado) : importeBase;
            cuotas.add(new Cuota(this, numero, importeCuota, ciclo));
            importeAcumulado = importeAcumulado.add(importeCuota);
            if (numero < cantidad) ciclo = movimientoOrigen.getCuenta().calcularCicloFacturacion(ciclo.getFechaCierre().plusDays(1));
        }
    }

    public void valorarCierre(TipoCambio tipoCambio) {
        Objects.requireNonNull(tipoCambio, "El tipo de cambio es obligatorio");
        if (importeValorizacionCierre != null) throw new IllegalStateException("La obligación ya tiene una valorización de cierre");
        if (getMonedaOriginal().equals(getMonedaLiquidacion())) throw new IllegalArgumentException("La obligación no requiere valorización de cierre mediante tipo de cambio");
        if (!getMonedaOriginal().equals(tipoCambio.getMonedaOrigen())) throw new IllegalArgumentException("La moneda de origen del tipo de cambio no coincide con la obligación");
        if (!getMonedaLiquidacion().equals(tipoCambio.getMonedaDestino())) throw new IllegalArgumentException("La moneda de destino del tipo de cambio no coincide con la obligación");
        this.tipoCambioCierre = tipoCambio;
        this.importeValorizacionCierre = tipoCambio.convertir(importeOriginal);
    }

    public void liquidar(TipoCambio tipoCambio) {
        Objects.requireNonNull(tipoCambio, "El tipo de cambio es obligatorio");
        if (importeLiquidacion != null) throw new IllegalStateException("La obligación ya tiene una liquidación");
        BigDecimal saldoParaLiquidar = getSaldoNoFinanciadoParaLiquidacion();
        if (saldoParaLiquidar.signum() == 0) throw new IllegalStateException("No existe saldo no financiado para liquidar");
        if (!getMonedaOriginal().equals(tipoCambio.getMonedaOrigen())) throw new IllegalArgumentException("La moneda de origen del tipo de cambio no coincide con la obligación");
        if (!getMonedaLiquidacion().equals(tipoCambio.getMonedaDestino())) throw new IllegalArgumentException("La moneda de destino del tipo de cambio no coincide con la obligación");
        this.tipoCambioLiquidacion = tipoCambio;
        this.importeLiquidacion = tipoCambio.convertir(saldoParaLiquidar);
        this.saldoLiquidacion = this.importeLiquidacion;
    }

    public void registrarPagoFinanciacion(Financiacion financiacion, BigDecimal importe) {
        Objects.requireNonNull(financiacion, "La financiación es obligatoria");
        if (financiacion.getObligacion() != this) {
            throw new IllegalArgumentException("La financiación debe pertenecer a esta obligación");
        }

        BigDecimal pago = Validaciones.importePositivo(importe, "El importe del pago es obligatorio");
        if (pago.compareTo(financiacion.getSaldoTotalPendiente()) > 0) {
            throw new IllegalArgumentException("El pago no puede superar el saldo total de la financiación");
        }

        if (financiacion.esSobreLiquidacion()) {
            if (!financiacion.getMoneda().equals(getMonedaLiquidacion())) {
                throw new IllegalArgumentException("La financiación sobre liquidación debe utilizar la moneda de liquidación");
            }
        }

        BigDecimal capitalPagado = financiacion.registrarPago(pago);
        if (!financiacion.esSobreLiquidacion() && capitalPagado.signum() > 0) {
            if (capitalPagado.compareTo(saldoPendiente) > 0) {
                throw new IllegalArgumentException("El capital pagado supera el saldo pendiente");
            }
            if (!cuotas.isEmpty()) {
                Cuota cuota = cuotas.stream()
                        .filter(c -> c.getFechaVencimiento().plusDays(1).equals(financiacion.getFechaInicio()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("La financiación no corresponde a una cuota de esta obligación"));
                cuota.registrarPago(capitalPagado);
            }
            saldoPendiente = saldoPendiente.subtract(capitalPagado);
        }

        estado = estaCompletamentePagada() ? EstadoObligacion.PAGADA : EstadoObligacion.PARCIAL;
    }

    public void registrarPago(BigDecimal importe) {
        if (estado == EstadoObligacion.PAGADA || estado == EstadoObligacion.REFINANCIADA || estado == EstadoObligacion.ANULADA) throw new IllegalStateException("La obligación no admite pagos en su estado actual");
        BigDecimal pago = Validaciones.importePositivo(importe, "El importe del pago es obligatorio");
        if (pago.compareTo(saldoPendiente) > 0) throw new IllegalArgumentException("El pago no puede superar el saldo pendiente");
        if (cuotas.isEmpty()) saldoPendiente = saldoPendiente.subtract(pago);
        else {
            BigDecimal restante = pago;
            for (Cuota cuota : cuotas) {
                if (restante.signum() == 0) break;
                BigDecimal pagoCuota = restante.min(cuota.getSaldoPendiente());
                if (pagoCuota.signum() > 0) {
                    cuota.registrarPago(pagoCuota);
                    restante = restante.subtract(pagoCuota);
                }
            }
            saldoPendiente = saldoPendiente.subtract(pago);
        }
        estado = saldoPendiente.signum() == 0 ? EstadoObligacion.PAGADA : EstadoObligacion.PARCIAL;
    }

    public void revertirPago(BigDecimal importe) {
        if (estado == EstadoObligacion.REFINANCIADA || estado == EstadoObligacion.ANULADA) {
            throw new IllegalStateException("La obligación no admite reversión en su estado actual");
        }
        BigDecimal monto = Validaciones.importePositivo(importe, "El importe de la reversión es obligatorio");
        if (saldoLiquidacion != null) {
            BigDecimal pagado = importeLiquidacion.subtract(saldoLiquidacion);
            if (monto.compareTo(pagado) > 0) {
                throw new IllegalArgumentException("La reversión supera el pago de liquidación");
            }
            saldoLiquidacion = saldoLiquidacion.add(monto);
            estado = EstadoObligacion.PARCIAL;
            return;
        }

        BigDecimal pagado = importeOriginal.subtract(saldoPendiente);
        if (monto.compareTo(pagado) > 0) {
            throw new IllegalArgumentException("La reversión supera los pagos de la obligación");
        }

        BigDecimal restante = monto;
        for (int i = cuotas.size() - 1; i >= 0 && restante.signum() > 0; i--) {
            Cuota cuota = cuotas.get(i);
            BigDecimal pagadoCuota = cuota.getImporteOriginal().subtract(cuota.getSaldoPendiente());
            BigDecimal restaurar = restante.min(pagadoCuota);
            if (restaurar.signum() > 0) {
                cuota.revertirPago(restaurar);
                restante = restante.subtract(restaurar);
            }
        }
        saldoPendiente = saldoPendiente.add(monto);
        estado = saldoPendiente.compareTo(importeOriginal) == 0
                ? EstadoObligacion.PENDIENTE
                : EstadoObligacion.PARCIAL;
    }

    public void registrarPagoLiquidacion(BigDecimal importe) {
        if (estado == EstadoObligacion.REFINANCIADA || estado == EstadoObligacion.ANULADA) throw new IllegalStateException("La obligación no admite pagos en su estado actual");
        if (saldoLiquidacion == null) throw new IllegalStateException("La obligación no tiene una liquidación");
        if (saldoLiquidacion.signum() == 0) throw new IllegalStateException("La liquidación ya está pagada");
        BigDecimal pago = Validaciones.importePositivo(importe, "El importe del pago es obligatorio");
        if (pago.compareTo(saldoLiquidacion) > 0) throw new IllegalArgumentException("El pago no puede superar el saldo de liquidación");
        saldoLiquidacion = saldoLiquidacion.subtract(pago);
        estado = estaCompletamentePagada() ? EstadoObligacion.PAGADA : EstadoObligacion.PARCIAL;
    }
}
