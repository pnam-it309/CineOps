import axios from '@/services/axios'

const BASE_URL = 'api/v1/admin/san-pham-di-kem'

export const foodService = {

  getAll(params) {
    return axios.get(BASE_URL, { params })
  },

  getById(id) {
    return axios.get(`${BASE_URL}/${id}`)
  },

  create(data) {
    return axios.post(BASE_URL, data)
  },

  update(id, data) {
    return axios.put(`${BASE_URL}/${id}`, data)
  },

  delete(id) {
    return axios.delete(`${BASE_URL}/${id}`)
  }

}