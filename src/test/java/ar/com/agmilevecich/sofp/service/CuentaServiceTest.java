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

import static org.junit.jupiter.api.Assertions.*;

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

        BigDecimal saldo =
                cuentaService.calcularSaldo(999L);

        assertEquals(
                BigDecimal.ZERO,
                saldo
        );
    }

    @Test
    void deberiaSumarUnIngresoAlSaldo() {

        DatosCuenta datos =
                persistirCuenta("ariel.saldo.ingreso." + System.nanoTime());

        Categoria categoria =
                new Categoria("Ingresos", datos.perfil());

        Movimiento movimiento =
                new Movimiento(
                        datos.cuenta(),
                        categoria,
                        TipoMovimiento.INGRESO,
                        new BigDecimal("10000.00"),
                        LocalDateTime.now(),
                        "Ingreso de prueba"
                );

        entityManager.getTransaction().begin();
        entityManager.persist(categoria);
        entityManager.persist(movimiento);
        entityManager.getTransaction().commit();

        entityManager.clear();

        assertEquals(
                new BigDecimal("10000.00"),
                cuentaService.calcularSaldo(datos.cuentaId())
        );
    }

    @Test
    void deberiaRestarUnEgresoDelSaldo() {

        DatosCuenta datos =
                persistirCuenta("ariel.saldo.egreso." + System.nanoTime());

        Categoria categoria =
                new Categoria("Gastos", datos.perfil());

        Movimiento movimiento =
                new Movimiento(
                        datos.cuenta(),
                        categoria,
                        TipoMovimiento.EGRESO,
                        new BigDecimal("3000.00"),
                        LocalDateTime.now(),
                        "Egreso de prueba"
                );

        entityManager.getTransaction().begin();
        entityManager.persist(categoria);
        entityManager.persist(movimiento);
        entityManager.getTransaction().commit();

        entityManager.clear();

        assertEquals(
                new BigDecimal("-3000.00"),
                cuentaService.calcularSaldo(datos.cuentaId())
        );
    }

    @Test
    void deberiaCalcularSaldoConMultiplesMovimientos() {

        DatosCuenta datos =
                persistirCuenta("ariel.saldo.multiple." + System.nanoTime());

        Categoria ingresos =
                new Categoria("Ingresos", datos.perfil());
        Categoria gastos =
                new Categoria("Gastos", datos.perfil());

        Movimiento ingreso1 =
                new Movimiento(
                        datos.cuenta(), ingresos, TipoMovimiento.INGRESO,
                        new BigDecimal("10000.00"),
                        LocalDateTime.now().minusDays(2), "Primer ingreso");

        Movimiento ingreso2 =
                new Movimiento(
                        datos.cuenta(), ingresos, TipoMovimiento.INGRESO,
                        new BigDecimal("5000.00"),
                        LocalDateTime.now().minusDays(1), "Segundo ingreso");

        Movimiento egreso =
                new Movimiento(
                        datos.cuenta(), gastos, TipoMovimiento.EGRESO,
                        new BigDecimal("3000.00"),
                        LocalDateTime.now(), "Egreso");

        entityManager.getTransaction().begin();
        entityManager.persist(ingresos);
        entityManager.persist(gastos);
        entityManager.persist(ingreso1);
        entityManager.persist(ingreso2);
        entityManager.persist(egreso);
        entityManager.getTransaction().commit();

        entityManager.clear();

        assertEquals(
                new BigDecimal("12000.00"),
                cuentaService.calcularSaldo(datos.cuentaId())
        );
    }

    @Test
    void deberiaRegistrarUnaCuenta() {

        DatosCuenta datos =
                crearDatosCuenta("ariel.registrar.cuenta." + System.nanoTime());

        assertNull(datos.cuenta().getId());

        entityManager.getTransaction().begin();
        entityManager.persist(datos.usuario());
        entityManager.persist(datos.perfil());
        entityManager.persist(datos.institucion());
        entityManager.persist(datos.moneda());
        Cuenta registrada = cuentaService.registrar(datos.cuenta());
        entityManager.getTransaction().commit();

        assertNotNull(registrada.getId());
        assertEquals("Cuenta principal", registrada.getNombre());
        assertEquals(TipoCuenta.CAJA_AHORRO, registrada.getTipoCuenta());
    }

    @Test
    void deberiaBuscarCuentaPorId() {

        DatosCuenta datos =
                persistirCuenta("ariel.buscar.cuenta." + System.nanoTime());

        Optional<Cuenta> resultado =
                cuentaService.buscarPorId(datos.cuentaId());

        assertTrue(resultado.isPresent());
        assertEquals(datos.cuentaId(), resultado.get().getId());
        assertEquals("Cuenta principal", resultado.get().getNombre());
    }

    @Test
    void deberiaListarTodasLasCuentas() {

        DatosCuenta datos =
                crearDatosCuenta("ariel.listar.cuentas." + System.nanoTime());

        Cuenta cuenta1 = datos.cuenta();
        Cuenta cuenta2 = new Cuenta(
                "Cuenta B",
                TipoCuenta.CUENTA_CORRIENTE,
                datos.perfil(),
                datos.institucion(),
                datos.moneda()
        );

        entityManager.getTransaction().begin();
        entityManager.persist(datos.usuario());
        entityManager.persist(datos.perfil());
        entityManager.persist(datos.institucion());
        entityManager.persist(datos.moneda());
        cuentaService.registrar(cuenta1);
        cuentaService.registrar(cuenta2);
        entityManager.getTransaction().commit();

        List<Cuenta> cuentas = cuentaService.listarTodas();

        assertEquals(2, cuentas.size());
        assertEquals("Cuenta principal", cuentas.get(0).getNombre());
        assertEquals("Cuenta B", cuentas.get(1).getNombre());
    }

    @Test
    void deberiaListarCuentasPorPerfilFinanciero() {

        Usuario usuario = crearUsuario("ariel.perfil.cuentas." + System.nanoTime());
        PerfilFinanciero perfil1 = new PerfilFinanciero("Perfil principal", usuario);
        PerfilFinanciero perfil2 = new PerfilFinanciero("Perfil secundario", usuario);
        usuario.agregarPerfilFinanciero(perfil1);
        usuario.agregarPerfilFinanciero(perfil2);

        InstitucionFinanciera institucion = crearInstitucion();
        Moneda moneda = crearMoneda();
        Cuenta cuenta1 = new Cuenta("Cuenta principal", TipoCuenta.CAJA_AHORRO,
                perfil1, institucion, moneda);
        Cuenta cuenta2 = new Cuenta("Cuenta secundaria", TipoCuenta.CUENTA_CORRIENTE,
                perfil2, institucion, moneda);

        entityManager.getTransaction().begin();
        entityManager.persist(usuario);
        entityManager.persist(perfil1);
        entityManager.persist(perfil2);
        entityManager.persist(institucion);
        entityManager.persist(moneda);
        cuentaService.registrar(cuenta1);
        cuentaService.registrar(cuenta2);
        entityManager.getTransaction().commit();

        List<Cuenta> cuentas =
                cuentaService.listarPorPerfilFinanciero(perfil1.getId());

        assertEquals(1, cuentas.size());
        assertEquals("Cuenta principal", cuentas.get(0).getNombre());
        assertEquals(perfil1.getId(), cuentas.get(0).getPerfilFinanciero().getId());
    }

    @Test
    void deberiaModificarNombreDeCuenta() {

        DatosCuenta datos = persistirCuenta("ariel.modificar.nombre." + System.nanoTime());

        Cuenta actualizada = cuentaService.modificarNombre(
                datos.cuentaId(), datos.usuario().getId(), "Cuenta modificada");

        assertEquals("Cuenta modificada", actualizada.getNombre());
    }

    @Test
    void deberiaModificarIdentificadorExternoDeCuenta() {

        DatosCuenta datos = persistirCuenta("ariel.modificar.identificador." + System.nanoTime());

        Cuenta actualizada = cuentaService.modificarIdentificadorExterno(
                datos.cuentaId(), datos.usuario().getId(), "CBU-123456789");

        assertEquals("CBU-123456789", actualizada.getIdentificadorExterno());
    }

    @Test
    void deberiaModificarTipoDeCuenta() {

        DatosCuenta datos = persistirCuenta("ariel.modificar.tipo." + System.nanoTime());

        Cuenta actualizada = cuentaService.modificarTipoCuenta(
                datos.cuentaId(), datos.usuario().getId(), TipoCuenta.CUENTA_CORRIENTE);

        assertEquals(TipoCuenta.CUENTA_CORRIENTE, actualizada.getTipoCuenta());
    }

    @Test
    void deberiaModificarInstitucionFinancieraDeCuenta() {

        DatosCuenta datos = persistirCuenta("ariel.modificar.institucion." + System.nanoTime());
        InstitucionFinanciera nuevaInstitucion =
                new InstitucionFinanciera("Banco Nuevo", TipoInstitucionFinanciera.BANCO);

        entityManager.getTransaction().begin();
        entityManager.persist(nuevaInstitucion);
        entityManager.getTransaction().commit();

        Cuenta actualizada = cuentaService.modificarInstitucionFinanciera(
                datos.cuentaId(), datos.usuario().getId(), nuevaInstitucion);

        assertEquals(nuevaInstitucion.getId(),
                actualizada.getInstitucionFinanciera().getId());
    }

    @Test
    void deberiaModificarMonedaDeCuenta() {

        DatosCuenta datos = persistirCuenta("ariel.modificar.moneda." + System.nanoTime());
        Moneda nuevaMoneda = new Moneda(
                "USD", "Dólar estadounidense", 2, TipoMoneda.FIAT);

        entityManager.getTransaction().begin();
        entityManager.persist(nuevaMoneda);
        entityManager.getTransaction().commit();

        Cuenta actualizada = cuentaService.modificarMoneda(
                datos.cuentaId(), datos.usuario().getId(), nuevaMoneda);

        assertEquals(nuevaMoneda.getId(), actualizada.getMoneda().getId());
    }

    @Test
    void deberiaActivarCuenta() {

        DatosCuenta datos = crearDatosCuenta("ariel.activar.cuenta." + System.nanoTime());
        datos.cuenta().desactivar();
        persistirDatos(datos);

        Cuenta actualizada = cuentaService.activar(
                datos.cuentaId(), datos.usuario().getId());

        assertTrue(actualizada.isActiva());
    }

    @Test
    void deberiaDesactivarCuenta() {

        DatosCuenta datos = persistirCuenta("ariel.desactivar.cuenta." + System.nanoTime());

        Cuenta actualizada = cuentaService.desactivar(
                datos.cuentaId(), datos.usuario().getId());

        assertFalse(actualizada.isActiva());
    }

    @Test
    void deberiaEliminarCuentaExistente() {

        DatosCuenta datos = persistirCuenta("ariel.eliminar.cuenta." + System.nanoTime());
        Long cuentaId = datos.cuentaId();

        cuentaService.eliminar(cuentaId, datos.usuario().getId());

        assertTrue(cuentaService.buscarPorId(cuentaId).isEmpty());
    }

    @Test
    void deberiaPersistirLaModificacionDelNombreDeCuenta() {

        DatosCuenta datos = persistirCuenta("ariel.persistencia.nombre." + System.nanoTime());

        cuentaService.modificarNombre(
                datos.cuentaId(), datos.usuario().getId(), "Nombre persistido");

        entityManager.clear();

        Optional<Cuenta> resultado = cuentaService.buscarPorId(datos.cuentaId());

        assertTrue(resultado.isPresent());
        assertEquals("Nombre persistido", resultado.get().getNombre());
    }

    @Test
    void deberiaPersistirLaModificacionDelIdentificadorExterno() {

        DatosCuenta datos = persistirCuenta("ariel.persistencia.identificador." + System.nanoTime());

        cuentaService.modificarIdentificadorExterno(
                datos.cuentaId(), datos.usuario().getId(), "CBU-987654321");

        entityManager.clear();

        Optional<Cuenta> resultado = cuentaService.buscarPorId(datos.cuentaId());

        assertTrue(resultado.isPresent());
        assertEquals("CBU-987654321", resultado.get().getIdentificadorExterno());
    }

    @Test
    void deberiaPermitirIdentificadorExternoNulo() {

        DatosCuenta datos = persistirCuenta("ariel.identificador.nulo." + System.nanoTime());
        cuentaService.modificarIdentificadorExterno(
                datos.cuentaId(), datos.usuario().getId(), null);

        entityManager.clear();

        Optional<Cuenta> resultado = cuentaService.buscarPorId(datos.cuentaId());

        assertTrue(resultado.isPresent());
        assertNull(resultado.get().getIdentificadorExterno());
    }

    @Test
    void deberiaPersistirLaActivacionDeUnaCuenta() {

        DatosCuenta datos = crearDatosCuenta("ariel.persistencia.activacion." + System.nanoTime());
        datos.cuenta().desactivar();
        persistirDatos(datos);

        cuentaService.activar(datos.cuentaId(), datos.usuario().getId());

        entityManager.clear();

        Optional<Cuenta> resultado = cuentaService.buscarPorId(datos.cuentaId());

        assertTrue(resultado.isPresent());
        assertTrue(resultado.get().isActiva());
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeCalculaSaldoConIdNulo() {
        assertThrows(NullPointerException.class,
                () -> cuentaService.calcularSaldo(null));
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeBuscaCuentaConIdNulo() {
        assertThrows(NullPointerException.class,
                () -> cuentaService.buscarPorId(null));
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeListaPorPerfilFinancieroConIdNulo() {
        assertThrows(NullPointerException.class,
                () -> cuentaService.listarPorPerfilFinanciero(null));
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeRegistraUnaCuentaNula() {
        assertThrows(NullPointerException.class,
                () -> cuentaService.registrar(null));
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeModificaNombreConIdNulo() {
        assertThrows(NullPointerException.class,
                () -> cuentaService.modificarNombre(null, 1L, "Nueva cuenta"));
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeModificaNombreConUsuarioNulo() {
        assertThrows(NullPointerException.class,
                () -> cuentaService.modificarNombre(1L, null, "Nueva cuenta"));
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeModificaNombreConNombreNulo() {
        assertThrows(NullPointerException.class,
                () -> cuentaService.modificarNombre(999999L, 1L, null));
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeModificaNombreDeCuentaInexistente() {
        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.modificarNombre(999999L, 1L, "Nueva cuenta"));
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeModificaIdentificadorConIdNulo() {
        assertThrows(NullPointerException.class,
                () -> cuentaService.modificarIdentificadorExterno(null, 1L, "CBU-123"));
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeModificaIdentificadorConUsuarioNulo() {
        assertThrows(NullPointerException.class,
                () -> cuentaService.modificarIdentificadorExterno(1L, null, "CBU-123"));
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeModificaIdentificadorDeCuentaInexistente() {
        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.modificarIdentificadorExterno(999999L, 1L, "CBU-123"));
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeModificaTipoConIdNulo() {
        assertThrows(NullPointerException.class,
                () -> cuentaService.modificarTipoCuenta(null, 1L, TipoCuenta.CAJA_AHORRO));
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeModificaTipoConUsuarioNulo() {
        assertThrows(NullPointerException.class,
                () -> cuentaService.modificarTipoCuenta(1L, null, TipoCuenta.CAJA_AHORRO));
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeModificaTipoConTipoNulo() {
        assertThrows(NullPointerException.class,
                () -> cuentaService.modificarTipoCuenta(1L, 1L, null));
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeModificaTipoDeCuentaInexistente() {
        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.modificarTipoCuenta(999999L, 1L, TipoCuenta.CAJA_AHORRO));
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeModificaInstitucionConIdNulo() {
        assertThrows(NullPointerException.class,
                () -> cuentaService.modificarInstitucionFinanciera(null, 1L,
                        new InstitucionFinanciera("Banco Test", TipoInstitucionFinanciera.BANCO)));
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeModificaInstitucionConUsuarioNulo() {
        assertThrows(NullPointerException.class,
                () -> cuentaService.modificarInstitucionFinanciera(1L, null,
                        new InstitucionFinanciera("Banco Test", TipoInstitucionFinanciera.BANCO)));
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeModificaInstitucionConInstitucionNula() {
        assertThrows(NullPointerException.class,
                () -> cuentaService.modificarInstitucionFinanciera(1L, 1L, null));
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeModificaInstitucionDeCuentaInexistente() {
        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.modificarInstitucionFinanciera(999999L, 1L,
                        new InstitucionFinanciera("Banco Test", TipoInstitucionFinanciera.BANCO)));
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeModificaMonedaConIdNulo() {
        assertThrows(NullPointerException.class,
                () -> cuentaService.modificarMoneda(null, 1L,
                        new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT)));
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeModificaMonedaConUsuarioNulo() {
        assertThrows(NullPointerException.class,
                () -> cuentaService.modificarMoneda(1L, null,
                        new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT)));
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeModificaMonedaConMonedaNula() {
        assertThrows(NullPointerException.class,
                () -> cuentaService.modificarMoneda(1L, 1L, null));
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeModificaMonedaDeCuentaInexistente() {
        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.modificarMoneda(999999L, 1L,
                        new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT)));
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeActivaConIdNulo() {
        assertThrows(NullPointerException.class,
                () -> cuentaService.activar(null, 1L));
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeActivaConUsuarioNulo() {
        assertThrows(NullPointerException.class,
                () -> cuentaService.activar(1L, null));
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeActivaCuentaInexistente() {
        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.activar(999999L, 1L));
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeDesactivaConIdNulo() {
        assertThrows(NullPointerException.class,
                () -> cuentaService.desactivar(null, 1L));
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeDesactivaConUsuarioNulo() {
        assertThrows(NullPointerException.class,
                () -> cuentaService.desactivar(1L, null));
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeDesactivaCuentaInexistente() {
        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.desactivar(999999L, 1L));
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeEliminaConIdNulo() {
        assertThrows(NullPointerException.class,
                () -> cuentaService.eliminar(null, 1L));
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeEliminaConUsuarioNulo() {
        assertThrows(NullPointerException.class,
                () -> cuentaService.eliminar(1L, null));
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeEliminaCuentaInexistente() {
        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.eliminar(999999L, 1L));
    }

    @Test
    void deberiaDevolverListaVaciaCuandoNoExistenCuentas() {
        List<Cuenta> cuentas = cuentaService.listarTodas();
        assertNotNull(cuentas);
        assertTrue(cuentas.isEmpty());
    }

    @Test
    void deberiaDevolverListaVaciaCuandoElPerfilNoTieneCuentas() {
        Usuario usuario = crearUsuario("ariel.perfil.sin.cuentas." + System.nanoTime());
        PerfilFinanciero perfil = new PerfilFinanciero("Perfil sin cuentas", usuario);
        usuario.agregarPerfilFinanciero(perfil);

        entityManager.getTransaction().begin();
        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.getTransaction().commit();

        List<Cuenta> cuentas = cuentaService.listarPorPerfilFinanciero(perfil.getId());

        assertNotNull(cuentas);
        assertTrue(cuentas.isEmpty());
    }

    @Test
    void deberiaRechazarModificarNombreSiElUsuarioNoEsPropietario() {
        DatosCuenta datos = persistirCuenta("ariel.autorizacion.nombre." + System.nanoTime());
        Usuario otroUsuario = persistirUsuario("ariel.otro.nombre." + System.nanoTime());

        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.modificarNombre(
                        datos.cuentaId(), otroUsuario.getId(), "No autorizado"));

        assertEquals("Cuenta principal",
                cuentaService.buscarPorId(datos.cuentaId()).orElseThrow().getNombre());
    }

    @Test
    void deberiaRechazarModificarIdentificadorSiElUsuarioNoEsPropietario() {
        DatosCuenta datos = persistirCuenta("ariel.autorizacion.identificador." + System.nanoTime());
        Usuario otroUsuario = persistirUsuario("ariel.otro.identificador." + System.nanoTime());

        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.modificarIdentificadorExterno(
                        datos.cuentaId(), otroUsuario.getId(), "CBU-NO-AUTORIZADO"));
    }

    @Test
    void deberiaRechazarModificarTipoSiElUsuarioNoEsPropietario() {
        DatosCuenta datos = persistirCuenta("ariel.autorizacion.tipo." + System.nanoTime());
        Usuario otroUsuario = persistirUsuario("ariel.otro.tipo." + System.nanoTime());

        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.modificarTipoCuenta(
                        datos.cuentaId(), otroUsuario.getId(), TipoCuenta.CUENTA_CORRIENTE));
    }

    @Test
    void deberiaRechazarModificarInstitucionSiElUsuarioNoEsPropietario() {
        DatosCuenta datos = persistirCuenta("ariel.autorizacion.institucion." + System.nanoTime());
        Usuario otroUsuario = persistirUsuario("ariel.otro.institucion." + System.nanoTime());
        InstitucionFinanciera institucion = new InstitucionFinanciera(
                "Banco No Autorizado", TipoInstitucionFinanciera.BANCO);

        entityManager.getTransaction().begin();
        entityManager.persist(institucion);
        entityManager.getTransaction().commit();

        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.modificarInstitucionFinanciera(
                        datos.cuentaId(), otroUsuario.getId(), institucion));
    }

    @Test
    void deberiaRechazarModificarMonedaSiElUsuarioNoEsPropietario() {
        DatosCuenta datos = persistirCuenta("ariel.autorizacion.moneda." + System.nanoTime());
        Usuario otroUsuario = persistirUsuario("ariel.otro.moneda." + System.nanoTime());
        Moneda moneda = new Moneda("USD", "Dólar estadounidense", 2, TipoMoneda.FIAT);

        entityManager.getTransaction().begin();
        entityManager.persist(moneda);
        entityManager.getTransaction().commit();

        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.modificarMoneda(
                        datos.cuentaId(), otroUsuario.getId(), moneda));
    }

    @Test
    void deberiaRechazarActivarSiElUsuarioNoEsPropietario() {
        DatosCuenta datos = crearDatosCuenta("ariel.autorizacion.activar." + System.nanoTime());
        datos.cuenta().desactivar();
        persistirDatos(datos);
        Usuario otroUsuario = persistirUsuario("ariel.otro.activar." + System.nanoTime());

        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.activar(datos.cuentaId(), otroUsuario.getId()));

        assertFalse(cuentaService.buscarPorId(datos.cuentaId()).orElseThrow().isActiva());
    }

    @Test
    void deberiaRechazarDesactivarSiElUsuarioNoEsPropietario() {
        DatosCuenta datos = persistirCuenta("ariel.autorizacion.desactivar." + System.nanoTime());
        Usuario otroUsuario = persistirUsuario("ariel.otro.desactivar." + System.nanoTime());

        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.desactivar(datos.cuentaId(), otroUsuario.getId()));

        assertTrue(cuentaService.buscarPorId(datos.cuentaId()).orElseThrow().isActiva());
    }

    @Test
    void deberiaRechazarEliminarSiElUsuarioNoEsPropietario() {
        DatosCuenta datos = persistirCuenta("ariel.autorizacion.eliminar." + System.nanoTime());
        Usuario otroUsuario = persistirUsuario("ariel.otro.eliminar." + System.nanoTime());

        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.eliminar(datos.cuentaId(), otroUsuario.getId()));

        assertTrue(cuentaService.buscarPorId(datos.cuentaId()).isPresent());
    }

    private DatosCuenta persistirCuenta(String email) {
        DatosCuenta datos = crearDatosCuenta(email);
        persistirDatos(datos);
        return datos;
    }

    private DatosCuenta crearDatosCuenta(String email) {
        Usuario usuario = crearUsuario(email);
        PerfilFinanciero perfil =
                new PerfilFinanciero("Perfil principal", usuario);
        usuario.agregarPerfilFinanciero(perfil);

        InstitucionFinanciera institucion = crearInstitucion();
        Moneda moneda = crearMoneda();

        Cuenta cuenta = new Cuenta(
                "Cuenta principal",
                TipoCuenta.CAJA_AHORRO,
                perfil,
                institucion,
                moneda
        );

        return new DatosCuenta(usuario, perfil, institucion, moneda, cuenta);
    }

    private void persistirDatos(DatosCuenta datos) {
        entityManager.getTransaction().begin();
        entityManager.persist(datos.usuario());
        entityManager.persist(datos.perfil());
        entityManager.persist(datos.institucion());
        entityManager.persist(datos.moneda());
        cuentaService.registrar(datos.cuenta());
        entityManager.getTransaction().commit();
    }

    private Usuario persistirUsuario(String email) {
        Usuario usuario = crearUsuario(email);
        PerfilFinanciero perfil = new PerfilFinanciero("Perfil", usuario);
        usuario.agregarPerfilFinanciero(perfil);

        entityManager.getTransaction().begin();
        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.getTransaction().commit();

        return usuario;
    }

    private Usuario crearUsuario(String email) {
        return new Usuario("Ariel", "Test", email, "hash");
    }

    private InstitucionFinanciera crearInstitucion() {
        return new InstitucionFinanciera(
                "Banco Test",
                TipoInstitucionFinanciera.BANCO
        );
    }

    private Moneda crearMoneda() {
        return new Moneda(
                "ARS",
                "Peso argentino",
                2,
                TipoMoneda.FIAT
        );
    }

    private record DatosCuenta(
            Usuario usuario,
            PerfilFinanciero perfil,
            InstitucionFinanciera institucion,
            Moneda moneda,
            Cuenta cuenta) {

        private Long cuentaId() {
            return cuenta.getId();
        }
    }
}
