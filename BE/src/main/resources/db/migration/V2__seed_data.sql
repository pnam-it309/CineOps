-- DATA SEED FOR CINEOPS
-- 0. LOẠI NGÀY
INSERT INTO loai_ngay (id, ten_loai_ngay, he_so_ngay, nguoi_tao) VALUES 
('ln-thuong', 'Ngày thường', 1.00, 'system'),
('ln-cuoituan', 'Cuối tuần', 1.20, 'system'),
('ln-letet', 'Lễ / Tết', 1.50, 'system');

-- 1. LOẠI GHẾ
INSERT INTO loai_ghe (id, ten_loai, phu_phi, nguoi_tao)
VALUES ('lg-normal-uuid', 'Thường', 0, 'system'),
       ('lg-vip-uuid', 'VIP', 20000, 'system'),
       ('lg-couple-uuid', 'Couple', 50000, 'system');

-- 2. PHÒNG CHIẾU
INSERT INTO phong_chieu (id, ten_phong, loai_man_hinh, tong_ghe, nguoi_tao) VALUES 
('pc-001-uuid-001', 'Phòng 01', 'IMAX 3D', 40, 'system'),
('pc-002-uuid-002', 'Phòng 02', '2D Digital', 30, 'system');

-- 3. GHẾ (Seed cho Phòng 01 - Hàng A, B, C)
-- Hàng A (Thường - 10 ghế)
INSERT INTO ghe (id, id_loai_ghe, id_phong_chieu, so_ghe, so_hang, so_cot, trang_thai, nguoi_tao)
VALUES (UUID(), 'lg-normal-uuid', 'pc-001-uuid-001', 'A1', 'A', 1, 1, 'system'),
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
INSERT INTO ghe (id, id_loai_ghe, id_phong_chieu, so_ghe, so_hang, so_cot, trang_thai, nguoi_tao)
VALUES (UUID(), 'lg-vip-uuid', 'pc-001-uuid-001', 'B1', 'B', 1, 1, 'system'),
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
INSERT INTO ghe (id, id_loai_ghe, id_phong_chieu, so_ghe, so_hang, so_cot, trang_thai, nguoi_tao)
VALUES (UUID(), 'lg-couple-uuid', 'pc-001-uuid-001', 'J1', 'J', 1, 1, 'system'),
       (UUID(), 'lg-couple-uuid', 'pc-001-uuid-001', 'J2', 'J', 2, 1, 'system'),
       (UUID(), 'lg-couple-uuid', 'pc-001-uuid-001', 'J3', 'J', 3, 1, 'system'),
       (UUID(), 'lg-couple-uuid', 'pc-001-uuid-001', 'J4', 'J', 4, 1, 'system');

-- 4. KHUNG GIỜ
INSERT INTO khung_gio (id, ten_khung_gio, gio_bat_dau, gio_ket_thuc, nguoi_tao)
VALUES ('kg-01-uuid', 'Sáng sớm', '08:00:00', '10:30:00', 'system'),
       ('kg-02-uuid', 'Trưa', '11:00:00', '13:30:00', 'system'),
       ('kg-03-uuid', 'Chiều', '14:30:00', '17:00:00', 'system'),
       ('kg-04-uuid', 'Tối', '18:00:00', '21:00:00', 'system'),
       ('kg-05-uuid', 'Khuya', '21:30:00', '23:59:00', 'system');

-- 5. PHIM
INSERT INTO phim (id, ten_phim, ma_phim, thoi_luong, ngay_khoi_chieu, ngay_ket_thuc, lich_chieu,
                  mo_ta, trailer, poster, ngon_ngu, do_tuoi,
                  danh_gia, gia_phim, loai_phim, phu_phi_loai_phim, trang_thai, nguoi_tao)
VALUES

-- ===== 15 PHIM ĐANG CHIếu / SắP CHIếu (GIỐNG RẠP)
('P1', 'Thỏ Ơi!!', 'P001', 95, '2025-02-01', '2025-03-30', '2,4,6,0',
 'Phim hoạt hình thiếu nhi vui nhộn',
 'https://www.youtube.com/watch?v=dQw4w9WgXcQ',
 'https://image.tmdb.org/t/p/w500/gRApXuxWmO2forYTuTmcz5RaNUV.jpg',
 'Tiếng Việt', 0, 8.5, 65000, '2D', 0, 1, 'admin'),

('P2', 'Mùi Phở', 'P002', 110, '2025-02-10', '2025-04-01', '1,2,3,4',
 'Phim Việt về văn hóa ẩm thực',
 'https://www.youtube.com/watch?v=dQw4w9WgXcQ',
 'https://image.tmdb.org/t/p/w500/6KErczPBROQty7QoIsaa6wJYXZi.jpg',
 'Tiếng Việt', 13, 8.0, 65000, '2D', 0, 1, 'admin'),

