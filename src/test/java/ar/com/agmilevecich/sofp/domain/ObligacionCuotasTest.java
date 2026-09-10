package ar.com.agmilevecich.sofp.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ObligacionCuotasTest {

    @Test
    void deberiaGenerarTresCuotasIgualesEnCiclosSucesivos() {
        Movimiento movimiento = crearMovimiento(new BigDecimal("120000.00"), LocalDateTime.of(2026, 9, 10, 12, 0));
        Obligacion obligacion = new Obligacion(movimiento);

        obligacion.generarCuotas(3);

        assertEquals(3, obligacion.getCuotas().size());
        assertEquals(new BigDecimal("40000.00"), obligacion.getCuotas().get(0).getImporteOriginal());
        assertEquals(new BigDecimal("40000.00"), obligacion.getCuotas().get(1).getImporteOriginal());
        assertEquals(new BigDecimal("40000.00"), obligacion.getCuotas().get(2).getImporteOriginal());

        assertEquals(15, obligacion.getCuotas().get(0).getFechaCierreCiclo().getDayOfMonth());
        assertEquals(15, obligacion.getCuotas().get(1).getFechaCierreCiclo().getDayOfMonth());
        assertEquals(15, obligacion.getCuotas().get(2).getFechaCierreCiclo().getDayOfMonth());
        assertEquals(2026, obligacion.getCuotas().get(0).getFechaCierreCiclo().getYear());
        assertEquals(2026, obligacion.getCuotas().get(1).getFechaCierreCiclo().getYear());
        assertEquals(2026, obligacion.getCuotas().get(2).getFechaCierreCiclo().getYear());
        assertEquals(9, obligacion.getCuotas().get(0).getFechaCierreCiclo().getMonthValue());
        assertEquals(10, obligacion.getCuotas().get(1).getFechaCierreCiclo().getMonthValue());
        assertEquals(11, obligacion.getCuotas().get(2).getFechaCierreCiclo().getMonthValue());
    }

    @Test
    void deberiaAsignarDiferenciaDeCentavosALaUltimaCuota() {
        Obligacion obligacion = new Obligacion(
                crearMovimiento(new BigDecimal("100.00"), LocalDateTime.of(2026, 9, 10, 12, 0))
        );

        obligacion.generarCuotas(3);

        assertEquals(new BigDecimal("33.33"), obligacion.getCuotas().get(0).getImporteOriginal());
        assertEquals(new BigDecimal("33.33"), obligacion.getCuotas().get(1).getImporteOriginal());
        assertEquals(new BigDecimal("33.34"), obligacion.getCuotas().get(2).getImporteOriginal());
    }

    @Test
    void deberiaAplicarPagoParcialDesdeLaCuotaMasAntigua() {
        Obligacion obligacion = new Obligacion(
                crearMovimiento(new BigDecimal("120000.00"), LocalDateTime.of(2026, 9, 10, 12, 0))
        );
        obligacion.generarCuotas(3);

        obligacion.registrarPago(new BigDecimal("50000.00"));

        assertEquals(new BigDecimal("70000.00"), obligacion.getSaldoPendiente());
        assertEquals(EstadoObligacion.PARCIAL, obligacion.getEstado());
        assertEquals(new BigDecimal("0.00"), obligacion.getCuotas().get(0).getSaldoPendiente());
        assertEquals(EstadoObligacion.PAGADA, obligacion.getCuotas().get(0).getEstado());
        assertEquals(new BigDecimal("30000.00"), obligacion.getCuotas().get(1).getSaldoPendiente());
        assertEquals(new BigDecimal("40000.00"), obligacion.getCuotas().get(2).getSaldoPendiente());
    }

    @Test
    void deberiaRechazarCantidadDeCuotasNoPositiva() {
        Obligacion obligacion = new Obligacion(
                crearMovimiento(new BigDecimal("120000.00"), LocalDateTime.of(2026, 9, 10, 12, 0))
        );

        assertThrows(IllegalArgumentException.class, () -> obligacion.generarCuotas(0));
        assertTrue(obligacion.getCuotas().isEmpty());
    }

    @Test
    void deberiaRechazarGenerarCuotasDosVeces() {
        Obligacion obligacion = new Obligacion(
                crearMovimiento(new BigDecimal("120000.00"), LocalDateTime.of(2026, 9, 10, 12, 0))
        );

        obligacion.generarCuotas(3);

        assertThrows(IllegalStateException.class, () -> obligacion.generarCuotas(2));
        assertEquals(3, obligacion.getCuotas().size());
    }

    private Movimiento crearMovimiento(BigDecimal importe, LocalDateTime fechaHora) {
        Usuario usuario = new Usuario(
                "Ariel",
                "Milevecich",
                "ariel.obligacion.cuotas." + System.nanoTime() + "@test.com",
                "hash"
        );
        PerfilFinanciero perfil = new PerfilFinanciero("Personal", usuario);
        InstitucionFinanciera banco = new InstitucionFinanciera(
                "Banco Santander",
                TipoInstitucionFinanciera.BANCO
        );
        Moneda moneda = new Moneda("ARS", "Peso Argentino", 2, TipoMoneda.FIAT);
        Cuenta tarjeta = new Cuenta(
                "Tarjeta",
                perfil,
                banco,
                moneda,
                new BigDecimal("500000.00"),
                15,
                10
        );
        Categoria categoria = new Categoria("Supermercado", perfil);

        return new Movimiento(
                tarjeta,
                categoria,
                moneda,
                TipoMovimiento.EGRESO,
                importe,
                fechaHora,
                "Compra financiada",
                FormaPago.TARJETA_CREDITO
        );
    }
}
