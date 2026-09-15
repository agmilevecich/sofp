package ar.com.agmilevecich.sofp.domain;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ObligacionTipoCambioJpaTest {

    @Test
    void deberiaPersistirLiquidacionConTipoCambioHistorico() {
        EntityManager em = JpaTestManager.createEntityManager();

        Usuario usuario = new Usuario(
                "Ariel",
                "Milevecich",
                "ariel.obligacion.tipo.cambio.jpa@test.com",
                "hash"
        );
        PerfilFinanciero perfil = new PerfilFinanciero("Personal", usuario);
        InstitucionFinanciera banco = new InstitucionFinanciera("Banco Santander", TipoInstitucionFinanciera.BANCO);
        Moneda ars = new Moneda("ARS", "Peso Argentino", 2, TipoMoneda.FIAT);
        Moneda usd = new Moneda("USD", "Dólar Estadounidense", 2, TipoMoneda.FIAT);
        Cuenta tarjeta = new Cuenta("Visa ARS", perfil, banco, ars, new BigDecimal("2000000.00"), 15, 10);
        Categoria categoria = new Categoria("Compra exterior", perfil);
        Movimiento movimiento = new Movimiento(
                tarjeta,
                categoria,
                usd,
                TipoMovimiento.EGRESO,
                new BigDecimal("100.00"),
                LocalDateTime.of(2026, 9, 15, 12, 0),
                "Consumo USD en tarjeta ARS",
                FormaPago.TARJETA_CREDITO
        );
        Obligacion obligacion = new Obligacion(movimiento);
        TipoCambio tipoCambio = new TipoCambio(
                usd,
                ars,
                new BigDecimal("1500.00"),
                LocalDateTime.of(2026, 9, 15, 13, 0),
                "Cotización manual"
        );
        obligacion.liquidar(tipoCambio);

        em.getTransaction().begin();
        em.persist(usuario);
        em.persist(perfil);
        em.persist(banco);
        em.persist(ars);
        em.persist(usd);
        em.persist(tarjeta);
        em.persist(categoria);
        em.persist(movimiento);
        em.persist(tipoCambio);
        em.persist(obligacion);
        em.getTransaction().commit();

        Long id = obligacion.getId();
        em.clear();

        Obligacion recuperada = em.find(Obligacion.class, id);

        assertNotNull(recuperada);
        assertEquals(new BigDecimal("100.00"), recuperada.getImporteOriginal());
        assertEquals(new BigDecimal("150000.00"), recuperada.getImporteLiquidacion());
        assertEquals(usd.getId(), recuperada.getTipoCambioLiquidacion().getMonedaOrigen().getId());
        assertEquals(ars.getId(), recuperada.getTipoCambioLiquidacion().getMonedaDestino().getId());
        assertEquals(0, new BigDecimal("1500.00").compareTo(recuperada.getTipoCambioLiquidacion().getCotizacion()));
        assertEquals(LocalDateTime.of(2026, 9, 15, 13, 0), recuperada.getTipoCambioLiquidacion().getFechaHora());
        assertEquals("Cotización manual", recuperada.getTipoCambioLiquidacion().getFuente());

        em.close();
        JpaTestManager.close();
    }
}
