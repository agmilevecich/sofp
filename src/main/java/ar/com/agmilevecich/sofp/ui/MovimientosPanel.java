package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.Movimiento;
import ar.com.agmilevecich.sofp.service.CuentaService;
import ar.com.agmilevecich.sofp.service.MovimientoService;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
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
    private Long cuentaId;
    private final Long usuarioId;
    private final JComboBox<Cuenta> cuentaCombo;

    /** Constructor del shell sin contexto de usuario. */
    public MovimientosPanel() {
        modeloMovimientos = new DefaultListModel<>();
        listaMovimientos = new JList<>(modeloMovimientos);
        movimientoService = null;
        cuentaId = null;
        usuarioId = null;
        cuentaCombo = null;
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
        cuentaCombo = null;

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

    /**
     * Constructor que permite elegir la cuenta desde el propio panel de movimientos.
     */
    public MovimientosPanel(MovimientoService movimientoService,
                            CuentaService cuentaService,
                            Long perfilFinancieroId,
                            Long usuarioId) {
        this.movimientoService = Objects.requireNonNull(
                movimientoService, "El MovimientoService es obligatorio");
        Objects.requireNonNull(cuentaService, "El CuentaService es obligatorio");
        Objects.requireNonNull(perfilFinancieroId, "El id del perfil financiero es obligatorio");
        this.usuarioId = Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");
        this.cuentaId = null;

        modeloMovimientos = new DefaultListModel<>();
        listaMovimientos = new JList<>(modeloMovimientos);
        cuentaCombo = new JComboBox<>();
        cuentaCombo.setName("cuentaMovimientosCombo");
        cuentaCombo.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel etiqueta = new JLabel();
            if (value instanceof Cuenta cuenta) {
                etiqueta.setText(cuenta.getNombre() + " | " + cuenta.getMoneda().getCodigo());
            } else {
                etiqueta.setText("Seleccione una cuenta");
            }
            if (isSelected) {
                etiqueta.setOpaque(true);
                etiqueta.setBackground(list.getSelectionBackground());
                etiqueta.setForeground(list.getSelectionForeground());
            }
            return etiqueta;
        });

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JLabel titulo = new JLabel("Movimientos");
        add(titulo, BorderLayout.NORTH);

        JPanel panelLista = new JPanel(new BorderLayout(8, 8));
        panelLista.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Movimientos registrados",
                TitledBorder.LEFT,
                TitledBorder.TOP
        ));
        JPanel selector = new JPanel(new BorderLayout(8, 0));
        selector.add(new JLabel("Cuenta:"), BorderLayout.WEST);
        selector.add(cuentaCombo, BorderLayout.CENTER);
        panelLista.add(selector, BorderLayout.NORTH);
        panelLista.add(new JScrollPane(listaMovimientos), BorderLayout.CENTER);
        add(panelLista, BorderLayout.CENTER);

        cuentaService.listarPorPerfilFinanciero(perfilFinancieroId, usuarioId)
                .forEach(cuentaCombo::addItem);
        cuentaCombo.addActionListener(event -> actualizarMovimientos());
        if (cuentaCombo.getItemCount() > 0) {
            cuentaCombo.setSelectedIndex(0);
        } else {
            modeloMovimientos.clear();
        }
    }

    public JList<String> getListaMovimientos() {
        return listaMovimientos;
    }

    public JComboBox<Cuenta> getCuentaComboBox() {
        return cuentaCombo;
    }

    void actualizarMovimientos() {
        if (cuentaCombo != null) {
            Cuenta seleccionada = (Cuenta) cuentaCombo.getSelectedItem();
            cuentaId = seleccionada != null ? seleccionada.getId() : null;
        }
        modeloMovimientos.clear();
        if (movimientoService != null && cuentaId != null) {
            cargarMovimientos(movimientoService.listarPorCuenta(cuentaId, usuarioId));
        }
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
