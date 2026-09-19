package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.TipoCambio;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.persistence.TipoCambioRepository;
import ar.com.agmilevecich.sofp.service.TipoCambioService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TipoCambioDialogTest {

    private EntityManager entityManager;
    private Moneda ars;
    private Moneda usd;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();
        ars = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        usd = new Moneda("USD", "Dólar estadounidense", 2, TipoMoneda.FIAT);
    }

    @AfterEach
    void tearDown() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
        JpaTestManager.close();
    }

    @Test
    void deberiaCrearCotizacionDesdeLosCamposIngresados() {
        TipoCambioDialog dialog = new TipoCambioDialog(
                new TipoCambioService(new TipoCambioRepository(entityManager)),
                usd,
                ars,
                LocalDateTime.of(2026, 9, 15, 17, 0)
        );

        dialog.getCotizacionField().setText("1500,25");
        dialog.getFechaField().setText("15/09/2026");
        dialog.getHoraField().setText("17:30");
        dialog.getFuenteField().setText("Fuente test");

        TipoCambio tipoCambio = dialog.crearTipoCambio();

        assertEquals(new BigDecimal("1500.25"), tipoCambio.getCotizacion());
        assertEquals(LocalDateTime.of(2026, 9, 15, 17, 30), tipoCambio.getFechaHora());
        assertEquals("Fuente test", tipoCambio.getFuente());
        assertEquals("USD", tipoCambio.getMonedaOrigen().getCodigo());
        assertEquals("ARS", tipoCambio.getMonedaDestino().getCodigo());
    }

    @Test
    void deberiaRechazarFechaInvalida() {
        TipoCambioDialog dialog = new TipoCambioDialog(
                new TipoCambioService(new TipoCambioRepository(entityManager)),
                usd,
                ars,
                LocalDateTime.of(2026, 9, 15, 17, 0)
        );
        dialog.getCotizacionField().setText("1500");
        dialog.getFechaField().setText("fecha incorrecta");

        assertThrows(IllegalArgumentException.class, dialog::crearTipoCambio);
    }
}
