package ar.com.agmilevecich.sofp.domain;

import ar.com.agmilevecich.sofp.util.Validaciones;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "categorias")
public class Categoria extends EntidadAuditable {

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false)
    private boolean activa = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "perfil_financiero_id", nullable = false)
    private PerfilFinanciero perfilFinanciero;

    /**
     * Constructor requerido por JPA.
     */
    protected Categoria() {
    }

    /**
     * Constructor principal del dominio.
     */
    public Categoria(String nombre, PerfilFinanciero perfilFinanciero) {

        this.nombre = Validaciones.textoObligatorio(
                nombre,
                "El nombre es obligatorio"
        );

        this.perfilFinanciero = Objects.requireNonNull(
                perfilFinanciero,
                "El perfil financiero es obligatorio"
        );
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean isActiva() {
        return activa;
    }

    public PerfilFinanciero getPerfilFinanciero() {
        return perfilFinanciero;
    }

    public void renombrar(String nuevoNombre) {

        this.nombre = Validaciones.textoObligatorio(
                nuevoNombre,
                "El nombre es obligatorio"
        );
    }

    public void cambiarDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void activar() {
        this.activa = true;
    }

    public void desactivar() {
        this.activa = false;
    }

    @Override
    public String toString() {
        return nombre;
    }
}