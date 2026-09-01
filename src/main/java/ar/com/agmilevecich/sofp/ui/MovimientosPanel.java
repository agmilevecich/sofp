package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.Movimiento;
import ar.com.agmilevecich.sofp.service.CategoriaService;
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
    private final JList<String> listaMovimientos;

    /** Constructor del shell sin contexto de usuario. */
    public MovimientosPanel() {
        modeloMovimientos = new DefaultListModel<>();
        listaMovimientos = new JList<>(modeloMovimientos);
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
        this(movimientoService, null, cuentaId, usuarioId);
    }

    /**
     * Constructor para consultar y registrar movimientos de una cuenta autorizada.
     * Las categorías se obtienen mediante CategoriaService y el alta se delega en MovimientoService.
     */
    public MovimientosPanel(MovimientoService movimientoService,
                            CategoriaService categoriaService,
                            Cuenta cuenta,
                            Long usuarioId) {
        Objects.requireNonNull(movimientoService, "El MovimientoService es obligatorio");
        Objects.requireNonNull(cuenta, "La cuenta es obligatoria");
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");

        modeloMovimientos = new DefaultListModel<>();
        listaMovimientos = new JList<>(modeloMovimientos);

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        add(new JLabel("Movimientos"), BorderLayout.NORTH);
        add(new JScrollPane(listaMovimientos), BorderLayout.CENTER);
        cargarMovimientos(movimientoService.listarPorCuenta(cuenta.getId(), usuarioId));

        if (categoriaService != null) {
            add(new RegistrarMovimientoPanel(
                    movimientoService,
                    categoriaService,
                    cuenta,
                    usuarioId
            ), BorderLayout.SOUTH);
        }
    }

    public JList<String> getListaMovimientos() {
        return listaMovimientos;
    }

    private MovimientosPanel(MovimientoService movimientoService,
                             CategoriaService categoriaService,
                             Long cuentaId,
                             Long usuarioId) {
        Objects.requireNonNull(movimientoService, "El MovimientoService es obligatorio");
        Objects.requireNonNull(cuentaId, "El id de la cuenta es obligatorio");
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");

        modeloMovimientos = new DefaultListModel<>();
        listaMovimientos = new JList<>(modeloMovimientos);

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(new JLabel("Movimientos"), BorderLayout.NORTH);
        add(new JScrollPane(listaMovimientos), BorderLayout.CENTER);
        cargarMovimientos(movimientoService.listarPorCuenta(cuentaId, usuarioId));
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
