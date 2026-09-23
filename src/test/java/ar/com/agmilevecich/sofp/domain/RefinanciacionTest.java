package ar.com.agmilevecich.sofp.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RefinanciacionTest {

    @Test
    void deberiaGenerarCuotasConSistemaFrancesYRedondeoFinal() {
        Refinanciacion refinanciacion = crearRefinanciacion(
                new BigDecimal("120000.00"),
                new BigDecimal("6000.00"),
                new BigDecimal("1000.00"),
                new BigDecimal("24.0000"),
                3
        );

        refinanciacion.generarCuotas();

        assertEquals(new BigDecimal("127000.00"), refinanciacion.getTotalPlan());
        assertEquals(new BigDecimal("132113.53"), refinanciacion.getSaldoPlan());

        CuotaRefinanciacion primera = refinanciacion.getCuotas().get(0);
        CuotaRefinanciacion segunda = refinanciacion.getCuotas().get(1);
        CuotaRefinanciacion tercera = refinanciacion.getCuotas().get(2);

        assertEquals(new BigDecimal("44037.84"), primera.getImporteOriginal());
        assertEquals(new BigDecimal("2540.00"), primera.getInteres());
        assertEquals(new BigDecimal("41497.84"), primera.getCapitalAmortizado());

        assertEquals(new BigDecimal("44037.84"), segunda.getImporteOriginal());
        assertEquals(new BigDecimal("1710.04"), segunda.getInteres());
        assertEquals(new BigDecimal("42327.80"), segunda.getCapitalAmortizado());

        assertEquals(new BigDecimal("44037.85"), tercera.getImporteOriginal());
        assertEquals(new BigDecimal("863.49"), tercera.getInteres());
        assertEquals(new BigDecimal("43174.36"), tercera.getCapitalAmortizado());

        assertEquals(new BigDecimal("127000.00"),
                primera.getCapitalAmortizado()
                        .add(segunda.getCapitalAmortizado())
                        .add(tercera.getCapitalAmortizado()));

        assertEquals(LocalDate.of(2026, 10, 26), primera.getFechaVencimiento());
        assertEquals(LocalDate.of(2026, 12, 26), tercera.getFechaVencimiento());
    }

    @Test
    void deberiaGenerarCuotasSinInteresCuandoLaTnaEsCero() {
        Refinanciacion refinanciacion = crearRefinanciacion(
                new BigDecimal("120000.00"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                3
        );

        refinanciacion.generarCuotas();

        assertEquals(new BigDecimal("40000.00"), refinanciacion.getCuotas().get(0).getImporteOriginal());
        assertEquals(new BigDecimal("0.00"), refinanciacion.getCuotas().get(0).getInteres());
        assertEquals(new BigDecimal("40000.00"), refinanciacion.getCuotas().get(0).getCapitalAmortizado());
        assertEquals(new BigDecimal("40000.00"), refinanciacion.getCuotas().get(2).getImporteOriginal());
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
                BigDecimal.ZERO,
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

        refinanciacion.registrarPago(new BigDecimal("124831.68"));

        assertEquals(new BigDecimal("0.00"), refinanciacion.getSaldoPlan());
        assertEquals(EstadoRefinanciacion.CANCELADA, refinanciacion.getEstado());
        assertTrue(refinanciacion.getCuotas().stream()
                .allMatch(cuota -> cuota.getSaldoPendiente().signum() == 0));
    }

    @Test
    void deberiaAplicarPagoParcialYReflejarElSaldoContractualPendiente() {
        Refinanciacion refinanciacion = crearRefinanciacion(
                new BigDecimal("120000.00"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal("24.0000"),
                3
        );
        refinanciacion.generarCuotas();

        refinanciacion.registrarPago(new BigDecimal("50000.00"));

        assertEquals(new BigDecimal("74831.68"), refinanciacion.getSaldoPlan());
        assertEquals(new BigDecimal("0.00"), refinanciacion.getCuotas().get(0).getSaldoPendiente());
        assertEquals(new BigDecimal("33221.12"), refinanciacion.getCuotas().get(1).getSaldoPendiente());
        assertEquals(new BigDecimal("41610.56"), refinanciacion.getCuotas().get(2).getSaldoPendiente());
        assertEquals(EstadoRefinanciacion.ACTIVA, refinanciacion.getEstado());
    }

    @Test
    void deberiaRevertirElPagoDesdeLaCuotaMasRecienteAfectada() {
        Refinanciacion refinanciacion = crearRefinanciacion(
                new BigDecimal("120000.00"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
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
                BigDecimal.ZERO,
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
                () -> refinanciacion.registrarPago(new BigDecimal("124831.69"))
        );
        assertEquals(new BigDecimal("132113.53"), refinanciacion.getSaldoPlan());
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

    @Test
    void noDeberiaGenerarCuotasSinTna() {
        Refinanciacion refinanciacion = crearRefinanciacion(
                new BigDecimal("120000.00"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                null,
                3
        );

        assertThrows(IllegalStateException.class, refinanciacion::generarCuotas);
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
