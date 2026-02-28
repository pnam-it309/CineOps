-- 1. BẢNG PHÂN QUYỀN
CREATE TABLE phan_quyen (
    id VARCHAR(36) PRIMARY KEY,
    ma_phan_quyen VARCHAR(50),
    ten_vai_tro VARCHAR(100) ,
    quyen_han TEXT,
    trang_thai INT DEFAULT 1,
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME ON UPDATE CURRENT_TIMESTAMP,
    nguoi_cap_nhat VARCHAR(100)
);

-- 2. BẢNG LOẠI KHÁCH HÀNG
CREATE TABLE loai_khach_hang (
    id VARCHAR(36) PRIMARY KEY,
    ten_loai VARCHAR(100),
    he_so_giam_gia DECIMAL(5,2),
    mo_ta TEXT,
    trang_thai INT DEFAULT 1,
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME ON UPDATE CURRENT_TIMESTAMP,
    nguoi_cap_nhat VARCHAR(100)
);

-- 3. BẢNG KHÁCH HÀNG
CREATE TABLE khach_hang (
    id VARCHAR(36) PRIMARY KEY,
    id_loai_khach_hang VARCHAR(36),
    ma_khach_hang VARCHAR(50) UNIQUE,
    ten_khach_hang VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    mat_khau VARCHAR(255),
    gioi_tinh INT,
    sdt VARCHAR(20),
    ngay_sinh DATE,
    hinh_anh VARCHAR(255),
    ghi_chu TEXT,
    trang_thai INT DEFAULT 1,
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME ON UPDATE CURRENT_TIMESTAMP,
    nguoi_cap_nhat VARCHAR(100),
    FOREIGN KEY (id_loai_khach_hang) REFERENCES loai_khach_hang(id)
);

-- 4. BẢNG NHÂN VIÊN
CREATE TABLE nhan_vien (
    id VARCHAR(36) PRIMARY KEY,
    id_phan_quyen VARCHAR(36),
    ma_nhan_vien VARCHAR(50) UNIQUE,
    ten_nhan_vien VARCHAR(100) NOT NULL,
    cccd VARCHAR(20),
    ngay_sinh DATE,
    que_quan VARCHAR(255),
    gioi_tinh INT,
    email VARCHAR(100),
    mat_khau VARCHAR(255),
    chuc_vu VARCHAR(100),
    anh_nhan_vien VARCHAR(255),
    so_dien_thoai VARCHAR(20),
    trang_thai INT DEFAULT 1,
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME ON UPDATE CURRENT_TIMESTAMP,
    nguoi_cap_nhat VARCHAR(100),
    FOREIGN KEY (id_phan_quyen) REFERENCES phan_quyen(id)
);

-- 5. BẢNG TOKEN
CREATE TABLE token (
    id VARCHAR(36) PRIMARY KEY,
    id_khach_hang VARCHAR(36),
    id_nhan_vien VARCHAR(36),
    ma_token TEXT,
    ngay_het_han DATETIME,
    trang_thai INT DEFAULT 1,
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME ON UPDATE CURRENT_TIMESTAMP,
    nguoi_cap_nhat VARCHAR(100),
    FOREIGN KEY (id_khach_hang) REFERENCES khach_hang(id),
    FOREIGN KEY (id_nhan_vien) REFERENCES nhan_vien(id)
);


-- 6. BẢNG ĐỊA CHỈ
CREATE TABLE dia_chi (
    id VARCHAR(36) PRIMARY KEY,
    id_khach_hang VARCHAR(36),
    id_nhan_vien VARCHAR(36),
    ma_dia_chi VARCHAR(50),
    thanh_pho_tinh VARCHAR(100),
    quan_huyen VARCHAR(100),
    phuong_xa VARCHAR(100),
    dia_chi_chi_tiet TEXT,
    trang_thai INT DEFAULT 1,
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME ON UPDATE CURRENT_TIMESTAMP,
    nguoi_cap_nhat VARCHAR(100),
    FOREIGN KEY (id_khach_hang) REFERENCES khach_hang(id),
    FOREIGN KEY (id_nhan_vien) REFERENCES nhan_vien(id)
);

-- 7. BẢNG PHÒNG CHIẾU
CREATE TABLE phong_chieu (
    id VARCHAR(36) PRIMARY KEY,
    ten_phong VARCHAR(50),
    loai_man_hinh VARCHAR(50),
    tong_ghe INT,
    trang_thai INT DEFAULT 1,
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME ON UPDATE CURRENT_TIMESTAMP,
    nguoi_cap_nhat VARCHAR(100)
);

