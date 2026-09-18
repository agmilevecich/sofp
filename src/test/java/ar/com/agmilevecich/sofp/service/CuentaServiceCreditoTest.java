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
import ar.com.agmilevecich.sofp.domain.TipoCambio;
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

class CuentaServiceCreditoTest {

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
    void deberiaCalcularCreditoDisponibleUsandoValorizacionMultidivisa() {
        Datos datos = persistirDatos();

        assertEquals(
                0,
                new BigDecimal("350000.00").compareTo(
                        cuentaService.calcularCreditoDisponible(
                                datos.cuenta().getId(),
                                datos.usuario().getId()
                        )
                )
        );
    }

    @Test
    void deberiaCalcularCreditoUtilizadoSumandoLaValorizacionDeCadaCuotaMultidivisa() {
        Datos datos = persistirDatos();
        Obligacion obligacion = datos.obligacion();

        entityManager.getTransaction().begin();

        obligacion.generarCuotas(3);

        TipoCambio cambioSeptiembre = new TipoCambio(
                datos.usd(),
                datos.ars(),
                new BigDecimal("1500.00"),
                LocalDateTime.of(2026, 9, 15, 23, 59),
                "TEST"
        );
        TipoCambio cambioOctubre = new TipoCambio(
                datos.usd(),
                datos.ars(),
                new BigDecimal("1600.00"),
                LocalDateTime.of(2026, 10, 15, 23, 59),
                "TEST"
        );
        TipoCambio cambioNoviembre = new TipoCambio(
                datos.usd(),
                datos.ars(),
                new BigDecimal("1700.00"),
                LocalDateTime.of(2026, 11, 15, 23, 59),
                "TEST"
        );

        entityManager.persist(cambioSeptiembre);
        entityManager.persist(cambioOctubre);
        entityManager.persist(cambioNoviembre);

        obligacion.getCuotas().get(0).valorarCierre(cambioSeptiembre);
        obligacion.getCuotas().get(1).valorarCierre(cambioOctubre);
        obligacion.getCuotas().get(2).valorarCierre(cambioNoviembre);

        assertEquals(new BigDecimal("33.33"), obligacion.getCuotas().get(0).getImporteOriginal());
        assertEquals(new BigDecimal("33.33"), obligacion.getCuotas().get(1).getImporteOriginal());
        assertEquals(new BigDecimal("33.34"), obligacion.getCuotas().get(2).getImporteOriginal());

        assertEquals(new BigDecimal("49995.00"), obligacion.getCuotas().get(0).getImporteValorizacionCierre());
        assertEquals(new BigDecimal("53328.00"), obligacion.getCuotas().get(1).getImporteValorizacionCierre());
        assertEquals(new BigDecimal("56678.00"), obligacion.getCuotas().get(2).getImporteValorizacionCierre());

        entityManager.flush();
        entityManager.getTransaction().commit();

        assertEquals(
                0,
                new BigDecimal("339999.00").compareTo(
                        cuentaService.calcularCreditoDisponible(
                                datos.cuenta().getId(),
                                datos.usuario().getId()
                        )
                )
        );
    }

    @Test
    void deberiaReducirCreditoUtilizadoMultidivisaAlPagarParcialmente() {
        Datos datos = persistirDatos();
        Obligacion obligacion = datos.obligacion();

        entityManager.getTransaction().begin();
        obligacion.registrarPago(new BigDecimal("40.00"));
        entityManager.flush();
        entityManager.getTransaction().commit();

        assertEquals(
                0,
                new BigDecimal("410000.00").compareTo(
                        cuentaService.calcularCreditoDisponible(
                                datos.cuenta().getId(),
                                datos.usuario().getId()
                        )
                )
        );
    }

    @Test
    void deberiaLiberarCreditoLuegoDePagarLaLiquidacionMultidivisa() {
        Datos datos = persistirDatos();
        Obligacion obligacion = datos.obligacion();
        TipoCambio tipoCambioLiquidacion = new TipoCambio(
                datos.usd(),
                datos.ars(),
                new BigDecimal("1600.00"),
                LocalDateTime.of(2026, 9, 16, 12, 0),
                "TEST LIQUIDACION"
        );

        entityManager.getTransaction().begin();
        entityManager.persist(tipoCambioLiquidacion);
        obligacion.registrarPago(new BigDecimal("40.00"));
        obligacion.liquidar(tipoCambioLiquidacion);
        obligacion.registrarPagoLiquidacion(new BigDecimal("96000.00"));
        entityManager.flush();
        entityManager.getTransaction().commit();

        assertEquals(
                0,
                new BigDecimal("500000.00").compareTo(
                        cuentaService.calcularCreditoDisponible(
                                datos.cuenta().getId(),
                                datos.usuario().getId()
                        )
                )
        );
    }

    private Datos persistirDatos() {
        Usuario usuario = new Usuario(
                "Ariel",
                "Milevecich",
                "cuenta.service.credito." + System.nanoTime() + "@test.com",
                "hash"
        );
        PerfilFinanciero perfil = new PerfilFinanciero("Personal", usuario);
        InstitucionFinanciera banco = new InstitucionFinanciera(
                "Banco Santander", TipoInstitucionFinanciera.BANCO
        );
        Moneda ars = new Moneda("ARS", "Peso Argentino", 2, TipoMoneda.FIAT);
        Moneda usd = new Moneda("USD", "Dólar Estadounidense", 2, TipoMoneda.FIAT);
        Cuenta cuenta = new Cuenta(
                "Tarjeta principal",
                perfil,
                banco,
                ars,
                new BigDecimal("500000.00"),
                15,
                10
        );
        Categoria categoria = new Categoria("Compra", perfil);
        Movimiento movimientoUsd = new Movimiento(
                cuenta,
                categoria,
                usd,
                TipoMovimiento.EGRESO,
                new BigDecimal("100.00"),
                LocalDateTime.of(2026, 9, 10, 10, 0),
                "Consumo USD",
                FormaPago.TARJETA_CREDITO
        );
        Obligacion obligacion = new Obligacion(movimientoUsd);
        TipoCambio tipoCambio = new TipoCambio(
                usd,
                ars,
                new BigDecimal("1500.00"),
                LocalDateTime.of(2026, 9, 15, 23, 59),
                "TEST"
        );
        obligacion.valorarCierre(tipoCambio);

        entityManager.getTransaction().begin();
        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(banco);
        entityManager.persist(ars);
        entityManager.persist(usd);
        entityManager.persist(cuenta);
        entityManager.persist(categoria);
        entityManager.persist(tipoCambio);
        entityManager.persist(movimientoUsd);
        entityManager.persist(obligacion);
        entityManager.getTransaction().commit();

        return new Datos(usuario, cuenta, obligacion, ars, usd);
    }

    private record Datos(
            Usuario usuario,
            Cuenta cuenta,
            Obligacion obligacion,
            Moneda ars,
            Moneda usd
    ) {
    }
}
