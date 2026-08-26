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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_origen_id")
    private Cuenta cuentaOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_destino_id")
    private Cuenta cuentaDestino;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoOperacionFinanciera tipoOperacion;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal importe;

    @OneToMany(mappedBy = "operacionFinanciera", fetch = FetchType.LAZY)
    private List<Movimiento> movimientos = new ArrayList<>();

    @OneToMany(mappedBy = "operacionFinanciera", fetch = FetchType.LAZY)
    private List<MovimientoActivo> movimientosActivos = new ArrayList<>();

    protected OperacionFinanciera() {
    }

    public OperacionFinanciera(Cuenta cuentaOrigen, Cuenta cuentaDestino, BigDecimal importe) {
        this(cuentaOrigen, cuentaDestino, importe, TipoOperacionFinanciera.TRANSFERENCIA);
    }

    public OperacionFinanciera(
            Cuenta cuentaOrigen,
            Cuenta cuentaDestino,
            BigDecimal importe,
            TipoOperacionFinanciera tipoOperacion) {

        this.tipoOperacion = Objects.requireNonNull(
                tipoOperacion,
                "El tipo de operación financiera es obligatorio"
        );

        validarCuentas(cuentaOrigen, cuentaDestino);

        this.importe = Validaciones.importePositivo(
                importe,
                "El importe es obligatorio"
        );

        this.cuentaOrigen = cuentaOrigen;
        this.cuentaDestino = cuentaDestino;
    }

    private void validarCuentas(Cuenta cuentaOrigen, Cuenta cuentaDestino) {
        switch (tipoOperacion) {
            case TRANSFERENCIA -> {
                Objects.requireNonNull(cuentaOrigen, "La cuenta de origen es obligatoria");
                Objects.requireNonNull(cuentaDestino, "La cuenta de destino es obligatoria");
                validarCuentasDistintas(cuentaOrigen, cuentaDestino);
            }
            case COMPRA -> Objects.requireNonNull(
                    cuentaOrigen,
                    "La cuenta de origen es obligatoria para una compra"
            );
            case VENTA -> Objects.requireNonNull(
                    cuentaDestino,
                    "La cuenta de destino es obligatoria para una venta"
            );
        }
    }

    private void validarCuentasDistintas(Cuenta cuentaOrigen, Cuenta cuentaDestino) {
        if (cuentaOrigen.equals(cuentaDestino)) {
            throw new IllegalArgumentException(
                    "La cuenta de origen y destino no pueden ser la misma"
            );
        }
    }

    public Cuenta getCuentaOrigen() {
        return cuentaOrigen;
    }

    public Cuenta getCuentaDestino() {
        return cuentaDestino;
    }

    public TipoOperacionFinanciera getTipoOperacion() {
        return tipoOperacion;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public List<Movimiento> getMovimientos() {
        return Collections.unmodifiableList(movimientos);
    }

    public List<MovimientoActivo> getMovimientosActivos() {
        return Collections.unmodifiableList(movimientosActivos);
    }

    public void agregarMovimiento(Movimiento movimiento) {
        Objects.requireNonNull(movimiento, "El movimiento es obligatorio");

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

        movimiento.asociarOperacionFinanciera(this);
        movimientos.add(movimiento);
    }

    public void agregarMovimientoActivo(MovimientoActivo movimientoActivo) {
        Objects.requireNonNull(
                movimientoActivo,
                "El movimiento de activo es obligatorio"
        );

        if (movimientosActivos.contains(movimientoActivo)) {
            throw new IllegalArgumentException(
                    "El movimiento de activo ya pertenece a la operación financiera"
            );
        }

        movimientoActivo.asociarOperacionFinanciera(this);
        movimientosActivos.add(movimientoActivo);
    }

    private void validarMovimiento(Movimiento movimiento) {
        if (movimientos.isEmpty()) {
            if (cuentaOrigen != null && !cuentaOrigen.equals(movimiento.getCuenta())) {
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

        if (tipoOperacion == TipoOperacionFinanciera.TRANSFERENCIA) {
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

}
