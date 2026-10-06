package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.domain.Activo;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.service.CarteraActivoService;
import ar.com.agmilevecich.sofp.service.CategoriaService;
import ar.com.agmilevecich.sofp.service.CuentaService;
import ar.com.agmilevecich.sofp.service.CotizacionActivoService;
import ar.com.agmilevecich.sofp.service.GastoService;
import ar.com.agmilevecich.sofp.service.IngresoService;
import ar.com.agmilevecich.sofp.service.InstitucionFinancieraService;
import ar.com.agmilevecich.sofp.service.MonedaService;
import ar.com.agmilevecich.sofp.service.MovimientoService;
import ar.com.agmilevecich.sofp.service.ObligacionService;
import ar.com.agmilevecich.sofp.service.OperacionFinancieraService;
import ar.com.agmilevecich.sofp.service.PagoTarjetaService;
import ar.com.agmilevecich.sofp.service.RefinanciacionService;
import ar.com.agmilevecich.sofp.service.PatrimonioFinancieroService;
import ar.com.agmilevecich.sofp.service.ResultadoFinancieroService;
import ar.com.agmilevecich.sofp.service.TipoCambioService;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

public class MainFrame extends JFrame {
    private static final String INICIO = "inicio";
    private static final String CUENTAS = "cuentas";
    private static final String INSTITUCIONES = "instituciones";
    private static final String CATEGORIAS = "categorias";
    private static final String INGRESOS = "ingresos";
    private static final String GASTOS = "gastos";
    private static final String MOVIMIENTOS = "movimientos";
    private static final String OBLIGACIONES = "obligaciones";
    private static final String TARJETAS = "tarjetas";
    private static final String INVERSIONES = "inversiones";
    private static final String REPORTES = "reportes";
    private static final String TRANSFERENCIAS = "transferencias";

    private final CardLayout cardLayout;
    private final JPanel areaCentral;
    private final CuentasPanel cuentasPanel;
    private final InstitucionesFinancierasPanel institucionesFinancierasPanel;
    private final GastosPanel gastosPanel;
    private final IngresosPanel ingresosPanel;
    private final TarjetasCreditoPanel tarjetasPanel;
    private final MovimientoService movimientoService;
    private final CategoriaService categoriaService;
    private final IngresoService ingresoService;
    private final GastoService gastoService;
    private final CarteraActivoService carteraActivoService;
    private final PerfilFinanciero perfilFinanciero;
    private final Long usuarioId;
    private final TipoCambioService tipoCambioService;
    private final PatrimonioFinancieroService patrimonioFinancieroService;
    private MovimientosPanel movimientosPanel;
    private ReportesPanel reportesPanel;

    public MainFrame() {
        this(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    public MainFrame(CuentaService cuentaService, Long perfilFinancieroId, Long usuarioId) {
        this(cuentaService, null, null, null, null, null, null,
                perfilFinancieroId, usuarioId, null, null, null, null, null, null, null, null, null);
    }

    public MainFrame(CuentaService cuentaService, MovimientoService movimientoService,
                     Long perfilFinancieroId, Long usuarioId) {
        this(cuentaService, movimientoService, null, null, null, null, null,
                perfilFinancieroId, usuarioId, null, null, null, null, null, null, null, null, null);
    }

    public MainFrame(CuentaService cuentaService, MovimientoService movimientoService,
                     CarteraActivoService carteraActivoService, PerfilFinanciero perfilFinanciero,
                     Long usuarioId) {
        this(cuentaService, movimientoService, null, null, null, carteraActivoService,
                perfilFinanciero, perfilFinanciero != null ? perfilFinanciero.getId() : null,
                usuarioId, null, null, null, null, null, null, null, null, null);
    }

    public MainFrame(CuentaService cuentaService, MovimientoService movimientoService,
                     CategoriaService categoriaService, CarteraActivoService carteraActivoService,
                     PerfilFinanciero perfilFinanciero, Long usuarioId) {
        this(cuentaService, movimientoService, categoriaService, null, null, carteraActivoService,
                perfilFinanciero, perfilFinanciero != null ? perfilFinanciero.getId() : null,
                usuarioId, null, null, null, null, null, null, null, null, null);
    }

    public MainFrame(CuentaService cuentaService, MovimientoService movimientoService,
                     CategoriaService categoriaService,
                     InstitucionFinancieraService institucionFinancieraService,
                     MonedaService monedaService, CarteraActivoService carteraActivoService,
                     PerfilFinanciero perfilFinanciero, Long usuarioId) {
        this(cuentaService, movimientoService, categoriaService, institucionFinancieraService,
                monedaService, carteraActivoService, perfilFinanciero,
                perfilFinanciero != null ? perfilFinanciero.getId() : null, usuarioId,
                null, null, null, null, null, null, null, null, null);
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
                obligacionService, null, null, null, null, null, null, null, null);
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
                obligacionService, operacionFinancieraService, null, null, null, null, null, null, null);
    }

