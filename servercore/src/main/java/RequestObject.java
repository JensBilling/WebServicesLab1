import java.util.HashMap;
import java.util.Map;

public class RequestObject {
    String type;
    String url;
    String body;
    String contentType;
    String contentLength;
    Map<String, String> urlParameters = new HashMap<>();


    public RequestObject(String type, String url) {
        this.type = type;
        this.url = url;
    }

    public RequestObject(String type, String url, Map<String, String> urlParameters) {
        this.type = type;
        this.url = url;
        this.urlParameters = urlParameters;
    }

    public RequestObject(String type, String url, String body, String contentType, String contentLength) {
        this.type = type;
        this.url = url;
        this.body = body;
        this.contentType = contentType;
        this.contentLength = contentLength;
    }

    public Map<String, String> getUrlParameters() {
        return urlParameters;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public String getBody() {
        return body;
    }

    public String getContentType() {
        return contentType;
    }

    public String getContentLength() {
        return contentLength;
    }

    public void setType(String type) {
        this.type = type;
    }
}
