package ar.com.agmilevecich.sofp.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class ResumenResultadoFinanciero {

    private final LocalDate fechaDesde;
    private final LocalDate fechaHasta;
    private final Map<Moneda, BigDecimal> ingresos;
    private final Map<Moneda, BigDecimal> egresos;

    public ResumenResultadoFinanciero(LocalDate fechaDesde,
                                      LocalDate fechaHasta,
                                      Map<Moneda, BigDecimal> ingresos,
                                      Map<Moneda, BigDecimal> egresos) {
        this.fechaDesde = Objects.requireNonNull(fechaDesde, "La fecha desde es obligatoria");
        this.fechaHasta = Objects.requireNonNull(fechaHasta, "La fecha hasta es obligatoria");
        if (fechaDesde.isAfter(fechaHasta)) {
            throw new IllegalArgumentException("La fecha desde no puede ser posterior a la fecha hasta");
        }
        this.ingresos = copiar(ingresos, "Los ingresos son obligatorios");
        this.egresos = copiar(egresos, "Los egresos son obligatorios");
    }

    public LocalDate getFechaDesde() { return fechaDesde; }
    public LocalDate getFechaHasta() { return fechaHasta; }

    public Map<Moneda, BigDecimal> getIngresos() {
        return ingresos;
    }

    public Map<Moneda, BigDecimal> getEgresos() {
        return egresos;
    }

    public BigDecimal getIngresos(Moneda moneda) {
        Objects.requireNonNull(moneda, "La moneda es obligatoria");
        return ingresos.getOrDefault(moneda, BigDecimal.ZERO.setScale(moneda.getCantidadDecimales()));
    }

    public BigDecimal getEgresos(Moneda moneda) {
        Objects.requireNonNull(moneda, "La moneda es obligatoria");
        return egresos.getOrDefault(moneda, BigDecimal.ZERO.setScale(moneda.getCantidadDecimales()));
    }

    public BigDecimal getResultado(Moneda moneda) {
        return getIngresos(moneda).subtract(getEgresos(moneda));
    }

    private Map<Moneda, BigDecimal> copiar(Map<Moneda, BigDecimal> valores, String mensaje) {
        Objects.requireNonNull(valores, mensaje);
        Map<Moneda, BigDecimal> copia = new LinkedHashMap<>();
        valores.forEach((moneda, importe) -> {
            Objects.requireNonNull(moneda, "La moneda es obligatoria");
            Objects.requireNonNull(importe, "El importe es obligatorio");
            copia.put(moneda, importe);
        });
        return Collections.unmodifiableMap(copia);
    }
}
