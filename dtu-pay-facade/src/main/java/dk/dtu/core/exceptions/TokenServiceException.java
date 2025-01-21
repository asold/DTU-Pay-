package dk.dtu.core.exceptions;

/**
 * Exception used to wrap failed events from the token microservice
 *
 * @author Simão Teixeira (s232431)
 */
public class TokenServiceException extends Exception {

    private final String errorCode;

    public TokenServiceException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public TokenServiceException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
