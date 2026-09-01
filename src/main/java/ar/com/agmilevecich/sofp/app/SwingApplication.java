package ar.com.agmilevecich.sofp.app;

import ar.com.agmilevecich.sofp.ui.MainFrame;

import javax.swing.SwingUtilities;

/** Punto de entrada del shell gráfico de SOFP. */
public final class SwingApplication {

    private SwingApplication() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
