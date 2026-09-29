package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.service.UsuarioService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Objects;
import java.util.function.Consumer;

public class RegistroUsuarioPanel extends JPanel {

    private final UsuarioService usuarioService;
    private final Consumer<Usuario> onRegistrado;
    private final JTextField nombreField;
    private final JTextField apellidoField;
    private final JTextField emailField;
    private final JPasswordField passwordField;
    private final JTextField perfilField;
    private final JButton registrarButton;

    public RegistroUsuarioPanel(
            UsuarioService usuarioService,
            Consumer<Usuario> onRegistrado) {

        this.usuarioService = Objects.requireNonNull(
                usuarioService,
                "El servicio de usuarios es obligatorio"
        );
        this.onRegistrado = onRegistrado;

        nombreField = new JTextField(24);
        apellidoField = new JTextField(24);
        emailField = new JTextField(24);
        passwordField = new JPasswordField(24);
        perfilField = new JTextField("Perfil principal", 24);
        registrarButton = new JButton("Registrar");

        registrarButton.addActionListener(event -> registrarConDialogo());

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(6, 6, 6, 6);
        constraints.fill = GridBagConstraints.HORIZONTAL;

        agregar(new JLabel("Nombre:"), nombreField, constraints, 0);
        agregar(new JLabel("Apellido:"), apellidoField, constraints, 1);
        agregar(new JLabel("Email:"), emailField, constraints, 2);
        agregar(new JLabel("Contraseña:"), passwordField, constraints, 3);
        agregar(new JLabel("Perfil financiero:"), perfilField, constraints, 4);

        constraints.gridx = 1;
        constraints.gridy = 5;
        add(registrarButton, constraints);
    }

    private void agregar(
            JLabel label,
            JTextField field,
            GridBagConstraints constraints,
            int row) {

        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.weightx = 0;
        add(label, constraints);

        constraints.gridx = 1;
        constraints.weightx = 1;
        add(field, constraints);
    }

    void registrar() {
        Usuario usuario = usuarioService.registrar(
                nombreField.getText(),
                apellidoField.getText(),
                emailField.getText(),
                new String(passwordField.getPassword()),
                perfilField.getText()
        );

        if (onRegistrado != null) {
            onRegistrado.accept(usuario);
        }
    }

    private void registrarConDialogo() {
        try {
            registrar();
            JOptionPane.showMessageDialog(
                    this,
                    "Usuario registrado correctamente.",
                    "SOFP",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (IllegalArgumentException | IllegalStateException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    exception.getMessage(),
                    "SOFP",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public JTextField getNombreField() {
        return nombreField;
    }

    public JTextField getApellidoField() {
        return apellidoField;
    }

    public JTextField getEmailField() {
        return emailField;
    }

    public JPasswordField getPasswordField() {
        return passwordField;
    }

    public JTextField getPerfilField() {
        return perfilField;
    }

    public JButton getRegistrarButton() {
        return registrarButton;
    }
}
