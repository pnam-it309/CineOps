-- =================================================================
-- 1. CẤU HÌNH HỆ THỐNG & PHÂN QUYỀN
-- =================================================================
INSERT INTO phan_quyen (id, ma_phan_quyen, ten_vai_tro, quyen_han, nguoi_tao) VALUES
                                                                                  ('pq-admin', 'ROLE_ADMIN', 'Quản trị viên', 'ALL', 'system'),
                                                                                  ('pq-staff', 'ROLE_STAFF', 'Nhân viên bán vé', 'POS,CHECKIN', 'system'),
                                                                                  ('pq-manager', 'ROLE_MANAGER', 'Quản lý rạp', 'REPORT,STOCKS', 'system'),
                                                                                  ('pq-marketing', 'ROLE_MARKETING', 'Nhân viên Marketing', 'VOUCHER,NEWS', 'system'),
                                                                                  ('pq-customer', 'ROLE_CUSTOMER', 'Khách hàng', 'BOOKING', 'system');

INSERT INTO loai_ngay (id, ten_loai_ngay, he_so_ngay, nguoi_tao) VALUES
                                                                     ('ln-1', 'Thứ 2 - Thứ 5', 1.0, 'system'),
                                                                     ('ln-2', 'Thứ 6', 1.1, 'system'),
                                                                     ('ln-3', 'Thứ 7', 1.25, 'system'),
                                                                     ('ln-4', 'Chủ nhật', 1.3, 'system'),
                                                                     ('ln-5', 'Lễ Giáng Sinh', 1.5, 'system'),
                                                                     ('ln-6', 'Tết Nguyên Đán', 2.0, 'system');

INSERT INTO loai_ghe (id, ten_loai, phu_phi, nguoi_tao) VALUES
                                                            ('lg-1', 'Thường', 0, 'system'),
                                                            ('lg-2', 'VIP', 20000, 'system'),
                                                            ('lg-3', 'Couple (Đôi)', 50000, 'system'),
                                                            ('lg-4', 'Sofa', 60000, 'system');

INSERT INTO loai_khach_hang (id, ten_loai, he_so_giam_gia, mo_ta, nguoi_tao) VALUES
                                                                                 ('lkh-1', 'Vãng lai', 0, 'Khách chưa có tài khoản', 'system'),
                                                                                 ('lkh-2', 'Thành viên mới', 0, 'Mới đăng ký', 'system'),
                                                                                 ('lkh-3', 'Đồng', 2, 'Tích lũy > 1tr', 'system'),
                                                                                 ('lkh-4', 'Bạc', 4, 'Tích lũy > 5tr', 'system'),
                                                                                 ('lkh-5', 'Vàng', 6, 'Tích lũy > 10tr', 'system');

-- =================================================================
-- 2. CƠ SỞ VẬT CHẤT RẠP CHIẾU
-- =================================================================
INSERT INTO phong_chieu (id, ten_phong, loai_man_hinh, tong_ghe, nguoi_tao) VALUES
                                                                                ('pc-1', 'Phòng IMAX 01', 'IMAX 3D', 200, 'system'),
                                                                                ('pc-2', 'Phòng Gold Class', '2D Digital', 40, 'system'),
                                                                                ('pc-3', 'Phòng 03', '2D', 150, 'system');

INSERT INTO khung_gio (id, ten_khung_gio, gio_bat_dau, gio_ket_thuc, nguoi_tao) VALUES
                                                                                    ('kg-1', 'Suất sớm', '07:30:00', '10:00:00', 'system'),
                                                                                    ('kg-2', 'Sáng', '10:30:00', '13:00:00', 'system'),
                                                                                    ('kg-3', 'Trưa', '13:30:00', '16:00:00', 'system'),
                                                                                    ('kg-4', 'Chiều', '16:30:00', '19:00:00', 'system'),
                                                                                    ('kg-5', 'Tối 1', '19:30:00', '22:00:00', 'system');

