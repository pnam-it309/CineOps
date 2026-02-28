package service.cinema.be.core.admin.quanlykhachhang.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.cinema.be.core.admin.quanlykhachhang.dto.request.AdKhachHangRequest;
import service.cinema.be.core.admin.quanlykhachhang.dto.response.AdKhachHangResponse;
import service.cinema.be.core.admin.quanlykhachhang.repository.AdKhachHangRepository;
import service.cinema.be.entity.KhachHang;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdKhachHangService {

    private final AdKhachHangRepository adKhachHangRepository;

    @Transactional(readOnly = true)
    public List<AdKhachHangResponse> getAllKhachHang(String search, Integer trangThai) {
        return adKhachHangRepository.findAllBySearchNative(search, trangThai).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public AdKhachHangResponse createKhachHang(AdKhachHangRequest request) {
        if (adKhachHangRepository.existsByEmail(request.getEmail()))
            throw new IllegalArgumentException("Email đã tồn tại");

        KhachHang kh = new KhachHang();
        kh.setMaKhachHang("KH" + System.currentTimeMillis());
        kh.setNgayTao(LocalDateTime.now());
        transferData(request, kh);
        return toResponse(adKhachHangRepository.save(kh));
    }

    @Transactional
    public AdKhachHangResponse updateKhachHang(String id, AdKhachHangRequest request) {
        KhachHang kh = adKhachHangRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng"));

        if (adKhachHangRepository.existsByEmailAndIdNot(request.getEmail(), id))
            throw new IllegalArgumentException("Email đã được sử dụng");

        transferData(request, kh);
        kh.setNgayCapNhat(LocalDateTime.now());
        return toResponse(adKhachHangRepository.save(kh));
    }

    @Transactional
    public void deleteKhachHang(String id) {
        adKhachHangRepository.deleteById(id);
    }

    private void transferData(AdKhachHangRequest request, KhachHang kh) {
        kh.setTenKhachHang(request.getTenKhachHang());
        kh.setEmail(request.getEmail());
        kh.setSdt(request.getSdt());
        kh.setGioiTinh(request.getGioiTinh());
        kh.setNgaySinh(request.getNgaySinh());
        kh.setTrangThai(request.getTrangThai() != null ? request.getTrangThai() : 1);
    }

    private AdKhachHangResponse toResponse(KhachHang kh) {
        return AdKhachHangResponse.builder()
                .id(kh.getId()).maKhachHang(kh.getMaKhachHang())
                .tenKhachHang(kh.getTenKhachHang()).email(kh.getEmail())
                .sdt(kh.getSdt()).gioiTinh(kh.getGioiTinh())
                .ngaySinh(kh.getNgaySinh()).trangThai(kh.getTrangThai())
                .build();
    }
}