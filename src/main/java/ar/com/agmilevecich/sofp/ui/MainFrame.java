package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.service.CarteraActivoService;
import ar.com.agmilevecich.sofp.service.CategoriaService;
import ar.com.agmilevecich.sofp.service.CuentaService;
import ar.com.agmilevecich.sofp.service.GastoService;
import ar.com.agmilevecich.sofp.service.InstitucionFinancieraService;
import ar.com.agmilevecich.sofp.service.MonedaService;
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
    private static final String CATEGORIAS = "categorias";
    private static final String GASTOS = "gastos";
    private static final String MOVIMIENTOS = "movimientos";
    private static final String INVERSIONES = "inversiones";
    private static final String REPORTES = "reportes";

    private final CardLayout cardLayout;
    private final JPanel areaCentral;
    private final CuentasPanel cuentasPanel;
    private final MovimientoService movimientoService;
    private final CategoriaService categoriaService;
    private final GastoService gastoService;
    private final CarteraActivoService carteraActivoService;
    private final PerfilFinanciero perfilFinanciero;
    private final Long usuarioId;
    private MovimientosPanel movimientosPanel;

    public MainFrame() {
        this(null, null, null, null, null, null, null, null, null);
    }

    /** Constructor para ejecutar el shell con el contexto del usuario actual. */
    public MainFrame(CuentaService cuentaService,
                     Long perfilFinancieroId,
                     Long usuarioId) {
        this(cuentaService, null, null, null, null, null, null,
                perfilFinancieroId, usuarioId);
    }

    /** Constructor para ejecutar el shell con cuentas y movimientos del usuario actual. */
    public MainFrame(CuentaService cuentaService,
                     MovimientoService movimientoService,
                     Long perfilFinancieroId,
                     Long usuarioId) {
        this(cuentaService, movimientoService, null, null, null, null, null,
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
        this(cuentaService, movimientoService, null, null, null, carteraActivoService,
                perfilFinanciero, perfilFinanciero != null ? perfilFinanciero.getId() : null, usuarioId);
    }

    /** Constructor para ejecutar el shell con alta de movimientos, cuentas e inversiones. */
    public MainFrame(CuentaService cuentaService,
                     MovimientoService movimientoService,
                     CategoriaService categoriaService,
                     CarteraActivoService carteraActivoService,
                     PerfilFinanciero perfilFinanciero,
                     Long usuarioId) {
        this(cuentaService, movimientoService, categoriaService,
                null, null, carteraActivoService,
                perfilFinanciero, perfilFinanciero != null ? perfilFinanciero.getId() : null,
                usuarioId);
    }

    /** Constructor para ejecutar el shell con alta de cuentas y movimientos. */
    public MainFrame(CuentaService cuentaService,
                     MovimientoService movimientoService,
                     CategoriaService categoriaService,
                     InstitucionFinancieraService institucionFinancieraService,
                     MonedaService monedaService,
                     CarteraActivoService carteraActivoService,
                     PerfilFinanciero perfilFinanciero,
                     Long usuarioId) {
        this(cuentaService, movimientoService, categoriaService,
                institucionFinancieraService, monedaService, carteraActivoService,
                perfilFinanciero, perfilFinanciero != null ? perfilFinanciero.getId() : null,
                usuarioId);
    }

    /** Constructor interno completo para mantener el contexto ya resuelto. */
    private MainFrame(CuentaService cuentaService,
                      MovimientoService movimientoService,
                      CategoriaService categoriaService,
                      InstitucionFinancieraService institucionFinancieraService,
                      MonedaService monedaService,
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
                    || categoriaService != null
                    || institucionFinancieraService != null
                    || monedaService != null
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
            this.categoriaService = null;
            this.gastoService = null;
            this.carteraActivoService = null;
            this.perfilFinanciero = null;
            this.usuarioId = null;
        } else {
            this.movimientoService = movimientoService;
            this.categoriaService = categoriaService;
            this.gastoService = movimientoService != null ? new GastoService(movimientoService) : null;
            this.carteraActivoService = carteraActivoService;
            this.perfilFinanciero = perfilFinanciero;
            this.usuarioId = Objects.requireNonNull(usuarioId, "usuarioId");
            if (institucionFinancieraService == null && monedaService == null && perfilFinanciero == null) {
                this.cuentasPanel = new CuentasPanel(
                        cuentaService,
                        perfilFinancieroId,
                        usuarioId
                );
            } else {
                this.cuentasPanel = new CuentasPanel(
                        cuentaService,
                        institucionFinancieraService,
                        monedaService,
                        perfilFinanciero,
                        usuarioId
                );
            }
        }

        cardLayout = new CardLayout();
        areaCentral = new JPanel(cardLayout);
        areaCentral.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        areaCentral.add(new InicioPanel(), INICIO);
        areaCentral.add(cuentasPanel, CUENTAS);

        if (categoriaService != null && perfilFinanciero != null && usuarioId != null) {
            areaCentral.add(
                    new CategoriasPanel(categoriaService, perfilFinanciero, usuarioId),
                    CATEGORIAS
            );
        } else {
            areaCentral.add(new CategoriasPanel(), CATEGORIAS);
        }

        if (gastoService != null && categoriaService != null && perfilFinanciero != null && usuarioId != null) {
            areaCentral.add(
                    new GastosPanel(
                            gastoService,
                            cuentaService,
                            categoriaService,
                            perfilFinanciero.getId(),
                            usuarioId
                    ),
                    GASTOS
            );
        } else {
            areaCentral.add(new GastosPanel(), GASTOS);
        }

        areaCentral.add(new MovimientosPanel(), MOVIMIENTOS);

        if (carteraActivoService != null && perfilFinanciero != null && usuarioId != null) {
            areaCentral.add(
                    new InversionesPanel(carteraActivoService, perfilFinanciero, usuarioId),
                    INVERSIONES
            );
            areaCentral.add(
                    new ReportesPanel(carteraActivoService, perfilFinanciero, usuarioId),
                    REPORTES
            );
        } else {
            areaCentral.add(new InversionesPanel(), INVERSIONES);
            areaCentral.add(new ReportesPanel(), REPORTES);
        }

        HeaderPanel headerPanel = new HeaderPanel();
        SidebarPanel sidebarPanel = new SidebarPanel(this::navegar);
        StatusBarPanel statusBarPanel = new StatusBarPanel();

        setLayout(new BorderLayout());
        add(headerPanel, BorderLayout.NORTH);
        add(sidebarPanel, BorderLayout.WEST);
        add(areaCentral, BorderLayout.CENTER);
        add(statusBarPanel, BorderLayout.SOUTH);
    }

    private void navegar(ActionEvent event) {
        String destino = event.getActionCommand();
        if (MOVIMIENTOS.equals(destino)) {
            mostrarMovimientos();
            return;
        }
        cardLayout.show(areaCentral, destino);
    }

    private void mostrarMovimientos() {
        if (movimientoService == null) {
            cardLayout.show(areaCentral, MOVIMIENTOS);
            return;
        }

        Cuenta cuentaSeleccionada = cuentasPanel.getCuentaSeleccionada();
        if (cuentaSeleccionada == null) {
            return;
        }

        movimientosPanel = new MovimientosPanel(
                movimientoService,
                categoriaService,
                cuentaSeleccionada,
                usuarioId
        );
        areaCentral.add(movimientosPanel, MOVIMIENTOS);
        cardLayout.show(areaCentral, MOVIMIENTOS);
    }
}
