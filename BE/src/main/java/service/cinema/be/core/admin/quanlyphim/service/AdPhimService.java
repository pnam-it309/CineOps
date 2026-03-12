    package service.cinema.be.core.admin.quanlyphim.service;
    
    import lombok.RequiredArgsConstructor;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.PageRequest;
    import org.springframework.data.domain.Pageable;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import service.cinema.be.core.admin.quanlyphim.dto.request.AdPhimRequest;
    import service.cinema.be.core.admin.quanlyphim.dto.response.AdPhimPageResponse;
    import service.cinema.be.core.admin.quanlyphim.dto.response.AdPhimResponse;
    import service.cinema.be.core.admin.quanlyphim.dto.response.AdTheLoaiResponse;
    import service.cinema.be.core.admin.quanlyphim.repository.AdPhimRepository;
    import service.cinema.be.core.admin.quanlyphim.repository.AdPhimTheLoaiRepository;
    import service.cinema.be.core.admin.quanlyphim.repository.AdTheLoaiRepository;
    import service.cinema.be.entity.Phim;
    import service.cinema.be.entity.PhimTheLoai;
    import service.cinema.be.entity.TheLoai;
    
    import java.util.Collections;
    import java.util.List;
    
    @Service
    @RequiredArgsConstructor
    public class AdPhimService {
    
        private final AdPhimRepository adPhimRepository;
        private final AdTheLoaiRepository adTheLoaiRepository;
        private final AdPhimTheLoaiRepository adPhimTheLoaiRepository;
    
        // ==================== DANH SÁCH + TÌM KIẾM ====================
    
        @Transactional(readOnly = true)
        public AdPhimPageResponse getPhim(String tenPhim, Integer trangThai, String idTheLoai, int page, int size) {
            Pageable pageable = PageRequest.of(page, size);
            Page<Phim> pageResult = adPhimRepository.searchPhim(
                    (tenPhim != null && tenPhim.isBlank()) ? null : tenPhim,
                    trangThai,
                    (idTheLoai != null && idTheLoai.isBlank()) ? null : idTheLoai,
                    pageable
            );
            List<AdPhimResponse> content = pageResult.getContent()
                    .stream()
                    .map(this::toResponse)
                    .toList();
            return AdPhimPageResponse.builder()
                    .content(content)
                    .pageNumber(pageResult.getNumber())
                    .pageSize(pageResult.getSize())
                    .totalElements(pageResult.getTotalElements())
                    .totalPages(pageResult.getTotalPages())
                    .build();
        }
    
        @Transactional(readOnly = true)
        public AdPhimResponse getPhimById(String id) {
            Phim phim = adPhimRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phim với id: " + id));
            return toResponse(phim);
        }
    
        // ==================== DROPDOWN THỂ LOẠI ====================
    
        @Transactional(readOnly = true)
        public List<AdTheLoaiResponse> getTheLoaiDropdown() {
            return adTheLoaiRepository.findAll().stream()
                    .map(tl -> AdTheLoaiResponse.builder()
                            .id(tl.getId())
                            .tenTheLoai(tl.getTenTheLoai())
                            .build())
                    .toList();
        }
    
        // ==================== THÊM PHIM ====================
    
        @Transactional
        public AdPhimResponse createPhim(AdPhimRequest request) {
            if (adPhimRepository.existsByTenPhimIgnoreCase(request.getTenPhim())) {
                throw new IllegalArgumentException("Tên phim đã tồn tại");
            }
            Phim phim = new Phim();
            mapRequestToEntity(request, phim);
            Phim saved = adPhimRepository.save(phim);
            savePhimTheLoai(saved, request.getIdTheLoais());
            return toResponse(adPhimRepository.findById(saved.getId()).orElseThrow());
        }
    
        // ==================== SỬA PHIM ====================
    
        @Transactional
        public AdPhimResponse updatePhim(String id, AdPhimRequest request) {
            Phim phim = adPhimRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phim với id: " + id));
            if (adPhimRepository.existsByTenPhimIgnoreCaseAndIdNot(request.getTenPhim(), id)) {
                throw new IllegalArgumentException("Tên phim đã tồn tại");
            }
            mapRequestToEntity(request, phim);
            adPhimRepository.save(phim);
            adPhimTheLoaiRepository.deleteByPhimId(id);
            savePhimTheLoai(phim, request.getIdTheLoais());
            return toResponse(adPhimRepository.findById(id).orElseThrow());
        }
    
        // ==================== ĐỔI TRẠNG THÁI ====================
    
        @Transactional
        public AdPhimResponse updateTrangThai(String id, Integer trangThai) {
            Phim phim = adPhimRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phim với id: " + id));
            phim.setTrangThai(trangThai);
            return toResponse(adPhimRepository.save(phim));
        }
    
        // ==================== XÓA PHIM ====================
    
        @Transactional
        public void deletePhim(String id) {
            Phim phim = adPhimRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phim với id: " + id));
            phim.setTrangThai(0); // 0 = Ngừng chiếu
            adPhimRepository.save(phim);
        }
    
        // ==================== HELPER ====================
    
        private void mapRequestToEntity(AdPhimRequest request, Phim phim) {
            java.time.LocalDate today = java.time.LocalDate.now();
            java.time.LocalDate start = request.getNgayKhoiChieu();
            java.time.LocalDate end = request.getNgayKetThuc();
    
            // 1. Validate ngày kết thúc không được trước ngày hôm nay
            if (end != null && end.isBefore(today)) {
                throw new IllegalArgumentException("Ngày kết thúc không được nhỏ hơn ngày hôm nay!");
            }
    
            // 2. Validate ngày kết thúc phải sau ngày khởi chiếu
            if (start != null && end != null && end.isBefore(start)) {
                throw new IllegalArgumentException("Ngày kết thúc phải sau hoặc bằng ngày khởi chiếu!");
            }
    
            phim.setTenPhim(request.getTenPhim());
            phim.setThoiLuong(request.getThoiLuong());
            phim.setNgayKhoiChieu(start);
            phim.setNgayKetThuc(end);
            phim.setLichChieu(request.getLichChieu());
            phim.setTrailer(request.getTrailer());
            phim.setPoster(request.getPoster());
            phim.setNgonNgu(request.getNgonNgu());
            phim.setDoTuoi(request.getDoTuoi());
            phim.setNhanDoTuoi(request.getNhanDoTuoi());
            phim.setHienThiCanhBaoDoTuoi(request.getHienThiCanhBaoDoTuoi());
            phim.setMoTa(request.getMoTa());
            phim.setDanhGia(request.getDanhGia());
            phim.setGiaPhim(request.getGiaPhim());
            phim.setMaPhim(request.getMaPhim());
    
            // 3. Tự động tính toán trạng thái
            // 2 = Sắp chiếu, 1 = Đang chiếu, 0 = Ngừng chiếu
            if (start != null && end != null) {
                if (today.isBefore(start)) {
                    phim.setTrangThai(2); // Sắp chiếu
                } else if (!today.isAfter(end)) {
                    phim.setTrangThai(1); // Đang chiếu
                } else {
                    phim.setTrangThai(0); // Ngừng chiếu
                }
            } else {
                // Trường hợp thiếu ngày thì dùng mặc định từ request hoặc 1
                phim.setTrangThai(request.getTrangThai() != null ? request.getTrangThai() : 1);
            }
    
            phim.setLoaiPhim(request.getLoaiPhim() != null ? request.getLoaiPhim() : "2D");
            phim.setPhuPhiLoaiPhim(request.getPhuPhiLoaiPhim() != null
                    ? request.getPhuPhiLoaiPhim()
                    : 0.0);
        }
    
        /**
         * Tự động cập nhật trạng thái phim mỗi ngày vào lúc 0h00
         */
        @org.springframework.scheduling.annotation.Scheduled(cron = "0 0 0 * * *")
        @Transactional
        public void autoUpdateMovieStatuses() {
            java.time.LocalDate today = java.time.LocalDate.now();
            List<Phim> allPhim = adPhimRepository.findAll();
            for (Phim phim : allPhim) {
                java.time.LocalDate start = phim.getNgayKhoiChieu();
                java.time.LocalDate end = phim.getNgayKetThuc();
                if (start != null && end != null) {
                    int oldStatus = phim.getTrangThai() != null ? phim.getTrangThai() : -1;
                    int newStatus;
                    if (today.isBefore(start)) {
                        newStatus = 2; // Sắp chiếu
                    } else if (!today.isAfter(end)) {
                        newStatus = 1; // Đang chiếu
                    } else {
                        newStatus = 0; // Ngừng chiếu
                    }
                    if (oldStatus != newStatus) {
                        phim.setTrangThai(newStatus);
                        adPhimRepository.save(phim);
                    }
                }
            }
        }
    
        private void savePhimTheLoai(Phim phim, List<String> idTheLoais) {
            if (idTheLoais == null || idTheLoais.isEmpty()) return;
            for (String idTL : idTheLoais) {
                TheLoai theLoai = adTheLoaiRepository.findById(idTL)
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thể loại: " + idTL));
                PhimTheLoai ptl = new PhimTheLoai();
                ptl.setPhim(phim);
                ptl.setTheLoai(theLoai);
                adPhimTheLoaiRepository.save(ptl);
            }
        }
    
        private AdPhimResponse toResponse(Phim phim) {
            List<AdTheLoaiResponse> theLoais = (phim.getPhimTheLoais() == null) ? Collections.emptyList() :
                    phim.getPhimTheLoais().stream()
                    .map(ptl -> AdTheLoaiResponse.builder()
                            .id(ptl.getTheLoai().getId())
                            .tenTheLoai(ptl.getTheLoai().getTenTheLoai())
                            .build())
                    .toList();
            return AdPhimResponse.builder()
                    .id(phim.getId())
                    .tenPhim(phim.getTenPhim())
                    .thoiLuong(phim.getThoiLuong())
                    .ngayKhoiChieu(phim.getNgayKhoiChieu())
                    .ngayKetThuc(phim.getNgayKetThuc())  // ngay kett thuc
                    .lichChieu(phim.getLichChieu()) // lich chieu
                    .trailer(phim.getTrailer())
                    .poster(phim.getPoster())
                    .ngonNgu(phim.getNgonNgu())
                    .doTuoi(phim.getDoTuoi())
                    .nhanDoTuoi(phim.getNhanDoTuoi())
                    .hienThiCanhBaoDoTuoi(phim.getHienThiCanhBaoDoTuoi())
                    .loaiPhim(phim.getLoaiPhim())
                    .phuPhiLoaiPhim(phim.getPhuPhiLoaiPhim())
                    .moTa(phim.getMoTa())
                    .danhGia(phim.getDanhGia())
                    .giaPhim(phim.getGiaPhim()) // giaVeGoc
                    .trangThai(phim.getTrangThai())
                    .theLoais(theLoais)
                    .maPhim(phim.getMaPhim())
                    .ngayTao(phim.getNgayTao() != null ? phim.getNgayTao().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() : null)
                    .nguoiTao(phim.getNguoiTao())
                    .ngayCapNhat(phim.getNgayCapNhat() != null ? phim.getNgayCapNhat().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() : null)
                    .nguoiCapNhat(phim.getNguoiCapNhat())
                    .build();
        }
        // ==================== THÊM NHANH THỂ LOẠI ====================

        @Transactional
        public AdTheLoaiResponse quickCreateTheLoai(String tenTheLoai) {
            // 1. Kiểm tra trống
            if (tenTheLoai == null || tenTheLoai.isBlank()) {
                throw new IllegalArgumentException("Tên thể loại không được để trống!");
            }

            // 2. Kiểm tra trùng lặp (Cần thêm method existsByTenTheLoaiIgnoreCase trong AdTheLoaiRepository)
            if (adTheLoaiRepository.existsByTenTheLoaiIgnoreCase(tenTheLoai)) {
                throw new IllegalArgumentException("Thể loại '" + tenTheLoai + "' đã tồn tại!");
            }

            // 3. Tạo entity mới
            TheLoai theLoai = new TheLoai();
            theLoai.setTenTheLoai(tenTheLoai);
            // Lưu ý: Nếu bạn dùng Spring Data JPA Auditing, ngay_tao và nguoi_tao sẽ tự có.
            // Nếu không, hãy set thủ công tại đây.

            TheLoai saved = adTheLoaiRepository.save(theLoai);

            // 4. Trả về Response DTO để Frontend cập nhật danh sách
            return AdTheLoaiResponse.builder()
                    .id(saved.getId())
                    .tenTheLoai(saved.getTenTheLoai())
                    .build();
        }
    }