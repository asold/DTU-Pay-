package dk.dtu.businesslogic.repositories.exceptions;

/**
 * @author Simão Teixeira (s232431)
 */
@SuppressWarnings("unused")
public class TokenNotFoundException extends Exception {

    public TokenNotFoundException() {
        super();
    }

    public TokenNotFoundException(String message) {
        super(message);
    }

    public TokenNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenNotFoundException(Throwable cause) {
        super(cause);
    }
}
