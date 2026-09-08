package ar.com.agmilevecich.sofp.ui;

import org.junit.jupiter.api.Test;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MainFrameNavigationTest {

    @Test
    void debeNavegarEntreLosModulosPrincipales() throws Exception {
        MainFrame mainFrame = new MainFrame();

        SwingUtilities.invokeAndWait(() -> {
            SidebarPanel sidebar = buscarSidebar(mainFrame.getContentPane());
            if (sidebar == null) throw new AssertionError("No se encontró el panel de navegación lateral");

            navegarYVerificar(sidebar, mainFrame, "Cuentas");
            navegarYVerificar(sidebar, mainFrame, "Ingresos");
            navegarYVerificar(sidebar, mainFrame, "Gastos");
            navegarYVerificar(sidebar, mainFrame, "Movimientos");
            navegarYVerificar(sidebar, mainFrame, "Inversiones");
            navegarYVerificar(sidebar, mainFrame, "Obligaciones");
            navegarYVerificar(sidebar, mainFrame, "Transferencias");
        });
    }

    private void navegarYVerificar(SidebarPanel sidebar, MainFrame mainFrame, String texto) {
        JButton boton = buscarBoton(sidebar, texto);
        if (boton == null) throw new AssertionError("No se encontró el botón: " + texto);
        boton.doClick();
        assertEquals(texto, tarjetaVisible(mainFrame.getContentPane()));
    }

    private SidebarPanel buscarSidebar(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof SidebarPanel sidebar) return sidebar;
            if (component instanceof Container hijo) {
                SidebarPanel encontrado = buscarSidebar(hijo);
                if (encontrado != null) return encontrado;
            }
        }
        return null;
    }

    private JButton buscarBoton(Container container, String texto) {
        for (Component component : container.getComponents()) {
            if (component instanceof JButton boton && texto.equals(boton.getText())) return boton;
            if (component instanceof Container hijo) {
                JButton encontrado = buscarBoton(hijo, texto);
                if (encontrado != null) return encontrado;
            }
        }
        return null;
    }

    private String tarjetaVisible(Container container) {
        for (Component component : container.getComponents()) {
            if (!component.isVisible()) continue;
            if (component instanceof InicioPanel) return etiquetaDelPanel(component);
            if (component instanceof CuentasPanel) return etiquetaDelPanel(component);
            if (component instanceof IngresosPanel) return etiquetaDelPanel(component);
            if (component instanceof GastosPanel) return etiquetaDelPanel(component);
            if (component instanceof MovimientosPanel) return etiquetaDelPanel(component);
            if (component instanceof InversionesPanel) return etiquetaDelPanel(component);
            if (component instanceof ObligacionesPanel) return etiquetaDelPanel(component);
            if (component instanceof TransferenciasPanel) return etiquetaDelPanel(component);
            if (component instanceof Container hijo) {
                String encontrado = tarjetaVisible(hijo);
                if (encontrado != null) return encontrado;
            }
        }
        return null;
    }

    private String etiquetaDelPanel(Component component) {
        if (!(component instanceof Container container)) return null;
        for (Component hijo : container.getComponents()) {
            if (hijo instanceof JLabel etiqueta) {
                String texto = etiqueta.getText();
                if ("Inicio".equals(texto) || "Cuentas".equals(texto)
                        || "Ingresos".equals(texto) || "Gastos".equals(texto)
                        || "Movimientos".equals(texto) || "Inversiones".equals(texto)
                        || "Obligaciones".equals(texto) || "Transferencias".equals(texto)) return texto;
            }
            if (hijo instanceof Container contenedor) {
                String encontrado = etiquetaDelPanel(contenedor);
                if (encontrado != null) return encontrado;
            }
        }
        return null;
    }
}
