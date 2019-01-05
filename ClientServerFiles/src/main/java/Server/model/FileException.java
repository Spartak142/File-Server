package Server.model;

public class FileException extends Exception {

    // Create a new instance thrown because of the specified reason.
    public FileException(String reason) {
        super(reason);
    }

    // Create a new instance thrown because of the specified reason and exception.
    public FileException(String reason, Throwable rootCause) {
        super(reason, rootCause);
    }
}
