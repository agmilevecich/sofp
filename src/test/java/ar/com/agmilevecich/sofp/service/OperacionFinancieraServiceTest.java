package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.Movimiento;
import ar.com.agmilevecich.sofp.domain.OperacionFinanciera;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.TipoCuenta;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.domain.TipoMovimiento;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import ar.com.agmilevecich.sofp.persistence.OperacionFinancieraRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OperacionFinancieraServiceTest {

    private EntityManager entityManager;
    private OperacionFinancieraService operacionFinancieraService;

    private Usuario usuario;
    private PerfilFinanciero perfilFinanciero;
    private InstitucionFinanciera institucionFinanciera;
    private Moneda moneda;

    private Cuenta cuentaOrigen;
    private Cuenta cuentaDestino;

    private Categoria categoriaOrigen;
    private Categoria categoriaDestino;

    @BeforeEach
    void setUp() {

        entityManager =
                JpaTestManager.createEntityManager();

        MovimientoRepository movimientoRepository =
                new MovimientoRepository(
                        entityManager
                );

        OperacionFinancieraRepository operacionFinancieraRepository =
                new OperacionFinancieraRepository(
                        entityManager
                );

        operacionFinancieraService =
                new OperacionFinancieraService(
                        entityManager,
                        movimientoRepository,
                        operacionFinancieraRepository
                );

        usuario =
                new Usuario(
                        "Ariel",
                        "Milevecich",
                        "ariel.operacion.service."
                                + System.nanoTime()
                                + "@test.com",
                        "hash"
                );

        perfilFinanciero =
                new PerfilFinanciero(
                        "Perfil principal",
                        usuario
                );

        usuario.agregarPerfilFinanciero(
                perfilFinanciero
        );

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

        cuentaOrigen =
                new Cuenta(
                        "Cuenta origen",
                        TipoCuenta.CAJA_AHORRO,
                        perfilFinanciero,
                        institucionFinanciera,
                        moneda
                );

        cuentaDestino =
                new Cuenta(
                        "Cuenta destino",
                        TipoCuenta.CUENTA_CORRIENTE,
                        perfilFinanciero,
                        institucionFinanciera,
                        moneda
                );

        categoriaOrigen =
                new Categoria(
                        "Transferencias enviadas",
                        perfilFinanciero
                );

        categoriaDestino =
                new Categoria(
                        "Transferencias recibidas",
                        perfilFinanciero
                );

        entityManager.getTransaction().begin();

        entityManager.persist(usuario);
        entityManager.persist(perfilFinanciero);
        entityManager.persist(institucionFinanciera);
        entityManager.persist(moneda);
        entityManager.persist(cuentaOrigen);
        entityManager.persist(cuentaDestino);
        entityManager.persist(categoriaOrigen);
        entityManager.persist(categoriaDestino);

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
    void deberiaRegistrarUnaTransferencia() {

        OperacionFinanciera operacion =
                operacionFinancieraService.transferir(
                        usuario.getId(),
                        cuentaOrigen,
                        cuentaDestino,
                        categoriaOrigen,
                        categoriaDestino,
                        new BigDecimal("100000.00"),
                        LocalDateTime.of(
                                2026,
                                8,
                                21,
                                10,
                                0
                        ),
                        "Transferencia entre cuentas"
                );

        assertNotNull(operacion);

        assertNotNull(
                operacion.getId()
        );

        assertEquals(
                cuentaOrigen.getId(),
                operacion.getCuentaOrigen().getId()
        );

        assertEquals(
                cuentaDestino.getId(),
                operacion.getCuentaDestino().getId()
        );

        assertEquals(
                new BigDecimal("100000.00"),
                operacion.getImporte()
        );
    }

    @Test
    void deberiaCrearUnEgresoEnLaCuentaOrigen() {

        operacionFinancieraService.transferir(
                usuario.getId(),
                cuentaOrigen,
                cuentaDestino,
                categoriaOrigen,
                categoriaDestino,
                new BigDecimal("100000.00"),
                LocalDateTime.of(
                        2026,
                        8,
                        21,
                        10,
                        0
                ),
                "Transferencia entre cuentas"
        );

        List<Movimiento> movimientos =
                entityManager.createQuery(
                                """
                                SELECT m
                                FROM Movimiento m
                                WHERE m.cuenta.id = :cuentaId
                                """,
                                Movimiento.class
                        )
                        .setParameter(
                                "cuentaId",
                                cuentaOrigen.getId()
                        )
                        .getResultList();

        assertEquals(
                1,
                movimientos.size()
        );

        Movimiento movimiento =
                movimientos.get(0);

        assertEquals(
                TipoMovimiento.EGRESO,
                movimiento.getTipoMovimiento()
        );

        assertEquals(
                new BigDecimal("100000.00"),
                movimiento.getImporte()
        );

        assertEquals(
                categoriaOrigen.getId(),
                movimiento.getCategoria().getId()
        );
    }

    @Test
    void deberiaCrearUnIngresoEnLaCuentaDestino() {

        operacionFinancieraService.transferir(
                usuario.getId(),
                cuentaOrigen,
                cuentaDestino,
                categoriaOrigen,
                categoriaDestino,
                new BigDecimal("100000.00"),
                LocalDateTime.of(
                        2026,
                        8,
                        21,
                        10,
                        0
                ),
                "Transferencia entre cuentas"
        );

        List<Movimiento> movimientos =
                entityManager.createQuery(
                                """
                                SELECT m
                                FROM Movimiento m
                                WHERE m.cuenta.id = :cuentaId
                                """,
                                Movimiento.class
                        )
                        .setParameter(
                                "cuentaId",
                                cuentaDestino.getId()
                        )
                        .getResultList();

        assertEquals(
                1,
                movimientos.size()
        );

        Movimiento movimiento =
                movimientos.get(0);

        assertEquals(
                TipoMovimiento.INGRESO,
                movimiento.getTipoMovimiento()
        );

        assertEquals(
                new BigDecimal("100000.00"),
                movimiento.getImporte()
        );

        assertEquals(
                categoriaDestino.getId(),
                movimiento.getCategoria().getId()
        );
    }

    @Test
    void deberiaCrearAmbosMovimientosConLaMismaFechaHora() {

        LocalDateTime fechaHora =
                LocalDateTime.of(
                        2026,
                        8,
                        21,
                        10,
                        30
                );

        operacionFinancieraService.transferir(
                usuario.getId(),
                cuentaOrigen,
                cuentaDestino,
                categoriaOrigen,
                categoriaDestino,
                new BigDecimal("50000.00"),
                fechaHora,
                "Transferencia"
        );

        List<Movimiento> movimientos =
                entityManager.createQuery(
                                """
                                SELECT m
                                FROM Movimiento m
                                ORDER BY m.id
                                """,
                                Movimiento.class
                        )
                        .getResultList();

        assertEquals(
                2,
                movimientos.size()
        );

        assertEquals(
                fechaHora,
                movimientos.get(0).getFechaHora()
        );

        assertEquals(
                fechaHora,
                movimientos.get(1).getFechaHora()
        );
    }

    @Test
    void deberiaRechazarUsuarioIdNulo() {

        assertThrows(
                NullPointerException.class,
                () -> operacionFinancieraService.transferir(
                        null,
                        cuentaOrigen,
                        cuentaDestino,
                        categoriaOrigen,
                        categoriaDestino,
                        new BigDecimal("100000.00"),
                        LocalDateTime.now(),
                        "Transferencia"
                )
        );
    }

    @Test
    void deberiaRechazarCuentaOrigenNula() {

        assertThrows(
                NullPointerException.class,
                () -> operacionFinancieraService.transferir(
                        usuario.getId(),
                        null,
                        cuentaDestino,
                        categoriaOrigen,
                        categoriaDestino,
                        new BigDecimal("100000.00"),
                        LocalDateTime.now(),
                        "Transferencia"
                )
        );
    }

    @Test
    void deberiaRechazarCuentaDestinoNula() {

        assertThrows(
                NullPointerException.class,
                () -> operacionFinancieraService.transferir(
                        usuario.getId(),
                        cuentaOrigen,
                        null,
                        categoriaOrigen,
                        categoriaDestino,
                        new BigDecimal("100000.00"),
                        LocalDateTime.now(),
                        "Transferencia"
                )
        );
    }

    @Test
    void deberiaRechazarCategoriaOrigenNula() {

        assertThrows(
                NullPointerException.class,
                () -> operacionFinancieraService.transferir(
                        usuario.getId(),
                        cuentaOrigen,
                        cuentaDestino,
                        null,
                        categoriaDestino,
                        new BigDecimal("100000.00"),
                        LocalDateTime.now(),
                        "Transferencia"
                )
        );
    }

    @Test
    void deberiaRechazarCategoriaDestinoNula() {

        assertThrows(
                NullPointerException.class,
                () -> operacionFinancieraService.transferir(
                        usuario.getId(),
                        cuentaOrigen,
                        cuentaDestino,
                        categoriaOrigen,
                        null,
                        new BigDecimal("100000.00"),
                        LocalDateTime.now(),
                        "Transferencia"
                )
        );
    }

    @Test
    void deberiaRechazarImporteNulo() {

        assertThrows(
                NullPointerException.class,
                () -> operacionFinancieraService.transferir(
                        usuario.getId(),
                        cuentaOrigen,
                        cuentaDestino,
                        categoriaOrigen,
                        categoriaDestino,
                        null,
                        LocalDateTime.now(),
                        "Transferencia"
                )
        );
    }

    @Test
    void deberiaRechazarImporteCero() {

        assertThrows(
                IllegalArgumentException.class,
                () -> operacionFinancieraService.transferir(
                        usuario.getId(),
                        cuentaOrigen,
                        cuentaDestino,
                        categoriaOrigen,
                        categoriaDestino,
                        BigDecimal.ZERO,
                        LocalDateTime.now(),
                        "Transferencia"
                )
        );
    }

    @Test
    void deberiaRechazarImporteNegativo() {

        assertThrows(
                IllegalArgumentException.class,
                () -> operacionFinancieraService.transferir(
                        usuario.getId(),
                        cuentaOrigen,
                        cuentaDestino,
                        categoriaOrigen,
                        categoriaDestino,
                        new BigDecimal("-100.00"),
                        LocalDateTime.now(),
                        "Transferencia"
                )
        );
    }

    @Test
    void deberiaRechazarCuentaOrigenInactiva() {

        cuentaOrigen.desactivar();

        assertThrows(
                IllegalArgumentException.class,
                () -> operacionFinancieraService.transferir(
                        usuario.getId(),
                        cuentaOrigen,
                        cuentaDestino,
                        categoriaOrigen,
                        categoriaDestino,
                        new BigDecimal("100000.00"),
                        LocalDateTime.now(),
                        "Transferencia"
                )
        );
    }

    @Test
    void deberiaRechazarCuentaDestinoInactiva() {

        cuentaDestino.desactivar();

        assertThrows(
                IllegalArgumentException.class,
                () -> operacionFinancieraService.transferir(
                        usuario.getId(),
                        cuentaOrigen,
                        cuentaDestino,
                        categoriaOrigen,
                        categoriaDestino,
                        new BigDecimal("100000.00"),
                        LocalDateTime.now(),
                        "Transferencia"
                )
        );
    }

    @Test
    void deberiaRechazarCategoriaOrigenDeOtroPerfil() {

        Usuario otroUsuario =
                new Usuario(
                        "Otro",
                        "Usuario",
                        "otro.operacion.service."
                                + System.nanoTime()
                                + "@test.com",
                        "hash"
                );

        PerfilFinanciero otroPerfil =
                new PerfilFinanciero(
                        "Otro perfil",
                        otroUsuario
                );

        Categoria otraCategoria =
                new Categoria(
                        "Otra categoría",
                        otroPerfil
                );

        entityManager.getTransaction().begin();

        entityManager.persist(otroUsuario);
        entityManager.persist(otroPerfil);
        entityManager.persist(otraCategoria);

        entityManager.getTransaction().commit();

        assertThrows(
                IllegalArgumentException.class,
                () -> operacionFinancieraService.transferir(
                        usuario.getId(),
                        cuentaOrigen,
                        cuentaDestino,
                        otraCategoria,
                        categoriaDestino,
                        new BigDecimal("100000.00"),
                        LocalDateTime.now(),
                        "Transferencia"
                )
        );
    }

    @Test
    void deberiaRechazarCategoriaDestinoDeOtroPerfil() {

        Usuario otroUsuario =
                new Usuario(
                        "Otro",
                        "Usuario",
                        "otro.destino.service."
                                + System.nanoTime()
                                + "@test.com",
                        "hash"
                );

        PerfilFinanciero otroPerfil =
                new PerfilFinanciero(
                        "Otro perfil",
                        otroUsuario
                );

        Categoria otraCategoria =
                new Categoria(
                        "Otra categoría",
                        otroPerfil
                );

        entityManager.getTransaction().begin();

        entityManager.persist(otroUsuario);
        entityManager.persist(otroPerfil);
        entityManager.persist(otraCategoria);

        entityManager.getTransaction().commit();

        assertThrows(
                IllegalArgumentException.class,
                () -> operacionFinancieraService.transferir(
                        usuario.getId(),
                        cuentaOrigen,
                        cuentaDestino,
                        categoriaOrigen,
                        otraCategoria,
                        new BigDecimal("100000.00"),
                        LocalDateTime.now(),
                        "Transferencia"
                )
        );
    }

    @Test
    void deberiaRechazarCuentasConMonedasDiferentes() {

        Moneda otraMoneda =
                new Moneda(
                        "USD",
                        "Dólar estadounidense",
                        2,
                        TipoMoneda.FIAT
                );

        entityManager.getTransaction().begin();

        entityManager.persist(otraMoneda);

        entityManager.getTransaction().commit();

        Cuenta cuentaDestinoDiferente =
                new Cuenta(
                        "Cuenta destino USD",
                        TipoCuenta.CUENTA_CORRIENTE,
                        perfilFinanciero,
                        institucionFinanciera,
                        otraMoneda
                );

        entityManager.getTransaction().begin();

        entityManager.persist(cuentaDestinoDiferente);

        entityManager.getTransaction().commit();

        assertThrows(
                IllegalArgumentException.class,
                () -> operacionFinancieraService.transferir(
                        usuario.getId(),
                        cuentaOrigen,
                        cuentaDestinoDiferente,
                        categoriaOrigen,
                        categoriaDestino,
                        new BigDecimal("100000.00"),
                        LocalDateTime.now(),
                        "Transferencia"
                )
        );
    }

    @Test
    void deberiaRechazarFechaHoraNula() {

        assertThrows(
                NullPointerException.class,
                () -> operacionFinancieraService.transferir(
                        usuario.getId(),
                        cuentaOrigen,
                        cuentaDestino,
                        categoriaOrigen,
                        categoriaDestino,
                        new BigDecimal("100000.00"),
                        null,
                        "Transferencia"
                )
        );
    }

    @Test
    void deberiaRechazarDescripcionNula() {

        assertThrows(
                NullPointerException.class,
                () -> operacionFinancieraService.transferir(
                        usuario.getId(),
                        cuentaOrigen,
                        cuentaDestino,
                        categoriaOrigen,
                        categoriaDestino,
                        new BigDecimal("100000.00"),
                        LocalDateTime.now(),
                        null
                )
        );
    }

    @Test
    void deberiaNoPersistirMovimientosCuandoLaCuentaOrigenEstaInactiva() {

        cuentaOrigen.desactivar();

        assertThrows(
                IllegalArgumentException.class,
                () -> operacionFinancieraService.transferir(
                        usuario.getId(),
                        cuentaOrigen,
                        cuentaDestino,
                        categoriaOrigen,
                        categoriaDestino,
                        new BigDecimal("100000.00"),
                        LocalDateTime.now(),
                        "Transferencia"
                )
        );

        assertEquals(
                0,
                entityManager.createQuery(
                                "SELECT m FROM Movimiento m",
                                Movimiento.class
                        )
                        .getResultList()
                        .size()
        );
    }

    @Test
    void deberiaNoPersistirMovimientosCuandoLasMonedasSonDiferentes() {

        Moneda otraMoneda =
                new Moneda(
                        "USD",
                        "Dólar estadounidense",
                        2,
                        TipoMoneda.FIAT
                );

        entityManager.getTransaction().begin();

        entityManager.persist(otraMoneda);

        entityManager.getTransaction().commit();

        Cuenta cuentaDestinoDiferente =
                new Cuenta(
                        "Cuenta destino USD",
                        TipoCuenta.CUENTA_CORRIENTE,
                        perfilFinanciero,
                        institucionFinanciera,
                        otraMoneda
                );

        entityManager.getTransaction().begin();

        entityManager.persist(cuentaDestinoDiferente);

        entityManager.getTransaction().commit();

        assertThrows(
                IllegalArgumentException.class,
                () -> operacionFinancieraService.transferir(
                        usuario.getId(),
                        cuentaOrigen,
                        cuentaDestinoDiferente,
                        categoriaOrigen,
                        categoriaDestino,
                        new BigDecimal("100000.00"),
                        LocalDateTime.now(),
                        "Transferencia"
                )
        );

        assertEquals(
                0,
                entityManager.createQuery(
                                "SELECT m FROM Movimiento m",
                                Movimiento.class
                        )
                        .getResultList()
                        .size()
        );
    }

    @Test
    void deberiaAsociarAmbosMovimientosALaMismaOperacionFinanciera() {

        OperacionFinanciera operacion =
                operacionFinancieraService.transferir(
                        usuario.getId(),
                        cuentaOrigen,
                        cuentaDestino,
                        categoriaOrigen,
                        categoriaDestino,
                        new BigDecimal("75000.00"),
                        LocalDateTime.of(
                                2026,
                                8,
                                23,
                                10,
                                0
                        ),
                        "Transferencia"
                );

        List<Movimiento> movimientos =
                entityManager.createQuery(
                                """
                                SELECT m
                                FROM Movimiento m
                                ORDER BY m.id
                                """,
                                Movimiento.class
                        )
                        .getResultList();

        assertEquals(
                2,
                movimientos.size()
        );

        assertNotNull(
                movimientos.get(0).getOperacionFinanciera()
        );

        assertNotNull(
                movimientos.get(1).getOperacionFinanciera()
        );

        assertEquals(
                operacion.getId(),
                movimientos.get(0)
                        .getOperacionFinanciera()
                        .getId()
        );

        assertEquals(
                operacion.getId(),
                movimientos.get(1)
                        .getOperacionFinanciera()
                        .getId()
        );
    }

    @Test
    void deberiaRechazarMismaCuentaComoOrigenYDestino() {

        assertThrows(
                IllegalArgumentException.class,
                () -> operacionFinancieraService.transferir(
                        usuario.getId(),
                        cuentaOrigen,
                        cuentaOrigen,
                        categoriaOrigen,
                        categoriaDestino,
                        new BigDecimal("100000.00"),
                        LocalDateTime.now(),
                        "Transferencia"
                )
        );

        assertEquals(
                0,
                entityManager.createQuery(
                                "SELECT m FROM Movimiento m",
                                Movimiento.class
                        )
                        .getResultList()
                        .size()
        );
    }
}
