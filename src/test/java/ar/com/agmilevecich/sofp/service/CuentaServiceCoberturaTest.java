package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.TipoCuenta;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.CuentaRepository;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CuentaServiceCoberturaTest {

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
    void deberiaPermitirIdentificadorExternoNulo() {
        Datos datos = persistirCuenta("identificador-nulo");

        Cuenta actualizada = cuentaService.modificarIdentificadorExterno(
                datos.cuenta().getId(), datos.usuario().getId(), null);

        assertNull(actualizada.getIdentificadorExterno());
    }

    @Test
    void deberiaPersistirIdentificadorExterno() {
        Datos datos = persistirCuenta("identificador-persistente");

        cuentaService.modificarIdentificadorExterno(
                datos.cuenta().getId(), datos.usuario().getId(), "CBU-123");

        entityManager.clear();

        assertEquals(
                "CBU-123",
                cuentaService.buscarPorId(datos.cuenta().getId(), datos.usuario().getId())
                        .orElseThrow().getIdentificadorExterno());
    }

    @Test
    void deberiaPersistirInstitucionFinanciera() {
        Datos datos = persistirCuenta("institucion-persistente");
        InstitucionFinanciera nueva = new InstitucionFinanciera(
                "Banco Nuevo", TipoInstitucionFinanciera.BANCO);
        persistir(nueva);

        cuentaService.modificarInstitucionFinanciera(
                datos.cuenta().getId(), datos.usuario().getId(), nueva);

        entityManager.clear();

        assertEquals(
                nueva.getId(),
                cuentaService.buscarPorId(datos.cuenta().getId(), datos.usuario().getId())
                        .orElseThrow().getInstitucionFinanciera().getId());
    }

    @Test
    void deberiaPersistirDesactivacion() {
        Datos datos = persistirCuenta("desactivacion-persistente");

        cuentaService.desactivar(datos.cuenta().getId(), datos.usuario().getId());

        entityManager.clear();

        assertFalse(cuentaService.buscarPorId(datos.cuenta().getId(), datos.usuario().getId())
                .orElseThrow().isActiva());
    }

    @Test
    void deberiaPersistirActivacion() {
        Datos datos = persistirCuenta("activacion-persistente");
        cuentaService.desactivar(datos.cuenta().getId(), datos.usuario().getId());

        cuentaService.activar(datos.cuenta().getId(), datos.usuario().getId());

        entityManager.clear();

        assertTrue(cuentaService.buscarPorId(datos.cuenta().getId(), datos.usuario().getId())
                .orElseThrow().isActiva());
    }

    @Test
    void deberiaEliminarCuentaExistente() {
        Datos datos = persistirCuenta("eliminar-existente");
        Long cuentaId = datos.cuenta().getId();

        cuentaService.eliminar(cuentaId, datos.usuario().getId());

        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.buscarPorId(cuentaId, datos.usuario().getId()));
    }

    @Test
    void deberiaDevolverListaVaciaCuandoElPerfilNoTieneCuentas() {
        Datos datos = persistirBase("perfil-vacio");

        List<Cuenta> cuentas = cuentaService.listarPorPerfilFinanciero(
                datos.perfil().getId(), datos.usuario().getId());

        assertTrue(cuentas.isEmpty());
    }

    @Test
    void deberiaRechazarRegistrarCuentaConUsuarioNulo() {
        Datos datos = persistirBase("registrar-usuario-nulo");
        Cuenta cuenta = nuevaCuenta(datos, "Cuenta nueva");

        assertThrows(NullPointerException.class,
                () -> cuentaService.registrar(cuenta, null));
    }

    @Test
    void deberiaRechazarRegistrarCuentaNula() {
        Datos datos = persistirBase("registrar-cuenta-nula");

        assertThrows(NullPointerException.class,
                () -> cuentaService.registrar(null, datos.usuario().getId()));
    }

    @Test
    void deberiaRechazarBuscarConIdNulo() {
        Datos datos = persistirBase("buscar-id-nulo");

        assertThrows(NullPointerException.class,
                () -> cuentaService.buscarPorId(null, datos.usuario().getId()));
    }

    @Test
    void deberiaRechazarBuscarConUsuarioNulo() {
        Datos datos = persistirCuenta("buscar-usuario-nulo");

        assertThrows(NullPointerException.class,
                () -> cuentaService.buscarPorId(datos.cuenta().getId(), null));
    }

    @Test
    void deberiaRechazarListarPerfilConIdNulo() {
        Datos datos = persistirBase("listar-perfil-id-nulo");

        assertThrows(NullPointerException.class,
                () -> cuentaService.listarPorPerfilFinanciero(null, datos.usuario().getId()));
    }

    @Test
    void deberiaRechazarListarPerfilConUsuarioNulo() {
        Datos datos = persistirBase("listar-perfil-usuario-nulo");

        assertThrows(NullPointerException.class,
                () -> cuentaService.listarPorPerfilFinanciero(datos.perfil().getId(), null));
    }

    @Test
    void deberiaRechazarModificarNombreConIdNulo() {
        Datos datos = persistirBase("nombre-id-nulo");

        assertThrows(NullPointerException.class,
                () -> cuentaService.modificarNombre(null, datos.usuario().getId(), "Nuevo"));
    }

    @Test
    void deberiaRechazarModificarNombreConUsuarioNulo() {
        Datos datos = persistirCuenta("nombre-usuario-nulo");

        assertThrows(NullPointerException.class,
                () -> cuentaService.modificarNombre(datos.cuenta().getId(), null, "Nuevo"));
    }

    @Test
    void deberiaRechazarModificarNombreConNombreNulo() {
        Datos datos = persistirCuenta("nombre-nulo");

        assertThrows(NullPointerException.class,
                () -> cuentaService.modificarNombre(datos.cuenta().getId(), datos.usuario().getId(), null));
    }

    @Test
    void deberiaRechazarModificarIdentificadorConIdNulo() {
        Datos datos = persistirBase("identificador-id-nulo");

        assertThrows(NullPointerException.class,
                () -> cuentaService.modificarIdentificadorExterno(null, datos.usuario().getId(), "CBU"));
    }

    @Test
    void deberiaRechazarModificarIdentificadorConUsuarioNulo() {
        Datos datos = persistirCuenta("identificador-usuario-nulo");

        assertThrows(NullPointerException.class,
                () -> cuentaService.modificarIdentificadorExterno(datos.cuenta().getId(), null, "CBU"));
    }

    @Test
    void deberiaRechazarModificarTipoConIdNulo() {
        Datos datos = persistirBase("tipo-id-nulo");

        assertThrows(NullPointerException.class,
                () -> cuentaService.modificarTipoCuenta(null, datos.usuario().getId(), TipoCuenta.CAJA_AHORRO));
    }

    @Test
    void deberiaRechazarModificarTipoConUsuarioNulo() {
        Datos datos = persistirCuenta("tipo-usuario-nulo");

        assertThrows(NullPointerException.class,
                () -> cuentaService.modificarTipoCuenta(datos.cuenta().getId(), null, TipoCuenta.CAJA_AHORRO));
    }

    @Test
    void deberiaRechazarModificarTipoConTipoNulo() {
        Datos datos = persistirCuenta("tipo-nulo");

        assertThrows(NullPointerException.class,
                () -> cuentaService.modificarTipoCuenta(datos.cuenta().getId(), datos.usuario().getId(), null));
    }

    @Test
    void deberiaRechazarModificarInstitucionConIdNulo() {
        Datos datos = persistirBase("institucion-id-nulo");
        InstitucionFinanciera institucion = new InstitucionFinanciera(
                "Banco Nuevo", TipoInstitucionFinanciera.BANCO);
        persistir(institucion);

        assertThrows(NullPointerException.class,
                () -> cuentaService.modificarInstitucionFinanciera(null, datos.usuario().getId(), institucion));
    }

    @Test
    void deberiaRechazarModificarInstitucionConUsuarioNulo() {
        Datos datos = persistirCuenta("institucion-usuario-nulo");
        InstitucionFinanciera institucion = new InstitucionFinanciera(
                "Banco Nuevo", TipoInstitucionFinanciera.BANCO);
        persistir(institucion);

        assertThrows(NullPointerException.class,
                () -> cuentaService.modificarInstitucionFinanciera(datos.cuenta().getId(), null, institucion));
    }

    @Test
    void deberiaRechazarModificarInstitucionConInstitucionNula() {
        Datos datos = persistirCuenta("institucion-nula");

        assertThrows(NullPointerException.class,
                () -> cuentaService.modificarInstitucionFinanciera(
                        datos.cuenta().getId(), datos.usuario().getId(), null));
    }

    @Test
    void deberiaRechazarModificarMonedaConIdNulo() {
        Datos datos = persistirBase("moneda-id-nulo");
        Moneda moneda = nuevaMoneda("USD");
        persistir(moneda);

        assertThrows(NullPointerException.class,
                () -> cuentaService.modificarMoneda(null, datos.usuario().getId(), moneda));
    }

    @Test
    void deberiaRechazarModificarMonedaConUsuarioNulo() {
        Datos datos = persistirCuenta("moneda-usuario-nulo");
        Moneda moneda = nuevaMoneda("USD");
        persistir(moneda);

        assertThrows(NullPointerException.class,
                () -> cuentaService.modificarMoneda(datos.cuenta().getId(), null, moneda));
    }

    @Test
    void deberiaRechazarModificarMonedaConMonedaNula() {
        Datos datos = persistirCuenta("moneda-nula");

        assertThrows(NullPointerException.class,
                () -> cuentaService.modificarMoneda(
                        datos.cuenta().getId(), datos.usuario().getId(), null));
    }

    @Test
    void deberiaRechazarActivarConIdNulo() {
        Datos datos = persistirBase("activar-id-nulo");

        assertThrows(NullPointerException.class,
                () -> cuentaService.activar(null, datos.usuario().getId()));
    }

    @Test
    void deberiaRechazarActivarConUsuarioNulo() {
        Datos datos = persistirCuenta("activar-usuario-nulo");

        assertThrows(NullPointerException.class,
                () -> cuentaService.activar(datos.cuenta().getId(), null));
    }

    @Test
    void deberiaRechazarDesactivarConIdNulo() {
        Datos datos = persistirBase("desactivar-id-nulo");

        assertThrows(NullPointerException.class,
                () -> cuentaService.desactivar(null, datos.usuario().getId()));
    }

    @Test
    void deberiaRechazarDesactivarConUsuarioNulo() {
        Datos datos = persistirCuenta("desactivar-usuario-nulo");

        assertThrows(NullPointerException.class,
                () -> cuentaService.desactivar(datos.cuenta().getId(), null));
    }

    @Test
    void deberiaRechazarEliminarConIdNulo() {
        Datos datos = persistirBase("eliminar-id-nulo");

        assertThrows(NullPointerException.class,
                () -> cuentaService.eliminar(null, datos.usuario().getId()));
    }

    @Test
    void deberiaRechazarEliminarConUsuarioNulo() {
        Datos datos = persistirCuenta("eliminar-usuario-nulo");

        assertThrows(NullPointerException.class,
                () -> cuentaService.eliminar(datos.cuenta().getId(), null));
    }

    @Test
    void deberiaRechazarOperacionesDeOtraCuenta() {
        Datos propietario = persistirCuenta("propietario");
        Datos otro = persistirCuenta("otro", propietario.moneda());

        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.buscarPorId(propietario.cuenta().getId(), otro.usuario().getId()));
        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.modificarNombre(propietario.cuenta().getId(), otro.usuario().getId(), "Nuevo"));
        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.modificarIdentificadorExterno(
                        propietario.cuenta().getId(), otro.usuario().getId(), "CBU"));
        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.modificarTipoCuenta(
                        propietario.cuenta().getId(), otro.usuario().getId(), TipoCuenta.CUENTA_CORRIENTE));
        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.modificarInstitucionFinanciera(
                        propietario.cuenta().getId(), otro.usuario().getId(), propietario.cuenta().getInstitucionFinanciera()));
        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.modificarMoneda(
                        propietario.cuenta().getId(), otro.usuario().getId(), propietario.cuenta().getMoneda()));
        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.activar(propietario.cuenta().getId(), otro.usuario().getId()));
        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.desactivar(propietario.cuenta().getId(), otro.usuario().getId()));
        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.eliminar(propietario.cuenta().getId(), otro.usuario().getId()));
    }

    @Test
    void deberiaRechazarOperacionesSobreCuentaInexistente() {
        Datos datos = persistirBase("inexistente");
        Long cuentaId = 999L;

        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.buscarPorId(cuentaId, datos.usuario().getId()));
        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.modificarNombre(cuentaId, datos.usuario().getId(), "Nuevo"));
        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.modificarIdentificadorExterno(cuentaId, datos.usuario().getId(), "CBU"));
        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.modificarTipoCuenta(cuentaId, datos.usuario().getId(), TipoCuenta.CUENTA_CORRIENTE));
        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.modificarInstitucionFinanciera(
                        cuentaId, datos.usuario().getId(), datos.institucion()));
        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.modificarMoneda(cuentaId, datos.usuario().getId(), datos.moneda()));
        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.activar(cuentaId, datos.usuario().getId()));
        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.desactivar(cuentaId, datos.usuario().getId()));
        assertThrows(IllegalArgumentException.class,
                () -> cuentaService.eliminar(cuentaId, datos.usuario().getId()));
    }

    private Datos persistirCuenta(String email) {
        Datos datos = persistirBase(email);
        Cuenta cuenta = nuevaCuenta(datos, "Cuenta test");
        persistir(cuenta);
        return new Datos(datos.usuario(), datos.perfil(), datos.institucion(), datos.moneda(), cuenta);
    }

    private Datos persistirBase(String email) {
        Usuario usuario = new Usuario("Ariel", "Test", email, "clave-segura");
        persistir(usuario);

        PerfilFinanciero perfil = new PerfilFinanciero("Perfil test", usuario);
        persistir(perfil);

        InstitucionFinanciera institucion = new InstitucionFinanciera(
                "Banco Test", TipoInstitucionFinanciera.BANCO);
        persistir(institucion);

        Moneda moneda = nuevaMoneda("ARS");
        persistir(moneda);

        return new Datos(usuario, perfil, institucion, moneda, null);
    }

    private Cuenta nuevaCuenta(Datos datos, String nombre) {
        return new Cuenta(
                nombre,
                TipoCuenta.CAJA_AHORRO,
                datos.perfil(),
                datos.institucion(),
                datos.moneda());
    }

    private Moneda nuevaMoneda(String codigo) {
        return new Moneda(codigo,
                codigo.equals("ARS") ? "Peso argentino" : "Dólar estadounidense",
                2,
                TipoMoneda.FIAT);
    }

    private void persistir(Object entidad) {
        var transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            entityManager.persist(entidad);
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    private Datos persistirBaseSinMoneda(String email) {
        Usuario usuario = new Usuario("Ariel", "Test", email, "clave-segura");
        persistir(usuario);

        PerfilFinanciero perfil = new PerfilFinanciero("Perfil test", usuario);
        persistir(perfil);

        InstitucionFinanciera institucion = new InstitucionFinanciera(
                "Banco Test", TipoInstitucionFinanciera.BANCO);
        persistir(institucion);

        return new Datos(usuario, perfil, institucion, null, null);
    }

    private Datos persistirCuenta(String email, Moneda moneda) {
        Datos datos = persistirBaseSinMoneda(email);

        Cuenta cuenta = nuevaCuenta(
                new Datos(
                        datos.usuario(),
                        datos.perfil(),
                        datos.institucion(),
                        moneda,
                        null),
                "Cuenta test");

        persistir(cuenta);

        return new Datos(
                datos.usuario(),
                datos.perfil(),
                datos.institucion(),
                moneda,
                cuenta);
    }

    private record Datos(
            Usuario usuario,
            PerfilFinanciero perfil,
            InstitucionFinanciera institucion,
            Moneda moneda,
            Cuenta cuenta) {
    }
}
