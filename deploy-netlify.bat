@echo off
echo 🚀 Library Management System - Netlify Deployment Preparation
echo ============================================================

REM Create deployment package
echo 📦 Preparing deployment package...

REM Check if frontend directory exists
if not exist "frontend" (
    echo ❌ Frontend directory not found!
    pause
    exit /b 1
)

echo ✅ Frontend directory found

REM Create a zip file for easy upload
echo 📁 Creating deployment archive...
if exist "library-management-netlify.zip" del "library-management-netlify.zip"

REM Use PowerShell to create zip file
powershell -command "Compress-Archive -Path 'frontend\*' -DestinationPath 'library-management-netlify.zip' -Force"

if exist "library-management-netlify.zip" (
    echo ✅ Deployment package created: library-management-netlify.zip
) else (
    echo ❌ Failed to create deployment package
    pause
    exit /b 1
)

echo.
echo 🌐 NETLIFY DEPLOYMENT INSTRUCTIONS:
echo =====================================
echo.
echo Option 1: Drag & Drop Deployment (Easiest)
echo ------------------------------------------
echo 1. Go to https://app.netlify.com/drop
echo 2. Drag and drop the 'frontend' folder
echo 3. Your site will be live in seconds!
echo.
echo Option 2: Git-based Deployment (Recommended)
echo --------------------------------------------
echo 1. Push your code to GitHub
echo 2. Go to https://netlify.com
echo 3. Click "New site from Git"
echo 4. Connect your GitHub repository
echo 5. Set publish directory to "frontend"
echo 6. Deploy!
echo.
echo Option 3: ZIP Upload
echo -------------------
echo 1. Go to https://app.netlify.com
echo 2. Drag and drop: library-management-netlify.zip
echo 3. Your site will be deployed!
echo.
echo 📱 Your Library Management System will be available at:
echo https://YOUR-SITE-NAME.netlify.app
echo.
echo 🎨 Features included:
echo ✅ Modern teal navigation bar
echo ✅ Responsive design for all devices
echo ✅ Complete library management interface
echo ✅ Dashboard with statistics
echo ✅ Books and members management
echo ✅ Transaction handling
echo ✅ Reports and analytics
echo.
echo 🔧 After deployment, you can:
echo - Change site name in Netlify dashboard
echo - Set up custom domain
echo - Configure redirects
echo - Monitor analytics
echo.

REM Open browser to Netlify
echo 🌐 Opening Netlify in your browser...
start https://app.netlify.com/drop

echo.
echo Ready for deployment! Choose your preferred method above.
pause
