package service.cinema.be.core.common.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import service.cinema.be.core.common.response.CccdDataResponse;
import org.opencv.objdetect.QRCodeDetector;
// Xóa import cứng để tránh lỗi compile nếu thư viện không có contrib
// import org.opencv.wechat_qrcode.WeChatQRCode; 

import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.EnumMap;

@Service
@RequiredArgsConstructor
public class CccdScanService {

    private final QrImageProcessor qrImageProcessor;
    private QRCodeDetector qrDetector;
    private Object weChatDetector; // Dùng Object để tránh lỗi compile nếu thiếu thư viện Contrib
    private java.lang.reflect.Method weChatDecodeMethod;

    @PostConstruct
    public void init() {
        try {
            nu.pattern.OpenCV.loadShared();
            qrDetector = new QRCodeDetector();

            // Khởi tạo WeChatQRCode bằng Reflection (Chống lỗi compile)
            try {
                Class<?> clazz = Class.forName("org.opencv.wechat_qrcode.WeChatQRCode");
                String modelDir = "opencv_models/"; // Cần: detect.prototxt, detect.caffemodel, sr.prototxt, sr.caffemodel
                
                java.io.File dir = new java.io.File(modelDir);
                if (dir.exists() && dir.isDirectory()) {
                    weChatDetector = clazz.getConstructor(String.class, String.class, String.class, String.class)
                        .newInstance(
                            modelDir + "detect.prototxt", modelDir + "detect.caffemodel",
                            modelDir + "sr.prototxt", modelDir + "sr.caffemodel"
                        );
                    weChatDecodeMethod = clazz.getMethod("detectAndDecode", Mat.class);
                    System.out.println("[SUCCESS] WeChatQRCode initialized via Reflection.");
                } else {
                    System.out.println("[INFO] WeChatQRCode models folder NOT FOUND at: " + dir.getAbsolutePath());
                }
            } catch (Exception e) {
                System.out.println("[INFO] WeChatQRCode library not present in this OpenCV build.");
            }

        } catch (Exception e) {
            System.err.println("OpenCV native library failed to load.");
        }
    }

    public CccdDataResponse scanCccdQr(MultipartFile file) throws Exception {
        Mat src = null;
        try {
            byte[] bytes = file.getBytes();
            src = Imgcodecs.imdecode(new MatOfByte(bytes), Imgcodecs.IMREAD_COLOR);
            if (src.empty()) throw new IllegalArgumentException("Khung hình rỗng hoặc không hợp lệ.");

            String qrContent = null;
            
            // BƯỚC 1: QUÉT LƯỚT (SHORT-CIRCUIT) - Thử ngay trên Ảnh Gốc (Variation 1)
            // Thời gian: < 10ms. Nếu được, bỏ qua toàn bộ xử lý AI nặng nề phía sau.
            qrContent = tryScanAllDetectors(src, 1, true);
            if (qrContent != null) {
                System.out.println("[INSTANT-SUCCESS] QR found on Original frame in " + (System.currentTimeMillis() - System.currentTimeMillis()) + " ms");
            } else {
                // Chỉ nếu Ảnh Gốc thất bại mới kích hoạt bộ xử lý AI (Tốn CPU/RAM)
                List<Mat> variations = qrImageProcessor.generateEnhancedVariations(src);

                // BƯỚC 2: QUÉT NHANH (FAST-TRACK) - Các biến thể "vô địch" còn lại
                int[] fastIndices = {1, 6, 7}; // Index 1 (Màn hình), 6 (Xóa Logo), 7 (Top-Hat)
                for (int idx : fastIndices) {
                    if (idx < variations.size()) {
                        qrContent = tryScanAllDetectors(variations.get(idx), idx + 1, true);
                        if (qrContent != null) {
                            System.out.println("[FAST-PATH] QR found on variation " + (idx + 1));
                            break;
                        }
                    }
                }

                // BƯỚC 3: QUÉT ĐA LUỒNG (PARALLEL) - Luồng siêu nặng
                if (qrContent == null) {
                    CompletableFuture<String> winner = new CompletableFuture<>();
                    AtomicBoolean isDone = new AtomicBoolean(false);
                    
                    List<CompletableFuture<?>> futuresList = new ArrayList<>();
                    for (int i = 0; i < variations.size(); i++) {
                        // Bỏ qua các index đã quét ở FAST-TRACK & SHORT-CIRCUIT
                        if (i == 0 || i == 1 || i == 6 || i == 7) continue;

                        final Mat mat = variations.get(i);
                        final int index = i + 1;
                        
                        futuresList.add(CompletableFuture.runAsync(() -> {
                            if (isDone.get()) return;
                            String result = tryScanAllDetectors(mat, index, false);
                            if (result != null && isDone.compareAndSet(false, true)) {
                                winner.complete(result);
                            }
                        }));
                    }

                    if (!futuresList.isEmpty()) {
                        CompletableFuture.allOf(futuresList.toArray(new CompletableFuture[0]))
                            .thenRun(() -> { if (!isDone.get()) winner.complete(null); });
                        
                        try { qrContent = winner.get(1500, TimeUnit.MILLISECONDS); } // Giảm xuống 1.5s
                        catch (Exception e) { qrContent = null; }
                    }
                }

                // Dọn dẹp RAM các biến thể
                for (Mat mat : variations) {
                    if (mat != null && !mat.empty()) mat.release();
                }
            }

            if (qrContent == null || qrContent.trim().isEmpty()) {
                throw new RuntimeException("Không phát hiện mã QR. Đã thử phóng to nhưng không thành công.");
            }

            System.out.println("[RESULT] Raw QR Content: " + qrContent);
            return parseCccdData(qrContent);

        } catch (Exception e) {
            throw new Exception("Lỗi xử lý mã QR: " + e.getMessage());
        } finally {
            if (src != null) src.release();
        }
    }

