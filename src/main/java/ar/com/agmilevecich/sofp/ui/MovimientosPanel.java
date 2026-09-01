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
import java.awt.BorderLayout;
import java.util.List;
import java.util.Objects;

/** Panel del módulo de movimientos. */
public class MovimientosPanel extends JPanel {

    private final DefaultListModel<String> modeloMovimientos;

    /** Constructor del shell sin contexto de usuario. */
    public MovimientosPanel() {
        modeloMovimientos = new DefaultListModel<>();
        setLayout(new BorderLayout());
        add(new JLabel("Movimientos", SwingConstants.CENTER), BorderLayout.CENTER);
    }

    /**
     * Constructor para mostrar los movimientos de una cuenta del usuario autenticado.
     * La consulta pasa por MovimientoService, que mantiene las reglas de autorización.
     */
    public MovimientosPanel(MovimientoService movimientoService,
                            Long cuentaId,
                            Long usuarioId) {

        Objects.requireNonNull(
                movimientoService,
                "El MovimientoService es obligatorio"
        );
        Objects.requireNonNull(
                cuentaId,
                "El id de la cuenta es obligatorio"
        );
        Objects.requireNonNull(
                usuarioId,
                "El id del usuario es obligatorio"
        );

        modeloMovimientos = new DefaultListModel<>();
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        add(new JLabel("Movimientos"), BorderLayout.NORTH);

        JList<String> lista = new JList<>(modeloMovimientos);
        add(new JScrollPane(lista), BorderLayout.CENTER);

        cargarMovimientos(movimientoService.listarPorCuenta(
                cuentaId,
                usuarioId
        ));
    }

    private void cargarMovimientos(List<Movimiento> movimientos) {
        for (Movimiento movimiento : movimientos) {
            modeloMovimientos.addElement(
                    movimiento.getTipoMovimiento()
                            + " - "
                            + movimiento.getDescripcion()
                            + " - "
                            + movimiento.getImporte()
            );
        }
    }
}
