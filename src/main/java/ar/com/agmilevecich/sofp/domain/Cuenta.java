package ar.com.agmilevecich.sofp.domain;

import ar.com.agmilevecich.sofp.util.Validaciones;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "cuentas")
public class Cuenta extends EntidadAuditable {

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(name = "identificador_externo", length = 150)
    private String identificadorExterno;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cuenta", nullable = false, length = 30)
    private TipoCuenta tipoCuenta;

    @Column(nullable = false)
    private boolean activa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "perfil_financiero_id", nullable = false)
    private PerfilFinanciero perfilFinanciero;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "institucion_financiera_id", nullable = false)
    private InstitucionFinanciera institucionFinanciera;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "moneda_id", nullable = false)
    private Moneda moneda;

    /**
     * Constructor requerido por JPA.
     */
    protected Cuenta() {
    }

    /**
     * Constructor principal del dominio.
     */
    public Cuenta(String nombre,
                  TipoCuenta tipoCuenta,
                  PerfilFinanciero perfilFinanciero,
                  InstitucionFinanciera institucionFinanciera,
                  Moneda moneda) {

        this.nombre = Validaciones.textoObligatorio(
                nombre,
                "El nombre es obligatorio"
        );

        this.tipoCuenta = Objects.requireNonNull(
                tipoCuenta,
                "El tipo de cuenta es obligatorio"
        );

        this.perfilFinanciero = Objects.requireNonNull(
                perfilFinanciero,
                "El perfil financiero es obligatorio"
        );

        this.institucionFinanciera = Objects.requireNonNull(
                institucionFinanciera,
                "La institución financiera es obligatoria"
        );

        this.moneda = Objects.requireNonNull(
                moneda,
                "La moneda es obligatoria"
        );

        this.activa = true;
    }

    public String getNombre() {
        return nombre;
    }

    public String getIdentificadorExterno() {
        return identificadorExterno;
    }

    public TipoCuenta getTipoCuenta() {
        return tipoCuenta;
    }

    public boolean isActiva() {
        return activa;
    }

    public PerfilFinanciero getPerfilFinanciero() {
        return perfilFinanciero;
    }

    public InstitucionFinanciera getInstitucionFinanciera() {
        return institucionFinanciera;
    }

    public Moneda getMoneda() {
        return moneda;
    }

    public void renombrar(String nuevoNombre) {
        this.nombre = Validaciones.textoObligatorio(
                nuevoNombre,
                "El nombre es obligatorio"
        );
    }

    public void cambiarIdentificadorExterno(String identificadorExterno) {
        this.identificadorExterno = identificadorExterno;
    }

    public void cambiarTipoCuenta(TipoCuenta tipoCuenta) {
        this.tipoCuenta = Objects.requireNonNull(
                tipoCuenta,
                "El tipo de cuenta es obligatoria"
        );
    }

    public void cambiarInstitucionFinanciera(InstitucionFinanciera institucionFinanciera) {
        this.institucionFinanciera = Objects.requireNonNull(
                institucionFinanciera,
                "La institución financiera es obligatoria"
        );
    }

    public void cambiarMoneda(Moneda moneda) {
        this.moneda = Objects.requireNonNull(
                moneda,
                "La moneda es obligatoria"
        );
    }

    public void activar() {
        this.activa = true;
    }

    public void desactivar() {
        this.activa = false;
    }
}