package ar.com.agmilevecich.sofp.persistence;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Activo;
import ar.com.agmilevecich.sofp.domain.CotizacionActivo;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CotizacionActivoRepositoryTest {

    @Test
    void deberiaGuardarYBuscarPorActivoYFecha() {
        EntityManager em = JpaTestManager.createEntityManager();
        try {
            Activo activo = persistirActivo(em);
            LocalDate fecha = LocalDate.of(2026, 9, 28);
            CotizacionActivo cotizacion = new CotizacionActivo(
                    activo, fecha, new BigDecimal("150"));

            em.getTransaction().begin();
            new CotizacionActivoRepository(em).guardar(cotizacion);
            em.getTransaction().commit();

            CotizacionActivo encontrada = new CotizacionActivoRepository(em)
                    .buscarPorActivoYFecha(activo.getId(), fecha)
                    .orElseThrow();

            assertEquals(new BigDecimal("150"), encontrada.getPrecio());
            assertEquals(activo.getId(), encontrada.getActivo().getId());
        } finally {
            em.close();
        }
    }

    @Test
    void deberiaBuscarLaUltimaCotizacionPorFecha() {
        EntityManager em = JpaTestManager.createEntityManager();
        try {
            Activo activo = persistirActivo(em);
            guardar(em, new CotizacionActivo(
                    activo, LocalDate.of(2026, 9, 27), new BigDecimal("140")));
            guardar(em, new CotizacionActivo(
                    activo, LocalDate.of(2026, 9, 28), new BigDecimal("150")));

            CotizacionActivo ultima = new CotizacionActivoRepository(em)
                    .buscarUltimaPorActivo(activo.getId())
                    .orElseThrow();

            assertEquals(LocalDate.of(2026, 9, 28), ultima.getFecha());
            assertEquals(new BigDecimal("150"), ultima.getPrecio());
        } finally {
            em.close();
        }
    }

    @Test
    void deberiaDevolverEmptySinCotizaciones() {
        EntityManager em = JpaTestManager.createEntityManager();
        try {
            Activo activo = persistirActivo(em);

            assertTrue(new CotizacionActivoRepository(em)
                    .buscarUltimaPorActivo(activo.getId())
                    .isEmpty());
        } finally {
            em.close();
        }
    }

    @Test
    void deberiaDevolverListaOrdenadaPorFechaDescendente() {
        EntityManager em = JpaTestManager.createEntityManager();
        try {
            Activo activo = persistirActivo(em);
            guardar(em, new CotizacionActivo(
                    activo, LocalDate.of(2026, 9, 26), new BigDecimal("130")));
            guardar(em, new CotizacionActivo(
                    activo, LocalDate.of(2026, 9, 28), new BigDecimal("150")));
            guardar(em, new CotizacionActivo(
                    activo, LocalDate.of(2026, 9, 27), new BigDecimal("140")));

            var cotizaciones = new CotizacionActivoRepository(em)
                    .listarPorActivo(activo.getId());

            assertEquals(3, cotizaciones.size());
            assertEquals(LocalDate.of(2026, 9, 28), cotizaciones.get(0).getFecha());
            assertEquals(LocalDate.of(2026, 9, 27), cotizaciones.get(1).getFecha());
            assertEquals(LocalDate.of(2026, 9, 26), cotizaciones.get(2).getFecha());
        } finally {
            em.close();
        }
    }

    @Test
    void deberiaRechazarIdDeActivoNulo() {
        EntityManager em = JpaTestManager.createEntityManager();
        try {
            CotizacionActivoRepository repository = new CotizacionActivoRepository(em);

            assertThrows(
                    NullPointerException.class,
                    () -> repository.buscarUltimaPorActivo(null));
        } finally {
            em.close();
        }
    }

    private Activo persistirActivo(EntityManager em) {
        Activo activo = new Activo(
                "Bono GD30",
                "GD30-" + System.nanoTime(),
                new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT));

        em.getTransaction().begin();
        em.persist(activo.getMoneda());
        em.persist(activo);
        em.getTransaction().commit();
        return activo;
    }

    private void guardar(EntityManager em, CotizacionActivo cotizacion) {
        em.getTransaction().begin();
        new CotizacionActivoRepository(em).guardar(cotizacion);
        em.getTransaction().commit();
    }
}
