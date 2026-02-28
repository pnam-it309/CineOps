import axios from '@/services/axios';

const BASE_URL = '/admin/khach-hang';

export const khachHangService = {
    getAll(keyword, trangThai) {
        return axios.get(BASE_URL, { 
            params: { keyword, trangThai } 
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
