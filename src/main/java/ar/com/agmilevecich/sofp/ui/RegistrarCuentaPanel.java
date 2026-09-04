package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.Moneda;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.TipoCuenta;
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
import java.util.List;
import java.util.Objects;

/** Formulario Swing para registrar una cuenta. */
public class RegistrarCuentaPanel extends JPanel {

    private final CuentaService cuentaService;
    private final PerfilFinanciero perfilFinanciero;
    private final Long usuarioId;
    private final Runnable onCuentaRegistrada;
    private final JTextField nombreField;
    private final JComboBox<TipoCuenta> tipoCuentaComboBox;
    private final JComboBox<InstitucionFinanciera> institucionComboBox;
    private final JComboBox<Moneda> monedaComboBox;
    private final JTextField identificadorExternoField;
    private final JButton registrarButton;

    /** Constructor del shell sin contexto de usuario. */
    public RegistrarCuentaPanel() {
        cuentaService = null;
        perfilFinanciero = null;
        usuarioId = null;
        onCuentaRegistrada = null;
        nombreField = new JTextField(16);
        tipoCuentaComboBox = new ComboBoxConSeleccione<>();
        institucionComboBox = new ComboBoxConSeleccione<>();
        monedaComboBox = new ComboBoxConSeleccione<>();
        agregarTiposCuenta();
        identificadorExternoField = new JTextField(16);
        registrarButton = new JButton("Registrar");
        construirFormulario();
        registrarButton.setEnabled(false);
    }

    /** Constructor para registrar cuentas del usuario autenticado. */
    public RegistrarCuentaPanel(CuentaService cuentaService,
                                InstitucionFinancieraService institucionFinancieraService,
                                MonedaService monedaService,
                                PerfilFinanciero perfilFinanciero,
                                Long usuarioId) {
        this(cuentaService, institucionFinancieraService, monedaService,
                perfilFinanciero, usuarioId, null);
    }

    /** Constructor para registrar cuentas y notificar a la pantalla contenedora. */
    public RegistrarCuentaPanel(CuentaService cuentaService,
                                InstitucionFinancieraService institucionFinancieraService,
                                MonedaService monedaService,
                                PerfilFinanciero perfilFinanciero,
                                Long usuarioId,
                                Runnable onCuentaRegistrada) {
        this.cuentaService = Objects.requireNonNull(
                cuentaService,
                "El CuentaService es obligatorio"
        );
        Objects.requireNonNull(
                institucionFinancieraService,
                "El InstitucionFinancieraService es obligatorio"
        );
        Objects.requireNonNull(
                monedaService,
                "El MonedaService es obligatorio"
        );
        this.perfilFinanciero = Objects.requireNonNull(
                perfilFinanciero,
                "El perfil financiero es obligatorio"
        );
        this.usuarioId = Objects.requireNonNull(
                usuarioId,
                "El id del usuario es obligatorio"
        );
        this.onCuentaRegistrada = onCuentaRegistrada;
        nombreField = new JTextField(16);
        tipoCuentaComboBox = new ComboBoxConSeleccione<>();
        institucionComboBox = new ComboBoxConSeleccione<>();
        monedaComboBox = new ComboBoxConSeleccione<>();
        agregarTiposCuenta();
        identificadorExternoField = new JTextField(16);
        registrarButton = new JButton("Registrar");

        cargarInstituciones(institucionFinancieraService.listarTodas());
        cargarMonedas(monedaService.listarTodas());
        construirFormulario();
        actualizarEstadoBoton();
        registrarButton.addActionListener(evento -> registrar());
        nombreField.getDocument().addDocumentListener(new DocumentListener() {
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
        });
        institucionComboBox.addActionListener(evento -> actualizarEstadoBoton());
        monedaComboBox.addActionListener(evento -> actualizarEstadoBoton());
    }

    public JTextField getNombreField() {
        return nombreField;
    }

    public JComboBox<TipoCuenta> getTipoCuentaComboBox() {
        return tipoCuentaComboBox;
    }

    public JComboBox<InstitucionFinanciera> getInstitucionComboBox() {
        return institucionComboBox;
    }

    public JComboBox<Moneda> getMonedaComboBox() {
        return monedaComboBox;
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
        agregarCampo(new JLabel("Tipo de cuenta"), tipoCuentaComboBox, constraints, 1);
        agregarCampo(new JLabel("Institución financiera"), institucionComboBox, constraints, 2);
        agregarCampo(new JLabel("Moneda"), monedaComboBox, constraints, 3);
        agregarCampo(new JLabel("Identificador externo"), identificadorExternoField, constraints, 4);

        constraints.gridx = 1;
        constraints.gridy = 5;
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

    private void agregarTiposCuenta() {
        for (TipoCuenta tipoCuenta : TipoCuenta.values()) {
            tipoCuentaComboBox.addItem(tipoCuenta);
        }
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
        registrarButton.setEnabled(!nombreField.getText().trim().isEmpty());
    }

    private void registrar() {
        if (tipoCuentaComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Olvidaste seleccionar el tipo de cuenta",
                    "Selección requerida",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        if (institucionComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Olvidaste seleccionar una institución financiera",
                    "Selección requerida",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        if (monedaComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Olvidaste seleccionar una moneda",
                    "Selección requerida",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            registrarCuenta();
            JOptionPane.showMessageDialog(
                    this,
                    "Cuenta registrada correctamente",
                    "Cuenta",
                    JOptionPane.INFORMATION_MESSAGE
            );
            limpiarFormulario();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "No se pudo registrar la cuenta",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /** Ejecuta el alta sin abrir diálogos, para permitir su prueba desde la UI. */
    void registrarCuenta() {
        String nombre = nombreField.getText().trim();
        TipoCuenta tipoCuenta = (TipoCuenta) Objects.requireNonNull(
                tipoCuentaComboBox.getSelectedItem(),
                "El tipo de cuenta es obligatorio"
        );
        InstitucionFinanciera institucion = (InstitucionFinanciera) Objects.requireNonNull(
                institucionComboBox.getSelectedItem(),
                "La institución financiera es obligatoria"
        );
        Moneda moneda = (Moneda) Objects.requireNonNull(
                monedaComboBox.getSelectedItem(),
                "La moneda es obligatoria"
        );
        String identificadorExterno = identificadorExternoField.getText().trim();

        Cuenta cuenta = new Cuenta(
                nombre,
                tipoCuenta,
                perfilFinanciero,
                institucion,
                moneda
        );
        cuenta.cambiarIdentificadorExterno(
                identificadorExterno.isEmpty() ? null : identificadorExterno
        );

        cuentaService.registrar(cuenta, usuarioId);

        if (onCuentaRegistrada != null) {
            onCuentaRegistrada.run();
        }
    }

    private void limpiarFormulario() {
        nombreField.setText("");
        identificadorExternoField.setText("");
        tipoCuentaComboBox.setSelectedIndex(0);
        institucionComboBox.setSelectedIndex(institucionComboBox.getItemCount() > 0 ? 0 : -1);
        monedaComboBox.setSelectedIndex(monedaComboBox.getItemCount() > 0 ? 0 : -1);
    }
}