-- 8. BẢNG LOẠI GHẾ
CREATE TABLE loai_ghe (
    id VARCHAR(36) PRIMARY KEY,
    ten_loai VARCHAR(50),
    phu_phi DECIMAL(20,2),
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME ON UPDATE CURRENT_TIMESTAMP,
    nguoi_cap_nhat VARCHAR(100)
);

-- 9. BẢNG GHẾ
CREATE TABLE ghe (
    id VARCHAR(36) PRIMARY KEY,
    id_loai_ghe VARCHAR(36),
    id_phong_chieu VARCHAR(36),
    so_ghe VARCHAR(10),
    so_hang VARCHAR(5),
    so_cot INT,
    trang_thai INT DEFAULT 1,
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME ON UPDATE CURRENT_TIMESTAMP,
    nguoi_cap_nhat VARCHAR(100),
    FOREIGN KEY (id_loai_ghe) REFERENCES loai_ghe(id),
    FOREIGN KEY (id_phong_chieu) REFERENCES phong_chieu(id)
);

-- 10. BẢNG KHUNG GIỜ
CREATE TABLE khung_gio (
    id VARCHAR(36) PRIMARY KEY,
    ten_khung_gio VARCHAR(100),
    gio_bat_dau TIME,
    gio_ket_thuc TIME,
    mo_ta TEXT,
    trang_thai INT DEFAULT 1,
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME ON UPDATE CURRENT_TIMESTAMP,
    nguoi_cap_nhat VARCHAR(100)
);

-- 11. BẢNG PHIM
CREATE TABLE phim (
    id VARCHAR(36) PRIMARY KEY,
    ten_phim VARCHAR(255) NOT NULL,
    thoi_luong INT,
    ngay_khoi_chieu DATE,
    ngay_ket_thuc DATE,
    lich_chieu VARCHAR(50),
    dao_dien VARCHAR(100),
    dien_vien TEXT,
    mo_ta TEXT,
    trailer VARCHAR(255),
    poster VARCHAR(255),
    ngon_ngu VARCHAR(100),
    do_tuoi INT,
    danh_gia DECIMAL(3,1),
    gia_ve_goc DECIMAL(20,2),
    trang_thai INT DEFAULT 1,
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME ON UPDATE CURRENT_TIMESTAMP,
    nguoi_cap_nhat VARCHAR(100)
);

-- 12. BẢNG THỂ LOẠI
CREATE TABLE the_loai (
    id VARCHAR(36) PRIMARY KEY,
    ten_the_loai VARCHAR(100),
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME ON UPDATE CURRENT_TIMESTAMP,
    nguoi_cap_nhat VARCHAR(100)
);

CREATE TABLE phim_the_loai (
    id VARCHAR(36) PRIMARY KEY,
    id_phim VARCHAR(36),
    id_the_loai VARCHAR(36),
    FOREIGN KEY (id_phim) REFERENCES phim(id),
    FOREIGN KEY (id_the_loai) REFERENCES the_loai(id)
);

-- 13. BẢNG SUẤT CHIẾU
CREATE TABLE suat_chieu (
    id VARCHAR(36) PRIMARY KEY,
    id_khung_gio VARCHAR(36),
    id_phong_chieu VARCHAR(36),
    id_phim VARCHAR(36),
    ngay_chieu DATE,
    so_ghe_trong INT,
    trang_thai INT DEFAULT 1,
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME ON UPDATE CURRENT_TIMESTAMP,
    nguoi_cap_nhat VARCHAR(100),
    FOREIGN KEY (id_khung_gio) REFERENCES khung_gio(id),
    FOREIGN KEY (id_phong_chieu) REFERENCES phong_chieu(id),
    FOREIGN KEY (id_phim) REFERENCES phim(id)
);

-- 14. BẢNG VÉ
CREATE TABLE ve (
    id VARCHAR(36) PRIMARY KEY,
    id_loai_khach_hang VARCHAR(36),
    id_ghe VARCHAR(36),
    id_suat_chieu VARCHAR(36),
    ma_ve VARCHAR(50),
    gia_ve_goc DECIMAL(20,2),
    loai_ve INT,
    trang_thai INT DEFAULT 1,
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME ON UPDATE CURRENT_TIMESTAMP,
    nguoi_cap_nhat VARCHAR(100),
    FOREIGN KEY (id_loai_khach_hang) REFERENCES loai_khach_hang(id),
    FOREIGN KEY (id_ghe) REFERENCES ghe(id),
    FOREIGN KEY (id_suat_chieu) REFERENCES suat_chieu(id)
);

-- 15. BẢNG LOẠI NGÀY
CREATE TABLE loai_ngay (
    id VARCHAR(36) PRIMARY KEY,
    ten_loai_ngay VARCHAR(100),
    he_so_ngay DECIMAL(5,2),
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME ON UPDATE CURRENT_TIMESTAMP,
    nguoi_cap_nhat VARCHAR(100)
);

