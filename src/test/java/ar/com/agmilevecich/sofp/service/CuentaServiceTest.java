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
import ar.com.agmilevecich.sofp.persistence.CuentaRepository;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CuentaServiceTest {

    private EntityManager entityManager;
    private CuentaRepository cuentaRepository;
    private MovimientoRepository movimientoRepository;
    private CuentaService cuentaService;

    @BeforeEach
    void setUp() {

        entityManager =
                JpaTestManager.createEntityManager();

        cuentaRepository =
                new CuentaRepository(
                        entityManager
                );

        movimientoRepository =
                new MovimientoRepository(
                        entityManager
                );

        cuentaService =
                new CuentaService(
                        cuentaRepository,
                        movimientoRepository,
                        entityManager
                );
    }

    @AfterEach
    void tearDown() {

        if (entityManager != null
                && entityManager.isOpen()) {

            entityManager.close();
        }

        JpaTestManager.close();
    }

    @Test
    void deberiaDevolverCeroCuandoLaCuentaNoExisteEnLaAPIInterna() {

        assertEquals(
                BigDecimal.ZERO,
                cuentaService.calcularSaldo(999L)
        );
    }

    @Test
    void deberiaSumarUnIngresoAlSaldo() {

        DatosCuenta datos =
                persistirCuenta("ariel.saldo.ingreso." + System.nanoTime());

        Categoria categoria =
                persistirCategoria(datos.perfil());

        Movimiento movimiento =
                new Movimiento(
                        datos.cuenta(),
                        categoria,
                        TipoMovimiento.INGRESO,
                        new BigDecimal("1500.00"),
                        LocalDateTime.now(),
                        "Ingreso"
                );

        entityManager.getTransaction().begin();
        entityManager.persist(movimiento);
        entityManager.getTransaction().commit();

        assertEquals(
                new BigDecimal("1500.00"),
                cuentaService.calcularSaldo(datos.cuenta().getId())
        );
    }

    @Test
    void deberiaRestarUnEgresoAlSaldo() {

        DatosCuenta datos =
                persistirCuenta("ariel.saldo.egreso." + System.nanoTime());

        Categoria categoria =
                persistirCategoria(datos.perfil());

        Movimiento ingreso =
                new Movimiento(
                        datos.cuenta(),
                        categoria,
                        TipoMovimiento.INGRESO,
                        new BigDecimal("2000.00"),
                        LocalDateTime.now(),
                        "Ingreso"
                );

        Movimiento egreso =
                new Movimiento(
                        datos.cuenta(),
                        categoria,
                        TipoMovimiento.EGRESO,
                        new BigDecimal("500.00"),
                        LocalDateTime.now(),
                        "Egreso"
                );

        entityManager.getTransaction().begin();
        entityManager.persist(ingreso);
        entityManager.persist(egreso);
        entityManager.getTransaction().commit();

        assertEquals(
                new BigDecimal("1500.00"),
                cuentaService.calcularSaldo(datos.cuenta().getId())
        );
    }

    @Test
    void deberiaIgnorarUnEgresoConTarjetaDeCreditoAlCalcularSaldo() {

        DatosCuenta datos =
                persistirCuenta("ariel.saldo.tarjeta." + System.nanoTime());

        Categoria categoria =
                persistirCategoria(datos.perfil());

        Movimiento ingreso =
                new Movimiento(
                        datos.cuenta(),
                        categoria,
                        TipoMovimiento.INGRESO,
                        new BigDecimal("10000.00"),
                        LocalDateTime.now(),
                        "Ingreso"
                );

        Movimiento egreso =
                new Movimiento(
                        datos.cuenta(),
                        categoria,
                        TipoMovimiento.EGRESO,
                        new BigDecimal("3000.00"),
                        LocalDateTime.now(),
                        "Consumo tarjeta",
                        ar.com.agmilevecich.sofp.domain.FormaPago.TARJETA_CREDITO
                );

        entityManager.getTransaction().begin();
        entityManager.persist(ingreso);
        entityManager.persist(egreso);
        entityManager.getTransaction().commit();

        assertEquals(
                new BigDecimal("10000.00"),
                cuentaService.calcularSaldo(datos.cuenta().getId())
        );
    }

    @Test
    void deberiaListarLasCuentasDelPerfilFinanciero() {

        DatosCuenta datos =
                persistirCuenta("ariel.cuentas.lista." + System.nanoTime());

        List<Cuenta> cuentas =
                cuentaService.listarPorPerfilFinanciero(
                        datos.perfil().getId()
                );

        assertEquals(1, cuentas.size());
        assertEquals(datos.cuenta().getId(), cuentas.get(0).getId());
    }

    @Test
    void deberiaBuscarCuentaPorId() {

        DatosCuenta datos =
                persistirCuenta("ariel.cuenta.buscar." + System.nanoTime());

        Optional<Cuenta> resultado =
                cuentaService.buscarPorId(
                        datos.cuenta().getId()
                );

        assertTrue(resultado.isPresent());
        assertEquals(datos.cuenta().getId(), resultado.get().getId());
    }

    @Test
    void deberiaRegistrarUnaCuenta() {

        DatosCuenta datos =
                persistirDatosBase("ariel.cuenta.registrar." + System.nanoTime());

        Cuenta cuenta =
                new Cuenta(
                        "Cuenta nueva",
                        TipoCuenta.CAJA_AHORRO,
                        datos.perfil(),
                        datos.institucion(),
                        datos.moneda()
                );

        Cuenta guardada =
                cuentaService.registrar(
                        cuenta,
                        datos.usuario().getId()
                );

        assertNotNull(guardada.getId());
    }

    @Test
    void deberiaListarTodasLasCuentas() {

        DatosCuenta datos =
                persistirCuenta("ariel.cuentas.todas." + System.nanoTime());

        List<Cuenta> cuentas =
                cuentaService.listarTodas();

        assertFalse(cuentas.isEmpty());
        assertTrue(
                cuentas.stream()
                        .anyMatch(c -> c.getId().equals(datos.cuenta().getId()))
        );
    }

    @Test
    void deberiaObtenerEvolucionDeSaldo() {

        DatosCuenta datos =
                persistirCuenta("ariel.evolucion." + System.nanoTime());

        Categoria categoria =
                persistirCategoria(datos.perfil());

        Movimiento ingreso =
                new Movimiento(
                        datos.cuenta(),
                        categoria,
                        TipoMovimiento.INGRESO,
                        new BigDecimal("1000.00"),
                        LocalDateTime.now().minusMinutes(2),
                        "Ingreso"
                );

        Movimiento egreso =
                new Movimiento(
                        datos.cuenta(),
                        categoria,
                        TipoMovimiento.EGRESO,
                        new BigDecimal("250.00"),
                        LocalDateTime.now(),
                        "Egreso"
                );

        entityManager.getTransaction().begin();
        entityManager.persist(ingreso);
        entityManager.persist(egreso);
        entityManager.getTransaction().commit();

        List<?> evolucion =
                cuentaService.obtenerEvolucionSaldo(
                        datos.cuenta().getId()
                );

        assertEquals(2, evolucion.size());
    }

    @Test
    void deberiaModificarNombre() {

        DatosCuenta datos =
                persistirCuenta("ariel.cuenta.nombre." + System.nanoTime());

        cuentaService.modificarNombre(
                datos.cuenta().getId(),
                datos.usuario().getId(),
                "Nuevo nombre"
        );

        Cuenta cuenta =
                cuentaRepository.buscarPorId(datos.cuenta().getId()).orElseThrow();

        assertEquals("Nuevo nombre", cuenta.getNombre());
    }

    private DatosCuenta persistirCuenta(String identificador) {

        DatosCuenta datos =
                persistirDatosBase(identificador);

        Cuenta cuenta =
                new Cuenta(
                        "Cuenta test",
                        TipoCuenta.CAJA_AHORRO,
                        datos.perfil(),
                        datos.institucion(),
                        datos.moneda()
                );

        entityManager.getTransaction().begin();
        entityManager.persist(cuenta);
        entityManager.getTransaction().commit();

        return new DatosCuenta(
                datos.usuario(),
                datos.perfil(),
                datos.institucion(),
                datos.moneda(),
                cuenta
        );
    }

    private Categoria persistirCategoria(PerfilFinanciero perfil) {

        Categoria categoria =
                new Categoria(
                        "Categoría test",
                        perfil
                );

        entityManager.getTransaction().begin();
        entityManager.persist(categoria);
        entityManager.getTransaction().commit();

        return categoria;
    }

    private DatosCuenta persistirDatosBase(String identificador) {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Test",
                        identificador + "@test.com",
                        "hash"
                );

        PerfilFinanciero perfil =
                new PerfilFinanciero(
                        "Perfil test",
                        usuario
                );

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Banco Test",
                        TipoInstitucionFinanciera.BANCO
                );

        Moneda moneda =
                new Moneda(
                        "ARS",
                        "Peso argentino",
                        2,
                        TipoMoneda.FIAT
                );

        entityManager.getTransaction().begin();
        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(institucion);
        entityManager.persist(moneda);
        entityManager.getTransaction().commit();

        return new DatosCuenta(
                usuario,
                perfil,
                institucion,
                moneda,
                null
        );
    }

    private record DatosCuenta(
            Usuario usuario,
            PerfilFinanciero perfil,
            InstitucionFinanciera institucion,
            Moneda moneda,
            Cuenta cuenta
    ) {
    }
}
