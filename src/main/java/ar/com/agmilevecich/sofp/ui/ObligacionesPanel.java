package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.EstadoObligacion;
import ar.com.agmilevecich.sofp.domain.Obligacion;
import ar.com.agmilevecich.sofp.service.CategoriaService;
import ar.com.agmilevecich.sofp.service.CuentaService;
import ar.com.agmilevecich.sofp.service.ObligacionService;
import ar.com.agmilevecich.sofp.service.PagoTarjetaService;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/** Panel para consultar obligaciones del usuario, cerrar ciclos y registrar sus pagos. */
public class ObligacionesPanel extends JPanel {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm");

    private final ObligacionService obligacionService;
    private final PagoTarjetaService pagoTarjetaService;
    private final CuentaService cuentaService;
    private final CategoriaService categoriaService;
    private final Long perfilFinancieroId;
    private final Long usuarioId;
    private final JList<Obligacion> obligacionesList;
    private final JComboBox<Cuenta> cuentaPagadoraCombo;
    private final JComboBox<Categoria> categoriaCombo;
    private final JTextField importePagoField;
    private final JButton cerrarCicloButton;
    private final JButton registrarPagoButton;

    /** Constructor del shell sin contexto de usuario. */
    public ObligacionesPanel() {
        obligacionService = null;
        pagoTarjetaService = null;
        cuentaService = null;
        categoriaService = null;
        perfilFinancieroId = null;
        usuarioId = null;
        obligacionesList = new JList<>();
        cuentaPagadoraCombo = new JComboBox<>();
        categoriaCombo = new JComboBox<>();
        importePagoField = new JTextField(12);
        cerrarCicloButton = new JButton("Cerrar ciclo");
        registrarPagoButton = new JButton("Registrar pago");
        construirPanel();
        cerrarCicloButton.setEnabled(false);
        registrarPagoButton.setEnabled(false);
    }

    /** Constructor de compatibilidad para consulta de obligaciones sin pago coordinado. */
    public ObligacionesPanel(ObligacionService obligacionService, Long usuarioId) {
        this(obligacionService, null, null, null, null, usuarioId);
    }

    public ObligacionesPanel(ObligacionService obligacionService,
                             PagoTarjetaService pagoTarjetaService,
                             CuentaService cuentaService,
                             CategoriaService categoriaService,
                             Long perfilFinancieroId,
                             Long usuarioId) {
        this.obligacionService = Objects.requireNonNull(
                obligacionService,
                "El ObligacionService es obligatorio"
        );
        this.pagoTarjetaService = pagoTarjetaService;
        this.cuentaService = cuentaService;
        this.categoriaService = categoriaService;
        this.perfilFinancieroId = perfilFinancieroId;
        this.usuarioId = Objects.requireNonNull(
                usuarioId,
                "El id del usuario es obligatorio"
        );
        obligacionesList = new JList<>();
        cuentaPagadoraCombo = new JComboBox<>();
        categoriaCombo = new JComboBox<>();
        importePagoField = new JTextField(12);
        cerrarCicloButton = new JButton("Cerrar ciclo");
        registrarPagoButton = new JButton("Registrar pago");

        configurarLista();
        configurarCombos();
        construirPanel();
        obligacionesList.addListSelectionListener(evento -> actualizarEstadoBotones());
        cerrarCicloButton.addActionListener(evento -> cerrarCiclo());
        registrarPagoButton.addActionListener(evento -> registrarPago());
        if (pagoTarjetaService != null && cuentaService != null && categoriaService != null && perfilFinancieroId != null) {
            refrescarCuentasYCategorias();
        }
        refrescar();
    }

    public JList<Obligacion> getObligacionesList() {
        return obligacionesList;
    }

    public JComboBox<Cuenta> getCuentaPagadoraCombo() {
        return cuentaPagadoraCombo;
    }

    public JComboBox<Categoria> getCategoriaCombo() {
        return categoriaCombo;
    }

    public JTextField getImportePagoField() {
        return importePagoField;
    }

    public JButton getCerrarCicloButton() {
        return cerrarCicloButton;
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

        actualizarEstadoBotones();
    }

