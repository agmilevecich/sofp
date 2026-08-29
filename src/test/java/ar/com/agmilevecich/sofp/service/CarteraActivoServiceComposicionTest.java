package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Activo;
import ar.com.agmilevecich.sofp.domain.Bono;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.DetalleComposicionCarteraActivo;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CarteraActivoServiceComposicionTest {

    @Test
    void deberiaObtenerComposicionDetalladaDesdeElServicio() {
        JpaTestManager.close();
        EntityManager em = JpaTestManager.createEntityManager();
        try {
            PerfilFinanciero perfil = crearPerfil("composicion.servicio");
            Moneda moneda = crearMoneda();
            Bono gd30 = crearBono("GD30", moneda);
            Bono al30 = crearBono("AL30", moneda);
            persistir(em, perfil, gd30, al30);

            persistirMovimientos(
                    em,
                    perfil,
                    movimiento(gd30, TipoMovimientoActivo.COMPRA, "100"),
                    movimiento(al30, TipoMovimientoActivo.COMPRA, "50")
            );

            CarteraActivoService service = new CarteraActivoService(new MovimientoActivoRepository(em));
            List<DetalleComposicionCarteraActivo> composicion = service.obtenerComposicion(
                    perfil,
                    Map.of(
                            gd30, new BigDecimal("120"),
                            al30, new BigDecimal("80")
                    )
            );

            assertEquals(2, composicion.size());
            assertEquals(new BigDecimal("75"), composicion.get(0).getParticipacionPorcentual());
            assertEquals(new BigDecimal("25"), composicion.get(1).getParticipacionPorcentual());
            assertEquals("GD30", composicion.get(0).getValorizacion()
                    .getPosicion().getActivo().getSimbolo());
            assertEquals(new BigDecimal("12000"), composicion.get(0).getValorizacion().getValorActual());
            assertEquals(new BigDecimal("4000"), composicion.get(1).getValorizacion().getValorActual());
        } finally {
            em.close();
        }
    }

    @Test
    void deberiaDevolverComposicionVaciaSinMovimientos() {
        JpaTestManager.close();
        EntityManager em = JpaTestManager.createEntityManager();
        try {
            PerfilFinanciero perfil = crearPerfil("composicion.vacia");
            persistir(em, perfil);

            CarteraActivoService service = new CarteraActivoService(new MovimientoActivoRepository(em));

            assertTrue(service.obtenerComposicion(perfil, Map.of()).isEmpty());
        } finally {
            em.close();
        }
    }

    @Test
    void deberiaRechazarPreciosActualesNulosAlObtenerComposicion() {
        JpaTestManager.close();
        EntityManager em = JpaTestManager.createEntityManager();
        try {
            PerfilFinanciero perfil = crearPerfil("composicion.null");
            CarteraActivoService service = new CarteraActivoService(new MovimientoActivoRepository(em));

            assertThrows(
                    NullPointerException.class,
                    () -> service.obtenerComposicion(perfil, null)
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

    private MovimientoActivo movimiento(Bono bono, TipoMovimientoActivo tipo, String cantidad) {
        return new MovimientoActivo(
                bono,
                tipo,
                new BigDecimal(cantidad),
                new BigDecimal("100")
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
