package service.cinema.be.core.excel.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import service.cinema.be.entity.SuatChieu;
import service.cinema.be.repository.SuatChieuRepository;

@Slf4j
@Service
public class SuatChieuImportService extends BaseImportService<SuatChieu, String> {

    @Autowired
    public SuatChieuImportService(@Qualifier("suatChieuRepository") SuatChieuRepository repository) {
        super(repository);
    }

    @Override
    protected void validateEntity(SuatChieu entity, int rowNum) throws IllegalArgumentException {
        if (entity.getNgayChieu() == null) {
            throw new IllegalArgumentException("Ngày chiếu không được để trống");
        }
        if (entity.getGioBatDau() == null) {
            throw new IllegalArgumentException("Giờ bắt đầu không được để trống");
        }
        if (entity.getGioKetThuc() == null) {
            throw new IllegalArgumentException("Giờ kết thúc không được để trống");
        }
        if (entity.getGioKetThuc().isBefore(entity.getGioBatDau())) {
            throw new IllegalArgumentException("Giờ kết thúc phải sau giờ bắt đầu");
        }
    }
}
