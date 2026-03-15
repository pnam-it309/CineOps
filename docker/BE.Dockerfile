# Stage 1: Build & Cache Dependencies
FROM gradle:7.6-jdk17 AS build
WORKDIR /app

# Cài đặt dos2unix để xử lý file gradlew trên Windows
USER root
RUN apt-get update && apt-get install -y dos2unix && rm -rf /var/lib/apt/lists/*

# Copy gradle files trước để có thể chown
COPY --chown=gradle:gradle gradlew build.gradle settings.gradle ./
COPY --chown=gradle:gradle gradle/ ./gradle/

# Đảm bảo user gradle có quyền trên toàn bộ thư mục app
RUN chown -R gradle:gradle /app

# Normalize line endings và cấp quyền thực thi
RUN dos2unix gradlew && chmod +x gradlew

# Download dependencies (Cache layer)
# Chạy với user gradle để bảo mật
USER gradle
RUN sh ./gradlew dependencies --no-daemon

# Stage 2: Production Build
FROM build AS build-production
USER gradle
COPY --chown=gradle:gradle src/ ./src/
# Build -x test để bỏ qua unit test giúp build nhanh hơn
RUN sh ./gradlew build -x test --no-daemon

# Stage 3: Extract Layers (Tách JAR thành các lớp để tối ưu Docker Cache)
FROM eclipse-temurin:17-jre-jammy AS extractor
WORKDIR /app
COPY --from=build-production /app/build/libs/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

# Stage 4: Runtime image (Sử dụng JRE để giảm dung lượng và tăng tốc startup)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Cài đặt các thư viện hệ thống cần thiết cho OpenCV / Graphics
RUN apt-get update && apt-get install -y \
    dos2unix \
    libgl1 \
    libglib2.0-0 \
    libsm6 \
    libxext6 \
    libxrender1 \
    && rm -rf /var/lib/apt/lists/*

# Tạo user hệ thống để chạy app (bảo mật)
RUN addgroup --system cineops && adduser --system --group cineops
USER cineops

# Copy từng layer từ extractor stage
# Thứ tự copy từ ít thay đổi nhất đến nhiều thay đổi nhất
COPY --from=extractor /app/dependencies/ ./
COPY --from=extractor /app/spring-boot-loader/ ./
COPY --from=extractor /app/snapshot-dependencies/ ./
COPY --from=extractor /app/application/ ./

EXPOSE 8888

# Sử dụng JarLauncher để tối ưu hóa việc load các lớp đã tách
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]