('P3', 'Tài', 'P003', 105, '2025-02-05', '2025-03-25', '2,3,4,5,6',
 'Phim tâm lý xã hội',
 'https://www.youtube.com/watch?v=dQw4w9WgXcQ',
 'https://image.tmdb.org/t/p/w500/qNBAXBIQlnOThrVvA6mA2B5ggV6.jpg',
 'Tiếng Việt', 16, 7.8, 65000, '2D', 0, 1, 'admin'),

('P4', 'Quỷ Nhập Tràng 2', 'P004', 120, '2025-02-15', '2025-04-10', '4,6,0,1,2',
 'Phim kinh dị Việt Nam',
 'https://www.youtube.com/watch?v=dQw4w9WgXcQ',
 'https://image.tmdb.org/t/p/w500/A4j8S6moJS2zNtRR8oWF08gRnL5.jpg',
 'Tiếng Việt', 18, 8.7, 65000, '2D', 0, 1, 'admin'),

-- Phim bảm tấc IMAX: phụ phí +50,000đ
('P5', 'Avatar: Lửa và Tro Tàn', 'P005', 165, '2025-12-20', '2026-02-01', '4,5,6,0,1',
 'Bom tấn khoa học viễn tưởng',
 'https://www.youtube.com/watch?v=5PSNL1qE6VY',
 'https://image.tmdb.org/t/p/w500/t6HIqrRAclMCA60NsSmeqe9RmNV.jpg',
 'Tiếng Anh', 13, 9.2, 65000, 'IMAX', 50000, 2, 'admin'),

('P6', 'Khủng Long Đón Tết', 'P006', 100, '2025-01-20', '2025-03-01', '2,3,4,5,6',
 'Hoạt hình gia đình dịp Tết',
 'https://www.youtube.com/watch?v=dQw4w9WgXcQ',
 'https://upload.wikimedia.org/wikipedia/commons/6/6e/Dinosaur_icon.png',
 'Tiếng Việt', 0, 8.3, 65000, '2D', 0, 1, 'admin'),

('P7', 'Tuyển Thủ Dê', 'P007', 98, '2025-03-01', '2025-04-15', '0,1,2,3',
 'Phim hoạt hình thể thao',
 'https://www.youtube.com/watch?v=dQw4w9WgXcQ',
 'https://image.tmdb.org/t/p/w500/sLjBuNcpyYSaeXpOREBJlIUElEX.jpg',
 'Tiếng Việt', 7, 7.9, 65000, '2D', 0, 2, 'admin'),

('P8', 'Doraemon Movie', 'P008', 105, '2025-02-18', '2025-04-20', '',
 'Phim hoạt hình Nhật Bản',
 'https://www.youtube.com/watch?v=dQw4w9WgXcQ',
 'https://image.tmdb.org/t/p/w500/oT3YgMdKGThPboY6sNTgfHezBQv.jpg',
 'Tiếng Nhật', 0, 8.8, 65000, '2D', 0, 1, 'admin'),

('P9', 'Conan Movie', 'P009', 110, '2025-03-10', '2025-05-01', '',
 'Thám tử lừng danh Conan movie',
'https://www.youtube.com/watch?v=dQw4w9WgXcQ',
 'https://image.tmdb.org/t/p/w500/zN41DPmFcKyDcRUiXMDl8yTYolk.jpg',
 'Tiếng Nhật', 13, 8.9, 65000, '2D', 0, 2, 'admin'),

('P10', 'One Piece Film', 'P010', 120, '2025-04-01', '2025-06-01', '1,3,5,0',
 'Phiêu lưu hải tầc',
 'https://www.youtube.com/watch?v=dQw4w9WgXcQ',
 'https://image.tmdb.org/t/p/w500/cezWGskPYx7GaglTTRN4Fugivo.jpg',
 'Tiếng Nhật', 13, 9.0, 65000, '2D', 0, 2, 'admin'),

-- ===== 10 PHIM HOLLYWOOD
('P11', 'Fast & Furious 10', 'P011', 140, '2024-12-01', '2025-03-01', '4,6',
 'Phim đua xe hành động',
 'https://www.youtube.com/watch?v=32RAq6JzY-w',
 'https://image.tmdb.org/t/p/w500/fiVW06jE7z9F3FaNBaWZxROCPzC.jpg',
 'Tiếng Anh', 16, 8.1, 65000, '2D', 0, 1, 'admin'),

('P12', 'Spider-Man: New World', 'P012', 130, '2025-01-05', '2025-04-01', '1,3',
 'Siêu anh hùng Marvel',
 'https://www.youtube.com/watch?v=JfVOs4VSpmA',
 'https://image.tmdb.org/t/p/w500/1g0dhYtq4irTY1GPXvft6k4YLjm.jpg',
 'Tiếng Anh', 13, 9.1, 65000, '3D', 30000, 1, 'admin'),

