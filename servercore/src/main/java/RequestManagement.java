import java.io.BufferedReader;
import java.io.InputStreamReader;

public class RequestManagement {

    public static RequestObject getManager(String url) {
        if (url.equals("/")) {
            return new RequestObject("GET", "servercore/src/main/resources/index.html");
        } else {
            return new RequestObject("GET", "servercore/src/main/resources" + url);
        }
    }

    public static RequestObject headManager(String url) {
        if (url.equals("/")) {
            return new RequestObject("HEAD", "servercore/src/main/resources/index.html");
        } else {
            return new RequestObject("HEAD", "servercore/src/main/resources" + url);
        }
    }

    public static RequestObject postManager(String url, String contentType, String contentLength, String requestBody) {

        return new RequestObject("POST", "servercore/src/main/resources" + url, requestBody, contentType, contentLength);

    }

}
