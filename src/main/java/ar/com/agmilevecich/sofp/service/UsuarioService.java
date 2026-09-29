package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.PerfilFinancieroRepository;
import ar.com.agmilevecich.sofp.persistence.UsuarioRepository;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EntityManager entityManager;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this(usuarioRepository, null);
    }

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            EntityManager entityManager) {

        this.usuarioRepository =
                Objects.requireNonNull(
                        usuarioRepository,
                        "El repositorio de usuarios es obligatorio"
                );
        this.entityManager = entityManager;
    }

    public Usuario guardar(Usuario usuario) {
        Objects.requireNonNull(usuario, "El usuario es obligatorio");
        return usuarioRepository.guardar(usuario);
    }

    public Usuario registrar(
            String nombre,
            String apellido,
            String email,
            String password,
            String nombrePerfil) {

        if (entityManager == null) {
            throw new IllegalStateException(
                    "El EntityManager es obligatorio para registrar usuarios"
            );
        }

        validarTexto(nombre, "El nombre es obligatorio");
        validarTexto(apellido, "El apellido es obligatorio");
        validarTexto(email, "El email es obligatorio");
        validarTexto(password, "La contraseña es obligatoria");
        validarTexto(nombrePerfil, "El nombre del perfil es obligatorio");

        if (usuarioRepository.buscarPorEmail(email.trim()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con ese email");
        }

        entityManager.getTransaction().begin();
        try {
            Usuario usuario = new Usuario(
                    nombre.trim(),
                    apellido.trim(),
                    email.trim(),
                    PasswordService.hash(password)
            );

            PerfilFinanciero perfil = new PerfilFinanciero(
                    nombrePerfil.trim(),
                    usuario
            );
            usuario.agregarPerfilFinanciero(perfil);

            usuarioRepository.guardar(usuario);
            new PerfilFinancieroRepository(entityManager).guardar(perfil);

            entityManager.getTransaction().commit();
            return usuario;
        } catch (RuntimeException exception) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw exception;
        }
    }

    public Optional<Usuario> buscarPorId(Long id) {
        Objects.requireNonNull(id, "El id del usuario es obligatorio");
        return usuarioRepository.buscarPorId(id);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        Objects.requireNonNull(email, "El email del usuario es obligatorio");
        return usuarioRepository.buscarPorEmail(email);
    }

    public Optional<Usuario> autenticar(String email, String password) {
        Objects.requireNonNull(email, "El email del usuario es obligatorio");
        Objects.requireNonNull(password, "La contraseña es obligatoria");

        return usuarioRepository.buscarPorEmail(email)
                .filter(Usuario::isActivo)
                .filter(usuario -> PasswordService.matches(
                        password,
                        usuario.getPasswordHash()
                ));
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.listarTodos();
    }

    public Usuario activar(Long usuarioId) {
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");
        Usuario usuario = usuarioRepository.buscarPorId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe el usuario con id: " + usuarioId
                ));
        usuario.activar();
        return usuarioRepository.guardar(usuario);
    }

    public Usuario desactivar(Long usuarioId) {
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");
        Usuario usuario = usuarioRepository.buscarPorId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe el usuario con id: " + usuarioId
                ));
        usuario.desactivar();
        return usuarioRepository.guardar(usuario);
    }

    private void validarTexto(String valor, String mensaje) {
        if (valor == null) {
            throw new NullPointerException(mensaje);
        }
        if (valor.isBlank()) {
            throw new IllegalArgumentException(mensaje);
        }
    }
}