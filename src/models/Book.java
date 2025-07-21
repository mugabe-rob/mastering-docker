package models;

import java.time.LocalDate;

/**
 * Book class representing a book in the library
 * Demonstrates encapsulation with private fields and public methods
 */
public class Book {
    private String bookId;
    private String title;
    private String author;
    private String isbn;
    private boolean isAvailable;
    private String borrowedBy;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    
    // Constructor
    public Book(String bookId, String title, String author, String isbn) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.isAvailable = true;
        this.borrowedBy = null;
        this.borrowDate = null;
        this.returnDate = null;
    }
    
    // Encapsulation: Getters and Setters
    public String getBookId() {
        return bookId;
    }
    
    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getIsbn() {
        return isbn;
    }
    
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    public boolean isAvailable() {
        return isAvailable;
    }
    
    public void setAvailable(boolean available) {
        isAvailable = available;
    }
    
    public String getBorrowedBy() {
        return borrowedBy;
    }
    
    public void setBorrowedBy(String borrowedBy) {
        this.borrowedBy = borrowedBy;
    }
    
    public LocalDate getBorrowDate() {
        return borrowDate;
    }
    
    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }
    
    public LocalDate getReturnDate() {
        return returnDate;
    }
    
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
    
    /**
     * Business logic method to borrow a book
     */
    public void borrowBook(String memberId) {
        this.isAvailable = false;
        this.borrowedBy = memberId;
        this.borrowDate = LocalDate.now();
        this.returnDate = LocalDate.now().plusDays(14); // 2 weeks loan period
    }
    
    /**
     * Business logic method to return a book
     */
    public void returnBook() {
        this.isAvailable = true;
        this.borrowedBy = null;
        this.borrowDate = null;
        this.returnDate = null;
    }
    
    /**
     * Method to check if book is overdue
     */
    public boolean isOverdue() {
        if (isAvailable || returnDate == null) {
            return false;
        }
        return LocalDate.now().isAfter(returnDate);
    }
    
    @Override
    public String toString() {
        String status = isAvailable ? "Available" : 
                       String.format("Borrowed by %s (Due: %s)", borrowedBy, returnDate);
        return String.format("Book[ID=%s, Title='%s', Author='%s', ISBN='%s', Status=%s]",
                           bookId, title, author, isbn, status);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Book book = (Book) obj;
        return bookId.equals(book.bookId);
    }
    
    @Override
    public int hashCode() {
        return bookId.hashCode();
    }
}
