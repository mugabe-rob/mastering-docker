# MySQL Database Setup Commands for Library Management System

## 🚀 Quick Setup Commands

### 1. **Create Database and Tables**
```bash
# Login to MySQL
mysql -u root -p

# Or with specific user
mysql -u your_username -p

# Run the schema file
source /path/to/your/project/database/library_schema.sql

# Or execute directly
mysql -u root -p < library_schema.sql
```

### 2. **One-Line Database Setup**
```bash
# From your project directory
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS library_management_system;" && mysql -u root -p library_management_system < database/library_schema.sql
```

## 📊 **Database Structure Created**

### **Tables:**
1. **`persons`** - Base table for inheritance (members + librarians)
2. **`members`** - Library members information
3. **`librarians`** - Library staff information  
4. **`books`** - Book catalog with availability tracking
5. **`book_borrowings`** - Borrowing history and active loans
6. **`transactions`** - Audit log for all operations
7. **`library_settings`** - System configuration

### **Views:**
- **`v_members_full`** - Complete member information with borrowing stats
- **`v_librarians_full`** - Complete librarian information
- **`v_books_status`** - Books with borrowing statistics
- **`v_active_borrowings`** - Current active book loans
- **`v_overdue_books`** - Overdue books with member details

### **Stored Procedures:**
- **`sp_borrow_book(member_id, book_id, librarian_id, days)`** - Handle book borrowing
- **`sp_return_book(member_id, book_id, librarian_id)`** - Handle book returns with fine calculation

## 🔧 **Configuration Options**

### **Database Settings:**
- **Host:** localhost (default)
- **Port:** 3306 (default)
- **Database:** library_management_system
- **Charset:** utf8mb4

### **Sample Connection Strings:**
```properties
# Java JDBC
jdbc:mysql://localhost:3306/library_management_system?useSSL=false&serverTimezone=UTC

# Spring Boot application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/library_management_system
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

## 📝 **Sample Data Included**

The script automatically creates:
- **8 Sample Books** (Fiction, Non-Fiction, Self-Help, etc.)
- **5 Sample Members** (with contact information)
- **3 Sample Librarians** (different departments)
- **3 Active Borrowings** (for testing)
- **Library Settings** (fine rates, borrowing limits, etc.)

## 🧪 **Test Queries**

### **Check Database Setup:**
```sql
USE library_management_system;
SHOW TABLES;
```

### **View Library Statistics:**
```sql
SELECT 
    'Total Books' as metric, COUNT(*) as value FROM books
UNION ALL
SELECT 'Available Books', SUM(available_copies) FROM books
UNION ALL
SELECT 'Total Members', COUNT(*) FROM members WHERE is_active = TRUE
UNION ALL
SELECT 'Active Borrowings', COUNT(*) FROM book_borrowings WHERE is_returned = FALSE;
```

### **Check Active Borrowings:**
```sql
SELECT * FROM v_active_borrowings;
```

### **Check Member Information:**
```sql
SELECT * FROM v_members_full;
```

## 🔐 **Security Features**

- **Foreign Key Constraints** - Data integrity
- **Check Constraints** - Valid data ranges  
- **Unique Constraints** - Prevent duplicates
- **Generated Columns** - Automatic calculations
- **Triggers** - Automatic updates
- **Views** - Secure data access

## 📈 **Performance Features**

- **Indexes** on frequently queried columns
- **Generated columns** for computed values
- **Optimized queries** in views
- **Efficient foreign key relationships**

## 🛠 **Maintenance Commands**

### **Backup Database:**
```bash
mysqldump -u root -p library_management_system > library_backup.sql
```

### **Restore Database:**
```bash
mysql -u root -p library_management_system < library_backup.sql
```

### **Reset Database:**
```bash
mysql -u root -p -e "DROP DATABASE IF EXISTS library_management_system;"
mysql -u root -p < database/library_schema.sql
```
