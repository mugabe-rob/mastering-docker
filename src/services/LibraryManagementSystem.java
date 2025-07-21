package services;

import models.*;
import exceptions.*;
import java.io.IOException;
import java.util.*;

/**
 * Main Library Management System class
 * Implements LibraryOperations interface and demonstrates all OOP concepts
 */
public class LibraryManagementSystem implements LibraryOperations {
    
    // Collections to store data - demonstrates use of ArrayList and HashMap
    private List<Book> books;
    private List<Member> members;
    private Map<String, Book> bookIndex;
    private Map<String, Member> memberIndex;
    private List<Librarian> librarians;
    
    // Constructor
    public LibraryManagementSystem() {
        this.books = new ArrayList<>();
        this.members = new ArrayList<>();
        this.bookIndex = new HashMap<>();
        this.memberIndex = new HashMap<>();
        this.librarians = new ArrayList<>();
        
        // Initialize data directory and load existing data
        FileService.initializeDataDirectory();
        loadData();
    }
    
    /**
     * Load data from files on startup
     */
    private void loadData() {
        try {
            // Load books
            books = FileService.loadBooks();
            for (Book book : books) {
                bookIndex.put(book.getBookId(), book);
            }
            
            // Load members
            members = FileService.loadMembers();
            for (Member member : members) {
                memberIndex.put(member.getId(), member);
            }
            
        } catch (IOException e) {
            System.err.println("Warning: Could not load existing data - " + e.getMessage());
        }
    }
    
    /**
     * Save data to files
     */
    public void saveData() {
        try {
            FileService.saveBooks(books);
            FileService.saveMembers(members);
            System.out.println("All data saved successfully!");
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }
    
    // Implementation of LibraryOperations interface methods
    
    @Override
    public void addBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }
        
        if (bookIndex.containsKey(book.getBookId())) {
            System.out.println("Book with ID " + book.getBookId() + " already exists!");
            return;
        }
        
