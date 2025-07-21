package models;

/**
 * Interface defining operations for library management
 * Demonstrates interface abstraction
 */
public interface LibraryOperations {
    
    /**
     * Adds a book to the library
     */
    void addBook(Book book);
    
    /**
     * Adds a member to the library
     */
    void addMember(Member member);
    
    /**
     * Allows a member to borrow a book
     */
    boolean borrowBook(String memberId, String bookId) throws Exception;
    
    /**
     * Allows a member to return a book
     */
    boolean returnBook(String memberId, String bookId) throws Exception;
    
    /**
     * Displays all available books
     */
    void displayAvailableBooks();
    
    /**
     * Displays all borrowed books
     */
    void displayBorrowedBooks();
}
