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
                <StatCard label="Tổng số ghế " subText="Số ghế đang có"
                    :value="configForm.rows * configForm.cols" icon="bi bi-grid-3x3-gap-fill" type="primary" />
            </div>
            <div class="col-md-4">
                <StatCard label="Lưu ý quan trọng" value="Xóa toàn bộ ghế cũ" subText="Xóa dữ liệu cũ khi tạo sơ đồ mới"
                    icon="bi bi-exclamation-triangle-fill" type="warning" subTextType="danger" />
            </div>
            <div class="col-md-4">
                <StatCard label="Tốc độ xử lý" value="Khởi tạo nhanh" subText="Xử lý dữ liệu trong < 1s"
                    icon="bi bi-lightning-charge-fill" type="success" />
            </div>
        </div>

        <!-- Main Form Content -->
        <div class="form-container-main bg-white rounded-3 border shadow-sm flex-grow-1 p-5 overflow-auto">
            <div class="mx-auto" style="max-width: 600px;">
                <div class="text-center mb-5">
                    <h4 class="fw-bold text-dark mb-2">Bộ tạo sơ đồ ghế tự động</h4>
                </div>

                <el-form label-position="top">
                    <el-form-item label="Phòng chiếu cần thiết lập" required class="mb-4">
                        <el-select v-model="configForm.idPhongChieu" placeholder="Chọn phòng..."
                            class="w-100 custom-control">
                            <el-option v-for="pc in phongChieuList" :key="pc.id" :label="pc.tenPhong" :value="pc.id" />
                        </el-select>
                    </el-form-item>

                    <div class="row g-4 mb-4">
                        <div class="col-md-6">
                            <el-form-item label="Số lượng Hàng (Ví dụ: 10 hàng)" required>
                                <el-input v-model.number="configForm.rows" type="number" placeholder="10"
                                    class="custom-control" />
                            </el-form-item>
                        </div>
                        <div class="col-md-6">
                            <el-form-item label="Số lượng Cột (Ví dụ: 12 cột)" required>
                                <el-input v-model.number="configForm.cols" type="number" placeholder="10"
                                    class="custom-control" />
                            </el-form-item>
                        </div>
                    </div>

                    <el-form-item label="Loại ghế mặc định" required class="mb-5">
                        <el-select v-model="configForm.idLoaiGheDefault" placeholder="Chọn loại ghế..."
                            class="w-100 custom-control">
                            <el-option v-for="lg in loaiGheList" :key="lg.id" :label="lg.tenLoai" :value="lg.id" />
                        </el-select>
                    </el-form-item>

                    <div class="mt-4 pt-4 border-top text-center">
                        <el-button size="large" class="btn-generate px-5 fw-bold"
                            @click="$emit('handle-generate', configForm)" :loading="generating">
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
    if (newVal && newVal.length && !configForm.value.idLoaiGheDefault) {
        configForm.value.idLoaiGheDefault = newVal[0].id;
    }
}, { immediate: true });
</script>

<style scoped>
.tab-content-wrapper {
    overflow: hidden;
}

.form-container-main {
    min-height: 0;
}

.btn-generate {
    height: 54px;
    font-size: 16px;
    transition: all 0.3s;
    background: #ffffff;
    border: 2px solid #d32f2f;
    color: #d32f2f;
    border-radius: 50px;
    box-shadow: none;
}

.btn-generate:hover {
    transform: translateY(-2px);
    background-color: #fef2f2;
    border-color: #b91c1c;
    color: #b91c1c;
    box-shadow: 0 6px 20px rgba(220, 38, 38, 0.15);
}

/* Form Styling Standards */
:deep(.el-form-item__label) {
    font-size: 16px !important;
    font-weight: 600 !important;
    color: #334155 !important;
    margin-bottom: 8px !important;
}

:deep(.custom-control .el-input__wrapper),
:deep(.custom-control .el-select__wrapper) {
    height: 50px !important;
    border-radius: 12px !important;
    border: 1px solid #e2e8f0 !important;
    box-shadow: none !important;
    background-color: #f8fafc !important;
    font-size: 18px !important;
    transition: all 0.2s;
}

:deep(.custom-control .el-input__wrapper.is-focus),
:deep(.custom-control .el-select__wrapper.is-focused) {
    border-color: #3b82f6 !important;
    background-color: #ffffff !important;
    box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.1) !important;
}

:deep(.el-input-number .el-input__inner) {
    text-align: center;
}

/* Hide Scrollbars */
.form-container-main::-webkit-scrollbar {
    display: none !important;
}

.form-container-main {
    scrollbar-width: none !important;
    -ms-overflow-style: none !important;
}
</style>
