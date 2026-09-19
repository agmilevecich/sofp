package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.TipoCambio;
import ar.com.agmilevecich.sofp.service.TipoCambioService;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/** Formulario para registrar una cotización histórica de un par de monedas. */
public class TipoCambioDialog extends JPanel {

    static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/uuuu");
    static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final TipoCambioService tipoCambioService;
    private final Moneda monedaOrigen;
    private final Moneda monedaDestino;
    private final JTextField cotizacionField;
    private final JTextField fechaField;
    private final JTextField horaField;
    private final JTextField fuenteField;

    public TipoCambioDialog(TipoCambioService tipoCambioService,
                            Moneda monedaOrigen,
                            Moneda monedaDestino,
                            LocalDateTime fechaHoraInicial) {
        this.tipoCambioService = Objects.requireNonNull(tipoCambioService, "El TipoCambioService es obligatorio");
        this.monedaOrigen = Objects.requireNonNull(monedaOrigen, "La moneda de origen es obligatoria");
        this.monedaDestino = Objects.requireNonNull(monedaDestino, "La moneda de destino es obligatoria");
        Objects.requireNonNull(fechaHoraInicial, "La fecha y hora iniciales son obligatorias");

        cotizacionField = new JTextField(16);
        fechaField = new JTextField(10);
        horaField = new JTextField(5);
        fuenteField = new JTextField(20);

        fechaField.setText(fechaHoraInicial.format(FORMATO_FECHA));
        horaField.setText(fechaHoraInicial.format(FORMATO_HORA));
        fuenteField.setText("Carga manual");

        construirPanel();
    }

    public JTextField getCotizacionField() {
        return cotizacionField;
    }

    public JTextField getFechaField() {
        return fechaField;
    }

    public JTextField getHoraField() {
        return horaField;
    }

    public JTextField getFuenteField() {
        return fuenteField;
    }

    TipoCambio registrar() {
        TipoCambio tipoCambio = crearTipoCambio();
        return tipoCambioService.registrar(tipoCambio);
    }

    TipoCambio crearTipoCambio() {
        return new TipoCambio(
                monedaOrigen,
                monedaDestino,
                parsearCotizacion(),
                parsearFechaHora(),
                fuenteField.getText().trim()
        );
    }

    private BigDecimal parsearCotizacion() {
        String texto = cotizacionField.getText().trim().replace(',', '.');
        if (texto.isBlank()) {
            throw new IllegalArgumentException("La cotización es obligatoria");
        }
        try {
            return new BigDecimal(texto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La cotización no es válida", e);
        }
    }

    private LocalDateTime parsearFechaHora() {
        try {
            LocalDate fecha = LocalDate.parse(fechaField.getText().trim(), FORMATO_FECHA);
            LocalTime hora = LocalTime.parse(horaField.getText().trim(), FORMATO_HORA);
            return LocalDateTime.of(fecha, hora);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("La fecha u hora no son válidas", e);
        }
    }

    private void construirPanel() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.anchor = GridBagConstraints.WEST;

        agregarFila(constraints, 0, "Moneda origen", new JLabel(monedaOrigen.getCodigo()));
        agregarFila(constraints, 1, "Moneda destino", new JLabel(monedaDestino.getCodigo()));
        agregarFila(constraints, 2, "Cotización", cotizacionField);
        agregarFila(constraints, 3, "Fecha", fechaField);
        agregarFila(constraints, 4, "Hora", horaField);
        agregarFila(constraints, 5, "Fuente", fuenteField);
    }

    private void agregarFila(GridBagConstraints constraints,
                             int fila,
                             String etiqueta,
                             java.awt.Component componente) {
        constraints.gridx = 0;
        constraints.gridy = fila;
        constraints.weightx = 0.0;
        constraints.fill = GridBagConstraints.NONE;
        add(new JLabel(etiqueta), constraints);

        constraints.gridx = 1;
        constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        add(componente, constraints);
    }
}
