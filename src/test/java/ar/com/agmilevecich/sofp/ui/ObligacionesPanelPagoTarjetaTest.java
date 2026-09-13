package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.FormaPago;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.Obligacion;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.TipoCuenta;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.CategoriaRepository;
import ar.com.agmilevecich.sofp.persistence.CuentaRepository;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import ar.com.agmilevecich.sofp.persistence.ObligacionRepository;
import ar.com.agmilevecich.sofp.service.CategoriaService;
import ar.com.agmilevecich.sofp.service.CuentaService;
import ar.com.agmilevecich.sofp.service.MovimientoService;
import ar.com.agmilevecich.sofp.service.ObligacionService;
import ar.com.agmilevecich.sofp.service.PagoTarjetaService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ObligacionesPanelPagoTarjetaTest {

    private EntityManager entityManager;
    private MovimientoRepository movimientoRepository;
    private ObligacionRepository obligacionRepository;
    private MovimientoService movimientoService;
    private ObligacionService obligacionService;
    private CuentaService cuentaService;
    private CategoriaService categoriaService;
    private PagoTarjetaService pagoTarjetaService;

    private Usuario usuario;
    private PerfilFinanciero perfil;
    private Cuenta tarjeta;
    private Cuenta cuentaPagadora;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();
        movimientoRepository = new MovimientoRepository(entityManager);
        obligacionRepository = new ObligacionRepository(entityManager);
        movimientoService = new MovimientoService(entityManager, movimientoRepository);
        obligacionService = new ObligacionService(entityManager, obligacionRepository);
        cuentaService = new CuentaService(
                new CuentaRepository(entityManager),
                movimientoRepository,
                entityManager
        );
        categoriaService = new CategoriaService(
                entityManager,
                new CategoriaRepository(entityManager)
        );
        pagoTarjetaService = new PagoTarjetaService(
                entityManager,
                movimientoRepository,
                obligacionRepository
        );

        usuario = new Usuario(
                "Juan",
                "Pérez",
                "juan.panel.pago." + System.nanoTime() + "@test.com",
                "hash"
        );
        perfil = new PerfilFinanciero("Perfil principal", usuario);
        usuario.agregarPerfilFinanciero(perfil);

        InstitucionFinanciera institucion = new InstitucionFinanciera(
                "Banco de Prueba",
                TipoInstitucionFinanciera.BANCO
        );
        Moneda moneda = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        tarjeta = new Cuenta(
                "Tarjeta principal",
                perfil,
                institucion,
                moneda,
                new BigDecimal("500000.00"),
                15,
                10
        );
        cuentaPagadora = new Cuenta(
                "Caja de ahorro",
                TipoCuenta.CAJA_AHORRO,
                perfil,
                institucion,
                moneda
        );
        categoria = new Categoria("Pago de tarjetas", perfil);

        entityManager.getTransaction().begin();
        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(institucion);
        entityManager.persist(moneda);
        entityManager.persist(tarjeta);
        entityManager.persist(cuentaPagadora);
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
    void deberiaRegistrarPagoDesdeLaUiComoSalidaRealDeFondos() {
        movimientoService.registrar(
                cuentaPagadora,
                categoria,
                new BigDecimal("20000.00"),
                LocalDateTime.of(2026, 9, 13, 10, 0),
                "Ingreso para pago",
                FormaPago.TRANSFERENCIA,
                usuario.getId()
        );

        MovimientoService movimientoDeTarjetaService = movimientoService;
        var movimientoTarjeta = movimientoDeTarjetaService.registrar(
                tarjeta,
                categoria,
                new BigDecimal("8000.00"),
                LocalDateTime.of(2026, 9, 12, 12, 0),
                "Compra con tarjeta",
                FormaPago.TARJETA_CREDITO,
                usuario.getId()
        );
        Obligacion obligacion = obligacionService.registrar(movimientoTarjeta);

        ObligacionesPanel panel = new ObligacionesPanel(
                obligacionService,
                pagoTarjetaService,
                cuentaService,
                categoriaService,
                perfil.getId(),
                usuario.getId()
        );

        panel.getObligacionesList().setSelectedIndex(0);
        panel.getCuentaPagadoraCombo().setSelectedItem(cuentaPagadora);
        panel.getCategoriaCombo().setSelectedItem(categoria);
        panel.getImportePagoField().setText("5000.00");

        panel.registrarPagoSeleccionado();
        entityManager.clear();

        Obligacion obligacionPersistida = obligacionRepository.buscarPorId(obligacion.getId()).orElseThrow();
        assertEquals(new BigDecimal("3000.00"), obligacionPersistida.getSaldoPendiente());

        var movimientosPagadora = movimientoRepository.listarPorCuenta(cuentaPagadora.getId());
        assertEquals(2, movimientosPagadora.size());
        assertTrue(movimientosPagadora.stream().anyMatch(movimiento ->
                movimiento.getTipoMovimiento().name().equals("EGRESO")
                        && movimiento.getFormaPago() == FormaPago.TRANSFERENCIA
                        && movimiento.getImporte().compareTo(new BigDecimal("5000.00")) == 0
        ));
    }
}
