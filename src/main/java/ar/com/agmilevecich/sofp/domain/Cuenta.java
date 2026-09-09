package ar.com.agmilevecich.sofp.domain;

import ar.com.agmilevecich.sofp.util.Validaciones;
import jakarta.persistence.*;

import java.math.BigDecimal;
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

    @Column(name = "limite_credito", precision = 19, scale = 2)
    private BigDecimal limiteCredito;

    @Column(name = "dia_cierre")
    private Integer diaCierre;

    @Column(name = "dia_vencimiento")
    private Integer diaVencimiento;

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
     * Constructor principal del dominio para cuentas que no son tarjetas.
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

    /**
     * Constructor para una cuenta especializada en tarjeta de crédito.
     */
    public Cuenta(String nombre,
                  PerfilFinanciero perfilFinanciero,
                  InstitucionFinanciera institucionFinanciera,
                  Moneda moneda,
                  BigDecimal limiteCredito,
                  Integer diaCierre,
                  Integer diaVencimiento) {

        this(
                nombre,
                TipoCuenta.TARJETA_CREDITO,
                perfilFinanciero,
                institucionFinanciera,
                moneda
        );

        configurarDatosCredito(limiteCredito, diaCierre, diaVencimiento);
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

    public BigDecimal getLimiteCredito() {
        return limiteCredito;
    }

    public Integer getDiaCierre() {
        return diaCierre;
    }

    public Integer getDiaVencimiento() {
        return diaVencimiento;
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

    public void configurarDatosCredito(BigDecimal limiteCredito,
                                       Integer diaCierre,
                                       Integer diaVencimiento) {

        this.limiteCredito = Objects.requireNonNull(
                limiteCredito,
                "El límite de crédito es obligatorio"
        );

        if (limiteCredito.signum() <= 0) {
            throw new IllegalArgumentException(
                    "El límite de crédito debe ser positivo"
            );
        }

        this.diaCierre = validarDia(
                diaCierre,
                "El día de cierre es obligatorio"
        );

        this.diaVencimiento = validarDia(
                diaVencimiento,
                "El día de vencimiento es obligatorio"
        );
    }

    /**
     * Calcula el crédito disponible a partir del crédito utilizado en la
     * moneda de la tarjeta. No realiza conversiones de moneda.
     */
    public BigDecimal calcularCreditoDisponible(BigDecimal creditoUtilizado) {
        Objects.requireNonNull(
                limiteCredito,
                "El límite de crédito es obligatorio"
        );
        Objects.requireNonNull(
                creditoUtilizado,
                "El crédito utilizado es obligatorio"
        );

        if (creditoUtilizado.signum() < 0) {
            throw new IllegalArgumentException(
                    "El crédito utilizado no puede ser negativo"
            );
        }

        return limiteCredito.subtract(creditoUtilizado);
    }

    private int validarDia(Integer dia, String mensaje) {

        Objects.requireNonNull(dia, mensaje);

        if (dia < 1 || dia > 31) {
            throw new IllegalArgumentException(
                    "El día debe estar entre 1 y 31"
            );
        }

        return dia;
    }

    public void activar() {
        this.activa = true;
    }

    public void desactivar() {
        this.activa = false;
    }
}
