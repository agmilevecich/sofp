package ar.com.agmilevecich.sofp.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CicloFacturacionTest {

    @Test
    void deberiaAsignarConsumoAnteriorAlCierreAlCicloActual() {
        Cuenta tarjeta = crearTarjeta(10, 25);

        CicloFacturacion ciclo = tarjeta.calcularCicloFacturacion(
                LocalDate.of(2026, 9, 5)
        );

        assertEquals(LocalDate.of(2026, 8, 11), ciclo.getFechaInicio());
        assertEquals(LocalDate.of(2026, 9, 10), ciclo.getFechaCierre());
        assertEquals(LocalDate.of(2026, 9, 25), ciclo.getFechaVencimiento());
    }

    @Test
    void deberiaAsignarConsumoDelDiaDeCierreAlCicloQueCierraEseDia() {
        Cuenta tarjeta = crearTarjeta(10, 25);

        CicloFacturacion ciclo = tarjeta.calcularCicloFacturacion(
                LocalDate.of(2026, 9, 10)
        );

        assertEquals(LocalDate.of(2026, 8, 11), ciclo.getFechaInicio());
        assertEquals(LocalDate.of(2026, 9, 10), ciclo.getFechaCierre());
        assertEquals(LocalDate.of(2026, 9, 25), ciclo.getFechaVencimiento());
    }

    @Test
    void deberiaAsignarConsumoPosteriorAlCierreAlCicloSiguiente() {
        Cuenta tarjeta = crearTarjeta(10, 25);

        CicloFacturacion ciclo = tarjeta.calcularCicloFacturacion(
                LocalDate.of(2026, 9, 11)
        );

        assertEquals(LocalDate.of(2026, 9, 11), ciclo.getFechaInicio());
        assertEquals(LocalDate.of(2026, 10, 10), ciclo.getFechaCierre());
        assertEquals(LocalDate.of(2026, 10, 25), ciclo.getFechaVencimiento());
    }

    @Test
    void deberiaLlevarElVencimientoAlMesSiguienteCuandoElDiaEsAnteriorAlCierre() {
        Cuenta tarjeta = crearTarjeta(10, 5);

        CicloFacturacion ciclo = tarjeta.calcularCicloFacturacion(
                LocalDate.of(2026, 9, 5)
        );

        assertEquals(LocalDate.of(2026, 9, 10), ciclo.getFechaCierre());
        assertEquals(LocalDate.of(2026, 10, 5), ciclo.getFechaVencimiento());
    }

    @Test
    void deberiaLlevarElVencimientoAlMesSiguienteCuandoCoincideConElCierre() {
        Cuenta tarjeta = crearTarjeta(10, 10);

        CicloFacturacion ciclo = tarjeta.calcularCicloFacturacion(
                LocalDate.of(2026, 9, 5)
        );

        assertEquals(LocalDate.of(2026, 9, 10), ciclo.getFechaCierre());
        assertEquals(LocalDate.of(2026, 10, 10), ciclo.getFechaVencimiento());
    }

    @Test
    void deberiaAjustarCierreAlUltimoDiaRealDeFebrero() {
        Cuenta tarjeta = crearTarjeta(31, 25);

        CicloFacturacion ciclo = tarjeta.calcularCicloFacturacion(
                LocalDate.of(2026, 2, 20)
        );

        assertEquals(LocalDate.of(2026, 2, 28), ciclo.getFechaCierre());
        assertEquals(LocalDate.of(2026, 2, 1), ciclo.getFechaInicio());
        assertEquals(LocalDate.of(2026, 3, 25), ciclo.getFechaVencimiento());
    }

    @Test
    void deberiaAjustarVencimientoAlUltimoDiaRealDelMes() {
        Cuenta tarjeta = crearTarjeta(10, 31);

        CicloFacturacion ciclo = tarjeta.calcularCicloFacturacion(
                LocalDate.of(2026, 9, 5)
        );

        assertEquals(LocalDate.of(2026, 9, 10), ciclo.getFechaCierre());
        assertEquals(LocalDate.of(2026, 10, 31), ciclo.getFechaVencimiento());
    }

    @Test
    void deberiaResolverCorrectamenteElCambioDeAnio() {
        Cuenta tarjeta = crearTarjeta(10, 25);

        CicloFacturacion ciclo = tarjeta.calcularCicloFacturacion(
                LocalDate.of(2026, 12, 11)
        );

        assertEquals(LocalDate.of(2026, 12, 11), ciclo.getFechaInicio());
        assertEquals(LocalDate.of(2027, 1, 10), ciclo.getFechaCierre());
        assertEquals(LocalDate.of(2027, 1, 25), ciclo.getFechaVencimiento());
    }

    @Test
    void deberiaAsignarConsumoAlCicloActualCuandoOcurreExactamenteEnElDiaDeCierre() {
        Cuenta tarjeta = crearTarjeta(15, 5);

        CicloFacturacion ciclo = tarjeta.calcularCicloFacturacion(
                LocalDate.of(2026, 9, 15)
        );

        assertEquals(LocalDate.of(2026, 8, 16), ciclo.getFechaInicio());
        assertEquals(LocalDate.of(2026, 9, 15), ciclo.getFechaCierre());
        assertEquals(LocalDate.of(2026, 10, 5), ciclo.getFechaVencimiento());
    }

    @Test
    void deberiaAsignarConsumoDelDiaPosteriorAlCierreAlCicloSiguiente() {
        Cuenta tarjeta = crearTarjeta(15, 5);

        CicloFacturacion ciclo = tarjeta.calcularCicloFacturacion(
                LocalDate.of(2026, 9, 16)
        );

        assertEquals(LocalDate.of(2026, 9, 16), ciclo.getFechaInicio());
        assertEquals(LocalDate.of(2026, 10, 15), ciclo.getFechaCierre());
        assertEquals(LocalDate.of(2026, 11, 5), ciclo.getFechaVencimiento());
    }

    @Test
    void noDeberiaPermitirFechaDeConsumoNula() {
        Cuenta tarjeta = crearTarjeta(10, 25);

        assertThrows(
                NullPointerException.class,
                () -> tarjeta.calcularCicloFacturacion(null)
        );
    }

    private Cuenta crearTarjeta(int diaCierre, int diaVencimiento) {
        Usuario usuario = new Usuario(
                "Ariel",
                "Usuario",
                "ariel@test.com",
                "hash"
        );
        PerfilFinanciero perfil = new PerfilFinanciero("Personal", usuario);
        InstitucionFinanciera institucion = new InstitucionFinanciera(
                "Banco Santander",
                TipoInstitucionFinanciera.BANCO
        );
        Moneda moneda = new Moneda(
                "ARS",
                "Peso Argentino",
                2,
                TipoMoneda.FIAT
        );

        return new Cuenta(
                "Visa Santander",
                perfil,
                institucion,
                moneda,
                new BigDecimal("500000.00"),
                diaCierre,
                diaVencimiento
        );
    }
}
