package http.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebServer {
    private static final int DEFAULT_PORT = 8080;
    private static final int N_THREADS = 3;

    private static String jdbcUrl = "jdbc:sqlite:tasks.db";
    private static Connection connectionBD;

    static {
        try {
            connectionBD = DriverManager.getConnection(jdbcUrl);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public WebServer() throws SQLException {
    }

    /**
     * WebServer constructor.
     */
    protected void start(int port) throws IOException {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Webserver starting up on port " + port);

            System.out.println("Waiting for connection");
            ExecutorService executor = Executors.newFixedThreadPool(N_THREADS);

            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    if (socket != null) {
                        executor.submit(new RequestHandler(socket, connectionBD));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Start the application.
     *
     * @param args
     */
    public static void main(String args[]) throws IOException, SQLException {
        TaskService taskService = new TaskService(connectionBD);
        taskService.create_table_if_not_exists();
        WebServer ws = new WebServer();
        ws.start(getValidPortParam(args));
    }

    /**
     * Parse command line arguments (string[] args) for valid port number
     *
     * @return int valid port number or default value (8080)
     */
    static int getValidPortParam(String args[]) throws NumberFormatException {
        if (args.length > 0) {
            int port = Integer.parseInt(args[0]);
            if (port > 0 && port < 65535) {
                return port;
            } else {
                throw new NumberFormatException("Invalid port! Port value is a number between 0 and 65535");
            }
        }
        return DEFAULT_PORT;
    }
}
