package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.domain.Cuota;
import ar.com.agmilevecich.sofp.domain.Movimiento;
import ar.com.agmilevecich.sofp.domain.Obligacion;
import ar.com.agmilevecich.sofp.domain.TipoCambio;
import ar.com.agmilevecich.sofp.persistence.ObligacionRepository;
import ar.com.agmilevecich.sofp.persistence.TipoCambioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Coordina la persistencia de obligaciones originadas por compras con tarjeta de crédito. */
public class ObligacionService {

    private final EntityManager entityManager;
    private final ObligacionRepository obligacionRepository;
    private final TipoCambioRepository tipoCambioRepository;

    public ObligacionService(EntityManager entityManager,
                             ObligacionRepository obligacionRepository) {
        this(entityManager, obligacionRepository, new TipoCambioRepository(entityManager));
    }

    public ObligacionService(EntityManager entityManager,
                             ObligacionRepository obligacionRepository,
                             TipoCambioRepository tipoCambioRepository) {
        this.entityManager = Objects.requireNonNull(
                entityManager,
                "El EntityManager es obligatorio"
        );
        this.obligacionRepository = Objects.requireNonNull(
                obligacionRepository,
                "El ObligacionRepository es obligatorio"
        );
        this.tipoCambioRepository = Objects.requireNonNull(
                tipoCambioRepository,
                "El TipoCambioRepository es obligatorio"
        );
    }

    public Obligacion registrar(Movimiento movimientoOrigen) {
        Obligacion obligacion = new Obligacion(movimientoOrigen);
        return guardar(obligacion);
    }

