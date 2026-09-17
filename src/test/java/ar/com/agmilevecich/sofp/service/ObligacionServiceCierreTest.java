package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.EstadoObligacion;
import ar.com.agmilevecich.sofp.domain.FormaPago;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.Movimiento;
import ar.com.agmilevecich.sofp.domain.Obligacion;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.TipoCambio;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ObligacionServiceCierreTest {

    private static final LocalDate FECHA_CIERRE = LocalDate.of(2026, 9, 15);

    private EntityManager entityManager;
    private ObligacionService obligacionService;
    private GastoService gastoService;
    private Cuenta tarjeta;
    private Categoria categoria;
    private Moneda ars;
    private Moneda usd;
    private Moneda eur;
    private long usuarioId;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();

        MovimientoService movimientoService = new MovimientoService(
                entityManager,
                new ar.com.agmilevecich.sofp.persistence.MovimientoRepository(entityManager)
        );
        obligacionService = new ObligacionService(
                entityManager,
                new ObligacionRepository(entityManager),
                new TipoCambioRepository(entityManager)
        );
        gastoService = new GastoService(movimientoService, obligacionService);

        var usuario = new ar.com.agmilevecich.sofp.domain.Usuario(
                "Juan", "Pérez", "juan.cierre." + System.nanoTime() + "@test.com", "hash"
        );
        PerfilFinanciero perfil = new PerfilFinanciero("Perfil principal", usuario);
        usuario.agregarPerfilFinanciero(perfil);

        InstitucionFinanciera institucion = new InstitucionFinanciera(
                "Banco de Prueba", TipoInstitucionFinanciera.BANCO
        );
        ars = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        usd = new Moneda("USD", "Dólar estadounidense", 2, TipoMoneda.FIAT);
        eur = new Moneda("EUR", "Euro", 2, TipoMoneda.FIAT);
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
        entityManager.persist(eur);
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
    void deberiaCerrarCicloSinTipoCambioParaObligacionEnMonedaDeLiquidacion() {
        Obligacion obligacion = registrarConsumo(ars, new BigDecimal("100.00"));

        List<Obligacion> cerradas = obligacionService.cerrarCiclo(
                tarjeta.getId(), FECHA_CIERRE
        );

        assertEquals(1, cerradas.size());
        assertEquals(obligacion.getId(), cerradas.get(0).getId());
        assertNull(cerradas.get(0).getImporteValorizacionCierre());
        assertNull(cerradas.get(0).getTipoCambioCierre());
        assertEquals(new BigDecimal("100.00"), cerradas.get(0).getSaldoPendiente());
        assertEquals(EstadoObligacion.PENDIENTE, cerradas.get(0).getEstado());
    }

    @Test
    void deberiaCerrarCicloValorizandoObligacionUsdConCotizacionHistoricaDelCierre() {
        Obligacion obligacion = registrarConsumo(usd, new BigDecimal("100.00"));
        TipoCambio tipoCambio = registrarTipoCambio(
                usd, ars, new BigDecimal("1500.00"),
                LocalDateTime.of(2026, 9, 15, 23, 59)
        );

        List<Obligacion> cerradas = obligacionService.cerrarCiclo(
                tarjeta.getId(), FECHA_CIERRE
        );

        assertEquals(1, cerradas.size());
        assertEquals(0, new BigDecimal("150000.00")
                .compareTo(cerradas.get(0).getImporteValorizacionCierre()));
        assertEquals(tipoCambio.getId(), cerradas.get(0).getTipoCambioCierre().getId());
        assertEquals(new BigDecimal("100.00"), cerradas.get(0).getSaldoPendiente());
        assertEquals(EstadoObligacion.PENDIENTE, cerradas.get(0).getEstado());
    }

    @Test
    void deberiaCerrarTodasLasObligacionesDelMismoCiclo() {
        Obligacion obligacionArs = registrarConsumo(ars, new BigDecimal("100.00"));
        Obligacion obligacionUsd = registrarConsumo(usd, new BigDecimal("100.00"));
        TipoCambio tipoCambio = registrarTipoCambio(
                usd, ars, new BigDecimal("1500.00"),
                LocalDateTime.of(2026, 9, 15, 18, 0)
        );

        List<Obligacion> cerradas = obligacionService.cerrarCiclo(
                tarjeta.getId(), FECHA_CIERRE
        );

        assertEquals(2, cerradas.size());

        Obligacion arsCerrada = cerradas.stream()
                .filter(o -> o.getId().equals(obligacionArs.getId()))
                .findFirst()
                .orElseThrow();
        Obligacion usdCerrada = cerradas.stream()
                .filter(o -> o.getId().equals(obligacionUsd.getId()))
                .findFirst()
                .orElseThrow();

        assertNull(arsCerrada.getImporteValorizacionCierre());
        assertNull(arsCerrada.getTipoCambioCierre());
        assertEquals(0, new BigDecimal("150000.00")
                .compareTo(usdCerrada.getImporteValorizacionCierre()));
        assertEquals(tipoCambio.getId(), usdCerrada.getTipoCambioCierre().getId());
    }

    @Test
    void deberiaRevertirTodoElCierreSiFaltaUnaCotizacionMultidivisa() {
        Obligacion obligacionUsd = registrarConsumo(usd, new BigDecimal("100.00"));
        Obligacion obligacionEur = registrarConsumo(eur, new BigDecimal("100.00"));
        registrarTipoCambio(
                usd, ars, new BigDecimal("1500.00"),
                LocalDateTime.of(2026, 9, 15, 18, 0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> obligacionService.cerrarCiclo(tarjeta.getId(), FECHA_CIERRE)
        );

        entityManager.clear();

        Obligacion usdRecargada = obligacionService.buscarPorId(obligacionUsd.getId())
                .orElseThrow();
        Obligacion eurRecargada = obligacionService.buscarPorId(obligacionEur.getId())
                .orElseThrow();

        assertNull(usdRecargada.getImporteValorizacionCierre());
        assertNull(usdRecargada.getTipoCambioCierre());
        assertNull(eurRecargada.getImporteValorizacionCierre());
        assertNull(eurRecargada.getTipoCambioCierre());
    }

    @Test
    void deberiaRechazarUnSegundoCierreDeUnaObligacionYaValorizada() {
        persistirObligacion(usd, new BigDecimal("100.00"));
        registrarTipoCambio(
                usd, ars, new BigDecimal("1500.00"),
                LocalDateTime.of(2026, 9, 15, 18, 0)
        );

        obligacionService.cerrarCiclo(tarjeta.getId(), FECHA_CIERRE);

        assertThrows(
                IllegalStateException.class,
                () -> obligacionService.cerrarCiclo(tarjeta.getId(), FECHA_CIERRE)
        );

        entityManager.clear();
        Obligacion recargada = obligacionService.listarTodas().get(0);

        assertEquals(0, new BigDecimal("150000.00")
                .compareTo(recargada.getImporteValorizacionCierre()));
        assertEquals(EstadoObligacion.PENDIENTE, recargada.getEstado());
    }

    @Test
    void deberiaValorarElImporteOriginalAunqueExistaPagoParcialAntesDelCierre() {
        Obligacion obligacion = persistirObligacion(usd, new BigDecimal("100.00"));

        obligacionService.registrarPago(
                obligacion.getId(),
                new BigDecimal("40.00"),
                usuarioId
        );
        registrarTipoCambio(
                usd, ars, new BigDecimal("1500.00"),
                LocalDateTime.of(2026, 9, 15, 18, 0)
        );

        Obligacion antesDelCierre = obligacionService.buscarPorId(obligacion.getId())
                .orElseThrow();
        assertEquals(new BigDecimal("60.00"), antesDelCierre.getSaldoPendiente());

        obligacionService.cerrarCiclo(tarjeta.getId(), FECHA_CIERRE);

        entityManager.clear();
        Obligacion cerrada = obligacionService.buscarPorId(obligacion.getId())
                .orElseThrow();

        assertEquals(0, new BigDecimal("150000.00")
                .compareTo(cerrada.getImporteValorizacionCierre()));
        assertEquals(new BigDecimal("60.00"), cerrada.getSaldoPendiente());
        assertEquals(EstadoObligacion.PARCIAL, cerrada.getEstado());
    }

    private Obligacion registrarConsumo(Moneda monedaOriginal, BigDecimal importe) {
        Movimiento movimiento = gastoService.registrar(
                tarjeta,
                categoria,
                monedaOriginal,
                importe,
                LocalDateTime.of(2026, 9, 10, 12, 0),
                "Compra con tarjeta",
                FormaPago.TARJETA_CREDITO,
                usuarioId
        );
        return obligacionService.buscarPorMovimientoOrigen(movimiento.getId())
                .orElseThrow();
    }

    private Obligacion persistirObligacion(Moneda monedaOriginal, BigDecimal importe) {
        return registrarConsumo(monedaOriginal, importe);
    }

    private TipoCambio registrarTipoCambio(
            Moneda monedaOrigen,
            Moneda monedaDestino,
            BigDecimal cotizacion,
            LocalDateTime fechaHora
    ) {
        TipoCambio tipoCambio = new TipoCambio(
                monedaOrigen,
                monedaDestino,
                cotizacion,
                fechaHora,
                "TEST"
        );

        entityManager.getTransaction().begin();
        entityManager.persist(tipoCambio);
        entityManager.getTransaction().commit();
        return tipoCambio;
    }
}
