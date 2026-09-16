package ar.com.agmilevecich.sofp.persistence;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.FormaPago;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.Movimiento;
import ar.com.agmilevecich.sofp.domain.Obligacion;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.TipoCuenta;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.domain.TipoMovimiento;
import ar.com.agmilevecich.sofp.domain.Usuario;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ObligacionRepositoryTest {

    @Test
    void deberiaListarObligacionesDeUnaCuentaYUnCierreDeCiclo() {
        JpaTestManager.close();
        EntityManager em = JpaTestManager.createEntityManager();

        try {
            Datos datos = crearDatos();
            Obligacion obligacionObjetivo = crearObligacion(
                    datos.cuenta(), datos.categoria(),
                    LocalDateTime.of(2026, 9, 10, 10, 0),
                    "Consumo objetivo"
            );
            Obligacion otraDelMismoCiclo = crearObligacion(
                    datos.cuenta(), datos.categoria(),
                    LocalDateTime.of(2026, 9, 12, 10, 0),
                    "Otro consumo"
            );
            Obligacion otroCiclo = crearObligacion(
                    datos.cuenta(), datos.categoria(),
                    LocalDateTime.of(2026, 9, 20, 10, 0),
                    "Consumo siguiente ciclo"
            );
            Cuenta otraCuenta = crearCuenta(
                    datos.perfil(), datos.banco(), datos.moneda(),
                    "Otra tarjeta"
            );
            Obligacion otraCuentaMismoCierre = crearObligacion(
                    otraCuenta, datos.categoria(),
                    LocalDateTime.of(2026, 9, 10, 10, 0),
                    "Consumo otra cuenta"
            );

            ObligacionRepository repository = new ObligacionRepository(em);

            em.getTransaction().begin();
            persistirDatosBase(em, datos);
            em.persist(otraCuenta);
            repository.guardar(obligacionObjetivo);
            repository.guardar(otraDelMismoCiclo);
            repository.guardar(otroCiclo);
            repository.guardar(otraCuentaMismoCierre);
            em.getTransaction().commit();

            List<Obligacion> resultado = repository.listarPorCuentaYCierreCiclo(
                    datos.cuenta().getId(),
                    LocalDate.of(2026, 9, 15)
            );

            assertEquals(2, resultado.size());
            assertEquals(obligacionObjetivo.getId(), resultado.get(0).getId());
            assertEquals(otraDelMismoCiclo.getId(), resultado.get(1).getId());
            assertTrue(resultado.stream().allMatch(
                    obligacion -> obligacion.getMovimientoOrigen().getCuenta().getId()
                            .equals(datos.cuenta().getId())
            ));
            assertTrue(resultado.stream().allMatch(
                    obligacion -> LocalDate.of(2026, 9, 15)
                            .equals(obligacion.getFechaCierreCiclo())
            ));

        } finally {
            em.close();
        }
    }

    @Test
    void deberiaDevolverListaVaciaSiNoHayObligacionesParaElCierre() {
        JpaTestManager.close();
        EntityManager em = JpaTestManager.createEntityManager();

        try {
            Datos datos = crearDatos();
            ObligacionRepository repository = new ObligacionRepository(em);

            em.getTransaction().begin();
            persistirDatosBase(em, datos);
            em.getTransaction().commit();

            List<Obligacion> resultado = repository.listarPorCuentaYCierreCiclo(
                    datos.cuenta().getId(),
                    LocalDate.of(2026, 9, 15)
            );

            assertTrue(resultado.isEmpty());

        } finally {
            em.close();
        }
    }

    @Test
    void deberiaRechazarCuentaNulaAlListarPorCierre() {
        JpaTestManager.close();
        EntityManager em = JpaTestManager.createEntityManager();

        try {
            ObligacionRepository repository = new ObligacionRepository(em);

            assertThrows(
                    NullPointerException.class,
                    () -> repository.listarPorCuentaYCierreCiclo(
                            null, LocalDate.of(2026, 9, 15)
                    )
            );

        } finally {
            em.close();
        }
    }

    @Test
    void deberiaRechazarFechaDeCierreNulaAlListarPorCierre() {
        JpaTestManager.close();
        EntityManager em = JpaTestManager.createEntityManager();

        try {
            ObligacionRepository repository = new ObligacionRepository(em);

            assertThrows(
                    NullPointerException.class,
                    () -> repository.listarPorCuentaYCierreCiclo(1L, null)
            );

        } finally {
            em.close();
        }
    }

    private Datos crearDatos() {
        Usuario usuario = new Usuario(
                "Ariel", "Milevecich",
                "obligacion.repository." + System.nanoTime() + "@test.com",
                "hash"
        );
        PerfilFinanciero perfil = new PerfilFinanciero("Personal", usuario);
        InstitucionFinanciera banco = new InstitucionFinanciera(
                "Banco Santander", TipoInstitucionFinanciera.BANCO
        );
        Moneda moneda = new Moneda(
                "ARS", "Peso Argentino", 2, TipoMoneda.FIAT
        );
        Cuenta cuenta = crearCuenta(perfil, banco, moneda, "Tarjeta principal");
        Categoria categoria = new Categoria("Compra", perfil);

        return new Datos(usuario, perfil, banco, moneda, cuenta, categoria);
    }

    private Cuenta crearCuenta(
            PerfilFinanciero perfil,
            InstitucionFinanciera banco,
            Moneda moneda,
            String nombre
    ) {
        return new Cuenta(
                nombre,
                TipoCuenta.TARJETA_CREDITO,
                perfil,
                banco,
                moneda,
                new BigDecimal("500000.00"),
                15,
                10
        );
    }

    private Obligacion crearObligacion(
            Cuenta cuenta,
            Categoria categoria,
            LocalDateTime fechaHora,
            String descripcion
    ) {
        Movimiento movimiento = new Movimiento(
                cuenta,
                categoria,
                TipoMovimiento.EGRESO,
                new BigDecimal("100.00"),
                fechaHora,
                descripcion,
                FormaPago.TARJETA_CREDITO
        );
        return new Obligacion(movimiento);
    }

    private void persistirDatosBase(EntityManager em, Datos datos) {
        em.persist(datos.usuario());
        em.persist(datos.perfil());
        em.persist(datos.banco());
        em.persist(datos.moneda());
        em.persist(datos.cuenta());
        em.persist(datos.categoria());
    }

    private record Datos(
            Usuario usuario,
            PerfilFinanciero perfil,
            InstitucionFinanciera banco,
            Moneda moneda,
            Cuenta cuenta,
            Categoria categoria
    ) {
    }
}
