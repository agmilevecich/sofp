package ar.com.agmilevecich.sofp.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OperacionFinancieraIntegridadTest {

    @Test
    void deberiaRechazarMovimientoDeCuentaAjenaALaOperacion() {

        Datos datos = crearDatos();

        Movimiento movimiento = new Movimiento(
                datos.otraCuenta,
                datos.categoriaOtraCuenta,
                TipoMovimiento.EGRESO,
                new BigDecimal("100.00"),
                fecha(),
                "Movimiento inválido"
        );

        OperacionFinanciera operacion = datos.operacion;

        assertThrows(
                IllegalArgumentException.class,
                () -> operacion.agregarMovimiento(movimiento)
        );

        assertEquals(0, operacion.getMovimientos().size());
        assertEquals(null, movimiento.getOperacionFinanciera());
    }

    @Test
    void deberiaRechazarPrimerMovimientoQueNoSeaEgreso() {

        Datos datos = crearDatos();

        Movimiento movimiento = new Movimiento(
                datos.cuentaOrigen,
                datos.categoriaOrigen,
                TipoMovimiento.INGRESO,
                new BigDecimal("100.00"),
                fecha(),
                "Ingreso inválido"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> datos.operacion.agregarMovimiento(movimiento)
        );

        assertEquals(0, datos.operacion.getMovimientos().size());
    }

    @Test
    void deberiaRechazarSegundoMovimientoQueNoSeaDeLaCuentaDestino() {

        Datos datos = crearDatos();

        Movimiento egreso = new Movimiento(
                datos.cuentaOrigen,
                datos.categoriaOrigen,
                TipoMovimiento.EGRESO,
                new BigDecimal("100.00"),
                fecha(),
                "Egreso válido"
        );

        Movimiento segundo = new Movimiento(
                datos.otraCuenta,
                datos.categoriaOtraCuenta,
                TipoMovimiento.INGRESO,
                new BigDecimal("100.00"),
                fecha(),
                "Ingreso inválido"
        );

        datos.operacion.agregarMovimiento(egreso);

        assertThrows(
                IllegalArgumentException.class,
                () -> datos.operacion.agregarMovimiento(segundo)
        );

        assertEquals(1, datos.operacion.getMovimientos().size());
        assertEquals(egreso, datos.operacion.getMovimientos().get(0));
        assertEquals(null, segundo.getOperacionFinanciera());
    }

    @Test
    void deberiaRechazarSegundoMovimientoQueNoSeaIngreso() {

        Datos datos = crearDatos();

        Movimiento egreso = new Movimiento(
                datos.cuentaOrigen,
                datos.categoriaOrigen,
                TipoMovimiento.EGRESO,
                new BigDecimal("100.00"),
                fecha(),
                "Egreso válido"
        );

        Movimiento segundo = new Movimiento(
                datos.cuentaDestino,
                datos.categoriaDestino,
                TipoMovimiento.EGRESO,
                new BigDecimal("100.00"),
                fecha(),
                "Segundo egreso inválido"
        );

        datos.operacion.agregarMovimiento(egreso);

        assertThrows(
                IllegalArgumentException.class,
                () -> datos.operacion.agregarMovimiento(segundo)
        );

        assertEquals(1, datos.operacion.getMovimientos().size());
    }

    private Datos crearDatos() {

        Usuario usuario = new Usuario(
                "Ariel",
                "Milevecich",
                "ariel.integridad."
                        + System.nanoTime()
                        + "@test.com",
                "hash"
        );

        PerfilFinanciero perfil = new PerfilFinanciero(
                "Personal",
                usuario
        );

        InstitucionFinanciera banco = new InstitucionFinanciera(
                "Banco Santander",
                TipoInstitucionFinanciera.BANCO
        );

        Moneda moneda = new Moneda(
                "ARS",
                "Peso Argentino",
                2,
                TipoMoneda.FIAT
        );

        Cuenta cuentaOrigen = new Cuenta(
                "Caja de Ahorro",
                TipoCuenta.CAJA_AHORRO,
                perfil,
                banco,
                moneda
        );

        Cuenta cuentaDestino = new Cuenta(
                "Cuenta Corriente",
                TipoCuenta.CUENTA_CORRIENTE,
                perfil,
                banco,
                moneda
        );

        Cuenta otraCuenta = new Cuenta(
                "Otra Cuenta",
                TipoCuenta.CAJA_AHORRO,
                perfil,
                banco,
                moneda
        );

        Categoria categoriaOrigen = new Categoria(
                "Transferencias origen",
                perfil
        );

        Categoria categoriaDestino = new Categoria(
                "Transferencias destino",
                perfil
        );

        Categoria categoriaOtraCuenta = new Categoria(
                "Otra categoría",
                perfil
        );

        OperacionFinanciera operacion = new OperacionFinanciera(
                cuentaOrigen,
                cuentaDestino,
                new BigDecimal("100.00")
        );

        Datos datos = new Datos();
        datos.operacion = operacion;
        datos.cuentaOrigen = cuentaOrigen;
        datos.cuentaDestino = cuentaDestino;
        datos.otraCuenta = otraCuenta;
        datos.categoriaOrigen = categoriaOrigen;
        datos.categoriaDestino = categoriaDestino;
        datos.categoriaOtraCuenta = categoriaOtraCuenta;
        return datos;
    }

    private LocalDateTime fecha() {
        return LocalDateTime.of(2026, 8, 26, 10, 0);
    }

    private static class Datos {
        private OperacionFinanciera operacion;
        private Cuenta cuentaOrigen;
        private Cuenta cuentaDestino;
        private Cuenta otraCuenta;
        private Categoria categoriaOrigen;
        private Categoria categoriaDestino;
        private Categoria categoriaOtraCuenta;
    }
}
