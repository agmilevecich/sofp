package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.Movimiento;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.TipoCuenta;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.domain.TipoMovimiento;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.CategoriaRepository;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoriaServiceTest {

    private EntityManager entityManager;
    private CategoriaRepository categoriaRepository;
    private CategoriaService categoriaService;

    @BeforeEach
    void setUp() {
        JpaTestManager.close();
        entityManager = JpaTestManager.createEntityManager();
        categoriaRepository = new CategoriaRepository(entityManager);
        categoriaService = new CategoriaService(entityManager, categoriaRepository);
    }

    @AfterEach
    void tearDown() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
        JpaTestManager.close();
    }

    @Test
    void deberiaRegistrarCategoria() {
        Usuario usuario = crearUsuario("registrar");
        PerfilFinanciero perfil = crearPerfil(usuario, "Perfil principal");
        Categoria categoria = new Categoria("Alimentación", perfil);

        persistir(usuario, perfil, categoria);

        assertTrue(categoria.getId() != null);
        assertEquals("Alimentación", categoria.getNombre());
    }

    @Test
    void deberiaBuscarCategoriaPorId() {
        Usuario usuario = crearUsuario("buscar");
        PerfilFinanciero perfil = crearPerfil(usuario, "Perfil principal");
        Categoria categoria = new Categoria("Alimentación", perfil);

        persistir(usuario, perfil, categoria);
        entityManager.clear();

        Optional<Categoria> resultado = categoriaService.buscarPorId(categoria.getId());

        assertTrue(resultado.isPresent());
        assertEquals(categoria.getId(), resultado.get().getId());
        assertEquals("Alimentación", resultado.get().getNombre());
    }

    @Test
    void deberiaListarTodasLasCategorias() {
        Usuario usuario = crearUsuario("listar");
        PerfilFinanciero perfil = crearPerfil(usuario, "Perfil principal");
        Categoria categoria1 = new Categoria("Alimentación", perfil);
        Categoria categoria2 = new Categoria("Transporte", perfil);

        persistir(usuario, perfil, categoria1, categoria2);

        List<Categoria> categorias = categoriaService.listarTodas();

        assertEquals(2, categorias.size());
        assertEquals("Alimentación", categorias.get(0).getNombre());
        assertEquals("Transporte", categorias.get(1).getNombre());
    }

    @Test
    void deberiaListarCategoriasPorPerfilFinanciero() {
        Usuario usuario = crearUsuario("perfil");
        PerfilFinanciero perfil1 = crearPerfil(usuario, "Perfil principal");
        PerfilFinanciero perfil2 = crearPerfil(usuario, "Perfil secundario");
        Categoria categoria1 = new Categoria("Alimentación", perfil1);
        Categoria categoria2 = new Categoria("Transporte", perfil2);

        persistir(usuario, perfil1, categoria1);
        persistir(usuario, perfil2, categoria2);

        List<Categoria> categorias = categoriaService.listarPorPerfilFinanciero(perfil1.getId());

        assertEquals(1, categorias.size());
        assertEquals("Alimentación", categorias.get(0).getNombre());
        assertEquals(perfil1.getId(), categorias.get(0).getPerfilFinanciero().getId());
    }

    @Test
    void deberiaModificarNombreDeCategoria() {
        Usuario usuario = crearUsuario("modificar.nombre");
        Categoria categoria = crearCategoriaPersistida(usuario, "Alimentación");

        Categoria actualizada = categoriaService.modificarNombre(
                categoria.getId(), usuario.getId(), "Supermercado");

        assertEquals(categoria.getId(), actualizada.getId());
        assertEquals("Supermercado", actualizada.getNombre());
    }

    @Test
    void deberiaModificarDescripcionDeCategoria() {
        Usuario usuario = crearUsuario("modificar.descripcion");
        Categoria categoria = crearCategoriaPersistida(usuario, "Alimentación");

        Categoria actualizada = categoriaService.modificarDescripcion(
                categoria.getId(), usuario.getId(),
                "Gastos relacionados con alimentos y supermercado");

        assertEquals(categoria.getId(), actualizada.getId());
        assertEquals("Gastos relacionados con alimentos y supermercado", actualizada.getDescripcion());
    }

    @Test
    void deberiaActivarCategoria() {
        Usuario usuario = crearUsuario("activar");
        Categoria categoria = crearCategoriaPersistida(usuario, "Alimentación");
        categoria.desactivar();
        entityManager.getTransaction().begin();
        entityManager.merge(categoria);
        entityManager.getTransaction().commit();

        Categoria actualizada = categoriaService.activar(categoria.getId(), usuario.getId());

        assertTrue(actualizada.isActiva());
    }

    @Test
    void deberiaDesactivarCategoria() {
        Usuario usuario = crearUsuario("desactivar");
        Categoria categoria = crearCategoriaPersistida(usuario, "Alimentación");

        Categoria actualizada = categoriaService.desactivar(categoria.getId(), usuario.getId());

        assertTrue(!actualizada.isActiva());
    }

    @Test
    void deberiaEliminarCategoriaExistente() {
        Usuario usuario = crearUsuario("eliminar");
        Categoria categoria = crearCategoriaPersistida(usuario, "Alimentación");
        Long categoriaId = categoria.getId();

        assertTrue(categoriaService.eliminar(categoriaId, usuario.getId()));

        assertTrue(categoriaService.buscarPorId(categoriaId).isEmpty());
    }

    @Test
    void deberiaDesactivarCategoriaConMovimientosYConservarElHistorial() {
        Usuario usuario = crearUsuario("eliminar.con.movimientos");
        PerfilFinanciero perfil = crearPerfil(usuario, "Perfil principal");
        InstitucionFinanciera institucion = new InstitucionFinanciera(
                "Banco de Prueba",
                TipoInstitucionFinanciera.BANCO
        );
        Moneda moneda = new Moneda(
                "ARS",
                "Peso argentino",
                2,
                TipoMoneda.FIAT
        );
        Cuenta cuenta = new Cuenta(
                "Cuenta principal",
                TipoCuenta.CAJA_AHORRO,
                perfil,
                institucion,
                moneda
        );
        Categoria categoria = new Categoria("Alimentación", perfil);
        Movimiento movimiento = new Movimiento(
                cuenta,
                categoria,
                TipoMovimiento.INGRESO,
                new BigDecimal("1000.00"),
                LocalDateTime.of(2026, 9, 4, 10, 0),
                "Movimiento histórico"
        );

        entityManager.getTransaction().begin();
        entityManager.persist(usuario);
        entityManager.persist(perfil);
        entityManager.persist(institucion);
        entityManager.persist(moneda);
        entityManager.persist(cuenta);
        entityManager.persist(categoria);
        entityManager.persist(movimiento);
        entityManager.getTransaction().commit();

        assertFalse(categoriaService.eliminar(categoria.getId(), usuario.getId()));
        entityManager.clear();

        Categoria conservada = categoriaService.buscarPorId(categoria.getId()).orElseThrow();
        assertFalse(conservada.isActiva());

        List<Movimiento> movimientos = new MovimientoRepository(entityManager)
                .listarPorCategoria(categoria.getId());
        assertEquals(1, movimientos.size());
        assertEquals(movimiento.getId(), movimientos.get(0).getId());
    }

    @Test
    void deberiaPersistirLaModificacionDelNombre() {
        Usuario usuario = crearUsuario("persistencia.nombre");
        Categoria categoria = crearCategoriaPersistida(usuario, "Nombre original");
        Long categoriaId = categoria.getId();

        categoriaService.modificarNombre(categoriaId, usuario.getId(), "Nombre persistido");
        entityManager.clear();

        assertEquals("Nombre persistido", categoriaService.buscarPorId(categoriaId).orElseThrow().getNombre());
    }

    @Test
    void deberiaRechazarModificacionDeCategoriaDeOtroUsuario() {
        Usuario propietario = crearUsuario("propietario");
        Categoria categoria = crearCategoriaPersistida(propietario, "Privada");
        Usuario otroUsuario = crearUsuarioPersistido("otro.usuario");

        assertThrows(IllegalArgumentException.class, () ->
                categoriaService.modificarNombre(categoria.getId(), otroUsuario.getId(), "Modificada"));
    }

    @Test
    void deberiaRechazarModificacionDescripcionDeCategoriaDeOtroUsuario() {
        Usuario propietario = crearUsuario("propietario.descripcion");
        Categoria categoria = crearCategoriaPersistida(propietario, "Privada");
        Usuario otroUsuario = crearUsuarioPersistido("otro.descripcion");

        assertThrows(IllegalArgumentException.class, () ->
                categoriaService.modificarDescripcion(categoria.getId(), otroUsuario.getId(), "Modificada"));
    }

    @Test
    void deberiaRechazarActivacionDeCategoriaDeOtroUsuario() {
        Usuario propietario = crearUsuario("propietario.activar");
        Categoria categoria = crearCategoriaPersistida(propietario, "Privada");
        categoria.desactivar();
        entityManager.getTransaction().begin();
        entityManager.merge(categoria);
        entityManager.getTransaction().commit();
        Usuario otroUsuario = crearUsuarioPersistido("otro.activar");

        assertThrows(IllegalArgumentException.class, () ->
                categoriaService.activar(categoria.getId(), otroUsuario.getId()));
    }

    @Test
    void deberiaRechazarDesactivacionDeCategoriaDeOtroUsuario() {
        Usuario propietario = crearUsuario("propietario.desactivar");
        Categoria categoria = crearCategoriaPersistida(propietario, "Privada");
        Usuario otroUsuario = crearUsuarioPersistido("otro.desactivar");

        assertThrows(IllegalArgumentException.class, () ->
                categoriaService.desactivar(categoria.getId(), otroUsuario.getId()));
    }

    @Test
    void deberiaRechazarEliminacionDeCategoriaDeOtroUsuario() {
        Usuario propietario = crearUsuario("propietario.eliminar");
        Categoria categoria = crearCategoriaPersistida(propietario, "Privada");
        Usuario otroUsuario = crearUsuarioPersistido("otro.eliminar");
        Long categoriaId = categoria.getId();

        assertThrows(IllegalArgumentException.class, () ->
                categoriaService.eliminar(categoriaId, otroUsuario.getId()));

        assertTrue(categoriaService.buscarPorId(categoriaId).isPresent());
    }

    @Test
    void deberiaLanzarExcepcionCuandoLaCategoriaNoExiste() {
        Usuario usuario = crearUsuarioPersistido("inexistente");

        assertThrows(IllegalArgumentException.class, () ->
                categoriaService.modificarNombre(999999L, usuario.getId(), "Nueva categoría"));
        assertThrows(IllegalArgumentException.class, () ->
                categoriaService.modificarDescripcion(999999L, usuario.getId(), "Nueva descripción"));
        assertThrows(IllegalArgumentException.class, () ->
                categoriaService.activar(999999L, usuario.getId()));
        assertThrows(IllegalArgumentException.class, () ->
                categoriaService.desactivar(999999L, usuario.getId()));
        assertThrows(IllegalArgumentException.class, () ->
                categoriaService.eliminar(999999L, usuario.getId()));
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeRegistraCategoriaNula() {
        assertThrows(NullPointerException.class, () -> categoriaService.registrar(null));
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeBuscaCategoriaConIdNulo() {
        assertThrows(NullPointerException.class, () -> categoriaService.buscarPorId(null));
    }

    @Test
    void deberiaLanzarExcepcionCuandoSeListaPorPerfilFinancieroConIdNulo() {
        assertThrows(NullPointerException.class, () -> categoriaService.listarPorPerfilFinanciero(null));
    }

    @Test
    void deberiaLanzarExcepcionCuandoLosIdsSonNulos() {
        assertThrows(NullPointerException.class, () -> categoriaService.modificarNombre(null, 1L, "Nueva"));
        assertThrows(NullPointerException.class, () -> categoriaService.modificarNombre(1L, null, "Nueva"));
        assertThrows(NullPointerException.class, () -> categoriaService.modificarDescripcion(null, 1L, "Nueva"));
        assertThrows(NullPointerException.class, () -> categoriaService.modificarDescripcion(1L, null, "Nueva"));
        assertThrows(NullPointerException.class, () -> categoriaService.activar(null, 1L));
        assertThrows(NullPointerException.class, () -> categoriaService.activar(1L, null));
        assertThrows(NullPointerException.class, () -> categoriaService.desactivar(null, 1L));
        assertThrows(NullPointerException.class, () -> categoriaService.desactivar(1L, null));
        assertThrows(NullPointerException.class, () -> categoriaService.eliminar(null, 1L));
        assertThrows(NullPointerException.class, () -> categoriaService.eliminar(1L, null));
    }

    @Test
    void deberiaLanzarExcepcionCuandoElNombreEsNulo() {
        Usuario usuario = crearUsuario("nombre.nulo");
        Categoria categoria = crearCategoriaPersistida(usuario, "Alimentación");

        assertThrows(NullPointerException.class, () ->
                categoriaService.modificarNombre(categoria.getId(), usuario.getId(), null));
    }

    @Test
    void deberiaDevolverListaVaciaCuandoNoExistenCategorias() {
        assertTrue(categoriaService.listarTodas().isEmpty());
    }

    private Usuario crearUsuario(String sufijo) {
        Usuario usuario = new Usuario(
                "Ariel",
                "Test",
                "ariel.categoria." + sufijo + "." + System.nanoTime() + "@test.com",
                "hash"
        );
        entityManager.getTransaction().begin();
        entityManager.persist(usuario);
        entityManager.getTransaction().commit();
        return usuario;
    }

    private Usuario crearUsuarioPersistido(String sufijo) {
        return crearUsuario(sufijo);
    }

    private PerfilFinanciero crearPerfil(Usuario usuario, String nombre) {
        return new PerfilFinanciero(nombre, usuario);
    }

    private Categoria crearCategoriaPersistida(Usuario usuario, String nombre) {
        PerfilFinanciero perfil = crearPerfil(usuario, "Perfil principal");
        Categoria categoria = new Categoria(nombre, perfil);

        entityManager.getTransaction().begin();
        entityManager.persist(perfil);
        categoriaService.registrar(categoria);
        entityManager.getTransaction().commit();

        return categoria;
    }

    private void persistir(Usuario usuario, PerfilFinanciero perfil, Categoria... categorias) {
        entityManager.getTransaction().begin();
        if (entityManager.find(Usuario.class, usuario.getId()) == null) {
            entityManager.persist(usuario);
        }
        if (perfil.getId() == null) {
            entityManager.persist(perfil);
        }
        for (Categoria categoria : categorias) {
            categoriaService.registrar(categoria);
        }
        entityManager.getTransaction().commit();
    }
}
