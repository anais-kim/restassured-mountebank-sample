package stub;

public class StubServiceException extends RuntimeException{

    public StubServiceException() {
        super();
    }

    public StubServiceException(String message) {
        super(message);
    }

    public StubServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
