package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.FormaPago;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.TipoCuenta;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.CategoriaRepository;
import ar.com.agmilevecich.sofp.persistence.CuentaRepository;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import ar.com.agmilevecich.sofp.service.CategoriaService;
import ar.com.agmilevecich.sofp.service.CuentaService;
import ar.com.agmilevecich.sofp.service.GastoService;
import ar.com.agmilevecich.sofp.service.MovimientoService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class GastosPanelTarjetaCreditoTest {

    private EntityManager entityManager;
    private CuentaService cuentaService;
    private CategoriaService categoriaService;
    private MovimientoService movimientoService;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();
        MovimientoRepository movimientoRepository = new MovimientoRepository(entityManager);
        movimientoService = new MovimientoService(entityManager, movimientoRepository);
        cuentaService = new CuentaService(
                new CuentaRepository(entityManager),
                movimientoRepository,
                entityManager
        );
        categoriaService = new CategoriaService(
                entityManager,
                new CategoriaRepository(entityManager),
                movimientoRepository
        );
    }

    @AfterEach
    void tearDown() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
        JpaTestManager.close();
    }

    @Test
    void deberiaMostrarSoloTarjetasActivasAlElegirTarjetaDeCredito() {
        Usuario usuario = new Usuario("Ariel", "Test", "ariel.gastos.tarjeta." + System.nanoTime(), "hash");
        PerfilFinanciero perfil = new PerfilFinanciero("Perfil principal", usuario);
        usuario.agregarPerfilFinanciero(perfil);
        InstitucionFinanciera institucion = new InstitucionFinanciera("Banco Test", TipoInstitucionFinanciera.BANCO);
        Moneda moneda = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);

        Cuenta cuenta = new Cuenta("Cuenta sueldo", TipoCuenta.CAJA_AHORRO, perfil, institucion, moneda);
        Cuenta tarjetaActiva = new Cuenta(
                "Visa Test",
                perfil,
                institucion,
                moneda,
                new BigDecimal("500000"),
                10,
                25
        );
        Cuenta tarjetaInactiva = new Cuenta(
                "Mastercard inactiva",
                perfil,
                institucion,
                moneda,
                new BigDecimal("300000"),
                15,
                5
        );
        tarjetaInactiva.desactivar();
        Categoria categoria = new Categoria("Supermercado", perfil);

        persistir(usuario, perfil, institucion, moneda, cuenta, tarjetaActiva, tarjetaInactiva, categoria);

        GastosPanel panel = new GastosPanel(
                new GastoService(movimientoService),
                cuentaService,
                categoriaService,
                perfil.getId(),
                usuario.getId()
        );

        assertEquals(2, panel.getCuentaComboBox().getItemCount());

        panel.getFormaPagoComboBox().setSelectedItem(FormaPago.TARJETA_CREDITO);

        assertEquals(1, panel.getCuentaComboBox().getItemCount());
        assertEquals(tarjetaActiva, panel.getCuentaComboBox().getItemAt(0));
        assertNull(panel.getCuentaComboBox().getSelectedItem());

        panel.getFormaPagoComboBox().setSelectedItem(FormaPago.TARJETA_DEBITO);

        assertEquals(2, panel.getCuentaComboBox().getItemCount());
        assertEquals(cuenta, panel.getCuentaComboBox().getItemAt(0));
        assertEquals(tarjetaActiva, panel.getCuentaComboBox().getItemAt(1));
        assertNull(panel.getCuentaComboBox().getSelectedItem());
    }

    private void persistir(Object... entidades) {
        entityManager.getTransaction().begin();
        for (Object entidad : entidades) {
            entityManager.persist(entidad);
        }
        entityManager.getTransaction().commit();
    }
}
