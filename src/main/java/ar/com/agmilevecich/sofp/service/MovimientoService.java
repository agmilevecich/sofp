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

    public MovimientoService(
            EntityManager entityManager,
            MovimientoRepository movimientoRepository) {

        this.entityManager = Objects.requireNonNull(
                entityManager,
                "El EntityManager es obligatorio"
        );

        this.movimientoRepository = Objects.requireNonNull(
                movimientoRepository,
                "El repositorio de movimientos es obligatorio"
        );
    }

    public Movimiento registrar(
            Cuenta cuenta,
            Categoria categoria,
            TipoMovimiento tipoMovimiento,
            BigDecimal importe,
            LocalDateTime fechaHora,
            String descripcion) {

        validarPerfilFinanciero(
                cuenta,
                categoria
        );

        if (!cuenta.isActiva()) {
            throw new IllegalArgumentException(
                    "No se puede registrar un movimiento en una cuenta desactivada"
            );
        }

        Movimiento movimiento = new Movimiento(
                cuenta,
                categoria,
                tipoMovimiento,
                importe,
                fechaHora,
                descripcion
        );

        EntityTransaction transaction =
                entityManager.getTransaction();

        try {
            transaction.begin();

            Movimiento guardado =
                    movimientoRepository.guardar(movimiento);

            entityManager.flush();

            transaction.commit();

            return guardado;

        } catch (RuntimeException e) {

            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        }
    }

    public Optional<Movimiento> buscarPorId(Long id) {

        Objects.requireNonNull(
                id,
                "El id del movimiento es obligatorio"
        );

        return movimientoRepository.buscarPorId(id);
    }

    public List<Movimiento> listarTodos() {

        return movimientoRepository.listarTodos();
    }

    public List<Movimiento> listarPorCuenta(Long cuentaId) {

        Objects.requireNonNull(
                cuentaId,
                "El id de la cuenta es obligatorio"
        );

        return movimientoRepository.listarPorCuenta(
                cuentaId
        );
    }

    public List<Movimiento> listarPorCategoria(Long categoriaId) {

        Objects.requireNonNull(
                categoriaId,
                "El id de la categoría es obligatorio"
        );

        return movimientoRepository.listarPorCategoria(
                categoriaId
        );
    }

    public Movimiento modificarDescripcion(
            Long movimientoId,
            Long usuarioId,
            String descripcion) {

        validarIds(
                movimientoId,
                usuarioId
        );

        Objects.requireNonNull(
                descripcion,
                "La descripción es obligatoria"
        );

        Movimiento movimiento =
                obtenerMovimientoAutorizado(
                        movimientoId,
                        usuarioId
                );

        EntityTransaction transaction =
                entityManager.getTransaction();

        try {
            transaction.begin();

            movimiento.cambiarDescripcion(descripcion);

            Movimiento actualizado =
                    movimientoRepository.guardar(movimiento);

            entityManager.flush();

            transaction.commit();

            return actualizado;

        } catch (RuntimeException e) {

            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        }
    }

    public Movimiento modificarObservaciones(
            Long movimientoId,
            Long usuarioId,
            String observaciones) {

        validarIds(
                movimientoId,
                usuarioId
        );

        Movimiento movimiento =
                obtenerMovimientoAutorizado(
                        movimientoId,
                        usuarioId
                );

        EntityTransaction transaction =
                entityManager.getTransaction();

        try {
            transaction.begin();

            movimiento.cambiarObservaciones(observaciones);

            Movimiento actualizado =
                    movimientoRepository.guardar(movimiento);

            entityManager.flush();

            transaction.commit();

            return actualizado;

        } catch (RuntimeException e) {

            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        }
    }

    public Movimiento cambiarCategoria(
            Long movimientoId,
            Long usuarioId,
            Categoria categoria) {

        validarIds(
                movimientoId,
                usuarioId
        );

        Objects.requireNonNull(
                categoria,
                "La categoría es obligatoria"
        );

        Movimiento movimiento =
                obtenerMovimientoAutorizado(
                        movimientoId,
                        usuarioId
                );

        validarPerfilFinanciero(
                movimiento.getCuenta(),
                categoria
        );

        EntityTransaction transaction =
                entityManager.getTransaction();

        try {
            transaction.begin();

            movimiento.cambiarCategoria(categoria);

            Movimiento actualizado =
                    movimientoRepository.guardar(movimiento);

            entityManager.flush();

            transaction.commit();

            return actualizado;

        } catch (RuntimeException e) {

            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        }
    }

    public Movimiento modificarTipoMovimiento(
            Long movimientoId,
            Long usuarioId,
            TipoMovimiento tipoMovimiento) {

        validarIds(
                movimientoId,
                usuarioId
        );

        Objects.requireNonNull(
                tipoMovimiento,
                "El tipo de movimiento es obligatorio"
        );

        Movimiento movimiento =
                obtenerMovimientoAutorizado(
                        movimientoId,
                        usuarioId
                );

        EntityTransaction transaction =
                entityManager.getTransaction();

        try {
            transaction.begin();

            movimiento.modificarTipoMovimiento(
                    tipoMovimiento
            );

            Movimiento actualizado =
                    movimientoRepository.guardar(movimiento);

            entityManager.flush();

            transaction.commit();

            return actualizado;

        } catch (RuntimeException e) {

            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        }
    }

    public Movimiento modificarImporte(
            Long movimientoId,
            Long usuarioId,
            BigDecimal importe) {

        validarIds(
                movimientoId,
                usuarioId
        );

        Objects.requireNonNull(
                importe,
                "El importe es obligatorio"
        );

        Movimiento movimiento =
                obtenerMovimientoAutorizado(
                        movimientoId,
                        usuarioId
                );

        EntityTransaction transaction =
                entityManager.getTransaction();

        try {
            transaction.begin();

            movimiento.cambiarImporte(importe);

            Movimiento actualizado =
                    movimientoRepository.guardar(movimiento);

            entityManager.flush();

            transaction.commit();

            return actualizado;

        } catch (RuntimeException e) {

            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        }
    }

    public Movimiento modificarFechaHora(
            Long movimientoId,
            Long usuarioId,
            LocalDateTime fechaHora) {

        validarIds(
                movimientoId,
                usuarioId
        );

        Objects.requireNonNull(
                fechaHora,
                "La fecha y hora son obligatorias"
        );

        Movimiento movimiento =
                obtenerMovimientoAutorizado(
                        movimientoId,
                        usuarioId
                );

        EntityTransaction transaction =
                entityManager.getTransaction();

        try {
            transaction.begin();

            movimiento.cambiarFechaHora(fechaHora);

            Movimiento actualizado =
                    movimientoRepository.guardar(movimiento);

            entityManager.flush();

            transaction.commit();

            return actualizado;

        } catch (RuntimeException e) {

            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        }
    }

    public void eliminar(
            Long movimientoId,
            Long usuarioId) {

        validarIds(
                movimientoId,
                usuarioId
        );

        Movimiento movimiento =
                obtenerMovimientoAutorizado(
                        movimientoId,
                        usuarioId
                );

        EntityTransaction transaction =
                entityManager.getTransaction();

        try {
            transaction.begin();

            movimientoRepository.eliminar(movimiento);

            entityManager.flush();

            transaction.commit();

        } catch (RuntimeException e) {

            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        }
    }

    private void validarPerfilFinanciero(
            Cuenta cuenta,
            Categoria categoria) {

        Objects.requireNonNull(
                cuenta,
                "La cuenta es obligatoria"
        );

        Objects.requireNonNull(
                categoria,
                "La categoría es obligatoria"
        );

        Long cuentaPerfilId =
                cuenta.getPerfilFinanciero().getId();

        Long categoriaPerfilId =
                categoria.getPerfilFinanciero().getId();

        if (!Objects.equals(
                cuentaPerfilId,
                categoriaPerfilId
        )) {
            throw new IllegalArgumentException(
                    "La cuenta y la categoría deben pertenecer al mismo perfil financiero"
            );
        }
    }

    private Movimiento obtenerMovimiento(Long movimientoId) {

        return movimientoRepository.buscarPorId(
                movimientoId
        ).orElseThrow(
                () -> new IllegalArgumentException(
                        "No existe un movimiento con id "
                                + movimientoId
                )
        );
    }

    private Movimiento obtenerMovimientoAutorizado(
            Long movimientoId,
            Long usuarioId) {

        Movimiento movimiento =
                obtenerMovimiento(movimientoId);

        if (!movimiento.getCuenta()
                .getPerfilFinanciero()
                .getUsuario()
                .getId()
                .equals(usuarioId)) {

            throw new IllegalArgumentException(
                    "El usuario no es propietario del movimiento"
            );
        }

        return movimiento;
    }

    private void validarIds(
            Long movimientoId,
            Long usuarioId) {

        Objects.requireNonNull(
                movimientoId,
                "El id del movimiento es obligatorio"
        );

        Objects.requireNonNull(
                usuarioId,
                "El id del usuario es obligatorio"
        );
    }
}