-- 16. GIÁ VÉ CHI TIẾT
CREATE TABLE gia_ve_chi_tiet (
    id VARCHAR(36) PRIMARY KEY,
    id_loai_ngay VARCHAR(36),
    id_loai_khach_hang VARCHAR(36),
    id_loai_ghe VARCHAR(36),
    id_khung_gio VARCHAR(36),
    gia_tien DECIMAL(20,2),
    trang_thai INT DEFAULT 1,
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME ON UPDATE CURRENT_TIMESTAMP,
    nguoi_cap_nhat VARCHAR(100),
    FOREIGN KEY (id_loai_khach_hang) REFERENCES loai_khach_hang(id),
    FOREIGN KEY (id_loai_ngay) REFERENCES loai_ngay(id),
    FOREIGN KEY (id_loai_ghe) REFERENCES loai_ghe(id),
    FOREIGN KEY (id_khung_gio) REFERENCES khung_gio(id)
);

-- 16. BẢNG PHIẾU GIẢM GIÁ
CREATE TABLE phieu_giam_gia (
    id VARCHAR(36) PRIMARY KEY,
    ma_phieu_giam_gia VARCHAR(50) UNIQUE,
    ten_phieu VARCHAR(100),
    loai_phieu INT,
    phan_tram_giam_gia DECIMAL(5,2),
    so_tien_giam DECIMAL(20,2),
    gia_tri_hoa_don_toi_thieu DECIMAL(20,2),
    co_cho_cong_don INT,
    giam_toi_da DECIMAL(20,2),
    ngay_bat_dau DATETIME,
    ngay_ket_thuc DATETIME,
    trang_thai INT DEFAULT 1,
    so_luong INT,
    dieu_kien_ap_dung TEXT,
    ghi_chu TEXT,
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME ON UPDATE CURRENT_TIMESTAMP,
    nguoi_cap_nhat VARCHAR(100)
);

CREATE TABLE phieu_giam_gia_chi_tiet (
    id VARCHAR(36) PRIMARY KEY,
    id_phieu_giam_gia VARCHAR(36),
    id_khach_hang VARCHAR(36),
    ma_phieu_giam_gia_chi_tiet VARCHAR(50),
    so_luong_dung INT DEFAULT 0,
    ghi_chu TEXT,
    trang_thai INT DEFAULT 1,
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME ON UPDATE CURRENT_TIMESTAMP,
    nguoi_cap_nhat VARCHAR(100),
    FOREIGN KEY (id_phieu_giam_gia) REFERENCES phieu_giam_gia(id),
    FOREIGN KEY (id_khach_hang) REFERENCES khach_hang(id)
);

-- 17. BẢNG HÓA ĐƠN
CREATE TABLE hoa_don (
    id VARCHAR(36) PRIMARY KEY,
    id_nhan_vien VARCHAR(36),
    id_phieu_giam_gia VARCHAR(36),
    id_khach_hang VARCHAR(36),
    ma_hoa_don VARCHAR(50),
    tong_tien DECIMAL(20,2),
    so_tien_giam DECIMAL(20,2),
    tong_tien_thanh_toan DECIMAL(20,2),
    phuong_thuc_thanh_toan INT,
    kem_ban_hang INT DEFAULT 0,
    trang_thai INT DEFAULT 1,
    ghi_chu TEXT,
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME ON UPDATE CURRENT_TIMESTAMP,
    nguoi_cap_nhat VARCHAR(100),
    FOREIGN KEY (id_nhan_vien) REFERENCES nhan_vien(id),
    FOREIGN KEY (id_phieu_giam_gia) REFERENCES phieu_giam_gia(id),
    FOREIGN KEY (id_khach_hang) REFERENCES khach_hang(id)
);

-- 18. BẢNG LỊCH SỬ HÓA ĐƠN
CREATE TABLE lich_su_hoa_don (
    id VARCHAR(36) PRIMARY KEY,
    hoa_don_id VARCHAR(36),
    hanh_dong VARCHAR(255),
    thoi_gian_hanh_dong DATETIME DEFAULT CURRENT_TIMESTAMP,
    trang_thai INT DEFAULT 1,
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME ON UPDATE CURRENT_TIMESTAMP,
    nguoi_cap_nhat VARCHAR(100),
    FOREIGN KEY (hoa_don_id) REFERENCES hoa_don(id)
);