('P13', 'Frozen 3', 'P013', 115, '2025-06-01', '2025-08-01', '0,1',
 'Hoạt hình Disney',
 'https://www.youtube.com/watch?v=TbQm5doF_Uc',
 'https://image.tmdb.org/t/p/w500/kgwjIb2JDHRhNk13lmSxiClFjVk.jpg',
 'Tiếng Anh', 0, 8.6, 65000, '2D', 0, 2, 'admin'),

('P14', 'The Nun 3', 'P014', 120, '2025-03-20', '2025-05-15', '2,4,6',
 'Phim kinh dị Hollywood',
 'https://www.youtube.com/watch?v=QF-oyCwaArU',
 'https://image.tmdb.org/t/p/w500/wodVhIn5KbpVojhKeMnzmIfmWiU.jpg',
 'Tiếng Anh', 18, 7.5, 65000, '2D', 0, 2, 'admin'),

('P15', 'Joker 2', 'P015', 135, '2025-10-01', '2025-12-01', '3,5,0',
 'Tâm lý tội phạm',
 'https://www.youtube.com/watch?v=zAGVQLHvwOY',
 'https://image.tmdb.org/t/p/w500/sc0YGSB4TJl3xkqzjSVEJCFWfVH.jpg',
 'Tiếng Anh', 18, 9.3, 65000, '2D', 0, 2, 'admin'),

-- ===== 15 PHIM NGỮNG CHIếu (DATA LỊCH SỬ)
('P16', 'Avengers: Endgame', 'P016', 180, '2019-04-26', '2019-07-01', '',
 'Siêu anh hùng Marvel',
 'https://www.youtube.com/watch?v=TcMBFSGVi1c',
 'https://image.tmdb.org/t/p/w500/or06FN3Dka5tukK1e9sl16pB3iy.jpg',
 'Tiếng Anh', 13, 9.5, 65000, 'IMAX', 50000, 0, 'admin'),

('P17', 'Titanic', 'P017', 195, '1997-12-19', '1998-06-01', '',
 'Phim tình cảm kiền điển',
 'https://www.youtube.com/watch?v=2e-eXJ6HgkQ',
 'https://image.tmdb.org/t/p/w500/9xjZS2rlVxm8SFx8kPC3aIGCOYQ.jpg',
 'Tiếng Anh', 13, 9.0, 65000, '2D', 0, 0, 'admin'),

('P18', 'Interstellar', 'P018', 169, '2014-11-07', '2015-02-01', '',
 'Du hành vũ trụ',
 'https://www.youtube.com/watch?v=zSWdZVtXT7E',
 'https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg',
 'Tiếng Anh', 13, 9.4, 65000, 'IMAX', 50000, 0, 'admin'),

('P19', 'The Lion King', 'P019', 118, '2019-07-19', '2019-10-01', '',
 'Hoạt hình Disney',
'https://www.youtube.com/watch?v=7TavVZMewpY',
 'https://image.tmdb.org/t/p/w500/sCanFr3dvkGOAMq5FzwLmECNAHr.jpg',
 'Tiếng Anh', 0, 8.5, 65000, '2D', 0, 0, 'admin'),

('P20', 'Minions', 'P020', 91, '2015-07-10', '2015-10-01', '',
 'Hoạt hình hài',
 'https://www.youtube.com/watch?v=eisKxhjBnZ0',
 'https://image.tmdb.org/t/p/w500/2LqaLgk4Z226KkgPJuiOQ58ShKD.jpg',
 'Tiếng Anh', 0, 8.2, 65000, '2D', 0, 0, 'admin'),

('P21', 'Transformers', 'P021', 144, '2007-07-03', '2007-10-01', '',
 'Robot hành động',
 'https://www.youtube.com/watch?v=v8ItGrI-Ou0',
 'https://image.tmdb.org/t/p/w500/7cqKGQMnNabzOpi7qaIgZvQ7NGV.jpg',
 'Tiếng Anh', 13, 8.0, 65000, '2D', 0, 0, 'admin'),

('P22', 'Jurassic World', 'P022', 124, '2015-06-12', '2015-09-01', '',
 'Khủng long phiêu lưu',
 'https://www.youtube.com/watch?v=RFinNxS5KN4',
 'https://image.tmdb.org/t/p/w500/dkMD5qlogeRMiEixC4YNPUvax2T.jpg',
 'Tiếng Anh', 13, 8.3, 65000, '2D', 0, 0, 'admin'),

('P23', 'Frozen', 'P023', 102, '2013-11-27', '2014-03-01', '',
 'Công chúa băng giá',
 'https://www.youtube.com/watch?v=TbQm5doF_Uc',
 'https://image.tmdb.org/t/p/w500/kgwjIb2JDHRhNk13lmSxiClFjVk.jpg' ,
 'Tiếng Anh', 0, 8.7, 65000, '2D', 0, 0, 'admin'),

