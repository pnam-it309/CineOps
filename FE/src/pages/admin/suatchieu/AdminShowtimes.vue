<template>
  <div class="admin-showtimes-page">
    <AdminTableLayout
      title="Quản lý suất chiếu"
      titleIcon="bi bi-alarm"
      addButtonLabel="Thêm suất chiếu"
      :data="filteredShowtimes"
      :loading="loading"
      :total="filteredShowtimes.length"
      v-model:currentPage="currentPage"
      v-model:pageSize="pageSize"
      @add-click="openDialog()"
      @reset-filter="resetFilter"
    >
      <template #header-actions-left>
        <ExcelActions module="suat-chieu" @import-success="fetchShowtimes" />
      </template>

      <!-- Extra action button slot: Auto Generate -->
      <template #header-actions>
        <div class="filter-item border-start ps-3 ms-2">
          <el-tooltip content="Tạo suất chiếu tự động" placement="top">
            <el-button
              id="btn-auto-generate-showtime"
              type="warning"
              :icon="MagicStick"
              @click="openBatchDialog"
              class="square"
              plain
            >
            </el-button>
          </el-tooltip>
        </div>
      </template>

      <!-- Filters -->
      <template #filters>
        <div class="filter-item search-input-wrapper">
          <el-input v-model="searchQuery" placeholder="Tìm tên phim..." :prefix-icon="Search" size="default" clearable />
        </div>
        <div class="filter-item">
          <el-date-picker v-model="filterDate" type="date" placeholder="Chọn ngày chiếu"
            format="DD/MM/YYYY" value-format="YYYY-MM-DD" size="default" style="width:200px;" />
        </div>
        <div class="filter-item">
          <el-select v-model="filterRoom" placeholder="Chọn phòng chiếu" clearable style="width:200px;">
            <el-option label="Tất cả phòng" value="" />
            <el-option v-for="r in phongChieuList" :key="r.id" :label="r.tenPhong" :value="r.id" />
          </el-select>
        </div>
        <div class="filter-item">
          <el-select v-model="filterTrangThai" placeholder="Chọn trạng thái" clearable style="width:200px;">
            <el-option label="Tất cả trạng thái" value="" />
            <el-option label="Sắp chiếu" :value="1" />
            <el-option label="Đang chiếu" :value="2" />
            <el-option label="Đã hủy" :value="0" />
          </el-select>
        </div>
      </template>

      <!-- Content: BaseTable trực tiếp, không qua sub-component -->
      <template #content>
        <BaseTable
          :data="paginatedData"
          :columns="columns"
          :loading="loading"
          :total="filteredShowtimes.length"
          v-model:currentPage="currentPage"
          v-model:pageSize="pageSize"
          v-model:selection="selectedShowtimes"
          :hide-pagination="true"
        >
          <template #cell-index="{ index }">
            <span class="text-secondary small">{{ (currentPage - 1) * pageSize + index + 1 }}</span>
          </template>

          <template #cell-phim="{ row }">
            <div class="d-flex align-items-center justify-content-center gap-2">
              <img v-if="row.poster" :src="row.poster" class="rounded shadow-sm flex-shrink-0"
                style="width:32px;height:44px;object-fit:cover;" :alt="row.tenPhim" />
              <div v-else class="rounded d-flex align-items-center justify-content-center bg-light border flex-shrink-0"
                style="width:32px;height:44px;">
                <i class="bi bi-film text-secondary"></i>
              </div>
              <div class="fw-semibold small text-dark">{{ row.tenPhim || '—' }}</div>
            </div>
          </template>

          <template #cell-phongChieu="{ row }">
            <div class="small fw-semibold text-dark">
              <i class="bi bi-door-open me-1 text-primary"></i>{{ row.tenPhongChieu }}
            </div>
          </template>

          <template #cell-ngayChieu="{ row }">
            <span class="small fw-semibold text-dark">{{ formatDate(row.ngayChieu) }}</span>
          </template>

          <template #cell-gioChieu="{ row }">
            <div class="small d-flex align-items-center justify-content-center gap-1">
              <el-tag type="info" effect="plain" size="small" class="fw-bold">{{ row.gioBatDau }}</el-tag>
              <span class="text-secondary">→</span>
              <el-tag type="info" effect="plain" size="small">{{ row.gioKetThuc }}</el-tag>
            </div>
          </template>

          <template #cell-soGheTrong="{ row }">
            <el-tag :type="row.soGheTrong > 0 ? 'success' : 'danger'" effect="light" size="small" class="fw-bold">
              {{ row.soGheTrong }}
            </el-tag>
          </template>

          <template #cell-trangThai="{ row }">
            <el-dropdown trigger="click" @command="status => handleUpdateStatus(row, status)" :disabled="row.trangThai === 0 || row.trangThai === 3">
              <el-tag :type="getStatusTag(row.trangThai)" round size="small" :class="{ 'cursor-pointer': row.trangThai !== 0 && row.trangThai !== 3 }">
                {{ getStatusLabel(row.trangThai) }}
              </el-tag>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item :command="1" :disabled="row.trangThai === 1">Sắp chiếu</el-dropdown-item>
                  <el-dropdown-item :command="2" :disabled="row.trangThai === 2">Đang chiếu</el-dropdown-item>
                  <el-dropdown-item :command="3" :disabled="row.trangThai === 3">Kết thúc</el-dropdown-item>
                  <el-dropdown-item :command="0" :disabled="row.trangThai === 0">Đã hủy</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>

          <template #actions="{ row }">
            <div class="d-flex justify-content-center align-items-center gap-1">
              <el-tooltip content="Xem chi tiết" placement="top">
                <button class="btn-action-icon action-view" @click="handleView(row)">
                  <i class="bi bi-eye"></i>
                </button>
              </el-tooltip>
              <el-tooltip content="Chỉnh sửa" placement="top">
                <button class="btn-action-icon action-edit" :disabled="row.trangThai === 0 || row.trangThai === 3" @click="openDialog(row)">
                  <i class="bi bi-pencil"></i>
                </button>
              </el-tooltip>
              <el-switch
                :model-value="row.trangThai === 1 || row.trangThai === 2"
                :disabled="row.trangThai === 3"
                @change="(val) => handleUpdateStatus(row, val ? 1 : 0)"
                class="status-switch mx-1"
                active-color="#ff4949"
                inactive-color="#ff4949"
              />
            </div>
          </template>
        </BaseTable>
      </template>
    </AdminTableLayout>

    <!-- Detail Modal -->
    <BaseModal v-model="detailVisible" title="Chi tiết suất chiếu" icon="bi bi-clock-history" width="580px" isDetail>
      <div v-if="selectedShowtime" class="p-0">
        <!-- Hero Movie Header (Colorless) -->
        <div class="p-4 border-bottom bg-white">
          <div class="d-flex gap-4">
            <div class="poster-card shadow-sm border" style="width: 100px; height: 150px; flex-shrink: 0;">
              <img v-if="selectedShowtime.poster" :src="selectedShowtime.poster" class="rounded-3 h-100 w-100 object-fit-cover" />
              <div v-else class="rounded-3 bg-light d-flex align-items-center justify-content-center h-100 w-100"><i class="bi bi-film fs-1"></i></div>
            </div>
            <div class="hero-info flex-grow-1 d-flex flex-column justify-content-center">
              <div class="d-flex align-items-center gap-2 mb-2">
                <el-tag type="info" effect="plain" round size="small">{{ selectedShowtime.loaiPhim || '2D' }}</el-tag>
                <el-tag type="info" effect="plain" round size="small">
                  {{ getStatusLabel(selectedShowtime.trangThai) }}
                </el-tag>
              </div>
              <h3 class="fw-bold m-0 lh-sm text-dark">{{ selectedShowtime.tenPhim }}</h3>
              <div class="small text-secondary mt-1"><i class="bi bi-stopwatch me-1"></i>Thời lượng: {{ selectedShowtime.thoiLuong }} phút</div>
            </div>
          </div>
        </div>

        <!-- Dashboard (Colorless) -->
        <div class="p-4 bg-white">
           <h6 class="text-uppercase small fw-bold text-secondary mb-3">Hiệu suất vận hành</h6>
           <div class="row g-3 mb-4">
             <div class="col-6">
                <div class="p-3 border bg-white">
                   <div class="tiny-label text-secondary small mb-1">DOANH THU ƯỚC TÍNH</div>
                   <div class="fw-bold fs-4 text-dark">{{ formatCurrency((selectedShowtime.tongGhe - selectedShowtime.soGheTrong) * (selectedShowtime.giaVe || 75000)) }}</div>
                </div>
             </div>
             <div class="col-6">
                <div class="p-3 border bg-white">
                   <div class="tiny-label text-secondary small mb-1">TỶ LỆ LẤP ĐẦY</div>
                   <div class="fw-bold fs-4 text-dark">{{ Math.round(((selectedShowtime.tongGhe - selectedShowtime.soGheTrong) / selectedShowtime.tongGhe) * 100) || 0 }}%</div>
                </div>
             </div>
           </div>

           <h6 class="text-uppercase small fw-bold text-secondary mb-3">Lịch trình & Địa điểm</h6>
           <div class="row g-3">
             <div class="col-12">
                <div class="p-3 bg-white border d-flex align-items-center justify-content-between">
                   <div class="text-center flex-grow-1">
                      <div class="small text-secondary">BẮT ĐẦU</div>
                      <div class="fs-5 text-dark fw-bold">{{ selectedShowtime.gioBatDau }}</div>
                   </div>
                   <div class="text-secondary px-3"><i class="bi bi-arrow-right"></i></div>
                   <div class="text-center flex-grow-1">
                      <div class="small text-secondary">KẾT THÚC</div>
                      <div class="fs-5 text-dark fw-bold">{{ selectedShowtime.gioKetThuc }}</div>
                   </div>
                </div>
             </div>
             <div class="col-6">
                <div class="p-3 border bg-white h-100">
                   <label class="text-secondary small d-block mb-1">Ngày chiếu</label>
                   <div class="fw-bold text-dark">{{ formatDate(selectedShowtime.ngayChieu) }}</div>
                </div>
             </div>
             <div class="col-6">
                <div class="p-3 border bg-white h-100">
                   <label class="text-secondary small d-block mb-1">Phòng chiếu</label>
                   <div class="fw-bold text-dark">{{ selectedShowtime.tenPhongChieu }}</div>
                </div>
             </div>
             <div class="col-12">
                <div class="p-3 bg-white border">
                   <div class="d-flex justify-content-between align-items-center mb-1">
                      <span class="fw-bold small text-secondary">TRẠNG THÁI GHẾ</span>
                      <span class="text-dark fw-bold">{{ selectedShowtime.soGheTrong }} / {{ selectedShowtime.tongGhe }} ghế trống</span>
                   </div>
                   <div class="bg-light" style="height: 6px;">
                      <div class="bg-dark h-100" :style="{ width: ((selectedShowtime.soGheTrong / selectedShowtime.tongGhe) * 100) + '%' }"></div>
                   </div>
                </div>
             </div>
           </div>
        </div>
      </div>
    </BaseModal>

    <!-- Add/Edit Modal -->
    <BaseModal
      v-model="dialogVisible"
      :title="editingId ? 'Cập nhật suất chiếu' : 'Thêm suất chiếu mới'"
      icon="bi bi-pencil-square"
      width="600px"
      :confirmText="editingId ? 'Cập nhật ngay' : 'Thêm suất chiếu'"
      :loading="saving"
      @confirm="handleSubmit"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <div class="row g-4">
          <div class="col-12">
            <el-form-item label="Phim chiếu" prop="idPhim">
              <el-select v-model="form.idPhim" class="w-100" placeholder="Chọn phim" filterable>
                <el-option v-for="p in phimList" :key="p.id"
                  :label="`${p.tenPhim} [${p.loaiPhim || '2D'}]`" :value="p.id" />
              </el-select>
            </el-form-item>
          </div>
          <div class="col-12">
            <el-form-item label="Phòng chiếu" prop="idPhongChieu">
              <el-select v-model="form.idPhongChieu" class="w-100" placeholder="Chọn phòng" filterable>
                <el-option v-for="pc in phongChieuList" :key="pc.id" :label="pc.tenPhong" :value="pc.id" />
              </el-select>
            </el-form-item>
          </div>
          <div class="col-6">
            <el-form-item label="Ngày chiếu" prop="ngayChieu">
              <el-date-picker v-model="form.ngayChieu" type="date" class="w-100"
                placeholder="Chọn ngày" value-format="YYYY-MM-DD" />
            </el-form-item>
          </div>
          <div class="col-6">
            <el-form-item label="Giờ bắt đầu" prop="gioBatDau">
              <el-time-picker v-model="form.gioBatDau" class="w-100" format="HH:mm" value-format="HH:mm:00" />
            </el-form-item>
          </div>
          <div class="col-6">
            <el-form-item label="Giờ kết thúc (dự kiến)">
              <el-time-picker v-model="form.gioKetThuc" class="w-100" format="HH:mm" value-format="HH:mm:00" readonly disabled />
              <div class="small text-secondary mt-1">Đã tính 15 phút dọn dẹp</div>
            </el-form-item>
          </div>

        </div>
      </el-form>
    </BaseModal>

    <!-- ══════════════════════════════════════════════════
         MODAL: Tạo hàng loạt suất chiếu tự động
         ══════════════════════════════════════════════════ -->
    <el-dialog
      v-model="batchDialogVisible"
      title=""
      width="680px"
      :close-on-click-modal="false"
      class="batch-gen-dialog"
      destroy-on-close
    >
      <!-- Custom header -->
      <template #header>
        <div class="batch-dialog-header">
          <div class="batch-header-icon">
            <i class="bi bi-magic"></i>
          </div>
          <div>
            <div class="batch-header-title">Tạo suất chiếu tự động</div>
            <div class="batch-header-sub">Hệ thống tự gen nhiều suất / ngày, tự kiểm tra trùng lịch phòng</div>
          </div>
        </div>
      </template>

      <!-- Form body -->
      <el-form
        v-if="!batchResult"
        ref="batchFormRef"
        label-position="top"
        class="batch-form"
      >
        <!-- Row 1: Phim + Phòng -->
        <div class="row g-3">
          <div class="col-7">
            <el-form-item label="Phim chiếu" prop="idPhim">
              <el-select
                v-model="batchForm.idPhim"
                class="w-100"
                placeholder="Chọn phim..."
                filterable
                @change="onBatchPhimChange"
              >
                <el-option
                  v-for="p in phimList"
                  :key="p.id"
                  :value="p.id"
                  :label="p.tenPhim"
                >
                  <div class="d-flex align-items-center gap-2">
                    <img v-if="p.poster" :src="p.poster" style="width:24px;height:32px;object-fit:cover;border-radius:3px;" />
                    <div v-else style="width:24px;height:32px;background:#eee;border-radius:3px;"></div>
                    <div>
                      <div class="fw-semibold" style="font-size:13px;">{{ p.tenPhim }}</div>
                      <div class="text-secondary" style="font-size:11px;">{{ p.thoiLuong }} phút · {{ p.loaiPhim }}</div>
                    </div>
                  </div>
                </el-option>
              </el-select>
              <!-- Hiển thị thông tin phim đã chọn -->
              <div v-if="selectedBatchPhim" class="batch-phim-info">
                <i class="bi bi-clock me-1"></i>
                Thời lượng: <strong>{{ selectedBatchPhim.thoiLuong }} phút</strong>
                <span class="mx-2">·</span>
                <i class="bi bi-film me-1"></i>
                Loại: <strong>{{ selectedBatchPhim.loaiPhim || 'Chưa xác định' }}</strong>
              </div>
            </el-form-item>
          </div>
          <div class="col-5">
            <el-form-item label="Phòng chiếu" prop="idPhongChieu">
              <el-select v-model="batchForm.idPhongChieu" class="w-100" placeholder="Chọn phòng..." filterable>
                <el-option
                  v-for="pc in phongChieuList"
                  :key="pc.id"
                  :label="pc.tenPhong"
                  :value="pc.id"
                />
              </el-select>
            </el-form-item>
          </div>
        </div>

        <!-- Row 2: Khoảng ngày -->
        <div class="row g-3">
          <div class="col-6">
            <el-form-item label="Từ ngày" prop="tuNgay">
              <el-date-picker
                v-model="batchForm.tuNgay"
                type="date"
                class="w-100"
                placeholder="Ngày bắt đầu"
                value-format="YYYY-MM-DD"
                :disabled-date="disabledBeforeToday"
              />
            </el-form-item>
          </div>
          <div class="col-6">
            <el-form-item label="Đến ngày" prop="denNgay">
              <el-date-picker
                v-model="batchForm.denNgay"
                type="date"
                class="w-100"
                placeholder="Ngày kết thúc"
                value-format="YYYY-MM-DD"
                :disabled-date="disabledBeforeStart"
              />
            </el-form-item>
          </div>
        </div>

        <!-- Row 3: Buffer + Trạng thái -->
        <div class="row g-3">
          <div class="col-6">
            <el-form-item label="Buffer giữa các suất (phút)">
              <el-input-number
                v-model="batchForm.bufferPhut"
                :min="0"
                :max="60"
                :step="5"
                class="w-100"
                controls-position="right"
              />
              <div class="batch-hint">Quảng cáo + dọn phòng (thường 15–20 phút)</div>
            </el-form-item>
          </div>

        </div>

        <!-- Row 4: Danh sách giờ chiếu -->
        <el-form-item label="Khung giờ trong ngày" prop="danhSachGioBatDau">
          <div class="slot-container">
            <div
              v-for="(slot, idx) in batchForm.danhSachGioBatDau"
              :key="idx"
              class="slot-item"
            >
              <el-time-picker
                v-model="batchForm.danhSachGioBatDau[idx]"
                format="HH:mm"
                value-format="HH:mm:ss"
                placeholder="--:--"
                class="slot-time-picker"
              />
              <!-- Hiển thị end time dự kiến -->
              <span v-if="selectedBatchPhim && slot" class="slot-end-preview">
                → {{ calcEndTime(slot) }}
              </span>
              <button
                type="button"
                class="slot-remove-btn"
                @click="removeSlot(idx)"
                :disabled="batchForm.danhSachGioBatDau.length <= 1"
              >
                <i class="bi bi-x-lg"></i>
              </button>
            </div>
            <button type="button" class="slot-add-btn" @click="addSlot">
              <i class="bi bi-plus-circle me-1"></i> Thêm khung giờ
            </button>
          </div>

          <!-- Preview số suất sẽ tạo -->
          <div v-if="batchPreviewCount > 0" class="batch-preview-count">
            <i class="bi bi-lightning-charge-fill me-1"></i>
            Dự kiến tạo tối đa
            <strong>{{ batchPreviewCount }} suất chiếu</strong>
            ({{ dayCount }} ngày × {{ batchForm.danhSachGioBatDau.filter(Boolean).length }} khung giờ)
          </div>
        </el-form-item>
      </el-form>

      <!-- Kết quả sau khi generate -->
      <div v-if="batchResult" class="batch-result">
        <!-- Summary cards -->
        <div class="batch-result-cards">
          <div class="result-card result-card-total">
            <div class="result-card-icon"><i class="bi bi-collection"></i></div>
            <div class="result-card-num">{{ batchResult.tongSlot }}</div>
            <div class="result-card-label">Tổng slot</div>
          </div>
          <div class="result-card result-card-success">
            <div class="result-card-icon"><i class="bi bi-check-circle-fill"></i></div>
            <div class="result-card-num">{{ batchResult.taoThanhCong }}</div>
            <div class="result-card-label">Đã tạo</div>
          </div>
          <div class="result-card result-card-skip">
            <div class="result-card-icon"><i class="bi bi-skip-forward-circle"></i></div>
            <div class="result-card-num">{{ batchResult.boBoBiTrung }}</div>
            <div class="result-card-label">Bỏ qua (trùng)</div>
          </div>
        </div>

        <!-- Success message -->
        <el-alert
          v-if="batchResult.taoThanhCong > 0"
          :title="`✅ Đã tạo thành công ${batchResult.taoThanhCong} suất chiếu!`"
          type="success"
          :closable="false"
          show-icon
          class="mb-3"
        />
        <el-alert
          v-if="batchResult.boBoBiTrung > 0"
          :title="`⚠️ ${batchResult.boBoBiTrung} slot bị bỏ qua do trùng lịch phòng`"
          type="warning"
          :closable="false"
          show-icon
          class="mb-3"
        />

        <!-- Danh sách slot bị trùng -->
        <el-collapse v-if="batchResult.danhSachBiTrung?.length > 0" class="batch-skip-collapse">
          <el-collapse-item :title="`Xem chi tiết ${batchResult.boBoBiTrung} slot bị bỏ qua`" name="skipped">
            <div class="skip-list">
              <div
                v-for="(skip, i) in batchResult.danhSachBiTrung"
                :key="i"
                class="skip-item"
              >
                <i class="bi bi-x-circle text-danger me-2"></i>
                <span class="skip-date">{{ formatDate(skip.ngay) }}</span>
                <el-tag type="danger" size="small" effect="plain" class="ms-2">
                  {{ formatTime(skip.gioBatDau) }} → {{ formatTime(skip.gioKetThuc) }}
                </el-tag>
                <span class="ms-2 text-secondary small">{{ skip.lyDo }}</span>
              </div>
            </div>
          </el-collapse-item>
        </el-collapse>
      </div>

      <!-- Footer -->
      <template #footer>
        <div class="batch-dialog-footer">
          <template v-if="!batchResult">
            <el-button @click="batchDialogVisible = false">Hủy</el-button>
            <el-button
              type="warning"
              :icon="MagicStick"
              :loading="batchSaving"
              @click="handleBatchSubmit"
              id="btn-confirm-batch-generate"
            >
              Tạo tự động ngay
            </el-button>
          </template>
          <template v-else>
            <el-button @click="closeBatchDialog">Đóng</el-button>
            <el-button type="primary" @click="resetBatchForm">
              <i class="bi bi-arrow-repeat me-1"></i> Tạo đợt mới
            </el-button>
          </template>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { Search, Monitor, Filter, MagicStick } from '@element-plus/icons-vue';
