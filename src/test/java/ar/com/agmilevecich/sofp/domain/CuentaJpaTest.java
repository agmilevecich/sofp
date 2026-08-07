package ar.com.agmilevecich.sofp.domain;

import ar.com.agmilevecich.sofp.config.JpaManager;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CuentaJpaTest {

    @Test
    void deberiaPersistirCuenta() {

        EntityManager entityManager = JpaManager.createEntityManager();

        try {

            Usuario usuario = new Usuario(
                    "Ariel",
                    "Usuario",
                    "ariel@test.com",
                    "hash"
            );

            PerfilFinanciero perfil =
                    new PerfilFinanciero(
                            "Personal",
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
                            "Peso Argentino",
                            2,
                            TipoMoneda.FIAT
                    );

            Cuenta cuenta =
                    new Cuenta(
                            "Caja de Ahorro",
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
            entityManager.persist(cuenta);

            entityManager.getTransaction().commit();

            Cuenta recuperada =
                    entityManager.find(
                            Cuenta.class,
                            cuenta.getId()
                    );

            assertNotNull(recuperada);

            assertEquals(
                    "Caja de Ahorro",
                    recuperada.getNombre()
            );

            assertEquals(
                    TipoCuenta.CAJA_AHORRO,
                    recuperada.getTipoCuenta()
            );

            assertEquals(
                    "Banco Santander",
                    recuperada.getInstitucionFinanciera().getNombre()
            );

            assertEquals(
                    "ARS",
                    recuperada.getMoneda().getCodigo()
            );

        } finally {

            entityManager.close();
        }
    }
}
