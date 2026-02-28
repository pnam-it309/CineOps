package service.cinema.be.core.admin.quanlynhanvien.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.cinema.be.core.admin.quanlynhanvien.dto.request.AdNhanVienRequest;
import service.cinema.be.core.admin.quanlynhanvien.dto.response.AdNhanVienResponse;
import service.cinema.be.core.admin.quanlynhanvien.repository.AdNhanVienRepository;
import service.cinema.be.entity.NhanVien;
import service.cinema.be.repository.PhanQuyenRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdNhanVienService {

    private final AdNhanVienRepository adNhanVienRepository;
    private final PhanQuyenRepository phanQuyenRepository;

    @Transactional(readOnly = true)
    public List<AdNhanVienResponse> getAll(String search, String idPhanQuyen, Integer trangThai) {
        // Gọi hàm search từ Repository đã cấu hình @Query
        return adNhanVienRepository.searchNhanVien(search, idPhanQuyen, trangThai).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public AdNhanVienResponse create(AdNhanVienRequest request) {
        // 1. Kiểm tra các thông tin duy nhất (Unique)
        if (adNhanVienRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email nhân viên đã tồn tại trên hệ thống!");
        }
        if (adNhanVienRepository.existsBySoDienThoai(request.getSoDienThoai())) {
            throw new IllegalArgumentException("Số điện thoại đã tồn tại trên hệ thống!");
        }
        if (adNhanVienRepository.existsByCccd(request.getCccd())) {
            throw new IllegalArgumentException("Số CCCD đã tồn tại trên hệ thống!");
        }

        NhanVien nv = new NhanVien();

        // 2. Sinh mã tự động NVxxx thay vì dùng System.currentTimeMillis()
        nv.setMaNhanVien(generateMaNhanVien());

        nv.setNgayTao(LocalDateTime.now());

        // Mật khẩu mặc định nếu không nhập
        if (request.getMatKhau() == null || request.getMatKhau().isBlank()) {
            nv.setMatKhau("123456");
        }

        transferData(request, nv);
        return toResponse(adNhanVienRepository.save(nv));
    }

    @Transactional
    public AdNhanVienResponse update(String id, AdNhanVienRequest request) {
        NhanVien nv = adNhanVienRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên có ID: " + id));

        // Kiểm tra trùng lặp thông tin nhưng loại trừ bản ghi hiện tại
        if (adNhanVienRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new IllegalArgumentException("Email đã được sử dụng bởi nhân viên khác!");
        }
        if (adNhanVienRepository.existsBySoDienThoaiAndIdNot(request.getSoDienThoai(), id)) {
            throw new IllegalArgumentException("Số điện thoại đã được sử dụng bởi nhân viên khác!");
        }
        if (adNhanVienRepository.existsByCccdAndIdNot(request.getCccd(), id)) {
            throw new IllegalArgumentException("Số CCCD đã được sử dụng bởi nhân viên khác!");
        }

        transferData(request, nv);
        nv.setNgayCapNhat(LocalDateTime.now());
        return toResponse(adNhanVienRepository.save(nv));
    }

    @Transactional
    public void delete(String id) {
        if (!adNhanVienRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy nhân viên để xóa!");
        }
        adNhanVienRepository.deleteById(id);
    }

    /**
     * Logic sinh mã NV tự tăng: NV001, NV002...
     */
    private String generateMaNhanVien() {
        String maxMa = adNhanVienRepository.findMaxMaNhanVien();
        if (maxMa == null || maxMa.isEmpty()) {
            return "NV001";
        }
        try {
            // Cắt chuỗi "NV" và tăng số lên 1
            int lastNumber = Integer.parseInt(maxMa.substring(2));
            return String.format("NV%03d", lastNumber + 1);
        } catch (Exception e) {
            // Backup trường hợp mã lỗi định dạng
            return "NV" + (System.currentTimeMillis() % 10000);
        }
    }

    private void transferData(AdNhanVienRequest request, NhanVien nv) {
        nv.setTenNhanVien(request.getTenNhanVien());
        nv.setEmail(request.getEmail());
        nv.setSoDienThoai(request.getSoDienThoai());
        nv.setCccd(request.getCccd());
        nv.setNgaySinh(request.getNgaySinh());
        nv.setQueQuan(request.getQueQuan());
        nv.setGioiTinh(request.getGioiTinh());
        nv.setChucVu(request.getChucVu());
        nv.setAnhNhanVien(request.getAnhNhanVien());
        nv.setTrangThai(request.getTrangThai() != null ? request.getTrangThai() : 1);

        if (request.getMatKhau() != null && !request.getMatKhau().isBlank()) {
            nv.setMatKhau(request.getMatKhau());
        }

        // Mapping phân quyền từ ID gửi lên
        if (request.getIdPhanQuyen() != null) {
            phanQuyenRepository.findById(request.getIdPhanQuyen())
                    .ifPresent(nv::setPhanQuyen);
        }
    }

    private AdNhanVienResponse toResponse(NhanVien nv) {
        return AdNhanVienResponse.builder()
                .id(nv.getId())
                .maNhanVien(nv.getMaNhanVien())
                .tenNhanVien(nv.getTenNhanVien())
                .email(nv.getEmail())
                // Tạo username từ phần trước dấu @ của email
                .tenDangNhap("@" + (nv.getEmail() != null ? nv.getEmail().split("@")[0] : "user"))
                .soDienThoai(nv.getSoDienThoai())
                .cccd(nv.getCccd())
                .ngaySinh(nv.getNgaySinh())
                .queQuan(nv.getQueQuan())
                .tenPhanQuyen(nv.getPhanQuyen() != null ? nv.getPhanQuyen().getTenVaiTro() : "Chưa xác định")
                .idPhanQuyen(nv.getPhanQuyen() != null ? nv.getPhanQuyen().getId() : null)
                .chucVu(nv.getChucVu())
                .anhNhanVien(nv.getAnhNhanVien())
                .trangThai(nv.getTrangThai())
                .ngayTao(nv.getNgayTao())
                .build();
    }
}