<template>
    <BaseModal
        :model-value="visible"
        @update:model-value="val => $emit('update:visible', val)"
        :title="editingId ? 'Chỉnh sửa Ghế' : 'Thêm Ghế mới'"
        :icon="editingId ? 'bi bi-pencil-square' : 'bi bi-plus-lg'"
        width="460px"
        :confirmText="editingId ? 'Lưu thay đổi' : 'Thêm ghế'"
        :loading="saving"
        @confirm="submit"
    >
        <el-form :model="form" :rules="rules" ref="formRef" label-position="top" class="premium-form">
            <div class="row g-2">
                <div class="col-6">
                    <el-form-item label="Phòng chiếu" prop="idPhongChieu">
                        <el-select v-model="form.idPhongChieu" class="w-100" placeholder="Chọn phòng"
                            :disabled="!!editingId">
                            <el-option v-for="pc in phongChieuList" :key="pc.id" :label="pc.tenPhong" :value="pc.id" />
                        </el-select>
                    </el-form-item>
                </div>
                <div class="col-6">
                    <el-form-item label="Loại ghế" prop="idLoaiGhe">
                        <el-select v-model="form.idLoaiGhe" class="w-100" placeholder="Chọn loại">
                            <el-option v-for="lg in loaiGheList" :key="lg.id" :label="lg.tenLoai" :value="lg.id" />
                        </el-select>
                    </el-form-item>
                </div>
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
                <div class="col-12" v-if="editingId">
                    <el-form-item label="Trạng thái" prop="trangThai">
                        <el-radio-group v-model="form.trangThai">
                            <el-radio-button :value="1">Hoạt động</el-radio-button>
                            <el-radio-button :value="0">Bảo trì</el-radio-button>
                        </el-radio-group>
                    </el-form-item>
                </div>
            </div>
        </el-form>
    </BaseModal>
</template>

<script setup>
import { ref, watch } from 'vue';
import BaseModal from '@/components/common/BaseModal.vue';
import { Monitor } from '@element-plus/icons-vue';

const props = defineProps({
    visible: Boolean,
    editingId: [Number, String],
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

const rules = {
    idPhongChieu: [{ required: true, message: 'Vui lòng chọn phòng chiếu', trigger: 'change' }],
    idLoaiGhe: [{ required: true, message: 'Vui lòng chọn loại ghế', trigger: 'change' }],
    soGhe: [{ required: true, message: 'Nhập số ghế', trigger: 'blur' }],
    soHang: [{ required: true, message: 'Nhập hàng', trigger: 'blur' }],
    soCot: [{ required: true, message: 'Nhập cột', trigger: 'blur' }],
};

watch(() => props.initialData, (newVal) => {
    if (newVal) {
        form.value = { ...newVal };
    } else {
        form.value = {
            idPhongChieu: '',
            idLoaiGhe: props.loaiGheList[0]?.id || '',
            soGhe: '',
            soHang: '',
            soCot: 1,
            trangThai: 1
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
