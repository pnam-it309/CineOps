<template>
  <div class="admin-food-container">
    <AdminTableLayout
      title="Quản lý sản phẩm đi kèm"
      titleIcon="bi bi-box-seam-fill"
      addButtonLabel="Thêm sản phẩm"
      :data="filteredItems"
      :loading="loading"
      :total="filteredItems.length"
      v-model:currentPage="currentPage"
      v-model:pageSize="pageSize"
      @add-click="openDialog()"
    >
      <!-- Header Actions Left Slot -->
      <template #header-actions-left>
      </template>



      <!-- Filters Slot -->
      <template #filters>
        <div class="filter-item search-input-wrapper">
          <el-input
            v-model="searchQuery"
            placeholder="Tìm kiếm sản phẩm..."
            :prefix-icon="Search"
            clearable
            @input="handleSearch"
            style="width: 250px;"
          />
        </div>

        <div class="filter-item">
          <el-select v-model="activeCategory" placeholder="Chọn danh mục" style="width: 180px;" @change="handleSearch" clearable>
            <el-option label="Tất cả danh mục" value="All"/>
            <el-option v-for="cat in categories" :key="cat.id" :label="cat.tenLoai" :value="cat.id"/>
          </el-select>
        </div>

        <div class="filter-item">
          <el-select v-model="filterTrangThai" placeholder="Chọn trạng thái" style="width: 180px;" @change="handleSearch" clearable>
            <el-option label="Tất cả trạng thái" value=""/>
            <el-option label="Đang bán" :value="1"/>
            <el-option label="Ngừng bán" :value="0"/>
          </el-select>
        </div>
      </template>

      <!-- Content Slot -->
      <template #content>
        <BaseTable
          v-loading="loading"
          :data="paginatedItems"
          :columns="tableColumns"
          :total="filteredItems.length"
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          v-model:selection="selectedItems"
          :hide-pagination="true"
          @edit="openDialog"
          @delete="handleDelete"
          @update-status="({ row, val }) => handleDelete(row)"
        >
          <template #cell-stt="{ index }">
            <span class="fw-bold text-secondary">{{ (currentPage - 1) * pageSize + index + 1 }}</span>
          </template>

          <template #cell-image="{ row }">
            <div class="py-1">
              <img :src="row.image || 'https://placehold.co/150'" 
                class="rounded-3 shadow-sm border"
                style="width:40px;height:40px;object-fit:cover;"
                @error="e => { e.target.src='https://placehold.co/150?text=No+Image'; }" />
            </div>
          </template>

          <template #cell-name="{ row }">
            <div class="fw-bold" style="color:#344767;">{{ row.name }}</div>
            <div class="text-secondary opacity-75" style="font-size: 11px;">#{{ row.id }} · {{ row.category }}</div>
          </template>

          <template #cell-sizeName="{ row }">
            <el-tag size="small" type="warning" effect="plain" class="rounded-pill px-2">Size {{ row.sizeName }}</el-tag>
          </template>

          <template #cell-huongVi="{ row }">
            <span class="small text-dark fw-medium">{{ row.huongVi || '—' }}</span>
          </template>

          <template #cell-stock="{ row }">
            <span :class="['small fw-bold', row.stock < 10 ? 'text-danger' : 'text-secondary']">{{ row.stock }}</span>
          </template>

          <template #cell-price="{ row }">
            <span class="fw-bold text-primary">{{ formatCurrency(row.price) }}</span>
          </template>


          <template #cell-trangThai="{ row }">
            <el-tag :type="row.trangThai === 1 ? 'success' : 'danger'" size="small" round>
              {{ row.trangThai === 1 ? 'Đang bán' : 'Ngừng bán' }}
            </el-tag>
          </template>

          <template #actions="{ row }">
            <div class="d-flex gap-1 justify-content-center align-items-center">
              <el-tooltip content="Xem chi tiết" placement="top">
                <button class="btn-action-icon action-view" @click="handleViewDetail(row)">
                  <i class="bi bi-eye"></i>
                </button>
              </el-tooltip>
              <el-tooltip content="Biến thể" placement="top">
                <button class="btn-action-icon action-variant" :disabled="row.trangThai === 0" @click="openVariantDialog(row)">
                  <i class="bi bi-layers-half text-primary"></i>
                </button>
              </el-tooltip>
              <el-tooltip content="Sửa" placement="top">
                <button class="btn-action-icon action-edit" :disabled="row.trangThai === 0" @click="openDialog(row)">
                  <i class="bi bi-pencil"></i>
                </button>
              </el-tooltip>
              <el-switch
                :model-value="row.trangThai === 1"
                @change="(val) => handleDelete(row)"
                class="status-switch mx-1"
                active-color="#ff4949"
                inactive-color="#ff4949"
              />
            </div>
          </template>
        </BaseTable>
      </template>
    </AdminTableLayout>

    <BaseModal
      v-model="dialogVisible"
      :title="isEditMode ? 'Chỉnh sửa sản phẩm' : 'Thêm sản phẩm mới'"
      icon="bi bi-box-seam"
      width="600px"
      :confirmText="isEditMode ? 'Cập nhật' : 'Xác nhận thêm'"
      @confirm="saveProduct"
    >
      <el-form :model="itemForm" label-position="top" class="premium-form">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="Tên sản phẩm">
              <el-input v-model="itemForm.tenSanPham" placeholder="Ví dụ: Bắp Phô Mai" :prefix-icon="Plus" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Danh mục">
              <el-select v-model="itemForm.idLoaiSanPham" class="w-100">
                <el-option v-for="cat in categories" :key="cat.id" :label="cat.tenLoai" :value="cat.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="Mô tả sản phẩm">
          <el-input type="textarea" v-model="itemForm.moTa" :rows="3" placeholder="Nhập mô tả sản phẩm..." />
        </el-form-item>

        <el-form-item label="Hình ảnh URL">
          <el-input v-model="itemForm.hinhAnh" :prefix-icon="Search" />
        </el-form-item>
      </el-form>
    </BaseModal>

    <!-- Variants Modal -->
    <BaseModal
      v-model="variantDialogVisible"
      title="Quản lý biến thể sản phẩm"
      icon="bi bi-layers-half"
      width="1300px"
      confirmText="Lưu biến thể"
      @confirm="saveVariants"
    >
      <div v-if="selectedProductForVariant" class="product-mini-info mb-4 d-flex align-items-center gap-3 p-3 bg-light rounded-4">
        <img :src="selectedProductForVariant.hinhAnh" class="rounded-3" style="width: 60px; height: 60px; object-fit: cover" />
        <div>
          <h5 class="m-0 fw-bold">{{ selectedProductForVariant.tenSanPham }}</h5>
          <div class="text-secondary small">{{ selectedProductForVariant.moTa }}</div>
        </div>
      </div>

      <div class="variant-section-premium">
        <div class="d-flex justify-content-between align-items-center mb-3">
          <h6 class="fw-bold m-0 text-primary">Danh sách kích cỡ & Giá</h6>
          <el-button type="success" size="small" :icon="Plus" @click="addNewVariantRow">
            Thêm kích cỡ mới
          </el-button>
        </div>

        <el-table 
          :data="variantForm.variants" 
          border 
          stripe 
          class="premium-table-inside variant-compact-table w-100" 
          header-cell-class-name="nowrap-header text-center"
          size="default"
        >
          <el-table-column width="50" align="center" label="✓">
            <template #default="scope">
              <el-checkbox v-model="scope.row.active" size="default" />
            </template>
          </el-table-column>

          <el-table-column label="Kích cỡ" width="220" align="center">
            <template #default="scope">
              <el-select v-model="scope.row.idKichCo" size="default" class="w-100">
                <el-option v-for="s in sizes" :key="s.id" :label="s.tenKichCo" :value="s.id" />
              </el-select>
            </template>
          </el-table-column>

          <el-table-column label="Định lượng" width="240" align="center">
            <template #default="scope">
              <div class="d-flex align-items-center gap-1">
                <el-input 
                  v-model.number="scope.row.giaTriDinhLuong" 
                  placeholder="0"
                  type="number"
                  size="default"
                  style="width: 100px"
                />
                <el-select v-model="scope.row.idDonViTinh" size="default" style="width: 110px">
                  <el-option v-for="u in units" :key="u.id" :label="u.tenDonViTinh" :value="u.id" />
                </el-select>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="Hương vị" min-width="200" align="center">
            <template #default="scope">
              <el-input v-model="scope.row.huongVi" size="default" placeholder="Ví dụ: Phô mai, Caramel..." />
            </template>
          </el-table-column>

          <el-table-column label="Giá bán" width="180" align="center">
            <template #default="scope">
              <el-input 
                v-model.number="scope.row.giaBan" 
                placeholder="0"
                type="number"
                size="default"
                class="w-100"
              >
                <template #suffix><span class="text-secondary fw-bold pe-2">₫</span></template>
              </el-input>
            </template>
          </el-table-column>

          <el-table-column label="Tồn kho" width="170" align="center">
            <template #default="scope">
              <el-input 
                v-model.number="scope.row.soLuongTon" 
                placeholder="0"
                type="number"
                size="default"
                class="w-100"
              />
            </template>
          </el-table-column>

          <el-table-column width="100" align="center">
            <template #default="scope">
              <button class="btn-action-icon action-delete" @click="removeVariantRow(scope.$index)">
                <i class="bi bi-trash"></i>
              </button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </BaseModal>

    <!-- Detail Modal -->
    <BaseModal v-model="detailVisible" title="Hồ sơ sản phẩm" icon="bi bi-box2-heart-fill" width="600px" isDetail onlyCancel>
      <div v-if="selectedItemDetail" class="p-0">
        <!-- Profile Header (Colorless) -->
        <div class="p-4 border-bottom bg-white d-flex align-items-center gap-4">
          <div class="shadow-sm border d-flex align-items-center justify-content-center bg-light" 
               style="width: 120px; height: 120px; overflow: hidden;">
            <img :src="selectedItemDetail.image || 'https://placehold.co/300'" class="w-100 h-100 object-fit-cover" @error="e => { e.target.src='https://placehold.co/300?text=No+Image'; }" />
          </div>
          <div class="flex-grow-1">
            <div class="d-flex align-items-center gap-2 mb-1">
              <h3 class="fw-bold m-0 text-dark">{{ selectedItemDetail.name }}</h3>
              <el-tag type="info" effect="plain" round size="small">SIZE {{ selectedItemDetail.sizeName?.toUpperCase() }}</el-tag>
            </div>
            <div class="d-flex align-items-center gap-2">
              <el-tag :type="selectedItemDetail.trangThai === 1 ? 'info' : 'plain'" effect="plain" round size="small">
                {{ selectedItemDetail.trangThai === 1 ? 'ĐANG KINH DOANH' : 'NGỪNG BÁN' }}
              </el-tag>
              <div class="small text-secondary"><i class="bi bi-tag me-1"></i>{{ selectedItemDetail.category }}</div>
            </div>
          </div>
        </div>

        <!-- Details Body -->
        <div class="p-4 bg-white">
          <div class="row g-4">
            <div class="col-12">
              <h6 class="text-uppercase small fw-bold text-secondary mb-3">Thông số thương mại</h6>
              <div class="row g-3">
                <div class="col-6">
                  <div class="p-3 border bg-white">
                    <div class="text-secondary small mb-1">GIÁ NIÊM YẾT</div>
                    <div class="fw-bold fs-4 text-dark">{{ formatCurrency(selectedItemDetail.price) }}</div>
                  </div>
                </div>
                <div class="col-6">
                  <div class="p-3 border bg-white">
                    <div class="text-secondary small mb-1">TỒN KHO HIỆN TẠI</div>
                    <div class="fw-bold fs-4 text-dark">{{ selectedItemDetail.stock }} SP</div>
                  </div>
                </div>
              </div>
            </div>

            <div class="col-12">
              <h6 class="text-uppercase small fw-bold text-secondary mb-3">Hiệu suất & Thành phần</h6>
              <div class="row g-3">
                <div class="col-4">
                  <div class="p-2 border bg-white text-center">
                    <div class="text-secondary tiny-text">ĐỊNH LƯỢNG</div>
                    <div class="fw-bold text-dark">{{ selectedItemDetail.quantityValue }} {{ selectedItemDetail.unitName }}</div>
                  </div>
                </div>
                <div class="col-4">
                  <div class="p-2 border bg-white text-center">
                    <div class="text-secondary tiny-text">HƯƠNG VỊ</div>
                    <div class="fw-bold text-dark">{{ selectedItemDetail.huongVi || 'Tự nhiên' }}</div>
                  </div>
                </div>
                <div class="col-4">
                  <div class="p-2 border bg-white text-center">
                    <div class="text-secondary tiny-text">DOANH THU</div>
                    <div class="fw-bold text-dark">{{ formatCurrency(selectedItemDetail.doanhThu || 0) }}</div>
                  </div>
                </div>
              </div>
            </div>

            <div class="col-12">
              <div class="p-3 border bg-light">
                <h6 class="text-uppercase small fw-bold text-secondary mb-1">MÔ TẢ SẢN PHẨM</h6>
                <p class="text-dark mb-0 small lh-base">
                  {{ selectedItemDetail.description || 'Không có mô tả chi tiết.' }}
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </BaseModal>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Plus, Edit, Delete, Search, Check } from '@element-plus/icons-vue'
import { sanPhamDiKemService } from '@/services/api/admin/sanPhamDiKemService'
import AdminTableLayout from '@/components/AdminTableLayout.vue'
import BaseTable from '@/components/common/BaseTable.vue'
import notification from '@/utils/notifications'
import confirmDialog from '@/utils/confirm'
import BaseModal from '@/components/common/BaseModal.vue'
import debounce from 'lodash/debounce'

