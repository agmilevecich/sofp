package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.service.CategoriaService;
import ar.com.agmilevecich.sofp.service.CuentaService;
import ar.com.agmilevecich.sofp.service.GastoService;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
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

/** Formulario especializado para registrar gastos como movimientos de egreso. */
public class GastosPanel extends JPanel {

    private final GastoService gastoService;
    private final CuentaService cuentaService;
    private final CategoriaService categoriaService;
    private final Long perfilFinancieroId;
    private final Long usuarioId;
    private final Runnable onGastoRegistrado;
    private final JComboBox<Cuenta> cuentaComboBox;
    private final JComboBox<Categoria> categoriaComboBox;
    private final JTextField importeField;
    private final DatePicker fechaField;
    private final JTextField descripcionField;
    private final JButton registrarButton;

    /** Constructor del shell sin contexto de usuario. */
    public GastosPanel() {
        gastoService = null;
        cuentaService = null;
        categoriaService = null;
        perfilFinancieroId = null;
        usuarioId = null;
        onGastoRegistrado = null;
        cuentaComboBox = new JComboBox<>();
        categoriaComboBox = new JComboBox<>();
        importeField = new JTextField(16);
        fechaField = crearFechaPicker();
        descripcionField = new JTextField(16);
        registrarButton = new JButton("Registrar gasto");
        construirFormulario();
        registrarButton.setEnabled(false);
    }

    public GastosPanel(GastoService gastoService,
                       CuentaService cuentaService,
                       CategoriaService categoriaService,
                       Long perfilFinancieroId,
                       Long usuarioId) {
        this(gastoService, cuentaService, categoriaService,
                perfilFinancieroId, usuarioId, null);
    }

    public GastosPanel(GastoService gastoService,
                       CuentaService cuentaService,
                       CategoriaService categoriaService,
                       Long perfilFinancieroId,
                       Long usuarioId,
                       Runnable onGastoRegistrado) {
        this.gastoService = Objects.requireNonNull(gastoService, "El GastoService es obligatorio");
        this.cuentaService = Objects.requireNonNull(cuentaService, "El CuentaService es obligatorio");
        this.categoriaService = Objects.requireNonNull(categoriaService, "El CategoriaService es obligatorio");
        this.perfilFinancieroId = Objects.requireNonNull(
                perfilFinancieroId,
                "El id del perfil financiero es obligatorio"
        );
        this.usuarioId = Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");
        this.onGastoRegistrado = onGastoRegistrado;

        cuentaComboBox = new JComboBox<>();
        categoriaComboBox = new JComboBox<>();
        importeField = new JTextField(16);
        fechaField = crearFechaPicker();
        descripcionField = new JTextField(16);
        registrarButton = new JButton("Registrar gasto");

        configurarRenderers();
        cargarCuentas();
        cargarCategorias();
        construirFormulario();
        registrarButton.addActionListener(evento -> registrar());
    }

    public JComboBox<Cuenta> getCuentaComboBox() {
        return cuentaComboBox;
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
        cuentaComboBox.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    javax.swing.JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value instanceof Cuenta cuenta ? cuenta.getNombre() : "Seleccione una cuenta");
                return this;
            }
        });
        categoriaComboBox.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    javax.swing.JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {
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
                cuentaComboBox.addItem(cuenta);
            }
        }
        cuentaComboBox.setSelectedItem(null);
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
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(4, 4, 4, 4);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        add(new JLabel("Gastos"), constraints);
        constraints.gridwidth = 1;

        agregarCampo(new JLabel("Cuenta"), cuentaComboBox, constraints, 1);
        agregarCampo(new JLabel("Categoría"), categoriaComboBox, constraints, 2);
        agregarCampo(new JLabel("Importe"), importeField, constraints, 3);
        agregarCampo(new JLabel("Fecha"), fechaField, constraints, 4);
        agregarCampo(new JLabel("Descripción"), descripcionField, constraints, 5);

        constraints.gridx = 1;
        constraints.gridy = 6;
        add(registrarButton, constraints);
    }

    private void agregarCampo(JLabel etiqueta,
                              java.awt.Component campo,
                              GridBagConstraints constraints,
                              int fila) {
        constraints.gridx = 0;
        constraints.gridy = fila;
        add(etiqueta, constraints);
        constraints.gridx = 1;
        add(campo, constraints);
    }

    private void registrar() {
        try {
            registrarGasto();
            JOptionPane.showMessageDialog(
                    this,
                    "Gasto registrado correctamente",
                    "Gastos",
                    JOptionPane.INFORMATION_MESSAGE
            );
            limpiarFormulario();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "No se pudo registrar el gasto",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /** Ejecuta el alta sin diálogos, para permitir su prueba desde la UI. */
    void registrarGasto() {
        Cuenta cuenta = (Cuenta) Objects.requireNonNull(
                cuentaComboBox.getSelectedItem(),
                "La cuenta es obligatoria"
        );
        Categoria categoria = (Categoria) Objects.requireNonNull(
                categoriaComboBox.getSelectedItem(),
                "La categoría es obligatoria"
        );
        java.math.BigDecimal importe = new java.math.BigDecimal(importeField.getText().trim());
        LocalDate fecha = Objects.requireNonNull(
                fechaField.getDate(),
                "La fecha es obligatoria"
        );
        LocalDateTime fechaHora = LocalDateTime.of(fecha, LocalTime.now());
        String descripcion = descripcionField.getText().trim();

        gastoService.registrar(cuenta, categoria, importe, fechaHora, descripcion, usuarioId);

        if (onGastoRegistrado != null) {
            onGastoRegistrado.run();
        }
    }

    private void limpiarFormulario() {
        importeField.setText("");
        fechaField.setDate(LocalDate.now());
        descripcionField.setText("");
        cuentaComboBox.setSelectedItem(null);
        categoriaComboBox.setSelectedItem(null);
    }
}
