import axios from '@/services/axios';

const BASE_URL = 'api/v1/admin/san-pham-di-kem';
const METADATA_URL = 'api/v1/admin/san-pham-di-kem-metadata';

export const sanPhamDiKemService = {
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
    },

    // Metadata
    getCategories() {
        return axios.get(`${METADATA_URL}/categories`);
    },

    getSizes() {
        return axios.get(`${METADATA_URL}/sizes`);
    },

    getUnits() {
        return axios.get(`${METADATA_URL}/units`);
    }
};
