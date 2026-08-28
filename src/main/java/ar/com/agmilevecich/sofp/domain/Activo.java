package ar.com.agmilevecich.sofp.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "activos")
public class Activo extends EntidadAuditable {

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, unique = true, length = 30)
    private String simbolo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "moneda_id", nullable = false)
    private Moneda moneda;

    /**
     * Constructor requerido por JPA.
     */
    protected Activo() {
    }

    /**
     * Constructor principal del dominio.
     */
    public Activo(String nombre, String simbolo, Moneda moneda) {
        this.nombre = Objects.requireNonNull(
                nombre,
                "El nombre es obligatorio"
        );
        this.simbolo = Objects.requireNonNull(
                simbolo,
                "El símbolo es obligatorio"
        );
        this.moneda = Objects.requireNonNull(
                moneda,
                "La moneda es obligatoria"
        );
    }

    public String getNombre() {
        return nombre;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public Moneda getMoneda() {
        return moneda;
    }

    public void cambiarNombre(String nuevoNombre) {
        this.nombre = Objects.requireNonNull(
                nuevoNombre,
                "El nombre es obligatorio"
        );
    }

    public void cambiarMoneda(Moneda nuevaMoneda) {
        this.moneda = Objects.requireNonNull(
                nuevaMoneda,
                "La moneda es obligatoria"
        );
    }
}
