@echo off
REM Library Management System - Windows Deployment Script

echo 🚀 Library Management System - Windows Deployment
echo ==================================================

REM Check if Docker is installed
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Docker is not installed. Please install Docker Desktop first.
    echo 📥 Download from: https://desktop.docker.com/win/main/amd64/Docker%%20Desktop%%20Installer.exe
    pause
    exit /b 1
)

REM Check if Docker Compose is installed
docker-compose --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Docker Compose is not installed. Please install Docker Compose first.
    pause
    exit /b 1
)

echo ✅ Docker and Docker Compose are installed

REM Build and start the application
echo 🔨 Building and starting the application...
docker-compose up --build -d

REM Wait for services to be ready
echo ⏳ Waiting for services to start...
timeout /t 10 /nobreak >nul

REM Check if services are running
docker-compose ps | findstr "Up" >nul
if %errorlevel% equ 0 (
    echo ✅ Application deployed successfully!
    echo.
    echo 🌐 Frontend Web Interface: http://localhost:3000
    echo 📊 Application Dashboard: http://localhost:3000#dashboard
    echo 📚 Books Management: http://localhost:3000#books
    echo 👥 Members Management: http://localhost:3000#members
    echo.
    echo 🔧 Management Commands:
    echo   View logs: docker-compose logs -f
    echo   Stop app:  docker-compose down
    echo   Restart:   docker-compose restart
    echo.
    echo 📱 To access from other devices on your network:
    echo   Find your IP: ipconfig
    echo   Access via: http://YOUR_IP:3000
    echo.
    echo 🌐 Opening browser...
    start http://localhost:3000
) else (
    echo ❌ Failed to start services. Check logs with: docker-compose logs
    pause
    exit /b 1
)

pause
