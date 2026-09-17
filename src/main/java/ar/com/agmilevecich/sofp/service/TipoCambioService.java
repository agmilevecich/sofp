package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.domain.TipoCambio;
import ar.com.agmilevecich.sofp.persistence.TipoCambioRepository;

import java.util.Objects;

public class TipoCambioService {

    private final TipoCambioRepository tipoCambioRepository;

    public TipoCambioService(TipoCambioRepository tipoCambioRepository) {
        this.tipoCambioRepository = Objects.requireNonNull(
                tipoCambioRepository,
                "El TipoCambioRepository es obligatorio"
        );
    }

    public TipoCambio registrar(TipoCambio tipoCambio) {
        Objects.requireNonNull(
                tipoCambio,
                "El tipo de cambio es obligatorio"
        );

        return tipoCambioRepository.guardar(tipoCambio);
    }
}
