# Multi-stage Docker build for Library Management System
# Stage 1: Build Java application
FROM openjdk:17-jdk-slim AS java-builder

WORKDIR /app

# Copy Java source files
COPY src/ ./src/
COPY *.java ./

# Create directories and compile Java files
RUN mkdir -p bin && \
    javac -d bin -cp src src/**/*.java && \
    javac -d bin *.java

# Stage 2: Runtime environment
FROM openjdk:17-jre-slim

WORKDIR /app

# Install Python for frontend server
RUN apt-get update && \
    apt-get install -y python3 python3-pip && \
    rm -rf /var/lib/apt/lists/*

# Copy compiled Java classes
COPY --from=java-builder /app/bin ./bin
COPY --from=java-builder /app/src ./src

# Copy frontend and data files
COPY frontend/ ./frontend/
COPY data/ ./data/
COPY serve_frontend.py ./
COPY *.py ./

# Create startup script
RUN echo '#!/bin/bash\n\
echo "Starting Library Management System..."\n\
echo "Frontend available at: http://localhost:3000"\n\
echo "Java console app can be run with: java -cp \"bin:src\" LibraryApp"\n\
python3 serve_frontend.py &\n\
echo "Press Ctrl+C to stop all services"\n\
wait' > start.sh && chmod +x start.sh

# Expose port for frontend
EXPOSE 3000

# Start the application
CMD ["./start.sh"]
