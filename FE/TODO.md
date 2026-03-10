# TODO: Fix Invoice Detail Page - Display Product Name and Movie Name

## Status: COMPLETED ✓

## Backend API Response Structure:
Dựa trên backend code bạn cung cấp, API trả về:
```java
public class AdHoaDonChiTietResponse {
    private String id;
    private Integer loai; // 0: Vé, 1: Đồ ăn
    private String tenPhim;
    private String tenPhongChieu;
    private String viTriGhe;
    private Integer soLuong;
    private BigDecimal donGia;
    private BigDecimal thanhTien;
    private String tenSanPham;
    private String maMuc; // Mã vé hoặc mã SP
    private BigDecimal giamGia;
}
```

## Files Modified:
1. `src/pages/admin/quanlyhoadon/AdminInvoices.vue` - Modal chi tiết hóa đơn
2. `src/pages/admin/quanlyhoadon/VatInvoiceTemplate.vue` - Template in VAT
3. `src/pages/admin/quanlyhoadon/InvoicePrint.vue` - Template in đơn giản

## Display Logic Applied (Matching Backend):

**Vé xem phim (loai == 0):**
- Tên: `item.tenPhim`
- Ghế: `item.viTriGhe`
- Phòng: `item.tenPhongChieu`
- Mã vé: `item.maMuc`

**Sản phẩm (loai == 1):**
- Tên: `item.tenSanPham`
- Mã SP: `item.maMuc`

Frontend giờ đây sẽ hiển thị đúng thông tin tên phim và sản phẩm theo cấu trúc API backend.

