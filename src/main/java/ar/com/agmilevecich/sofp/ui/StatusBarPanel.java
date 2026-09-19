package ar.com.agmilevecich.sofp.ui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;

/** Barra de estado común del shell principal. */
public class StatusBarPanel extends JPanel {

    public StatusBarPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        add(new JLabel("Listo"), BorderLayout.WEST);
    }
}
