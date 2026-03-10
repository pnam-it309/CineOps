-- =================================================================
-- V2: UNIFIED REALISTIC DATASET (SYNCHRONIZED & FIXED FOR GROUPING TEST)
-- =================================================================
SET NAMES utf8mb4;
SET time_zone = '+07:00';
SET FOREIGN_KEY_CHECKS = 0;

-- Clean existing data to ensure idempotency
TRUNCATE TABLE hoa_don_chi_tiet;
TRUNCATE TABLE thanh_toan;
TRUNCATE TABLE ve;
TRUNCATE TABLE hoa_don;
TRUNCATE TABLE phieu_giam_gia_chi_tiet;
TRUNCATE TABLE dia_chi;
TRUNCATE TABLE chi_tiet_san_pham_di_kem;
TRUNCATE TABLE token;
TRUNCATE TABLE nhan_vien;
TRUNCATE TABLE khach_hang;
TRUNCATE TABLE gia_ve_chi_tiet;
TRUNCATE TABLE suat_chieu;
TRUNCATE TABLE phim_the_loai;
TRUNCATE TABLE ghe;
TRUNCATE TABLE tai_khoan;
TRUNCATE TABLE phieu_giam_gia;
TRUNCATE TABLE san_pham_di_kem;
TRUNCATE TABLE don_vi_tinh;
TRUNCATE TABLE kich_co;
TRUNCATE TABLE loai_san_pham_di_kem;
TRUNCATE TABLE the_loai;
TRUNCATE TABLE phim;
TRUNCATE TABLE khung_gio;
TRUNCATE TABLE phong_chieu;
TRUNCATE TABLE loai_khach_hang;
TRUNCATE TABLE loai_ghe;
TRUNCATE TABLE loai_ngay;
TRUNCATE TABLE phan_quyen;

SET FOREIGN_KEY_CHECKS = 1;

-- =================================================================
-- NHÓM 1: CÁC BẢNG CƠ SỞ
-- =================================================================

INSERT INTO phan_quyen (id, ma_phan_quyen, ten_vai_tro, quyen_han, trang_thai, ngay_tao, nguoi_tao)
VALUES ('pq-admin', 'ROLE_ADMIN', 'Quản trị viên', 'ALL', 1, NOW(), 'system'),
       ('pq-staff', 'ROLE_STAFF', 'Nhân viên bán vé', 'POS,CHECKIN', 1, NOW(), 'system'),
       ('pq-customer', 'ROLE_CUSTOMER', 'Khách hàng', 'BOOKING', 1, NOW(), 'system');

INSERT INTO loai_ngay (id, ten_loai_ngay, he_so_ngay, ngay_tao, nguoi_tao)
VALUES ('ln-1', 'Thứ 2 - Thứ 5', 1.0, NOW(), 'system'),
       ('ln-3', 'Thứ 7 - CN', 1.25, NOW(), 'system');

INSERT INTO loai_ghe (id, ten_loai, phu_phi, ngay_tao, nguoi_tao)
VALUES ('lg-1', 'Thường', 0, NOW(), 'system'),
       ('lg-2', 'VIP', 20000, NOW(), 'system');

INSERT INTO loai_khach_hang (id, ten_loai, he_so_giam_gia, mo_ta, trang_thai, ngay_tao, nguoi_tao)
VALUES ('lkh-1', 'Vãng lai', 0, 'Khách chưa có tài khoản', 1, NOW(), 'system'),
       ('lkh-2', 'Thành viên', 5, 'Khách đã đăng ký', 1, NOW(), 'system');

INSERT INTO phong_chieu (id, ten_phong, loai_man_hinh, tong_ghe, trang_thai, ngay_tao, nguoi_tao)
VALUES ('pc-1', 'Phòng IMAX 01', 'IMAX 3D', 200, 1, NOW(), 'system');

INSERT INTO khung_gio (id, ten_khung_gio, thu_trong_tuan, gio_bat_dau, gio_ket_thuc, he_so_gia, la_khung_gio_vang, mo_ta, trang_thai, ngay_tao, nguoi_tao)
VALUES ('kg-5', 'Tối 1', 1, '19:30:00', '22:00:00', 1.3, TRUE, 'Giờ vàng cinema', 1, NOW(), 'system');

