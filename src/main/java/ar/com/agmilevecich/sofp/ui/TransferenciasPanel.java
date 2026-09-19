package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.service.CategoriaService;
import ar.com.agmilevecich.sofp.service.CuentaService;
import ar.com.agmilevecich.sofp.service.OperacionFinancieraService;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/** Formulario especializado para registrar transferencias entre cuentas propias. */
public class TransferenciasPanel extends JPanel {

    private final OperacionFinancieraService operacionFinancieraService;
    private final CuentaService cuentaService;
    private final CategoriaService categoriaService;
    private final Long perfilFinancieroId;
    private final Long usuarioId;
    private final Runnable onTransferenciaRegistrada;
    private final JComboBox<Cuenta> cuentaOrigenComboBox;
    private final JComboBox<Cuenta> cuentaDestinoComboBox;
    private final JComboBox<Categoria> categoriaComboBox;
    private final JTextField importeField;
    private final DatePicker fechaField;
    private final JTextField descripcionField;
    private final JButton registrarButton;

    /** Constructor del shell sin contexto de usuario. */
    public TransferenciasPanel() {
        operacionFinancieraService = null;
        cuentaService = null;
        categoriaService = null;
        perfilFinancieroId = null;
        usuarioId = null;
        onTransferenciaRegistrada = null;
        cuentaOrigenComboBox = new JComboBox<>();
        cuentaDestinoComboBox = new JComboBox<>();
        categoriaComboBox = new JComboBox<>();
        importeField = new JTextField(16);
        fechaField = crearFechaPicker();
        descripcionField = new JTextField(16);
        registrarButton = new JButton("Registrar transferencia");
        construirFormulario();
        registrarButton.setEnabled(false);
    }

    public TransferenciasPanel(OperacionFinancieraService operacionFinancieraService,
                               CuentaService cuentaService,
                               CategoriaService categoriaService,
                               Long perfilFinancieroId,
                               Long usuarioId) {
        this(operacionFinancieraService, cuentaService, categoriaService,
                perfilFinancieroId, usuarioId, null);
    }

    public TransferenciasPanel(OperacionFinancieraService operacionFinancieraService,
                               CuentaService cuentaService,
                               CategoriaService categoriaService,
                               Long perfilFinancieroId,
                               Long usuarioId,
                               Runnable onTransferenciaRegistrada) {
        this.operacionFinancieraService = Objects.requireNonNull(
                operacionFinancieraService, "El OperacionFinancieraService es obligatorio");
        this.cuentaService = Objects.requireNonNull(cuentaService, "El CuentaService es obligatorio");
        this.categoriaService = Objects.requireNonNull(categoriaService, "El CategoriaService es obligatorio");
        this.perfilFinancieroId = Objects.requireNonNull(
                perfilFinancieroId, "El id del perfil financiero es obligatorio");
        this.usuarioId = Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");
        this.onTransferenciaRegistrada = onTransferenciaRegistrada;
        cuentaOrigenComboBox = new JComboBox<>();
        cuentaDestinoComboBox = new JComboBox<>();
        categoriaComboBox = new JComboBox<>();
        importeField = new JTextField(16);
        fechaField = crearFechaPicker();
        descripcionField = new JTextField(16);
        registrarButton = new JButton("Registrar transferencia");

        configurarRenderers();
        cargarCuentas();
        cargarCategorias();
        construirFormulario();
        registrarButton.addActionListener(evento -> registrar());
    }

    public JComboBox<Cuenta> getCuentaOrigenComboBox() {
        return cuentaOrigenComboBox;
    }

    public JComboBox<Cuenta> getCuentaDestinoComboBox() {
        return cuentaDestinoComboBox;
    }

    public JComboBox<Categoria> getCategoriaComboBox() {
        return categoriaComboBox;
    }

    public JTextField getImporteField() {
        return importeField;
    }

    public DatePicker getFechaField() {
        return fechaField;
    }

    public JTextField getDescripcionField() {
        return descripcionField;
    }

    public JButton getRegistrarButton() {
        return registrarButton;
    }

    private DatePicker crearFechaPicker() {
        DatePickerSettings dateSettings = new DatePickerSettings(new Locale("es", "AR"));
        dateSettings.setAllowEmptyDates(true);
        dateSettings.setFirstDayOfWeek(DayOfWeek.SUNDAY);
        dateSettings.setFormatForDatesCommonEra("dd/MM/uuuu");
        dateSettings.setFormatForDatesBeforeCommonEra("dd/MM/uuuu");
        DatePicker datePicker = new DatePicker(dateSettings);
        datePicker.setDate(LocalDate.now());
        return datePicker;
    }

