<template>
    <BaseModal
        :model-value="visible"
        @update:model-value="val => $emit('update:visible', val)"
        :title="isBulk ? `Cập nhật ${bulkCount} ghế` : (editingId ? 'Chỉnh sửa Ghế' : 'Thêm Ghế mới')"
        :icon="isBulk ? 'bi bi-collection' : (editingId ? 'bi bi-pencil-square' : 'bi bi-plus-lg')"
        width="460px"
        :confirmText="isBulk ? 'Cập nhật hàng loạt' : (editingId ? 'Lưu thay đổi' : 'Thêm ghế')"
        :loading="saving"
        @confirm="submit"
    >
        <el-form :model="form" :rules="rules" ref="formRef" label-position="top" class="premium-form">
            <div class="row g-2">
                <div class="col-12" v-if="isBulk">
                    <p class="text-secondary small mb-3">
                        <i class="bi bi-info-circle me-1"></i>
                        Các trường để trống sẽ không được cập nhật.
                    </p>
                </div>
                <div class="col-6" v-if="!isBulk">
                    <el-form-item label="Phòng chiếu" prop="idPhongChieu">
                        <el-select v-model="form.idPhongChieu" class="w-100" placeholder="Chọn phòng"
                            :disabled="!!editingId">
                            <el-option v-for="pc in phongChieuList" :key="pc.id" :label="pc.tenPhong" :value="pc.id" />
                        </el-select>
                    </el-form-item>
                </div>
                <div :class="isBulk ? 'col-12' : 'col-6'">
                    <el-form-item label="Loại ghế" prop="idLoaiGhe">
                        <el-select v-model="form.idLoaiGhe" class="w-100" placeholder="Chọn loại" :clearable="isBulk">
                            <el-option v-for="lg in loaiGheList" :key="lg.id" :label="lg.tenLoai" :value="lg.id" />
                        </el-select>
                    </el-form-item>
                </div>
                <template v-if="!isBulk">
                    <div class="col-4">
                        <el-form-item label="Số ghế" prop="soGhe">
                            <el-input v-model="form.soGhe" placeholder="Tự động" disabled :prefix-icon="Monitor" />
                        </el-form-item>
                    </div>
                    <div class="col-4">
                        <el-form-item label="Hàng" prop="soHang">
                            <el-input v-model="form.soHang" placeholder="A" />
                        </el-form-item>
                    </div>
                    <div class="col-4">
                        <el-form-item label="Cột" prop="soCot">
                            <el-input-number v-model="form.soCot" :min="1" class="w-100" controls-position="right" />
                        </el-form-item>
                    </div>
                </template>

            </div>
        </el-form>
    </BaseModal>
</template>

<script setup>
import { ref, watch, computed } from 'vue';
import BaseModal from '@/components/common/BaseModal.vue';
import { Monitor } from '@element-plus/icons-vue';

const props = defineProps({
    visible: Boolean,
    editingId: [Number, String],
    isBulk: Boolean,
    bulkCount: Number,
    initialData: Object,
    phongChieuList: Array,
    loaiGheList: Array,
    saving: Boolean
});

const emit = defineEmits(['update:visible', 'submit']);

const formRef = ref(null);
const form = ref({
    idPhongChieu: '',
    idLoaiGhe: '',
    soGhe: '',
    soHang: '',
    soCot: 1,
    trangThai: 1
});

const rules = computed(() => {
    if (props.isBulk) return {};
    return {
        idPhongChieu: [{ required: true, message: 'Vui lòng chọn phòng chiếu', trigger: 'change' }],
        idLoaiGhe: [{ required: true, message: 'Vui lòng chọn loại ghế', trigger: 'change' }],
        soGhe: [{ required: true, message: 'Nhập số ghế', trigger: 'blur' }],
        soHang: [{ required: true, message: 'Nhập hàng', trigger: 'blur' }],
        soCot: [{ required: true, message: 'Nhập cột', trigger: 'blur' }],
    };
});

watch(() => props.initialData, (newVal) => {
    if (newVal) {
        form.value = { ...newVal };
    } else {
        form.value = {
            idPhongChieu: props.phongChieuList[0]?.id || '',
            idLoaiGhe: props.loaiGheList[0]?.id || '',
            soGhe: '',
            soHang: '',
            soCot: 1,
            trangThai: props.isBulk ? null : 1
        };
    }
}, { immediate: true });

watch([() => form.value.soHang, () => form.value.soCot], ([newHang, newCot]) => {
    if (newHang && newCot) {
        form.value.soGhe = `${newHang}${newCot}`;
    } else {
        form.value.soGhe = '';
    }
});

const submit = async () => {
    if (!formRef.value) return;
    await formRef.value.validate((valid) => {
        if (valid) {
            emit('submit', form.value);
        }
    });
};
</script>

<style scoped>
.dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
}
</style>
