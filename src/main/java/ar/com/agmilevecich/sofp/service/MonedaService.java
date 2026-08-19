package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.persistence.MonedaRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MonedaService {

    private final MonedaRepository monedaRepository;

    public MonedaService(MonedaRepository monedaRepository) {

        this.monedaRepository =
                Objects.requireNonNull(
                        monedaRepository,
                        "El MonedaRepository es obligatorio"
                );
    }

    public Moneda guardar(Moneda moneda) {

        Objects.requireNonNull(
                moneda,
                "La moneda es obligatoria"
        );

        return monedaRepository.guardar(moneda);
    }

    public Optional<Moneda> buscarPorId(Long id) {

        Objects.requireNonNull(
                id,
                "El id es obligatorio"
        );

        return monedaRepository.buscarPorId(id);
    }

    public Optional<Moneda> buscarPorCodigo(String codigo) {

        Objects.requireNonNull(
                codigo,
                "El código es obligatorio"
        );

        return monedaRepository.buscarPorCodigo(codigo);
    }

    public List<Moneda> listarTodas() {

        return monedaRepository.listarTodas();
    }

    public Moneda cambiarNombre(
            Long monedaId,
            String nuevoNombre) {

        Objects.requireNonNull(
                monedaId,
                "El id de la moneda es obligatorio"
        );

        Moneda moneda =
                monedaRepository.buscarPorId(monedaId)
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "No existe una moneda con id: "
                                                + monedaId
                                )
                        );

        moneda.cambiarNombre(nuevoNombre);

        return monedaRepository.guardar(moneda);
    }

    public Moneda cambiarCantidadDecimales(
            Long monedaId,
            Integer cantidadDecimales) {

        Objects.requireNonNull(
                monedaId,
                "El id de la moneda es obligatorio"
        );

        Moneda moneda =
                monedaRepository.buscarPorId(monedaId)
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "No existe una moneda con id: "
                                                + monedaId
                                )
                        );

        moneda.cambiarCantidadDecimales(cantidadDecimales);

        return monedaRepository.guardar(moneda);
    }
}