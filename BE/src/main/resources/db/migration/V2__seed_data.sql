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
INSERT INTO the_loai (id, ten_the_loai, nguoi_tao) VALUES
                                                       ('tl-1', 'Hành động', 'system'), ('tl-2', 'Kinh dị', 'system'), ('tl-3', 'Hoạt hình', 'system');

INSERT INTO phim (id, ten_phim, ma_phim, thoi_luong, gia_phim, loai_phim, trang_thai, nguoi_tao) VALUES
                                                                                                     ('p-1', 'Avatar: Dòng chảy của nước', 'P001', 192, 85000, '3D', 1, 'system'),
                                                                                                     ('p-2', 'Conan: Tàu ngầm sắt', 'P002', 110, 65000, '2D', 1, 'system'),
                                                                                                     ('p-3', 'Oppenheimer', 'P003', 180, 85000, 'IMAX', 1, 'system');

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
INSERT INTO suat_chieu (id, id_khung_gio, id_phong_chieu, id_phim, ngay_chieu, so_ghe_trong, trang_thai, nguoi_tao) VALUES
    ('sc-1', 'kg-5', 'pc-1', 'p-1', CURDATE(), 200, 1, 'system');

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