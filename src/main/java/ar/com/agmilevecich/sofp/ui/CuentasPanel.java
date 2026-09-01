package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.service.CuentaService;

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

/** Panel del módulo de cuentas. */
public class CuentasPanel extends JPanel {

    private final DefaultListModel<Cuenta> modeloCuentas;
    private final JList<Cuenta> listaCuentas;

    /**
     * Constructor del shell sin contexto de usuario.
     * Mantiene el estado inicial utilizado por la navegación básica.
     */
    public CuentasPanel() {
        modeloCuentas = new DefaultListModel<>();
        listaCuentas = new JList<>(modeloCuentas);
        listaCuentas.setCellRenderer((lista, cuenta, indice, seleccionado, conFoco) ->
                new JLabel(cuenta.getNombre())
        );
        setLayout(new BorderLayout());
        add(new JLabel("Cuentas", SwingConstants.CENTER), BorderLayout.CENTER);
    }

    /**
     * Constructor para mostrar las cuentas del perfil del usuario autenticado.
     * La consulta pasa por CuentaService, que mantiene las reglas de autorización.
     */
    public CuentasPanel(CuentaService cuentaService,
                        Long perfilFinancieroId,
                        Long usuarioId) {

        Objects.requireNonNull(cuentaService, "El CuentaService es obligatorio");
        Objects.requireNonNull(perfilFinancieroId, "El id del perfil financiero es obligatorio");
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");

        modeloCuentas = new DefaultListModel<>();
        listaCuentas = new JList<>(modeloCuentas);
        listaCuentas.setCellRenderer((lista, cuenta, indice, seleccionado, conFoco) ->
                new JLabel(cuenta.getNombre())
        );

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        add(new JLabel("Cuentas"), BorderLayout.NORTH);
        add(new JScrollPane(listaCuentas), BorderLayout.CENTER);

        cargarCuentas(cuentaService.listarPorPerfilFinanciero(
                perfilFinancieroId,
                usuarioId
        ));
    }

    /** Devuelve la cuenta actualmente seleccionada, o null si no hay selección. */
    public Cuenta getCuentaSeleccionada() {
        return listaCuentas.getSelectedValue();
    }

    private void cargarCuentas(List<Cuenta> cuentas) {
        for (Cuenta cuenta : cuentas) {
            modeloCuentas.addElement(cuenta);
        }
    }
}
