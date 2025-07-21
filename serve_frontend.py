#!/usr/bin/env python3
"""
Simple HTTP Server for Library Management Frontend
Serves the frontend files with proper MIME types
"""

import http.server
import socketserver
import os
import webbrowser
from pathlib import Path

# Configuration
PORT = 3000
FRONTEND_DIR = "frontend"

class CustomHTTPRequestHandler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=FRONTEND_DIR, **kwargs)
    
    def end_headers(self):
        # Add CORS headers for API calls
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS')
        self.send_header('Access-Control-Allow-Headers', 'Content-Type, Authorization')
        super().end_headers()

def main():
    # Change to the project directory
    project_dir = Path(__file__).parent
    os.chdir(project_dir)
    
    # Check if frontend directory exists
    if not os.path.exists(FRONTEND_DIR):
        print(f"Error: {FRONTEND_DIR} directory not found!")
        return
    
    # Create server
    handler = CustomHTTPRequestHandler
    
    try:
        with socketserver.TCPServer(("", PORT), handler) as httpd:
            print("=" * 60)
            print("    Library Management System - Frontend Server")
            print("=" * 60)
            print(f"Server running at: http://localhost:{PORT}")
            print(f"Serving directory: {os.path.abspath(FRONTEND_DIR)}")
            print("\nAvailable pages:")
            print(f"  📊 Dashboard:     http://localhost:{PORT}")
            print(f"  📚 Books:         http://localhost:{PORT}#books")
            print(f"  👥 Members:       http://localhost:{PORT}#members")
            print(f"  🔄 Transactions:  http://localhost:{PORT}#transactions")
            print(f"  📈 Reports:       http://localhost:{PORT}#reports")
            print("\nPress Ctrl+C to stop the server")
            print("-" * 60)
            
            # Try to open browser automatically
            try:
                webbrowser.open(f'http://localhost:{PORT}')
                print("✅ Browser opened automatically")
            except:
                print("⚠️  Please open your browser and visit the URL above")
            
            print("-" * 60)
            
            # Start serving
            httpd.serve_forever()
            
    except KeyboardInterrupt:
        print("\n" + "=" * 60)
        print("Server stopped. Thank you for using Library Management System!")
        print("=" * 60)
    except OSError as e:
        if e.errno == 48:  # Address already in use
            print(f"Error: Port {PORT} is already in use.")
            print("Please stop any other servers or change the PORT in this script.")
        else:
            print(f"Error starting server: {e}")

if __name__ == "__main__":
    main()
