/*
Done?
 */
package Server.model;

public class RejectedException extends Exception {

    // Create a new instance thrown because of the specified reason.
    public RejectedException(String reason) {
        super(reason);
    }

    // Create a new instance thrown because of the specified reason and exception.
    public RejectedException(String reason, Throwable rootCause) {
        super(reason, rootCause);
    }
}
