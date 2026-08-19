package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.UsuarioRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(
            UsuarioRepository usuarioRepository) {

        this.usuarioRepository =
                Objects.requireNonNull(
                        usuarioRepository,
                        "El repositorio de usuarios es obligatorio"
                );
    }

    public Usuario guardar(Usuario usuario) {

        Objects.requireNonNull(
                usuario,
                "El usuario es obligatorio"
        );

        return usuarioRepository.guardar(usuario);
    }

    public Optional<Usuario> buscarPorId(Long id) {

        Objects.requireNonNull(
                id,
                "El id del usuario es obligatorio"
        );

        return usuarioRepository.buscarPorId(id);
    }

    public Optional<Usuario> buscarPorEmail(String email) {

        Objects.requireNonNull(
                email,
                "El email del usuario es obligatorio"
        );

        return usuarioRepository.buscarPorEmail(email);
    }

    public List<Usuario> listarTodos() {

        return usuarioRepository.listarTodos();
    }

    public Usuario activar(Long usuarioId) {

        Objects.requireNonNull(
                usuarioId,
                "El id del usuario es obligatorio"
        );

        Usuario usuario =
                usuarioRepository.buscarPorId(usuarioId)
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "No existe el usuario con id: "
                                                + usuarioId
                                )
                        );

        usuario.activar();

        return usuarioRepository.guardar(usuario);
    }

    public Usuario desactivar(Long usuarioId) {

        Objects.requireNonNull(
                usuarioId,
                "El id del usuario es obligatorio"
        );

        Usuario usuario =
                usuarioRepository.buscarPorId(usuarioId)
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "No existe el usuario con id: "
                                                + usuarioId
                                )
                        );

        usuario.desactivar();

        return usuarioRepository.guardar(usuario);
    }
}