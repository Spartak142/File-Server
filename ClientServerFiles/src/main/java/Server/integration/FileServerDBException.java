package Server.integration;

// Thrown when a call to the FileServer database fails.
public class FileServerDBException extends Exception {

    // Create a new instance thrown because of the specified reason.
    public FileServerDBException(String reason) {
        super(reason);
    }

    // Create a new instance thrown because of the specified reason and exception.
    public FileServerDBException(String reason, Throwable rootCause) {
        super(reason, rootCause);
    }
}
