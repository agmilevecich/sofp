package ar.com.agmilevecich.sofp.config;

import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoMoneda;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.InstitucionFinancieraRepository;
import ar.com.agmilevecich.sofp.persistence.MonedaRepository;
import ar.com.agmilevecich.sofp.persistence.PerfilFinancieroRepository;
import ar.com.agmilevecich.sofp.persistence.UsuarioRepository;
import ar.com.agmilevecich.sofp.service.PasswordService;
import jakarta.persistence.EntityManager;

import java.util.Objects;

public final class DatosInicialesDesarrollo {

    public static final String EMAIL = "demo@sofp.local";
    public static final String PASSWORD = "sofp1234";

    private static final String INSTITUCION_NOMBRE = "Institución de desarrollo";
    private static final String MONEDA_ARS = "ARS";
    private static final String MONEDA_USD = "USD";

    private DatosInicialesDesarrollo() {
    }

    public static void crearSiNoExisten(EntityManager entityManager) {
        Objects.requireNonNull(entityManager, "El EntityManager es obligatorio");

        UsuarioRepository usuarioRepository = new UsuarioRepository(entityManager);
        InstitucionFinancieraRepository institucionRepository =
                new InstitucionFinancieraRepository(entityManager);
        MonedaRepository monedaRepository = new MonedaRepository(entityManager);

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

            if (institucionRepository.buscarPorNombre(INSTITUCION_NOMBRE).isEmpty()) {
                institucionRepository.guardar(
                        new InstitucionFinanciera(
                                INSTITUCION_NOMBRE,
                                TipoInstitucionFinanciera.BANCO
                        )
                );
            }

            if (monedaRepository.buscarPorCodigo(MONEDA_ARS).isEmpty()) {
                monedaRepository.guardar(
                        new Moneda(
                                MONEDA_ARS,
                                "Peso argentino",
                                2,
                                TipoMoneda.FIAT
                        )
                );
            }

            if (monedaRepository.buscarPorCodigo(MONEDA_USD).isEmpty()) {
                monedaRepository.guardar(
                        new Moneda(
                                MONEDA_USD,
                                "Dólar estadounidense",
                                2,
                                TipoMoneda.FIAT
                        )
                );
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
