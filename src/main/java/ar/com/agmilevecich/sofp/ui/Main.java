package ar.com.agmilevecich.sofp.ui;

import ar.com.agmilevecich.sofp.config.DatosInicialesDesarrollo;
import ar.com.agmilevecich.sofp.config.JpaManager;
import ar.com.agmilevecich.sofp.domain.PerfilFinanciero;
import ar.com.agmilevecich.sofp.domain.Usuario;
import ar.com.agmilevecich.sofp.persistence.CategoriaRepository;
import ar.com.agmilevecich.sofp.persistence.CuentaRepository;
import ar.com.agmilevecich.sofp.persistence.InstitucionFinancieraRepository;
import ar.com.agmilevecich.sofp.persistence.MonedaRepository;
import ar.com.agmilevecich.sofp.persistence.MovimientoActivoRepository;
import ar.com.agmilevecich.sofp.persistence.MovimientoRepository;
import ar.com.agmilevecich.sofp.persistence.PerfilFinancieroRepository;
import ar.com.agmilevecich.sofp.persistence.UsuarioRepository;
import ar.com.agmilevecich.sofp.service.CarteraActivoService;
import ar.com.agmilevecich.sofp.service.CategoriaService;
import ar.com.agmilevecich.sofp.service.CuentaService;
import ar.com.agmilevecich.sofp.service.InstitucionFinancieraService;
import ar.com.agmilevecich.sofp.service.MonedaService;
import ar.com.agmilevecich.sofp.service.MovimientoService;
import ar.com.agmilevecich.sofp.service.PerfilFinancieroService;
import ar.com.agmilevecich.sofp.service.UsuarioService;
import jakarta.persistence.EntityManager;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.function.Function;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::iniciar);
    }

    private static void iniciar() {
        EntityManager entityManager = JpaManager.createEntityManager();

        UsuarioService usuarioService = new UsuarioService(
                new UsuarioRepository(entityManager)
        );

        PerfilFinancieroService perfilFinancieroService = new PerfilFinancieroService(
                new PerfilFinancieroRepository(entityManager)
        );

        if (Boolean.getBoolean("sofp.dev")) {
            DatosInicialesDesarrollo.crearSiNoExisten(entityManager);
        }

        JFrame loginFrame = new JFrame("SOFP - Ingreso");
        LoginPanel loginPanel = new LoginPanel(
                usuarioService,
                usuario -> abrirAplicacion(
                        loginFrame,
                        entityManager,
                        usuario,
                        perfilFinancieroService
                )
        );

        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setContentPane(loginPanel);
        loginFrame.pack();
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setVisible(true);
    }

    private static void abrirAplicacion(
            JFrame loginFrame,
            EntityManager entityManager,
            Usuario usuario,
            PerfilFinancieroService perfilFinancieroService) {

        List<PerfilFinanciero> perfiles =
                perfilFinancieroService.listarPorUsuario(usuario.getId());

        if (perfiles.isEmpty()) {
            JOptionPane.showMessageDialog(
                    loginFrame,
                    "El usuario autenticado no tiene perfiles financieros.",
                    "SOFP",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        PerfilFinanciero perfil = seleccionarPerfil(loginFrame, perfiles);
        if (perfil == null) {
            return;
        }

        CuentaRepository cuentaRepository = new CuentaRepository(entityManager);
        MovimientoRepository movimientoRepository = new MovimientoRepository(entityManager);
        CategoriaRepository categoriaRepository = new CategoriaRepository(entityManager);
        InstitucionFinancieraRepository institucionFinancieraRepository =
                new InstitucionFinancieraRepository(entityManager);
        MonedaRepository monedaRepository = new MonedaRepository(entityManager);
        MovimientoActivoRepository movimientoActivoRepository =
                new MovimientoActivoRepository(entityManager);

        CuentaService cuentaService = new CuentaService(
                cuentaRepository,
                movimientoRepository,
                entityManager
        );
        MovimientoService movimientoService = new MovimientoService(
                entityManager,
                movimientoRepository
        );
        CategoriaService categoriaService = new CategoriaService(
                entityManager,
                categoriaRepository
        );
        InstitucionFinancieraService institucionFinancieraService =
                new InstitucionFinancieraService(institucionFinancieraRepository);
        MonedaService monedaService = new MonedaService(monedaRepository);
        CarteraActivoService carteraActivoService = new CarteraActivoService(
                movimientoActivoRepository
        );

        MainFrame mainFrame = new MainFrame(
                cuentaService,
                movimientoService,
                categoriaService,
                institucionFinancieraService,
                monedaService,
                carteraActivoService,
                perfil,
                usuario.getId()
        );

        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (entityManager.isOpen()) {
                    entityManager.close();
                }
                JpaManager.close();
            }
        });

        loginFrame.dispose();
        mainFrame.setVisible(true);
    }

    static PerfilFinanciero seleccionarPerfil(
            JFrame parent,
            List<PerfilFinanciero> perfiles) {
        return seleccionarPerfil(
                perfiles,
                opciones -> mostrarDialogoSeleccionPerfil(parent, opciones)
        );
    }

    static PerfilFinanciero seleccionarPerfil(
            List<PerfilFinanciero> perfiles,
            Function<List<PerfilFinanciero>, PerfilFinanciero> selector) {

        if (perfiles.size() == 1) {
            return perfiles.get(0);
        }

        return selector.apply(perfiles);
    }

    private static PerfilFinanciero mostrarDialogoSeleccionPerfil(
            JFrame parent,
            List<PerfilFinanciero> perfiles) {

        Object seleccion = JOptionPane.showInputDialog(
                parent,
                "Seleccioná el perfil financiero con el que querés trabajar:",
                "SOFP - Perfil financiero",
                JOptionPane.QUESTION_MESSAGE,
                null,
                perfiles.toArray(),
                perfiles.get(0)
        );

        if (seleccion == null) {
            return null;
        }

        return (PerfilFinanciero) seleccion;
    }
}
