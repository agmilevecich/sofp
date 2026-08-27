package ar.com.agmilevecich.sofp.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        assertEquals(
                TipoOperacionFinanciera.TRANSFERENCIA,
                operacion.getTipoOperacion()
        );
    }

    @Test
    void deberiaCrearOperacionConTipoEspecificado() {

        OperacionFinanciera operacion =
                crearOperacionFinanciera(
                        TipoOperacionFinanciera.COMPRA
                );

        assertEquals(
                TipoOperacionFinanciera.COMPRA,
                operacion.getTipoOperacion()
        );
    }

    @Test
    void deberiaCrearOperacionDeVentaConTipoEspecificado() {

        OperacionFinanciera operacion =
                crearOperacionFinanciera(
                        TipoOperacionFinanciera.VENTA
                );

        assertEquals(
                TipoOperacionFinanciera.VENTA,
                operacion.getTipoOperacion()
        );
    }

    @Test
    void deberiaRechazarTipoOperacionNulo() {

        assertThrows(
                NullPointerException.class,
                () -> crearOperacionFinanciera(null)
        );
    }

    @Test
    void deberiaCrearCompraSinCuentaDestino() {

        OperacionFinanciera operacion =
                crearOperacionFinancieraSinCuentaDestino(
                        TipoOperacionFinanciera.COMPRA
                );

        assertEquals(
                TipoOperacionFinanciera.COMPRA,
                operacion.getTipoOperacion()
        );

        assertTrue(
                operacion.getCuentaOrigen() != null
        );

        assertEquals(
                null,
                operacion.getCuentaDestino()
        );
    }

    @Test
    void deberiaCrearVentaSinCuentaOrigen() {

        OperacionFinanciera operacion =
                crearOperacionFinancieraSinCuentaOrigen(
                        TipoOperacionFinanciera.VENTA
                );

        assertEquals(
                TipoOperacionFinanciera.VENTA,
                operacion.getTipoOperacion()
        );

        assertEquals(
                null,
                operacion.getCuentaOrigen()
        );

        assertTrue(
                operacion.getCuentaDestino() != null
        );
    }

    @Test
    void deberiaCrearOperacionSinMovimientosInicialmente() {

        OperacionFinanciera operacion =
                crearOperacionFinanciera();

        assertTrue(
                operacion.getMovimientos().isEmpty()
        );
    }

    @Test
    void deberiaCrearOperacionSinMovimientosDeActivoInicialmente() {

        OperacionFinanciera operacion =
                crearOperacionFinanciera();

        assertTrue(
                operacion.getMovimientosActivos().isEmpty()
        );
    }

    @Test
    void deberiaAgregarMovimientoAOperacionFinanciera() {

        OperacionFinanciera operacion =
                crearOperacionFinanciera();

        Movimiento movimiento =
                crearMovimiento(
                        operacion.getCuentaOrigen(),
                        "Transferencia enviada"
                );

        operacion.agregarMovimiento(movimiento);

        assertEquals(
                1,
                operacion.getMovimientos().size()
        );

        assertEquals(
                movimiento,
                operacion.getMovimientos().get(0)
        );

        assertEquals(
                operacion,
                movimiento.getOperacionFinanciera()
        );
    }

    @Test
    void deberiaAgregarDosMovimientosAOperacionFinanciera() {

        OperacionFinanciera operacion =
                crearOperacionFinanciera();

        Movimiento egreso =
                crearMovimiento(
                        operacion.getCuentaOrigen(),
                        "Transferencia enviada",
                        TipoMovimiento.EGRESO
                );

        Movimiento ingreso =
                crearMovimiento(
                        operacion.getCuentaDestino(),
                        "Transferencia recibida",
                        TipoMovimiento.INGRESO
                );

        operacion.agregarMovimiento(egreso);
        operacion.agregarMovimiento(ingreso);

        List<Movimiento> movimientos =
                operacion.getMovimientos();

        assertEquals(
                2,
                movimientos.size()
        );

        assertEquals(
                egreso,
                movimientos.get(0)
        );

        assertEquals(
                ingreso,
                movimientos.get(1)
        );
    }

    @Test
    void deberiaRechazarTercerMovimiento() {

        OperacionFinanciera operacion =
                crearOperacionFinanciera();

        Movimiento primero =
                crearMovimiento(
                        operacion.getCuentaOrigen(),
                        "Transferencia enviada",
                        TipoMovimiento.EGRESO
                );

        Movimiento segundo =
                crearMovimiento(
                        operacion.getCuentaDestino(),
                        "Transferencia recibida",
                        TipoMovimiento.INGRESO
                );

        Movimiento tercero =
                crearMovimiento(
                        operacion.getCuentaOrigen(),
                        "Movimiento adicional",
                        TipoMovimiento.EGRESO
                );

        operacion.agregarMovimiento(primero);
        operacion.agregarMovimiento(segundo);

        assertThrows(
                IllegalStateException.class,
                () -> operacion.agregarMovimiento(tercero)
        );

        assertEquals(
                2,
                operacion.getMovimientos().size()
        );
    }

    @Test
    void deberiaRechazarMovimientoRepetido() {

        OperacionFinanciera operacion =
                crearOperacionFinanciera();

        Movimiento movimiento =
                crearMovimiento(
                        operacion.getCuentaOrigen(),
                        "Transferencia enviada"
                );

        operacion.agregarMovimiento(movimiento);

        assertThrows(
                IllegalArgumentException.class,
                () -> operacion.agregarMovimiento(movimiento)
        );

        assertEquals(
                1,
                operacion.getMovimientos().size()
        );
    }

    @Test
    void deberiaRechazarMovimientoYaAsociadoAOtraOperacion() {

        OperacionFinanciera primeraOperacion =
                crearOperacionFinanciera();

        Cuenta cuentaDestinoAlternativa =
                new Cuenta(
                        "Cuenta Destino Alternativa",
                        TipoCuenta.CUENTA_CORRIENTE,
                        primeraOperacion.getCuentaOrigen().getPerfilFinanciero(),
                        primeraOperacion.getCuentaOrigen().getInstitucionFinanciera(),
                        primeraOperacion.getCuentaOrigen().getMoneda()
                );

        OperacionFinanciera segundaOperacion =
                new OperacionFinanciera(
                        primeraOperacion.getCuentaOrigen(),
                        cuentaDestinoAlternativa,
                        new BigDecimal("100000.00")
                );

        Movimiento movimiento =
                crearMovimiento(
                        primeraOperacion.getCuentaOrigen(),
                        "Transferencia enviada"
                );

        primeraOperacion.agregarMovimiento(
                movimiento
        );

        assertThrows(
                IllegalStateException.class,
                () -> segundaOperacion.agregarMovimiento(
                        movimiento
                )
        );

        assertEquals(
                primeraOperacion,
                movimiento.getOperacionFinanciera()
        );

        assertEquals(
                1,
                primeraOperacion.getMovimientos().size()
        );

        assertEquals(
                0,
                segundaOperacion.getMovimientos().size()
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoLaCuentaOrigenEsNula() {

        OperacionFinanciera operacion =
                crearOperacionFinanciera();

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

        OperacionFinanciera operacion =
                crearOperacionFinanciera();

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

        OperacionFinanciera operacion =
                crearOperacionFinanciera();

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

        OperacionFinanciera operacion =
                crearOperacionFinanciera();

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

        OperacionFinanciera operacion =
                crearOperacionFinanciera();

        assertThrows(
                IllegalArgumentException.class,
                () -> new OperacionFinanciera(
                        operacion.getCuentaOrigen(),
                        operacion.getCuentaDestino(),
                        new BigDecimal("-100.00")
                )
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoLaCuentaOrigenYDestinoSonLaMisma() {

        OperacionFinanciera operacion =
                crearOperacionFinanciera();

        assertThrows(
                IllegalArgumentException.class,
                () -> new OperacionFinanciera(
                        operacion.getCuentaOrigen(),
                        operacion.getCuentaOrigen(),
                        new BigDecimal("100000.00")
                )
        );
    }

    private OperacionFinanciera crearOperacionFinanciera() {
        return crearOperacionFinanciera(
                TipoOperacionFinanciera.TRANSFERENCIA
        );
    }

    private OperacionFinanciera crearOperacionFinanciera(
            TipoOperacionFinanciera tipoOperacion) {

        Usuario usuario = new Usuario(
                "Ariel",
                "Milevecich",
                "ariel.operacion.helper."
                        + System.nanoTime()
                        + "@test.com",
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
                new BigDecimal("100000.00"),
                tipoOperacion
        );
    }

    private OperacionFinanciera crearOperacionFinancieraSinCuentaDestino(
            TipoOperacionFinanciera tipoOperacion) {

        Cuenta cuentaOrigen = crearCuenta("Caja de Ahorro");

        return new OperacionFinanciera(
                cuentaOrigen,
                null,
                new BigDecimal("100000.00"),
                tipoOperacion
        );
    }

    private OperacionFinanciera crearOperacionFinancieraSinCuentaOrigen(
            TipoOperacionFinanciera tipoOperacion) {

        Cuenta cuentaDestino = crearCuenta("Cuenta Corriente");

        return new OperacionFinanciera(
                null,
                cuentaDestino,
                new BigDecimal("100000.00"),
                tipoOperacion
        );
    }

    private Cuenta crearCuenta(String nombre) {

        Usuario usuario = new Usuario(
                "Ariel",
                "Milevecich",
                "ariel.operacion.cuenta."
                        + System.nanoTime()
                        + "@test.com",
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

        return new Cuenta(
                nombre,
                TipoCuenta.CAJA_AHORRO,
                perfil,
                banco,
                moneda
        );
    }

    private Movimiento crearMovimiento(
            Cuenta cuenta,
            String descripcion) {
        return crearMovimiento(
                cuenta,
                descripcion,
                TipoMovimiento.EGRESO
        );
    }

    private Movimiento crearMovimiento(
            Cuenta cuenta,
            String descripcion,
            TipoMovimiento tipo) {

        Categoria categoria =
                new Categoria(
                        "Transferencias",
                        cuenta.getPerfilFinanciero()
                );

        return new Movimiento(
                cuenta,
                categoria,
                tipo,
                new BigDecimal("100000.00"),
                LocalDateTime.of(
                        2026,
                        8,
                        23,
                        10,
                        0
                ),
                descripcion
        );
    }

    private MovimientoActivo crearMovimientoActivo() {

        Moneda moneda =
                new Moneda(
                        "ARS",
                        "Peso Argentino",
                        2,
                        TipoMoneda.FIAT
                );

        Activo activo =
                new Activo(
                        "Bono GD30",
                        "GD30",
                        moneda
                );

        return new MovimientoActivo(
                activo,
                TipoMovimientoActivo.COMPRA,
                new BigDecimal("100"),
                new BigDecimal("1000.00")
        );
    }
}
