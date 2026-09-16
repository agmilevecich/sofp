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
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import ar.com.agmilevecich.sofp.persistence.ObligacionRepository;
import ar.com.agmilevecich.sofp.persistence.TipoCambioRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ObligacionServiceCierreTest {

    private EntityManager entityManager;
    private ObligacionService obligacionService;
    private GastoService gastoService;
    private Cuenta tarjeta;
    private Categoria categoria;
    private Moneda ars;
    private Moneda usd;
    private long usuarioId;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();

        MovimientoRepository movimientoRepository = new MovimientoRepository(entityManager);
        MovimientoService movimientoService = new MovimientoService(entityManager, movimientoRepository);
        obligacionService = new ObligacionService(
                entityManager,
                new ObligacionRepository(entityManager),
                new TipoCambioRepository(entityManager)
        );
        gastoService = new GastoService(movimientoService, obligacionService);

        var usuario = new ar.com.agmilevecich.sofp.domain.Usuario(
                "Juan", "Pérez", "juan.cierre." + System.nanoTime() + "@test.com", "hash"
        );
        usuarioId = usuario.getId() == null ? 0L : usuario.getId();
        PerfilFinanciero perfil = new PerfilFinanciero("Perfil principal", usuario);
        usuario.agregarPerfilFinanciero(perfil);

        InstitucionFinanciera institucion = new InstitucionFinanciera(
                "Banco de Prueba", TipoInstitucionFinanciera.BANCO
        );
        ars = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        usd = new Moneda("USD", "Dólar estadounidense", 2, TipoMoneda.FIAT);
        tarjeta = new Cuenta(
                "Tarjeta principal", perfil, institucion, ars,
                new BigDecimal("100000.00"), 15, 5
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
        usuarioId = usuario.getId();
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
    void deberiaValorarObligacionesMultidivisaAlCerrarCiclo() {
        Movimiento consumoUsd = gastoService.registrar(
                tarjeta, categoria, usd, new BigDecimal("100.00"),
                LocalDateTime.of(2026, 9, 10, 12, 0),
                "Compra USD", FormaPago.TARJETA_CREDITO, usuarioId
        );

        entityManager.getTransaction().begin();
        entityManager.persist(new TipoCambio(
                usd, ars, new BigDecimal("1500.00"),
                LocalDateTime.of(2026, 9, 15, 10, 0), "Cotizacion cierre"
        ));
        entityManager.getTransaction().commit();

        List<Obligacion> cerradas = obligacionService.cerrarCiclo(
                tarjeta.getId(), LocalDate.of(2026, 9, 15)
        );

        assertEquals(1, cerradas.size());
        assertEquals(new BigDecimal("150000.00"),
                cerradas.get(0).getImporteValorizacionCierre());
        assertEquals(new BigDecimal("100.00"),
                cerradas.get(0).getImporteOriginal());
        assertEquals(consumoUsd.getId(), cerradas.get(0).getMovimientoOrigen().getId());
    }

    @Test
    void deberiaNoValorarObligacionesEnLaMonedaDeLaTarjeta() {
        gastoService.registrar(
                tarjeta, categoria, ars, new BigDecimal("10000.00"),
                LocalDateTime.of(2026, 9, 10, 12, 0),
                "Compra ARS", FormaPago.TARJETA_CREDITO, usuarioId
        );

        List<Obligacion> cerradas = obligacionService.cerrarCiclo(
                tarjeta.getId(), LocalDate.of(2026, 9, 15)
        );

        assertEquals(1, cerradas.size());
        assertEquals(null, cerradas.get(0).getImporteValorizacionCierre());
        assertEquals(null, cerradas.get(0).getTipoCambioCierre());
    }

    @Test
    void deberiaFallarSiFaltaCotizacionHistoricaYHacerRollback() {
        Movimiento consumoUsd = gastoService.registrar(
                tarjeta, categoria, usd, new BigDecimal("100.00"),
                LocalDateTime.of(2026, 9, 10, 12, 0),
                "Compra USD", FormaPago.TARJETA_CREDITO, usuarioId
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> obligacionService.cerrarCiclo(
                        tarjeta.getId(), LocalDate.of(2026, 9, 15)
                )
        );

        entityManager.clear();
        Obligacion recargada = entityManager.createQuery(
                        "SELECT o FROM Obligacion o WHERE o.movimientoOrigen.id = :movimientoId",
                        Obligacion.class
                )
                .setParameter("movimientoId", consumoUsd.getId())
                .getSingleResult();

        assertEquals(null, recargada.getImporteValorizacionCierre());
        assertEquals(null, recargada.getTipoCambioCierre());
    }

    @Test
    void deberiaUsarLaUltimaCotizacionDelDia() {
        gastoService.registrar(
                tarjeta, categoria, usd, new BigDecimal("100.00"),
                LocalDateTime.of(2026, 9, 10, 12, 0),
                "Compra USD", FormaPago.TARJETA_CREDITO, usuarioId
        );

        entityManager.getTransaction().begin();
        entityManager.persist(new TipoCambio(
                usd, ars, new BigDecimal("1490.00"),
                LocalDateTime.of(2026, 9, 15, 10, 0), "Cotizacion apertura"
        ));
        entityManager.persist(new TipoCambio(
                usd, ars, new BigDecimal("1500.00"),
                LocalDateTime.of(2026, 9, 15, 18, 0), "Cotizacion cierre"
        ));
        entityManager.getTransaction().commit();

        List<Obligacion> cerradas = obligacionService.cerrarCiclo(
                tarjeta.getId(), LocalDate.of(2026, 9, 15)
        );

        assertEquals(new BigDecimal("150000.00"),
                cerradas.get(0).getImporteValorizacionCierre());
        assertEquals(new BigDecimal("1500.00"),
                cerradas.get(0).getTipoCambioCierre().getCotizacion());
    }
}