    private String tryScanAllDetectors(Mat mat, int variationIndex, boolean preferOpenCV) {
        long start = System.currentTimeMillis();
        
        // Nếu ưu tiên OpenCV (dành cho Fast-track)
        if (preferOpenCV && qrDetector != null) {
            try {
                String result = qrDetector.detectAndDecode(mat);
                if (result != null && !result.trim().isEmpty()) {
                    System.out.println(String.format("[FAST-WIN] Variation %d found via OpenCV in %d ms", variationIndex, (System.currentTimeMillis() - start)));
                    return result;
                }
            } catch (Exception ignored) {}
        }

        // 1. Thử ZXing (Mạnh nhất cho CCCD pipe-format)
        String zxingResult = scanWithZXing(mat);
        if (zxingResult != null && !zxingResult.trim().isEmpty()) {
            System.out.println(String.format("[WINNER] Variation %d found QR via ZXing in %d ms", variationIndex, (System.currentTimeMillis() - start)));
            return zxingResult;
        }

        // 2. Thử WeChatQRCode (Nếu có)
        if (weChatDetector != null && weChatDecodeMethod != null) {
            try {
                @SuppressWarnings("unchecked")
                List<String> results = (List<String>) weChatDecodeMethod.invoke(weChatDetector, mat);
                if (results != null && !results.isEmpty()) {
                    System.out.println(String.format("[WINNER] Variation %d found QR via WeChat in %d ms", variationIndex, (System.currentTimeMillis() - start)));
                    return results.get(0);
                }
            } catch (Exception ignored) {}
        }

        // 3. Thử OpenCV (Nếu chưa thử ở trên)
        if (!preferOpenCV && qrDetector != null) {
            try {
                String result = qrDetector.detectAndDecode(mat);
                if (result != null && !result.trim().isEmpty()) {
                    System.out.println(String.format("[WINNER] Variation %d found QR via OpenCV in %d ms", variationIndex, (System.currentTimeMillis() - start)));
                    return result;
                }
            } catch (Exception ignored) {}
        }
        return null;
    }
    private String scanWithZXing(Mat enhanced) {
        try {
            MatOfByte mob = new MatOfByte();
            Imgcodecs.imencode(".png", enhanced, mob);
            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(mob.toArray());
            java.awt.image.BufferedImage img = javax.imageio.ImageIO.read(bais);

            com.google.zxing.LuminanceSource source = new com.google.zxing.client.j2se.BufferedImageLuminanceSource(img);

            Map<com.google.zxing.DecodeHintType, Object> hints = new EnumMap<>(com.google.zxing.DecodeHintType.class);
            hints.put(com.google.zxing.DecodeHintType.TRY_HARDER, Boolean.TRUE);
            hints.put(com.google.zxing.DecodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(com.google.zxing.DecodeHintType.ALSO_INVERTED, Boolean.TRUE);
            // Thêm PURE_BARCODE để tối ưu cho ảnh đã được zoom và crop cực nét
            hints.put(com.google.zxing.DecodeHintType.PURE_BARCODE, Boolean.FALSE); 

            com.google.zxing.NotFoundException firstError = null;
            com.google.zxing.MultiFormatReader reader = new com.google.zxing.MultiFormatReader();

            // 1. Thử HybridBinarizer trước (tốt cho ảnh bị bóng lóa không đều, zoom lớn)
            try {
                com.google.zxing.BinaryBitmap hybridBitmap = new com.google.zxing.BinaryBitmap(new com.google.zxing.common.HybridBinarizer(source));
                return reader.decode(hybridBitmap, hints).getText();
            } catch (com.google.zxing.NotFoundException e) {
                firstError = e;
            }

            // 2. Dự phòng bằng GlobalHistogramBinarizer (phù hợp ảnh đen trắng rõ ràng, ít nhiễu cục bộ)
            try {
                com.google.zxing.BinaryBitmap globalBitmap = new com.google.zxing.BinaryBitmap(new com.google.zxing.common.GlobalHistogramBinarizer(source));
                return new com.google.zxing.MultiFormatReader().decode(globalBitmap, hints).getText();
            } catch (com.google.zxing.NotFoundException e) {
                // Nếu cả 2 đều không được, quăng NotFoundException cuối
                throw firstError != null ? firstError : e;
            }

        } catch (Exception e) {
            return null;
        }
    }

    private CccdDataResponse parseCccdData(String text) {
        CccdDataResponse response = new CccdDataResponse();
        response.setRawData(text);
        
        if (text == null || text.trim().isEmpty()) return response;

        // Xử lý cả trường hợp text có hoặc không có dấu |
        String[] parts = text.split("\\|");
        
        if (parts.length >= 6) {
            response.setCccd(parts[0]);
            response.setName(parts[2]);
            String rDob = parts[3];
            if (rDob != null && rDob.length() >= 8) {
                // Đề phòng chuỗi ngày sinh có thể dài hơn
                String d = rDob.substring(0, 2);
                String m = rDob.substring(2, 4);
                String y = rDob.substring(4, 8);
                response.setDob(y + "-" + m + "-" + d);
            }
            response.setGender(parts[4]);
            
            // Lấy địa chỉ raw
            String fullAddress = "";
            if (parts.length == 7) {
                fullAddress = parts[5];
            } else {
                StringBuilder addr = new StringBuilder();
                int endLimit = (parts.length >= 7) ? parts.length - 1 : parts.length;
                for (int i = 5; i < endLimit; i++) {
                    addr.append(parts[i]);
                    if (i < endLimit - 1) addr.append(", ");
                }
                fullAddress = addr.toString();
            }
            
            // Lưu địa chỉ raw
            response.setAddress(fullAddress);
            
            // Parse địa chỉ thành các trường (Tỉnh, Xã) theo yêu cầu: KHÔNG CÒN HUYỆN
            // Định dạng raw thường là: [Số nhà], [Xã], [Huyện], [Tỉnh]
            if (fullAddress != null && !fullAddress.isEmpty()) {
                String[] addrParts = fullAddress.split(",\\s*");
                if (addrParts.length >= 2) {
                    // Lấy Tỉnh là phần cuối cùng
                    response.setProvince(addrParts[addrParts.length - 1].trim());
                    // Lấy Xã là phần ngay trước Tỉnh (Dù part đó có là Huyện hay Xã, ta coi là Xã như yêu cầu)
                    response.setWard(addrParts[addrParts.length - 2].trim());
                    // District để trống vì "không còn huyện" theo lời người dùng
                    response.setDistrict(null);
                } else if (addrParts.length == 1) {
                    response.setProvince(addrParts[0].trim());
                }
            }
        } else {
            // Nếu không đúng định dạng CCCD chuẩn, vẫn trả về dữ liệu thô vào trường Name để hiển thị đỡ
            response.setCccd("UNKNOWN");
            response.setName("QR Data: " + text);
        }
        return response;
    }
}