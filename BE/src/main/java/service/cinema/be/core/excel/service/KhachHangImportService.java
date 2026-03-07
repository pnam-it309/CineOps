package service.cinema.be.core.excel.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import service.cinema.be.entity.KhachHang;
import service.cinema.be.repository.KhachHangRepository;

import java.util.regex.Pattern;

@Slf4j
@Service
public class KhachHangImportService extends BaseImportService<KhachHang, String> {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10,15}$");

    @Autowired
    public KhachHangImportService(@Qualifier("khachHangRepository") KhachHangRepository repository) {
        super(repository);
    }

    @Override
    protected void validateEntity(KhachHang entity, int rowNum) throws IllegalArgumentException {
        if (entity.getTenKhachHang() == null || entity.getTenKhachHang().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên khách hàng không được để trống");
        }
        if (entity.getSdt() != null && !entity.getSdt().trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(entity.getSdt().trim()).matches()) {
                throw new IllegalArgumentException("Số điện thoại không hợp lệ");
            }
        }
        if (entity.getTrangThai() == null) {
            entity.setTrangThai(1);
        }
    }
}
