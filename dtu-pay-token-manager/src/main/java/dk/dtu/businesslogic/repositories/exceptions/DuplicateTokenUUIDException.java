package dk.dtu.businesslogic.repositories.exceptions;

/**
 * Exception used when tokens have the same UUID (Token UUID must be unique) This exception will
 * probably never be thrown as tokens ID use always random generated UUIDs from UUID.randomUUID()
 * java implementation which guarantees extremely low probability of collision.
 *
 * @author Simão Teixeira (s232431)
 */
@SuppressWarnings("unused")
public class DuplicateTokenUUIDException extends RuntimeException {

    public DuplicateTokenUUIDException() {
        super();
    }

    public DuplicateTokenUUIDException(String message) {
        super(message);
    }

    public DuplicateTokenUUIDException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateTokenUUIDException(Throwable cause) {
        super(cause);
    }
}
