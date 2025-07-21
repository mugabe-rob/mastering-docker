package models;

/**
 * Abstract base class representing a person in the library system
 * Demonstrates abstraction concept
 */
public abstract class Person {
    protected String id;
    protected String name;
    protected String email;
    
    public Person(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
    
    // Encapsulation with getters and setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    // Abstract method to be implemented by subclasses
    public abstract String getPersonType();
    
    // Template method - demonstrates polymorphism
    public String getFullInfo() {
        return String.format("%s - ID: %s, Name: %s, Email: %s", 
                           getPersonType(), id, name, email);
    }
    
    @Override
    public String toString() {
        return getFullInfo();
    }
}
