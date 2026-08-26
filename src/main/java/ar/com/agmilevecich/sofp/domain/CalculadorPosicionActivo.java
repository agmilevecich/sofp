package ar.com.agmilevecich.sofp.domain;

import java.util.List;
import java.util.Objects;

/**
 * Calcula la posición actual de un activo a partir de sus movimientos.
 *
 * La posición es un valor derivado y no se persiste como entidad propia.
 */
public final class CalculadorPosicionActivo {

    private CalculadorPosicionActivo() {
        // Clase de utilidad de dominio.
    }

    public static PosicionActivo calcular(
            Activo activo,
            List<MovimientoActivo> movimientos) {

        Objects.requireNonNull(activo, "El activo no puede ser nulo");
        Objects.requireNonNull(movimientos, "Los movimientos no pueden ser nulos");

        PosicionActivo posicion = new PosicionActivo(activo);

        for (MovimientoActivo movimiento : movimientos) {
            posicion.aplicarMovimiento(movimiento);
        }

        return posicion;
    }
}
