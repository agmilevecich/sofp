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
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "tipo_cambio_liquidacion_id") private TipoCambio tipoCambioLiquidacion;
    @OneToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "movimiento_origen_id", nullable = false, unique = true) private Movimiento movimientoOrigen;
    @OneToMany(mappedBy = "obligacion", cascade = CascadeType.ALL, orphanRemoval = true) @OrderBy("numero ASC") private List<Cuota> cuotas = new ArrayList<>();

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
    public BigDecimal getImporteLiquidacion() { return importeLiquidacion; }
    public BigDecimal getSaldoLiquidacion() { return saldoLiquidacion; }
    public BigDecimal getSaldoPendiente() { return saldoPendiente; }
    public EstadoObligacion getEstado() { return estado; }
    public Movimiento getMovimientoOrigen() { return movimientoOrigen; }
    public List<Cuota> getCuotas() { return Collections.unmodifiableList(cuotas); }
    public Moneda getMonedaOriginal() { return monedaOriginal != null ? monedaOriginal : movimientoOrigen.getMoneda(); }
    public Moneda getMonedaLiquidacion() { return monedaLiquidacion != null ? monedaLiquidacion : movimientoOrigen.getCuenta().getMoneda(); }
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

    public void liquidar(TipoCambio tipoCambio) {
        Objects.requireNonNull(tipoCambio, "El tipo de cambio es obligatorio");
        if (importeLiquidacion != null) throw new IllegalStateException("La obligación ya tiene una liquidación");
        if (!getMonedaOriginal().equals(tipoCambio.getMonedaOrigen())) {
            throw new IllegalArgumentException("La moneda de origen del tipo de cambio no coincide con la obligación");
        }
        if (!getMonedaLiquidacion().equals(tipoCambio.getMonedaDestino())) {
            throw new IllegalArgumentException("La moneda de destino del tipo de cambio no coincide con la obligación");
        }
        this.tipoCambioLiquidacion = tipoCambio;
        this.importeLiquidacion = tipoCambio.convertir(importeOriginal);
        this.saldoLiquidacion = this.importeLiquidacion;
    }

    public void registrarPago(BigDecimal importe) {
        if (estado == EstadoObligacion.PAGADA) throw new IllegalStateException("La obligación ya está pagada");
        BigDecimal pago = Validaciones.importePositivo(importe, "El importe del pago es obligatorio");
        if (pago.compareTo(saldoPendiente) > 0) throw new IllegalArgumentException("El pago no puede superar el saldo pendiente");
        if (cuotas.isEmpty()) saldoPendiente = saldoPendiente.subtract(pago);
        else {
            BigDecimal restante = pago;
            for (Cuota cuota : cuotas) {
                if (restante.signum() == 0) break;
                BigDecimal pagoCuota = restante.min(cuota.getSaldoPendiente());
                cuota.registrarPago(pagoCuota);
                restante = restante.subtract(pagoCuota);
            }
            saldoPendiente = saldoPendiente.subtract(pago);
        }
        estado = saldoPendiente.signum() == 0 ? EstadoObligacion.PAGADA : EstadoObligacion.PARCIAL;
    }
}
