package dk.dtu.core.exceptions;

public class AccountRegistrationException extends RuntimeException {

    public AccountRegistrationException(String message) { super(message); }

    @SuppressWarnings("unused")
    public AccountRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }

    @SuppressWarnings("unused")
    public AccountRegistrationException(Throwable cause) {
        super(cause);
    }

    @Override
    public String toString() {
        return this.getMessage();
    }
}
