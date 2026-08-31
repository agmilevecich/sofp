package ar.com.agmilevecich.sofp.ui;

import org.junit.jupiter.api.Test;

import javax.swing.SwingUtilities;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainFrameTest {

    @Test
    void debeCrearVentanaPrincipal() throws Exception {
        AtomicReference<MainFrame> frame = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> frame.set(new MainFrame()));

        MainFrame mainFrame = frame.get();
        assertNotNull(mainFrame);
        assertEquals("SOFP - Sistema Operativo Financiero Personal", mainFrame.getTitle());
        assertTrue(mainFrame.isDisplayable() || !mainFrame.isVisible());
        mainFrame.dispose();
    }
}
