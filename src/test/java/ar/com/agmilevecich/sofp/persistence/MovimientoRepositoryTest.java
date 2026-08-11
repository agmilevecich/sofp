package ar.com.agmilevecich.sofp.persistence;

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
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MovimientoRepositoryTest {

    @Test
    void deberiaGuardarYBuscarMovimientoPorId() {

        JpaTestManager.close();

        EntityManager em = JpaTestManager.createEntityManager();

        try {
            DatosMovimiento datos =
                    crearDatosMovimiento(
                            "movimiento.id@test.com",
                            "ARS"
                    );

            MovimientoRepository repository =
                    new MovimientoRepository(em);

            em.getTransaction().begin();

            persistirDatosBase(em, datos);

            repository.guardar(datos.movimiento());

            em.getTransaction().commit();

            Optional<Movimiento> resultado =
                    repository.buscarPorId(
                            datos.movimiento().getId()
                    );

            assertTrue(resultado.isPresent());

            assertEquals(
                    datos.movimiento().getId(),
                    resultado.get().getId()
            );

            assertEquals(
                    new BigDecimal("15000.50"),
                    resultado.get().getImporte()
            );

            assertEquals(
                    "Compra Carrefour",
                    resultado.get().getDescripcion()
            );

            assertEquals(
                    datos.cuenta().getId(),
                    resultado.get().getCuenta().getId()
            );

            assertEquals(
                    datos.categoria().getId(),
                    resultado.get().getCategoria().getId()
            );

        } finally {
            em.close();
        }
    }

    @Test
    void deberiaListarTodosLosMovimientos() {

        JpaTestManager.close();

        EntityManager em = JpaTestManager.createEntityManager();

        try {
            DatosMovimiento datos1 =
                    crearDatosMovimiento(
                            "movimiento.lista1@test.com",
                            "ARS"
                    );

            DatosMovimiento datos2 =
                    crearDatosMovimiento(
                            "movimiento.lista2@test.com",
                            "USD"
                    );

            Movimiento movimiento1 =
                    datos1.movimiento();

            Movimiento movimiento2 =
                    new Movimiento(
                            datos2.cuenta(),
                            datos2.categoria(),
                            TipoMovimiento.INGRESO,
                            new BigDecimal("25000.00"),
                            LocalDateTime.now().plusMinutes(1),
                            "Cobro sueldo"
                    );

            MovimientoRepository repository =
                    new MovimientoRepository(em);

            em.getTransaction().begin();

            persistirDatosBase(em, datos1);
            persistirDatosBase(em, datos2);

            repository.guardar(movimiento1);
            repository.guardar(movimiento2);

            em.getTransaction().commit();

            List<Movimiento> movimientos =
                    repository.listarTodos();

            assertEquals(2, movimientos.size());

            assertEquals(
                    "Compra Carrefour",
                    movimientos.get(0).getDescripcion()
            );

            assertEquals(
                    "Cobro sueldo",
                    movimientos.get(1).getDescripcion()
            );

        } finally {
            em.close();
        }
    }

    @Test
    void deberiaListarMovimientosDeUnaCuenta() {

        JpaTestManager.close();

        EntityManager em = JpaTestManager.createEntityManager();

        try {
            DatosMovimiento datos1 =
                    crearDatosMovimiento(
                            "movimiento.cuenta1@test.com",
                            "ARS"
                    );

            DatosMovimiento datos2 =
                    crearDatosMovimiento(
                            "movimiento.cuenta2@test.com",
                            "USD"
                    );

            Movimiento movimientoCuenta1 =
                    datos1.movimiento();

            Movimiento movimientoCuenta2 =
                    new Movimiento(
                            datos2.cuenta(),
                            datos2.categoria(),
                            TipoMovimiento.INGRESO,
                            new BigDecimal("30000.00"),
                            LocalDateTime.now(),
                            "Cobro sueldo"
                    );

            MovimientoRepository repository =
                    new MovimientoRepository(em);

            em.getTransaction().begin();

            persistirDatosBase(em, datos1);
            persistirDatosBase(em, datos2);

            repository.guardar(movimientoCuenta1);
            repository.guardar(movimientoCuenta2);

            em.getTransaction().commit();

            List<Movimiento> movimientos =
                    repository.listarPorCuenta(
                            datos1.cuenta().getId()
                    );

            assertEquals(1, movimientos.size());

            assertEquals(
                    "Compra Carrefour",
                    movimientos.get(0).getDescripcion()
            );

            assertEquals(
                    datos1.cuenta().getId(),
                    movimientos.get(0)
                            .getCuenta()
                            .getId()
            );

        } finally {
            em.close();
        }
    }

    @Test
    void deberiaListarMovimientosDeUnaCategoria() {

        JpaTestManager.close();

        EntityManager em = JpaTestManager.createEntityManager();

        try {
            DatosMovimiento datos =
                    crearDatosMovimiento(
                            "movimiento.categoria@test.com",
                            "ARS"
                    );

            Categoria otraCategoria =
                    new Categoria(
                            "Combustible",
                            datos.perfil()
                    );

            Movimiento movimientoOtraCategoria =
                    new Movimiento(
                            datos.cuenta(),
                            otraCategoria,
                            TipoMovimiento.EGRESO,
                            new BigDecimal("8000.00"),
                            LocalDateTime.now().plusMinutes(1),
                            "Carga de combustible"
                    );

            MovimientoRepository repository =
                    new MovimientoRepository(em);

            em.getTransaction().begin();

            persistirDatosBase(em, datos);

            em.persist(otraCategoria);

            repository.guardar(datos.movimiento());
            repository.guardar(movimientoOtraCategoria);

            em.getTransaction().commit();

            List<Movimiento> movimientos =
                    repository.listarPorCategoria(
                            datos.categoria().getId()
                    );

            assertEquals(1, movimientos.size());

            assertEquals(
                    "Compra Carrefour",
                    movimientos.get(0).getDescripcion()
            );

            assertEquals(
                    datos.categoria().getId(),
                    movimientos.get(0)
                            .getCategoria()
                            .getId()
            );

        } finally {
            em.close();
        }
    }

    @Test
    void deberiaActualizarMovimientoExistente() {

        JpaTestManager.close();

        EntityManager em = JpaTestManager.createEntityManager();

        try {
            DatosMovimiento datos =
                    crearDatosMovimiento(
                            "movimiento.update@test.com",
                            "ARS"
                    );

            MovimientoRepository repository =
                    new MovimientoRepository(em);

            em.getTransaction().begin();

            persistirDatosBase(em, datos);

            repository.guardar(datos.movimiento());

            em.getTransaction().commit();

            Long id =
                    datos.movimiento().getId();

            em.clear();

            Movimiento movimientoModificado =
                    repository.buscarPorId(id)
                            .orElseThrow();

            em.getTransaction().begin();

            movimientoModificado.cambiarDescripcion(
                    "Compra supermercado actualizada"
            );

            movimientoModificado.cambiarObservaciones(
                    "Compra registrada correctamente"
            );

            Movimiento resultado =
                    repository.guardar(
                            movimientoModificado
                    );

            em.getTransaction().commit();

            em.clear();

            Movimiento movimientoVerificado =
                    repository.buscarPorId(id)
                            .orElseThrow();

            assertEquals(
                    id,
                    movimientoVerificado.getId()
            );

            assertEquals(
                    "Compra supermercado actualizada",
                    movimientoVerificado.getDescripcion()
            );

            assertEquals(
                    "Compra registrada correctamente",
                    movimientoVerificado.getObservaciones()
            );

        } finally {
            em.close();
        }
    }

    private DatosMovimiento crearDatosMovimiento(
            String email,
            String codigoMoneda
    ) {

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

        String nombreMoneda =
                "ARS".equals(codigoMoneda)
                        ? "Peso Argentino"
                        : "Dólar Estadounidense";

        Moneda moneda =
                new Moneda(
                        codigoMoneda,
                        nombreMoneda,
                        2,
                        TipoMoneda.FIAT
                );

        Cuenta cuenta =
                new Cuenta(
                        "Caja de Ahorro",
                        TipoCuenta.CAJA_AHORRO,
                        perfil,
                        banco,
                        moneda
                );

        Categoria categoria =
                new Categoria(
                        "Supermercado",
                        perfil
                );

        Movimiento movimiento =
                new Movimiento(
                        cuenta,
                        categoria,
                        TipoMovimiento.EGRESO,
                        new BigDecimal("15000.50"),
                        LocalDateTime.now(),
                        "Compra Carrefour"
                );

        return new DatosMovimiento(
                usuario,
                perfil,
                banco,
                moneda,
                cuenta,
                categoria,
                movimiento
        );
    }

    private void persistirDatosBase(
            EntityManager em,
            DatosMovimiento datos
    ) {

        em.persist(datos.usuario());
        em.persist(datos.perfil());
        em.persist(datos.banco());
        em.persist(datos.moneda());
        em.persist(datos.cuenta());
        em.persist(datos.categoria());
    }

    private record DatosMovimiento(
            Usuario usuario,
            PerfilFinanciero perfil,
            InstitucionFinanciera banco,
            Moneda moneda,
            Cuenta cuenta,
            Categoria categoria,
            Movimiento movimiento
    ) {
    }
}