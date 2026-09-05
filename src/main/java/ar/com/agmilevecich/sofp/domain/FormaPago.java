package ar.com.agmilevecich.sofp.domain;

/**
 * Representa la forma o medio utilizado para realizar un pago.
 *
 * <p>La forma de pago es independiente de la cuenta financiera afectada.
 * Una compra con tarjeta de crédito, por ejemplo, puede generar una
 * obligación sin producir una salida inmediata de fondos de una cuenta.</p>
 */
public enum FormaPago {

    /**
     * Pago realizado en efectivo.
     */
    EFECTIVO,

    /**
     * Pago realizado mediante transferencia.
     */
    TRANSFERENCIA,

    /**
     * Pago realizado con tarjeta de débito.
     */
    TARJETA_DEBITO,

    /**
     * Pago realizado con tarjeta de crédito.
     */
    TARJETA_CREDITO,

    /**
     * Pago realizado mediante un código QR.
     */
    QR
}
