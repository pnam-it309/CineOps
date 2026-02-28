-- DATA SEED FOR CINEOPS
-- 1. LOẠI GHẾ
INSERT INTO loai_ghe (id, ten_loai, phu_phi, nguoi_tao) VALUES 
('lg-normal-uuid', 'Thường', 0, 'system'),
('lg-vip-uuid', 'VIP', 20000, 'system'),
('lg-couple-uuid', 'Couple', 50000, 'system');

-- 2. PHÒNG CHIẾU
INSERT INTO phong_chieu (id, ten_phong, loai_man_hinh, tong_ghe, nguoi_tao) VALUES 
('pc-001-uuid-001', 'Phòng 01', 'IMAX 3D', 40, 'system'),
('pc-002-uuid-002', 'Phòng 02', '2D Digital', 30, 'system');

-- 3. GHẾ (Seed cho Phòng 01 - Hàng A, B, C)
-- Hàng A (Thường - 10 ghế)
INSERT INTO ghe (id, id_loai_ghe, id_phong_chieu, so_ghe, so_hang, so_cot, trang_thai, nguoi_tao) VALUES 
(UUID(), 'lg-normal-uuid', 'pc-001-uuid-001', 'A1', 'A', 1, 1, 'system'),
(UUID(), 'lg-normal-uuid', 'pc-001-uuid-001', 'A2', 'A', 2, 1, 'system'),
(UUID(), 'lg-normal-uuid', 'pc-001-uuid-001', 'A3', 'A', 3, 1, 'system'),
(UUID(), 'lg-normal-uuid', 'pc-001-uuid-001', 'A4', 'A', 4, 1, 'system'),
(UUID(), 'lg-normal-uuid', 'pc-001-uuid-001', 'A5', 'A', 5, 1, 'system'),
(UUID(), 'lg-normal-uuid', 'pc-001-uuid-001', 'A6', 'A', 6, 1, 'system'),
(UUID(), 'lg-normal-uuid', 'pc-001-uuid-001', 'A7', 'A', 7, 1, 'system'),
(UUID(), 'lg-normal-uuid', 'pc-001-uuid-001', 'A8', 'A', 8, 1, 'system'),
(UUID(), 'lg-normal-uuid', 'pc-001-uuid-001', 'A9', 'A', 9, 1, 'system'),
(UUID(), 'lg-normal-uuid', 'pc-001-uuid-001', 'A10', 'A', 10, 1, 'system');

-- Hàng B (VIP - 10 ghế)
INSERT INTO ghe (id, id_loai_ghe, id_phong_chieu, so_ghe, so_hang, so_cot, trang_thai, nguoi_tao) VALUES 
(UUID(), 'lg-vip-uuid', 'pc-001-uuid-001', 'B1', 'B', 1, 1, 'system'),
(UUID(), 'lg-vip-uuid', 'pc-001-uuid-001', 'B2', 'B', 2, 1, 'system'),
(UUID(), 'lg-vip-uuid', 'pc-001-uuid-001', 'B3', 'B', 3, 1, 'system'),
(UUID(), 'lg-vip-uuid', 'pc-001-uuid-001', 'B4', 'B', 4, 1, 'system'),
(UUID(), 'lg-vip-uuid', 'pc-001-uuid-001', 'B5', 'B', 5, 1, 'system'),
(UUID(), 'lg-vip-uuid', 'pc-001-uuid-001', 'B6', 'B', 6, 1, 'system'),
(UUID(), 'lg-vip-uuid', 'pc-001-uuid-001', 'B7', 'B', 7, 1, 'system'),
(UUID(), 'lg-vip-uuid', 'pc-001-uuid-001', 'B8', 'B', 8, 1, 'system'),
(UUID(), 'lg-vip-uuid', 'pc-001-uuid-001', 'B9', 'B', 9, 1, 'system'),
(UUID(), 'lg-vip-uuid', 'pc-001-uuid-001', 'B10', 'B', 10, 1, 'system');

-- Hàng J (Couple - 4 ghế)
INSERT INTO ghe (id, id_loai_ghe, id_phong_chieu, so_ghe, so_hang, so_cot, trang_thai, nguoi_tao) VALUES 
(UUID(), 'lg-couple-uuid', 'pc-001-uuid-001', 'J1', 'J', 1, 1, 'system'),
(UUID(), 'lg-couple-uuid', 'pc-001-uuid-001', 'J2', 'J', 2, 1, 'system'),
(UUID(), 'lg-couple-uuid', 'pc-001-uuid-001', 'J3', 'J', 3, 1, 'system'),
(UUID(), 'lg-couple-uuid', 'pc-001-uuid-001', 'J4', 'J', 4, 1, 'system');

-- 4. KHUNG GIỜ
INSERT INTO khung_gio (id, ten_khung_gio, gio_bat_dau, gio_ket_thuc, nguoi_tao) VALUES 
('kg-01-uuid', 'Sáng sớm', '08:00:00', '10:30:00', 'system'),
('kg-02-uuid', 'Trưa', '11:00:00', '13:30:00', 'system'),
('kg-03-uuid', 'Chiều', '14:30:00', '17:00:00', 'system'),
('kg-04-uuid', 'Tối', '18:00:00', '21:00:00', 'system'),
('kg-05-uuid', 'Khuya', '21:30:00', '23:59:00', 'system');

