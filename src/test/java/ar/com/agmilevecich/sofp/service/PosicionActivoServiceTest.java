package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Bono;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.OperacionFinanciera;
import ar.com.agmilevecich.sofp.domain.PosicionActivo;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.TipoCuenta;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.MovimientoActivoRepository;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import ar.com.agmilevecich.sofp.persistence.OperacionFinancieraRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PosicionActivoServiceTest {

    @Test
    void deberiaObtenerPosicionDelActivoParaElPerfil() {
        JpaTestManager.close();
        EntityManager em = JpaTestManager.createEntityManager();
        try {
            Moneda moneda = crearMonedaPersistida(em);
            Bono bono = crearBonoPersistido(em, moneda);
            Contexto contexto = crearContexto(em, "perfil.posicion.1", moneda, bono);
            registrarCompra(em, contexto.cuenta, contexto.categoria, bono, "100");

            PosicionActivoService service = new PosicionActivoService(
                    new MovimientoActivoRepository(em)
            );

            PosicionActivo posicion = service.obtenerPosicion(
                    contexto.perfil,
                    bono
            );

            assertEquals(bono.getId(), posicion.getActivo().getId());
            assertEquals(0, new BigDecimal("100").compareTo(posicion.getCantidad()));
        } finally {
            em.close();
        }
    }

    @Test
    void noDeberiaMezclarMovimientosDelMismoActivoEntrePerfiles() {
        JpaTestManager.close();
        EntityManager em = JpaTestManager.createEntityManager();
        try {
            Moneda moneda = crearMonedaPersistida(em);
            Bono bono = crearBonoPersistido(em, moneda);
            Contexto contexto1 = crearContexto(em, "perfil.posicion.2", moneda, bono);
            Contexto contexto2 = crearContexto(em, "perfil.posicion.3", moneda, bono);

            registrarCompra(em, contexto1.cuenta, contexto1.categoria, bono, "100");
            registrarCompra(em, contexto2.cuenta, contexto2.categoria, bono, "40");

            PosicionActivoService service = new PosicionActivoService(
                    new MovimientoActivoRepository(em)
            );

            PosicionActivo posicion1 = service.obtenerPosicion(
                    contexto1.perfil,
                    bono
            );

            PosicionActivo posicion2 = service.obtenerPosicion(
                    contexto2.perfil,
                    bono
            );

            assertEquals(0, new BigDecimal("100").compareTo(posicion1.getCantidad()));
            assertEquals(0, new BigDecimal("40").compareTo(posicion2.getCantidad()));
        } finally {
            em.close();
        }
    }

    @Test
    void deberiaObtenerPosicionCeroSinMovimientosDelPerfil() {
        JpaTestManager.close();
        EntityManager em = JpaTestManager.createEntityManager();
        try {
            Moneda moneda = crearMonedaPersistida(em);
            Bono bono = crearBonoPersistido(em, moneda);
            Contexto contexto = crearContexto(em, "perfil.posicion.4", moneda, bono);

            PosicionActivoService service = new PosicionActivoService(
                    new MovimientoActivoRepository(em)
            );

            PosicionActivo posicion = service.obtenerPosicion(
                    contexto.perfil,
                    bono
            );

            assertEquals(0, posicion.getCantidad().compareTo(BigDecimal.ZERO));
        } finally {
            em.close();
        }
    }

    @Test
    void deberiaRechazarPerfilNulo() {
        JpaTestManager.close();
        EntityManager em = JpaTestManager.createEntityManager();
        try {
            Moneda moneda = crearMonedaPersistida(em);
            Bono bono = crearBonoPersistido(em, moneda);
            PosicionActivoService service = new PosicionActivoService(
                    new MovimientoActivoRepository(em)
            );

            assertThrows(
                    NullPointerException.class,
                    () -> service.obtenerPosicion(null, bono)
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
            Moneda moneda = crearMonedaPersistida(em);
            Bono bono = crearBonoPersistido(em, moneda);
            Contexto contexto = crearContexto(em, "perfil.posicion.6", moneda, bono);
            PosicionActivoService service = new PosicionActivoService(
                    new MovimientoActivoRepository(em)
            );

            assertThrows(
                    NullPointerException.class,
                    () -> service.obtenerPosicion(contexto.perfil, null)
            );
        } finally {
            em.close();
        }
    }

    private Moneda crearMonedaPersistida(EntityManager em) {
        Moneda moneda = new Moneda(
                "ARS",
                "Peso argentino",
                2,
                TipoMoneda.FIAT
        );

        em.getTransaction().begin();
        em.persist(moneda);
        em.getTransaction().commit();

        return moneda;
    }

    private Bono crearBonoPersistido(EntityManager em, Moneda moneda) {
        Bono bono = new Bono("Bono GD30", "GD30", moneda);

        em.getTransaction().begin();
        em.persist(bono);
        em.getTransaction().commit();

        return bono;
    }

    private Contexto crearContexto(
            EntityManager em,
            String emailPrefijo,
            Moneda moneda,
            Bono bono) {

        Usuario usuario = new Usuario(
                "Ariel",
                "Milevecich",
                emailPrefijo + "." + System.nanoTime() + "@test.com",
                "hash"
        );
        PerfilFinanciero perfil = new PerfilFinanciero("Perfil principal", usuario);
        InstitucionFinanciera banco = new InstitucionFinanciera(
                "Banco de Prueba " + System.nanoTime(),
                TipoInstitucionFinanciera.BANCO
        );
        Cuenta cuenta = new Cuenta(
                "Cuenta inversiones " + System.nanoTime(),
                TipoCuenta.CAJA_AHORRO,
                perfil,
                banco,
                moneda
        );
        Categoria categoria = new Categoria(
                "Inversiones " + System.nanoTime(),
                perfil
        );

        em.getTransaction().begin();
        em.persist(usuario);
        em.persist(perfil);
        em.persist(banco);
        em.persist(cuenta);
        em.persist(categoria);
        em.getTransaction().commit();

        return new Contexto(perfil, cuenta, categoria, bono);
    }

    private void registrarCompra(
            EntityManager em,
            Cuenta cuenta,
            Categoria categoria,
            Bono bono,
            String cantidad) {

        MovimientoRepository movimientoRepository = new MovimientoRepository(em);
        OperacionFinancieraRepository operacionRepository =
                new OperacionFinancieraRepository(em);
        OperacionFinancieraService operacionService =
                new OperacionFinancieraService(
                        em,
                        movimientoRepository,
                        operacionRepository
                );

        operacionService.comprarActivo(
                cuenta,
                categoria,
                bono,
                new BigDecimal(cantidad),
                new BigDecimal("125"),
                LocalDateTime.of(2026, 8, 27, 10, 0),
                "Compra Bono GD30"
        );
    }

    private record Contexto(
            PerfilFinanciero perfil,
            Cuenta cuenta,
            Categoria categoria,
            Bono bono) {
    }
}
