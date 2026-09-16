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

class MovimientoCreditoMultimonedaTest {

    private EntityManager entityManager;
    private MovimientoService movimientoService;
    private GastoService gastoService;
    private Usuario usuario;
    private Cuenta tarjeta;
    private Categoria categoria;
    private Moneda ars;
    private Moneda usd;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();

        MovimientoRepository movimientoRepository = new MovimientoRepository(entityManager);
        movimientoService = new MovimientoService(entityManager, movimientoRepository);
        ObligacionService obligacionService = new ObligacionService(
                entityManager,
                new ar.com.agmilevecich.sofp.persistence.ObligacionRepository(entityManager)
        );
        gastoService = new GastoService(movimientoService, obligacionService);

        usuario = new Usuario(
                "Juan",
                "Pérez",
                "juan.credito.multimoneda." + System.nanoTime() + "@test.com",
                "hash"
        );
        PerfilFinanciero perfil = new PerfilFinanciero("Perfil principal", usuario);
        usuario.agregarPerfilFinanciero(perfil);

        InstitucionFinanciera institucion = new InstitucionFinanciera(
                "Banco de Prueba",
                TipoInstitucionFinanciera.BANCO
        );
        ars = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        usd = new Moneda("USD", "Dólar estadounidense", 2, TipoMoneda.FIAT);
        tarjeta = new Cuenta(
                "Tarjeta principal",
                perfil,
                institucion,
                ars,
                new BigDecimal("50000.00"),
                15,
                5
        );
        categoria = new Categoria("Alimentos", perfil);

        entityManager.getTransaction().begin();
        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(institucion);
        entityManager.persist(ars);
        entityManager.persist(usd);
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
    void deberiaUsarLaValorizacionDeCierreDeUnaObligacionMultidivisaParaElCredito() {
        Movimiento consumoUsd = gastoService.registrar(
                tarjeta,
                categoria,
                usd,
                new BigDecimal("30.00"),
                LocalDateTime.of(2026, 9, 15, 12, 0),
                "Compra USD con tarjeta",
                FormaPago.TARJETA_CREDITO,
                usuario.getId()
        );

        Obligacion obligacion = entityManager.createQuery(
                        "SELECT o FROM Obligacion o WHERE o.movimientoOrigen.id = :movimientoId",
                        Obligacion.class
                )
                .setParameter("movimientoId", consumoUsd.getId())
                .getSingleResult();

        TipoCambio tipoCambioCierre = new TipoCambio(
                usd,
                ars,
                new BigDecimal("1500.00"),
                LocalDateTime.of(2026, 9, 15, 23, 59),
                "Cotizacion cierre"
        );

        entityManager.getTransaction().begin();
        entityManager.persist(tipoCambioCierre);
        obligacion.valorarCierre(tipoCambioCierre);
        entityManager.flush();
        entityManager.getTransaction().commit();

        assertEquals(
                new BigDecimal("45000.00"),
                obligacion.getImporteValorizacionCierre()
        );

        Movimiento consumoArs = movimientoService.registrar(
                tarjeta,
                categoria,
                ars,
                TipoMovimiento.EGRESO,
                new BigDecimal("5000.00"),
                LocalDateTime.of(2026, 9, 16, 10, 0),
                "Compra ARS con tarjeta",
                FormaPago.TARJETA_CREDITO,
                usuario.getId()
        );

        assertEquals(new BigDecimal("5000.00"), consumoArs.getImporte());

        assertThrows(
                IllegalArgumentException.class,
                () -> movimientoService.registrar(
                        tarjeta,
                        categoria,
                        ars,
                        TipoMovimiento.EGRESO,
                        new BigDecimal("5001.00"),
                        LocalDateTime.of(2026, 9, 16, 10, 5),
                        "Compra ARS que supera el limite",
                        FormaPago.TARJETA_CREDITO,
                        usuario.getId()
                )
        );
    }
}
