package dk.dtu.businesslogic.exceptions;

public class DuplicateTokenUUIDException extends Exception {
    public DuplicateTokenUUIDException(String message) {
        super(message);
    }

    @SuppressWarnings("unused")
    public DuplicateTokenUUIDException(String message, Throwable cause) {
        super(message, cause);
    }

    @SuppressWarnings("unused")
    public DuplicateTokenUUIDException(Throwable cause) {
        super(cause);
    }

}
