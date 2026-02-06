-- 1. BẢNG PHÂN QUYỀN (Role)
CREATE TABLE phan_quyen (
    id VARCHAR(36) PRIMARY KEY,
    ma_phan_quyen VARCHAR(50) NOT NULL UNIQUE,
    ten_vai_tro VARCHAR(100) NOT NULL,
    quyen_han TEXT,
    mo_ta TEXT,
    trang_thai INT DEFAULT 1, -- 1: Hoạt động, 0: Khóa
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME ON UPDATE CURRENT_TIMESTAMP,
    nguoi_cap_nhat VARCHAR(100)
);

-- 2. BẢNG TÀI KHOẢN (Account)
CREATE TABLE tai_khoan (
    id VARCHAR(36) PRIMARY KEY,
    id_phan_quyen VARCHAR(36),
    ten_tai_khoan VARCHAR(100) NOT NULL UNIQUE,
    mat_khau VARCHAR(255) NOT NULL,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_modified_date DATETIME ON UPDATE CURRENT_TIMESTAMP,
    trang_thai INT DEFAULT 1,
    FOREIGN KEY (id_phan_quyen) REFERENCES phan_quyen(id)
);

-- 3. BẢNG KHÁCH HÀNG (Customer)
CREATE TABLE khach_hang (
    id VARCHAR(36) PRIMARY KEY,
    id_tai_khoan VARCHAR(36) UNIQUE,
    ma_khach_hang VARCHAR(50) UNIQUE,
    ten_khach_hang VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    sdt VARCHAR(20),
    gioi_tinh INT, -- 0: Nam, 1: Nữ, 2: Khác
    ngay_sinh DATE,
    hinh_anh VARCHAR(255),
    ghi_chu TEXT,
    trang_thai INT DEFAULT 1,
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME,
    nguoi_cap_nhat VARCHAR(100),
    FOREIGN KEY (id_tai_khoan) REFERENCES tai_khoan(id)
);

-- 4. BẢNG ĐỊA CHỈ (Address - của Khách hàng)
CREATE TABLE dia_chi (
    id VARCHAR(36) PRIMARY KEY,
    id_khach_hang VARCHAR(36),
    id_nhan_vien VARCHAR(36), -- Có thể null nếu là địa chỉ khách
    mo_ta_dia_chi TEXT,
    thanh_pho_tinh VARCHAR(100),
    phuong_xa VARCHAR(100),
    dia_chi_chi_tiet TEXT,
    trang_thai INT DEFAULT 1,
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME,
    nguoi_cap_nhat VARCHAR(100),
    FOREIGN KEY (id_khach_hang) REFERENCES khach_hang(id)
);

-- 5. BẢNG NHÂN VIÊN (Staff)
CREATE TABLE nhan_vien (
    id VARCHAR(36) PRIMARY KEY,
    id_tai_khoan VARCHAR(36) UNIQUE,
    ma_nhan_vien VARCHAR(50) UNIQUE,
    ten_nhan_vien VARCHAR(100) NOT NULL,
    cccd VARCHAR(20),
    ngay_sinh DATE,
    que_quan VARCHAR(255),
    gioi_tinh INT,
    email VARCHAR(100),
    so_dien_thoai VARCHAR(20),
    chuc_vu VARCHAR(100),
    anh_nhan_vien VARCHAR(255),
    trang_thai INT DEFAULT 1,
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME,
    nguoi_cap_nhat VARCHAR(100),
    FOREIGN KEY (id_tai_khoan) REFERENCES tai_khoan(id)
);

-- 6. BẢNG GIAO CA (Shift Handover)
CREATE TABLE giao_ca (
    id VARCHAR(36) PRIMARY KEY,
    id_nhan_vien_trong_ca VARCHAR(36),
    id_nhan_vien_ban_giao VARCHAR(36),
    thoi_gian_nhan_ca DATETIME,
    thoi_gian_giao_ca DATETIME,
    tien_ban_dau DECIMAL(15,2),
    tien_mat_trong_ca DECIMAL(15,2),
    tien_chuyen_khoan DECIMAL(15,2),
    tong_tien_thuc_te DECIMAL(15,2),
    chenh_lech DECIMAL(15,2),
    ghi_chu TEXT,
    trang_thai INT,
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME,
    nguoi_cap_nhat VARCHAR(100),
    FOREIGN KEY (id_nhan_vien_trong_ca) REFERENCES nhan_vien(id)
);

-- 7. BẢNG THỂ LOẠI PHIM (Genre)
CREATE TABLE the_loai (
    id VARCHAR(36) PRIMARY KEY,
    ten_the_loai VARCHAR(100) NOT NULL,
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME,
    nguoi_cap_nhat VARCHAR(100)
);

