package ar.com.agmilevecich.sofp.config;

import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.PerfilFinancieroRepository;
import ar.com.agmilevecich.sofp.persistence.UsuarioRepository;
import ar.com.agmilevecich.sofp.service.PasswordService;
import jakarta.persistence.EntityManager;

import java.util.Objects;

public final class DatosInicialesDesarrollo {

    public static final String EMAIL = "demo@sofp.local";
    public static final String PASSWORD = "sofp1234";

    private DatosInicialesDesarrollo() {
    }

    public static void crearSiNoExisten(EntityManager entityManager) {
        Objects.requireNonNull(entityManager, "El EntityManager es obligatorio");

        UsuarioRepository usuarioRepository = new UsuarioRepository(entityManager);
        if (!usuarioRepository.listarTodos().isEmpty()) {
            return;
        }

        Usuario usuario = new Usuario(
                "Usuario",
                "Demo",
                EMAIL,
                PasswordService.hash(PASSWORD)
        );
        PerfilFinanciero perfil = new PerfilFinanciero(
                "Perfil de desarrollo",
                usuario
        );
        usuario.agregarPerfilFinanciero(perfil);

        entityManager.getTransaction().begin();
        try {
            usuarioRepository.guardar(usuario);
            new PerfilFinancieroRepository(entityManager).guardar(perfil);
            entityManager.getTransaction().commit();
        } catch (RuntimeException e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        }
    }
}
