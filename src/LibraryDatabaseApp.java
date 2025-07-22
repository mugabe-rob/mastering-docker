import config.DatabaseConfig;
import models.*;
import services.*;
import exceptions.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Database-Integrated Library Management System Application
 * Uses MySQL database for persistent data storage
 */
public class LibraryDatabaseApp {
    private static Scanner scanner = new Scanner(System.in);
    private static DatabaseConfig dbConfig;
    
    public static void main(String[] args) {
        System.out.println("🏛️  LIBRARY MANAGEMENT SYSTEM (DATABASE VERSION)");
        System.out.println("=" .repeat(60));
        
        // Initialize database connection
        if (!initializeDatabase()) {
            System.out.println("❌ Failed to connect to database. Exiting...");
            System.out.println("💡 Make sure to:");
            System.out.println("   1. Run setup-database.bat to create the database");
            System.out.println("   2. Update credentials in DatabaseConfig.java");
            System.out.println("   3. Ensure MySQL service is running");
            return;
        }
        
        // Main application loop
        runMainMenu();
        
        // Cleanup
        cleanup();
    }
    
    /**
     * Initialize database connection and test connectivity
     */
    private static boolean initializeDatabase() {
        try {
            System.out.println("🔌 Connecting to MySQL database...");
            dbConfig = new DatabaseConfig();
            dbConfig.initialize();
            
            if (dbConfig.testConnection()) {
                System.out.println("✅ Database connected successfully!");
                return true;
            } else {
                System.out.println("❌ Database connection failed!");
                return false;
            }
        } catch (Exception e) {
            System.out.println("❌ Database initialization error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Main menu loop
     */
    private static void runMainMenu() {
        while (true) {
            displayMainMenu();
            int choice = getIntInput("Enter your choice: ");
            
            switch (choice) {
                case 1:
                    manageBooks();
                    break;
                case 2:
                    manageMembers();
                    break;
                case 3:
                    manageBorrowing();
                    break;
                case 4:
                    generateReports();
                    break;
                case 5:
                    testDatabaseOperations();
                    break;
                case 0:
                    System.out.println("👋 Thank you for using Library Management System!");
                    return;
                default:
                    System.out.println("❌ Invalid choice. Please try again.");
            }
        }
    }
    
    /**
     * Display main menu options
     */
    private static void displayMainMenu() {
        System.out.println("\n" + "=" .repeat(50));
        System.out.println("📚 MAIN MENU");
        System.out.println("=" .repeat(50));
        System.out.println("1. 📖 Book Management");
        System.out.println("2. 👤 Member Management");
        System.out.println("3. 🔄 Borrowing & Returns");
        System.out.println("4. 📊 Reports & Analytics");
        System.out.println("5. 🧪 Test Database Operations");
        System.out.println("0. 🚪 Exit");
        System.out.println("=" .repeat(50));
    }
    
    /**
     * Book management menu
     */
    private static void manageBooks() {
        while (true) {
            System.out.println("\n📖 BOOK MANAGEMENT");
            System.out.println("1. Add New Book");
            System.out.println("2. Search Books");
            System.out.println("3. Display All Books");
            System.out.println("4. Update Book Details");
            System.out.println("5. Remove Book");
            System.out.println("0. Back to Main Menu");
            
            int choice = getIntInput("Enter your choice: ");
            
            switch (choice) {
                case 1:
                    addNewBook();
                    break;
                case 2:
                    searchBooks();
                    break;
                case 3:
                    displayAllBooks();
                    break;
                case 4:
                    updateBook();
                    break;
                case 5:
                    removeBook();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("❌ Invalid choice.");
            }
        }
    }
    
    /**
     * Add a new book to the database
     */
    private static void addNewBook() {
        try {
            System.out.println("\n➕ ADD NEW BOOK");
            System.out.println("-" .repeat(30));
            
            String title = getStringInput("📚 Enter book title: ");
            String author = getStringInput("✍️  Enter author name: ");
            String isbn = getStringInput("🔢 Enter ISBN: ");
            String genre = getStringInput("🎭 Enter genre: ");
            int quantity = getIntInput("📦 Enter quantity: ");
            
            if (title.isEmpty() || author.isEmpty() || isbn.isEmpty()) {
                System.out.println("❌ Title, author, and ISBN are required!");
                return;
            }
            
            // Insert into database
            String sql = "INSERT INTO books (title, author, isbn, genre, total_copies, available_copies) VALUES (?, ?, ?, ?, ?, ?)";
            
            try (Connection conn = dbConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                
                stmt.setString(1, title);
                stmt.setString(2, author);
                stmt.setString(3, isbn);
                stmt.setString(4, genre.isEmpty() ? "General" : genre);
                stmt.setInt(5, quantity);
                stmt.setInt(6, quantity); // Initially all copies are available
                
                int rowsAffected = stmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    // Get the generated book ID
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int bookId = generatedKeys.getInt(1);
                            System.out.println("✅ Book added successfully with ID: " + bookId);
                            System.out.println("📖 Title: " + title);
                            System.out.println("✍️  Author: " + author);
                            System.out.println("📦 Copies: " + quantity);
                        }
                    }
                } else {
                    System.out.println("❌ Failed to add book.");
                }
                
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                System.out.println("❌ A book with this ISBN already exists!");
            } else {
                System.out.println("❌ Database error: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("❌ Error adding book: " + e.getMessage());
        }
    }
    
    /**
     * Search for books in the database
     */
    private static void searchBooks() {
        try {
            System.out.println("\n🔍 SEARCH BOOKS");
            System.out.println("1. Search by Title");
            System.out.println("2. Search by Author");
            System.out.println("3. Search by Genre");
            System.out.println("4. Search by ISBN");
            System.out.println("5. Show Available Books Only");
            
            int choice = getIntInput("Enter search type: ");
            
            String sql = "";
            String searchTerm = "";
            
            switch (choice) {
                case 1:
                    searchTerm = getStringInput("Enter title to search: ");
                    sql = "SELECT * FROM books WHERE title LIKE ?";
                    break;
                case 2:
                    searchTerm = getStringInput("Enter author to search: ");
                    sql = "SELECT * FROM books WHERE author LIKE ?";
                    break;
                case 3:
                    searchTerm = getStringInput("Enter genre to search: ");
                    sql = "SELECT * FROM books WHERE genre LIKE ?";
                    break;
                case 4:
                    searchTerm = getStringInput("Enter ISBN to search: ");
                    sql = "SELECT * FROM books WHERE isbn = ?";
                    break;
                case 5:
                    sql = "SELECT * FROM books WHERE available_copies > 0";
                    break;
                default:
                    System.out.println("❌ Invalid search type.");
                    return;
            }
            
            try (Connection conn = dbConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                if (choice != 5) {
                    if (choice == 4) {
                        stmt.setString(1, searchTerm);
                    } else {
                        stmt.setString(1, "%" + searchTerm + "%");
                    }
                }
                
                ResultSet rs = stmt.executeQuery();
                displayBookResults(rs);
                
            }
        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage());
        }
    }
    
    /**
     * Display all books from database
     */
    private static void displayAllBooks() {
        try {
            System.out.println("\n📚 ALL BOOKS IN LIBRARY");
            
            String sql = "SELECT * FROM books ORDER BY title";
            
            try (Connection conn = dbConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                displayBookResults(rs);
            }
        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage());
        }
    }
    