-- 8. BẢNG PHIM (Movie)
CREATE TABLE phim (
    id VARCHAR(36) PRIMARY KEY,
    ten_phim VARCHAR(255) NOT NULL,
    thoi_luong INT, -- Tính bằng phút
    ngay_khoi_chieu DATE,
    dao_dien VARCHAR(100),
    dien_vien TEXT,
    trailer VARCHAR(255),
    poster VARCHAR(255),
    do_tuoi_gioi_han INT,
    noi_dung TEXT,
    trang_thai INT DEFAULT 1, -- 1: Đang chiếu, 2: Sắp chiếu, 0: Ngưng
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME,
    nguoi_cap_nhat VARCHAR(100)
);

-- 9. BẢNG TRUNG GIAN PHIM - THỂ LOẠI
CREATE TABLE phim_the_loai (
    id VARCHAR(36) PRIMARY KEY,
    id_phim VARCHAR(36),
    id_the_loai VARCHAR(36),
    FOREIGN KEY (id_phim) REFERENCES phim(id),
    FOREIGN KEY (id_the_loai) REFERENCES the_loai(id)
);

-- 10. BẢNG ĐÁNH GIÁ PHIM (Movie Review)
CREATE TABLE danh_gia_phim (
    id VARCHAR(36) PRIMARY KEY,
    id_phim VARCHAR(36),
    id_khach_hang VARCHAR(36),
    diem_so INT, -- 1 đến 5 sao
    noi_dung TEXT,
    ngay_tao_danh_gia DATETIME DEFAULT CURRENT_TIMESTAMP,
    trang_thai INT DEFAULT 1,
    ngay_tao DATETIME,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME,
    nguoi_cap_nhat VARCHAR(100),
    FOREIGN KEY (id_phim) REFERENCES phim(id),
    FOREIGN KEY (id_khach_hang) REFERENCES khach_hang(id)
);

-- 11. BẢNG RẠP (Cinema)
CREATE TABLE rap (
    id VARCHAR(36) PRIMARY KEY,
    ten_rap VARCHAR(100) NOT NULL,
    dia_chi TEXT,
    thanh_pho VARCHAR(100),
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME,
    nguoi_cap_nhat VARCHAR(100)
);

-- 12. BẢNG PHÒNG CHIẾU (Screening Room)
CREATE TABLE phong_chieu (
    id VARCHAR(36) PRIMARY KEY,
    id_rap VARCHAR(36),
    ten_phong VARCHAR(50),
    loai_man_hinh VARCHAR(50), -- 2D, 3D, IMAX
    so_luong_ghe INT,
    trang_thai INT DEFAULT 1,
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME,
    nguoi_cap_nhat VARCHAR(100),
    FOREIGN KEY (id_rap) REFERENCES rap(id)
);

-- 13. BẢNG LOẠI GHẾ (Seat Type)
CREATE TABLE loai_ghe (
    id VARCHAR(36) PRIMARY KEY,
    ten_loai VARCHAR(50) NOT NULL, -- VIP, Thường, Đôi
    phu_phi DECIMAL(10,2) DEFAULT 0,
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME,
    nguoi_cap_nhat VARCHAR(100)
);

-- 14. BẢNG GHẾ (Seat)
CREATE TABLE ghe (
    id VARCHAR(36) PRIMARY KEY,
    id_phong_chieu VARCHAR(36),
    id_loai_ghe VARCHAR(36),
    so_ghe VARCHAR(10), -- A1, A2...
    hang VARCHAR(5), -- A, B...
    cot INT, -- 1, 2...
    trang_thai INT DEFAULT 1, -- 1: Tốt, 0: Hỏng
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME,
    nguoi_cap_nhat VARCHAR(100),
    FOREIGN KEY (id_phong_chieu) REFERENCES phong_chieu(id),
    FOREIGN KEY (id_loai_ghe) REFERENCES loai_ghe(id)
);

-- 15. CẤU HÌNH GIÁ: BẢNG GIÁ, KHUNG GIỜ, LOẠI NGÀY
CREATE TABLE bang_gia (
    id VARCHAR(36) PRIMARY KEY,
    ten_bang_gia VARCHAR(100),
    ngay_bat_dau DATETIME,
    ngay_ket_thuc DATETIME,
    is_default BOOLEAN DEFAULT FALSE,
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME,
    nguoi_cap_nhat VARCHAR(100)
);

CREATE TABLE khung_gio (
    id VARCHAR(36) PRIMARY KEY,
    ten_khung_gio VARCHAR(100), -- Sáng, Trưa, Tối
    gio_bat_dau TIME,
    gio_ket_thuc TIME,
    mo_ta TEXT,
    trang_thai INT DEFAULT 1,
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME,
    nguoi_cap_nhat VARCHAR(100)
);

