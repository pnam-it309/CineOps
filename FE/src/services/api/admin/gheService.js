import axios from '@/services/axios';
import {
  API_ADMIN_GHE,
  API_ADMIN_GHE_GENERATE,
  API_ADMIN_LOAI_GHE,
  API_ADMIN_PHONG_CHIEU_DROP
} from '@/constants/apiEndpoints';

export const gheService = {
  // Lấy danh sách phòng chiếu cho dropdown
  getPhongChieuDropdown() {
    return axios.get(API_ADMIN_PHONG_CHIEU_DROP);
  },

  // Lấy danh sách loại ghế
  getLoaiGheList() {
    return axios.get(API_ADMIN_LOAI_GHE);
  },

  // Lấy danh sách ghế theo phòng
  getSeatsByRoom(idPhongChieu) {
    return axios.get(String(API_ADMIN_GHE), { params: { idPhongChieu } });
  },

  // Tạo ghế tự động
  generateSeats(configData) {
    return axios.post(API_ADMIN_GHE_GENERATE, configData);
  },

  // Thêm ghế lẻ
  createSeat(seatData) {
    return axios.post(API_ADMIN_GHE, seatData);
  },

  // Cập nhật ghế
  updateSeat(id, seatData) {
    return axios.put(`${API_ADMIN_GHE}/${id}`, seatData);
  },

  // Xóa ghế
  deleteSeat(id) {
    return axios.delete(`${API_ADMIN_GHE}/${id}`);
  }
};
