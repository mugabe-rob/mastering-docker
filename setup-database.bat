@echo off
echo 🗄️  Library Management System - MySQL Database Setup
echo =====================================================

REM Check if MySQL is installed
mysql --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ MySQL is not installed or not in PATH
    echo 📥 Please install MySQL from: https://dev.mysql.com/downloads/mysql/
    echo 💡 Or install XAMPP from: https://www.apachefriends.org/download.html
    pause
    exit /b 1
)

echo ✅ MySQL is installed

echo.
echo 🔧 Database Setup Options:
echo ========================
echo 1. Create database with sample data (Recommended)
echo 2. Create empty database structure only
echo 3. Reset existing database (WARNING: This will delete all data)
echo 4. Exit
echo.

set /p choice="Enter your choice (1-4): "

if "%choice%"=="1" goto setup_with_data
if "%choice%"=="2" goto setup_empty
if "%choice%"=="3" goto reset_database
if "%choice%"=="4" goto exit
goto invalid_choice

:setup_with_data
echo.
echo 📊 Setting up database with sample data...
echo.
set /p username="Enter MySQL username (default: root): "
if "%username%"=="" set username=root

echo Running database setup...
mysql -u %username% -p < database\library_schema.sql

if %errorlevel% equ 0 (
    echo.
    echo ✅ Database setup completed successfully!
    echo.
    echo 📋 What was created:
    echo ==================
    echo • Database: library_management_system
    echo • Tables: persons, members, librarians, books, book_borrowings, transactions, library_settings
    echo • Views: v_members_full, v_librarians_full, v_books_status, v_active_borrowings, v_overdue_books
    echo • Procedures: sp_borrow_book, sp_return_book
    echo • Sample Data: 10 books, 5 members, 3 librarians, 3 active borrowings
    echo.
    echo 🔗 Connection Details:
    echo =====================
    echo • Host: localhost
    echo • Port: 3306
    echo • Database: library_management_system
    echo • JDBC URL: jdbc:mysql://localhost:3306/library_management_system
    echo.
    echo 🧪 Test your setup:
    echo ==================
    echo mysql -u %username% -p -e "USE library_management_system; SELECT * FROM v_active_borrowings;"
) else (
    echo ❌ Database setup failed. Please check your MySQL credentials and try again.
)
goto end

:setup_empty
echo.
echo 📊 Setting up empty database structure...
echo.
set /p username="Enter MySQL username (default: root): "
if "%username%"=="" set username=root

REM Create a temporary SQL file without sample data
echo Creating temporary schema file...
findstr /v "INSERT INTO" database\library_schema.sql > temp_schema.sql
findstr /v "CALL sp_borrow_book" temp_schema.sql > temp_schema2.sql
move temp_schema2.sql temp_schema.sql

mysql -u %username% -p < temp_schema.sql
del temp_schema.sql

if %errorlevel% equ 0 (
    echo ✅ Empty database structure created successfully!
    echo You can now add your own data.
) else (
    echo ❌ Database setup failed.
)
goto end

:reset_database
echo.
echo ⚠️  WARNING: This will delete ALL existing data!
set /p confirm="Are you sure? (yes/no): "
if not "%confirm%"=="yes" goto end

set /p username="Enter MySQL username (default: root): "
if "%username%"=="" set username=root

echo Dropping existing database...
mysql -u %username% -p -e "DROP DATABASE IF EXISTS library_management_system;"
mysql -u %username% -p < database\library_schema.sql

if %errorlevel% equ 0 (
    echo ✅ Database reset completed!
) else (
    echo ❌ Database reset failed.
)
goto end

:invalid_choice
echo ❌ Invalid choice. Please try again.
pause
goto start

:end
echo.
echo 📚 Next Steps:
echo =============
echo 1. Update your Java application with database connection
echo 2. Add MySQL JDBC driver to your project
echo 3. Configure database credentials in your application
echo.
echo 📖 For more information, check database\README.md
echo.

:exit
pause
