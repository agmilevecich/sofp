package ar.com.agmilevecich.sofp.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;


class OperacionFinancieraTest {

    @Test
    void deberiaCrearOperacionFinancieraDeTransferencia() {

        Usuario usuario = new Usuario(
                "Ariel",
                "Milevecich",
                "ariel.operacion@test.com",
                "hash"
        );

        PerfilFinanciero perfil =
                new PerfilFinanciero(
                        "Personal",
                        usuario
                );

        InstitucionFinanciera banco =
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

        Cuenta cuentaOrigen =
                new Cuenta(
                        "Caja de Ahorro",
                        TipoCuenta.CAJA_AHORRO,
                        perfil,
                        banco,
                        moneda
                );

        Cuenta cuentaDestino =
                new Cuenta(
                        "Cuenta Corriente",
                        TipoCuenta.CUENTA_CORRIENTE,
                        perfil,
                        banco,
                        moneda
                );

        OperacionFinanciera operacion =
                new OperacionFinanciera(
                        cuentaOrigen,
                        cuentaDestino,
                        new BigDecimal("100000.00")
                );

        assertEquals(
                cuentaOrigen,
                operacion.getCuentaOrigen()
        );

        assertEquals(
                cuentaDestino,
                operacion.getCuentaDestino()
        );

        assertEquals(
                new BigDecimal("100000.00"),
                operacion.getImporte()
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoLaCuentaOrigenEsNula() {

        OperacionFinanciera operacion = crearOperacionFinanciera();

        assertThrows(
                NullPointerException.class,
                () -> new OperacionFinanciera(
                        null,
                        operacion.getCuentaDestino(),
                        new BigDecimal("100000.00")
                )
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoLaCuentaDestinoEsNula() {

        OperacionFinanciera operacion = crearOperacionFinanciera();

        assertThrows(
                NullPointerException.class,
                () -> new OperacionFinanciera(
                        operacion.getCuentaOrigen(),
                        null,
                        new BigDecimal("100000.00")
                )
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoElImporteEsNulo() {

        OperacionFinanciera operacion = crearOperacionFinanciera();

        assertThrows(
                IllegalArgumentException.class,
                () -> new OperacionFinanciera(
                        operacion.getCuentaOrigen(),
                        operacion.getCuentaDestino(),
                        null
                )
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoElImporteEsCero() {

        OperacionFinanciera operacion = crearOperacionFinanciera();

        assertThrows(
                IllegalArgumentException.class,
                () -> new OperacionFinanciera(
                        operacion.getCuentaOrigen(),
                        operacion.getCuentaDestino(),
                        BigDecimal.ZERO
                )
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoElImporteEsNegativo() {

        OperacionFinanciera operacion = crearOperacionFinanciera();

        assertThrows(
                IllegalArgumentException.class,
                () -> new OperacionFinanciera(
                        operacion.getCuentaOrigen(),
                        operacion.getCuentaDestino(),
                        new BigDecimal("-100.00")
                )
        );
    }

    private OperacionFinanciera crearOperacionFinanciera() {

        Usuario usuario = new Usuario(
                "Ariel",
                "Milevecich",
                "ariel.operacion.helper@test.com",
                "hash"
        );

        PerfilFinanciero perfil =
                new PerfilFinanciero(
                        "Personal",
                        usuario
                );

        InstitucionFinanciera banco =
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

        Cuenta cuentaOrigen =
                new Cuenta(
                        "Caja de Ahorro",
                        TipoCuenta.CAJA_AHORRO,
                        perfil,
                        banco,
                        moneda
                );

        Cuenta cuentaDestino =
                new Cuenta(
                        "Cuenta Corriente",
                        TipoCuenta.CUENTA_CORRIENTE,
                        perfil,
                        banco,
                        moneda
                );

        return new OperacionFinanciera(
                cuentaOrigen,
                cuentaDestino,
                new BigDecimal("100000.00")
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoLaCuentaOrigenYDestinoSonLaMisma() {

        OperacionFinanciera operacion = crearOperacionFinanciera();

        assertThrows(
                IllegalArgumentException.class,
                () -> new OperacionFinanciera(
                        operacion.getCuentaOrigen(),
                        operacion.getCuentaOrigen(),
                        new BigDecimal("100000.00")
                )
        );
    }
}
