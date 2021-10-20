package http.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class HttpRequest {
    Map<String, String> headers = new HashMap<>();
    Map<String, String> params = new HashMap<>();
    HttpMethod method;
    String uri;
    String version;

    public HttpRequest(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder str = new StringBuilder(reader.readLine());
        if (str != null) {
            parseRequestLine(str.toString());
        }

        str = new StringBuilder(reader.readLine());
        while (str != null && !str.toString().isBlank()) {
            parseRequestHeader(str.toString());
            str = new StringBuilder(reader.readLine());
        }

        if (Objects.equals(headers.get("Content-Type"), "application/x-www-form-urlencoded")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            str = new StringBuilder();
            for (int i = 0; i < contentLength; i++) {
                str.append((char) reader.read());
            }
            parseParams(str.toString());
        }
    }

    private void parseRequestLine(String str) {
        String[] split = str.split("\\s+");
        try {
            method = HttpMethod.valueOf(split[0]);
        } catch (Exception e) {
            method = HttpMethod.UNRECOGNIZED;
        }
        if (split[1].contains("?")) {
            String[] split_params = split[1].split("\\?");
            uri = split_params[0];
            parseParams(split_params[1]);
        } else {
            uri = split[1];
        }

        version = split[2];
    }

    private void parseRequestHeader(String str) {
        String[] split = str.split(": ");
        headers.put(split[0], split[1]);
    }

    private void parseParams(String str) {
        String[] split = str.split("&");
        for (String param : split) {
            String[] field = param.split("=");
            params.put(field[0], field[1]);
        }
    }
}
