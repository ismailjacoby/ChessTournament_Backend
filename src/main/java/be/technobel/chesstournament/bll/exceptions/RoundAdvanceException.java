package be.technobel.chesstournament.bll.exceptions;

/**
 * Custom runtime exception thrown to indicate an error during the advancement to the next round in a tournament.
 */
public class RoundAdvanceException extends RuntimeException {

    /**
     * Constructs a new RoundAdvanceException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     */
    public RoundAdvanceException(String message) {
        super(message);
    }

    /**
     * Constructs a new RoundAdvanceException with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     * @param cause   the cause (which is saved for later retrieval by the getCause() method).
     */
    public RoundAdvanceException(String message, Throwable cause) {
        super(message, cause);
    }
}
