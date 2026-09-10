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
import ar.com.agmilevecich.sofp.domain.TipoMovimiento;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

class PagoTarjetaServiceTest {

    private EntityManager entityManager;
    private Cuenta tarjeta;
    private Cuenta cuentaPagadora;
    private Categoria categoriaCompras;
    private Categoria categoriaPago;
    private Moneda ars;
    private Usuario usuario;
    private GastoService gastoService;
    private ObligacionService obligacionService;
    private CuentaService cuentaService;
    private PagoTarjetaService pagoTarjetaService;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();

        MovimientoRepository movimientoRepository = new MovimientoRepository(entityManager);
        ObligacionRepository obligacionRepository = new ObligacionRepository(entityManager);
        MovimientoService movimientoService = new MovimientoService(
                entityManager, movimientoRepository, obligacionRepository);
        obligacionService = new ObligacionService(entityManager, obligacionRepository);
        gastoService = new GastoService(movimientoService, obligacionService);
        cuentaService = new CuentaService(
                new CuentaRepository(entityManager), movimientoRepository,
                obligacionRepository, entityManager);
        pagoTarjetaService = new PagoTarjetaService(
                entityManager, movimientoRepository, obligacionRepository);

        usuario = new Usuario(
                "Juan", "Pérez",
                "pago.tarjeta." + System.nanoTime() + "@test.com", "hash");
        PerfilFinanciero perfil = new PerfilFinanciero("Perfil principal", usuario);
        usuario.agregarPerfilFinanciero(perfil);

        InstitucionFinanciera institucion = new InstitucionFinanciera(
                "Banco de Prueba", TipoInstitucionFinanciera.BANCO);
        ars = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);

        tarjeta = new Cuenta(
                "Visa", perfil, institucion, ars,
                new BigDecimal("500000.00"), 10, 25);
        cuentaPagadora = new Cuenta(
                "Caja de ahorro", TipoCuenta.CAJA_AHORRO,
                perfil, institucion, ars);
        categoriaCompras = new Categoria("Compras", perfil);
        categoriaPago = new Categoria("Pago tarjeta", perfil);

        entityManager.getTransaction().begin();
        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(institucion);
        entityManager.persist(ars);
        entityManager.persist(tarjeta);
        entityManager.persist(cuentaPagadora);
        entityManager.persist(categoriaCompras);
        entityManager.persist(categoriaPago);
        entityManager.persist(new Movimiento(
                cuentaPagadora, categoriaPago, TipoMovimiento.INGRESO,
                new BigDecimal("200000.00"),
                LocalDateTime.of(2026, 9, 1, 9, 0), "Saldo inicial"));
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
    void deberiaRegistrarPagoParcialYDescontarloDeLaCuentaPagadora() {
        Obligacion obligacion = registrarGasto("120000.00");

        pagoTarjetaService.registrarPago(
                obligacion.getId(), cuentaPagadora, categoriaPago,
                new BigDecimal("50000.00"),
                LocalDateTime.of(2026, 9, 10, 10, 0),
                "Pago tarjeta", usuario.getId());

        assertEquals(new BigDecimal("70000.00"), obligacion.getSaldoPendiente());
        assertEquals(new BigDecimal("150000.00"), cuentaService.calcularSaldo(cuentaPagadora.getId(), usuario.getId()));
        assertEquals(new BigDecimal("430000.00"), cuentaService.calcularCreditoDisponible(tarjeta.getId(), usuario.getId()));
    }

    @Test
    void deberiaRegistrarPagoCompletoYDejarLaObligacionPagada() {
        Obligacion obligacion = registrarGasto("120000.00");

        pagoTarjetaService.registrarPago(
                obligacion.getId(), cuentaPagadora, categoriaPago,
                new BigDecimal("120000.00"),
                LocalDateTime.of(2026, 9, 10, 10, 0),
                "Pago tarjeta", usuario.getId());

        assertEquals(new BigDecimal("0.00"), obligacion.getSaldoPendiente());
        assertEquals("PAGADA", obligacion.getEstado().name());
        assertEquals(new BigDecimal("80000.00"), cuentaService.calcularSaldo(cuentaPagadora.getId(), usuario.getId()));
        assertEquals(new BigDecimal("500000.00"), cuentaService.calcularCreditoDisponible(tarjeta.getId(), usuario.getId()));
    }

    @Test
    void deberiaRechazarPagoQueSupereLosFondosDeLaCuentaPagadora() {
        Obligacion obligacion = registrarGasto("120000.00");

        assertThrows(IllegalArgumentException.class, () -> pagoTarjetaService.registrarPago(
                obligacion.getId(), cuentaPagadora, categoriaPago,
                new BigDecimal("200001.00"),
                LocalDateTime.of(2026, 9, 10, 10, 0),
                "Pago tarjeta", usuario.getId()));

        assertEquals(new BigDecimal("120000.00"), obligacion.getSaldoPendiente());
        assertEquals(new BigDecimal("200000.00"), cuentaService.calcularSaldo(cuentaPagadora.getId(), usuario.getId()));
    }

    private Obligacion registrarGasto(String importe) {
        Movimiento movimiento = gastoService.registrar(
                tarjeta, categoriaCompras, ars, new BigDecimal(importe),
                LocalDateTime.of(2026, 9, 9, 10, 0), "Compra con tarjeta",
                FormaPago.TARJETA_CREDITO, usuario.getId());
        return obligacionService.buscarPorMovimientoOrigen(movimiento.getId()).orElseThrow();
    }
}
