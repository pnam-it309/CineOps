SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 1. BẢNG PHÂN QUYỀN
INSERT INTO phan_quyen (id, ma_phan_quyen, ten_vai_tro, quyen_han, trang_thai, ngay_tao, nguoi_tao, ngay_cap_nhat, nguoi_cap_nhat)
VALUES ('pq-1', 'ROLE_ADMIN', 'Quản trị viên', 'ALL_PRIVILEGES', 1, NOW(), 'system', NOW(), 'system'),
       ('pq-2', 'ROLE_STAFF', 'Nhân viên', 'SALE_PRIVILEGES', 1, NOW(), 'system', NOW(), 'system'),
       ('pq-3', 'ROLE_USER', 'Khách hàng', 'BOOKING_PRIVILEGES', 1, NOW(), 'system', NOW(), 'system'),
       ('pq-4', 'ROLE_MANAGER', 'Quản lý', 'MANAGEMENT_PRIVILEGES', 1, NOW(), 'system', NOW(), 'system'),
       ('pq-5', 'ROLE_GUEST', 'Khách vãng lai', 'VIEW_ONLY', 1, NOW(), 'system', NOW(), 'system');

-- 2. BẢNG LOẠI KHÁCH HÀNG
INSERT INTO loai_khach_hang (id, ten_loai, he_so_giam_gia, mo_ta, trang_thai, ngay_tao, nguoi_tao, ngay_cap_nhat, nguoi_cap_nhat)
VALUES ('lkh-1', 'Bạc', 0.0, 'Khách hàng mới', 1, NOW(), 'system', NOW(), 'system'),
       ('lkh-2', 'Vàng', 5.0, 'Khách hàng thân thiết', 1, NOW(), 'system', NOW(), 'system'),
       ('lkh-3', 'Kim cương', 10.0, 'Khách hàng VIP', 1, NOW(), 'system', NOW(), 'system'),
       ('lkh-4', 'Thành viên', 2.0, 'Hạng phổ thông', 1, NOW(), 'system', NOW(), 'system'),
       ('lkh-5', 'Đối tác', 15.0, 'Khách hàng doanh nghiệp', 1, NOW(), 'system', NOW(), 'system');

-- 3. BẢNG PHÒNG CHIẾU
INSERT INTO phong_chieu (id, ten_phong, loai_man_hinh, tong_ghe, trang_thai, ngay_tao, nguoi_tao, ngay_cap_nhat, nguoi_cap_nhat)
VALUES ('pc-1', 'Phòng 01', 'IMAX', 100, 1, NOW(), 'system', NOW(), 'system'),
       ('pc-2', 'Phòng 02', '2D Standard', 150, 1, NOW(), 'system', NOW(), 'system'),
       ('pc-3', 'Phòng 03', '3D Dolby', 120, 1, NOW(), 'system', NOW(), 'system'),
       ('pc-4', 'Phòng 04', '4DX', 80, 1, NOW(), 'system', NOW(), 'system'),
       ('pc-5', 'Phòng 05', 'Gold Class', 40, 1, NOW(), 'system', NOW(), 'system');

-- 4. BẢNG LOẠI GHẾ
INSERT INTO loai_ghe (id, ten_loai, phu_phi, ngay_tao, nguoi_tao, ngay_cap_nhat, nguoi_cap_nhat)
VALUES ('lg-1', 'Thường', 0.0, NOW(), 'system', NOW(), 'system'),
       ('lg-2', 'VIP', 20000.0, NOW(), 'system', NOW(), 'system'),
       ('lg-3', 'Sweetbox', 50000.0, NOW(), 'system', NOW(), 'system'),
       ('lg-4', 'Deluxe', 30000.0, NOW(), 'system', NOW(), 'system'),
       ('lg-5', 'Premium', 40000.0, NOW(), 'system', NOW(), 'system');

