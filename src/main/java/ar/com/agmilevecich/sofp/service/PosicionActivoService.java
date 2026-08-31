package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.domain.Activo;
import ar.com.agmilevecich.sofp.domain.CalculadorPosicionActivo;
import ar.com.agmilevecich.sofp.domain.PosicionActivo;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
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

    public PosicionActivo obtenerPosicion(
            PerfilFinanciero perfilFinanciero,
            Activo activo) {

        Objects.requireNonNull(
                perfilFinanciero,
                "El perfil financiero es obligatorio"
        );

        Objects.requireNonNull(
                activo,
                "El activo es obligatorio"
        );

        return CalculadorPosicionActivo.calcular(
                activo,
                movimientoActivoRepository.listarPorActivoYPerfilFinanciero(
                        activo.getId(),
                        perfilFinanciero.getId()
                )
        );
    }
}
