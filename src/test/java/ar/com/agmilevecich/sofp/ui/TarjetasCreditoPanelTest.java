package ar.com.agmilevecich.sofp.ui;

import org.junit.jupiter.api.Test;

import javax.swing.JLabel;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TarjetasCreditoPanelTest {

    @Test
    void deberiaConstruirElPanelDeTarjetasSinContexto() {
        TarjetasCreditoPanel panel = new TarjetasCreditoPanel();

        assertNotNull(panel);
        assertNotNull(encontrarEtiqueta(panel, "Tarjetas"));
    }

    private JLabel encontrarEtiqueta(java.awt.Container container, String texto) {
        for (java.awt.Component component : container.getComponents()) {
            if (component instanceof JLabel label && texto.equals(label.getText())) {
                return label;
            }
            if (component instanceof java.awt.Container hijo) {
                JLabel encontrada = encontrarEtiqueta(hijo, texto);
                if (encontrada != null) {
                    return encontrada;
                }
            }
        }
        return null;
    }
}
