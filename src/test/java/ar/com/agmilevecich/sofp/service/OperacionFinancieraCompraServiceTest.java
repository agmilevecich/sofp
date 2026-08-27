package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Activo;
import ar.com.agmilevecich.sofp.domain.Bono;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.Movimiento;
import ar.com.agmilevecich.sofp.domain.MovimientoActivo;
import ar.com.agmilevecich.sofp.domain.OperacionFinanciera;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.TipoCuenta;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.domain.TipoMovimiento;
import ar.com.agmilevecich.sofp.domain.TipoMovimientoActivo;
import ar.com.agmilevecich.sofp.domain.TipoOperacionFinanciera;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import ar.com.agmilevecich.sofp.persistence.OperacionFinancieraRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OperacionFinancieraCompraServiceTest {

    private EntityManager entityManager;
    private OperacionFinancieraService operacionFinancieraService;

    private Cuenta cuentaOrigen;
    private Categoria categoriaOrigen;
    private Activo activo;

    @BeforeEach
    void setUp() {

        entityManager = JpaTestManager.createEntityManager();

        MovimientoRepository movimientoRepository =
                new MovimientoRepository(entityManager);

        OperacionFinancieraRepository operacionFinancieraRepository =
                new OperacionFinancieraRepository(entityManager);

        operacionFinancieraService =
                new OperacionFinancieraService(
                        entityManager,
                        movimientoRepository,
                        operacionFinancieraRepository
                );

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Milevecich",
                        "operacion.compra."
                                + System.nanoTime()
                                + "@test.com",
                        "hash"
                );

        PerfilFinanciero perfil =
                new PerfilFinanciero(
                        "Perfil principal",
                        usuario
                );

        InstitucionFinanciera banco =
                new InstitucionFinanciera(
                        "Banco de Prueba",
                        TipoInstitucionFinanciera.BANCO
                );

        Moneda moneda =
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
                        perfil,
                        banco,
                        moneda
                );

        categoriaOrigen =
                new Categoria(
                        "Inversiones",
                        perfil
                );

        activo =
                new Bono(
                        "Bono GD30",
                        moneda
                );

        entityManager.getTransaction().begin();

        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(banco);
        entityManager.persist(moneda);
        entityManager.persist(cuentaOrigen);
        entityManager.persist(categoriaOrigen);
        entityManager.persist(activo);

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
    void deberiaComprarUnActivo() {

        LocalDateTime fechaHora =
                LocalDateTime.of(
                        2026,
                        8,
                        27,
                        10,
                        0
                );

        BigDecimal cantidad =
                new BigDecimal("100");

        BigDecimal precioUnitario =
                new BigDecimal("125");

        BigDecimal importeEsperado =
                new BigDecimal("12500");

        OperacionFinanciera operacion =
                operacionFinancieraService.comprarActivo(
                        cuentaOrigen,
                        categoriaOrigen,
                        activo,
                        cantidad,
                        precioUnitario,
                        fechaHora,
                        "Compra Bono GD30"
                );

        assertNotNull(operacion);
        assertNotNull(operacion.getId());

        assertEquals(
                TipoOperacionFinanciera.COMPRA,
                operacion.getTipoOperacion()
        );

        assertEquals(
                cuentaOrigen.getId(),
                operacion.getCuentaOrigen().getId()
        );

        assertEquals(
                importeEsperado,
                operacion.getImporte()
        );

        assertEquals(
                1,
                operacion.getMovimientos().size()
        );

        Movimiento movimiento =
                operacion.getMovimientos().get(0);

        assertEquals(
                TipoMovimiento.EGRESO,
                movimiento.getTipoMovimiento()
        );

        assertEquals(
                importeEsperado,
                movimiento.getImporte()
        );

        assertEquals(
                cuentaOrigen.getId(),
                movimiento.getCuenta().getId()
        );

        assertEquals(
                1,
                operacion.getMovimientosActivos().size()
        );

        MovimientoActivo movimientoActivo =
                operacion.getMovimientosActivos().get(0);

        assertEquals(
                activo.getId(),
                movimientoActivo.getActivo().getId()
        );

        assertEquals(
                TipoMovimientoActivo.COMPRA,
                movimientoActivo.getTipoMovimiento()
        );

        assertEquals(
                cantidad,
                movimientoActivo.getCantidad()
        );

        assertEquals(
                precioUnitario,
                movimientoActivo.getPrecioUnitario()
        );
    }
}
