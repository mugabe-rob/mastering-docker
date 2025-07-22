-- ===================================================================
-- Library Management System - MySQL Database Schema
-- ===================================================================
-- This script creates the complete database structure for the
-- Library Management System with all necessary tables and relationships
-- ===================================================================

-- Create the database
CREATE DATABASE IF NOT EXISTS library_management_system;
USE library_management_system;

-- Drop tables if they exist (for clean setup)
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS book_borrowings;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS members;
DROP TABLE IF EXISTS librarians;
DROP TABLE IF EXISTS persons;

-- ===================================================================
-- 1. PERSONS TABLE (Base table for inheritance)
-- ===================================================================
CREATE TABLE persons (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    person_type ENUM('MEMBER', 'LIBRARIAN') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_person_type (person_type),
    INDEX idx_email (email)
);

-- ===================================================================
-- 2. MEMBERS TABLE (Extends persons)
-- ===================================================================
CREATE TABLE members (
    id VARCHAR(20) PRIMARY KEY,
    membership_date DATE NOT NULL,
    max_books_allowed INT DEFAULT 5,
    is_active BOOLEAN DEFAULT TRUE,
    phone VARCHAR(20),
    address TEXT,
    
    FOREIGN KEY (id) REFERENCES persons(id) ON DELETE CASCADE,
    INDEX idx_membership_date (membership_date),
    INDEX idx_active_status (is_active)
);

-- ===================================================================
-- 3. LIBRARIANS TABLE (Extends persons)
-- ===================================================================
CREATE TABLE librarians (
    id VARCHAR(20) PRIMARY KEY,
    employee_id VARCHAR(20) UNIQUE NOT NULL,
    department VARCHAR(50),
    salary DECIMAL(10,2),
    hire_date DATE DEFAULT (CURRENT_DATE),
    
    FOREIGN KEY (id) REFERENCES persons(id) ON DELETE CASCADE,
    INDEX idx_employee_id (employee_id),
    INDEX idx_department (department)
);

-- ===================================================================
-- 4. BOOKS TABLE
-- ===================================================================
CREATE TABLE books (
    id VARCHAR(20) PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(100) NOT NULL,
    isbn VARCHAR(17) UNIQUE,
    genre VARCHAR(50),
    publication_year YEAR,
    publisher VARCHAR(100),
    total_copies INT DEFAULT 1,
    available_copies INT DEFAULT 1,
    is_available BOOLEAN GENERATED ALWAYS AS (available_copies > 0) STORED,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_title (title),
    INDEX idx_author (author),
    INDEX idx_isbn (isbn),
    INDEX idx_genre (genre),
    INDEX idx_availability (is_available),
    
    CONSTRAINT chk_copies CHECK (available_copies >= 0 AND available_copies <= total_copies)
);

-- ===================================================================
-- 5. BOOK_BORROWINGS TABLE (Track borrowing history)
-- ===================================================================
CREATE TABLE book_borrowings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    member_id VARCHAR(20) NOT NULL,
    book_id VARCHAR(20) NOT NULL,
    librarian_id VARCHAR(20),
    borrow_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE NULL,
    is_returned BOOLEAN DEFAULT FALSE,
    is_overdue BOOLEAN GENERATED ALWAYS AS (
        CASE 
            WHEN is_returned = FALSE AND CURDATE() > due_date THEN TRUE 
            ELSE FALSE 
        END
    ) STORED,
    fine_amount DECIMAL(8,2) DEFAULT 0.00,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE RESTRICT,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE RESTRICT,
    FOREIGN KEY (librarian_id) REFERENCES librarians(id) ON DELETE SET NULL,
    
    INDEX idx_member_borrow (member_id, is_returned),
    INDEX idx_book_borrow (book_id, is_returned),
    INDEX idx_borrow_date (borrow_date),
    INDEX idx_due_date (due_date),
    INDEX idx_overdue (is_overdue),
    
    UNIQUE KEY unique_active_borrowing (member_id, book_id, is_returned)
);

-- ===================================================================
-- 6. TRANSACTIONS TABLE (Audit log for all library operations)
-- ===================================================================
CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    transaction_type ENUM('BOOK_ADDED', 'BOOK_BORROWED', 'BOOK_RETURNED', 'MEMBER_ADDED', 'MEMBER_UPDATED', 'FINE_PAID') NOT NULL,
    entity_type ENUM('BOOK', 'MEMBER', 'LIBRARIAN', 'BORROWING') NOT NULL,
    entity_id VARCHAR(20) NOT NULL,
    performed_by VARCHAR(20),
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    details JSON,
    
    INDEX idx_transaction_type (transaction_type),
    INDEX idx_entity (entity_type, entity_id),
    INDEX idx_transaction_date (transaction_date),
    INDEX idx_performed_by (performed_by)
);

