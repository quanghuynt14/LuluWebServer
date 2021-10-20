package http.server;

public enum ContentType {
    CSS("CSS"), //
    GIF("GIF"), //
    HTM("HTM"), //
    HTML("HTML"), //
    ICO("ICO"), //
    JPG("JPG"), //
    JPEG("JPEG"), //
    PNG("PNG"), //
    TXT("TXT"), //
    XML("XML"), //
    JSON("JSON"), //
    MP4("MP4"), //
    MP3("MP3"); //

    private final String extension;

    ContentType(String extension) {
        this.extension = extension;
    }

    @Override
    public String toString() {
        switch (this) {
            case CSS:
                return "Content-Type: text/css";
            case GIF:
                return "Content-Type: image/gif";
            case HTM:
            case HTML:
                return "Content-Type: text/html";
            case ICO:
                return "Content-Type: image/x-icon";
            case JPG:
            case JPEG:
                return "Content-Type: image/jpeg";
            case PNG:
                return "Content-Type: image/png";
            case TXT:
                return "Content-type: text/plain";
            case XML:
                return "Content-type: text/xml";
            case JSON:
                return "Content-type: application/json";
            case MP4:
                return "Content-type: video/mp4";
            case MP3:
                return "Content-Type: audio/mpeg";
            default:
                return null;
        }
    }
}
