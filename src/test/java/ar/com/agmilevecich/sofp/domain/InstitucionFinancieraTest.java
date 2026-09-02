package ar.com.agmilevecich.sofp.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InstitucionFinancieraTest {

    @Test
    void deberiaCrearInstitucionActiva() {

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Banco Santander",
                        TipoInstitucionFinanciera.BANCO);

        assertEquals("Banco Santander", institucion.getNombre());
        assertEquals(TipoInstitucionFinanciera.BANCO, institucion.getTipo());
        assertTrue(institucion.isActiva());
    }

    @Test
    void deberiaDesactivarInstitucion() {

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Banco Santander",
                        TipoInstitucionFinanciera.BANCO);

        institucion.desactivar();

        assertFalse(institucion.isActiva());
    }

    @Test
    void deberiaActivarInstitucion() {

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Banco Santander",
                        TipoInstitucionFinanciera.BANCO);

        institucion.desactivar();

        institucion.activar();

        assertTrue(institucion.isActiva());
    }

    @Test
    void deberiaRenombrarInstitucion() {

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Banco Santander",
                        TipoInstitucionFinanciera.BANCO);

        institucion.renombrar("Santander Argentina");

        assertEquals(
                "Santander Argentina",
                institucion.getNombre());
    }

    @Test
    void deberiaMostrarNombreEnToString() {

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Banco Santander",
                        TipoInstitucionFinanciera.BANCO);

        assertEquals("Banco Santander", institucion.toString());
    }

}