-- =================================================================
-- 3. PHIM & THỂ LOẠI
-- =================================================================
-- 5. PHIM
INSERT INTO phim (id, ten_phim, thoi_luong, ngay_khoi_chieu, ngay_ket_thuc, lich_chieu,
                  dao_dien, dien_vien, mo_ta, trailer, poster, ngon_ngu, do_tuoi,
                  danh_gia, gia_phim, trang_thai, nguoi_tao)
VALUES

-- ===== 15 PHIM ĐANG CHIẾU / SẮP CHIẾU (GIỐNG RẠP)
('P1', 'Thỏ Ơi!!', 95, '2025-02-01', '2025-03-30', '2,4,6,0',
 'Trấn Thành', 'Trấn Thành, Ly Ly, Pháo, Văn Mai Hương, Quốc Anh', 'Phim hoạt hình thiếu nhi vui nhộn',
 'https://www.youtube.com/watch?v=dQw4w9WgXcQ',
 'https://upload.wikimedia.org/wikipedia/en/e/e1/Joker_%282019_film%29_poster.jpg',
 'Tiếng Việt', 0, 8.5, 65000, 1, 'admin'),

('P2', 'Mùi Phở', 110, '2025-02-10', '2025-04-01', '1,2,3,4',
 'Minh Beta', 'Xuân Hinh, Thu Trang, Thanh Thanh Hiền, Quốc Tuấn', 'Phim Việt về văn hóa ẩm thực',
 'https://www.youtube.com/watch?v=dQw4w9WgXcQ',
 'https://upload.wikimedia.org/wikipedia/en/e/e1/Joker_%282019_film%29_poster.jpg',
 'Tiếng Việt', 13, 8.0, 65000, 1, 'admin'),

('P3', 'Tài', 105, '2025-02-05', '2025-03-25', '2,3,4,5,6',
 'Mỹ Tâm', 'Mỹ Tâm, Vinh Râu', 'Phim tâm lý xã hội',
 'https://www.youtube.com/watch?v=dQw4w9WgXcQ',
 'https://upload.wikimedia.org/wikipedia/en/e/e1/Joker_%282019_film%29_poster.jpg',
 'Tiếng Việt', 16, 7.8, 65000, 1, 'admin'),

('P4', 'Quỷ Nhập Tràng 2', 120, '2025-02-15', '2025-04-10', '4,6,0,1,2',
 'Đang cập nhật', 'Khả Như, Vân Dung', 'Phim kinh dị Việt Nam',
 'https://www.youtube.com/watch?v=dQw4w9WgXcQ',
 'https://upload.wikimedia.org/wikipedia/en/e/e1/Joker_%282019_film%29_poster.jpg',
 'Tiếng Việt', 18, 8.7, 65000, 1, 'admin'),

('P5', 'Avatar: Lửa và Tro Tàn', 165, '2025-12-20', '2026-02-01', '4,5,6,0,1',
 'James Cameron', 'Sam Worthington, Zoe Saldana', 'Bom tấn khoa học viễn tưởng',
 'https://www.youtube.com/watch?v=5PSNL1qE6VY',
 'https://upload.wikimedia.org/wikipedia/en/e/e1/Joker_%282019_film%29_poster.jpg',
 'Tiếng Anh', 13, 9.2, 65000, 2, 'admin'),

('P6', 'Khủng Long Đón Tết', 100, '2025-01-20', '2025-03-01', '2,3,4,5,6',
 'Pixar VN', 'Lồng tiếng Việt', 'Hoạt hình gia đình dịp Tết',
 'https://www.youtube.com/watch?v=dQw4w9WgXcQ',
 'https://upload.wikimedia.org/wikipedia/en/e/e1/Joker_%282019_film%29_poster.jpg',
 'Tiếng Việt', 0, 8.3, 65000, 1, 'admin'),

('P7', 'Tuyển Thủ Dê', 98, '2025-03-01', '2025-04-15', '0,1,2,3',
 'Gabrielle Unio', 'Diễn viên lồng tiếng', 'Phim hoạt hình thể thao',
 'https://www.youtube.com/watch?v=dQw4w9WgXcQ',
 'https://upload.wikimedia.org/wikipedia/en/e/e1/Joker_%282019_film%29_poster.jpg',
 'Tiếng Việt', 7, 7.9, 65000, 2, 'admin'),

