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
import java.util.Optional;
import java.util.function.Consumer;

/** Panel de autenticación de usuarios de SOFP. */
public class LoginPanel extends JPanel {

    private final UsuarioService usuarioService;
    private final Consumer<Usuario> onAutenticado;
    private final JTextField emailField;
    private final JPasswordField passwordField;
    private final JButton ingresarButton;

    public LoginPanel() {
        this(null, null);
    }

    public LoginPanel(UsuarioService usuarioService, Consumer<Usuario> onAutenticado) {
        this.usuarioService = usuarioService;
        this.onAutenticado = onAutenticado;

        emailField = new JTextField(24);
        passwordField = new JPasswordField(24);
        ingresarButton = new JButton("Ingresar");
        ingresarButton.setEnabled(usuarioService != null);
        ingresarButton.addActionListener(event -> autenticarConDialogo());

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(6, 6, 6, 6);
        constraints.fill = GridBagConstraints.HORIZONTAL;

        agregar(new JLabel("Email:"), emailField, constraints, 0);
        agregar(new JLabel("Contraseña:"), passwordField, constraints, 1);

        constraints.gridx = 1;
        constraints.gridy = 2;
        add(ingresarButton, constraints);
    }

    private void agregar(JLabel label,
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

    void autenticar() {
        Objects.requireNonNull(usuarioService, "El servicio de usuarios es obligatorio");

        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        Optional<Usuario> usuario = usuarioService.autenticar(email, password);
        if (usuario.isEmpty()) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        if (onAutenticado != null) {
            onAutenticado.accept(usuario.get());
        }
    }

    private void autenticarConDialogo() {
        try {
            autenticar();
            JOptionPane.showMessageDialog(
                    this,
                    "Autenticación correcta",
                    "SOFP",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    exception.getMessage(),
                    "SOFP",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public JTextField getEmailField() {
        return emailField;
    }

    public JPasswordField getPasswordField() {
        return passwordField;
    }

    public JButton getIngresarButton() {
        return ingresarButton;
    }
}
