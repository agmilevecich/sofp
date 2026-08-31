package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.domain.Activo;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.Movimiento;
import ar.com.agmilevecich.sofp.domain.MovimientoActivo;
import ar.com.agmilevecich.sofp.domain.OperacionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMovimiento;
import ar.com.agmilevecich.sofp.domain.TipoMovimientoActivo;
import ar.com.agmilevecich.sofp.domain.TipoOperacionFinanciera;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import ar.com.agmilevecich.sofp.persistence.OperacionFinancieraRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class OperacionFinancieraService {

    private final EntityManager entityManager;
    private final MovimientoRepository movimientoRepository;
    private final OperacionFinancieraRepository operacionFinancieraRepository;

    public OperacionFinancieraService(
            EntityManager entityManager,
            MovimientoRepository movimientoRepository,
            OperacionFinancieraRepository operacionFinancieraRepository) {

        this.entityManager = Objects.requireNonNull(
                entityManager,
                "El EntityManager es obligatorio"
        );

        this.movimientoRepository = Objects.requireNonNull(
                movimientoRepository,
                "El repositorio de movimientos es obligatorio"
        );

        this.operacionFinancieraRepository =
                Objects.requireNonNull(
                        operacionFinancieraRepository,
                        "El repositorio de operaciones financieras es obligatorio"
                );
    }

    public OperacionFinanciera transferir(
            Long usuarioId,
            Cuenta cuentaOrigen,
            Cuenta cuentaDestino,
            Categoria categoriaOrigen,
            Categoria categoriaDestino,
            BigDecimal importe,
            LocalDateTime fechaHora,
            String descripcion) {

        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");
        Objects.requireNonNull(cuentaOrigen, "La cuenta de origen es obligatoria");
        Objects.requireNonNull(cuentaDestino, "La cuenta de destino es obligatoria");
        Objects.requireNonNull(categoriaOrigen, "La categoría de origen es obligatoria");
        Objects.requireNonNull(categoriaDestino, "La categoría de destino es obligatoria");
        Objects.requireNonNull(fechaHora, "La fecha y hora son obligatorias");
        Objects.requireNonNull(descripcion, "La descripción es obligatoria");

        validarPropietario(usuarioId, cuentaOrigen);
        validarPropietario(usuarioId, cuentaDestino);
        validarPropietario(usuarioId, categoriaOrigen);
        validarPropietario(usuarioId, categoriaDestino);

        if (cuentaOrigen.equals(cuentaDestino)) {
            throw new IllegalArgumentException(
                    "La cuenta de origen y destino deben ser diferentes"
            );
        }

        if (!cuentaOrigen.isActiva()) {
            throw new IllegalArgumentException("No se puede realizar una transferencia desde una cuenta desactivada");
        }
        if (!cuentaDestino.isActiva()) {
            throw new IllegalArgumentException("No se puede realizar una transferencia hacia una cuenta desactivada");
        }

        validarMismoPerfil(cuentaOrigen, categoriaOrigen);
        validarMismoPerfil(cuentaDestino, categoriaDestino);
        validarMismaMoneda(cuentaOrigen, cuentaDestino);
        validarImporte(importe);

        OperacionFinanciera operacion = new OperacionFinanciera(cuentaOrigen, cuentaDestino, importe);

        Movimiento egreso = new Movimiento(cuentaOrigen, categoriaOrigen, TipoMovimiento.EGRESO, importe, fechaHora, descripcion);
        Movimiento ingreso = new Movimiento(cuentaDestino, categoriaDestino, TipoMovimiento.INGRESO, importe, fechaHora, descripcion);

        operacion.agregarMovimiento(egreso);
        operacion.agregarMovimiento(ingreso);

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            operacionFinancieraRepository.guardar(operacion);
            movimientoRepository.guardar(egreso);
            movimientoRepository.guardar(ingreso);
            entityManager.flush();
            transaction.commit();
            return operacion;
        } catch (RuntimeException e) {
            if (transaction.isActive()) transaction.rollback();
            throw e;
        }
    }

    public OperacionFinanciera comprarActivo(
            Long usuarioId,
            Cuenta cuentaOrigen,
            Categoria categoriaOrigen,
            Activo activo,
            BigDecimal cantidad,
            BigDecimal precioUnitario,
            LocalDateTime fechaHora,
            String descripcion) {

        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");
        Objects.requireNonNull(cuentaOrigen, "La cuenta de origen es obligatoria");
        Objects.requireNonNull(categoriaOrigen, "La categoría de origen es obligatoria");
        Objects.requireNonNull(activo, "El activo es obligatorio");
        Objects.requireNonNull(fechaHora, "La fecha y hora son obligatorias");
        Objects.requireNonNull(descripcion, "La descripción es obligatoria");

        validarPropietario(usuarioId, cuentaOrigen);
        validarPropietario(usuarioId, categoriaOrigen);

        if (!cuentaOrigen.isActiva()) {
            throw new IllegalArgumentException("No se puede realizar una compra desde una cuenta desactivada");
        }

        validarMismoPerfil(cuentaOrigen, categoriaOrigen);
        Objects.requireNonNull(cantidad, "La cantidad es obligatoria");
        Objects.requireNonNull(precioUnitario, "El precio unitario es obligatorio");

        if (cantidad.signum() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva");
        }
        if (precioUnitario.signum() <= 0) {
            throw new IllegalArgumentException("El precio unitario debe ser positivo");
        }

        BigDecimal importe = cantidad.multiply(precioUnitario);

        OperacionFinanciera operacion = new OperacionFinanciera(
                cuentaOrigen, null, importe, TipoOperacionFinanciera.COMPRA);

        Movimiento egreso = new Movimiento(
                cuentaOrigen, categoriaOrigen, TipoMovimiento.EGRESO,
                importe, fechaHora, descripcion);

        MovimientoActivo movimientoActivo = new MovimientoActivo(
                activo, TipoMovimientoActivo.COMPRA, cantidad, precioUnitario);

        operacion.agregarMovimiento(egreso);
        operacion.agregarMovimientoActivo(movimientoActivo);

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            operacionFinancieraRepository.guardar(operacion);
            movimientoRepository.guardar(egreso);
            entityManager.persist(movimientoActivo);
            entityManager.flush();
            transaction.commit();
            return operacion;
        } catch (RuntimeException e) {
            if (transaction.isActive()) transaction.rollback();
            throw e;
        }
    }

    public OperacionFinanciera venderActivo(
            Long usuarioId,
            Cuenta cuentaDestino,
            Categoria categoriaDestino,
            Activo activo,
            BigDecimal cantidad,
            BigDecimal precioUnitario,
            LocalDateTime fechaHora,
            String descripcion) {

        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");
        Objects.requireNonNull(cuentaDestino, "La cuenta de destino es obligatoria");
        Objects.requireNonNull(categoriaDestino, "La categoría de destino es obligatoria");
        Objects.requireNonNull(activo, "El activo es obligatorio");
        Objects.requireNonNull(cantidad, "La cantidad es obligatoria");
        Objects.requireNonNull(precioUnitario, "El precio unitario es obligatorio");
        Objects.requireNonNull(fechaHora, "La fecha y hora son obligatorias");
        Objects.requireNonNull(descripcion, "La descripción es obligatoria");

        validarPropietario(usuarioId, cuentaDestino);
        validarPropietario(usuarioId, categoriaDestino);

        if (!cuentaDestino.isActiva()) {
            throw new IllegalArgumentException(
                    "No se puede realizar una venta hacia una cuenta desactivada"
            );
        }

        validarMismoPerfil(cuentaDestino, categoriaDestino);

        if (cantidad.signum() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva");
        }

        if (precioUnitario.signum() <= 0) {
            throw new IllegalArgumentException("El precio unitario debe ser positivo");
        }

        BigDecimal importe = cantidad.multiply(precioUnitario);

        OperacionFinanciera operacion = new OperacionFinanciera(
                null,
                cuentaDestino,
                importe,
                TipoOperacionFinanciera.VENTA
        );

        Movimiento ingreso = new Movimiento(
                cuentaDestino,
                categoriaDestino,
                TipoMovimiento.INGRESO,
                importe,
                fechaHora,
                descripcion
        );

        MovimientoActivo movimientoActivo = new MovimientoActivo(
                activo,
                TipoMovimientoActivo.VENTA,
                cantidad,
                precioUnitario
        );

        operacion.agregarMovimiento(ingreso);
        operacion.agregarMovimientoActivo(movimientoActivo);

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            operacionFinancieraRepository.guardar(operacion);
            movimientoRepository.guardar(ingreso);
            entityManager.persist(movimientoActivo);
            entityManager.flush();
            transaction.commit();
            return operacion;
        } catch (RuntimeException e) {
            if (transaction.isActive()) transaction.rollback();
            throw e;
        }
    }

    private void validarPropietario(Long usuarioId, Cuenta cuenta) {
        Long propietarioId = cuenta.getPerfilFinanciero()
                .getUsuario()
                .getId();

        if (!Objects.equals(propietarioId, usuarioId)) {
            throw new IllegalArgumentException(
                    "El usuario no es propietario de la cuenta"
            );
        }
    }

    private void validarPropietario(Long usuarioId, Categoria categoria) {
        Long propietarioId = categoria.getPerfilFinanciero()
                .getUsuario()
                .getId();

        if (!Objects.equals(propietarioId, usuarioId)) {
            throw new IllegalArgumentException(
                    "El usuario no es propietario de la categoría"
            );
        }
    }

    private void validarImporte(BigDecimal importe) {
        Objects.requireNonNull(importe, "El importe es obligatorio");

        if (importe.signum() <= 0) {
            throw new IllegalArgumentException("El importe debe ser positivo");
        }
    }

    private void validarMismoPerfil(Cuenta cuenta, Categoria categoria) {
        Long cuentaPerfilId = cuenta.getPerfilFinanciero().getId();
        Long categoriaPerfilId = categoria.getPerfilFinanciero().getId();

        if (!Objects.equals(cuentaPerfilId, categoriaPerfilId)) {
            throw new IllegalArgumentException(
                    "La cuenta y la categoría deben pertenecer al mismo perfil financiero"
            );
        }
    }

    private void validarMismaMoneda(Cuenta cuentaOrigen, Cuenta cuentaDestino) {
        if (!Objects.equals(
                cuentaOrigen.getMoneda().getId(),
                cuentaDestino.getMoneda().getId())) {
            throw new IllegalArgumentException(
                    "La cuenta de origen y destino deben utilizar la misma moneda"
            );
        }
    }
}
