package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.domain.Cuenta;
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

        this.cuentaRepository =
                Objects.requireNonNull(
                        cuentaRepository,
                        "El CuentaRepository es obligatorio"
                );

        this.movimientoRepository =
                Objects.requireNonNull(
                        movimientoRepository,
                        "El MovimientoRepository es obligatorio"
                );

        this.entityManager =
                Objects.requireNonNull(
                        entityManager,
                        "El EntityManager es obligatorio"
                );
    }

    public Cuenta registrar(Cuenta cuenta) {

        Objects.requireNonNull(
                cuenta,
                "La cuenta es obligatoria"
        );

        return cuentaRepository.guardar(cuenta);
    }

    public Optional<Cuenta> buscarPorId(Long id) {

        Objects.requireNonNull(
                id,
                "El id de la cuenta es obligatorio"
        );

        return cuentaRepository.buscarPorId(id);
    }

    public List<Cuenta> listarTodas() {

        return cuentaRepository.listarTodas();
    }

    public List<Cuenta> listarPorPerfilFinanciero(
            Long perfilFinancieroId) {

        Objects.requireNonNull(
                perfilFinancieroId,
                "El id del perfil financiero es obligatorio"
        );

        return cuentaRepository.listarPorPerfilFinanciero(
                perfilFinancieroId
        );
    }

    public BigDecimal calcularSaldo(Long cuentaId) {

        Objects.requireNonNull(
                cuentaId,
                "El id de la cuenta es obligatorio"
        );

        List<Movimiento> movimientos =
                movimientoRepository.listarPorCuenta(cuentaId);

        BigDecimal saldo = BigDecimal.ZERO;

        for (Movimiento movimiento : movimientos) {

            if (movimiento.getTipoMovimiento()
                    == TipoMovimiento.INGRESO) {

                saldo = saldo.add(
                        movimiento.getImporte()
                );

            } else if (movimiento.getTipoMovimiento()
                    == TipoMovimiento.EGRESO) {

                saldo = saldo.subtract(
                        movimiento.getImporte()
                );
            }
        }

        return saldo;
    }

    public Cuenta modificarNombre(
            Long cuentaId,
            String nombre) {

        Objects.requireNonNull(
                cuentaId,
                "El id de la cuenta es obligatorio"
        );

        Objects.requireNonNull(
                nombre,
                "El nombre es obligatorio"
        );

        Cuenta cuenta =
                obtenerCuenta(cuentaId);

        EntityTransaction transaction =
                entityManager.getTransaction();

        try {
            transaction.begin();

            cuenta.renombrar(nombre);

            Cuenta actualizada =
                    cuentaRepository.guardar(cuenta);

            entityManager.flush();

            transaction.commit();

            return actualizada;

        } catch (RuntimeException e) {

            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        }
    }

    public Cuenta modificarIdentificadorExterno(
            Long cuentaId,
            String identificadorExterno) {

        Objects.requireNonNull(
                cuentaId,
                "El id de la cuenta es obligatorio"
        );

        Cuenta cuenta =
                obtenerCuenta(cuentaId);

        EntityTransaction transaction =
                entityManager.getTransaction();

        try {
            transaction.begin();

            cuenta.cambiarIdentificadorExterno(
                    identificadorExterno
            );

            Cuenta actualizada =
                    cuentaRepository.guardar(cuenta);

            entityManager.flush();

            transaction.commit();

            return actualizada;

        } catch (RuntimeException e) {

            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        }
    }

    public Cuenta modificarTipoCuenta(
            Long cuentaId,
            TipoCuenta tipoCuenta) {

        Objects.requireNonNull(
                cuentaId,
                "El id de la cuenta es obligatorio"
        );

        Objects.requireNonNull(
                tipoCuenta,
                "El tipo de cuenta es obligatorio"
        );

        Cuenta cuenta =
                obtenerCuenta(cuentaId);

        EntityTransaction transaction =
                entityManager.getTransaction();

        try {
            transaction.begin();

            cuenta.cambiarTipoCuenta(
                    tipoCuenta
            );

            Cuenta actualizada =
                    cuentaRepository.guardar(cuenta);

            entityManager.flush();

            transaction.commit();

            return actualizada;

        } catch (RuntimeException e) {

            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        }
    }

    public Cuenta modificarInstitucionFinanciera(
            Long cuentaId,
            InstitucionFinanciera institucionFinanciera) {

        Objects.requireNonNull(
                cuentaId,
                "El id de la cuenta es obligatorio"
        );

        Objects.requireNonNull(
                institucionFinanciera,
                "La institución financiera es obligatoria"
        );

        Cuenta cuenta =
                obtenerCuenta(cuentaId);

        EntityTransaction transaction =
                entityManager.getTransaction();

        try {
            transaction.begin();

            cuenta.cambiarInstitucionFinanciera(
                    institucionFinanciera
            );

            Cuenta actualizada =
                    cuentaRepository.guardar(cuenta);

            entityManager.flush();

            transaction.commit();

            return actualizada;

        } catch (RuntimeException e) {

            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        }
    }

    public Cuenta modificarMoneda(
            Long cuentaId,
            Moneda moneda) {

        Objects.requireNonNull(
                cuentaId,
                "El id de la cuenta es obligatorio"
        );

        Objects.requireNonNull(
                moneda,
                "La moneda es obligatoria"
        );

        Cuenta cuenta =
                obtenerCuenta(cuentaId);

        EntityTransaction transaction =
                entityManager.getTransaction();

        try {
            transaction.begin();

            cuenta.cambiarMoneda(
                    moneda
            );

            Cuenta actualizada =
                    cuentaRepository.guardar(cuenta);

            entityManager.flush();

            transaction.commit();

            return actualizada;

        } catch (RuntimeException e) {

            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        }
    }

    public Cuenta activar(Long cuentaId) {

        Objects.requireNonNull(
                cuentaId,
                "El id de la cuenta es obligatorio"
        );

        Cuenta cuenta =
                obtenerCuenta(cuentaId);

        EntityTransaction transaction =
                entityManager.getTransaction();

        try {
            transaction.begin();

            cuenta.activar();

            Cuenta actualizada =
                    cuentaRepository.guardar(cuenta);

            entityManager.flush();

            transaction.commit();

            return actualizada;

        } catch (RuntimeException e) {

            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        }
    }

    public Cuenta desactivar(Long cuentaId) {

        Objects.requireNonNull(
                cuentaId,
                "El id de la cuenta es obligatorio"
        );

        Cuenta cuenta =
                obtenerCuenta(cuentaId);

        EntityTransaction transaction =
                entityManager.getTransaction();

        try {
            transaction.begin();

            cuenta.desactivar();

            Cuenta actualizada =
                    cuentaRepository.guardar(cuenta);

            entityManager.flush();

            transaction.commit();

            return actualizada;

        } catch (RuntimeException e) {

            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        }
    }

    private Cuenta obtenerCuenta(Long cuentaId) {

        return cuentaRepository.buscarPorId(
                cuentaId
        ).orElseThrow(
                () -> new IllegalArgumentException(
                        "No existe una cuenta con id "
                                + cuentaId
                )
        );
    }
}