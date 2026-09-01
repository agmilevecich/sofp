package ar.com.agmilevecich.sofp.app;

import org.junit.jupiter.api.Test;

import java.awt.GraphicsEnvironment;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MainTest {

    @Test
    void deberiaArrancarElShellSwingSinExcepcion() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }

        assertDoesNotThrow(() -> Main.main(new String[0]));
    }
}
