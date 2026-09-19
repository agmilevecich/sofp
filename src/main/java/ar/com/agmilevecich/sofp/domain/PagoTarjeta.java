package ar.com.agmilevecich.sofp.domain;

import ar.com.agmilevecich.sofp.util.Validaciones;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "pagos_tarjeta")
public class PagoTarjeta extends EntidadAuditable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "obligacion_id", nullable = false)
    private Obligacion obligacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financiacion_id")
    private Financiacion financiacion;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movimiento_id", nullable = false, unique = true)
    private Movimiento movimiento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cuenta_pagadora_id", nullable = false)
    private Cuenta cuentaPagadora;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "moneda_id", nullable = false)
    private Moneda moneda;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal importe;

    @Column(name = "importe_financiacion", nullable = false, precision = 19, scale = 2)
    private BigDecimal importeFinanciacion;

    @Column(name = "importe_obligacion", nullable = false, precision = 19, scale = 2)
    private BigDecimal importeObligacion;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPagoTarjeta estado;

    @Column(name = "fecha_reversion")
    private LocalDateTime fechaReversion;

    protected PagoTarjeta() {}

    public PagoTarjeta(Obligacion obligacion,
                       Financiacion financiacion,
                       Movimiento movimiento,
                       Cuenta cuentaPagadora,
                       Categoria categoria,
                       Moneda moneda,
                       BigDecimal importe,
                       BigDecimal importeFinanciacion,
                       BigDecimal importeObligacion,
                       LocalDateTime fechaHora) {
        this.obligacion = Objects.requireNonNull(obligacion, "La obligación es obligatoria");
        this.financiacion = financiacion;
        this.movimiento = Objects.requireNonNull(movimiento, "El movimiento es obligatorio");
        this.cuentaPagadora = Objects.requireNonNull(cuentaPagadora, "La cuenta pagadora es obligatoria");
        this.categoria = Objects.requireNonNull(categoria, "La categoría es obligatoria");
        this.moneda = Objects.requireNonNull(moneda, "La moneda es obligatoria");
        this.importe = Validaciones.importePositivo(importe, "El importe es obligatorio");
        this.importeFinanciacion = validarNoNegativo(importeFinanciacion, "El importe de financiación");
        this.importeObligacion = validarNoNegativo(importeObligacion, "El importe de obligación");
        if (this.importeFinanciacion.add(this.importeObligacion).compareTo(this.importe) != 0) {
            throw new IllegalArgumentException("La distribución del pago no coincide con el importe");
        }
        this.fechaHora = Objects.requireNonNull(fechaHora, "La fecha y hora son obligatorias");
        this.estado = EstadoPagoTarjeta.ACTIVO;
    }

    public Obligacion getObligacion() { return obligacion; }
    public Financiacion getFinanciacion() { return financiacion; }
    public Movimiento getMovimiento() { return movimiento; }
    public Cuenta getCuentaPagadora() { return cuentaPagadora; }
    public Categoria getCategoria() { return categoria; }
    public Moneda getMoneda() { return moneda; }
    public BigDecimal getImporte() { return importe; }
    public BigDecimal getImporteFinanciacion() { return importeFinanciacion; }
    public BigDecimal getImporteObligacion() { return importeObligacion; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public EstadoPagoTarjeta getEstado() { return estado; }
    public LocalDateTime getFechaReversion() { return fechaReversion; }

    public void marcarRevertido(LocalDateTime fechaReversion) {
        if (estado == EstadoPagoTarjeta.REVERSADO) {
            throw new IllegalStateException("El pago ya está revertido");
        }
        this.estado = EstadoPagoTarjeta.REVERSADO;
        this.fechaReversion = Objects.requireNonNull(fechaReversion, "La fecha de reversión es obligatoria");
    }

    private BigDecimal validarNoNegativo(BigDecimal importe, String mensaje) {
        Objects.requireNonNull(importe, mensaje + " es obligatorio");
        if (importe.signum() < 0) throw new IllegalArgumentException(mensaje + " no puede ser negativo");
        return importe.setScale(2);
    }
}
