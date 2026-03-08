import axios from 'axios';

const BASE_URL = 'https://provinces.open-api.vn/api';

export const addressService = {
  getProvinces() {
    return axios.get(`${BASE_URL}/p/`);
  },
  getDistricts(provinceCode) {
    if (!provinceCode) return Promise.resolve({ data: [] });
    return axios.get(`${BASE_URL}/p/${provinceCode}?depth=2`);
  },
  getWards(districtCode) {
    if (!districtCode) return Promise.resolve({ data: [] });
    return axios.get(`${BASE_URL}/d/${districtCode}?depth=2`);
  }
};
