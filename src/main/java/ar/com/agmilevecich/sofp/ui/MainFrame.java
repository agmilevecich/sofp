package ar.com.agmilevecich.sofp.ui;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;

/** Ventana principal y shell de navegación de SOFP. */
public class MainFrame extends JFrame {

    public MainFrame() {
        super("SOFP - Sistema Operativo Financiero Personal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        JPanel content = new JPanel(new BorderLayout());
        content.add(new HeaderPanel(), BorderLayout.NORTH);
        content.add(new SidebarPanel(), BorderLayout.WEST);
        content.add(crearAreaCentral(), BorderLayout.CENTER);
        content.add(new StatusBarPanel(), BorderLayout.SOUTH);
        setContentPane(content);
    }

    private JPanel crearAreaCentral() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        panel.add(new JLabel("Inicio", SwingConstants.CENTER), BorderLayout.CENTER);
        return panel;
    }
}
