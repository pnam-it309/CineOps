/**
 * High-performance Computer Vision Worker for QR Scanning Pre-processing
 * - Real-time Blur Detection (Laplacian Variance)
 * - Basic Contrast Normalization
 */

self.onmessage = async (e) => {
    const { imageData, width, height, threshold } = e.data;
    
    // 1. Convert to Grayscale
    const gray = toGrayscale(imageData.data, width, height);
    
    // 2. Laplacian Variance (Blur Detection)
    // Kernel: 
    // [0,  1, 0]
    // [1, -4, 1]
    // [0,  1, 0]
    const score = calculateLaplacianVariance(gray, width, height);
    
    // 3. Return result
    self.postMessage({
        isSharp: score > (threshold || 10),
        score: score
    });
};

function toGrayscale(data, width, height) {
    const gray = new Uint8ClampedArray(width * height);
    for (let i = 0, j = 0; i < data.length; i += 4, j++) {
        // Human eye Luma formula
        gray[j] = (data[i] * 0.299 + data[i + 1] * 0.587 + data[i + 2] * 0.114);
    }
    return gray;
}

function calculateLaplacianVariance(gray, w, h) {
    let mean = 0;
    const laplacian = new Float32Array(w * h);
    
    // Apply 3x3 Laplacian operator
    for (let y = 1; y < h - 1; y++) {
        for (let x = 1; x < w - 1; x++) {
            const idx = y * w + x;
            const val = (
                gray[idx - w] + 
                gray[idx - 1] + 
                gray[idx + 1] + 
                gray[idx + w] - 
                4 * gray[idx]
            );
            laplacian[idx] = val;
            mean += val;
        }
    }
    mean /= (w * h);
    
    // Variance
    let variance = 0;
    for (let i = 0; i < laplacian.length; i++) {
        variance += Math.pow(laplacian[i] - mean, 2);
    }
    return variance / (w * h);
}
