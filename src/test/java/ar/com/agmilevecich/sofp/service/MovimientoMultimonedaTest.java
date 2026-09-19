package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.FormaPago;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.Movimiento;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.TipoCuenta;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.domain.TipoMovimiento;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.CuentaRepository;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MovimientoMultimonedaTest {

    private EntityManager entityManager;
    private CuentaService cuentaService;
    private MovimientoService movimientoService;
    private Usuario usuario;
    private Cuenta cuenta;
    private Categoria categoria;
    private Moneda ars;
    private Moneda usd;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();
        MovimientoRepository movimientoRepository = new MovimientoRepository(entityManager);
        CuentaRepository cuentaRepository = new CuentaRepository(entityManager);
        cuentaService = new CuentaService(cuentaRepository, movimientoRepository, entityManager);
        movimientoService = new MovimientoService(entityManager, movimientoRepository);

        usuario = new Usuario(
                "Juan",
                "Pérez",
                "juan.multimoneda." + System.nanoTime() + "@test.com",
                "hash"
        );
        PerfilFinanciero perfil = new PerfilFinanciero("Perfil principal", usuario);
        usuario.agregarPerfilFinanciero(perfil);

        InstitucionFinanciera institucion = new InstitucionFinanciera(
                "Banco de Prueba", TipoInstitucionFinanciera.BANCO);
        ars = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        usd = new Moneda("USD", "Dólar estadounidense", 2, TipoMoneda.FIAT);
        cuenta = new Cuenta("Cuenta principal", TipoCuenta.CAJA_AHORRO, perfil, institucion, ars);
        categoria = new Categoria("Movimientos", perfil);

        entityManager.getTransaction().begin();
        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(institucion);
        entityManager.persist(ars);
        entityManager.persist(usd);
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
    void deberiaCalcularElSaldoDeLaCuentaSoloConLaMonedaDeLaCuenta() {
        persistirMovimiento(ars, TipoMovimiento.INGRESO, "10000.00");
        persistirMovimiento(usd, TipoMovimiento.INGRESO, "5000.00");

        assertEquals(
                new BigDecimal("10000.00"),
                cuentaService.calcularSaldo(cuenta.getId())
        );
    }

    @Test
    void deberiaIgnorarElSaldoUsdAlValidarUnEgresoArs() {
        persistirMovimiento(usd, TipoMovimiento.INGRESO, "100000.00");

        assertThrows(
                IllegalArgumentException.class,
                () -> movimientoService.registrar(
                        cuenta,
                        categoria,
                        ars,
                        TipoMovimiento.EGRESO,
                        new BigDecimal("1.00"),
                        LocalDateTime.now(),
                        "Egreso ARS",
                        FormaPago.EFECTIVO,
                        usuario.getId()
                )
        );
    }

    @Test
    void deberiaValidarFondosEnLaMonedaExplicitaDelMovimiento() {
        persistirMovimiento(usd, TipoMovimiento.INGRESO, "10000.00");

        Movimiento egreso = movimientoService.registrar(
                cuenta,
                categoria,
                usd,
                TipoMovimiento.EGRESO,
                new BigDecimal("4000.00"),
                LocalDateTime.now(),
                "Egreso USD",
                FormaPago.EFECTIVO,
                usuario.getId()
        );

        assertEquals(usd.getId(), egreso.getMoneda().getId());
        assertEquals(new BigDecimal("4000.00"), egreso.getImporte());
    }

    @Test
    void deberiaMantenerSeparadosLosSaldosDeArsYUsd() {
        persistirMovimiento(ars, TipoMovimiento.INGRESO, "12000.00");
        persistirMovimiento(usd, TipoMovimiento.INGRESO, "7000.00");
        persistirMovimiento(usd, TipoMovimiento.EGRESO, "1500.00");

        assertEquals(new BigDecimal("12000.00"), cuentaService.calcularSaldo(cuenta.getId()));

        BigDecimal saldoUsd = movimientoServiceSaldoPorMoneda(usd);
        assertEquals(new BigDecimal("5500.00"), saldoUsd);
    }

    private void persistirMovimiento(Moneda moneda, TipoMovimiento tipoMovimiento, String importe) {
        Movimiento movimiento = new Movimiento(
                cuenta,
                categoria,
                moneda,
                tipoMovimiento,
                new BigDecimal(importe),
                LocalDateTime.now(),
                "Movimiento de prueba",
                FormaPago.EFECTIVO
        );
        entityManager.getTransaction().begin();
        entityManager.persist(movimiento);
        entityManager.getTransaction().commit();
    }

    private BigDecimal movimientoServiceSaldoPorMoneda(Moneda moneda) {
        BigDecimal saldo = BigDecimal.ZERO;
        for (Movimiento movimiento : new MovimientoRepository(entityManager).listarPorCuenta(cuenta.getId())) {
            if (!moneda.equals(movimiento.getMoneda())) {
                continue;
            }
            if (movimiento.getTipoMovimiento() == TipoMovimiento.INGRESO) {
                saldo = saldo.add(movimiento.getImporte());
            } else if (movimiento.getTipoMovimiento() == TipoMovimiento.EGRESO
                    && movimiento.getFormaPago() != FormaPago.TARJETA_CREDITO) {
                saldo = saldo.subtract(movimiento.getImporte());
            }
        }
        return saldo;
    }
}
