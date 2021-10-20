package http.server;

/**
 * Method enum maps the HTTP/1.1 available request methods
 */
public enum HttpMethod {
    GET("GET"), //
    HEAD("HEAD"), //
    POST("POST"), //
    PUT("PUT"), //
    DELETE("DELETE"), //
    UNRECOGNIZED(null); //

    private final String method;

    HttpMethod(String method) {
        this.method = method;
    }
}
