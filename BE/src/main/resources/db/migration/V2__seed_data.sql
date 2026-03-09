-- =================================================================
-- V2: UNIFIED REALISTIC DATASET (SYNCHRONIZED WITH V1 SCHEMA)
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
-- NHÓM 1: CÁC BẢNG CƠ SỞ (KHÔNG CHỨA KHÓA NGOẠI)
-- =================================================================

-- 1. BẢNG PHÂN QUYỀN
INSERT INTO phan_quyen (id, ma_phan_quyen, ten_vai_tro, quyen_han, trang_thai, ngay_tao, nguoi_tao)
VALUES ('pq-admin', 'ROLE_ADMIN', 'Quản trị viên', 'ALL', 1, NOW(), 'system'),
       ('pq-staff', 'ROLE_STAFF', 'Nhân viên bán vé', 'POS,CHECKIN', 1, NOW(), 'system'),
       ('pq-manager', 'ROLE_MANAGER', 'Quản lý rạp', 'REPORT,STOCKS', 1, NOW(), 'system'),
       ('pq-marketing', 'ROLE_MARKETING', 'Nhân viên Marketing', 'VOUCHER,NEWS', 1, NOW(), 'system'),
       ('pq-customer', 'ROLE_CUSTOMER', 'Khách hàng', 'BOOKING', 1, NOW(), 'system');

-- 2. BẢNG LOẠI NGÀY
INSERT INTO loai_ngay (id, ten_loai_ngay, he_so_ngay, ngay_tao, nguoi_tao)
VALUES ('ln-1', 'Thứ 2 - Thứ 5', 1.0, NOW(), 'system'),
       ('ln-2', 'Thứ 6', 1.1, NOW(), 'system'),
       ('ln-3', 'Thứ 7', 1.25, NOW(), 'system'),
       ('ln-4', 'Chủ nhật', 1.3, NOW(), 'system'),
       ('ln-5', 'Lễ Giáng Sinh', 1.5, NOW(), 'system'),
       ('ln-6', 'Tết Nguyên Đán', 2.0, NOW(), 'system'),
       ('ln-7', 'Ngày lễ đặc biệt', 1.8, NOW(), 'system'),
       ('ln-8', 'Sự kiện tri ân khách hàng', 0.8, NOW(), 'system');

-- 3. BẢNG LOẠI GHẾ
INSERT INTO loai_ghe (id, ten_loai, phu_phi, ngay_tao, nguoi_tao)
VALUES ('lg-1', 'Thường', 0, NOW(), 'system'),
       ('lg-2', 'VIP', 20000, NOW(), 'system'),
       ('lg-3', 'Couple (Đôi)', 50000, NOW(), 'system'),
       ('lg-4', 'Sofa', 60000, NOW(), 'system'),
       ('lg-5', 'Giường nằm Calla', 100000, NOW(), 'system');

-- 4. BẢNG LOẠI KHÁCH HÀNG
INSERT INTO loai_khach_hang (id, ten_loai, he_so_giam_gia, mo_ta, trang_thai, ngay_tao, nguoi_tao)
VALUES ('lkh-1', 'Vãng lai', 0, 'Khách chưa có tài khoản', 1, NOW(), 'system'),
       ('lkh-2', 'Thành viên mới', 0, 'Mới đăng ký', 1, NOW(), 'system'),
       ('lkh-3', 'Thành viên Đồng', 2, 'Tích lũy > 1tr', 1, NOW(), 'system'),
       ('lkh-4', 'Thành viên Bạc', 5, 'Tích lũy > 5tr', 1, NOW(), 'system'),
       ('lkh-5', 'Thành viên Vàng', 10, 'Tích lũy > 10tr', 1, NOW(), 'system'),
       ('lkh-6', 'Thành viên Bạch Kim', 15, 'Hạng cao cấp', 1, NOW(), 'system'),
       ('lkh-7', 'Thành viên Kim Cương', 20, 'Hạng tối cao', 1, NOW(), 'system'),
       ('lkh-8', 'Học sinh - Sinh viên', 15, 'Ưu đãi HSSV', 1, NOW(), 'system'),
       ('lkh-9', 'Người cao tuổi', 20, 'Ưu đãi > 60 tuổi', 1, NOW(), 'system'),
       ('lkh-10', 'Doanh nghiệp', 25, 'Đối tác B2B', 1, NOW(), 'system'),
       ('lkh-11', 'Khách hàng ưu tiên', 5, 'Đối tác booking', 1, NOW(), 'system'),
       ('lkh-12', 'Nhân viên nội bộ', 50, 'Đặc quyền nhân viên', 1, NOW(), 'system');

