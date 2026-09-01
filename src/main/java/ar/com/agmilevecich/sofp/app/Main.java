package ar.com.agmilevecich.sofp.app;

import ar.com.agmilevecich.sofp.ui.MainFrame;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