-- 5. BẢNG KHUNG GIỜ
INSERT INTO khung_gio (id, ten_khung_gio, thu_trong_tuan, gio_bat_dau, gio_ket_thuc, he_so_gia, la_khung_gio_vang, mo_ta, trang_thai, ngay_tao, nguoi_tao, ngay_cap_nhat, nguoi_cap_nhat)
VALUES ('kg-1', 'Sáng sớm', 2, '08:00:00', '10:00:00', 1.0, 0, 'Giờ hành chính', 1, NOW(), 'system', NOW(), 'system'),
       ('kg-2', 'Trưa', 3, '11:00:00', '13:00:00', 1.0, 0, 'Giờ nghỉ trưa', 1, NOW(), 'system', NOW(), 'system'),
       ('kg-3', 'Chiều', 4, '14:00:00', '17:00:00', 1.1, 0, 'Giờ tan tầm', 1, NOW(), 'system', NOW(), 'system'),
       ('kg-4', 'Tối', 5, '18:00:00', '21:00:00', 1.3, 1, 'Giờ cao điểm', 1, NOW(), 'system', NOW(), 'system'),
       ('kg-5', 'Khuya', 6, '22:00:00', '00:00:00', 1.2, 0, 'Suất đêm', 1, NOW(), 'system', NOW(), 'system');

-- 6. BẢNG PHIM
INSERT INTO phim (id, ten_phim, ma_phim, thoi_luong, ngay_khoi_chieu, ngay_ket_thuc, lich_chieu, dao_dien, dien_vien, mo_ta, trailer, poster, ngon_ngu, do_tuoi, nhan_do_tuoi, hien_thi_canh_bao_do_tuoi, danh_gia, gia_phim, loai_phim, phu_phi_loai_phim, trang_thai, ngay_tao, nguoi_tao, ngay_cap_nhat, nguoi_cap_nhat)
VALUES ('p-1', 'Avatar 2', 'AVT02', 192, '2025-01-01', '2025-03-01', 'Hàng ngày', 'James Cameron', 'Sam Worthington', 'Hành tinh Pandora', 'youtube.com/avt2', 'poster1.png', 'English', 13, 'T13', 1, 9.0, 100000.0, '3D', 20000.0, 1, NOW(), 'system', NOW(), 'system'),
       ('p-2', 'Lật Mặt 7', 'LM07', 138, '2025-02-01', '2025-04-01', 'Hàng ngày', 'Lý Hải', 'Nghệ sĩ Thanh Hiền', 'Câu chuyện gia đình', 'youtube.com/lm7', 'poster2.png', 'Vietnamese', 0, 'P', 1, 8.5, 80000.0, '2D', 0.0, 1, NOW(), 'system', NOW(), 'system'),
       ('p-3', 'Dune 2', 'DUNE02', 166, '2025-03-01', '2025-05-01', 'Hàng ngày', 'Denis Villeneuve', 'Timothée Chalamet', 'Hành tinh cát', 'youtube.com/dune2', 'poster3.png', 'English', 13, 'T13', 1, 9.5, 110000.0, 'IMAX', 30000.0, 1, NOW(), 'system', NOW(), 'system'),
       ('p-4', 'Conan Movie 27', 'CONAN27', 110, '2025-04-01', '2025-06-01', 'Hàng ngày', 'Chika Nagaoka', 'Minami Takayama', 'Thám tử lừng danh', 'youtube.com/conan', 'poster4.png', 'Japanese', 13, 'T13', 1, 8.8, 70000.0, '2D', 0.0, 1, NOW(), 'system', NOW(), 'system'),
       ('p-5', 'Inside Out 2', 'IO2', 96, '2025-05-01', '2025-07-01', 'Hàng ngày', 'Kelsey Mann', 'Amy Poehler', 'Những mảnh ghép cảm xúc', 'youtube.com/io2', 'poster5.png', 'English', 0, 'P', 1, 9.2, 90000.0, '2D', 0.0, 1, NOW(), 'system', NOW(), 'system');

-- 7. BẢNG THỂ LOẠI
INSERT INTO the_loai (id, ten_the_loai, ngay_tao, nguoi_tao, ngay_cap_nhat, nguoi_cap_nhat)
VALUES ('tl-1', 'Hành động', NOW(), 'system', NOW(), 'system'),
       ('tl-2', 'Tình cảm', NOW(), 'system', NOW(), 'system'),
       ('tl-3', 'Kinh dị', NOW(), 'system', NOW(), 'system'),
       ('tl-4', 'Hoạt hình', NOW(), 'system', NOW(), 'system'),
       ('tl-5', 'Hài kịch', NOW(), 'system', NOW(), 'system');

