package ar.com.agmilevecich.sofp.domain;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;

/**
 * Representa la valorización de una posición activa a un precio determinado.
 *
 * No es una entidad persistente. La valorización se deriva de la posición
 * y del precio actual informado para el activo.
 */
public class ValorizacionPosicionActivo {

    private final PosicionActivo posicion;
    private final BigDecimal precioActual;
    private final BigDecimal valorActual;
    private final BigDecimal gananciaPerdida;
    private final BigDecimal rendimientoPorcentual;

    public ValorizacionPosicionActivo(PosicionActivo posicion, BigDecimal precioActual) {
        this.posicion = Objects.requireNonNull(posicion, "La posición no puede ser nula");
        this.precioActual = Objects.requireNonNull(
                precioActual,
                "El precio actual no puede ser nulo"
        );

        if (precioActual.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio actual no puede ser negativo");
        }

        this.valorActual = posicion.getCantidad().multiply(precioActual);
        this.gananciaPerdida = valorActual.subtract(posicion.getCostoAdquisicion());
        this.rendimientoPorcentual = calcularRendimientoPorcentual();
    }

    public PosicionActivo getPosicion() {
        return posicion;
    }

    public BigDecimal getPrecioActual() {
        return precioActual;
    }

    public BigDecimal getValorActual() {
        return valorActual;
    }

    public BigDecimal getGananciaPerdida() {
        return gananciaPerdida;
    }

    public BigDecimal getRendimientoPorcentual() {
        return rendimientoPorcentual;
    }

    private BigDecimal calcularRendimientoPorcentual() {
        BigDecimal costo = posicion.getCostoAdquisicion();
        if (costo.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return gananciaPerdida
                .divide(costo, MathContext.DECIMAL128)
                .multiply(new BigDecimal("100"));
    }
}
