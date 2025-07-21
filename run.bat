@echo off
echo ============================================
echo    Library Management System - Compiler
echo ============================================

echo.
echo Cleaning previous compiled files...
if exist "*.class" del "*.class"
if exist "models\*.class" del "models\*.class"
if exist "services\*.class" del "services\*.class"
if exist "exceptions\*.class" del "exceptions\*.class"

echo.
echo Compiling Java source files...
cd src
javac -d .. *.java models\*.java services\*.java exceptions\*.java

cd ..

if %errorlevel% equ 0 (
    echo.
    echo ============================================
    echo    Compilation successful!
    echo    Starting Library Management System...
    echo ============================================
    echo.
    java LibraryApp
) else (
    echo.
    echo ============================================
    echo    Compilation failed! Please check errors.
    echo ============================================
    pause
)