-- 8. BẢNG LOẠI NGÀY
INSERT INTO loai_ngay (id, ten_loai_ngay, he_so_ngay, ngay_tao, nguoi_tao, ngay_cap_nhat, nguoi_cap_nhat)
VALUES ('ln-1', 'Ngày thường', 1.0, NOW(), 'system', NOW(), 'system'),
       ('ln-2', 'Cuối tuần', 1.2, NOW(), 'system', NOW(), 'system'),
       ('ln-3', 'Ngày lễ', 1.5, NOW(), 'system', NOW(), 'system'),
       ('ln-4', 'Thứ 4 vui vẻ', 0.8, NOW(), 'system', NOW(), 'system'),
       ('ln-5', 'Suất chiếu sớm', 1.3, NOW(), 'system', NOW(), 'system');

-- 9. BẢNG PHIẾU GIẢM GIÁ
INSERT INTO phieu_giam_gia (id, ma_phieu_giam_gia, ten_phieu, kieu_phat_hanh, loai_phieu, phan_tram_giam_gia, so_tien_giam, gia_tri_hoa_don_toi_thieu, co_cho_cong_don, giam_toi_da, ngay_bat_dau, ngay_ket_thuc, trang_thai, so_luong, dieu_kien_ap_dung, ghi_chu, ngay_tao, nguoi_tao, ngay_cap_nhat, nguoi_cap_nhat)
VALUES ('pg-1', 'GIAM20K', 'Giảm 20k', 0, 1, 0, 20000, 100000, 0, 20000, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 1, 100, 'Áp dụng mọi suất', 'Test', NOW(), 'system', NOW(), 'system'),
       ('pg-2', 'VC10PER', 'Giảm 10%', 0, 0, 10, 0, 200000, 0, 50000, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 1, 50, 'Áp dụng trên 200k', 'Test', NOW(), 'system', NOW(), 'system'),
       ('pg-3', 'HE2025', 'Chào hè 2025', 1, 1, 0, 50000, 500000, 1, 50000, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 1, 200, 'VIP member', 'Test', NOW(), 'system', NOW(), 'system'),
       ('pg-4', 'MILKTEA', 'Tặng trà sữa', 0, 1, 0, 30000, 150000, 0, 30000, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 1, 500, 'Combo bắp nước', 'Test', NOW(), 'system', NOW(), 'system'),
       ('pg-5', 'NEWBIE', 'Khách mới', 1, 0, 20, 0, 50000, 0, 100000, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 1, 1000, 'Lần đầu đặt vé', 'Test', NOW(), 'system', NOW(), 'system');

-- 10. BẢNG LOẠI SẢN PHẨM ĐI KÈM
INSERT INTO loai_san_pham_di_kem (id, ten_loai, ngay_tao, nguoi_tao, ngay_cap_nhat, nguoi_cap_nhat)
VALUES ('lsp-1', 'Đồ ăn', NOW(), 'system', NOW(), 'system'),
       ('lsp-2', 'Nước uống', NOW(), 'system', NOW(), 'system'),
       ('lsp-3', 'Combo', NOW(), 'system', NOW(), 'system'),
       ('lsp-4', 'Quà tặng', NOW(), 'system', NOW(), 'system'),
       ('lsp-5', 'Khác', NOW(), 'system', NOW(), 'system');

-- 11. BẢNG KÍCH CỠ
INSERT INTO kich_co (id, ten_kich_co, mo_ta)
VALUES ('kc-1', 'S', 'Size nhỏ'),
       ('kc-2', 'M', 'Size vừa'),
       ('kc-3', 'L', 'Size lớn'),
       ('kc-4', 'XL', 'Size đại'),
       ('kc-5', 'Bình thường', 'Không phân size');

-- 12. BẢNG ĐƠN VỊ TÍNH
INSERT INTO don_vi_tinh (id, ten_don_vi_tinh, mo_ta)
VALUES ('dvt-1', 'Ly', 'Dành cho nước'),
       ('dvt-2', 'Hộp', 'Dành cho bắp'),
       ('dvt-3', 'Cái', 'Dành cho đồ chơi'),
       ('dvt-4', 'Phần', 'Dành cho combo'),
       ('dvt-5', 'Chai', 'Nước đóng chai');

