package be.technobel.chesstournament.bll.exceptions;

/**
 * Custom runtime exception thrown to indicate an error during the modification of a match.
 */
public class MatchModificationException extends RuntimeException {

    /**
     * Constructs a new MatchModificationException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     */
    public MatchModificationException(String message) {
        super(message);
    }

    /**
     * Constructs a new MatchModificationException with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     * @param cause   the cause (which is saved for later retrieval by the getCause() method).
     */
    public MatchModificationException(String message, Throwable cause) {
        super(message, cause);
    }
}