package ar.com.agmilevecich.sofp.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "usuarios")
public class Usuario extends EntidadAuditable {

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private boolean activo = true;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<PerfilFinanciero> perfilesFinancieros = new ArrayList<>();

    /**
     * Constructor requerido por JPA.
     */
    protected Usuario() {
    }

    /**
     * Constructor principal del dominio.
     */
    public Usuario(String nombre,
                   String apellido,
                   String email,
                   String passwordHash) {

        this.nombre = Objects.requireNonNull(nombre, "El nombre es obligatorio");
        this.apellido = Objects.requireNonNull(apellido, "El apellido es obligatorio");
        this.email = Objects.requireNonNull(email, "El email es obligatorio");
        this.passwordHash = Objects.requireNonNull(passwordHash, "El password hash es obligatorio");
        this.activo = true;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public boolean isActivo() {
        return activo;
    }

    public List<PerfilFinanciero> getPerfilesFinancieros() {
        return perfilesFinancieros;
    }

    /**
     * Agrega un perfil financiero manteniendo ambas referencias sincronizadas.
     */
    public void agregarPerfilFinanciero(PerfilFinanciero perfil) {

        Objects.requireNonNull(perfil, "El perfil financiero es obligatorio");

        perfilesFinancieros.add(perfil);
        perfil.asignarUsuario(this);
    }

    public void activar() {
        this.activo = true;
    }

    public void desactivar() {
        this.activo = false;
    }
}