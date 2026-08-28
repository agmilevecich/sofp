package ar.com.agmilevecich.sofp.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OperacionFinancieraMovimientoActivoIntegridadTest {

    @Test
    void deberiaAceptarMovimientoDeActivoCompraEnOperacionCompra() {

        OperacionFinanciera operacion = crearCompra();
        MovimientoActivo movimiento = crearMovimientoActivo(
                TipoMovimientoActivo.COMPRA
        );

        operacion.agregarMovimientoActivo(movimiento);

        assertEquals(1, operacion.getMovimientosActivos().size());
        assertEquals(operacion, movimiento.getOperacionFinanciera());
    }

    @Test
    void deberiaRechazarMovimientoDeActivoVentaEnOperacionCompra() {

        OperacionFinanciera operacion = crearCompra();
        MovimientoActivo movimiento = crearMovimientoActivo(
                TipoMovimientoActivo.VENTA
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> operacion.agregarMovimientoActivo(movimiento)
        );
    }

    @Test
    void deberiaAceptarMovimientoDeActivoVentaEnOperacionVenta() {

        OperacionFinanciera operacion = crearVenta();
        MovimientoActivo movimiento = crearMovimientoActivo(
                TipoMovimientoActivo.VENTA
        );

        operacion.agregarMovimientoActivo(movimiento);

        assertEquals(1, operacion.getMovimientosActivos().size());
        assertEquals(operacion, movimiento.getOperacionFinanciera());
    }

    @Test
    void deberiaRechazarMovimientoDeActivoCompraEnOperacionVenta() {

        OperacionFinanciera operacion = crearVenta();
        MovimientoActivo movimiento = crearMovimientoActivo(
                TipoMovimientoActivo.COMPRA
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> operacion.agregarMovimientoActivo(movimiento)
        );
    }

    private OperacionFinanciera crearCompra() {
        Cuenta cuentaOrigen = crearCuenta("Cuenta origen");

        return new OperacionFinanciera(
                cuentaOrigen,
                null,
                new BigDecimal("12500.00"),
                TipoOperacionFinanciera.COMPRA
        );
    }

    private OperacionFinanciera crearVenta() {
        Cuenta cuentaDestino = crearCuenta("Cuenta destino");

        return new OperacionFinanciera(
                null,
                cuentaDestino,
                new BigDecimal("13000.00"),
                TipoOperacionFinanciera.VENTA
        );
    }

    private MovimientoActivo crearMovimientoActivo(
            TipoMovimientoActivo tipoMovimiento) {

        Moneda moneda = new Moneda(
                "ARS",
                "Peso Argentino",
                2,
                TipoMoneda.FIAT
        );

        Activo activo = new Activo(
                "Activo de prueba",
                "TEST",
                moneda
        );

        return new MovimientoActivo(
                activo,
                tipoMovimiento,
                new BigDecimal("10.00"),
                new BigDecimal("1250.00")
        );
    }

    private Cuenta crearCuenta(String nombre) {

        Usuario usuario = new Usuario(
                "Ariel",
                "Milevecich",
                "ariel.operacion.integridad."
                        + System.nanoTime()
                        + "@test.com",
                "hash"
        );

        PerfilFinanciero perfil = new PerfilFinanciero(
                "Personal",
                usuario
        );

        InstitucionFinanciera banco = new InstitucionFinanciera(
                "Banco de prueba",
                TipoInstitucionFinanciera.BANCO
        );

        Moneda moneda = new Moneda(
                "ARS",
                "Peso Argentino",
                2,
                TipoMoneda.FIAT
        );

        return new Cuenta(
                nombre,
                TipoCuenta.CAJA_AHORRO,
                perfil,
                banco,
                moneda
        );
    }
}
