package service.cinema.be.core.common;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.cinema.be.core.common.response.ApiResponse;

import javax.sql.DataSource;
import java.sql.Connection;

@RestController
@RequiredArgsConstructor
public class CommonController {

    private final DataSource dataSource;

    @GetMapping("/")
    public String home() {
        return "CineOps API is running";
    }

    @GetMapping("/api/v1/common/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("UP", "System is healthy"));
    }

    @GetMapping("/api/v1/common/db-ping")
    public ResponseEntity<ApiResponse<String>> dbPing() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(2)) {
                return ResponseEntity.ok(ApiResponse.success("Database is connectable"));
            } else {
                return ResponseEntity.status(500).body(ApiResponse.error(500, "Database connection is invalid"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error(500, "Database error: " + e.getMessage()));
        }
    }
}
