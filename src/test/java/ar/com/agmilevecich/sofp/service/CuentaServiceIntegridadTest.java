package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.Movimiento;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.TipoCuenta;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.domain.TipoMovimiento;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.CuentaRepository;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CuentaServiceIntegridadTest {

    private EntityManager entityManager;
    private CuentaService cuentaService;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();
        cuentaService = new CuentaService(
                new CuentaRepository(entityManager),
                new MovimientoRepository(entityManager),
                entityManager
        );
    }

    @AfterEach
    void tearDown() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
        JpaTestManager.close();
    }

    @Test
    void noDeberiaPermitirCambiarTipoConMovimientos() {
        Datos datos = persistirCuenta(TipoCuenta.CAJA_AHORRO, "ariel.integridad.tipo." + System.nanoTime());
        persistirMovimiento(datos, "Movimiento histórico");

        assertThrows(IllegalArgumentException.class, () -> cuentaService.modificarTipoCuenta(
                datos.cuenta().getId(),
                datos.usuario().getId(),
                TipoCuenta.CUENTA_CORRIENTE
        ));

        assertEquals(TipoCuenta.CAJA_AHORRO, cuentaService.buscarPorId(
                datos.cuenta().getId(), datos.usuario().getId()).orElseThrow().getTipoCuenta());
    }

    @Test
    void noDeberiaPermitirCambiarMonedaConMovimientos() {
        Datos datos = persistirCuenta(TipoCuenta.CAJA_AHORRO, "ariel.integridad.moneda." + System.nanoTime());
        persistirMovimiento(datos, "Movimiento histórico");
        Moneda usd = new Moneda("USD", "Dólar estadounidense", 2, TipoMoneda.FIAT);

        entityManager.getTransaction().begin();
        entityManager.persist(usd);
        entityManager.getTransaction().commit();

        assertThrows(IllegalArgumentException.class, () -> cuentaService.modificarMoneda(
                datos.cuenta().getId(),
                datos.usuario().getId(),
                usd
        ));

        assertEquals("ARS", cuentaService.buscarPorId(
                datos.cuenta().getId(), datos.usuario().getId()).orElseThrow().getMoneda().getCodigo());
    }

    @Test
    void deberiaPermitirCambiarEntreTiposNoTarjetaSinMovimientos() {
        Datos datos = persistirCuenta(TipoCuenta.CAJA_AHORRO, "ariel.integridad.tipo.sin.historial." + System.nanoTime());

        Cuenta actualizada = cuentaService.modificarTipoCuenta(
                datos.cuenta().getId(),
                datos.usuario().getId(),
                TipoCuenta.CUENTA_CORRIENTE
        );

        assertEquals(TipoCuenta.CUENTA_CORRIENTE, actualizada.getTipoCuenta());
    }

    @Test
    void noDeberiaPermitirTransicionesHaciaOViniendoDeTarjetaPorEstaApi() {
        Datos normal = persistirCuenta(TipoCuenta.CAJA_AHORRO, "ariel.integridad.card.hacia." + System.nanoTime());
        assertThrows(IllegalArgumentException.class, () -> cuentaService.modificarTipoCuenta(
                normal.cuenta().getId(), normal.usuario().getId(), TipoCuenta.TARJETA_CREDITO));

        Datos tarjeta = persistirCuentaTarjeta("ariel.integridad.card.desde." + System.nanoTime());
        assertThrows(IllegalArgumentException.class, () -> cuentaService.modificarTipoCuenta(
                tarjeta.cuenta().getId(), tarjeta.usuario().getId(), TipoCuenta.CAJA_AHORRO));
    }

    private Datos persistirCuenta(TipoCuenta tipo, String nombreUsuario) {
        Usuario usuario = new Usuario(nombreUsuario, "clave-segura");
        PerfilFinanciero perfil = new PerfilFinanciero("Perfil principal", usuario);
        usuario.agregarPerfilFinanciero(perfil);
        InstitucionFinanciera institucion = new InstitucionFinanciera("Banco", TipoInstitucionFinanciera.BANCO);
        Moneda moneda = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        Cuenta cuenta = new Cuenta("Cuenta principal", tipo, perfil, institucion, moneda);

        entityManager.getTransaction().begin();
        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(institucion);
        entityManager.persist(moneda);
        entityManager.persist(cuenta);
        entityManager.getTransaction().commit();

        return new Datos(usuario, perfil, institucion, moneda, cuenta);
    }

    private Datos persistirCuentaTarjeta(String nombreUsuario) {
        Usuario usuario = new Usuario(nombreUsuario, "clave-segura");
        PerfilFinanciero perfil = new PerfilFinanciero("Perfil principal", usuario);
        usuario.agregarPerfilFinanciero(perfil);
        InstitucionFinanciera institucion = new InstitucionFinanciera("Banco", TipoInstitucionFinanciera.BANCO);
        Moneda moneda = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        Cuenta cuenta = new Cuenta(
                "Tarjeta",
                perfil,
                institucion,
                moneda,
                new BigDecimal("100000.00"),
                10,
                20
        );

        entityManager.getTransaction().begin();
        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(institucion);
        entityManager.persist(moneda);
        entityManager.persist(cuenta);
        entityManager.getTransaction().commit();

        return new Datos(usuario, perfil, institucion, moneda, cuenta);
    }

    private void persistirMovimiento(Datos datos, String descripcion) {
        Categoria categoria = new Categoria("Gastos", datos.perfil());
        Movimiento movimiento = new Movimiento(
                datos.cuenta(),
                categoria,
                TipoMovimiento.EGRESO,
                new BigDecimal("1000.00"),
                LocalDateTime.now(),
                descripcion
        );

        entityManager.getTransaction().begin();
        entityManager.persist(categoria);
        entityManager.persist(movimiento);
        entityManager.getTransaction().commit();
    }

    private record Datos(
            Usuario usuario,
            PerfilFinanciero perfil,
            InstitucionFinanciera institucion,
            Moneda moneda,
            Cuenta cuenta) {
    }
}