('P24', 'Inception', 'P024', 148, '2010-07-16', '2010-10-01', '',
 'Giấc mơ tầng sâu',
 'https://www.youtube.com/watch?v=YoHD9XEInc0',
 'https://image.tmdb.org/t/p/w500/edv5CZvWj09upOsy2Y6IwDhK8bt.jpg',
 'Tiếng Anh', 13, 9.2, 65000, 'IMAX', 50000, 0, 'admin'),

('P25', 'Doctor Strange', 'P025', 115, '2016-11-04', '2017-02-01', '',
 'Pháp sư tối thượng',
 'https://www.youtube.com/watch?v=HSzx-zryEgM',
 'https://image.tmdb.org/t/p/w500/uGy4DCmgj5G40Mq0FgOUT94b1oA.jpg',
 'Tiếng Anh', 13, 8.6, 65000, '2D', 0, 0, 'admin'),

('P26', 'Aquaman', 'P026', 143, '2018-12-21', '2019-03-01', '',
 'Anh hùng đại dương',
 'https://www.youtube.com/watch?v=WDkg3h8PCVU',
 'https://image.tmdb.org/t/p/w500/2wurSpMJ85WRoX0gJMoG3BKs5oc.jpg',
 'Tiếng Anh', 13, 8.1, 65000, '2D', 0, 0, 'admin'),

('P27', 'Black Panther', 'P027', 134, '2018-02-16', '2018-05-01', '',
 'Vua Wakanda',
 'https://www.youtube.com/watch?v=xjDjIWPwcPU',
 'https://image.tmdb.org/t/p/w500/uxzzxijgPIY7slzFvMotPv8wjKA.jpg',
 'Tiếng Anh', 13, 8.8, 65000, '2D', 0, 0, 'admin'),

('P28', 'Aladdin', 'P028', 128, '2019-05-24', '2019-08-01', '0,1,2,3,4',
 'Phim phiêu lưu kỳ ảo',
 'https://www.youtube.com/watch?v=wmiIUN-7qhE',
 'https://image.tmdb.org/t/p/w500/w9kR8qbmQ01HwnvK4alvnQ2ca0L.jpg',
 'Tiếng Anh', 0, 8.9, 65000, '2D', 0, 0, 'admin'),

('P30', 'Kung Fu Panda', 'P030', 95, '2008-06-06', '2008-09-01', '1,3,5,6',
 'Hoạt hình võ thuật',
 'https://www.youtube.com/watch?v=PXi3Mv6KMzY',
 'https://image.tmdb.org/t/p/w500/wWt5JEXUIhFBMVMDruAzBwlSMhc.jpg',
 'Tiếng Anh', 0, 8.8, 65000, '2D', 0, 0, 'admin');


-- 6. SUẤT CHIẾU
INSERT INTO suat_chieu (id, id_khung_gio, id_phong_chieu, id_phim, ngay_chieu, so_ghe_trong, trang_thai, nguoi_tao) VALUES 
(UUID(), 'kg-02-uuid', 'pc-001-uuid-001', 'P1', CURDATE(), 40, 1, 'system'),
(UUID(), 'kg-04-uuid', 'pc-001-uuid-001', 'P1', CURDATE(), 40, 1, 'system'),
(UUID(), 'kg-03-uuid', 'pc-002-uuid-002', 'P2', CURDATE(), 30, 1, 'system'),
(UUID(), 'kg-05-uuid', 'pc-002-uuid-002', 'P2', CURDATE(), 30, 2, 'system'),
(UUID(), 'kg-01-uuid', 'pc-001-uuid-001', 'P1', DATE_ADD(CURDATE(), INTERVAL 1 DAY), 40, 1, 'system'),
(UUID(), 'kg-02-uuid', 'pc-001-uuid-001', 'P3', DATE_ADD(CURDATE(), INTERVAL 1 DAY), 40, 1, 'system');

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
INSERT INTO phieu_giam_gia (id, ma_phieu_giam_gia, ten_phieu, loai_phieu, phan_tram_giam_gia, so_tien_giam, gia_tri_hoa_don_toi_thieu, co_cho_cong_don, giam_toi_da, ngay_bat_dau, ngay_ket_thuc, trang_thai, so_luong, dieu_kien_ap_dung, ghi_chu, nguoi_tao) VALUES 
('pgg-01-uuid', 'PGG001', 'Chào mừng thành viên mới', 1, 10.00, 0, 100000, 0, 50000, '2024-01-01 00:00:00', '2026-12-31 23:59:59', 1, 100, 'Áp dụng cho KH mới', 'Voucher dành cho khách hàng lần đầu thực hiện giao dịch', 'system'),
('pgg-02-uuid', 'PGG002', 'Tri ân khách hàng VIP', 2, 0, 50000, 200000, 1, 50000, '2024-01-01 00:00:00', '2026-12-31 23:59:59', 1, 50, 'Áp dụng cho hạng VIP trở lên', 'Ưu đãi đặc biệt tri ân các khách hành thân thiết', 'system');

