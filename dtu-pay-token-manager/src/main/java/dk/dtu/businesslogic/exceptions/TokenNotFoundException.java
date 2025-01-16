package dk.dtu.businesslogic.exceptions;

public class TokenNotFoundException extends Exception {

    public TokenNotFoundException(String message) {
        super(message);
    }

    @SuppressWarnings("unused")
    public TokenNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    @SuppressWarnings("unused")
    public TokenNotFoundException(Throwable cause) {
        super(cause);
    }
}
