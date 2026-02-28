
// import axios from '@/services/axios';
//
// // Đảm bảo BASE_URL khớp với MappingConstants.API_ADMIN_PREFIX + "/khach-hang"
// const BASE_URL = 'api/v1/admin/khach-hang';
//
// export const khachHangService = {
//     /**
//      * Lấy danh sách khách hàng có tìm kiếm và lọc
//      * @param {String} search - Từ khóa tìm kiếm (tên, sdt, email)
//      * @param {Number} trangThai - 1: Hoạt động, 0: Ngừng hoạt động
//      */
//     getAll(search, trangThai) {
//         return axios.get(BASE_URL, {
//             params: {
//                 search: search || null,
//                 trangThai: (trangThai === 0 || trangThai === 1) ? trangThai : null
//             }
//         });
//     },
//
//     getById(id) {
//         return axios.get(`${BASE_URL}/${id}`);
//     },
//
//     create(data) {
//         return axios.post(BASE_URL, data);
//     },
//
//     /**
//      * Cập nhật thông tin khách hàng
//      * @param {String} id - ID của khách hàng (UUID)
//      * @param {Object} data - Dữ liệu cập nhật
//      */
//     update(id, data) {
//         return axios.put(`${BASE_URL}/${id}`, data);
//     },
//
//     delete(id) {
//         return axios.delete(`${BASE_URL}/${id}`);
//     }
// };
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
    getAll(search, trangThai, page = 0, size = 10) {
        return axios.get(BASE_URL, {
            params: {
                search: search || null,
                trangThai: (trangThai === 0 || trangThai === 1) ? trangThai : null,
                page: page, // Bổ sung tham số này
                size: size  // Bổ sung tham số này
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
