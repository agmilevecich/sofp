package ar.com.agmilevecich.sofp.domain;

import ar.com.agmilevecich.sofp.util.Validaciones;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    @OneToMany(
            mappedBy = "operacionFinanciera",
            fetch = FetchType.LAZY
    )
    private List<Movimiento> movimientos = new ArrayList<>();

    @OneToMany(
            mappedBy = "operacionFinanciera",
            fetch = FetchType.LAZY
    )
    private List<MovimientoActivo> movimientosActivos = new ArrayList<>();

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

    public List<Movimiento> getMovimientos() {
        return Collections.unmodifiableList(
                movimientos
        );
    }

    public List<MovimientoActivo> getMovimientosActivos() {
        return Collections.unmodifiableList(
                movimientosActivos
        );
    }

    public void agregarMovimiento(
            Movimiento movimiento) {

        Objects.requireNonNull(
                movimiento,
                "El movimiento es obligatorio"
        );

        if (movimientos.size() >= 2) {
            throw new IllegalStateException(
                    "Una operación financiera no puede tener más de dos movimientos"
            );
        }

        if (movimientos.contains(movimiento)) {
            throw new IllegalArgumentException(
                    "El movimiento ya pertenece a la operación financiera"
            );
        }

        validarMovimiento(movimiento);

        movimiento.asociarOperacionFinanciera(
                this
        );

        movimientos.add(movimiento);
    }

    public void agregarMovimientoActivo(
            MovimientoActivo movimientoActivo) {

        Objects.requireNonNull(
                movimientoActivo,
                "El movimiento de activo es obligatorio"
        );

        if (movimientosActivos.contains(movimientoActivo)) {
            throw new IllegalArgumentException(
                    "El movimiento de activo ya pertenece a la operación financiera"
            );
        }

        movimientoActivo.asociarOperacionFinanciera(
                this
        );

        movimientosActivos.add(movimientoActivo);
    }

    private void validarMovimiento(
            Movimiento movimiento) {

        if (movimientos.isEmpty()) {
            if (!cuentaOrigen.equals(movimiento.getCuenta())) {
                throw new IllegalArgumentException(
                        "El primer movimiento debe pertenecer a la cuenta de origen"
                );
            }

            if (movimiento.getTipoMovimiento() != TipoMovimiento.EGRESO) {
                throw new IllegalArgumentException(
                        "El primer movimiento debe ser un egreso"
                );
            }

            return;
        }

        if (!cuentaDestino.equals(movimiento.getCuenta())) {
            throw new IllegalArgumentException(
                    "El segundo movimiento debe pertenecer a la cuenta de destino"
            );
        }

        if (movimiento.getTipoMovimiento() != TipoMovimiento.INGRESO) {
            throw new IllegalArgumentException(
                    "El segundo movimiento debe ser un ingreso"
            );
        }
    }

}
