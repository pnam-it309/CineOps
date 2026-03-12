<template>
  <div class="schedule-page">
    <!-- ── Dòng 1: Title ── -->
    <div class="schedule-page-header">
      <div class="d-flex align-items-center gap-2">
        <i class="bi bi-calendar-week-fill fs-4 text-danger"></i>
        <h2 class="fw-bold mb-0" style="font-size: 20px; color: #1e293b">
          Quản lý lịch chiếu
        </h2>
      </div>
    </div>

    <!-- ── Khung chính ── -->
    <div class="schedule-main-card">
      <!-- Tab bar -->
      <div class="schedule-tab-bar">
        <div class="schedule-tabs">
          <button
            class="schedule-tab-btn"
            :class="{ active: activeTab === 'visual' }"
            @click="activeTab = 'visual'"
          >
            <i class="bi bi-grid-3x3-gap me-1"></i> Sơ đồ lịch chiếu
          </button>
          <button
            class="schedule-tab-btn"
            :class="{ active: activeTab === 'list' }"
            @click="activeTab = 'list'"
          >
            <i class="bi bi-list-ul me-1"></i> Danh sách lịch chiếu
          </button>
        </div>
      </div>

      <!-- Filter bar (chỉ tab list) -->
      <div v-if="activeTab === 'list'" class="schedule-filter-bar">
        <el-input
          v-model="searchQuery"
          placeholder="Tìm tên phim..."
          :prefix-icon="Search"
          clearable
          @input="handleSearch"
          style="width: 230px"
        />
        <el-date-picker
          v-model="filterDate"
          type="date"
          placeholder="Chọn ngày chiếu"
          format="DD/MM/YYYY"
          value-format="YYYY-MM-DD"
          style="width: 180px"
          @change="handleSearch"
          clearable
        />
        <el-select
          v-model="filterRoomId"
          placeholder="Chọn phòng"
          style="width: 180px"
          @change="handleSearch"
          clearable
        >
          <el-option label="Tất cả phòng" value="" />
          <el-option
            v-for="r in phongChieuList"
            :key="r.id"
            :label="r.tenPhong || r.ten_phong"
            :value="r.id"
          />
        </el-select>
        <el-select
          v-model="filterStatus"
          placeholder="Chọn trạng thái"
          style="width: 180px"
          @change="handleSearch"
          clearable
        >
          <el-option label="Tất cả trạng thái" value="" />
          <el-option label="Sắp chiếu" :value="1" />
          <el-option label="Đang chiếu" :value="2" />
          <el-option label="Đã hủy" :value="0" />
          <el-option label="Kết thúc" :value="3" />
        </el-select>
        <el-button
          @click="resetFilter"
          :icon="Refresh"
          plain
          class="rounded-3 ms-auto"
          >Đặt lại</el-button
        >
      </div>

      <!-- Content -->
      <div class="schedule-content">
        <!-- Tab Sơ đồ -->
        <div v-if="activeTab === 'visual'" class="h-100">
          <ScheduleVisual
            :showtimes="showtimes"
            :phongChieuList="phongChieuList"
            :phimList="phimList"
            :allKhungGio="allKhungGio"
            @view="handleView"
            @addShowtime="handleAddShowtime"
            @weekChange="fetchShowtimes"
          />
        </div>

        <!-- Tab Danh sách -->
        <div v-else class="h-100 d-flex flex-column">
          <BaseTable
            :data="paginatedShowtimes"
            :columns="listColumns"
            :loading="loading"
            :total="filteredShowtimes.length"
            v-model:currentPage="currentPage"
            v-model:pageSize="pageSize"
            :hide-pagination="true"
            @edit="openDialog"
            @delete="handleUpdateStatus"
          >
            <template #cell-stt="{ index }">
              <span class="small fw-bold text-secondary">{{
                (currentPage - 1) * pageSize + index + 1
              }}</span>
            </template>
            <template #cell-phong="{ row }">
              <div class="fw-bold text-dark small">
                <i class="bi bi-door-open me-1 text-primary"></i
                >{{ row.tenPhongChieu || row.ten_phong_chieu }}
              </div>
            </template>
            <template #cell-ngay="{ row }">
              <span class="small fw-semibold">{{
                formatDate(row.ngayChieu || row.ngay_chieu)
              }}</span>
            </template>
            <template #cell-gio="{ row }">
              <div class="small d-flex align-items-center gap-1">
                <el-tag
                  type="info"
                  effect="plain"
                  size="small"
                  class="fw-bold"
                  >{{ row.gioBatDau || row.gio_bat_dau }}</el-tag
                >
                <span class="text-secondary">→</span>
                <el-tag type="info" effect="plain" size="small">{{
                  row.gioKetThuc || row.gio_ket_thuc
                }}</el-tag>
              </div>
            </template>
            <template #cell-ghe="{ row }">
              <el-tag
                :type="
                  (row.soGheTrong ?? row.so_ghe_trong) > 10
                    ? 'success'
                    : 'warning'
                "
                size="small"
                round
              >
                {{ row.soGheTrong ?? row.so_ghe_trong }} ghế
              </el-tag>
            </template>
            <template #cell-status="{ row }">
              <el-tag
                :type="getStatusTag(row.trangThai ?? row.trang_thai)"
                size="small"
                round
              >
                {{ getStatusLabel(row.trangThai ?? row.trang_thai) }}
              </el-tag>
            </template>
            <template #actions="{ row }">
              <div class="d-flex justify-content-center gap-1">
                <el-tooltip content="Xem chi tiết" placement="top">
                  <button
                    class="btn-action-icon action-view"
                    @click="handleView(row)"
                  >
                    <i class="bi bi-eye"></i>
                  </button>
                </el-tooltip>
                <el-tooltip content="Chỉnh sửa" placement="top">
                  <button
                    class="btn-action-icon action-edit"
                    :disabled="
                      (row.trangThai ?? row.trang_thai) === 0 ||
                      (row.trangThai ?? row.trang_thai) === 3
                    "
                    @click="openDialog(row)"
                  >
                    <i class="bi bi-pencil"></i>
                  </button>
                </el-tooltip>
              </div>
            </template>
          </BaseTable>

          <!-- Pagination -->
          <div class="schedule-pagination border-top px-4 py-2">
            <div class="pagination-footer-row">
              <div class="footer-left">
                <span class="pagination-info-text"
                  >Hiển thị {{ startRecord }}–{{ endRecord }} /
                  {{ filteredShowtimes.length }} bản ghi</span
                >
                <el-select
                  :model-value="pageSize"
                  size="default"
                  style="width: 160px"
                  @update:model-value="
                    (v) => {
                      pageSize = v;
                      currentPage = 1;
                    }
                  "
                >
                  <el-option label="5 dòng / trang" :value="5" />
                  <el-option label="10 dòng / trang" :value="10" />
                  <el-option label="20 dòng / trang" :value="20" />
                  <el-option label="50 dòng / trang" :value="50" />
                </el-select>
              </div>
              <div class="footer-center">
                <el-button
                  class="nav-btn-circle"
                  @click="currentPage--"
                  :disabled="currentPage <= 1"
                  circle
                  ><el-icon><ArrowLeft /></el-icon
                ></el-button>
                <el-button
                  v-for="p in pagesToShow"
                  :key="p"
                  class="nav-btn-page"
                  :class="{ 'is-active': currentPage === p }"
                  @click="currentPage = p"
                  >{{ p }}</el-button
                >
                <el-button
                  class="nav-btn-circle"
                  @click="currentPage++"
                  :disabled="currentPage >= totalPages"
                  circle
                  ><el-icon><ArrowRight /></el-icon
                ></el-button>
              </div>
              <div class="footer-right">
                Trang <strong>{{ currentPage }}</strong> /
                <strong>{{ totalPages }}</strong>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal: Chi tiết -->
    <BaseModal
      v-model="detailVisible"
      title="Chi tiết lịch chiếu"
      icon="bi bi-calendar-event"
      width="550px"
    >
      <div v-if="selectedShowtime" class="p-2">
        <div class="d-flex gap-4 mb-4 pb-4 border-bottom">
          <img
            v-if="selectedShowtime.poster"
            :src="selectedShowtime.poster"
            class="rounded-3 shadow-sm"
            style="width: 100px; height: 140px; object-fit: cover"
          />
          <div
            v-else
            class="rounded-3 bg-light d-flex align-items-center justify-content-center border"
            style="width: 100px; height: 140px"
          >
            <i class="bi bi-film text-secondary fs-1"></i>
          </div>
          <div class="flex-grow-1">
            <h4 class="fw-bold text-dark mb-1">
              {{ selectedShowtime.tenPhim || selectedShowtime.ten_phim }}
            </h4>
            <div class="d-flex align-items-center gap-2">
              <el-tag
                :type="
                  getStatusTag(
                    selectedShowtime.trangThai ?? selectedShowtime.trang_thai,
                  )
                "
                round
                size="small"
                >{{
                  getStatusLabel(
                    selectedShowtime.trangThai ?? selectedShowtime.trang_thai,
                  )
                }}</el-tag
              >
              <el-tag
                type="danger"
                effect="dark"
                round
                size="small"
                v-if="selectedShowtime.loaiPhim || selectedShowtime.loai_phim"
                >{{
                  selectedShowtime.loaiPhim || selectedShowtime.loai_phim
                }}</el-tag
              >
              <span class="text-secondary small"
                ><i class="bi bi-clock me-1"></i
                >{{
                  selectedShowtime.thoiLuong || selectedShowtime.thoi_luong
                }}
                phút</span
              >
            </div>
          </div>
        </div>
        <div class="bg-light p-3 rounded-4 mb-4">
          <div class="small text-secondary mb-1">Phòng chiếu</div>
          <div class="fw-bold text-dark">
            <i class="bi bi-door-open me-2 text-primary"></i>
            {{
              selectedShowtime.tenPhongChieu || selectedShowtime.ten_phong_chieu
            }}
            ({{
              selectedShowtime.loaiManHinh || selectedShowtime.loai_man_hinh
            }})
          </div>
        </div>
        <div class="row g-4">
          <div class="col-6">
            <div class="p-3 bg-light rounded-3">
              <div class="small text-secondary mb-1">
                <i class="bi bi-calendar3 me-2 text-primary"></i>Ngày chiếu
              </div>
              <div class="fw-bold">
                {{
                  formatDate(
                    selectedShowtime.ngayChieu || selectedShowtime.ngay_chieu,
                  )
                }}
              </div>
            </div>
          </div>
          <div class="col-6">
            <div class="p-3 bg-light rounded-3">
              <div class="small text-secondary mb-1">
                <i class="bi bi-alarm me-2 text-primary"></i>Thời gian
              </div>
              <div class="fw-bold text-primary">
                {{ selectedShowtime.gioBatDau || selectedShowtime.gio_bat_dau }}
                →
                {{
                  selectedShowtime.gioKetThuc || selectedShowtime.gio_ket_thuc
                }}
              </div>
            </div>
          </div>
        </div>
      </div>
      <template #footer></template>
    </BaseModal>

    <!-- Modal: Thêm / Cập nhật -->
    <BaseModal
      v-model="dialogVisible"
      :title="editingId ? 'Cập nhật lịch chiếu' : 'Thêm lịch chiếu mới'"
      icon="bi bi-calendar-plus"
      width="820px"
      :confirmText="editingId ? 'Cập nhật' : 'Thêm lịch chiếu'"
      :loading="saving"
      @confirm="handleSubmit"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <div class="row g-4">
          <div class="col-12">
            <el-form-item label="Phim" prop="idPhim">
              <el-select
                v-model="form.idPhim"
                class="w-100"
                placeholder="Chọn phim"
                filterable
              >
                <el-option
                  v-for="p in phimList"
                  :key="p.id"
                  :label="`${p.tenPhim} [${p.loaiPhim || '2D'}]`"
                  :value="p.id"
                />
              </el-select>
            </el-form-item>
          </div>
          <div class="col-12">
            <el-form-item label="Phòng chiếu" prop="idPhongChieu">
              <el-select
                v-model="form.idPhongChieu"
                class="w-100"
                placeholder="Chọn phòng"
                filterable
              >
                <el-option
                  v-for="pc in phongChieuList"
                  :key="pc.id"
                  :label="pc.tenPhong || pc.ten_phong"
                  :value="pc.id"
                />
              </el-select>
            </el-form-item>
          </div>
          <div class="col-6">
            <el-form-item label="Khung giờ" prop="idKhungGio">
              <el-select
                v-model="form.idKhungGio"
                class="w-100"
                placeholder="Chọn khung giờ"
                filterable
              >
                <el-option
                  v-for="kg in allKhungGio"
                  :key="kg.id"
                  :label="`${kg.tenKhungGio || kg.ten_khung_gio} (${formatTime(kg.gioBatDau || kg.gio_bat_dau)} → ${formatTime(kg.gioKetThuc || kg.gio_ket_thuc)})`"
                  :value="kg.id"
                />
              </el-select>
            </el-form-item>
          </div>
          <div class="col-6">
            <el-form-item label="Ngày chiếu" prop="ngayChieu">
              <el-date-picker
                v-model="form.ngayChieu"
                type="date"
                class="w-100"
                placeholder="Chọn ngày"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </div>
        </div>
      </el-form>
    </BaseModal>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import {
  Search,
  Refresh,
  ArrowLeft,
  ArrowRight,
} from "@element-plus/icons-vue";
import { suatChieuService } from "@/services/api/admin/suatChieuService";
import notification from "@/utils/notifications";
import confirmDialog from "@/utils/confirm";
import dayjs from "dayjs";
import debounce from "lodash/debounce";

