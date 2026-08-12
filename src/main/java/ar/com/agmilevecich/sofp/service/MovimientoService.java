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

        return movimientoRepository.buscarPorId(id);
    }

    public List<Movimiento> listarTodos() {

        return movimientoRepository.listarTodos();
    }

    public List<Movimiento> listarPorCuenta(Long cuentaId) {

        return movimientoRepository.listarPorCuenta(cuentaId);
    }

    public List<Movimiento> listarPorCategoria(Long categoriaId) {

        return movimientoRepository.listarPorCategoria(categoriaId);
    }
}