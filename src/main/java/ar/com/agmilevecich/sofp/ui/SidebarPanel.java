package ar.com.agmilevecich.sofp.ui;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Dimension;

/** Navegación lateral del shell principal. */
public class SidebarPanel extends JPanel {

    public SidebarPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setPreferredSize(new Dimension(190, 0));

        add(new JButton("Inicio"));
        add(new JButton("Cuentas"));
        add(new JButton("Movimientos"));
        add(new JButton("Inversiones"));
        add(new JButton("Reportes"));
    }
}
