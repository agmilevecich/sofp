package ar.com.agmilevecich.sofp.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa un punto de la evolución histórica del saldo de una cuenta.
 *
 * No es una entidad persistente. El saldo representa el resultado acumulado
 * inmediatamente después del movimiento indicado por la fecha y hora.
 */
public class EvolucionSaldoCuenta {

    private final LocalDateTime fechaHora;
    private final BigDecimal saldo;

    public EvolucionSaldoCuenta(
            LocalDateTime fechaHora,
            BigDecimal saldo) {

        this.fechaHora = Objects.requireNonNull(
                fechaHora,
                "La fecha y hora son obligatorias"
        );

        this.saldo = Objects.requireNonNull(
                saldo,
                "El saldo es obligatorio"
        );
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }
}