    public MainFrame(CuentaService cuentaService, MovimientoService movimientoService,
                     CategoriaService categoriaService,
                     InstitucionFinancieraService institucionFinancieraService,
                     MonedaService monedaService, CarteraActivoService carteraActivoService,
                     PerfilFinanciero perfilFinanciero, Long usuarioId,
                     ObligacionService obligacionService,
                     OperacionFinancieraService operacionFinancieraService,
                     PagoTarjetaService pagoTarjetaService) {
        this(cuentaService, movimientoService, categoriaService, institucionFinancieraService,
                monedaService, carteraActivoService, perfilFinanciero,
                perfilFinanciero != null ? perfilFinanciero.getId() : null, usuarioId,
                obligacionService, operacionFinancieraService, pagoTarjetaService, null, null, null, null, null, null);
    }

    public MainFrame(CuentaService cuentaService, MovimientoService movimientoService,
                     CategoriaService categoriaService,
                     InstitucionFinancieraService institucionFinancieraService,
                     MonedaService monedaService, CarteraActivoService carteraActivoService,
                     PerfilFinanciero perfilFinanciero, Long usuarioId,
                     ObligacionService obligacionService,
                     OperacionFinancieraService operacionFinancieraService,
                     PagoTarjetaService pagoTarjetaService,
                     TipoCambioService tipoCambioService) {
        this(cuentaService, movimientoService, categoriaService, institucionFinancieraService,
                monedaService, carteraActivoService, perfilFinanciero,
                perfilFinanciero != null ? perfilFinanciero.getId() : null, usuarioId,
                obligacionService, operacionFinancieraService, pagoTarjetaService,
                tipoCambioService, null, null, null, null, null);
    }

    public MainFrame(CuentaService cuentaService, MovimientoService movimientoService,
                     CategoriaService categoriaService,
                     InstitucionFinancieraService institucionFinancieraService,
                     MonedaService monedaService, CarteraActivoService carteraActivoService,
                     PerfilFinanciero perfilFinanciero, Long usuarioId,
                     ObligacionService obligacionService,
                     OperacionFinancieraService operacionFinancieraService,
                     PagoTarjetaService pagoTarjetaService,
                     TipoCambioService tipoCambioService,
                     PatrimonioFinancieroService patrimonioFinancieroService) {
        this(cuentaService, movimientoService, categoriaService, institucionFinancieraService,
                monedaService, carteraActivoService, perfilFinanciero,
                perfilFinanciero != null ? perfilFinanciero.getId() : null, usuarioId,
                obligacionService, operacionFinancieraService, pagoTarjetaService,
                tipoCambioService, patrimonioFinancieroService, null, null, null, null);
    }

    public MainFrame(CuentaService cuentaService, MovimientoService movimientoService,
                     CategoriaService categoriaService,
                     InstitucionFinancieraService institucionFinancieraService,
                     MonedaService monedaService, CarteraActivoService carteraActivoService,
                     PerfilFinanciero perfilFinanciero, Long usuarioId,
                     ObligacionService obligacionService,
                     OperacionFinancieraService operacionFinancieraService,
                     PagoTarjetaService pagoTarjetaService,
                     TipoCambioService tipoCambioService,
                     PatrimonioFinancieroService patrimonioFinancieroService,
                     CotizacionActivoService cotizacionActivoService) {
        this(cuentaService, movimientoService, categoriaService, institucionFinancieraService,
                monedaService, carteraActivoService, perfilFinanciero,
                perfilFinanciero != null ? perfilFinanciero.getId() : null, usuarioId,
                obligacionService, operacionFinancieraService, pagoTarjetaService,
                tipoCambioService, patrimonioFinancieroService, cotizacionActivoService, null, null, null);
    }