    /**
     * Display book results in formatted table
     */
    private static void displayBookResults(ResultSet rs) throws SQLException {
        System.out.println("\n" + "-" .repeat(100));
        System.out.printf("%-5s %-30s %-25s %-15s %-15s %-8s %-8s%n", 
                         "ID", "Title", "Author", "ISBN", "Genre", "Total", "Available");
        System.out.println("-" .repeat(100));
        
        boolean hasResults = false;
        while (rs.next()) {
            hasResults = true;
            System.out.printf("%-5d %-30s %-25s %-15s %-15s %-8d %-8d%n",
                rs.getInt("book_id"),
                truncate(rs.getString("title"), 30),
                truncate(rs.getString("author"), 25),
                rs.getString("isbn"),
                rs.getString("genre"),
                rs.getInt("total_copies"),
                rs.getInt("available_copies")
            );
        }
        
        if (!hasResults) {
            System.out.println("📭 No books found matching your criteria.");
        }
        System.out.println("-" .repeat(100));
    }
    
    /**
     * Member management menu
     */
    private static void manageMembers() {
        while (true) {
            System.out.println("\n👤 MEMBER MANAGEMENT");
            System.out.println("1. Add New Member");
            System.out.println("2. Search Members");
            System.out.println("3. Display All Members");
            System.out.println("4. View Member Details");
            System.out.println("5. View Member Borrowing History");
            System.out.println("0. Back to Main Menu");
            
            int choice = getIntInput("Enter your choice: ");
            
            switch (choice) {
                case 1:
                    addNewMember();
                    break;
                case 2:
                    searchMembers();
                    break;
                case 3:
                    displayAllMembers();
                    break;
                case 4:
                    viewMemberDetails();
                    break;
                case 5:
                    viewMemberHistory();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("❌ Invalid choice.");
            }
        }
    }
    
