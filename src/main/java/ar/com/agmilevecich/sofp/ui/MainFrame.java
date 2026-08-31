package ar.com.agmilevecich.sofp.ui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;

/**
 * Ventana principal de SOFP.
 *
 * <p>El shell inicial mantiene la navegación y los módulos desacoplados.
 * La lógica de negocio permanece en la capa de servicios.</p>
 */
public class MainFrame extends JFrame {

    public MainFrame() {
        super("SOFP - Sistema Operativo Financiero Personal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        JPanel content = new JPanel(new BorderLayout());
        content.add(new JLabel("SOFP", SwingConstants.CENTER), BorderLayout.CENTER);
        setContentPane(content);
    }
}