        books.add(book);
        bookIndex.put(book.getBookId(), book);
        System.out.println("Book added successfully: " + book.getTitle());
    }
    
    @Override
    public void addMember(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("Member cannot be null");
        }
        
        if (memberIndex.containsKey(member.getId())) {
            System.out.println("Member with ID " + member.getId() + " already exists!");
            return;
        }
        
        members.add(member);
        memberIndex.put(member.getId(), member);
        System.out.println("Member added successfully: " + member.getName());
    }
    
    public void addLibrarian(Librarian librarian) {
        if (librarian == null) {
            throw new IllegalArgumentException("Librarian cannot be null");
        }
        
        librarians.add(librarian);
        System.out.println("Librarian added successfully: " + librarian.getName());
    }
    
    @Override
    public boolean borrowBook(String memberId, String bookId) throws Exception {
        // Exception handling with custom exceptions
        Member member = memberIndex.get(memberId);
        if (member == null) {
            throw new MemberNotFoundException("Member with ID " + memberId + " not found");
        }
        
        Book book = bookIndex.get(bookId);
        if (book == null) {
            throw new BookNotFoundException("Book with ID " + bookId + " not found");
        }
        
        if (!book.isAvailable()) {
            throw new BookUnavailableException(
                "Book '" + book.getTitle() + "' is currently borrowed by member: " + book.getBorrowedBy()
            );
        }
        
        if (!member.canBorrowMoreBooks()) {
            throw new BookUnavailableException(
                "Member " + member.getName() + " has reached the maximum borrowing limit or is inactive"
            );
        }
        
        // Perform the borrowing operation
        book.borrowBook(memberId);
        member.addBorrowedBook(bookId);
        
        System.out.println("Book '" + book.getTitle() + "' borrowed successfully by " + member.getName());
        System.out.println("Return date: " + book.getReturnDate());
        
        return true;
    }
    
    @Override
    public boolean returnBook(String memberId, String bookId) throws Exception {
        Member member = memberIndex.get(memberId);
        if (member == null) {
            throw new MemberNotFoundException("Member with ID " + memberId + " not found");
        }
        
        Book book = bookIndex.get(bookId);
        if (book == null) {
            throw new BookNotFoundException("Book with ID " + bookId + " not found");
        }
        
        if (book.isAvailable()) {
            System.out.println("Book '" + book.getTitle() + "' is not currently borrowed");
            return false;
        }
        
        if (!book.getBorrowedBy().equals(memberId)) {
            throw new BookUnavailableException(
                "Book '" + book.getTitle() + "' was not borrowed by member " + member.getName()
            );
        }
        
        // Check if book is overdue
        boolean wasOverdue = book.isOverdue();
        
        // Perform the return operation
        book.returnBook();
        member.removeBorrowedBook(bookId);
        
        System.out.println("Book '" + book.getTitle() + "' returned successfully by " + member.getName());
        
        if (wasOverdue) {
            System.out.println("WARNING: This book was overdue!");
        }
        
        return true;
    }
    
    @Override
    public void displayAvailableBooks() {
        System.out.println("\n=== AVAILABLE BOOKS ===");
        List<Book> availableBooks = books.stream()
                .filter(Book::isAvailable)
                .toList();
        
        if (availableBooks.isEmpty()) {
            System.out.println("No books available for borrowing.");
            return;
        }
        
        System.out.printf("%-10s %-30s %-20s %-15s%n", "ID", "Title", "Author", "ISBN");
        System.out.println("-".repeat(80));
        
        for (Book book : availableBooks) {
            System.out.printf("%-10s %-30s %-20s %-15s%n",
                    book.getBookId(),
                    truncate(book.getTitle(), 30),
                    truncate(book.getAuthor(), 20),
                    book.getIsbn());
        }
        System.out.println("Total available books: " + availableBooks.size());
    }
    
    @Override
    public void displayBorrowedBooks() {
        System.out.println("\n=== BORROWED BOOKS ===");
        List<Book> borrowedBooks = books.stream()
                .filter(book -> !book.isAvailable())
                .toList();
        
        if (borrowedBooks.isEmpty()) {
            System.out.println("No books are currently borrowed.");
            return;
        }
        
        System.out.printf("%-10s %-25s %-15s %-12s %-10s%n", 
                "ID", "Title", "Borrowed By", "Borrow Date", "Due Date");
        System.out.println("-".repeat(80));
        
        for (Book book : borrowedBooks) {
            Member borrower = memberIndex.get(book.getBorrowedBy());
            String borrowerName = borrower != null ? borrower.getName() : "Unknown";
            String status = book.isOverdue() ? " (OVERDUE)" : "";
            
            System.out.printf("%-10s %-25s %-15s %-12s %-10s%s%n",
                    book.getBookId(),
                    truncate(book.getTitle(), 25),
                    truncate(borrowerName, 15),
                    book.getBorrowDate(),
                    book.getReturnDate(),
                    status);
        }
        System.out.println("Total borrowed books: " + borrowedBooks.size());
    }
    
    // Additional utility methods
    
    public void displayAllMembers() {
        System.out.println("\n=== ALL MEMBERS ===");
        if (members.isEmpty()) {
            System.out.println("No members registered.");
            return;
        }
        
        System.out.printf("%-10s %-20s %-25s %-15s %-8s %-8s%n",
                "ID", "Name", "Email", "Membership", "Books", "Active");
        System.out.println("-".repeat(90));
        
        for (Member member : members) {
            System.out.printf("%-10s %-20s %-25s %-15s %-8s %-8s%n",
                    member.getId(),
                    truncate(member.getName(), 20),
                    truncate(member.getEmail(), 25),
                    member.getMembershipDate(),
                    member.getBorrowedBooksCount() + "/" + member.getMaxBooksAllowed(),
                    member.isActive() ? "Yes" : "No");
        }
        System.out.println("Total members: " + members.size());
    }
    
    public void displayLibrarians() {
        System.out.println("\n=== LIBRARIANS ===");
        if (librarians.isEmpty()) {
            System.out.println("No librarians registered.");
            return;
        }
        
        for (Librarian librarian : librarians) {
            System.out.println(librarian.getFullInfo());
        }
    }
    
    public Book findBookById(String bookId) {
        return bookIndex.get(bookId);
    }
    
    public Member findMemberById(String memberId) {
        return memberIndex.get(memberId);
    }
    
    public List<Book> searchBooksByTitle(String title) {
        return books.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase()))
                .toList();
    }
    
    public List<Book> searchBooksByAuthor(String author) {
        return books.stream()
                .filter(book -> book.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .toList();
    }
    
    public List<Book> getOverdueBooks() {
        return books.stream()
                .filter(Book::isOverdue)
                .toList();
    }
    
    // Utility method to truncate strings for display
    private String truncate(String str, int maxLength) {
        if (str == null) return "";
        return str.length() > maxLength ? str.substring(0, maxLength - 3) + "..." : str;
    }
    
    // Getters for collections (defensive copies)
    public List<Book> getAllBooks() {
        return new ArrayList<>(books);
    }
    
    public List<Member> getAllMembers() {
        return new ArrayList<>(members);
    }
    
    public List<Librarian> getAllLibrarians() {
        return new ArrayList<>(librarians);
    }
    
    public int getTotalBooks() {
        return books.size();
    }
    
    public int getAvailableBookCount() {
        return (int) books.stream().filter(Book::isAvailable).count();
    }
    
    public int getBorrowedBookCount() {
        return (int) books.stream().filter(book -> !book.isAvailable()).count();
    }
    
    public int getTotalMembers() {
        return members.size();
    }
}
