package ar.com.agmilevecich.sofp.ui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;

/** Cabecera común del shell principal. */
public class HeaderPanel extends JPanel {

    public HeaderPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        add(new JLabel("SOFP"), BorderLayout.WEST);
    }
}
