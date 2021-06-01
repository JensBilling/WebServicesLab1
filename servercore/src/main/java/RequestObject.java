public class RequestObject {
    String type;
    String url;

    public void setType(String type) {
        this.type = type;
    }
// add parameters when needed

    public RequestObject(String type, String url) {
        this.type = type;
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }
}
