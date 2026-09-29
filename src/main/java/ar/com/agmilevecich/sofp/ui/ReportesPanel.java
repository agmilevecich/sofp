package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.domain.Activo;
import ar.com.agmilevecich.sofp.domain.DetalleMovimientoCarteraActivo;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.ResumenPatrimonial;
import ar.com.agmilevecich.sofp.domain.ResumenResultadoFinanciero;
import ar.com.agmilevecich.sofp.service.CarteraActivoService;
import ar.com.agmilevecich.sofp.service.PatrimonioFinancieroService;
import ar.com.agmilevecich.sofp.service.ResultadoFinancieroService;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Panel de reportes basado en los reportes de movimientos de la cartera existente. */
public class ReportesPanel extends JPanel {

    private final DefaultListModel<String> modeloReportes;
    private PatrimonioFinancieroService patrimonioFinancieroService;
    private ResultadoFinancieroService resultadoFinancieroService;
    private PerfilFinanciero perfilFinanciero;
    private Long usuarioId;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;

    /** Constructor del shell sin contexto de usuario. */
    public ReportesPanel() {
        modeloReportes = new DefaultListModel<>();
        setLayout(new BorderLayout());
        add(new JLabel("Reportes"), BorderLayout.NORTH);
        add(new JScrollPane(new JList<>(modeloReportes)), BorderLayout.CENTER);
    }

    /**
     * Constructor para mostrar los movimientos de activos del perfil del usuario autenticado.
     * La consulta pasa por CarteraActivoService, que mantiene las reglas de autorización.
     */
    public ReportesPanel(CarteraActivoService carteraActivoService,
                         PerfilFinanciero perfilFinanciero,
                         Long usuarioId) {
        Objects.requireNonNull(carteraActivoService, "El CarteraActivoService es obligatorio");
        Objects.requireNonNull(perfilFinanciero, "El perfil financiero es obligatorio");
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");

        modeloReportes = new DefaultListModel<>();
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(new JLabel("Reporte de movimientos de inversiones"), BorderLayout.NORTH);
        add(new JScrollPane(new JList<>(modeloReportes)), BorderLayout.CENTER);

        cargarMovimientos(carteraActivoService.obtenerMovimientos(perfilFinanciero, usuarioId));
    }

    /**
     * Constructor para mostrar el resultado financiero de un período del perfil.
     * El cálculo y las reglas de clasificación permanecen en ResultadoFinancieroService.
     */
    public ReportesPanel(ResultadoFinancieroService resultadoFinancieroService,
                         PerfilFinanciero perfilFinanciero,
                         Long usuarioId,
                         LocalDate fechaDesde,
                         LocalDate fechaHasta) {
        Objects.requireNonNull(resultadoFinancieroService, "El ResultadoFinancieroService es obligatorio");
        Objects.requireNonNull(perfilFinanciero, "El perfil financiero es obligatorio");
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");
        Objects.requireNonNull(fechaDesde, "La fecha desde es obligatoria");
        Objects.requireNonNull(fechaHasta, "La fecha hasta es obligatoria");

        modeloReportes = new DefaultListModel<>();
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(new JLabel("Resultado financiero"), BorderLayout.NORTH);
        add(new JScrollPane(new JList<>(modeloReportes)), BorderLayout.CENTER);

        cargarResultado(resultadoFinancieroService.calcular(
                perfilFinanciero,
                usuarioId,
                fechaDesde,
                fechaHasta
        ));
    }