import { suatChieuService } from '@/services/api/admin/suatChieuService';
import notification from '@/utils/notifications';
import confirmDialog from '@/utils/confirm';
import dayjs from 'dayjs';
import AdminTableLayout from '@/components/AdminTableLayout.vue';
import BaseModal from '@/components/common/BaseModal.vue';
import BaseTable from '@/components/common/BaseTable.vue';
import ExcelActions from '@/components/common/ExcelActions.vue';

// ── State ─────────────────────────────────────────────────────────────────────
const loading  = ref(false);
const saving   = ref(false);
const batchSaving = ref(false);
const dialogVisible = ref(false);
const detailVisible = ref(false);
const batchDialogVisible = ref(false);
const batchResult = ref(null);
const selectedShowtime = ref(null);
const editingId = ref(null);

const handleView = (row) => {
  selectedShowtime.value = row;
  detailVisible.value = true;
};
const formRef  = ref(null);
const batchFormRef = ref(null);

const showtimes      = ref([]);
const phongChieuList = ref([]);
const phimList       = ref([]);

const filterDate     = ref(null);
const filterRoom     = ref(null);
const filterTrangThai = ref('');
const searchQuery    = ref('');
const currentPage    = ref(1);
const pageSize       = ref(5);
const selectedShowtimes = ref([]);

