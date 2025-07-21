package services;

import models.Book;
import models.Member;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * File I/O service for saving and loading library data
 * Demonstrates file handling and exception management
 */
public class FileService {
    private static final String BOOKS_FILE = "data/books.txt";
    private static final String MEMBERS_FILE = "data/members.txt";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    
    /**
     * Save books to file
     */
    public static void saveBooks(List<Book> books) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOOKS_FILE))) {
            for (Book book : books) {
                StringBuilder line = new StringBuilder();
                line.append(book.getBookId()).append("|")
                    .append(book.getTitle()).append("|")
                    .append(book.getAuthor()).append("|")
                    .append(book.getIsbn()).append("|")
                    .append(book.isAvailable()).append("|")
                    .append(book.getBorrowedBy() != null ? book.getBorrowedBy() : "").append("|")
                    .append(book.getBorrowDate() != null ? book.getBorrowDate().format(DATE_FORMATTER) : "").append("|")
                    .append(book.getReturnDate() != null ? book.getReturnDate().format(DATE_FORMATTER) : "");
                
                writer.write(line.toString());
                writer.newLine();
            }
            System.out.println("Books data saved successfully to " + BOOKS_FILE);
        } catch (IOException e) {
            System.err.println("Error saving books: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Load books from file
     */
    public static List<Book> loadBooks() throws IOException {
        List<Book> books = new ArrayList<>();
        File file = new File(BOOKS_FILE);
        
        if (!file.exists()) {
            System.out.println("Books file not found. Starting with empty library.");
            return books;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 4) {
                        Book book = new Book(parts[0], parts[1], parts[2], parts[3]);
                        
                        if (parts.length > 4) {
                            book.setAvailable(Boolean.parseBoolean(parts[4]));
                        }
                        if (parts.length > 5 && !parts[5].isEmpty()) {
                            book.setBorrowedBy(parts[5]);
                        }
                        if (parts.length > 6 && !parts[6].isEmpty()) {
                            book.setBorrowDate(LocalDate.parse(parts[6], DATE_FORMATTER));
                        }
                        if (parts.length > 7 && !parts[7].isEmpty()) {
                            book.setReturnDate(LocalDate.parse(parts[7], DATE_FORMATTER));
                        }
                        
                        books.add(book);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing book line: " + line + " - " + e.getMessage());
                }
            }
            System.out.println("Loaded " + books.size() + " books from file.");
        } catch (IOException e) {
            System.err.println("Error loading books: " + e.getMessage());
            throw e;
        }
        
        return books;
    }
    
    /**
     * Save members to file
     */
    public static void saveMembers(List<Member> members) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MEMBERS_FILE))) {
            for (Member member : members) {
                StringBuilder line = new StringBuilder();
                line.append(member.getId()).append("|")
                    .append(member.getName()).append("|")
                    .append(member.getEmail()).append("|")
                    .append(member.getMembershipDate().format(DATE_FORMATTER)).append("|")
                    .append(member.getMaxBooksAllowed()).append("|")
                    .append(member.isActive()).append("|");
                
                // Add borrowed books
                List<String> borrowedBooks = member.getBorrowedBooks();
                for (int i = 0; i < borrowedBooks.size(); i++) {
                    line.append(borrowedBooks.get(i));
                    if (i < borrowedBooks.size() - 1) {
                        line.append(",");
                    }
                }
                
                writer.write(line.toString());
                writer.newLine();
            }
            System.out.println("Members data saved successfully to " + MEMBERS_FILE);
        } catch (IOException e) {
            System.err.println("Error saving members: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Load members from file
     */
    public static List<Member> loadMembers() throws IOException {
        List<Member> members = new ArrayList<>();
        File file = new File(MEMBERS_FILE);
        
        if (!file.exists()) {
            System.out.println("Members file not found. Starting with empty member list.");
            return members;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 3) {
                        Member member = new Member(parts[0], parts[1], parts[2]);
                        
                        if (parts.length > 3 && !parts[3].isEmpty()) {
                            member.setMembershipDate(LocalDate.parse(parts[3], DATE_FORMATTER));
                        }
                        if (parts.length > 4) {
                            member.setMaxBooksAllowed(Integer.parseInt(parts[4]));
                        }
                        if (parts.length > 5) {
                            member.setActive(Boolean.parseBoolean(parts[5]));
                        }
                        if (parts.length > 6 && !parts[6].isEmpty()) {
                            String[] borrowedBooks = parts[6].split(",");
                            for (String bookId : borrowedBooks) {
                                if (!bookId.trim().isEmpty()) {
                                    member.addBorrowedBook(bookId.trim());
                                }
                            }
                        }
                        
                        members.add(member);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing member line: " + line + " - " + e.getMessage());
                }
            }
            System.out.println("Loaded " + members.size() + " members from file.");
        } catch (IOException e) {
            System.err.println("Error loading members: " + e.getMessage());
            throw e;
        }
        
        return members;
    }
    
    /**
     * Create data directory if it doesn't exist
     */
    public static void initializeDataDirectory() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            if (dataDir.mkdirs()) {
                System.out.println("Data directory created successfully.");
            } else {
                System.err.println("Failed to create data directory.");
            }
        }
    }
}
