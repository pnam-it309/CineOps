package service.cinema.be.core.excel.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import service.cinema.be.entity.NhanVien;
import service.cinema.be.repository.NhanVienRepository;

import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

@Slf4j
@Service
public class NhanVienImportService extends BaseImportService<NhanVien, String> {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[0-9]{10,15}$"
    );
    
    private static final Pattern CCCD_PATTERN = Pattern.compile(
        "^[0-9]{12}$"
    );

    @Autowired
    public NhanVienImportService(@Qualifier("nhanVienRepository") NhanVienRepository repository) {
        super(repository);
    }

    @Override
    protected void validateEntity(NhanVien entity, int rowNum) throws IllegalArgumentException {
        // Custom validation for NhanVien entity
        
        // Validate employee code is not empty
        if (entity.getMaNhanVien() == null || entity.getMaNhanVien().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã nhân viên không được để trống");
        }
        
        // Validate employee name is not empty
        if (entity.getTenNhanVien() == null || entity.getTenNhanVien().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên nhân viên không được để trống");
        }
        
        // Validate CCCD format if provided
        if (entity.getCccd() != null && !entity.getCccd().trim().isEmpty()) {
            if (!CCCD_PATTERN.matcher(entity.getCccd().trim()).matches()) {
                throw new IllegalArgumentException("CCCD phải có 12 chữ số");
            }
        }
        
        // Validate birth date if provided
        if (entity.getNgaySinh() != null) {
            LocalDate today = LocalDate.now();
            if (entity.getNgaySinh().isAfter(today)) {
                throw new IllegalArgumentException("Ngày sinh không được lớn hơn ngày hiện tại");
            }
            
            // Validate age is reasonable (between 16 and 70)
            int age = Period.between(entity.getNgaySinh(), today).getYears();
            if (age < 16 || age > 70) {
                throw new IllegalArgumentException("Độ tuổi nhân viên không hợp lệ (phải từ 16-70 tuổi)");
            }
        }
        
        // Validate gender if provided (0: Nữ, 1: Nam, 2: Khác)
        if (entity.getGioiTinh() != null && (entity.getGioiTinh() < 0 || entity.getGioiTinh() > 2)) {
            throw new IllegalArgumentException("Giới tính không hợp lệ (0: Nữ, 1: Nam, 2: Khác)");
        }
        
        // Validate phone number format if provided
        if (entity.getSoDienThoai() != null && !entity.getSoDienThoai().trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(entity.getSoDienThoai().trim()).matches()) {
                throw new IllegalArgumentException("Số điện thoại phải có 10-15 chữ số");
            }
        }
        
        // Validate status is valid (0: inactive, 1: active)
        if (entity.getTrangThai() != null && (entity.getTrangThai() < 0 || entity.getTrangThai() > 1)) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ (0: inactive, 1: active)");
        }
        
        log.debug("Validated NhanVien entity at row {}: {}", rowNum, entity.getTenNhanVien());
    }
}
