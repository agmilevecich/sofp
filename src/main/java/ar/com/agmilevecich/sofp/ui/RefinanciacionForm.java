package ar.com.agmilevecich.sofp.ui;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/** Formulario Swing para capturar los parámetros de una refinanciación. */
public class RefinanciacionForm extends JPanel {

    private final JTextField fechaInicioField = new JTextField(10);
    private final JTextField cantidadCuotasField = new JTextField(10);
    private final JTextField interesInicialField = new JTextField(10);
    private final JTextField cargosInicialesField = new JTextField(10);
    private final JTextField tasaAnualField = new JTextField(10);
    private final JButton confirmarButton = new JButton("Confirmar");

    public RefinanciacionForm() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;

        agregarFila(c, 0, "Fecha inicio (dd/MM/uuuu)", fechaInicioField);
        agregarFila(c, 1, "Cantidad de cuotas", cantidadCuotasField);
        agregarFila(c, 2, "Interés inicial", interesInicialField);
        agregarFila(c, 3, "Cargos iniciales", cargosInicialesField);
        agregarFila(c, 4, "Tasa anual (%)", tasaAnualField);

        c.gridx = 1;
        c.gridy = 5;
        add(confirmarButton, c);
    }

    private void agregarFila(GridBagConstraints c, int fila, String texto, JTextField campo) {
        c.gridx = 0;
        c.gridy = fila;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        add(new JLabel(texto), c);

        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(campo, c);
    }

    public LocalDate getFechaInicio() {
        Objects.requireNonNull(fechaInicioField.getText(), "La fecha de inicio es obligatoria");
        return LocalDate.parse(fechaInicioField.getText().trim(),
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/uuuu"));
    }

    public int getCantidadCuotas() {
        return Integer.parseInt(cantidadCuotasField.getText().trim());
    }

    public BigDecimal getInteresInicial() {
        return new BigDecimal(interesInicialField.getText().trim());
    }

    public BigDecimal getCargosIniciales() {
        return new BigDecimal(cargosInicialesField.getText().trim());
    }

    public BigDecimal getTasaAnual() {
        return new BigDecimal(tasaAnualField.getText().trim());
    }

    public JButton getConfirmarButton() {
        return confirmarButton;
    }

    public void setFechaInicio(LocalDate fecha) {
        fechaInicioField.setText(fecha.format(
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/uuuu")));
    }

    public void setCantidadCuotas(int cantidad) {
        cantidadCuotasField.setText(Integer.toString(cantidad));
    }

    public void setInteresInicial(BigDecimal importe) {
        interesInicialField.setText(importe.toPlainString());
    }

    public void setCargosIniciales(BigDecimal importe) {
        cargosInicialesField.setText(importe.toPlainString());
    }

    public void setTasaAnual(BigDecimal tasa) {
        tasaAnualField.setText(tasa.toPlainString());
    }
}
