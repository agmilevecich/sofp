package ar.com.agmilevecich.sofp.ui;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;

/** Ventana principal y shell de navegación de SOFP. */
public class MainFrame extends JFrame {

    private static final String INICIO = "inicio";
    private static final String CUENTAS = "cuentas";
    private static final String MOVIMIENTOS = "movimientos";
    private static final String INVERSIONES = "inversiones";

    private final CardLayout cardLayout;
    private final JPanel areaCentral;

    public MainFrame() {
        super("SOFP - Sistema Operativo Financiero Personal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        areaCentral = new JPanel(cardLayout);
        areaCentral.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        areaCentral.add(new InicioPanel(), INICIO);
        areaCentral.add(new CuentasPanel(), CUENTAS);
        areaCentral.add(new MovimientosPanel(), MOVIMIENTOS);
        areaCentral.add(new InversionesPanel(), INVERSIONES);

        JPanel content = new JPanel(new BorderLayout());
        content.add(new HeaderPanel(), BorderLayout.NORTH);
        content.add(new SidebarPanel(this::navegar), BorderLayout.WEST);
        content.add(areaCentral, BorderLayout.CENTER);
        content.add(new StatusBarPanel(), BorderLayout.SOUTH);
        setContentPane(content);
    }

    private void navegar(ActionEvent evento) {
        cardLayout.show(areaCentral, evento.getActionCommand());
    }
}
