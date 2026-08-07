package ar.com.agmilevecich.sofp.util;

import java.math.BigDecimal;

public final class Validaciones {

    private Validaciones() {
    }

    public static String textoObligatorio(String valor, String mensaje) {

        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensaje);
        }

        return valor;
    }

    public static BigDecimal importePositivo(BigDecimal valor, String mensaje) {

        if (valor == null) {
            throw new IllegalArgumentException(mensaje);
        }

        if (valor.signum() <= 0) {
            throw new IllegalArgumentException(mensaje);
        }

        return valor;
    }
}