#!/bin/bash

# Library Management System Deployment Script
echo "🚀 Library Management System - Deployment Script"
echo "=================================================="

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install Docker first."
    exit 1
fi

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose is not installed. Please install Docker Compose first."
    exit 1
fi

echo "✅ Docker and Docker Compose are installed"

# Build and start the application
echo "🔨 Building and starting the application..."
docker-compose up --build -d

# Wait for services to be ready
echo "⏳ Waiting for services to start..."
sleep 10

# Check if services are running
if docker-compose ps | grep -q "Up"; then
    echo "✅ Application deployed successfully!"
    echo ""
    echo "🌐 Frontend Web Interface: http://localhost:3000"
    echo "📊 Application Dashboard: http://localhost:3000#dashboard"
    echo "📚 Books Management: http://localhost:3000#books"
    echo "👥 Members Management: http://localhost:3000#members"
    echo ""
    echo "🔧 Management Commands:"
    echo "  View logs: docker-compose logs -f"
    echo "  Stop app:  docker-compose down"
    echo "  Restart:   docker-compose restart"
    echo ""
    echo "📱 To access from other devices on your network:"
    echo "  Find your IP: ip route | grep default"
    echo "  Access via: http://YOUR_IP:3000"
else
    echo "❌ Failed to start services. Check logs with: docker-compose logs"
    exit 1
fi
