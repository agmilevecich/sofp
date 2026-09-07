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

class IngresoServiceTest {

    private EntityManager entityManager;
    private IngresoService ingresoService;
    private Usuario usuario;
    private Cuenta cuenta;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();
        MovimientoService movimientoService = new MovimientoService(
                entityManager,
                new MovimientoRepository(entityManager)
        );
        ingresoService = new IngresoService(movimientoService);

        usuario = new Usuario(
                "Juan",
                "Pérez",
                "juan." + System.nanoTime() + "@test.com",
                "hash"
        );
        PerfilFinanciero perfilFinanciero = new PerfilFinanciero(
                "Perfil principal",
                usuario
        );
        usuario.agregarPerfilFinanciero(perfilFinanciero);

        InstitucionFinanciera institucionFinanciera = new InstitucionFinanciera(
                "Banco de Prueba",
                TipoInstitucionFinanciera.BANCO
        );
        Moneda moneda = new Moneda(
                "ARS",
                "Peso argentino",
                2,
                TipoMoneda.FIAT
        );
        cuenta = new Cuenta(
                "Cuenta principal",
                TipoCuenta.CAJA_AHORRO,
                perfilFinanciero,
                institucionFinanciera,
                moneda
        );
        categoria = new Categoria("Sueldos", perfilFinanciero);

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
        if (entityManager != null && entityManager.isOpen()) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            entityManager.close();
        }
        JpaTestManager.close();
    }

    @Test
    void deberiaRegistrarUnIngresoSinRequerirFondosPrevios() {
        Movimiento movimiento = ingresoService.registrar(
                cuenta,
                categoria,
                new BigDecimal("150000.00"),
                LocalDateTime.of(2026, 9, 7, 10, 0),
                "Sueldo",
                usuario.getId()
        );

        assertNotNull(movimiento);
        assertNotNull(movimiento.getId());
        assertEquals(TipoMovimiento.INGRESO, movimiento.getTipoMovimiento());
        assertEquals(new BigDecimal("150000.00"), movimiento.getImporte());
        assertEquals("Sueldo", movimiento.getDescripcion());
    }

    @Test
    void deberiaRechazarUsuarioNulo() {
        assertThrows(
                NullPointerException.class,
                () -> ingresoService.registrar(
                        cuenta,
                        categoria,
                        new BigDecimal("1000.00"),
                        LocalDateTime.of(2026, 9, 7, 11, 0),
                        "Ingreso",
                        null
                )
        );
    }

    @Test
    void deberiaRechazarUsuarioQueNoEsPropietario() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ingresoService.registrar(
                        cuenta,
                        categoria,
                        new BigDecimal("1000.00"),
                        LocalDateTime.of(2026, 9, 7, 11, 30),
                        "Ingreso",
                        999999L
                )
        );
    }

    @Test
    void deberiaRechazarDependenciaNula() {
        assertThrows(
                NullPointerException.class,
                () -> new IngresoService(null)
        );
    }
}
