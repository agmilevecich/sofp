package ar.com.agmilevecich.sofp.domain;

import ar.com.agmilevecich.sofp.util.Validaciones;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
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

    /**
     * Constructor requerido por JPA.
     */
    protected Obligacion() {
    }

    /**
     * Crea una obligación a partir de un movimiento de egreso realizado
     * con tarjeta de crédito.
     */
    public Obligacion(Movimiento movimientoOrigen) {

        this.movimientoOrigen = Objects.requireNonNull(
                movimientoOrigen,
                "El movimiento de origen es obligatorio"
        );

        if (movimientoOrigen.getTipoMovimiento() != TipoMovimiento.EGRESO) {
            throw new IllegalArgumentException(
                    "El movimiento de origen debe ser un egreso"
            );
        }

        if (movimientoOrigen.getFormaPago() != FormaPago.TARJETA_CREDITO) {
            throw new IllegalArgumentException(
                    "El movimiento de origen debe utilizar tarjeta de crédito"
            );
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

    public java.time.LocalDateTime getFechaOrigen() {
        return movimientoOrigen.getFechaHora();
    }

    /**
     * Registra un pago sobre la obligación y actualiza su estado.
     */
    public void registrarPago(BigDecimal importe) {

        if (estado == EstadoObligacion.PAGADA) {
            throw new IllegalStateException(
                    "La obligación ya está pagada"
            );
        }

        BigDecimal pago = Validaciones.importePositivo(
                importe,
                "El importe del pago es obligatorio"
        );

        if (pago.compareTo(saldoPendiente) > 0) {
            throw new IllegalArgumentException(
                    "El pago no puede superar el saldo pendiente"
            );
        }

        saldoPendiente = saldoPendiente.subtract(pago);

        estado = saldoPendiente.signum() == 0
                ? EstadoObligacion.PAGADA
                : EstadoObligacion.PARCIAL;
    }
}
