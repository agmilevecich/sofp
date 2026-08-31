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
import ar.com.agmilevecich.sofp.persistence.CategoriaRepository;
import ar.com.agmilevecich.sofp.persistence.CuentaRepository;
import ar.com.agmilevecich.sofp.persistence.MovimientoActivoRepository;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import ar.com.agmilevecich.sofp.persistence.PerfilFinancieroRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AislamientoDatosServiceTest {

    private EntityManager entityManager;
    private CuentaService cuentaService;
    private CategoriaService categoriaService;
    private MovimientoService movimientoService;
    private PerfilFinancieroService perfilService;
    private PosicionActivoService posicionService;
    private CarteraActivoService carteraService;

    private Usuario usuario1;
    private Usuario usuario2;
    private PerfilFinanciero perfil1;
    private PerfilFinanciero perfil2;
    private Cuenta cuenta1;
    private Cuenta cuenta2;
    private Categoria categoria1;
    private Categoria categoria2;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();
        cuentaService = new CuentaService(new CuentaRepository(entityManager),
                new MovimientoRepository(entityManager), entityManager);
        categoriaService = new CategoriaService(entityManager, new CategoriaRepository(entityManager));
        movimientoService = new MovimientoService(entityManager, new MovimientoRepository(entityManager));
        perfilService = new PerfilFinancieroService(new PerfilFinancieroRepository(entityManager));
        MovimientoActivoRepository movimientoActivoRepository = new MovimientoActivoRepository(entityManager);
        posicionService = new PosicionActivoService(movimientoActivoRepository);
        carteraService = new CarteraActivoService(movimientoActivoRepository);
        crearContexto();
    }

    @AfterEach
    void tearDown() {
        if (entityManager != null && entityManager.isOpen()) entityManager.close();
        JpaTestManager.close();
    }

    @Test
    void deberiaPermitirLeerCuentaPropiaYRechazarCuentaAjena() {
        assertTrue(cuentaService.buscarPorId(cuenta1.getId(), usuario1.getId()).isPresent());
        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.buscarPorId(cuenta2.getId(), usuario1.getId()));
    }

    @Test
    void deberiaPermitirListarCuentasDelPerfilPropioYRechazarPerfilAjeno() {
        assertEquals(1, cuentaService.listarPorPerfilFinanciero(perfil1.getId(), usuario1.getId()).size());
        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.listarPorPerfilFinanciero(perfil2.getId(), usuario1.getId()));
    }

    @Test
    void deberiaAislarSaldoYLecturaDeCategoria() {
        assertEquals(BigDecimal.ZERO, cuentaService.calcularSaldo(cuenta1.getId(), usuario1.getId()));
        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.calcularSaldo(cuenta2.getId(), usuario1.getId()));
        assertTrue(categoriaService.buscarPorId(categoria1.getId(), usuario1.getId()).isPresent());
        assertThrows(IllegalArgumentException.class,
                () -> categoriaService.buscarPorId(categoria2.getId(), usuario1.getId()));
    }

    @Test
    void deberiaAislarRegistroDeCuentaYCategoria() {
        Cuenta cuentaAjena = nuevaCuenta(perfil2);
        Categoria categoriaAjena = new Categoria("Ajena nueva", perfil2);

        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.registrar(cuentaAjena, usuario1.getId()));
        assertThrows(IllegalArgumentException.class,
                () -> categoriaService.registrar(categoriaAjena, usuario1.getId()));
    }

    @Test
    void deberiaAislarMovimientosPorIdCuentaYCategoria() {
        Movimiento movimientoAjeno = new Movimiento(
                cuenta2, categoria2, TipoMovimiento.EGRESO,
                new BigDecimal("100"), LocalDateTime.now(), "Ajeno");
        entityManager.getTransaction().begin();
        entityManager.persist(movimientoAjeno);
        entityManager.getTransaction().commit();
        entityManager.clear();

        assertThrows(IllegalArgumentException.class,
                () -> movimientoService.buscarPorId(movimientoAjeno.getId(), usuario1.getId()));
        assertThrows(IllegalArgumentException.class,
                () -> movimientoService.listarPorCuenta(cuenta2.getId(), usuario1.getId()));
        assertThrows(IllegalArgumentException.class,
                () -> movimientoService.listarPorCategoria(categoria2.getId(), usuario1.getId()));
    }

    @Test
    void deberiaImpedirCrearMovimientoConRecursosDeOtroUsuario() {
        assertThrows(IllegalArgumentException.class,
                () -> movimientoService.registrar(
                        cuenta2, categoria2, TipoMovimiento.EGRESO,
                        new BigDecimal("100"), LocalDateTime.now(), "Intento", usuario1.getId()));
    }

    @Test
    void deberiaAislarPerfilPosicionYReporteDeCartera() {
        assertTrue(perfilService.buscarPorId(perfil1.getId(), usuario1.getId()).isPresent());
        assertThrows(IllegalArgumentException.class,
                () -> perfilService.buscarPorId(perfil2.getId(), usuario1.getId()));
        assertThrows(IllegalArgumentException.class,
                () -> posicionService.obtenerPosicion(perfil2, null, usuario1.getId()));
        assertThrows(IllegalArgumentException.class,
                () -> carteraService.obtenerPosiciones(perfil2, usuario1.getId()));
    }

    private void crearContexto() {
        usuario1 = new Usuario("Usuario 1", "Test", "u1." + System.nanoTime() + "@test.com", "hash");
        usuario2 = new Usuario("Usuario 2", "Test", "u2." + System.nanoTime() + "@test.com", "hash");
        perfil1 = new PerfilFinanciero("Perfil 1", usuario1);
        perfil2 = new PerfilFinanciero("Perfil 2", usuario2);
        usuario1.agregarPerfilFinanciero(perfil1);
        usuario2.agregarPerfilFinanciero(perfil2);
        InstitucionFinanciera banco = new InstitucionFinanciera("Banco Test", TipoInstitucionFinanciera.BANCO);
        Moneda moneda = new Moneda("ARS" + (System.nanoTime() % 1000000), "Peso", 2, TipoMoneda.FIAT);
        cuenta1 = new Cuenta("Cuenta 1", TipoCuenta.CAJA_AHORRO, perfil1, banco, moneda);
        cuenta2 = new Cuenta("Cuenta 2", TipoCuenta.CAJA_AHORRO, perfil2, banco, moneda);
        categoria1 = new Categoria("Categoria 1", perfil1);
        categoria2 = new Categoria("Categoria 2", perfil2);
        entityManager.getTransaction().begin();
        entityManager.persist(usuario1);
        entityManager.persist(usuario2);
        entityManager.persist(perfil1);
        entityManager.persist(perfil2);
        entityManager.persist(banco);
        entityManager.persist(moneda);
        entityManager.persist(cuenta1);
        entityManager.persist(cuenta2);
        entityManager.persist(categoria1);
        entityManager.persist(categoria2);
        entityManager.getTransaction().commit();
        entityManager.clear();
        cuenta1 = entityManager.find(Cuenta.class, cuenta1.getId());
        cuenta2 = entityManager.find(Cuenta.class, cuenta2.getId());
        categoria1 = entityManager.find(Categoria.class, categoria1.getId());
        categoria2 = entityManager.find(Categoria.class, categoria2.getId());
        perfil1 = entityManager.find(PerfilFinanciero.class, perfil1.getId());
        perfil2 = entityManager.find(PerfilFinanciero.class, perfil2.getId());
        usuario1 = entityManager.find(Usuario.class, usuario1.getId());
        usuario2 = entityManager.find(Usuario.class, usuario2.getId());
    }

    private Cuenta nuevaCuenta(PerfilFinanciero perfil) {
        InstitucionFinanciera banco = entityManager.createQuery(
                "SELECT i FROM InstitucionFinanciera i", InstitucionFinanciera.class)
                .setMaxResults(1).getSingleResult();
        Moneda moneda = entityManager.createQuery(
                "SELECT m FROM Moneda m", Moneda.class)
                .setMaxResults(1).getSingleResult();
        return new Cuenta("Nueva", TipoCuenta.CAJA_AHORRO, perfil, banco, moneda);
    }
}
