package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.domain.DetalleMovimientoCarteraActivo;
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

/** Panel de reportes basado en los reportes de movimientos de la cartera existente. */
public class ReportesPanel extends JPanel {

    private final DefaultListModel<String> modeloReportes;

    /** Constructor del shell sin contexto de usuario. */
    public ReportesPanel() {
        modeloReportes = new DefaultListModel<>();
        setLayout(new BorderLayout());
        add(new JLabel("Reportes"), BorderLayout.NORTH);
        add(new JScrollPane(new JList<>(modeloReportes)), BorderLayout.CENTER);
    }

    /**
     * Constructor para mostrar los movimientos de activos del perfil del usuario autenticado.
     * La consulta pasa por CarteraActivoService, que mantiene las reglas de autorización.
     */
    public ReportesPanel(CarteraActivoService carteraActivoService,
                         PerfilFinanciero perfilFinanciero,
                         Long usuarioId) {
        Objects.requireNonNull(carteraActivoService, "El CarteraActivoService es obligatorio");
        Objects.requireNonNull(perfilFinanciero, "El perfil financiero es obligatorio");
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");

        modeloReportes = new DefaultListModel<>();
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(new JLabel("Reporte de movimientos de inversiones"), BorderLayout.NORTH);
        add(new JScrollPane(new JList<>(modeloReportes)), BorderLayout.CENTER);

        cargarMovimientos(carteraActivoService.obtenerMovimientos(perfilFinanciero, usuarioId));
    }

    private void cargarMovimientos(List<DetalleMovimientoCarteraActivo> movimientos) {
        for (DetalleMovimientoCarteraActivo detalle : movimientos) {
            modeloReportes.addElement(
                    detalle.getTipoMovimiento()
                            + " - "
                            + detalle.getActivo().getSimbolo()
                            + " - "
                            + detalle.getCantidad()
                            + " - "
                            + detalle.getImporte()
            );
        }
    }
}
