package exceptions;

/**
 * Custom exception thrown when attempting to borrow an unavailable book
 */
public class BookUnavailableException extends Exception {
    
    public BookUnavailableException(String message) {
        super(message);
    }
    
    public BookUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}

