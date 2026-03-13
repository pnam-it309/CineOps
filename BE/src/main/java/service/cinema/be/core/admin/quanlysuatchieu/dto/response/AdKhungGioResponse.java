package service.cinema.be.core.admin.quanlysuatchieu.dto.response;

import lombok.Data;

import java.time.LocalTime;

@Data
public class AdKhungGioResponse {
    private String id;

    private String tenKhungGio;

    private LocalTime gioBatDau;

    private LocalTime gioKetThuc;
}
