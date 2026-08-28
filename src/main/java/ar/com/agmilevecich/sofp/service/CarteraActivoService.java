package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.domain.Activo;
import ar.com.agmilevecich.sofp.domain.MovimientoActivo;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.PosicionActivo;
import ar.com.agmilevecich.sofp.persistence.MovimientoActivoRepository;

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

    public List<PosicionActivo> obtenerPosiciones(PerfilFinanciero perfilFinanciero) {
        Objects.requireNonNull(perfilFinanciero, "El perfil financiero no puede ser nulo");

        List<MovimientoActivo> movimientos = movimientoActivoRepository
                .listarPorPerfilFinanciero(perfilFinanciero.getId());

        Map<Activo, List<MovimientoActivo>> movimientosPorActivo = new LinkedHashMap<>();
        for (MovimientoActivo movimiento : movimientos) {
            movimientosPorActivo
                    .computeIfAbsent(movimiento.getActivo(), activo -> new ArrayList<>())
                    .add(movimiento);
        }

        List<PosicionActivo> posiciones = new ArrayList<>();
        CalculadorPosicionActivo calculador = new CalculadorPosicionActivo();
        for (Map.Entry<Activo, List<MovimientoActivo>> entry : movimientosPorActivo.entrySet()) {
            PosicionActivo posicion = calculador.calcular(entry.getKey(), entry.getValue());
            if (posicion.getCantidad().compareTo(java.math.BigDecimal.ZERO) != 0) {
                posiciones.add(posicion);
            }
        }

        return posiciones;
    }
}