    public MainFrame(CuentaService cuentaService, MovimientoService movimientoService,
                     CategoriaService categoriaService,
                     InstitucionFinancieraService institucionFinancieraService,
                     MonedaService monedaService, CarteraActivoService carteraActivoService,
                     PerfilFinanciero perfilFinanciero, Long usuarioId,
                     ObligacionService obligacionService,
                     OperacionFinancieraService operacionFinancieraService,
                     PagoTarjetaService pagoTarjetaService,
                     TipoCambioService tipoCambioService,
                     PatrimonioFinancieroService patrimonioFinancieroService,
                     CotizacionActivoService cotizacionActivoService,
                     ResultadoFinancieroService resultadoFinancieroService,
                     java.time.LocalDate fechaDesde,
                     java.time.LocalDate fechaHasta) {
        this(cuentaService, movimientoService, categoriaService, institucionFinancieraService,
                monedaService, carteraActivoService, perfilFinanciero,
                perfilFinanciero != null ? perfilFinanciero.getId() : null, usuarioId,
                obligacionService, operacionFinancieraService, pagoTarjetaService,
                tipoCambioService, patrimonioFinancieroService, cotizacionActivoService,
                resultadoFinancieroService, fechaDesde, fechaHasta, null);
    }

    public MainFrame(CuentaService cuentaService, MovimientoService movimientoService,
                     CategoriaService categoriaService,
                     InstitucionFinancieraService institucionFinancieraService,
                     MonedaService monedaService, CarteraActivoService carteraActivoService,
                     PerfilFinanciero perfilFinanciero, Long usuarioId,
                     ObligacionService obligacionService,
                     OperacionFinancieraService operacionFinancieraService,
                     PagoTarjetaService pagoTarjetaService,
                     TipoCambioService tipoCambioService,
                     PatrimonioFinancieroService patrimonioFinancieroService,
                     CotizacionActivoService cotizacionActivoService,
                     ResultadoFinancieroService resultadoFinancieroService,
                     java.time.LocalDate fechaDesde,
                     java.time.LocalDate fechaHasta,
                     RefinanciacionService refinanciacionService) {
        this(cuentaService, movimientoService, categoriaService, institucionFinancieraService,
                monedaService, carteraActivoService, perfilFinanciero,
                perfilFinanciero != null ? perfilFinanciero.getId() : null, usuarioId,
                obligacionService, operacionFinancieraService, pagoTarjetaService,
                tipoCambioService, patrimonioFinancieroService, cotizacionActivoService,
                resultadoFinancieroService, fechaDesde, fechaHasta, refinanciacionService);
    }