const form = ref({
  idPhim: '',
  idPhongChieu: '',
  ngayChieu: null,
  gioBatDau: null,
  gioKetThuc: null,
  trangThai: 1
});

const batchForm = ref({
  idPhim: '',
  idPhongChieu: '',
  tuNgay: null,
  denNgay: null,
  danhSachGioBatDau: ['08:00:00', '11:00:00', '14:00:00', '18:00:00'],
  bufferPhut: 20,
  trangThai: 1
});

const rules = {
  idPhim:       [{ required: true, message: 'Vui lòng chọn phim', trigger: 'change' }],
  idPhongChieu: [{ required: true, message: 'Vui lòng chọn phòng chiếu', trigger: 'change' }],
  ngayChieu:    [{ required: true, message: 'Vui lòng chọn ngày chiếu',  trigger: 'change' }],
  gioBatDau:    [{ required: true, message: 'Vui lòng chọn giờ bắt đầu', trigger: 'change' }],
};

const batchRules = {
  idPhim:       [{ required: true, message: 'Vui lòng chọn phim',        trigger: 'change' }],
  idPhongChieu: [{ required: true, message: 'Vui lòng chọn phòng chiếu', trigger: 'change' }],
  tuNgay:       [{ required: true, message: 'Vui lòng chọn ngày bắt đầu', trigger: 'change' }],
  denNgay:      [{ required: true, message: 'Vui lòng chọn ngày kết thúc', trigger: 'change' }],
  danhSachGioBatDau: [
    { required: true, message: 'Cần ít nhất 1 khung giờ', trigger: 'change' },
    {
      validator: (rule, value, cb) => {
        const valid = value && value.filter(Boolean).length > 0;
        valid ? cb() : cb(new Error('Vui lòng nhập ít nhất 1 khung giờ hợp lệ'));
      },
      trigger: 'change'
    }
  ]
};

