package ar.com.agmilevecich.sofp.domain;

import ar.com.agmilevecich.sofp.util.Validaciones;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "obligaciones")
public class Obligacion extends EntidadAuditable {

    @Column(name = "importe_original", nullable = false, precision = 19, scale = 2)
    private BigDecimal importeOriginal;

    @Column(name = "saldo_pendiente", nullable = false, precision = 19, scale = 2)
    private BigDecimal saldoPendiente;

    @Column(nullable = false, length = 20)
    @jakarta.persistence.Enumerated(jakarta.persistence.EnumType.STRING)
    private EstadoObligacion estado;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movimiento_origen_id", nullable = false, unique = true)
    private Movimiento movimientoOrigen;

    @OneToMany(mappedBy = "obligacion", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("numero ASC")
    private List<Cuota> cuotas = new ArrayList<>();

    protected Obligacion() {
    }

    public Obligacion(Movimiento movimientoOrigen) {
        this.movimientoOrigen = Objects.requireNonNull(movimientoOrigen, "El movimiento de origen es obligatorio");

        if (movimientoOrigen.getTipoMovimiento() != TipoMovimiento.EGRESO) {
            throw new IllegalArgumentException("El movimiento de origen debe ser un egreso");
        }

        if (movimientoOrigen.getFormaPago() != FormaPago.TARJETA_CREDITO) {
            throw new IllegalArgumentException("El movimiento de origen debe utilizar tarjeta de crédito");
        }

        this.importeOriginal = Validaciones.importePositivo(
                movimientoOrigen.getImporte(),
                "El importe original es obligatorio"
        );

        this.saldoPendiente = this.importeOriginal;
        this.estado = EstadoObligacion.PENDIENTE;
    }

    public BigDecimal getImporteOriginal() {
        return importeOriginal;
    }

    public BigDecimal getSaldoPendiente() {
        return saldoPendiente;
    }

    public EstadoObligacion getEstado() {
        return estado;
    }

    public Movimiento getMovimientoOrigen() {
        return movimientoOrigen;
    }

    public List<Cuota> getCuotas() {
        return Collections.unmodifiableList(cuotas);
    }

    /** La moneda de la obligación es la moneda económica del movimiento que la originó. */
    public Moneda getMoneda() {
        return movimientoOrigen.getMoneda();
    }

    public java.time.LocalDateTime getFechaOrigen() {
        return movimientoOrigen.getFechaHora();
    }

    /**
     * Calcula el ciclo de facturación correspondiente al consumo que originó la obligación.
     * El ciclo no se persiste: se deriva de la configuración actual de la tarjeta y de la fecha del consumo.
     */
    public CicloFacturacion getCicloFacturacion() {
        return movimientoOrigen.getCuenta().calcularCicloFacturacion(
                movimientoOrigen.getFechaHora().toLocalDate()
        );
    }

    /**
     * Genera cuotas iguales sin intereses. La diferencia de centavos, cuando existe,
     * se asigna a la última cuota para que la suma coincida exactamente con el consumo.
     */
    public void generarCuotas(int cantidad) {
        if (cantidad < 1) {
            throw new IllegalArgumentException("La cantidad de cuotas debe ser positiva");
        }
        if (!cuotas.isEmpty()) {
            throw new IllegalStateException("La obligación ya tiene cuotas generadas");
        }

        BigDecimal importeBase = importeOriginal.divide(
                BigDecimal.valueOf(cantidad),
                2,
                RoundingMode.DOWN
        );
        BigDecimal importeAcumulado = BigDecimal.ZERO;
        CicloFacturacion ciclo = getCicloFacturacion();

        for (int numero = 1; numero <= cantidad; numero++) {
            BigDecimal importeCuota = numero == cantidad
                    ? importeOriginal.subtract(importeAcumulado)
                    : importeBase;

            cuotas.add(new Cuota(this, numero, importeCuota, ciclo));
            importeAcumulado = importeAcumulado.add(importeCuota);

            if (numero < cantidad) {
                ciclo = movimientoOrigen.getCuenta().calcularCicloFacturacion(
                        ciclo.getFechaCierre().plusDays(1)
                );
            }
        }
    }

    public void registrarPago(BigDecimal importe) {
        if (estado == EstadoObligacion.PAGADA) {
            throw new IllegalStateException("La obligación ya está pagada");
        }

        BigDecimal pago = Validaciones.importePositivo(importe, "El importe del pago es obligatorio");

        if (pago.compareTo(saldoPendiente) > 0) {
            throw new IllegalArgumentException("El pago no puede superar el saldo pendiente");
        }

        if (cuotas.isEmpty()) {
            saldoPendiente = saldoPendiente.subtract(pago);
        } else {
            BigDecimal restante = pago;
            for (Cuota cuota : cuotas) {
                if (restante.signum() == 0) {
                    break;
                }
                BigDecimal pagoCuota = restante.min(cuota.getSaldoPendiente());
                cuota.registrarPago(pagoCuota);
                restante = restante.subtract(pagoCuota);
            }
            saldoPendiente = saldoPendiente.subtract(pago);
        }

        estado = saldoPendiente.signum() == 0 ? EstadoObligacion.PAGADA : EstadoObligacion.PARCIAL;
    }
}
