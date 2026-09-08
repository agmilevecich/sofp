package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.FormaPago;
import ar.com.agmilevecich.sofp.domain.Movimiento;
import ar.com.agmilevecich.sofp.domain.Obligacion;
import ar.com.agmilevecich.sofp.domain.TipoMovimiento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/** Coordina el registro de gastos y los materializa como movimientos de egreso. */
public class GastoService {

    private final MovimientoService movimientoService;
    private final ObligacionService obligacionService;

    public GastoService(MovimientoService movimientoService,
                        ObligacionService obligacionService) {
        this.movimientoService = Objects.requireNonNull(
                movimientoService,
                "El MovimientoService es obligatorio"
        );
        this.obligacionService = Objects.requireNonNull(
                obligacionService,
                "El ObligacionService es obligatorio"
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

        Movimiento movimiento = movimientoService.registrar(
                cuenta,
                categoria,
                TipoMovimiento.EGRESO,
                importe,
                fechaHora,
                descripcion,
                formaPago,
                usuarioId
        );

        if (formaPago == FormaPago.TARJETA_CREDITO) {
            Obligacion obligacion = obligacionService.registrar(movimiento);
            if (obligacion.getMovimientoOrigen().getId() == null) {
                throw new IllegalStateException("La obligación debe quedar asociada a un movimiento persistido");
            }
        }

        return movimiento;
    }
}
