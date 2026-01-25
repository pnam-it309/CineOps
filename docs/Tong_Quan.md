# Tổng Quan Dự Án CineOps

## 1. Giới thiệu
**CineOps** là hệ thống quản lý rạp chiếu phim hiện đại, bao gồm:
- **Frontend (FE)**: Xây dựng bằng Vue.js 3, cung cấp giao diện người dùng tương tác cao cho cả Khách hàng và Quản trị viên.
- **Backend (BE)**: Xây dựng bằng Spring Boot 3, cung cấp API RESTful mạnh mẽ, bảo mật và khả năng mở rộng.

## 2. Sơ đồ Kiến trúc
```mermaid
graph LR
    User[Người dùng] --> FE[Frontend (Port 3456)]
    FE -->|API Request (JSON)| BE[Backend (Port 8888)]
    BE -->|Query| DB[(MySQL Database)]
    BE -->|Email Service| Gmail[SMTP Gmail]
    BE -->|File Storage| Cloudinary[Cloudinary]
```

## 3. Quy trình Khởi chạy Hệ thống

### Bước 1: Khởi chạy Database
Đảm bảo **MySQL** đang chạy ở cổng `3306`.
Hệ thống sẽ tự động tạo database `cineops` nếu chưa tồn tại (nhờ cấu hình `createDatabaseIfNotExist=true`).

### Bước 2: Khởi chạy Backend
1. Mở terminal tại thư mục `BE`.
2. Đảm bảo file `.env` đã được cấu hình đúng.
3. Chạy lệnh:
   ```bash
   ./gradlew bootRun
   ```
4. Backend sẽ khởi động tại `http://localhost:8888`.
5. Tài liệu API (Swagger) có tại: `http://localhost:8888/swagger-ui.html`.

### Bước 3: Khởi chạy Frontend
1. Mở terminal tại thư mục `FE`.
2. Cài đặt thư viện (nếu chưa):
   ```bash
   npm install
   ```
3. Chạy ứng dụng:
   ```bash
   npm run dev
   ```
4. Frontend sẽ khởi động tại `http://localhost:3456`.

## 4. Tài khoản Mặc định (Seeding)
Khi khởi chạy lần đầu với `GENERATE_DB=true`:
- **Admin**: `admin@cineops.com` / (Mật khẩu mặc định trong code hoặc Database)
