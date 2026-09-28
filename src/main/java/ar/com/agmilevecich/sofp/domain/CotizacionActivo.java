package ar.com.agmilevecich.sofp.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "cotizaciones_activo")
public class CotizacionActivo extends EntidadAuditable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "activo_id", nullable = false)
    private Activo activo;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal precio;

    protected CotizacionActivo() {
    }

    public CotizacionActivo(Activo activo, LocalDate fecha, BigDecimal precio) {
        this.activo = Objects.requireNonNull(activo, "El activo es obligatorio");
        this.fecha = Objects.requireNonNull(fecha, "La fecha es obligatoria");
        this.precio = validarPrecio(precio);
    }

    public Activo getActivo() { return activo; }
    public LocalDate getFecha() { return fecha; }
    public BigDecimal getPrecio() { return precio; }

    public void cambiarPrecio(BigDecimal nuevoPrecio) {
        this.precio = validarPrecio(nuevoPrecio);
    }

    private BigDecimal validarPrecio(BigDecimal precio) {
        Objects.requireNonNull(precio, "El precio es obligatorio");
        if (precio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que cero");
        }
        return precio;
    }
}
