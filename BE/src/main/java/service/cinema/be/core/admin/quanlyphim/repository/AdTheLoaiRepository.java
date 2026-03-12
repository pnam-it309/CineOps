package service.cinema.be.core.admin.quanlyphim.repository;

import service.cinema.be.repository.TheLoaiRepository;

public interface AdTheLoaiRepository extends TheLoaiRepository {
    // Kiểm tra tồn tại theo tên, không phân biệt hoa thường
    boolean existsByTenTheLoaiIgnoreCase(String tenTheLoai);
}
