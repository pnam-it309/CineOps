<template>
    <div class="tab-content-wrapper d-flex flex-column h-100 p-3 bg-light">
        <!-- Header Section -->
        <div class="page-header d-flex justify-content-between align-items-center mb-4 flex-shrink-0">
            <h3 class="fw-bold text-dark mb-0" style="font-size: 20px;">
                <i class="bi bi-grid-3x3-gap me-2 text-primary"></i>Cấu hình khởi tạo phòng
            </h3>
        </div>

        <!-- Info Cards (Replacing Stats) -->
        <div class="row g-3 mb-4 flex-shrink-0">
            <div class="col-md-4">
                <StatCard 
                    label="Tổng số ghế dự kiến" 
                    :value="configForm.rows * configForm.cols" 
                    icon="bi bi-grid-3x3-gap-fill"
                    type="primary"
                />
            </div>
            <div class="col-md-4">
                <StatCard 
                    label="Lưu ý quan trọng" 
                    value="Xóa toàn bộ ghế cũ"
                    subText="Xóa dữ liệu cũ khi tạo sơ đồ mới"
                    icon="bi bi-exclamation-triangle-fill"
                    type="warning"
                    subTextType="danger"
                />
            </div>
            <div class="col-md-4">
                <StatCard 
                    label="Tốc độ xử lý" 
                    value="Khởi tạo nhanh"
                    subText="Xử lý dữ liệu trong < 1s"
                    icon="bi bi-lightning-charge-fill"
                    type="success"
                />
            </div>
        </div>

        <!-- Main Form Content -->
        <div class="form-container-main bg-white rounded-3 border shadow-sm flex-grow-1 p-5 overflow-auto">
            <div class="mx-auto" style="max-width: 600px;">
                <div class="text-center mb-5">
                    <h4 class="fw-bold text-dark mb-2">Bộ tạo sơ đồ ghế tự động</h4>
                </div>

                <el-form label-position="top">
                    <el-form-item label="Phòng chiếu cần thiết lập" required>
                        <el-select v-model="configForm.idPhongChieu" placeholder="Chọn phòng..." class="w-100 custom-input">
                            <el-option v-for="pc in phongChieuList" :key="pc.id" :label="pc.tenPhong" :value="pc.id" />
                        </el-select>
                    </el-form-item>

                    <div class="row g-4 scroll-hidden">
                        <div class="col-md-6">
                            <el-form-item label="Số lượng Hàng (Ví dụ: 10 hàng)" required>
                                <el-input-number v-model="configForm.rows" :min="1" :max="26" class="w-100" />
                            </el-form-item>
                        </div>
                        <div class="col-md-6">
                            <el-form-item label="Số lượng Cột (Ví dụ: 12 cột)" required>
                                <el-input-number v-model="configForm.cols" :min="1" :max="30" class="w-100" />
                            </el-form-item>
                        </div>
                    </div>

                    <el-form-item label="Loại ghế mặc định" required class="mt-4">
                        <el-select v-model="configForm.idLoaiGheDefault" placeholder="Chọn loại ghế..." class="w-100 custom-input">
                            <el-option v-for="lg in loaiGheList" :key="lg.id" :label="lg.tenLoai" :value="lg.id" />
                        </el-select>
                    </el-form-item>

                    <div class="mt-5 pt-3 border-top text-center">
                        <el-button 
                            type="primary" 
                            size="large" 
                            :icon="MagicStick" 
                            class="btn-generate px-5 rounded-3 fw-bold"
                            @click="$emit('handle-generate', configForm)" 
                            :loading="generating"
                        >
                            Xác nhận khởi tạo lại sơ đồ
                        </el-button>
                    </div>
                </el-form>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import { Grid, WarningFilled, MagicStick } from '@element-plus/icons-vue';
import StatCard from '@/components/common/StatCard.vue';

const props = defineProps({
    phongChieuList: Array,
    loaiGheList: Array,
    generating: Boolean
});

defineEmits(['handle-generate']);

const configForm = ref({
    idPhongChieu: '',
    idLoaiGheDefault: '',
    rows: 10,
    cols: 10
});

// Set default loai ghe if list available
watch(() => props.loaiGheList, (newVal) => {
    if (newVal.length && !configForm.value.idLoaiGheDefault) {
        configForm.value.idLoaiGheDefault = newVal[0].id;
    }
}, { immediate: true });
</script>

<style scoped>
.tab-content-wrapper {
    overflow: hidden;
}

.info-card {
    transition: transform 0.2s, box-shadow 0.2s;
}

.icon-box {
    width: 48px;
    height: 48px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.bg-primary-soft { background-color: #eff6ff; }
.bg-warning-soft { background-color: #fffbeb; }
.bg-success-soft { background-color: #f0fdf4; }

.form-container-main {
    min-height: 0;
}

.btn-generate {
    height: 48px;
    font-size: 15px;
    letter-spacing: 0.5px;
    box-shadow: 0 4px 6px -1px rgba(64, 158, 255, 0.3);
}

/* Hide Scrollbars */
.form-container-main::-webkit-scrollbar {
    display: none !important;
}
.form-container-main {
    scrollbar-width: none !important;
    -ms-overflow-style: none !important;
}

:deep(.custom-input .el-input__wrapper) {
  border: 1px solid #e2e8f0 !important;
  box-shadow: none !important;
  border-radius: 8px !important;
}
</style>
