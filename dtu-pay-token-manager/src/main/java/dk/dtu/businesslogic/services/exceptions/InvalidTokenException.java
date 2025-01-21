package dk.dtu.businesslogic.services.exceptions;

/**
 * Exception thrown when invalid token is used within the Token Service
 *
 * @author Simão Teixeira (s232431)
 */
@SuppressWarnings("unused")
public class InvalidTokenException extends Exception implements IErrorCodeException {

    private static final String ERROR_CODE = "INVALID_TOKEN";

    public InvalidTokenException() {
        super();
    }

    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidTokenException(Throwable cause) {
        super(cause);
    }

    public String getErrorCode() {
        return ERROR_CODE;
    }
}
