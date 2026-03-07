package service.cinema.be.core.common.response;

import lombok.Data;

@Data
public class CccdDataResponse {
    private String cccd;
    private String name;
    private String dob;
    private String gender;
    private String address;
    private String rawData;
}
