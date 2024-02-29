package be.technobel.chesstournament.bll.exceptions;

/**
 * Custom runtime exception thrown to indicate an error during tournament registration.
 */
public class TournamentRegistrationException extends RuntimeException {

    /**
     * Constructs a new TournamentRegistrationException with no detail message.
     */
    public TournamentRegistrationException() {
        super();
    }

    /**
     * Constructs a new TournamentRegistrationException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     */
    public TournamentRegistrationException(String message) {
        super(message);
    }

    /**
     * Constructs a new TournamentRegistrationException with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     * @param cause   the cause (which is saved for later retrieval by the getCause() method).
     */
    public TournamentRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new TournamentRegistrationException with the specified cause and no detail message.
     *
     * @param cause the cause (which is saved for later retrieval by the getCause() method).
     */
    public TournamentRegistrationException(Throwable cause) {
        super(cause);
    }
}