('P8', 'Doraemon Movie', 105, '2025-02-18', '2025-04-20', '',
 'Fujiko F. Fujio', 'Doraemon, Nobita', 'Phim hoạt hình Nhật Bản',
 'https://www.youtube.com/watch?v=dQw4w9WgXcQ',
 'https://upload.wikimedia.org/wikipedia/en/e/e1/Joker_%282019_film%29_poster.jpg',
 'Tiếng Nhật', 0, 8.8, 65000, 1, 'admin'),

('P9', 'Conan Movie', 110, '2025-03-10', '2025-05-01', '',
 'Aoyama Gosho', 'Conan, Ran', 'Thám tử lừng danh Conan movie',
 'https://www.youtube.com/watch?v=dQw4w9WgXcQ',
 'https://upload.wikimedia.org/wikipedia/en/e/e1/Joker_%282019_film%29_poster.jpg',
 'Tiếng Nhật', 13, 8.9, 65000, 2, 'admin'),

('P10', 'One Piece Film', 120, '2025-04-01', '2025-06-01', '1,3,5,0',
 'Eiichiro Oda', 'Luffy, Zoro', 'Phiêu lưu hải tặc',
 'https://www.youtube.com/watch?v=dQw4w9WgXcQ',
 'https://upload.wikimedia.org/wikipedia/en/e/e1/Joker_%282019_film%29_poster.jpg',
 'Tiếng Nhật', 13, 9.0, 65000, 2, 'admin'),

-- ===== 10 PHIM HOLLYWOOD (ĐANG CHIẾU MOCK)
('P11', 'Fast & Furious 10', 140, '2024-12-01', '2025-03-01', '4,6',
 'Louis Leterrier', 'Vin Diesel, Jason Momoa', 'Phim đua xe hành động',
 'https://www.youtube.com/watch?v=32RAq6JzY-w',
 'https://upload.wikimedia.org/wikipedia/en/e/e1/Joker_%282019_film%29_poster.jpg',
 'Tiếng Anh', 16, 8.1, 65000, 1, 'admin'),

('P12', 'Spider-Man: New World', 130, '2025-01-05', '2025-04-01', '1,3',
 'Marvel Studios', 'Tom Holland', 'Siêu anh hùng Marvel',
 'https://www.youtube.com/watch?v=JfVOs4VSpmA',
 'https://upload.wikimedia.org/wikipedia/en/e/e1/Joker_%282019_film%29_poster.jpg',
 'Tiếng Anh', 13, 9.1, 65000, 1, 'admin'),

('P13', 'Frozen 3', 115, '2025-06-01', '2025-08-01', '0,1',
 'Disney', 'Elsa, Anna', 'Hoạt hình Disney',
 'https://www.youtube.com/watch?v=TbQm5doF_Uc',
 'https://upload.wikimedia.org/wikipedia/en/0/05/Frozen_%282013_film%29_poster.jpg',
 'Tiếng Anh', 0, 8.6, 65000, 2, 'admin'),

('P14', 'The Nun 3', 120, '2025-03-20', '2025-05-15', '2,4,6',
 'Michael Chaves', 'Taissa Farmiga', 'Phim kinh dị Hollywood',
 'https://www.youtube.com/watch?v=QF-oyCwaArU',
 'https://upload.wikimedia.org/wikipedia/en/e/e1/Joker_%282019_film%29_poster.jpg',
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
 'https://upload.wikimedia.org/wikipedia/en/0/05/Frozen_%282013_film%29_poster.jpg',
 'Tiếng Anh', 13, 9.5, 65000, 0, 'admin'),

