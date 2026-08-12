package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.persistence.CategoriaRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(
            CategoriaRepository categoriaRepository) {

        this.categoriaRepository =
                Objects.requireNonNull(
                        categoriaRepository,
                        "El CategoriaRepository es obligatorio"
                );
    }

    public Categoria registrar(
            Categoria categoria) {

        Objects.requireNonNull(
                categoria,
                "La categoría es obligatoria"
        );

        return categoriaRepository.guardar(categoria);
    }

    public Optional<Categoria> buscarPorId(Long id) {

        Objects.requireNonNull(
                id,
                "El id de la categoría es obligatorio"
        );

        return categoriaRepository.buscarPorId(id);
    }

    public List<Categoria> listarTodas() {

        return categoriaRepository.listarTodas();
    }

    public List<Categoria> listarPorPerfilFinanciero(
            Long perfilFinancieroId) {

        Objects.requireNonNull(
                perfilFinancieroId,
                "El id del perfil financiero es obligatorio"
        );

        return categoriaRepository.listarPorPerfilFinanciero(
                perfilFinancieroId
        );
    }
}