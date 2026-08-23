package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.Movimiento;
import ar.com.agmilevecich.sofp.domain.OperacionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMovimiento;
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
            Cuenta cuentaOrigen,
            Cuenta cuentaDestino,
            Categoria categoriaOrigen,
            Categoria categoriaDestino,
            BigDecimal importe,
            LocalDateTime fechaHora,
            String descripcion) {

        Objects.requireNonNull(
                cuentaOrigen,
                "La cuenta de origen es obligatoria"
        );

        Objects.requireNonNull(
                cuentaDestino,
                "La cuenta de destino es obligatoria"
        );

        Objects.requireNonNull(
                categoriaOrigen,
                "La categoría de origen es obligatoria"
        );

        Objects.requireNonNull(
                categoriaDestino,
                "La categoría de destino es obligatoria"
        );

        Objects.requireNonNull(
                fechaHora,
                "La fecha y hora son obligatorias"
        );

        Objects.requireNonNull(
                descripcion,
                "La descripción es obligatoria"
        );

        if (!cuentaOrigen.isActiva()) {
            throw new IllegalArgumentException(
                    "No se puede realizar una transferencia desde una cuenta desactivada"
            );
        }

        if (!cuentaDestino.isActiva()) {
            throw new IllegalArgumentException(
                    "No se puede realizar una transferencia hacia una cuenta desactivada"
            );
        }

        validarMismoPerfil(
                cuentaOrigen,
                categoriaOrigen
        );

        validarMismoPerfil(
                cuentaDestino,
                categoriaDestino
        );

        validarMismaMoneda(
                cuentaOrigen,
                cuentaDestino
        );

        OperacionFinanciera operacion =
                new OperacionFinanciera(
                        cuentaOrigen,
                        cuentaDestino,
                        importe
                );

        Movimiento egreso =
                new Movimiento(
                        cuentaOrigen,
                        categoriaOrigen,
                        TipoMovimiento.EGRESO,
                        importe,
                        fechaHora,
                        descripcion
                );

        Movimiento ingreso =
                new Movimiento(
                        cuentaDestino,
                        categoriaDestino,
                        TipoMovimiento.INGRESO,
                        importe,
                        fechaHora,
                        descripcion
                );

        operacion.agregarMovimiento(
                egreso
        );

        operacion.agregarMovimiento(
                ingreso
        );

        EntityTransaction transaction =
                entityManager.getTransaction();

        try {
            transaction.begin();

            operacionFinancieraRepository.guardar(
                    operacion
            );

            movimientoRepository.guardar(
                    egreso
            );

            movimientoRepository.guardar(
                    ingreso
            );

            entityManager.flush();

            transaction.commit();

            return operacion;

        } catch (RuntimeException e) {

            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        }
    }

    private void validarMismoPerfil(
            Cuenta cuenta,
            Categoria categoria) {

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

    private void validarMismaMoneda(
            Cuenta cuentaOrigen,
            Cuenta cuentaDestino) {

        if (!Objects.equals(
                cuentaOrigen.getMoneda().getId(),
                cuentaDestino.getMoneda().getId()
        )) {
            throw new IllegalArgumentException(
                    "La cuenta de origen y destino deben utilizar la misma moneda"
            );
        }
    }
}
