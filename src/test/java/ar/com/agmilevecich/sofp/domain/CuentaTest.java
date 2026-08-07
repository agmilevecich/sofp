package ar.com.agmilevecich.sofp.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CuentaTest {

    @Test
    void deberiaCrearUnaCuentaCorrectamente() {

        Usuario usuario = new Usuario(
                "Ariel",
                "Usuario",
                "ariel@test.com",
                "hash"
        );

        PerfilFinanciero perfil = new PerfilFinanciero(
                "Personal",
                usuario
        );

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Banco Santander",
                        TipoInstitucionFinanciera.BANCO
                );

        Moneda moneda =
                new Moneda(
                        "ARS",
                        "Peso Argentino",
                        2,
                        TipoMoneda.FIAT
                );

        Cuenta cuenta =
                new Cuenta(
                        "Caja de Ahorro",
                        TipoCuenta.CAJA_AHORRO,
                        perfil,
                        institucion,
                        moneda
                );

        assertEquals("Caja de Ahorro", cuenta.getNombre());
        assertEquals(TipoCuenta.CAJA_AHORRO, cuenta.getTipoCuenta());
        assertEquals(moneda, cuenta.getMoneda());
        assertEquals(institucion, cuenta.getInstitucionFinanciera());
        assertEquals(perfil, cuenta.getPerfilFinanciero());
        assertTrue(cuenta.isActiva());
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

        Moneda usd =
                new Moneda(
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

        InstitucionFinanciera uala =
                new InstitucionFinanciera(
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

        assertEquals(
                TipoCuenta.BILLETERA_VIRTUAL,
                cuenta.getTipoCuenta()
        );
    }

    @Test
    void deberiaActivarYDesactivarCuenta() {

        Cuenta cuenta = crearCuenta();

        cuenta.desactivar();

        assertFalse(cuenta.isActiva());

        cuenta.activar();

        assertTrue(cuenta.isActiva());
    }

    private Cuenta crearCuenta() {

        Usuario usuario = new Usuario(
                "Ariel",
                "Usuario",
                "ariel@test.com",
                "hash"
        );

        PerfilFinanciero perfil =
                new PerfilFinanciero(
                        "Personal",
                        usuario
                );

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Banco Santander",
                        TipoInstitucionFinanciera.BANCO
                );

        Moneda moneda =
                new Moneda(
                        "ARS",
                        "Peso Argentino",
                        2,
                        TipoMoneda.FIAT
                );

        return new Cuenta(
                "Caja de Ahorro",
                TipoCuenta.CAJA_AHORRO,
                perfil,
                institucion,
                moneda
        );
    }
}
