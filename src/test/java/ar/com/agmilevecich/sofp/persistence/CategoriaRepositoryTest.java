package ar.com.agmilevecich.sofp.persistence;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.Usuario;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CategoriaRepositoryTest {

    private EntityManager em;

    @BeforeEach
    void iniciarBaseDeDatos() {

        JpaTestManager.close();

        em = JpaTestManager.createEntityManager();
    }

    @AfterEach
    void cerrarBaseDeDatos() {

        if (em != null && em.isOpen()) {
            em.close();
        }

        JpaTestManager.close();
    }

    @Test
    void deberiaGuardarYBuscarCategoriaPorId() {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Milevecich",
                        "ariel." + System.nanoTime() + "@test.com",
                        "hash-test"
                );

        PerfilFinanciero perfil =
                new PerfilFinanciero(
                        "Perfil principal",
                        usuario
                );

        Categoria categoria =
                new Categoria(
                        "Alimentación",
                        perfil
                );

        em.getTransaction().begin();

        em.persist(usuario);
        em.persist(perfil);

        CategoriaRepository repository =
                new CategoriaRepository(em);

        repository.guardar(categoria);

        em.getTransaction().commit();

        Optional<Categoria> resultado =
                repository.buscarPorId(
                        categoria.getId()
                );

        assertTrue(resultado.isPresent());

        assertEquals(
                "Alimentación",
                resultado.get().getNombre()
        );

        assertTrue(
                resultado.get().isActiva()
        );

        assertEquals(
                perfil.getId(),
                resultado.get()
                        .getPerfilFinanciero()
                        .getId()
        );
    }

    @Test
    void deberiaListarTodasLasCategorias() {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Milevecich",
                        "ariel." + System.nanoTime() + "@test.com",
                        "hash-test"
                );

        PerfilFinanciero perfil =
                new PerfilFinanciero(
                        "Perfil principal",
                        usuario
                );

        Categoria categoria1 =
                new Categoria(
                        "Alimentación",
                        perfil
                );

        Categoria categoria2 =
                new Categoria(
                        "Transporte",
                        perfil
                );

        em.getTransaction().begin();

        em.persist(usuario);
        em.persist(perfil);

        CategoriaRepository repository =
                new CategoriaRepository(em);

        repository.guardar(categoria1);
        repository.guardar(categoria2);

        em.getTransaction().commit();

        List<Categoria> categorias =
                repository.listarTodas();

        assertEquals(
                2,
                categorias.size()
        );

        assertEquals(
                "Alimentación",
                categorias.get(0).getNombre()
        );

        assertEquals(
                "Transporte",
                categorias.get(1).getNombre()
        );
    }

    @Test
    void deberiaListarCategoriasPorPerfilFinanciero() {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Milevecich",
                        "ariel." + System.nanoTime() + "@test.com",
                        "hash-test"
                );

        PerfilFinanciero perfil1 =
                new PerfilFinanciero(
                        "Perfil principal",
                        usuario
                );

        PerfilFinanciero perfil2 =
                new PerfilFinanciero(
                        "Perfil secundario",
                        usuario
                );

        Categoria categoriaPerfil1 =
                new Categoria(
                        "Alimentación",
                        perfil1
                );

        Categoria categoriaPerfil2 =
                new Categoria(
                        "Transporte",
                        perfil2
                );

        em.getTransaction().begin();

        em.persist(usuario);
        em.persist(perfil1);
        em.persist(perfil2);

        CategoriaRepository repository =
                new CategoriaRepository(em);

        repository.guardar(categoriaPerfil1);
        repository.guardar(categoriaPerfil2);

        em.getTransaction().commit();

        List<Categoria> categorias =
                repository.listarPorPerfilFinanciero(
                        perfil1.getId()
                );

        assertEquals(
                1,
                categorias.size()
        );

        assertEquals(
                "Alimentación",
                categorias.get(0).getNombre()
        );

        assertEquals(
                perfil1.getId(),
                categorias.get(0)
                        .getPerfilFinanciero()
                        .getId()
        );
    }

    @Test
    void deberiaActualizarCategoriaExistente() {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Milevecich",
                        "ariel." + System.nanoTime() + "@test.com",
                        "hash-test"
                );

        PerfilFinanciero perfil =
                new PerfilFinanciero(
                        "Perfil principal",
                        usuario
                );

        Categoria categoria =
                new Categoria(
                        "Alimentación",
                        perfil
                );

        em.getTransaction().begin();

        em.persist(usuario);
        em.persist(perfil);

        CategoriaRepository repository =
                new CategoriaRepository(em);

        repository.guardar(categoria);

        em.getTransaction().commit();

        Long id = categoria.getId();

        em.clear();

        Categoria categoriaModificada =
                repository.buscarPorId(id)
                        .orElseThrow();

        em.getTransaction().begin();

        categoriaModificada.renombrar(
                "Alimentación y supermercado"
        );

        categoriaModificada.cambiarDescripcion(
                "Gastos relacionados con alimentación"
        );

        repository.guardar(categoriaModificada);

        em.getTransaction().commit();

        em.clear();

        Categoria categoriaVerificada =
                repository.buscarPorId(id)
                        .orElseThrow();

        assertEquals(
                id,
                categoriaVerificada.getId()
        );

        assertEquals(
                "Alimentación y supermercado",
                categoriaVerificada.getNombre()
        );

        assertEquals(
                "Gastos relacionados con alimentación",
                categoriaVerificada.getDescripcion()
        );
    }
}
