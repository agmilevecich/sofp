package ar.com.agmilevecich.sofp.domain;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Resumen dinámico del patrimonio financiero actual de un perfil.
 *
 * Los importes están expresados en la moneda de presentación del reporte.
 * No es una entidad persistente.
 */
public class ResumenPatrimonial {

    private final Moneda monedaPresentacion;
    private final BigDecimal activosMonetarios;
    private final BigDecimal activosInversiones;
    private final BigDecimal activosTotales;
    private final BigDecimal pasivosTarjetas;
    private final BigDecimal pasivosTotales;
    private final BigDecimal patrimonioNeto;

    public ResumenPatrimonial(
            Moneda monedaPresentacion,
            BigDecimal activosMonetarios,
            BigDecimal activosInversiones,
            BigDecimal pasivosTarjetas) {

        this.monedaPresentacion = Objects.requireNonNull(
                monedaPresentacion, "La moneda de presentación es obligatoria");
        this.activosMonetarios = importeNoNulo(activosMonetarios, "Los activos monetarios");
        this.activosInversiones = importeNoNulo(activosInversiones, "Los activos de inversiones");
        this.pasivosTarjetas = importeNoNulo(pasivosTarjetas, "Los pasivos de tarjetas");
        this.activosTotales = this.activosMonetarios.add(this.activosInversiones);
        this.pasivosTotales = this.pasivosTarjetas;
        this.patrimonioNeto = this.activosTotales.subtract(this.pasivosTotales);
    }

    public Moneda getMonedaPresentacion() {
        return monedaPresentacion;
    }

    public BigDecimal getActivosMonetarios() {
        return activosMonetarios;
    }

    public BigDecimal getActivosInversiones() {
        return activosInversiones;
    }

    public BigDecimal getActivosTotales() {
        return activosTotales;
    }

    public BigDecimal getPasivosTarjetas() {
        return pasivosTarjetas;
    }

    public BigDecimal getPasivosTotales() {
        return pasivosTotales;
    }

    public BigDecimal getPatrimonioNeto() {
        return patrimonioNeto;
    }

    private BigDecimal importeNoNulo(BigDecimal importe, String nombre) {
        return Objects.requireNonNull(importe, nombre + " son obligatorios");
    }
}