-- 5. BẢNG PHÒNG CHIẾU
INSERT INTO phong_chieu (id, ten_phong, loai_man_hinh, tong_ghe, trang_thai, ngay_tao, nguoi_tao)
VALUES ('pc-1', 'Phòng IMAX 01', 'IMAX 3D', 200, 1, NOW(), 'system'),
       ('pc-2', 'Phòng Gold Class', '2D Digital', 40, 1, NOW(), 'system'),
       ('pc-3', 'Phòng 03', '2D', 150, 1, NOW(), 'system'),
       ('pc-4', 'Phòng 04 (Mini)', '2D', 60, 1, NOW(), 'system'),
       ('pc-5', 'Phòng 05 (VIP)', '2D Digital', 30, 1, NOW(), 'system'),
       ('pc-6', 'Phòng IMAX 02', 'IMAX Laser', 250, 1, NOW(), 'system'),
       ('pc-7', 'Phòng chiếu ngoài trời', 'LED Screen', 100, 1, NOW(), 'system');

-- 6. BẢNG KHUNG GIỜ
INSERT INTO khung_gio (id, ten_khung_gio, thu_trong_tuan, gio_bat_dau, gio_ket_thuc, he_so_gia, la_khung_gio_vang, mo_ta, trang_thai, ngay_tao, nguoi_tao)
VALUES ('kg-1', 'Suất sớm', 1, '07:30:00', '10:00:00', 0.9, FALSE, 'Sáng sớm', 1, NOW(), 'system'),
       ('kg-2', 'Sáng', 1, '10:30:00', '13:00:00', 1.0, FALSE, 'Sáng', 1, NOW(), 'system'),
       ('kg-3', 'Trưa', 1, '13:30:00', '16:00:00', 1.1, FALSE, 'Trưa', 1, NOW(), 'system'),
       ('kg-4', 'Chiều', 1, '16:30:00', '19:00:00', 1.2, TRUE, 'Chiều cao điểm', 1, NOW(), 'system'),
       ('kg-5', 'Tối 1', 1, '19:30:00', '22:00:00', 1.3, TRUE, 'Giờ vàng cinema', 1, NOW(), 'system');

-- 7. BẢNG THỂ LOẠI
INSERT INTO the_loai (id, ten_the_loai, ngay_tao, nguoi_tao)
VALUES ('tl-1', 'Hành động', NOW(), 'system'),
       ('tl-2', 'Kinh dị', NOW(), 'system'),
       ('tl-3', 'Hoạt hình', NOW(), 'system'),
       ('tl-5', 'Hài hước', NOW(), 'system'),
       ('tl-6', 'Tình cảm', NOW(), 'system'),
       ('tl-7', 'Khoa học viễn tưởng', NOW(), 'system'),
       ('tl-10', 'Tâm lý', NOW(), 'system');

