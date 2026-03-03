import api from '@/services/axios'

const API_URL = "/api/v1/admin/hoa-don"

export const hoaDonService = {
  // Cập nhật đường dẫn /tim-kiem để khớp với Controller
  getInvoices(params) {
    const cleanParams = Object.fromEntries(
      Object.entries(params).filter(([_, v]) => v !== null && v !== '')
    );
    return api.get(`${API_URL}/tim-kiem`, { params: cleanParams })
  },

  getInvoiceDetails(id) {
    return api.get(`${API_URL}/${id}/chi-tiet`)
  },

  createInvoice(payload) {
    return api.post(`${API_URL}/thanh-toan`, payload)
  }
}