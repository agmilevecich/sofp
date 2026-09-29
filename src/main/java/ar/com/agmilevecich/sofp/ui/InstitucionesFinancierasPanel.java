package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.domain.InstitucionFinanciera;
import ar.com.agmilevecich.sofp.domain.TipoInstitucionFinanciera;
import ar.com.agmilevecich.sofp.service.InstitucionFinancieraService;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.List;
import java.util.Objects;

public class InstitucionesFinancierasPanel extends JPanel {

    private final InstitucionFinancieraService institucionFinancieraService;
    private final Long usuarioId;
    private final DefaultListModel<String> modeloInstituciones;
    private final JList<String> listaInstituciones;
    private final JTextField nombreField;
    private final JComboBox<TipoInstitucionFinanciera> tipoComboBox;

    public InstitucionesFinancierasPanel(
            InstitucionFinancieraService institucionFinancieraService) {
        this(institucionFinancieraService, null);
    }

    public InstitucionesFinancierasPanel(
            InstitucionFinancieraService institucionFinancieraService,
            Long usuarioId) {

        this.institucionFinancieraService = Objects.requireNonNull(
                institucionFinancieraService,
                "El InstitucionFinancieraService es obligatorio"
        );
        this.usuarioId = usuarioId;

        modeloInstituciones = new DefaultListModel<>();
        listaInstituciones = new JList<>(modeloInstituciones);
        nombreField = new JTextField();
        tipoComboBox = new JComboBox<>();

        configurarComboBox(tipoComboBox);
        agregarTiposInstitucion();

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel titulo = new JLabel("Instituciones financieras");
        add(titulo, BorderLayout.NORTH);

        JPanel panelLista = new JPanel(new BorderLayout());
        panelLista.setBorder(BorderFactory.createTitledBorder("Instituciones registradas"));
        panelLista.add(new JScrollPane(listaInstituciones), BorderLayout.CENTER);
        add(panelLista, BorderLayout.CENTER);

        JPanel formulario = new JPanel(new GridLayout(0, 2, 8, 8));
        formulario.setBorder(BorderFactory.createTitledBorder("Registrar institución"));

        formulario.add(new JLabel("Nombre"));
        formulario.add(nombreField);

        formulario.add(new JLabel("Tipo"));
        formulario.add(tipoComboBox);

        JButton registrarButton = new JButton("Registrar");
        registrarButton.addActionListener(event -> registrarInstitucion());

        formulario.add(new JLabel());
        formulario.add(registrarButton);

        add(formulario, BorderLayout.SOUTH);

        actualizarInstituciones();
    }

    public JTextField getNombreField() {
        return nombreField;
    }

    public JComboBox<TipoInstitucionFinanciera> getTipoComboBox() {
        return tipoComboBox;
    }

    public JList<String> getListaInstituciones() {
        return listaInstituciones;
    }

    void registrarInstitucion() {
        String nombre = nombreField.getText().trim();
        if (nombre.isEmpty()) return;

        TipoInstitucionFinanciera tipo =
                (TipoInstitucionFinanciera) tipoComboBox.getSelectedItem();

        if (tipo == null) return;

        InstitucionFinanciera institucion =
                new InstitucionFinanciera(nombre, tipo);

        if (usuarioId != null) {
            institucionFinancieraService.registrar(institucion, usuarioId);
        } else {
            institucionFinancieraService.registrar(institucion);
        }

        nombreField.setText("");
        tipoComboBox.setSelectedItem(null);
        actualizarInstituciones();
    }

    void actualizarInstituciones() {
        List<InstitucionFinanciera> instituciones =
                usuarioId != null
                        ? institucionFinancieraService.listarPorUsuario(usuarioId)
                        : institucionFinancieraService.listarTodas();
        cargarInstituciones(instituciones);
    }

    private <T> void configurarComboBox(JComboBox<T> comboBox) {
        comboBox.addItem(null);
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                setText(value == null ? "Seleccione..." : value.toString());
                return this;
            }
        });
    }

    private void agregarTiposInstitucion() {
        for (TipoInstitucionFinanciera tipo : TipoInstitucionFinanciera.values()) {
            tipoComboBox.addItem(tipo);
        }
    }

    private void cargarInstituciones(List<InstitucionFinanciera> instituciones) {
        modeloInstituciones.clear();
        for (InstitucionFinanciera institucion : instituciones) {
            String estado = institucion.isActiva() ? "Activa" : "Inactiva";
            modeloInstituciones.addElement(
                    institucion.getNombre() + " — " + institucion.getTipo() + " — " + estado
            );
        }
    }
}
