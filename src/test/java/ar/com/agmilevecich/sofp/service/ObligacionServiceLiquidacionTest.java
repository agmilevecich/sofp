package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.EstadoObligacion;
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
import ar.com.agmilevecich.sofp.persistence.ObligacionRepository;
import ar.com.agmilevecich.sofp.persistence.TipoCambioRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ObligacionServiceLiquidacionTest {

    private EntityManager entityManager;
    private ObligacionService obligacionService;
    private Usuario usuario;
    private Cuenta cuenta;
    private Categoria categoria;
    private Moneda ars;
    private Moneda usd;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();
        obligacionService = new ObligacionService(
                entityManager,
                new ObligacionRepository(entityManager),
                new TipoCambioRepository(entityManager)
        );

        usuario = new Usuario("Juan", "Pérez", "juan." + System.nanoTime() + "@test.com", "hash");
        PerfilFinanciero perfil = new PerfilFinanciero("Perfil principal", usuario);
        usuario.agregarPerfilFinanciero(perfil);
        InstitucionFinanciera institucion = new InstitucionFinanciera("Banco de Prueba", TipoInstitucionFinanciera.BANCO);
        ars = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        usd = new Moneda("USD", "Dólar estadounidense", 2, TipoMoneda.FIAT);
        cuenta = new Cuenta("Tarjeta principal", perfil, institucion, ars, new BigDecimal("500000.00"), 15, 10);
        categoria = new Categoria("Alimentos", perfil);

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
            if (entityManager.getTransaction().isActive()) entityManager.getTransaction().rollback();
            entityManager.close();
        }
        JpaTestManager.close();
    }

    @Test
    void deberiaLiquidarSoloElSaldoPendienteUsandoLaUltimaCotizacionDisponible() {
        Obligacion obligacion = crearObligacionUsd(new BigDecimal("100.00"));
        obligacionService.registrarPago(obligacion.getId(), new BigDecimal("40.00"), usuario.getId());

        guardarTipoCambio(new BigDecimal("1500.00"), LocalDateTime.of(2026, 9, 17, 10, 0));
        TipoCambio cambioLiquidacion = guardarTipoCambio(new BigDecimal("1600.00"), LocalDateTime.of(2026, 9, 17, 15, 0));
        guardarTipoCambio(new BigDecimal("1700.00"), LocalDateTime.of(2026, 9, 17, 18, 0));

        Obligacion actualizada = obligacionService.liquidar(
                obligacion.getId(),
                LocalDateTime.of(2026, 9, 17, 16, 0),
                usuario.getId()
        );

        assertEquals(new BigDecimal("60.00"), actualizada.getSaldoPendiente());
        assertEquals(new BigDecimal("96000.00"), actualizada.getImporteLiquidacion());
        assertEquals(new BigDecimal("96000.00"), actualizada.getSaldoLiquidacion());
        assertEquals(cambioLiquidacion.getId(), actualizada.getTipoCambioLiquidacion().getId());
        assertEquals(EstadoObligacion.PARCIAL, actualizada.getEstado());
    }

    @Test
    void deberiaPersistirLiquidacion() {
        Obligacion obligacion = crearObligacionUsd(new BigDecimal("100.00"));
        guardarTipoCambio(new BigDecimal("1600.00"), LocalDateTime.of(2026, 9, 17, 15, 0));

        obligacionService.liquidar(
                obligacion.getId(),
                LocalDateTime.of(2026, 9, 17, 16, 0),
                usuario.getId()
        );

        entityManager.clear();
        Obligacion recargada = obligacionService.buscarPorId(obligacion.getId()).orElseThrow();

        assertEquals(new BigDecimal("160000.00"), recargada.getImporteLiquidacion());
        assertEquals(new BigDecimal("160000.00"), recargada.getSaldoLiquidacion());
        assertEquals(new BigDecimal("1600.00"), recargada.getTipoCambioLiquidacion().getCotizacion());
    }

    @Test
    void deberiaRechazarLiquidacionSinCotizacionDisponible() {
        Obligacion obligacion = crearObligacionUsd(new BigDecimal("100.00"));

        assertThrows(
                IllegalArgumentException.class,
                () -> obligacionService.liquidar(
                        obligacion.getId(),
                        LocalDateTime.of(2026, 9, 17, 16, 0),
                        usuario.getId()
                )
        );

        entityManager.clear();
        Obligacion recargada = obligacionService.buscarPorId(obligacion.getId()).orElseThrow();
        assertEquals(null, recargada.getImporteLiquidacion());
        assertEquals(new BigDecimal("100.00"), recargada.getSaldoPendiente());
    }

    @Test
    void deberiaRechazarLiquidacionDeUsuarioNoAutorizado() {
        Obligacion obligacion = crearObligacionUsd(new BigDecimal("100.00"));
        guardarTipoCambio(new BigDecimal("1600.00"), LocalDateTime.of(2026, 9, 17, 15, 0));

        assertThrows(
                IllegalArgumentException.class,
                () -> obligacionService.liquidar(
                        obligacion.getId(),
                        LocalDateTime.of(2026, 9, 17, 16, 0),
                        usuario.getId() + 1
                )
        );
    }

    private Obligacion crearObligacionUsd(BigDecimal importe) {
        Movimiento movimiento = new Movimiento(
                cuenta,
                categoria,
                usd,
                TipoMovimiento.EGRESO,
                importe,
                LocalDateTime.of(2026, 9, 17, 9, 0),
                "Compra en dólares"
        );

        entityManager.getTransaction().begin();
        entityManager.persist(movimiento);
        entityManager.getTransaction().commit();

        return obligacionService.registrar(movimiento);
    }

    private TipoCambio guardarTipoCambio(BigDecimal cotizacion, LocalDateTime fechaHora) {
        TipoCambio tipoCambio = new TipoCambio(usd, ars, cotizacion, fechaHora, "BCRA");
        entityManager.getTransaction().begin();
        entityManager.persist(tipoCambio);
        entityManager.getTransaction().commit();
        return tipoCambio;
    }
}
