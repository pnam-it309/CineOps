import axios from '@/services/axios';

const BASE_URL = 'api/v1/admin/roles';

export const phanQuyenService = {
    /**
     * Lấy danh sách vai trò cho phép gán cho nhân viên
     */
    getAllRoles() {
        return axios.get(BASE_URL);
    }
};
