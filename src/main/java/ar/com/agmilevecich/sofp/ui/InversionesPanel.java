package ar.com.agmilevecich.sofp.ui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;

/** Panel inicial del módulo de inversiones. */
public class InversionesPanel extends JPanel {

    public InversionesPanel() {
        setLayout(new BorderLayout());
        add(new JLabel("Inversiones", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
