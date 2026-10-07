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
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/** Coordina el pago de una obligación de tarjeta con la salida de fondos de una cuenta. */
public class PagoTarjetaService {

    private final EntityManager entityManager;
    private final MovimientoRepository movimientoRepository;
    private final ObligacionRepository obligacionRepository;
    private final Clock clock;

    public PagoTarjetaService(EntityManager entityManager,
                              MovimientoRepository movimientoRepository,
                              ObligacionRepository obligacionRepository) {
        this(entityManager, movimientoRepository, obligacionRepository, Clock.systemDefaultZone());
    }

    public PagoTarjetaService(EntityManager entityManager,
                              MovimientoRepository movimientoRepository,
                              ObligacionRepository obligacionRepository,
                              Clock clock) {
        this.entityManager = Objects.requireNonNull(entityManager, "El EntityManager es obligatorio");
        this.movimientoRepository = Objects.requireNonNull(movimientoRepository, "El MovimientoRepository es obligatorio");
        this.obligacionRepository = Objects.requireNonNull(obligacionRepository, "El ObligacionRepository es obligatorio");
        this.clock = Objects.requireNonNull(clock, "El Clock es obligatorio");
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
            Obligacion resultado = registrarPagoEnTransaccion(
                    obligacion, cuentaPagadora, categoria, importe, fechaHora, descripcion, usuarioId);
            transaction.commit();
            return resultado;
        } catch (RuntimeException e) {
            if (transaction.isActive()) transaction.rollback();
            throw e;
        }
    }

    /**
     * Registra el pago total pendiente de una tarjeta distribuyéndolo entre todas
     * sus obligaciones pendientes. Cada obligación conserva su propio movimiento
     * y trazabilidad de PagoTarjeta, pero toda la operación es atómica.
     */
    public BigDecimal registrarPagoTotalTarjeta(Long tarjetaId,
                                                 Cuenta cuentaPagadora,
                                                 Categoria categoria,
                                                 LocalDateTime fechaHora,
                                                 String descripcion,
                                                 Long usuarioId) {
        Objects.requireNonNull(tarjetaId, "El id de la tarjeta es obligatorio");
        Objects.requireNonNull(cuentaPagadora, "La cuenta pagadora es obligatoria");
        Objects.requireNonNull(categoria, "La categoría es obligatoria");
        Objects.requireNonNull(fechaHora, "La fecha y hora son obligatorias");
        Objects.requireNonNull(descripcion, "La descripción es obligatoria");
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            Cuenta tarjeta = entityManager.find(Cuenta.class, tarjetaId);
            if (tarjeta == null) {
                throw new IllegalArgumentException("La tarjeta no existe");
            }
            validarPropietario(usuarioId, tarjeta);
            if (tarjeta.getTipoCuenta() != ar.com.agmilevecich.sofp.domain.TipoCuenta.TARJETA_CREDITO) {
                throw new IllegalArgumentException("La cuenta indicada no es una tarjeta de crédito");
            }

            validarPropietario(usuarioId, cuentaPagadora);
            validarPropietario(usuarioId, categoria);
            if (!cuentaPagadora.isActiva()) {
                throw new IllegalArgumentException("No se puede pagar desde una cuenta desactivada");
            }
            if (cuentaPagadora.getTipoCuenta() == ar.com.agmilevecich.sofp.domain.TipoCuenta.TARJETA_CREDITO) {
                throw new IllegalArgumentException("La cuenta pagadora no puede ser una tarjeta de crédito");
            }
            if (!Objects.equals(cuentaPagadora.getPerfilFinanciero().getId(), categoria.getPerfilFinanciero().getId())) {
                throw new IllegalArgumentException("La cuenta y la categoría deben pertenecer al mismo perfil financiero");
            }

            List<Obligacion> obligaciones = obligacionRepository.listarPorUsuario(usuarioId).stream()
                    .filter(o -> o.getMovimientoOrigen().getCuenta().getId().equals(tarjetaId))
                    .filter(o -> o.getEstado() != ar.com.agmilevecich.sofp.domain.EstadoObligacion.PAGADA)
                    .filter(o -> o.getEstado() != ar.com.agmilevecich.sofp.domain.EstadoObligacion.ANULADA)
                    .toList();

            if (obligaciones.isEmpty()) {
                throw new IllegalArgumentException("La tarjeta no tiene deuda pendiente para pagar");
            }

            BigDecimal total = BigDecimal.ZERO.setScale(2);
            for (Obligacion obligacion : obligaciones) {
                validarFechaPago(obligacion, fechaHora);
                Financiacion financiacionPendiente = buscarFinanciacionPendiente(obligacion, fechaHora);
                Refinanciacion refinanciacionPendiente = buscarRefinanciacionPendiente(obligacion, fechaHora);
                actualizarInteresesSiCorresponde(financiacionPendiente, fechaHora.toLocalDate(), obligacion);
                validarMonedaPagadora(obligacion, cuentaPagadora, financiacionPendiente, refinanciacionPendiente);
                total = total.add(calcularSaldoExigible(
                        obligacion, financiacionPendiente, refinanciacionPendiente));
            }

            if (total.signum() <= 0) {
                throw new IllegalArgumentException("La tarjeta no tiene deuda pendiente para pagar");
            }

            BigDecimal saldoDisponible = calcularSaldo(cuentaPagadora);
            if (saldoDisponible.compareTo(total) < 0) {
                throw new IllegalArgumentException("No hay fondos suficientes en la cuenta para pagar la tarjeta");
            }

            for (Obligacion obligacion : obligaciones) {
                Financiacion financiacionPendiente = buscarFinanciacionPendiente(obligacion, fechaHora);
                Refinanciacion refinanciacionPendiente = buscarRefinanciacionPendiente(obligacion, fechaHora);
                BigDecimal importe = calcularSaldoExigible(
                        obligacion, financiacionPendiente, refinanciacionPendiente);
                if (importe.signum() > 0) {
                    registrarPagoEnTransaccion(
                            obligacion, cuentaPagadora, categoria, importe,
                            fechaHora, descripcion, usuarioId);
                }
            }

            transaction.commit();
            return total;
        } catch (RuntimeException e) {
            if (transaction.isActive()) transaction.rollback();
            throw e;
        }
    }

    private BigDecimal calcularSaldoExigible(Obligacion obligacion,
                                               Financiacion financiacionPendiente,
                                               Refinanciacion refinanciacionPendiente) {
        if (refinanciacionPendiente != null) {
            return refinanciacionPendiente.getSaldoPlan();
        }
        if (financiacionPendiente != null) {
            if (obligacion.getSaldoLiquidacion() == null) {
                return obligacion.getSaldoPendiente()
                        .add(financiacionPendiente.getSaldoCargosPendiente());
            }
            return financiacionPendiente.getSaldoTotalPendiente();
        }
        return obligacion.getSaldoLiquidacion() != null
                ? obligacion.getSaldoLiquidacion()
                : obligacion.getSaldoPendiente();
    }

    private Obligacion registrarPagoEnTransaccion(Obligacion obligacion,
                                                   Cuenta cuentaPagadora,
                                                   Categoria categoria,
                                                   BigDecimal importe,
                                                   LocalDateTime fechaHora,
                                                   String descripcion,
                                                   Long usuarioId) {
        validarPropietario(usuarioId, obligacion);
        validarPropietario(usuarioId, cuentaPagadora);
        validarPropietario(usuarioId, categoria);
        validarFechaPago(obligacion, fechaHora);
        if (!cuentaPagadora.isActiva()) {
            throw new IllegalArgumentException("No se puede pagar desde una cuenta desactivada");
        }
        if (cuentaPagadora.getTipoCuenta() == ar.com.agmilevecich.sofp.domain.TipoCuenta.TARJETA_CREDITO) {
            throw new IllegalArgumentException("La cuenta pagadora no puede ser una tarjeta de crédito");
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
        BigDecimal saldoPendiente = calcularSaldoExigible(
                obligacion, financiacionPendiente, refinanciacionPendiente);
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
                refinanciacionPendiente,
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
        return obligacion;
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

            if (fechaHoraReversion.isBefore(pago.getFechaHora())) {
                throw new IllegalArgumentException("La fecha de reversión no puede ser anterior al pago");
            }
            if (fechaHoraReversion.isAfter(LocalDateTime.now(clock))) {
                throw new IllegalArgumentException("La fecha de reversión no puede ser futura");
            }

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
            pago.marcarRevertido(fechaHoraReversion, movimientoReversion);
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
        if (fechaHora.isAfter(LocalDateTime.now(clock))) {
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