// ── Watch auto end time calculation ──
import { watch } from 'vue';
watch([() => form.value.idPhim, () => form.value.gioBatDau], ([newPhimId, newStart]) => {
  if (newPhimId && newStart) {
    const phim = phimList.value.find(p => p.id === newPhimId);
    if (phim?.thoiLuong) {
      const [h, m] = newStart.split(':').map(Number);
      const total = h * 60 + m + phim.thoiLuong + 15;
      form.value.gioKetThuc =
        `${String(Math.floor(total / 60) % 24).padStart(2,'0')}:${String(total % 60).padStart(2,'0')}:00`;
    }
  }
});

// ── Columns ───────────────────────────────────────────────────────────────────
const columns = [
  { label: 'STT',         key: 'index',      width: '60px'    },
  { label: 'Phòng chiếu', key: 'phongChieu', width: '150px'   },
  { label: 'Ngày chiếu',  key: 'ngayChieu',  width: '130px'   },
  { label: 'Giờ chiếu',   key: 'gioChieu',   width: '160px'   },
  { label: 'Trống',       key: 'soGheTrong', width: '90px'    },
  { label: 'Trạng thái',  key: 'trangThai',  width: '130px'   },
];

// ── Computed ──────────────────────────────────────────────────────────────────
const filteredShowtimes = computed(() =>
  showtimes.value.filter(s => {
    const matchRoom   = !filterRoom.value   || s.idPhongChieu === filterRoom.value;
    const matchStatus  = filterTrangThai.value === '' || s.trangThai === filterTrangThai.value;
    const matchDate   = !filterDate.value   || s.ngayChieu === filterDate.value;
    const matchSearch = !searchQuery.value  || s.tenPhim?.toLowerCase().includes(searchQuery.value.toLowerCase());
    return matchRoom && matchStatus && matchDate && matchSearch;
  })
);

const paginatedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  return filteredShowtimes.value.slice(start, start + pageSize.value);
});

/** Phim đang chọn trong batch form */
const selectedBatchPhim = computed(() =>
  phimList.value.find(p => p.id === batchForm.value.idPhim) || null
);

/** Số ngày trong khoảng */
const dayCount = computed(() => {
  if (!batchForm.value.tuNgay || !batchForm.value.denNgay) return 0;
  const diff = dayjs(batchForm.value.denNgay).diff(dayjs(batchForm.value.tuNgay), 'day') + 1;
  return diff > 0 ? diff : 0;
});

/** Số suất dự kiến tạo tối đa */
const batchPreviewCount = computed(() => {
  const validSlots = batchForm.value.danhSachGioBatDau.filter(Boolean).length;
  return dayCount.value * validSlots;
});

// ── Methods ───────────────────────────────────────────────────────────────────
async function fetchShowtimes() {
  loading.value = true;
  try {
    const params = {};
    if (filterDate.value) params.ngayChieu    = filterDate.value;
    if (filterRoom.value) params.idPhongChieu = filterRoom.value;
    const res = await suatChieuService.getShowtimes(params);
    showtimes.value = res.data?.data || [];
  } catch {
    notification.error('Không thể tải danh sách suất chiếu');
  } finally {
    loading.value = false;
  }
}

