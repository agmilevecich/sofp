package ar.com.agmilevecich.sofp.ui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;

/** Panel inicial del sistema. */
public class InicioPanel extends JPanel {

    public InicioPanel() {
        setLayout(new BorderLayout());
        add(new JLabel("Inicio", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
