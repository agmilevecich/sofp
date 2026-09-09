package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.domain.EstadoObligacion;
import ar.com.agmilevecich.sofp.domain.Obligacion;
import ar.com.agmilevecich.sofp.service.ObligacionService;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/** Panel para consultar obligaciones del usuario y registrar sus pagos. */
public class ObligacionesPanel extends JPanel {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm");

    private final ObligacionService obligacionService;
    private final Long usuarioId;
    private final JList<Obligacion> obligacionesList;
    private final JTextField importePagoField;
    private final JButton registrarPagoButton;

    /** Constructor del shell sin contexto de usuario. */
    public ObligacionesPanel() {
        obligacionService = null;
        usuarioId = null;
        obligacionesList = new JList<>();
        importePagoField = new JTextField(12);
        registrarPagoButton = new JButton("Registrar pago");
        construirPanel();
        registrarPagoButton.setEnabled(false);
    }

    public ObligacionesPanel(ObligacionService obligacionService, Long usuarioId) {
        this.obligacionService = Objects.requireNonNull(
                obligacionService,
                "El ObligacionService es obligatorio"
        );
        this.usuarioId = Objects.requireNonNull(
                usuarioId,
                "El id del usuario es obligatorio"
        );
        obligacionesList = new JList<>();
        importePagoField = new JTextField(12);
        registrarPagoButton = new JButton("Registrar pago");

        configurarLista();
        construirPanel();
        obligacionesList.addListSelectionListener(evento -> actualizarEstadoBoton());
        registrarPagoButton.addActionListener(evento -> registrarPago());
        refrescar();
    }

    public JList<Obligacion> getObligacionesList() {
        return obligacionesList;
    }

    public JTextField getImportePagoField() {
        return importePagoField;
    }

    public JButton getRegistrarPagoButton() {
        return registrarPagoButton;
    }

    /** Recarga las obligaciones del usuario autorizado. */
    public void refrescar() {
        if (obligacionService == null || usuarioId == null) {
            return;
        }

        Long idSeleccionado = null;
        Obligacion seleccionada = obligacionesList.getSelectedValue();
        if (seleccionada != null) {
            idSeleccionado = seleccionada.getId();
        }

        List<Obligacion> obligaciones = obligacionService.listarPorUsuario(usuarioId);
        obligacionesList.setListData(obligaciones.toArray(new Obligacion[0]));

        if (idSeleccionado != null) {
            for (int i = 0; i < obligacionesList.getModel().getSize(); i++) {
                if (idSeleccionado.equals(obligacionesList.getModel().getElementAt(i).getId())) {
                    obligacionesList.setSelectedIndex(i);
                    break;
                }
            }
        }

        actualizarEstadoBoton();
    }

    /** Registra el pago seleccionado sin mostrar diálogos, para permitir su prueba desde la UI. */
    void registrarPagoSeleccionado() {
        Obligacion obligacion = Objects.requireNonNull(
                obligacionesList.getSelectedValue(),
                "La obligación es obligatoria"
        );
        BigDecimal importe = new BigDecimal(importePagoField.getText().trim());
        obligacionService.registrarPago(obligacion.getId(), importe, usuarioId);
        refrescar();
    }

    private void configurarLista() {
        obligacionesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        obligacionesList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Obligacion obligacion) {
                    setText(String.format(
                            "%.2f %s | pendiente %.2f %s | %s | %s",
                            obligacion.getImporteOriginal(),
                            obligacion.getMoneda().getCodigo(),
                            obligacion.getSaldoPendiente(),
                            obligacion.getMoneda().getCodigo(),
                            obligacion.getEstado(),
                            obligacion.getFechaOrigen().format(FORMATO_FECHA)
                    ));
                } else {
                    setText("No hay obligaciones");
                }
                return this;
            }
        });
    }

    private void construirPanel() {
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel titulo = new JLabel("Obligaciones");
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 4, 4, 4));
        add(titulo, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(obligacionesList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Obligaciones del usuario"));
        add(scrollPane, BorderLayout.CENTER);

        JPanel panelPago = new JPanel(new GridBagLayout());
        panelPago.setBorder(BorderFactory.createTitledBorder("Registrar pago"));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(6, 6, 6, 6);
        constraints.anchor = GridBagConstraints.WEST;

        constraints.gridx = 0;
        constraints.gridy = 0;
        panelPago.add(new JLabel("Importe"), constraints);

        constraints.gridx = 1;
        constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panelPago.add(importePagoField, constraints);

        constraints.gridx = 2;
        constraints.weightx = 0.0;
        constraints.fill = GridBagConstraints.NONE;
        panelPago.add(registrarPagoButton, constraints);

        add(panelPago, BorderLayout.SOUTH);
    }

    private void actualizarEstadoBoton() {
        Obligacion seleccionada = obligacionesList.getSelectedValue();
        registrarPagoButton.setEnabled(
                seleccionada != null
                        && seleccionada.getEstado() != EstadoObligacion.PAGADA
        );
    }

    private void registrarPago() {
        try {
            registrarPagoSeleccionado();
            JOptionPane.showMessageDialog(
                    this,
                    "Pago registrado correctamente",
                    "Obligaciones",
                    JOptionPane.INFORMATION_MESSAGE
            );
            importePagoField.setText("");
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "No se pudo registrar el pago",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
