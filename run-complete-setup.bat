@echo off
title Library Management System - Complete Setup and Run
color 0A

echo.
echo 🏛️  LIBRARY MANAGEMENT SYSTEM - COMPLETE SETUP
echo ================================================================
echo.

REM Step 1: Download MySQL JDBC Driver
echo 📥 Step 1: Downloading MySQL JDBC Driver...
echo ----------------------------------------------------------------
call download-mysql-driver.bat
if errorlevel 1 (
    echo ❌ Failed to download JDBC driver
    pause
    exit /b 1
)

echo.
echo ✅ JDBC driver setup completed!
echo.

REM Step 2: Setup Database
echo 🗄️  Step 2: Setting up MySQL Database...
echo ----------------------------------------------------------------
echo 💡 Make sure MySQL is running before proceeding!
echo.
set /p proceed="Do you want to setup the database now? (y/n): "
if /i "%proceed%"=="y" (
    call setup-database.bat
    if errorlevel 1 (
        echo ❌ Database setup failed
        echo 💡 Please check MySQL connection and try again
        pause
        exit /b 1
    )
) else (
    echo ⚠️  Skipping database setup. Make sure to run setup-database.bat manually!
)

echo.
echo ✅ Database setup completed!
echo.

REM Step 3: Compile Application
echo ⚙️  Step 3: Compiling Java Application...
echo ----------------------------------------------------------------
call compile-with-mysql.bat
if errorlevel 1 (
    echo ❌ Compilation failed
    pause
    exit /b 1
)

echo.
echo ✅ Compilation successful!
echo.

REM Step 4: Test Database Connection
echo 🧪 Step 4: Testing Database Connection...
echo ----------------------------------------------------------------
call test-database.bat
if errorlevel 1 (
    echo ⚠️  Database connection test had issues
    echo 💡 You may need to update credentials in DatabaseConfig.java
) else (
    echo ✅ Database connection test passed!
)

echo.
echo 🚀 Step 5: Choose Application to Run
echo ----------------------------------------------------------------
echo 1. LibraryDatabaseApp   - New database-integrated version (Recommended)
echo 2. LibraryApp          - Original file-based version
echo 3. Exit
echo.

set /p app_choice="Enter your choice (1-3): "

if "%app_choice%"=="1" (
    echo.
    echo 🚀 Starting Library Management System (Database Version)...
    echo ================================================================
    java -cp "bin;src;lib\mysql-connector-java-8.0.33.jar" LibraryDatabaseApp
) else if "%app_choice%"=="2" (
    echo.
    echo 🚀 Starting Library Management System (File Version)...
    echo ================================================================
    java -cp "bin;src;lib\mysql-connector-java-8.0.33.jar" LibraryApp
) else (
    echo 👋 Goodbye!
    goto end
)

:end
echo.
echo 📋 Quick Reference Commands:
echo ===========================
echo • run-database-app.bat     - Run database version directly
echo • run-with-mysql.bat       - Run original version with MySQL support
echo • test-database.bat        - Test database connection
echo • compile-with-mysql.bat   - Recompile with MySQL support
echo.
pause
