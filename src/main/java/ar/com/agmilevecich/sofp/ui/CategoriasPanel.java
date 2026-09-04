package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.service.CategoriaService;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Panel del módulo de categorías. */
public class CategoriasPanel extends JPanel {

    private final DefaultListModel<String> modeloCategorias;
    private final JList<String> listaCategorias;
    private final JTextField nombreField;
    private final JTextArea descripcionArea;
    private final JButton registrarButton;
    private final JButton modificarButton;
    private final JButton estadoButton;
    private final JButton eliminarButton;
    private final CategoriaService categoriaService;
    private final PerfilFinanciero perfilFinanciero;
    private final Long perfilFinancieroId;
    private final Long usuarioId;
    private final List<Categoria> categorias;

    /** Constructor del shell sin contexto de usuario. */
    public CategoriasPanel() {
        modeloCategorias = new DefaultListModel<>();
        listaCategorias = new JList<>(modeloCategorias);
        nombreField = new JTextField();
        descripcionArea = new JTextArea(3, 20);
        registrarButton = new JButton("Registrar");
        modificarButton = new JButton("Modificar");
        estadoButton = new JButton("Activar/Desactivar");
        eliminarButton = new JButton("Eliminar");
        categoriaService = null;
        perfilFinanciero = null;
        perfilFinancieroId = null;
        usuarioId = null;
        categorias = new ArrayList<>();
        setLayout(new BorderLayout(8, 8));
        add(new JLabel("Categorías"), BorderLayout.NORTH);
        add(new JScrollPane(listaCategorias), BorderLayout.CENTER);
    }

