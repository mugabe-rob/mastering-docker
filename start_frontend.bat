@echo off
echo ============================================
echo    Library Management System - Frontend
echo ============================================

echo.
echo Starting frontend server...
echo.

python serve_frontend.py

if %errorlevel% neq 0 (
    echo.
    echo ============================================
    echo    Failed to start server!
    echo    Please make sure Python is installed.
    echo ============================================
    pause
)
