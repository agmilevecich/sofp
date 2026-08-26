package ar.com.agmilevecich.sofp.persistence;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.OperacionFinanciera;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.TipoCuenta;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.domain.Bono;
import ar.com.agmilevecich.sofp.domain.MovimientoActivo;
import ar.com.agmilevecich.sofp.domain.TipoMovimientoActivo;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class OperacionFinancieraRepositoryTest {

    @Test
    void deberiaGuardarYBuscarOperacionPorId() {

        JpaTestManager.close();

        EntityManager em =
                JpaTestManager.createEntityManager();

        try {
            DatosOperacion datos =
                    crearDatosOperacion(
                            "operacion.id@test.com"
                    );

            OperacionFinancieraRepository repository =
                    new OperacionFinancieraRepository(em);

            em.getTransaction().begin();

            persistirDatosBase(em, datos);

            repository.guardar(datos.operacion());

            em.getTransaction().commit();

            Optional<OperacionFinanciera> resultado =
                    repository.buscarPorId(
                            datos.operacion().getId()
                    );

            assertTrue(resultado.isPresent());

            assertEquals(
                    datos.operacion().getId(),
                    resultado.get().getId()
            );

            assertEquals(
                    datos.cuentaOrigen().getId(),
                    resultado.get()
                            .getCuentaOrigen()
                            .getId()
            );

            assertEquals(
                    datos.cuentaDestino().getId(),
                    resultado.get()
                            .getCuentaDestino()
                            .getId()
            );

            assertEquals(
                    new BigDecimal("100000.00"),
                    resultado.get().getImporte()
            );

        } finally {
            em.close();
        }
    }

    @Test
    void deberiaRetornarOptionalVacioCuandoNoExisteOperacion() {

        JpaTestManager.close();

        EntityManager em =
                JpaTestManager.createEntityManager();

        try {
            OperacionFinancieraRepository repository =
                    new OperacionFinancieraRepository(em);

            Optional<OperacionFinanciera> resultado =
                    repository.buscarPorId(999999L);

            assertTrue(resultado.isEmpty());

        } finally {
            em.close();
        }
    }

    @Test
    void deberiaListarTodasLasOperaciones() {

        JpaTestManager.close();

        EntityManager em =
                JpaTestManager.createEntityManager();

        try {
            DatosOperacion datos =
                    crearDatosOperacion(
                            "operacion.lista@test.com"
                    );

            OperacionFinanciera segundaOperacion =
                    new OperacionFinanciera(
                            datos.cuentaDestino(),
                            datos.cuentaOrigen(),
                            new BigDecimal("50000.00")
                    );

            OperacionFinancieraRepository repository =
                    new OperacionFinancieraRepository(em);

            em.getTransaction().begin();

            persistirDatosBase(em, datos);

            repository.guardar(datos.operacion());
            repository.guardar(segundaOperacion);

            em.getTransaction().commit();

            List<OperacionFinanciera> operaciones =
                    repository.listarTodas();

            assertEquals(
                    2,
                    operaciones.size()
            );

            assertEquals(
                    new BigDecimal("100000.00"),
                    operaciones.get(0).getImporte()
            );

            assertEquals(
                    new BigDecimal("50000.00"),
                    operaciones.get(1).getImporte()
            );

        } finally {
            em.close();
        }
    }

    @Test
    void deberiaListarOperacionesPorCuentaOrigen() {

        JpaTestManager.close();

        EntityManager em =
                JpaTestManager.createEntityManager();

        try {
            DatosOperacion datos =
                    crearDatosOperacion(
                            "operacion.origen@test.com"
                    );

            OperacionFinanciera otraOperacion =
                    new OperacionFinanciera(
                            datos.cuentaDestino(),
                            datos.cuentaOrigen(),
                            new BigDecimal("25000.00")
                    );

            OperacionFinancieraRepository repository =
                    new OperacionFinancieraRepository(em);

            em.getTransaction().begin();

            persistirDatosBase(em, datos);

            repository.guardar(datos.operacion());
            repository.guardar(otraOperacion);

            em.getTransaction().commit();

            List<OperacionFinanciera> operaciones =
                    repository.listarPorCuentaOrigen(
                            datos.cuentaOrigen().getId()
                    );

            assertEquals(
                    1,
                    operaciones.size()
            );

            assertEquals(
                    new BigDecimal("100000.00"),
                    operaciones.get(0).getImporte()
            );

            assertEquals(
                    datos.cuentaOrigen().getId(),
                    operaciones.get(0)
                            .getCuentaOrigen()
                            .getId()
            );

        } finally {
            em.close();
        }
    }

    @Test
    void deberiaListarOperacionesPorCuentaDestino() {

        JpaTestManager.close();

        EntityManager em =
                JpaTestManager.createEntityManager();

        try {
            DatosOperacion datos =
                    crearDatosOperacion(
                            "operacion.destino@test.com"
                    );

            OperacionFinanciera otraOperacion =
                    new OperacionFinanciera(
                            datos.cuentaDestino(),
                            datos.cuentaOrigen(),
                            new BigDecimal("25000.00")
                    );

            OperacionFinancieraRepository repository =
                    new OperacionFinancieraRepository(em);

            em.getTransaction().begin();

            persistirDatosBase(em, datos);

            repository.guardar(datos.operacion());
            repository.guardar(otraOperacion);

            em.getTransaction().commit();

            List<OperacionFinanciera> operaciones =
                    repository.listarPorCuentaDestino(
                            datos.cuentaOrigen().getId()
                    );

            assertEquals(
                    1,
                    operaciones.size()
            );

            assertEquals(
                    new BigDecimal("25000.00"),
                    operaciones.get(0).getImporte()
            );

            assertEquals(
                    datos.cuentaOrigen().getId(),
                    operaciones.get(0)
                            .getCuentaDestino()
                            .getId()
            );

        } finally {
            em.close();
        }
    }

    @Test
    void deberiaActualizarOperacionExistente() {

        JpaTestManager.close();

        EntityManager em =
                JpaTestManager.createEntityManager();

        try {
            DatosOperacion datos =
                    crearDatosOperacion(
                            "operacion.update@test.com"
                    );

            OperacionFinancieraRepository repository =
                    new OperacionFinancieraRepository(em);

            em.getTransaction().begin();

            persistirDatosBase(em, datos);

            repository.guardar(datos.operacion());

            em.getTransaction().commit();

            Long id =
                    datos.operacion().getId();

            em.clear();

            OperacionFinanciera operacion =
                    repository.buscarPorId(id)
                            .orElseThrow();

            assertEquals(
                    new BigDecimal("100000.00"),
                    operacion.getImporte()
            );

            assertEquals(
                    id,
                    operacion.getId()
            );

        } finally {
            em.close();
        }
    }

    @Test
    void deberiaRechazarOperacionNulaAlGuardar() {

        JpaTestManager.close();

        EntityManager em =
                JpaTestManager.createEntityManager();

        try {
            OperacionFinancieraRepository repository =
                    new OperacionFinancieraRepository(em);

            assertThrows(
                    NullPointerException.class,
                    () -> repository.guardar(null)
            );

        } finally {
            em.close();
        }
    }

    @Test
    void deberiaRechazarIdNuloAlBuscar() {

        JpaTestManager.close();

        EntityManager em =
                JpaTestManager.createEntityManager();

        try {
            OperacionFinancieraRepository repository =
                    new OperacionFinancieraRepository(em);

            assertThrows(
                    NullPointerException.class,
                    () -> repository.buscarPorId(null)
            );

        } finally {
            em.close();
        }
    }

    @Test
    void deberiaRechazarCuentaOrigenNulaAlListar() {

        JpaTestManager.close();

        EntityManager em =
                JpaTestManager.createEntityManager();

        try {
            OperacionFinancieraRepository repository =
                    new OperacionFinancieraRepository(em);

            assertThrows(
                    NullPointerException.class,
                    () -> repository.listarPorCuentaOrigen(null)
            );

        } finally {
            em.close();
        }
    }

    @Test
    void deberiaRechazarCuentaDestinoNulaAlListar() {

        JpaTestManager.close();

        EntityManager em =
                JpaTestManager.createEntityManager();

        try {
            OperacionFinancieraRepository repository =
                    new OperacionFinancieraRepository(em);

            assertThrows(
                    NullPointerException.class,
                    () -> repository.listarPorCuentaDestino(null)
            );

        } finally {
            em.close();
        }
    }

    @Test
    void deberiaPersistirMovimientoActivoDentroDeOperacionFinanciera() {

        JpaTestManager.close();

        EntityManager em =
                JpaTestManager.createEntityManager();

        try {
            DatosOperacion datos =
                    crearDatosOperacion(
                            "operacion.activo@test.com"
                    );

            Bono bono =
                    new Bono(
                            "Bono GD30",
                            datos.moneda()
                    );

            MovimientoActivo movimientoActivo =
                    new MovimientoActivo(
                            bono,
                            TipoMovimientoActivo.COMPRA,
                            new BigDecimal("100"),
                            new BigDecimal("125")
                    );

            datos.operacion().agregarMovimientoActivo(
                    movimientoActivo
            );

            OperacionFinancieraRepository repository =
                    new OperacionFinancieraRepository(em);

            em.getTransaction().begin();

            persistirDatosBase(em, datos);
            em.persist(bono);
            em.persist(movimientoActivo);
            repository.guardar(datos.operacion());

            em.getTransaction().commit();

            em.clear();

            OperacionFinanciera resultado =
                    repository.buscarPorId(
                            datos.operacion().getId()
                    ).orElseThrow();

            assertEquals(
                    1,
                    resultado.getMovimientosActivos().size()
            );

            MovimientoActivo recuperado =
                    resultado.getMovimientosActivos().get(0);

            assertEquals(
                    bono.getId(),
                    recuperado.getActivo().getId()
            );

            assertEquals(
                    new BigDecimal("100"),
                    recuperado.getCantidad()
            );

            assertEquals(
                    new BigDecimal("125"),
                    recuperado.getPrecioUnitario()
            );

            assertEquals(
                    resultado.getId(),
                    recuperado.getOperacionFinanciera().getId()
            );

        } finally {
            em.close();
        }
    }

    @Test
    void deberiaPersistirOperacionSinMovimientosActivos() {

        JpaTestManager.close();

        EntityManager em =
                JpaTestManager.createEntityManager();

        try {
            DatosOperacion datos =
                    crearDatosOperacion(
                            "operacion.sin.activo@test.com"
                    );

            OperacionFinancieraRepository repository =
                    new OperacionFinancieraRepository(em);

            em.getTransaction().begin();

            persistirDatosBase(em, datos);
            repository.guardar(datos.operacion());

            em.getTransaction().commit();

            em.clear();

            OperacionFinanciera resultado =
                    repository.buscarPorId(
                            datos.operacion().getId()
                    ).orElseThrow();

            assertTrue(
                    resultado.getMovimientosActivos().isEmpty()
            );

        } finally {
            em.close();
        }
    }

    private DatosOperacion crearDatosOperacion(
            String email) {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Milevecich",
                        email,
                        "hash123"
                );

        PerfilFinanciero perfil =
                new PerfilFinanciero(
                        "Finanzas personales",
                        usuario
                );

        InstitucionFinanciera banco =
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

        Cuenta cuentaOrigen =
                new Cuenta(
                        "Caja de Ahorro",
                        TipoCuenta.CAJA_AHORRO,
                        perfil,
                        banco,
                        moneda
                );

        Cuenta cuentaDestino =
                new Cuenta(
                        "Cuenta Corriente",
                        TipoCuenta.CUENTA_CORRIENTE,
                        perfil,
                        banco,
                        moneda
                );

        Categoria categoriaOrigen =
                new Categoria(
                        "Transferencias enviadas",
                        perfil
                );

        Categoria categoriaDestino =
                new Categoria(
                        "Transferencias recibidas",
                        perfil
                );

        OperacionFinanciera operacion =
                new OperacionFinanciera(
                        cuentaOrigen,
                        cuentaDestino,
                        new BigDecimal("100000.00")
                );

        return new DatosOperacion(
                usuario,
                perfil,
                banco,
                moneda,
                cuentaOrigen,
                cuentaDestino,
                categoriaOrigen,
                categoriaDestino,
                operacion
        );
    }

    private void persistirDatosBase(
            EntityManager em,
            DatosOperacion datos) {

        em.persist(datos.usuario());
        em.persist(datos.perfil());
        em.persist(datos.banco());
        em.persist(datos.moneda());
        em.persist(datos.cuentaOrigen());
        em.persist(datos.cuentaDestino());
        em.persist(datos.categoriaOrigen());
        em.persist(datos.categoriaDestino());
    }

    private record DatosOperacion(
            Usuario usuario,
            PerfilFinanciero perfil,
            InstitucionFinanciera banco,
            Moneda moneda,
            Cuenta cuentaOrigen,
            Cuenta cuentaDestino,
            Categoria categoriaOrigen,
            Categoria categoriaDestino,
            OperacionFinanciera operacion
    ) {
    }
}