CREATE TABLE loai_ngay (
    id VARCHAR(36) PRIMARY KEY,
    ten_loai_ngay VARCHAR(100), -- Ngày thường, Cuối tuần, Lễ
    mo_ta TEXT,
    he_so_ngay DECIMAL(5,2) DEFAULT 1.0,
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME,
    nguoi_cap_nhat VARCHAR(100)
);

-- 16. GIÁ VÉ CHI TIẾT (Matrix giá)
CREATE TABLE gia_ve_chi_tiet (
    id VARCHAR(36) PRIMARY KEY,
    id_loai_ghe VARCHAR(36),
    id_bang_gia VARCHAR(36),
    id_khung_gio VARCHAR(36),
    id_loai_ngay VARCHAR(36),
    gia_tien DECIMAL(15,2) NOT NULL,
    trang_thai INT DEFAULT 1,
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME,
    nguoi_cap_nhat VARCHAR(100),
    FOREIGN KEY (id_loai_ghe) REFERENCES loai_ghe(id),
    FOREIGN KEY (id_bang_gia) REFERENCES bang_gia(id),
    FOREIGN KEY (id_khung_gio) REFERENCES khung_gio(id),
    FOREIGN KEY (id_loai_ngay) REFERENCES loai_ngay(id)
);

-- 17. BẢNG SUẤT CHIẾU (Showtime)
CREATE TABLE suat_chieu (
    id VARCHAR(36) PRIMARY KEY,
    id_phim VARCHAR(36),
    id_phong_chieu VARCHAR(36),
    ngay_chieu DATE,
    gio_bat_dau TIME,
    gio_ket_thuc TIME,
    trang_thai INT DEFAULT 1, -- 1: Sắp chiếu, 2: Đang chiếu, 3: Đã chiếu
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME,
    nguoi_cap_nhat VARCHAR(100),
    FOREIGN KEY (id_phim) REFERENCES phim(id),
    FOREIGN KEY (id_phong_chieu) REFERENCES phong_chieu(id)
);

-- 18. BẢNG LOẠI SẢN PHẨM & SẢN PHẨM (Concessions/Food)
CREATE TABLE loai_san_pham (
    id VARCHAR(36) PRIMARY KEY,
    ten_loai VARCHAR(100),
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME,
    nguoi_cap_nhat VARCHAR(100)
);

CREATE TABLE san_pham_dich_vu (
    id VARCHAR(36) PRIMARY KEY,
    id_loai_san_pham VARCHAR(36),
    ten_san_pham VARCHAR(150),
    gia_tien DECIMAL(15,2),
    so_luong_ton INT,
    trang_thai INT DEFAULT 1,
    hinh_anh VARCHAR(255),
    mo_ta TEXT,
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME,
    nguoi_cap_nhat VARCHAR(100),
    FOREIGN KEY (id_loai_san_pham) REFERENCES loai_san_pham(id)
);

-- 19. BẢNG PHIẾU GIẢM GIÁ (Voucher/Promotion)
CREATE TABLE phieu_giam_gia (
    id VARCHAR(36) PRIMARY KEY,
    ma_phieu_giam_gia VARCHAR(50) UNIQUE,
    ten_phieu VARCHAR(100),
    loai_phieu INT, -- 1: % , 2: Tiền mặt
    phan_tram_giam_gia DECIMAL(5,2),
    so_tien_giam DECIMAL(15,2),
    giam_toi_da DECIMAL(15,2),
    ngay_bat_dau DATETIME,
    ngay_ket_thuc DATETIME,
    so_luong INT,
    dieu_kien_ap_dung TEXT,
    trang_thai INT,
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME,
    nguoi_cap_nhat VARCHAR(100)
);

-- Bảng chi tiết khách hàng sở hữu phiếu (Wallet)
CREATE TABLE phieu_giam_gia_chi_tiet (
    id VARCHAR(36) PRIMARY KEY,
    id_phieu_giam_gia VARCHAR(36),
    id_khach_hang VARCHAR(36),
    so_luong_su_dung INT DEFAULT 0,
    trang_thai INT, -- 1: Chưa dùng, 2: Đã dùng
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME,
    nguoi_cap_nhat VARCHAR(100),
    FOREIGN KEY (id_phieu_giam_gia) REFERENCES phieu_giam_gia(id),
    FOREIGN KEY (id_khach_hang) REFERENCES khach_hang(id)
);

