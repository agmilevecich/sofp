package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Bono;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.MovimientoActivo;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.OperacionFinanciera;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.PosicionActivo;
import ar.com.agmilevecich.sofp.domain.TipoCuenta;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.domain.TipoMovimientoActivo;
import ar.com.agmilevecich.sofp.domain.TipoOperacionFinanciera;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.MovimientoActivoRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CarteraActivoServiceTest {

    @Test
    void deberiaObtenerPosicionesDeTodosLosActivosDelPerfil() {
        JpaTestManager.close();
        EntityManager em = JpaTestManager.createEntityManager();
        try {
            PerfilFinanciero perfil = crearPerfil("cartera.todos");
            Bono gd30 = crearBono("GD30");
            Bono al30 = crearBono("AL30");
            persistir(em, perfil, gd30, al30);

            MovimientoActivo compraGd30 = movimiento(gd30, TipoMovimientoActivo.COMPRA, "100");
            MovimientoActivo ventaGd30 = movimiento(gd30, TipoMovimientoActivo.VENTA, "30");
            MovimientoActivo compraAl30 = movimiento(al30, TipoMovimientoActivo.COMPRA, "50");
            persistirMovimientos(em, perfil, compraGd30, ventaGd30, compraAl30);

            CarteraActivoService service = new CarteraActivoService(new MovimientoActivoRepository(em));
            List<PosicionActivo> posiciones = service.obtenerPosiciones(perfil);

            assertEquals(2, posiciones.size());
            assertEquals(new BigDecimal("70"), cantidadDe(posiciones, "GD30"));
            assertEquals(new BigDecimal("50"), cantidadDe(posiciones, "AL30"));
        } finally {
            em.close();
        }
    }

    @Test
    void deberiaDevolverListaVaciaSinMovimientos() {
        JpaTestManager.close();
        EntityManager em = JpaTestManager.createEntityManager();
        try {
            PerfilFinanciero perfil = crearPerfil("cartera.vacia");
            persistir(em, perfil);

            CarteraActivoService service = new CarteraActivoService(new MovimientoActivoRepository(em));

            assertTrue(service.obtenerPosiciones(perfil).isEmpty());
        } finally {
            em.close();
        }
    }

    @Test
    void deberiaExcluirPosicionQueTerminaEnCero() {
        JpaTestManager.close();
        EntityManager em = JpaTestManager.createEntityManager();
        try {
            PerfilFinanciero perfil = crearPerfil("cartera.cero");
            Bono gd30 = crearBono("GD30");
            persistir(em, perfil, gd30);

            MovimientoActivo compra = movimiento(gd30, TipoMovimientoActivo.COMPRA, "100");
            MovimientoActivo venta = movimiento(gd30, TipoMovimientoActivo.VENTA, "100");
            persistirMovimientos(em, perfil, compra, venta);

            CarteraActivoService service = new CarteraActivoService(new MovimientoActivoRepository(em));

            assertTrue(service.obtenerPosiciones(perfil).isEmpty());
        } finally {
            em.close();
        }
    }

    @Test
    void noDeberiaMezclarMovimientosDeOtroPerfil() {
        JpaTestManager.close();
        EntityManager em = JpaTestManager.createEntityManager();
        try {
            PerfilFinanciero perfil1 = crearPerfil("cartera.perfil1");
            PerfilFinanciero perfil2 = crearPerfil("cartera.perfil2");
            Bono gd30 = crearBono("GD30");
            persistir(em, perfil1, perfil2, gd30);

            MovimientoActivo movimientoPerfil1 = movimiento(gd30, TipoMovimientoActivo.COMPRA, "100");
            MovimientoActivo movimientoPerfil2 = movimiento(gd30, TipoMovimientoActivo.COMPRA, "200");
            persistirMovimientos(em, perfil1, movimientoPerfil1);
            persistirMovimientos(em, perfil2, movimientoPerfil2);

            CarteraActivoService service = new CarteraActivoService(new MovimientoActivoRepository(em));

            List<PosicionActivo> posiciones = service.obtenerPosiciones(perfil1);
            assertEquals(1, posiciones.size());
            assertEquals(new BigDecimal("100"), posiciones.get(0).getCantidad());
        } finally {
            em.close();
        }
    }

    @Test
    void deberiaRechazarPerfilNulo() {
        JpaTestManager.close();
        EntityManager em = JpaTestManager.createEntityManager();
        try {
            CarteraActivoService service = new CarteraActivoService(new MovimientoActivoRepository(em));
            assertThrows(NullPointerException.class, () -> service.obtenerPosiciones(null));
        } finally {
            em.close();
        }
    }

    private PerfilFinanciero crearPerfil(String prefijo) {
        Usuario usuario = new Usuario("Ariel", "Milevecich",
                prefijo + "." + System.nanoTime() + "@test.com", "hash");
        return new PerfilFinanciero("Perfil cartera", usuario);
    }

    private Bono crearBono(String simbolo) {
        Moneda moneda = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        return new Bono("Bono " + simbolo, simbolo, moneda);
    }

    private MovimientoActivo movimiento(Bono bono, TipoMovimientoActivo tipo, String cantidad) {
        return new MovimientoActivo(bono, tipo, new BigDecimal(cantidad), new BigDecimal("100"));
    }

    private void persistir(EntityManager em, Object... objetos) {
        em.getTransaction().begin();
        for (Object objeto : objetos) {
            if (objeto instanceof PerfilFinanciero perfil) {
                em.persist(perfil.getUsuario());
                em.persist(perfil);
            } else if (objeto instanceof Bono bono) {
                em.persist(bono.getMoneda());
                em.persist(bono);
            }
        }
        em.getTransaction().commit();
    }

    private void persistirMovimientos(EntityManager em, PerfilFinanciero perfil, MovimientoActivo... movimientos) {
        InstitucionFinanciera banco = new InstitucionFinanciera(
                "Banco Cartera " + System.nanoTime(), TipoInstitucionFinanciera.BANCO);
        Moneda moneda = new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
        Cuenta cuenta = new Cuenta("Cuenta cartera", TipoCuenta.CAJA_AHORRO, perfil, banco, moneda);
        Categoria categoria = new Categoria("Inversiones " + System.nanoTime(), perfil);

        em.getTransaction().begin();
        em.persist(banco);
        em.persist(moneda);
        em.persist(cuenta);
        em.persist(categoria);

        for (MovimientoActivo movimiento : movimientos) {
            TipoOperacionFinanciera tipoOperacion = movimiento.getTipoMovimiento() == TipoMovimientoActivo.COMPRA
                    ? TipoOperacionFinanciera.COMPRA
                    : TipoOperacionFinanciera.VENTA;

            BigDecimal importe = movimiento.getCantidad().multiply(movimiento.getPrecioUnitario());
            OperacionFinanciera operacion = tipoOperacion == TipoOperacionFinanciera.COMPRA
                    ? new OperacionFinanciera(cuenta, null, importe, tipoOperacion)
                    : new OperacionFinanciera(null, cuenta, importe, tipoOperacion);

            operacion.agregarMovimientoActivo(movimiento);
            em.persist(operacion);
        }
        em.getTransaction().commit();
    }

    private BigDecimal cantidadDe(List<PosicionActivo> posiciones, String simbolo) {
        return posiciones.stream()
                .filter(posicion -> simbolo.equals(posicion.getActivo().getSimbolo()))
                .findFirst()
                .orElseThrow()
                .getCantidad();
    }
}
