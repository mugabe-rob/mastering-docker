package exceptions;

/**
 * Custom exception thrown when a book is not found in the library system
 */
public class BookNotFoundException extends Exception {
    
    public BookNotFoundException(String message) {
        super(message);
    }
    
    public BookNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
