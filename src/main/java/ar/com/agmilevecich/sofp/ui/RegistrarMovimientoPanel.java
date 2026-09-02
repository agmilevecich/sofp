package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.Movimiento;
import ar.com.agmilevecich.sofp.domain.TipoMovimiento;
import ar.com.agmilevecich.sofp.service.CategoriaService;
import ar.com.agmilevecich.sofp.service.MovimientoService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/** Formulario Swing para registrar un movimiento monetario. */
public class RegistrarMovimientoPanel extends JPanel {

    private final MovimientoService movimientoService;
    private final Cuenta cuenta;
    private final Long usuarioId;
    private final Runnable onMovimientoRegistrado;
    private final JComboBox<Categoria> categoriaComboBox;
    private final JComboBox<TipoMovimiento> tipoMovimientoComboBox;
    private final JTextField importeField;
    private final JTextField fechaHoraField;
    private final JTextField descripcionField;
    private final JButton registrarButton;

    /** Constructor del shell sin contexto de usuario. */
    public RegistrarMovimientoPanel() {
        movimientoService = null;
        cuenta = null;
        usuarioId = null;
        onMovimientoRegistrado = null;
        categoriaComboBox = new JComboBox<>();
        tipoMovimientoComboBox = new JComboBox<>(TipoMovimiento.values());
        importeField = new JTextField(16);
        fechaHoraField = new JTextField(16);
        descripcionField = new JTextField(16);
        registrarButton = new JButton("Registrar");
        construirFormulario();
        registrarButton.setEnabled(false);
    }

    /**
     * Constructor para registrar movimientos de una cuenta del usuario autenticado.
     * Las categorías se obtienen mediante CategoriaService, respetando su autorización.
     */
    public RegistrarMovimientoPanel(MovimientoService movimientoService,
                                    CategoriaService categoriaService,
                                    Cuenta cuenta,
                                    Long usuarioId) {
        this(movimientoService, categoriaService, cuenta, usuarioId, null);
    }

    /**
     * Constructor para registrar movimientos y notificar a la pantalla contenedora
     * cuando el alta se completa correctamente.
     */
    public RegistrarMovimientoPanel(MovimientoService movimientoService,
                                    CategoriaService categoriaService,
                                    Cuenta cuenta,
                                    Long usuarioId,
                                    Runnable onMovimientoRegistrado) {
        this.movimientoService = Objects.requireNonNull(
                movimientoService,
                "El MovimientoService es obligatorio"
        );
        Objects.requireNonNull(categoriaService, "El CategoriaService es obligatorio");
        this.cuenta = Objects.requireNonNull(cuenta, "La cuenta es obligatoria");
        this.usuarioId = Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");
        this.onMovimientoRegistrado = onMovimientoRegistrado;
        categoriaComboBox = new JComboBox<>();
        tipoMovimientoComboBox = new JComboBox<>(TipoMovimiento.values());
        importeField = new JTextField(16);
        fechaHoraField = new JTextField(16);
        descripcionField = new JTextField(16);
        registrarButton = new JButton("Registrar");

        cargarCategorias(categoriaService.listarPorPerfilFinanciero(
                cuenta.getPerfilFinanciero().getId(),
                usuarioId
        ));
        construirFormulario();
        registrarButton.addActionListener(evento -> registrar());
    }

    public JComboBox<Categoria> getCategoriaComboBox() {
        return categoriaComboBox;
    }

    public JComboBox<TipoMovimiento> getTipoMovimientoComboBox() {
        return tipoMovimientoComboBox;
    }

    public JTextField getImporteField() {
        return importeField;
    }

    public JTextField getFechaHoraField() {
        return fechaHoraField;
    }

    public JTextField getDescripcionField() {
        return descripcionField;
    }

    public JButton getRegistrarButton() {
        return registrarButton;
    }

    private void construirFormulario() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(4, 4, 4, 4);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        agregarCampo(new JLabel("Categoría"), categoriaComboBox, constraints, 0);
        agregarCampo(new JLabel("Tipo"), tipoMovimientoComboBox, constraints, 1);
        agregarCampo(new JLabel("Importe"), importeField, constraints, 2);
        agregarCampo(new JLabel("Fecha y hora (AAAA-MM-DDTHH:MM)"), fechaHoraField, constraints, 3);
        agregarCampo(new JLabel("Descripción"), descripcionField, constraints, 4);

        constraints.gridx = 1;
        constraints.gridy = 5;
        add(registrarButton, constraints);
    }

    private void agregarCampo(JLabel etiqueta,
                              java.awt.Component campo,
                              GridBagConstraints constraints,
                              int fila) {
        constraints.gridx = 0;
        constraints.gridy = fila;
        add(etiqueta, constraints);
        constraints.gridx = 1;
        add(campo, constraints);
    }

    private void cargarCategorias(List<Categoria> categorias) {
        for (Categoria categoria : categorias) {
            if (categoria.isActiva()) {
                categoriaComboBox.addItem(categoria);
            }
        }
    }

    private void registrar() {
        try {
            Categoria categoria = (Categoria) Objects.requireNonNull(
                    categoriaComboBox.getSelectedItem(),
                    "La categoría es obligatoria"
            );
            TipoMovimiento tipoMovimiento = (TipoMovimiento) Objects.requireNonNull(
                    tipoMovimientoComboBox.getSelectedItem(),
                    "El tipo de movimiento es obligatorio"
            );
            BigDecimal importe = new BigDecimal(importeField.getText().trim());
            LocalDateTime fechaHora = LocalDateTime.parse(fechaHoraField.getText().trim());
            String descripcion = descripcionField.getText().trim();

            movimientoService.registrar(
                    cuenta,
                    categoria,
                    tipoMovimiento,
                    importe,
                    fechaHora,
                    descripcion,
                    usuarioId
            );

            if (onMovimientoRegistrado != null) {
                onMovimientoRegistrado.run();
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Movimiento registrado correctamente",
                    "Movimiento",
                    JOptionPane.INFORMATION_MESSAGE
            );
            limpiarFormulario();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "No se pudo registrar el movimiento",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void limpiarFormulario() {
        importeField.setText("");
        fechaHoraField.setText("");
        descripcionField.setText("");
        categoriaComboBox.setSelectedIndex(categoriaComboBox.getItemCount() > 0 ? 0 : -1);
        tipoMovimientoComboBox.setSelectedIndex(0);
    }
}
