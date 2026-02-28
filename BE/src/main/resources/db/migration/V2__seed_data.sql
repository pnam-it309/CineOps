-- DATA SEED FOR CINEOPS
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
INSERT INTO phim (id, ten_phim, thoi_luong, ngay_khoi_chieu, ngay_ket_thuc, lich_chieu,
                  dao_dien, dien_vien, mo_ta, trailer, poster, ngon_ngu, do_tuoi,
                  danh_gia, gia_ve_goc, trang_thai, nguoi_tao)
VALUES

-- ===== 15 PHIM ĐANG CHIẾU / SẮP CHIẾU (GIỐNG RẠP)
('P1', 'Thỏ Ơi!!', 95, '2025-02-01', '2025-03-30', '2,4,6,0',
 'Trấn Thành', 'Trấn Thành, Ly Ly, Pháo, Văn Mai Hương, Quốc Anh', 'Phim hoạt hình thiếu nhi vui nhộn',
 'https://www.youtube.com/watch?v=dQw4w9WgXcQ',
 'https://upload.wikimedia.org/wikipedia/commons/3/3a/Cute_bunny_cartoon.png',
 'Tiếng Việt', 0, 8.5, 65000, 1, 'admin'),

('P2', 'Mùi Phở', 110, '2025-02-10', '2025-04-01', '1,2,3,4',
 'Minh Beta', 'Xuân Hinh, Thu Trang, Thanh Thanh Hiền, Quốc Tuấn', 'Phim Việt về văn hóa ẩm thực',
 'https://www.youtube.com/watch?v=dQw4w9WgXcQ',
 'https://upload.wikimedia.org/wikipedia/commons/6/60/Pho_Bo.jpg',
 'Tiếng Việt', 13, 8.0, 65000, 1, 'admin'),

('P3', 'Tài', 105, '2025-02-05', '2025-03-25', '2,3,4,5,6',
 'Mỹ Tâm', 'Mỹ Tâm, Vinh Râu', 'Phim tâm lý xã hội',
 'https://www.youtube.com/watch?v=dQw4w9WgXcQ',
 'https://upload.wikimedia.org/wikipedia/commons/8/89/Film_reel_icon.png',
 'Tiếng Việt', 16, 7.8, 65000, 1, 'admin'),

('P4', 'Quỷ Nhập Tràng 2', 120, '2025-02-15', '2025-04-10', '4,6,0,1,2',
 'Đang cập nhật', 'Khả Như, Vân Dung', 'Phim kinh dị Việt Nam',
 'https://www.youtube.com/watch?v=dQw4w9WgXcQ',
 'https://upload.wikimedia.org/wikipedia/commons/1/17/Horror_film_clapperboard.png',
 'Tiếng Việt', 18, 8.7, 65000, 1, 'admin'),

('P5', 'Avatar: Lửa và Tro Tàn', 165, '2025-12-20', '2026-02-01', '4,5,6,0,1',
 'James Cameron', 'Sam Worthington, Zoe Saldana', 'Bom tấn khoa học viễn tưởng',
 'https://www.youtube.com/watch?v=5PSNL1qE6VY',
 'https://upload.wikimedia.org/wikipedia/en/d/d6/Avatar_The_Way_of_Water_poster.jpg',
 'Tiếng Anh', 13, 9.2, 65000, 2, 'admin'),

('P6', 'Khủng Long Đón Tết', 100, '2025-01-20', '2025-03-01', '2,3,4,5,6',
 'Pixar VN', 'Lồng tiếng Việt', 'Hoạt hình gia đình dịp Tết',
 'https://www.youtube.com/watch?v=dQw4w9WgXcQ',
 'https://upload.wikimedia.org/wikipedia/commons/6/6e/Dinosaur_icon.png',
 'Tiếng Việt', 0, 8.3, 65000, 1, 'admin'),

('P7', 'Tuyển Thủ Dê', 98, '2025-03-01', '2025-04-15', '0,1,2,3',
 'Gabrielle Unio', 'Diễn viên lồng tiếng', 'Phim hoạt hình thể thao',
 'https://www.youtube.com/watch?v=dQw4w9WgXcQ',
 'https://upload.wikimedia.org/wikipedia/commons/0/0b/Soccer_ball.svg',
 'Tiếng Việt', 7, 7.9, 65000, 2, 'admin'),

('P8', 'Doraemon Movie', 105, '2025-02-18', '2025-04-20', '',
 'Fujiko F. Fujio', 'Doraemon, Nobita', 'Phim hoạt hình Nhật Bản',
 'https://www.youtube.com/watch?v=dQw4w9WgXcQ',
 'https://upload.wikimedia.org/wikipedia/en/0/0d/Doraemon_character.png',
 'Tiếng Nhật', 0, 8.8, 65000, 1, 'admin'),