import BaseModal from "@/components/common/BaseModal.vue";
import BaseTable from "@/components/common/BaseTable.vue";
import ScheduleVisual from "./ScheduleVisual.vue";

const activeTab = ref("visual");
const loading = ref(false);
const saving = ref(false);
const dialogVisible = ref(false);
const detailVisible = ref(false);
const selectedShowtime = ref(null);
const editingId = ref(null);
const formRef = ref(null);

const showtimes = ref([]);
const phongChieuList = ref([]);
const phimList = ref([]);
const allKhungGio = ref([]); // ← tất cả khung giờ từ DB

const searchQuery = ref("");
const filterDate = ref(null);
const filterRoomId = ref("");
const filterStatus = ref("");
const currentPage = ref(1);
const pageSize = ref(10);

const listColumns = [
  { label: "STT", key: "stt", width: "60px" },
  { label: "Phòng chiếu", key: "phong", minWidth: "150px" },
  { label: "Ngày chiếu", key: "ngay", width: "130px" },
  { label: "Giờ chiếu", key: "gio", width: "200px" },
  { label: "Ghế trống", key: "ghe", width: "120px" },
  { label: "Trạng thái", key: "status", width: "130px" },
];

const form = ref({
  idPhim: "",
  idPhongChieu: "",
  idKhungGio: "",
  ngayChieu: null,
  trangThai: 1,
});