INSERT INTO the_loai (id, ten_the_loai, ngay_tao, nguoi_tao)
VALUES ('tl-1', 'Hành động', NOW(), 'system');

-- FIX LỖI CHÍNH TẢ TẠI CỘT hien_thi_canh_bao_do_tuoi
INSERT INTO phim (id, ten_phim, ma_phim, thoi_luong, ngay_khoi_chieu, ngay_ket_thuc, lich_chieu, mo_ta, trailer, poster, ngon_ngu, do_tuoi, nhan_do_tuoi, hien_thi_canh_bao_do_tuoi, danh_gia, gia_phim, loai_phim, phu_phi_loai_phim, trang_thai, ngay_tao, nguoi_tao)
VALUES ('p-1', 'Avatar: Dòng chảy của nước', 'P001', 192, '2023-12-15', '2024-03-15', 'Daily', 'Phần 2 Avatar', 't1', 'p1', 'Tiếng Anh', 13, 'T13', TRUE, 8.5, 85000, 'IMAX 3D', 20000, 1, NOW(), 'system'),
       ('p-4', 'Lật Mặt 7: Một Điều Ước', 'LM007', 138, '2024-04-26', '2024-07-26', 'Daily', 'Phim Lý Hải', 't4', 'p4', 'Tiếng Việt', 0, 'P', TRUE, 9.0, 95000, '2D', 0, 1, NOW(), 'system'),
       ('p-7', 'Inside Out 2', 'P007', 96, '2024-06-14', '2024-08-14', 'Daily', 'Phim hoạt hình', 't7', 'p7', 'Tiếng Anh', 0, 'P', TRUE, 8.9, 75000, '2D', 0, 1, NOW(), 'system'),
       ('p-8', 'Godzilla x Kong', 'P008', 115, '2024-03-29', '2024-05-29', 'Daily', 'Monsterverse', 't8', 'p8', 'Tiếng Anh', 13, 'T13', TRUE, 7.8, 90000, 'IMAX', 15000, 1, NOW(), 'system');

INSERT INTO loai_san_pham_di_kem (id, ten_loai, ngay_tao, nguoi_tao)
VALUES ('lsp-1', 'Bắp rang', NOW(), 'system'),
       ('lsp-2', 'Nước uống', NOW(), 'system');

INSERT INTO kich_co (id, ten_kich_co, mo_ta) VALUES ('kc-1', 'Nhỏ', 'S');
INSERT INTO don_vi_tinh (id, ten_don_vi_tinh, mo_ta) VALUES ('dvt-2', 'Hộp', 'H'), ('dvt-1', 'Ly', 'L');

-- =================================================================
-- NHÓM 2: TÀI KHOẢN, GHẾ, SUẤT CHIẾU, SẢN PHẨM
-- =================================================================

INSERT INTO tai_khoan (id, id_phan_quyen, email, mat_khau, trang_thai, ngay_tao, nguoi_tao)
VALUES ('tk-1', 'pq-admin', 'admin@cineops.com', '123456', 1, NOW(), 'system'),
       ('tk-2', 'pq-staff', 'staff@cineops.com', '123456', 1, NOW(), 'system'),
       ('tk-4', 'pq-customer', 'khach1@gmail.com', '123456', 1, NOW(), 'system');

INSERT INTO ghe (id, id_loai_ghe, id_phong_chieu, so_ghe, so_hang, so_cot, trang_thai, ngay_tao, nguoi_tao)
VALUES ('g-1', 'lg-1', 'pc-1', 'A1', 'A', 1, 1, NOW(), 'system'),
       ('ghe-B1', 'lg-2', 'pc-1', 'B1', 'B', 1, 1, NOW(), 'system'),
       ('ghe-B2', 'lg-2', 'pc-1', 'B2', 'B', 2, 1, NOW(), 'system'),
       ('ghe-B3', 'lg-2', 'pc-1', 'B3', 'B', 3, 1, NOW(), 'system'),
       ('ghe-C1', 'lg-1', 'pc-1', 'C1', 'C', 1, 1, NOW(), 'system'),
       ('ghe-C2', 'lg-1', 'pc-1', 'C2', 'C', 2, 1, NOW(), 'system'),
       ('ghe-C3', 'lg-1', 'pc-1', 'C3', 'C', 3, 1, NOW(), 'system'),
       ('ghe-C4', 'lg-1', 'pc-1', 'C4', 'C', 4, 1, NOW(), 'system'),
       ('ghe-D1', 'lg-1', 'pc-1', 'D1', 'D', 1, 1, NOW(), 'system'),
       ('ghe-D2', 'lg-1', 'pc-1', 'D2', 'D', 2, 1, NOW(), 'system');

