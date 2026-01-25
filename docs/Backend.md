# Tài Liệu Kỹ Thuật Backend (BE)

## 1. Công nghệ Sử dụng
- **Ngôn ngữ**: Java 17
- **Framework**: Spring Boot 3.2.2
- **Build Tool**: Gradle
- **Database**: MySQL 8.0
- **Migration**: Flyway
- **Connection Pool**: HikariCP
- **Security**: Spring Security + JWT
- **API Documentation**: SpringDoc OpenAPI (Swagger)

## 2. Cấu trúc Thư mục (`src/main/java`)
Dự án áp dụng kiến trúc **Domain-Driven Design (DDD)** tinh gọn:

- **`service/`**: Chứa các module nghiệp vụ chính.
  - **`cinema/`**: Module quản lý rạp.
    - **`be/`**:
      - **`core/`**: Chứa các interface, abstract class dùng chung.
      - **`entity/`**: Các thực thể JPA mapping với Database.
      - **`repository/`**: Layer truy xuất dữ liệu (Spring Data JPA).
      - **`infrastructure/`**: Các thành phần hạ tầng:
        - `config/`: Cấu hình (Security, Swagger, Cors).
        - `security/`: Xử lý xác thực, phân quyền (JWT Filter).
        - `exception/`: Xử lý lỗi tập trung (@ControllerAdvice).
      - **`utils/`**: Các hàm tiện ích.

## 3. Cấu hình Biến Môi trường (`.env`)
Backend yêu cầu các biến môi trường sau để hoạt động an toàn:

| Biến | Mô tả | Mặc định/Ví dụ |
|------|-------|----------------|
| `SERVER_PORT` | Cổng chạy server | `8888` |
| `DB_URL` | Đường dẫn kết nối DB | `jdbc:mysql://localhost:3306/cineops...` |
| `DB_USERNAME` | Tài khoản DB | `root` |
| `DB_PASSWORD` | Mật khẩu DB | `...` |
| `JWT_SECRET` | Khóa bí mật mã hóa Token | (Chuỗi ngẫu nhiên dài) |
| `FRONTEND_URL` | URL Frontend (CORS) | `http://localhost:3456` |
| `CLOUDINARY_*` | Cấu hình lưu trữ ảnh | ... |
| `MAIL_*` | Cấu hình gửi mail | Gmail SMTP |

## 4. Các Tính năng Chính
1. **Authentication**: Đăng nhập, Đăng ký, Quên mật khẩu, Refresh Token.
2. **Authorization**: Phân quyền dựa trên Role (ADMIN, USER, STAFF).
3. **Database Migration**: Flyway tự động quản lý version DB tại `src/main/resources/db/migration`.
4. **Data Seeding**: Tự động tạo dữ liệu mẫu khi `GENERATE_DB=true`.
