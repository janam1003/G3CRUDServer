package exceptions;

/**
 * Exception thrown if any error hapens during data update.
 *
 * @author Janam
 */
public class UpdateException extends Exception {

    /**
     * Creates a new instance of <code>UpdateException</code> without detail
     * message.
     */
    public UpdateException() {
    }

    /**
     * Constructs an instance of <code>UpdateException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public UpdateException(String msg) {
        super(msg);
    }
}