-- 10. PHIẾU GIẢM GIÁ CHI TIẾT
INSERT INTO phieu_giam_gia_chi_tiet (id, id_phieu_giam_gia, id_khach_hang, ma_phieu_giam_gia_chi_tiet, so_luong_dung, trang_thai, nguoi_tao) VALUES 
(UUID(), 'pgg-01-uuid', 'kh-001-uuid', 'PGGCT-001', 0, 1, 'system'),
(UUID(), 'pgg-02-uuid', 'kh-002-uuid', 'PGGCT-002', 0, 1, 'system');

-- 11. THỂ LOẠI
INSERT INTO the_loai (id, ten_the_loai, nguoi_tao)
VALUES ('TL1', 'Hành động', 'admin'),
       ('TL2', 'Hoạt hình', 'admin'),
       ('TL3', 'Kinh dị', 'admin'),
       ('TL4', 'Tình cảm', 'admin'),
       ('TL5', 'Hài', 'admin'),
       ('TL6', 'Phiêu lưu', 'admin'),
       ('TL7', 'Gia đình', 'admin'),
       ('TL8', 'Giả tưởng', 'admin'),
       ('TL9', 'Tâm lý', 'admin'),
       ('TL10', 'Khoa học viễn tưởng', 'admin');

-- 12. PHIM THỂ LOẠI

INSERT INTO phim_the_loai (id, id_phim, id_the_loai) VALUES

-- P1 - Thỏ Ơi!! → Hoạt hình, Hài, Gia đình
('PTL001', 'P1', 'TL2'),
('PTL002', 'P1', 'TL5'),
('PTL003', 'P1', 'TL7'),

-- P2 - Mùi Phở → Hài, Gia đình, Tâm lý
('PTL004', 'P2', 'TL5'),
('PTL005', 'P2', 'TL7'),
('PTL006', 'P2', 'TL9'),

-- P3 - Tài → Tâm lý, Tình cảm
('PTL007', 'P3', 'TL9'),
('PTL008', 'P3', 'TL4'),

-- P4 - Quỷ Nhập Tràng 2 → Kinh dị
('PTL009', 'P4', 'TL3'),

-- P5 - Avatar: Lửa và Tro Tàn → Hành động, Khoa học viễn tưởng, Phiêu lưu
('PTL010', 'P5', 'TL1'),
('PTL011', 'P5', 'TL10'),
('PTL012', 'P5', 'TL6'),

-- P6 - Khủng Long Đón Tết → Hoạt hình, Gia đình, Hài
('PTL013', 'P6', 'TL2'),
('PTL014', 'P6', 'TL7'),
('PTL015', 'P6', 'TL5'),

-- P7 - Tuyển Thủ Dê → Hoạt hình, Gia đình
('PTL016', 'P7', 'TL2'),
('PTL017', 'P7', 'TL7'),

-- P8 - Doraemon Movie → Hoạt hình, Phiêu lưu, Gia đình
('PTL018', 'P8', 'TL2'),
('PTL019', 'P8', 'TL6'),
('PTL020', 'P8', 'TL7'),

-- P9 - Conan Movie → Hành động, Phiêu lưu
('PTL021', 'P9', 'TL1'),
('PTL022', 'P9', 'TL6'),

-- P10 - One Piece Film → Hành động, Phiêu lưu, Giả tưởng
('PTL023', 'P10', 'TL1'),
('PTL024', 'P10', 'TL6'),
('PTL025', 'P10', 'TL8'),

-- P11 - Fast & Furious 10 → Hành động
('PTL026', 'P11', 'TL1'),

-- P12 - Spider-Man: New World → Hành động, Giả tưởng, Phiêu lưu
('PTL027', 'P12', 'TL1'),
('PTL028', 'P12', 'TL8'),
('PTL029', 'P12', 'TL6'),

-- P13 - Frozen 3 → Hoạt hình, Gia đình, Giả tưởng
('PTL030', 'P13', 'TL2'),
('PTL031', 'P13', 'TL7'),
('PTL032', 'P13', 'TL8'),

-- P14 - The Nun 3 → Kinh dị
('PTL033', 'P14', 'TL3'),

-- P15 - Joker 2 → Tâm lý, Hành động
('PTL034', 'P15', 'TL9'),
('PTL035', 'P15', 'TL1'),

-- P16 - Avengers: Endgame → Hành động, Giả tưởng, Khoa học viễn tưởng
('PTL036', 'P16', 'TL1'),
('PTL037', 'P16', 'TL8'),
('PTL038', 'P16', 'TL10'),

-- P17 - Titanic → Tình cảm
('PTL039', 'P17', 'TL4'),

-- P18 - Interstellar → Khoa học viễn tưởng, Tâm lý, Phiêu lưu
('PTL040', 'P18', 'TL10'),
('PTL041', 'P18', 'TL9'),
('PTL042', 'P18', 'TL6'),