INSERT INTO suat_chieu (id, id_khung_gio, id_phong_chieu, id_phim, ngay_chieu, gio_bat_dau, gio_ket_thuc, thoi_gian_don_ve_sinh, so_ghe_trong, trang_thai, ngay_tao, nguoi_tao)
VALUES ('sc-1', 'kg-5', 'pc-1', 'p-1', CURDATE(), '19:30:00', '22:00:00', 15, 100, 1, NOW(), 'system'),
       ('sc-10', 'kg-5', 'pc-1', 'p-7', CURDATE(), '19:30:00', '22:00:00', 15, 100, 1, NOW(), 'system'),
       ('sc-11', 'kg-5', 'pc-1', 'p-8', CURDATE(), '19:30:00', '22:00:00', 15, 100, 1, NOW(), 'system'),
       ('sc-12', 'kg-5', 'pc-1', 'p-4', CURDATE(), '19:30:00', '22:00:00', 15, 100, 1, NOW(), 'system');

INSERT INTO san_pham_di_kem (id, id_loai_san_pham, ten_san_pham, mo_ta, hinh_anh, trang_thai, ngay_tao, nguoi_tao)
VALUES ('sp-1', 'lsp-1', 'Bắp Caramel', 'Vị ngọt', 'pop.jpg', 1, NOW(), 'system'),
       ('sp-3', 'lsp-2', 'Coca Cola', 'Có gas', 'coca.jpg', 1, NOW(), 'system');

INSERT INTO chi_tiet_san_pham_di_kem (id, id_san_pham, id_kich_co, id_don_vi_tinh, gia_tri_dinh_luong, huong_vi, gia_ban, so_luong_ton, ngay_tao, nguoi_tao)
VALUES ('ctsp-1', 'sp-1', 'kc-1', 'dvt-2', 100, 'Caramel', 45000, 100, NOW(), 'system'),
       ('ctsp-4', 'sp-3', 'kc-1', 'dvt-1', 500, 'Classic', 25000, 200, NOW(), 'system');

-- =================================================================
-- NHÓM 3: NHÂN VIÊN & KHÁCH HÀNG
-- =================================================================

INSERT INTO nhan_vien (id, id_tai_khoan, ma_nhan_vien, ten_nhan_vien, cccd, ngay_sinh, que_quan, gioi_tinh, chuc_vu, anh_nhan_vien, so_dien_thoai, trang_thai, ngay_tao, nguoi_tao)
VALUES ('nv-2', 'tk-2', 'NV002', 'Nguyễn Thị Lan', '031095001', '1995-05-12', 'HP', 0, 'Bán vé', 'nv2.jpg', '098123', 1, NOW(), 'system');

INSERT INTO khach_hang (id, id_tai_khoan, id_loai_khach_hang, ma_khach_hang, ten_khach_hang, gioi_tinh, sdt, ngay_sinh, hinh_anh, ghi_chu, trang_thai, ngay_tao, nguoi_tao)
VALUES ('kh-1', 'tk-4', 'lkh-2', 'KH001', 'Trần Anh Tuấn', 1, '093456789', '1998-01-01', 'c.jpg', 'Note', 1, NOW(), 'system');

-- =================================================================
-- NHÓM 4: GIAO DỊCH (TÍCH HỢP DATA ĐỂ TEST GOM NHÓM CHI TIẾT)
-- =================================================================

