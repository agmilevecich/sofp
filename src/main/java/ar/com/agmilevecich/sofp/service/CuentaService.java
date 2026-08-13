package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.Movimiento;
import ar.com.agmilevecich.sofp.domain.TipoMovimiento;
import ar.com.agmilevecich.sofp.persistence.CuentaRepository;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CuentaService {

    private final CuentaRepository cuentaRepository;
    private final MovimientoRepository movimientoRepository;

    public CuentaService(
            CuentaRepository cuentaRepository,
            MovimientoRepository movimientoRepository) {

        this.cuentaRepository =
                Objects.requireNonNull(
                        cuentaRepository,
                        "El CuentaRepository es obligatorio"
                );

        this.movimientoRepository =
                Objects.requireNonNull(
                        movimientoRepository,
                        "El MovimientoRepository es obligatorio"
                );
    }

    public Cuenta registrar(Cuenta cuenta) {

        Objects.requireNonNull(
                cuenta,
                "La cuenta es obligatoria"
        );

        return cuentaRepository.guardar(cuenta);
    }

    public Optional<Cuenta> buscarPorId(Long id) {

        Objects.requireNonNull(
                id,
                "El id de la cuenta es obligatorio"
        );

        return cuentaRepository.buscarPorId(id);
    }

    public List<Cuenta> listarTodas() {

        return cuentaRepository.listarTodas();
    }

    public List<Cuenta> listarPorPerfilFinanciero(
            Long perfilFinancieroId) {

        Objects.requireNonNull(
                perfilFinancieroId,
                "El id del perfil financiero es obligatorio"
        );

        return cuentaRepository.listarPorPerfilFinanciero(
                perfilFinancieroId
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

            if (movimiento.getTipoMovimiento()
                    == TipoMovimiento.INGRESO) {

                saldo = saldo.add(
                        movimiento.getImporte()
                );

            } else if (movimiento.getTipoMovimiento()
                    == TipoMovimiento.EGRESO) {

                saldo = saldo.subtract(
                        movimiento.getImporte()
                );
            }
        }

        return saldo;
    }
}