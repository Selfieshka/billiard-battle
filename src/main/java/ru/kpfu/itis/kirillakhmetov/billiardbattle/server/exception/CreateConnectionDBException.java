package ru.kpfu.itis.kirillakhmetov.billiardbattle.server.exception;

public class CreateConnectionDBException extends Exception {
    public CreateConnectionDBException() {
        super();
    }

    public CreateConnectionDBException(String message) {
        super(message);
    }

    public CreateConnectionDBException(String message, Throwable cause) {
        super(message, cause);
    }

    public CreateConnectionDBException(Throwable cause) {
        super(cause);
    }
}