-- 8. BẢNG PHIM
INSERT INTO phim (id, ten_phim, ma_phim, thoi_luong, ngay_khoi_chieu, ngay_ket_thuc, lich_chieu, mo_ta, trailer, poster, ngon_ngu, do_tuoi, nhan_do_tuoi, hien_thi_canh_bao_do_tuoi, danh_gia, gia_phim, loai_phim, phu_phi_loai_phim, trang_thai, ngay_tao, nguoi_tao)
VALUES ('p-1', 'Avatar: Dòng chảy của nước', 'P001', 192, '2023-12-15', '2024-03-15', 'Daily', 'Phần 2 Avatar', 't1', 'p1', 'Tiếng Anh', 13, 'T13', TRUE, 8.5, 85000, 'IMAX 3D', 20000, 1, NOW(), 'system'),
       ('p-2', 'Chiến binh báo đen', 'P002', 161, '2023-11-10', '2024-01-10', 'Daily', 'Wakanda Forever', 't2', 'p2', 'Tiếng Anh', 13, 'T13', TRUE, 7.5, 80000, '2D', 0, 1, NOW(), 'system'),
       ('p-3', 'Mèo đi hia', 'P003', 102, '2023-12-22', '2024-02-22', 'Daily', 'Puss in Boots', 't3', 'p3', 'Tiếng Anh', 0, 'P', TRUE, 8.2, 75000, '2D', 0, 1, NOW(), 'system'),
       ('p-4', 'Lật Mặt 7: Một Điều Ước', 'LM007', 138, '2024-04-26', '2024-07-26', 'Daily', 'Gia đình bà Hai', 't4', 'p4', 'Tiếng Việt', 0, 'P', TRUE, 9.0, 95000, '2D', 0, 1, NOW(), 'system'),
       ('p-5', 'Mai', 'MAI01', 131, '2024-02-10', '2024-05-10', 'Daily', 'Phim của Trấn Thành', 't5', 'p5', 'Tiếng Việt', 18, 'T18', TRUE, 8.8, 90000, '2D Digital', 5000, 1, NOW(), 'system'),
       ('p-6', 'Deadpool & Wolverine', 'P006', 127, '2024-07-26', '2024-09-26', 'Daily', 'Marvel crossover', 't6', 'p6', 'Tiếng Anh', 18, 'T18', TRUE, 9.1, 95000, '2D', 0, 1, NOW(), 'system'),
       ('p-7', 'Inside Out 2', 'P007', 96, '2024-06-14', '2024-08-14', 'Daily', 'Pixar sequel', 't7', 'p7', 'Tiếng Anh', 0, 'P', TRUE, 8.9, 75000, '2D', 0, 1, NOW(), 'system'),
       ('p-8', 'Godzilla x Kong', 'P008', 115, '2024-03-29', '2024-05-29', 'Daily', 'Monsterverse', 't8', 'p8', 'Tiếng Anh', 13, 'T13', TRUE, 7.8, 90000, 'IMAX', 15000, 1, NOW(), 'system');

-- 9. BẢNG LOẠI SẢN PHẨM ĐI KÈM
INSERT INTO loai_san_pham_di_kem (id, ten_loai, ngay_tao, nguoi_tao)
VALUES ('lsp-1', 'Bắp rang', NOW(), 'system'),
       ('lsp-2', 'Nước uống', NOW(), 'system');

-- 10. BẢNG KÍCH CỠ
INSERT INTO kich_co (id, ten_kich_co, mo_ta)
VALUES ('kc-1', 'Nhỏ', 'S'),
       ('kc-2', 'Vừa', 'M'),
       ('kc-3', 'Lớn', 'L');

-- 11. BẢNG ĐƠN VỊ TÍNH
INSERT INTO don_vi_tinh (id, ten_don_vi_tinh, mo_ta)
VALUES ('dvt-1', 'Ly', 'L'),
       ('dvt-2', 'Hộp', 'H'),
       ('dvt-3', 'Chai', 'C');

-- 12. BẢNG PHIẾU GIẢM GIÁ
INSERT INTO phieu_giam_gia (id, ma_phieu_giam_gia, ten_phieu, kieu_phat_hanh, loai_phieu, phan_tram_giam_gia, so_tien_giam, gia_tri_hoa_don_toi_thieu, co_cho_cong_don, giam_toi_da, ngay_bat_dau, ngay_ket_thuc, trang_thai, so_luong, dieu_kien_ap_dung, ghi_chu, ngay_tao, nguoi_tao)
VALUES ('v-2', 'TET2026', 'Lì xì đầu năm', 1, 2, 0, 50000, 200000, 0, 50000, '2026-01-01', '2026-03-31', 1, 100, 'Hóa đơn > 200k', 'Dịp Tết', NOW(), 'system'),
       ('v-3', 'VALENTINE26', 'Lễ tình nhân', 1, 1, 10, 0, 150000, 1, 20000, '2026-02-10', '2026-02-20', 1, 200, 'Cặp đôi', 'Giảm 10%', NOW(), 'system'),
       ('v-4', 'WOMEN2026', 'Quốc tế Phụ nữ', 1, 1, 20, 0, 100000, 0, 40000, '2026-03-01', '2026-03-10', 1, 500, 'Khách hàng nữ', 'Chào mừng 8/3', NOW(), 'system'),
       ('v-5', 'STUDENT', 'Ưu đãi sinh viên', 1, 2, 0, 20000, 50000, 0, 20000, '2026-01-01', '2026-12-31', 1, 9999, 'Có thẻ HSSV', 'Giảm tiền mặt', NOW(), 'system'),
       ('v-31', 'VNPAY', 'Ưu đãi VNPAY', 1, 2, 0, 20000, 150000, 0, 20000, '2026-01-01', '2026-06-30', 1, 1000, 'Thanh toán VNPAY', 'Đối tác', NOW(), 'system');