    private void configurarRenderers() {
        javax.swing.DefaultListCellRenderer renderer = new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    javax.swing.JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value instanceof Cuenta cuenta ? cuenta.getNombre() : "Seleccione una cuenta");
                return this;
            }
        };
        cuentaOrigenComboBox.setRenderer(renderer);
        cuentaDestinoComboBox.setRenderer(renderer);
        categoriaComboBox.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    javax.swing.JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value instanceof Categoria categoria ? categoria.getNombre() : "Seleccione una categoría");
                return this;
            }
        });
    }

    private void cargarCuentas() {
        List<Cuenta> cuentas = cuentaService.listarPorPerfilFinanciero(perfilFinancieroId, usuarioId);
        for (Cuenta cuenta : cuentas) {
            if (cuenta.isActiva()) {
                cuentaOrigenComboBox.addItem(cuenta);
                cuentaDestinoComboBox.addItem(cuenta);
            }
        }
        cuentaOrigenComboBox.setSelectedItem(null);
        cuentaDestinoComboBox.setSelectedItem(null);
    }

    private void cargarCategorias() {
        List<Categoria> categorias = categoriaService.listarPorPerfilFinanciero(perfilFinancieroId, usuarioId);
        for (Categoria categoria : categorias) {
            if (categoria.isActiva()) {
                categoriaComboBox.addItem(categoria);
            }
        }
        categoriaComboBox.setSelectedItem(null);
    }

    private void construirFormulario() {
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel titulo = new JLabel("Transferencias");
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 4, 4, 4));
        add(titulo, BorderLayout.NORTH);

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Registrar transferencia",
                TitledBorder.LEFT, TitledBorder.TOP));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(6, 6, 6, 6);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.0;

        agregarCampo(panelFormulario, new JLabel("Cuenta origen"), cuentaOrigenComboBox, constraints, 0, 0);
        agregarCampo(panelFormulario, new JLabel("Cuenta destino"), cuentaDestinoComboBox, constraints, 2, 0);
        agregarCampo(panelFormulario, new JLabel("Categoría"), categoriaComboBox, constraints, 0, 1);
        agregarCampo(panelFormulario, new JLabel("Importe"), importeField, constraints, 2, 1);
        agregarCampo(panelFormulario, new JLabel("Fecha"), fechaField, constraints, 0, 2);
        agregarCampo(panelFormulario, new JLabel("Descripción"), descripcionField, constraints, 2, 2);

        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 4;
        constraints.weightx = 1.0;
        constraints.anchor = GridBagConstraints.EAST;
        panelFormulario.add(registrarButton, constraints);
        add(panelFormulario, BorderLayout.NORTH);
    }

    private void agregarCampo(JPanel panel, JLabel etiqueta, java.awt.Component campo,
                              GridBagConstraints constraints, int columna, int fila) {
        constraints.gridx = columna;
        constraints.gridy = fila;
        constraints.gridwidth = 1;
        constraints.weightx = 0.0;
        panel.add(etiqueta, constraints);
        constraints.gridx = columna + 1;
        constraints.weightx = 1.0;
        panel.add(campo, constraints);
    }

    private void registrar() {
        try {
            registrarTransferencia();
            JOptionPane.showMessageDialog(this, "Transferencia registrada correctamente",
                    "Transferencias", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "No se pudo registrar la transferencia", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Ejecuta el alta sin diálogos, para permitir su prueba desde la UI. */
    void registrarTransferencia() {
        Cuenta cuentaOrigen = (Cuenta) Objects.requireNonNull(
                cuentaOrigenComboBox.getSelectedItem(), "La cuenta de origen es obligatoria");
        Cuenta cuentaDestino = (Cuenta) Objects.requireNonNull(
                cuentaDestinoComboBox.getSelectedItem(), "La cuenta de destino es obligatoria");
        Categoria categoria = (Categoria) Objects.requireNonNull(
                categoriaComboBox.getSelectedItem(), "La categoría es obligatoria");
        java.math.BigDecimal importe = new java.math.BigDecimal(importeField.getText().trim());
        LocalDate fecha = Objects.requireNonNull(fechaField.getDate(), "La fecha es obligatoria");
        LocalDateTime fechaHora = LocalDateTime.of(fecha, LocalTime.now());
        String descripcion = descripcionField.getText().trim();

        operacionFinancieraService.transferir(
                usuarioId,
                cuentaOrigen,
                cuentaDestino,
                categoria,
                categoria,
                importe,
                fechaHora,
                descripcion
        );

        if (onTransferenciaRegistrada != null) {
            onTransferenciaRegistrada.run();
        }
    }

    private void limpiarFormulario() {
        importeField.setText("");
        fechaField.setDate(LocalDate.now());
        descripcionField.setText("");
        cuentaOrigenComboBox.setSelectedItem(null);
        cuentaDestinoComboBox.setSelectedItem(null);
        categoriaComboBox.setSelectedItem(null);
    }
}
