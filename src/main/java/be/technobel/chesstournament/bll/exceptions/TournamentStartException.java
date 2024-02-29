package be.technobel.chesstournament.bll.exceptions;

/**
 * Custom runtime exception thrown to indicate an error during the start of a tournament.
 */
public class TournamentStartException extends RuntimeException {
    /**
     * Constructs a new TournamentStartException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     */
    public TournamentStartException(String message) {
        super(message);
    }
}
