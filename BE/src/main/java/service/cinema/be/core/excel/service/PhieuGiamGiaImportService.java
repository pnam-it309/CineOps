package service.cinema.be.core.excel.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import service.cinema.be.entity.PhieuGiamGia;
import service.cinema.be.repository.PhieuGiamGiaRepository;

@Slf4j
@Service
public class PhieuGiamGiaImportService extends BaseImportService<PhieuGiamGia, String> {

    @Autowired
    public PhieuGiamGiaImportService(@Qualifier("phieuGiamGiaRepository") PhieuGiamGiaRepository repository) {
        super(repository);
    }

    @Override
    protected void validateEntity(PhieuGiamGia entity, int rowNum) throws IllegalArgumentException {
        if (entity.getMaPhieuGiamGia() == null || entity.getMaPhieuGiamGia().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phiếu giảm giá không được để trống");
        }
        if (entity.getTenPhieu() == null || entity.getTenPhieu().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên phiếu không được để trống");
        }
        if (entity.getNgayBatDau() != null && entity.getNgayKetThuc() != null) {
            if (entity.getNgayKetThuc().isBefore(entity.getNgayBatDau())) {
                throw new IllegalArgumentException("Ngày kết thúc phải sau ngày bắt đầu");
            }
        }
    }
}
