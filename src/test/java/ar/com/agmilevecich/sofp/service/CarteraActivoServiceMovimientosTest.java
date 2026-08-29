package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Bono;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.DetalleMovimientoCarteraActivo;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.MovimientoActivo;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.OperacionFinanciera;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
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

class CarteraActivoServiceMovimientosTest {

    @Test
    void deberiaObtenerReporteDeMovimientosDelPerfil() {
        JpaTestManager.close();
        EntityManager em = JpaTestManager.createEntityManager();
        try {
            PerfilFinanciero perfil = crearPerfil("movimientos.servicio");
            Moneda moneda = crearMoneda();
            Bono gd30 = crearBono("GD30", moneda);
            Bono al30 = crearBono("AL30", moneda);
            persistir(em, perfil, gd30, al30);

            MovimientoActivo compraGd30 = movimiento(gd30, TipoMovimientoActivo.COMPRA, "100", "120");
            MovimientoActivo ventaAl30 = movimiento(al30, TipoMovimientoActivo.VENTA, "20", "80");
            persistirMovimientos(em, perfil, compraGd30, ventaAl30);

            CarteraActivoService service = new CarteraActivoService(new MovimientoActivoRepository(em));
            List<DetalleMovimientoCarteraActivo> detalles = service.obtenerMovimientos(perfil);

            assertEquals(2, detalles.size());
            assertEquals("GD30", detalles.get(0).getActivo().getSimbolo());
            assertEquals(TipoMovimientoActivo.COMPRA, detalles.get(0).getTipoMovimiento());
            assertEquals(0, detalles.get(0).getCantidad().compareTo(new BigDecimal("100")));
            assertEquals(0, detalles.get(0).getPrecioUnitario().compareTo(new BigDecimal("120")));
            assertEquals(0, detalles.get(0).getImporte().compareTo(new BigDecimal("12000")));
            assertSame(compraGd30, detalles.get(0).getMovimiento());

            assertEquals("AL30", detalles.get(1).getActivo().getSimbolo());
            assertEquals(TipoMovimientoActivo.VENTA, detalles.get(1).getTipoMovimiento());
            assertEquals(0, detalles.get(1).getImporte().compareTo(new BigDecimal("1600")));
            assertSame(ventaAl30, detalles.get(1).getMovimiento());
        } finally {
            em.close();
        }
    }

    @Test
    void deberiaDevolverListaVaciaSinMovimientos() {
        JpaTestManager.close();
        EntityManager em = JpaTestManager.createEntityManager();
        try {
            PerfilFinanciero perfil = crearPerfil("movimientos.vacia");
            persistir(em, perfil);

            CarteraActivoService service = new CarteraActivoService(new MovimientoActivoRepository(em));

            assertTrue(service.obtenerMovimientos(perfil).isEmpty());
        } finally {
            em.close();
        }
    }

    @Test
    void deberiaRechazarPerfilNuloAlObtenerMovimientos() {
        JpaTestManager.close();
        EntityManager em = JpaTestManager.createEntityManager();
        try {
            CarteraActivoService service = new CarteraActivoService(new MovimientoActivoRepository(em));

            assertThrows(
                    NullPointerException.class,
                    () -> service.obtenerMovimientos(null)
            );
        } finally {
            em.close();
        }
    }

    private PerfilFinanciero crearPerfil(String prefijo) {
        Usuario usuario = new Usuario(
                "Ariel",
                "Milevecich",
                prefijo + "." + System.nanoTime() + "@test.com",
                "hash"
        );
        return new PerfilFinanciero("Perfil cartera", usuario);
    }

    private Moneda crearMoneda() {
        return new Moneda("ARS", "Peso argentino", 2, TipoMoneda.FIAT);
    }

    private Bono crearBono(String simbolo, Moneda moneda) {
        return new Bono("Bono " + simbolo, simbolo, moneda);
    }

    private MovimientoActivo movimiento(
            Bono bono,
            TipoMovimientoActivo tipo,
            String cantidad,
            String precio) {
        return new MovimientoActivo(
                bono,
                tipo,
                new BigDecimal(cantidad),
                new BigDecimal(precio)
        );
    }

    private void persistir(EntityManager em, Object... objetos) {
        em.getTransaction().begin();
        for (Object objeto : objetos) {
            if (objeto instanceof PerfilFinanciero perfil) {
                em.persist(perfil.getUsuario());
                em.persist(perfil);
            } else if (objeto instanceof Bono bono) {
                if (!em.contains(bono.getMoneda())) {
                    em.persist(bono.getMoneda());
                }
                em.persist(bono);
            }
        }
        em.getTransaction().commit();
    }

    private void persistirMovimientos(
            EntityManager em,
            PerfilFinanciero perfil,
            MovimientoActivo... movimientos) {
        InstitucionFinanciera banco = new InstitucionFinanciera(
                "Banco Cartera " + System.nanoTime(),
                TipoInstitucionFinanciera.BANCO
        );
        Moneda moneda = movimientos[0].getActivo().getMoneda();
        Cuenta cuenta = new Cuenta(
                "Cuenta cartera",
                TipoCuenta.CAJA_AHORRO,
                perfil,
                banco,
                moneda
        );
        Categoria categoria = new Categoria("Inversiones " + System.nanoTime(), perfil);

        em.getTransaction().begin();
        em.persist(banco);
        em.persist(cuenta);
        em.persist(categoria);

        for (MovimientoActivo movimiento : movimientos) {
            TipoOperacionFinanciera tipoOperacion = movimiento.getTipoMovimiento()
                    == TipoMovimientoActivo.COMPRA
                    ? TipoOperacionFinanciera.COMPRA
                    : TipoOperacionFinanciera.VENTA;
            BigDecimal importe = movimiento.getCantidad()
                    .multiply(movimiento.getPrecioUnitario());
            OperacionFinanciera operacion = tipoOperacion == TipoOperacionFinanciera.COMPRA
                    ? new OperacionFinanciera(cuenta, null, importe, tipoOperacion)
                    : new OperacionFinanciera(null, cuenta, importe, tipoOperacion);

            operacion.agregarMovimientoActivo(movimiento);
            em.persist(operacion);
            em.persist(movimiento);
        }
        em.getTransaction().commit();
    }
}
