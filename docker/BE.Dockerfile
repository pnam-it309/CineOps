# Stage 1: Preparation stage
# Sử dụng bản JDK 17 trên nền Ubuntu Jammy để đảm bảo đầy đủ glibc
FROM gradle:7.6-jdk17 AS build
WORKDIR /app

# Switch sang root để cài đặt thư viện hệ thống
USER root

# Cài đặt các thư viện hệ thống cần thiết cho OpenCV (đầy đủ hơn)
RUN apt-get update && apt-get install -y \
    libgl1 \
    libglib2.0-0 \
    libsm6 \
    libxext6 \
    libxrender1 \
    && rm -rf /var/lib/apt/lists/*

# Trở lại user gradle để bảo mật (nếu cần) nhưng vẫn giữ quyền chạy bootRun
# Lưu ý: Trong dev mode, mount volume đôi khi cần quyền root để tránh lỗi permission
# USER gradle

# Copy gradle files only to download dependencies
COPY --chown=gradle:gradle gradlew build.gradle settings.gradle ./
COPY --chown=gradle:gradle gradle/ ./gradle/

# Grant execute permission
RUN chmod +x gradlew

# Download dependencies (Cache layer)
RUN ./gradlew dependencies --no-daemon

# Stage 2: Production Build stage
FROM build AS build-production
COPY --chown=gradle:gradle src/ ./src/
RUN ./gradlew build -x test --no-daemon

# Stage 3: Runtime image
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Cài đặt thư viện đồ họa tương tự ở bản Runtime
RUN apt-get update && apt-get install -y \
    libgl1 \
    libglib2.0-0 \
    libsm6 \
    libxext6 \
    libxrender1 \
    && rm -rf /var/lib/apt/lists/*

RUN addgroup --system cineops && adduser --system --group cineops
USER cineops
COPY --from=build-production /app/build/libs/*.jar ./app.jar

EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]