-- =================================================================
-- NHÓM 2: CÁC BẢNG PHỤ THUỘC LEVEL 1 (THAM CHIẾU NHÓM 1)
-- =================================================================

-- 13. BẢNG TÀI KHOẢN
INSERT INTO tai_khoan (id, id_phan_quyen, email, mat_khau, trang_thai, ngay_tao, nguoi_tao)
VALUES ('tk-1', 'pq-admin', 'admin@cineops.com', 'pwd1', 1, NOW(), 'system'),
       ('tk-2', 'pq-staff', 'lan.nguyen@cineops.com', 'pwd123', 1, NOW(), 'system'),
       ('tk-3', 'pq-staff', 'quan.tran@cineops.com', 'pwd123', 1, NOW(), 'system'),
       ('tk-4', 'pq-customer', 'khach1@gmail.com', 'pwd3', 1, NOW(), 'system'),
       ('tk-12', 'pq-customer', 'kh012@gmail.com', '123456', 1, NOW(), 'system');

-- 14. BẢNG GHẾ (Supporting ve entries and grids)
INSERT INTO ghe (id, id_loai_ghe, id_phong_chieu, so_ghe, so_hang, so_cot, trang_thai, ngay_tao, nguoi_tao)
VALUES ('g-pc1-A1', 'lg-1', 'pc-1', 'A1', 'A', 1, 1, NOW(), 'system'),
       ('g-1', 'lg-1', 'pc-3', 'A1', 'A', 1, 1, NOW(), 'system'),
       ('ghe-B1', 'lg-2', 'pc-1', 'B1', 'B', 1, 1, NOW(), 'system'),
       ('ghe-B2', 'lg-2', 'pc-1', 'B2', 'B', 2, 1, NOW(), 'system'),
       ('ghe-B3', 'lg-2', 'pc-1', 'B3', 'B', 3, 1, NOW(), 'system'),
       ('ghe-B4', 'lg-2', 'pc-1', 'B4', 'B', 4, 1, NOW(), 'system'),
       ('ghe-C1', 'lg-1', 'pc-1', 'C1', 'C', 1, 1, NOW(), 'system'),
       ('ghe-C2', 'lg-1', 'pc-1', 'C2', 'C', 2, 1, NOW(), 'system'),
       ('ghe-C3', 'lg-1', 'pc-1', 'C3', 'C', 3, 1, NOW(), 'system'),
       ('ghe-C4', 'lg-1', 'pc-1', 'C4', 'C', 4, 1, NOW(), 'system'),
       ('ghe-D1', 'lg-1', 'pc-1', 'D1', 'D', 1, 1, NOW(), 'system'),
       ('ghe-D2', 'lg-1', 'pc-1', 'D2', 'D', 2, 1, NOW(), 'system');

-- 15. BẢNG SUẤT CHIẾU
INSERT INTO suat_chieu (id, id_khung_gio, id_phong_chieu, id_phim, ngay_chieu, gio_bat_dau, gio_ket_thuc, thoi_gian_don_ve_sinh, so_ghe_trong, trang_thai, ngay_tao, nguoi_tao)
VALUES ('sc-1', 'kg-5', 'pc-1', 'p-1', CURDATE(), '19:30:00', '22:00:00', 15, 40, 1, NOW(), 'system'),
       ('sc-10', 'kg-5', 'pc-1', 'p-7', CURDATE(), '19:30:00', '22:00:00', 15, 40, 1, NOW(), 'system'),
       ('sc-11', 'kg-5', 'pc-1', 'p-8', CURDATE(), '19:30:00', '22:00:00', 15, 40, 1, NOW(), 'system'),
       ('sc-12', 'kg-5', 'pc-1', 'p-4', CURDATE(), '19:30:00', '22:00:00', 15, 40, 1, NOW(), 'system'),
       ('sc-13', 'kg-5', 'pc-1', 'p-5', CURDATE(), '19:30:00', '22:00:00', 15, 40, 1, NOW(), 'system'),
       ('sc-14', 'kg-5', 'pc-1', 'p-6', CURDATE(), '19:30:00', '22:00:00', 15, 40, 1, NOW(), 'system'),
       ('sc-15', 'kg-5', 'pc-1', 'p-2', CURDATE(), '19:30:00', '22:00:00', 15, 40, 1, NOW(), 'system');

