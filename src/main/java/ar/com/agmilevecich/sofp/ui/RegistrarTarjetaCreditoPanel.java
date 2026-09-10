package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.service.CuentaService;
import ar.com.agmilevecich.sofp.service.InstitucionFinancieraService;
import ar.com.agmilevecich.sofp.service.MonedaService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/** Formulario Swing para registrar una tarjeta de crédito. */
public class RegistrarTarjetaCreditoPanel extends JPanel {

    private final CuentaService cuentaService;
    private final PerfilFinanciero perfilFinanciero;
    private final Long usuarioId;
    private final Runnable onTarjetaRegistrada;
    private final JTextField nombreField;
    private final JComboBox<InstitucionFinanciera> institucionComboBox;
    private final JComboBox<Moneda> monedaComboBox;
    private final JTextField limiteCreditoField;
    private final JTextField diaCierreField;
    private final JTextField diaVencimientoField;
    private final JTextField identificadorExternoField;
    private final JButton registrarButton;

    /** Constructor del shell sin contexto de usuario. */
    public RegistrarTarjetaCreditoPanel() {
        cuentaService = null;
        perfilFinanciero = null;
        usuarioId = null;
        onTarjetaRegistrada = null;
        nombreField = new JTextField(16);
        institucionComboBox = new ComboBoxConSeleccione<>();
        monedaComboBox = new ComboBoxConSeleccione<>();
        limiteCreditoField = new JTextField(16);
        diaCierreField = new JTextField(16);
        diaVencimientoField = new JTextField(16);
        identificadorExternoField = new JTextField(16);
        registrarButton = new JButton("Registrar");
        construirFormulario();
        registrarButton.setEnabled(false);
    }

    /** Constructor para registrar tarjetas del usuario autenticado. */
    public RegistrarTarjetaCreditoPanel(CuentaService cuentaService,
                                       InstitucionFinancieraService institucionFinancieraService,
                                       MonedaService monedaService,
                                       PerfilFinanciero perfilFinanciero,
                                       Long usuarioId) {
        this(cuentaService, institucionFinancieraService, monedaService,
                perfilFinanciero, usuarioId, null);
    }

    /** Constructor para registrar tarjetas y notificar a la pantalla contenedora. */
    public RegistrarTarjetaCreditoPanel(CuentaService cuentaService,
                                       InstitucionFinancieraService institucionFinancieraService,
                                       MonedaService monedaService,
                                       PerfilFinanciero perfilFinanciero,
                                       Long usuarioId,
                                       Runnable onTarjetaRegistrada) {
        this.cuentaService = Objects.requireNonNull(cuentaService, "El CuentaService es obligatorio");
        Objects.requireNonNull(institucionFinancieraService,
                "El InstitucionFinancieraService es obligatorio");
        Objects.requireNonNull(monedaService, "El MonedaService es obligatorio");
        this.perfilFinanciero = Objects.requireNonNull(perfilFinanciero,
                "El perfil financiero es obligatorio");
        this.usuarioId = Objects.requireNonNull(usuarioId,
                "El id del usuario es obligatorio");
        this.onTarjetaRegistrada = onTarjetaRegistrada;
        nombreField = new JTextField(16);
        institucionComboBox = new ComboBoxConSeleccione<>();
        monedaComboBox = new ComboBoxConSeleccione<>();
        limiteCreditoField = new JTextField(16);
        diaCierreField = new JTextField(16);
        diaVencimientoField = new JTextField(16);
        identificadorExternoField = new JTextField(16);
        registrarButton = new JButton("Registrar");

        cargarInstituciones(institucionFinancieraService.listarTodas());
        cargarMonedas(monedaService.listarTodas());
        construirFormulario();
        actualizarEstadoBoton();
        registrarButton.addActionListener(evento -> registrar());
        DocumentListener listener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent evento) {
                actualizarEstadoBoton();
            }

            @Override
            public void removeUpdate(DocumentEvent evento) {
                actualizarEstadoBoton();
            }