const rules = {
  idPhim: [
    { required: true, message: "Vui lòng chọn phim", trigger: "change" },
  ],
  idPhongChieu: [
    { required: true, message: "Vui lòng chọn phòng chiếu", trigger: "change" },
  ],
  idKhungGio: [
    { required: true, message: "Vui lòng chọn khung giờ", trigger: "change" },
  ],
  ngayChieu: [
    { required: true, message: "Vui lòng chọn ngày chiếu", trigger: "change" },
  ],
};

watch(activeTab, () => {
  currentPage.value = 1;
});

// ── Fetch ──────────────────────────────────────────────────────────────────
async function fetchShowtimes() {
  loading.value = true;
  try {
    const res = await suatChieuService.getShowtimes();
    showtimes.value = res.data?.data || [];
  } catch {
    notification.error("Không thể tải danh sách lịch chiếu");
  } finally {
    loading.value = false;
  }
}

// ── Computed: list tab ─────────────────────────────────────────────────────
const filteredShowtimes = computed(() =>
  showtimes.value.filter((s) => {
    const roomId = s.idPhongChieu ?? s.id_phong_chieu;
    const status = s.trangThai ?? s.trang_thai;
    const date = s.ngayChieu ?? s.ngay_chieu;
    const tenPhim = s.tenPhim ?? s.ten_phim ?? "";
    const matchRoom = !filterRoomId.value || roomId === filterRoomId.value;
    const matchStatus =
      filterStatus.value === "" || status === filterStatus.value;
    const matchDate = !filterDate.value || date === filterDate.value;
    const matchSearch =
      !searchQuery.value ||
      tenPhim.toLowerCase().includes(searchQuery.value.toLowerCase());
    return matchRoom && matchStatus && matchDate && matchSearch;
  }),
);
const totalPages = computed(
  () => Math.ceil(filteredShowtimes.value.length / pageSize.value) || 1,
);
const pagesToShow = computed(() => {
  const total = totalPages.value,
    current = currentPage.value;
  if (total <= 5) return Array.from({ length: total }, (_, i) => i + 1);
  let start = Math.max(1, current - 2),
    end = Math.min(total, start + 4);
  if (end === total) start = Math.max(1, end - 4);
  return Array.from({ length: end - start + 1 }, (_, i) => start + i);
});
const startRecord = computed(() =>
  filteredShowtimes.value.length === 0
    ? 0
    : (currentPage.value - 1) * pageSize.value + 1,
);
const endRecord = computed(() =>
  Math.min(currentPage.value * pageSize.value, filteredShowtimes.value.length),
);
const paginatedShowtimes = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  return filteredShowtimes.value.slice(start, start + pageSize.value);
});

