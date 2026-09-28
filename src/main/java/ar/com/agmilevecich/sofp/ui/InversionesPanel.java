package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.domain.Activo;
import ar.com.agmilevecich.sofp.domain.PosicionActivo;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.service.CarteraActivoService;
import ar.com.agmilevecich.sofp.service.CotizacionActivoService;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/** Panel del módulo de inversiones. */
public class InversionesPanel extends JPanel {

    private final DefaultListModel<String> modeloPosiciones;
    private final List<PosicionActivo> posiciones;
    private final Map<Activo, BigDecimal> preciosActuales;
    private final JTextField campoPrecio;
    private final JList<String> listaPosiciones;
    private final Consumer<Map<Activo, BigDecimal>> actualizadorPrecios;
    private final CotizacionActivoService cotizacionActivoService;

    /** Constructor del shell sin contexto de usuario. */
    public InversionesPanel() {
        this(null, null, null, null, null);
    }

    /**
     * Constructor para mostrar las posiciones de inversión del perfil del usuario autenticado.
     * La consulta pasa por CarteraActivoService, que mantiene las reglas de autorización.
     */
    public InversionesPanel(CarteraActivoService carteraActivoService,
                            PerfilFinanciero perfilFinanciero,
                            Long usuarioId) {
        this(carteraActivoService, perfilFinanciero, usuarioId, null, null);
    }

    /**
     * Constructor que permite informar precios actuales explícitos y notificarlos
     * a la capa de aplicación.
     */
    public InversionesPanel(CarteraActivoService carteraActivoService,
                            PerfilFinanciero perfilFinanciero,
                            Long usuarioId,
                            Consumer<Map<Activo, BigDecimal>> actualizadorPrecios) {
        this(carteraActivoService, perfilFinanciero, usuarioId, actualizadorPrecios, null);
    }

    /** Constructor que persiste las cotizaciones registradas desde la pantalla. */
    public InversionesPanel(CarteraActivoService carteraActivoService,
                            PerfilFinanciero perfilFinanciero,
                            Long usuarioId,
                            Consumer<Map<Activo, BigDecimal>> actualizadorPrecios,
                            CotizacionActivoService cotizacionActivoService) {

        this.modeloPosiciones = new DefaultListModel<>();
        this.posiciones = new ArrayList<>();
        this.preciosActuales = new LinkedHashMap<>();
        this.actualizadorPrecios = actualizadorPrecios;
        this.cotizacionActivoService = cotizacionActivoService;
        this.campoPrecio = new JTextField(12);

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(new JLabel("Inversiones"), BorderLayout.NORTH);

        this.listaPosiciones = new JList<>(modeloPosiciones);
        listaPosiciones.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                cargarPrecioSeleccionado();
            }
        });
        add(new JScrollPane(listaPosiciones), BorderLayout.CENTER);

        JButton botonActualizar = new JButton("Actualizar precio");
        botonActualizar.addActionListener(event -> {
            try {
                actualizarPrecioSeleccionado();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage(),
                        "Precio inválido",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        JPanel editorPrecio = new JPanel(new FlowLayout(FlowLayout.LEFT));
        editorPrecio.add(new JLabel("Precio actual:"));
        editorPrecio.add(campoPrecio);
        editorPrecio.add(botonActualizar);
        add(editorPrecio, BorderLayout.SOUTH);

        if (carteraActivoService != null || perfilFinanciero != null || usuarioId != null) {
            Objects.requireNonNull(
                    carteraActivoService,
                    "El CarteraActivoService es obligatorio"
            );
            Objects.requireNonNull(
                    perfilFinanciero,
                    "El perfil financiero es obligatorio"
            );
            Objects.requireNonNull(
                    usuarioId,
                    "El id del usuario es obligatorio"
            );

            cargarPosiciones(carteraActivoService.obtenerPosiciones(
                    perfilFinanciero,
                    usuarioId
            ));
        }
    }

    private void cargarPosiciones(List<PosicionActivo> nuevasPosiciones) {
        posiciones.clear();
        posiciones.addAll(nuevasPosiciones);
        refrescarLista();

        if (!posiciones.isEmpty()) {
            listaPosiciones.setSelectedIndex(0);
        }
    }

    private void cargarPrecioSeleccionado() {
        int indice = listaPosiciones.getSelectedIndex();
        if (indice < 0 || indice >= posiciones.size()) {
            campoPrecio.setText("");
            return;
        }

        Activo activo = posiciones.get(indice).getActivo();
        BigDecimal precio = preciosActuales.get(activo);
        campoPrecio.setText(precio != null ? precio.toPlainString() : "");
    }

    void actualizarPrecioSeleccionado() {
        int indice = listaPosiciones.getSelectedIndex();
        if (indice < 0 || indice >= posiciones.size()) {
            throw new IllegalArgumentException("Debe seleccionar un activo");
        }

        String texto = campoPrecio.getText().trim().replace(',', '.');
        if (texto.isEmpty()) {
            throw new IllegalArgumentException("El precio actual es obligatorio");
        }

        BigDecimal precio;
        try {
            precio = new BigDecimal(texto);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("El precio actual debe ser numérico", ex);
        }

        if (precio.signum() <= 0) {
            throw new IllegalArgumentException("El precio actual debe ser mayor que cero");
        }

        Activo activo = posiciones.get(indice).getActivo();
        if (cotizacionActivoService != null) {
            cotizacionActivoService.registrarCotizacion(activo, LocalDate.now(), precio);
        }

        preciosActuales.put(activo, precio);
        refrescarLista();
        listaPosiciones.setSelectedIndex(indice);
        campoPrecio.setText(precio.toPlainString());

        if (actualizadorPrecios != null) {
            actualizadorPrecios.accept(new LinkedHashMap<>(preciosActuales));
        }
    }

    private void refrescarLista() {
        modeloPosiciones.clear();

        for (PosicionActivo posicion : posiciones) {
            BigDecimal precio = preciosActuales.get(posicion.getActivo());
            String textoPrecio = precio != null
                    ? precio.toPlainString()
                    : "sin informar";

            modeloPosiciones.addElement(
                    posicion.getActivo().getSimbolo()
                            + " - "
                            + posicion.getCantidad()
                            + " - Precio actual: "
                            + textoPrecio
            );
        }
    }
}
