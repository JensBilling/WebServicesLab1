public class RequestObject {
    private String type;
    private String url;
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