const resetFilter = () => {
  filterDate.value = null; filterRoom.value = null;
  filterTrangThai.value = ''; searchQuery.value = '';
  currentPage.value = 1;
  fetchShowtimes();
};

const openDialog = (row = null) => {
  editingId.value = row?.id || null;
  form.value = row
    ? { idPhim: row.idPhim, idPhongChieu: row.idPhongChieu, ngayChieu: row.ngayChieu,
        gioBatDau: row.gioBatDau, gioKetThuc: row.gioKetThuc, trangThai: row.trangThai }
    : { idPhim: '', idPhongChieu: '', ngayChieu: null, gioBatDau: null, gioKetThuc: null, trangThai: 1 };
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (!valid) return;
    saving.value = true;
    try {
      if (editingId.value) {
        await suatChieuService.updateShowtime(editingId.value, form.value);
        notification.updateSuccess('suất chiếu');
      } else {
        await suatChieuService.createShowtime(form.value);
        notification.addSuccess('suất chiếu');
      }
      dialogVisible.value = false;
      await fetchShowtimes();
    } catch (e) {
      notification.error(e.response?.data?.message || 'Có lỗi xảy ra');
    } finally {
      saving.value = false;
    }
  });
};

const handleUpdateStatus = (row, status = null) => {
  const newStatus = status !== null ? status : (row.trangThai === 0 ? 1 : 0);
  const label = getStatusLabel(newStatus).toLowerCase();
  
  if (newStatus === row.trangThai) return;

  confirmDialog.custom(`Đổi trạng thái suất chiếu sang <b>${label}</b>?`, 'Xác nhận').then(async () => {
    try {
      await suatChieuService.updateShowtime(row.id, { ...row, trangThai: newStatus });
      notification.success('Cập nhật trạng thái thành công');
      fetchShowtimes();
    } catch { notification.error('Cập nhật thất bại'); }
  }).catch(() => {});
};

