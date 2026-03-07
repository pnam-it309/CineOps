package service.cinema.be.core.excel.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import service.cinema.be.entity.SanPhamDichVu;
import service.cinema.be.repository.SanPhamDichVuRepository;

@Slf4j
@Service
public class SanPhamDichVuImportService extends BaseImportService<SanPhamDichVu, String> {

    @Autowired
    public SanPhamDichVuImportService(@Qualifier("sanPhamDichVuRepository") SanPhamDichVuRepository repository) {
        super(repository);
    }

    @Override
    protected void validateEntity(SanPhamDichVu entity, int rowNum) throws IllegalArgumentException {
        if (entity.getTenSanPham() == null || entity.getTenSanPham().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống");
        }
    }
}
