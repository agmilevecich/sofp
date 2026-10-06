package ar.com.agmilevecich.sofp.ui;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RefinanciacionFormTest {

    @Test
    void deberiaCapturarParametrosDeRefinanciacion() {
        RefinanciacionForm form = new RefinanciacionForm();
        form.setFechaInicio(LocalDate.of(2026, 10, 6));
        form.setCantidadCuotas(12);
        form.setInteresInicial(new BigDecimal("1500.00"));
        form.setCargosIniciales(new BigDecimal("250.00"));
        form.setTasaAnual(new BigDecimal("48.0000"));

        assertEquals(LocalDate.of(2026, 10, 6), form.getFechaInicio());
        assertEquals(12, form.getCantidadCuotas());
        assertEquals(new BigDecimal("1500.00"), form.getInteresInicial());
        assertEquals(new BigDecimal("250.00"), form.getCargosIniciales());
        assertEquals(new BigDecimal("48.0000"), form.getTasaAnual());
    }
}
