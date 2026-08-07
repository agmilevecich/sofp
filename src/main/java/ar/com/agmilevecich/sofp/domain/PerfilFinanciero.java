package ar.com.agmilevecich.sofp.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "perfiles_financieros")
public class PerfilFinanciero extends EntidadAuditable {

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false)
    private boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;


    /**
     * Constructor requerido por JPA.
     */
    protected PerfilFinanciero() {
    }


    /**
     * Constructor principal del dominio.
     */
    public PerfilFinanciero(String nombre, Usuario usuario) {

        this.nombre = Objects.requireNonNull(
                nombre,
                "El nombre del perfil es obligatorio"
        );

        this.usuario = Objects.requireNonNull(
                usuario,
                "El usuario es obligatorio"
        );
    }


    public String getNombre() {
        return nombre;
    }


    public String getDescripcion() {
        return descripcion;
    }


    public boolean isActivo() {
        return activo;
    }


    public Usuario getUsuario() {
        return usuario;
    }


    /**
     * Método utilizado para sincronizar la relación con Usuario.
     */
    protected void asignarUsuario(Usuario usuario) {

        this.usuario = Objects.requireNonNull(
                usuario,
                "El usuario es obligatorio"
        );
    }


    public void cambiarDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }


    public void activar() {
        this.activo = true;
    }


    public void desactivar() {
        this.activo = false;
    }
}