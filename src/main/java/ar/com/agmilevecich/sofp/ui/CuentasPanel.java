package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.service.CuentaService;
import ar.com.agmilevecich.sofp.service.InstitucionFinancieraService;
import ar.com.agmilevecich.sofp.service.MonedaService;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Panel del módulo de cuentas. */
public class CuentasPanel extends JPanel {

    private final DefaultListModel<String> modeloCuentas;
    private final JList<String> listaCuentas;
    private final List<Cuenta> cuentas;
    private final CuentaService cuentaService;
    private final InstitucionFinancieraService institucionFinancieraService;
    private final MonedaService monedaService;
    private final PerfilFinanciero perfilFinanciero;
    private final Long perfilFinancieroId;
    private final Long usuarioId;

    /** Constructor del shell sin contexto de usuario. */
    public CuentasPanel() {
        modeloCuentas = new DefaultListModel<>();
        listaCuentas = new JList<>(modeloCuentas);
        cuentas = new ArrayList<>();
        cuentaService = null;
        institucionFinancieraService = null;
        monedaService = null;
        perfilFinanciero = null;
        perfilFinancieroId = null;
        usuarioId = null;
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
        this(cuentaService, null, null, null, perfilFinancieroId, usuarioId);
    }

    /** Constructor para listar cuentas y permitir su alta desde Swing. */
    public CuentasPanel(CuentaService cuentaService,
                        InstitucionFinancieraService institucionFinancieraService,
                        MonedaService monedaService,
                        PerfilFinanciero perfilFinanciero,
                        Long usuarioId) {
        this(cuentaService, institucionFinancieraService, monedaService,
                perfilFinanciero, perfilFinanciero != null ? perfilFinanciero.getId() : null,
                usuarioId);
    }

    private CuentasPanel(CuentaService cuentaService,
                         InstitucionFinancieraService institucionFinancieraService,
                         MonedaService monedaService,
                         PerfilFinanciero perfilFinanciero,
                         Long perfilFinancieroId,
                         Long usuarioId) {
        this.cuentaService = Objects.requireNonNull(cuentaService, "El CuentaService es obligatorio");
        this.perfilFinancieroId = Objects.requireNonNull(
                perfilFinancieroId,
                "El id del perfil financiero es obligatorio"
        );
        this.usuarioId = Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");
        this.institucionFinancieraService = institucionFinancieraService;
        this.monedaService = monedaService;
        this.perfilFinanciero = perfilFinanciero;

        if ((institucionFinancieraService == null) != (monedaService == null)
                || (institucionFinancieraService != null && perfilFinanciero == null)) {
            throw new IllegalArgumentException(
                    "Los servicios de alta requieren institución financiera, moneda y perfil financiero"
            );
        }

        modeloCuentas = new DefaultListModel<>();
        listaCuentas = new JList<>(modeloCuentas);
        cuentas = new ArrayList<>();

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel titulo = new JLabel("Cuentas");
        add(titulo, BorderLayout.NORTH);

        JPanel panelLista = new JPanel(new BorderLayout());
        panelLista.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Cuentas registradas",
                TitledBorder.LEFT,
                TitledBorder.TOP
        ));
        panelLista.add(new JScrollPane(listaCuentas), BorderLayout.CENTER);
        add(panelLista, BorderLayout.CENTER);

        if (institucionFinancieraService != null) {
            JPanel panelFormulario = new JPanel(new BorderLayout());
            panelFormulario.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createEtchedBorder(),
                    "Registrar cuenta",
                    TitledBorder.LEFT,
                    TitledBorder.TOP
            ));
            panelFormulario.add(new RegistrarCuentaPanel(
                    cuentaService,
                    institucionFinancieraService,
                    monedaService,
                    perfilFinanciero,
                    usuarioId,
                    this::actualizarCuentas
            ), BorderLayout.CENTER);
            add(panelFormulario, BorderLayout.SOUTH);
        }

        actualizarCuentas();
    }

    /** Devuelve la cuenta actualmente seleccionada, o null si no hay selección. */
    public Cuenta getCuentaSeleccionada() {
        int indice = listaCuentas.getSelectedIndex();
        if (indice < 0 || indice >= cuentas.size()) {
            return null;
        }
        return cuentas.get(indice);
    }

    /** Actualiza el listado con las cuentas autorizadas del perfil actual. */
    void actualizarCuentas() {
        modeloCuentas.clear();
        cuentas.clear();
        cargarCuentas(cuentaService.listarPorPerfilFinanciero(
                perfilFinancieroId,
                usuarioId
        ));
    }

    private void cargarCuentas(List<Cuenta> cuentas) {
        this.cuentas.addAll(cuentas);
        for (Cuenta cuenta : cuentas) {
            modeloCuentas.addElement(cuenta.getNombre());
        }
    }
}
