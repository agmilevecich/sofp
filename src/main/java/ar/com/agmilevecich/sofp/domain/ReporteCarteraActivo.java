package ar.com.agmilevecich.sofp.domain;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.Objects;

/**
 * Representa un reporte consolidado de la valorización de una cartera de activos.
 *
 * No es una entidad persistente. El reporte se construye a partir de las
 * valorizaciones de las posiciones ya calculadas.
 */
public class ReporteCarteraActivo {

    private final List<ValorizacionPosicionActivo> valorizaciones;
    private final BigDecimal costoTotal;
    private final BigDecimal valorActualTotal;
    private final BigDecimal gananciaPerdidaTotal;
    private final BigDecimal rendimientoPorcentualTotal;

    public ReporteCarteraActivo(List<ValorizacionPosicionActivo> valorizaciones) {
        Objects.requireNonNull(valorizaciones, "Las valorizaciones no pueden ser nulas");
        if (valorizaciones.stream().anyMatch(Objects::isNull)) {
            throw new NullPointerException("Las valorizaciones no pueden contener elementos nulos");
        }

        this.valorizaciones = List.copyOf(valorizaciones);
        this.costoTotal = calcularCostoTotal();
        this.valorActualTotal = calcularValorActualTotal();
        this.gananciaPerdidaTotal = valorActualTotal.subtract(costoTotal);
        this.rendimientoPorcentualTotal = calcularRendimientoPorcentualTotal();
    }

    public List<ValorizacionPosicionActivo> getValorizaciones() {
        return valorizaciones;
    }

    public BigDecimal getCostoTotal() {
        return costoTotal;
    }

    public BigDecimal getValorActualTotal() {
        return valorActualTotal;
    }

    public BigDecimal getGananciaPerdidaTotal() {
        return gananciaPerdidaTotal;
    }

    public BigDecimal getRendimientoPorcentualTotal() {
        return rendimientoPorcentualTotal;
    }

    private BigDecimal calcularCostoTotal() {
        return valorizaciones.stream()
                .map(valorizacion -> valorizacion.getPosicion().getCostoAdquisicion())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calcularValorActualTotal() {
        return valorizaciones.stream()
                .map(ValorizacionPosicionActivo::getValorActual)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calcularRendimientoPorcentualTotal() {
        if (costoTotal.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return gananciaPerdidaTotal
                .divide(costoTotal, MathContext.DECIMAL128)
                .multiply(new BigDecimal("100"));
    }
}
