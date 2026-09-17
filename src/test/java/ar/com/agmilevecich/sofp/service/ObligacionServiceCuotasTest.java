package ar.com.agmilevecich.sofp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.CicloFacturacion;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.Cuota;
import ar.com.agmilevecich.sofp.domain.FormaPago;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.Movimiento;
import ar.com.agmilevecich.sofp.domain.Obligacion;
import ar.com.agmilevecich.sofp.domain.TipoCambio;
import ar.com.agmilevecich.sofp.domain.TipoMovimiento;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.repository.TipoCambioRepository;

class ObligacionServiceCuotasTest extends BaseServiceTest {

    private Cuenta tarjeta;
    private Categoria categoria;
    private Usuario usuario;
    private Moneda ars;
    private Moneda usd;
    private GastoService gastoService;
    private ObligacionService obligacionService;

    @Override
    protected void setUpTestData() {
        usuario = registrarUsuario("cuotas@test.com");
        ars = registrarMoneda("ARS");
        usd = registrarMoneda("USD");
        tarjeta = registrarCuentaTarjeta("Tarjeta Cuotas", ars, 15, new BigDecimal("100000.00"), usuario);
        categoria = registrarCategoria("Compras");
        gastoService = new GastoService(new MovimientoService(entityManager), new ObligacionService(entityManager));
        obligacionService = new ObligacionService(entityManager, new TipoCambioRepository(entityManager));
    }

    @Test
    void deberiaAsignarCadaCuotaAlCicloDeFacturacionCorrespondiente() {
        Movimiento movimiento = gastoService.registrar(
                tarjeta,
                categoria,
                new BigDecimal("10000.00"),
                LocalDateTime.of(2026, 9, 10, 12, 0),
                "Compra en cuotas",
                FormaPago.TARJETA_CREDITO,
                usuario.getId(),
                3
        );

        Obligacion obligacion = new ObligacionService(entityManager)
                .buscarPorMovimientoOrigen(movimiento.getId())
                .orElseThrow();

        assertEquals(3, obligacion.getCuotas().size());
        assertEquals(0, new BigDecimal("3333.33").compareTo(obligacion.getCuotas().get(0).getImporteOriginal()));
        assertEquals(0, new BigDecimal("3333.33").compareTo(obligacion.getCuotas().get(1).getImporteOriginal()));
        assertEquals(0, new BigDecimal("3333.34").compareTo(obligacion.getCuotas().get(2).getImporteOriginal()));

        assertEquals(LocalDate.of(2026, 9, 15), obligacion.getCuotas().get(0).getFechaCierreCiclo());
        assertEquals(LocalDate.of(2026, 10, 15), obligacion.getCuotas().get(1).getFechaCierreCiclo());
        assertEquals(LocalDate.of(2026, 11, 15), obligacion.getCuotas().get(2).getFechaCierreCiclo());
    }

    @Test
    void deberiaMantenerSaldoYEstadoDeLaObligacionAlGenerarCuotas() {
        Movimiento movimiento = gastoService.registrar(
                tarjeta,
                categoria,
                new BigDecimal("10000.00"),
                LocalDateTime.of(2026, 9, 10, 12, 0),
                "Compra en cuotas",
                FormaPago.TARJETA_CREDITO,
                usuario.getId(),
                3
        );

        Obligacion obligacion = new ObligacionService(entityManager)
                .buscarPorMovimientoOrigen(movimiento.getId())
                .orElseThrow();

        assertEquals(0, new BigDecimal("10000.00").compareTo(obligacion.getSaldoPendiente()));
        assertEquals(Obligacion.Estado.PENDIENTE, obligacion.getEstado());
        assertEquals(3, obligacion.getCuotas().size());
        assertTrue(obligacion.getCuotas().stream().allMatch(c -> c.getSaldoPendiente().signum() > 0));
    }

    @Test
    void deberiaAsignarLasCuotasPosterioresAunqueLaCompraEsteAntesDelCierre() {
        Movimiento movimiento = gastoService.registrar(
                tarjeta,
                categoria,
                new BigDecimal("9000.00"),
                LocalDateTime.of(2026, 9, 14, 23, 0),
                "Compra en cuotas",
                FormaPago.TARJETA_CREDITO,
                usuario.getId(),
                3
        );

        Obligacion obligacion = new ObligacionService(entityManager)
                .buscarPorMovimientoOrigen(movimiento.getId())
                .orElseThrow();

        assertEquals(LocalDate.of(2026, 9, 15), obligacion.getFechaCierreCiclo());
        assertEquals(LocalDate.of(2026, 9, 15), obligacion.getCuotas().get(0).getFechaCierreCiclo());
        assertEquals(LocalDate.of(2026, 10, 15), obligacion.getCuotas().get(1).getFechaCierreCiclo());
        assertEquals(LocalDate.of(2026, 11, 15), obligacion.getCuotas().get(2).getFechaCierreCiclo());
    }

