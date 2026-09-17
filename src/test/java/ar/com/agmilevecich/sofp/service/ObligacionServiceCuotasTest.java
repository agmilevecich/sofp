package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.CicloFacturacion;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.Cuota;
import ar.com.agmilevecich.sofp.domain.EstadoObligacion;
import ar.com.agmilevecich.sofp.domain.FormaPago;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.Movimiento;
import ar.com.agmilevecich.sofp.domain.Obligacion;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import ar.com.agmilevecich.sofp.persistence.ObligacionRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ObligacionServiceCuotasTest {

    private EntityManager entityManager;
    private GastoService gastoService;
    private ObligacionService obligacionService;
    private Cuenta tarjeta;
    private Categoria categoria;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();

        MovimientoRepository movimientoRepository = new MovimientoRepository(entityManager);
        MovimientoService movimientoService = new MovimientoService(entityManager, movimientoRepository);
        obligacionService = new ObligacionService(entityManager, new ObligacionRepository(entityManager));
        gastoService = new GastoService(movimientoService, obligacionService);

        usuario = new Usuario(
                "Juan", "Pérez", "juan.cuotas." + System.nanoTime() + "@test.com", "hash"
        );
        PerfilFinanciero perfil = new PerfilFinanciero("Perfil principal", usuario);
        usuario.agregarPerfilFinanciero(perfil);
        InstitucionFinanciera institucion = new InstitucionFinanciera(
                "Banco de Prueba", TipoInstitucionFinanciera.BANCO
        );
        Moneda ars = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        tarjeta = new Cuenta(
                "Tarjeta principal", perfil, institucion, ars,
                new BigDecimal("100000.00"), 15, 10
        );
        categoria = new Categoria("Alimentos", perfil);

        entityManager.getTransaction().begin();
        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(institucion);
        entityManager.persist(ars);
        entityManager.persist(tarjeta);
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
    void deberiaAsignarCadaCuotaAlCicloDeFacturacionCorrespondiente() {
        Obligacion obligacion = registrarCompraEnTresCuotas(new BigDecimal("10000.00"));

        List<Cuota> cuotas = obligacion.getCuotas();

        assertEquals(3, cuotas.size());
        assertEquals(new BigDecimal("3333.33"), cuotas.get(0).getImporteOriginal());
        assertEquals(new BigDecimal("3333.33"), cuotas.get(1).getImporteOriginal());
        assertEquals(new BigDecimal("3333.34"), cuotas.get(2).getImporteOriginal());

        assertCiclo(cuotas.get(0), LocalDate.of(2026, 9, 15));
        assertCiclo(cuotas.get(1), LocalDate.of(2026, 10, 15));
        assertCiclo(cuotas.get(2), LocalDate.of(2026, 11, 15));
    }

    @Test
    void deberiaMantenerSaldoYEstadoDeLaObligacionAlGenerarCuotas() {
        Obligacion obligacion = registrarCompraEnTresCuotas(new BigDecimal("10000.00"));

        assertEquals(new BigDecimal("10000.00"), obligacion.getSaldoPendiente());
        assertEquals(EstadoObligacion.PENDIENTE, obligacion.getEstado());
        assertFalse(obligacion.getCuotas().isEmpty());
    }

    @Test
    void deberiaAsignarLasCuotasPosterioresAunqueLaCompraEsteAntesDelCierre() {
        Movimiento movimiento = gastoService.registrar(
                tarjeta,
                categoria,
                new BigDecimal("9000.00"),
                LocalDateTime.of(2026, 9, 14, 12, 0),
                "Compra con tarjeta en cuotas",
                FormaPago.TARJETA_CREDITO,
                usuario.getId()
        );

        Obligacion obligacion = obligacionService.buscarPorMovimientoOrigen(movimiento.getId()).orElseThrow();

        assertEquals(LocalDate.of(2026, 9, 15), obligacion.getCicloFacturacion().getFechaCierre());
        assertEquals(LocalDate.of(2026, 9, 15), obligacion.getCuotas().get(0).getFechaCierreCiclo());
        assertEquals(LocalDate.of(2026, 10, 15), obligacion.getCuotas().get(1).getFechaCierreCiclo());
        assertEquals(LocalDate.of(2026, 11, 15), obligacion.getCuotas().get(2).getFechaCierreCiclo());
    }

    private Obligacion registrarCompraEnTresCuotas(BigDecimal importe) {
        Movimiento movimiento = gastoService.registrar(
                tarjeta,
                categoria,
                importe,
                LocalDateTime.of(2026, 9, 10, 12, 0),
                "Compra con tarjeta en cuotas",
                FormaPago.TARJETA_CREDITO,
                usuario.getId()
        );
        return obligacionService.buscarPorMovimientoOrigen(movimiento.getId()).orElseThrow();
    }

    private void assertCiclo(Cuota cuota, LocalDate fechaCierre) {
        assertEquals(fechaCierre, cuota.getFechaCierreCiclo());
        CicloFacturacion ciclo = tarjeta.calcularCicloFacturacion(cuota.getFechaInicioCiclo());
        assertEquals(fechaCierre, ciclo.getFechaCierre());
    }
}
