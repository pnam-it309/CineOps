import axios from '@/services/axios';
import {
  API_ADMIN_SUAT_CHIEU,
  API_ADMIN_PHONG_CHIEU_DROP,
  API_PHIM_DROP
} from '@/constants/apiEndpoints';

export const suatChieuService = {
  // Lấy danh sách phim cho dropdown
  getPhimDropdown() {
    return axios.get(`${API_ADMIN_SUAT_CHIEU}/phim-dropdown`);
  },

  // Lấy danh sách phòng chiếu cho dropdown
  getPhongChieuDropdown() {
    return axios.get(`${API_ADMIN_SUAT_CHIEU}/phong-chieu-dropdown`);
  },

  // Lấy danh sách suất chiếu (có lọc theo ngày/phòng)
  getShowtimes(params = {}) {
    return axios.get(API_ADMIN_SUAT_CHIEU, { params });
  },

  // Thêm suất chiếu mới
  createShowtime(showtimeData) {
    return axios.post(API_ADMIN_SUAT_CHIEU, showtimeData);
  },

  // Cập nhật suất chiếu
  updateShowtime(id, showtimeData) {
    return axios.put(`${API_ADMIN_SUAT_CHIEU}/${id}`, showtimeData);
  },

  // Xóa suất chiếu
  deleteShowtime(id) {
    return axios.delete(`${API_ADMIN_SUAT_CHIEU}/${id}`);
  }
};
