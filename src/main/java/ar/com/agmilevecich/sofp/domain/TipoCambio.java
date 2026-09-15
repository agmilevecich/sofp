package ar.com.agmilevecich.sofp.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "tipos_cambio")
public class TipoCambio extends EntidadAuditable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "moneda_origen_id", nullable = false)
    private Moneda monedaOrigen;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "moneda_destino_id", nullable = false)
    private Moneda monedaDestino;

    @Column(nullable = false, precision = 19, scale = 10)
    private BigDecimal cotizacion;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false, length = 100)
    private String fuente;

    protected TipoCambio() {
    }

    public TipoCambio(Moneda monedaOrigen,
                      Moneda monedaDestino,
                      BigDecimal cotizacion,
                      LocalDateTime fechaHora,
                      String fuente) {
        this.monedaOrigen = Objects.requireNonNull(monedaOrigen, "La moneda de origen es obligatoria");
        this.monedaDestino = Objects.requireNonNull(monedaDestino, "La moneda de destino es obligatoria");
        if (Objects.equals(monedaOrigen, monedaDestino)) {
            throw new IllegalArgumentException("La moneda de origen y destino deben ser diferentes");
        }
        this.cotizacion = validarCotizacion(cotizacion);
        this.fechaHora = Objects.requireNonNull(fechaHora, "La fecha y hora son obligatorias");
        this.fuente = validarFuente(fuente);
    }

    public Moneda getMonedaOrigen() {
        return monedaOrigen;
    }

    public Moneda getMonedaDestino() {
        return monedaDestino;
    }

    public BigDecimal getCotizacion() {
        return cotizacion;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getFuente() {
        return fuente;
    }

    public BigDecimal convertir(BigDecimal importeOrigen) {
        Objects.requireNonNull(importeOrigen, "El importe de origen es obligatorio");
        if (importeOrigen.signum() <= 0) {
            throw new IllegalArgumentException("El importe de origen debe ser positivo");
        }
        return importeOrigen.multiply(cotizacion)
                .setScale(monedaDestino.getCantidadDecimales(), RoundingMode.HALF_UP);
    }

    private BigDecimal validarCotizacion(BigDecimal cotizacion) {
        Objects.requireNonNull(cotizacion, "La cotización es obligatoria");
        if (cotizacion.signum() <= 0) {
            throw new IllegalArgumentException("La cotización debe ser positiva");
        }
        return cotizacion;
    }

    private String validarFuente(String fuente) {
        Objects.requireNonNull(fuente, "La fuente es obligatoria");
        if (fuente.isBlank()) {
            throw new IllegalArgumentException("La fuente es obligatoria");
        }
        return fuente;
    }
}
