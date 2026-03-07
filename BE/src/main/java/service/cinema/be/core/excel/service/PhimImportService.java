package service.cinema.be.core.excel.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import service.cinema.be.entity.Phim;
import service.cinema.be.repository.PhimRepository;

import java.math.BigDecimal;

@Slf4j
@Service
public class PhimImportService extends BaseImportService<Phim, String> {

    @Autowired
    public PhimImportService(@Qualifier("phimRepository") PhimRepository repository) {
        super(repository);
    }

    @Override
    protected void validateEntity(Phim entity, int rowNum) throws IllegalArgumentException {
        // Custom validation for Phim entity
        
        // Validate movie name is not empty
        if (entity.getTenPhim() == null || entity.getTenPhim().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên phim không được để trống");
        }
        
        // Validate duration is positive
        if (entity.getThoiLuong() != null && entity.getThoiLuong() <= 0) {
            throw new IllegalArgumentException("Thời lượng phim phải lớn hơn 0");
        }
        
        // Validate release date is before end date
        if (entity.getNgayKhoiChieu() != null && entity.getNgayKetThuc() != null) {
            if (entity.getNgayKhoiChieu().isAfter(entity.getNgayKetThuc())) {
                throw new IllegalArgumentException("Ngày khởi chiếu phải trước ngày kết thúc");
            }
        }
        
        // Validate age rating is reasonable
        if (entity.getDoTuoi() != null && (entity.getDoTuoi() < 0 || entity.getDoTuoi() > 100)) {
            throw new IllegalArgumentException("Độ tuổi không hợp lệ (phải từ 0-100)");
        }
        
        // Validate rating is within valid range
        if (entity.getDanhGia() != null) {
            if (entity.getDanhGia() < 0 || entity.getDanhGia() > 10) {
                throw new IllegalArgumentException("Đánh giá phải từ 0-10");
            }
        }
        
        // Validate status is valid (0: sắp chiếu, 1: đang chiếu, 2: kết thúc, 3: ẩn)
        if (entity.getTrangThai() != null && (entity.getTrangThai() < 0 || entity.getTrangThai() > 3)) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ (0: sắp chiếu, 1: đang chiếu, 2: kết thúc, 3: ẩn)");
        }
        
        // Validate movie format if provided
        if (entity.getLoaiPhim() != null) {
            String[] validFormats = {"2D", "3D", "IMAX", "4DX"};
            boolean isValidFormat = false;
            for (String format : validFormats) {
                if (format.equalsIgnoreCase(entity.getLoaiPhim())) {
                    isValidFormat = true;
                    break;
                }
            }
            if (!isValidFormat) {
                throw new IllegalArgumentException("Loại phim không hợp lệ (phải là: 2D, 3D, IMAX, 4DX)");
            }
        }
        
        // Validate prices are non-negative
        if (entity.getGiaPhim() != null && entity.getGiaPhim() < 0) {
            throw new IllegalArgumentException("Giá phim không được âm");
        }
        
        if (entity.getPhuPhiLoaiPhim() != null && entity.getPhuPhiLoaiPhim() < 0) {
            throw new IllegalArgumentException("Phụ phí loại phim không được âm");
        }
        
        log.debug("Validated Phim entity at row {}: {}", rowNum, entity.getTenPhim());
    }
}
