package request;

public enum Server {

    test("http://localhost", 5959, "");

    private final String host;
    private final int port;
    private final String basePath;

    Server(String host, int port, String basePath) {
        this.host = host;
        this.port = port;
        this.basePath = basePath;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getBasePath() {
        return basePath;
    }
}
