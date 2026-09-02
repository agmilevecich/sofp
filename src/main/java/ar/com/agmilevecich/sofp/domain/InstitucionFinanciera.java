package ar.com.agmilevecich.sofp.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "instituciones_financieras")
public class InstitucionFinanciera extends EntidadAuditable {

    @Column(nullable = false, length = 100)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoInstitucionFinanciera tipo;

    @Column(length = 200)
    private String sitioWeb;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false)
    private boolean activa;

    /**
     * Constructor requerido por JPA.
     */
    protected InstitucionFinanciera() {
    }

    /**
     * Constructor principal del dominio.
     */
    public InstitucionFinanciera(
            String nombre,
            TipoInstitucionFinanciera tipo) {

        this.nombre =
                Objects.requireNonNull(
                        nombre,
                        "El nombre es obligatorio"
                );

        this.tipo =
                Objects.requireNonNull(
                        tipo,
                        "El tipo es obligatorio"
                );

        this.activa = true;
    }

    // ===========================
    // Getters
    // ===========================

    public String getNombre() {
        return nombre;
    }

    public TipoInstitucionFinanciera getTipo() {
        return tipo;
    }

    public String getSitioWeb() {
        return sitioWeb;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean isActiva() {
        return activa;
    }

    // ===========================
    // Comportamiento del dominio
    // ===========================

    public void renombrar(String nuevoNombre) {

        this.nombre =
                Objects.requireNonNull(
                        nuevoNombre,
                        "El nombre es obligatorio"
                );
    }

    public void actualizarSitioWeb(String sitioWeb) {

        this.sitioWeb =
                Objects.requireNonNull(
                        sitioWeb,
                        "El sitio web es obligatorio"
                );
    }

    public void actualizarDescripcion(String descripcion) {

        this.descripcion =
                Objects.requireNonNull(
                        descripcion,
                        "La descripción es obligatoria"
                );
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