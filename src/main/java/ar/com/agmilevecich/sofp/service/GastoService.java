package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.FormaPago;
import ar.com.agmilevecich.sofp.domain.Movimiento;
import ar.com.agmilevecich.sofp.domain.TipoMovimiento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/** Coordina el registro de gastos y los materializa como movimientos de egreso. */
public class GastoService {

    private final MovimientoService movimientoService;

    public GastoService(MovimientoService movimientoService) {
        this.movimientoService = Objects.requireNonNull(
                movimientoService,
                "El MovimientoService es obligatorio"
        );
    }

    public Movimiento registrar(Cuenta cuenta,
                                Categoria categoria,
                                BigDecimal importe,
                                LocalDateTime fechaHora,
                                String descripcion,
                                FormaPago formaPago,
                                Long usuarioId) {
        Objects.requireNonNull(formaPago, "La forma de pago es obligatoria");
        if (formaPago == FormaPago.TARJETA_CREDITO) {
            throw new IllegalArgumentException(
                    "La tarjeta de crédito requiere el modelo de obligaciones pendiente"
            );
        }
        return movimientoService.registrar(
                cuenta,
                categoria,
                TipoMovimiento.EGRESO,
                importe,
                fechaHora,
                descripcion,
                formaPago,
                usuarioId
        );
    }
}
