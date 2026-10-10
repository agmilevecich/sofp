package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.domain.Movimiento;
import ar.com.agmilevecich.sofp.service.MovimientoService;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

/** Panel del módulo de movimientos. */
public class MovimientosPanel extends JPanel {

    private final DefaultListModel<String> modeloMovimientos;
    private final JList<String> listaMovimientos;
    private final MovimientoService movimientoService;
    private final Long cuentaId;
    private final Long usuarioId;

    /** Constructor del shell sin contexto de usuario. */
    public MovimientosPanel() {
        modeloMovimientos = new DefaultListModel<>();
        listaMovimientos = new JList<>(modeloMovimientos);
        movimientoService = null;
        cuentaId = null;
        usuarioId = null;
        setLayout(new BorderLayout());
        add(new JLabel("Movimientos", SwingConstants.CENTER), BorderLayout.CENTER);
    }

    /**
     * Constructor para consultar los movimientos de una cuenta autorizada.
     * La consulta pasa por MovimientoService, que mantiene las reglas de autorización.
     */
    public MovimientosPanel(MovimientoService movimientoService,
                            Long cuentaId,
                            Long usuarioId) {
        this.movimientoService = Objects.requireNonNull(
                movimientoService,
                "El MovimientoService es obligatorio"
        );
        this.cuentaId = Objects.requireNonNull(
                cuentaId,
                "El id de la cuenta es obligatorio"
        );
        this.usuarioId = Objects.requireNonNull(
                usuarioId,
                "El id del usuario es obligatorio"
        );

        modeloMovimientos = new DefaultListModel<>();
        listaMovimientos = new JList<>(modeloMovimientos);

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel titulo = new JLabel("Movimientos");
        add(titulo, BorderLayout.NORTH);

        JPanel panelLista = new JPanel(new BorderLayout());
        panelLista.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Movimientos registrados",
                TitledBorder.LEFT,
                TitledBorder.TOP
        ));
        panelLista.add(new JScrollPane(listaMovimientos), BorderLayout.CENTER);
        add(panelLista, BorderLayout.CENTER);
        actualizarMovimientos();
    }

    public JList<String> getListaMovimientos() {
        return listaMovimientos;
    }

    void actualizarMovimientos() {
        modeloMovimientos.clear();
        cargarMovimientos(movimientoService.listarPorCuenta(cuentaId, usuarioId));
    }

    private void cargarMovimientos(List<Movimiento> movimientos) {
        for (Movimiento movimiento : movimientos) {
            modeloMovimientos.addElement(
                    movimiento.getTipoMovimiento()
                            + " - "
                            + movimiento.getDescripcion()
                            + " - "
                            + movimiento.getImporte().setScale(2, RoundingMode.HALF_UP).toPlainString()
            );
        }
    }
}