-- 20. BẢNG HÓA ĐƠN (Invoice/Order)
CREATE TABLE hoa_don (
    id VARCHAR(36) PRIMARY KEY,
    id_nhan_vien VARCHAR(36), -- Nhân viên bán (nếu tại quầy)
    id_khach_hang VARCHAR(36), -- Khách mua
    id_phieu_giam_gia VARCHAR(36),
    ma_hoa_don VARCHAR(50) UNIQUE,
    tong_tien DECIMAL(15,2),
    so_tien_giam DECIMAL(15,2),
    thuc_thu DECIMAL(15,2), -- Số tiền khách cần trả
    tong_tien_thanh_toan DECIMAL(15,2), -- Số tiền khách đã đưa/chuyển
    tien_thua DECIMAL(15,2),
    hinh_thuc_thanh_toan INT, -- 1: Tiền mặt, 2: Chuyển khoản, 3: VNPAY...
    ngay_mua DATETIME DEFAULT CURRENT_TIMESTAMP,
    trang_thai INT, -- 1: Đã thanh toán, 0: Chờ thanh toán, 2: Hủy
    ghi_chu TEXT,
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME,
    nguoi_cap_nhat VARCHAR(100),
    FOREIGN KEY (id_nhan_vien) REFERENCES nhan_vien(id),
    FOREIGN KEY (id_khach_hang) REFERENCES khach_hang(id),
    FOREIGN KEY (id_phieu_giam_gia) REFERENCES phieu_giam_gia(id)
);

-- 21. BẢNG VÉ (Ticket - Chi tiết hóa đơn phần vé)
CREATE TABLE ve (
    id VARCHAR(36) PRIMARY KEY,
    id_hoa_don VARCHAR(36),
    id_ghe VARCHAR(36),
    id_suat_chieu VARCHAR(36),
    id_gia_ve_chi_tiet VARCHAR(36), -- Để biết vé này áp dụng giá nào
    gia_ve_thuc_te DECIMAL(15,2), -- Giá tại thời điểm mua (tránh thay đổi bảng giá)
    trang_thai INT DEFAULT 1, -- 1: Đã đặt, 0: Hủy
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME,
    nguoi_cap_nhat VARCHAR(100),
    FOREIGN KEY (id_hoa_don) REFERENCES hoa_don(id),
    FOREIGN KEY (id_ghe) REFERENCES ghe(id),
    FOREIGN KEY (id_suat_chieu) REFERENCES suat_chieu(id),
    FOREIGN KEY (id_gia_ve_chi_tiet) REFERENCES gia_ve_chi_tiet(id)
);

-- 22. BẢNG HÓA ĐƠN CHI TIẾT (Chi tiết hóa đơn phần đồ ăn/uống)
CREATE TABLE hoa_don_chi_tiet (
    id VARCHAR(36) PRIMARY KEY,
    id_hoa_don VARCHAR(36),
    id_san_pham_dich_vu VARCHAR(36),
    so_luong INT,
    don_gia DECIMAL(15,2),
    thanh_tien DECIMAL(15,2),
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME,
    nguoi_cap_nhat VARCHAR(100),
    FOREIGN KEY (id_hoa_don) REFERENCES hoa_don(id),
    FOREIGN KEY (id_san_pham_dich_vu) REFERENCES san_pham_dich_vu(id)
);

-- 23. BẢNG THANH TOÁN (Payment Transaction log)
CREATE TABLE thanh_toan (
    id VARCHAR(36) PRIMARY KEY,
    id_hoa_don VARCHAR(36),
    ma_giao_dich VARCHAR(100), -- Mã GD ngân hàng/VNPAY
    phuong_thuc_thanh_toan VARCHAR(50),
    so_tien DECIMAL(15,2),
    ngay_thanh_toan DATETIME DEFAULT CURRENT_TIMESTAMP,
    noi_dung TEXT,
    trang_thai INT, -- 1: Thành công, 0: Thất bại
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME,
    nguoi_cap_nhat VARCHAR(100),
    FOREIGN KEY (id_hoa_don) REFERENCES hoa_don(id)
);

-- 24. BẢNG ĐÁNH GIÁ DỊCH VỤ (Service Review)
CREATE TABLE danh_gia_dich_vu (
    id VARCHAR(36) PRIMARY KEY,
    id_hoa_don VARCHAR(36),
    id_khach_hang VARCHAR(36),
    diem_so INT,
    noi_dung TEXT,
    ngay_tao_danh_gia DATETIME DEFAULT CURRENT_TIMESTAMP,
    trang_thai INT,
    ngay_tao DATETIME,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME,
    nguoi_cap_nhat VARCHAR(100),
    FOREIGN KEY (id_hoa_don) REFERENCES hoa_don(id),
    FOREIGN KEY (id_khach_hang) REFERENCES khach_hang(id)
);

-- 25. BẢNG BANNER (Quảng cáo)
CREATE TABLE banner (
    id VARCHAR(36) PRIMARY KEY,
    hinh_anh_url VARCHAR(255),
    lien_ket VARCHAR(255),
    tieu_de VARCHAR(200),
    thu_tu_hien_thi INT,
    trang_thai INT DEFAULT 1,
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME,
    nguoi_cap_nhat VARCHAR(100)
);