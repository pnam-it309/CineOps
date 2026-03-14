<template>
  <div class="admin-food-container">
    <BaseTable title="Quản lý sản phẩm đi kèm" titleIcon="bi bi-box-seam-fill" addButtonLabel="Thêm sản phẩm"
      :data="paginatedItems" :columns="tableColumns" :loading="loading" :total="filteredItems.length"
      v-model:currentPage="currentPage" v-model:pageSize="pageSize" v-model:selection="selectedItems"
      @add-click="goToAddPage" @edit="goToEditPage" @delete="handleDelete" @reset-filter="handleReset"
      @update-status="({ row, val }) => handleDelete(row)">
      <!-- Filters Slot -->
      <template #filters>
        <div class="d-flex flex-column gap-1">
          <label class="smaller text-secondary fw-bold ms-1">Tìm kiếm sản phẩm</label>
          <el-input v-model="searchQuery" placeholder="Nhập tên sản phẩm..." :prefix-icon="Search" clearable class="w-100" />
        </div>

        <div class="d-flex flex-column gap-1">
          <label class="smaller text-secondary fw-bold ms-1">Danh mục</label>
          <el-select v-model="activeCategory" placeholder="Tất cả" class="w-100" clearable>
            <el-option label="Tất cả danh mục" value="All" />
            <el-option v-for="cat in categories" :key="cat.id" :label="cat.tenLoai" :value="cat.id" />
          </el-select>
        </div>

        <div class="d-flex flex-column gap-1">
          <label class="smaller text-secondary fw-bold ms-1">Trạng thái</label>
          <el-select v-model="filterTrangThai" placeholder="Tất cả" class="w-100" clearable>
            <el-option label="Tất cả trạng thái" value="" />
            <el-option label="Hoạt động" :value="1" />
            <el-option label="Ngừng hoạt động" :value="0" />
          </el-select>
        </div>
      </template>

      <template #cell-stt="{ index }">
        <div class="text-center fw-bold text-secondary">{{ (currentPage - 1) * pageSize + index + 1 }}</div>
      </template>

      <template #cell-image="{ row }">
        <div class="py-1 text-center">
          <img :src="row.image || 'https://placehold.co/150'" class="rounded-3 shadow-sm border"
            style="width:45px;height:45px;object-fit:cover;"
            @error="e => { e.target.src = 'https://placehold.co/150?text=No+Image'; }" />
        </div>
      </template>

      <template #cell-name="{ row }">
        <div class="fw-bold" style="color:#344767;">{{ row.name }}</div>
        <div class="small text-secondary">#{{ row.id.substring(0, 8) }}</div>
      </template>

      <template #cell-category="{ row }">
        <el-tag size="small" effect="plain" class="rounded-pill px-3">{{ row.category }}</el-tag>
      </template>

      <template #cell-description="{ row }">
        <div class="small text-secondary text-truncate text-center mx-auto" style="max-width: 150px;">
          {{ row.description || '—' }}
        </div>
      </template>

      <template #cell-variantSummary="{ row }">
        <div v-if="row.variantsCount > 0" class="d-flex flex-column align-items-center justify-content-center py-2 h-100">
          <div class="d-flex flex-wrap justify-content-center gap-1 mb-2">
            <el-tag v-for="v in row.variants.slice(0, 3)" :key="v.idChiTiet" size="small"
              class="rounded-pill border-0 px-2 fw-bold" 
              style="background-color: #f1f5f9; color: #475569; font-size: 10px; height: 18px; line-height: 18px;">
              {{ v.tenKichCo }}
            </el-tag>
            <el-tag v-if="row.variantsCount > 3" size="small" type="info" effect="plain" class="rounded-pill px-2" style="font-size: 10px; height: 18px; line-height: 18px;">
              +{{ row.variantsCount - 3 }}
            </el-tag>
          </div>
          <div v-if="row.minPrice > 0" class="fw-bold text-dark no-break" style="font-size: 13px; color: #334155 !important;">
            <span v-if="row.minPrice === row.maxPrice">{{ formatCurrency(row.minPrice) }}</span>
            <template v-else>
              <div class="text-secondary smaller opacity-75 fw-normal mb-0" style="font-size: 10px; line-height: 1;">Khoảng giá</div>
              <span>{{ formatCurrency(row.minPrice) }} - {{ formatCurrency(row.maxPrice) }}</span>
            </template>
          </div>
        </div>
        <div v-else class="text-center">
          <span class="text-secondary opacity-40 small italic">Chưa có biến thể</span>
        </div>
      </template>

      <template #cell-stockSummary="{ row }">
        <div class="text-center">
          <div class="fw-bold fs-6">{{ row.totalStock }}</div>
          <div class="tiny-text opacity-75">Sản phẩm</div>
        </div>
      </template>

      <template #cell-trangThai="{ row }">
        <div class="text-center">
          <el-tag :type="row.trangThai === 1 ? 'success' : 'danger'" size="small" round>
            {{ row.trangThai === 1 ? 'Hoạt động' : 'Ngừng hoạt động' }}
          </el-tag>
        </div>
      </template>

      <template #actions="{ row }">
        <div class="d-flex gap-1 justify-content-center align-items-center">
          <el-tooltip content="Sửa & Biến thể" placement="top">
            <button class="btn-action-icon action-edit" :disabled="row.trangThai === 0" @click="goToEditPage(row)">
              <i class="bi bi-pencil-square"></i>
            </button>
          </el-tooltip>
          <el-switch :model-value="row.trangThai === 1" @change="(val) => handleDelete(row)"
            class="status-switch mx-1" />
        </div>
      </template>
    </BaseTable>

  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { Plus, Edit, Delete, Search, Check } from '@element-plus/icons-vue'