const activeCategory = ref('All')
const filterTrangThai = ref('')
const searchQuery = ref('')
const dialogVisible = ref(false)
const variantDialogVisible = ref(false)
const selectedProductForVariant = ref(null)
const detailVisible = ref(false)
const selectedItemDetail = ref(null)
const isEditMode = ref(false)
const loading = ref(false)

const categories = ref([])
const sizes = ref([])
const units = ref([])
const items = ref([])

const tableColumns = [
  { label: 'STT',           key: 'stt',          width: '60px'   },
  { label: 'Hình ảnh',      key: 'image',        width: '90px'  },
  { label: 'Tên sản phẩm',  key: 'name',         width: '180px' },
  { label: 'Kích cỡ',       key: 'sizeName',     width: '100px'  },
  { label: 'Hương vị',      key: 'huongVi',      width: '120px'  },
  { label: 'Giá bán',       key: 'price',        width: '130px'  },
  { label: 'Tồn kho',       key: 'stock',        width: '100px'  },
  { label: 'Trạng thái',    key: 'trangThai',    width: '140px'  },
];

const selectedItems = ref([]);
const currentPage = ref(1);
const pageSize = ref(5);


const handleViewDetail = (item) => {
  selectedItemDetail.value = item
  detailVisible.value = true
}
const itemForm = ref({
  id: null, tenSanPham: '', idLoaiSanPham: '', moTa: '', hinhAnh: ''
})

