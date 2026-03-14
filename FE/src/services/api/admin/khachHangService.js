import axios from '@/services/axios';

const BASE_URL = 'api/v1/admin/khach-hang';

export const khachHangService = {
    /**
     * Lấy danh sách khách hàng có tìm kiếm, lọc và PHÂN TRANG
     * @param {String} search - Từ khóa tìm kiếm
     * @param {Number} trangThai - Trạng thái
     * @param {Number} page - Trang hiện tại (bắt đầu từ 0)
     * @param {Number} size - Số bản ghi trên mỗi trang
     */
    getAll(search, trangThai, gioiTinh, idLoaiKhachHang, page = 0, size = 10, sort = 'id,desc') {
        return axios.get(BASE_URL, {
            params: {
                search: search || null,
                trangThai: (trangThai === 0 || trangThai === 1 || trangThai === 2) ? trangThai : null,
                gioiTinh: (gioiTinh === 0 || gioiTinh === 1) ? gioiTinh : null,
                idLoaiKhachHang: idLoaiKhachHang || null,
                page: page,
                size: size,
                sort: sort
            }
        });
    },
    getLoaiKhachHang() {
        return axios.get(`${BASE_URL}/loai-khach-hang`);
    },
    getById(id) {
        return axios.get(`${BASE_URL}/${id}`);
    },

    create(data) {
        return axios.post(BASE_URL, data);
    },

    update(id, data) {
        return axios.put(`${BASE_URL}/${id}`, data);
    },

    delete(id) {
        return axios.delete(`${BASE_URL}/${id}`);
    }
};
