package http.server;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpResponse {
    public static final String VERSION = "HTTP/1.1";
    List<String> headers = new ArrayList<>();
    byte[] body;

    public HttpResponse(HttpRequest req, Connection connectionDB) {
        switch (req.method) {
            case HEAD:
                fillHeaders(Status._200);
                break;
            case GET:
                try {
                    File file = new File("." + req.uri);

                    if (file.isFile() || req.uri.equals("/favicon.ico")) {
                        getFile(req, file);
                    } else {
                        getEndpoint(req, connectionDB);
                    }
                } catch (Exception e) {
                    System.out.println("Response Error: " + e);
                    fillHeaders(Status._400);
                    fillBody(Status._400.toString());
                }
                break;
            case POST:
                try {
                    postEndpoint(req, connectionDB);
                } catch (Exception e) {
                    System.out.println("Response Error: " + e);
                    fillHeaders(Status._400);
                    fillBody(Status._400.toString());
                }
                break;
            case PUT:
                try {
                    putEndpoint(req, connectionDB);
                } catch (Exception e) {
                    System.out.println("Response Error: " + e);
                    fillHeaders(Status._400);
                    fillBody(Status._400.toString());
                }
                break;
            case DELETE:
                try {
                    deleteEndpoint(req, connectionDB);
                } catch (Exception e) {
                    System.out.println("Response Error: " + e);
                    fillHeaders(Status._400);
                    fillBody(Status._400.toString());
                }
                break;
            case UNRECOGNIZED:
                fillHeaders(Status._400);
                fillBody(Status._400.toString());
                break;
            default:
                fillHeaders(Status._501);
                fillBody(Status._501.toString());
        }
    }

    private void fillHeaders(Status status) {
        headers.add(HttpResponse.VERSION + " " + status.toString());
        headers.add("server: lulu");
    }

    private void fillBody(String response) {
        body = response.getBytes();
        setContentLength();
    }

    private void fillBody(byte[] response) {
        body = response;
        setContentLength();
    }

    public void sendToClient(OutputStream os) throws IOException {
        for (String header : headers) {
            os.write(header.getBytes());
            os.write("\r\n".getBytes());
        }

        os.write("\r\n".getBytes());
        if (body != null) {
            os.write(body);
        }
    }

    private void setContentType(String uri) {
        try {
            String ext = uri.substring(uri.indexOf(".") + 1);
            headers.add(ContentType.valueOf(ext.toUpperCase()).toString());
        } catch (Exception e) {
            System.out.println("ContentType not found: " + e);
        }
    }

    private void setContentLength() {
        if (body != null) {
            headers.add("Content-Length: " + body.length);
        }
    }

    private void getEndpoint(HttpRequest req, Connection connectionBD) throws SQLException {
        System.out.println("GET Endpoint: " + req.uri);
        switch (req.uri) {
            case "/":
                fillHeaders(Status._200);
                headers.add(ContentType.HTML.toString());
                StringBuilder result = new StringBuilder("<!DOCTYPE html>");
                result.append("<html><head><title>PR INSA Lyon</title></head>");
                result.append("<body><h1>Welcome to WebServer</h1>");
                result.append("<h2>Made by LOMBARD Louis & TRAN Quang Huy</h2>");
                result.append("</body></html>");
                fillBody(result.toString());
                break;
            case "/tasklist":
                TaskService taskService = new TaskService(connectionBD);
                List<WSTask> tasks = taskService.getAllTasks();

                fillHeaders(Status._200);
                headers.add(ContentType.JSON.toString());
                result = new StringBuilder("{");
                result.append("\"result\": \"success\",");
                result.append("\"tasks\": [");
                for (int i = 0; i < tasks.size(); i++) {
                    result.append("{");
                    result.append("\"taskid\": ");
                    result.append(tasks.get(i).getTaskid());
                    result.append(",");
                    result.append("\"content\": \"");
                    result.append(tasks.get(i).getContent());
                    result.append("\",");
                    result.append("\"created_at\": \"");
                    result.append(tasks.get(i).getCreated_at());
                    result.append("\"}");
                    if (i < tasks.size() - 1) {
                        result.append(",");
                    }
                }
                result.append("]");
                result.append("}");
                fillBody(result.toString());
                break;
            default:
                System.out.println("Endpoint not found");
                fillHeaders(Status._400);
                fillBody(Status._400.toString());
        }
    }

    private void getFile(HttpRequest req, File file) throws IOException {
        System.out.println("GET File: " + file.getAbsolutePath());
        if (file.exists()) {
            System.out.println("File exists");
            fillHeaders(Status._200);
            setContentType(req.uri);

            FileInputStream fileInputStream = new FileInputStream(file);
            fillBody(fileInputStream.readAllBytes());
        } else {
            System.out.println("File not found");
            fillHeaders(Status._404);
            fillBody(Status._404.toString());
        }
    }

    private void postEndpoint(HttpRequest req, Connection connectionBD) throws SQLException {
        System.out.println("POST Endpoint: " + req.uri);
        StringBuilder result;
        switch (req.uri) {
            case "/add":
                int res = 0;
                for (Map.Entry<String, String> param : req.params.entrySet()) {
                    res += Integer.parseInt(param.getValue());
                }

                fillHeaders(Status._200);
                headers.add(ContentType.JSON.toString());
                result = new StringBuilder("{");
                result.append("\"result\": \"");
                result.append(res);
                result.append("\"}");
                fillBody(result.toString());
                break;
            case "/task":
                TaskService taskService = new TaskService(connectionBD);
                taskService.insert_task(req.params.get("content"));

                fillHeaders(Status._200);
                headers.add(ContentType.JSON.toString());
                result = new StringBuilder("{");
                result.append("\"result\": \"success\"");
                result.append("}");
                fillBody(result.toString());
                break;
            default:
                System.out.println("Endpoint not found");
                fillHeaders(Status._400);
                fillBody(Status._400.toString());
        }
    }

    private void putEndpoint(HttpRequest req, Connection connectionBD) throws SQLException {
        System.out.println("PUT Endpoint: " + req.uri);
        StringBuilder result;
        switch (req.uri) {
            case "/task":
                TaskService taskService = new TaskService(connectionBD);
                taskService.update_task(Integer.parseInt(req.params.get("taskid")), req.params.get("content"));

                fillHeaders(Status._200);
                headers.add(ContentType.JSON.toString());
                result = new StringBuilder("{");
                result.append("\"result\": \"success\"");
                result.append("}");
                fillBody(result.toString());
                break;
            default:
                System.out.println("Endpoint not found");
                fillHeaders(Status._400);
                fillBody(Status._400.toString());
        }
    }

    private void deleteEndpoint(HttpRequest req, Connection connectionBD) throws SQLException {
        System.out.println("DELETE Endpoint: " + req.uri);
        StringBuilder result;
        switch (req.uri) {
            case "/task":
                TaskService taskService = new TaskService(connectionBD);
                taskService.delete_task(Integer.parseInt(req.params.get("taskid")));

                fillHeaders(Status._200);
                headers.add(ContentType.JSON.toString());
                result = new StringBuilder("{");
                result.append("\"result\": \"success\"");
                result.append("}");
                fillBody(result.toString());
                break;
            default:
                System.out.println("Endpoint not found");
                fillHeaders(Status._400);
                fillBody(Status._400.toString());
        }
    }
}
