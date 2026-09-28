package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.domain.Activo;
import ar.com.agmilevecich.sofp.domain.CotizacionActivo;
import ar.com.agmilevecich.sofp.persistence.CotizacionActivoRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class CotizacionActivoService {

    private final CotizacionActivoRepository cotizacionActivoRepository;

    public CotizacionActivoService(CotizacionActivoRepository cotizacionActivoRepository) {
        this.cotizacionActivoRepository = Objects.requireNonNull(
                cotizacionActivoRepository,
                "El repositorio de cotizaciones de activos es obligatorio"
        );
    }

    public CotizacionActivo registrarCotizacion(Activo activo, LocalDate fecha, BigDecimal precio) {
        Objects.requireNonNull(activo, "El activo es obligatorio");
        Objects.requireNonNull(fecha, "La fecha es obligatoria");
        Objects.requireNonNull(precio, "El precio es obligatorio");

        Optional<CotizacionActivo> existente =
                cotizacionActivoRepository.buscarPorActivoYFecha(activo.getId(), fecha);

        if (existente.isPresent()) {
            CotizacionActivo cotizacion = existente.get();
            cotizacion.cambiarPrecio(precio);
            return cotizacionActivoRepository.guardar(cotizacion);
        }

        return cotizacionActivoRepository.guardar(
                new CotizacionActivo(activo, fecha, precio)
        );
    }

    public Optional<CotizacionActivo> obtenerUltimaCotizacion(Activo activo) {
        Objects.requireNonNull(activo, "El activo es obligatorio");
        Objects.requireNonNull(activo.getId(), "El activo debe estar persistido");
        return cotizacionActivoRepository.buscarUltimaPorActivo(activo.getId());
    }

    public Map<Activo, BigDecimal> obtenerPreciosActuales(List<Activo> activos) {
        Objects.requireNonNull(activos, "La lista de activos es obligatoria");

        Map<Activo, BigDecimal> precios = new LinkedHashMap<>();
        for (Activo activo : activos) {
            Objects.requireNonNull(activo, "El activo no puede ser nulo");
            obtenerUltimaCotizacion(activo)
                    .ifPresent(cotizacion -> precios.put(activo, cotizacion.getPrecio()));
        }
        return precios;
    }
}