-- P19 - The Lion King → Hoạt hình, Gia đình
('PTL043', 'P19', 'TL2'),
('PTL044', 'P19', 'TL7'),

-- P20 - Minions → Hoạt hình, Hài, Gia đình
('PTL045', 'P20', 'TL2'),
('PTL046', 'P20', 'TL5'),
('PTL047', 'P20', 'TL7'),

-- P21 - Transformers → Hành động, Khoa học viễn tưởng
('PTL048', 'P21', 'TL1'),
('PTL049', 'P21', 'TL10'),

-- P22 - Jurassic World → Hành động, Phiêu lưu, Khoa học viễn tưởng
('PTL050', 'P22', 'TL1'),
('PTL051', 'P22', 'TL6'),
('PTL052', 'P22', 'TL10'),

-- P23 - Frozen → Hoạt hình, Gia đình, Giả tưởng
('PTL053', 'P23', 'TL2'),
('PTL054', 'P23', 'TL7'),
('PTL055', 'P23', 'TL8'),

-- P24 - Inception → Hành động, Khoa học viễn tưởng, Tâm lý
('PTL056', 'P24', 'TL1'),
('PTL057', 'P24', 'TL10'),
('PTL058', 'P24', 'TL9'),

-- P25 - Doctor Strange → Hành động, Giả tưởng
('PTL059', 'P25', 'TL1'),
('PTL060', 'P25', 'TL8'),

-- P26 - Aquaman → Hành động, Phiêu lưu, Giả tưởng
('PTL061', 'P26', 'TL1'),
('PTL062', 'P26', 'TL6'),
('PTL063', 'P26', 'TL8'),

-- P27 - Black Panther → Hành động, Giả tưởng
('PTL064', 'P27', 'TL1'),
('PTL065', 'P27', 'TL8'),

-- P28 - Aladdin → Hoạt hình, Phiêu lưu, Gia đình, Giả tưởng
('PTL066', 'P28', 'TL2'),
('PTL067', 'P28', 'TL6'),
('PTL068', 'P28', 'TL7'),
('PTL069', 'P28', 'TL8'),

-- P30 - Kung Fu Panda → Hoạt hình, Hành động, Hài, Gia đình
('PTL073', 'P30', 'TL2'),
('PTL074', 'P30', 'TL1'),
('PTL075', 'P30', 'TL5'),
('PTL076', 'P30', 'TL7');

-- 13. LOẠI SẢN PHẨM ĐI KÈM
INSERT INTO loai_san_pham_di_kem (id, ten_loai, nguoi_tao) VALUES 
('lsp-popcorn', 'Bắp', 'system'),
('lsp-drink', 'Nước', 'system');

-- 14. KÍCH CỠ
INSERT INTO kich_co (id, ten_kich_co, mo_ta) VALUES 
('kc-s', 'S', 'Nhỏ'),
('kc-m', 'M', 'Vừa'),
('kc-l', 'L', 'Lớn'),
('kc-xl', 'XL', 'Rất lớn');

-- 15. ĐƠN VỊ TÍNH
INSERT INTO don_vi_tinh (id, ten_don_vi_tinh, mo_ta) VALUES 
('dvt-g', 'g', 'Gram'),
('dvt-ml', 'ml', 'Milliliter'),
('dvt-ly', 'Ly', 'Ly'),
('dvt-hop', 'Hộp', 'Hộp');

