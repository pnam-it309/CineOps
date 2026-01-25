# Tài Liệu Kỹ Thuật Frontend (FE)

## 1. Công nghệ Sử dụng
- **Framework**: Vue.js 3 (Composition API)
- **Build Tool**: Vite 7
- **Styling**: 
  - **Tailwind CSS 3**: Styling chính (Utility-first).
  - **Bootstrap 5**: Hỗ trợ Grid system và một số component legacy.
- **State Management**: Pinia
- **Routing**: Vue Router 4
- **HTTP Client**: Axios (với Interceptors)

## 2. Cấu trúc Thư mục (`src/`)

- **`assets/`**: Tài nguyên tĩnh.
  - `main.css`: File CSS chính, import Bootstrap trước Tailwind.
- **`components/`**: Vue Components (được Auto-import).
- **`layouts/`**: Các layout chính (DefaultLayout, AdminLayout).
- **`pages/`**: Các trang giao diện (Views).
- **`routes/`**: Cấu hình đường dẫn (Router).
- **`services/`**: Gọi API Backend.
  - `axios.js`: Instance cấu hình sẵn Token & Error Handling.
- **`stores/`**: Quản lý trạng thái toàn cục (Pinia).
  - `auth.js`: Lưu user & token.
- **`utils/`**: Hàm tiện ích.

## 3. Tính năng Nổi bật
- **Auto Imports**: Không cần import thủ công `ref`, `computed`, `axios`, v.v.
- **Centralized API Handling**: Tự động gắn Bearer Token vào mọi request. Tự động logout khi Token hết hạn (401).
- **Hybrid Styling**: Kết hợp sức mạnh Grid của Bootstrap và sự linh hoạt của Tailwind.

## 4. Hướng dẫn Phát triển
### Tạo trang mới
1. Tạo file `.vue` trong `src/pages/`.
2. Khai báo route trong `src/routes/index.js`.

### Gọi API
Sử dụng `src/services/axios.js`:
```javascript
import api from '@/services/axios';

const getUser = async () => {
  try {
    const res = await api.get('/users/profile');
    console.log(res.data);
  } catch (error) {
    console.error(error);
  }
}
```

### Sử dụng Store
```javascript
const authStore = useAuthStore(); // Auto-import store
console.log(authStore.user);
```
