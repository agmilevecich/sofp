package ar.com.agmilevecich.sofp.domain;

import ar.com.agmilevecich.sofp.util.Validaciones;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "movimientos")
public class Movimiento extends EntidadAuditable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cuenta_id", nullable = false)
    private Cuenta cuenta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoMovimiento tipoMovimiento;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal importe;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false, length = 250)
    private String descripcion;

    @Column(length = 1000)
    private String observaciones;

    /**
     * Constructor requerido por JPA.
     */
    protected Movimiento() {
    }

    /**
     * Constructor principal del dominio.
     */
    public Movimiento(
            Cuenta cuenta,
            Categoria categoria,
            TipoMovimiento tipoMovimiento,
            BigDecimal importe,
            LocalDateTime fechaHora,
            String descripcion) {

        this.cuenta = Objects.requireNonNull(
                cuenta,
                "La cuenta es obligatoria"
        );

        this.categoria = Objects.requireNonNull(
                categoria,
                "La categoría es obligatoria"
        );

        this.tipoMovimiento = Objects.requireNonNull(
                tipoMovimiento,
                "El tipo de movimiento es obligatorio"
        );

        this.importe = Validaciones.importePositivo(
                importe,
                "El importe es obligatorio"
        );

        this.fechaHora = Objects.requireNonNull(
                fechaHora,
                "La fecha y hora son obligatorias"
        );

        this.descripcion = Validaciones.textoObligatorio(
                descripcion,
                "La descripción es obligatoria"
        );
    }

    public Cuenta getCuenta() {
        return cuenta;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public TipoMovimiento getTipoMovimiento() {
        return tipoMovimiento;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void modificarTipoMovimiento(
            TipoMovimiento tipoMovimiento) {

        this.tipoMovimiento = Objects.requireNonNull(
                tipoMovimiento,
                "El tipo de movimiento es obligatorio"
        );
    }

    public void cambiarImporte(BigDecimal importe) {

        this.importe = Validaciones.importePositivo(
                importe,
                "El importe es obligatorio"
        );
    }

    public void cambiarFechaHora(LocalDateTime fechaHora) {

        this.fechaHora = Objects.requireNonNull(
                fechaHora,
                "La fecha y hora son obligatorias"
        );
    }

    public void cambiarDescripcion(String descripcion) {

        this.descripcion = Validaciones.textoObligatorio(
                descripcion,
                "La descripción es obligatoria"
        );
    }

    public void cambiarObservaciones(String observaciones) {

        this.observaciones = observaciones;
    }

    public void cambiarCategoria(Categoria categoria) {

        this.categoria = Objects.requireNonNull(
                categoria,
                "La categoría es obligatoria"
        );
    }
}