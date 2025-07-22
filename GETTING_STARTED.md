# 🚀 Getting Started with Library Management System

## 📋 Quick Start Guide

### Prerequisites
- ☕ Java 17 or higher
- 🐬 MySQL 8.0 or higher
- 💻 Windows/Linux/macOS

### 🔧 Installation Steps

#### 1. Setup MySQL Database
```bash
# Run the database setup script
setup-database.bat
```

#### 2. Download MySQL JDBC Driver
```bash
# Download and setup JDBC driver
download-mysql-driver.bat
```

#### 3. Configure Database Connection
Edit `src/config/DatabaseConfig.java` with your MySQL credentials:
```java
private static final String URL = "jdbc:mysql://localhost:3306/library_management";
private static final String USERNAME = "your_username";
private static final String PASSWORD = "your_password";
```

#### 4. Compile and Run
```bash
# Compile the project
compile-with-mysql.bat

# Test database connection
test-database.bat

# Run the application
run-with-mysql.bat
```

### 🌐 Frontend Access
Open `frontend/index.html` in your browser or deploy to Netlify.

### 📚 Documentation
- [Database Schema](docs/database-schema.md)
- [API Documentation](docs/api-docs.md)
- [Deployment Guide](docs/deployment.md)

---

## 🗂️ Project Structure

```
java-project/
├── src/                           # Java source code
│   ├── config/                    # Configuration classes
│   ├── models/                    # Data models
│   ├── services/                  # Business logic
│   ├── exceptions/                # Custom exceptions
│   └── LibraryApp.java           # Main application
├── frontend/                      # Web interface
│   ├── index.html                # Main page
│   ├── styles.css               # Styling
│   └── script.js                # JavaScript logic
├── database/                      # Database files
│   └── library_schema.sql        # MySQL schema
├── lib/                          # External libraries
├── bin/                          # Compiled classes
├── docs/                         # Documentation
└── scripts/                      # Utility scripts
```

---

## 🎯 Features

### Core Functionality
- ✅ Member Management
- ✅ Book Catalog
- ✅ Borrowing System
- ✅ Return Processing
- ✅ Fine Calculation
- ✅ Search & Filtering

### Advanced Features
- 📊 Reports & Analytics
- 🔐 User Authentication
- 📱 Responsive Design
- 🌐 Web Interface
- 🗄️ Database Persistence

---

## 🧪 Testing

### Test Database Connection
```bash
test-database.bat
```

### Run Unit Tests
```bash
# If Maven is installed
mvn test

# Manual testing
java -cp "bin;lib/*" TestRunner
```

---

## 🚀 Deployment

### Local Development
1. Follow installation steps above
2. Access at `http://localhost:8080`

### Production Deployment

#### Netlify (Frontend)
1. Push to GitHub repository
2. Connect to Netlify
3. Auto-deploy on commits

#### Backend Deployment
- **Heroku**: Use `Procfile` for deployment
- **AWS**: Deploy to EC2 or Lambda
- **Google Cloud**: Use App Engine or Compute Engine

---

## 🐛 Troubleshooting

### Common Issues

#### Database Connection Failed
```
Solution: Check MySQL service is running
Commands: net start mysql80
```

#### JDBC Driver Not Found
```
Solution: Run download-mysql-driver.bat
```

#### Compilation Errors
```
Solution: Ensure Java 17+ is installed
Commands: java -version
```

#### Port Already in Use
```
Solution: Change port in configuration
File: src/config/DatabaseConfig.java
```

---

## 📞 Support

### Getting Help
- 📧 Create an issue on GitHub
- 💬 Check documentation in `/docs`
- 🔍 Search existing issues

### Contributing
1. Fork the repository
2. Create feature branch
3. Submit pull request

---

## 📄 License
This project is licensed under the MIT License.

---

*Happy coding! 🎉*