            @Override
            public void changedUpdate(DocumentEvent evento) {
                actualizarEstadoBoton();
            }
        };
        nombreField.getDocument().addDocumentListener(listener);
        limiteCreditoField.getDocument().addDocumentListener(listener);
        diaCierreField.getDocument().addDocumentListener(listener);
        diaVencimientoField.getDocument().addDocumentListener(listener);
        institucionComboBox.addActionListener(evento -> actualizarEstadoBoton());
        monedaComboBox.addActionListener(evento -> actualizarEstadoBoton());
    }

    public JTextField getNombreField() {
        return nombreField;
    }

    public JComboBox<InstitucionFinanciera> getInstitucionComboBox() {
        return institucionComboBox;
    }

    public JComboBox<Moneda> getMonedaComboBox() {
        return monedaComboBox;
    }

    public JTextField getLimiteCreditoField() {
        return limiteCreditoField;
    }

    public JTextField getDiaCierreField() {
        return diaCierreField;
    }

    public JTextField getDiaVencimientoField() {
        return diaVencimientoField;
    }

    public JTextField getIdentificadorExternoField() {
        return identificadorExternoField;
    }

    public JButton getRegistrarButton() {
        return registrarButton;
    }

    private void construirFormulario() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(4, 4, 4, 4);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        agregarCampo(new JLabel("Nombre"), nombreField, constraints, 0);
        agregarCampo(new JLabel("Institución financiera"), institucionComboBox, constraints, 1);
        agregarCampo(new JLabel("Moneda"), monedaComboBox, constraints, 2);
        agregarCampo(new JLabel("Límite de crédito"), limiteCreditoField, constraints, 3);
        agregarCampo(new JLabel("Día de cierre"), diaCierreField, constraints, 4);
        agregarCampo(new JLabel("Día de vencimiento"), diaVencimientoField, constraints, 5);
        agregarCampo(new JLabel("Identificador externo"), identificadorExternoField, constraints, 6);

        constraints.gridx = 1;
        constraints.gridy = 7;
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

    private void cargarInstituciones(List<InstitucionFinanciera> instituciones) {
        for (InstitucionFinanciera institucion : instituciones) {
            if (institucion.isActiva()) {
                institucionComboBox.addItem(institucion);
            }
        }
    }

    private void cargarMonedas(List<Moneda> monedas) {
        for (Moneda moneda : monedas) {
            monedaComboBox.addItem(moneda);
        }
    }

    private void actualizarEstadoBoton() {
        registrarButton.setEnabled(
                !nombreField.getText().trim().isEmpty()
                        && !limiteCreditoField.getText().trim().isEmpty()
                        && !diaCierreField.getText().trim().isEmpty()
                        && !diaVencimientoField.getText().trim().isEmpty()
        );
    }

    private void registrar() {
        if (institucionComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Olvidaste seleccionar una institución financiera",
                    "Selección requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (monedaComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Olvidaste seleccionar una moneda",
                    "Selección requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            registrarTarjetaCredito();
            JOptionPane.showMessageDialog(this, "Tarjeta de crédito registrada correctamente",
                    "Tarjeta de crédito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "No se pudo registrar la tarjeta de crédito", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Ejecuta el alta sin abrir diálogos, para permitir su prueba desde la UI. */
    void registrarTarjetaCredito() {
        String nombre = Objects.requireNonNull(nombreField.getText().trim(), "El nombre es obligatorio");
        InstitucionFinanciera institucion = (InstitucionFinanciera) Objects.requireNonNull(
                institucionComboBox.getSelectedItem(), "La institución financiera es obligatoria");
        Moneda moneda = (Moneda) Objects.requireNonNull(
                monedaComboBox.getSelectedItem(), "La moneda es obligatoria");
        BigDecimal limiteCredito = convertirDecimal(limiteCreditoField.getText(), "El límite de crédito");
        Integer diaCierre = convertirEntero(diaCierreField.getText(), "El día de cierre");
        Integer diaVencimiento = convertirEntero(diaVencimientoField.getText(), "El día de vencimiento");
        String identificadorExterno = identificadorExternoField.getText().trim();

        Cuenta tarjeta = new Cuenta(
                nombre,
                perfilFinanciero,
                institucion,
                moneda,
                limiteCredito,
                diaCierre,
                diaVencimiento
        );
        tarjeta.cambiarIdentificadorExterno(
                identificadorExterno.isEmpty() ? null : identificadorExterno
        );

        cuentaService.registrar(tarjeta, usuarioId);

        if (onTarjetaRegistrada != null) {
            onTarjetaRegistrada.run();
        }
    }

    private BigDecimal convertirDecimal(String valor, String campo) {
        try {
            return new BigDecimal(valor.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(campo + " debe ser un número válido", e);
        }
    }

    private Integer convertirEntero(String valor, String campo) {
        try {
            return Integer.valueOf(valor.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(campo + " debe ser un número entero válido", e);
        }
    }

    private void limpiarFormulario() {
        nombreField.setText("");
        limiteCreditoField.setText("");
        diaCierreField.setText("");
        diaVencimientoField.setText("");
        identificadorExternoField.setText("");
        institucionComboBox.setSelectedIndex(institucionComboBox.getItemCount() > 0 ? 0 : -1);
        monedaComboBox.setSelectedIndex(monedaComboBox.getItemCount() > 0 ? 0 : -1);
    }
}
