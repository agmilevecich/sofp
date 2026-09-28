package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.domain.Movimiento;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.ResumenResultadoFinanciero;
import ar.com.agmilevecich.sofp.domain.TipoMovimiento;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ResultadoFinancieroService {

    private final EntityManager entityManager;
    private final MovimientoRepository movimientoRepository;

    public ResultadoFinancieroService(EntityManager entityManager,
                                      MovimientoRepository movimientoRepository) {
        this.entityManager = Objects.requireNonNull(entityManager, "El EntityManager es obligatorio");
        this.movimientoRepository = Objects.requireNonNull(movimientoRepository, "El MovimientoRepository es obligatorio");
    }

    public ResumenResultadoFinanciero calcular(PerfilFinanciero perfilFinanciero,
                                               Long usuarioId,
                                               LocalDate fechaDesde,
                                               LocalDate fechaHasta) {
        Objects.requireNonNull(perfilFinanciero, "El perfil financiero es obligatorio");
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");
        Objects.requireNonNull(fechaDesde, "La fecha desde es obligatoria");
        Objects.requireNonNull(fechaHasta, "La fecha hasta es obligatoria");

        if (fechaDesde.isAfter(fechaHasta)) {
            throw new IllegalArgumentException("La fecha desde no puede ser posterior a la fecha hasta");
        }

        if (!Objects.equals(perfilFinanciero.getUsuario().getId(), usuarioId)) {
            throw new IllegalArgumentException("El perfil financiero no pertenece al usuario autorizado");
        }

        Set<Long> movimientosPago = entityManager.createQuery(
                """
                SELECT p.movimiento.id
                FROM PagoTarjeta p
                WHERE p.obligacion.movimientoOrigen.cuenta.perfilFinanciero.id = :perfilId
                """,
                Long.class
        ).setParameter("perfilId", perfilFinanciero.getId())
         .getResultStream()
         .collect(Collectors.toSet());

        Set<Long> movimientosReversion = entityManager.createQuery(
                """
                SELECT p.movimientoReversion.id
                FROM PagoTarjeta p
                WHERE p.obligacion.movimientoOrigen.cuenta.perfilFinanciero.id = :perfilId
                  AND p.movimientoReversion IS NOT NULL
                """,
                Long.class
        ).setParameter("perfilId", perfilFinanciero.getId())
         .getResultStream()
         .collect(Collectors.toSet());

        Map<ar.com.agmilevecich.sofp.domain.Moneda, BigDecimal> ingresos = new LinkedHashMap<>();
        Map<ar.com.agmilevecich.sofp.domain.Moneda, BigDecimal> egresos = new LinkedHashMap<>();

        List<Movimiento> movimientos = movimientoRepository.listarTodos();
        for (Movimiento movimiento : movimientos) {
            if (!Objects.equals(movimiento.getCuenta().getPerfilFinanciero().getId(), perfilFinanciero.getId())) {
                continue;
            }
            LocalDate fecha = movimiento.getFechaHora().toLocalDate();
            if (fecha.isBefore(fechaDesde) || fecha.isAfter(fechaHasta)) {
                continue;
            }
            if (movimiento.getOperacionFinanciera() != null
                    || movimientosPago.contains(movimiento.getId())
                    || movimientosReversion.contains(movimiento.getId())) {
                continue;
            }

            Map<ar.com.agmilevecich.sofp.domain.Moneda, BigDecimal> destino =
                    movimiento.getTipoMovimiento() == TipoMovimiento.INGRESO ? ingresos : egresos;
            destino.merge(
                    movimiento.getMoneda(),
                    movimiento.getImporte(),
                    BigDecimal::add
            );
        }

        return new ResumenResultadoFinanciero(fechaDesde, fechaHasta, ingresos, egresos);
    }
}
