package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.EvolucionSaldoCuenta;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.Movimiento;
import ar.com.agmilevecich.sofp.domain.TipoCuenta;
import ar.com.agmilevecich.sofp.domain.TipoMovimiento;
import ar.com.agmilevecich.sofp.persistence.CuentaRepository;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CuentaService {

    private final CuentaRepository cuentaRepository;
    private final MovimientoRepository movimientoRepository;
    private final EntityManager entityManager;

    public CuentaService(
            CuentaRepository cuentaRepository,
            MovimientoRepository movimientoRepository,
            EntityManager entityManager) {

        this.cuentaRepository = Objects.requireNonNull(cuentaRepository, "El CuentaRepository es obligatorio");
        this.movimientoRepository = Objects.requireNonNull(movimientoRepository, "El MovimientoRepository es obligatorio");
        this.entityManager = Objects.requireNonNull(entityManager, "El EntityManager es obligatorio");
    }

    public Cuenta registrar(Cuenta cuenta, Long usuarioId) {
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");
        Objects.requireNonNull(cuenta, "La cuenta es obligatoria");
        validarPropietario(usuarioId, cuenta);
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Cuenta registrada = cuentaRepository.guardar(cuenta);
            entityManager.flush();
            transaction.commit();
            return registrada;
        } catch (RuntimeException e) {
            if (transaction.isActive()) transaction.rollback();
            throw e;
        }
    }

    public Optional<Cuenta> buscarPorId(Long id, Long usuarioId) {
        Objects.requireNonNull(id, "El id de la cuenta es obligatorio");
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");
        return Optional.of(obtenerCuentaAutorizada(id, usuarioId));
    }

    public List<Cuenta> listarPorPerfilFinanciero(Long perfilFinancieroId, Long usuarioId) {
        Objects.requireNonNull(perfilFinancieroId, "El id del perfil financiero es obligatorio");
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");

        Long propietarioId = entityManager.createQuery(
                "SELECT p.usuario.id FROM PerfilFinanciero p WHERE p.id = :perfilId",
                Long.class
        ).setParameter("perfilId", perfilFinancieroId).getResultStream().findFirst().orElseThrow(
                () -> new IllegalArgumentException("No existe el perfil financiero con id " + perfilFinancieroId)
        );

        if (!Objects.equals(propietarioId, usuarioId)) {
            throw new IllegalArgumentException("El usuario no es propietario del perfil financiero");
        }

        return cuentaRepository.listarPorPerfilFinanciero(perfilFinancieroId);
    }

    public BigDecimal calcularSaldo(Long cuentaId, Long usuarioId) {
        Objects.requireNonNull(cuentaId, "El id de la cuenta es obligatorio");
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");
        obtenerCuentaAutorizada(cuentaId, usuarioId);

        return calcularSaldoInterno(cuentaId);
    }

    public List<EvolucionSaldoCuenta> obtenerEvolucionSaldo(Long cuentaId, Long usuarioId) {
        Objects.requireNonNull(cuentaId, "El id de la cuenta es obligatorio");
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");
        obtenerCuentaAutorizada(cuentaId, usuarioId);

        return obtenerEvolucionSaldoInterno(cuentaId);
    }

    /* API interna de compatibilidad para tests y coordinación interna del paquete. */
    Cuenta registrar(Cuenta cuenta) {
        Objects.requireNonNull(cuenta, "La cuenta es obligatoria");
        return cuentaRepository.guardar(cuenta);
    }

    Optional<Cuenta> buscarPorId(Long id) {
        Objects.requireNonNull(id, "El id de la cuenta es obligatorio");
        return cuentaRepository.buscarPorId(id);
    }

    List<Cuenta> listarTodas() {
        return cuentaRepository.listarTodas();
    }

    List<Cuenta> listarPorPerfilFinanciero(Long perfilFinancieroId) {
        Objects.requireNonNull(perfilFinancieroId, "El id del perfil financiero es obligatorio");
        return cuentaRepository.listarPorPerfilFinanciero(perfilFinancieroId);
    }

    BigDecimal calcularSaldo(Long cuentaId) {
        Objects.requireNonNull(cuentaId, "El id de la cuenta es obligatorio");
        return calcularSaldoInterno(cuentaId);
    }

    List<EvolucionSaldoCuenta> obtenerEvolucionSaldo(Long cuentaId) {
        Objects.requireNonNull(cuentaId, "El id de la cuenta es obligatorio");
        return obtenerEvolucionSaldoInterno(cuentaId);
    }

    private BigDecimal calcularSaldoInterno(Long cuentaId) {
        List<Movimiento> movimientos = movimientoRepository.listarPorCuenta(cuentaId);
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

    private List<EvolucionSaldoCuenta> obtenerEvolucionSaldoInterno(Long cuentaId) {
        List<EvolucionSaldoCuenta> evolucion = new ArrayList<>();
        BigDecimal saldo = BigDecimal.ZERO;
        for (Movimiento movimiento : movimientoRepository.listarPorCuenta(cuentaId)) {
            if (movimiento.getTipoMovimiento() == TipoMovimiento.INGRESO) {
                saldo = saldo.add(movimiento.getImporte());
            } else if (movimiento.getTipoMovimiento() == TipoMovimiento.EGRESO) {
                saldo = saldo.subtract(movimiento.getImporte());
            }
            evolucion.add(new EvolucionSaldoCuenta(movimiento.getFechaHora(), saldo));
        }
        return evolucion;
    }

    public Cuenta modificarNombre(Long cuentaId, Long usuarioId, String nombre) {
        validarIds(cuentaId, usuarioId);
        Objects.requireNonNull(nombre, "El nombre es obligatorio");
        Cuenta cuenta = obtenerCuentaAutorizada(cuentaId, usuarioId);
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            cuenta.renombrar(nombre);
            Cuenta actualizada = cuentaRepository.guardar(cuenta);
            entityManager.flush();
            transaction.commit();
            return actualizada;
        } catch (RuntimeException e) {
            if (transaction.isActive()) transaction.rollback();
            throw e;
        }
    }

    public Cuenta modificarIdentificadorExterno(Long cuentaId, Long usuarioId, String identificadorExterno) {
        validarIds(cuentaId, usuarioId);
        Cuenta cuenta = obtenerCuentaAutorizada(cuentaId, usuarioId);
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            cuenta.cambiarIdentificadorExterno(identificadorExterno);
            Cuenta actualizada = cuentaRepository.guardar(cuenta);
            entityManager.flush();
            transaction.commit();
            return actualizada;
        } catch (RuntimeException e) {
            if (transaction.isActive()) transaction.rollback();
            throw e;
        }
    }

    public Cuenta modificarTipoCuenta(Long cuentaId, Long usuarioId, TipoCuenta tipoCuenta) {
        validarIds(cuentaId, usuarioId);
        Objects.requireNonNull(tipoCuenta, "El tipo de cuenta es obligatorio");
        Cuenta cuenta = obtenerCuentaAutorizada(cuentaId, usuarioId);
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            cuenta.cambiarTipoCuenta(tipoCuenta);
            Cuenta actualizada = cuentaRepository.guardar(cuenta);
            entityManager.flush();
            transaction.commit();
            return actualizada;
        } catch (RuntimeException e) {
            if (transaction.isActive()) transaction.rollback();
            throw e;
        }
    }

    public Cuenta modificarInstitucionFinanciera(Long cuentaId, Long usuarioId, InstitucionFinanciera institucionFinanciera) {
        validarIds(cuentaId, usuarioId);
        Objects.requireNonNull(institucionFinanciera, "La institución financiera es obligatoria");
        Cuenta cuenta = obtenerCuentaAutorizada(cuentaId, usuarioId);
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            cuenta.cambiarInstitucionFinanciera(institucionFinanciera);
            Cuenta actualizada = cuentaRepository.guardar(cuenta);
            entityManager.flush();
            transaction.commit();
            return actualizada;
        } catch (RuntimeException e) {
            if (transaction.isActive()) transaction.rollback();
            throw e;
        }
    }

    public Cuenta modificarMoneda(Long cuentaId, Long usuarioId, Moneda moneda) {
        validarIds(cuentaId, usuarioId);
        Objects.requireNonNull(moneda, "La moneda es obligatoria");
        Cuenta cuenta = obtenerCuentaAutorizada(cuentaId, usuarioId);
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            cuenta.cambiarMoneda(moneda);
            Cuenta actualizada = cuentaRepository.guardar(cuenta);
            entityManager.flush();
            transaction.commit();
            return actualizada;
        } catch (RuntimeException e) {
            if (transaction.isActive()) transaction.rollback();
            throw e;
        }
    }

    public Cuenta activar(Long cuentaId, Long usuarioId) {
        validarIds(cuentaId, usuarioId);
        Cuenta cuenta = obtenerCuentaAutorizada(cuentaId, usuarioId);
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            cuenta.activar();
            Cuenta actualizada = cuentaRepository.guardar(cuenta);
            entityManager.flush();
            transaction.commit();
            return actualizada;
        } catch (RuntimeException e) {
            if (transaction.isActive()) transaction.rollback();
            throw e;
        }
    }

    public Cuenta desactivar(Long cuentaId, Long usuarioId) {
        validarIds(cuentaId, usuarioId);
        Cuenta cuenta = obtenerCuentaAutorizada(cuentaId, usuarioId);
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            cuenta.desactivar();
            Cuenta actualizada = cuentaRepository.guardar(cuenta);
            entityManager.flush();
            transaction.commit();
            return actualizada;
        } catch (RuntimeException e) {
            if (transaction.isActive()) entityManager.getTransaction().rollback();
            throw e;
        }
    }

    private Cuenta obtenerCuenta(Long cuentaId) {
        return cuentaRepository.buscarPorId(cuentaId).orElseThrow(
                () -> new IllegalArgumentException("No existe una cuenta con id " + cuentaId));
    }

    private Cuenta obtenerCuentaAutorizada(Long cuentaId, Long usuarioId) {
        Cuenta cuenta = obtenerCuenta(cuentaId);
        validarPropietario(usuarioId, cuenta);
        return cuenta;
    }

    private void validarPropietario(Long usuarioId, Cuenta cuenta) {
        Long propietarioId = cuenta.getPerfilFinanciero().getUsuario().getId();
        if (!Objects.equals(propietarioId, usuarioId)) {
            throw new IllegalArgumentException("El usuario no es propietario de la cuenta");
        }
    }

    private void validarIds(Long cuentaId, Long usuarioId) {
        Objects.requireNonNull(cuentaId, "El id de la cuenta es obligatorio");
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");
    }

    public void eliminar(Long cuentaId, Long usuarioId) {
        validarIds(cuentaId, usuarioId);
        Cuenta cuenta = obtenerCuentaAutorizada(cuentaId, usuarioId);
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            cuentaRepository.eliminar(cuenta);
            entityManager.flush();
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction.isActive()) transaction.rollback();
            throw e;
        }
    }
}
