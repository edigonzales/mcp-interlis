# syntax=docker/dockerfile:1

# Build stage: compile the application using the Gradle wrapper and JDK 21
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

# Copy build configuration and sources
COPY gradle/ gradle/
COPY gradlew settings.gradle build.gradle ./
COPY src/ src/

# Ensure the Gradle wrapper is executable and build the application
RUN chmod +x gradlew \
    && ./gradlew bootJar --no-daemon

# Runtime stage: use a slim JRE to execute the MCP server
FROM eclipse-temurin:21-jre
WORKDIR /app

# Create an unprivileged user that will run the MCP server
RUN useradd --create-home --shell /usr/sbin/nologin appuser

# Copy the built jar from the build stage and assign it to the app user
COPY --from=build --chown=appuser:appuser /workspace/build/libs/mcp-interlis.jar ./mcp-interlis.jar

USER appuser

# Keep the Java process attached to STDIN/STDOUT so MCP clients can communicate via stdio
ENTRYPOINT ["java", "-jar", "/app/mcp-interlis.jar"]
