import { ElNotification } from 'element-plus';

/**
 * Common notification utility for CineOps
 * Uses Element Plus ElNotification with premium custom styling
 */
const notify = (type, title, message, icon) => {
  ElNotification({
    title,
    message: message || '',
    type,
    position: 'top-right',
    duration: 3000,
    customClass: `cine-notification ${type}`,
    dangerouslyUseHTMLString: true,
    icon: null, // We'll use our own icon element
    message: `
      <div class="d-flex align-items-center">
        <div class="cine-notification-icon me-3">
          <i class="${icon}"></i>
        </div>
        <div>
          <div class="el-notification__title">${title}</div>
          <div class="el-notification__content">${message}</div>
        </div>
      </div>
    `,
    // Hide original title since we put it in the custom message HTML
    title: ''
  });
};

export const notification = {
  success: (message) => {
    notify('success', 'Thành công', message, 'bi bi-film');
  },
  error: (message) => {
    notify('error', 'Lỗi hệ thống', message || 'Có lỗi xảy ra, vui lòng thử lại', 'bi bi-exclamation-octagon');
  },
  warning: (message) => {
    notify('warning', 'Cảnh báo', message, 'bi bi-megaphone');
  },
  info: (message) => {
    notify('info', 'Thông tin', message, 'bi bi-info-circle');
  },

  /**
   * Shorthand for "Add Success"
   */
  addSuccess: (entityName = '') => {
    const msg = entityName ? `Đã thêm ${entityName} vào danh sách` : 'Thêm mới thành công';
    notify('success', 'Phát hành mới', msg, 'bi bi-plus-circle-fill');
  },

  /**
   * Shorthand for "Update Success"
   */
  updateSuccess: (entityName = '') => {
    const msg = entityName ? `Thông tin ${entityName} đã được cập nhật` : 'Cập nhật thành công';
    notify('success', 'Cập nhật', msg, 'bi bi-pencil-square');
  },

  /**
   * Shorthand for "Delete Success"
   */
  deleteSuccess: (entityName = '') => {
    const msg = entityName ? `Đã gỡ bỏ ${entityName} khỏi hệ thống` : 'Xóa dữ liệu thành công';
    notify('success', 'Đã xóa', msg, 'bi bi-trash-fill');
  },

  /**
   * Shorthand for generic "Save Success"
   */
  saveSuccess: (entityName = '') => {
    const msg = entityName ? `Đã lưu các thay đổi của ${entityName}` : 'Lưu thành công';
    notify('success', 'Ghi nhận', msg, 'bi bi-check-all');
  },

  /**
   * Validation Error - Use for form checking
   */
  validationError: (message) => {
    notify('warning', 'Thiếu thông tin', message || 'Vui lòng điền đầy đủ các trường bắt buộc (*)', 'bi bi-exclamation-triangle-fill');
  },

  /**
   * Bad Request from Server (Multiple errors)
   */
  badRequest: (errors) => {
    let msg = 'Dữ liệu không hợp lệ:';
    if (Array.isArray(errors)) {
      msg = `<ul class="mb-0 ps-3">` + errors.map(e => `<li>${e}</li>`).join('') + `</ul>`;
    } else if (typeof errors === 'object') {
      msg = `<ul class="mb-0 ps-3">` + Object.values(errors).map(e => `<li>${e}</li>`).join('') + `</ul>`;
    } else {
      msg = errors || msg;
    }
    notify('error', 'Lỗi dữ liệu', msg, 'bi bi-shield-lock-fill');
  }
};

export default notification;
