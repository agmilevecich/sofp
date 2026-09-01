package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Bono;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.OperacionFinanciera;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.TipoCuenta;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.MovimientoActivoRepository;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import ar.com.agmilevecich.sofp.persistence.OperacionFinancieraRepository;
import ar.com.agmilevecich.sofp.service.CarteraActivoService;
import ar.com.agmilevecich.sofp.service.OperacionFinancieraService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.JList;
import java.awt.Component;
import java.awt.Container;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InversionesPanelTest {

    private EntityManager entityManager;
    private CarteraActivoService carteraActivoService;

    @BeforeEach
    void setUp() {
        entityManager = JpaTestManager.createEntityManager();
        carteraActivoService = new CarteraActivoService(
                new MovimientoActivoRepository(entityManager)
        );
    }

    @AfterEach
    void tearDown() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
        JpaTestManager.close();
    }

    @Test
    void deberiaMostrarLasPosicionesDelPerfilDelUsuario() {
        Moneda moneda = crearMonedaPersistida();
        Bono bono = crearBonoPersistido(moneda);
        Contexto contexto = crearContexto(moneda);

        registrarCompra(contexto, bono, "100");

        InversionesPanel panel = new InversionesPanel(
                carteraActivoService,
                contexto.perfil,
                contexto.usuario.getId()
        );

        JList<?> lista = buscarLista(panel);

        assertNotNull(lista);
        assertEquals(1, lista.getModel().getSize());
        assertEquals(
                "GD30 - 100",
                lista.getModel().getElementAt(0)
        );
    }

    @Test
    void deberiaRechazarUnPerfilDeOtroUsuario() {
        Moneda moneda = crearMonedaPersistida();
        Contexto contextoPropietario = crearContexto(moneda);
        Contexto contextoAjeno = crearContexto(moneda);

        assertThrows(
                IllegalArgumentException.class,
                () -> new InversionesPanel(
                        carteraActivoService,
                        contextoAjeno.perfil,
                        contextoPropietario.usuario.getId()
                )
        );
    }

    @Test
    void deberiaMostrarListaVaciaCuandoElPerfilNoTienePosiciones() {
        Moneda moneda = crearMonedaPersistida();
        Contexto contexto = crearContexto(moneda);

        InversionesPanel panel = new InversionesPanel(
                carteraActivoService,
                contexto.perfil,
                contexto.usuario.getId()
        );

        JList<?> lista = buscarLista(panel);

        assertNotNull(lista);
        assertEquals(0, lista.getModel().getSize());
    }

    private Moneda crearMonedaPersistida() {
        Moneda moneda = new Moneda(
                "ARS",
                "Peso argentino",
                2,
                TipoMoneda.FIAT
        );

        entityManager.getTransaction().begin();
        entityManager.persist(moneda);
        entityManager.getTransaction().commit();

        return moneda;
    }

    private Bono crearBonoPersistido(Moneda moneda) {
        Bono bono = new Bono(
                "Bono GD30",
                "GD30",
                moneda
        );

        entityManager.getTransaction().begin();
        entityManager.persist(bono);
        entityManager.getTransaction().commit();

        return bono;
    }

    private Contexto crearContexto(Moneda moneda) {
        Usuario usuario = new Usuario(
                "Ariel",
                "Test",
                "ariel.inversiones.panel." + System.nanoTime(),
                "hash"
        );
        PerfilFinanciero perfil = new PerfilFinanciero(
                "Perfil inversiones",
                usuario
        );
        usuario.agregarPerfilFinanciero(perfil);

        InstitucionFinanciera institucion = new InstitucionFinanciera(
                "Banco Test " + System.nanoTime(),
                TipoInstitucionFinanciera.BANCO
        );
        Cuenta cuenta = new Cuenta(
                "Cuenta inversiones " + System.nanoTime(),
                TipoCuenta.CAJA_AHORRO,
                perfil,
                institucion,
                moneda
        );
        Categoria categoria = new Categoria(
                "Inversiones " + System.nanoTime(),
                perfil
        );

        entityManager.getTransaction().begin();
        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(institucion);
        entityManager.persist(cuenta);
        entityManager.persist(categoria);
        entityManager.getTransaction().commit();

        return new Contexto(usuario, perfil, cuenta, categoria);
    }

    private void registrarCompra(Contexto contexto, Bono bono, String cantidad) {
        OperacionFinancieraService operacionService = new OperacionFinancieraService(
                entityManager,
                new MovimientoRepository(entityManager),
                new OperacionFinancieraRepository(entityManager)
        );

        operacionService.comprarActivo(
                contexto.usuario.getId(),
                contexto.cuenta,
                contexto.categoria,
                bono,
                new BigDecimal(cantidad),
                new BigDecimal("125"),
                LocalDateTime.of(2026, 8, 27, 10, 0),
                "Compra Bono GD30"
        );
    }

    private JList<?> buscarLista(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof JList<?> lista) {
                return lista;
            }

            if (component instanceof Container hijo) {
                JList<?> encontrada = buscarLista(hijo);
                if (encontrada != null) {
                    return encontrada;
                }
            }
        }

        return null;
    }

    private record Contexto(
            Usuario usuario,
            PerfilFinanciero perfil,
            Cuenta cuenta,
            Categoria categoria) {
    }
}