// ── Handlers ───────────────────────────────────────────────────────────────
const handleSearch = debounce(() => {
  currentPage.value = 1;
}, 300);
const resetFilter = () => {
  searchQuery.value = "";
  filterDate.value = null;
  filterRoomId.value = "";
  filterStatus.value = "";
  currentPage.value = 1;
  fetchShowtimes();
};
const handleView = (row) => {
  selectedShowtime.value = row;
  detailVisible.value = true;
};
const openDialog = (row = null) => {
  editingId.value = row?.id || null;
  form.value = row
    ? {
        idPhim: row.idPhim || row.id_phim,
        idPhongChieu: row.idPhongChieu || row.id_phong_chieu,
        idKhungGio: row.idKhungGio || row.id_khung_gio,
        ngayChieu: row.ngayChieu || row.ngay_chieu,
        trangThai: row.trangThai ?? row.trang_thai ?? 1,
      }
    : {
        idPhim: "",
        idPhongChieu: "",
        idKhungGio: "",
        ngayChieu: null,
        trangThai: 1,
      };
  dialogVisible.value = true;
};
const handleAddShowtime = ({ phimId, khungId, date }) => {
  openDialog(null);
  form.value.idPhim = phimId;
  form.value.idKhungGio = khungId;
  form.value.ngayChieu = date;
};
const handleUpdateStatus = (row) => {
  const cur = row.trangThai ?? row.trang_thai;
  const newStatus = cur === 0 ? 1 : 0;
  if (newStatus === cur) return;
  confirmDialog
    .custom(
      `Đổi trạng thái sang <b>${getStatusLabel(newStatus).toLowerCase()}</b>?`,
      "Xác nhận",
    )
    .then(async () => {
      try {
        await suatChieuService.updateShowtime(row.id, {
          ...row,
          trangThai: newStatus,
        });
        notification.success("Cập nhật trạng thái thành công");
        fetchShowtimes();
      } catch {
        notification.error("Cập nhật thất bại");
      }
    })
    .catch(() => {});
};
const handleSubmit = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (!valid) return;
    saving.value = true;
    try {
      if (editingId.value) {
        await suatChieuService.updateShowtime(editingId.value, form.value);
        notification.updateSuccess("lịch chiếu");
      } else {
        await suatChieuService.createShowtime(form.value);
        notification.addSuccess("lịch chiếu");
      }
      dialogVisible.value = false;
      await fetchShowtimes();
    } catch (e) {
      notification.error(e.response?.data?.message || "Có lỗi xảy ra");
    } finally {
      saving.value = false;
    }
  });
};

