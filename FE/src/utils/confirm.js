import { ElMessageBox } from 'element-plus';

/**
 * Utility function to handle common confirmation dialogs in CineOps
 * Uses Element Plus ElMessageBox.confirm
 */
export const confirmDialog = {
  /**
   * Generic custom confirm method
   */
  custom: (message, title = 'Xác nhận', confirmText = 'Đồng ý', type = 'warning', iconClass = '') => {
    return ElMessageBox.confirm(
      `<div class="d-flex align-items-center gap-3">
        ${iconClass ? `<div class="confirm-icon-wrapper ${type}"><i class="${iconClass}"></i></div>` : ''}
        <div class="confirm-content-text">${message}</div>
      </div>`,
      title,
      {
        dangerouslyUseHTMLString: true,
        confirmButtonText: confirmText,
        cancelButtonText: 'Hủy bỏ',
        type: type,
        customClass: 'premium-confirm-box',
        center: true,
        showClose: false,
        buttonSize: 'default',
        confirmButtonClass: `btn-confirm-${type}`
      }
    );
  },

  /**
   * Confirm adding a new item
   */
  add: (entityName = 'dữ liệu') => {
    return confirmDialog.custom(
      `Bạn có chắc chắn muốn tạo mới <b>${entityName}</b> này không?`,
      'Xác nhận tạo mới',
      'Xác nhận tạo',
      'success',
      'bi bi-plus-circle-fill'
    );
  },

  /**
   * Confirm updating an existing item
   */
  update: (entityName = 'dữ liệu') => {
    return confirmDialog.custom(
      `Mọi thay đổi sẽ được lưu trữ. Bạn có chắc chắn muốn cập nhật <b>${entityName}</b> này không?`,
      'Xác nhận cập nhật',
      'Cập nhật ngay',
      'warning',
      'bi bi-pencil-square'
    );
  },

  /**
   * Confirm status change
   */
  status: (entityName = 'dữ liệu', newStatusLabel = 'thay đổi', isActivation = true) => {
    return confirmDialog.custom(
      `Bạn đang thực hiện <b>${newStatusLabel}</b> cho <b>${entityName}</b>. Tiếp tục?`,
      'Thay đổi trạng thái',
      isActivation ? 'Kích hoạt' : 'Xác nhận',
      isActivation ? 'success' : 'warning',
      isActivation ? 'bi bi-check-circle-fill' : 'bi bi-exclamation-triangle-fill'
    );
  },

  /**
   * Confirm deleting an item
   */
  delete: (entityName = 'dữ liệu', detail = '') => {
    const detailHtml = detail ? ` "<b>${detail}</b>"` : '';
    return confirmDialog.custom(
      `Hành động này <b>không thể hoàn tác</b>. Bạn có chắc chắn muốn xóa ${entityName}${detailHtml} không?`,
      'Cảnh báo xóa dữ liệu',
      'Xóa vĩnh viễn',
      'error',
      'bi bi-trash3-fill'
    );
  },

  /**
   * Confirm a generic action
   */
  confirm: (title, message, confirmText = 'Xác nhận', type = 'info') => {
    return confirmDialog.custom(message, title, confirmText, type, 'bi bi-question-circle-fill');
  }
};

export default confirmDialog;