    /**
     * Add a new member to the database
     */
    private static void addNewMember() {
        try {
            System.out.println("\n➕ ADD NEW MEMBER");
            System.out.println("-" .repeat(30));
            
            String firstName = getStringInput("👤 Enter first name: ");
            String lastName = getStringInput("👤 Enter last name: ");
            String email = getStringInput("📧 Enter email: ");
            String phone = getStringInput("📱 Enter phone: ");
            String address = getStringInput("🏠 Enter address: ");
            
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
                System.out.println("❌ First name, last name, and email are required!");
                return;
            }
            
            // First insert into persons table
            String personSql = "INSERT INTO persons (first_name, last_name, email, phone_number, address) VALUES (?, ?, ?, ?, ?)";
            
            try (Connection conn = dbConfig.getConnection()) {
                conn.setAutoCommit(false); // Start transaction
                
                int personId;
                try (PreparedStatement personStmt = conn.prepareStatement(personSql, Statement.RETURN_GENERATED_KEYS)) {
                    personStmt.setString(1, firstName);
                    personStmt.setString(2, lastName);
                    personStmt.setString(3, email);
                    personStmt.setString(4, phone.isEmpty() ? null : phone);
                    personStmt.setString(5, address.isEmpty() ? null : address);
                    
                    int rowsAffected = personStmt.executeUpdate();
                    if (rowsAffected == 0) {
                        throw new SQLException("Failed to insert person");
                    }
                    
                    try (ResultSet generatedKeys = personStmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            personId = generatedKeys.getInt(1);
                        } else {
                            throw new SQLException("Failed to get person ID");
                        }
                    }
                }
                
                // Then insert into members table
                String memberSql = "INSERT INTO members (person_id, membership_type, max_books_allowed) VALUES (?, 'regular', 5)";
                try (PreparedStatement memberStmt = conn.prepareStatement(memberSql)) {
                    memberStmt.setInt(1, personId);
                    memberStmt.executeUpdate();
                }
                
                conn.commit(); // Commit transaction
                System.out.println("✅ Member added successfully!");
                System.out.println("🆔 Member ID: " + personId);
                System.out.println("👤 Name: " + firstName + " " + lastName);
                System.out.println("📧 Email: " + email);
                System.out.println("📚 Max books allowed: 5");
                
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                System.out.println("❌ A member with this email already exists!");
            } else {
                System.out.println("❌ Database error: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("❌ Error adding member: " + e.getMessage());
        }
    }
    
