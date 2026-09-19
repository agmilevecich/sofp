package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.CicloFacturacion;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.FormaPago;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
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
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SaldoTarjetaCreditoTest {

    private EntityManager entityManager;
    private CuentaService cuentaService;
    private GastoService gastoService;
    private ObligacionService obligacionService;
    private Usuario usuario;
    private Cuenta tarjeta;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();

        MovimientoRepository movimientoRepository =
                new MovimientoRepository(entityManager);
        MovimientoService movimientoService =
                new MovimientoService(entityManager, movimientoRepository);
        obligacionService = new ObligacionService(
                entityManager,
                new ObligacionRepository(entityManager)
        );
        gastoService = new GastoService(movimientoService, obligacionService);

        cuentaService = new CuentaService(
                new CuentaRepository(entityManager),
                movimientoRepository,
                entityManager
        );

        usuario = new Usuario(
                "Juan",
                "Pérez",
                "juan." + System.nanoTime() + "@test.com",
                "hash"
        );
        PerfilFinanciero perfilFinanciero = new PerfilFinanciero(
                "Perfil principal",
                usuario
        );
        usuario.agregarPerfilFinanciero(perfilFinanciero);

        InstitucionFinanciera institucionFinanciera = new InstitucionFinanciera(
                "Banco de Prueba",
                TipoInstitucionFinanciera.BANCO
        );
        Moneda moneda = new Moneda(
                "ARS",
                "Peso argentino",
                2,
                TipoMoneda.FIAT
        );
        tarjeta = new Cuenta(
                "Tarjeta principal",
                perfilFinanciero,
                institucionFinanciera,
                moneda,
                new BigDecimal("50000.00"),
                15,
                5
        );
        categoria = new Categoria("Alimentos", perfilFinanciero);

        entityManager.getTransaction().begin();
        entityManager.persist(usuario);
        entityManager.persist(perfilFinanciero);
        entityManager.persist(institucionFinanciera);
        entityManager.persist(moneda);
        entityManager.persist(tarjeta);
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
    void deberiaMantenerSaldoMonetarioSinCambiosAlRegistrarCompraConTarjeta() {
        gastoService.registrar(
                tarjeta,
                categoria,
                new BigDecimal("15000.00"),
                LocalDateTime.of(2026, 9, 10, 12, 0),
                "Compra con tarjeta",
                FormaPago.TARJETA_CREDITO,
                usuario.getId()
        );

        assertEquals(
                BigDecimal.ZERO,
                cuentaService.calcularSaldo(tarjeta.getId())
        );
    }

    @Test
    void deberiaCalcularElCicloDeFacturacionDeLaObligacionCreadaPorUnaCompraConTarjeta() {
        LocalDateTime fechaConsumo = LocalDateTime.of(2026, 9, 10, 12, 0);

        var movimiento = gastoService.registrar(
                tarjeta,
                categoria,
                new BigDecimal("15000.00"),
                fechaConsumo,
                "Compra con tarjeta",
                FormaPago.TARJETA_CREDITO,
                usuario.getId()
        );

        Obligacion obligacion = obligacionService
                .buscarPorMovimientoOrigen(movimiento.getId())
                .orElseThrow();

        CicloFacturacion ciclo = obligacion.getCicloFacturacion();

        assertEquals(LocalDate.of(2026, 8, 16), ciclo.getFechaInicio());
        assertEquals(LocalDate.of(2026, 9, 15), ciclo.getFechaCierre());
        assertEquals(LocalDate.of(2026, 10, 5), ciclo.getFechaVencimiento());
    }
}
