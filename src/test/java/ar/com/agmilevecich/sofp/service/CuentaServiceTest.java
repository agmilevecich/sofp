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

class CuentaServiceTest {

    private EntityManager entityManager;
    private MovimientoRepository movimientoRepository;
    private CuentaService cuentaService;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();
        movimientoRepository = new MovimientoRepository(entityManager);
        cuentaService = new CuentaService(movimientoRepository);
    }

    @AfterEach
    void tearDown() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }

        JpaTestManager.close();
    }

    @Test
    void deberiaDevolverCeroCuandoLaCuentaNoTieneMovimientos() {

        Long cuentaId = 999L;

        BigDecimal saldo = cuentaService.calcularSaldo(cuentaId);

        assertEquals(
                BigDecimal.ZERO,
                saldo
        );
    }

    @Test
    void deberiaSumarUnIngresoAlSaldo() {

        Usuario usuario = new Usuario(
                "Ariel",
                "Test",
                "ariel.test@example.com",
                "hash"
        );

        PerfilFinanciero perfil = new PerfilFinanciero(
                "Perfil principal",
                usuario
        );

        usuario.agregarPerfilFinanciero(perfil);

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Banco Test",
                        TipoInstitucionFinanciera.BANCO
                );

        Moneda moneda = new Moneda(
                "ARS",
                "Peso argentino",
                2,
                TipoMoneda.FIAT
        );

        Cuenta cuenta = new Cuenta(
                "Cuenta de prueba",
                TipoCuenta.CAJA_AHORRO,
                perfil,
                institucion,
                moneda
        );

        Categoria categoria = new Categoria(
                "Ingresos",
                perfil
        );

        Movimiento movimiento = new Movimiento(
                cuenta,
                categoria,
                TipoMovimiento.INGRESO,
                new BigDecimal("10000.00"),
                LocalDateTime.now(),
                "Ingreso de prueba"
        );

        entityManager.getTransaction().begin();

        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(institucion);
        entityManager.persist(moneda);
        entityManager.persist(cuenta);
        entityManager.persist(categoria);
        entityManager.persist(movimiento);

        entityManager.getTransaction().commit();

        entityManager.clear();

        BigDecimal saldo =
                cuentaService.calcularSaldo(cuenta.getId());

        assertEquals(
                new BigDecimal("10000.00"),
                saldo
        );
    }

    @Test
    void deberiaRestarUnEgresoDelSaldo() {

        Usuario usuario = new Usuario(
                "Ariel",
                "Test",
                "ariel.egreso@example.com",
                "hash"
        );

        PerfilFinanciero perfil = new PerfilFinanciero(
                "Perfil principal",
                usuario
        );

        usuario.agregarPerfilFinanciero(perfil);

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Banco Test",
                        TipoInstitucionFinanciera.BANCO
                );

        Moneda moneda = new Moneda(
                "ARS",
                "Peso argentino",
                2,
                TipoMoneda.FIAT
        );

        Cuenta cuenta = new Cuenta(
                "Cuenta de prueba",
                TipoCuenta.CAJA_AHORRO,
                perfil,
                institucion,
                moneda
        );

        Categoria categoria = new Categoria(
                "Gastos",
                perfil
        );

        Movimiento movimiento = new Movimiento(
                cuenta,
                categoria,
                TipoMovimiento.EGRESO,
                new BigDecimal("3000.00"),
                LocalDateTime.now(),
                "Egreso de prueba"
        );

        entityManager.getTransaction().begin();

        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(institucion);
        entityManager.persist(moneda);
        entityManager.persist(cuenta);
        entityManager.persist(categoria);
        entityManager.persist(movimiento);

        entityManager.getTransaction().commit();

        entityManager.clear();

        BigDecimal saldo =
                cuentaService.calcularSaldo(cuenta.getId());

        assertEquals(
                new BigDecimal("-3000.00"),
                saldo
        );
    }

    @Test
    void deberiaCalcularSaldoConMultiplesMovimientos() {

        Usuario usuario = new Usuario(
                "Ariel",
                "Test",
                "ariel.multiple@example.com",
                "hash"
        );

        PerfilFinanciero perfil = new PerfilFinanciero(
                "Perfil principal",
                usuario
        );

        usuario.agregarPerfilFinanciero(perfil);

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Banco Test",
                        TipoInstitucionFinanciera.BANCO
                );

        Moneda moneda = new Moneda(
                "ARS",
                "Peso argentino",
                2,
                TipoMoneda.FIAT
        );

        Cuenta cuenta = new Cuenta(
                "Cuenta de prueba",
                TipoCuenta.CAJA_AHORRO,
                perfil,
                institucion,
                moneda
        );

        Categoria categoriaIngresos = new Categoria(
                "Ingresos",
                perfil
        );

        Categoria categoriaGastos = new Categoria(
                "Gastos",
                perfil
        );

        Movimiento ingreso1 = new Movimiento(
                cuenta,
                categoriaIngresos,
                TipoMovimiento.INGRESO,
                new BigDecimal("10000.00"),
                LocalDateTime.now().minusDays(2),
                "Primer ingreso"
        );

        Movimiento ingreso2 = new Movimiento(
                cuenta,
                categoriaIngresos,
                TipoMovimiento.INGRESO,
                new BigDecimal("5000.00"),
                LocalDateTime.now().minusDays(1),
                "Segundo ingreso"
        );

        Movimiento egreso = new Movimiento(
                cuenta,
                categoriaGastos,
                TipoMovimiento.EGRESO,
                new BigDecimal("3000.00"),
                LocalDateTime.now(),
                "Egreso"
        );

        entityManager.getTransaction().begin();

        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(institucion);
        entityManager.persist(moneda);
        entityManager.persist(cuenta);
        entityManager.persist(categoriaIngresos);
        entityManager.persist(categoriaGastos);
        entityManager.persist(ingreso1);
        entityManager.persist(ingreso2);
        entityManager.persist(egreso);

        entityManager.getTransaction().commit();

        entityManager.clear();

        BigDecimal saldo =
                cuentaService.calcularSaldo(cuenta.getId());

        assertEquals(
                new BigDecimal("12000.00"),
                saldo
        );
    }
}