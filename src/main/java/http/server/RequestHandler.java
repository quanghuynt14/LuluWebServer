package http.server;

import java.net.Socket;
import java.sql.Connection;
import java.util.Map;

public class RequestHandler implements Runnable {
    private Socket socket;
    private Connection connectionDB;

    public RequestHandler(Socket socket, Connection connectionDB) {
        this.socket = socket;
        this.connectionDB = connectionDB;
    }

    public void run() {
        try {
            if (socket != null) {
                System.out.println("Run request handler for " + socket.toString());
                HttpRequest req = new HttpRequest(socket.getInputStream());
                if (req.method != null) {
                    System.out.println(req.method);
                    System.out.println(req.uri);
                    for (Map.Entry<String, String> header : req.headers.entrySet()) {
                        System.out.println(header);
                    }
                    for (Map.Entry<String, String> param : req.params.entrySet()) {
                        System.out.println(param);
                    }
                    HttpResponse res = new HttpResponse(req, connectionDB);
                    res.sendToClient(socket.getOutputStream());
                }

                socket.close();
            }
        } catch (Exception e) {
            System.out.println("Runtime Error: " + e);
        }
    }
}