// ── Batch Generate ────────────────────────────────────────────────────────────
const openBatchDialog = () => {
  batchResult.value = null;
  batchForm.value = {
    idPhim: '',
    idPhongChieu: '',
    tuNgay: null,
    denNgay: null,
    danhSachGioBatDau: ['08:00:00', '11:00:00', '14:00:00', '18:00:00'],
    bufferPhut: 20,
    trangThai: 1
  };
  batchDialogVisible.value = true;
};

const closeBatchDialog = () => {
  batchDialogVisible.value = false;
  batchResult.value = null;
  fetchShowtimes();
};

const resetBatchForm = () => {
  batchResult.value = null;
};

const onBatchPhimChange = () => { /* reactive — selectedBatchPhim computed handles it */ };

const addSlot = () => {
  batchForm.value.danhSachGioBatDau.push('');
};

const removeSlot = (idx) => {
  if (batchForm.value.danhSachGioBatDau.length > 1) {
    batchForm.value.danhSachGioBatDau.splice(idx, 1);
  }
};

/** Tính giờ kết thúc dự kiến = gioBatDau + thoiLuong + buffer */
const calcEndTime = (timeStr) => {
  if (!timeStr || !selectedBatchPhim.value) return '';
  const [h, m] = timeStr.split(':').map(Number);
  const totalMinutes = h * 60 + m + (selectedBatchPhim.value.thoiLuong || 120) + (batchForm.value.bufferPhut || 20);
  const endH = Math.floor(totalMinutes / 60) % 24;
  const endM = totalMinutes % 60;
  return `${String(endH).padStart(2, '0')}:${String(endM).padStart(2, '0')}`;
};

const disabledBeforeToday = (time) => time < new Date(new Date().setHours(0, 0, 0, 0));
const disabledBeforeStart = (time) => {
  if (!batchForm.value.tuNgay) return false;
  return time < new Date(batchForm.value.tuNgay);
};

const handleBatchSubmit = async () => {
  if (!batchFormRef.value) return;
  await batchFormRef.value.validate(async (valid) => {
    if (!valid) return;

    const validSlots = batchForm.value.danhSachGioBatDau.filter(Boolean);
    if (validSlots.length === 0) {
      notification.error('Vui lòng thêm ít nhất 1 khung giờ hợp lệ');
      return;
    }

    batchSaving.value = true;
    try {
      const payload = {
        ...batchForm.value,
        danhSachGioBatDau: validSlots.map(t => t.length === 5 ? t + ':00' : t)
      };
      const res = await suatChieuService.generateBatch(payload);
      batchResult.value = res.data?.data || res.data;
    } catch (e) {
      notification.error(e.response?.data?.message || 'Có lỗi khi tạo suất chiếu tự động');
    } finally {
      batchSaving.value = false;
    }
  });
};

// ── Helpers ───────────────────────────────────────────────────────────────────
const getStatusTag   = (s) => ({ 0: 'danger', 1: 'primary', 2: 'success', 3: 'info' }[s] || 'info');
const getStatusLabel = (s) => ({ 0: 'Đã hủy', 1: 'Sắp chiếu', 2: 'Đang chiếu', 3: 'Kết thúc' }[s] || '—');
const formatCurrency = (v) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(v);
const formatDate     = (d) => d ? dayjs(d).format('DD/MM/YYYY') : '—';
const formatTime     = (t) => t ? String(t).substring(0, 5) : '—';

onMounted(async () => {
  const [pcRes, phimRes] = await Promise.all([
    suatChieuService.getPhongChieuDropdown(),
    suatChieuService.getPhimDropdown()
  ]);
  phongChieuList.value = pcRes.data?.data || [];
  phimList.value = phimRes.data?.data || [];
  await fetchShowtimes();
});
</script>

<style scoped>
.admin-showtimes-page {
  height: 100%;
  display: flex;
  flex-direction: column;
}
.admin-showtimes-page :deep(.admin-table-layout) { flex: 1; }

/* Auto-generate button */
.btn-auto-gen {
  font-weight: 600;
  letter-spacing: 0.3px;
}

