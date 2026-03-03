import api from '@/services/axios'

const API_URL = "/api/v1/admin/hoa-don"

export const hoaDonService = {
  // 1. Lấy danh sách hóa đơn (có phân trang, bộ lọc)
  getInvoices(params) {
    return api.get(`${API_URL}`, { params })
  },

  // 2. Lấy chi tiết 1 hóa đơn
  getInvoiceDetails(id) {
    return api.get(`${API_URL}/${id}/chi-tiet`)
  },

  // 3. Tạo hóa đơn mới (Thanh toán tại quầy)
  createInvoice(payload) {
    return api.post(`${API_URL}/thanh-toan`, payload)
  }
}