    /**
     * Constructor para mostrar patrimonio y resultado financiero del perfil en un único reporte.
     * El período del resultado se recibe explícitamente desde la capa de aplicación.
     */
    public ReportesPanel(PatrimonioFinancieroService patrimonioFinancieroService,
                         ResultadoFinancieroService resultadoFinancieroService,
                         PerfilFinanciero perfilFinanciero,
                         Long usuarioId,
                         LocalDate fechaDesde,
                         LocalDate fechaHasta) {
        Objects.requireNonNull(patrimonioFinancieroService, "El PatrimonioFinancieroService es obligatorio");
        Objects.requireNonNull(resultadoFinancieroService, "El ResultadoFinancieroService es obligatorio");
        Objects.requireNonNull(perfilFinanciero, "El perfil financiero es obligatorio");
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");
        Objects.requireNonNull(fechaDesde, "La fecha desde es obligatoria");
        Objects.requireNonNull(fechaHasta, "La fecha hasta es obligatoria");

        modeloReportes = new DefaultListModel<>();
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(new JLabel("Reportes financieros consolidados"), BorderLayout.NORTH);
        add(new JScrollPane(new JList<>(modeloReportes)), BorderLayout.CENTER);

        this.patrimonioFinancieroService = patrimonioFinancieroService;
        this.resultadoFinancieroService = resultadoFinancieroService;
        this.perfilFinanciero = perfilFinanciero;
        this.usuarioId = usuarioId;
        this.fechaDesde = fechaDesde;
        this.fechaHasta = fechaHasta;

        cargarReporteConsolidado(
                patrimonioFinancieroService.calcular(perfilFinanciero, usuarioId),
                resultadoFinancieroService.calcular(perfilFinanciero, usuarioId, fechaDesde, fechaHasta)
        );
    }

    /**
     * Constructor para mostrar el patrimonio financiero consolidado del perfil.
     * La valorización y las reglas patrimoniales permanecen en PatrimonioFinancieroService.
     */
    public ReportesPanel(PatrimonioFinancieroService patrimonioFinancieroService,
                         PerfilFinanciero perfilFinanciero,
                         Long usuarioId) {
        Objects.requireNonNull(
                patrimonioFinancieroService,
                "El PatrimonioFinancieroService es obligatorio"
        );
        Objects.requireNonNull(perfilFinanciero, "El perfil financiero es obligatorio");
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");

        modeloReportes = new DefaultListModel<>();
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(new JLabel("Patrimonio financiero consolidado"), BorderLayout.NORTH);
        add(new JScrollPane(new JList<>(modeloReportes)), BorderLayout.CENTER);

        this.patrimonioFinancieroService = patrimonioFinancieroService;
        this.perfilFinanciero = perfilFinanciero;
        this.usuarioId = usuarioId;
        cargarPatrimonio(
                patrimonioFinancieroService.calcular(
                        perfilFinanciero,
                        usuarioId
                )
        );
    }

    /**
     * Constructor para mostrar el patrimonio consolidado utilizando precios
     * explícitos provistos por la capa de aplicación.
     */
    /** Actualiza el reporte utilizando precios explícitos provistos por la capa de aplicación. */
    public void actualizarPatrimonio(Map<Activo, BigDecimal> preciosActuales) {
        Objects.requireNonNull(preciosActuales, "Los precios actuales son obligatorios");
        if (patrimonioFinancieroService == null || perfilFinanciero == null || usuarioId == null) {
            throw new IllegalStateException("El panel no tiene contexto patrimonial");
        }
        modeloReportes.clear();
        ResumenPatrimonial resumenPatrimonial = patrimonioFinancieroService.calcular(
                perfilFinanciero,
                usuarioId,
                preciosActuales
        );
        if (resultadoFinancieroService != null && fechaDesde != null && fechaHasta != null) {
            cargarReporteConsolidado(
                    resumenPatrimonial,
                    resultadoFinancieroService.calcular(
                            perfilFinanciero,
                            usuarioId,
                            fechaDesde,
                            fechaHasta
                    )
            );
        } else {
            cargarPatrimonio(resumenPatrimonial);
        }
    }

    private void mostrarRequierePrecios() {
        modeloReportes.addElement("Precios actuales no provistos");
        modeloReportes.addElement("La valorización de inversiones requiere precios explícitos.");
    }

