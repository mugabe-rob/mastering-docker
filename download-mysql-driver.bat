@echo off
echo 📥 MySQL JDBC Driver Download & Setup
echo =====================================

REM Create lib directory if it doesn't exist
if not exist "lib" mkdir lib

echo.
echo 🔗 Downloading MySQL JDBC Driver...
echo.

REM Check if curl is available (Windows 10+)
curl --version >nul 2>&1
if %errorlevel% equ 0 (
    echo Using curl to download MySQL JDBC driver...
    curl -L -o "lib\mysql-connector-java-8.0.33.jar" "https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.33/mysql-connector-java-8.0.33.jar"
    
    if exist "lib\mysql-connector-java-8.0.33.jar" (
        echo ✅ MySQL JDBC driver downloaded successfully!
    ) else (
        echo ❌ Download failed with curl
        goto manual_download
    )
) else (
    echo curl not available, checking for PowerShell...
    goto powershell_download
)

goto setup_classpath

:powershell_download
echo Using PowerShell to download MySQL JDBC driver...
powershell -Command "& {Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.33/mysql-connector-java-8.0.33.jar' -OutFile 'lib\mysql-connector-java-8.0.33.jar'}"

if exist "lib\mysql-connector-java-8.0.33.jar" (
    echo ✅ MySQL JDBC driver downloaded successfully!
) else (
    echo ❌ Download failed with PowerShell
    goto manual_download
)

goto setup_classpath

:manual_download
echo.
echo 📋 Manual Download Instructions:
echo ================================
echo 1. Go to: https://dev.mysql.com/downloads/connector/j/
echo 2. Download MySQL Connector/J (JDBC Driver)
echo 3. Extract the JAR file
echo 4. Copy mysql-connector-java-X.X.XX.jar to the lib\ folder
echo 5. Rename it to: mysql-connector-java-8.0.33.jar
echo.
echo Or download directly from Maven repository:
echo https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.33/mysql-connector-java-8.0.33.jar
echo.
pause
goto end

:setup_classpath
echo.
echo 🔧 Setting up classpath...

REM Create a batch file for easy Java compilation and execution
echo @echo off > compile-with-mysql.bat
echo echo Compiling Java files with MySQL JDBC driver... >> compile-with-mysql.bat
echo javac -d bin -cp "src;lib\mysql-connector-java-8.0.33.jar" src\**\*.java src\*.java >> compile-with-mysql.bat
echo if %%errorlevel%% equ 0 ( >> compile-with-mysql.bat
echo     echo ✅ Compilation successful! >> compile-with-mysql.bat
echo     echo. >> compile-with-mysql.bat
echo     echo 🚀 Run the application with: >> compile-with-mysql.bat
echo     echo java -cp "bin;src;lib\mysql-connector-java-8.0.33.jar" LibraryApp >> compile-with-mysql.bat
echo ^) else ( >> compile-with-mysql.bat
echo     echo ❌ Compilation failed! >> compile-with-mysql.bat
echo ^) >> compile-with-mysql.bat

echo @echo off > run-with-mysql.bat
echo echo 🚀 Running Library Management System with MySQL... >> run-with-mysql.bat
echo java -cp "bin;src;lib\mysql-connector-java-8.0.33.jar" LibraryApp >> run-with-mysql.bat

echo @echo off > test-database.bat
echo echo 🧪 Testing Database Connection... >> test-database.bat
echo java -cp "bin;src;lib\mysql-connector-java-8.0.33.jar" config.DatabaseConfig >> test-database.bat

echo.
echo ✅ Setup completed!
echo.
echo 📋 Available Commands:
echo =====================
echo • compile-with-mysql.bat   - Compile Java files with MySQL support
echo • run-with-mysql.bat       - Run the application with MySQL
echo • test-database.bat        - Test database connection
echo • setup-database.bat       - Setup MySQL database and tables
echo.
echo 📂 Files Created:
echo ================
echo • lib\mysql-connector-java-8.0.33.jar
echo • compile-with-mysql.bat
echo • run-with-mysql.bat  
echo • test-database.bat
echo.
echo 🔧 Next Steps:
echo =============
echo 1. Run setup-database.bat to create the database
echo 2. Update database credentials in src\config\DatabaseConfig.java
echo 3. Run compile-with-mysql.bat to compile with MySQL support
echo 4. Run test-database.bat to verify connection
echo 5. Run run-with-mysql.bat to start the application
echo.

:end
pause