-- 19. BẢNG THANH TOÁN
CREATE TABLE thanh_toan (
    id VARCHAR(36) PRIMARY KEY,
    id_hoa_don VARCHAR(36),
    ma_giao_dich VARCHAR(100),
    phuong_thuc_thanh_toan INT,
    so_tien DECIMAL(20,2),
    ngay_thanh_toan DATETIME DEFAULT CURRENT_TIMESTAMP,
    noi_dung TEXT,
    trang_thai INT DEFAULT 1,
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME ON UPDATE CURRENT_TIMESTAMP,
    nguoi_cap_nhat VARCHAR(100),
    FOREIGN KEY (id_hoa_don) REFERENCES hoa_don(id)
);

-- 20. BẢNG LỊCH SỬ THANH TOÁN
CREATE TABLE lich_su_thanh_toan (
    id VARCHAR(36) PRIMARY KEY,
    id_thanh_toan VARCHAR(36),
    trang_thai INT DEFAULT 1,
    thoi_gian DATETIME DEFAULT CURRENT_TIMESTAMP,
    ma_phan_hoi VARCHAR(255),
    loai_thao_tac VARCHAR(255),
    noi_dung TEXT,
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME ON UPDATE CURRENT_TIMESTAMP,
    nguoi_cap_nhat VARCHAR(100),
    FOREIGN KEY (id_thanh_toan) REFERENCES thanh_toan(id)
);

-- 21. BẢNG LOẠI SẢN PHẨM ĐI KÈM
CREATE TABLE loai_san_pham_di_kem (
    id VARCHAR(36) PRIMARY KEY,
    ten_loai VARCHAR(100),
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME ON UPDATE CURRENT_TIMESTAMP,
    nguoi_cap_nhat VARCHAR(100)
);

-- 22. BẢNG SẢN PHẨM ĐI KÈM
CREATE TABLE san_pham_di_kem (
    id VARCHAR(36) PRIMARY KEY,
    id_loai_san_pham VARCHAR(36),
    ten_san_pham VARCHAR(150),
    mo_ta TEXT,
    hinh_anh VARCHAR(255),
    trang_thai INT DEFAULT 1,
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME ON UPDATE CURRENT_TIMESTAMP,
    nguoi_cap_nhat VARCHAR(100),
    FOREIGN KEY (id_loai_san_pham) REFERENCES loai_san_pham_di_kem(id)
);

-- 23. BẢNG KÍCH CỠ
CREATE TABLE kich_co (
    id VARCHAR(36) PRIMARY KEY,
    ten_kich_co VARCHAR(100),
    mo_ta TEXT
);

-- 24. BẢNG ĐƠN VỊ TÍNH
CREATE TABLE don_vi_tinh (
    id VARCHAR(36) PRIMARY KEY,
    ten_don_vi_tinh VARCHAR(100),
    mo_ta TEXT
);

-- 25. BẢNG CHI TIẾT SẢN PHẨM ĐI KÈM
CREATE TABLE chi_tiet_san_pham_di_kem (
    id VARCHAR(36) PRIMARY KEY,
    id_san_pham VARCHAR(36),
    id_kich_co VARCHAR(36),
    id_don_vi_tinh VARCHAR(36),
    gia_tri_dinh_luong DOUBLE,
    huong_vi VARCHAR(100),
    gia_ban DECIMAL(20,2),
    so_luong_ton INT DEFAULT 0,
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME ON UPDATE CURRENT_TIMESTAMP,
    nguoi_cap_nhat VARCHAR(100),
    FOREIGN KEY (id_san_pham) REFERENCES san_pham_di_kem(id),
    FOREIGN KEY (id_kich_co) REFERENCES kich_co(id),
    FOREIGN KEY (id_don_vi_tinh) REFERENCES don_vi_tinh(id)
);

-- 26. BẢNG HÓA ĐƠN CHI TIẾT
CREATE TABLE hoa_don_chi_tiet (
    id VARCHAR(36) PRIMARY KEY,
    id_hoa_don VARCHAR(36),
    id_ve VARCHAR(36),
    id_chi_tiet_san_pham_di_kem VARCHAR(36),
    loai INT, -- 1: Vé, 2: Sản phẩm
    so_luong INT,
    don_gia DECIMAL(20,2),
    thanh_tien DECIMAL(20,2),
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    nguoi_tao VARCHAR(100),
    ngay_cap_nhat DATETIME ON UPDATE CURRENT_TIMESTAMP,
    nguoi_cap_nhat VARCHAR(100),
    FOREIGN KEY (id_hoa_don) REFERENCES hoa_don(id),
    FOREIGN KEY (id_ve) REFERENCES ve(id),
    FOREIGN KEY (id_chi_tiet_san_pham_di_kem) REFERENCES chi_tiet_san_pham_di_kem(id)
);