package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.service.CarteraActivoService;
import ar.com.agmilevecich.sofp.service.CuentaService;
import ar.com.agmilevecich.sofp.service.MovimientoService;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.util.Objects;

/** Ventana principal y shell de navegación de SOFP. */
public class MainFrame extends JFrame {

    private static final String INICIO = "inicio";
    private static final String CUENTAS = "cuentas";
    private static final String MOVIMIENTOS = "movimientos";
    private static final String INVERSIONES = "inversiones";
    private static final String REPORTES = "reportes";

    private final CardLayout cardLayout;
    private final JPanel areaCentral;
    private final CuentasPanel cuentasPanel;
    private final MovimientoService movimientoService;
    private final CarteraActivoService carteraActivoService;
    private final PerfilFinanciero perfilFinanciero;
    private final Long usuarioId;
    private MovimientosPanel movimientosPanel;

    public MainFrame() {
        this(null, null, null, null, null);
    }

    /**
     * Constructor para ejecutar el shell con el contexto del usuario actual.
     * La UI recibe el servicio ya construido; no crea repositorios ni duplica
     * reglas de negocio.
     */
    public MainFrame(CuentaService cuentaService,
                     Long perfilFinancieroId,
                     Long usuarioId) {
        this(cuentaService, null, null, null, perfilFinancieroId, usuarioId);
    }

    /**
     * Constructor para ejecutar el shell con cuentas y movimientos del usuario
     * actual. Mantiene la firma utilizada por la integración de movimientos.
     */
    public MainFrame(CuentaService cuentaService,
                     MovimientoService movimientoService,
                     Long perfilFinancieroId,
                     Long usuarioId) {
        this(cuentaService, movimientoService, null, null,
                perfilFinancieroId, usuarioId);
    }

    /**
     * Constructor para ejecutar el shell con cuentas, movimientos e inversiones
     * del usuario actual.
     */
    public MainFrame(CuentaService cuentaService,
                     MovimientoService movimientoService,
                     CarteraActivoService carteraActivoService,
                     PerfilFinanciero perfilFinanciero,
                     Long usuarioId) {
        this(cuentaService, movimientoService, carteraActivoService,
                perfilFinanciero, perfilFinanciero != null ? perfilFinanciero.getId() : null, usuarioId);
    }

    /**
     * Constructor interno completo para mantener el contexto ya resuelto.
     */
    private MainFrame(CuentaService cuentaService,
                      MovimientoService movimientoService,
                      CarteraActivoService carteraActivoService,
                      PerfilFinanciero perfilFinanciero,
                      Long perfilFinancieroId,
                      Long usuarioId) {
        super("SOFP - Sistema Operativo Financiero Personal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        if (cuentaService == null) {
            if (movimientoService != null
                    || carteraActivoService != null
                    || perfilFinanciero != null
                    || perfilFinancieroId != null
                    || usuarioId != null) {
                throw new IllegalArgumentException(
                        "El CuentaService es obligatorio cuando se informa el contexto de usuario"
                );
            }
            this.cuentasPanel = new CuentasPanel();
            this.movimientoService = null;
            this.carteraActivoService = null;
            this.perfilFinanciero = null;
            this.usuarioId = null;
        } else {
            this.cuentasPanel = new CuentasPanel(
                    cuentaService,
                    Objects.requireNonNull(
                            perfilFinancieroId,
                            "El id del perfil financiero es obligatorio"
                    ),
                    Objects.requireNonNull(
                            usuarioId,
                            "El id del usuario es obligatorio"
                    )
            );
            this.movimientoService = movimientoService;
            this.carteraActivoService = carteraActivoService;
            this.perfilFinanciero = perfilFinanciero;
            this.usuarioId = usuarioId;
        }

        cardLayout = new CardLayout();
        areaCentral = new JPanel(cardLayout);
        areaCentral.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        areaCentral.add(new InicioPanel(), INICIO);
        areaCentral.add(cuentasPanel, CUENTAS);

        movimientosPanel = new MovimientosPanel();
        areaCentral.add(movimientosPanel, MOVIMIENTOS);

        if (carteraActivoService != null && perfilFinanciero != null) {
            areaCentral.add(new InversionesPanel(
                    carteraActivoService,
                    perfilFinanciero,
                    usuarioId
            ), INVERSIONES);
            areaCentral.add(new ReportesPanel(
                    carteraActivoService,
                    perfilFinanciero,
                    usuarioId
            ), REPORTES);
        } else {
            areaCentral.add(new InversionesPanel(), INVERSIONES);
            areaCentral.add(new ReportesPanel(), REPORTES);
        }

        JPanel content = new JPanel(new BorderLayout());
        content.add(new HeaderPanel(), BorderLayout.NORTH);
        content.add(new SidebarPanel(this::navegar), BorderLayout.WEST);
        content.add(areaCentral, BorderLayout.CENTER);
        content.add(new StatusBarPanel(), BorderLayout.SOUTH);
        setContentPane(content);
    }

    private void navegar(ActionEvent evento) {
        if (MOVIMIENTOS.equals(evento.getActionCommand())) {
            mostrarMovimientosDeCuentaSeleccionada();
            return;
        }

        cardLayout.show(areaCentral, evento.getActionCommand());
    }

    private void mostrarMovimientosDeCuentaSeleccionada() {
        if (movimientoService != null) {
            Cuenta cuenta = cuentasPanel.getCuentaSeleccionada();
            if (cuenta != null) {
                areaCentral.remove(movimientosPanel);
                movimientosPanel = new MovimientosPanel(
                        movimientoService,
                        cuenta.getId(),
                        usuarioId
                );
                areaCentral.add(movimientosPanel, MOVIMIENTOS);
                areaCentral.revalidate();
                areaCentral.repaint();
            }
        }

        cardLayout.show(areaCentral, MOVIMIENTOS);
    }
}
