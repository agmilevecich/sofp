package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.Usuario;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;

class MainTest {

    @Test
    void deberiaSeleccionarAutomaticamenteElUnicoPerfil() {
        Usuario usuario = new Usuario(
                "Ariel",
                "Test",
                "ariel.main." + System.nanoTime(),
                "hash"
        );
        PerfilFinanciero perfil = new PerfilFinanciero("Perfil principal", usuario);

        assertSame(perfil, Main.seleccionarPerfil(List.of(perfil)));
    }
}