    /**
     * Display all members
     */
    private static void displayAllMembers() {
        try {
            System.out.println("\n👥 ALL LIBRARY MEMBERS");
            
            String sql = """
                SELECT m.member_id, p.first_name, p.last_name, p.email, 
                       m.membership_type, m.join_date, m.max_books_allowed,
                       COUNT(bb.borrowing_id) as active_borrowings
                FROM members m 
                JOIN persons p ON m.person_id = p.person_id 
                LEFT JOIN book_borrowings bb ON m.member_id = bb.member_id AND bb.status = 'borrowed'
                GROUP BY m.member_id, p.first_name, p.last_name, p.email, m.membership_type, m.join_date, m.max_books_allowed
                ORDER BY p.last_name, p.first_name
                """;
            
            try (Connection conn = dbConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                System.out.println("\n" + "-" .repeat(90));
                System.out.printf("%-5s %-20s %-25s %-15s %-12s %-8s%n", 
                                 "ID", "Name", "Email", "Type", "Join Date", "Books");
                System.out.println("-" .repeat(90));
                
                boolean hasResults = false;
                while (rs.next()) {
                    hasResults = true;
                    String fullName = rs.getString("first_name") + " " + rs.getString("last_name");
                    System.out.printf("%-5d %-20s %-25s %-15s %-12s %-8d%n",
                        rs.getInt("member_id"),
                        truncate(fullName, 20),
                        truncate(rs.getString("email"), 25),
                        rs.getString("membership_type"),
                        rs.getDate("join_date").toString(),
                        rs.getInt("active_borrowings")
                    );
                }
                
                if (!hasResults) {
                    System.out.println("📭 No members found.");
                }
                System.out.println("-" .repeat(90));
            }
        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage());
        }
    }
    
    /**
     * Borrowing and returns management
     */
    private static void manageBorrowing() {
        while (true) {
            System.out.println("\n🔄 BORROWING & RETURNS");
            System.out.println("1. Borrow Book");
            System.out.println("2. Return Book");
            System.out.println("3. View Active Borrowings");
            System.out.println("4. View Overdue Books");
            System.out.println("5. Check Member Borrowing Status");
            System.out.println("0. Back to Main Menu");
            
            int choice = getIntInput("Enter your choice: ");
            
            switch (choice) {
                case 1:
                    borrowBook();
                    break;
                case 2:
                    returnBook();
                    break;
                case 3:
                    viewActiveBorrowings();
                    break;
                case 4:
                    viewOverdueBooks();
                    break;
                case 5:
                    checkMemberBorrowingStatus();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("❌ Invalid choice.");
            }
        }
    }
    
    /**
     * Borrow a book
     */
    private static void borrowBook() {
        try {
            System.out.println("\n📤 BORROW BOOK");
            System.out.println("-" .repeat(20));
            
            int memberId = getIntInput("Enter member ID: ");
            int bookId = getIntInput("Enter book ID: ");
            
            // Check if member exists and can borrow more books
            String memberCheckSql = """
                SELECT m.max_books_allowed, COUNT(bb.borrowing_id) as current_borrowings,
                       p.first_name, p.last_name
                FROM members m 
                JOIN persons p ON m.person_id = p.person_id
                LEFT JOIN book_borrowings bb ON m.member_id = bb.member_id AND bb.status = 'borrowed'
                WHERE m.member_id = ?
                GROUP BY m.member_id, m.max_books_allowed, p.first_name, p.last_name
                """;
            
            // Check if book is available
            String bookCheckSql = "SELECT title, author, available_copies FROM books WHERE book_id = ?";
            
            try (Connection conn = dbConfig.getConnection()) {
                // Check member
                try (PreparedStatement memberStmt = conn.prepareStatement(memberCheckSql)) {
                    memberStmt.setInt(1, memberId);
                    ResultSet memberRs = memberStmt.executeQuery();
                    
                    if (!memberRs.next()) {
                        System.out.println("❌ Member not found!");
                        return;
                    }
                    
                    int maxBooks = memberRs.getInt("max_books_allowed");
                    int currentBooks = memberRs.getInt("current_borrowings");
                    String memberName = memberRs.getString("first_name") + " " + memberRs.getString("last_name");
                    
                    if (currentBooks >= maxBooks) {
                        System.out.println("❌ " + memberName + " has reached the maximum borrowing limit (" + maxBooks + " books)");
                        return;
                    }
                }
                
                // Check book
                try (PreparedStatement bookStmt = conn.prepareStatement(bookCheckSql)) {
                    bookStmt.setInt(1, bookId);
                    ResultSet bookRs = bookStmt.executeQuery();
                    
                    if (!bookRs.next()) {
                        System.out.println("❌ Book not found!");
                        return;
                    }
                    
                    int availableCopies = bookRs.getInt("available_copies");
                    String bookTitle = bookRs.getString("title");
                    String bookAuthor = bookRs.getString("author");
                    
                    if (availableCopies <= 0) {
                        System.out.println("❌ No copies available for: " + bookTitle);
                        return;
                    }
                    
                    // Process borrowing
                    conn.setAutoCommit(false);
                    
                    // Insert borrowing record
                    String borrowSql = "INSERT INTO book_borrowings (member_id, book_id, borrow_date, due_date, status) VALUES (?, ?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 14 DAY), 'borrowed')";
                    try (PreparedStatement borrowStmt = conn.prepareStatement(borrowSql)) {
                        borrowStmt.setInt(1, memberId);
                        borrowStmt.setInt(2, bookId);
                        borrowStmt.executeUpdate();
                    }
                    
                    // Update available copies
                    String updateSql = "UPDATE books SET available_copies = available_copies - 1 WHERE book_id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, bookId);
                        updateStmt.executeUpdate();
                    }
                    
                    conn.commit();
                    LocalDate dueDate = LocalDate.now().plusDays(14);
                    
                    System.out.println("✅ Book borrowed successfully!");
                    System.out.println("📖 Book: " + bookTitle + " by " + bookAuthor);
                    System.out.println("📅 Due date: " + dueDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    System.out.println("⏰ Return within 14 days to avoid late fees.");
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage());
        }
    }
    
    /**
     * View active borrowings
     */
    private static void viewActiveBorrowings() {
        try {
            System.out.println("\n📋 ACTIVE BORROWINGS");
            
            String sql = """
                SELECT bb.borrowing_id, bb.member_id, 
                       CONCAT(p.first_name, ' ', p.last_name) as member_name,
                       b.title, b.author, bb.borrow_date, bb.due_date,
                       DATEDIFF(bb.due_date, CURDATE()) as days_remaining
                FROM book_borrowings bb
                JOIN members m ON bb.member_id = m.member_id
                JOIN persons p ON m.person_id = p.person_id
                JOIN books b ON bb.book_id = b.book_id
                WHERE bb.status = 'borrowed'
                ORDER BY bb.due_date
                """;
            
            try (Connection conn = dbConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                System.out.println("\n" + "-" .repeat(110));
                System.out.printf("%-5s %-6s %-20s %-25s %-15s %-12s %-12s %-8s%n", 
                                 "ID", "MbrID", "Member Name", "Book Title", "Author", "Borrowed", "Due Date", "Days");
                System.out.println("-" .repeat(110));
                
                boolean hasResults = false;
                while (rs.next()) {
                    hasResults = true;
                    int daysRemaining = rs.getInt("days_remaining");
                    String status = daysRemaining < 0 ? "OVERDUE" : 
                                   daysRemaining <= 3 ? "DUE SOON" : 
                                   String.valueOf(daysRemaining);
                    
                    System.out.printf("%-5d %-6d %-20s %-25s %-15s %-12s %-12s %-8s%n",
                        rs.getInt("borrowing_id"),
                        rs.getInt("member_id"),
                        truncate(rs.getString("member_name"), 20),
                        truncate(rs.getString("title"), 25),
                        truncate(rs.getString("author"), 15),
                        rs.getDate("borrow_date").toString(),
                        rs.getDate("due_date").toString(),
                        status
                    );
                }
                
                if (!hasResults) {
                    System.out.println("📭 No active borrowings found.");
                }
                System.out.println("-" .repeat(110));
            }
        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage());
        }
    }
    
    /**
     * Test database operations
     */
    private static void testDatabaseOperations() {
        System.out.println("\n🧪 TESTING DATABASE OPERATIONS");
        System.out.println("=" .repeat(50));
        
        try {
            // Test connection
            System.out.println("1. Testing database connection...");
            if (dbConfig.testConnection()) {
                System.out.println("   ✅ Connection successful!");
            } else {
                System.out.println("   ❌ Connection failed!");
                return;
            }
            
            // Test book count
            System.out.println("\n2. Testing book statistics...");
            String bookStatsSql = """
                SELECT 
                    COUNT(*) as total_books,
                    SUM(total_copies) as total_copies,
                    SUM(available_copies) as available_copies,
                    COUNT(DISTINCT author) as unique_authors
                FROM books
                """;
            
            try (Connection conn = dbConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(bookStatsSql);
                 ResultSet rs = stmt.executeQuery()) {
                
                if (rs.next()) {
                    System.out.println("   📚 Total book titles: " + rs.getInt("total_books"));
                    System.out.println("   📖 Total copies: " + rs.getInt("total_copies"));
                    System.out.println("   ✅ Available copies: " + rs.getInt("available_copies"));
                    System.out.println("   ✍️  Unique authors: " + rs.getInt("unique_authors"));
                }
            }
            
            // Test member count
            System.out.println("\n3. Testing member statistics...");
            String memberStatsSql = """
                SELECT 
                    COUNT(*) as total_members,
                    COUNT(CASE WHEN bb.member_id IS NOT NULL THEN 1 END) as active_borrowers,
                    SUM(CASE WHEN bb.status = 'borrowed' THEN 1 ELSE 0 END) as total_borrowed_books
                FROM members m
                LEFT JOIN book_borrowings bb ON m.member_id = bb.member_id
                """;
            
            try (Connection conn = dbConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(memberStatsSql);
                 ResultSet rs = stmt.executeQuery()) {
                
                if (rs.next()) {
                    System.out.println("   👤 Total members: " + rs.getInt("total_members"));
                    System.out.println("   📚 Active borrowers: " + rs.getInt("active_borrowers"));
                    System.out.println("   📖 Books currently borrowed: " + rs.getInt("total_borrowed_books"));
                }
            }
            
            // Test view query
            System.out.println("\n4. Testing database views...");
            String viewSql = "SELECT COUNT(*) as available_titles FROM available_books_view";
            try (Connection conn = dbConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(viewSql);
                 ResultSet rs = stmt.executeQuery()) {
                
                if (rs.next()) {
                    System.out.println("   ✅ Available book titles: " + rs.getInt("available_titles"));
                }
            }
            
            // Test sample data
            System.out.println("\n5. Sample available books:");
            String sampleSql = "SELECT title, author, available_copies FROM available_books_view LIMIT 5";
            try (Connection conn = dbConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sampleSql);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    System.out.println("   📖 " + rs.getString("title") + 
                                     " by " + rs.getString("author") + 
                                     " (" + rs.getInt("available_copies") + " copies)");
                }
            }
            
            System.out.println("\n✅ All database tests completed successfully!");
            
        } catch (SQLException e) {
            System.out.println("❌ Database test failed: " + e.getMessage());
        }
    }
    
    // Utility methods
    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
    
    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number.");
            }
        }
    }
    
    private static String truncate(String str, int length) {
        if (str == null) return "";
        return str.length() > length ? str.substring(0, length - 3) + "..." : str;
    }
    
    // Stub methods for remaining functionality - to be implemented
    private static void updateBook() {
        System.out.println("🚧 Update book functionality - Coming soon!");
    }
    
    private static void removeBook() {
        System.out.println("🚧 Remove book functionality - Coming soon!");
    }
    
    private static void searchMembers() {
        System.out.println("🚧 Search members functionality - Coming soon!");
    }
    
    private static void viewMemberDetails() {
        System.out.println("🚧 View member details functionality - Coming soon!");
    }
    
    private static void viewMemberHistory() {
        System.out.println("🚧 View member history functionality - Coming soon!");
    }
    
    private static void returnBook() {
        System.out.println("🚧 Return book functionality - Coming soon!");
    }
    
    private static void viewOverdueBooks() {
        System.out.println("🚧 View overdue books functionality - Coming soon!");
    }
    
    private static void checkMemberBorrowingStatus() {
        System.out.println("🚧 Check member borrowing status functionality - Coming soon!");
    }
    
    private static void generateReports() {
        System.out.println("🚧 Generate reports functionality - Coming soon!");
    }
    
    /**
     * Cleanup resources before exit
     */
    private static void cleanup() {
        try {
            if (scanner != null) {
                scanner.close();
            }
            System.out.println("✅ Resources cleaned up successfully.");
        } catch (Exception e) {
            System.out.println("⚠️  Warning: Error during cleanup: " + e.getMessage());
        }
    }
}
