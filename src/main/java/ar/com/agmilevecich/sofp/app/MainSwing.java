package ar.com.agmilevecich.sofp.app;

import ar.com.agmilevecich.sofp.ui.MainFrame;

import javax.swing.SwingUtilities;

/** Entrada explícita para ejecutar el shell Swing de SOFP. */
public final class MainSwing {

    private MainSwing() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
