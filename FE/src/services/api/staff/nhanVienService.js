// import axios from '@/services/axios';
//
// // Khớp với MappingConstants.API_ADMIN_PREFIX + "/nhan-vien"
// const BASE_URL = 'api/v1/admin/nhan-vien';
//
// export const nhanVienService = {
//     /**
//      * Lấy danh sách nhân viên có tìm kiếm và lọc đa năng
//      * @param {String} search - Từ khóa (tên, mã NV, sdt, email)
//      * @param {String} idPhanQuyen - ID vai trò (Quản lý, Nhân viên...)
//      * @param {Number} trangThai - 1: Hoạt động, 0: Ngừng hoạt động
//      */
//     getAll(search, idPhanQuyen, trangThai) {
//         return axios.get(BASE_URL, {
//             params: {
//                 search: search || null,
//                 idPhanQuyen: idPhanQuyen || null,
//                 trangThai: (trangThai === 0 || trangThai === 1) ? trangThai : null
//             }
//         });
//     },
//
//     /**
//      * Lấy chi tiết nhân viên theo ID
//      */
//     getById(id) {
//         return axios.get(`${BASE_URL}/${id}`);
//     },
//
//     /**
//      * Tạo mới nhân viên
//      * @param {Object} data - Dữ liệu từ AdNhanVienRequest
//      */
//     create(data) {
//         return axios.post(BASE_URL, data);
//     },
//
//     /**
//      * Cập nhật thông tin nhân viên
//      * @param {String} id - UUID của nhân viên
//      * @param {Object} data - Dữ liệu cập nhật
//      */
//     update(id, data) {
//         return axios.put(`${BASE_URL}/${id}`, data);
//     },
//
//     /**
//      * Xóa nhân viên
//      */
//     delete(id) {
//         return axios.delete(`${BASE_URL}/${id}`);
//     }
// };