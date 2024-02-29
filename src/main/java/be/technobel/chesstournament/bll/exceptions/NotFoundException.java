package be.technobel.chesstournament.bll.exceptions;

/**
 * Custom runtime exception thrown to indicate that a requested resource was not found.
 */

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException{
    /**
     * Constructs a new NotFoundException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     */
    public NotFoundException(String message){
        super(message);
    }
}