    private MainFrame(CuentaService cuentaService, MovimientoService movimientoService,
                      CategoriaService categoriaService,
                      InstitucionFinancieraService institucionFinancieraService,
                      MonedaService monedaService, CarteraActivoService carteraActivoService,
                      PerfilFinanciero perfilFinanciero, Long perfilFinancieroId, Long usuarioId,
                      ObligacionService obligacionService,
                      OperacionFinancieraService operacionFinancieraService,
                      PagoTarjetaService pagoTarjetaService,
                      TipoCambioService tipoCambioService,
                      PatrimonioFinancieroService patrimonioFinancieroService,
                      CotizacionActivoService cotizacionActivoService,
                      ResultadoFinancieroService resultadoFinancieroService,
                      java.time.LocalDate fechaDesde,
                      java.time.LocalDate fechaHasta,
                      RefinanciacionService refinanciacionService) {
        super("SOFP - Sistema Operativo Financiero Personal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        this.tipoCambioService = tipoCambioService;
        this.patrimonioFinancieroService = patrimonioFinancieroService;

        if (cuentaService == null) {
            if (movimientoService != null || categoriaService != null || institucionFinancieraService != null
                    || monedaService != null || carteraActivoService != null || perfilFinanciero != null
                    || perfilFinancieroId != null || usuarioId != null || obligacionService != null
                    || operacionFinancieraService != null || pagoTarjetaService != null || tipoCambioService != null
                    || patrimonioFinancieroService != null || cotizacionActivoService != null
                    || resultadoFinancieroService != null || fechaDesde != null || fechaHasta != null) {
                throw new IllegalArgumentException("El CuentaService es obligatorio cuando se informa el contexto de usuario");
            }
            this.cuentasPanel = new CuentasPanel();
            this.institucionesFinancierasPanel = null;
            this.gastosPanel = new GastosPanel();
            this.ingresosPanel = new IngresosPanel();
            this.tarjetasPanel = new TarjetasCreditoPanel();
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
            this.institucionesFinancierasPanel = institucionFinancieraService != null
                    ? new InstitucionesFinancierasPanel(institucionFinancieraService, usuarioId)
                    : null;

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
            if (obligacionService != null && pagoTarjetaService != null && categoriaService != null
                    && perfilFinancieroId != null && usuarioId != null) {
                this.tarjetasPanel = new TarjetasCreditoPanel(
                        cuentaService, obligacionService, pagoTarjetaService,
                        categoriaService, perfilFinancieroId, usuarioId
                );
            } else {
                this.tarjetasPanel = new TarjetasCreditoPanel();
            }
        }

        cardLayout = new CardLayout();
        areaCentral = new JPanel(cardLayout);
        areaCentral.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        areaCentral.add(new InicioPanel(), INICIO);
        areaCentral.add(cuentasPanel, CUENTAS);
        areaCentral.add(institucionesFinancierasPanel != null
                ? institucionesFinancierasPanel : new JPanel(), INSTITUCIONES);
        areaCentral.add(categoriaService != null && perfilFinanciero != null && usuarioId != null
                ? new CategoriasPanel(categoriaService, perfilFinanciero, usuarioId) : new CategoriasPanel(), CATEGORIAS);
        areaCentral.add(ingresosPanel, INGRESOS);
        areaCentral.add(gastosPanel, GASTOS);
        areaCentral.add(new MovimientosPanel(), MOVIMIENTOS);
        areaCentral.add(tarjetasPanel, TARJETAS);
        areaCentral.add(obligacionService != null && usuarioId != null
                ? new ObligacionesPanel(obligacionService, pagoTarjetaService, cuentaService, categoriaService,
                perfilFinancieroId, usuarioId, tipoCambioService, refinanciacionService)
                : new ObligacionesPanel(), OBLIGACIONES);

        if (carteraActivoService != null && perfilFinanciero != null && usuarioId != null) {
            if (patrimonioFinancieroService != null) {
                areaCentral.add(new InversionesPanel(
                        carteraActivoService,
                        perfilFinanciero,
                        usuarioId,
                        this::actualizarPreciosActivos,
                        cotizacionActivoService
                ), INVERSIONES);
            } else {
                areaCentral.add(new InversionesPanel(
                        carteraActivoService,
                        perfilFinanciero,
                        usuarioId
                ), INVERSIONES);
            }
        } else {
            areaCentral.add(new InversionesPanel(), INVERSIONES);
        }

        if (patrimonioFinancieroService != null && resultadoFinancieroService != null
                && perfilFinanciero != null && usuarioId != null
                && fechaDesde != null && fechaHasta != null) {
            reportesPanel = new ReportesPanel(
                    patrimonioFinancieroService,
                    resultadoFinancieroService,
                    perfilFinanciero,
                    usuarioId,
                    fechaDesde,
                    fechaHasta
            );
            areaCentral.add(reportesPanel, REPORTES);
        } else if (patrimonioFinancieroService != null && perfilFinanciero != null && usuarioId != null) {
            reportesPanel = new ReportesPanel(patrimonioFinancieroService, perfilFinanciero, usuarioId);
            areaCentral.add(reportesPanel, REPORTES);
        } else if (carteraActivoService != null && perfilFinanciero != null && usuarioId != null) {
            reportesPanel = new ReportesPanel(carteraActivoService, perfilFinanciero, usuarioId);
            areaCentral.add(reportesPanel, REPORTES);
        } else {
            reportesPanel = new ReportesPanel();
            areaCentral.add(reportesPanel, REPORTES);
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

    public void actualizarPreciosActivos(Map<Activo, BigDecimal> preciosActuales) {
        Objects.requireNonNull(preciosActuales, "Los precios actuales son obligatorios");
        if (patrimonioFinancieroService == null || reportesPanel == null) {
            throw new IllegalStateException("El MainFrame no tiene contexto patrimonial");
        }
        reportesPanel.actualizarPatrimonio(preciosActuales);
        cardLayout.show(areaCentral, REPORTES);
    }

    private void navegar(ActionEvent event) {
        String destino = event.getActionCommand();
        if (MOVIMIENTOS.equals(destino)) {
            mostrarMovimientos();
            return;
        }
        if (CUENTAS.equals(destino)) {
            cuentasPanel.actualizarInstituciones();
        }
        if (INGRESOS.equals(destino)) {
            ingresosPanel.actualizarCuentasYCategorias();
        }
        if (GASTOS.equals(destino)) {
            gastosPanel.actualizarCuentasYCategorias();
        }
        if (REPORTES.equals(destino)) {
            reportesPanel.actualizar();
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
        movimientosPanel = new MovimientosPanel(movimientoService, cuentaSeleccionada.getId(), usuarioId);
        areaCentral.add(movimientosPanel, MOVIMIENTOS);
        cardLayout.show(areaCentral, MOVIMIENTOS);
    }
}
