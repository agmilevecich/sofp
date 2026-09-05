package ar.com.agmilevecich.sofp.ui;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.event.ActionListener;

/** Navegación lateral del shell principal. */
public class SidebarPanel extends JPanel {

    public SidebarPanel(ActionListener navigationListener) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setPreferredSize(new Dimension(190, 0));

        add(crearBoton("Inicio", "inicio", navigationListener));
        add(crearBoton("Cuentas", "cuentas", navigationListener));
        add(crearBoton("Categorías", "categorias", navigationListener));
        add(crearBoton("Gastos", "gastos", navigationListener));
        add(crearBoton("Movimientos", "movimientos", navigationListener));
        add(crearBoton("Inversiones", "inversiones", navigationListener));
        add(crearBoton("Reportes", "reportes", navigationListener));
    }

    private JButton crearBoton(String texto, String comando, ActionListener listener) {
        JButton boton = new JButton(texto);
        boton.setActionCommand(comando);
        boton.addActionListener(listener);
        return boton;
    }
}
