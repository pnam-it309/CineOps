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
     * Tạo ra nhiều phiên bản của ảnh dựa trên ĐỘ SẮC NÉT (SHARP)
     * Giúp xử lý đa tầng từ ảnh mờ tịt đến ảnh rõ nét.
     */
    public List<Mat> generateEnhancedVariations(Mat src) {
        List<Mat> variations = new ArrayList<>();

        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);

        // 1. BIẾN THỂ GỐC: Dành cho mã QR chuẩn (in giấy)
        variations.add(src.clone());

        // 2. BIẾN THỂ CHỐNG NHIỄU MÀN HÌNH (Quét từ điện thoại khác)
        // Lọc song phương để xóa vân Moire, sau đó tăng tương phản
        Mat filtered = applyBilateralFilter(gray);
        Mat claheScreen = applyClahe(filtered);
        variations.add(applySharpen(claheScreen));
        filtered.release();
        claheScreen.release();

        // 3. BIẾN THỂ CHỐNG LÓA (Adaptive Threshold)
        // Cực tốt khi thẻ CCCD bị bóng đèn điện chiếu trực tiếp
        Mat adaptive = applyAdaptiveThreshold(gray);
        variations.add(adaptive);

        // 4. BIẾN THỂ SIÊU ZOOM (3.0x): Dành cho mã QR CCCD (1cm)
        Mat zoomedSmall = new Mat();
        Imgproc.resize(gray, zoomedSmall, new Size(), 3.0, 3.0, Imgproc.INTER_LANCZOS4);
        Mat claheSmall = applyClahe(zoomedSmall);
        variations.add(applySuperSharpen(claheSmall));
        zoomedSmall.release();
        claheSmall.release();

        // 5. BIẾN THỂ SIÊU ZOOM + NỐI ĐIỂM (3.5x): Cứu cánh cho mã siêu nhỏ
        Mat zoomedMicro = new Mat();
        Imgproc.resize(gray, zoomedMicro, new Size(), 3.5, 3.5, Imgproc.INTER_LANCZOS4);
        Mat superSmall = applySuperSharpen(zoomedMicro);
        Mat morphed = applyMorphologicalClose(superSmall);
        variations.add(morphed);
        zoomedMicro.release();
        superSmall.release();

        // 6. BIẾN THỂ ĐẢO NGƯỢC (Invert): Dành cho DarkMode
        Mat inverted = new Mat();
        Core.bitwise_not(gray, inverted);
        variations.add(applyClahe(inverted));
        inverted.release();

        // 7. BIẾN THỂ XÓA LOGO (Logo Suppression): Dành cho mã MoMo/Ngân hàng có logo to ở giữa
        // ZXing cực nhạy khi tâm mã QR được làm sạch
        variations.add(applyLogoSuppression(gray));

        // 8. BIẾN THỂ NẮN DÒNG (Top-Hat): Lọc bỏ vùng lóa, chỉ giữ lại khung mã QR
        variations.add(applyTopHatFilter(gray));

        gray.release();
        return variations;
    }

    /**
     * Xóa Logo ở tâm để máy tập trung vào các Finder Patterns
     */
    private Mat applyLogoSuppression(Mat src) {
        Mat dest = src.clone();
        int size = (int) (Math.min(src.width(), src.height()) * 0.25);
        int x = (src.width() - size) / 2;
        int y = (src.height() - size) / 2;
        // Vẽ đè màu trắng lên vùng trung tâm (thường là nơi đặt logo)
        Imgproc.rectangle(dest, new org.opencv.core.Point(x, y), 
                          new org.opencv.core.Point(x + size, y + size), 
                          new org.opencv.core.Scalar(255), -1);
        return dest;
    }

    /**
     * Top-Hat Filter: Cực tốt để xử lý ảnh bị bóng đèn điện chiếu trực tiếp
     */
    private Mat applyTopHatFilter(Mat src) {
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(15, 15));
        Mat dest = new Mat();
        Imgproc.morphologyEx(src, dest, Imgproc.MORPH_TOPHAT, kernel);
        Core.add(src, dest, dest); // Trộn lại để tăng cường độ tương phản cục bộ
        kernel.release();
        return dest;
    }

    private Mat applyGlobalThreshold(Mat src) {
        Mat dest = new Mat();
        Imgproc.threshold(src, dest, 0, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
        return dest;
    }

    private Mat applyErosion(Mat src) {
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 2));
        Mat dest = new Mat();
        Imgproc.erode(src, dest, kernel);
        kernel.release();
        return dest;
    }

    private Mat applyAdaptiveThreshold(Mat src) {
        Mat dest = new Mat();
        Imgproc.adaptiveThreshold(src, dest, 255, 
            Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 21, 10);
        return dest;
    }

    /**
     * Nối các pixel bị rạn nứt nát ra sau khi bị phóng to cực đại
     */
    private Mat applyMorphologicalClose(Mat src) {
        // Dùng Kernel 2x2 hoặc 3x3 để trám các khe nứt nhỏ trong module QR
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 2));
        Mat result = new Mat();
        Imgproc.morphologyEx(src, result, Imgproc.MORPH_CLOSE, kernel);
        kernel.release();
        return result;
    }

    /**
     * Hàm tính độ sắc nét bằng phương pháp Laplacian Variance
     */
    public double calculateSharpness(Mat gray) {
        Mat laplacian = new Mat();
        Imgproc.Laplacian(gray, laplacian, org.opencv.core.CvType.CV_64F);

        org.opencv.core.MatOfDouble mean = new org.opencv.core.MatOfDouble();
        org.opencv.core.MatOfDouble stddev = new org.opencv.core.MatOfDouble();
        Core.meanStdDev(laplacian, mean, stddev);

        double var = Math.pow(stddev.get(0, 0)[0], 2);

        laplacian.release();
        mean.release();
        stddev.release();

        return var;
    }

    private Mat applySuperSharpen(Mat src) {
        Mat kernel = new Mat(3, 3, org.opencv.core.CvType.CV_32F);
        kernel.put(0, 0, 0, -1, 0, -1, 5, -1, 0, -1, 0); // Sharp kernel
        Mat result = new Mat();
        Imgproc.filter2D(src, result, -1, kernel);
        kernel.release();
        return result;
    }

    private Mat applyBilateralFilter(Mat src) {
        Mat result = new Mat();
        Imgproc.bilateralFilter(src, result, 9, 75, 75);
        return result;
    }

    private Mat applyGammaCorrection(Mat src, double gamma) {
        Mat lookUpTable = new Mat(1, 256, org.opencv.core.CvType.CV_8U);
        byte[] lutData = new byte[256];
        for (int i = 0; i < 256; i++) {
            lutData[i] = (byte) (Math.min(255, (int) (Math.pow(i / 255.0, gamma) * 255.0)));
        }
        lookUpTable.put(0, 0, lutData);
        Mat result = new Mat();
        Core.LUT(src, lookUpTable, result);
        lookUpTable.release();
        return result;
    }

    private Mat applyLaplacianSharpen(Mat src) {
        Mat laplacian = new Mat();
        Imgproc.Laplacian(src, laplacian, org.opencv.core.CvType.CV_8U, 3, 1, 0, Core.BORDER_DEFAULT);
        Mat result = new Mat();
        Core.addWeighted(src, 1.5, laplacian, -0.5, 0, result);
        laplacian.release();
        return result;
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