-- 13. BẢNG TÀI KHOẢN
INSERT INTO tai_khoan (id, id_phan_quyen, email, mat_khau, trang_thai, ngay_tao, nguoi_tao, ngay_cap_nhat, nguoi_cap_nhat)
VALUES ('tk-1', 'pq-1', 'admin@gmail.com', '$2a$10$abcdef', 1, NOW(), 'system', NOW(), 'system'),
       ('tk-2', 'pq-2', 'nv01@gmail.com', '$2a$10$abcdef', 1, NOW(), 'system', NOW(), 'system'),
       ('tk-3', 'pq-3', 'kh01@gmail.com', '$2a$10$abcdef', 1, NOW(), 'system', NOW(), 'system'),
       ('tk-4', 'pq-2', 'nv02@gmail.com', '$2a$10$abcdef', 1, NOW(), 'system', NOW(), 'system'),
       ('tk-5', 'pq-3', 'kh02@gmail.com', '$2a$10$abcdef', 1, NOW(), 'system', NOW(), 'system');

-- 14. BẢNG GHẾ
INSERT INTO ghe (id, id_loai_ghe, id_phong_chieu, so_ghe, so_hang, so_cot, trang_thai, ngay_tao, nguoi_tao, ngay_cap_nhat, nguoi_cap_nhat)
VALUES ('g-1', 'lg-1', 'pc-1', 'A01', 'A', 1, 1, NOW(), 'system', NOW(), 'system'),
       ('g-2', 'lg-1', 'pc-1', 'A02', 'A', 2, 1, NOW(), 'system', NOW(), 'system'),
       ('g-3', 'lg-2', 'pc-1', 'B01', 'B', 1, 1, NOW(), 'system', NOW(), 'system'),
       ('g-4', 'lg-2', 'pc-1', 'B02', 'B', 2, 1, NOW(), 'system', NOW(), 'system'),
       ('g-5', 'lg-3', 'pc-1', 'S01', 'S', 1, 1, NOW(), 'system', NOW(), 'system');

-- 15. BẢNG PHIM_THỂ LOẠI
INSERT INTO phim_the_loai (id, id_phim, id_the_loai)
VALUES ('ptl-1', 'p-1', 'tl-1'),
       ('ptl-2', 'p-1', 'tl-2'),
       ('ptl-3', 'p-2', 'tl-5'),
       ('ptl-4', 'p-3', 'tl-1'),
       ('ptl-5', 'p-5', 'tl-4');

-- 16. BẢNG SUẤT CHIẾU
INSERT INTO suat_chieu (id, id_khung_gio, id_phong_chieu, id_phim, ngay_chieu, gio_bat_dau, gio_ket_thuc, thoi_gian_don_ve_sinh, so_ghe_trong, trang_thai, ngay_tao, nguoi_tao, ngay_cap_nhat, nguoi_cap_nhat)
VALUES ('sc-1', 'kg-1', 'pc-1', 'p-1', CURDATE(), '08:00:00', '11:12:00', 15, 100, 1, NOW(), 'system', NOW(), 'system'),
       ('sc-2', 'kg-2', 'pc-1', 'p-2', CURDATE(), '11:30:00', '13:48:00', 15, 100, 1, NOW(), 'system', NOW(), 'system'),
       ('sc-3', 'kg-4', 'pc-2', 'p-3', CURDATE(), '18:00:00', '20:46:00', 15, 150, 1, NOW(), 'system', NOW(), 'system'),
       ('sc-4', 'kg-5', 'pc-3', 'p-1', CURDATE(), '22:00:00', '01:12:00', 15, 120, 1, NOW(), 'system', NOW(), 'system'),
       ('sc-5', 'kg-1', 'pc-4', 'p-5', DATE_ADD(CURDATE(), INTERVAL 1 DAY), '08:00:00', '09:36:00', 15, 80, 1, NOW(), 'system', NOW(), 'system');

-- 17. BẢNG GIÁ VÉ CHI TIẾT
INSERT INTO gia_ve_chi_tiet (id, id_loai_ngay, id_loai_khach_hang, id_loai_ghe, id_khung_gio, gia_tien, trang_thai, ngay_tao, nguoi_tao, ngay_cap_nhat, nguoi_cap_nhat)
VALUES ('gvc-1', 'ln-1', 'lkh-1', 'lg-1', 'kg-1', 45000.0, 1, NOW(), 'system', NOW(), 'system'),
       ('gvc-2', 'ln-2', 'lkh-2', 'lg-2', 'kg-4', 75000.0, 1, NOW(), 'system', NOW(), 'system'),
       ('gvc-3', 'ln-3', 'lkh-3', 'lg-3', 'kg-4', 150000.0, 1, NOW(), 'system', NOW(), 'system'),
       ('gvc-4', 'ln-1', 'lkh-4', 'lg-1', 'kg-2', 50000.0, 1, NOW(), 'system', NOW(), 'system'),
       ('gvc-5', 'ln-5', 'lkh-2', 'lg-5', 'kg-4', 120000.0, 1, NOW(), 'system', NOW(), 'system');

