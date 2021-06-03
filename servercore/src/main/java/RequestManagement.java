import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class RequestManagement {

    public static RequestObject getManager(String url) {
        String parameters = "";

        if (url.contains("?")) {
            String[] splitUrl = url.split("\\?");
            url = splitUrl[0];
            parameters = splitUrl[1];


        }

        if (url.equals("/")) {
            return new RequestObject("GET", "servercore/src/main/resources/index.html");
        } else if (url.equals("/create")) {
            String[] splitParams = parameters.split("&");
            Map<String, String> queryStringMap = new HashMap<>();
            for (String string : splitParams) {
                String[] keyValueSplit = string.split("=");
                String key = keyValueSplit[0];
                String value = keyValueSplit[1];
                queryStringMap.put(key, value);

            }

            return new RequestObject("GET", url, queryStringMap);


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
