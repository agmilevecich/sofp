package ar.com.agmilevecich.sofp.config;

import org.h2.tools.Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class H2ServerManager {

    private static final int PUERTO = 9092;
    private static final String URL = "jdbc:h2:tcp://localhost:" + PUERTO + "/./database/sofp";

    private static Server server;
    private static boolean servidorPropio;

    private H2ServerManager() {
    }

    public static synchronized void start() throws SQLException {
        if (server != null && server.isRunning(false)) {
            return;
        }

        try {
            server = Server.createTcpServer(
                    "-tcpPort", String.valueOf(PUERTO)
            ).start();
            servidorPropio = true;
        } catch (SQLException e) {
            if (estaDisponible()) {
                server = null;
                servidorPropio = false;
                return;
            }
            throw e;
        }
    }

    public static synchronized void stop() {
        if (servidorPropio && server != null && server.isRunning(false)) {
            server.stop();
        }

        server = null;
        servidorPropio = false;
    }

    static boolean estaDisponible() {
        try (Connection ignored = DriverManager.getConnection(URL, "sa", "")) {
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    static boolean esServidorPropio() {
        return servidorPropio;
    }
}