// ── Helpers ────────────────────────────────────────────────────────────────
const getStatusTag = (s) =>
  ({ 0: "danger", 1: "primary", 2: "success", 3: "info" })[s] || "info";
const getStatusLabel = (s) =>
  ({ 0: "Đã hủy", 1: "Sắp chiếu", 2: "Đang chiếu", 3: "Kết thúc" })[s] || "—";
const formatDate = (d) => (d ? dayjs(d).format("DD/MM/YYYY") : "—");
const formatTime = (t) => (t ? String(t).substring(0, 5) : "—");

// ── onMounted: fetch phim đầy đủ (có trang_thai, ngay_khoi_chieu) ──────────
onMounted(async () => {
  try {
    const [phongRes, phimRes, khungRes] = await Promise.all([
      suatChieuService.getPhongChieuDropdown(),
      suatChieuService.getPhimDropdown(), // trả về full field phim
      suatChieuService.getKhungGioDropdown(), // cần thêm hàm này vào service
    ]);

    // Map phong chiếu — hỗ trợ cả snake_case lẫn camelCase
    phongChieuList.value = (phongRes.data?.data || []).map((r) => ({
      ...r,
      id: r.id,
      tenPhong: r.tenPhong ?? r.ten_phong,
      loaiManHinh: r.loaiManHinh ?? r.loai_man_hinh,
      tongGhe: r.tongGhe ?? r.tong_ghe,
    }));

    // Map phim — chuẩn hoá field từ DB (snake_case)
    phimList.value = (phimRes.data?.data || []).map((p) => ({
      ...p,
      id: p.id,
      tenPhim: p.tenPhim ?? p.ten_phim,
      thoiLuong: p.thoiLuong ?? p.thoi_luong ?? 0,
      ngayKhoiChieu: p.ngayKhoiChieu ?? p.ngay_khoi_chieu ?? null,
      loaiPhim: p.loaiPhim ?? p.loai_phim ?? "2D",
      poster: p.poster ?? "",
      trangThai: p.trangThai ?? p.trang_thai ?? null,
    }));

    // Map khung giờ
    allKhungGio.value = (khungRes.data?.data || []).map((k) => ({
      ...k,
      id: k.id,
      tenKhungGio: k.tenKhungGio ?? k.ten_khung_gio,
      gioBatDau: k.gioBatDau ?? k.gio_bat_dau,
      gioKetThuc: k.gioKetThuc ?? k.gio_ket_thuc,
    }));

    await fetchShowtimes();
  } catch (e) {
    notification.error("Không thể tải dữ liệu ban đầu");
    console.error(e);
  }
});
</script>

