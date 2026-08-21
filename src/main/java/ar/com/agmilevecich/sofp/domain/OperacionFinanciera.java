package ar.com.agmilevecich.sofp.domain;

import ar.com.agmilevecich.sofp.util.Validaciones;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "operaciones_financieras")
public class OperacionFinanciera extends EntidadAuditable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cuenta_origen_id", nullable = false)
    private Cuenta cuentaOrigen;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cuenta_destino_id", nullable = false)
    private Cuenta cuentaDestino;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal importe;

    /**
     * Constructor requerido por JPA.
     */
    protected OperacionFinanciera() {
    }

    /**
     * Constructor principal del dominio.
     */
    public OperacionFinanciera(
            Cuenta cuentaOrigen,
            Cuenta cuentaDestino,
            BigDecimal importe) {

        this.cuentaOrigen = Objects.requireNonNull(
                cuentaOrigen,
                "La cuenta de origen es obligatoria"
        );

        this.cuentaDestino = Objects.requireNonNull(
                cuentaDestino,
                "La cuenta de destino es obligatoria"
        );

        if (this.cuentaOrigen.equals(this.cuentaDestino)) {
            throw new IllegalArgumentException(
                    "La cuenta de origen y destino no pueden ser la misma"
            );
        }

        this.importe = Validaciones.importePositivo(
                importe,
                "El importe es obligatorio"
        );
    }

    public Cuenta getCuentaOrigen() {
        return cuentaOrigen;
    }

    public Cuenta getCuentaDestino() {
        return cuentaDestino;
    }

    public BigDecimal getImporte() {
        return importe;
    }
}
