package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.domain.DetalleMovimientoCarteraActivo;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.ResumenPatrimonial;
import ar.com.agmilevecich.sofp.service.CarteraActivoService;
import ar.com.agmilevecich.sofp.service.PatrimonioFinancieroService;

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

    /**
     * Constructor para mostrar el patrimonio financiero consolidado del perfil.
     * La valorización y las reglas patrimoniales permanecen en PatrimonioFinancieroService.
     */
    public ReportesPanel(PatrimonioFinancieroService patrimonioFinancieroService,
                         PerfilFinanciero perfilFinanciero,
                         Long usuarioId) {
        Objects.requireNonNull(
                patrimonioFinancieroService,
                "El PatrimonioFinancieroService es obligatorio"
        );
        Objects.requireNonNull(perfilFinanciero, "El perfil financiero es obligatorio");
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");

        modeloReportes = new DefaultListModel<>();
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(new JLabel("Patrimonio financiero consolidado"), BorderLayout.NORTH);
        add(new JScrollPane(new JList<>(modeloReportes)), BorderLayout.CENTER);

        cargarPatrimonio(
                patrimonioFinancieroService.calcular(
                        perfilFinanciero,
                        usuarioId,
                        java.util.Map.of()
                )
        );
    }

    private void cargarPatrimonio(ResumenPatrimonial resumen) {
        String moneda = resumen.getMonedaPresentacion().getCodigo();

        modeloReportes.addElement("Moneda de presentación: " + moneda);
        modeloReportes.addElement("ACTIVOS");
        modeloReportes.addElement("  Activos monetarios: " + resumen.getActivosMonetarios() + " " + moneda);
        modeloReportes.addElement("  Inversiones: " + resumen.getActivosInversiones() + " " + moneda);
        modeloReportes.addElement("  Activos totales: " + resumen.getActivosTotales() + " " + moneda);
        modeloReportes.addElement("PASIVOS");
        modeloReportes.addElement("  Tarjetas y obligaciones asociadas: "
                + resumen.getPasivosTarjetas() + " " + moneda);
        modeloReportes.addElement("  Pasivos totales: " + resumen.getPasivosTotales() + " " + moneda);
        modeloReportes.addElement("PATRIMONIO NETO");
        modeloReportes.addElement("  Patrimonio neto: " + resumen.getPatrimonioNeto() + " " + moneda);
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
