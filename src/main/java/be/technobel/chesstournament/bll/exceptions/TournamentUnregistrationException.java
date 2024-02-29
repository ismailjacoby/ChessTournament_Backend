package be.technobel.chesstournament.bll.exceptions;

/**
 * Custom runtime exception thrown to indicate an error during tournament unregistration.
 */
public class TournamentUnregistrationException extends RuntimeException {

    /**
     * Constructs a new TournamentUnregistrationException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     */
    public TournamentUnregistrationException(String message) {
        super(message);
    }
}