const variantForm = ref({
  variants: []
})

const formatCurrency = v => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(v)

const fetchMetadata = async () => {
  try {
    const [cats, szs, unts] = await Promise.all([
      sanPhamDiKemService.getCategories(),
      sanPhamDiKemService.getSizes(),
      sanPhamDiKemService.getUnits()
    ])
    categories.value = cats.data.data
    sizes.value = szs.data.data
    units.value = unts.data.data
  } catch (error) {
    console.error('Lỗi tải metadata:', error)
  }
}

const fetchItems = async () => {
  loading.value = true
  try {
    const params = {
      keyword: searchQuery.value || null,
      trangThai: null, // Có thể mở rộng sau
      page: 0,
      size: 100 // Tạm thời load hết để FE filter categories
    }
    const res = await sanPhamDiKemService.getAll(params)
    const rawData = res.data.data.content || []
    
    // Flatten products for UI display (one card per size)
    const flattened = []
    // Ensure metadata is loaded if we're going to use it for fallbacks
    if (!categories.value.length) await fetchMetadata()

    rawData.forEach(sp => {
      sp.variants.forEach(v => {
        flattened.push({
          id: v.idChiTiet,
          productId: sp.id,
          name: sp.tenSanPham,
          sizeId: v.idKichCo,
          sizeName: v.tenKichCo,
          category: sp.tenLoaiSanPham,
          categoryId: sp.idLoaiSanPham || categories.value.find(c => c.tenLoai === sp.tenLoaiSanPham)?.id,
          description: sp.moTa,
          image: sp.hinhAnh,
          quantityValue: v.giaTriDinhLuong,
          unitName: v.tenDonViTinh,
          unitId: v.idDonViTinh,
          price: v.giaBan,
          stock: v.soLuongTon,
          huongVi: v.huongVi, // Add huongVi here
          trangThai: sp.trangThai,
          rawProduct: sp,
          tongBan: v.tongSoLuongBan, // Assuming this comes from backend
          doanhThu: v.tongDoanhThu // Assuming this comes from backend
        })
      })
    })
    items.value = flattened
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

const openDialog = (product = null) => {
  if (product) {
    isEditMode.value = true
    const sp = product.rawProduct || product // Handle both flattened item and raw product
    itemForm.value = {
      id: sp.id,
      tenSanPham: sp.tenSanPham,
      idLoaiSanPham: sp.idLoaiSanPham || categories.value.find(c => c.tenLoai === sp.tenLoaiSanPham)?.id,
      moTa: sp.moTa,
      hinhAnh: sp.hinhAnh
    }
  } else {
    isEditMode.value = false
    itemForm.value = {
      id: null, tenSanPham: '', idLoaiSanPham: categories.value[0]?.id, moTa: '', hinhAnh: ''
    }
  }
  dialogVisible.value = true
}

const openVariantDialog = (item) => {
  const sp = item.rawProduct
  selectedProductForVariant.value = sp
  variantForm.value.variants = sp.variants.map(v => ({
    idChiTiet: v.idChiTiet,
    idKichCo: v.idKichCo || sizes.value.find(s => s.tenKichCo === v.tenKichCo)?.id,
    idDonViTinh: v.idDonViTinh || units.value.find(u => u.tenDonViTinh === v.tenDonViTinh)?.id,
    giaTriDinhLuong: v.giaTriDinhLuong,
    huongVi: v.huongVi,
    giaBan: v.giaBan,
    soLuongTon: v.soLuongTon,
    active: true
  }))
  if (variantForm.value.variants.length === 0) {
    addNewVariantRow()
  }
  variantDialogVisible.value = true
}

const addNewVariantRow = () => {
  variantForm.value.variants.push({
    idKichCo: sizes.value[0]?.id,
    idDonViTinh: units.value[0]?.id,
    giaTriDinhLuong: 0,
    huongVi: '',
    giaBan: 0,
    soLuongTon: 0,
    active: true
  })
}

const removeVariantRow = (index) => {
  variantForm.value.variants.splice(index, 1)
}

const saveProduct = async () => {
  if (!itemForm.value.tenSanPham) return notification.error('Vui lòng nhập tên sản phẩm')
  if (!itemForm.value.idLoaiSanPham) return notification.error('Vui lòng chọn danh mục')
  
  if (itemForm.value.hinhAnh) {
    const imgRegex = /^(https?:\/\/).+\.(jpg|jpeg|png|webp)$/i;
    if (!imgRegex.test(itemForm.value.hinhAnh)) {
      return notification.error('URL hình ảnh không hợp lệ (phải có đuôi jpg/png/webp)')
    }
  }

  try {
    if (isEditMode.value) {
      await confirmDialog.update('sản phẩm');
    } else {
      await confirmDialog.add('sản phẩm');
    }
  } catch { return; }

  try {
    const payload = {
      tenSanPham: itemForm.value.tenSanPham,
      idLoaiSanPham: itemForm.value.idLoaiSanPham,
      moTa: itemForm.value.moTa,
      hinhAnh: itemForm.value.hinhAnh,
      trangThai: 1,
      variants: isEditMode.value ? undefined : [] 
    }

    if (isEditMode.value) {
      await sanPhamDiKemService.update(itemForm.value.id, payload)
      notification.updateSuccess('sản phẩm')
    } else {
      await sanPhamDiKemService.create(payload)
      notification.addSuccess('sản phẩm. Hãy vào phần Quản lý biến thể để thêm các kích cỡ cho sản phẩm này.')
    }
    dialogVisible.value = false
    fetchItems()
  } catch (error) {
    notification.error(error.response?.data?.message || 'Có lỗi xảy ra khi lưu sản phẩm')
  }
}

const saveVariants = async () => {
  const selected = variantForm.value.variants.filter(v => v.active)
  if (!selected.length) return notification.warning('Vui lòng chọn ít nhất 1 biến thể')

  for (const v of selected) {
    if (!v.giaBan || v.giaBan <= 0) {
      return notification.error('Giá bán của các biến thể phải lớn hơn 0')
    }
    if (!v.idKichCo) return notification.warning('Vui lòng chọn đầy đủ kích cỡ')
  }

  try {
    await confirmDialog.custom('Xác nhận cập nhật danh sách biến thể?', 'Cập nhật', 'Xác nhận')
    const sp = selectedProductForVariant.value
    const payload = {
      tenSanPham: sp.tenSanPham,
      idLoaiSanPham: sp.idLoaiSanPham,
      moTa: sp.moTa,
      hinhAnh: sp.hinhAnh,
      trangThai: 1,
      variants: selected.map(v => ({
        idKichCo: v.idKichCo,
        idDonViTinh: v.idDonViTinh,
        giaTriDinhLuong: v.giaTriDinhLuong,
        huongVi: v.huongVi,
        giaBan: v.giaBan,
        soLuongTon: v.soLuongTon
      }))
    }

    await sanPhamDiKemService.update(sp.id, payload)
    notification.success('Đã cập nhật các biến thể thành công')
    variantDialogVisible.value = false
    fetchItems()
  } catch (error) {
    if (error !== 'cancel') {
        notification.error(error.response?.data?.message || 'Lỗi cập nhật biến thể')
    }
  }
}

const handleDelete = item => {
  const isInactive = item.rawProduct.trangThai === 0;
  const newStatus = isInactive ? 1 : 0;
  const label = isInactive ? 'kích hoạt' : 'ngừng bán';
  
  confirmDialog.custom(
    `Thay đổi trạng thái sản phẩm <b>${item.name}</b> thành <b>${label}</b>?`,
    'Cập nhật trạng thái',
    'Xác nhận'
  ).then(async () => {
    try {
      const payload = { ...item.rawProduct, trangThai: newStatus };
      
      // Nếu ngừng kinh doanh, set tồn kho tất cả biến thể về 0
      if (newStatus === 0 && payload.variants) {
        payload.variants = payload.variants.map(v => ({
          ...v,
          soLuongTon: 0
        }));
      }

      await sanPhamDiKemService.update(item.productId, payload);
      notification.success(`Đã ${label} sản phẩm thành công`);
      fetchItems();
    } catch (error) {
      notification.error('Cập nhật trạng thái thất bại');
    }
  }).catch(() => {});
}

onMounted(() => {
  fetchMetadata()
  fetchItems()
})
</script>

<style scoped>

.hero-image-box { height: 200px; position: relative; }
.image-overlay-category { position: absolute; bottom: 0; left: 0; right: 0; text-align: center; }
.product-main-name { font-size: 24px; letter-spacing: -0.5px; }
.uppercase-label { text-transform: uppercase; letter-spacing: 1px; font-size: 11px; }
.section-title-sm { font-size: 12px; font-weight: 800; text-transform: uppercase; letter-spacing: 1px; color: #94a3b8; margin-bottom: 15px; }
.tiny-text { font-size: 10px; font-weight: 700; text-transform: uppercase; color: #94a3b8; display: block; }
.spec-card { transition: all 0.2s; }
.spec-card:hover { transform: translateY(-3px); border-color: var(--el-color-primary-light-5) !important; }
.dot-indicator { width: 8px; height: 8px; border-radius: 50%; background: #22c55e; display: inline-block; vertical-align: middle; box-shadow: 0 0 8px rgba(34, 197, 94, 0.4); }

.section-divider-sm { display: flex; align-items: center; color: #94a3b8; font-size: 11px; font-weight: 800; text-transform: uppercase; letter-spacing: 1px; }
.section-divider-sm::after { content: ''; flex-grow: 1; height: 1px; background: #f1f5f9; margin-left: 10px; }
.stat-card { transition: all 0.2s; }
.stat-card:hover { border-color: var(--el-color-primary) !important; transform: translateY(-2px); }
.dot { width: 8px; height: 8px; border-radius: 50%; }
.spec-plate { transition: all 0.2s; border: 1px solid #f1f5f9; }
.spec-plate:hover { border-color: var(--el-color-primary) !important; background: #fff !important; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }

.admin-food-container { 
  overflow: hidden;
}
.food-grid-container {
  flex: 1;
}



/* Biến thể section styling */
.variant-section-premium {
  background: #f8fafc;
  padding: 20px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.detail-img-wrapper {
  transition: all 0.3s;
}
.detail-img-wrapper:hover {
  transform: scale(1.02);
}
.detail-info-item {
  padding: 12px;
  background: #f8fafc;
  border-radius: 10px;
  border: 1px solid #f1f5f9;
}

/* Chỉnh hiển thị table */
:deep(.nowrap-header .cell) {
  white-space: nowrap !important;
  word-break: keep-all !important;
}

:deep(.premium-table-inside) {
  --el-table-header-bg-color: #f1f5f9;
  --el-table-header-text-color: #1e293b;
}

/* Compact variant table styles */
:deep(.variant-compact-table th.el-table__cell) {
  font-weight: 700;
  font-size: 11px;
  letter-spacing: normal;
  text-transform: none;
  padding: 7px 6px !important;
  color: #475569;
}

:deep(.variant-compact-table td.el-table__cell) {
  padding: 5px 6px !important;
}

:deep(.variant-compact-table .el-input-number) {
  width: 100%;
}

:deep(.variant-compact-table .el-input-number .el-input__inner) {
  text-align: center;
}
</style>