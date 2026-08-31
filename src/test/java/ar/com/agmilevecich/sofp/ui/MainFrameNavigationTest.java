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

            if (sidebar == null) {
                throw new AssertionError(
                        "No se encontró el panel de navegación lateral"
                );
            }

            JButton botonCuentas = buscarBoton(sidebar, "Cuentas");

            if (botonCuentas == null) {
                throw new AssertionError(
                        "No se encontró el botón: Cuentas"
                );
            }

            botonCuentas.doClick();

            assertEquals(
                    "Cuentas",
                    tarjetaVisible(mainFrame.getContentPane())
            );

            JButton botonMovimientos =
                    buscarBoton(sidebar, "Movimientos");

            if (botonMovimientos == null) {
                throw new AssertionError(
                        "No se encontró el botón: Movimientos"
                );
            }

            botonMovimientos.doClick();

            assertEquals(
                    "Movimientos",
                    tarjetaVisible(mainFrame.getContentPane())
            );

            JButton botonInversiones =
                    buscarBoton(sidebar, "Inversiones");

            if (botonInversiones == null) {
                throw new AssertionError(
                        "No se encontró el botón: Inversiones"
                );
            }

            botonInversiones.doClick();

            assertEquals(
                    "Inversiones",
                    tarjetaVisible(mainFrame.getContentPane())
            );
        });
    }

    private SidebarPanel buscarSidebar(Container container) {

        for (Component component : container.getComponents()) {

            if (component instanceof SidebarPanel sidebar) {
                return sidebar;
            }

            if (component instanceof Container hijo) {

                SidebarPanel encontrado =
                        buscarSidebar(hijo);

                if (encontrado != null) {
                    return encontrado;
                }
            }
        }

        return null;
    }

    private JButton buscarBoton(
            Container container,
            String texto) {

        for (Component component : container.getComponents()) {

            if (component instanceof JButton boton
                    && texto.equals(boton.getText())) {

                return boton;
            }

            if (component instanceof Container hijo) {

                JButton encontrado =
                        buscarBoton(hijo, texto);

                if (encontrado != null) {
                    return encontrado;
                }
            }
        }

        return null;
    }

    private String tarjetaVisible(Container container) {

        for (Component component : container.getComponents()) {

            if (!component.isVisible()) {
                continue;
            }

            if (component instanceof InicioPanel) {
                return etiquetaDelPanel(component);
            }

            if (component instanceof CuentasPanel) {
                return etiquetaDelPanel(component);
            }

            if (component instanceof MovimientosPanel) {
                return etiquetaDelPanel(component);
            }

            if (component instanceof InversionesPanel) {
                return etiquetaDelPanel(component);
            }

            if (component instanceof Container hijo) {

                String encontrado =
                        tarjetaVisible(hijo);

                if (encontrado != null) {
                    return encontrado;
                }
            }
        }

        return null;
    }

    private String etiquetaDelPanel(Component component) {

        if (!(component instanceof Container container)) {
            return null;
        }

        for (Component hijo : container.getComponents()) {

            if (hijo instanceof JLabel etiqueta) {

                String texto = etiqueta.getText();

                if ("Inicio".equals(texto)
                        || "Cuentas".equals(texto)
                        || "Movimientos".equals(texto)
                        || "Inversiones".equals(texto)) {

                    return texto;
                }
            }

            if (hijo instanceof Container contenedor) {

                String encontrado =
                        etiquetaDelPanel(contenedor);

                if (encontrado != null) {
                    return encontrado;
                }
            }
        }

        return null;
    }
}