
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

    public static RequestObject postManager(String url) {
        if (url.equals("/")) {
            return new RequestObject("POST", "servercore/src/main/resources/index.html");
        } else {
            return new RequestObject("POST", "servercore/src/main/resources" + url);
        }
    }

}
