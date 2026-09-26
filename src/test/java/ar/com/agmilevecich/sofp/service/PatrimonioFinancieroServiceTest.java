package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.FormaPago;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.ResumenPatrimonial;
import ar.com.agmilevecich.sofp.domain.TipoCuenta;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.domain.TipoMovimiento;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.CuentaRepository;
import ar.com.agmilevecich.sofp.persistence.MonedaRepository;
import ar.com.agmilevecich.sofp.persistence.MovimientoActivoRepository;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import ar.com.agmilevecich.sofp.persistence.ObligacionRepository;
import ar.com.agmilevecich.sofp.persistence.TipoCambioRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PatrimonioFinancieroServiceTest {

    private EntityManager entityManager;
    private Usuario usuario;
    private PerfilFinanciero perfil;
    private Cuenta cuenta;
    private Categoria categoria;
    private Moneda ars;
    private MovimientoService movimientoService;
    private PatrimonioFinancieroService patrimonioService;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();

        movimientoService = new MovimientoService(
                entityManager,
                new MovimientoRepository(entityManager)
        );

        patrimonioService = new PatrimonioFinancieroService(
                new CuentaService(
                        new CuentaRepository(entityManager),
                        new MovimientoRepository(entityManager),
                        entityManager
                ),
                new CarteraActivoService(
                        new MovimientoActivoRepository(entityManager)
                ),
                new ObligacionRepository(entityManager),
                new TipoCambioRepository(entityManager),
                new MonedaRepository(entityManager)
        );

        usuario = new Usuario(
                "Ariel",
                "Patrimonio",
                "patrimonio." + System.nanoTime() + "@test.com",
                "hash"
        );
        perfil = new PerfilFinanciero("Perfil patrimonial", usuario);
        usuario.agregarPerfilFinanciero(perfil);

        InstitucionFinanciera institucion = new InstitucionFinanciera(
                "Banco Patrimonio",
                TipoInstitucionFinanciera.BANCO
        );

        ars = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        cuenta = new Cuenta(
                "Cuenta principal",
                TipoCuenta.CAJA_AHORRO,
                perfil,
                institucion,
                ars
        );
        categoria = new Categoria("General", perfil);

        entityManager.getTransaction().begin();
        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(institucion);
        entityManager.persist(ars);
        entityManager.persist(cuenta);
        entityManager.persist(categoria);
        entityManager.getTransaction().commit();
    }

    @AfterEach
    void tearDown() {
        if (entityManager != null && entityManager.isOpen()) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            entityManager.close();
        }
        JpaTestManager.close();
    }

    @Test
    void deberiaCalcularPatrimonioConSaldoMonetario() {
        movimientoService.registrar(
                cuenta,
                categoria,
                TipoMovimiento.INGRESO,
                new BigDecimal("100000.00"),
                LocalDateTime.of(2026, 9, 25, 10, 0),
                "Saldo inicial",
                usuario.getId()
        );

        ResumenPatrimonial resumen = patrimonioService.calcular(
                perfil,
                usuario.getId(),
                Map.of()
        );

        assertEquals(new BigDecimal("100000.00"), resumen.getActivosMonetarios());
        assertEquals(new BigDecimal("100000.00"), resumen.getActivosTotales());
        assertEquals(BigDecimal.ZERO, resumen.getPasivosTotales());
        assertEquals(new BigDecimal("100000.00"), resumen.getPatrimonioNeto());
    }

    @Test
    void deberiaDescontarDeudaDeTarjetaSinContarLaTarjetaComoActivo() {
        movimientoService.registrar(
                cuenta,
                categoria,
                TipoMovimiento.INGRESO,
                new BigDecimal("100000.00"),
                LocalDateTime.of(2026, 9, 25, 10, 0),
                "Saldo inicial",
                usuario.getId()
        );

        Cuenta tarjeta = new Cuenta(
                "Visa Patrimonio",
                perfil,
                entityManager.createQuery(
                        "SELECT i FROM InstitucionFinanciera i",
                        InstitucionFinanciera.class
                ).setMaxResults(1).getSingleResult(),
                ars,
                new BigDecimal("500000.00"),
                10,
                25
        );

        entityManager.getTransaction().begin();
        entityManager.persist(tarjeta);
        entityManager.getTransaction().commit();

        movimientoService.registrar(
                tarjeta,
                categoria,
                TipoMovimiento.EGRESO,
                new BigDecimal("30000.00"),
                LocalDateTime.of(2026, 9, 25, 11, 0),
                "Compra con tarjeta",
                FormaPago.TARJETA_CREDITO,
                usuario.getId()
        );

        ResumenPatrimonial resumen = patrimonioService.calcular(
                perfil,
                usuario.getId(),
                Map.of()
        );

        assertEquals(new BigDecimal("100000.00"), resumen.getActivosMonetarios());
        assertEquals(new BigDecimal("30000.00"), resumen.getPasivosTarjetas());
        assertEquals(new BigDecimal("100000.00"), resumen.getActivosTotales());
        assertEquals(new BigDecimal("70000.00"), resumen.getPatrimonioNeto());
    }

    @Test
    void deberiaRechazarPerfilDeOtroUsuario() {
        Usuario otroUsuario = new Usuario(
                "Otro",
                "Usuario",
                "otro." + System.nanoTime() + "@test.com",
                "hash"
        );
        PerfilFinanciero otroPerfil = new PerfilFinanciero(
                "Otro perfil",
                otroUsuario
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> patrimonioService.calcular(
                        otroPerfil,
                        usuario.getId(),
                        Map.of()
                )
        );
    }
}