    /** Registra una obligación y persiste sus cuotas dentro de la misma transacción. */
    public Obligacion registrar(Movimiento movimientoOrigen, int cantidadCuotas) {
        Objects.requireNonNull(movimientoOrigen, "El movimiento origen es obligatorio");
        if (cantidadCuotas < 1) {
            throw new IllegalArgumentException("La cantidad de cuotas debe ser positiva");
        }

        if (entityManager.getTransaction().isActive()) {
            return registrarEnTransaccion(movimientoOrigen, cantidadCuotas);
        }

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Obligacion guardada = registrarEnTransaccion(movimientoOrigen, cantidadCuotas);
            transaction.commit();
            return guardada;
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    private Obligacion registrarEnTransaccion(Movimiento movimientoOrigen, int cantidadCuotas) {
        Obligacion obligacion = new Obligacion(movimientoOrigen);
        obligacion.generarCuotas(cantidadCuotas);
        Obligacion guardada = obligacionRepository.guardar(obligacion);
        entityManager.flush();
        return guardada;
    }

    /**
     * Valora el importe que corresponde al ciclo de cierre indicado.
     * En obligaciones financiadas, la valorización pertenece a la cuota de ese ciclo;
     * en obligaciones sin cuotas, se mantiene la valorización a nivel de obligación.
     */
    public List<Obligacion> cerrarCiclo(Long cuentaId, LocalDate fechaCierre) {
        Objects.requireNonNull(cuentaId, "El id de la cuenta es obligatorio");
        Objects.requireNonNull(fechaCierre, "La fecha de cierre es obligatoria");

        if (entityManager.getTransaction().isActive()) {
            return cerrarCicloEnTransaccion(cuentaId, fechaCierre);
        }

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            List<Obligacion> obligaciones = cerrarCicloEnTransaccion(cuentaId, fechaCierre);
            transaction.commit();
            return obligaciones;
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    private List<Obligacion> cerrarCicloEnTransaccion(Long cuentaId, LocalDate fechaCierre) {
        List<Obligacion> obligaciones = obligacionRepository.listarPorCuentaYCierreCiclo(
                cuentaId,
                fechaCierre
        );

        for (Obligacion obligacion : obligaciones) {
            if (obligacion.getMonedaOriginal().equals(obligacion.getMonedaLiquidacion())) {
                continue;
            }

            Optional<TipoCambio> tipoCambio = tipoCambioRepository.buscarPorMonedasYFecha(
                    obligacion.getMonedaOriginal(),
                    obligacion.getMonedaLiquidacion(),
                    fechaCierre
            );

            TipoCambio cambio = tipoCambio.orElseThrow(() -> new IllegalArgumentException(
                    "No existe cotización histórica para cerrar la obligación " + obligacion.getId()
            ));

            if (obligacion.getCuotas().isEmpty()) {
                obligacion.valorarCierre(cambio);
            } else {
                obligacion.getCuotas().stream()
                        .filter(cuota -> fechaCierre.equals(cuota.getFechaCierreCiclo()))
                        .filter(cuota -> cuota.getSaldoPendiente().signum() > 0)
                        .findFirst()
                        .ifPresent(cuota -> {
                            cuota.valorarCierre(cambio);
                        });
            }
        }

        entityManager.flush();
        return obligaciones;
    }

    /**
     * Liquida una obligación multidivisa usando la última cotización disponible
     * hasta el instante de cancelación.
     */
    public Obligacion liquidar(Long obligacionId,
                               LocalDateTime fechaHoraLiquidacion,
                               Long usuarioId) {
        Objects.requireNonNull(obligacionId, "El id de la obligación es obligatorio");
        Objects.requireNonNull(
                fechaHoraLiquidacion,
                "La fecha y hora de liquidación son obligatorias"
        );
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            Obligacion obligacion = obligacionRepository.buscarPorId(obligacionId)
                    .orElseThrow(() -> new IllegalArgumentException("La obligación no existe"));

            Long propietarioId = obligacion.getMovimientoOrigen()
                    .getCuenta()
                    .getPerfilFinanciero()
                    .getUsuario()
                    .getId();

            if (!usuarioId.equals(propietarioId)) {
                throw new IllegalArgumentException("La obligación no pertenece al usuario autorizado");
            }

            if (obligacion.getMonedaOriginal().equals(obligacion.getMonedaLiquidacion())) {
                throw new IllegalArgumentException("La obligación ya está en la moneda de liquidación");
            }

            TipoCambio cambio = tipoCambioRepository.buscarPorMonedasYFechaHora(
                            obligacion.getMonedaOriginal(),
                            obligacion.getMonedaLiquidacion(),
                            fechaHoraLiquidacion
                    )
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No existe cotización histórica para liquidar la obligación " + obligacion.getId()
                    ));

            obligacion.liquidar(cambio);
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

    /** Registra un pago verificando que la obligación pertenezca al usuario autorizado. */
    public Obligacion registrarPago(Long obligacionId, BigDecimal importe, Long usuarioId) {
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");
        Objects.requireNonNull(obligacionId, "El id de la obligación es obligatorio");

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            Obligacion obligacion = obligacionRepository.buscarPorId(obligacionId)
                    .orElseThrow(() -> new IllegalArgumentException("La obligación no existe"));

            Long propietarioId = obligacion.getMovimientoOrigen()
                    .getCuenta()
                    .getPerfilFinanciero()
                    .getUsuario()
                    .getId();

            if (!usuarioId.equals(propietarioId)) {
                throw new IllegalArgumentException("La obligación no pertenece al usuario autorizado");
            }

            obligacion.registrarPago(importe);
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

    public Optional<Obligacion> buscarPorId(Long id) {
        return obligacionRepository.buscarPorId(id);
    }

    public Optional<Obligacion> buscarPorMovimientoOrigen(Long movimientoId) {
        return obligacionRepository.buscarPorMovimientoOrigen(movimientoId);
    }

    public List<Obligacion> listarTodas() {
        return obligacionRepository.listarTodas();
    }

    public List<Obligacion> listarPorUsuario(Long usuarioId) {
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");
        return obligacionRepository.listarPorUsuario(usuarioId);
    }

    private Obligacion guardar(Obligacion obligacion) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Obligacion guardada = obligacionRepository.guardar(obligacion);
            entityManager.flush();
            transaction.commit();
            return guardada;
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }
}
