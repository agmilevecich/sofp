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
            String descripcion) {

        Objects.requireNonNull(
                movimientoId,
                "El id del movimiento es obligatorio"
        );

        Objects.requireNonNull(
                descripcion,
                "La descripción es obligatoria"
        );

        Movimiento movimiento =
                obtenerMovimiento(movimientoId);

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
            String observaciones) {

        Objects.requireNonNull(
                movimientoId,
                "El id del movimiento es obligatorio"
        );

        Movimiento movimiento =
                obtenerMovimiento(movimientoId);

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
            Categoria categoria) {

        Objects.requireNonNull(
                movimientoId,
                "El id del movimiento es obligatorio"
        );

        Objects.requireNonNull(
                categoria,
                "La categoría es obligatoria"
        );

        Movimiento movimiento =
                obtenerMovimiento(movimientoId);

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
            TipoMovimiento tipoMovimiento) {

        Objects.requireNonNull(
                movimientoId,
                "El id del movimiento es obligatorio"
        );

        Objects.requireNonNull(
                tipoMovimiento,
                "El tipo de movimiento es obligatorio"
        );

        Movimiento movimiento =
                obtenerMovimiento(movimientoId);

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
            BigDecimal importe) {

        Objects.requireNonNull(
                movimientoId,
                "El id del movimiento es obligatorio"
        );

        Objects.requireNonNull(
                importe,
                "El importe es obligatorio"
        );

        Movimiento movimiento =
                obtenerMovimiento(movimientoId);

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
            LocalDateTime fechaHora) {

        Objects.requireNonNull(
                movimientoId,
                "El id del movimiento es obligatorio"
        );

        Objects.requireNonNull(
                fechaHora,
                "La fecha y hora son obligatorias"
        );

        Movimiento movimiento =
                obtenerMovimiento(movimientoId);

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

    public void eliminar(Long movimientoId) {

        Objects.requireNonNull(
                movimientoId,
                "El id del movimiento es obligatorio"
        );

        Movimiento movimiento =
                obtenerMovimiento(movimientoId);

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
}
