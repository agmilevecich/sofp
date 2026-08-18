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
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MovimientoServiceTest {

    private EntityManager entityManager;
    private MovimientoService movimientoService;

    private Usuario usuario;
    private PerfilFinanciero perfilFinanciero;
    private InstitucionFinanciera institucionFinanciera;
    private Moneda moneda;
    private Cuenta cuenta;
    private Categoria categoria;

    @BeforeEach
    void setUp() {

        entityManager = JpaTestManager.createEntityManager();

        MovimientoRepository movimientoRepository =
                new MovimientoRepository(entityManager);

        movimientoService =
                new MovimientoService(
                        entityManager,
                        movimientoRepository
                );

        usuario = new Usuario(
                "Juan",
                "Pérez",
                "juan." + System.nanoTime() + "@test.com",
                "hash"
        );

        perfilFinanciero =
                new PerfilFinanciero(
                        "Perfil principal",
                        usuario
                );

        usuario.agregarPerfilFinanciero(perfilFinanciero);

        institucionFinanciera =
                new InstitucionFinanciera(
                        "Banco de Prueba",
                        TipoInstitucionFinanciera.BANCO
                );

        moneda =
                new Moneda(
                        "ARS",
                        "Peso argentino",
                        2,
                        TipoMoneda.FIAT
                );

        cuenta =
                new Cuenta(
                        "Cuenta principal",
                        TipoCuenta.CAJA_AHORRO,
                        perfilFinanciero,
                        institucionFinanciera,
                        moneda
                );

        categoria =
                new Categoria(
                        "Alimentación",
                        perfilFinanciero
                );

        entityManager.getTransaction().begin();

        entityManager.persist(usuario);
        entityManager.persist(perfilFinanciero);
        entityManager.persist(institucionFinanciera);
        entityManager.persist(moneda);
        entityManager.persist(cuenta);
        entityManager.persist(categoria);

        entityManager.getTransaction().commit();
    }

    @AfterEach
    void tearDown() {

        if (entityManager != null
                && entityManager.isOpen()) {

            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }

            entityManager.close();
        }

        JpaTestManager.close();
    }

    @Test
    void deberiaRegistrarUnIngreso() {

        Movimiento movimiento =
                movimientoService.registrar(
                        cuenta,
                        categoria,
                        TipoMovimiento.INGRESO,
                        new BigDecimal("150000.00"),
                        LocalDateTime.of(
                                2026,
                                8,
                                11,
                                10,
                                0
                        ),
                        "Sueldo"
                );

        assertNotNull(movimiento);
        assertNotNull(movimiento.getId());

        assertEquals(
                new BigDecimal("150000.00"),
                movimiento.getImporte()
        );

        assertEquals(
                TipoMovimiento.INGRESO,
                movimiento.getTipoMovimiento()
        );

        assertEquals(
                "Sueldo",
                movimiento.getDescripcion()
        );
    }

    @Test
    void deberiaRegistrarUnEgreso() {

        Movimiento movimiento =
                movimientoService.registrar(
                        cuenta,
                        categoria,
                        TipoMovimiento.EGRESO,
                        new BigDecimal("25000.00"),
                        LocalDateTime.of(
                                2026,
                                8,
                                11,
                                12,
                                0
                        ),
                        "Compra supermercado"
                );

        assertNotNull(movimiento);
        assertNotNull(movimiento.getId());

        assertEquals(
                new BigDecimal("25000.00"),
                movimiento.getImporte()
        );

        assertEquals(
                TipoMovimiento.EGRESO,
                movimiento.getTipoMovimiento()
        );

        assertEquals(
                "Compra supermercado",
                movimiento.getDescripcion()
        );
    }

    @Test
    void deberiaListarMovimientosPorCuenta() {

        movimientoService.registrar(
                cuenta,
                categoria,
                TipoMovimiento.INGRESO,
                new BigDecimal("100000.00"),
                LocalDateTime.of(
                        2026,
                        8,
                        11,
                        9,
                        0
                ),
                "Sueldo"
        );

        movimientoService.registrar(
                cuenta,
                categoria,
                TipoMovimiento.EGRESO,
                new BigDecimal("15000.00"),
                LocalDateTime.of(
                        2026,
                        8,
                        11,
                        10,
                        0
                ),
                "Supermercado"
        );

        var movimientos =
                movimientoService.listarPorCuenta(
                        cuenta.getId()
                );

        assertEquals(2, movimientos.size());

        assertEquals(
                TipoMovimiento.INGRESO,
                movimientos.get(0).getTipoMovimiento()
        );

        assertEquals(
                TipoMovimiento.EGRESO,
                movimientos.get(1).getTipoMovimiento()
        );
    }

    @Test
    void deberiaListarMovimientosPorCategoria() {

        movimientoService.registrar(
                cuenta,
                categoria,
                TipoMovimiento.INGRESO,
                new BigDecimal("80000.00"),
                LocalDateTime.of(
                        2026,
                        8,
                        12,
                        9,
                        0
                ),
                "Sueldo"
        );

        movimientoService.registrar(
                cuenta,
                categoria,
                TipoMovimiento.EGRESO,
                new BigDecimal("12000.00"),
                LocalDateTime.of(
                        2026,
                        8,
                        12,
                        10,
                        0
                ),
                "Supermercado"
        );

        var movimientos =
                movimientoService.listarPorCategoria(
                        categoria.getId()
                );

        assertEquals(2, movimientos.size());

        assertEquals(
                TipoMovimiento.INGRESO,
                movimientos.get(0).getTipoMovimiento()
        );

        assertEquals(
                TipoMovimiento.EGRESO,
                movimientos.get(1).getTipoMovimiento()
        );
    }

    @Test
    void deberiaListarTodosLosMovimientos() {

        movimientoService.registrar(
                cuenta,
                categoria,
                TipoMovimiento.INGRESO,
                new BigDecimal("120000.00"),
                LocalDateTime.of(
                        2026,
                        8,
                        12,
                        8,
                        0
                ),
                "Sueldo"
        );

        movimientoService.registrar(
                cuenta,
                categoria,
                TipoMovimiento.EGRESO,
                new BigDecimal("20000.00"),
                LocalDateTime.of(
                        2026,
                        8,
                        12,
                        9,
                        0
                ),
                "Supermercado"
        );

        var movimientos =
                movimientoService.listarTodos();

        assertEquals(2, movimientos.size());

        assertEquals(
                TipoMovimiento.INGRESO,
                movimientos.get(0).getTipoMovimiento()
        );

        assertEquals(
                TipoMovimiento.EGRESO,
                movimientos.get(1).getTipoMovimiento()
        );
    }

    @Test
    void deberiaBuscarMovimientoPorId() {

        Movimiento movimientoRegistrado =
                movimientoService.registrar(
                        cuenta,
                        categoria,
                        TipoMovimiento.INGRESO,
                        new BigDecimal("95000.00"),
                        LocalDateTime.of(
                                2026,
                                8,
                                12,
                                11,
                                0
                        ),
                        "Transferencia recibida"
                );

        var resultado =
                movimientoService.buscarPorId(
                        movimientoRegistrado.getId()
                );

        assertEquals(true, resultado.isPresent());

        Movimiento movimientoEncontrado =
                resultado.orElseThrow();

        assertEquals(
                movimientoRegistrado.getId(),
                movimientoEncontrado.getId()
        );

        assertEquals(
                new BigDecimal("95000.00"),
                movimientoEncontrado.getImporte()
        );

        assertEquals(
                TipoMovimiento.INGRESO,
                movimientoEncontrado.getTipoMovimiento()
        );

        assertEquals(
                "Transferencia recibida",
                movimientoEncontrado.getDescripcion()
        );
    }

    @Test
    void deberiaModificarLaDescripcionDeUnMovimiento() {

        Movimiento movimiento =
                movimientoService.registrar(
                        cuenta,
                        categoria,
                        TipoMovimiento.EGRESO,
                        new BigDecimal("5000.00"),
                        LocalDateTime.of(
                                2026,
                                8,
                                13,
                                10,
                                0
                        ),
                        "Compra original"
                );

        Movimiento actualizado =
                movimientoService.modificarDescripcion(
                        movimiento.getId(),
                        "Compra supermercado"
                );

        assertEquals(
                movimiento.getId(),
                actualizado.getId()
        );

        assertEquals(
                "Compra supermercado",
                actualizado.getDescripcion()
        );
    }

    @Test
    void deberiaModificarLasObservacionesDeUnMovimiento() {

        Movimiento movimiento =
                movimientoService.registrar(
                        cuenta,
                        categoria,
                        TipoMovimiento.EGRESO,
                        new BigDecimal("3500.00"),
                        LocalDateTime.of(
                                2026,
                                8,
                                13,
                                11,
                                0
                        ),
                        "Compra"
                );

        Movimiento actualizado =
                movimientoService.modificarObservaciones(
                        movimiento.getId(),
                        "Pago realizado con tarjeta"
                );

        assertEquals(
                movimiento.getId(),
                actualizado.getId()
        );

        assertEquals(
                "Pago realizado con tarjeta",
                actualizado.getObservaciones()
        );
    }

    @Test
    void deberiaCambiarLaCategoriaDeUnMovimiento() {

        Categoria nuevaCategoria =
                new Categoria(
                        "Transporte",
                        perfilFinanciero
                );

        entityManager.getTransaction().begin();

        entityManager.persist(nuevaCategoria);

        entityManager.getTransaction().commit();

        Movimiento movimiento =
                movimientoService.registrar(
                        cuenta,
                        categoria,
                        TipoMovimiento.EGRESO,
                        new BigDecimal("7000.00"),
                        LocalDateTime.of(
                                2026,
                                8,
                                13,
                                12,
                                0
                        ),
                        "Carga de combustible"
                );

        Movimiento actualizado =
                movimientoService.cambiarCategoria(
                        movimiento.getId(),
                        nuevaCategoria
                );

        assertEquals(
                movimiento.getId(),
                actualizado.getId()
        );

        assertEquals(
                nuevaCategoria.getId(),
                actualizado.getCategoria().getId()
        );

        assertEquals(
                "Transporte",
                actualizado.getCategoria().getNombre()
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeModificaUnMovimientoInexistente() {

        Long movimientoIdInexistente = 999999L;

        assertThrows(
                IllegalArgumentException.class,
                () -> movimientoService.modificarDescripcion(
                        movimientoIdInexistente,
                        "Nueva descripción"
                )
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeCambiaLaCategoriaDeUnMovimientoInexistente() {

        Long movimientoIdInexistente = 999999L;

        assertThrows(
                IllegalArgumentException.class,
                () -> movimientoService.cambiarCategoria(
                        movimientoIdInexistente,
                        categoria
                )
        );
    }

    @Test
    void deberiaModificarTipoMovimiento() {

        Movimiento movimiento =
                movimientoService.registrar(
                        cuenta,
                        categoria,
                        TipoMovimiento.INGRESO,
                        new BigDecimal("50000.00"),
                        LocalDateTime.of(
                                2026,
                                8,
                                14,
                                10,
                                0
                        ),
                        "Movimiento original"
                );

        Movimiento actualizado =
                movimientoService.modificarTipoMovimiento(
                        movimiento.getId(),
                        TipoMovimiento.EGRESO
                );

        assertNotNull(actualizado);

        assertEquals(
                TipoMovimiento.EGRESO,
                actualizado.getTipoMovimiento()
        );
    }

    @Test
    void deberiaModificarImporte() {

        Movimiento movimiento =
                movimientoService.registrar(
                        cuenta,
                        categoria,
                        TipoMovimiento.INGRESO,
                        new BigDecimal("50000.00"),
                        LocalDateTime.of(
                                2026,
                                8,
                                14,
                                10,
                                0
                        ),
                        "Movimiento original"
                );

        Movimiento actualizado =
                movimientoService.modificarImporte(
                        movimiento.getId(),
                        new BigDecimal("75000.00")
                );

        assertNotNull(actualizado);

        assertEquals(
                new BigDecimal("75000.00"),
                actualizado.getImporte()
        );
    }

    @Test
    void deberiaModificarFechaHora() {

        Movimiento movimiento =
                movimientoService.registrar(
                        cuenta,
                        categoria,
                        TipoMovimiento.INGRESO,
                        new BigDecimal("50000.00"),
                        LocalDateTime.of(
                                2026,
                                8,
                                14,
                                10,
                                0
                        ),
                        "Movimiento original"
                );

        LocalDateTime nuevaFechaHora =
                LocalDateTime.of(
                        2026,
                        8,
                        15,
                        15,
                        30
                );

        Movimiento actualizado =
                movimientoService.modificarFechaHora(
                        movimiento.getId(),
                        nuevaFechaHora
                );

        assertNotNull(actualizado);

        assertEquals(
                nuevaFechaHora,
                actualizado.getFechaHora()
        );
    }

    @Test
    void deberiaEliminarUnMovimiento() {

        Movimiento movimiento =
                movimientoService.registrar(
                        cuenta,
                        categoria,
                        TipoMovimiento.EGRESO,
                        new BigDecimal("5000.00"),
                        LocalDateTime.of(
                                2026,
                                8,
                                15,
                                10,
                                0
                        ),
                        "Movimiento a eliminar"
                );

        Long movimientoId =
                movimiento.getId();

        movimientoService.eliminar(
                movimientoId
        );

        assertTrue(
                movimientoService.buscarPorId(
                        movimientoId
                ).isEmpty()
        );
    }

    @Test
    void deberiaRechazarMovimientoCuandoCuentaYCategoriaPertenecenADistintosPerfiles() {

        Usuario usuario1 =
                new Usuario(
                        "Ariel",
                        "Test",
                        "ariel.perfil1."
                                + System.nanoTime()
                                + "@test.com",
                        "hash"
                );

        Usuario usuario2 =
                new Usuario(
                        "Ariel",
                        "Test",
                        "ariel.perfil2."
                                + System.nanoTime()
                                + "@test.com",
                        "hash"
                );

        PerfilFinanciero perfil1 =
                new PerfilFinanciero(
                        "Perfil principal",
                        usuario1
                );

        PerfilFinanciero perfil2 =
                new PerfilFinanciero(
                        "Perfil secundario",
                        usuario2
                );

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Banco Test",
                        TipoInstitucionFinanciera.BANCO
                );

        Moneda moneda = entityManager
                .createQuery(
                        "SELECT m FROM Moneda m WHERE m.codigo = :codigo",
                        Moneda.class
                )
                .setParameter("codigo", "ARS")
                .getSingleResult();

        Cuenta cuenta =
                new Cuenta(
                        "Cuenta principal",
                        TipoCuenta.CAJA_AHORRO,
                        perfil1,
                        institucion,
                        moneda
                );

        Categoria categoria =
                new Categoria(
                        "Gastos",
                        perfil2
                );

        entityManager.getTransaction().begin();

        entityManager.persist(usuario1);
        entityManager.persist(usuario2);
        entityManager.persist(perfil1);
        entityManager.persist(perfil2);
        entityManager.persist(institucion);
        entityManager.persist(cuenta);
        entityManager.persist(categoria);

        entityManager.getTransaction().commit();

        assertThrows(
                IllegalArgumentException.class,
                () -> movimientoService.registrar(
                        cuenta,
                        categoria,
                        TipoMovimiento.EGRESO,
                        new BigDecimal("3000.00"),
                        LocalDateTime.now(),
                        "Movimiento inválido"
                )
        );
    }

    @Test
    void deberiaRechazarMovimientoCuandoLaCuentaEstaDesactivada() {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Test",
                        "ariel.cuenta.desactivada."
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

        Moneda moneda = entityManager
                .createQuery(
                        "SELECT m FROM Moneda m WHERE m.codigo = :codigo",
                        Moneda.class
                )
                .setParameter("codigo", "ARS")
                .getSingleResult();

        Cuenta cuenta =
                new Cuenta(
                        "Cuenta desactivada",
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

        cuenta.desactivar();

        entityManager.getTransaction().begin();

        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(institucion);
        entityManager.persist(cuenta);
        entityManager.persist(categoria);

        entityManager.getTransaction().commit();

        assertThrows(
                IllegalArgumentException.class,
                () -> movimientoService.registrar(
                        cuenta,
                        categoria,
                        TipoMovimiento.EGRESO,
                        new BigDecimal("3000.00"),
                        LocalDateTime.now(),
                        "Movimiento sobre cuenta desactivada"
                )
        );
    }

    @Test
    void deberiaRechazarCambioDeCategoriaCuandoPerteneceAOtroPerfil() {

        Usuario usuario1 =
                new Usuario(
                        "Ariel",
                        "Test",
                        "ariel.cambio.categoria1."
                                + System.nanoTime()
                                + "@test.com",
                        "hash"
                );

        Usuario usuario2 =
                new Usuario(
                        "Ariel",
                        "Test",
                        "ariel.cambio.categoria2."
                                + System.nanoTime()
                                + "@test.com",
                        "hash"
                );

        PerfilFinanciero perfil1 =
                new PerfilFinanciero(
                        "Perfil principal",
                        usuario1
                );

        PerfilFinanciero perfil2 =
                new PerfilFinanciero(
                        "Perfil secundario",
                        usuario2
                );

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(
                        "Banco Test",
                        TipoInstitucionFinanciera.BANCO
                );

        Moneda moneda = entityManager
                .createQuery(
                        "SELECT m FROM Moneda m WHERE m.codigo = :codigo",
                        Moneda.class
                )
                .setParameter("codigo", "ARS")
                .getSingleResult();

        Cuenta cuenta =
                new Cuenta(
                        "Cuenta principal",
                        TipoCuenta.CAJA_AHORRO,
                        perfil1,
                        institucion,
                        moneda
                );

        Categoria categoria1 =
                new Categoria(
                        "Gastos",
                        perfil1
                );

        Categoria categoria2 =
                new Categoria(
                        "Gastos personales",
                        perfil2
                );

        entityManager.getTransaction().begin();

        entityManager.persist(usuario1);
        entityManager.persist(usuario2);
        entityManager.persist(perfil1);
        entityManager.persist(perfil2);
        entityManager.persist(institucion);
        entityManager.persist(cuenta);
        entityManager.persist(categoria1);
        entityManager.persist(categoria2);

        entityManager.getTransaction().commit();

        Movimiento movimiento =
                movimientoService.registrar(
                        cuenta,
                        categoria1,
                        TipoMovimiento.EGRESO,
                        new BigDecimal("3000.00"),
                        LocalDateTime.now(),
                        "Movimiento válido"
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> movimientoService.cambiarCategoria(
                        movimiento.getId(),
                        categoria2
                )
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeBuscaMovimientoConIdNulo() {

        assertThrows(
                NullPointerException.class,
                () -> movimientoService.buscarPorId(null)
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeListaPorCuentaConIdNulo() {

        assertThrows(
                NullPointerException.class,
                () -> movimientoService.listarPorCuenta(null)
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeListaPorCategoriaConIdNulo() {

        assertThrows(
                NullPointerException.class,
                () -> movimientoService.listarPorCategoria(null)
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeModificaDescripcionConIdNulo() {

        assertThrows(
                NullPointerException.class,
                () -> movimientoService.modificarDescripcion(
                        null,
                        "Nueva descripción"
                )
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeModificaDescripcionConDescripcionNula() {

        Movimiento movimiento =
                movimientoService.registrar(
                        cuenta,
                        categoria,
                        TipoMovimiento.EGRESO,
                        new BigDecimal("5000.00"),
                        LocalDateTime.of(
                                2026,
                                8,
                                16,
                                10,
                                0
                        ),
                        "Compra"
                );

        assertThrows(
                NullPointerException.class,
                () -> movimientoService.modificarDescripcion(
                        movimiento.getId(),
                        null
                )
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeCambiaCategoriaConIdNulo() {

        assertThrows(
                NullPointerException.class,
                () -> movimientoService.cambiarCategoria(
                        null,
                        categoria
                )
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeCambiaCategoriaConCategoriaNula() {

        Movimiento movimiento =
                movimientoService.registrar(
                        cuenta,
                        categoria,
                        TipoMovimiento.EGRESO,
                        new BigDecimal("5000.00"),
                        LocalDateTime.of(
                                2026,
                                8,
                                16,
                                11,
                                0
                        ),
                        "Compra"
                );

        assertThrows(
                NullPointerException.class,
                () -> movimientoService.cambiarCategoria(
                        movimiento.getId(),
                        null
                )
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeModificaTipoConIdNulo() {

        assertThrows(
                NullPointerException.class,
                () -> movimientoService.modificarTipoMovimiento(
                        null,
                        TipoMovimiento.INGRESO
                )
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeModificaTipoConTipoNulo() {

        Movimiento movimiento =
                movimientoService.registrar(
                        cuenta,
                        categoria,
                        TipoMovimiento.INGRESO,
                        new BigDecimal("5000.00"),
                        LocalDateTime.of(
                                2026,
                                8,
                                16,
                                12,
                                0
                        ),
                        "Movimiento"
                );

        assertThrows(
                NullPointerException.class,
                () -> movimientoService.modificarTipoMovimiento(
                        movimiento.getId(),
                        null
                )
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeModificaImporteConIdNulo() {

        assertThrows(
                NullPointerException.class,
                () -> movimientoService.modificarImporte(
                        null,
                        new BigDecimal("5000.00")
                )
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeModificaImporteConImporteNulo() {

        Movimiento movimiento =
                movimientoService.registrar(
                        cuenta,
                        categoria,
                        TipoMovimiento.INGRESO,
                        new BigDecimal("5000.00"),
                        LocalDateTime.of(
                                2026,
                                8,
                                16,
                                13,
                                0
                        ),
                        "Movimiento"
                );

        assertThrows(
                NullPointerException.class,
                () -> movimientoService.modificarImporte(
                        movimiento.getId(),
                        null
                )
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeModificaFechaHoraConIdNulo() {

        assertThrows(
                NullPointerException.class,
                () -> movimientoService.modificarFechaHora(
                        null,
                        LocalDateTime.now()
                )
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeModificaFechaHoraConFechaNula() {

        Movimiento movimiento =
                movimientoService.registrar(
                        cuenta,
                        categoria,
                        TipoMovimiento.INGRESO,
                        new BigDecimal("5000.00"),
                        LocalDateTime.of(
                                2026,
                                8,
                                16,
                                14,
                                0
                        ),
                        "Movimiento"
                );

        assertThrows(
                NullPointerException.class,
                () -> movimientoService.modificarFechaHora(
                        movimiento.getId(),
                        null
                )
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeEliminaConIdNulo() {

        assertThrows(
                NullPointerException.class,
                () -> movimientoService.eliminar(null)
        );
    }

    @Test
    void deberiaDevolverOptionalVacioCuandoSeBuscaMovimientoInexistente() {

        Long movimientoIdInexistente = 999999L;

        assertTrue(
                movimientoService.buscarPorId(
                        movimientoIdInexistente
                ).isEmpty()
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeModificaTipoDeMovimientoInexistente() {

        Long movimientoIdInexistente = 999999L;

        assertThrows(
                IllegalArgumentException.class,
                () -> movimientoService.modificarTipoMovimiento(
                        movimientoIdInexistente,
                        TipoMovimiento.EGRESO
                )
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeModificaImporteDeMovimientoInexistente() {

        Long movimientoIdInexistente = 999999L;

        assertThrows(
                IllegalArgumentException.class,
                () -> movimientoService.modificarImporte(
                        movimientoIdInexistente,
                        new BigDecimal("5000.00")
                )
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeModificaFechaHoraDeMovimientoInexistente() {

        Long movimientoIdInexistente = 999999L;

        assertThrows(
                IllegalArgumentException.class,
                () -> movimientoService.modificarFechaHora(
                        movimientoIdInexistente,
                        LocalDateTime.now()
                )
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeEliminaUnMovimientoInexistente() {

        Long movimientoIdInexistente = 999999L;

        assertThrows(
                IllegalArgumentException.class,
                () -> movimientoService.eliminar(
                        movimientoIdInexistente
                )
        );
    }
}
