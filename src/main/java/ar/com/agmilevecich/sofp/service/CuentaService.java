package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.domain.Movimiento;
import ar.com.agmilevecich.sofp.domain.TipoMovimiento;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class CuentaService {

    private final MovimientoRepository movimientoRepository;

    public CuentaService(MovimientoRepository movimientoRepository) {
        this.movimientoRepository = Objects.requireNonNull(
                movimientoRepository,
                "El MovimientoRepository es obligatorio"
        );
    }

    public BigDecimal calcularSaldo(Long cuentaId) {

        Objects.requireNonNull(
                cuentaId,
                "El id de la cuenta es obligatorio"
        );

        List<Movimiento> movimientos =
                movimientoRepository.listarPorCuenta(cuentaId);

        BigDecimal saldo = BigDecimal.ZERO;

        for (Movimiento movimiento : movimientos) {

            if (movimiento.getTipoMovimiento() == TipoMovimiento.INGRESO) {
                saldo = saldo.add(movimiento.getImporte());
            } else if (movimiento.getTipoMovimiento() == TipoMovimiento.EGRESO) {
                saldo = saldo.subtract(movimiento.getImporte());
            }
        }

        return saldo;
    }
}