package ar.com.agmilevecich.sofp.config;

import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.InstitucionFinancieraRepository;
import ar.com.agmilevecich.sofp.persistence.PerfilFinancieroRepository;
import ar.com.agmilevecich.sofp.persistence.UsuarioRepository;
import ar.com.agmilevecich.sofp.service.PasswordService;
import jakarta.persistence.EntityManager;

import java.util.Objects;

public final class DatosInicialesDesarrollo {

    public static final String EMAIL = "demo@sofp.local";
    public static final String PASSWORD = "sofp1234";

    private static final String INSTITUCION_NOMBRE = "Institución de desarrollo";

    private DatosInicialesDesarrollo() {
    }

    public static void crearSiNoExisten(EntityManager entityManager) {
        Objects.requireNonNull(entityManager, "El EntityManager es obligatorio");

        UsuarioRepository usuarioRepository = new UsuarioRepository(entityManager);
        InstitucionFinancieraRepository institucionRepository =
                new InstitucionFinancieraRepository(entityManager);

        entityManager.getTransaction().begin();
        try {
            Usuario usuario = usuarioRepository.buscarPorEmail(EMAIL).orElse(null);

            if (usuario == null) {
                usuario = new Usuario(
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
                usuarioRepository.guardar(usuario);
                new PerfilFinancieroRepository(entityManager).guardar(perfil);
            }

            InstitucionFinanciera institucionDesarrollo =
                    institucionRepository.buscarPorNombre(INSTITUCION_NOMBRE).orElse(null);

            if (institucionDesarrollo == null) {
                institucionRepository.guardar(
                        new InstitucionFinanciera(
                                INSTITUCION_NOMBRE,
                                TipoInstitucionFinanciera.BANCO,
                                usuario
                        )
                );
            } else if (institucionDesarrollo.getUsuario() == null) {
                institucionDesarrollo.asignarUsuario(usuario);
                institucionRepository.guardar(institucionDesarrollo);
            }

            entityManager.getTransaction().commit();
        } catch (RuntimeException e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        }
    }
}
