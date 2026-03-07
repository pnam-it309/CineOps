package service.cinema.be.core.common.service;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.CLAHE;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class QrImageProcessor {

    /**
     * Tạo ra nhiều phiên bản của ảnh (Phóng to, làm nét, giảm chói)
     * Để quét các CCCD ở xa, mã QR siêu nhỏ.
     */
    public List<Mat> generateEnhancedVariations(Mat src) {
        List<Mat> variations = new ArrayList<>();

        // 1. Ảnh gốc (Phòng trường hợp ảnh đã hoàn hảo)
        variations.add(src.clone());

        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
        variations.add(gray.clone());

        // 2. Chống chói (CLAHE) - Cân bằng histogram hiệu quả vùng sáng tối
        Mat claheMat = applyClahe(gray);
        variations.add(claheMat.clone());

        // 3. Khử màng phản quang / bóng chói bằng hình thái học (BlackHat/TopHat logic)
        // Kéo nổi các chấm đen (QR modules) trên nền thẻ sáng bị chói
        Mat highlightedDots = applyBlackHatEnhancement(claheMat);
        variations.add(highlightedDots.clone());
        
        // 4. Binarization cứng (Otsu) trực tiếp
        Mat otsu = new Mat();
        Imgproc.threshold(highlightedDots, otsu, 0, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
        variations.add(otsu);

        // 5. CHIẾN LƯỢC ZOOM (Cứu cánh cho CCCD để xa)
        // Zoom vừa phải để không làm nát nét, sau đó làm nét lại!
        double[] scales = {1.5, 2.0};

        for (double scale : scales) {
            Mat zoomed = new Mat();
            Imgproc.resize(gray, zoomed, new Size(), scale, scale, Imgproc.INTER_CUBIC);

            Mat zoomedClahe = applyClahe(zoomed);
            variations.add(zoomedClahe.clone());

            // Phóng to xong làm nét (Sharpen) 
            Mat sharpened = applySharpen(zoomedClahe);
            variations.add(sharpened.clone());

            // Adaptive Threshold cho các đường viền khó đọc
            Mat adaptive = new Mat();
            Imgproc.adaptiveThreshold(sharpened, adaptive, 255,
                    Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 15, 5);
            variations.add(adaptive);

            zoomed.release();
            zoomedClahe.release();
        }

        gray.release();
        claheMat.release();
        return variations;
    }

    private Mat applyBlackHatEnhancement(Mat src) {
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(15, 15));
        Mat blackHat = new Mat();
        Imgproc.morphologyEx(src, blackHat, Imgproc.MORPH_BLACKHAT, kernel);
        Mat result = new Mat();
        Core.subtract(src, blackHat, result);
        kernel.release();
        blackHat.release();
        return result;
    }

    private Mat applyClahe(Mat graySrc) {
        CLAHE clahe = Imgproc.createCLAHE(2.0, new Size(8, 8));
        Mat result = new Mat();
        clahe.apply(graySrc, result);
        return result;
    }

    private Mat applySharpen(Mat src) {
        Mat blurred = new Mat();
        Imgproc.GaussianBlur(src, blurred, new Size(0, 0), 3);
        Mat sharpened = new Mat();
        Core.addWeighted(src, 1.5, blurred, -0.5, 0, sharpened);
        blurred.release();
        return sharpened;
    }
}