<style scoped>
.schedule-page {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding-top: 8px;
  overflow: hidden;
}
.schedule-page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-shrink: 0;
}
.schedule-main-card {
  flex: 1;
  min-height: 0;
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.schedule-tab-bar {
  padding: 12px 16px;
  border-bottom: 1px solid #f1f5f9;
  flex-shrink: 0;
}
.schedule-tabs {
  display: flex;
  gap: 4px;
}
.schedule-tab-btn {
  display: inline-flex;
  align-items: center;
  padding: 6px 16px;
  border-radius: 6px;
  border: 1px solid #e2e8f0;
  background: transparent;
  color: #64748b;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}
.schedule-tab-btn:hover {
  border-color: #e31e24;
  color: #e31e24;
  background: #fef2f2;
}
.schedule-tab-btn.active {
  background: #fef2f2;
  border-color: #e31e24;
  color: #e31e24;
}
.schedule-filter-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: nowrap;
  padding: 12px 16px;
  border-bottom: 1px solid #f1f5f9;
  background: #f8fafc;
  flex-shrink: 0;
}
.schedule-content {
  flex: 1;
  min-height: 0;
  overflow: hidden;
}
.schedule-pagination {
  background: #fff;
  flex-shrink: 0;
}
.pagination-footer-row {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: center;
}
.footer-left {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #72777e;
  font-size: 13px;
}
.footer-center {
  display: flex;
  align-items: center;
  gap: 4px;
  justify-content: center;
}
.footer-right {
  display: flex;
  justify-content: flex-end;
  font-size: 14px;
  font-weight: 600;
  color: #72777e;
}
.nav-btn-circle {
  border: 1px solid #e2e8f0 !important;
  background: white !important;
  color: #64748b !important;
  width: 30px !important;
  height: 30px !important;
}
.nav-btn-circle:hover:not(:disabled) {
  color: #e31e24 !important;
  border-color: #e31e24 !important;
}
.nav-btn-page {
  font-weight: 600 !important;
  color: #64748b !important;
  min-width: 30px !important;
  height: 30px !important;
  font-size: 14px !important;
  border: 1px solid #e2e8f0 !important;
  background-color: white !important;
  border-radius: 8px !important;
}
.nav-btn-page:hover {
  border-color: #e31e24 !important;
  color: #e31e24 !important;
}
.nav-btn-page.is-active {
  background-color: white !important;
  color: #e31e24 !important;
  border: 1.5px solid #e31e24 !important;
  font-weight: 700 !important;
}
</style>
