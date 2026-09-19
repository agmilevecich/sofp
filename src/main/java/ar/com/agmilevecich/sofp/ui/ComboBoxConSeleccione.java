package ar.com.agmilevecich.sofp.ui;

import javax.swing.JComboBox;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import java.awt.Component;

/** JComboBox con una opción inicial real para indicar que no hay selección. */
public class ComboBoxConSeleccione<T> extends JComboBox<T> {

    private static final Object PLACEHOLDER = new Object();

    public ComboBoxConSeleccione() {
        super();
        agregarPlaceholder();
        setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list,
                                                           Object value,
                                                           int index,
                                                           boolean isSelected,
                                                           boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(esPlaceholder(value) ? "Seleccione..." : String.valueOf(value));
                return this;
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void agregarPlaceholder() {
        super.addItem((T) PLACEHOLDER);
    }

    private boolean esPlaceholder(Object value) {
        return value == null || value == PLACEHOLDER;
    }

    @Override
    public Object getSelectedItem() {
        Object seleccionado = super.getSelectedItem();
        return esPlaceholder(seleccionado) ? null : seleccionado;
    }

    @Override
    public T getItemAt(int index) {
        if (index == 0 && super.getItemAt(index) == PLACEHOLDER) {
            return null;
        }
        return super.getItemAt(index);
    }

    @Override
    public void setSelectedItem(Object item) {
        super.setSelectedItem(item == null ? PLACEHOLDER : item);
    }
}
