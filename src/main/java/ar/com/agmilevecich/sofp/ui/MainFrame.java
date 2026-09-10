package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.service.CarteraActivoService;
import ar.com.agmilevecich.sofp.service.CategoriaService;
import ar.com.agmilevecich.sofp.service.CuentaService;
import ar.com.agmilevecich.sofp.service.GastoService;
import ar.com.agmilevecich.sofp.service.IngresoService;
import ar.com.agmilevecich.sofp.service.InstitucionFinancieraService;
import ar.com.agmilevecich.sofp.service.MonedaService;
import ar.com.agmilevecich.sofp.service.MovimientoService;
import ar.com.agmilevecich.sofp.service.ObligacionService;
import ar.com.agmilevecich.sofp.service.OperacionFinancieraService;

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
    private static final String INGRESOS = "ingresos";
    private static final String GASTOS = "gastos";
    private static final String MOVIMIENTOS = "movimientos";
    private static final String OBLIGACIONES = "obligaciones";
    private static final String INVERSIONES = "inversiones";
    private static final String REPORTES = "reportes";
    private static final String TRANSFERENCIAS = "transferencias";

    private final CardLayout cardLayout;
    private final JPanel areaCentral;
    private final CuentasPanel cuentasPanel;
    private final GastosPanel gastosPanel;
    private final IngresosPanel ingresosPanel;
    private final MovimientoService movimientoService;
    private final CategoriaService categoriaService;
    private final IngresoService ingresoService;
    private final GastoService gastoService;
    private final CarteraActivoService carteraActivoService;
    private final PerfilFinanciero perfilFinanciero;
    private final Long usuarioId;
    private MovimientosPanel movimientosPanel;

    public MainFrame() {
        this(null, null, null, null, null, null, null, null, null, null, null);
    }

    public MainFrame(CuentaService cuentaService, Long perfilFinancieroId, Long usuarioId) {
        this(cuentaService, null, null, null, null, null, null,
                perfilFinancieroId, usuarioId, null, null);
    }

    public MainFrame(CuentaService cuentaService, MovimientoService movimientoService,
                     Long perfilFinancieroId, Long usuarioId) {
        this(cuentaService, movimientoService, null, null, null, null, null,
                perfilFinancieroId, usuarioId, null, null);
    }

    public MainFrame(CuentaService cuentaService, MovimientoService movimientoService,
                     CarteraActivoService carteraActivoService, PerfilFinanciero perfilFinanciero,
                     Long usuarioId) {
        this(cuentaService, movimientoService, null, null, null, carteraActivoService,
                perfilFinanciero, perfilFinanciero != null ? perfilFinanciero.getId() : null, usuarioId, null, null);
    }

    public MainFrame(CuentaService cuentaService, MovimientoService movimientoService,
                     CategoriaService categoriaService, CarteraActivoService carteraActivoService,
                     PerfilFinanciero perfilFinanciero, Long usuarioId) {
        this(cuentaService, movimientoService, categoriaService, null, null, carteraActivoService,
                perfilFinanciero, perfilFinanciero != null ? perfilFinanciero.getId() : null, usuarioId, null, null);
    }

    public MainFrame(CuentaService cuentaService, MovimientoService movimientoService,
                     CategoriaService categoriaService,
                     InstitucionFinancieraService institucionFinancieraService,
                     MonedaService monedaService, CarteraActivoService carteraActivoService,
                     PerfilFinanciero perfilFinanciero, Long usuarioId) {
        this(cuentaService, movimientoService, categoriaService, institucionFinancieraService,
                monedaService, carteraActivoService, perfilFinanciero,
                perfilFinanciero != null ? perfilFinanciero.getId() : null, usuarioId, null, null);
    }

    public MainFrame(CuentaService cuentaService, MovimientoService movimientoService,
                     CategoriaService categoriaService,
                     InstitucionFinancieraService institucionFinancieraService,
                     MonedaService monedaService, CarteraActivoService carteraActivoService,
                     PerfilFinanciero perfilFinanciero, Long usuarioId,
                     ObligacionService obligacionService) {
        this(cuentaService, movimientoService, categoriaService, institucionFinancieraService,
                monedaService, carteraActivoService, perfilFinanciero,
                perfilFinanciero != null ? perfilFinanciero.getId() : null, usuarioId,
                obligacionService, null);
    }

    public MainFrame(CuentaService cuentaService, MovimientoService movimientoService,
                     CategoriaService categoriaService,
                     InstitucionFinancieraService institucionFinancieraService,
                     MonedaService monedaService, CarteraActivoService carteraActivoService,
                     PerfilFinanciero perfilFinanciero, Long usuarioId,
                     ObligacionService obligacionService,
                     OperacionFinancieraService operacionFinancieraService) {
        this(cuentaService, movimientoService, categoriaService, institucionFinancieraService,
                monedaService, carteraActivoService, perfilFinanciero,
                perfilFinanciero != null ? perfilFinanciero.getId() : null, usuarioId,
                obligacionService, operacionFinancieraService);
    }

    private MainFrame(CuentaService cuentaService, MovimientoService movimientoService,
                      CategoriaService categoriaService,
                      InstitucionFinancieraService institucionFinancieraService,
                      MonedaService monedaService, CarteraActivoService carteraActivoService,
                      PerfilFinanciero perfilFinanciero, Long perfilFinancieroId, Long usuarioId,
                      ObligacionService obligacionService,
                      OperacionFinancieraService operacionFinancieraService) {
        super("SOFP - Sistema Operativo Financiero Personal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        if (cuentaService == null) {
            if (movimientoService != null || categoriaService != null || institucionFinancieraService != null
                    || monedaService != null || carteraActivoService != null || perfilFinanciero != null
                    || perfilFinancieroId != null || usuarioId != null || obligacionService != null
                    || operacionFinancieraService != null) {
                throw new IllegalArgumentException("El CuentaService es obligatorio cuando se informa el contexto de usuario");
            }
            this.cuentasPanel = new CuentasPanel();
            this.gastosPanel = new GastosPanel();
            this.ingresosPanel = new IngresosPanel();
            this.movimientoService = null;
            this.categoriaService = null;
            this.ingresoService = null;
            this.gastoService = null;
            this.carteraActivoService = null;
            this.perfilFinanciero = null;
            this.usuarioId = null;
        } else {
            this.movimientoService = movimientoService;
            this.categoriaService = categoriaService;
            this.ingresoService = movimientoService != null ? new IngresoService(movimientoService) : null;
            this.gastoService = movimientoService != null
                    ? (obligacionService != null ? new GastoService(movimientoService, obligacionService)
                    : new GastoService(movimientoService)) : null;
            this.carteraActivoService = carteraActivoService;
            this.perfilFinanciero = perfilFinanciero;
            this.usuarioId = Objects.requireNonNull(usuarioId, "usuarioId");
            if (institucionFinancieraService == null && monedaService == null && perfilFinanciero == null) {
                this.cuentasPanel = new CuentasPanel(cuentaService, perfilFinancieroId, usuarioId);
            } else {
                this.cuentasPanel = new CuentasPanel(cuentaService, institucionFinancieraService,
                        monedaService, perfilFinanciero, usuarioId);
            }
            if (gastoService != null && categoriaService != null && perfilFinanciero != null && usuarioId != null) {
                this.gastosPanel = new GastosPanel(gastoService, cuentaService, categoriaService,
                        perfilFinanciero.getId(), usuarioId);
            } else {
                this.gastosPanel = new GastosPanel();
            }
            if (ingresoService != null && categoriaService != null && perfilFinanciero != null && usuarioId != null) {
                this.ingresosPanel = new IngresosPanel(ingresoService, cuentaService, categoriaService,
                        perfilFinanciero.getId(), usuarioId);
            } else {
                this.ingresosPanel = new IngresosPanel();
            }
        }

        cardLayout = new CardLayout();
        areaCentral = new JPanel(cardLayout);
        areaCentral.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        areaCentral.add(new InicioPanel(), INICIO);
        areaCentral.add(cuentasPanel, CUENTAS);
        areaCentral.add(categoriaService != null && perfilFinanciero != null && usuarioId != null
                ? new CategoriasPanel(categoriaService, perfilFinanciero, usuarioId) : new CategoriasPanel(), CATEGORIAS);
        areaCentral.add(ingresosPanel, INGRESOS);
        areaCentral.add(gastosPanel, GASTOS);
        areaCentral.add(new MovimientosPanel(), MOVIMIENTOS);
        areaCentral.add(obligacionService != null && usuarioId != null
                ? new ObligacionesPanel(obligacionService, usuarioId) : new ObligacionesPanel(), OBLIGACIONES);
        if (carteraActivoService != null && perfilFinanciero != null && usuarioId != null) {
            areaCentral.add(new InversionesPanel(carteraActivoService, perfilFinanciero, usuarioId), INVERSIONES);
            areaCentral.add(new ReportesPanel(carteraActivoService, perfilFinanciero, usuarioId), REPORTES);
        } else {
            areaCentral.add(new InversionesPanel(), INVERSIONES);
            areaCentral.add(new ReportesPanel(), REPORTES);
        }
        if (operacionFinancieraService != null && cuentaService != null && categoriaService != null
                && perfilFinanciero != null && usuarioId != null) {
            areaCentral.add(new TransferenciasPanel(operacionFinancieraService, cuentaService, categoriaService,
                    perfilFinanciero.getId(), usuarioId), TRANSFERENCIAS);
        } else {
            areaCentral.add(new TransferenciasPanel(), TRANSFERENCIAS);
        }

        setLayout(new BorderLayout());
        add(new HeaderPanel(), BorderLayout.NORTH);
        add(new SidebarPanel(this::navegar), BorderLayout.WEST);
        add(new StatusBarPanel(), BorderLayout.SOUTH);
        add(areaCentral, BorderLayout.CENTER);
    }

    private void navegar(ActionEvent event) {
        String destino = event.getActionCommand();
        if (MOVIMIENTOS.equals(destino)) {
            mostrarMovimientos();
            return;
        }
        if (INGRESOS.equals(destino)) {
            ingresosPanel.actualizarCuentasYCategorias();
        }
        if (GASTOS.equals(destino)) {
            gastosPanel.actualizarCuentas();
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
        movimientosPanel = new MovimientosPanel(movimientoService, categoriaService, cuentaSeleccionada, usuarioId);
        areaCentral.add(movimientosPanel, MOVIMIENTOS);
        cardLayout.show(areaCentral, MOVIMIENTOS);
    }
}
