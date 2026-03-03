import api from '@/services/axios'

const API_URL = "/api/v1/admin/ve"

export const adVeService = {
  timKiemVe(params) {
    return api.get(`${API_URL}/tim-kiem`, { params })
  },

  datVeMoi(payload) {
    return api.post(`${API_URL}/dat-ve`, payload)
  },

  getThongKe() {
    return api.get(`${API_URL}/thong-ke-tong-quan`)
  },

  huyVe(id) {
    return api.delete(`${API_URL}/${id}/huy`)
  }
}