-- 16. BẢNG SẢN PHẨM ĐI KÈM
INSERT INTO san_pham_di_kem (id, id_loai_san_pham, ten_san_pham, mo_ta, hinh_anh, trang_thai, ngay_tao, nguoi_tao)
VALUES ('sp-1', 'lsp-1', 'Bắp Caramel', 'Vị ngọt', 'pop.jpg', 1, NOW(), 'system'),
       ('sp-3', 'lsp-2', 'Coca Cola', 'Có gas', 'coca.jpg', 1, NOW(), 'system');

-- =================================================================
-- NHÓM 3: CÁC BẢNG LIÊN QUAN ĐẾN USER & SẢN PHẨM CHI TIẾT
-- =================================================================

-- 17. BẢNG NHÂN VIÊN
INSERT INTO nhan_vien (id, id_tai_khoan, ma_nhan_vien, ten_nhan_vien, cccd, ngay_sinh, que_quan, gioi_tinh, chuc_vu, anh_nhan_vien, so_dien_thoai, trang_thai, ngay_tao, nguoi_tao)
VALUES ('nv-1', 'tk-1', 'NV001', 'Đức Huy', '123', '1990-01-01', 'HN', 1, 'Admin', 'a.jpg', '091', 1, NOW(), 'system'),
       ('nv-2', 'tk-2', 'NV002', 'Nguyễn Thị Lan', '031095001', '1995-05-12', 'HP', 0, 'Bán vé', 'nv2.jpg', '098123', 1, NOW(), 'system'),
       ('nv-3', 'tk-3', 'NV003', 'Trần Anh Quân', '031092002', '1992-08-20', 'HN', 1, 'Quản lý', 'nv3.jpg', '098456', 1, NOW(), 'system');

-- 18. BẢNG KHÁCH HÀNG
INSERT INTO khach_hang (id, id_tai_khoan, id_loai_khach_hang, ma_khach_hang, ten_khach_hang, gioi_tinh, sdt, ngay_sinh, hinh_anh, ghi_chu, trang_thai, ngay_tao, nguoi_tao)
VALUES ('kh-1', 'tk-4', 'lkh-2', 'KH001', 'Tuấn Anh', 1, '093', '1998-01-01', 'c.jpg', 'Note', 1, NOW(), 'system'),
       ('kh-2', 'tk-12', 'lkh-3', 'KH002', 'Nguyễn Văn An', 1, '0901001', '2000-01-01', 'd.jpg', NULL, 1, NOW(), 'system');

-- 19. BẢNG CHI TIẾT SẢN PHẨM ĐI KÈM
INSERT INTO chi_tiet_san_pham_di_kem (id, id_san_pham, id_kich_co, id_don_vi_tinh, gia_tri_dinh_luong, huong_vi, gia_ban, so_luong_ton, ngay_tao, nguoi_tao)
VALUES ('ctsp-1', 'sp-1', 'kc-1', 'dvt-2', 100, 'Caramel', 45000, 100, NOW(), 'system'),
       ('ctsp-4', 'sp-3', 'kc-1', 'dvt-1', 500, 'Classic', 25000, 200, NOW(), 'system');

-- =================================================================
-- NHÓM 5: GIAO DỊCH LÕI (HÓA ĐƠN -> VÉ -> CHI TIẾT HÓA ĐƠN)
-- =================================================================

