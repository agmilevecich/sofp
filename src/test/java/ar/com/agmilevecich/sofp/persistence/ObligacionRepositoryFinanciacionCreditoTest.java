package ar.com.agmilevecich.sofp.persistence;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.Financiacion;
import ar.com.agmilevecich.sofp.domain.FormaPago;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.Movimiento;
import ar.com.agmilevecich.sofp.domain.Obligacion;
import ar.com.agmilevecich.sofp.domain.Refinanciacion;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.domain.TipoMovimiento;
import ar.com.agmilevecich.sofp.domain.Usuario;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ObligacionRepositoryFinanciacionCreditoTest {

    @Test
    void deudaFinanciadaPendienteContinuaConsumientoCreditoSinDobleConteo() {
        try (Fixture fixture = new Fixture()) {
            Obligacion obligacion = fixture.crearObligacion(new BigDecimal("100000.00"), fixture.ars);
            obligacion.crearFinanciacion(
                    LocalDateTime.of(2026, 9, 26, 0, 0).toLocalDate(),
                    new BigDecimal("100000.00")
            );
            fixture.persistir(obligacion);

            assertCredito(fixture, "100000.00");
        }
    }

    @Test
    void cargosFinancierosPendientesDeFinanciacionContinuanConsumientoCredito() {
        try (Fixture fixture = new Fixture()) {
            Obligacion obligacion = fixture.crearObligacion(new BigDecimal("100000.00"), fixture.ars);
            Financiacion financiacion = obligacion.crearFinanciacion(
                    LocalDate.of(2026, 9, 26),
                    new BigDecimal("100000.00")
            );
            financiacion.registrarInteres(
                    new BigDecimal("200.00"),
                    LocalDate.of(2026, 9, 28),
                    new BigDecimal("100000.00"),
                    new BigDecimal("36.5000"),
                    2
            );
            fixture.persistir(obligacion);

            assertCredito(fixture, "100200.00");
        }
    }

    @Test
    void pagoYReversionDeCargoFinancieroRestauranElCredito() {
        try (Fixture fixture = new Fixture()) {
            Obligacion obligacion = fixture.crearObligacion(new BigDecimal("100000.00"), fixture.ars);
            Financiacion financiacion = obligacion.crearFinanciacion(
                    LocalDate.of(2026, 9, 26),
                    new BigDecimal("100000.00")
            );
            financiacion.registrarInteres(
                    new BigDecimal("200.00"),
                    LocalDate.of(2026, 9, 28),
                    new BigDecimal("100000.00"),
                    new BigDecimal("36.5000"),
                    2
            );
            obligacion.registrarPagoFinanciacion(financiacion, new BigDecimal("200.00"));
            fixture.persistir(obligacion);

            assertCredito(fixture, "100000.00");

            financiacion.revertirPago(new BigDecimal("200.00"));
            fixture.merge(obligacion);

            assertCredito(fixture, "100200.00");
        }
    }

    @Test
    void pagoParcialDeFinanciacionReduceCreditoUtilizado() {
        try (Fixture fixture = new Fixture()) {
            Obligacion obligacion = fixture.crearObligacion(new BigDecimal("100000.00"), fixture.ars);
            Financiacion financiacion = obligacion.crearFinanciacion(
                    LocalDateTime.of(2026, 9, 26, 0, 0).toLocalDate(),
                    new BigDecimal("100000.00")
            );
            obligacion.registrarPagoFinanciacion(financiacion, new BigDecimal("40000.00"));
            fixture.persistir(obligacion);

            assertCredito(fixture, "60000.00");
        }
    }

    @Test
    void pagoTotalDeFinanciacionLiberaCredito() {
        try (Fixture fixture = new Fixture()) {
            Obligacion obligacion = fixture.crearObligacion(new BigDecimal("100000.00"), fixture.ars);
            Financiacion financiacion = obligacion.crearFinanciacion(
                    LocalDateTime.of(2026, 9, 26, 0, 0).toLocalDate(),
                    new BigDecimal("100000.00")
            );
            obligacion.registrarPagoFinanciacion(financiacion, new BigDecimal("100000.00"));
            fixture.persistir(obligacion);

            assertCredito(fixture, "0.00");
        }
    }

    @Test
    void variasFinanciacionesNoDuplicanElCreditoUtilizado() {
        try (Fixture fixture = new Fixture()) {
            Obligacion obligacion = fixture.crearObligacion(new BigDecimal("100000.00"), fixture.ars);
            obligacion.crearFinanciacion(
                    LocalDateTime.of(2026, 9, 26, 0, 0).toLocalDate(),
                    new BigDecimal("60000.00")
            );
            obligacion.crearFinanciacion(
                    LocalDateTime.of(2026, 10, 26, 0, 0).toLocalDate(),
                    new BigDecimal("40000.00")
            );
            fixture.persistir(obligacion);

            assertCredito(fixture, "100000.00");
        }
    }

    @Test
    void financiacionParcialMantieneSoloElSaldoPendienteComoCredito() {
        try (Fixture fixture = new Fixture()) {
            Obligacion obligacion = fixture.crearObligacion(new BigDecimal("100000.00"), fixture.ars);
            Financiacion financiacion = obligacion.crearFinanciacion(
                    LocalDateTime.of(2026, 9, 26, 0, 0).toLocalDate(),
                    new BigDecimal("60000.00")
            );
            obligacion.registrarPagoFinanciacion(financiacion, new BigDecimal("20000.00"));
            fixture.persistir(obligacion);

            assertCredito(fixture, "80000.00");
        }
    }

    @Test
    void financiacionMultidivisaUsaLaValorizacionPendienteParaElCredito() {
        try (Fixture fixture = new Fixture()) {
            Obligacion obligacion = fixture.crearObligacion(new BigDecimal("100.00"), fixture.usd);
            var tipoCambio = new ar.com.agmilevecich.sofp.domain.TipoCambio(
                    fixture.usd,
                    fixture.ars,
                    new BigDecimal("1500.00"),
                    LocalDateTime.of(2026, 9, 15, 23, 59),
                    "TEST"
            );
            Financiacion financiacion = obligacion.crearFinanciacion(
                    LocalDateTime.of(2026, 9, 26, 0, 0).toLocalDate(),
                    new BigDecimal("100.00"),
                    fixture.usd,
                    tipoCambio,
                    false
            );
            fixture.persistirTipoCambio(tipoCambio);
            fixture.persistir(obligacion);

            assertCredito(fixture, "150000.00");

            obligacion.registrarPagoFinanciacion(financiacion, new BigDecimal("40.00"));
            fixture.merge(obligacion);

            assertCredito(fixture, "90000.00");
        }
    }

    @Test
    void deudaNormalYFinanciadaSeSumanUnaSolaVez() {
        try (Fixture fixture = new Fixture()) {
            Obligacion financiada = fixture.crearObligacion(new BigDecimal("100000.00"), fixture.ars);
            financiada.crearFinanciacion(
                    LocalDateTime.of(2026, 9, 26, 0, 0).toLocalDate(),
                    new BigDecimal("100000.00")
            );

            Obligacion normal = fixture.crearObligacion(
                    new BigDecimal("50000.00"),
                    fixture.ars,
                    LocalDateTime.of(2026, 9, 11, 10, 0)
            );

            fixture.persistir(financiada);
            fixture.persistir(normal);

            assertCredito(fixture, "150000.00");
        }
    }

    @Test
    void refinanciacionPendienteContinuaConsumientoCreditoYLosPagosLoLiberan() {
        try (Fixture fixture = new Fixture()) {
            Obligacion obligacion = fixture.crearObligacion(new BigDecimal("100000.00"), fixture.ars);
            Refinanciacion refinanciacion = new Refinanciacion(
                    obligacion,
                    fixture.ars,
                    LocalDate.of(2026, 9, 26),
                    new BigDecimal("100000.00"),
                    new BigDecimal("10000.00"),
                    BigDecimal.ZERO,
                    new BigDecimal("24.0000"),
                    3
            );
            refinanciacion.generarCuotas();
            obligacion.marcarRefinanciada();

            fixture.em.getTransaction().begin();
            fixture.em.persist(obligacion);
            fixture.em.persist(refinanciacion);
            fixture.em.getTransaction().commit();
            fixture.em.clear();

            assertCredito(fixture, "110000.00");

            fixture.em.getTransaction().begin();
            Refinanciacion persistida = fixture.em.find(Refinanciacion.class, refinanciacion.getId());
            persistida.registrarPago(new BigDecimal("40000.00"));
            fixture.em.getTransaction().commit();
            fixture.em.clear();

            assertCredito(fixture, "70000.00");
        }
    }

    @Test
    void financiacionSobreLiquidacionMantieneElCreditoHastaSuCancelacion() {
        try (Fixture fixture = new Fixture()) {
            Obligacion obligacion = fixture.crearObligacion(new BigDecimal("100.00"), fixture.usd);
            var tipoCambio = new ar.com.agmilevecich.sofp.domain.TipoCambio(
                    fixture.usd,
                    fixture.ars,
                    new BigDecimal("1500.00"),
                    LocalDateTime.of(2026, 9, 15, 12, 0),
                    "TEST"
            );
            obligacion.liquidar(tipoCambio);
            Financiacion financiacion = obligacion.crearFinanciacion(
                    java.time.LocalDate.of(2026, 9, 26),
                    new BigDecimal("150000.00"),
                    fixture.ars,
                    null,
                    true
            );
            fixture.persistirTipoCambio(tipoCambio);
            fixture.persistir(obligacion);

            assertCredito(fixture, "150000.00");

            obligacion.registrarPagoFinanciacion(financiacion, new BigDecimal("50000.00"));
            fixture.merge(obligacion);

            assertCredito(fixture, "100000.00");

            obligacion.registrarPagoFinanciacion(financiacion, new BigDecimal("100000.00"));
            fixture.merge(obligacion);

            assertCredito(fixture, "0.00");
        }
    }

    private void assertCredito(Fixture fixture, String esperado) {
        BigDecimal resultado = fixture.repository.sumarCreditoUtilizadoPorCuenta(
                fixture.tarjeta.getId(),
                fixture.ars
        );
        assertEquals(0, new BigDecimal(esperado).compareTo(resultado));
    }

    private static final class Fixture implements AutoCloseable {
        private final EntityManager em = JpaTestManager.createEntityManager();
        private final ObligacionRepository repository = new ObligacionRepository(em);
        private final Usuario usuario;
        private final Cuenta tarjeta;
        private final Categoria categoria;
        private final Moneda ars;
        private final Moneda usd;

        private Fixture() {
            usuario = new Usuario(
                    "Ariel",
                    "Milevecich",
                    "credito.financiacion." + System.nanoTime() + "@test.com",
                    "hash"
            );
            PerfilFinanciero perfil = new PerfilFinanciero("Personal", usuario);
            InstitucionFinanciera banco = new InstitucionFinanciera(
                    "Banco de Prueba",
                    TipoInstitucionFinanciera.BANCO
            );
            ars = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
            usd = new Moneda("USD", "Dólar estadounidense", 2, TipoMoneda.FIAT);
            tarjeta = new Cuenta(
                    "Tarjeta",
                    perfil,
                    banco,
                    ars,
                    new BigDecimal("500000.00"),
                    15,
                    10
            );
            categoria = new Categoria("Compras", perfil);

            em.getTransaction().begin();
            em.persist(usuario);
            em.persist(perfil);
            em.persist(banco);
            em.persist(ars);
            em.persist(usd);
            em.persist(tarjeta);
            em.persist(categoria);
            em.getTransaction().commit();
        }

        private Obligacion crearObligacion(BigDecimal importe, Moneda moneda) {
            return crearObligacion(
                    importe,
                    moneda,
                    LocalDateTime.of(2026, 9, 10, 10, 0)
            );
        }

        private Obligacion crearObligacion(
                BigDecimal importe,
                Moneda moneda,
                LocalDateTime fechaHora
        ) {
            Movimiento movimiento = new Movimiento(
                    tarjeta,
                    categoria,
                    moneda,
                    TipoMovimiento.EGRESO,
                    importe,
                    fechaHora,
                    "Consumo",
                    FormaPago.TARJETA_CREDITO
            );
            em.getTransaction().begin();
            em.persist(movimiento);
            Obligacion obligacion = new Obligacion(movimiento);
            em.getTransaction().commit();
            return obligacion;
        }

        private void persistir(Obligacion obligacion) {
            em.getTransaction().begin();
            em.persist(obligacion);
            em.getTransaction().commit();
            em.clear();
        }

        private void merge(Obligacion obligacion) {
            em.getTransaction().begin();
            em.merge(obligacion);
            em.getTransaction().commit();
            em.clear();
        }

        private void persistirTipoCambio(ar.com.agmilevecich.sofp.domain.TipoCambio tipoCambio) {
            em.getTransaction().begin();
            em.persist(tipoCambio);
            em.getTransaction().commit();
        }

        @Override
        public void close() {
            if (em.isOpen()) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                em.close();
            }
            JpaTestManager.close();
        }
    }
}