('P9', 'Conan Movie', 110, '2025-03-10', '2025-05-01', '',
 'Aoyama Gosho', 'Conan, Ran', 'Thám tử lừng danh Conan movie',
 'https://www.youtube.com/watch?v=dQw4w9WgXcQ',
 'https://upload.wikimedia.org/wikipedia/en/1/1b/Detective_Conan_logo.png',
 'Tiếng Nhật', 13, 8.9, 65000, 2, 'admin'),

('P10', 'One Piece Film', 120, '2025-04-01', '2025-06-01', '1,3,5,0',
 'Eiichiro Oda', 'Luffy, Zoro', 'Phiêu lưu hải tặc',
 'https://www.youtube.com/watch?v=dQw4w9WgXcQ',
 'https://upload.wikimedia.org/wikipedia/en/2/2c/One_Piece_Logo.png',
 'Tiếng Nhật', 13, 9.0, 65000, 2, 'admin'),

-- ===== 10 PHIM HOLLYWOOD (ĐANG CHIẾU MOCK)
('P11', 'Fast & Furious 10', 140, '2024-12-01', '2025-03-01', '4,6',
 'Louis Leterrier', 'Vin Diesel, Jason Momoa', 'Phim đua xe hành động',
 'https://www.youtube.com/watch?v=32RAq6JzY-w',
 'https://upload.wikimedia.org/wikipedia/en/0/0c/Fast_X_poster.jpg',
 'Tiếng Anh', 16, 8.1, 65000, 1, 'admin'),

('P12', 'Spider-Man: New World', 130, '2025-01-05', '2025-04-01', '1,3',
 'Marvel Studios', 'Tom Holland', 'Siêu anh hùng Marvel',
 'https://www.youtube.com/watch?v=JfVOs4VSpmA',
 'https://upload.wikimedia.org/wikipedia/en/0/0c/Spider-Man_No_Way_Home_poster.jpg',
 'Tiếng Anh', 13, 9.1, 65000, 1, 'admin'),

('P13', 'Frozen 3', 115, '2025-06-01', '2025-08-01', '0,1',
 'Disney', 'Elsa, Anna', 'Hoạt hình Disney',
 'https://www.youtube.com/watch?v=TbQm5doF_Uc',
 'https://upload.wikimedia.org/wikipedia/en/0/05/Frozen_%282013_film%29_poster.jpg',
 'Tiếng Anh', 0, 8.6, 65000, 2, 'admin'),

('P14', 'The Nun 3', 120, '2025-03-20', '2025-05-15', '2,4,6',
 'Michael Chaves', 'Taissa Farmiga', 'Phim kinh dị Hollywood',
 'https://www.youtube.com/watch?v=QF-oyCwaArU',
 'https://upload.wikimedia.org/wikipedia/en/4/47/The_Nun_II_poster.jpg',
 'Tiếng Anh', 18, 7.5, 65000, 2, 'admin'),

('P15', 'Joker 2', 135, '2025-10-01', '2025-12-01', '3,5,0',
 'Todd Phillips', 'Joaquin Phoenix', 'Tâm lý tội phạm',
 'https://www.youtube.com/watch?v=zAGVQLHvwOY',
 'https://upload.wikimedia.org/wikipedia/en/e/e1/Joker_%282019_film%29_poster.jpg',
 'Tiếng Anh', 18, 9.3, 65000, 2, 'admin'),

-- ===== 15 PHIM NGỪNG CHIẾU (DATA LỊCH SỬ)
('P16', 'Avengers: Endgame', 180, '2019-04-26', '2019-07-01', '',
 'Russo Brothers', 'Robert Downey Jr', 'Siêu anh hùng Marvel',
 'https://www.youtube.com/watch?v=TcMBFSGVi1c',
 'https://upload.wikimedia.org/wikipedia/en/0/0d/Avengers_Endgame_poster.jpg',
 'Tiếng Anh', 13, 9.5, 65000, 0, 'admin'),

('P17', 'Titanic', 195, '1997-12-19', '1998-06-01', '',
 'James Cameron', 'Leonardo DiCaprio', 'Phim tình cảm kinh điển',
 'https://www.youtube.com/watch?v=2e-eXJ6HgkQ',
 'https://upload.wikimedia.org/wikipedia/en/2/22/Titanic_poster.jpg',
 'Tiếng Anh', 13, 9.0, 65000, 0, 'admin'),

('P18', 'Interstellar', 169, '2014-11-07', '2015-02-01', '',
 'Christopher Nolan', 'Matthew McConaughey', 'Du hành vũ trụ',
 'https://www.youtube.com/watch?v=zSWdZVtXT7E',
 'https://upload.wikimedia.org/wikipedia/en/b/bc/Interstellar_film_poster.jpg',
 'Tiếng Anh', 13, 9.4, 65000, 0, 'admin'),

('P19', 'The Lion King', 118, '2019-07-19', '2019-10-01', '',
 'Disney', 'Simba', 'Hoạt hình Disney',
 'https://www.youtube.com/watch?v=7TavVZMewpY',
 'https://upload.wikimedia.org/wikipedia/en/7/73/The_Lion_King_2019_poster.jpg',
 'Tiếng Anh', 0, 8.5, 65000, 0, 'admin'),

