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

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CccdScanService {

    private final QrImageProcessor qrImageProcessor;
    private QRCodeDetector qrDetector;

    @PostConstruct
    public void init() {
        try {
            nu.pattern.OpenCV.loadShared();
            qrDetector = new QRCodeDetector();
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

            // Gọi AI tiền xử lý để sinh ra các phiên bản ảnh (kể cả ảnh đã được zoom to)
            List<Mat> variations = qrImageProcessor.generateEnhancedVariations(src);

            // Duyệt qua từng phiên bản ảnh, ảnh nào nhỏ sẽ được đọc ở vòng lặp zoom
            for (Mat mat : variations) {
                if (qrContent != null) break;

                // 1. Thử bằng OpenCV
                if (qrDetector != null) {
                    try {
                        String result = qrDetector.detectAndDecode(mat);
                        if (result != null && !result.trim().isEmpty()) {
                            qrContent = result;
                            break;
                        }
                    } catch (Exception ignored) {}
                }

                // 2. Thử bằng ZXing (ZXing cực kỳ thích ảnh được zoom to)
                if (qrContent == null) {
                    String zxingResult = scanWithZXing(mat);
                    if (zxingResult != null && !zxingResult.trim().isEmpty()) {
                        qrContent = zxingResult;
                        break;
                    }
                }
            }

            // Dọn dẹp RAM
            for (Mat mat : variations) {
                if (mat != null && !mat.empty()) mat.release();
            }

            if (qrContent == null || qrContent.trim().isEmpty()) {
                throw new RuntimeException("Không phát hiện mã QR. Đã thử phóng to nhưng không thành công.");
            }

            return parseCccdData(qrContent);

        } catch (Exception e) {
            throw new Exception("Lỗi xử lý mã QR: " + e.getMessage());
        } finally {
            if (src != null) src.release();
        }
    }

    private String scanWithZXing(Mat enhanced) {
        try {
            MatOfByte mob = new MatOfByte();
            Imgcodecs.imencode(".png", enhanced, mob);
            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(mob.toArray());
            java.awt.image.BufferedImage img = javax.imageio.ImageIO.read(bais);

            com.google.zxing.LuminanceSource source = new com.google.zxing.client.j2se.BufferedImageLuminanceSource(img);
            com.google.zxing.BinaryBitmap hybridBitmap = new com.google.zxing.BinaryBitmap(new com.google.zxing.common.HybridBinarizer(source));

            Map<com.google.zxing.DecodeHintType, Object> hints = new EnumMap<>(com.google.zxing.DecodeHintType.class);
            hints.put(com.google.zxing.DecodeHintType.TRY_HARDER, Boolean.TRUE);
            hints.put(com.google.zxing.DecodeHintType.CHARACTER_SET, "UTF-8");

            // Cấu hình thêm để quét các mã QR bị lộn ngược hoặc méo
            hints.put(com.google.zxing.DecodeHintType.ALSO_INVERTED, Boolean.TRUE);

            try {
                return new com.google.zxing.MultiFormatReader().decode(hybridBitmap, hints).getText();
            } catch (com.google.zxing.NotFoundException e) {
                // Fallback tĩnh: Cứu cánh khi binarizer phức tạp của ZXing bị tịt do chói
                com.google.zxing.BinaryBitmap globalBitmap = new com.google.zxing.BinaryBitmap(new com.google.zxing.common.GlobalHistogramBinarizer(source));
                return new com.google.zxing.MultiFormatReader().decode(globalBitmap, hints).getText();
            }
        } catch (Exception e) {
            return null;
        }
    }

    private CccdDataResponse parseCccdData(String text) {
        // Giữ nguyên logic parse dữ liệu của bạn
        String[] parts = text.split("\\|");
        CccdDataResponse response = new CccdDataResponse();
        response.setRawData(text);

        if (parts.length >= 6) {
            response.setCccd(parts[0]);
            response.setName(parts[2]);
            String rDob = parts[3];
            if (rDob != null && rDob.length() == 8) {
                String d = rDob.substring(0, 2);
                String m = rDob.substring(2, 4);
                String y = rDob.substring(4, 8);
                response.setDob(y + "-" + m + "-" + d);
            }
            response.setGender(parts[4]);
            response.setAddress(parts[5]);
        }
        return response;
    }
}