package ar.com.agmilevecich.sofp.util;

public final class Validaciones {

    private Validaciones() {
    }

    public static String textoObligatorio(String valor, String mensaje) {

        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensaje);
        }

        return valor;
    }
}