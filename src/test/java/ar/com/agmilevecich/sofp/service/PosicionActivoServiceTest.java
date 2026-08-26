package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Bono;
import ar.com.agmilevecich.sofp.domain.MovimientoActivo;
import ar.com.agmilevecich.sofp.domain.PosicionActivo;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.domain.TipoMovimientoActivo;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.persistence.MovimientoActivoRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PosicionActivoServiceTest {

    @Test
    void deberiaObtenerPosicionDelActivo() {

        JpaTestManager.close();
        EntityManager em = JpaTestManager.createEntityManager();

        try {
            Bono bono = crearBono();

            MovimientoActivo compra =
                    crearMovimiento(
                            bono,
                            TipoMovimientoActivo.COMPRA,
                            "100",
                            "125"
                    );

            MovimientoActivo venta =
                    crearMovimiento(
                            bono,
                            TipoMovimientoActivo.VENTA,
                            "30",
                            "135"
                    );

            em.getTransaction().begin();

            em.persist(bono.getMoneda());
            em.persist(bono);

            MovimientoActivoRepository repository =
                    new MovimientoActivoRepository(em);

            repository.guardar(compra);
            repository.guardar(venta);

            em.getTransaction().commit();

            PosicionActivoService service =
                    new PosicionActivoService(repository);

            PosicionActivo posicion =
                    service.obtenerPosicion(bono);

            assertEquals(bono, posicion.getActivo());
            assertEquals(
                    0,
                    posicion.getCantidad().compareTo(
                            new BigDecimal("70")
                    )
            );

        } finally {
            em.close();
        }
    }

    @Test
    void deberiaObtenerPosicionCeroSinMovimientos() {

        JpaTestManager.close();
        EntityManager em = JpaTestManager.createEntityManager();

        try {
            Bono bono = crearBono();

            em.getTransaction().begin();
            em.persist(bono.getMoneda());
            em.persist(bono);
            em.getTransaction().commit();

            MovimientoActivoRepository repository =
                    new MovimientoActivoRepository(em);

            PosicionActivoService service =
                    new PosicionActivoService(repository);

            PosicionActivo posicion =
                    service.obtenerPosicion(bono);

            assertEquals(
                    0,
                    posicion.getCantidad().compareTo(BigDecimal.ZERO)
            );

        } finally {
            em.close();
        }
    }

    @Test
    void deberiaRechazarActivoNulo() {

        JpaTestManager.close();
        EntityManager em = JpaTestManager.createEntityManager();

        try {
            MovimientoActivoRepository repository =
                    new MovimientoActivoRepository(em);

            PosicionActivoService service =
                    new PosicionActivoService(repository);

            assertThrows(
                    NullPointerException.class,
                    () -> service.obtenerPosicion(null)
            );

        } finally {
            em.close();
        }
    }

    private Bono crearBono() {

        Moneda moneda = new Moneda(
                "ARS",
                "Peso argentino",
                2,
                TipoMoneda.FIAT
        );

        return new Bono(
                "Bono GD30",
                moneda
        );
    }

    private MovimientoActivo crearMovimiento(
            Bono bono,
            TipoMovimientoActivo tipo,
            String cantidad,
            String precioUnitario) {

        return new MovimientoActivo(
                bono,
                tipo,
                new BigDecimal(cantidad),
                new BigDecimal(precioUnitario)
        );
    }
}