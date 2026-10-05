package ar.com.agmilevecich.sofp.persistence;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Bono;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.OperacionFinanciera;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.MovimientoActivo;
import ar.com.agmilevecich.sofp.domain.TipoMovimientoActivo;
import ar.com.agmilevecich.sofp.domain.TipoCuenta;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoOperacionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMovimiento;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MovimientoActivoRepositoryTest {

    @Test
    void deberiaGuardarYBuscarMovimientoActivoPorId() {

        JpaTestManager.close();
        EntityManager em = JpaTestManager.createEntityManager();

        try {
            Bono bono = crearBono();
            MovimientoActivo movimiento = crearMovimiento(bono, TipoMovimientoActivo.COMPRA, "100", "105.50");
            MovimientoActivoRepository repository = new MovimientoActivoRepository(em);

            em.getTransaction().begin();
            em.persist(bono.getMoneda());
            em.persist(bono);
            repository.guardar(movimiento);
            em.getTransaction().commit();

            Long id = movimiento.getId();
            em.clear();

            Optional<MovimientoActivo> resultado = repository.buscarPorId(id);

            assertTrue(resultado.isPresent());
            assertEquals(id, resultado.get().getId());
            assertEquals(TipoMovimientoActivo.COMPRA, resultado.get().getTipoMovimiento());
            assertEquals(0, new BigDecimal("100").compareTo(resultado.get().getCantidad()));
            assertEquals(0, new BigDecimal("105.50").compareTo(resultado.get().getPrecioUnitario()));
            assertEquals(bono.getId(), resultado.get().getActivo().getId());
        } finally {
            em.close();
        }
    }

    @Test
    void deberiaRetornarOptionalVacioCuandoNoExisteMovimientoActivo() {

        JpaTestManager.close();
        EntityManager em = JpaTestManager.createEntityManager();

        try {
            MovimientoActivoRepository repository = new MovimientoActivoRepository(em);

            Optional<MovimientoActivo> resultado = repository.buscarPorId(999999L);

            assertTrue(resultado.isEmpty());
        } finally {
            em.close();
        }
    }

    @Test
    void deberiaListarTodosLosMovimientosActivos() {

        JpaTestManager.close();
        EntityManager em = JpaTestManager.createEntityManager();

        try {
            Bono bono = crearBono();
            MovimientoActivo primero = crearMovimiento(bono, TipoMovimientoActivo.COMPRA, "100", "105.50");
            MovimientoActivo segundo = crearMovimiento(bono, TipoMovimientoActivo.VENTA, "30", "110.25");
            MovimientoActivoRepository repository = new MovimientoActivoRepository(em);

            em.getTransaction().begin();
            em.persist(bono.getMoneda());
            em.persist(bono);
            repository.guardar(primero);
            repository.guardar(segundo);
            em.getTransaction().commit();

            List<MovimientoActivo> movimientos = repository.listarTodas();

            assertEquals(2, movimientos.size());
            assertEquals(TipoMovimientoActivo.COMPRA, movimientos.get(0).getTipoMovimiento());
            assertEquals(TipoMovimientoActivo.VENTA, movimientos.get(1).getTipoMovimiento());
        } finally {
            em.close();
        }
    }

    @Test
    void deberiaActualizarMovimientoActivoExistente() {

        JpaTestManager.close();
        EntityManager em = JpaTestManager.createEntityManager();

        try {
            Bono bono = crearBono();
            MovimientoActivo movimiento = crearMovimiento(bono, TipoMovimientoActivo.COMPRA, "100", "105.50");
            MovimientoActivoRepository repository = new MovimientoActivoRepository(em);

            em.getTransaction().begin();
            em.persist(bono.getMoneda());
            em.persist(bono);
            repository.guardar(movimiento);
            em.getTransaction().commit();

            Long id = movimiento.getId();
            em.clear();

            MovimientoActivo existente = repository.buscarPorId(id).orElseThrow();
            existente.cambiarCantidad(new BigDecimal("125"));
            existente.cambiarPrecioUnitario(new BigDecimal("107.75"));

            em.getTransaction().begin();
            repository.guardar(existente);
            em.getTransaction().commit();
            em.clear();

            MovimientoActivo actualizado = repository.buscarPorId(id).orElseThrow();

            assertEquals(0, new BigDecimal("125").compareTo(actualizado.getCantidad()));
            assertEquals(0, new BigDecimal("107.75").compareTo(actualizado.getPrecioUnitario()));
        } finally {
            em.close();
        }
    }

    @Test
    void deberiaRechazarMovimientoActivoNuloAlGuardar() {

        JpaTestManager.close();
        EntityManager em = JpaTestManager.createEntityManager();

        try {
            MovimientoActivoRepository repository = new MovimientoActivoRepository(em);

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
        EntityManager em = JpaTestManager.createEntityManager();

        try {
            MovimientoActivoRepository repository = new MovimientoActivoRepository(em);

            assertThrows(
                    NullPointerException.class,
                    () -> repository.buscarPorId(null)
            );
        } finally {
            em.close();
        }
    }


    private PerfilFinanciero crearPerfil(String prefijo) {
        Usuario usuario = new Usuario(
                "Ariel",
                "Milevecich",
                prefijo + "." + System.nanoTime() + "@test.com",
                "hash"
        );
        return new PerfilFinanciero("Perfil temporal", usuario);
    }

    private void persistirOperacion(
            EntityManager em,
            InstitucionFinanciera banco,
            Cuenta cuenta,
            Categoria categoria,
            MovimientoActivo movimientoActivo,
            TipoOperacionFinanciera tipoOperacion,
            LocalDateTime fechaHora) {

        OperacionFinanciera operacion = tipoOperacion == TipoOperacionFinanciera.COMPRA
                ? new OperacionFinanciera(cuenta, null,
                        movimientoActivo.getCantidad().multiply(movimientoActivo.getPrecioUnitario()),
                        tipoOperacion)
                : new OperacionFinanciera(null, cuenta,
                        movimientoActivo.getCantidad().multiply(movimientoActivo.getPrecioUnitario()),
                        tipoOperacion);

        Movimiento movimiento = tipoOperacion == TipoOperacionFinanciera.COMPRA
                ? new Movimiento(
                        cuenta,
                        categoria,
                        TipoMovimiento.EGRESO,
                        movimientoActivo.getCantidad().multiply(movimientoActivo.getPrecioUnitario()),
                        fechaHora,
                        "Compra " + movimientoActivo.getActivo().getSimbolo()
                )
                : new Movimiento(
                        cuenta,
                        categoria,
                        TipoMovimiento.INGRESO,
                        movimientoActivo.getCantidad().multiply(movimientoActivo.getPrecioUnitario()),
                        fechaHora,
                        "Venta " + movimientoActivo.getActivo().getSimbolo()
                );

        operacion.agregarMovimiento(movimiento);
        operacion.agregarMovimientoActivo(movimientoActivo);

        em.getTransaction().begin();
        if (!em.contains(cuenta.getPerfilFinanciero())) {
            em.persist(cuenta.getPerfilFinanciero().getUsuario());
            em.persist(cuenta.getPerfilFinanciero());
        }
        if (!em.contains(movimientoActivo.getActivo().getMoneda())) {
            em.persist(movimientoActivo.getActivo().getMoneda());
        }
        if (!em.contains(movimientoActivo.getActivo())) {
            em.persist(movimientoActivo.getActivo());
        }
        if (!em.contains(banco)) {
            em.persist(banco);
        }
        if (!em.contains(cuenta)) {
            em.persist(cuenta);
        }
        if (!em.contains(categoria)) {
            em.persist(categoria);
        }
        em.persist(operacion);
        em.persist(movimiento);
        em.persist(movimientoActivo);
        em.getTransaction().commit();
    }

    private Bono crearBono() {
        Moneda moneda = new Moneda(
                "ARS",
                "Peso Argentino",
                2,
                TipoMoneda.FIAT
        );
        return new Bono("Bono GD30", "GD30", moneda);
    }

    private MovimientoActivo crearMovimiento(
            Bono bono,
            TipoMovimientoActivo tipo,
            String cantidad,
            String precio) {

        return new MovimientoActivo(
                bono,
                tipo,
                new BigDecimal(cantidad),
                new BigDecimal(precio)
        );
    }


    @Test
    void deberiaListarMovimientosPorActivoYPerfilHastaFechaIncluyendoLimiteYExcluyendoPosteriores() {

        JpaTestManager.close();
        EntityManager em = JpaTestManager.createEntityManager();

        try {
            PerfilFinanciero perfil = crearPerfil("temporal.incluye");
            Moneda moneda = crearMoneda();
            Bono bono = new Bono("Bono GD30", "GD30", moneda);
            InstitucionFinanciera banco = new InstitucionFinanciera(
                    "Banco Temporal " + System.nanoTime(),
                    TipoInstitucionFinanciera.BANCO
            );
            Cuenta cuenta = new Cuenta(
                    "Cuenta temporal",
                    TipoCuenta.CAJA_AHORRO,
                    perfil,
                    banco,
                    moneda
            );
            Categoria categoria = new Categoria("Inversiones temporal", perfil);

            MovimientoActivo compraInicial =
                    crearMovimiento(bono, TipoMovimientoActivo.COMPRA, "100", "100");
            MovimientoActivo ventaEnLimite =
                    crearMovimiento(bono, TipoMovimientoActivo.VENTA, "40", "110");
            MovimientoActivo compraFutura =
                    crearMovimiento(bono, TipoMovimientoActivo.COMPRA, "50", "120");

            persistirOperacion(
                    em,
                    banco,
                    cuenta,
                    categoria,
                    compraInicial,
                    TipoOperacionFinanciera.COMPRA,
                    LocalDateTime.of(2026, 9, 24, 10, 0)
            );
            persistirOperacion(
                    em,
                    banco,
                    cuenta,
                    categoria,
                    ventaEnLimite,
                    TipoOperacionFinanciera.VENTA,
                    LocalDateTime.of(2026, 9, 24, 11, 0)
            );
            persistirOperacion(
                    em,
                    banco,
                    cuenta,
                    categoria,
                    compraFutura,
                    TipoOperacionFinanciera.COMPRA,
                    LocalDateTime.of(2026, 9, 24, 12, 0)
            );

            MovimientoActivoRepository repository = new MovimientoActivoRepository(em);

            List<MovimientoActivo> movimientos =
                    repository.listarPorActivoYPerfilFinancieroHastaFecha(
                            bono.getId(),
                            perfil.getId(),
                            LocalDateTime.of(2026, 9, 24, 11, 0)
                    );

            assertEquals(2, movimientos.size());
            assertEquals(compraInicial.getId(), movimientos.get(0).getId());
            assertEquals(ventaEnLimite.getId(), movimientos.get(1).getId());
            assertFalse(
                    movimientos.stream()
                            .anyMatch(m -> m.getId().equals(compraFutura.getId()))
            );
        } finally {
            em.close();
        }
    }

    @Test
    void deberiaRechazarParametrosNulosAlListarPorActivoYPerfilHastaFecha() {

        JpaTestManager.close();
        EntityManager em = JpaTestManager.createEntityManager();

        try {
            MovimientoActivoRepository repository = new MovimientoActivoRepository(em);

            assertThrows(
                    NullPointerException.class,
                    () -> repository.listarPorActivoYPerfilFinancieroHastaFecha(
                            null,
                            1L,
                            LocalDateTime.of(2026, 9, 24, 11, 0)
                    )
            );

            assertThrows(
                    NullPointerException.class,
                    () -> repository.listarPorActivoYPerfilFinancieroHastaFecha(
                            1L,
                            null,
                            LocalDateTime.of(2026, 9, 24, 11, 0)
                    )
            );

            assertThrows(
                    NullPointerException.class,
                    () -> repository.listarPorActivoYPerfilFinancieroHastaFecha(
                            1L,
                            1L,
                            null
                    )
            );
        } finally {
            em.close();
        }
    }

    @Test
    void deberiaListarMovimientosPorActivo() {

        JpaTestManager.close();
        EntityManager em = JpaTestManager.createEntityManager();

        try {
            Bono bono = crearBono();
            Bono otroBono = new Bono("Bono AL30", "AL30", bono.getMoneda());

            MovimientoActivo primero =
                    crearMovimiento(bono, TipoMovimientoActivo.COMPRA, "100", "105.50");

            MovimientoActivo segundo =
                    crearMovimiento(bono, TipoMovimientoActivo.VENTA, "30", "110.25");

            MovimientoActivo deOtroActivo =
                    crearMovimiento(otroBono, TipoMovimientoActivo.COMPRA, "50", "120");

            MovimientoActivoRepository repository =
                    new MovimientoActivoRepository(em);

            em.getTransaction().begin();

            em.persist(bono.getMoneda());
            em.persist(bono);
            em.persist(otroBono);

            repository.guardar(primero);
            repository.guardar(segundo);
            repository.guardar(deOtroActivo);

            em.getTransaction().commit();

            List<MovimientoActivo> movimientos =
                    repository.listarPorActivo(bono.getId());

            assertEquals(2, movimientos.size());
            assertEquals(primero.getId(), movimientos.get(0).getId());
            assertEquals(segundo.getId(), movimientos.get(1).getId());

        } finally {
            em.close();
        }
    }

    @Test
    void deberiaRetornarListaVaciaCuandoActivoNoTieneMovimientos() {

        JpaTestManager.close();
        EntityManager em = JpaTestManager.createEntityManager();

        try {
            Bono bono = crearBono();
            MovimientoActivoRepository repository =
                    new MovimientoActivoRepository(em);

            em.getTransaction().begin();
            em.persist(bono.getMoneda());
            em.persist(bono);
            em.getTransaction().commit();

            List<MovimientoActivo> movimientos =
                    repository.listarPorActivo(bono.getId());

            assertTrue(movimientos.isEmpty());

        } finally {
            em.close();
        }
    }

    @Test
    void deberiaRechazarIdActivoNuloAlListarPorActivo() {

        JpaTestManager.close();
        EntityManager em = JpaTestManager.createEntityManager();

        try {
            MovimientoActivoRepository repository =
                    new MovimientoActivoRepository(em);

            assertThrows(
                    NullPointerException.class,
                    () -> repository.listarPorActivo(null)
            );

        } finally {
            em.close();
        }
    }
}
