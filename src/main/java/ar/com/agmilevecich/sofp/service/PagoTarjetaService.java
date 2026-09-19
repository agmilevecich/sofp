package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.FormaPago;
import ar.com.agmilevecich.sofp.domain.Financiacion;
import ar.com.agmilevecich.sofp.domain.Movimiento;
import ar.com.agmilevecich.sofp.domain.Obligacion;
import ar.com.agmilevecich.sofp.domain.PagoTarjeta;
import ar.com.agmilevecich.sofp.domain.Refinanciacion;
import ar.com.agmilevecich.sofp.domain.TipoTasaInteres;
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
            validarFechaPago(obligacion, fechaHora);
            if (!cuentaPagadora.isActiva()) {
                throw new IllegalArgumentException("No se puede pagar desde una cuenta desactivada");
            }
            if (!Objects.equals(cuentaPagadora.getPerfilFinanciero().getId(), categoria.getPerfilFinanciero().getId())) {
                throw new IllegalArgumentException("La cuenta y la categoría deben pertenecer al mismo perfil financiero");
            }
            Financiacion financiacionPendiente = buscarFinanciacionPendiente(obligacion, fechaHora);
            Refinanciacion refinanciacionPendiente = buscarRefinanciacionPendiente(obligacion, fechaHora);
            actualizarInteresesSiCorresponde(financiacionPendiente, fechaHora.toLocalDate(), obligacion);
            validarMonedaPagadora(obligacion, cuentaPagadora, financiacionPendiente, refinanciacionPendiente);
            if (importe.signum() <= 0) {
                throw new IllegalArgumentException("El importe debe ser positivo");
            }
            BigDecimal saldoPendiente = obligacion.getSaldoLiquidacion() != null
                    ? obligacion.getSaldoLiquidacion()
                    : obligacion.getSaldoPendiente();
            if (refinanciacionPendiente != null) {
                saldoPendiente = refinanciacionPendiente.getSaldoPlan();
            } else if (financiacionPendiente != null) {
                saldoPendiente = financiacionPendiente.getSaldoTotalPendiente();
                if (obligacion.getSaldoLiquidacion() == null) {
                    saldoPendiente = obligacion.getSaldoPendiente()
                            .add(financiacionPendiente.getSaldoCargosPendiente());
                }
                if (obligacion.getSaldoLiquidacion() != null
                        && importe.compareTo(financiacionPendiente.getSaldoTotalPendiente()) > 0) {
                    throw new IllegalArgumentException("El pago supera el saldo total de la financiación");
                }
            }
            if (importe.compareTo(saldoPendiente) > 0) {
                throw new IllegalArgumentException("El pago supera el saldo pendiente de la obligación");
            }

            BigDecimal saldoDisponible = calcularSaldo(cuentaPagadora);
            if (saldoDisponible.compareTo(importe) < 0) {
                throw new IllegalArgumentException("No hay fondos suficientes en la cuenta para pagar la tarjeta");
            }

            Movimiento movimientoPago = new Movimiento(
                    cuentaPagadora, categoria, cuentaPagadora.getMoneda(), TipoMovimiento.EGRESO,
                    importe, fechaHora, descripcion, FormaPago.TRANSFERENCIA
            );

            BigDecimal importeFinanciacion = BigDecimal.ZERO.setScale(2);
            BigDecimal importeRefinanciacion = BigDecimal.ZERO.setScale(2);
            BigDecimal importeObligacion = importe;
            if (refinanciacionPendiente != null) {
                importeRefinanciacion = importe;
                importeObligacion = BigDecimal.ZERO.setScale(2);
            } else if (financiacionPendiente != null) {
                importeFinanciacion = importe.min(financiacionPendiente.getSaldoTotalPendiente());
                importeObligacion = importe.subtract(importeFinanciacion);
            }

            if (refinanciacionPendiente != null) {
                refinanciacionPendiente.registrarPago(importe);
            } else if (financiacionPendiente != null) {
                if (obligacion.getSaldoLiquidacion() != null) {
                    obligacion.registrarPagoFinanciacion(financiacionPendiente, importe);
                } else {
                    BigDecimal pagoFinanciacion = importe.min(financiacionPendiente.getSaldoTotalPendiente());
                    obligacion.registrarPagoFinanciacion(financiacionPendiente, pagoFinanciacion);
                    BigDecimal restante = importe.subtract(pagoFinanciacion);
                    if (restante.signum() > 0) {
                        obligacion.registrarPago(restante);
                    }
                }
            } else if (obligacion.getSaldoLiquidacion() != null) {
                obligacion.registrarPagoLiquidacion(importe);
            } else {
                obligacion.registrarPago(importe);
            }
            movimientoRepository.guardar(movimientoPago);
            PagoTarjeta pagoTarjeta = new PagoTarjeta(
                    obligacion,
                    financiacionPendiente,
                    movimientoPago,
                    cuentaPagadora,
                    categoria,
                    cuentaPagadora.getMoneda(),
                    importe,
                    importeFinanciacion,
                    importeObligacion,
                    importeRefinanciacion,
                    fechaHora
            );
            entityManager.persist(pagoTarjeta);
            entityManager.flush();
            transaction.commit();
            return obligacion;
        } catch (RuntimeException e) {
            if (transaction.isActive()) transaction.rollback();
            throw e;
        }
    }

    public PagoTarjeta revertirUltimoPago(Long obligacionId,
                                              Long usuarioId,
                                              LocalDateTime fechaHoraReversion) {
        Objects.requireNonNull(obligacionId, "El id de la obligación es obligatorio");
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");
        Objects.requireNonNull(fechaHoraReversion, "La fecha de reversión es obligatoria");

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            PagoTarjeta pago = entityManager.createQuery(
                    """
                    SELECT p
                    FROM PagoTarjeta p
                    WHERE p.obligacion.id = :obligacionId
                      AND p.estado = ar.com.agmilevecich.sofp.domain.EstadoPagoTarjeta.ACTIVO
                    ORDER BY p.fechaHora DESC, p.id DESC
                    """,
                    PagoTarjeta.class
            )
            .setParameter("obligacionId", obligacionId)
            .setMaxResults(1)
            .getResultStream()
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No existe un pago activo para revertir"));

            validarPropietario(usuarioId, pago.getObligacion());

            if (pago.getObligacion().getEstado() == ar.com.agmilevecich.sofp.domain.EstadoObligacion.REFINANCIADA
                    && pago.getRefinanciacion() == null) {
                throw new IllegalStateException("La obligación no admite reversión del pago de origen después de refinanciarse");
            }
            if (pago.getObligacion().getEstado() == ar.com.agmilevecich.sofp.domain.EstadoObligacion.ANULADA) {
                throw new IllegalStateException("La obligación no admite reversión en su estado actual");
            }

            if (pago.getFinanciacion() != null) {
                boolean hayCargoPosterior = pago.getFinanciacion().getCargos().stream()
                        .anyMatch(cargo -> cargo.getFechaGeneracion().isAfter(pago.getFechaHora().toLocalDate()));
                if (hayCargoPosterior) {
                    throw new IllegalStateException("No se puede revertir un pago con cargos posteriores");
                }
                if (pago.getImporteFinanciacion().signum() > 0) {
                    pago.getFinanciacion().revertirPago(pago.getImporteFinanciacion());
                }
            }

            if (pago.getRefinanciacion() != null
                    && pago.getImporteRefinanciacion().signum() > 0) {
                pago.getRefinanciacion().revertirPago(pago.getImporteRefinanciacion());
            }

            if (pago.getImporteObligacion().signum() > 0) {
                pago.getObligacion().revertirPago(pago.getImporteObligacion());
            }

            Movimiento movimientoReversion = new Movimiento(
                    pago.getCuentaPagadora(),
                    pago.getCategoria(),
                    pago.getCuentaPagadora().getMoneda(),
                    TipoMovimiento.INGRESO,
                    pago.getImporte(),
                    fechaHoraReversion,
                    "Reversión de pago de tarjeta",
                    FormaPago.TRANSFERENCIA
            );
            movimientoRepository.guardar(movimientoReversion);
            pago.marcarRevertido(fechaHoraReversion);
            entityManager.flush();
            transaction.commit();
            return pago;
        } catch (RuntimeException e) {
            if (transaction.isActive()) transaction.rollback();
            throw e;
        }
    }

    private void actualizarInteresesSiCorresponde(Financiacion financiacion,
                                                   java.time.LocalDate fecha,
                                                   Obligacion obligacion) {
        if (financiacion == null || !financiacion.estaPendiente()) {
            return;
        }
        FinanciacionService financiacionService = new FinanciacionService(entityManager);
        Long cuentaId = obligacion.getMovimientoOrigen().getCuenta().getId();
        if (financiacionService.buscarTasaVigente(cuentaId, TipoTasaInteres.TNA_FINANCIERA, fecha).isPresent()
                && fecha.isAfter(financiacion.getFechaUltimoCalculoInteres())) {
            financiacionService.calcularInteres(
                    financiacion.getId(), fecha,
                    obligacion.getMovimientoOrigen().getCuenta().getPerfilFinanciero().getUsuario().getId()
            );
        }
        if (financiacionService.buscarTasaVigente(cuentaId, TipoTasaInteres.TNA_PUNITORIA, fecha).isPresent()
                && fecha.isAfter(financiacion.getFechaUltimoCalculoPunitorio())) {
            financiacionService.calcularPunitorio(
                    financiacion.getId(), fecha,
                    obligacion.getMovimientoOrigen().getCuenta().getPerfilFinanciero().getUsuario().getId()
            );
        }
    }

    private void validarMonedaPagadora(Obligacion obligacion,
                                        Cuenta cuentaPagadora,
                                        Financiacion financiacionPendiente,
                                        Refinanciacion refinanciacionPendiente) {
        var monedaEsperada = refinanciacionPendiente != null
                ? refinanciacionPendiente.getMoneda()
                : financiacionPendiente != null
                    ? financiacionPendiente.getMoneda()
                    : obligacion.getSaldoLiquidacion() != null
                        ? obligacion.getMonedaLiquidacion()
                        : obligacion.getMonedaOriginal();
        if (!Objects.equals(cuentaPagadora.getMoneda(), monedaEsperada)) {
            throw new IllegalArgumentException("La cuenta pagadora y la moneda de pago de la obligación deben coincidir");
        }
    }

    private Financiacion buscarFinanciacionPendiente(Obligacion obligacion, LocalDateTime fechaHora) {
        return obligacion.getFinanciaciones().stream()
                .filter(Financiacion::estaPendiente)
                .filter(financiacion -> !fechaHora.toLocalDate().isBefore(financiacion.getFechaInicio()))
                .findFirst()
                .orElse(null);
    }

    private Refinanciacion buscarRefinanciacionPendiente(Obligacion obligacion, LocalDateTime fechaHora) {
        return entityManager.createQuery(
                """
                SELECT r
                FROM Refinanciacion r
                WHERE r.obligacionOrigen.id = :obligacionId
                  AND r.estado = ar.com.agmilevecich.sofp.domain.EstadoRefinanciacion.ACTIVA
                  AND r.fechaInicio <= :fecha
                ORDER BY r.id DESC
                """,
                Refinanciacion.class
        )
        .setParameter("obligacionId", obligacion.getId())
        .setParameter("fecha", fechaHora.toLocalDate())
        .setMaxResults(1)
        .getResultStream()
        .findFirst()
        .orElse(null);
    }

    private void validarFechaPago(Obligacion obligacion, LocalDateTime fechaHora) {
        LocalDateTime fechaOrigen = obligacion.getFechaOrigen();
        if (fechaHora.isBefore(fechaOrigen)) {
            throw new IllegalArgumentException("La fecha de pago no puede ser anterior al consumo que origina la obligación");
        }
        if (fechaHora.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha de pago no puede ser futura");
        }
    }

    private BigDecimal calcularSaldo(Cuenta cuenta) {
        BigDecimal saldo = BigDecimal.ZERO;
        List<Movimiento> movimientos = movimientoRepository.listarPorCuenta(cuenta.getId());
        for (Movimiento movimiento : movimientos) {
            if (movimiento.getTipoMovimiento() == TipoMovimiento.INGRESO) saldo = saldo.add(movimiento.getImporte());
            else if (movimiento.getTipoMovimiento() == TipoMovimiento.EGRESO && movimiento.getFormaPago() != FormaPago.TARJETA_CREDITO) saldo = saldo.subtract(movimiento.getImporte());
        }
        return saldo;
    }

    private void validarPropietario(Long usuarioId, Obligacion obligacion) {
        Long propietarioId = obligacion.getMovimientoOrigen().getCuenta().getPerfilFinanciero().getUsuario().getId();
        if (!Objects.equals(propietarioId, usuarioId)) throw new IllegalArgumentException("La obligación no pertenece al usuario autorizado");
    }

    private void validarPropietario(Long usuarioId, Cuenta cuenta) {
        if (!Objects.equals(cuenta.getPerfilFinanciero().getUsuario().getId(), usuarioId)) throw new IllegalArgumentException("El usuario no es propietario de la cuenta");
    }

    private void validarPropietario(Long usuarioId, Categoria categoria) {
        if (!Objects.equals(categoria.getPerfilFinanciero().getUsuario().getId(), usuarioId)) throw new IllegalArgumentException("El usuario no es propietario de la categoría");
    }
}
