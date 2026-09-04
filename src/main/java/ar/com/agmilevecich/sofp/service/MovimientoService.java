package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.Movimiento;
import ar.com.agmilevecich.sofp.domain.TipoMovimiento;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final EntityManager entityManager;

    public MovimientoService(EntityManager entityManager, MovimientoRepository movimientoRepository) {
        this.entityManager = Objects.requireNonNull(entityManager, "El EntityManager es obligatorio");
        this.movimientoRepository = Objects.requireNonNull(movimientoRepository, "El repositorio de movimientos es obligatorio");
    }

    public Movimiento registrar(Cuenta cuenta, Categoria categoria, TipoMovimiento tipoMovimiento,
                                BigDecimal importe, LocalDateTime fechaHora, String descripcion,
                                Long usuarioId) {
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");
        validarPropietario(usuarioId, cuenta);
        validarPropietario(usuarioId, categoria);
        validarPerfilFinanciero(cuenta, categoria);
        if (!cuenta.isActiva()) throw new IllegalArgumentException("No se puede registrar un movimiento en una cuenta desactivada");
        validarSaldoDisponible(cuenta, tipoMovimiento, importe, null);
        return guardar(new Movimiento(cuenta, categoria, tipoMovimiento, importe, fechaHora, descripcion));
    }

    public Optional<Movimiento> buscarPorId(Long id, Long usuarioId) {
        Objects.requireNonNull(id, "El id del movimiento es obligatorio");
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");
        return Optional.of(obtenerMovimientoAutorizado(id, usuarioId));
    }

    public List<Movimiento> listarPorCuenta(Long cuentaId, Long usuarioId) {
        Objects.requireNonNull(cuentaId, "El id de la cuenta es obligatorio");
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");
        validarPropietario(usuarioId, obtenerCuenta(cuentaId));
        return movimientoRepository.listarPorCuenta(cuentaId);
    }

    public List<Movimiento> listarPorCategoria(Long categoriaId, Long usuarioId) {
        Objects.requireNonNull(categoriaId, "El id de la categoría es obligatorio");
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");
        validarPropietario(usuarioId, obtenerCategoria(categoriaId));
        return movimientoRepository.listarPorCategoria(categoriaId);
    }

    /* API interna de compatibilidad para tests y coordinación interna del paquete. */
    Movimiento registrar(Cuenta cuenta, Categoria categoria, TipoMovimiento tipoMovimiento,
                         BigDecimal importe, LocalDateTime fechaHora, String descripcion) {
        validarPerfilFinanciero(cuenta, categoria);
        if (!cuenta.isActiva()) throw new IllegalArgumentException("No se puede registrar un movimiento en una cuenta desactivada");
        return guardar(new Movimiento(cuenta, categoria, tipoMovimiento, importe, fechaHora, descripcion));
    }

    Optional<Movimiento> buscarPorId(Long id) {
        Objects.requireNonNull(id, "El id del movimiento es obligatorio");
        return movimientoRepository.buscarPorId(id);
    }

    List<Movimiento> listarTodos() {
        return movimientoRepository.listarTodos();
    }

    List<Movimiento> listarPorCuenta(Long cuentaId) {
        Objects.requireNonNull(cuentaId, "El id de la cuenta es obligatorio");
        return movimientoRepository.listarPorCuenta(cuentaId);
    }

    List<Movimiento> listarPorCategoria(Long categoriaId) {
        Objects.requireNonNull(categoriaId, "El id de la categoría es obligatorio");
        return movimientoRepository.listarPorCategoria(categoriaId);
    }

    public Movimiento modificarDescripcion(Long movimientoId, Long usuarioId, String descripcion) {
        validarIds(movimientoId, usuarioId);
        Objects.requireNonNull(descripcion, "La descripción es obligatoria");
        Movimiento movimiento = obtenerMovimientoAutorizado(movimientoId, usuarioId);
        return modificar(movimiento, () -> movimiento.cambiarDescripcion(descripcion));
    }

    public Movimiento modificarObservaciones(Long movimientoId, Long usuarioId, String observaciones) {
        validarIds(movimientoId, usuarioId);
        Movimiento movimiento = obtenerMovimientoAutorizado(movimientoId, usuarioId);
        return modificar(movimiento, () -> movimiento.cambiarObservaciones(observaciones));
    }

    public Movimiento cambiarCategoria(Long movimientoId, Long usuarioId, Categoria categoria) {
        validarIds(movimientoId, usuarioId);
        Objects.requireNonNull(categoria, "La categoría es obligatoria");
        Movimiento movimiento = obtenerMovimientoAutorizado(movimientoId, usuarioId);
        validarPropietario(usuarioId, categoria);
        validarPerfilFinanciero(movimiento.getCuenta(), categoria);
        return modificar(movimiento, () -> movimiento.cambiarCategoria(categoria));
    }

    public Movimiento modificarTipoMovimiento(Long movimientoId, Long usuarioId, TipoMovimiento tipoMovimiento) {
        validarIds(movimientoId, usuarioId);
        Objects.requireNonNull(tipoMovimiento, "El tipo de movimiento es obligatorio");
        Movimiento movimiento = obtenerMovimientoAutorizado(movimientoId, usuarioId);
        validarSaldoDisponible(movimiento.getCuenta(), tipoMovimiento, movimiento.getImporte(), movimiento);
        return modificar(movimiento, () -> movimiento.modificarTipoMovimiento(tipoMovimiento));
    }

    public Movimiento modificarImporte(Long movimientoId, Long usuarioId, BigDecimal importe) {
        validarIds(movimientoId, usuarioId);
        Objects.requireNonNull(importe, "El importe es obligatorio");
        Movimiento movimiento = obtenerMovimientoAutorizado(movimientoId, usuarioId);
        validarSaldoDisponible(movimiento.getCuenta(), movimiento.getTipoMovimiento(), importe, movimiento);
        return modificar(movimiento, () -> movimiento.cambiarImporte(importe));
    }

    public Movimiento modificarFechaHora(Long movimientoId, Long usuarioId, LocalDateTime fechaHora) {
        validarIds(movimientoId, usuarioId);
        Objects.requireNonNull(fechaHora, "La fecha y hora son obligatorias");
        Movimiento movimiento = obtenerMovimientoAutorizado(movimientoId, usuarioId);
        return modificar(movimiento, () -> movimiento.cambiarFechaHora(fechaHora));
    }

    public void eliminar(Long movimientoId, Long usuarioId) {
        validarIds(movimientoId, usuarioId);
        Movimiento movimiento = obtenerMovimientoAutorizado(movimientoId, usuarioId);
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            movimientoRepository.eliminar(movimiento);
            entityManager.flush();
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction.isActive()) transaction.rollback();
            throw e;
        }
    }

    private Movimiento guardar(Movimiento movimiento) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Movimiento guardado = movimientoRepository.guardar(movimiento);
            entityManager.flush();
            transaction.commit();
            return guardado;
        } catch (RuntimeException e) {
            if (transaction.isActive()) transaction.rollback();
            throw e;
        }
    }

    private Movimiento modificar(Movimiento movimiento, Runnable cambio) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            cambio.run();
            Movimiento actualizado = movimientoRepository.guardar(movimiento);
            entityManager.flush();
            transaction.commit();
            return actualizado;
        } catch (RuntimeException e) {
            if (transaction.isActive()) transaction.rollback();
            throw e;
        }
    }

    private void validarSaldoDisponible(Cuenta cuenta, TipoMovimiento tipoMovimiento,
                                        BigDecimal importe, Movimiento movimientoActual) {
        if (tipoMovimiento != TipoMovimiento.EGRESO) return;

        BigDecimal saldoDisponible = calcularSaldo(cuenta.getId());
        if (movimientoActual != null) {
            if (movimientoActual.getTipoMovimiento() == TipoMovimiento.INGRESO) {
                saldoDisponible = saldoDisponible.subtract(movimientoActual.getImporte());
            } else if (movimientoActual.getTipoMovimiento() == TipoMovimiento.EGRESO) {
                saldoDisponible = saldoDisponible.add(movimientoActual.getImporte());
            }
        }

        if (saldoDisponible.compareTo(importe) < 0) {
            throw new IllegalArgumentException("No hay fondos suficientes en la cuenta para registrar el egreso");
        }
    }

    private BigDecimal calcularSaldo(Long cuentaId) {
        BigDecimal saldo = BigDecimal.ZERO;
        for (Movimiento movimiento : movimientoRepository.listarPorCuenta(cuentaId)) {
            if (movimiento.getTipoMovimiento() == TipoMovimiento.INGRESO) {
                saldo = saldo.add(movimiento.getImporte());
            } else if (movimiento.getTipoMovimiento() == TipoMovimiento.EGRESO) {
                saldo = saldo.subtract(movimiento.getImporte());
            }
        }
        return saldo;
    }

    private void validarPerfilFinanciero(Cuenta cuenta, Categoria categoria) {
        Objects.requireNonNull(cuenta, "La cuenta es obligatoria");
        Objects.requireNonNull(categoria, "La categoría es obligatoria");
        if (!Objects.equals(cuenta.getPerfilFinanciero().getId(), categoria.getPerfilFinanciero().getId())) {
            throw new IllegalArgumentException("La cuenta y la categoría deben pertenecer al mismo perfil financiero");
        }
    }

    private Movimiento obtenerMovimiento(Long movimientoId) {
        return movimientoRepository.buscarPorId(movimientoId).orElseThrow(
                () -> new IllegalArgumentException("No existe un movimiento con id " + movimientoId));
    }

    private Movimiento obtenerMovimientoAutorizado(Long movimientoId, Long usuarioId) {
        Movimiento movimiento = obtenerMovimiento(movimientoId);
        validarPropietario(usuarioId, movimiento.getCuenta());
        return movimiento;
    }

    private Cuenta obtenerCuenta(Long cuentaId) {
        Cuenta cuenta = entityManager.find(Cuenta.class, cuentaId);
        if (cuenta == null) throw new IllegalArgumentException("No existe una cuenta con id " + cuentaId);
        return cuenta;
    }

    private Categoria obtenerCategoria(Long categoriaId) {
        Categoria categoria = entityManager.find(Categoria.class, categoriaId);
        if (categoria == null) throw new IllegalArgumentException("No existe la categoría con id " + categoriaId);
        return categoria;
    }

    private void validarPropietario(Long usuarioId, Cuenta cuenta) {
        Objects.requireNonNull(cuenta, "La cuenta es obligatoria");
        if (!Objects.equals(cuenta.getPerfilFinanciero().getUsuario().getId(), usuarioId)) {
            throw new IllegalArgumentException("El usuario no es propietario de la cuenta");
        }
    }

    private void validarPropietario(Long usuarioId, Categoria categoria) {
        Objects.requireNonNull(categoria, "La categoría es obligatoria");
        if (!Objects.equals(categoria.getPerfilFinanciero().getUsuario().getId(), usuarioId)) {
            throw new IllegalArgumentException("El usuario no es propietario de la categoría");
        }
    }

    private void validarIds(Long movimientoId, Long usuarioId) {
        Objects.requireNonNull(movimientoId, "El id del movimiento es obligatorio");
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");
    }
}
