package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.config.JpaTestManager;
import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.CategoriaRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoriaServiceTest {

    private EntityManager entityManager;
    private CategoriaRepository categoriaRepository;
    private CategoriaService categoriaService;

    @BeforeEach
    void setUp() {

        entityManager =
                JpaTestManager.createEntityManager();

        categoriaRepository =
                new CategoriaRepository(entityManager);

        categoriaService =
                new CategoriaService(
                        categoriaRepository
                );
    }

    @AfterEach
    void tearDown() {

        if (entityManager != null
                && entityManager.isOpen()) {

            entityManager.close();
        }

        JpaTestManager.close();
    }

    @Test
    void deberiaRegistrarCategoria() {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Test",
                        "ariel.categoria." + System.nanoTime() + "@test.com",
                        "hash"
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

        entityManager.getTransaction().begin();

        entityManager.persist(usuario);
        entityManager.persist(perfil);

        Categoria registrada =
                categoriaService.registrar(
                        categoria
                );

        entityManager.getTransaction().commit();

        assertTrue(
                registrada.getId() != null
        );

        assertEquals(
                "Alimentación",
                registrada.getNombre()
        );
    }

    @Test
    void deberiaBuscarCategoriaPorId() {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Test",
                        "ariel.buscar." + System.nanoTime() + "@test.com",
                        "hash"
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

        entityManager.getTransaction().begin();

        entityManager.persist(usuario);
        entityManager.persist(perfil);

        categoriaService.registrar(
                categoria
        );

        entityManager.getTransaction().commit();

        Optional<Categoria> resultado =
                categoriaService.buscarPorId(
                        categoria.getId()
                );

        assertTrue(
                resultado.isPresent()
        );

        assertEquals(
                categoria.getId(),
                resultado.get().getId()
        );

        assertEquals(
                "Alimentación",
                resultado.get().getNombre()
        );
    }

    @Test
    void deberiaListarTodasLasCategorias() {

        Usuario usuario =
                new Usuario(
                        "Ariel",
                        "Test",
                        "ariel.listar." + System.nanoTime() + "@test.com",
                        "hash"
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

        entityManager.getTransaction().begin();

        entityManager.persist(usuario);
        entityManager.persist(perfil);

        categoriaService.registrar(
                categoria1
        );

        categoriaService.registrar(
                categoria2
        );

        entityManager.getTransaction().commit();

        List<Categoria> categorias =
                categoriaService.listarTodas();

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
                        "Test",
                        "ariel.perfil." + System.nanoTime() + "@test.com",
                        "hash"
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

        entityManager.getTransaction().begin();

        entityManager.persist(usuario);
        entityManager.persist(perfil1);
        entityManager.persist(perfil2);

        categoriaService.registrar(
                categoriaPerfil1
        );

        categoriaService.registrar(
                categoriaPerfil2
        );

        entityManager.getTransaction().commit();

        List<Categoria> categorias =
                categoriaService.listarPorPerfilFinanciero(
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
}