    /** Constructor para gestionar las categorías del perfil del usuario autenticado. */
    public CategoriasPanel(CategoriaService categoriaService,
                           PerfilFinanciero perfilFinanciero,
                           Long usuarioId) {
        this.categoriaService = Objects.requireNonNull(
                categoriaService,
                "El CategoriaService es obligatorio"
        );
        this.perfilFinanciero = Objects.requireNonNull(
                perfilFinanciero,
                "El perfil financiero es obligatorio"
        );
        this.perfilFinancieroId = Objects.requireNonNull(
                perfilFinanciero.getId(),
                "El id del perfil financiero es obligatorio"
        );
        this.usuarioId = Objects.requireNonNull(
                usuarioId,
                "El id del usuario es obligatorio"
        );

        modeloCategorias = new DefaultListModel<>();
        listaCategorias = new JList<>(modeloCategorias);
        nombreField = new JTextField(20);
        descripcionArea = new JTextArea(3, 20);
        descripcionArea.setLineWrap(true);
        descripcionArea.setWrapStyleWord(true);
        registrarButton = new JButton("Registrar");
        modificarButton = new JButton("Modificar");
        estadoButton = new JButton("Activar/Desactivar");
        eliminarButton = new JButton("Eliminar");
        categorias = new ArrayList<>();

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(new JLabel("Categorías"), BorderLayout.NORTH);
        add(new JScrollPane(listaCategorias), BorderLayout.CENTER);
        add(crearFormulario(), BorderLayout.SOUTH);

        listaCategorias.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                cargarCategoriaSeleccionada();
            }
        });
        registrarButton.addActionListener(this::registrarCategoria);
        modificarButton.addActionListener(this::modificarCategoria);
        estadoButton.addActionListener(this::cambiarEstado);
        eliminarButton.addActionListener(this::eliminarCategoria);

        actualizarCategorias();
    }

    private JPanel crearFormulario() {
        JPanel formulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formulario.add(new JLabel("Nombre"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        formulario.add(nombreField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formulario.add(new JLabel("Descripción"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        formulario.add(new JScrollPane(descripcionArea), gbc);

        JPanel acciones = new JPanel();
        acciones.add(registrarButton);
        acciones.add(modificarButton);
        acciones.add(estadoButton);
        acciones.add(eliminarButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        formulario.add(acciones, gbc);
        return formulario;
    }

    private void registrarCategoria(ActionEvent event) {
        try {
            registrarCategoria();
        } catch (RuntimeException e) {
            mostrarMensaje(
                    e.getMessage(),
                    "No se pudo registrar la categoría",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    void registrarCategoria() {
        Categoria categoria = new Categoria(nombreField.getText(), perfilFinanciero);
        categoria.cambiarDescripcion(descripcionArea.getText().isBlank()
                ? null
                : descripcionArea.getText());
        categoriaService.registrar(categoria, usuarioId);
        actualizarCategorias();
        limpiarFormulario();
    }

    private void modificarCategoria(ActionEvent event) {
        Categoria categoria = getCategoriaSeleccionada();
        if (categoria == null) {
            return;
        }
        try {
            categoriaService.modificarNombre(
                    categoria.getId(),
                    usuarioId,
                    nombreField.getText()
            );
            categoriaService.modificarDescripcion(
                    categoria.getId(),
                    usuarioId,
                    descripcionArea.getText().isBlank() ? null : descripcionArea.getText()
            );
            actualizarCategorias();
        } catch (RuntimeException e) {
            mostrarMensaje(
                    e.getMessage(),
                    "No se pudo modificar la categoría",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void cambiarEstado(ActionEvent event) {
        Categoria categoria = getCategoriaSeleccionada();
        if (categoria == null) {
            return;
        }
        try {
            if (categoria.isActiva()) {
                categoriaService.desactivar(categoria.getId(), usuarioId);
            } else {
                categoriaService.activar(categoria.getId(), usuarioId);
            }
            actualizarCategorias();
        } catch (RuntimeException e) {
            mostrarMensaje(
                    e.getMessage(),
                    "No se pudo cambiar el estado de la categoría",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void eliminarCategoria(ActionEvent event) {
        Categoria categoria = getCategoriaSeleccionada();
        if (categoria == null) {
            return;
        }
        try {
            boolean eliminada = categoriaService.eliminar(categoria.getId(), usuarioId);
            actualizarCategorias();
            if (eliminada) {
                limpiarFormulario();
                return;
            }
            mostrarMensaje(
                    "La categoría tiene movimientos asociados y no puede eliminarse. Se desactivó para conservar el historial.",
                    "Categoría desactivada",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (RuntimeException e) {
            mostrarMensaje(
                    e.getMessage(),
                    "No se pudo eliminar la categoría",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    void mostrarMensaje(String mensaje, String titulo, int tipoMensaje) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipoMensaje);
    }

    void actualizarCategorias() {
        int indiceSeleccionado = listaCategorias.getSelectedIndex();
        modeloCategorias.clear();
        categorias.clear();
        categorias.addAll(categoriaService.listarPorPerfilFinanciero(
                perfilFinancieroId,
                usuarioId
        ));
        for (Categoria categoria : categorias) {
            modeloCategorias.addElement(
                    categoria.getNombre() + (categoria.isActiva() ? " (activa)" : " (inactiva)")
            );
        }
        if (!categorias.isEmpty()) {
            int indice = Math.min(indiceSeleccionado, categorias.size() - 1);
            listaCategorias.setSelectedIndex(indice < 0 ? 0 : indice);
        }
    }

    private void cargarCategoriaSeleccionada() {
        Categoria categoria = getCategoriaSeleccionada();
        if (categoria == null) {
            return;
        }
        nombreField.setText(categoria.getNombre());
        descripcionArea.setText(categoria.getDescripcion() == null
                ? ""
                : categoria.getDescripcion());
    }

    private Categoria getCategoriaSeleccionada() {
        int indice = listaCategorias.getSelectedIndex();
        if (indice < 0 || indice >= categorias.size()) {
            return null;
        }
        return categorias.get(indice);
    }

    private void limpiarFormulario() {
        listaCategorias.clearSelection();
        nombreField.setText("");
        descripcionArea.setText("");
    }

    public JList<String> getListaCategorias() {
        return listaCategorias;
    }

    public JTextField getNombreField() {
        return nombreField;
    }

    public JTextArea getDescripcionArea() {
        return descripcionArea;
    }

    public JButton getRegistrarButton() {
        return registrarButton;
    }

    public JButton getModificarButton() {
        return modificarButton;
    }

    public JButton getEstadoButton() {
        return estadoButton;
    }

    public JButton getEliminarButton() {
        return eliminarButton;
    }
}
