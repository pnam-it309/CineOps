package service.cinema.be.core.admin.quanlynhanvien.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.cinema.be.core.admin.quanlynhanvien.dto.response.AdPhanQuyenResponse;
import service.cinema.be.repository.PhanQuyenRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdPhanQuyenService {

    private final PhanQuyenRepository phanQuyenRepository;

    @Transactional(readOnly = true)
    public List<AdPhanQuyenResponse> getAllActiveRoles() {
        return phanQuyenRepository.findAll().stream()
                .filter(pq -> pq.getTrangThai() == null || pq.getTrangThai() == 1)
                .map(pq -> AdPhanQuyenResponse.builder()
                        .id(pq.getId())
                        .maPhanQuyen(pq.getMaPhanQuyen())
                        .tenVaiTro(pq.getTenVaiTro())
                        .quyenHan(pq.getQuyenHan())
                        .trangThai(pq.getTrangThai())
                        .build())
                .toList();
    }
}
