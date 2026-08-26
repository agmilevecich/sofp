package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.domain.Activo;
import ar.com.agmilevecich.sofp.domain.CalculadorPosicionActivo;
import ar.com.agmilevecich.sofp.domain.PosicionActivo;
import ar.com.agmilevecich.sofp.persistence.MovimientoActivoRepository;

import java.util.Objects;

public class PosicionActivoService {

    private final MovimientoActivoRepository movimientoActivoRepository;

    public PosicionActivoService(
            MovimientoActivoRepository movimientoActivoRepository) {

        this.movimientoActivoRepository = Objects.requireNonNull(
                movimientoActivoRepository,
                "El repositorio de movimientos de activo es obligatorio"
        );
    }

    public PosicionActivo obtenerPosicion(Activo activo) {

        Objects.requireNonNull(
                activo,
                "El activo es obligatorio"
        );

        return CalculadorPosicionActivo.calcular(
                activo,
                movimientoActivoRepository.listarPorActivo(activo.getId())
        );
    }
}
