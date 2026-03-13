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
<<<<<<< HEAD

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
=======
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
>>>>>>> origin/feat/yen_dev

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

<<<<<<< HEAD
-- 2. HÓA ĐƠN HD020 (Gom nhóm 3 vé của cùng 1 phim)
INSERT INTO hoa_don (id, id_nhan_vien, id_khach_hang, ma_hoa_don, tong_tien, so_tien_giam, tong_tien_thanh_toan, phuong_thuc_thanh_toan, kenh_ban_hang, trang_thai, ngay_tao, nguoi_tao)
VALUES ('hd-20', 'nv-2', 'kh-1', 'HD020', 255000, 0, 255000, 1, 0, 1, NOW(), 'system');
=======
-- =================================================================
-- 6. GIAO DỊCH (GHẾ, SUẤT CHIẾU, HÓA ĐƠN, VÉ)
-- =================================================================
>>>>>>> origin/feat/yen_dev

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