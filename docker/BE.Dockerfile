# Stage 1: Build the application
FROM gradle:7.6-jdk17-alpine AS build
WORKDIR /app

# Copy gradle files
COPY --chown=gradle:gradle gradlew build.gradle settings.gradle ./
COPY --chown=gradle:gradle gradle/ ./gradle/

# Build caching layer for dependencies
# Note: In real scenarios, this is a bit trickier with Gradle than with Maven,
# but we'll copy the source and build the jar.
COPY --chown=gradle:gradle src/ ./src/

# Grant execute permission
RUN chmod +x gradlew

# Build JAR file (skip tests as they are run in CI)
RUN ./gradlew build -x test --no-daemon

# Stage 2: Runtime image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Add a non-root user for security
RUN addgroup -S cineops && adduser -S cineops -G cineops
USER cineops

# Copy built jar from the build stage
# Assuming the jar is named based on the project version, typically *.jar
COPY --from=build /app/build/libs/*.jar ./app.jar

# Define port and command
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
