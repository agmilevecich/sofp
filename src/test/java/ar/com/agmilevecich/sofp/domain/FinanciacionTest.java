package ar.com.agmilevecich.sofp.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FinanciacionTest {

    @Test
    void deberiaCrearFinanciacionConCapitalInicial() {
        Obligacion obligacion = crearObligacion();

        Financiacion financiacion = new Financiacion(
                obligacion,
                LocalDate.of(2026, 9, 26),
                new BigDecimal("240000.00")
        );

        assertSame(obligacion, financiacion.getObligacion());
        assertEquals(LocalDate.of(2026, 9, 26), financiacion.getFechaInicio());
        assertEquals(new BigDecimal("240000.00"), financiacion.getCapitalOriginal());
        assertEquals(new BigDecimal("240000.00"), financiacion.getSaldoCapital());
        assertTrue(financiacion.estaPendiente());
        assertFalse(financiacion.estaCancelada());
    }

    @Test
    void deberiaRegistrarPagoParcialSobreCapital() {
        Financiacion financiacion = new Financiacion(
                crearObligacion(),
                LocalDate.of(2026, 9, 26),
                new BigDecimal("240000.00")
        );

        financiacion.registrarPago(new BigDecimal("80000.00"));

        assertEquals(new BigDecimal("160000.00"), financiacion.getSaldoCapital());
        assertTrue(financiacion.estaPendiente());
        assertFalse(financiacion.estaCancelada());
    }

    @Test
    void deberiaCancelarFinanciacionAlPagarTodoElCapital() {
        Financiacion financiacion = new Financiacion(
                crearObligacion(),
                LocalDate.of(2026, 9, 26),
                new BigDecimal("240000.00")
        );

        financiacion.registrarPago(new BigDecimal("240000.00"));

        assertEquals(BigDecimal.ZERO.setScale(2), financiacion.getSaldoCapital());
        assertFalse(financiacion.estaPendiente());
        assertTrue(financiacion.estaCancelada());
    }

    @Test
    void deberiaRechazarObligacionNula() {
        assertThrows(NullPointerException.class, () ->
                new Financiacion(null, LocalDate.of(2026, 9, 26), new BigDecimal("240000.00")));
    }

    @Test
    void deberiaRechazarFechaInicioNula() {
        assertThrows(NullPointerException.class, () ->
                new Financiacion(crearObligacion(), null, new BigDecimal("240000.00")));
    }

    @Test
    void deberiaRechazarCapitalNuloOCero() {
        Obligacion obligacion = crearObligacion();

        assertThrows(IllegalArgumentException.class, () ->
                new Financiacion(obligacion, LocalDate.of(2026, 9, 26), null));

        assertThrows(IllegalArgumentException.class, () ->
                new Financiacion(obligacion, LocalDate.of(2026, 9, 26), BigDecimal.ZERO));
    }

    @Test
    void deberiaRechazarPagoMayorAlSaldo() {
        Financiacion financiacion = new Financiacion(
                crearObligacion(),
                LocalDate.of(2026, 9, 26),
                new BigDecimal("240000.00")
        );

        assertThrows(IllegalArgumentException.class, () ->
                financiacion.registrarPago(new BigDecimal("240000.01")));

        assertEquals(new BigDecimal("240000.00"), financiacion.getSaldoCapital());
    }

    @Test
    void deberiaAgregarFinanciacionASuObligacion() {
        Obligacion obligacion = crearObligacion();
        Financiacion financiacion = new Financiacion(
                obligacion,
                LocalDate.of(2026, 9, 26),
                new BigDecimal("240000.00")
        );

        obligacion.agregarFinanciacion(financiacion);

        assertEquals(1, obligacion.getFinanciaciones().size());
        assertSame(financiacion, obligacion.getFinanciaciones().get(0));
    }

    @Test
    void deberiaRechazarFinanciacionDeOtraObligacion() {
        Obligacion obligacion = crearObligacion();
        Financiacion otra = new Financiacion(
                crearObligacion(),
                LocalDate.of(2026, 9, 26),
                new BigDecimal("240000.00")
        );

        assertThrows(IllegalArgumentException.class, () -> obligacion.agregarFinanciacion(otra));
    }

    private Obligacion crearObligacion() {
        Usuario usuario = new Usuario(
                "Ariel", "Milevecich",
                "ariel.financiacion." + System.nanoTime() + "@test.com", "hash"
        );
        PerfilFinanciero perfil = new PerfilFinanciero("Personal", usuario);
        InstitucionFinanciera banco = new InstitucionFinanciera(
                "Banco Santander", TipoInstitucionFinanciera.BANCO
        );
        Moneda moneda = new Moneda("ARS", "Peso Argentino", 2, TipoMoneda.FIAT);
        Cuenta cuenta = new Cuenta(
                "Tarjeta", perfil, banco, moneda,
                new BigDecimal("500000.00"), 15, 10
        );
        Categoria categoria = new Categoria("Supermercado", perfil);

        Movimiento movimiento = new Movimiento(
                cuenta, categoria, TipoMovimiento.EGRESO,
                new BigDecimal("320000.00"),
                LocalDateTime.of(2026, 9, 4, 12, 0),
                "Compra con tarjeta", FormaPago.TARJETA_CREDITO
        );

        return new Obligacion(movimiento);
    }
}
