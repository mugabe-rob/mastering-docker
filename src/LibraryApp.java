import models.*;
import services.*;
import exceptions.*;
import java.util.Scanner;
import java.util.List;

/**
 * Main application class for Library Management System
 * Demonstrates console input handling and system integration
 */
public class LibraryApp {
    
    private static LibraryManagementSystem library;
    private static Scanner scanner;
    
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("    WELCOME TO LIBRARY MANAGEMENT SYSTEM");
        System.out.println("=".repeat(60));
        
        // Initialize the library system and scanner
        library = new LibraryManagementSystem();
        scanner = new Scanner(System.in);
        
        // Add some sample data if library is empty
        initializeSampleData();
        
        // Main application loop
        boolean running = true;
        while (running) {
            try {
                displayMainMenu();
                int choice = getIntInput("Enter your choice: ");
                
                switch (choice) {
                    case 1 -> handleBookManagement();
                    case 2 -> handleMemberManagement();
                    case 3 -> handleBorrowReturn();
                    case 4 -> handleReports();
                    case 5 -> handleSearch();
                    case 6 -> handleDataManagement();
                    case 0 -> {
                        System.out.println("Saving data and exiting...");
                        library.saveData();
                        running = false;
                    }
                    default -> System.out.println("Invalid choice! Please try again.");
                }
            } catch (Exception e) {
                System.err.println("An error occurred: " + e.getMessage());
                System.out.println("Please try again.");
            }
        }
        
