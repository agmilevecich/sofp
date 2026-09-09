package ar.com.agmilevecich.sofp.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CuentaTest {

    @Test
    void deberiaCrearUnaCuentaCorrectamente() {
        Cuenta cuenta = crearCuenta();

        assertEquals("Caja de Ahorro", cuenta.getNombre());
        assertEquals(TipoCuenta.CAJA_AHORRO, cuenta.getTipoCuenta());
        assertEquals(crearMoneda(), cuenta.getMoneda());
        assertEquals(crearInstitucion(), cuenta.getInstitucionFinanciera());
        assertNotNull(cuenta.getPerfilFinanciero());
        assertTrue(cuenta.isActiva());
    }

    @Test
    void deberiaCrearUnaTarjetaDeCreditoCorrectamente() {
        Cuenta tarjeta = crearTarjeta();

        assertEquals(TipoCuenta.TARJETA_CREDITO, tarjeta.getTipoCuenta());
        assertEquals(new BigDecimal("500000.00"), tarjeta.getLimiteCredito());
        assertEquals(10, tarjeta.getDiaCierre());
        assertEquals(25, tarjeta.getDiaVencimiento());
        assertTrue(tarjeta.isActiva());
    }

    @Test
    void noDeberiaPermitirLimiteCreditoNulo() {
        assertThrows(
                NullPointerException.class,
                () -> crearTarjetaCon(null, 10, 25)
        );
    }

    @Test
    void noDeberiaPermitirLimiteCreditoNoPositivo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> crearTarjetaCon(BigDecimal.ZERO, 10, 25)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> crearTarjetaCon(new BigDecimal("-1"), 10, 25)
        );
    }

    @Test
    void noDeberiaPermitirDiasNulos() {
        assertThrows(
                NullPointerException.class,
                () -> crearTarjetaCon(new BigDecimal("500000"), null, 25)
        );

        assertThrows(
                NullPointerException.class,
                () -> crearTarjetaCon(new BigDecimal("500000"), 10, null)
        );
    }

    @Test
    void noDeberiaPermitirDiasFueraDeRango() {
        assertThrows(
                IllegalArgumentException.class,
                () -> crearTarjetaCon(new BigDecimal("500000"), 0, 25)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> crearTarjetaCon(new BigDecimal("500000"), 10, 32)
        );
    }

    @Test
    void deberiaConfigurarDatosDeCredito() {
        Cuenta cuenta = crearCuenta();

        cuenta.configurarDatosCredito(
                new BigDecimal("750000"),
                15,
                28
        );

        assertEquals(new BigDecimal("750000"), cuenta.getLimiteCredito());
        assertEquals(15, cuenta.getDiaCierre());
        assertEquals(28, cuenta.getDiaVencimiento());
    }

    @Test
    void deberiaRenombrarCuenta() {
        Cuenta cuenta = crearCuenta();
        cuenta.renombrar("Cuenta Principal");
        assertEquals("Cuenta Principal", cuenta.getNombre());
    }

    @Test
    void deberiaCambiarMoneda() {
        Cuenta cuenta = crearCuenta();
        Moneda usd = new Moneda(
                "USD",
                "Dólar Estadounidense",
                2,
                TipoMoneda.FIAT
        );

        cuenta.cambiarMoneda(usd);

        assertEquals(usd, cuenta.getMoneda());
    }

    @Test
    void deberiaCambiarInstitucion() {
        Cuenta cuenta = crearCuenta();
        InstitucionFinanciera uala = new InstitucionFinanciera(
                "Ualá",
                TipoInstitucionFinanciera.BILLETERA_VIRTUAL
        );

        cuenta.cambiarInstitucionFinanciera(uala);

        assertEquals(uala, cuenta.getInstitucionFinanciera());
    }

    @Test
    void deberiaCambiarTipoCuenta() {
        Cuenta cuenta = crearCuenta();

        cuenta.cambiarTipoCuenta(TipoCuenta.BILLETERA_VIRTUAL);

        assertEquals(TipoCuenta.BILLETERA_VIRTUAL, cuenta.getTipoCuenta());
    }

    @Test
    void deberiaActivarYDesactivarCuenta() {
        Cuenta cuenta = crearCuenta();

        cuenta.desactivar();
        assertFalse(cuenta.isActiva());

        cuenta.activar();
        assertTrue(cuenta.isActiva());
    }

    private Cuenta crearTarjeta() {
        return crearTarjetaCon(new BigDecimal("500000.00"), 10, 25);
    }

    private Cuenta crearTarjetaCon(
            BigDecimal limiteCredito,
            Integer diaCierre,
            Integer diaVencimiento) {

        return new Cuenta(
                "Visa Santander",
                crearPerfil(),
                crearInstitucion(),
                crearMoneda(),
                limiteCredito,
                diaCierre,
                diaVencimiento
        );
    }

    private Cuenta crearCuenta() {
        return new Cuenta(
                "Caja de Ahorro",
                TipoCuenta.CAJA_AHORRO,
                crearPerfil(),
                crearInstitucion(),
                crearMoneda()
        );
    }

    private PerfilFinanciero crearPerfil() {
        Usuario usuario = new Usuario(
                "Ariel",
                "Usuario",
                "ariel@test.com",
                "hash"
        );

        return new PerfilFinanciero("Personal", usuario);
    }

    private InstitucionFinanciera crearInstitucion() {
        return new InstitucionFinanciera(
                "Banco Santander",
                TipoInstitucionFinanciera.BANCO
        );
    }

    private Moneda crearMoneda() {
        return new Moneda(
                "ARS",
                "Peso Argentino",
                2,
                TipoMoneda.FIAT
        );
    }
}
