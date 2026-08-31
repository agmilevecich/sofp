package ar.com.agmilevecich.sofp.ui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;

/** Panel inicial del módulo de movimientos. */
public class MovimientosPanel extends JPanel {

    public MovimientosPanel() {
        setLayout(new BorderLayout());
        add(new JLabel("Movimientos", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
