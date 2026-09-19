package ar.com.agmilevecich.sofp.domain;

import ar.com.agmilevecich.sofp.util.Validaciones;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "tasas_interes")
public class TasaInteres extends EntidadAuditable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cuenta_id", nullable = false)
    private Cuenta cuenta;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 30)
    private TipoTasaInteres tipo;

    @Column(name = "fecha_desde", nullable = false)
    private LocalDate fechaDesde;

    @Column(name = "fecha_hasta")
    private LocalDate fechaHasta;

    @Column(name = "tasa_anual", nullable = false, precision = 9, scale = 4)
    private BigDecimal tasaAnual;

    @Column(name = "fuente", nullable = false, length = 150)
    private String fuente;

    protected TasaInteres() {}

    public TasaInteres(Cuenta cuenta,
                       TipoTasaInteres tipo,
                       LocalDate fechaDesde,
                       LocalDate fechaHasta,
                       BigDecimal tasaAnual,
                       String fuente) {
        this.cuenta = Objects.requireNonNull(cuenta, "La cuenta es obligatoria");
        this.tipo = Objects.requireNonNull(tipo, "El tipo de tasa es obligatorio");
        this.fechaDesde = Objects.requireNonNull(fechaDesde, "La fecha desde es obligatoria");
        if (fechaHasta != null && fechaHasta.isBefore(fechaDesde)) {
            throw new IllegalArgumentException("La fecha hasta no puede ser anterior a la fecha desde");
        }
        this.fechaHasta = fechaHasta;
        this.tasaAnual = validarTasa(tasaAnual);
        this.fuente = Objects.requireNonNull(fuente, "La fuente es obligatoria");
        if (fuente.isBlank()) {
            throw new IllegalArgumentException("La fuente es obligatoria");
        }
    }

    public Cuenta getCuenta() { return cuenta; }
    public TipoTasaInteres getTipo() { return tipo; }
    public LocalDate getFechaDesde() { return fechaDesde; }
    public LocalDate getFechaHasta() { return fechaHasta; }
    public BigDecimal getTasaAnual() { return tasaAnual; }
    public String getFuente() { return fuente; }

    public boolean vigenteEn(LocalDate fecha) {
        Objects.requireNonNull(fecha, "La fecha es obligatoria");
        return !fecha.isBefore(fechaDesde)
                && (fechaHasta == null || !fecha.isAfter(fechaHasta));
    }

    private BigDecimal validarTasa(BigDecimal tasa) {
        Objects.requireNonNull(tasa, "La tasa anual es obligatoria");
        if (tasa.signum() < 0) {
            throw new IllegalArgumentException("La tasa anual no puede ser negativa");
        }
        return tasa.setScale(4);
    }
}