    public ReportesPanel(PatrimonioFinancieroService patrimonioFinancieroService,
                         PerfilFinanciero perfilFinanciero,
                         Long usuarioId,
                         Map<Activo, BigDecimal> preciosActuales) {
        Objects.requireNonNull(
                patrimonioFinancieroService,
                "El PatrimonioFinancieroService es obligatorio"
        );
        Objects.requireNonNull(perfilFinanciero, "El perfil financiero es obligatorio");
        Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");
        Objects.requireNonNull(preciosActuales, "Los precios actuales son obligatorios");

        modeloReportes = new DefaultListModel<>();
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(new JLabel("Patrimonio financiero consolidado"), BorderLayout.NORTH);
        add(new JScrollPane(new JList<>(modeloReportes)), BorderLayout.CENTER);

        this.patrimonioFinancieroService = patrimonioFinancieroService;
        this.perfilFinanciero = perfilFinanciero;
        this.usuarioId = usuarioId;
        cargarPatrimonio(
                patrimonioFinancieroService.calcular(
                        perfilFinanciero,
                        usuarioId,
                        preciosActuales
                )
        );
    }

    private void cargarPatrimonio(ResumenPatrimonial resumen) {
        String moneda = resumen.getMonedaPresentacion().getCodigo();

        modeloReportes.addElement("Moneda de presentación: " + moneda);
        modeloReportes.addElement("ACTIVOS");
        modeloReportes.addElement("  Activos monetarios: " + resumen.getActivosMonetarios() + " " + moneda);
        modeloReportes.addElement("  Inversiones: " + resumen.getActivosInversiones() + " " + moneda);
        modeloReportes.addElement("  Activos totales: " + resumen.getActivosTotales() + " " + moneda);
        modeloReportes.addElement("PASIVOS");
        modeloReportes.addElement("  Tarjetas y obligaciones asociadas: "
                + resumen.getPasivosTarjetas() + " " + moneda);
        modeloReportes.addElement("  Pasivos totales: " + resumen.getPasivosTotales() + " " + moneda);
        modeloReportes.addElement("PATRIMONIO NETO");
        modeloReportes.addElement("  Patrimonio neto: " + resumen.getPatrimonioNeto() + " " + moneda);
    }

    private void cargarReporteConsolidado(ResumenPatrimonial patrimonio,
                                               ResumenResultadoFinanciero resultado) {
        cargarPatrimonio(patrimonio);
        modeloReportes.addElement("RESULTADO FINANCIERO");
        cargarResultado(resultado);
    }

    private void cargarResultado(ResumenResultadoFinanciero resumen) {
        modeloReportes.addElement("Período: " + resumen.getFechaDesde() + " a " + resumen.getFechaHasta());
        modeloReportes.addElement("INGRESOS");
        resumen.getIngresos().forEach((moneda, importe) ->
                modeloReportes.addElement("  " + importe + " " + moneda.getCodigo()));
        modeloReportes.addElement("EGRESOS");
        resumen.getEgresos().forEach((moneda, importe) ->
                modeloReportes.addElement("  " + importe + " " + moneda.getCodigo()));
        modeloReportes.addElement("RESULTADO");
        java.util.LinkedHashSet<ar.com.agmilevecich.sofp.domain.Moneda> monedas =
                new java.util.LinkedHashSet<>();
        monedas.addAll(resumen.getIngresos().keySet());
        monedas.addAll(resumen.getEgresos().keySet());
        monedas.forEach(moneda ->
                modeloReportes.addElement("  " + resumen.getResultado(moneda) + " " + moneda.getCodigo()));
    }

    private void cargarMovimientos(List<DetalleMovimientoCarteraActivo> movimientos) {
        for (DetalleMovimientoCarteraActivo detalle : movimientos) {
            modeloReportes.addElement(
                    detalle.getTipoMovimiento()
                            + " - "
                            + detalle.getActivo().getSimbolo()
                            + " - "
                            + detalle.getCantidad()
                            + " - "
                            + detalle.getImporte()
            );
        }
    }
}
