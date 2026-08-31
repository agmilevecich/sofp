package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.domain.Activo;
import ar.com.agmilevecich.sofp.domain.CalculadorPosicionActivo;
import ar.com.agmilevecich.sofp.domain.PosicionActivo;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.persistence.MovimientoActivoRepository;

import java.util.Objects;

public class PosicionActivoService {

    private final MovimientoActivoRepository movimientoActivoRepository;

    public PosicionActivoService(MovimientoActivoRepository movimientoActivoRepository) {
        this.movimientoActivoRepository = Objects.requireNonNull(
                movimientoActivoRepository,
                "El repositorio de movimientos de activo es obligatorio");
    }

    public PosicionActivo obtenerPosicion(PerfilFinanciero perfilFinanciero, Activo activo, Long usuarioId) {
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");
        validarPropietario(perfilFinanciero, usuarioId);
        return obtenerPosicionInterna(perfilFinanciero, activo);
    }

    /* API interna para coordinación del paquete y tests existentes. */
    PosicionActivo obtenerPosicion(PerfilFinanciero perfilFinanciero, Activo activo) {
        return obtenerPosicionInterna(perfilFinanciero, activo);
    }

    private PosicionActivo obtenerPosicionInterna(PerfilFinanciero perfilFinanciero, Activo activo) {
        Objects.requireNonNull(perfilFinanciero, "El perfil financiero es obligatorio");
        Objects.requireNonNull(activo, "El activo es obligatorio");
        return CalculadorPosicionActivo.calcular(
                activo,
                movimientoActivoRepository.listarPorActivoYPerfilFinanciero(
                        activo.getId(), perfilFinanciero.getId()));
    }

    private void validarPropietario(PerfilFinanciero perfilFinanciero, Long usuarioId) {
        Objects.requireNonNull(perfilFinanciero, "El perfil financiero es obligatorio");
        if (!Objects.equals(perfilFinanciero.getUsuario().getId(), usuarioId)) {
            throw new IllegalArgumentException("El usuario no es propietario del perfil financiero");
        }
    }
}
