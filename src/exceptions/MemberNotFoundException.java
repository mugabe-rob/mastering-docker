package exceptions;

/**
 * Custom exception thrown when a member is not found in the library system
 */
public class MemberNotFoundException extends Exception {
    
    public MemberNotFoundException(String message) {
        super(message);
    }
    
    public MemberNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
