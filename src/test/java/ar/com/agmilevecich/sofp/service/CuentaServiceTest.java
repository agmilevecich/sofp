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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CuentaServiceTest {

    private EntityManager entityManager;
    private CuentaRepository cuentaRepository;
    private MovimientoRepository movimientoRepository;
    private CuentaService cuentaService;

    @BeforeEach
    void setUp() {

        entityManager =
                JpaTestManager.createEntityManager();

        cuentaRepository =
                new CuentaRepository(
                        entityManager
                );

        movimientoRepository =
                new MovimientoRepository(
                        entityManager
                );

        cuentaService =
                new CuentaService(
                        cuentaRepository,
                        movimientoRepository,
                        entityManager
                );
    }

    @AfterEach
    void tearDown() {

        if (entityManager != null
                && entityManager.isOpen()) {

            entityManager.close();
        }

        JpaTestManager.close();
    }

    @Test
    void deberiaDevolverCeroCuandoLaCuentaNoTieneMovimientos() {

        Long cuentaId = 999L;

        BigDecimal saldo =
                cuentaService.calcularSaldo(
                        cuentaId
                );

        assertEquals(
                BigDecimal.ZERO,
                saldo
        );
    }

    @Test
    void deberiaSumarUnIngresoAlSaldo() {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Test",
                        "ariel.test@example.com",
                        "hash"
                );

        PerfilFinanciero perfil =
                new PerfilFinanciero(
                        "Perfil principal",
                        usuario
                );

        usuario.agregarPerfilFinanciero(
                perfil
        );

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Banco Test",
                        TipoInstitucionFinanciera.BANCO
                );

        Moneda moneda =
                new Moneda(
                        "ARS",
                        "Peso argentino",
                        2,
                        TipoMoneda.FIAT
                );

        Cuenta cuenta =
                new Cuenta(
                        "Cuenta de prueba",
                        TipoCuenta.CAJA_AHORRO,
                        perfil,
                        institucion,
                        moneda
                );

        Categoria categoria =
                new Categoria(
                        "Ingresos",
                        perfil
                );

        Movimiento movimiento =
                new Movimiento(
                        cuenta,
                        categoria,
                        TipoMovimiento.INGRESO,
                        new BigDecimal("10000.00"),
                        LocalDateTime.now(),
                        "Ingreso de prueba"
                );

        entityManager.getTransaction().begin();

        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(institucion);
        entityManager.persist(moneda);
        entityManager.persist(cuenta);
        entityManager.persist(categoria);
        entityManager.persist(movimiento);

        entityManager.getTransaction().commit();

        entityManager.clear();

        BigDecimal saldo =
                cuentaService.calcularSaldo(
                        cuenta.getId()
                );

        assertEquals(
                new BigDecimal("10000.00"),
                saldo
        );
    }

    @Test
    void deberiaRestarUnEgresoDelSaldo() {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Test",
                        "ariel.egreso@example.com",
                        "hash"
                );

        PerfilFinanciero perfil =
                new PerfilFinanciero(
                        "Perfil principal",
                        usuario
                );

        usuario.agregarPerfilFinanciero(
                perfil
        );

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Banco Test",
                        TipoInstitucionFinanciera.BANCO
                );

        Moneda moneda =
                new Moneda(
                        "ARS",
                        "Peso argentino",
                        2,
                        TipoMoneda.FIAT
                );

        Cuenta cuenta =
                new Cuenta(
                        "Cuenta de prueba",
                        TipoCuenta.CAJA_AHORRO,
                        perfil,
                        institucion,
                        moneda
                );

        Categoria categoria =
                new Categoria(
                        "Gastos",
                        perfil
                );

        Movimiento movimiento =
                new Movimiento(
                        cuenta,
                        categoria,
                        TipoMovimiento.EGRESO,
                        new BigDecimal("3000.00"),
                        LocalDateTime.now(),
                        "Egreso de prueba"
                );

        entityManager.getTransaction().begin();

        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(institucion);
        entityManager.persist(moneda);
        entityManager.persist(cuenta);
        entityManager.persist(categoria);
        entityManager.persist(movimiento);

        entityManager.getTransaction().commit();

        entityManager.clear();

        BigDecimal saldo =
                cuentaService.calcularSaldo(
                        cuenta.getId()
                );

        assertEquals(
                new BigDecimal("-3000.00"),
                saldo
        );
    }

    @Test
    void deberiaCalcularSaldoConMultiplesMovimientos() {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Test",
                        "ariel.multiple@example.com",
                        "hash"
                );

        PerfilFinanciero perfil =
                new PerfilFinanciero(
                        "Perfil principal",
                        usuario
                );

        usuario.agregarPerfilFinanciero(
                perfil
        );

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Banco Test",
                        TipoInstitucionFinanciera.BANCO
                );

        Moneda moneda =
                new Moneda(
                        "ARS",
                        "Peso argentino",
                        2,
                        TipoMoneda.FIAT
                );

        Cuenta cuenta =
                new Cuenta(
                        "Cuenta de prueba",
                        TipoCuenta.CAJA_AHORRO,
                        perfil,
                        institucion,
                        moneda
                );

        Categoria categoriaIngresos =
                new Categoria(
                        "Ingresos",
                        perfil
                );

        Categoria categoriaGastos =
                new Categoria(
                        "Gastos",
                        perfil
                );

        Movimiento ingreso1 =
                new Movimiento(
                        cuenta,
                        categoriaIngresos,
                        TipoMovimiento.INGRESO,
                        new BigDecimal("10000.00"),
                        LocalDateTime.now().minusDays(2),
                        "Primer ingreso"
                );

        Movimiento ingreso2 =
                new Movimiento(
                        cuenta,
                        categoriaIngresos,
                        TipoMovimiento.INGRESO,
                        new BigDecimal("5000.00"),
                        LocalDateTime.now().minusDays(1),
                        "Segundo ingreso"
                );

        Movimiento egreso =
                new Movimiento(
                        cuenta,
                        categoriaGastos,
                        TipoMovimiento.EGRESO,
                        new BigDecimal("3000.00"),
                        LocalDateTime.now(),
                        "Egreso"
                );

        entityManager.getTransaction().begin();

        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(institucion);
        entityManager.persist(moneda);
        entityManager.persist(cuenta);
        entityManager.persist(categoriaIngresos);
        entityManager.persist(categoriaGastos);
        entityManager.persist(ingreso1);
        entityManager.persist(ingreso2);
        entityManager.persist(egreso);

        entityManager.getTransaction().commit();

        entityManager.clear();

        BigDecimal saldo =
                cuentaService.calcularSaldo(
                        cuenta.getId()
                );

        assertEquals(
                new BigDecimal("12000.00"),
                saldo
        );
    }

    @Test
    void deberiaRegistrarUnaCuenta() {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Test",
                        "ariel.registrar.cuenta."
                                + System.nanoTime()
                                + "@test.com",
                        "hash"
                );

        PerfilFinanciero perfil =
                new PerfilFinanciero(
                        "Perfil principal",
                        usuario
                );

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Banco Test",
                        TipoInstitucionFinanciera.BANCO
                );

        Moneda moneda =
                new Moneda(
                        "ARS",
                        "Peso argentino",
                        2,
                        TipoMoneda.FIAT
                );

        Cuenta cuenta =
                new Cuenta(
                        "Cuenta principal",
                        TipoCuenta.CAJA_AHORRO,
                        perfil,
                        institucion,
                        moneda
                );

        entityManager.getTransaction().begin();

        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(institucion);
        entityManager.persist(moneda);

        Cuenta registrada =
                cuentaService.registrar(
                        cuenta
                );

        entityManager.getTransaction().commit();

        assertTrue(
                registrada.getId() != null
        );

        assertEquals(
                "Cuenta principal",
                registrada.getNombre()
        );

        assertEquals(
                TipoCuenta.CAJA_AHORRO,
                registrada.getTipoCuenta()
        );
    }

    @Test
    void deberiaBuscarCuentaPorId() {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Test",
                        "ariel.buscar.cuenta."
                                + System.nanoTime()
                                + "@test.com",
                        "hash"
                );

        PerfilFinanciero perfil =
                new PerfilFinanciero(
                        "Perfil principal",
                        usuario
                );

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Banco Test",
                        TipoInstitucionFinanciera.BANCO
                );

        Moneda moneda =
                new Moneda(
                        "ARS",
                        "Peso argentino",
                        2,
                        TipoMoneda.FIAT
                );

        Cuenta cuenta =
                new Cuenta(
                        "Cuenta principal",
                        TipoCuenta.CAJA_AHORRO,
                        perfil,
                        institucion,
                        moneda
                );

        entityManager.getTransaction().begin();

        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(institucion);
        entityManager.persist(moneda);

        cuentaService.registrar(
                cuenta
        );

        entityManager.getTransaction().commit();

        Optional<Cuenta> resultado =
                cuentaService.buscarPorId(
                        cuenta.getId()
                );

        assertTrue(
                resultado.isPresent()
        );

        assertEquals(
                cuenta.getId(),
                resultado.get().getId()
        );

        assertEquals(
                "Cuenta principal",
                resultado.get().getNombre()
        );
    }

    @Test
    void deberiaListarTodasLasCuentas() {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Test",
                        "ariel.listar.cuentas."
                                + System.nanoTime()
                                + "@test.com",
                        "hash"
                );

        PerfilFinanciero perfil =
                new PerfilFinanciero(
                        "Perfil principal",
                        usuario
                );

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Banco Test",
                        TipoInstitucionFinanciera.BANCO
                );

        Moneda moneda =
                new Moneda(
                        "ARS",
                        "Peso argentino",
                        2,
                        TipoMoneda.FIAT
                );

        Cuenta cuenta1 =
                new Cuenta(
                        "Cuenta A",
                        TipoCuenta.CAJA_AHORRO,
                        perfil,
                        institucion,
                        moneda
                );

        Cuenta cuenta2 =
                new Cuenta(
                        "Cuenta B",
                        TipoCuenta.CUENTA_CORRIENTE,
                        perfil,
                        institucion,
                        moneda
                );

        entityManager.getTransaction().begin();

        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(institucion);
        entityManager.persist(moneda);

        cuentaService.registrar(
                cuenta1
        );

        cuentaService.registrar(
                cuenta2
        );

        entityManager.getTransaction().commit();

        List<Cuenta> cuentas =
                cuentaService.listarTodas();

        assertEquals(
                2,
                cuentas.size()
        );

        assertEquals(
                "Cuenta A",
                cuentas.get(0).getNombre()
        );

        assertEquals(
                "Cuenta B",
                cuentas.get(1).getNombre()
        );
    }

    @Test
    void deberiaListarCuentasPorPerfilFinanciero() {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Test",
                        "ariel.perfil.cuentas."
                                + System.nanoTime()
                                + "@test.com",
                        "hash"
                );

        PerfilFinanciero perfil1 =
                new PerfilFinanciero(
                        "Perfil principal",
                        usuario
                );

        PerfilFinanciero perfil2 =
                new PerfilFinanciero(
                        "Perfil secundario",
                        usuario
                );

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Banco Test",
                        TipoInstitucionFinanciera.BANCO
                );

        Moneda moneda =
                new Moneda(
                        "ARS",
                        "Peso argentino",
                        2,
                        TipoMoneda.FIAT
                );

        Cuenta cuentaPerfil1 =
                new Cuenta(
                        "Cuenta principal",
                        TipoCuenta.CAJA_AHORRO,
                        perfil1,
                        institucion,
                        moneda
                );

        Cuenta cuentaPerfil2 =
                new Cuenta(
                        "Cuenta secundaria",
                        TipoCuenta.CUENTA_CORRIENTE,
                        perfil2,
                        institucion,
                        moneda
                );

        entityManager.getTransaction().begin();

        entityManager.persist(usuario);
        entityManager.persist(perfil1);
        entityManager.persist(perfil2);
        entityManager.persist(institucion);
        entityManager.persist(moneda);

        cuentaService.registrar(
                cuentaPerfil1
        );

        cuentaService.registrar(
                cuentaPerfil2
        );

        entityManager.getTransaction().commit();

        List<Cuenta> cuentas =
                cuentaService.listarPorPerfilFinanciero(
                        perfil1.getId()
                );

        assertEquals(
                1,
                cuentas.size()
        );

        assertEquals(
                "Cuenta principal",
                cuentas.get(0).getNombre()
        );

        assertEquals(
                perfil1.getId(),
                cuentas.get(0)
                        .getPerfilFinanciero()
                        .getId()
        );
    }

    @Test
    void deberiaModificarNombreDeCuenta() {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Test",
                        "ariel.modificar.nombre."
                                + System.nanoTime()
                                + "@test.com",
                        "hash"
                );

        PerfilFinanciero perfil =
                new PerfilFinanciero(
                        "Perfil principal",
                        usuario
                );

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Banco Test",
                        TipoInstitucionFinanciera.BANCO
                );

        Moneda moneda =
                new Moneda(
                        "ARS",
                        "Peso argentino",
                        2,
                        TipoMoneda.FIAT
                );

        Cuenta cuenta =
                new Cuenta(
                        "Cuenta original",
                        TipoCuenta.CAJA_AHORRO,
                        perfil,
                        institucion,
                        moneda
                );

        entityManager.getTransaction().begin();

        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(institucion);
        entityManager.persist(moneda);
        cuentaService.registrar(cuenta);

        entityManager.getTransaction().commit();

        Cuenta actualizada =
                cuentaService.modificarNombre(
                        cuenta.getId(),
                        "Cuenta modificada"
                );

        assertEquals(
                "Cuenta modificada",
                actualizada.getNombre()
        );
    }

    @Test
    void deberiaModificarIdentificadorExternoDeCuenta() {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Test",
                        "ariel.modificar.identificador."
                                + System.nanoTime()
                                + "@test.com",
                        "hash"
                );

        PerfilFinanciero perfil =
                new PerfilFinanciero(
                        "Perfil principal",
                        usuario
                );

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Banco Test",
                        TipoInstitucionFinanciera.BANCO
                );

        Moneda moneda =
                new Moneda(
                        "ARS",
                        "Peso argentino",
                        2,
                        TipoMoneda.FIAT
                );

        Cuenta cuenta =
                new Cuenta(
                        "Cuenta de prueba",
                        TipoCuenta.CAJA_AHORRO,
                        perfil,
                        institucion,
                        moneda
                );

        entityManager.getTransaction().begin();

        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(institucion);
        entityManager.persist(moneda);
        cuentaService.registrar(cuenta);

        entityManager.getTransaction().commit();

        Cuenta actualizada =
                cuentaService.modificarIdentificadorExterno(
                        cuenta.getId(),
                        "CBU-123456789"
                );

        assertEquals(
                "CBU-123456789",
                actualizada.getIdentificadorExterno()
        );
    }

    @Test
    void deberiaModificarTipoDeCuenta() {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Test",
                        "ariel.modificar.tipo."
                                + System.nanoTime()
                                + "@test.com",
                        "hash"
                );

        PerfilFinanciero perfil =
                new PerfilFinanciero(
                        "Perfil principal",
                        usuario
                );

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Banco Test",
                        TipoInstitucionFinanciera.BANCO
                );

        Moneda moneda =
                new Moneda(
                        "ARS",
                        "Peso argentino",
                        2,
                        TipoMoneda.FIAT
                );

        Cuenta cuenta =
                new Cuenta(
                        "Cuenta de prueba",
                        TipoCuenta.CAJA_AHORRO,
                        perfil,
                        institucion,
                        moneda
                );

        entityManager.getTransaction().begin();

        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(institucion);
        entityManager.persist(moneda);
        cuentaService.registrar(cuenta);

        entityManager.getTransaction().commit();

        Cuenta actualizada =
                cuentaService.modificarTipoCuenta(
                        cuenta.getId(),
                        TipoCuenta.CUENTA_CORRIENTE
                );

        assertEquals(
                TipoCuenta.CUENTA_CORRIENTE,
                actualizada.getTipoCuenta()
        );
    }

    @Test
    void deberiaModificarInstitucionFinancieraDeCuenta() {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Test",
                        "ariel.modificar.institucion."
                                + System.nanoTime()
                                + "@test.com",
                        "hash"
                );

        PerfilFinanciero perfil =
                new PerfilFinanciero(
                        "Perfil principal",
                        usuario
                );

        InstitucionFinanciera institucionOriginal =
                new InstitucionFinanciera(
                        "Banco Original",
                        TipoInstitucionFinanciera.BANCO
                );

        InstitucionFinanciera nuevaInstitucion =
                new InstitucionFinanciera(
                        "Banco Nuevo",
                        TipoInstitucionFinanciera.BANCO
                );

        Moneda moneda =
                new Moneda(
                        "ARS",
                        "Peso argentino",
                        2,
                        TipoMoneda.FIAT
                );

        Cuenta cuenta =
                new Cuenta(
                        "Cuenta de prueba",
                        TipoCuenta.CAJA_AHORRO,
                        perfil,
                        institucionOriginal,
                        moneda
                );

        entityManager.getTransaction().begin();

        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(institucionOriginal);
        entityManager.persist(nuevaInstitucion);
        entityManager.persist(moneda);
        cuentaService.registrar(cuenta);

        entityManager.getTransaction().commit();

        Cuenta actualizada =
                cuentaService.modificarInstitucionFinanciera(
                        cuenta.getId(),
                        nuevaInstitucion
                );

        assertEquals(
                nuevaInstitucion.getId(),
                actualizada
                        .getInstitucionFinanciera()
                        .getId()
        );
    }

    @Test
    void deberiaModificarMonedaDeCuenta() {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Test",
                        "ariel.modificar.moneda."
                                + System.nanoTime()
                                + "@test.com",
                        "hash"
                );

        PerfilFinanciero perfil =
                new PerfilFinanciero(
                        "Perfil principal",
                        usuario
                );

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Banco Test",
                        TipoInstitucionFinanciera.BANCO
                );

        Moneda monedaOriginal =
                new Moneda(
                        "ARS",
                        "Peso argentino",
                        2,
                        TipoMoneda.FIAT
                );

        Moneda nuevaMoneda =
                new Moneda(
                        "USD",
                        "Dólar estadounidense",
                        2,
                        TipoMoneda.FIAT
                );

        Cuenta cuenta =
                new Cuenta(
                        "Cuenta de prueba",
                        TipoCuenta.CAJA_AHORRO,
                        perfil,
                        institucion,
                        monedaOriginal
                );

        entityManager.getTransaction().begin();

        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(institucion);
        entityManager.persist(monedaOriginal);
        entityManager.persist(nuevaMoneda);
        cuentaService.registrar(cuenta);

        entityManager.getTransaction().commit();

        Cuenta actualizada =
                cuentaService.modificarMoneda(
                        cuenta.getId(),
                        nuevaMoneda
                );

        assertEquals(
                nuevaMoneda.getId(),
                actualizada.getMoneda().getId()
        );
    }

    @Test
    void deberiaActivarCuenta() {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Test",
                        "ariel.activar.cuenta."
                                + System.nanoTime()
                                + "@test.com",
                        "hash"
                );

        PerfilFinanciero perfil =
                new PerfilFinanciero(
                        "Perfil principal",
                        usuario
                );

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Banco Test",
                        TipoInstitucionFinanciera.BANCO
                );

        Moneda moneda =
                new Moneda(
                        "ARS",
                        "Peso argentino",
                        2,
                        TipoMoneda.FIAT
                );

        Cuenta cuenta =
                new Cuenta(
                        "Cuenta de prueba",
                        TipoCuenta.CAJA_AHORRO,
                        perfil,
                        institucion,
                        moneda
                );

        cuenta.desactivar();

        entityManager.getTransaction().begin();

        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(institucion);
        entityManager.persist(moneda);
        cuentaService.registrar(cuenta);

        entityManager.getTransaction().commit();

        Cuenta actualizada =
                cuentaService.activar(
                        cuenta.getId()
                );

        assertTrue(
                actualizada.isActiva()
        );
    }

    @Test
    void deberiaDesactivarCuenta() {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Test",
                        "ariel.desactivar.cuenta."
                                + System.nanoTime()
                                + "@test.com",
                        "hash"
                );

        PerfilFinanciero perfil =
                new PerfilFinanciero(
                        "Perfil principal",
                        usuario
                );

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Banco Test",
                        TipoInstitucionFinanciera.BANCO
                );

        Moneda moneda =
                new Moneda(
                        "ARS",
                        "Peso argentino",
                        2,
                        TipoMoneda.FIAT
                );

        Cuenta cuenta =
                new Cuenta(
                        "Cuenta de prueba",
                        TipoCuenta.CAJA_AHORRO,
                        perfil,
                        institucion,
                        moneda
                );

        entityManager.getTransaction().begin();

        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(institucion);
        entityManager.persist(moneda);
        cuentaService.registrar(cuenta);

        entityManager.getTransaction().commit();

        Cuenta actualizada =
                cuentaService.desactivar(
                        cuenta.getId()
                );

        assertTrue(
                !actualizada.isActiva()
        );
    }
}