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
        assertEquals(new BigDecimal("15000.50"), movimiento.getImporte());
        assertEquals("Compra Carrefour", movimiento.getDescripcion());
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
}