('P17', 'Titanic', 195, '1997-12-19', '1998-06-01', '',
 'James Cameron', 'Leonardo DiCaprio', 'Phim tình cảm kinh điển',
 'https://www.youtube.com/watch?v=2e-eXJ6HgkQ',
 'https://upload.wikimedia.org/wikipedia/en/0/05/Frozen_%282013_film%29_poster.jpg',
 'Tiếng Anh', 13, 9.0, 65000, 0, 'admin'),

('P18', 'Interstellar', 169, '2014-11-07', '2015-02-01', '',
 'Christopher Nolan', 'Matthew McConaughey', 'Du hành vũ trụ',
 'https://www.youtube.com/watch?v=zSWdZVtXT7E',
 'https://upload.wikimedia.org/wikipedia/en/b/bc/Interstellar_film_poster.jpg',
 'Tiếng Anh', 13, 9.4, 65000, 0, 'admin'),

('P19', 'The Lion King', 118, '2019-07-19', '2019-10-01', '',
 'Disney', 'Simba', 'Hoạt hình Disney',
 'https://www.youtube.com/watch?v=7TavVZMewpY',
 'https://upload.wikimedia.org/wikipedia/en/0/05/Frozen_%282013_film%29_poster.jpg',
 'Tiếng Anh', 0, 8.5, 65000, 0, 'admin'),

('P20', 'Minions', 91, '2015-07-10', '2015-10-01', '',
 'Illumination', 'Minions', 'Hoạt hình hài',
 'https://www.youtube.com/watch?v=eisKxhjBnZ0',
 'https://upload.wikimedia.org/wikipedia/en/0/05/Frozen_%282013_film%29_poster.jpg',
 'Tiếng Anh', 0, 8.2, 65000, 0, 'admin'),

('P21', 'Transformers', 144, '2007-07-03', '2007-10-01', '',
 'Michael Bay', 'Shia LaBeouf', 'Robot hành động',
 'https://www.youtube.com/watch?v=v8ItGrI-Ou0',
 'https://upload.wikimedia.org/wikipedia/en/0/05/Frozen_%282013_film%29_poster.jpg',
 'Tiếng Anh', 13, 8.0, 65000, 0, 'admin'),

('P22', 'Jurassic World', 124, '2015-06-12', '2015-09-01', '',
 'Colin Trevorrow', 'Chris Pratt', 'Khủng long phiêu lưu',
 'https://www.youtube.com/watch?v=RFinNxS5KN4',
 'https://upload.wikimedia.org/wikipedia/en/0/05/Frozen_%282013_film%29_poster.jpg',
 'Tiếng Anh', 13, 8.3, 65000, 0, 'admin'),

('P23', 'Frozen', 102, '2013-11-27', '2014-03-01', '',
 'Disney', 'Elsa, Anna', 'Công chúa băng giá',
 'https://www.youtube.com/watch?v=TbQm5doF_Uc',
 'https://upload.wikimedia.org/wikipedia/en/0/05/Frozen_%282013_film%29_poster.jpg',
 'Tiếng Anh', 0, 8.7, 65000, 0, 'admin'),

('P24', 'Inception', 148, '2010-07-16', '2010-10-01', '',
 'Christopher Nolan', 'Leonardo DiCaprio', 'Giấc mơ tầng sâu',
 'https://www.youtube.com/watch?v=YoHD9XEInc0',
 'https://upload.wikimedia.org/wikipedia/en/0/05/Frozen_%282013_film%29_poster.jpg',
 'Tiếng Anh', 13, 9.2, 65000, 0, 'admin'),

('P25', 'Doctor Strange', 115, '2016-11-04', '2017-02-01', '',
 'Marvel Studios', 'Benedict Cumberbatch', 'Pháp sư tối thượng',
 'https://www.youtube.com/watch?v=HSzx-zryEgM',
 'https://upload.wikimedia.org/wikipedia/en/0/05/Frozen_%282013_film%29_poster.jpg',
 'Tiếng Anh', 13, 8.6, 65000, 0, 'admin'),

