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
    getAll(search, trangThai, page = 0, size = 10, sort = 'id,desc') {
        return axios.get(BASE_URL, {
            params: {
                search: search || null,
                trangThai: (trangThai === 0 || trangThai === 1) ? trangThai : null,
                page: page,
                size: size,
                sort: sort
            }
        });
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
