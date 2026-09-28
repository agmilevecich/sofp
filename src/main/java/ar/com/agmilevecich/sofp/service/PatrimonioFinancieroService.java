package ar.com.agmilevecich.sofp.service;

import ar.com.agmilevecich.sofp.domain.Activo;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.ResumenPatrimonial;
import ar.com.agmilevecich.sofp.domain.TipoCuenta;
import ar.com.agmilevecich.sofp.domain.TipoCambio;
import ar.com.agmilevecich.sofp.domain.ValorizacionPosicionActivo;
import ar.com.agmilevecich.sofp.persistence.MonedaRepository;
import ar.com.agmilevecich.sofp.persistence.ObligacionRepository;
import ar.com.agmilevecich.sofp.persistence.TipoCambioRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PatrimonioFinancieroService {

    private static final String CODIGO_MONEDA_PRESENTACION = "ARS";

    private final CuentaService cuentaService;
    private final CarteraActivoService carteraActivoService;
    private final CotizacionActivoService cotizacionActivoService;
    private final ObligacionRepository obligacionRepository;
    private final TipoCambioRepository tipoCambioRepository;
    private final MonedaRepository monedaRepository;

    public PatrimonioFinancieroService(
            CuentaService cuentaService,
            CarteraActivoService carteraActivoService,
            ObligacionRepository obligacionRepository,
            TipoCambioRepository tipoCambioRepository,
            MonedaRepository monedaRepository) {
        this(
                cuentaService,
                carteraActivoService,
                null,
                obligacionRepository,
                tipoCambioRepository,
                monedaRepository
        );
    }

    public PatrimonioFinancieroService(
            CuentaService cuentaService,
            CarteraActivoService carteraActivoService,
            CotizacionActivoService cotizacionActivoService,
            ObligacionRepository obligacionRepository,
            TipoCambioRepository tipoCambioRepository,
            MonedaRepository monedaRepository) {

        this.cuentaService = Objects.requireNonNull(cuentaService, "El CuentaService es obligatorio");
        this.carteraActivoService = Objects.requireNonNull(
                carteraActivoService, "El CarteraActivoService es obligatorio");
        this.cotizacionActivoService = cotizacionActivoService;
        this.obligacionRepository = Objects.requireNonNull(
                obligacionRepository, "El ObligacionRepository es obligatorio");
        this.tipoCambioRepository = Objects.requireNonNull(
                tipoCambioRepository, "El TipoCambioRepository es obligatorio");
        this.monedaRepository = Objects.requireNonNull(
                monedaRepository, "El MonedaRepository es obligatorio");
    }

    public ResumenPatrimonial calcular(
            PerfilFinanciero perfilFinanciero,
            Long usuarioId,
            Map<Activo, BigDecimal> preciosActuales) {

        validarEntrada(perfilFinanciero, usuarioId);
        Objects.requireNonNull(preciosActuales, "Los precios actuales son obligatorios");

        return calcularInterno(
                perfilFinanciero,
                usuarioId,
                carteraActivoService.obtenerValorizaciones(
                        perfilFinanciero,
                        preciosActuales,
                        usuarioId
                )
        );
    }

    public ResumenPatrimonial calcular(
            PerfilFinanciero perfilFinanciero,
            Long usuarioId) {

        validarEntrada(perfilFinanciero, usuarioId);

        if (cotizacionActivoService == null) {
            throw new IllegalStateException(
                    "El servicio de cotizaciones de activos no está configurado"
            );
        }

        return calcularInterno(
                perfilFinanciero,
                usuarioId,
                carteraActivoService.obtenerValorizaciones(perfilFinanciero, usuarioId)
        );
    }

    private ResumenPatrimonial calcularInterno(
            PerfilFinanciero perfilFinanciero,
            Long usuarioId,
            List<ValorizacionPosicionActivo> valorizaciones) {

        Moneda monedaPresentacion = monedaRepository.buscarPorCodigo(CODIGO_MONEDA_PRESENTACION)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe la moneda de presentación " + CODIGO_MONEDA_PRESENTACION));

        BigDecimal activosMonetarios = calcularActivosMonetarios(
                perfilFinanciero,
                usuarioId,
                monedaPresentacion
        );

        BigDecimal activosInversiones = calcularActivosInversiones(
                valorizaciones,
                monedaPresentacion
        );

        BigDecimal pasivosTarjetas = calcularPasivosTarjetas(
                perfilFinanciero,
                usuarioId,
                monedaPresentacion
        );

        return new ResumenPatrimonial(
                monedaPresentacion,
                activosMonetarios,
                activosInversiones,
                pasivosTarjetas
        );
    }

    private BigDecimal calcularActivosMonetarios(
            PerfilFinanciero perfilFinanciero,
            Long usuarioId,
            Moneda monedaPresentacion) {

        BigDecimal total = BigDecimal.ZERO;

        for (Cuenta cuenta : cuentaService.listarPorPerfilFinanciero(
                perfilFinanciero.getId(), usuarioId)) {

            if (cuenta.getTipoCuenta() == TipoCuenta.TARJETA_CREDITO) {
                continue;
            }

            BigDecimal saldo = cuentaService.calcularSaldo(cuenta.getId(), usuarioId);
            total = total.add(convertir(saldo, cuenta.getMoneda(), monedaPresentacion));
        }

        return total;
    }

    private BigDecimal calcularActivosInversiones(
            List<ValorizacionPosicionActivo> valorizaciones,
            Moneda monedaPresentacion) {

        Map<Moneda, BigDecimal> importesPorMoneda = new HashMap<>();

        for (ValorizacionPosicionActivo valorizacion : valorizaciones) {
            Moneda monedaActivo = valorizacion.getPosicion().getActivo().getMoneda();
            importesPorMoneda.merge(
                    monedaActivo,
                    valorizacion.getValorActual(),
                    BigDecimal::add
            );
        }

        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<Moneda, BigDecimal> entry : importesPorMoneda.entrySet()) {
            total = total.add(convertir(
                    entry.getValue(),
                    entry.getKey(),
                    monedaPresentacion
            ));
        }

        return total;
    }

    private BigDecimal calcularPasivosTarjetas(
            PerfilFinanciero perfilFinanciero,
            Long usuarioId,
            Moneda monedaPresentacion) {

        BigDecimal total = BigDecimal.ZERO;

        for (Cuenta cuenta : cuentaService.listarPorPerfilFinanciero(
                perfilFinanciero.getId(), usuarioId)) {

            if (cuenta.getTipoCuenta() != TipoCuenta.TARJETA_CREDITO) {
                continue;
            }

            BigDecimal deuda = obligacionRepository.sumarCreditoUtilizadoPorCuenta(
                    cuenta.getId(),
                    cuenta.getMoneda()
            );

            total = total.add(convertir(deuda, cuenta.getMoneda(), monedaPresentacion));
        }

        return total;
    }

    private BigDecimal convertir(
            BigDecimal importe,
            Moneda monedaOrigen,
            Moneda monedaDestino) {

        if (monedaOrigen.equals(monedaDestino)) {
            return importe;
        }

        TipoCambio cambio = tipoCambioRepository
                .buscarUltimaPorMonedas(monedaOrigen, monedaDestino)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe cotización actual de "
                                + monedaOrigen.getCodigo()
                                + " a "
                                + monedaDestino.getCodigo()
                ));

        return cambio.convertir(importe);
    }

    private void validarEntrada(PerfilFinanciero perfilFinanciero, Long usuarioId) {
        Objects.requireNonNull(perfilFinanciero, "El perfil financiero es obligatorio");
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");
        validarPropietario(perfilFinanciero, usuarioId);
    }

    private void validarPropietario(PerfilFinanciero perfilFinanciero, Long usuarioId) {
        if (!Objects.equals(perfilFinanciero.getUsuario().getId(), usuarioId)) {
            throw new IllegalArgumentException(
                    "El usuario no es propietario del perfil financiero"
            );
        }
    }
}
