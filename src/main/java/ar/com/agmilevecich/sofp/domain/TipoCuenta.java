package ar.com.agmilevecich.sofp.domain;

/**
 * Representa los distintos tipos de cuentas que administra el SOFP.
 */
public enum TipoCuenta {

    /**
     * Caja de ahorro bancaria.
     */
    CAJA_AHORRO,

    /**
     * Cuenta corriente bancaria.
     */
    CUENTA_CORRIENTE,

    /**
     * Billetera virtual.
     */
    BILLETERA_VIRTUAL,

    /**
     * Cuenta de efectivo.
     */
    EFECTIVO,

    /**
     * Cuenta comitente para operar inversiones.
     */
    CUENTA_COMITENTE,

    /**
     * Cuenta destinada a la administración de criptoactivos.
     */
    CRIPTOACTIVOS
}