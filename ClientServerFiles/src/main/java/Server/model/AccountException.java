package Server.model;

public class AccountException extends Exception {

    // Create a new instance thrown because of the specified reason.
    public AccountException(String reason) {
        super(reason);
    }

    // Create a new instance thrown because of the specified reason and exception.
    public AccountException(String reason, Throwable rootCause) {
        super(reason, rootCause);
    }
}
