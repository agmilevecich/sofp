package ar.com.agmilevecich.sofp.ui;

import org.junit.jupiter.api.Test;

import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainFrameLayoutTest {

    @Test
    void debeConstruirShellConNavegacionCabeceraYEstado() throws Exception {
        AtomicReference<MainFrame> frame = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> frame.set(new MainFrame()));

        MainFrame mainFrame = frame.get();
        assertNotNull(mainFrame);
        assertEquals(4, mainFrame.getContentPane().getComponentCount());
        assertTrue(contieneComponente(mainFrame.getContentPane(), HeaderPanel.class));
        assertTrue(contieneComponente(mainFrame.getContentPane(), SidebarPanel.class));
        assertTrue(contieneComponente(mainFrame.getContentPane(), StatusBarPanel.class));
        mainFrame.dispose();
    }

    private boolean contieneComponente(Container container, Class<? extends Component> type) {
        for (Component component : container.getComponents()) {
            if (type.isInstance(component)) {
                return true;
            }
        }
        return false;
    }
}
