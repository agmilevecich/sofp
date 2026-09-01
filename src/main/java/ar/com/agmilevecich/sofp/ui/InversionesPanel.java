package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.domain.PosicionActivo;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.service.CarteraActivoService;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.util.List;
import java.util.Objects;

/** Panel del módulo de inversiones. */
public class InversionesPanel extends JPanel {

    private final DefaultListModel<String> modeloPosiciones;

    /** Constructor del shell sin contexto de usuario. */
    public InversionesPanel() {
        modeloPosiciones = new DefaultListModel<>();
        setLayout(new BorderLayout());
        add(new JLabel("Inversiones"), BorderLayout.NORTH);
        add(new JScrollPane(new JList<>(modeloPosiciones)), BorderLayout.CENTER);
    }

    /**
     * Constructor para mostrar las posiciones de inversión del perfil del usuario autenticado.
     * La consulta pasa por CarteraActivoService, que mantiene las reglas de autorización.
     */
    public InversionesPanel(CarteraActivoService carteraActivoService,
                            PerfilFinanciero perfilFinanciero,
                            Long usuarioId) {

        Objects.requireNonNull(
                carteraActivoService,
                "El CarteraActivoService es obligatorio"
        );
        Objects.requireNonNull(
                perfilFinanciero,
                "El perfil financiero es obligatorio"
        );
        Objects.requireNonNull(
                usuarioId,
                "El id del usuario es obligatorio"
        );

        modeloPosiciones = new DefaultListModel<>();
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        add(new JLabel("Inversiones"), BorderLayout.NORTH);

        JList<String> lista = new JList<>(modeloPosiciones);
        add(new JScrollPane(lista), BorderLayout.CENTER);

        cargarPosiciones(carteraActivoService.obtenerPosiciones(
                perfilFinanciero,
                usuarioId
        ));
    }

    private void cargarPosiciones(List<PosicionActivo> posiciones) {
        for (PosicionActivo posicion : posiciones) {
            modeloPosiciones.addElement(
                    posicion.getActivo().getSimbolo()
                            + " - "
                            + posicion.getCantidad()
            );
        }
    }
}