    /** Recarga las cuentas y categorías disponibles para registrar pagos coordinados. */
    public void refrescarCuentasYCategorias() {
        if (cuentaService == null || categoriaService == null || perfilFinancieroId == null || usuarioId == null) {
            return;
        }

        cuentaPagadoraCombo.removeAllItems();
        for (Cuenta cuenta : cuentaService.listarPorPerfilFinanciero(perfilFinancieroId, usuarioId)) {
            cuentaPagadoraCombo.addItem(cuenta);
        }

        categoriaCombo.removeAllItems();
        for (Categoria categoria : categoriaService.listarPorPerfilFinanciero(perfilFinancieroId, usuarioId)) {
            categoriaCombo.addItem(categoria);
        }
    }

    /** Ejecuta el cierre del ciclo al que pertenece la obligación seleccionada. */
    void cerrarCicloSeleccionado() {
        Obligacion obligacion = Objects.requireNonNull(
                obligacionesList.getSelectedValue(),
                "La obligación es obligatoria"
        );
        Cuenta cuenta = obligacion.getMovimientoOrigen().getCuenta();
        LocalDateTime fechaOrigen = obligacion.getFechaOrigen();
        obligacionService.cerrarCiclo(
                cuenta.getId(),
                obligacion.getCicloFacturacion().getFechaCierre()
        );
        refrescar();
    }

    /** Registra el pago seleccionado mediante el servicio coordinador, sin mostrar diálogos. */
    void registrarPagoSeleccionado() {
        if (pagoTarjetaService == null) {
            throw new IllegalStateException("El pago coordinado de tarjeta no está configurado");
        }
        Obligacion obligacion = Objects.requireNonNull(
                obligacionesList.getSelectedValue(),
                "La obligación es obligatoria"
        );
        Cuenta cuentaPagadora = Objects.requireNonNull(
                (Cuenta) cuentaPagadoraCombo.getSelectedItem(),
                "La cuenta pagadora es obligatoria"
        );
        Categoria categoria = Objects.requireNonNull(
                (Categoria) categoriaCombo.getSelectedItem(),
                "La categoría es obligatoria"
        );
        BigDecimal importe = new BigDecimal(importePagoField.getText().trim());

        pagoTarjetaService.registrarPago(
                obligacion.getId(),
                cuentaPagadora,
                categoria,
                importe,
                LocalDateTime.now(),
                "Pago de tarjeta",
                usuarioId
        );
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
                            Locale.ROOT,
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

    private void configurarCombos() {
        cuentaPagadoraCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Cuenta cuenta) {
                    setText(cuenta.getNombre() + " | " + cuenta.getMoneda().getCodigo());
                }
                return this;
            }
        });
        categoriaCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Categoria categoria) {
                    setText(categoria.getNombre());
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
        panelPago.add(new JLabel("Cuenta pagadora"), constraints);

        constraints.gridx = 1;
        constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panelPago.add(cuentaPagadoraCombo, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 0.0;
        constraints.fill = GridBagConstraints.NONE;
        panelPago.add(new JLabel("Categoría"), constraints);

        constraints.gridx = 1;
        constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panelPago.add(categoriaCombo, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.weightx = 0.0;
        constraints.fill = GridBagConstraints.NONE;
        panelPago.add(new JLabel("Importe"), constraints);

        constraints.gridx = 1;
        constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panelPago.add(importePagoField, constraints);

        constraints.gridx = 2;
        constraints.weightx = 0.0;
        constraints.fill = GridBagConstraints.NONE;
        panelPago.add(registrarPagoButton, constraints);

        constraints.gridx = 3;
        panelPago.add(cerrarCicloButton, constraints);

        add(panelPago, BorderLayout.SOUTH);
    }

    private void actualizarEstadoBotones() {
        Obligacion seleccionada = obligacionesList.getSelectedValue();
        cerrarCicloButton.setEnabled(seleccionada != null);
        registrarPagoButton.setEnabled(
                pagoTarjetaService != null
                        && cuentaPagadoraCombo.getSelectedItem() != null
                        && categoriaCombo.getSelectedItem() != null
                        && seleccionada != null
                        && seleccionada.getEstado() != EstadoObligacion.PAGADA
        );
    }

    private void cerrarCiclo() {
        try {
            cerrarCicloSeleccionado();
            JOptionPane.showMessageDialog(
                    this,
                    "Ciclo cerrado correctamente",
                    "Obligaciones",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "No se pudo cerrar el ciclo",
                    JOptionPane.ERROR_MESSAGE
            );
        }
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
