<template>
  <div class="excel-actions d-inline-flex gap-2">
    <el-tooltip content="Tải tệp mẫu Excel" placement="top">
      <el-button @click="handleDownloadTemplate" :icon="Download" class="btn-cine-excel-template square" plain>
      </el-button>
    </el-tooltip>

    <el-upload
      class="excel-upload"
      :show-file-list="false"
      :before-upload="handleBeforeUpload"
      accept=".xlsx"
    >
      <el-tooltip content="Nhập dữ liệu từ Excel" placement="top">
        <el-button :icon="Upload" class="btn-cine-excel-import square" type="success">
        </el-button>
      </el-tooltip>
    </el-upload>
  </div>
</template>

<script setup>
import { Download, Upload } from '@element-plus/icons-vue';
import { excelService } from '@/services/api/admin/excelService';
import notification from '@/utils/notifications';

const props = defineProps({
  module: {
    type: String,
    required: true
  }
});

const emit = defineEmits(['import-success']);

const handleDownloadTemplate = () => {
  excelService.downloadTemplate(props.module);
};

const handleBeforeUpload = async (file) => {
  const isExcel = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' || 
                  file.name.endsWith('.xlsx');
  
  if (!isExcel) {
    notification.error('Chỉ chấp nhận tệp Excel (.xlsx)');
    return false;
  }

  const result = await excelService.importExcel(props.module, file);
  if (result) {
    emit('import-success', result);
  }
  return false; // Prevent automatic upload by el-upload
};
</script>

<style scoped>
.btn-cine-excel-template {
  background-color: #f8fafc;
  color: #64748b;
  border-color: #e2e8f0;
}
.btn-cine-excel-template:hover {
  background-color: #f1f5f9;
  color: #1e293b;
  border-color: #cbd5e1;
}

.btn-cine-excel-import {
  background-color: #10b981;
  border-color: #10b981;
}
.btn-cine-excel-import:hover {
  background-color: #059669;
  border-color: #059669;
}

.excel-upload {
  display: inline-block;
}
</style>