-- 18. BẢNG SẢN PHẨM ĐI KÈM
INSERT INTO san_pham_di_kem (id, id_loai_san_pham, ten_san_pham, mo_ta, hinh_anh, trang_thai, ngay_tao, nguoi_tao, ngay_cap_nhat, nguoi_cap_nhat)
VALUES ('sp-1', 'lsp-1', 'Bắp rang bơ', 'Vị truyền thống', 'bap1.png', 1, NOW(), 'system', NOW(), 'system'),
       ('sp-2', 'lsp-2', 'Pepsi', 'Nước ngọt', 'pepsi.png', 1, NOW(), 'system', NOW(), 'system'),
       ('sp-3', 'lsp-1', 'Bắp rang Phô mai', 'Vị phô mai', 'bap2.png', 1, NOW(), 'system', NOW(), 'system'),
       ('sp-4', 'lsp-2', 'Trà đào', 'Thanh mát', 'tradao.png', 1, NOW(), 'system', NOW(), 'system'),
       ('sp-5', 'lsp-3', 'Combo Solo', '1 Bắp + 1 Nước', 'combo1.png', 1, NOW(), 'system', NOW(), 'system');

-- 19. BẢNG KHÁCH HÀNG
INSERT INTO khach_hang (id, id_tai_khoan, id_loai_khach_hang, ma_khach_hang, ten_khach_hang, gioi_tinh, sdt, ngay_sinh, hinh_anh, ghi_chu, trang_thai, ngay_tao, nguoi_tao, ngay_cap_nhat, nguoi_cap_nhat)
VALUES ('kh-1', 'tk-3', 'lkh-1', 'KH001', 'Nguyễn Văn A', 1, '0901234567', '1995-10-10', 'kh1.png', 'VIP', 1, NOW(), 'system', NOW(), 'system'),
       ('kh-2', 'tk-5', 'lkh-2', 'KH002', 'Lê Thị B', 0, '0901234568', '1998-05-12', 'kh2.png', 'Thân thiết', 1, NOW(), 'system', NOW(), 'system'),
       ('kh-3', NULL, 'lkh-1', 'KH003', 'Khách lẻ 01', 1, '0901234569', NULL, 'default.png', 'Walk-in', 1, NOW(), 'system', NOW(), 'system'),
       ('kh-4', NULL, 'lkh-4', 'KH004', 'Trần Văn C', 1, '0901234570', '1990-01-01', 'kh4.png', 'Gần rạp', 1, NOW(), 'system', NOW(), 'system'),
       ('kh-5', NULL, 'lkh-2', 'KH005', 'Phạm Thị D', 0, '0901234571', '2000-12-30', 'kh5.png', 'Học sinh', 1, NOW(), 'system', NOW(), 'system');

-- 20. BẢNG NHÂN VIÊN
INSERT INTO nhan_vien (id, id_tai_khoan, ma_nhan_vien, ten_nhan_vien, cccd, ngay_sinh, que_quan, gioi_tinh, chuc_vu, anh_nhan_vien, so_dien_thoai, trang_thai, ngay_tao, nguoi_tao, ngay_cap_nhat, nguoi_cap_nhat)
VALUES ('nv-1', 'tk-2', 'NV001', 'Trần Nhân Viên', '123456789012', '1996-01-01', 'Hà Nội', 1, 'Bán vé', 'nv1.png', '0999999999', 1, NOW(), 'system', NOW(), 'system'),
       ('nv-2', 'tk-4', 'NV002', 'Lê Kế Toán', '123456789013', '1994-05-05', 'Hải Phòng', 0, 'Kế toán', 'nv2.png', '0999999998', 1, NOW(), 'system', NOW(), 'system'),
       ('nv-3', 'tk-1', 'NV003', 'Sếp Tổng', '123456789014', '1985-12-12', 'TP HCM', 1, 'Admin', 'nv3.png', '0999999997', 1, NOW(), 'system', NOW(), 'system'),
       ('nv-4', NULL, 'NV004', 'Bảo Vệ', '123456789015', '1980-01-01', 'Nam Định', 1, 'An ninh', 'nv4.png', '0999999996', 1, NOW(), 'system', NOW(), 'system'),
       ('nv-5', NULL, 'NV005', 'Tạp Vụ', '123456789016', '1990-06-06', 'Thanh Hóa', 0, 'Vệ sinh', 'nv5.png', '0999999995', 1, NOW(), 'system', NOW(), 'system');

