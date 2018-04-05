package by.ititon.orm.exception;

public class MetaInfoNotFoundException extends RuntimeException {

    public MetaInfoNotFoundException() {
        super();
    }

    public MetaInfoNotFoundException(String message) {
        super(message);
    }

    public MetaInfoNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MetaInfoNotFoundException(Throwable cause) {
        super(cause);
    }

    protected MetaInfoNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
