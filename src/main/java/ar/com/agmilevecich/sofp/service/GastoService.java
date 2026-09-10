package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.FormaPago;
import ar.com.agmilevecich.sofp.domain.Moneda;
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

    public GastoService(MovimientoService movimientoService) {
        this.movimientoService = Objects.requireNonNull(movimientoService, "El MovimientoService es obligatorio");
        this.obligacionService = null;
    }

    public GastoService(MovimientoService movimientoService, ObligacionService obligacionService) {
        this.movimientoService = Objects.requireNonNull(movimientoService, "El MovimientoService es obligatorio");
        this.obligacionService = Objects.requireNonNull(obligacionService, "El ObligacionService es obligatorio");
    }

    public Movimiento registrar(Cuenta cuenta, Categoria categoria, BigDecimal importe,
                                LocalDateTime fechaHora, String descripcion,
                                FormaPago formaPago, Long usuarioId) {
        return registrar(cuenta, categoria, cuenta.getMoneda(), importe, fechaHora, descripcion, formaPago, usuarioId, 1);
    }

    /** Registra un gasto indicando expresamente la moneda del consumo. */
    public Movimiento registrar(Cuenta cuenta, Categoria categoria, Moneda moneda,
                                BigDecimal importe, LocalDateTime fechaHora, String descripcion,
                                FormaPago formaPago, Long usuarioId) {
        return registrar(cuenta, categoria, moneda, importe, fechaHora, descripcion, formaPago, usuarioId, 1);
    }

    /** Registra un gasto con la cantidad indicada de cuotas usando la moneda de la cuenta. */
    public Movimiento registrar(Cuenta cuenta, Categoria categoria, BigDecimal importe,
                                LocalDateTime fechaHora, String descripcion,
                                FormaPago formaPago, Long usuarioId, int cantidadCuotas) {
        return registrar(cuenta, categoria, cuenta.getMoneda(), importe, fechaHora, descripcion, formaPago, usuarioId, cantidadCuotas);
    }

    /** Registra un gasto y, si es con tarjeta de crédito, genera la cantidad indicada de cuotas sin interés. */
    public Movimiento registrar(Cuenta cuenta, Categoria categoria, Moneda moneda,
                                BigDecimal importe, LocalDateTime fechaHora, String descripcion,
                                FormaPago formaPago, Long usuarioId, int cantidadCuotas) {
        Objects.requireNonNull(formaPago, "La forma de pago es obligatoria");
        Objects.requireNonNull(moneda, "La moneda es obligatoria");

        if (formaPago == FormaPago.TARJETA_CREDITO && cantidadCuotas < 1) {
            throw new IllegalArgumentException("La cantidad de cuotas debe ser positiva");
        }

        if (formaPago == FormaPago.TARJETA_CREDITO && obligacionService == null) {
            throw new IllegalStateException("El ObligacionService es obligatorio para gastos con tarjeta de crédito");
        }

        Movimiento movimiento = movimientoService.registrar(
                cuenta,
                categoria,
                moneda,
                TipoMovimiento.EGRESO,
                importe,
                fechaHora,
                descripcion,
                formaPago,
                usuarioId
        );

        if (formaPago == FormaPago.TARJETA_CREDITO) {
            Obligacion obligacion = obligacionService.registrar(movimiento);
            obligacion.generarCuotas(cantidadCuotas);
            if (obligacion.getMovimientoOrigen().getId() == null) {
                throw new IllegalStateException("La obligación debe quedar asociada a un movimiento persistido");
            }
        }

        return movimiento;
    }
}
