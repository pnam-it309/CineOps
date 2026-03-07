package service.cinema.be.core.excel.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import service.cinema.be.entity.Ghe;
import service.cinema.be.repository.GheRepository;

@Slf4j
@Service
public class GheImportService extends BaseImportService<Ghe, String> {

    @Autowired
    public GheImportService(@Qualifier("gheRepository") GheRepository repository) {
        super(repository);
    }

    @Override
    protected void validateEntity(Ghe entity, int rowNum) throws IllegalArgumentException {
        if (entity.getSoGhe() == null || entity.getSoGhe().trim().isEmpty()) {
            throw new IllegalArgumentException("Số ghế không được để trống");
        }
        if (entity.getSoHang() == null || entity.getSoHang().trim().isEmpty()) {
            throw new IllegalArgumentException("Hàng không được để trống");
        }
        if (entity.getSoCot() == null) {
            throw new IllegalArgumentException("Cột không được để trống");
        }
    }
}
