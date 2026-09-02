package ar.com.agmilevecich.sofp.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "monedas")
public class Moneda extends EntidadAuditable {

    @Column(nullable = false, unique = true, length = 10)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(name = "cantidad_decimales", nullable = false)
    private Integer cantidadDecimales = 2;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoMoneda tipo;


    /**
     * Constructor requerido por JPA.
     */
    protected Moneda() {
    }


    /**
     * Constructor principal del dominio.
     */
    public Moneda(String codigo,
                  String nombre,
                  Integer cantidadDecimales,
                  TipoMoneda tipo) {

        this.codigo = Objects.requireNonNull(
                codigo,
                "El código es obligatorio"
        );

        this.nombre = Objects.requireNonNull(
                nombre,
                "El nombre es obligatorio"
        );

        this.cantidadDecimales = Objects.requireNonNull(
                cantidadDecimales,
                "La cantidad de decimales es obligatoria"
        );

        this.tipo = Objects.requireNonNull(
                tipo,
                "El tipo de moneda es obligatorio"
        );
    }


    public String getCodigo() {
        return codigo;
    }


    public String getNombre() {
        return nombre;
    }


    public Integer getCantidadDecimales() {
        return cantidadDecimales;
    }


    public TipoMoneda getTipo() {
        return tipo;
    }


    public void cambiarNombre(String nuevoNombre) {

        this.nombre = Objects.requireNonNull(
                nuevoNombre,
                "El nombre es obligatorio"
        );
    }


    public void cambiarCantidadDecimales(Integer cantidadDecimales) {

        this.cantidadDecimales = Objects.requireNonNull(
                cantidadDecimales,
                "La cantidad de decimales es obligatoria"
        );
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre;
    }
}