package ar.com.agmilevecich.sofp.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MovimientoTest {

    @Test
    void deberiaCrearMovimiento() {

        Usuario usuario = new Usuario(
                "Ariel",
                "Milevecich",
                "ariel@test.com",
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

        Cuenta cuenta = new Cuenta(
                "Caja de Ahorro",
                TipoCuenta.CAJA_AHORRO,
                perfil,
                banco,
                moneda
        );

        Categoria categoria = new Categoria(
                "Supermercado",
                perfil
        );

        Movimiento movimiento = new Movimiento(
                cuenta,
                categoria,
                TipoMovimiento.EGRESO,
                new BigDecimal("15000.50"),
                LocalDateTime.now(),
                "Compra Carrefour"
        );

        assertEquals(cuenta, movimiento.getCuenta());
        assertEquals(categoria, movimiento.getCategoria());
        assertEquals(TipoMovimiento.EGRESO, movimiento.getTipoMovimiento());
        assertNull(movimiento.getFormaPago());
        assertEquals(new BigDecimal("15000.50"), movimiento.getImporte());
        assertEquals("Compra Carrefour", movimiento.getDescripcion());
    }

    @Test
    void deberiaCrearMovimientoConFormaDePago() {
        Movimiento movimiento = crearMovimientoConFormaPago(FormaPago.EFECTIVO);

        assertEquals(FormaPago.EFECTIVO, movimiento.getFormaPago());
    }

    @Test
    void deberiaCambiarFormaDePago() {
        Movimiento movimiento = crearMovimientoConFormaPago(FormaPago.EFECTIVO);

        movimiento.cambiarFormaPago(FormaPago.TRANSFERENCIA);

        assertEquals(FormaPago.TRANSFERENCIA, movimiento.getFormaPago());
    }

    @Test
    void deberiaPermitirFormaDePagoNulaParaMovimientosLegados() {
        Movimiento movimiento = crearMovimientoConFormaPago(FormaPago.EFECTIVO);

        movimiento.cambiarFormaPago(null);

        assertNull(movimiento.getFormaPago());
    }

    @Test
    void deberiaCambiarDescripcion() {

        Movimiento movimiento = crearMovimiento();

        movimiento.cambiarDescripcion("Compra Coto");

        assertEquals("Compra Coto", movimiento.getDescripcion());
    }

    @Test
    void deberiaCambiarObservaciones() {

        Movimiento movimiento = crearMovimiento();

        movimiento.cambiarObservaciones("Pagado con tarjeta");

        assertEquals(
                "Pagado con tarjeta",
                movimiento.getObservaciones()
        );
    }

    @Test
    void deberiaCambiarCategoria() {

        Usuario usuario = new Usuario(
                "Ariel",
                "Milevecich",
                "ariel@test.com",
                "hash"
        );

        PerfilFinanciero perfil = new PerfilFinanciero(
                "Personal",
                usuario
        );

        Categoria nuevaCategoria = new Categoria(
                "Combustible",
                perfil
        );

        Movimiento movimiento = crearMovimiento();

        movimiento.cambiarCategoria(nuevaCategoria);

        assertEquals(
                nuevaCategoria,
                movimiento.getCategoria()
        );
    }

    private Movimiento crearMovimiento() {

        Usuario usuario = new Usuario(
                "Ariel",
                "Milevecich",
                "ariel@test.com",
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

        Cuenta cuenta = new Cuenta(
                "Caja de Ahorro",
                TipoCuenta.CAJA_AHORRO,
                perfil,
                banco,
                moneda
        );

        Categoria categoria = new Categoria(
                "Supermercado",
                perfil
        );

        return new Movimiento(
                cuenta,
                categoria,
                TipoMovimiento.EGRESO,
                new BigDecimal("15000.50"),
                LocalDateTime.now(),
                "Compra Carrefour"
        );
    }

    private Movimiento crearMovimientoConFormaPago(FormaPago formaPago) {
        Usuario usuario = new Usuario(
                "Ariel",
                "Milevecich",
                "ariel.forma.pago." + System.nanoTime(),
                "hash"
        );
        PerfilFinanciero perfil = new PerfilFinanciero("Personal", usuario);
        InstitucionFinanciera banco = new InstitucionFinanciera("Banco Santander", TipoInstitucionFinanciera.BANCO);
        Moneda moneda = new Moneda("ARS", "Peso Argentino", 2, TipoMoneda.FIAT);
        Cuenta cuenta = new Cuenta("Caja de Ahorro", TipoCuenta.CAJA_AHORRO, perfil, banco, moneda);
        Categoria categoria = new Categoria("Supermercado", perfil);

        return new Movimiento(
                cuenta,
                categoria,
                TipoMovimiento.EGRESO,
                new BigDecimal("100.00"),
                LocalDateTime.now(),
                "Compra",
                formaPago
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoLaCuentaEsNula() {

        Usuario usuario = new Usuario(
                "Ariel",
                "Milevecich",
                "ariel.cuenta.nula@test.com",
                "hash"
        );

        PerfilFinanciero perfil =
                new PerfilFinanciero(
                        "Personal",
                        usuario
                );

        Categoria categoria =
                new Categoria(
                        "Supermercado",
                        perfil
                );

        assertThrows(
                NullPointerException.class,
                () -> new Movimiento(
                        null,
                        categoria,
                        TipoMovimiento.EGRESO,
                        new BigDecimal("15000.00"),
                        LocalDateTime.now(),
                        "Compra"
                )
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoLaCategoriaEsNula() {

        Movimiento movimiento = crearMovimiento();

        assertThrows(
                NullPointerException.class,
                () -> new Movimiento(
                        movimiento.getCuenta(),
                        null,
                        TipoMovimiento.EGRESO,
                        new BigDecimal("15000.00"),
                        LocalDateTime.now(),
                        "Compra"
                )
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoElTipoMovimientoEsNulo() {

        Movimiento movimiento = crearMovimiento();

        assertThrows(
                NullPointerException.class,
                () -> new Movimiento(
                        movimiento.getCuenta(),
                        movimiento.getCategoria(),
                        null,
                        new BigDecimal("15000.00"),
                        LocalDateTime.now(),
                        "Compra"
                )
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoElImporteEsNulo() {

        Movimiento movimiento = crearMovimiento();

        assertThrows(
                IllegalArgumentException.class,
                () -> new Movimiento(
                        movimiento.getCuenta(),
                        movimiento.getCategoria(),
                        TipoMovimiento.EGRESO,
                        null,
                        LocalDateTime.now(),
                        "Compra"
                )
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoElImporteEsCero() {

        Movimiento movimiento = crearMovimiento();

        assertThrows(
                IllegalArgumentException.class,
                () -> new Movimiento(
                        movimiento.getCuenta(),
                        movimiento.getCategoria(),
                        TipoMovimiento.EGRESO,
                        BigDecimal.ZERO,
                        LocalDateTime.now(),
                        "Compra"
                )
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoElImporteEsNegativo() {

        Movimiento movimiento = crearMovimiento();

        assertThrows(
                IllegalArgumentException.class,
                () -> new Movimiento(
                        movimiento.getCuenta(),
                        movimiento.getCategoria(),
                        TipoMovimiento.EGRESO,
                        new BigDecimal("-100.00"),
                        LocalDateTime.now(),
                        "Compra"
                )
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoLaFechaHoraEsNula() {

        Movimiento movimiento = crearMovimiento();

        assertThrows(
                NullPointerException.class,
                () -> new Movimiento(
                        movimiento.getCuenta(),
                        movimiento.getCategoria(),
                        TipoMovimiento.EGRESO,
                        new BigDecimal("15000.00"),
                        null,
                        "Compra"
                )
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoLaDescripcionEsNula() {

        Movimiento movimiento = crearMovimiento();

        assertThrows(
                IllegalArgumentException.class,
                () -> new Movimiento(
                        movimiento.getCuenta(),
                        movimiento.getCategoria(),
                        TipoMovimiento.EGRESO,
                        new BigDecimal("15000.00"),
                        LocalDateTime.now(),
                        null
                )
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoLaDescripcionEsVacia() {

        Movimiento movimiento = crearMovimiento();

        assertThrows(
                IllegalArgumentException.class,
                () -> new Movimiento(
                        movimiento.getCuenta(),
                        movimiento.getCategoria(),
                        TipoMovimiento.EGRESO,
                        new BigDecimal("15000.00"),
                        LocalDateTime.now(),
                        ""
                )
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoLaDescripcionContieneSoloEspacios() {

        Movimiento movimiento = crearMovimiento();

        assertThrows(
                IllegalArgumentException.class,
                () -> new Movimiento(
                        movimiento.getCuenta(),
                        movimiento.getCategoria(),
                        TipoMovimiento.EGRESO,
                        new BigDecimal("15000.00"),
                        LocalDateTime.now(),
                        "   "
                )
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeModificaTipoMovimientoConValorNulo() {

        Movimiento movimiento = crearMovimiento();

        assertThrows(
                NullPointerException.class,
                () -> movimiento.modificarTipoMovimiento(null)
        );
    }

    @Test
    void deberiaModificarCorrectamenteElTipoMovimiento() {

        Movimiento movimiento = crearMovimiento();

        movimiento.modificarTipoMovimiento(
                TipoMovimiento.INGRESO
        );

        assertEquals(
                TipoMovimiento.INGRESO,
                movimiento.getTipoMovimiento()
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeCambiaElImportePorNull() {

        Movimiento movimiento = crearMovimiento();

        assertThrows(
                IllegalArgumentException.class,
                () -> movimiento.cambiarImporte(null)
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeCambiaElImportePorCero() {

        Movimiento movimiento = crearMovimiento();

        assertThrows(
                IllegalArgumentException.class,
                () -> movimiento.cambiarImporte(
                        BigDecimal.ZERO
                )
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeCambiaElImportePorUnValorNegativo() {

        Movimiento movimiento = crearMovimiento();

        assertThrows(
                IllegalArgumentException.class,
                () -> movimiento.cambiarImporte(
                        new BigDecimal("-500.00")
                )
        );
    }

    @Test
    void deberiaCambiarCorrectamenteElImporte() {

        Movimiento movimiento = crearMovimiento();

        movimiento.cambiarImporte(
                new BigDecimal("25000.00")
        );

        assertEquals(
                new BigDecimal("25000.00"),
                movimiento.getImporte()
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeCambiaLaFechaHoraPorNull() {

        Movimiento movimiento = crearMovimiento();

        assertThrows(
                NullPointerException.class,
                () -> movimiento.cambiarFechaHora(null)
        );
    }

    @Test
    void deberiaCambiarCorrectamenteLaFechaHora() {

        Movimiento movimiento = crearMovimiento();

        LocalDateTime nuevaFecha =
                LocalDateTime.of(
                        2026,
                        8,
                        20,
                        18,
                        30
                );

        movimiento.cambiarFechaHora(nuevaFecha);

        assertEquals(
                nuevaFecha,
                movimiento.getFechaHora()
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeCambiaLaDescripcionPorNull() {

        Movimiento movimiento = crearMovimiento();

        assertThrows(
                IllegalArgumentException.class,
                () -> movimiento.cambiarDescripcion(null)
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeCambiaLaDescripcionPorVacia() {

        Movimiento movimiento = crearMovimiento();

        assertThrows(
                IllegalArgumentException.class,
                () -> movimiento.cambiarDescripcion("")
        );
    }

    @Test
    void deberiaCambiarCorrectamenteLaDescripcion() {

        Movimiento movimiento = crearMovimiento();

        movimiento.cambiarDescripcion(
                "Compra supermercado"
        );

        assertEquals(
                "Compra supermercado",
                movimiento.getDescripcion()
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeCambiaLaCategoriaPorNull() {

        Movimiento movimiento = crearMovimiento();

        assertThrows(
                NullPointerException.class,
                () -> movimiento.cambiarCategoria(null)
        );
    }

    @Test
    void deberiaPermitirEstablecerObservacionesComoNull() {

        Movimiento movimiento = crearMovimiento();

        movimiento.cambiarObservaciones(null);

        assertNull(
                movimiento.getObservaciones()
        );
    }
}