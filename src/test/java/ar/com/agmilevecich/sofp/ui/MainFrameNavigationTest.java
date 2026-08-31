package ar.com.agmilevecich.sofp.ui;

import org.junit.jupiter.api.Test;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MainFrameNavigationTest {

    @Test
    void debeNavegarEntreLosModulosPrincipales() throws Exception {
        AtomicReference<MainFrame> frame = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> frame.set(new MainFrame()));

        MainFrame mainFrame = frame.get();
        SidebarPanel sidebar = buscarSidebar(mainFrame.getContentPane());
        JButton cuentas = buscarBoton(sidebar, "Cuentas");
        JButton movimientos = buscarBoton(sidebar, "Movimientos");
        JButton inversiones = buscarBoton(sidebar, "Inversiones");

        SwingUtilities.invokeAndWait(cuentas::doClick);
        assertEquals("Cuentas", etiquetaVisible(mainFrame));

        SwingUtilities.invokeAndWait(movimientos::doClick);
        assertEquals("Movimientos", etiquetaVisible(mainFrame));

        SwingUtilities.invokeAndWait(inversiones::doClick);
        assertEquals("Inversiones", etiquetaVisible(mainFrame));

        mainFrame.dispose();
    }

    private SidebarPanel buscarSidebar(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof SidebarPanel sidebar) {
                return sidebar;
            }
            if (component instanceof Container hijo) {
                SidebarPanel encontrado = buscarSidebar(hijo);
                if (encontrado != null) {
                    return encontrado;
                }
            }
        }
        throw new AssertionError("No se encontró el panel de navegación lateral");
    }

    private JButton buscarBoton(Container container, String texto) {
        for (Component component : container.getComponents()) {
            if (component instanceof JButton boton && texto.equals(boton.getText())) {
                return boton;
            }
            if (component instanceof Container hijo) {
                JButton encontrado = buscarBoton(hijo, texto);
                if (encontrado != null) {
                    return encontrado;
                }
            }
        }
        throw new AssertionError("No se encontró el botón: " + texto);
    }

    private String etiquetaVisible(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof JLabel etiqueta && etiqueta.isVisible()) {
                String texto = etiqueta.getText();
                if ("Cuentas".equals(texto) || "Movimientos".equals(texto) || "Inversiones".equals(texto)) {
                    return texto;
                }
            }
            if (component instanceof Container hijo) {
                String encontrado = etiquetaVisible(hijo);
                if (encontrado != null) {
                    return encontrado;
                }
            }
        }
        return null;
    }
}
