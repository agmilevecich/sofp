package ar.com.agmilevecich.sofp.app;

import org.junit.jupiter.api.Test;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SwingApplicationTest {

    @Test
    void deberiaArrancarElShellSwing() throws Exception {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }

        assertDoesNotThrow(() -> SwingApplication.main(new String[0]));
        SwingUtilities.invokeAndWait(() -> {
        });

        Frame[] frames = JFrame.getFrames();
        assertTrue(frames.length > 0);
        assertTrue(frames[frames.length - 1].isVisible());

        for (Frame frame : frames) {
            frame.dispose();
        }
    }
}