-- 20. BẢNG HÓA ĐƠN
INSERT INTO hoa_don (id, id_nhan_vien, id_phieu_giam_gia, id_khach_hang, ma_hoa_don, tong_tien, so_tien_giam, tong_tien_thanh_toan, phuong_thuc_thanh_toan, kenh_ban_hang, trang_thai, ghi_chu, thoi_gian_het_han, ngay_tao, nguoi_tao)
VALUES ('hd-1', 'nv-2', NULL, 'kh-1', 'HD001', 130000, 0, 130000, 1, 0, 1, 'Ghi chú 1', NULL, NOW(), 'system'),
       ('hd-20', 'nv-2', NULL, 'kh-1', 'HD020', 255000, 0, 255000, 1, 0, 1, 'Group 1', NULL, NOW(), 'system'),
       ('hd-21', 'nv-2', NULL, 'kh-1', 'HD021', 95000, 0, 95000, 1, 0, 1, 'Group 2', NULL, NOW(), 'system'),
       ('hd-22', 'nv-2', NULL, 'kh-1', 'HD022', 300000, 0, 300000, 1, 0, 1, 'Group 3', NULL, NOW(), 'system'),
       ('hd-23', 'nv-2', NULL, 'kh-1', 'HD023', 180000, 0, 180000, 1, 0, 1, 'Group 4', NULL, NOW(), 'system');

-- 21. BẢNG VÉ
INSERT INTO ve (id, id_loai_khach_hang, id_ghe, id_suat_chieu, id_hoa_don, ma_ve, gia_thanh_toan, loai_ve, trang_thai, version, ngay_tao, nguoi_tao)
VALUES ('ve-1', 'lkh-2', 'g-1', 'sc-1', 'hd-1', 'VE001', 85000, 0, 1, 0, NOW(), 'system'),
       ('ve-2', 'lkh-5', 'ghe-B1', 'sc-12', 'hd-20', 'VE002', 85000, 0, 1, 0, NOW(), 'system'),
       ('ve-3', 'lkh-5', 'ghe-B2', 'sc-12', 'hd-20', 'VE003', 85000, 0, 1, 0, NOW(), 'system'),
       ('ve-4', 'lkh-5', 'ghe-B3', 'sc-12', 'hd-20', 'VE004', 85000, 0, 1, 0, NOW(), 'system'),
       ('ve-5', 'lkh-4', 'ghe-C1', 'sc-14', 'hd-21', 'VE005', 95000, 0, 1, 0, NOW(), 'system'),
       ('ve-6', 'lkh-3', 'ghe-C2', 'sc-10', 'hd-22', 'VE006', 75000, 0, 1, 0, NOW(), 'system'),
       ('ve-7', 'lkh-3', 'ghe-C3', 'sc-10', 'hd-22', 'VE007', 75000, 0, 1, 0, NOW(), 'system'),
       ('ve-8', 'lkh-3', 'ghe-C4', 'sc-10', 'hd-22', 'VE008', 75000, 0, 1, 0, NOW(), 'system'),
       ('ve-9', 'lkh-3', 'ghe-B4', 'sc-10', 'hd-22', 'VE009', 75000, 0, 1, 0, NOW(), 'system'),
       ('ve-10', 'lkh-2', 'ghe-D1', 'sc-11', 'hd-23', 'VE010', 90000, 0, 1, 0, NOW(), 'system'),
       ('ve-11', 'lkh-2', 'ghe-D2', 'sc-11', 'hd-23', 'VE011', 90000, 0, 1, 0, NOW(), 'system');

-- 22. BẢNG HÓA ĐƠN CHI TIẾT
INSERT INTO hoa_don_chi_tiet (id, id_hoa_don, id_ve, id_chi_tiet_san_pham_di_kem, loai, so_luong, don_gia, thanh_tien, ngay_tao, nguoi_tao)
VALUES ('hdct-1', 'hd-1', 've-1', NULL, 1, 1, 85000, 85000, NOW(), 'system');

-- 23. BẢNG THANH TOÁN
INSERT INTO thanh_toan (id, id_hoa_don, ma_giao_dich, phuong_thuc_thanh_toan, so_tien, ngay_thanh_toan, noi_dung, trang_thai, ngay_tao, nguoi_tao)
VALUES ('tt-1', 'hd-1', 'TX001', 1, 130000, NOW(), 'Thanh toán HD001', 1, NOW(), 'system');