('P26', 'Aquaman', 143, '2018-12-21', '2019-03-01', '',
 'James Wan', 'Jason Momoa', 'Anh hùng đại dương',
 'https://www.youtube.com/watch?v=WDkg3h8PCVU',
 'https://upload.wikimedia.org/wikipedia/en/0/05/Frozen_%282013_film%29_poster.jpg',
 'Tiếng Anh', 13, 8.1, 65000, 0, 'admin'),

('P27', 'Black Panther', 134, '2018-02-16', '2018-05-01', '',
 'Ryan Coogler', 'Chadwick Boseman', 'Vua Wakanda',
 'https://www.youtube.com/watch?v=xjDjIWPwcPU',
 'https://upload.wikimedia.org/wikipedia/en/0/05/Frozen_%282013_film%29_poster.jpg',
 'Tiếng Anh', 13, 8.8, 65000, 0, 'admin'),

('P28', 'Aladdin', 128, '2019-05-24', '2019-08-01', '0,1,2,3,4',
 'Guy Ritchie', 'Will Smith', 'Phiêu lưu thần đèn',
 'https://www.youtube.com/watch?v=foyufD52aog',
 'https://upload.wikimedia.org/wikipedia/en/0/05/Frozen_%282013_film%29_poster.jpg',
 'Tiếng Anh', 0, 8.4, 65000, 0, 'admin'),

('P29', 'Toy Story 4', 100, '2019-06-21', '2019-09-01', '',
 'Pixar', 'Woody, Buzz', 'Hoạt hình Pixar',
 'https://www.youtube.com/watch?v=wmiIUN-7qhE',
 'https://upload.wikimedia.org/wikipedia/en/0/05/Frozen_%282013_film%29_poster.jpg',
 'Tiếng Anh', 0, 8.9, 65000, 0, 'admin'),

('P30', 'Kung Fu Panda', 95, '2008-06-06', '2008-09-01', '1,3,5,6',
 'DreamWorks', 'Po', 'Hoạt hình võ thuật',
 'https://www.youtube.com/watch?v=PXi3Mv6KMzY',
 'https://upload.wikimedia.org/wikipedia/en/0/05/Frozen_%282013_film%29_poster.jpg',
 'Tiếng Anh', 0, 8.8, 65000, 0, 'admin');

-- 6. SUẤT CHIẾU
INSERT INTO suat_chieu (id, id_khung_gio, id_phong_chieu, id_phim, ngay_chieu, so_ghe_trong, trang_thai, nguoi_tao)
VALUES ('sc-1', 'kg-2', 'pc-1', 'P1', CURDATE(), 40, 1, 'system'),
       ('sc-2', 'kg-4', 'pc-1', 'P2', CURDATE(), 40, 1, 'system'),
       ('sc-3', 'kg-3', 'pc-2', 'P3', CURDATE(), 30, 1, 'system'),
       ('sc-4', 'kg-5', 'pc-2', 'P4', CURDATE(), 30, 2, 'system'),
       ('sc-5', 'kg-1', 'pc-1', 'P5', DATE_ADD(CURDATE(), INTERVAL 1 DAY), 40, 1, 'system'),
       ('sc-6', 'kg-2', 'pc-1', 'P6', DATE_ADD(CURDATE(), INTERVAL 1 DAY), 40, 1, 'system');

-- 7. THỂ LOẠI
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

-- 8. PHIM THỂ LOẠI

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

-- P29 - Toy Story 4 → Hoạt hình, Hài, Gia đình
('PTL070', 'P29', 'TL2'),
('PTL071', 'P29', 'TL5'),
('PTL072', 'P29', 'TL7'),

