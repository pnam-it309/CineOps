import { ElMessageBox } from 'element-plus';

/**
 * Utility function to handle common confirmation dialogs in CineOps
 * Uses Element Plus ElMessageBox.confirm
 */
export const confirmDialog = {
  /**
   * Generic confirm method
   * @param {string} message - The main message of the dialog
   * @param {string} title - The title of the dialog
   * @param {string} confirmText - Text for confirm button
   * @param {string} type - 'warning', 'info', 'success', 'error'
   * @returns {Promise} Resolves if user confirms, rejects if cancels
   */
  custom: (message, title = 'Xác nhận', confirmText = 'Đồng ý', type = 'warning') => {
    return ElMessageBox.confirm(message, title, {
      dangerouslyUseHTMLString: true,
      confirmButtonText: confirmText,
      cancelButtonText: 'Hủy',
      type: type,
    });
  },

  /**
   * Confirm adding a new item
   * @param {string} entityName - Name of the entity being added (e.g., 'nhân viên', 'ghế')
   * @returns {Promise}
   */
  add: (entityName = 'dữ liệu') => {
    return confirmDialog.custom(
      `Bạn có chắc chắn muốn thêm <b>${entityName}</b> này không?`,
      'Xác nhận thêm mới',
      'Thêm',
      'info'
    );
  },

  /**
   * Confirm updating an existing item
   * @param {string} entityName - Name of the entity being updated
   * @returns {Promise}
   */
  update: (entityName = 'dữ liệu') => {
    return confirmDialog.custom(
      `Bạn có chắc chắn muốn cập nhật thông tin <b>${entityName}</b> này không?`,
      'Xác nhận cập nhật',
      'Cập nhật',
      'warning'
    );
  },

  /**
   * Confirm deleting an item
   * @param {string} entityName - Name of the entity being deleted
   * @param {string} detail - Extra detail to show (e.g., the name of the item)
   * @returns {Promise}
   */
  delete: (entityName = 'dữ liệu', detail = '') => {
    const detailHtml = detail ? ` "<b>${detail}</b>"` : '';
    return confirmDialog.custom(
      `Bạn có chắc chắn muốn xóa ${entityName}${detailHtml} không? Hành động này không thể hoàn tác.`,
      'Xác nhận xóa',
      'Xóa',
      'error'
    );
  },

  /**
   * Confirm a generic save action
   * @returns {Promise}
   */
  save: () => {
    return confirmDialog.custom(
      'Bạn có chắc chắn muốn lưu các thay đổi này không?',
      'Xác nhận lưu',
      'Lưu lại',
      'warning'
    );
  }
};

export default confirmDialog;
