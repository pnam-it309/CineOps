<template>
    <div class="tab-content-wrapper d-flex flex-column h-100 p-3 bg-light">
        <h3 class="fw-bold text-dark mb-4" style="font-size: 20px;">
            <i class="bi bi-geo-alt me-2 text-primary"></i>Sơ đồ ghế trực quan
        </h3>

        <div
            class="filter-mini mb-3 d-flex gap-3 align-items-center flex-shrink-0 p-3 bg-white border rounded-3 shadow-sm">
            <span class="fw-bold small text-secondary">Chọn phòng xem sơ đồ:</span>
            <el-select :model-value="selectedRoom" @update:model-value="val => $emit('update:selectedRoom', val)"
                placeholder="Chọn phòng" style="width: 250px;" @change="$emit('fetch-seats')">
                <el-option v-for="pc in phongChieuList" :key="pc.id" :label="pc.tenPhong"
                    :value="pc.id" />
            </el-select>
            <div class="ms-auto d-flex gap-4">
                <span class="seat-legend normal">Ghế thường</span>
                <span class="seat-legend vip">Ghế VIP</span>
                <span class="seat-legend maintenance">Bảo trì</span>
            </div>
        </div>

        <div v-if="selectedRoom && seats.length > 0"
            class="seat-map-main flex-grow-1 bg-white rounded-3 border p-5 overflow-auto shadow-inner position-relative">
            <div class="seat-map-container mx-auto">
                <div class="text-center mb-5">
                    <div class="screen-bar-large mx-auto rounded-pill shadow-sm"></div>
                    <span class="small text-secondary fw-bold text-uppercase letter-spacing-2">Màn hình chiếu</span>
                </div>

                <div class="seat-grid-main d-flex flex-column gap-3 align-items-center pb-5">
                    <div v-for="row in seatRows" :key="row.label" class="seat-row d-flex gap-2 align-items-center">
                        <span class="row-label me-3 fw-bold text-secondary">{{ row.label }}</span>
                        <div v-for="seat in row.seats" :key="seat.id" class="seat-item" :class="{
                            'vip': seat.tenLoaiGhe?.toLowerCase().includes('vip'),
                            'maintenance': seat.trangThai === 0
                        }" @click="$emit('open-dialog', seat)">
                            {{ seat.soCot }}
                            <el-tooltip :content="`${seat.soGhe} - ${seat.tenLoaiGhe}`" placement="top" :hide-after="0">
                                <div class="seat-click-overlay"></div>
                            </el-tooltip>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <el-empty v-else description="Chưa chọn phòng hoặc phòng chưa có sơ đồ ghế" />
    </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
    seats: Array,
    phongChieuList: Array,
    selectedRoom: [Number, String]
});

defineEmits(['update:selectedRoom', 'fetch-seats', 'open-dialog']);

const seatRows = computed(() => {
    if (!props.seats.length) return [];
    const rows = {};
    props.seats.forEach(s => {
        if (!rows[s.soHang]) rows[s.soHang] = [];
        rows[s.soHang].push(s);
    });
    return Object.keys(rows).sort().map(key => ({
        label: key,
        seats: rows[key].sort((a, b) => a.soCot - b.soCot)
    }));
});
</script>

<style scoped>
.tab-content-wrapper {
    overflow-y: auto;
}

.seat-map-main {
    background-image: radial-gradient(#cbd5e1 1px, transparent 1px);
    background-size: 30px 30px;
}

.screen-bar-large {
    height: 12px;
    width: 70%;
    background: linear-gradient(180deg, #1e293b 0%, #334155 100%);
    border: 3px solid rgba(255, 255, 255, 0.2);
}

.seat-item {
    width: 42px;
    height: 42px;
    background: #fff;
    border: 2px solid #cbd5e1;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 13px;
    font-weight: 800;
    color: #475569;
    cursor: pointer;
    position: relative;
    transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.seat-item:hover {
    transform: scale(1.15);
    border-color: #3b82f6;
    color: #3b82f6;
    z-index: 10;
    box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
}

.seat-item.vip {
    background: #fffbeb;
    border-color: #f59e0b;
    color: #b45309;
}

.seat-item.maintenance {
    background: #f8fafc;
    border-color: #94a3b8;
    color: #94a3b8;
    opacity: 0.6;
    cursor: not-allowed;
}

.seat-legend {
    font-size: 13px;
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: 500;
}

.seat-legend::before {
    content: '';
    width: 16px;
    height: 16px;
    border-radius: 4px;
    display: inline-block;
}

.seat-legend.normal::before {
    background: #fff;
    border: 2px solid #cbd5e1;
}

.seat-legend.vip::before {
    background: #fffbeb;
    border: 2px solid #f59e0b;
}

.seat-legend.maintenance::before {
    background: #f8fafc;
    border: 2px solid #94a3b8;
}

.row-label {
    min-width: 25px;
    text-align: right;
    font-size: 14px;
}

/* Hide Scrollbars */
.tab-content-wrapper::-webkit-scrollbar,
.seat-map-main::-webkit-scrollbar,
.tab-content-wrapper :deep(*)::-webkit-scrollbar {
    display: none !important;
}

.tab-content-wrapper,
.seat-map-main,
.tab-content-wrapper :deep(*) {
    scrollbar-width: none !important;
    -ms-overflow-style: none !important;
}
</style>
