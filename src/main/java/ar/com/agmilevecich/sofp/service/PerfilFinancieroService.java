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
            String descripcion) {

        Objects.requireNonNull(
                perfilId,
                "El id del perfil financiero es obligatorio"
        );

        Objects.requireNonNull(
                descripcion,
                "La descripción es obligatoria"
        );

        PerfilFinanciero perfil =
                obtenerPorId(perfilId);

        perfil.cambiarDescripcion(descripcion);

        return perfilFinancieroRepository.guardar(perfil);
    }

    public PerfilFinanciero activar(Long perfilId) {

        Objects.requireNonNull(
                perfilId,
                "El id del perfil financiero es obligatorio"
        );

        PerfilFinanciero perfil =
                obtenerPorId(perfilId);

        perfil.activar();

        return perfilFinancieroRepository.guardar(perfil);
    }

    public PerfilFinanciero desactivar(Long perfilId) {

        Objects.requireNonNull(
                perfilId,
                "El id del perfil financiero es obligatorio"
        );

        PerfilFinanciero perfil =
                obtenerPorId(perfilId);

        perfil.desactivar();

        return perfilFinancieroRepository.guardar(perfil);
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