-- 21. BẢNG TOKEN
INSERT INTO token (id, id_tai_khoan, ma_token, ngay_het_han, trang_thai, ngay_tao, nguoi_tao, ngay_cap_nhat, nguoi_cap_nhat)
VALUES ('tok-1', 'tk-1', 'abc-xyz-123', DATE_ADD(NOW(), INTERVAL 1 DAY), 1, NOW(), 'system', NOW(), 'system'),
       ('tok-2', 'tk-2', 'abc-xyz-456', DATE_ADD(NOW(), INTERVAL 1 DAY), 1, NOW(), 'system', NOW(), 'system'),
       ('tok-3', 'tk-3', 'abc-xyz-789', DATE_ADD(NOW(), INTERVAL 1 DAY), 1, NOW(), 'system', NOW(), 'system'),
       ('tok-4', 'tk-4', 'abc-xyz-012', DATE_ADD(NOW(), INTERVAL 1 DAY), 1, NOW(), 'system', NOW(), 'system'),
       ('tok-5', 'tk-5', 'abc-xyz-345', DATE_ADD(NOW(), INTERVAL 1 DAY), 1, NOW(), 'system', NOW(), 'system');

-- 22. BẢNG CHI TIẾT SẢN PHẨM ĐI KÈM
INSERT INTO chi_tiet_san_pham_di_kem (id, id_san_pham, id_kich_co, id_don_vi_tinh, gia_tri_dinh_luong, huong_vi, gia_ban, so_luong_ton, ngay_tao, nguoi_tao, ngay_cap_nhat, nguoi_cap_nhat)
VALUES ('ctsp-1', 'sp-1', 'kc-1', 'dvt-2', 500.0, 'Mặn', 45000.0, 100, NOW(), 'system', NOW(), 'system'),
       ('ctsp-2', 'sp-1', 'kc-2', 'dvt-2', 750.0, 'Phô mai', 55000.0, 80, NOW(), 'system', NOW(), 'system'),
       ('ctsp-3', 'sp-2', 'kc-2', 'dvt-1', 500.0, 'Truyền thống', 25000.0, 200, NOW(), 'system', NOW(), 'system'),
       ('ctsp-4', 'sp-5', 'kc-5', 'dvt-4', 1.0, 'Mixed', 85000.0, 50, NOW(), 'system', NOW(), 'system'),
       ('ctsp-5', 'sp-4', 'kc-3', 'dvt-1', 700.0, 'Peach', 35000.0, 100, NOW(), 'system', NOW(), 'system');

-- 23. BẢNG ĐỊA CHỈ
INSERT INTO dia_chi (id, id_khach_hang, id_nhan_vien, ma_dia_chi, thanh_pho_tinh, quan_huyen, phuong_xa, dia_chi_chi_tiet, trang_thai, ngay_tao, nguoi_tao, ngay_cap_nhat, nguoi_cap_nhat)
VALUES ('dc-1', 'kh-1', NULL, 'DC001', 'Hà Nội', 'Cầu Giấy', 'Dịch Vọng', 'Số 1 Duy Tân', 1, NOW(), 'system', NOW(), 'system'),
       ('dc-2', 'kh-2', NULL, 'DC002', 'Đà Nẵng', 'Hải Châu', 'Hòa Cường', '123 Phan Chu Trinh', 1, NOW(), 'system', NOW(), 'system'),
       ('dc-3', NULL, 'nv-1', 'DC003', 'Hà Nội', 'Nam Từ Liêm', 'Mỹ Đình', 'Vinhomes Gardenia', 1, NOW(), 'system', NOW(), 'system'),
       ('dc-4', 'kh-4', NULL, 'DC004', 'Hải Phòng', 'Lê Chân', 'Vĩnh Niệm', 'Lạch Tray', 1, NOW(), 'system', NOW(), 'system'),
       ('dc-5', NULL, 'nv-2', 'DC005', 'Bắc Ninh', 'Tiên Du', 'Lãng Ngâm', 'Thôn Làng', 1, NOW(), 'system', NOW(), 'system');

