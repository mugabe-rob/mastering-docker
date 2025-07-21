package models;

import java.time.LocalDate;

/**
 * Librarian class representing a library staff member
 * Demonstrates inheritance and polymorphism
 */
public class Librarian extends Person {
    private String employeeId;
    private LocalDate hireDate;
    private String department;
    private double salary;
    
    // Constructor
    public Librarian(String id, String name, String email, String employeeId, String department) {
        super(id, name, email);
        this.employeeId = employeeId;
        this.hireDate = LocalDate.now();
        this.department = department;
        this.salary = 50000.0; // Default salary
    }
    
    // Constructor with salary
    public Librarian(String id, String name, String email, String employeeId, 
                    String department, double salary) {
        super(id, name, email);
        this.employeeId = employeeId;
        this.hireDate = LocalDate.now();
        this.department = department;
        this.salary = salary;
    }
    
    // Getters and Setters
    public String getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
    
    public LocalDate getHireDate() {
        return hireDate;
    }
    
    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public double getSalary() {
        return salary;
    }
    
    public void setSalary(double salary) {
        this.salary = salary;
    }
    
    // Override abstract method from Person class
    @Override
    public String getPersonType() {
        return "Librarian";
    }
    
    // Override getFullInfo to add librarian-specific information
    @Override
    public String getFullInfo() {
        return String.format("%s, Employee ID: %s, Department: %s, Hire Date: %s, Salary: $%.2f",
                           super.getFullInfo(), employeeId, department, hireDate, salary);
    }
    
    // Business method specific to librarians
    public boolean hasManagementRights() {
        return department.equalsIgnoreCase("Management") || 
               department.equalsIgnoreCase("Administration");
    }
}
