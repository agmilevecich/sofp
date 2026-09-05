package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
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
                                Long usuarioId) {
        return movimientoService.registrar(
                cuenta,
                categoria,
                TipoMovimiento.EGRESO,
                importe,
                fechaHora,
                descripcion,
                usuarioId
        );
    }
}
