package service.cinema.be.core.admin.QuanLySanPhamDiKem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.cinema.be.core.admin.QuanLySanPhamDiKem.dto.request.AdSanPhamDiKemRequest;
import service.cinema.be.core.admin.QuanLySanPhamDiKem.dto.response.AdSanPhamDiKemResponse;
import service.cinema.be.core.admin.QuanLySanPhamDiKem.repository.AdSanPhamDiKemRepository;
import service.cinema.be.entity.ChiTietSanPhamDiKem;
import service.cinema.be.entity.LoaiSanPham;
import service.cinema.be.entity.SanPhamDichVu;
import service.cinema.be.repository.ChiTietSanPhamDiKemRepository;
import service.cinema.be.repository.DonViTinhRepository;
import service.cinema.be.repository.KichCoRepository;
import service.cinema.be.repository.LoaiSanPhamRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdSanPhamDiKemService {

    private final AdSanPhamDiKemRepository repository;
    private final LoaiSanPhamRepository loaiSanPhamRepository;
    private final ChiTietSanPhamDiKemRepository chiTietRepository;
    private final KichCoRepository kichCoRepository;
    private final DonViTinhRepository dvtRepository;

    @Transactional(readOnly = true)
    public Page<AdSanPhamDiKemResponse> getAll(String keyword, Integer trangThai, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "ngayTao"));
        return repository.getAll(pageable, keyword, trangThai).map(this::toResponse);
    }

    @Transactional
    public AdSanPhamDiKemResponse create(AdSanPhamDiKemRequest request) {
        if (repository.existsByTenSanPhamIgnoreCase(request.getTenSanPham())) {
            throw new RuntimeException("Tên sản phẩm đã tồn tại");
        }

        SanPhamDichVu sp = new SanPhamDichVu();
        sp.setTenSanPham(request.getTenSanPham());
        sp.setMoTa(request.getMoTa());
        sp.setHinhAnh(request.getHinhAnh());
        sp.setTrangThai(request.getTrangThai() == null ? 1 : request.getTrangThai());
        sp.setLoaiSanPham(loaiSanPhamRepository.findById(request.getIdLoaiSanPham())
                .orElseThrow(() -> new RuntimeException("Loại sản phẩm không tồn tại")));

        SanPhamDichVu savedSp = repository.save(sp);

        saveVariants(savedSp, request.getVariants());

        return toResponse(savedSp);
    }

    @Transactional
    public AdSanPhamDiKemResponse update(String id, AdSanPhamDiKemRequest request) {
        SanPhamDichVu sp = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        sp.setTenSanPham(request.getTenSanPham());
        sp.setMoTa(request.getMoTa());
        sp.setHinhAnh(request.getHinhAnh());
        sp.setTrangThai(request.getTrangThai());
        sp.setLoaiSanPham(loaiSanPhamRepository.findById(request.getIdLoaiSanPham()).orElseThrow());

        // Xóa chi tiết cũ và cập nhật lại mới
        chiTietRepository.deleteBySanPhamId(id);
        saveVariants(sp, request.getVariants());

        return toResponse(repository.save(sp));
    }

    private void saveVariants(SanPhamDichVu sp, List<AdSanPhamDiKemRequest.VariantRequest> variantRequests) {
        if (variantRequests != null) {
            for (AdSanPhamDiKemRequest.VariantRequest vReq : variantRequests) {
                ChiTietSanPhamDiKem ct = new ChiTietSanPhamDiKem();
                ct.setSanPham(sp);
                ct.setGiaBan(vReq.getGiaBan());
                ct.setSoLuongTon(vReq.getSoLuongTon());
                ct.setGiaTriDinhLuong(vReq.getGiaTriDinhLuong());
                ct.setKichCo(kichCoRepository.findById(vReq.getIdKichCo()).orElseThrow());
                ct.setDonViTinh(dvtRepository.findById(vReq.getIdDonViTinh()).orElseThrow());
                chiTietRepository.save(ct);
            }
        }
    }

    private AdSanPhamDiKemResponse toResponse(SanPhamDichVu sp) {
        List<AdSanPhamDiKemResponse.VariantResponse> variants = sp.getChiTietSanPhamDiKems().stream()
                .map(ct -> new AdSanPhamDiKemResponse.VariantResponse(
                        ct.getId(), // ID kiểu String từ PrimaryEntity
                        ct.getKichCo().getTenKichCo(),
                        ct.getDonViTinh().getTenDonViTinh(),
                        ct.getGiaTriDinhLuong(),
                        ct.getGiaBan(),
                        ct.getSoLuongTon()
                )).collect(Collectors.toList());

        return AdSanPhamDiKemResponse.builder()
                .id(sp.getId())
                .tenSanPham(sp.getTenSanPham())
                .tenLoaiSanPham(sp.getLoaiSanPham().getTenLoai())
                .moTa(sp.getMoTa())
                .hinhAnh(sp.getHinhAnh())
                .trangThai(sp.getTrangThai())
                .variants(variants)
                .build();
    }

    @Transactional
    public void delete(String id) {
        repository.deleteById(id);
    }
}