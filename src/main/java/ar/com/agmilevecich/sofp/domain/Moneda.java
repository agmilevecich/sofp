package ar.com.agmilevecich.sofp.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "monedas")
public class Moneda extends EntidadAuditable {

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(name = "cantidad_decimales", nullable = false)
    private Integer cantidadDecimales = 2;

    private String nombre;

    private String tipo;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Integer getCantidadDecimales() {
        return cantidadDecimales;
    }

    public void setCantidadDecimales(Integer cantidadDecimales) {
        this.cantidadDecimales = cantidadDecimales;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