-- 1. HÓA ĐƠN HD-10001 (1 vé + 1 bắp)
INSERT INTO hoa_don (id, id_nhan_vien, id_phieu_giam_gia, id_khach_hang, ma_hoa_don, tong_tien, so_tien_giam, tong_tien_thanh_toan, phuong_thuc_thanh_toan, kenh_ban_hang, trang_thai, ghi_chu, ngay_tao, nguoi_tao)
VALUES ('hd-1', 'nv-2', NULL, 'kh-1', 'HD-10001', 130000, 0, 130000, 1, 0, 1, 'Test single', NOW(), 'system');

INSERT INTO ve (id, id_loai_khach_hang, id_ghe, id_suat_chieu, id_hoa_don, ma_ve, gia_thanh_toan, loai_ve, trang_thai, version, ngay_tao, nguoi_tao)
VALUES ('ve-1', 'lkh-2', 'g-1', 'sc-1', 'hd-1', 'VE001', 85000, 0, 1, 0, NOW(), 'system');

INSERT INTO hoa_don_chi_tiet (id, id_hoa_don, id_ve, id_chi_tiet_san_pham_di_kem, loai, so_luong, don_gia, thanh_tien, ngay_tao, nguoi_tao)
VALUES ('hdct-1-v', 'hd-1', 've-1', NULL, 1, 1, 85000, 85000, NOW(), 'system'),
       ('hdct-1-c', 'hd-1', NULL, 'ctsp-1', 2, 1, 45000, 45000, NOW(), 'system');

-- 2. HÓA ĐƠN HD020 (Gom nhóm 3 vé của cùng 1 phim)
INSERT INTO hoa_don (id, id_nhan_vien, id_khach_hang, ma_hoa_don, tong_tien, so_tien_giam, tong_tien_thanh_toan, phuong_thuc_thanh_toan, kenh_ban_hang, trang_thai, ngay_tao, nguoi_tao)
VALUES ('hd-20', 'nv-2', 'kh-1', 'HD020', 255000, 0, 255000, 1, 0, 1, NOW(), 'system');

INSERT INTO ve (id, id_loai_khach_hang, id_ghe, id_suat_chieu, id_hoa_don, ma_ve, gia_thanh_toan, loai_ve, trang_thai, version, ngay_tao, nguoi_tao)
VALUES ('ve-2', 'lkh-2', 'ghe-B1', 'sc-12', 'hd-20', 'VE002', 85000, 0, 1, 0, NOW(), 'system'),
       ('ve-3', 'lkh-2', 'ghe-B2', 'sc-12', 'hd-20', 'VE003', 85000, 0, 1, 0, NOW(), 'system'),
       ('ve-4', 'lkh-2', 'ghe-B3', 'sc-12', 'hd-20', 'VE004', 85000, 0, 1, 0, NOW(), 'system');

INSERT INTO hoa_don_chi_tiet (id, id_hoa_don, id_ve, id_chi_tiet_san_pham_di_kem, loai, so_luong, don_gia, thanh_tien, ngay_tao, nguoi_tao)
VALUES ('hdct-20-1', 'hd-20', 've-2', NULL, 1, 1, 85000, 85000, NOW(), 'system'),
       ('hdct-20-2', 'hd-20', 've-3', NULL, 1, 1, 85000, 85000, NOW(), 'system'),
       ('hdct-20-3', 'hd-20', 've-4', NULL, 1, 1, 85000, 85000, NOW(), 'system');

-- 3. HÓA ĐƠN HD022 (Gom nhóm 4 vé hoạt hình)
INSERT INTO hoa_don (id, id_nhan_vien, id_khach_hang, ma_hoa_don, tong_tien, so_tien_giam, tong_tien_thanh_toan, phuong_thuc_thanh_toan, kenh_ban_hang, trang_thai, ngay_tao, nguoi_tao)
VALUES ('hd-22', 'nv-2', 'kh-1', 'HD022', 300000, 0, 300000, 1, 0, 1, NOW(), 'system');