/* ── Batch Dialog Styles ──────────────────────────────────────────────────── */
:deep(.batch-gen-dialog .el-dialog__header) {
  padding: 20px 24px 0;
  border-bottom: none;
}
:deep(.batch-gen-dialog .el-dialog__body) {
  padding: 16px 24px 8px;
}
:deep(.batch-gen-dialog .el-dialog__footer) {
  padding: 12px 24px 20px;
  border-top: 1px solid #f0f0f0;
}

.batch-dialog-header {
  display: flex;
  align-items: center;
  gap: 14px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 4px;
}
.batch-header-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: linear-gradient(135deg, #f39c12, #e67e22);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(243, 156, 18, 0.35);
}
.batch-header-title {
  font-size: 17px;
  font-weight: 700;
  color: #1a1a2e;
}
.batch-header-sub {
  font-size: 13px;
  color: #888;
  margin-top: 2px;
}

/* Batch form */
.batch-form {
  margin-top: 8px;
}
.batch-phim-info {
  margin-top: 6px;
  font-size: 12px;
  color: #555;
  background: #f8f9ff;
  border: 1px solid #e8eaf6;
  border-radius: 6px;
  padding: 6px 10px;
}
.batch-hint {
  font-size: 11.5px;
  color: #aaa;
  margin-top: 4px;
}

/* Slot time picker list */
.slot-container {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.slot-item {
  display: flex;
  align-items: center;
  gap: 10px;
  background: #fafafa;
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 8px 12px;
  transition: border-color 0.2s;
}
.slot-item:hover {
  border-color: #f0a500;
}
.slot-time-picker {
  width: 130px !important;
}
.slot-end-preview {
  font-size: 12px;
  color: #409eff;
  font-weight: 600;
  white-space: nowrap;
}
.slot-remove-btn {
  background: none;
  border: none;
  color: #ccc;
  cursor: pointer;
  padding: 4px 6px;
  border-radius: 4px;
  font-size: 12px;
  margin-left: auto;
  transition: color 0.2s, background 0.2s;
}
.slot-remove-btn:hover:not(:disabled) {
  color: #f56c6c;
  background: #fff0f0;
}
.slot-remove-btn:disabled {
  cursor: not-allowed;
  opacity: 0.35;
}
.slot-add-btn {
  background: none;
  border: 2px dashed #e0e0e0;
  border-radius: 8px;
  padding: 8px 16px;
  color: #888;
  cursor: pointer;
  font-size: 13px;
  text-align: left;
  transition: all 0.2s;
}
.slot-add-btn:hover {
  border-color: #f0a500;
  color: #f0a500;
  background: #fffbf0;
}

/* Preview count */
.batch-preview-count {
  margin-top: 10px;
  padding: 8px 14px;
  background: linear-gradient(135deg, #fff8e1, #fff3cd);
  border: 1px solid #ffe082;
  border-radius: 8px;
  font-size: 13px;
  color: #795548;
}
.batch-preview-count strong {
  color: #e65100;
  font-size: 15px;
}

/* Footer buttons */
.batch-dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* ── Result section ───────────────────────────────────────────────────────── */
.batch-result {
  padding: 4px 0;
}
.batch-result-cards {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}
.result-card {
  flex: 1;
  border-radius: 12px;
  padding: 16px;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}
.result-card-total  { background: #f8f9fa; border: 1px solid #dee2e6; }
.result-card-success { background: #f0fff4; border: 1px solid #b7ebc8; }
.result-card-skip   { background: #fff8e1; border: 1px solid #ffe082; }

.result-card-icon { font-size: 22px; margin-bottom: 4px; }
.result-card-total  .result-card-icon { color: #6c757d; }
.result-card-success .result-card-icon { color: #28a745; }
.result-card-skip   .result-card-icon { color: #f39c12; }

.result-card-num {
  font-size: 28px;
  font-weight: 800;
  line-height: 1;
}
.result-card-total  .result-card-num { color: #495057; }
.result-card-success .result-card-num { color: #28a745; }
.result-card-skip   .result-card-num { color: #f39c12; }

.result-card-label {
  font-size: 12px;
  color: #888;
  font-weight: 500;
}

/* Skipped list */
.batch-skip-collapse { margin-top: 4px; }
.skip-list { max-height: 200px; overflow-y: auto; display: flex; flex-direction: column; gap: 6px; }
.skip-item { display: flex; align-items: center; padding: 6px 10px; background: #fff5f5; border-radius: 6px; font-size: 13px; }
.skip-date { font-weight: 600; color: #555; }
@keyframes pulse { 0% { transform: scale(1); opacity: 0.6; } 100% { transform: scale(3); opacity: 0; } }

.section-divider-sm { display: flex; align-items: center; color: #94a3b8; font-size: 11px; font-weight: 800; text-transform: uppercase; letter-spacing: 1px; }
.section-divider-sm::after { content: ''; flex-grow: 1; height: 1px; background: #f1f5f9; margin-left: 10px; }
.stat-card { transition: all 0.2s; }
.stat-card:hover { border-color: var(--el-color-primary) !important; transform: translateY(-2px); }
.dot { width: 8px; height: 8px; border-radius: 50%; }
</style>