('P20', 'Minions', 91, '2015-07-10', '2015-10-01', '',
 'Illumination', 'Minions', 'Hoạt hình hài',
 'https://www.youtube.com/watch?v=eisKxhjBnZ0',
 'https://upload.wikimedia.org/wikipedia/en/3/3d/Minions_poster.jpg',
 'Tiếng Anh', 0, 8.2, 65000, 0, 'admin'),

('P21', 'Transformers', 144, '2007-07-03', '2007-10-01', '',
 'Michael Bay', 'Shia LaBeouf', 'Robot hành động',
 'https://www.youtube.com/watch?v=v8ItGrI-Ou0',
 'https://upload.wikimedia.org/wikipedia/en/6/66/Transformers07.jpg',
 'Tiếng Anh', 13, 8.0, 65000, 0, 'admin'),

('P22', 'Jurassic World', 124, '2015-06-12', '2015-09-01', '',
 'Colin Trevorrow', 'Chris Pratt', 'Khủng long phiêu lưu',
 'https://www.youtube.com/watch?v=RFinNxS5KN4',
 'https://upload.wikimedia.org/wikipedia/en/6/6e/Jurassic_World_poster.jpg',
 'Tiếng Anh', 13, 8.3, 65000, 0, 'admin'),

('P23', 'Frozen', 102, '2013-11-27', '2014-03-01', '',
 'Disney', 'Elsa, Anna', 'Công chúa băng giá',
 'https://www.youtube.com/watch?v=TbQm5doF_Uc',
 'https://upload.wikimedia.org/wikipedia/en/0/05/Frozen_%282013_film%29_poster.jpg',
 'Tiếng Anh', 0, 8.7, 65000, 0, 'admin'),

('P24', 'Inception', 148, '2010-07-16', '2010-10-01', '',
 'Christopher Nolan', 'Leonardo DiCaprio', 'Giấc mơ tầng sâu',
 'https://www.youtube.com/watch?v=YoHD9XEInc0',
 'https://upload.wikimedia.org/wikipedia/en/7/7f/Inception_ver3.jpg',
 'Tiếng Anh', 13, 9.2, 65000, 0, 'admin'),

('P25', 'Doctor Strange', 115, '2016-11-04', '2017-02-01', '',
 'Marvel Studios', 'Benedict Cumberbatch', 'Pháp sư tối thượng',
 'https://www.youtube.com/watch?v=HSzx-zryEgM',
 'https://upload.wikimedia.org/wikipedia/en/c/c7/Doctor_Strange_poster.jpg',
 'Tiếng Anh', 13, 8.6, 65000, 0, 'admin'),

('P26', 'Aquaman', 143, '2018-12-21', '2019-03-01', '',
 'James Wan', 'Jason Momoa', 'Anh hùng đại dương',
 'https://www.youtube.com/watch?v=WDkg3h8PCVU',
 'https://upload.wikimedia.org/wikipedia/en/e/ea/Aquaman_%282018_film%29_poster.jpg',
 'Tiếng Anh', 13, 8.1, 65000, 0, 'admin'),

('P27', 'Black Panther', 134, '2018-02-16', '2018-05-01', '',
 'Ryan Coogler', 'Chadwick Boseman', 'Vua Wakanda',
 'https://www.youtube.com/watch?v=xjDjIWPwcPU',
 'https://upload.wikimedia.org/wikipedia/en/d/d6/Black_Panther_%28film%29_poster.jpg',
 'Tiếng Anh', 13, 8.8, 65000, 0, 'admin'),

('P28', 'Aladdin', 128, '2019-05-24', '2019-08-01', '0,1,2,3,4',
 'Guy Ritchie', 'Will Smith', 'Phiêu lưu thần đèn',
 'https://www.youtube.com/watch?v=foyufD52aog',
 'https://upload.wikimedia.org/wikipedia/en/5/5c/Aladdin_%282019_film%29.png',
 'Tiếng Anh', 0, 8.4, 65000, 0, 'admin'),

('P29', 'Toy Story 4', 100, '2019-06-21', '2019-09-01', '',
 'Pixar', 'Woody, Buzz', 'Hoạt hình Pixar',
 'https://www.youtube.com/watch?v=wmiIUN-7qhE',
 'https://upload.wikimedia.org/wikipedia/en/4/4c/Toy_Story_4_poster.jpg',
 'Tiếng Anh', 0, 8.9, 65000, 0, 'admin'),

('P30', 'Kung Fu Panda', 95, '2008-06-06', '2008-09-01', '1,3,5,6',
 'DreamWorks', 'Po', 'Hoạt hình võ thuật',
 'https://www.youtube.com/watch?v=PXi3Mv6KMzY',
 'https://upload.wikimedia.org/wikipedia/en/7/76/Kungfupanda.jpg',
 'Tiếng Anh', 0, 8.8, 65000, 0, 'admin');

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