-- 24. BẢNG PHIẾU GIẢM GIÁ CHI TIẾT
INSERT INTO phieu_giam_gia_chi_tiet (id, id_phieu_giam_gia, id_khach_hang, ma_phieu_giam_gia_chi_tiet, so_luong_dung, ghi_chu, trang_thai, ngay_tao, nguoi_tao, ngay_cap_nhat, nguoi_cap_nhat)
VALUES ('pg-ct-1', 'pg-1', 'kh-1', 'VOU-001', 1, 'Đã dùng 1 lần', 1, NOW(), 'system', NOW(), 'system'),
       ('pg-ct-2', 'pg-2', 'kh-2', 'VOU-002', 0, 'Chưa dùng', 1, NOW(), 'system', NOW(), 'system'),
       ('pg-ct-3', 'pg-5', 'kh-3', 'VOU-003', 2, 'Dùng 2 lần', 1, NOW(), 'system', NOW(), 'system'),
       ('pg-ct-4', 'pg-1', 'kh-4', 'VOU-004', 1, 'Đã dùng', 1, NOW(), 'system', NOW(), 'system'),
       ('pg-ct-5', 'pg-4', 'kh-5', 'VOU-005', 0, 'Quà tặng', 1, NOW(), 'system', NOW(), 'system');

-- 25. BẢNG HÓA ĐƠN
INSERT INTO hoa_don (id, id_nhan_vien, id_phieu_giam_gia, id_khach_hang, ma_hoa_don, tong_tien, so_tien_giam, tong_tien_thanh_toan, phuong_thuc_thanh_toan, kenh_ban_hang, trang_thai, ghi_chu, thoi_gian_het_han, ngay_tao, nguoi_tao, ngay_cap_nhat, nguoi_cap_nhat)
VALUES ('hd-1', 'nv-1', 'pg-1', 'kh-1', 'HD001', 150000.0, 20000.0, 130000.0, 1, 0, 1, 'Thanh toán tiền mặt', NULL, NOW(), 'system', NOW(), 'system'),
       ('hd-2', 'nv-1', NULL, 'kh-2', 'HD002', 80000.0, 0.0, 80000.0, 2, 1, 1, 'Chuyển khoản VNPAY', NULL, NOW(), 'system', NOW(), 'system'),
       ('hd-3', 'nv-2', 'pg-2', 'kh-4', 'HD003', 250000.0, 50000.0, 200000.0, 1, 0, 1, 'Mua tại quầy', NULL, NOW(), 'system', NOW(), 'system'),
       ('hd-4', NULL, NULL, 'kh-3', 'HD004', 90000.0, 0.0, 90000.0, 2, 1, 0, 'Hóa đơn chờ', DATE_ADD(NOW(), INTERVAL 10 MINUTE), NOW(), 'system', NOW(), 'system'),
       ('hd-5', 'nv-1', NULL, 'kh-1', 'HD005', 500000.0, 0.0, 500000.0, 1, 0, 1, 'Khách đoàn', NULL, NOW(), 'system', NOW(), 'system');

-- 26. BẢNG VÉ
INSERT INTO ve (id, id_loai_khach_hang, id_ghe, id_suat_chieu, id_hoa_don, ma_ve, gia_thanh_toan, loai_ve, trang_thai, version, ngay_tao, nguoi_tao, ngay_cap_nhat, nguoi_cap_nhat)
VALUES ('v-1', 'lkh-1', 'g-1', 'sc-1', 'hd-1', 'V001', 45000.0, 0, 1, 0, NOW(), 'system', NOW(), 'system'),
       ('v-2', 'lkh-2', 'g-2', 'sc-1', 'hd-1', 'V002', 45000.0, 0, 1, 0, NOW(), 'system', NOW(), 'system'),
       ('v-3', 'lkh-1', 'g-3', 'sc-2', 'hd-2', 'V003', 80000.0, 0, 1, 0, NOW(), 'system', NOW(), 'system'),
       ('v-4', 'lkh-2', 'g-4', 'sc-3', 'hd-3', 'V004', 110000.0, 0, 1, 0, NOW(), 'system', NOW(), 'system'),
       ('v-5', 'lkh-3', 'g-5', 'sc-4', 'hd-5', 'V005', 120000.0, 0, 1, 0, NOW(), 'system', NOW(), 'system');

