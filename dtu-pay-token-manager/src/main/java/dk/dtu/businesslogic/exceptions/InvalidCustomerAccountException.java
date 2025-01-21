package dk.dtu.businesslogic.exceptions;

/**
 * Exception thrown when invalid / non-existent user tries to call the Token Service
 *
 * @author Simão Teixeira (s232431)
 */
public class InvalidCustomerAccountException extends Exception implements IErrorCodeException {

    private static final String ERROR_CODE = "INVALID_CUSTOMER_ACCOUNT";

    public InvalidCustomerAccountException() {
        super();
    }

    public InvalidCustomerAccountException(String message) {
        super(message);
    }

    public InvalidCustomerAccountException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidCustomerAccountException(Throwable cause) {
        super(cause);
    }

    public String getErrorCode() {
        return ERROR_CODE;
    }
}
