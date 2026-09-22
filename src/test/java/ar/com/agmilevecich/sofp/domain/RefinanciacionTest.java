package ar.com.agmilevecich.sofp.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RefinanciacionTest {

    @Test
    void deberiaGenerarCuotasConRedondeoEnLaUltima() {
        Refinanciacion refinanciacion = crearRefinanciacion(
                new BigDecimal("120000.00"),
                new BigDecimal("6000.00"),
                new BigDecimal("1000.00"),
                new BigDecimal("24.0000"),
                3
        );

        refinanciacion.generarCuotas();

        assertEquals(new BigDecimal("127000.00"), refinanciacion.getTotalPlan());
        assertEquals(new BigDecimal("42333.33"), refinanciacion.getCuotas().get(0).getImporteOriginal());
        assertEquals(new BigDecimal("42333.33"), refinanciacion.getCuotas().get(1).getImporteOriginal());
        assertEquals(new BigDecimal("42333.34"), refinanciacion.getCuotas().get(2).getImporteOriginal());
        assertEquals(LocalDate.of(2026, 10, 26), refinanciacion.getCuotas().get(0).getFechaVencimiento());
        assertEquals(LocalDate.of(2026, 12, 26), refinanciacion.getCuotas().get(2).getFechaVencimiento());
    }

    @Test
    void noDeberiaGenerarCuotasDosVeces() {
        Refinanciacion refinanciacion = crearRefinanciacion(
                new BigDecimal("120000.00"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal("24.0000"),
                3
        );

        refinanciacion.generarCuotas();

        assertThrows(IllegalStateException.class, refinanciacion::generarCuotas);
        assertEquals(3, refinanciacion.getCuotas().size());
    }

    @Test
    void noDeberiaRegistrarPagoSinCuotasGeneradas() {
        Refinanciacion refinanciacion = crearRefinanciacion(
                new BigDecimal("120000.00"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal("24.0000"),
                3
        );

        assertThrows(
                IllegalStateException.class,
                () -> refinanciacion.registrarPago(new BigDecimal("10000.00"))
        );
        assertEquals(new BigDecimal("120000.00"), refinanciacion.getSaldoPlan());
    }

    @Test
    void noDeberiaRevertirPagoSinCuotasGeneradas() {
        Refinanciacion refinanciacion = crearRefinanciacion(
                new BigDecimal("120000.00"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal("24.0000"),
                3
        );

        assertThrows(
                IllegalStateException.class,
                () -> refinanciacion.revertirPago(new BigDecimal("1.00"))
        );
        assertEquals(new BigDecimal("120000.00"), refinanciacion.getSaldoPlan());
    }

    @Test
    void deberiaAplicarPagosDesdeLaCuotaMasAntigua() {
        Refinanciacion refinanciacion = crearRefinanciacion(
                new BigDecimal("120000.00"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal("24.0000"),
                3
        );
        refinanciacion.generarCuotas();

        refinanciacion.registrarPago(new BigDecimal("50000.00"));

        assertEquals(new BigDecimal("70000.00"), refinanciacion.getSaldoPlan());
        assertEquals(new BigDecimal("0.00"), refinanciacion.getCuotas().get(0).getSaldoPendiente());
        assertEquals(new BigDecimal("30000.00"), refinanciacion.getCuotas().get(1).getSaldoPendiente());
        assertEquals(new BigDecimal("40000.00"), refinanciacion.getCuotas().get(2).getSaldoPendiente());
    }

    @Test
    void deberiaCancelarElPlanCuandoSePagaTodo() {
        Refinanciacion refinanciacion = crearRefinanciacion(
                new BigDecimal("120000.00"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal("24.0000"),
                3
        );
        refinanciacion.generarCuotas();

        refinanciacion.registrarPago(new BigDecimal("120000.00"));

        assertEquals(new BigDecimal("0.00"), refinanciacion.getSaldoPlan());
        assertEquals(EstadoRefinanciacion.CANCELADA, refinanciacion.getEstado());
    }

    @Test
    void deberiaRevertirElPagoDesdeLaCuotaMasRecienteAfectada() {
        Refinanciacion refinanciacion = crearRefinanciacion(
                new BigDecimal("120000.00"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal("24.0000"),
                3
        );
        refinanciacion.generarCuotas();
        refinanciacion.registrarPago(new BigDecimal("50000.00"));

        refinanciacion.revertirPago(new BigDecimal("20000.00"));

        assertEquals(new BigDecimal("90000.00"), refinanciacion.getSaldoPlan());
        assertEquals(new BigDecimal("10000.00"), refinanciacion.getCuotas().get(0).getSaldoPendiente());
        assertEquals(new BigDecimal("40000.00"), refinanciacion.getCuotas().get(1).getSaldoPendiente());
    }

    @Test
    void deberiaReactivarElPlanAlRevertirSuUltimoPago() {
        Refinanciacion refinanciacion = crearRefinanciacion(
                new BigDecimal("120000.00"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal("24.0000"),
                1
        );
        refinanciacion.generarCuotas();
        refinanciacion.registrarPago(new BigDecimal("120000.00"));

        refinanciacion.revertirPago(new BigDecimal("120000.00"));

        assertEquals(new BigDecimal("120000.00"), refinanciacion.getSaldoPlan());
        assertEquals(EstadoRefinanciacion.ACTIVA, refinanciacion.getEstado());
        assertEquals(new BigDecimal("120000.00"), refinanciacion.getCuotas().get(0).getSaldoPendiente());
    }

    @Test
    void deberiaRechazarPagoMayorAlSaldo() {
        Refinanciacion refinanciacion = crearRefinanciacion(
                new BigDecimal("120000.00"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal("24.0000"),
                3
        );
        refinanciacion.generarCuotas();

        assertThrows(
                IllegalArgumentException.class,
                () -> refinanciacion.registrarPago(new BigDecimal("120000.01"))
        );
        assertEquals(new BigDecimal("120000.00"), refinanciacion.getSaldoPlan());
    }

    @Test
    void deberiaRechazarTasaAnualNegativa() {
        assertThrows(
                IllegalArgumentException.class,
                () -> crearRefinanciacion(
                        new BigDecimal("120000.00"),
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        new BigDecimal("-1.0000"),
                        3
                )
        );
    }

    private Refinanciacion crearRefinanciacion(
            BigDecimal capital,
            BigDecimal interes,
            BigDecimal cargos,
            BigDecimal tasaAnual,
            int cantidadCuotas
    ) {
        return new Refinanciacion(
                crearObligacion(),
                crearMoneda(),
                LocalDate.of(2026, 9, 26),
                capital,
                interes,
                cargos,
                tasaAnual,
                cantidadCuotas
        );
    }

    private Moneda crearMoneda() {
        return new Moneda("ARS", "Peso Argentino", 2, TipoMoneda.FIAT);
    }

    private Obligacion crearObligacion() {
        Usuario usuario = new Usuario(
                "Ariel",
                "Milevecich",
                "ariel.refinanciacion." + System.nanoTime() + "@test.com",
                "hash"
        );
        PerfilFinanciero perfil = new PerfilFinanciero("Personal", usuario);
        InstitucionFinanciera banco = new InstitucionFinanciera(
                "Banco Santander",
                TipoInstitucionFinanciera.BANCO
        );
        Moneda moneda = crearMoneda();
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

        Movimiento movimiento = new Movimiento(
                tarjeta,
                categoria,
                TipoMovimiento.EGRESO,
                new BigDecimal("120000.00"),
                LocalDateTime.of(2026, 9, 10, 12, 0),
                "Compra con tarjeta",
                FormaPago.TARJETA_CREDITO
        );
        return new Obligacion(movimiento);
    }
}