-- P30 - Kung Fu Panda → Hoạt hình, Hành động, Hài, Gia đình
('PTL073', 'P30', 'TL2'),
('PTL074', 'P30', 'TL1'),
('PTL075', 'P30', 'TL5'),
('PTL076', 'P30', 'TL7');
-- =================================================================
-- 4. TÀI KHOẢN & NGƯỜI DÙNG
-- =================================================================
INSERT INTO tai_khoan (id, id_phan_quyen, email, mat_khau, nguoi_tao) VALUES
                                                                          ('tk-1', 'pq-admin', 'admin@cineops.com', '$2a$12$7kS8B.zE3h.Wv.r6G.n.u.V8Y.n.u.V8Y.n.u.V8Y.n.u', 'system'),
                                                                          ('tk-2', 'pq-staff', 'staff1@cineops.com', '$2a$12$7kS8B.zE3h.Wv.r6G.n.u.V8Y.n.u.V8Y.n.u.V8Y.n.u', 'system'),
                                                                          ('tk-4', 'pq-customer', 'khach1@gmail.com', '$2a$12$7kS8B.zE3h.Wv.r6G.n.u.V8Y.n.u.V8Y.n.u.V8Y.n.u', 'system'),
                                                                          ('tk-5', 'pq-customer', 'khach2@gmail.com', '$2a$12$7kS8B.zE3h.Wv.r6G.n.u.V8Y.n.u.V8Y.n.u.V8Y.n.u', 'system');

INSERT INTO nhan_vien (id, id_tai_khoan, ma_nhan_vien, ten_nhan_vien, chuc_vu, nguoi_tao) VALUES
                                                                                              ('nv-1', 'tk-1', 'NV001', 'Nguyễn Huy Đức', 'Quản trị', 'system'),
                                                                                              ('nv-2', 'tk-2', 'NV002', 'Lê Văn Tám', 'Bán vé', 'system');

INSERT INTO khach_hang (id, id_tai_khoan, id_loai_khach_hang, ma_khach_hang, ten_khach_hang, sdt, nguoi_tao) VALUES
                                                                                                                 ('kh-1', 'tk-4', 'lkh-2', 'KH001', 'Trần Anh Tuấn', '0912345678', 'system'),
                                                                                                                 ('kh-2', 'tk-5', 'lkh-3', 'KH002', 'Lê Mỹ Linh', '0922345678', 'system');

-- =================================================================
-- 5. BẮP NƯỚC & VOUCHER
-- =================================================================
INSERT INTO loai_san_pham_di_kem (id, ten_loai) VALUES ('lsp-1', 'Bắp rang'), ('lsp-2', 'Nước uống');
INSERT INTO kich_co (id, ten_kich_co) VALUES ('kc-1', 'Nhỏ'), ('kc-2', 'Vừa'), ('kc-3', 'Lớn');
INSERT INTO don_vi_tinh (id, ten_don_vi_tinh) VALUES ('dvt-1', 'Ly'), ('dvt-2', 'Hộp'), ('dvt-3', 'Chai');

INSERT INTO san_pham_di_kem (id, id_loai_san_pham, ten_san_pham, nguoi_tao) VALUES
                                                                                ('sp-1', 'lsp-1', 'Bắp Caramel', 'system'),
                                                                                ('sp-3', 'lsp-2', 'Coca Cola', 'system');

INSERT INTO chi_tiet_san_pham_di_kem (id, id_san_pham, id_kich_co, id_don_vi_tinh, gia_ban, so_luong_ton) VALUES
                                                                                                              ('ctsp-1', 'sp-1', 'kc-1', 'dvt-2', 45000, 100),
                                                                                                              ('ctsp-4', 'sp-3', 'kc-1', 'dvt-1', 25000, 200);

INSERT INTO phieu_giam_gia (id, ma_phieu_giam_gia, ten_phieu, phan_tram_giam_gia, so_luong, nguoi_tao) VALUES
    ('v-1', 'CINEMA2026', 'Chào năm mới', 20.0, 100, 'system');

-- =================================================================
-- 6. GIAO DỊCH (GHẾ, SUẤT CHIẾU, HÓA ĐƠN, VÉ)
-- =================================================================

