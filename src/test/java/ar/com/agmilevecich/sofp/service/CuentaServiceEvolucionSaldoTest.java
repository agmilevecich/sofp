package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.EvolucionSaldoCuenta;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CuentaServiceEvolucionSaldoTest {

    private EntityManager entityManager;
    private CuentaService cuentaService;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();
        cuentaService = new CuentaService(
                new CuentaRepository(entityManager),
                new MovimientoRepository(entityManager),
                entityManager
        );
    }

    @AfterEach
    void tearDown() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
        JpaTestManager.close();
    }

    @Test
    void deberiaDevolverListaVaciaCuandoLaCuentaNoTieneMovimientos() {
        List<EvolucionSaldoCuenta> evolucion =
                cuentaService.obtenerEvolucionSaldo(999L);

        assertTrue(evolucion.isEmpty());
    }

    @Test
    void deberiaRegistrarElSaldoPosteriorAUnIngreso() {
        DatosCuenta datos = crearDatosCuenta("evolucion.ingreso");
        LocalDateTime fechaHora = LocalDateTime.of(2026, 8, 20, 10, 0);

        Movimiento movimiento = new Movimiento(
                datos.cuenta(),
                datos.categoriaIngresos(),
                TipoMovimiento.INGRESO,
                new BigDecimal("10000.00"),
                fechaHora,
                "Ingreso"
        );

        persistir(datos, movimiento);

        List<EvolucionSaldoCuenta> evolucion =
                cuentaService.obtenerEvolucionSaldo(datos.cuenta().getId());

        assertEquals(1, evolucion.size());
        assertEquals(fechaHora, evolucion.get(0).getFechaHora());
        assertEquals(new BigDecimal("10000.00"), evolucion.get(0).getSaldo());
    }

    @Test
    void deberiaRegistrarElSaldoPosteriorAUnEgreso() {
        DatosCuenta datos = crearDatosCuenta("evolucion.egreso");
        LocalDateTime fechaHora = LocalDateTime.of(2026, 8, 20, 11, 0);

        Movimiento movimiento = new Movimiento(
                datos.cuenta(),
                datos.categoriaGastos(),
                TipoMovimiento.EGRESO,
                new BigDecimal("3000.00"),
                fechaHora,
                "Egreso"
        );

        persistir(datos, movimiento);

        List<EvolucionSaldoCuenta> evolucion =
                cuentaService.obtenerEvolucionSaldo(datos.cuenta().getId());

        assertEquals(1, evolucion.size());
        assertEquals(fechaHora, evolucion.get(0).getFechaHora());
        assertEquals(new BigDecimal("-3000.00"), evolucion.get(0).getSaldo());
    }

    @Test
    void deberiaCalcularLaEvolucionAcumuladaEnOrdenCronologico() {
        DatosCuenta datos = crearDatosCuenta("evolucion.multiple");
        LocalDateTime fecha1 = LocalDateTime.of(2026, 8, 20, 9, 0);
        LocalDateTime fecha2 = LocalDateTime.of(2026, 8, 20, 10, 0);
        LocalDateTime fecha3 = LocalDateTime.of(2026, 8, 20, 11, 0);

        Movimiento ingreso = new Movimiento(
                datos.cuenta(), datos.categoriaIngresos(), TipoMovimiento.INGRESO,
                new BigDecimal("10000.00"), fecha1, "Primer ingreso");

        Movimiento segundoIngreso = new Movimiento(
                datos.cuenta(), datos.categoriaIngresos(), TipoMovimiento.INGRESO,
                new BigDecimal("5000.00"), fecha2, "Segundo ingreso");

        Movimiento egreso = new Movimiento(
                datos.cuenta(), datos.categoriaGastos(), TipoMovimiento.EGRESO,
                new BigDecimal("3000.00"), fecha3, "Egreso");

        persistir(datos, ingreso, segundoIngreso, egreso);

        List<EvolucionSaldoCuenta> evolucion =
                cuentaService.obtenerEvolucionSaldo(datos.cuenta().getId());

        assertEquals(3, evolucion.size());
        assertEquals(fecha1, evolucion.get(0).getFechaHora());
        assertEquals(new BigDecimal("10000.00"), evolucion.get(0).getSaldo());
        assertEquals(fecha2, evolucion.get(1).getFechaHora());
        assertEquals(new BigDecimal("15000.00"), evolucion.get(1).getSaldo());
        assertEquals(fecha3, evolucion.get(2).getFechaHora());
        assertEquals(new BigDecimal("12000.00"), evolucion.get(2).getSaldo());
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeObtieneEvolucionConIdNulo() {
        assertThrows(
                NullPointerException.class,
                () -> cuentaService.obtenerEvolucionSaldo(null)
        );
    }

    private DatosCuenta crearDatosCuenta(String sufijo) {
        Usuario usuario = new Usuario(
                "Ariel", "Test",
                "ariel." + sufijo + "." + System.nanoTime() + "@test.com",
                "hash");

        PerfilFinanciero perfil = new PerfilFinanciero(
                "Perfil principal", usuario);
        usuario.agregarPerfilFinanciero(perfil);

        InstitucionFinanciera institucion = new InstitucionFinanciera(
                "Banco Test", TipoInstitucionFinanciera.BANCO);

        Moneda moneda = new Moneda(
                "ARS", "Peso argentino", 2, TipoMoneda.FIAT);

        Cuenta cuenta = new Cuenta(
                "Cuenta de prueba", TipoCuenta.CAJA_AHORRO,
                perfil, institucion, moneda);

        Categoria categoriaIngresos = new Categoria("Ingresos", perfil);
        Categoria categoriaGastos = new Categoria("Gastos", perfil);

        return new DatosCuenta(
                usuario, perfil, institucion, moneda, cuenta,
                categoriaIngresos, categoriaGastos);
    }

    private void persistir(DatosCuenta datos, Movimiento... movimientos) {
        entityManager.getTransaction().begin();
        entityManager.persist(datos.usuario());
        entityManager.persist(datos.perfil());
        entityManager.persist(datos.institucion());
        entityManager.persist(datos.moneda());
        entityManager.persist(datos.cuenta());
        entityManager.persist(datos.categoriaIngresos());
        entityManager.persist(datos.categoriaGastos());

        for (Movimiento movimiento : movimientos) {
            entityManager.persist(movimiento);
        }

        entityManager.getTransaction().commit();
        entityManager.clear();
    }

    private record DatosCuenta(
            Usuario usuario,
            PerfilFinanciero perfil,
            InstitucionFinanciera institucion,
            Moneda moneda,
            Cuenta cuenta,
            Categoria categoriaIngresos,
            Categoria categoriaGastos) {
    }
}