-- 27. BẢNG LỊCH SỬ HÓA ĐƠN
INSERT INTO lich_su_hoa_don (id, hoa_don_id, hanh_dong, thoi_gian_hanh_dong, trang_thai, ngay_tao, nguoi_tao, ngay_cap_nhat, nguoi_cap_nhat)
VALUES ('lshd-1', 'hd-1', 'Tạo hóa đơn', NOW(), 1, NOW(), 'system', NOW(), 'system'),
       ('lshd-2', 'hd-1', 'Thanh toán thành công', NOW(), 1, NOW(), 'system', NOW(), 'system'),
       ('lshd-3', 'hd-4', 'Đang chờ xử lý', NOW(), 1, NOW(), 'system', NOW(), 'system'),
       ('lshd-4', 'hd-2', 'Xác nhận online', NOW(), 1, NOW(), 'system', NOW(), 'system'),
       ('lshd-5', 'hd-5', 'Hoàn tất giao dịch', NOW(), 1, NOW(), 'system', NOW(), 'system');

-- 28. BẢNG THANH TOÁN
INSERT INTO thanh_toan (id, id_hoa_don, ma_giao_dich, phuong_thuc_thanh_toan, so_tien, ngay_thanh_toan, noi_dung, trang_thai, ngay_tao, nguoi_tao, ngay_cap_nhat, nguoi_cap_nhat)
VALUES ('tt-1', 'hd-1', 'TX1001', 1, 130000.0, NOW(), 'Thanh toán HD001', 1, NOW(), 'system', NOW(), 'system'),
       ('tt-2', 'hd-2', 'TX1002', 2, 80000.0, NOW(), 'Thanh toán HD002', 1, NOW(), 'system', NOW(), 'system'),
       ('tt-3', 'hd-3', 'TX1003', 1, 200000.0, NOW(), 'Thanh toán HD003', 1, NOW(), 'system', NOW(), 'system'),
       ('tt-4', 'hd-5', 'TX1004', 1, 500000.0, NOW(), 'Thanh toán HD005', 1, NOW(), 'system', NOW(), 'system'),
       ('tt-5', 'hd-3', 'TX1005', 1, 50000.0, NOW(), 'Thanh toán bổ sung', 1, NOW(), 'system', NOW(), 'system');

-- 29. BẢNG LỊCH SỬ THANH TOÁN
INSERT INTO lich_su_thanh_toan (id, id_thanh_toan, trang_thai, thoi_gian, ma_phan_hoi, loai_thao_tac, noi_dung, ngay_tao, nguoi_tao, ngay_cap_nhat, nguoi_cap_nhat)
VALUES ('lstt-1', 'tt-1', 1, NOW(), '200', 'CASH_PAY', 'Thành công', NOW(), 'system', NOW(), 'system'),
       ('lstt-2', 'tt-2', 1, NOW(), '00', 'VNPAY_PAY', 'Thành công VNPAY', NOW(), 'system', NOW(), 'system'),
       ('lstt-3', 'tt-3', 1, NOW(), '200', 'CASH_PAY', 'Thành công', NOW(), 'system', NOW(), 'system'),
       ('lstt-4', 'tt-1', 1, NOW(), '200', 'REFUND', 'Hoàn tiền test', NOW(), 'system', NOW(), 'system'),
       ('lstt-5', 'tt-4', 1, NOW(), '200', 'CASH_PAY', 'Thành công', NOW(), 'system', NOW(), 'system');

-- 30. BẢNG HÓA ĐƠN CHI TIẾT
INSERT INTO hoa_don_chi_tiet (id, id_hoa_don, id_ve, id_chi_tiet_san_pham_di_kem, loai, so_luong, don_gia, thanh_tien, ngay_tao, nguoi_tao, ngay_cap_nhat, nguoi_cap_nhat)
VALUES ('hdct-1', 'hd-1', 'v-1', NULL, 1, 1, 45000.0, 45000.0, NOW(), 'system', NOW(), 'system'),
       ('hdct-2', 'hd-1', NULL, 'ctsp-1', 2, 2, 45000.0, 90000.0, NOW(), 'system', NOW(), 'system'),
       ('hdct-3', 'hd-2', 'v-3', NULL, 1, 1, 80000.0, 80000.0, NOW(), 'system', NOW(), 'system'),
       ('hdct-4', 'hd-3', 'v-4', NULL, 1, 1, 110000.0, 110000.0, NOW(), 'system', NOW(), 'system'),
       ('hdct-5', 'hd-3', NULL, 'ctsp-4', 2, 1, 85000.0, 85000.0, NOW(), 'system', NOW(), 'system');

SET FOREIGN_KEY_CHECKS = 1;
