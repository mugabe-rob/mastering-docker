package models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Member class representing a library member
 * Demonstrates inheritance by extending Person class
 */
public class Member extends Person {
    private LocalDate membershipDate;
    private List<String> borrowedBooks;
    private int maxBooksAllowed;
    private boolean isActive;
    
    // Constructor
    public Member(String id, String name, String email) {
        super(id, name, email); // Call parent constructor
        this.membershipDate = LocalDate.now();
        this.borrowedBooks = new ArrayList<>();
        this.maxBooksAllowed = 5; // Default limit
        this.isActive = true;
    }
    
    // Constructor with custom max books
    public Member(String id, String name, String email, int maxBooksAllowed) {
        super(id, name, email);
        this.membershipDate = LocalDate.now();
        this.borrowedBooks = new ArrayList<>();
        this.maxBooksAllowed = maxBooksAllowed;
        this.isActive = true;
    }
    
    // Getters and Setters
    public LocalDate getMembershipDate() {
        return membershipDate;
    }
    
    public void setMembershipDate(LocalDate membershipDate) {
        this.membershipDate = membershipDate;
    }
    
    public List<String> getBorrowedBooks() {
        return new ArrayList<>(borrowedBooks); // Return copy for encapsulation
    }
    
    public int getMaxBooksAllowed() {
        return maxBooksAllowed;
    }
    
    public void setMaxBooksAllowed(int maxBooksAllowed) {
        this.maxBooksAllowed = maxBooksAllowed;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    // Business logic methods
    public boolean canBorrowMoreBooks() {
        return borrowedBooks.size() < maxBooksAllowed && isActive;
    }
    
    public void addBorrowedBook(String bookId) {
        if (!borrowedBooks.contains(bookId)) {
            borrowedBooks.add(bookId);
        }
    }
    
    public void removeBorrowedBook(String bookId) {
        borrowedBooks.remove(bookId);
    }
    
    public int getBorrowedBooksCount() {
        return borrowedBooks.size();
    }
    
    // Override abstract method from Person class - demonstrates polymorphism
    @Override
    public String getPersonType() {
        return "Library Member";
    }
    
    // Override getFullInfo to add member-specific information
    @Override
    public String getFullInfo() {
        return String.format("%s, Membership Date: %s, Books Borrowed: %d/%d, Active: %s",
                           super.getFullInfo(), membershipDate, 
                           borrowedBooks.size(), maxBooksAllowed, isActive);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Member member = (Member) obj;
        return id.equals(member.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
