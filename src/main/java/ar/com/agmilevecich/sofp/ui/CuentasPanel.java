package ar.com.agmilevecich.sofp.ui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;

/** Panel inicial del módulo de cuentas. */
public class CuentasPanel extends JPanel {

    public CuentasPanel() {
        setLayout(new BorderLayout());
        add(new JLabel("Cuentas", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
