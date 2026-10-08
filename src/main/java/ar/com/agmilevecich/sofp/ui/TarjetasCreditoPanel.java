package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.domain.Categoria;
import ar.com.agmilevecich.sofp.domain.Cuenta;
import ar.com.agmilevecich.sofp.domain.EstadoObligacion;
import ar.com.agmilevecich.sofp.domain.Financiacion;
import ar.com.agmilevecich.sofp.domain.Obligacion;
import ar.com.agmilevecich.sofp.service.CategoriaService;
import ar.com.agmilevecich.sofp.service.CuentaService;
import ar.com.agmilevecich.sofp.service.ObligacionService;
import ar.com.agmilevecich.sofp.service.PagoTarjetaService;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TarjetasCreditoPanel extends JPanel {

    private final CuentaService cuentaService;
    private final ObligacionService obligacionService;
    private final PagoTarjetaService pagoTarjetaService;
    private final CategoriaService categoriaService;
    private final Long perfilFinancieroId;
    private final Long usuarioId;

    private final JComboBox<Cuenta> tarjetasCombo = new JComboBox<>();
    private final JList<Obligacion> obligacionesList = new JList<>(new DefaultListModel<>());
    private final JComboBox<Cuenta> cuentaPagadoraCombo = new JComboBox<>();
    private final JComboBox<Categoria> categoriaCombo = new JComboBox<>();
    private final JComboBox<String> modalidadPagoCombo = new JComboBox<>(
            new String[]{"Pago mínimo", "Pago parcial", "Pago total"});
    private final JTextField importeField = new JTextField(12);
    private final JLabel limiteLabel = new JLabel();
    private final JLabel disponibleLabel = new JLabel();
    private final JLabel consumidoLabel = new JLabel();
    private final JLabel totalResumenLabel = new JLabel();
    private final JLabel pagoMinimoLabel = new JLabel();
    private final JLabel saldoTotalLabel = new JLabel();
    private final JLabel cicloLabel = new JLabel();
    private final JLabel vencimientoLabel = new JLabel();
    private final JLabel estadoLabel = new JLabel();
    private final JLabel detalleLabel = new JLabel();

    public TarjetasCreditoPanel(CuentaService cuentaService,
                                ObligacionService obligacionService,
                                PagoTarjetaService pagoTarjetaService,
                                CategoriaService categoriaService,
                                Long perfilFinancieroId,
                                Long usuarioId) {
        this.cuentaService = Objects.requireNonNull(cuentaService, "El CuentaService es obligatorio");
        this.obligacionService = Objects.requireNonNull(obligacionService, "El ObligacionService es obligatorio");
        this.pagoTarjetaService = Objects.requireNonNull(pagoTarjetaService, "El PagoTarjetaService es obligatorio");
        this.categoriaService = Objects.requireNonNull(categoriaService, "El CategoriaService es obligatorio");
        this.perfilFinancieroId = Objects.requireNonNull(perfilFinancieroId, "El id del perfil financiero es obligatorio");
        this.usuarioId = Objects.requireNonNull(usuarioId, "El id del usuario es obligatorio");

        configurarRenderers();
        construir();
        tarjetasCombo.addActionListener(e -> actualizarTarjeta());
        cargarDatos();
    }

    public TarjetasCreditoPanel() {
        cuentaService = null;
        obligacionService = null;
        pagoTarjetaService = null;
        categoriaService = null;
        perfilFinancieroId = null;
        usuarioId = null;
        configurarRenderers();
        construir();
    }


    private void configurarRenderers() {
        tarjetasCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Cuenta cuenta) {
                    setText(cuenta.getNombre() + " | " + cuenta.getMoneda().getCodigo());
                } else {
                    setText("Seleccione una tarjeta");
                }
                return this;
            }
        });
        cuentaPagadoraCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Cuenta cuenta) {
                    setText(cuenta.getNombre() + " | " + cuenta.getMoneda().getCodigo());
                } else {
                    setText("Seleccione una cuenta pagadora");
                }
                return this;
            }
        });
    }

    private void construir() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel resumen = new JPanel(new GridBagLayout());
        resumen.setBorder(BorderFactory.createTitledBorder("Tarjeta de crédito"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 8, 4, 8);
        c.anchor = GridBagConstraints.WEST;
        agregarResumen(resumen, c, 0, "Límite", limiteLabel);
        agregarResumen(resumen, c, 1, "Disponible", disponibleLabel);
        agregarResumen(resumen, c, 2, "Consumido", consumidoLabel);
        agregarResumen(resumen, c, 3, "Resumen del ciclo", totalResumenLabel);
        agregarResumen(resumen, c, 4, "Pago mínimo", pagoMinimoLabel);
        agregarResumen(resumen, c, 5, "Saldo total", saldoTotalLabel);
        agregarResumen(resumen, c, 6, "Ciclo actual", cicloLabel);
        agregarResumen(resumen, c, 7, "Vencimiento", vencimientoLabel);
        agregarResumen(resumen, c, 8, "Estado", estadoLabel);
        c.gridx = 0;
        c.gridy = 9;
        resumen.add(new JLabel("Tarjeta"), c);
        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        resumen.add(tarjetasCombo, c);

        add(resumen, BorderLayout.NORTH);

        obligacionesList.setCellRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value,
                                                                    int index, boolean selected, boolean focus) {
                super.getListCellRendererComponent(list, value, index, selected, focus);
                if (value instanceof Obligacion o) {
                    var movimiento = o.getMovimientoOrigen();
                    setText(String.format("%s | %s | %.2f %s | %s",
                            movimiento.getFechaHora().toLocalDate(),
                            movimiento.getDescripcion(),
                            movimiento.getImporte(),
                            movimiento.getMoneda().getCodigo(),
                            o.getEstado()));
                }
                return this;
            }
        });
        obligacionesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) actualizarDetalle(obligacionesList.getSelectedValue());
        });
        JScrollPane obligacionesScroll = new JScrollPane(obligacionesList);
        obligacionesScroll.setBorder(BorderFactory.createTitledBorder("Consumos de la tarjeta"));
        add(obligacionesScroll, BorderLayout.CENTER);

        JPanel pago = new JPanel(new GridBagLayout());
        pago.setBorder(BorderFactory.createTitledBorder("Registrar pago"));
        c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.anchor = GridBagConstraints.WEST;
        agregarCampo(pago, c, 0, "Cuenta pagadora", cuentaPagadoraCombo);
        agregarCampo(pago, c, 1, "Categoría", categoriaCombo);
        agregarCampo(pago, c, 2, "Modalidad", modalidadPagoCombo);
        agregarCampo(pago, c, 3, "Importe", importeField);
        JButton pagar = new JButton("Pagar");
        c.gridx = 2;
        c.gridy = 3;
        pago.add(pagar, c);
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 3;
        pago.add(detalleLabel, c);
        JButton refrescar = new JButton("Actualizar");
        c.gridy = 5;
        pago.add(refrescar, c);
        modalidadPagoCombo.addActionListener(e -> actualizarImporteSegunModalidad());
        pagar.addActionListener(e -> registrarPagoSeleccionado());
        refrescar.addActionListener(e -> cargarDatos());
        actualizarImporteSegunModalidad();
        add(pago, BorderLayout.SOUTH);
    }

    private void agregarResumen(JPanel panel, GridBagConstraints c, int fila, String nombre, JLabel valor) {
        c.gridx = 0;
        c.gridy = fila;
        panel.add(new JLabel(nombre), c);
        c.gridx = 1;
        panel.add(valor, c);
    }

    private void agregarCampo(JPanel panel, GridBagConstraints c, int fila, String nombre, java.awt.Component campo) {
        c.gridx = 0;
        c.gridy = fila;
        panel.add(new JLabel(nombre), c);
        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(campo, c);
        c.weightx = 0;
    }

    private void cargarDatos() {
        if (cuentaService == null) {
            return;
        }
        tarjetasCombo.removeAllItems();
        List<Cuenta> cuentas = cuentaService.listarPorPerfilFinanciero(perfilFinancieroId, usuarioId)
                .stream()
                .filter(c -> c.getTipoCuenta() == ar.com.agmilevecich.sofp.domain.TipoCuenta.TARJETA_CREDITO)
                .toList();
        cuentas.forEach(tarjetasCombo::addItem);

        cuentaPagadoraCombo.removeAllItems();
        cuentaService.listarPorPerfilFinanciero(perfilFinancieroId, usuarioId)
                .stream()
                .filter(cuenta -> cuenta.getTipoCuenta() != ar.com.agmilevecich.sofp.domain.TipoCuenta.TARJETA_CREDITO)
                .forEach(cuentaPagadoraCombo::addItem);

        categoriaCombo.removeAllItems();
        categoriaService.listarPorPerfilFinanciero(perfilFinancieroId, usuarioId)
                .forEach(categoriaCombo::addItem);

        actualizarTarjeta();
    }

    private void actualizarTarjeta() {
        Cuenta tarjeta = (Cuenta) tarjetasCombo.getSelectedItem();
        if (tarjeta == null) {
            return;
        }
        BigDecimal disponible = cuentaService.calcularCreditoDisponible(tarjeta.getId(), usuarioId);
        BigDecimal consumido = tarjeta.getLimiteCredito().subtract(disponible);
        limiteLabel.setText(tarjeta.getLimiteCredito() + " " + tarjeta.getMoneda().getCodigo());
        disponibleLabel.setText(disponible + " " + tarjeta.getMoneda().getCodigo());
        consumidoLabel.setText(consumido + " " + tarjeta.getMoneda().getCodigo());

        DefaultListModel<Obligacion> model = (DefaultListModel<Obligacion>) obligacionesList.getModel();
        model.clear();
        obligacionService.listarPorUsuarioConDetalles(usuarioId).stream()
                .filter(o -> o.getMovimientoOrigen().getCuenta().getId().equals(tarjeta.getId()))
                .forEach(model::addElement);

        actualizarResumenCiclo(tarjeta, model);
    }

    private void actualizarResumenCiclo(Cuenta tarjeta, DefaultListModel<Obligacion> model) {
        ar.com.agmilevecich.sofp.domain.CicloFacturacion ciclo =
                tarjeta.calcularCicloFacturacion(LocalDate.now());
        BigDecimal totalResumen = BigDecimal.ZERO.setScale(2);
        BigDecimal saldoTotal = BigDecimal.ZERO.setScale(2);
        LocalDate vencimiento = null;
        boolean tieneDeuda = false;

        for (int i = 0; i < model.size(); i++) {
            Obligacion obligacion = model.getElementAt(i);
            if (obligacion.getEstado() == EstadoObligacion.PAGADA
                    || obligacion.getEstado() == EstadoObligacion.ANULADA) {
                continue;
            }
            saldoTotal = saldoTotal.add(obligacion.getSaldoPendiente());
            if (ciclo.getFechaCierre().equals(obligacion.getCicloFacturacion().getFechaCierre())) {
                BigDecimal saldoCiclo = obligacion.getSaldoPendienteDelCiclo(ciclo.getFechaCierre());
                totalResumen = totalResumen.add(saldoCiclo);
                tieneDeuda = tieneDeuda || saldoCiclo.signum() > 0;
                LocalDate fecha = obligacion.getFechaLimitePago();
                if (vencimiento == null || fecha.isBefore(vencimiento)) {
                    vencimiento = fecha;
                }
            }
        }

        BigDecimal pagoMinimo = tarjeta.calcularPagoMinimo(totalResumen);
        BigDecimal consumoCiclo = BigDecimal.ZERO.setScale(2);
        for (int i = 0; i < model.size(); i++) {
            Obligacion obligacion = model.getElementAt(i);
            if (obligacion.getEstado() == EstadoObligacion.ANULADA) {
                continue;
            }
            if (ciclo.getFechaCierre().equals(obligacion.getCicloFacturacion().getFechaCierre())) {
                consumoCiclo = consumoCiclo.add(obligacion.getImporteOriginal());
            }
        }

        String moneda = tarjeta.getMoneda().getCodigo();
        totalResumenLabel.setText(totalResumen + " " + moneda);
        consumoCicloLabel.setText(consumoCiclo + " " + moneda);
        pagoMinimoLabel.setText(pagoMinimo + " " + moneda);
        saldoTotalLabel.setText(saldoTotal + " " + moneda);
        cicloLabel.setText(ciclo.getFechaInicio() + " → " + ciclo.getFechaCierre());
        vencimientoLabel.setText(tieneDeuda && vencimiento != null ? vencimiento.toString() : "-");
        estadoLabel.setText(saldoTotal.signum() > 0 ? "CON DEUDA" : "SIN DEUDA");
    }

    private void actualizarDetalle(Obligacion obligacion) {
        if (obligacion == null) {
            detalleLabel.setText("");
            return;
        }
        String financiaciones = obligacion.getFinanciaciones().stream()
                .map(this::describirFinanciacion)
                .collect(Collectors.joining(" | "));
        detalleLabel.setText("Deuda: " + obligacion.getDeudaParaPagoMinimo()
                + " " + obligacion.getMonedaLiquidacion().getCodigo()
                + " | Pago mínimo: " + obligacion.calcularPagoMinimo()
                + " | Financiaciones: " + (financiaciones.isEmpty() ? "ninguna" : financiaciones));
    }

    private String describirFinanciacion(Financiacion f) {
        return String.format("saldo %.2f %s, inicio %s, cargos %.2f",
                f.getSaldoCapital(), f.getMoneda().getCodigo(), f.getFechaInicio(), f.getSaldoCargosPendiente());
    }

    private void actualizarImporteSegunModalidad() {
        String modalidad = (String) modalidadPagoCombo.getSelectedItem();
        boolean editable = "Pago parcial".equals(modalidad);
        importeField.setEditable(editable);
        if ("Pago mínimo".equals(modalidad)) {
            importeField.setText(pagoMinimoLabel.getText().split(" ")[0]);
        } else if ("Pago total".equals(modalidad)) {
            importeField.setText(saldoTotalLabel.getText().split(" ")[0]);
        } else if (!editable) {
            importeField.setText("");
        }
    }

    private void registrarPagoSeleccionado() {
        String modalidad = (String) modalidadPagoCombo.getSelectedItem();
        if ("Pago mínimo".equals(modalidad)) {
            registrarPagoMinimo();
        } else if ("Pago total".equals(modalidad)) {
            registrarPagoTotal();
        } else {
            registrarPagoParcial();
        }
    }

    private void registrarPagoTotal() {
        try {
            Cuenta tarjeta = (Cuenta) tarjetasCombo.getSelectedItem();
            Cuenta cuentaPagadora = (Cuenta) cuentaPagadoraCombo.getSelectedItem();
            Categoria categoria = (Categoria) categoriaCombo.getSelectedItem();

            BigDecimal total = pagoTarjetaService.registrarPagoTotalTarjeta(
                    tarjeta.getId(),
                    cuentaPagadora,
                    categoria,
                    LocalDateTime.now(),
                    "Pago total de tarjeta",
                    usuarioId
            );
            importeField.setText("");
            cargarDatos();
            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Pago total registrado: " + total + " " + tarjeta.getMoneda().getCodigo(),
                    "Pago de tarjeta",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE
            );
        } catch (RuntimeException e) {
            javax.swing.JOptionPane.showMessageDialog(
                    this, e.getMessage(), "No se pudo registrar el pago total",
                    javax.swing.JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void registrarPagoParcial() {
        try {
            Cuenta tarjeta = (Cuenta) tarjetasCombo.getSelectedItem();
            Cuenta cuentaPagadora = (Cuenta) cuentaPagadoraCombo.getSelectedItem();
            Categoria categoria = (Categoria) categoriaCombo.getSelectedItem();
            BigDecimal importe = new BigDecimal(importeField.getText().trim());
            pagoTarjetaService.registrarPagoParcialTarjeta(
                    tarjeta.getId(), cuentaPagadora, categoria, importe,
                    LocalDateTime.now(), "Pago parcial de tarjeta", usuarioId);
            importeField.setText("");
            cargarDatos();
        } catch (RuntimeException e) {
            javax.swing.JOptionPane.showMessageDialog(
                    this, e.getMessage(), "No se pudo registrar el pago",
                    javax.swing.JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void registrarPagoMinimo() {
        try {
            Cuenta tarjeta = (Cuenta) tarjetasCombo.getSelectedItem();
            Cuenta cuentaPagadora = (Cuenta) cuentaPagadoraCombo.getSelectedItem();
            Categoria categoria = (Categoria) categoriaCombo.getSelectedItem();
            BigDecimal minimo = new BigDecimal(pagoMinimoLabel.getText().split(" ")[0]);
            if (minimo.signum() <= 0) {
                throw new IllegalArgumentException("La tarjeta no tiene pago mínimo pendiente");
            }
            pagoTarjetaService.registrarPagoParcialTarjeta(
                    tarjeta.getId(), cuentaPagadora, categoria, minimo,
                    LocalDateTime.now(), "Pago mínimo de tarjeta", usuarioId);
            importeField.setText("");
            cargarDatos();
        } catch (RuntimeException e) {
            javax.swing.JOptionPane.showMessageDialog(
                    this, e.getMessage(), "No se pudo registrar el pago mínimo",
                    javax.swing.JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
