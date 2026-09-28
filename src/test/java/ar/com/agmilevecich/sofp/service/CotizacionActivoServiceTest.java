package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Activo;
import ar.com.agmilevecich.sofp.domain.Bono;
import ar.com.agmilevecich.sofp.domain.CotizacionActivo;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.persistence.CotizacionActivoRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CotizacionActivoServiceTest {

    @Test
    void deberiaRegistrarNuevaCotizacion() {
        EntityManager em = JpaTestManager.createEntityManager();
        try {
            Bono activo = persistirActivo(em);
            CotizacionActivoService service = service(em);

            em.getTransaction().begin();
            CotizacionActivo cotizacion = service.registrarCotizacion(
                    activo, LocalDate.of(2026, 9, 28), new BigDecimal("150"));
            em.getTransaction().commit();

            assertNotNull(cotizacion.getId());
            assertEquals(new BigDecimal("150"), cotizacion.getPrecio());
        } finally {
            em.close();
        }
    }

    @Test
    void deberiaActualizarCotizacionDeLaMismaFecha() {
        EntityManager em = JpaTestManager.createEntityManager();
        try {
            Bono activo = persistirActivo(em);
            CotizacionActivoService service = service(em);

            em.getTransaction().begin();
            service.registrarCotizacion(activo, LocalDate.of(2026, 9, 28), new BigDecimal("150"));
            em.getTransaction().commit();

            em.getTransaction().begin();
            CotizacionActivo actualizada = service.registrarCotizacion(
                    activo, LocalDate.of(2026, 9, 28), new BigDecimal("155"));
            em.getTransaction().commit();

            assertEquals(new BigDecimal("155"), actualizada.getPrecio());
            assertEquals(1, service.obtenerPreciosActuales(List.of(activo)).size());
            assertEquals(new BigDecimal("155"),
                    service.obtenerUltimaCotizacion(activo).orElseThrow().getPrecio());
        } finally {
            em.close();
        }
    }

    @Test
    void deberiaObtenerLaUltimaCotizacionDeCadaActivo() {
        EntityManager em = JpaTestManager.createEntityManager();
        try {
            Bono gd30 = persistirActivo(em, "GD30");
            Bono al30 = persistirActivo(em, "AL30");
            CotizacionActivoService service = service(em);

            em.getTransaction().begin();
            service.registrarCotizacion(gd30, LocalDate.of(2026, 9, 27), new BigDecimal("140"));
            service.registrarCotizacion(gd30, LocalDate.of(2026, 9, 28), new BigDecimal("150"));
            service.registrarCotizacion(al30, LocalDate.of(2026, 9, 28), new BigDecimal("80"));
            em.getTransaction().commit();

            Map<Activo, BigDecimal> precios =
                    service.obtenerPreciosActuales(List.of(gd30, al30));

            assertEquals(2, precios.size());
            assertEquals(new BigDecimal("150"), precios.get(gd30));
            assertEquals(new BigDecimal("80"), precios.get(al30));
        } finally {
            em.close();
        }
    }

    @Test
    void deberiaOmitirActivoSinCotizacion() {
        EntityManager em = JpaTestManager.createEntityManager();
        try {
            Bono cotizado = persistirActivo(em, "GD30");
            Bono sinCotizacion = persistirActivo(em, "AL30");
            CotizacionActivoService service = service(em);

            em.getTransaction().begin();
            service.registrarCotizacion(cotizado, LocalDate.of(2026, 9, 28), new BigDecimal("150"));
            em.getTransaction().commit();

            Map<Activo, BigDecimal> precios =
                    service.obtenerPreciosActuales(List.of(cotizado, sinCotizacion));

            assertEquals(Map.of(cotizado, new BigDecimal("150")), precios);
        } finally {
            em.close();
        }
    }

    @Test
    void deberiaRechazarActivoNulo() {
        EntityManager em = JpaTestManager.createEntityManager();
        try {
            CotizacionActivoService service = service(em);
            assertThrows(NullPointerException.class,
                    () -> service.obtenerUltimaCotizacion(null));
        } finally {
            em.close();
        }
    }

    @Test
    void deberiaRechazarListaNula() {
        EntityManager em = JpaTestManager.createEntityManager();
        try {
            CotizacionActivoService service = service(em);
            assertThrows(NullPointerException.class,
                    () -> service.obtenerPreciosActuales(null));
        } finally {
            em.close();
        }
    }

    private CotizacionActivoService service(EntityManager em) {
        return new CotizacionActivoService(new CotizacionActivoRepository(em));
    }

    private Bono persistirActivo(EntityManager em) {
        return persistirActivo(em, "GD30-" + System.nanoTime());
    }

    private Bono persistirActivo(EntityManager em, String simbolo) {
        Moneda moneda = new Moneda(
                "ARS",
                "Peso argentino",
                2,
                TipoMoneda.FIAT);
        Bono activo = new Bono("Bono " + simbolo, simbolo, moneda);

        em.getTransaction().begin();
        em.persist(moneda);
        em.persist(activo);
        em.getTransaction().commit();
        return activo;
    }
}
