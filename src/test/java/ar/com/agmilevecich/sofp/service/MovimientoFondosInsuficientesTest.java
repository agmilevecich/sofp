package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.Movimiento;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.TipoCuenta;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.domain.TipoMovimiento;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MovimientoFondosInsuficientesTest {

    private EntityManager entityManager;
    private MovimientoService movimientoService;
    private Usuario usuario;
    private Cuenta cuenta;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();
        movimientoService = new MovimientoService(
                entityManager,
                new MovimientoRepository(entityManager)
        );

        usuario = new Usuario(
                "Juan",
                "Pérez",
                "juan.fondos." + System.nanoTime() + "@test.com",
                "hash"
        );
        PerfilFinanciero perfil = new PerfilFinanciero("Perfil principal", usuario);
        usuario.agregarPerfilFinanciero(perfil);

        InstitucionFinanciera institucion = new InstitucionFinanciera(
                "Banco de Prueba",
                TipoInstitucionFinanciera.BANCO
        );
        Moneda moneda = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        cuenta = new Cuenta(
                "Cuenta principal",
                TipoCuenta.CAJA_AHORRO,
                perfil,
                institucion,
                moneda
        );
        categoria = new Categoria("Alimentación", perfil);

        entityManager.getTransaction().begin();
        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(institucion);
        entityManager.persist(moneda);
        entityManager.persist(cuenta);
        entityManager.persist(categoria);
        entityManager.getTransaction().commit();
    }

    @AfterEach
    void tearDown() {
        if (entityManager != null && entityManager.isOpen()) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            entityManager.close();
        }
        JpaTestManager.close();
    }

    @Test
    void deberiaPermitirEgresoMenorAlSaldoDisponible() {
        registrarIngreso("100000.00");

        Movimiento egreso = registrarEgreso("40000.00");

        assertEquals(new BigDecimal("40000.00"), egreso.getImporte());
    }

    @Test
    void deberiaPermitirEgresoIgualAlSaldoDisponible() {
        registrarIngreso("100000.00");

        registrarEgreso("100000.00");

        assertEquals(
                BigDecimal.ZERO,
                new CuentaService(
                        new ar.com.agmilevecich.sofp.persistence.CuentaRepository(entityManager),
                        new MovimientoRepository(entityManager),
                        entityManager
                ).calcularSaldo(cuenta.getId(), usuario.getId())
        );
    }

    @Test
    void deberiaRechazarEgresoMayorAlSaldoDisponible() {
        registrarIngreso("100000.00");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> registrarEgreso("100000.01")
        );

        assertEquals(
                "No hay fondos suficientes en la cuenta para registrar el egreso",
                exception.getMessage()
        );
    }

    @Test
    void deberiaPermitirModificarEgresoSinSuperarElSaldoDisponible() {
        registrarIngreso("100000.00");
        Movimiento egreso = registrarEgreso("40000.00");

        Movimiento actualizado = movimientoService.modificarImporte(
                egreso.getId(),
                usuario.getId(),
                new BigDecimal("100000.00")
        );

        assertEquals(new BigDecimal("100000.00"), actualizado.getImporte());
    }

    @Test
    void deberiaRechazarModificarEgresoCuandoGeneraSaldoNegativo() {
        registrarIngreso("100000.00");
        Movimiento egreso = registrarEgreso("40000.00");

        assertThrows(
                IllegalArgumentException.class,
                () -> movimientoService.modificarImporte(
                        egreso.getId(),
                        usuario.getId(),
                        new BigDecimal("100000.01")
                )
        );
    }

    @Test
    void deberiaRechazarCambiarIngresoAEgresoSinFondosSuficientes() {
        Movimiento ingreso = registrarIngreso("100000.00");

        assertThrows(
                IllegalArgumentException.class,
                () -> movimientoService.modificarTipoMovimiento(
                        ingreso.getId(),
                        usuario.getId(),
                        TipoMovimiento.EGRESO
                )
        );
    }

    private Movimiento registrarIngreso(String importe) {
        return movimientoService.registrar(
                cuenta,
                categoria,
                TipoMovimiento.INGRESO,
                new BigDecimal(importe),
                LocalDateTime.of(2026, 9, 4, 10, 0),
                "Ingreso de prueba",
                usuario.getId()
        );
    }

    private Movimiento registrarEgreso(String importe) {
        return movimientoService.registrar(
                cuenta,
                categoria,
                TipoMovimiento.EGRESO,
                new BigDecimal(importe),
                LocalDateTime.of(2026, 9, 4, 11, 0),
                "Egreso de prueba",
                usuario.getId()
        );
    }
}
