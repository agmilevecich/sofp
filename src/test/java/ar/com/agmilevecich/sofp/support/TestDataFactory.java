package ar.com.agmilevecich.sofp.support;

import ar.com.agmilevecich.sofp.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class TestDataFactory {

    private static final String NOMBRE_USUARIO = "Ariel";
    private static final String APELLIDO_USUARIO = "Usuario";
    private static final String EMAIL_USUARIO = "ariel@test.com";
    private static final String PASSWORD_HASH = "hash";
    private static final String NOMBRE_PERFIL = "Personal";
    private static final String NOMBRE_BANCO = "Banco Santander";
    private static final String CODIGO_MONEDA = "ARS";
    private static final String NOMBRE_MONEDA = "Peso Argentino";
    private static final int DECIMALES_MONEDA = 2;
    private static final String NOMBRE_CUENTA = "Caja de Ahorro";
    private static final String NOMBRE_CATEGORIA = "Supermercado";
    private static final BigDecimal IMPORTE_MOVIMIENTO =
            new BigDecimal("15000.50");

    private static final String DESCRIPCION_MOVIMIENTO =
            "Compra Carrefour";

    private TestDataFactory() {
    }

    public static Usuario crearUsuario() {

        return new Usuario(
                NOMBRE_USUARIO,
                APELLIDO_USUARIO,
                EMAIL_USUARIO,
                PASSWORD_HASH
        );
    }

    public static PerfilFinanciero crearPerfil() {

        Usuario usuario = crearUsuario();

        PerfilFinanciero perfil = new PerfilFinanciero(
                NOMBRE_PERFIL,
                usuario
        );

        usuario.agregarPerfilFinanciero(perfil);

        return perfil;
    }

    public static InstitucionFinanciera crearBanco() {

        return new InstitucionFinanciera(
                NOMBRE_BANCO,
                TipoInstitucionFinanciera.BANCO
        );
    }

    public static Moneda crearMoneda() {

        return new Moneda(
                CODIGO_MONEDA,
                NOMBRE_MONEDA,
                DECIMALES_MONEDA,
                TipoMoneda.FIAT
        );
    }

    public static Cuenta crearCuenta() {

        PerfilFinanciero perfil = crearPerfil();
        InstitucionFinanciera banco = crearBanco();
        Moneda moneda = crearMoneda();

        return new Cuenta(
                NOMBRE_CUENTA,
                TipoCuenta.CAJA_AHORRO,
                perfil,
                banco,
                moneda
        );
    }

    public static Categoria crearCategoria() {

        PerfilFinanciero perfil = crearPerfil();

        return new Categoria(
                NOMBRE_CATEGORIA,
                perfil
        );
    }

    public static Movimiento crearMovimiento() {

        Cuenta cuenta = crearCuenta();
        Categoria categoria = crearCategoria();

        return new Movimiento(
                cuenta,
                categoria,
                TipoMovimiento.EGRESO,
                IMPORTE_MOVIMIENTO,
                LocalDateTime.now(),
                DESCRIPCION_MOVIMIENTO
        );
    }
}