package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.persistence.PerfilFinancieroRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PerfilFinancieroService {

    private final PerfilFinancieroRepository perfilFinancieroRepository;

    public PerfilFinancieroService(
            PerfilFinancieroRepository perfilFinancieroRepository) {

        this.perfilFinancieroRepository =
                Objects.requireNonNull(
                        perfilFinancieroRepository,
                        "El repositorio de perfiles financieros es obligatorio"
                );
    }

    public PerfilFinanciero guardar(PerfilFinanciero perfil) {

        Objects.requireNonNull(
                perfil,
                "El perfil financiero es obligatorio"
        );

        return perfilFinancieroRepository.guardar(perfil);
    }

    public Optional<PerfilFinanciero> buscarPorId(Long id) {

        Objects.requireNonNull(
                id,
                "El id del perfil financiero es obligatorio"
        );

        return perfilFinancieroRepository.buscarPorId(id);
    }

    public List<PerfilFinanciero> listarTodos() {

        return perfilFinancieroRepository.listarTodos();
    }

    public List<PerfilFinanciero> listarPorUsuario(Long usuarioId) {

        Objects.requireNonNull(
                usuarioId,
                "El id del usuario es obligatorio"
        );

        return perfilFinancieroRepository.listarPorUsuario(usuarioId);
    }

    public PerfilFinanciero cambiarDescripcion(
            Long perfilId,
            Long usuarioId,
            String descripcion) {

        validarIds(perfilId, usuarioId);

        Objects.requireNonNull(
                descripcion,
                "La descripción es obligatoria"
        );

        PerfilFinanciero perfil =
                obtenerPorId(perfilId);

        verificarPropietario(perfil, usuarioId);

        perfil.cambiarDescripcion(descripcion);

        return perfilFinancieroRepository.guardar(perfil);
    }

    public PerfilFinanciero activar(
            Long perfilId,
            Long usuarioId) {

        validarIds(perfilId, usuarioId);

        PerfilFinanciero perfil =
                obtenerPorId(perfilId);

        verificarPropietario(perfil, usuarioId);

        perfil.activar();

        return perfilFinancieroRepository.guardar(perfil);
    }

    public PerfilFinanciero desactivar(
            Long perfilId,
            Long usuarioId) {

        validarIds(perfilId, usuarioId);

        PerfilFinanciero perfil =
                obtenerPorId(perfilId);

        verificarPropietario(perfil, usuarioId);

        perfil.desactivar();

        return perfilFinancieroRepository.guardar(perfil);
    }

    private void validarIds(Long perfilId, Long usuarioId) {

        Objects.requireNonNull(
                perfilId,
                "El id del perfil financiero es obligatorio"
        );

        Objects.requireNonNull(
                usuarioId,
                "El id del usuario es obligatorio"
        );
    }

    private void verificarPropietario(
            PerfilFinanciero perfil,
            Long usuarioId) {

        if (!perfil.getUsuario().getId().equals(usuarioId)) {
            throw new IllegalArgumentException(
                    "El usuario no es propietario del perfil financiero"
            );
        }
    }

    private PerfilFinanciero obtenerPorId(Long perfilId) {

        Objects.requireNonNull(
                perfilId,
                "El id del perfil financiero es obligatorio"
        );

        return perfilFinancieroRepository.buscarPorId(perfilId)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "No existe el perfil financiero con id: "
                                        + perfilId
                        )
                );
    }
}