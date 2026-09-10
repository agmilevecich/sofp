package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.FormaPago;
import ar.com.agmilevecich.sofp.domain.Movimiento;
import ar.com.agmilevecich.sofp.domain.Obligacion;
import ar.com.agmilevecich.sofp.domain.TipoMovimiento;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import ar.com.agmilevecich.sofp.persistence.ObligacionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/** Coordina el pago de una obligación de tarjeta con la salida de fondos de una cuenta. */
public class PagoTarjetaService {

    private final EntityManager entityManager;
    private final MovimientoRepository movimientoRepository;
    private final ObligacionRepository obligacionRepository;

    public PagoTarjetaService(EntityManager entityManager,
                              MovimientoRepository movimientoRepository,
                              ObligacionRepository obligacionRepository) {
        this.entityManager = Objects.requireNonNull(entityManager, "El EntityManager es obligatorio");
        this.movimientoRepository = Objects.requireNonNull(movimientoRepository, "El MovimientoRepository es obligatorio");
        this.obligacionRepository = Objects.requireNonNull(obligacionRepository, "El ObligacionRepository es obligatorio");
    }

    public Obligacion registrarPago(Long obligacionId,
                                    Cuenta cuentaPagadora,
                                    Categoria categoria,
                                    BigDecimal importe,
                                    LocalDateTime fechaHora,
                                    String descripcion,
                                    Long usuarioId) {
        Objects.requireNonNull(obligacionId, "El id de la obligación es obligatorio");
        Objects.requireNonNull(cuentaPagadora, "La cuenta pagadora es obligatoria");
        Objects.requireNonNull(categoria, "La categoría es obligatoria");
        Objects.requireNonNull(importe, "El importe es obligatorio");
        Objects.requireNonNull(fechaHora, "La fecha y hora son obligatorias");
        Objects.requireNonNull(descripcion, "La descripción es obligatoria");
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            Obligacion obligacion = obligacionRepository.buscarPorId(obligacionId)
                    .orElseThrow(() -> new IllegalArgumentException("La obligación no existe"));

            validarPropietario(usuarioId, obligacion);
            validarPropietario(usuarioId, cuentaPagadora);
            validarPropietario(usuarioId, categoria);
            if (!cuentaPagadora.isActiva()) {
                throw new IllegalArgumentException("No se puede pagar desde una cuenta desactivada");
            }
            if (!Objects.equals(cuentaPagadora.getPerfilFinanciero().getId(), categoria.getPerfilFinanciero().getId())) {
                throw new IllegalArgumentException("La cuenta y la categoría deben pertenecer al mismo perfil financiero");
            }
            if (!Objects.equals(cuentaPagadora.getMoneda(), obligacion.getMoneda())) {
                throw new IllegalArgumentException("La cuenta pagadora y la obligación deben utilizar la misma moneda");
            }
            if (importe.signum() <= 0) {
                throw new IllegalArgumentException("El importe debe ser positivo");
            }
            if (importe.compareTo(obligacion.getSaldoPendiente()) > 0) {
                throw new IllegalArgumentException("El pago supera el saldo pendiente de la obligación");
            }

            BigDecimal saldoDisponible = calcularSaldo(cuentaPagadora);
            if (saldoDisponible.compareTo(importe) < 0) {
                throw new IllegalArgumentException("No hay fondos suficientes en la cuenta para pagar la tarjeta");
            }

            Movimiento movimientoPago = new Movimiento(
                    cuentaPagadora,
                    categoria,
                    cuentaPagadora.getMoneda(),
                    TipoMovimiento.EGRESO,
                    importe,
                    fechaHora,
                    descripcion,
                    FormaPago.TRANSFERENCIA
            );

            obligacion.registrarPago(importe);
            movimientoRepository.guardar(movimientoPago);
            entityManager.flush();
            transaction.commit();
            return obligacion;
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    private BigDecimal calcularSaldo(Cuenta cuenta) {
        BigDecimal saldo = BigDecimal.ZERO;
        List<Movimiento> movimientos = movimientoRepository.listarPorCuenta(cuenta.getId());
        for (Movimiento movimiento : movimientos) {
            if (movimiento.getTipoMovimiento() == TipoMovimiento.INGRESO) {
                saldo = saldo.add(movimiento.getImporte());
            } else if (movimiento.getTipoMovimiento() == TipoMovimiento.EGRESO
                    && movimiento.getFormaPago() != FormaPago.TARJETA_CREDITO) {
                saldo = saldo.subtract(movimiento.getImporte());
            }
        }
        return saldo;
    }

    private void validarPropietario(Long usuarioId, Obligacion obligacion) {
        Long propietarioId = obligacion.getMovimientoOrigen()
                .getCuenta()
                .getPerfilFinanciero()
                .getUsuario()
                .getId();
        if (!Objects.equals(propietarioId, usuarioId)) {
            throw new IllegalArgumentException("La obligación no pertenece al usuario autorizado");
        }
    }

    private void validarPropietario(Long usuarioId, Cuenta cuenta) {
        if (!Objects.equals(cuenta.getPerfilFinanciero().getUsuario().getId(), usuarioId)) {
            throw new IllegalArgumentException("El usuario no es propietario de la cuenta");
        }
    }

    private void validarPropietario(Long usuarioId, Categoria categoria) {
        if (!Objects.equals(categoria.getPerfilFinanciero().getUsuario().getId(), usuarioId)) {
            throw new IllegalArgumentException("El usuario no es propietario de la categoría");
        }
    }
}
