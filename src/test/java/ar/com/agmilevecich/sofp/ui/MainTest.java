package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.Usuario;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
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

        assertSame(perfil, Main.seleccionarPerfil(null, List.of(perfil)));
    }

    @Test
    void deberiaSeleccionarElPerfilElegidoEntreVarios() {
        Usuario usuario = new Usuario(
                "Ariel",
                "Test",
                "ariel.main.multiple." + System.nanoTime(),
                "hash"
        );
        PerfilFinanciero primerPerfil = new PerfilFinanciero("Perfil principal", usuario);
        PerfilFinanciero segundoPerfil = new PerfilFinanciero("Perfil secundario", usuario);
        List<PerfilFinanciero> perfiles = List.of(primerPerfil, segundoPerfil);

        assertSame(
                segundoPerfil,
                Main.seleccionarPerfil(perfiles, opciones -> opciones.get(1))
        );
    }

    @Test
    void deberiaRetornarNullSiSeCancelaLaSeleccion() {
        Usuario usuario = new Usuario(
                "Ariel",
                "Test",
                "ariel.main.cancel." + System.nanoTime(),
                "hash"
        );
        PerfilFinanciero primerPerfil = new PerfilFinanciero("Perfil principal", usuario);
        PerfilFinanciero segundoPerfil = new PerfilFinanciero("Perfil secundario", usuario);
        List<PerfilFinanciero> perfiles = List.of(primerPerfil, segundoPerfil);

        assertNull(Main.seleccionarPerfil(perfiles, opciones -> null));
    }
}
