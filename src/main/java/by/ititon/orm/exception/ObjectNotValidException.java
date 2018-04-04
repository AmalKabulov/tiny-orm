package by.ititon.orm.exception;

public class ObjectNotValidException extends RuntimeException {
    public ObjectNotValidException() {
        super();
    }

    public ObjectNotValidException(String message) {
        super(message);
    }

    public ObjectNotValidException(String message, Throwable cause) {
        super(message, cause);
    }

    public ObjectNotValidException(Throwable cause) {
        super(cause);
    }

    protected ObjectNotValidException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
