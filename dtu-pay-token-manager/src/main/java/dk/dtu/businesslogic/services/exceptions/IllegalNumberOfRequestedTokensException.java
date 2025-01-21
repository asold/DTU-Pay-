package dk.dtu.businesslogic.services.exceptions;

/**
 * Exception thrown when an Illegal Number of Tokens is requested by a user
 *
 * @author Simao Teixeira (s232431)
 */
public class IllegalNumberOfRequestedTokensException extends Exception
        implements IErrorCodeException {

    private static final String ERROR_CODE = "ILLEGAL_NUMBER_REQUESTED_TOKENS";

    public IllegalNumberOfRequestedTokensException() {
        super();
    }

    public IllegalNumberOfRequestedTokensException(String message) {
        super(message);
    }

    public IllegalNumberOfRequestedTokensException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalNumberOfRequestedTokensException(Throwable cause) {
        super(cause);
    }

    public String getErrorCode() {
        return ERROR_CODE;
    }
}
