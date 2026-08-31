package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.domain.Activo;
import ar.com.agmilevecich.sofp.domain.CalculadorPosicionActivo;
import ar.com.agmilevecich.sofp.domain.DetalleComposicionCarteraActivo;
import ar.com.agmilevecich.sofp.domain.DetalleMovimientoCarteraActivo;
import ar.com.agmilevecich.sofp.domain.MovimientoActivo;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.PosicionActivo;
import ar.com.agmilevecich.sofp.domain.ReporteCarteraActivo;
import ar.com.agmilevecich.sofp.domain.ValorizacionPosicionActivo;
import ar.com.agmilevecich.sofp.persistence.MovimientoActivoRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CarteraActivoService {

    private final MovimientoActivoRepository movimientoActivoRepository;

    public CarteraActivoService(MovimientoActivoRepository movimientoActivoRepository) {
        this.movimientoActivoRepository = Objects.requireNonNull(
                movimientoActivoRepository, "El repositorio de movimientos de activo no puede ser nulo");
    }

    public List<PosicionActivo> obtenerPosiciones(PerfilFinanciero perfilFinanciero, Long usuarioId) {
        validarPropietario(perfilFinanciero, usuarioId);
        return obtenerPosicionesInternas(perfilFinanciero);
    }

    public List<ValorizacionPosicionActivo> obtenerValorizaciones(
            PerfilFinanciero perfilFinanciero,
            Map<Activo, BigDecimal> preciosActuales,
            Long usuarioId) {
        validarPropietario(perfilFinanciero, usuarioId);
        Objects.requireNonNull(preciosActuales, "Los precios actuales no pueden ser nulos");
        List<ValorizacionPosicionActivo> valorizaciones = new ArrayList<>();
        for (PosicionActivo posicion : obtenerPosicionesInternas(perfilFinanciero)) {
            Activo activo = posicion.getActivo();
            if (!preciosActuales.containsKey(activo)) {
                throw new IllegalArgumentException("No existe precio actual para el activo: " + activo.getSimbolo());
            }
            valorizaciones.add(new ValorizacionPosicionActivo(posicion, preciosActuales.get(activo)));
        }
        return valorizaciones;
    }

    public ReporteCarteraActivo obtenerReporte(
            PerfilFinanciero perfilFinanciero,
            Map<Activo, BigDecimal> preciosActuales,
            Long usuarioId) {
        validarPropietario(perfilFinanciero, usuarioId);
        return new ReporteCarteraActivo(obtenerValorizacionesInternas(perfilFinanciero, preciosActuales));
    }

    public List<DetalleComposicionCarteraActivo> obtenerComposicion(
            PerfilFinanciero perfilFinanciero,
            Map<Activo, BigDecimal> preciosActuales,
            Long usuarioId) {
        validarPropietario(perfilFinanciero, usuarioId);
        return new ReporteCarteraActivo(obtenerValorizacionesInternas(perfilFinanciero, preciosActuales)).getComposicion();
    }

    public List<DetalleMovimientoCarteraActivo> obtenerMovimientos(
            PerfilFinanciero perfilFinanciero, Long usuarioId) {
        validarPropietario(perfilFinanciero, usuarioId);
        return movimientoActivoRepository.listarPorPerfilFinanciero(perfilFinanciero.getId())
                .stream().map(DetalleMovimientoCarteraActivo::new).toList();
    }

    /* API interna para coordinación del paquete y tests existentes. */
    List<PosicionActivo> obtenerPosiciones(PerfilFinanciero perfilFinanciero) {
        return obtenerPosicionesInternas(perfilFinanciero);
    }

    List<ValorizacionPosicionActivo> obtenerValorizaciones(
            PerfilFinanciero perfilFinanciero, Map<Activo, BigDecimal> preciosActuales) {
        return obtenerValorizacionesInternas(perfilFinanciero, preciosActuales);
    }

    ReporteCarteraActivo obtenerReporte(PerfilFinanciero perfilFinanciero, Map<Activo, BigDecimal> preciosActuales) {
        return new ReporteCarteraActivo(obtenerValorizacionesInternas(perfilFinanciero, preciosActuales));
    }

    List<DetalleComposicionCarteraActivo> obtenerComposicion(
            PerfilFinanciero perfilFinanciero, Map<Activo, BigDecimal> preciosActuales) {
        return obtenerReporte(perfilFinanciero, preciosActuales).getComposicion();
    }

    List<DetalleMovimientoCarteraActivo> obtenerMovimientos(PerfilFinanciero perfilFinanciero) {
        return movimientoActivoRepository.listarPorPerfilFinanciero(perfilFinanciero.getId())
                .stream().map(DetalleMovimientoCarteraActivo::new).toList();
    }

    private List<PosicionActivo> obtenerPosicionesInternas(PerfilFinanciero perfilFinanciero) {
        Objects.requireNonNull(perfilFinanciero, "El perfil financiero no puede ser nulo");
        List<MovimientoActivo> movimientos = movimientoActivoRepository.listarPorPerfilFinanciero(perfilFinanciero.getId());
        Map<Activo, List<MovimientoActivo>> movimientosPorActivo = new LinkedHashMap<>();
        for (MovimientoActivo movimiento : movimientos) {
            movimientosPorActivo.computeIfAbsent(movimiento.getActivo(), activo -> new ArrayList<>()).add(movimiento);
        }
        List<PosicionActivo> posiciones = new ArrayList<>();
        for (Map.Entry<Activo, List<MovimientoActivo>> entry : movimientosPorActivo.entrySet()) {
            PosicionActivo posicion = CalculadorPosicionActivo.calcular(entry.getKey(), entry.getValue());
            if (posicion.getCantidad().compareTo(BigDecimal.ZERO) != 0) posiciones.add(posicion);
        }
        return posiciones;
    }

    private List<ValorizacionPosicionActivo> obtenerValorizacionesInternas(
            PerfilFinanciero perfilFinanciero, Map<Activo, BigDecimal> preciosActuales) {
        Objects.requireNonNull(preciosActuales, "Los precios actuales no pueden ser nulos");
        List<ValorizacionPosicionActivo> valorizaciones = new ArrayList<>();
        for (PosicionActivo posicion : obtenerPosicionesInternas(perfilFinanciero)) {
            Activo activo = posicion.getActivo();
            if (!preciosActuales.containsKey(activo)) {
                throw new IllegalArgumentException("No existe precio actual para el activo: " + activo.getSimbolo());
            }
            valorizaciones.add(new ValorizacionPosicionActivo(posicion, preciosActuales.get(activo)));
        }
        return valorizaciones;
    }

    private void validarPropietario(PerfilFinanciero perfilFinanciero, Long usuarioId) {
        Objects.requireNonNull(perfilFinanciero, "El perfil financiero no puede ser nulo");
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");
        if (!Objects.equals(perfilFinanciero.getUsuario().getId(), usuarioId)) {
            throw new IllegalArgumentException("El usuario no es propietario del perfil financiero");
        }
    }
}
