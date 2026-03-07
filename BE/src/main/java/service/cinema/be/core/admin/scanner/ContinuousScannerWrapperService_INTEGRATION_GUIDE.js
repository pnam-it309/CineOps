// /**
//  * Integration Guide cho Continuous Scanner System
//  * Hướng dẫn tích hợp với API hiện có của bạn
//  */
//
// package service.cinema.be.core.admin.scanner;
//
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;
//
// // IMPORT SERVICE HIỆN CÓ CỦA BẠN
// // Ví dụ:
// // import service.cinema.be.service.CccdScanService;
// // import service.cinema.be.dto.CccdScanResult;
//
// @Slf4j
// @Service
// @RequiredArgsConstructor
// public class ContinuousScannerWrapperService {
//
//     // INJECT SERVICE HIỆN CỦA BẠN
//     // Ví dụ:
//     // private final CccdScanService existingCccdScanService;
//
//     // ... (code hiện có từ file trước)
//
//     /**
//      * Gọi đến API scan hiện tại (không sửa đổi)
//      * INTEGRATION POINT - CHỈN SỬA ĐOẠN NÀY
//      */
//     private ResponseEntity<?> callExistingScanAPI(MultipartFile file) {
//         try {
//             // OPTION 1: Nếu service hiện tại trả về Response Entity trực tiếp
//             /*
//             CccdScanResult result = existingCccdScanService.scanCccd(file);
//             return ResponseEntity.ok(ApiResponse.success(result, "Quét thành công"));
//             */
//
//             // OPTION 2: Nếu service hiện tại trả về DTO
//             /*
//             CccdScanDTO dto = existingCccdScanService.processCccdImage(file);
//             return ResponseEntity.ok(ApiResponse.success(dto, "Quét thành công"));
//             */
//
//             // OPTION 3: Nếu service hiện tại có controller riêng
//             /*
//             return existingCccdController.scanCccd(file);
//             */
//
//             // OPTION 4: Nếu service hiện tại trả về plain object
//             /*
//             Object result = existingCccdScanService.scan(file);
//             return ResponseEntity.ok(ApiResponse.success(result, "Quét thành công"));
//             */
//
//             // OPTION 5: Nếu có exception handling riêng
//             /*
//             try {
//                 Object result = existingCccdScanService.scanCccd(file);
//                 return ResponseEntity.ok(ApiResponse.success(result, "Quét thành công"));
//             } catch (QrNotFoundException e) {
//                 return ResponseEntity.badRequest().body(
//                     ApiResponse.error("Không tìm thấy mã QR trên CCCD")
//                 );
//             } catch (InvalidImageException e) {
//                 return ResponseEntity.badRequest().body(
//                     ApiResponse.error("Ảnh không hợp lệ: " + e.getMessage())
//                 );
//             }
//             */
//
//             // OPTION 6: Nếu service hiện tại trả về String JSON
//             /*
//             String jsonResult = existingCccdScanService.scanToJson(file);
//             Object parsedResult = parseJson(jsonResult);
//             return ResponseEntity.ok(ApiResponse.success(parsedResult, "Quét thành công"));
//             */
//
//             // === CHỌN MỘT TRONG CÁC OPTION TRÊN VÀ COMMENT CÁC OPTION KHÁC ===
//
//             // Tạm thời throw exception để nhắc nhở tích hợp
//             throw new UnsupportedOperationException(
//                 "VUI LÒNG TÍCH HỢP SERVICE HIỆN CỠ CỦA BẠN TẠI ĐÂY!\n\n" +
//                 "Các bước tích hợp:\n" +
//                 "1. Uncomment @Autowired cho service hiện tại\n" +
//                 "2. Chọn một OPTION phù hợp ở trên\n" +
//                 "3. Comment/remove throw này\n" +
//                 "4. Test lại với frontend\n\n" +
//                 "Example:\n" +
//                 "@Autowired\n" +
//                 "private YourCccdScanService existingService;\n" +
//                 "// Sau đó gọi: existingService.scanCccd(file)"
//             );
//
//         } catch (Exception e) {
//             log.error("Error calling existing scan API: {}", e.getMessage(), e);
//             throw e; // Re-throw để xử lý ở tầng trên
//         }
//     }
//
//     /**
//      * Helper method để parse JSON nếu cần (OPTION 6)
//      */
//     private Object parseJson(String jsonResult) {
//         try {
//             // Sử dụng Jackson hoặc Gson để parse
//             com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
//             return mapper.readValue(jsonResult, Object.class);
//         } catch (Exception e) {
//             log.error("Error parsing JSON result: {}", e.getMessage());
//             return jsonResult; // Return as string if parsing fails
//         }
//     }
// }