        scanner.close();
        System.out.println("Thank you for using Library Management System!");
    }
    
    private static void displayMainMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("              MAIN MENU");
        System.out.println("=".repeat(50));
        System.out.println("1. Book Management");
        System.out.println("2. Member Management");
        System.out.println("3. Borrow/Return Books");
        System.out.println("4. Reports & Statistics");
        System.out.println("5. Search");
        System.out.println("6. Data Management");
        System.out.println("0. Exit");
        System.out.println("-".repeat(50));
    }
    
    private static void handleBookManagement() {
        System.out.println("\n--- BOOK MANAGEMENT ---");
        System.out.println("1. Add New Book");
        System.out.println("2. View All Books");
        System.out.println("3. View Available Books");
        System.out.println("4. View Borrowed Books");
        System.out.println("0. Back to Main Menu");
        
        int choice = getIntInput("Enter your choice: ");
        
        switch (choice) {
            case 1 -> addNewBook();
            case 2 -> displayAllBooks();
            case 3 -> library.displayAvailableBooks();
            case 4 -> library.displayBorrowedBooks();
            case 0 -> { /* Return to main menu */ }
            default -> System.out.println("Invalid choice!");
        }
    }
    
    private static void handleMemberManagement() {
        System.out.println("\n--- MEMBER MANAGEMENT ---");
        System.out.println("1. Add New Member");
        System.out.println("2. View All Members");
        System.out.println("3. Add Librarian");
        System.out.println("4. View Librarians");
        System.out.println("0. Back to Main Menu");
        
        int choice = getIntInput("Enter your choice: ");
        
        switch (choice) {
            case 1 -> addNewMember();
            case 2 -> library.displayAllMembers();
            case 3 -> addNewLibrarian();
            case 4 -> library.displayLibrarians();
            case 0 -> { /* Return to main menu */ }
            default -> System.out.println("Invalid choice!");
        }
    }
    
    private static void handleBorrowReturn() {
        System.out.println("\n--- BORROW/RETURN MANAGEMENT ---");
        System.out.println("1. Borrow Book");
        System.out.println("2. Return Book");
        System.out.println("3. View Member's Borrowed Books");
        System.out.println("4. View Overdue Books");
        System.out.println("0. Back to Main Menu");
        
        int choice = getIntInput("Enter your choice: ");
        
        switch (choice) {
            case 1 -> borrowBook();
            case 2 -> returnBook();
            case 3 -> viewMemberBooks();
            case 4 -> viewOverdueBooks();
            case 0 -> { /* Return to main menu */ }
            default -> System.out.println("Invalid choice!");
        }
    }
    
    private static void handleReports() {
        System.out.println("\n--- REPORTS & STATISTICS ---");
        System.out.println("Library Statistics:");
        System.out.println("- Total Books: " + library.getTotalBooks());
        System.out.println("- Available Books: " + library.getAvailableBookCount());
        System.out.println("- Borrowed Books: " + library.getBorrowedBookCount());
        System.out.println("- Total Members: " + library.getTotalMembers());
        System.out.println("- Total Librarians: " + library.getAllLibrarians().size());
        
        List<Book> overdueBooks = library.getOverdueBooks();
        System.out.println("- Overdue Books: " + overdueBooks.size());
        
        if (!overdueBooks.isEmpty()) {
            System.out.println("\nOverdue Books Details:");
            for (Book book : overdueBooks) {
                Member borrower = library.findMemberById(book.getBorrowedBy());
                System.out.println("  - " + book.getTitle() + " (borrowed by " + 
                                 (borrower != null ? borrower.getName() : "Unknown") + 
                                 ", due: " + book.getReturnDate() + ")");
            }
        }
    }
    
    private static void handleSearch() {
        System.out.println("\n--- SEARCH ---");
        System.out.println("1. Search Books by Title");
        System.out.println("2. Search Books by Author");
        System.out.println("3. Find Book by ID");
        System.out.println("4. Find Member by ID");
        System.out.println("0. Back to Main Menu");
        
        int choice = getIntInput("Enter your choice: ");
        
        switch (choice) {
            case 1 -> searchBooksByTitle();
            case 2 -> searchBooksByAuthor();
            case 3 -> findBookById();
            case 4 -> findMemberById();
            case 0 -> { /* Return to main menu */ }
            default -> System.out.println("Invalid choice!");
        }
    }
    
    private static void handleDataManagement() {
        System.out.println("\n--- DATA MANAGEMENT ---");
        System.out.println("1. Save Data to Files");
        System.out.println("2. Load Sample Data");
        System.out.println("0. Back to Main Menu");
        
        int choice = getIntInput("Enter your choice: ");
        
        switch (choice) {
            case 1 -> library.saveData();
            case 2 -> initializeSampleData();
            case 0 -> { /* Return to main menu */ }
            default -> System.out.println("Invalid choice!");
        }
    }
    
    // Individual operation methods
    
    private static void addNewBook() {
        try {
            System.out.println("\n--- ADD NEW BOOK ---");
            String id = getStringInput("Enter Book ID: ");
            String title = getStringInput("Enter Book Title: ");
            String author = getStringInput("Enter Author Name: ");
            String isbn = getStringInput("Enter ISBN: ");
            
            Book book = new Book(id, title, author, isbn);
            library.addBook(book);
            
        } catch (Exception e) {
            System.err.println("Error adding book: " + e.getMessage());
        }
    }
    
    private static void addNewMember() {
        try {
            System.out.println("\n--- ADD NEW MEMBER ---");
            String id = getStringInput("Enter Member ID: ");
            String name = getStringInput("Enter Member Name: ");
            String email = getStringInput("Enter Email: ");
            int maxBooks = getIntInput("Enter Maximum Books Allowed (default 5): ");
            
            if (maxBooks <= 0) maxBooks = 5;
            
            Member member = new Member(id, name, email, maxBooks);
            library.addMember(member);
            
        } catch (Exception e) {
            System.err.println("Error adding member: " + e.getMessage());
        }
    }
    
    private static void addNewLibrarian() {
        try {
            System.out.println("\n--- ADD NEW LIBRARIAN ---");
            String id = getStringInput("Enter Librarian ID: ");
            String name = getStringInput("Enter Librarian Name: ");
            String email = getStringInput("Enter Email: ");
            String empId = getStringInput("Enter Employee ID: ");
            String department = getStringInput("Enter Department: ");
            double salary = getDoubleInput("Enter Salary: ");
            
            Librarian librarian = new Librarian(id, name, email, empId, department, salary);
            library.addLibrarian(librarian);
            
        } catch (Exception e) {
            System.err.println("Error adding librarian: " + e.getMessage());
        }
    }
    
    private static void borrowBook() {
        try {
            System.out.println("\n--- BORROW BOOK ---");
            library.displayAvailableBooks();
            
            String memberId = getStringInput("Enter Member ID: ");
            String bookId = getStringInput("Enter Book ID: ");
            
            library.borrowBook(memberId, bookId);
            
        } catch (MemberNotFoundException | BookNotFoundException | BookUnavailableException e) {
            System.err.println("Borrowing failed: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }
    
    private static void returnBook() {
        try {
            System.out.println("\n--- RETURN BOOK ---");
            library.displayBorrowedBooks();
            
            String memberId = getStringInput("Enter Member ID: ");
            String bookId = getStringInput("Enter Book ID: ");
            
            library.returnBook(memberId, bookId);
            
        } catch (MemberNotFoundException | BookNotFoundException | BookUnavailableException e) {
            System.err.println("Return failed: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }
    
    private static void viewMemberBooks() {
        String memberId = getStringInput("Enter Member ID: ");
        Member member = library.findMemberById(memberId);
        
        if (member == null) {
            System.out.println("Member not found!");
            return;
        }
        
        System.out.println("\n--- BOOKS BORROWED BY " + member.getName() + " ---");
        List<String> borrowedBookIds = member.getBorrowedBooks();
        
        if (borrowedBookIds.isEmpty()) {
            System.out.println("No books currently borrowed.");
            return;
        }
        
        for (String bookId : borrowedBookIds) {
            Book book = library.findBookById(bookId);
            if (book != null) {
                System.out.println("- " + book.getTitle() + " (Due: " + book.getReturnDate() + 
                                 (book.isOverdue() ? " - OVERDUE!" : ""));
            }
        }
    }
    
    private static void viewOverdueBooks() {
        List<Book> overdueBooks = library.getOverdueBooks();
        
        if (overdueBooks.isEmpty()) {
            System.out.println("No overdue books!");
            return;
        }
        
        System.out.println("\n--- OVERDUE BOOKS ---");
        for (Book book : overdueBooks) {
            Member borrower = library.findMemberById(book.getBorrowedBy());
            System.out.println("- " + book.getTitle() + 
                             " (borrowed by " + (borrower != null ? borrower.getName() : "Unknown") + 
                             ", due: " + book.getReturnDate() + ")");
        }
    }
    
    private static void displayAllBooks() {
        List<Book> allBooks = library.getAllBooks();
        
        if (allBooks.isEmpty()) {
            System.out.println("No books in the library.");
            return;
        }
        
        System.out.println("\n=== ALL BOOKS ===");
        System.out.printf("%-10s %-30s %-20s %-15s %-12s%n", 
                "ID", "Title", "Author", "ISBN", "Status");
        System.out.println("-".repeat(90));
        
        for (Book book : allBooks) {
            String status = book.isAvailable() ? "Available" : 
                           "Borrowed" + (book.isOverdue() ? " (Overdue)" : "");
            System.out.printf("%-10s %-30s %-20s %-15s %-12s%n",
                    book.getBookId(),
                    truncate(book.getTitle(), 30),
                    truncate(book.getAuthor(), 20),
                    book.getIsbn(),
                    status);
        }
        System.out.println("Total books: " + allBooks.size());
    }
    
    private static void searchBooksByTitle() {
        String title = getStringInput("Enter title to search: ");
        List<Book> results = library.searchBooksByTitle(title);
        
        if (results.isEmpty()) {
            System.out.println("No books found with title containing: " + title);
            return;
        }
        
        System.out.println("\n--- SEARCH RESULTS ---");
        for (Book book : results) {
            System.out.println(book);
        }
    }
    
    private static void searchBooksByAuthor() {
        String author = getStringInput("Enter author to search: ");
        List<Book> results = library.searchBooksByAuthor(author);
        
        if (results.isEmpty()) {
            System.out.println("No books found by author containing: " + author);
            return;
        }
        
        System.out.println("\n--- SEARCH RESULTS ---");
        for (Book book : results) {
            System.out.println(book);
        }
    }
    
    private static void findBookById() {
        String bookId = getStringInput("Enter Book ID: ");
        Book book = library.findBookById(bookId);
        
        if (book == null) {
            System.out.println("Book not found!");
        } else {
            System.out.println("\n--- BOOK DETAILS ---");
            System.out.println(book);
        }
    }
    
    private static void findMemberById() {
        String memberId = getStringInput("Enter Member ID: ");
        Member member = library.findMemberById(memberId);
        
        if (member == null) {
            System.out.println("Member not found!");
        } else {
            System.out.println("\n--- MEMBER DETAILS ---");
            System.out.println(member.getFullInfo());
        }
    }
    
    // Utility methods for input handling
    
    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
    
    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = Integer.parseInt(scanner.nextLine().trim());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
    
    private static double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                double value = Double.parseDouble(scanner.nextLine().trim());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
    
    private static String truncate(String str, int maxLength) {
        if (str == null) return "";
        return str.length() > maxLength ? str.substring(0, maxLength - 3) + "..." : str;
    }
    
    // Initialize sample data for demonstration
    private static void initializeSampleData() {
        try {
            // Only add sample data if library is empty
            if (library.getTotalBooks() == 0) {
                System.out.println("Adding sample data...");
                
                // Sample books
                library.addBook(new Book("B001", "The Great Gatsby", "F. Scott Fitzgerald", "978-0-7432-7356-5"));
                library.addBook(new Book("B002", "To Kill a Mockingbird", "Harper Lee", "978-0-06-112008-4"));
                library.addBook(new Book("B003", "1984", "George Orwell", "978-0-452-28423-4"));
                library.addBook(new Book("B004", "Pride and Prejudice", "Jane Austen", "978-0-14-143951-8"));
                library.addBook(new Book("B005", "The Catcher in the Rye", "J.D. Salinger", "978-0-316-76948-0"));
                
                // Sample members
                library.addMember(new Member("M001", "John Doe", "john.doe@email.com"));
                library.addMember(new Member("M002", "Jane Smith", "jane.smith@email.com"));
                library.addMember(new Member("M003", "Robert Johnson", "robert.j@email.com"));
                
                // Sample librarians
                library.addLibrarian(new Librarian("L001", "Alice Brown", "alice.brown@library.com", 
                                                 "EMP001", "Management", 65000));
                library.addLibrarian(new Librarian("L002", "Bob Wilson", "bob.wilson@library.com", 
                                                 "EMP002", "Circulation", 45000));
                
                System.out.println("Sample data added successfully!");
            }
        } catch (Exception e) {
            System.err.println("Error adding sample data: " + e.getMessage());
        }
    }
}