    @Test
    void deberiaIncluirLaObligacionEnCadaCierreQueContengaUnaCuotaPendiente() {
        Movimiento movimiento = gastoService.registrar(
                tarjeta,
                categoria,
                new BigDecimal("9000.00"),
                LocalDateTime.of(2026, 9, 10, 12, 0),
                "Compra en cuotas",
                FormaPago.TARJETA_CREDITO,
                usuario.getId(),
                3
        );

        Obligacion obligacion = new ObligacionService(entityManager)
                .buscarPorMovimientoOrigen(movimiento.getId())
                .orElseThrow();

        List<Obligacion> cierreSeptiembre = obligacionService.cerrarCiclo(tarjeta.getId(), LocalDate.of(2026, 9, 15));
        List<Obligacion> cierreOctubre = obligacionService.cerrarCiclo(tarjeta.getId(), LocalDate.of(2026, 10, 15));
        List<Obligacion> cierreNoviembre = obligacionService.cerrarCiclo(tarjeta.getId(), LocalDate.of(2026, 11, 15));

        assertEquals(1, cierreSeptiembre.size());
        assertEquals(1, cierreOctubre.size());
        assertEquals(1, cierreNoviembre.size());
        assertEquals(obligacion.getId(), cierreSeptiembre.get(0).getId());
        assertEquals(obligacion.getId(), cierreOctubre.get(0).getId());
        assertEquals(obligacion.getId(), cierreNoviembre.get(0).getId());
        assertEquals(0, new BigDecimal("3000.00").compareTo(cierreSeptiembre.get(0).getCuotas().get(0).getSaldoPendiente()));
    }

    @Test
    void deberiaValorarCadaCuotaMultidivisaConLaCotizacionDeSuCierre() {
        Obligacion obligacion = registrarCompraUsdEnTresCuotas(new BigDecimal("900.00"));

        TipoCambio cambioSeptiembre = registrarTipoCambio(
                new BigDecimal("1500.00"),
                LocalDateTime.of(2026, 9, 15, 23, 59)
        );
        TipoCambio cambioOctubre = registrarTipoCambio(
                new BigDecimal("1600.00"),
                LocalDateTime.of(2026, 10, 15, 23, 59)
        );
        TipoCambio cambioNoviembre = registrarTipoCambio(
                new BigDecimal("1700.00"),
                LocalDateTime.of(2026, 11, 15, 23, 59)
        );

        List<Obligacion> cierreSeptiembre = obligacionService.cerrarCiclo(
                tarjeta.getId(), LocalDate.of(2026, 9, 15)
        );

        assertEquals(1, cierreSeptiembre.size());
        assertEquals(obligacion.getId(), cierreSeptiembre.get(0).getId());
        assertEquals(0, new BigDecimal("450000.00")
                .compareTo(cierreSeptiembre.get(0).getImporteValorizacionCierre()));
        assertEquals(cambioSeptiembre.getId(), cierreSeptiembre.get(0).getTipoCambioCierre().getId());

        List<Obligacion> cierreOctubre = obligacionService.cerrarCiclo(
                tarjeta.getId(), LocalDate.of(2026, 10, 15)
        );

        assertEquals(1, cierreOctubre.size());
        assertEquals(obligacion.getId(), cierreOctubre.get(0).getId());
        assertEquals(0, new BigDecimal("480000.00")
                .compareTo(cierreOctubre.get(0).getImporteValorizacionCierre()));
        assertEquals(cambioOctubre.getId(), cierreOctubre.get(0).getTipoCambioCierre().getId());

        List<Obligacion> cierreNoviembre = obligacionService.cerrarCiclo(
                tarjeta.getId(), LocalDate.of(2026, 11, 15)
        );

        assertEquals(1, cierreNoviembre.size());
        assertEquals(obligacion.getId(), cierreNoviembre.get(0).getId());
        assertEquals(0, new BigDecimal("510000.00")
                .compareTo(cierreNoviembre.get(0).getImporteValorizacionCierre()));
        assertEquals(cambioNoviembre.getId(), cierreNoviembre.get(0).getTipoCambioCierre().getId());
    }

    private Obligacion registrarCompraUsdEnTresCuotas(BigDecimal importe) {
        Movimiento movimiento = gastoService.registrar(
                tarjeta,
                categoria,
                usd,
                importe,
                LocalDateTime.of(2026, 9, 10, 12, 0),
                "Compra USD con tarjeta en cuotas",
                FormaPago.TARJETA_CREDITO,
                usuario.getId(),
                3
        );

        return obligacionService.buscarPorMovimientoOrigen(movimiento.getId()).orElseThrow();
    }

    private TipoCambio registrarTipoCambio(BigDecimal cotizacion, LocalDateTime fechaHora) {
        TipoCambio tipoCambio = new TipoCambio(
                usd,
                ars,
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
