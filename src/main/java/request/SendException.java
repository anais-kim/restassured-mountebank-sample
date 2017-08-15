package request;

public class SendException extends  RuntimeException {

    public SendException() {
        super();
    }

    public SendException(String message) {
        super(message);
    }

    public SendException(String message, Throwable cause) {
        super(message, cause);
    }
}
