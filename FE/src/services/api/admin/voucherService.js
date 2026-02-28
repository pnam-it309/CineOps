import axios from '@/services/axios';

const BASE_URL = '/admin/phieu-giam-gia';

export const voucherService = {
    getAll(params) {
        return axios.get(BASE_URL, { params });
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