INSERT INTO ghe (id, id_loai_ghe, id_phong_chieu, so_ghe, so_hang, so_cot, trang_thai, nguoi_tao) VALUES
                                                                                                      ('ghe-1', 'lg-1', 'pc-1', 'A1', 'A', 1, 1, 'system'),
                                                                                                      ('ghe-2', 'lg-1', 'pc-1', 'A2', 'A', 2, 1, 'system'),
                                                                                                      ('ghe-3', 'lg-2', 'pc-1', 'H1', 'H', 1, 1, 'system'),
                                                                                                      ('ghe-4', 'lg-3', 'pc-1', 'K1', 'K', 1, 1, 'system'),
                                                                                                      ('ghe-5', 'lg-2', 'pc-1', 'H2', 'H', 2, 1, 'system');

INSERT INTO hoa_don (id, id_nhan_vien, id_khach_hang, ma_hoa_don, tong_tien, so_tien_giam, tong_tien_thanh_toan, phuong_thuc_thanh_toan, trang_thai, ngay_tao) VALUES
                                                                                                                                                                   ('hd-1', 'nv-2', 'kh-1', 'HD-10001', 130000, 0, 130000, 1, 1, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
                                                                                                                                                                   ('hd-2', 'nv-2', 'kh-2', 'HD-10002', 85000, 0, 85000, 0, 1, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
                                                                                                                                                                   ('hd-3', 'nv-2', NULL, 'HD-10003', 105000, 0, 105000, 1, 1, DATE_SUB(NOW(), INTERVAL 3 HOUR)),
                                                                                                                                                                   ('hd-4', 'nv-2', 'kh-1', 'HD-10004', 135000, 0, 135000, 1, 1, DATE_SUB(NOW(), INTERVAL 5 HOUR)),
                                                                                                                                                                   ('hd-5', 'nv-2', NULL, 'HD-10005', 105000, 0, 105000, 0, 1, DATE_SUB(NOW(), INTERVAL 1 DAY));

INSERT INTO ve (id, id_loai_khach_hang, id_ghe, id_suat_chieu, id_hoa_don, ma_ve, gia_thanh_toan, loai_ve, trang_thai, ngay_tao, nguoi_tao) VALUES
                                                                                                                                                ('ve-1', 'lkh-1', 'ghe-1', 'sc-1', 'hd-1', 'VE-10001', 85000, 0, 1, DATE_SUB(NOW(), INTERVAL 1 HOUR), 'NV002'),
                                                                                                                                                ('ve-2', 'lkh-2', 'ghe-2', 'sc-1', 'hd-2', 'VE-10002', 85000, 0, 1, DATE_SUB(NOW(), INTERVAL 2 HOUR), 'NV002'),
                                                                                                                                                ('ve-3', 'lkh-3', 'ghe-3', 'sc-1', 'hd-3', 'VE-10003', 105000, 1, 1, DATE_SUB(NOW(), INTERVAL 3 HOUR), 'Hệ thống Online'),
                                                                                                                                                ('ve-4', 'lkh-4', 'ghe-4', 'sc-1', 'hd-4', 'VE-10004', 135000, 1, 1, DATE_SUB(NOW(), INTERVAL 5 HOUR), 'Hệ thống Online'),
                                                                                                                                                ('ve-5', NULL,    'ghe-5', 'sc-1', 'hd-5', 'VE-10005', 105000, 0, 1, DATE_SUB(NOW(), INTERVAL 1 DAY), 'NV002');

INSERT INTO hoa_don_chi_tiet (id, id_hoa_don, id_ve, id_chi_tiet_san_pham_di_kem, loai, so_luong, don_gia, thanh_tien) VALUES
                                                                                                                           ('hdct-1', 'hd-1', 've-1', NULL, 1, 1, 85000, 85000),
                                                                                                                           ('hdct-2', 'hd-1', NULL, 'ctsp-1', 2, 1, 45000, 45000),
                                                                                                                           ('hdct-3', 'hd-2', 've-2', NULL, 1, 1, 85000, 85000),
                                                                                                                           ('hdct-4', 'hd-3', 've-3', NULL, 1, 1, 105000, 105000),
                                                                                                                           ('hdct-5', 'hd-4', 've-4', NULL, 1, 1, 135000, 135000),
                                                                                                                           ('hdct-6', 'hd-5', 've-5', NULL, 1, 1, 105000, 105000);