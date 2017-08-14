package request;

public enum API {

    list_customers("/customers", "GET", "application/json"),
    get_customer("/customers/{customerId}", "GET", "application/json"),
    create_customer("/customers", "POST", "application/json"),
    delete_customer("/customers", "DELETE", "application/json");

    private final String path;
    private final String method;
    private final String contentType;

    API(String path, String method, String contentType) {
        this.path = path;
        this.method = method;
        this.contentType = contentType;
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }

    public String getContentType() {
        return contentType;
    }
}
