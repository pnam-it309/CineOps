import instance from '@/services/axios';

const BASE = '/api/v1/admin/phim';

export const phimApi = {
    getPhim: (params) => instance.get(BASE, { params }),
    getById: (id) => instance.get(`${BASE}/${id}`),
    getTheLoai: () => instance.get(`${BASE}/the-loai`),
    create: (data) => instance.post(BASE, data),
    update: (id, data) => instance.put(`${BASE}/${id}`, data),
    updateTrangThai: (id, trangThai) =>
        instance.patch(`${BASE}/${id}/trang-thai`, null, { params: { trangThai } }),
    delete: (id) => instance.delete(`${BASE}/${id}`),
};