import { sanPhamDiKemService } from '@/services/api/admin/sanPhamDiKemService'
import BaseTable from '@/components/common/BaseTable.vue'
import notification from '@/utils/notifications'
import confirmDialog from '@/utils/confirm'
import BaseModal from '@/components/common/BaseModal.vue'
import debounce from 'lodash/debounce'
import { ROUTES_CONSTANTS } from '@/constants/routeConstants'

const router = useRouter()
const activeCategory = ref('All')
const filterTrangThai = ref('')
const searchQuery = ref('')
const loading = ref(false)

const categories = ref([])
const items = ref([])

const tableColumns = [
  { label: 'STT', key: 'stt', width: '70px' },
  { label: 'Hình ảnh', key: 'image', width: '100px' },
  { label: 'Tên sản phẩm', key: 'name', width: '220px' },
  { label: 'Danh mục', key: 'category', width: '130px', align: 'center' },
  { label: 'Mô tả', key: 'description', width: '180px', align: 'center' },
  { label: 'Thông tin biến thể', key: 'variantSummary', width: '240px', align: 'center' },
  { label: 'Tổng tồn', key: 'stockSummary', width: '110px', align: 'center' },
  { label: 'Trạng thái', key: 'trangThai', width: '120px', align: 'center' },
];

const selectedItems = ref([]);
const currentPage = ref(1);
const pageSize = ref(5);


const formatCurrency = v => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(v)

const fetchMetadata = async () => {
  try {
    const res = await sanPhamDiKemService.getCategories()
    categories.value = res.data.data
  } catch (error) {
    console.error('Lỗi tải metadata:', error)
  }
}

const fetchItems = async () => {
  loading.value = true
  try {
    const params = {
      keyword: searchQuery.value || null,
      trangThai: null,
      page: 0,
      size: 100,
      sort: 'id,desc'
    }
    const res = await sanPhamDiKemService.getAll(params)
    const rawData = res.data.data.content || []

    const processed = rawData.map(sp => {
      const prices = sp.variants?.map(v => v.giaBan) || [];
      const minPrice = prices.length ? Math.min(...prices) : 0;
      const maxPrice = prices.length ? Math.max(...prices) : 0;
      const totalStock = sp.variants?.reduce((acc, v) => acc + v.soLuongTon, 0) || 0;
      const totalRevenue = sp.variants?.reduce((acc, v) => acc + v.tongDoanhThu || 0, 0) || 0;

      return {
        id: sp.id,
        name: sp.tenSanPham,
        category: sp.tenLoaiSanPham,
        categoryId: sp.idLoaiSanPham,
        description: sp.moTa,
        image: sp.hinhAnh,
        trangThai: sp.trangThai,
        revenue: totalRevenue,
        minPrice,
        maxPrice,
        totalStock: totalStock,
        variantsCount: sp.variants?.length || 0,
        variants: sp.variants,
        rawProduct: sp
      }
    })
    items.value = processed
  } catch (error) {
    notification.error('Lỗi tải danh sách sản phẩm')
  } finally {
    loading.value = false
  }
}

const handleSearch = debounce(() => {
  currentPage.value = 1
  fetchItems()
}, 300)

const handleReset = () => {
  searchQuery.value = ''
  activeCategory.value = 'All'
  filterTrangThai.value = ''
  currentPage.value = 1
  fetchItems()
}

watch([searchQuery], handleSearch)
watch([activeCategory, filterTrangThai, currentPage], fetchItems)

const filteredItems = computed(() => {
  let result = items.value
  if (activeCategory.value !== 'All' && activeCategory.value !== '') {
    result = result.filter(i => i.categoryId === activeCategory.value)
  }
  if (filterTrangThai.value !== '') {
    result = result.filter(i => i.rawProduct.trangThai === filterTrangThai.value)
  }
  return result
})

const paginatedItems = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredItems.value.slice(start, end);
});

const goToAddPage = () => {
  router.push({ name: ROUTES_CONSTANTS.ADMIN.children.FOOD_ADD.name })
}

const goToEditPage = (row) => {
  router.push({ 
    name: ROUTES_CONSTANTS.ADMIN.children.FOOD_EDIT.name,
    params: { id: row.id }
  })
}

const handleDelete = item => {
  const isInactive = item.trangThai === 0;
  const newStatus = isInactive ? 1 : 0;
  const label = isInactive ? 'kích hoạt' : 'ngừng hoạt động';

  confirmDialog.status('sản phẩm', label, isInactive).then(async () => {
    try {
      const payload = { ...item.rawProduct, trangThai: newStatus };
      if (newStatus === 0 && payload.variants) {
        payload.variants = payload.variants.map(v => ({ ...v, soLuongTon: 0 }));
      }
      await sanPhamDiKemService.update(item.id, payload);
      notification.success(`Đã ${label} sản phẩm thành công`);
      fetchItems();
    } catch (error) {
      notification.error('Cập nhật trạng thái thất bại');
    }
  }).catch(() => { });
}

onMounted(() => {
  fetchMetadata()
  fetchItems()
})
</script>

<style scoped>
.status-switch {
  --el-switch-on-color: #10b981;
  --el-switch-off-color: #ef4444;
}

.action-view {
  color: #3b82f6;
}

.action-view:hover {
  background: #eff6ff;
  color: #1d4ed8;
}

.action-edit {
  color: #f59e0b;
}

.action-edit:hover {
  background: #fffbeb;
  color: #d97706;
}

.tiny-text {
  font-size: 10px;
  font-weight: 700;
  text-transform: uppercase;
  color: #94a3b8;
  display: block;
}
</style>