-- ===================================================================
-- 7. LIBRARY_SETTINGS TABLE (System configuration)
-- ===================================================================
CREATE TABLE library_settings (
    setting_key VARCHAR(50) PRIMARY KEY,
    setting_value TEXT NOT NULL,
    description TEXT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ===================================================================
-- VIEWS FOR EASY DATA ACCESS
-- ===================================================================

-- View: Complete member information
CREATE VIEW v_members_full AS
SELECT 
    p.id,
    p.name,
    p.email,
    m.membership_date,
    m.max_books_allowed,
    m.is_active,
    m.phone,
    m.address,
    COUNT(bb.id) as total_borrowed,
    COUNT(CASE WHEN bb.is_returned = FALSE THEN 1 END) as currently_borrowed,
    COUNT(CASE WHEN bb.is_overdue = TRUE THEN 1 END) as overdue_books
FROM persons p
JOIN members m ON p.id = m.id
LEFT JOIN book_borrowings bb ON m.id = bb.member_id
WHERE p.person_type = 'MEMBER'
GROUP BY p.id, p.name, p.email, m.membership_date, m.max_books_allowed, m.is_active, m.phone, m.address;

-- View: Complete librarian information
CREATE VIEW v_librarians_full AS
SELECT 
    p.id,
    p.name,
    p.email,
    l.employee_id,
    l.department,
    l.salary,
    l.hire_date
FROM persons p
JOIN librarians l ON p.id = l.id
WHERE p.person_type = 'LIBRARIAN';

-- View: Books with borrowing status
CREATE VIEW v_books_status AS
SELECT 
    b.id,
    b.title,
    b.author,
    b.isbn,
    b.genre,
    b.total_copies,
    b.available_copies,
    b.is_available,
    COUNT(bb.id) as total_times_borrowed,
    COUNT(CASE WHEN bb.is_returned = FALSE THEN 1 END) as currently_borrowed_count
FROM books b
LEFT JOIN book_borrowings bb ON b.id = bb.book_id
GROUP BY b.id, b.title, b.author, b.isbn, b.genre, b.total_copies, b.available_copies, b.is_available;

-- View: Current active borrowings
CREATE VIEW v_active_borrowings AS
SELECT 
    bb.id as borrowing_id,
    bb.member_id,
    p.name as member_name,
    bb.book_id,
    b.title as book_title,
    b.author as book_author,
    bb.borrow_date,
    bb.due_date,
    bb.is_overdue,
    DATEDIFF(CURDATE(), bb.due_date) as days_overdue,
    bb.fine_amount
FROM book_borrowings bb
JOIN members m ON bb.member_id = m.id
JOIN persons p ON m.id = p.id
JOIN books b ON bb.book_id = b.id
WHERE bb.is_returned = FALSE;

-- View: Overdue books
CREATE VIEW v_overdue_books AS
SELECT 
    bb.id as borrowing_id,
    bb.member_id,
    p.name as member_name,
    p.email as member_email,
    bb.book_id,
    b.title as book_title,
    b.author as book_author,
    bb.borrow_date,
    bb.due_date,
    DATEDIFF(CURDATE(), bb.due_date) as days_overdue,
    bb.fine_amount
FROM book_borrowings bb
JOIN members m ON bb.member_id = m.id
JOIN persons p ON m.id = p.id
JOIN books b ON bb.book_id = b.id
WHERE bb.is_returned = FALSE AND bb.is_overdue = TRUE;

-- ===================================================================
-- STORED PROCEDURES
-- ===================================================================

-- Procedure: Borrow a book
DELIMITER //
CREATE PROCEDURE sp_borrow_book(
    IN p_member_id VARCHAR(20),
    IN p_book_id VARCHAR(20),
    IN p_librarian_id VARCHAR(20),
    IN p_days_to_return INT DEFAULT 14
)
BEGIN
    DECLARE v_available_copies INT;
    DECLARE v_member_current_books INT;
    DECLARE v_member_max_books INT;
    DECLARE v_due_date DATE;
    
    -- Check if book is available
    SELECT available_copies INTO v_available_copies 
    FROM books WHERE id = p_book_id;
    
    IF v_available_copies <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Book is not available';
    END IF;
    
    -- Check member borrowing limits
    SELECT COUNT(*), max_books_allowed 
    INTO v_member_current_books, v_member_max_books
    FROM book_borrowings bb
    JOIN members m ON bb.member_id = m.id
    WHERE bb.member_id = p_member_id AND bb.is_returned = FALSE
    GROUP BY m.max_books_allowed;
    
    IF v_member_current_books >= v_member_max_books THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Member has reached maximum borrowing limit';
    END IF;
    
    -- Calculate due date
    SET v_due_date = DATE_ADD(CURDATE(), INTERVAL p_days_to_return DAY);
    
    -- Start transaction
    START TRANSACTION;
    
    -- Create borrowing record
    INSERT INTO book_borrowings (member_id, book_id, librarian_id, borrow_date, due_date)
    VALUES (p_member_id, p_book_id, p_librarian_id, CURDATE(), v_due_date);
    
    -- Update book availability
    UPDATE books 
    SET available_copies = available_copies - 1 
    WHERE id = p_book_id;
    
    -- Log transaction
    INSERT INTO transactions (transaction_type, entity_type, entity_id, performed_by, details)
    VALUES ('BOOK_BORROWED', 'BORROWING', LAST_INSERT_ID(), p_librarian_id, 
            JSON_OBJECT('member_id', p_member_id, 'book_id', p_book_id, 'due_date', v_due_date));
    
    COMMIT;
END //
DELIMITER ;

-- Procedure: Return a book
DELIMITER //
CREATE PROCEDURE sp_return_book(
    IN p_member_id VARCHAR(20),
    IN p_book_id VARCHAR(20),
    IN p_librarian_id VARCHAR(20)
)
BEGIN
    DECLARE v_borrowing_id INT;
    DECLARE v_due_date DATE;
    DECLARE v_fine_amount DECIMAL(8,2) DEFAULT 0.00;
    DECLARE v_days_overdue INT DEFAULT 0;
    
    -- Find active borrowing
    SELECT id, due_date 
    INTO v_borrowing_id, v_due_date
    FROM book_borrowings 
    WHERE member_id = p_member_id AND book_id = p_book_id AND is_returned = FALSE
    LIMIT 1;
    
    IF v_borrowing_id IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No active borrowing found for this book and member';
    END IF;
    
    -- Calculate fine if overdue
    SET v_days_overdue = DATEDIFF(CURDATE(), v_due_date);
    IF v_days_overdue > 0 THEN
        SET v_fine_amount = v_days_overdue * 1.00; -- $1 per day overdue
    END IF;
    
    -- Start transaction
    START TRANSACTION;
    
    -- Update borrowing record
    UPDATE book_borrowings 
    SET is_returned = TRUE, 
        return_date = CURDATE(),
        fine_amount = v_fine_amount
    WHERE id = v_borrowing_id;
    
    -- Update book availability
    UPDATE books 
    SET available_copies = available_copies + 1 
    WHERE id = p_book_id;
    
    -- Log transaction
    INSERT INTO transactions (transaction_type, entity_type, entity_id, performed_by, details)
    VALUES ('BOOK_RETURNED', 'BORROWING', v_borrowing_id, p_librarian_id,
            JSON_OBJECT('member_id', p_member_id, 'book_id', p_book_id, 'fine_amount', v_fine_amount));
    
    COMMIT;
    
    -- Return fine amount for reference
    SELECT v_fine_amount as fine_amount, v_days_overdue as days_overdue;
END //
DELIMITER ;

-- ===================================================================
-- TRIGGERS
-- ===================================================================

-- Trigger: Update book availability when borrowing
DELIMITER //
CREATE TRIGGER tr_after_borrowing_insert
AFTER INSERT ON book_borrowings
FOR EACH ROW
BEGIN
    UPDATE books 
    SET available_copies = available_copies - 1 
    WHERE id = NEW.book_id;
END //
DELIMITER ;

-- Trigger: Update book availability when returning
DELIMITER //
CREATE TRIGGER tr_after_borrowing_return
AFTER UPDATE ON book_borrowings
FOR EACH ROW
BEGIN
    IF NEW.is_returned = TRUE AND OLD.is_returned = FALSE THEN
        UPDATE books 
        SET available_copies = available_copies + 1 
        WHERE id = NEW.book_id;
    END IF;
END //
DELIMITER ;

-- ===================================================================
-- INSERT SAMPLE DATA
-- ===================================================================

-- Insert library settings
INSERT INTO library_settings (setting_key, setting_value, description) VALUES
('max_borrow_days', '14', 'Default number of days for book borrowing'),
('fine_per_day', '1.00', 'Fine amount per day for overdue books'),
('max_renewals', '2', 'Maximum number of times a book can be renewed'),
('library_name', 'Central Library Management System', 'Name of the library'),
('library_address', '123 Library Street, Knowledge City', 'Library address'),
('library_phone', '+1-555-LIBRARY', 'Library contact phone');

-- Insert sample persons (base data for members and librarians)
INSERT INTO persons (id, name, email, person_type) VALUES
('M001', 'John Doe', 'john.doe@email.com', 'MEMBER'),
('M002', 'Jane Smith', 'jane.smith@email.com', 'MEMBER'),
('M003', 'Robert Johnson', 'robert.j@email.com', 'MEMBER'),
('M004', 'Alice Cooper', 'alice.cooper@email.com', 'MEMBER'),
('M005', 'David Wilson', 'david.wilson@email.com', 'MEMBER'),
('L001', 'Sarah Brown', 'sarah.brown@library.com', 'LIBRARIAN'),
('L002', 'Michael Davis', 'michael.davis@library.com', 'LIBRARIAN'),
('L003', 'Emily Johnson', 'emily.johnson@library.com', 'LIBRARIAN');

-- Insert sample members
INSERT INTO members (id, membership_date, max_books_allowed, is_active, phone, address) VALUES
('M001', '2023-01-15', 5, TRUE, '+1-555-0101', '123 Main St, Anytown, ST 12345'),
('M002', '2023-02-20', 5, TRUE, '+1-555-0102', '456 Oak Ave, Somewhere, ST 12346'),
('M003', '2023-03-10', 3, TRUE, '+1-555-0103', '789 Pine Rd, Elsewhere, ST 12347'),
('M004', '2023-04-05', 5, TRUE, '+1-555-0104', '321 Elm St, Nowhere, ST 12348'),
('M005', '2023-05-12', 5, FALSE, '+1-555-0105', '654 Maple Dr, Anywhere, ST 12349');

-- Insert sample librarians
INSERT INTO librarians (id, employee_id, department, salary, hire_date) VALUES
('L001', 'EMP001', 'Circulation', 45000.00, '2022-06-01'),
('L002', 'EMP002', 'Reference', 52000.00, '2022-08-15'),
('L003', 'EMP003', 'Administration', 65000.00, '2021-03-01');

-- Insert sample books
INSERT INTO books (id, title, author, isbn, genre, publication_year, publisher, total_copies, available_copies) VALUES
('B001', 'The Great Gatsby', 'F. Scott Fitzgerald', '978-0-7432-7356-5', 'Fiction', 1925, 'Scribner', 3, 3),
('B002', 'To Kill a Mockingbird', 'Harper Lee', '978-0-06-112008-4', 'Fiction', 1960, 'J.B. Lippincott & Co.', 2, 2),
('B003', '1984', 'George Orwell', '978-0-452-28423-4', 'Dystopian Fiction', 1949, 'Secker & Warburg', 4, 4),
('B004', 'Pride and Prejudice', 'Jane Austen', '978-0-14-143951-8', 'Romance', 1813, 'T. Egerton', 2, 2),
('B005', 'The Catcher in the Rye', 'J.D. Salinger', '978-0-316-76948-0', 'Fiction', 1951, 'Little, Brown and Company', 2, 2),
('B006', 'Atomic Habits', 'James Clear', '978-0-7352-1129-2', 'Self-Help', 2018, 'Avery', 3, 3),
('B007', 'The Midnight Library', 'Matt Haig', '978-0-525-55947-4', 'Fiction', 2020, 'Viking', 2, 2),
('B008', 'Educated', 'Tara Westover', '978-0-399-59050-4', 'Memoir', 2018, 'Random House', 2, 2),
('B009', 'The Alchemist', 'Paulo Coelho', '978-0-06-112241-5', 'Fiction', 1988, 'HarperCollins', 3, 3),
('B010', 'Dune', 'Frank Herbert', '978-0-441-17271-9', 'Science Fiction', 1965, 'Chilton Books', 2, 2);

-- Insert some sample borrowings
CALL sp_borrow_book('M001', 'B001', 'L001', 14);
CALL sp_borrow_book('M002', 'B003', 'L001', 14);
CALL sp_borrow_book('M003', 'B006', 'L002', 21);

-- ===================================================================
-- USEFUL QUERIES FOR TESTING
-- ===================================================================

-- Show all tables
-- SHOW TABLES;

-- Check member information
-- SELECT * FROM v_members_full;

-- Check book status
-- SELECT * FROM v_books_status;

-- Check active borrowings
-- SELECT * FROM v_active_borrowings;

-- Check overdue books
-- SELECT * FROM v_overdue_books;

-- Check library statistics
/*
SELECT 
    'Total Books' as metric, COUNT(*) as value FROM books
UNION ALL
SELECT 
    'Available Books', SUM(available_copies) FROM books
UNION ALL
SELECT 
    'Total Members', COUNT(*) FROM members WHERE is_active = TRUE
UNION ALL
SELECT 
    'Active Borrowings', COUNT(*) FROM book_borrowings WHERE is_returned = FALSE
UNION ALL
SELECT 
    'Overdue Books', COUNT(*) FROM book_borrowings WHERE is_returned = FALSE AND is_overdue = TRUE;
*/

-- ===================================================================
-- END OF SCHEMA
-- ===================================================================

COMMIT;
