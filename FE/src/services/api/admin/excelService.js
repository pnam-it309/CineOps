import axios from '@/services/axios';
import notification from '@/utils/notifications';

export const excelService = {
  downloadTemplate: async (module) => {
    try {
      const response = await axios.get(`/api/excel/${module}/template`, {
        responseType: 'blob'
      });
      
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      
      // Get filename from response headers or use default
      const contentDisposition = response.headers['content-disposition'];
      let filename = `${module}_template.xlsx`;
      if (contentDisposition) {
        const filenameMatch = contentDisposition.match(/filename=(.+)/);
        if (filenameMatch && filenameMatch.length > 1) {
          filename = filenameMatch[1];
        }
      }
      
      link.setAttribute('download', filename);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
      
      notification.success('Đã tải xuống tệp mẫu');
    } catch (error) {
      console.error('Error downloading template', error);
      notification.error('Có lỗi khi tải xuống tệp mẫu');
    }
  },

  importExcel: async (module, file) => {
    const formData = new FormData();
    formData.append('file', file);
    
    try {
      const response = await axios.post(`/api/excel/${module}/import`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      });
      
      if (response.data.success) {
        const result = response.data.importResult;
        notification.success(`Nhập dữ liệu thành công! Tổng: ${result.totalRows}, Thành công: ${result.successCount}, Thất bại: ${result.failureCount}`);
        return response.data;
      } else {
        notification.error(response.data.message || 'Có lỗi xảy ra khi nhập dữ liệu');
        return null;
      }
    } catch (error) {
      console.error('Error importing excel', error);
      const errorMsg = error.response?.data?.message || 'Có lỗi xảy ra khi kết nối máy chủ';
      notification.error(errorMsg);
      return null;
    }
  }
};
