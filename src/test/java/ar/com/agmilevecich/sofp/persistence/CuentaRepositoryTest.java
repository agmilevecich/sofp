package ar.com.agmilevecich.sofp.persistence;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.TipoCuenta;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.domain.Usuario;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CuentaRepositoryTest {

    private EntityManager em;

    @BeforeEach
    void iniciarBaseDeDatos() {
        JpaTestManager.close();
        em = JpaTestManager.createEntityManager();
    }

    @AfterEach
    void cerrarBaseDeDatos() {
        if (em != null && em.isOpen()) {
            em.close();
        }

        JpaTestManager.close();
    }

    @Test
    void deberiaGuardarYBuscarCuentaPorId() {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Milevecich",
                        "ariel@test.com",
                        "hash-test"
                );

        PerfilFinanciero perfil =
                new PerfilFinanciero(
                        "Perfil principal",
                        usuario
                );

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Banco Santander",
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

        em.getTransaction().begin();

        em.persist(usuario);
        em.persist(perfil);
        em.persist(institucion);
        em.persist(moneda);

        CuentaRepository repository =
                new CuentaRepository(em);

        repository.guardar(cuenta);

        em.getTransaction().commit();

        Optional<Cuenta> resultado =
                repository.buscarPorId(cuenta.getId());

        assertTrue(resultado.isPresent());

        assertEquals(
                "Cuenta principal",
                resultado.get().getNombre()
        );

        assertEquals(
                TipoCuenta.CAJA_AHORRO,
                resultado.get().getTipoCuenta()
        );

        assertTrue(
                resultado.get().isActiva()
        );
    }

    @Test
    void deberiaListarTodasLasCuentas() {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Milevecich",
                        "ariel@test.com",
                        "hash-test"
                );

        PerfilFinanciero perfil =
                new PerfilFinanciero(
                        "Perfil principal",
                        usuario
                );

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Banco Santander",
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
                        "Cuenta principal",
                        TipoCuenta.CAJA_AHORRO,
                        perfil,
                        institucion,
                        moneda
                );

        Cuenta cuenta2 =
                new Cuenta(
                        "Cuenta secundaria",
                        TipoCuenta.CUENTA_CORRIENTE,
                        perfil,
                        institucion,
                        moneda
                );

        em.getTransaction().begin();

        em.persist(usuario);
        em.persist(perfil);
        em.persist(institucion);
        em.persist(moneda);

        CuentaRepository repository =
                new CuentaRepository(em);

        repository.guardar(cuenta1);
        repository.guardar(cuenta2);

        em.getTransaction().commit();

        List<Cuenta> cuentas =
                repository.listarTodas();

        assertEquals(
                2,
                cuentas.size()
        );

        assertEquals(
                "Cuenta principal",
                cuentas.get(0).getNombre()
        );

        assertEquals(
                "Cuenta secundaria",
                cuentas.get(1).getNombre()
        );
    }

    @Test
    void deberiaListarCuentasPorPerfilFinanciero() {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Milevecich",
                        "ariel@test.com",
                        "hash-test"
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
                        "Banco Santander",
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

        em.getTransaction().begin();

        em.persist(usuario);
        em.persist(perfil1);
        em.persist(perfil2);
        em.persist(institucion);
        em.persist(moneda);

        CuentaRepository repository =
                new CuentaRepository(em);

        repository.guardar(cuentaPerfil1);
        repository.guardar(cuentaPerfil2);

        em.getTransaction().commit();

        List<Cuenta> cuentas =
                repository.listarPorPerfilFinanciero(
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
    void deberiaActualizarCuentaExistente() {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Milevecich",
                        "ariel@test.com",
                        "hash-test"
                );

        PerfilFinanciero perfil =
                new PerfilFinanciero(
                        "Perfil principal",
                        usuario
                );

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Banco Santander",
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

        em.getTransaction().begin();

        em.persist(usuario);
        em.persist(perfil);
        em.persist(institucion);
        em.persist(moneda);

        CuentaRepository repository =
                new CuentaRepository(em);

        repository.guardar(cuenta);

        em.getTransaction().commit();

        Long id = cuenta.getId();

        em.clear();

        Cuenta cuentaModificada =
                repository.buscarPorId(id)
                        .orElseThrow();

        em.getTransaction().begin();

        cuentaModificada.renombrar(
                "Cuenta principal actualizada"
        );

        cuentaModificada.cambiarIdentificadorExterno(
                "CTA-001"
        );

        repository.guardar(cuentaModificada);

        em.getTransaction().commit();

        em.clear();

        Cuenta cuentaVerificada =
                repository.buscarPorId(id)
                        .orElseThrow();

        assertEquals(
                id,
                cuentaVerificada.getId()
        );

        assertEquals(
                "Cuenta principal actualizada",
                cuentaVerificada.getNombre()
        );

        assertEquals(
                "CTA-001",
                cuentaVerificada.getIdentificadorExterno()
        );
    }
}