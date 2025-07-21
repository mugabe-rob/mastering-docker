# Library Management System

A comprehensive Java-based Library Management System that demonstrates all essential Java programming concepts including Object-Oriented Programming principles, exception handling, file I/O, and collections.

## Project Structure

```
src/
├── LibraryApp.java              # Main application with console interface
├── models/
│   ├── Person.java              # Abstract base class
│   ├── Member.java              # Library member class (inherits Person)
│   ├── Librarian.java           # Librarian class (inherits Person)
│   ├── Book.java                # Book entity class
│   └── LibraryOperations.java   # Interface for library operations
├── services/
│   ├── LibraryManagementSystem.java  # Main service class
│   └── FileService.java             # File I/O operations
└── exceptions/
    ├── BookNotFoundException.java
    ├── MemberNotFoundException.java
    └── BookUnavailableException.java

data/
├── books.txt                    # Persistent storage for books
└── members.txt                  # Persistent storage for members
```

## Java Concepts Demonstrated

### 1. Object-Oriented Programming (OOP)

#### Classes and Objects
- **Book**: Represents a book entity with properties and behaviors
- **Member**: Represents library members with borrowing capabilities
- **Librarian**: Represents library staff with management capabilities

#### Inheritance
- **Person** (abstract base class)
  - **Member** extends Person
  - **Librarian** extends Person
- Demonstrates code reuse and "is-a" relationships

#### Polymorphism
- Method overriding in Member and Librarian classes
- Interface implementation in LibraryManagementSystem
- Runtime polymorphism with Person references

#### Encapsulation
- Private fields with public getter/setter methods
- Data hiding and controlled access to object properties
- Defensive copying in collection getters

#### Abstraction
- **Person** abstract class with abstract methods
- **LibraryOperations** interface defining contracts
- Abstract away implementation details

### 2. Exception Handling
- Custom exceptions for domain-specific errors
- Try-catch blocks for error management
- Proper exception propagation and handling

### 3. Collections Framework
- **ArrayList**: Dynamic arrays for storing books and members
- **HashMap**: Fast lookups for books and members by ID
- **List**: Interface usage for flexibility
- Stream API for filtering and processing collections

### 4. File I/O Operations
- Reading from and writing to text files
- Persistent data storage
- Error handling for file operations
- Data serialization and deserialization

### 5. Additional Java Features
- Packages for code organization
- Static methods and variables
- String manipulation and formatting
- Date and time handling with LocalDate
- Scanner for console input
- StringBuilder for efficient string building

## Features

### Core Functionality
1. **Book Management**
   - Add new books to the library
   - View all books, available books, and borrowed books
   - Search books by title or author

2. **Member Management**
   - Register new library members
   - View member information
   - Track member borrowing history

3. **Borrowing System**
   - Borrow available books
   - Return borrowed books
   - Track due dates and overdue books
   - Enforce borrowing limits

4. **Search and Reports**
   - Search functionality for books and members
   - Library statistics and reports
   - Overdue book tracking

5. **Data Persistence**
   - Save and load data from files
   - Automatic data loading on startup
   - Data backup and restoration

### Business Rules
- Members can borrow up to 5 books by default (configurable)
- Books have a 14-day borrowing period
- System tracks overdue books
- Prevents borrowing unavailable books
- Validates member and book existence

## How to Run

### Prerequisites
- Java 8 or higher
- Command line access

### Compilation and Execution

1. **Navigate to the project directory:**
   ```bash
   cd "m:\PROJECTS\MTN_Rwanda Projects\java-project"
   ```

2. **Compile all Java files:**
   ```bash
   javac -d . src\*.java src\models\*.java src\services\*.java src\exceptions\*.java
   ```

3. **Run the application:**
   ```bash
   java LibraryApp
   ```

### Using the Application

1. **Main Menu Navigation:**
   - Use numeric choices to navigate menus
   - Follow on-screen prompts for input

2. **Sample Data:**
   - Application includes sample books and members
   - Use these for testing functionality

3. **Data Persistence:**
   - Data is automatically saved to `data/` directory
   - Files are created automatically if they don't exist

## Sample Usage Scenarios

### Adding a New Book
1. Select "Book Management" → "Add New Book"
2. Enter book details (ID, title, author, ISBN)
3. Book is added to the system

### Borrowing a Book
1. Select "Borrow/Return Books" → "Borrow Book"
2. View available books
3. Enter member ID and book ID
4. System validates and processes the borrowing

### Viewing Reports
1. Select "Reports & Statistics"
2. View library statistics and overdue books
3. Get insights into library usage

## Error Handling Examples

The system handles various error scenarios:
- Attempting to borrow non-existent books
- Borrowing already borrowed books
- Member not found scenarios
- File I/O errors
- Invalid input validation

## Code Quality Features

- **Clean Code**: Meaningful variable and method names
- **Comments**: Comprehensive documentation
- **Modular Design**: Separation of concerns
- **Error Handling**: Robust exception management
- **Input Validation**: User input sanitization
- **Defensive Programming**: Null checks and boundary validations

## Educational Value

This project serves as an excellent learning resource for:
- Java fundamentals and OOP concepts
- Exception handling best practices
- File I/O operations
- Collections framework usage
- Console application development
- Software design patterns
- Clean code principles

## Extensibility

The system is designed for easy extension:
- Add new book types (e.g., eBooks, audiobooks)
- Implement different member types (student, faculty)
- Add reservation functionality
- Implement fine calculation for overdue books
- Add email notification system
- Create a GUI interface

## Contributing

This is an educational project demonstrating Java concepts. Feel free to:
- Add new features
- Improve error handling
- Enhance the user interface
- Add unit tests
- Implement additional design patterns

---

*This project demonstrates practical application of Java programming concepts in a real-world scenario, making it an excellent learning resource for Java developers.*