INSERT INTO ve (id, id_loai_khach_hang, id_ghe, id_suat_chieu, id_hoa_don, ma_ve, gia_thanh_toan, loai_ve, trang_thai, version, ngay_tao, nguoi_tao)
VALUES ('ve-6', 'lkh-2', 'ghe-C1', 'sc-10', 'hd-22', 'VE006', 75000, 0, 1, 0, NOW(), 'system'),
       ('ve-7', 'lkh-2', 'ghe-C2', 'sc-10', 'hd-22', 'VE007', 75000, 0, 1, 0, NOW(), 'system'),
       ('ve-8', 'lkh-2', 'ghe-C3', 'sc-10', 'hd-22', 'VE008', 75000, 0, 1, 0, NOW(), 'system'),
       ('ve-9', 'lkh-2', 'ghe-C4', 'sc-10', 'hd-22', 'VE009', 75000, 0, 1, 0, NOW(), 'system');

INSERT INTO hoa_don_chi_tiet (id, id_hoa_don, id_ve, id_chi_tiet_san_pham_di_kem, loai, so_luong, don_gia, thanh_tien, ngay_tao, nguoi_tao)
VALUES ('hdct-22-1', 'hd-22', 've-6', NULL, 1, 1, 75000, 75000, NOW(), 'system'),
       ('hdct-22-2', 'hd-22', 've-7', NULL, 1, 1, 75000, 75000, NOW(), 'system'),
       ('hdct-22-3', 'hd-22', 've-8', NULL, 1, 1, 75000, 75000, NOW(), 'system'),
       ('hdct-22-4', 'hd-22', 've-9', NULL, 1, 1, 75000, 75000, NOW(), 'system');

-- 4. HÓA ĐƠN HD023 (Gom nhóm 2 vé + Gom nhóm 2 Coca)
INSERT INTO hoa_don (id, id_nhan_vien, id_khach_hang, ma_hoa_don, tong_tien, so_tien_giam, tong_tien_thanh_toan, phuong_thuc_thanh_toan, kenh_ban_hang, trang_thai, ngay_tao, nguoi_tao)
VALUES ('hd-23', 'nv-2', 'kh-1', 'HD023', 230000, 0, 230000, 1, 0, 1, NOW(), 'system');

INSERT INTO ve (id, id_loai_khach_hang, id_ghe, id_suat_chieu, id_hoa_don, ma_ve, gia_thanh_toan, loai_ve, trang_thai, version, ngay_tao, nguoi_tao)
VALUES ('ve-10', 'lkh-2', 'ghe-D1', 'sc-11', 'hd-23', 'VE010', 90000, 0, 1, 0, NOW(), 'system'),
       ('ve-11', 'lkh-2', 'ghe-D2', 'sc-11', 'hd-23', 'VE011', 90000, 0, 1, 0, NOW(), 'system');

INSERT INTO hoa_don_chi_tiet (id, id_hoa_don, id_ve, id_chi_tiet_san_pham_di_kem, loai, so_luong, don_gia, thanh_tien, ngay_tao, nguoi_tao)
VALUES ('hdct-23-v1', 'hd-23', 've-10', NULL, 1, 1, 90000, 90000, NOW(), 'system'),
       ('hdct-23-v2', 'hd-23', 've-11', NULL, 1, 1, 90000, 90000, NOW(), 'system'),
       ('hdct-23-c', 'hd-23', NULL, 'ctsp-4', 2, 2, 25000, 50000, NOW(), 'system');

-- BẢNG THANH TOÁN ĐỒNG BỘ
INSERT INTO thanh_toan (id, id_hoa_don, ma_giao_dich, phuong_thuc_thanh_toan, so_tien, ngay_thanh_toan, noi_dung, trang_thai, ngay_tao, nguoi_tao)
VALUES ('tt-1', 'hd-1', 'TX10001', 1, 130000, NOW(), 'Thanh toán HD-10001', 1, NOW(), 'system'),
       ('tt-20', 'hd-20', 'TX10020', 1, 255000, NOW(), 'Thanh toán HD020', 1, NOW(), 'system'),
       ('tt-22', 'hd-22', 'TX10022', 1, 300000, NOW(), 'Thanh toán HD022', 1, NOW(), 'system'),
       ('tt-23', 'hd-23', 'TX10023', 1, 230000, NOW(), 'Thanh toán HD023', 1, NOW(), 'system');

SET FOREIGN_KEY_CHECKS = 1;