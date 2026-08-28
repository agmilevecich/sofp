package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Activo;
import ar.com.agmilevecich.sofp.domain.Bono;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.MovimientoActivo;
import ar.com.agmilevecich.sofp.domain.OperacionFinanciera;
import ar.com.agmilevecich.sofp.domain.PosicionActivo;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.TipoCuenta;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.domain.TipoMovimientoActivo;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.MovimientoActivoRepository;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import ar.com.agmilevecich.sofp.persistence.OperacionFinancieraRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PosicionActivoServiceTest {

    @Test
    void deberiaObtenerPosicionDelActivo() {
        JpaTestManager.close();
        EntityManager em = JpaTestManager.createEntityManager();
        try {
            Bono bono = crearBono();
            MovimientoActivo compra = crearMovimiento(bono, TipoMovimientoActivo.COMPRA, "100", "125");
            MovimientoActivo venta = crearMovimiento(bono, TipoMovimientoActivo.VENTA, "30", "135");
            em.getTransaction().begin();
            em.persist(bono.getMoneda());
            em.persist(bono);
            MovimientoActivoRepository repository = new MovimientoActivoRepository(em);
            repository.guardar(compra);
            repository.guardar(venta);
            em.getTransaction().commit();
            PosicionActivoService service = new PosicionActivoService(repository);
            PosicionActivo posicion = service.obtenerPosicion(bono);
            assertEquals(bono, posicion.getActivo());
            assertEquals(0, posicion.getCantidad().compareTo(new BigDecimal("70")));
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
            MovimientoActivoRepository repository = new MovimientoActivoRepository(em);
            PosicionActivoService service = new PosicionActivoService(repository);
            PosicionActivo posicion = service.obtenerPosicion(bono);
            assertEquals(0, posicion.getCantidad().compareTo(BigDecimal.ZERO));
        } finally {
            em.close();
        }
    }

    @Test
    void deberiaRechazarActivoNulo() {
        JpaTestManager.close();
        EntityManager em = JpaTestManager.createEntityManager();
        try {
            MovimientoActivoRepository repository = new MovimientoActivoRepository(em);
            PosicionActivoService service = new PosicionActivoService(repository);
            assertThrows(NullPointerException.class, () -> service.obtenerPosicion(null));
        } finally {
            em.close();
        }
    }

    @Test
    void deberiaCalcularPosicionDesdeCompraYVentaReal() {
        JpaTestManager.close();
        EntityManager em = JpaTestManager.createEntityManager();
        try {
            Usuario usuario = new Usuario("Ariel", "Milevecich", "posicion.integracion." + System.nanoTime() + "@test.com", "hash");
            PerfilFinanciero perfil = new PerfilFinanciero("Perfil principal", usuario);
            InstitucionFinanciera banco = new InstitucionFinanciera("Banco de Prueba", TipoInstitucionFinanciera.BANCO);
            Moneda moneda = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
            Cuenta cuenta = new Cuenta("Cuenta inversiones", TipoCuenta.CAJA_AHORRO, perfil, banco, moneda);
            Categoria categoria = new Categoria("Inversiones", perfil);
            Bono bono = new Bono("Bono GD30", "GD30", moneda);

            em.getTransaction().begin();
            em.persist(usuario);
            em.persist(perfil);
            em.persist(banco);
            em.persist(moneda);
            em.persist(cuenta);
            em.persist(categoria);
            em.persist(bono);
            em.getTransaction().commit();

            MovimientoRepository movimientoRepository = new MovimientoRepository(em);
            OperacionFinancieraRepository operacionRepository = new OperacionFinancieraRepository(em);
            OperacionFinancieraService operacionService = new OperacionFinancieraService(em, movimientoRepository, operacionRepository);

            operacionService.comprarActivo(cuenta, categoria, bono, new BigDecimal("100"), new BigDecimal("125"), LocalDateTime.of(2026, 8, 27, 10, 0), "Compra Bono GD30");
            operacionService.venderActivo(cuenta, categoria, bono, new BigDecimal("30"), new BigDecimal("135"), LocalDateTime.of(2026, 8, 27, 14, 0), "Venta Bono GD30");

            MovimientoActivoRepository movimientoActivoRepository = new MovimientoActivoRepository(em);
            PosicionActivoService posicionService = new PosicionActivoService(movimientoActivoRepository);
            PosicionActivo posicion = posicionService.obtenerPosicion(bono);

            assertEquals(bono.getId(), posicion.getActivo().getId());
            assertEquals(0, new BigDecimal("70").compareTo(posicion.getCantidad()));
        } finally {
            em.close();
        }
    }

    private Bono crearBono() {
        Moneda moneda = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        return new Bono("Bono GD30", "GD30", moneda);
    }

    private MovimientoActivo crearMovimiento(Bono bono, TipoMovimientoActivo tipo, String cantidad, String precioUnitario) {
        return new MovimientoActivo(bono, tipo, new BigDecimal(cantidad), new BigDecimal(precioUnitario));
    }
}