-- 5. PHIM
INSERT INTO phim (id, ten_phim, thoi_luong, ngay_khoi_chieu, mo_ta, poster, nguoi_tao) VALUES 
('phim-01-uuid', 'Avatar: Dòng Chảy Của Nước', 192, '2022-12-16', 'Sau hành trình hơn một thập kỷ, phần tiếp theo của siêu phẩm Avatar...', 'https://image.tmdb.org/t/p/w500/t6Sna4vF9u9ScmXvzXvYnAsY6S2.jpg', 'system'),
('phim-02-uuid', 'Kẻ Hủy Diệt: Vận Mệnh', 128, '2023-11-01', 'Một cuộc chiến mới nổ ra giữa nhân loại và máy móc...', 'https://image.tmdb.org/t/p/w500/86YpS69WpXSpK7v9LpIdXkU7hLz.jpg', 'system'),
('phim-03-uuid', 'Mắt Biếc', 117, '2019-12-20', 'Chuyện tình dang dở của Ngạn và Hà Lan...', 'https://image.tmdb.org/t/p/w500/t6Sna4vF9u9ScmXvzXvYnAsY6S2.jpg', 'system');

-- 6. SUẤT CHIẾU
INSERT INTO suat_chieu (id, id_khung_gio, id_phong_chieu, id_phim, ngay_chieu, so_ghe_trong, trang_thai, nguoi_tao) VALUES 
(UUID(), 'kg-02-uuid', 'pc-001-uuid-001', 'phim-01-uuid', CURDATE(), 40, 1, 'system'),
(UUID(), 'kg-04-uuid', 'pc-001-uuid-001', 'phim-01-uuid', CURDATE(), 40, 1, 'system'),
(UUID(), 'kg-03-uuid', 'pc-002-uuid-002', 'phim-02-uuid', CURDATE(), 30, 1, 'system'),
(UUID(), 'kg-05-uuid', 'pc-002-uuid-002', 'phim-02-uuid', CURDATE(), 30, 2, 'system'),
(UUID(), 'kg-01-uuid', 'pc-001-uuid-001', 'phim-01-uuid', DATE_ADD(CURDATE(), INTERVAL 1 DAY), 40, 1, 'system'),
(UUID(), 'kg-02-uuid', 'pc-001-uuid-001', 'phim-03-uuid', DATE_ADD(CURDATE(), INTERVAL 1 DAY), 40, 1, 'system');

-- 7. LOẠI KHÁCH HÀNG
INSERT INTO loai_khach_hang (id, ten_loai, he_so_giam_gia, mo_ta, nguoi_tao) VALUES 
('lkh-member-uuid', 'Thành viên', 0, 'Khách hàng mới đăng ký', 'system'),
('lkh-vip-uuid', 'VIP', 5, 'Khách hàng thân thiết', 'system'),
('lkh-diamond-uuid', 'Diamond', 10, 'Khách hàng hạng kim cương', 'system');

-- 8. KHÁCH HÀNG
INSERT INTO khach_hang (id, id_loai_khach_hang, ma_khach_hang, ten_khach_hang, email, sdt, gioi_tinh, trang_thai, nguoi_tao) VALUES 
('kh-001-uuid', 'lkh-member-uuid', 'KH001', 'Nguyễn Văn A', 'anv@example.com', '0987654321', 1, 1, 'system'),
('kh-002-uuid', 'lkh-vip-uuid', 'KH002', 'Trần Thị B', 'btt@example.com', '0123456789', 0, 1, 'system');

-- 9. PHIẾU GIẢM GIÁ
INSERT INTO phieu_giam_gia (id, ten_phieu, loai_phieu, phan_tram_giam_gia, so_tien_giam, gia_tri_hoa_don_toi_thieu, co_cho_cong_don, giam_toi_da, ngay_bat_dau, ngay_ket_thuc, trang_thai, ghi_chu, nguoi_tao) VALUES 
('pgg-01-uuid', 'Chào mừng thành viên mới', 1, 10.00, 0, 100000, 0, 50000, '2024-01-01 00:00:00', '2026-12-31 23:59:59', 1, 'Giảm 10% cho hóa đơn từ 100k', 'system'),
('pgg-02-uuid', 'Tri ân khách hàng VIP', 2, 0, 50000, 200000, 1, 50000, '2024-01-01 00:00:00', '2026-12-31 23:59:59', 1, 'Giảm trực tiếp 50k cho khách VIP', 'system');

-- 10. PHIẾU GIẢM GIÁ CHI TIẾT
INSERT INTO phieu_giam_gia_chi_tiet (id, id_phieu_giam_gia, id_khach_hang, ma_phieu_giam_gia_chi_tiet, so_luong_dung, trang_thai, nguoi_tao) VALUES 
(UUID(), 'pgg-01-uuid', 'kh-001-uuid', 'PGGCT-001', 0, 1, 'system'),
(UUID(), 'pgg-02-uuid', 'kh-002-uuid', 'PGGCT-002', 0, 1, 'system');