-- 16. SẢN PHẨM ĐI KÈM
INSERT INTO san_pham_di_kem (id, id_loai_san_pham, ten_san_pham, mo_ta, hinh_anh, trang_thai, nguoi_tao) VALUES
('sp-popcorn-cheese', 'lsp-popcorn', 'Bắp Phô Mai', 'Bắp rang bơ vị phô mai thơm ngon, giòn tan', 'https://images.unsplash.com/photo-1585735024088-0c4e0e5b3b3e?w=400', 1, 'system'),
('sp-popcorn-caramel', 'lsp-popcorn', 'Bắp Caramel', 'Bắp rang bơ caramel ngọt ngào, hấp dẫn', 'https://images.unsplash.com/photo-1578849278619-e73505e9610f?w=400', 1, 'system'),
('sp-popcorn-butter', 'lsp-popcorn', 'Bắp Bơ Truyền Thống', 'Bắp rang bơ truyền thống, vị cổ điển', 'https://images.unsplash.com/photo-1612404730960-5c0b4d0e9fb0?w=400', 1, 'system'),
('sp-coca', 'lsp-drink', 'Coca Cola', 'Nước ngọt có ga Coca Cola mát lạnh', 'https://images.unsplash.com/photo-1554866585-cd94860890b7?w=400', 1, 'system'),
('sp-pepsi', 'lsp-drink', 'Pepsi', 'Nước ngọt Pepsi sảng khoái', 'https://images.unsplash.com/photo-1629203851122-3726ecdf080e?w=400', 1, 'system'),
('sp-tra-dao', 'lsp-drink', 'Trà Đào Cam Sả', 'Trà đào cam sả thanh mát, giải khát', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400', 1, 'system'),
('sp-nuoc-suoi', 'lsp-drink', 'Nước Suối', 'Nước suối tinh khiết Aquafina', 'https://images.unsplash.com/photo-1560023907-5f339617ea55?w=400', 1, 'system'),
('sp-popcorn-spicy', 'lsp-popcorn', 'Bắp Cay', 'Bắp rang bơ vị cay nồng, kích thích vị giác', 'https://images.unsplash.com/photo-1505686994434-e3cc5abf1330?w=400', 1, 'system');

-- 17. CHI TIẾT SẢN PHẨM ĐI KÈM (Biến thể theo Size)
INSERT INTO chi_tiet_san_pham_di_kem (id, id_san_pham, id_kich_co, id_don_vi_tinh, gia_tri_dinh_luong, huong_vi, gia_ban, so_luong_ton, nguoi_tao) VALUES
-- Bắp Phô Mai: S/M/L
('ct-pc-s', 'sp-popcorn-cheese', 'kc-s', 'dvt-g', 50, 'Phô Mai', 35000, 100, 'system'),
('ct-pc-m', 'sp-popcorn-cheese', 'kc-m', 'dvt-g', 80, 'Phô Mai', 49000, 80, 'system'),
('ct-pc-l', 'sp-popcorn-cheese', 'kc-l', 'dvt-g', 120, 'Phô Mai', 65000, 60, 'system'),

-- Bắp Caramel: S/M/L
('ct-pcr-s', 'sp-popcorn-caramel', 'kc-s', 'dvt-g', 50, 'Caramel', 35000, 90, 'system'),
('ct-pcr-m', 'sp-popcorn-caramel', 'kc-m', 'dvt-g', 80, 'Caramel', 49000, 70, 'system'),
('ct-pcr-l', 'sp-popcorn-caramel', 'kc-l', 'dvt-g', 120, 'Caramel', 65000, 50, 'system'),

-- Bắp Bơ Truyền Thống: S/M/L
('ct-pb-s', 'sp-popcorn-butter', 'kc-s', 'dvt-g', 50, 'Bơ', 30000, 120, 'system'),
('ct-pb-m', 'sp-popcorn-butter', 'kc-m', 'dvt-g', 80, 'Bơ', 42000, 100, 'system'),
('ct-pb-l', 'sp-popcorn-butter', 'kc-l', 'dvt-g', 120, 'Bơ', 55000, 80, 'system'),

-- Bắp Cay: M/L
('ct-ps-m', 'sp-popcorn-spicy', 'kc-m', 'dvt-g', 80, 'Cay', 49000, 60, 'system'),
('ct-ps-l', 'sp-popcorn-spicy', 'kc-l', 'dvt-g', 120, 'Cay', 65000, 40, 'system'),

-- Coca Cola: S/M/L
('ct-cc-s', 'sp-coca', 'kc-s', 'dvt-ml', 300, NULL, 25000, 200, 'system'),
('ct-cc-m', 'sp-coca', 'kc-m', 'dvt-ml', 500, NULL, 35000, 150, 'system'),
('ct-cc-l', 'sp-coca', 'kc-l', 'dvt-ml', 700, NULL, 45000, 100, 'system'),

-- Pepsi: S/M/L
('ct-pp-s', 'sp-pepsi', 'kc-s', 'dvt-ml', 300, NULL, 25000, 180, 'system'),
('ct-pp-m', 'sp-pepsi', 'kc-m', 'dvt-ml', 500, NULL, 35000, 130, 'system'),
('ct-pp-l', 'sp-pepsi', 'kc-l', 'dvt-ml', 700, NULL, 45000, 90, 'system'),

-- Trà Đào Cam Sả: M/L
('ct-td-m', 'sp-tra-dao', 'kc-m', 'dvt-ml', 500, 'Đào Cam Sả', 39000, 80, 'system'),
('ct-td-l', 'sp-tra-dao', 'kc-l', 'dvt-ml', 700, 'Đào Cam Sả', 49000, 60, 'system'),

-- Nước Suối: S
('ct-ns-s', 'sp-nuoc-suoi', 'kc-s', 'dvt-ml', 500, NULL, 15000, 300, 'system');
-- =================================================================
-- 18. BẢNG PHÂN QUYỀN (Bắt buộc để đăng nhập)
-- =================================================================
INSERT INTO phan_quyen (id, ma_phan_quyen, ten_vai_tro, quyen_han, nguoi_tao) VALUES
                                                                                  ('pq-admin', 'ROLE_ADMIN', 'Quản trị viên', 'ALL', 'system'),
                                                                                  ('pq-staff', 'ROLE_STAFF', 'Nhân viên bán vé', 'POS,CHECKIN', 'system');

-- =================================================================
-- 19. BẢNG NHÂN VIÊN (Để lưu người tạo Hóa đơn)
-- =================================================================
INSERT INTO nhan_vien (id, id_phan_quyen, ma_nhan_vien, ten_nhan_vien, chuc_vu, email, mat_khau, so_dien_thoai, nguoi_tao) VALUES
                                                                                                                               ('nv-admin', 'pq-admin', 'NV001', 'Nguyễn Huy Đức', 'Quản lý', 'admin@cineops.com', '$2a$12$7k...', '0988888888', 'system'),
                                                                                                                               ('nv-staff', 'pq-staff', 'NV002', 'Lê Thị Nhân Viên', 'Bán vé', 'staff@cineops.com', '$2a$12$7k...', '0911111111', 'system');

-- =================================================================
-- 20. BẢNG GIÁ VÉ CHI TIẾT (Bắt buộc để hàm Tính Giá Vé không bị lỗi Null)
-- Map chuẩn với các ID loại ngày, khách, ghế, khung giờ trong script của Đức
-- =================================================================
INSERT INTO gia_ve_chi_tiet (id, id_loai_ngay, id_loai_khach_hang, id_loai_ghe, id_khung_gio, gia_tien, nguoi_tao) VALUES
                                                                                                                       ('gvct-1', 'ln-thuong', 'lkh-member-uuid', 'lg-normal-uuid', 'kg-02-uuid', 0, 'system'),
                                                                                                                       ('gvct-2', 'ln-thuong', 'lkh-member-uuid', 'lg-vip-uuid', 'kg-02-uuid', 20000, 'system'),
                                                                                                                       ('gvct-3', 'ln-cuoituan', 'lkh-member-uuid', 'lg-normal-uuid', 'kg-04-uuid', 10000, 'system'),
                                                                                                                       ('gvct-4', 'ln-cuoituan', 'lkh-member-uuid', 'lg-vip-uuid', 'kg-04-uuid', 30000, 'system');

-- =================================================================
-- 21. TẠO 1 SUẤT CHIẾU CÓ ID CỐ ĐỊNH (Dùng làm mồi cho Hóa Đơn)
-- =================================================================
INSERT INTO suat_chieu (id, id_khung_gio, id_phong_chieu, id_phim, ngay_chieu, so_ghe_trong, trang_thai, nguoi_tao) VALUES
    ('sc-fix-001', 'kg-04-uuid', 'pc-001-uuid-001', 'P1', CURDATE(), 40, 1, 'system');

-- =================================================================
-- 22. TẠO HÓA ĐƠN & GIAO DỊCH MẪU (Để xem được trên màn hình Vue 3)
-- =================================================================
-- a. Tạo 1 Vé (Đã thanh toán, version=0)
INSERT INTO ve (id, id_loai_khach_hang, id_ghe, id_suat_chieu, ma_ve, gia_thanh_toan, loai_ve, trang_thai, version) VALUES
                                                                                                               ('ve-001', 'lkh-member-uuid', (SELECT id FROM ghe WHERE so_ghe = 'A1' AND id_phong_chieu = 'pc-001-uuid-001' LIMIT 1), 'sc-fix-001', 'VE-001', 65000, 0, 1, 0);

-- b. Tạo Hóa Đơn đã thanh toán (trang_thai=1, thoi_gian_het_han=NULL vì đã xong)
INSERT INTO hoa_don (id, id_nhan_vien, id_khach_hang, ma_hoa_don, tong_tien, so_tien_giam, tong_tien_thanh_toan, phuong_thuc_thanh_toan, trang_thai, thoi_gian_het_han) VALUES
    ('hd-001', 'nv-staff', 'kh-001-uuid', 'HD-1700000000', 114000, 0, 114000, 1, 1, NULL);

-- c. Tạo Chi tiết Hóa Đơn (1 dòng vé phim, 1 dòng bắp)
INSERT INTO hoa_don_chi_tiet (id, id_hoa_don, id_ve, loai, so_luong, don_gia, thanh_tien) VALUES
    ('hdct-ve1', 'hd-001', 've-001', 0, 1, 65000, 65000);

-- Map với id 'ct-pc-m' (Bắp Phô Mai size M) trong script của Đức
INSERT INTO hoa_don_chi_tiet (id, id_hoa_don, id_chi_tiet_san_pham_di_kem, loai, so_luong, don_gia, thanh_tien) VALUES
    ('hdct-sp1', 'hd-001', 'ct-pc-m', 1, 1, 49000, 49000);

-- d. Ghi nhận Thanh toán và Lịch sử
INSERT INTO thanh_toan (id, id_hoa_don, ma_giao_dich, phuong_thuc_thanh_toan, so_tien, trang_thai) VALUES
    ('tt-001', 'hd-001', 'VNPay-12345', 1, 114000, 1);

INSERT INTO lich_su_hoa_don (id, hoa_don_id, hanh_dong, trang_thai) VALUES
    ('lshd-001', 'hd-001', 'Tạo hóa đơn tại quầy', 1);