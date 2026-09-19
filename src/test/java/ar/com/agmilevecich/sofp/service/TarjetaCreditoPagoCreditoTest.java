package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.FormaPago;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.Movimiento;
import ar.com.agmilevecich.sofp.domain.Obligacion;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.TipoCuenta;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.CuentaRepository;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import ar.com.agmilevecich.sofp.persistence.ObligacionRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TarjetaCreditoPagoCreditoTest {

    private EntityManager entityManager;
    private Cuenta cuenta;
    private Categoria categoria;
    private Moneda usd;
    private Usuario usuario;
    private GastoService gastoService;
    private ObligacionService obligacionService;
    private CuentaService cuentaService;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();

        MovimientoRepository movimientoRepository = new MovimientoRepository(entityManager);
        ObligacionRepository obligacionRepository = new ObligacionRepository(entityManager);
        MovimientoService movimientoService = new MovimientoService(
                entityManager,
                movimientoRepository,
                obligacionRepository
        );
        obligacionService = new ObligacionService(entityManager, obligacionRepository);
        gastoService = new GastoService(movimientoService, obligacionService);
        cuentaService = new CuentaService(
                new CuentaRepository(entityManager),
                movimientoRepository,
                obligacionRepository,
                entityManager
        );

        usuario = new Usuario(
                "Juan",
                "Pérez",
                "tarjeta." + System.nanoTime() + "@test.com",
                "hash"
        );
        PerfilFinanciero perfil = new PerfilFinanciero("Perfil principal", usuario);
        usuario.agregarPerfilFinanciero(perfil);

        InstitucionFinanciera institucion = new InstitucionFinanciera(
                "Banco de Prueba",
                TipoInstitucionFinanciera.BANCO
        );
        Moneda ars = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        usd = new Moneda("USD", "Dólar estadounidense", 2, TipoMoneda.FIAT);
        cuenta = new Cuenta(
                "Visa",
                perfil,
                institucion,
                ars,
                new BigDecimal("500000.00"),
                10,
                25
        );
        categoria = new Categoria("Compras", perfil);

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
    void deberiaLiberarCreditoConPagoParcial() {
        Obligacion obligacion = registrarGasto("120000.00", cuenta.getMoneda());

        assertEquals(new BigDecimal("380000.00"),
                cuentaService.calcularCreditoDisponible(cuenta.getId(), usuario.getId()));

        obligacionService.registrarPago(obligacion.getId(), new BigDecimal("50000.00"), usuario.getId());

        assertEquals(new BigDecimal("430000.00"),
                cuentaService.calcularCreditoDisponible(cuenta.getId(), usuario.getId()));
    }

    @Test
    void deberiaLiberarTodoElCreditoConPagoCompleto() {
        Obligacion obligacion = registrarGasto("120000.00", cuenta.getMoneda());

        obligacionService.registrarPago(obligacion.getId(), new BigDecimal("120000.00"), usuario.getId());

        assertEquals(new BigDecimal("500000.00"),
                cuentaService.calcularCreditoDisponible(cuenta.getId(), usuario.getId()));
    }

    @Test
    void deberiaPermitirNuevoConsumoLuegoDeLiberarCredito() {
        Obligacion primera = registrarGasto("120000.00", cuenta.getMoneda());
        obligacionService.registrarPago(primera.getId(), new BigDecimal("120000.00"), usuario.getId());

        registrarGasto("500000.00", cuenta.getMoneda());

        assertEquals(new BigDecimal("0.00"),
                cuentaService.calcularCreditoDisponible(cuenta.getId(), usuario.getId()));
    }

    @Test
    void deberiaIgnorarObligacionEnMonedaDiferenteParaElCreditoDeLaTarjeta() {
        registrarGasto("600.00", usd);

        assertEquals(new BigDecimal("500000.00"),
                cuentaService.calcularCreditoDisponible(cuenta.getId(), usuario.getId()));
    }

    @Test
    void deberiaSumarSoloLosSaldosPendientesDeLasObligaciones() {
        Obligacion primera = registrarGasto("100000.00", cuenta.getMoneda());
        registrarGasto("70000.00", cuenta.getMoneda());

        obligacionService.registrarPago(primera.getId(), new BigDecimal("40000.00"), usuario.getId());

        assertEquals(new BigDecimal("370000.00"),
                cuentaService.calcularCreditoDisponible(cuenta.getId(), usuario.getId()));
    }

    private Obligacion registrarGasto(String importe, Moneda moneda) {
        Movimiento movimiento = gastoService.registrar(
                cuenta,
                categoria,
                moneda,
                new BigDecimal(importe),
                LocalDateTime.of(2026, 9, 9, 10, 0),
                "Compra con tarjeta",
                FormaPago.TARJETA_CREDITO,
                usuario.getId()
        );

        return obligacionService.buscarPorMovimientoOrigen(movimiento.getId()).orElseThrow();
    }
}
