<template>
  <div class="admin-food-container">
    <AdminTableLayout
      title="Quản lý sản phẩm đi kèm"
      titleIcon="bi bi-box-seam-fill"
      addButtonLabel="Thêm sản phẩm"
      :data="filteredItems"
      :loading="loading"
      :total="filteredItems.length"
      hide-pagination
      @add-click="openDialog()"
    >
      <!-- Header Actions Left Slot -->
      <template #header-actions-left>
        <div class="d-flex align-items-center gap-2">
          <el-button 
            :type="selectedIds.length === filteredItems.length && filteredItems.length > 0 ? 'warning' : 'info'" 
            plain round 
            :icon="Check" 
            @click="handleSelectAll"
          >
            {{ selectedIds.length === filteredItems.length && filteredItems.length > 0 ? 'Bỏ chọn tất cả' : 'Chọn tất cả' }}
          </el-button>
          <el-button v-if="selectedIds.length" type="danger" plain round :icon="Delete" @click="handleBulkDelete">
            Xóa {{ selectedIds.length }} sản phẩm
          </el-button>
        </div>
      </template>

      <!-- Stats Slot -->
      <template #stats>
        <div class="col-md-3">
          <StatCard 
            label="Tổng sản phẩm" 
            :value="items.length" 
            icon="bi bi-grid-fill"
            type="dark"
          />
        </div>
        <div class="col-md-3">
          <StatCard 
            label="Đang bán" 
            :value="items.length" 
            icon="bi bi-check-circle-fill"
            type="success"
          />
        </div>
        <div class="col-md-3">
          <StatCard 
            label="Sắp hết hàng" 
            :value="items.filter(s => s.stock < 10 && s.stock > 0).length" 
            icon="bi bi-exclamation-triangle-fill"
            type="warning"
          />
        </div>
        <div class="col-md-3">
          <StatCard 
            label="Hết hàng" 
            :value="items.filter(s => s.stock === 0).length" 
            icon="bi bi-x-circle-fill"
            type="danger"
          />
        </div>
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
          />
        </div>

        <div class="filter-item">
          <el-radio-group v-model="activeCategory" size="default" @change="handleSearch">
            <el-radio-button value="All">Tất cả</el-radio-button>
            <el-radio-button v-for="cat in categories" :key="cat.id" :value="cat.id">
              {{ cat.tenLoai }}
            </el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <!-- Content Slot -->
      <template #content>
        <div class="food-grid-container p-2 h-100 overflow-auto no-scroll">
          <el-row :gutter="20" v-if="filteredItems.length">
            <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="item in filteredItems" :key="item.id" class="mb-4">
              <div 
                class="food-card bg-white rounded-4 shadow-sm h-100 d-flex flex-column position-relative cursor-pointer" 
                :class="{'selected-card': isSelected(item.id)}"
                @click="toggleSelection(item)"
              >
                <div class="selection-checkbox" @click.stop>
                  <el-checkbox :model-value="isSelected(item.id)" @change="toggleSelection(item)" />
                </div>
                <div class="card-image-wrapper">
                  <img :src="item.image || 'https://via.placeholder.com/150'" class="w-100 h-100 object-fit-cover" />
                  <el-tag effect="dark" class="size-badge" type="warning" round>Size {{ item.sizeName }}</el-tag>
                </div>
                <div class="p-3 d-flex flex-column flex-grow-1">
                  <div class="d-flex justify-content-between align-items-start mb-1">
                    <h6 class="fw-bold m-0 text-dark" style="font-size: 15px;">{{ item.name }}</h6>
                    <el-tag size="small" type="info" effect="plain" class="rounded-pill border-0 bg-light">{{ item.category }}</el-tag>
                  </div>
                  
                  <p class="text-secondary small mb-3 description-text">
                    {{ item.description || 'Chưa có mô tả cho sản phẩm này.' }}
                  </p>

                  <div class="mt-auto">
                    <div class="d-flex justify-content-between align-items-center mb-2">
                      <span class="text-primary fw-bold fs-5">{{ formatCurrency(item.price) }}</span>
                      <span class="text-secondary opacity-75 small fw-semibold">{{ item.quantityValue }} {{ item.unitName }}</span>
                    </div>
                    <div class="d-flex align-items-center gap-1 small" :class="item.stock > 0 ? 'text-success' : 'text-danger'">
                      <i class="bi" :class="item.stock > 0 ? 'bi-check-circle' : 'bi-x-circle'"></i>
                      Tồn kho: <b>{{ item.stock }}</b>
                    </div>
                  </div>
                  
                  <div class="mt-3 pt-3 border-top d-flex justify-content-end gap-2">
                    <el-tooltip content="Xem chi tiết" placement="top">
                      <button class="btn-action-icon btn-action-view" @click.stop="handleViewDetail(item)">
                        <i class="bi bi-eye"></i>
                      </button>
                    </el-tooltip>
                    <el-tooltip content="Quản lý biến thể" placement="top">
                      <button class="btn-action-icon btn-action-variant" @click.stop="openVariantDialog(item)">
                        <i class="bi bi-layers-half text-primary"></i>
                      </button>
                    </el-tooltip>
                    <el-tooltip content="Chỉnh sửa" placement="top">
                      <button class="btn-action-icon btn-action-edit" @click.stop="openDialog(item)">
                        <i class="bi bi-pencil"></i>
                      </button>
                    </el-tooltip>
                    <el-tooltip content="Xóa" placement="top">
                      <button class="btn-action-icon btn-action-delete" @click.stop="handleDelete(item)">
                        <i class="bi bi-trash"></i>
                      </button>
                    </el-tooltip>
                  </div>
                </div>
              </div>
            </el-col>
          </el-row>
          <el-empty v-else description="Không có sản phẩm nào" />
        </div>
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
          <el-button type="success" size="small" :icon="Plus" plain @click="addNewVariantRow" class="btn-add-premium">
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

          <el-table-column width="70" align="center">
            <template #default="scope">
              <button class="btn-action-icon btn-action-delete" @click="removeVariantRow(scope.$index)">
                <i class="bi bi-trash"></i>
              </button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </BaseModal>

    <!-- Detail Modal -->
    <BaseModal
      v-model="detailVisible"
      title="Chi tiết sản phẩm"
      icon="bi bi-info-circle"
      width="600px"
    >
      <div v-if="selectedItemDetail" class="p-2">
        <div class="d-flex gap-4 mb-4 pb-4 border-bottom">
          <div class="detail-img-wrapper">
            <img :src="selectedItemDetail.image || 'https://via.placeholder.com/150'" class="rounded-4 shadow-sm" style="width: 140px; height: 140px; object-fit: cover;" />
          </div>
          <div class="flex-grow-1">
            <div class="d-flex justify-content-between align-items-start mb-2">
              <h4 class="fw-bold text-dark m-0">{{ selectedItemDetail.name }}</h4>
              <el-tag type="warning" effect="dark" round>Size {{ selectedItemDetail.sizeName }}</el-tag>
            </div>
            <p class="text-secondary mb-3">{{ selectedItemDetail.description || 'Sản phẩm chưa có mô tả chi tiết.' }}</p>
            <div class="d-flex align-items-center gap-2">
              <el-tag size="small" type="info" round>{{ selectedItemDetail.category }}</el-tag>
              <el-tag size="small" :type="selectedItemDetail.stock > 0 ? 'success' : 'danger'" round>
                {{ selectedItemDetail.stock > 0 ? 'Đang kinh doanh' : 'Tạm hết hàng' }}
              </el-tag>
            </div>
          </div>
        </div>

        <div class="row g-4">
          <div class="col-6">
            <div class="detail-info-item">
              <div class="lbl mb-1 text-secondary small">Giá bán lẻ</div>
              <div class="val fw-bold text-dark fs-5">{{ formatCurrency(selectedItemDetail.price) }}</div>
            </div>
          </div>
          <div class="col-6">
            <div class="detail-info-item p-3 rounded-4 bg-light shadow-sm">
              <div class="lbl mb-1 text-secondary small"><i class="bi bi-palette me-2 text-primary"></i>Hương vị</div>
              <div class="val fw-bold text-dark">{{ selectedItemDetail.huongVi || 'Mặc định' }}</div>
            </div>
          </div>
          <div class="col-6">
            <div class="detail-info-item">
              <div class="lbl mb-1 text-secondary small">Định lượng</div>
              <div class="val fw-semibold">{{ selectedItemDetail.quantityValue }} {{ selectedItemDetail.unitName }}</div>
            </div>
          </div>
          <div class="col-6">
            <div class="detail-info-item">
              <div class="lbl mb-1 text-secondary small">Tồn kho hiện tại</div>
              <div class="val fw-bold" :class="selectedItemDetail.stock < 10 ? 'text-warning' : 'text-dark'">
                {{ selectedItemDetail.stock }} sản phẩm
              </div>
            </div>
          </div>
          <div class="col-6">
            <div class="detail-info-item">
              <div class="lbl mb-1 text-secondary small">Mã biến thể</div>
              <div class="val text-secondary font-monospace">#{{ selectedItemDetail.id }}</div>
            </div>
          </div>
        </div>
      </div>
      <template #footer></template>
    </BaseModal>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Plus, Edit, Delete, Search, Check } from '@element-plus/icons-vue'
import { sanPhamDiKemService } from '@/services/api/admin/sanPhamDiKemService'
import AdminTableLayout from '@/components/AdminTableLayout.vue'
import StatCard from '@/components/common/StatCard.vue'
import notification from '@/utils/notifications'
import confirmDialog from '@/utils/confirm'
import BaseModal from '@/components/common/BaseModal.vue'
import debounce from 'lodash/debounce'

const activeCategory = ref('All')
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
const selectedItems = ref([])
const selectedIds = computed(() => selectedItems.value.map(i => i.id))

const isSelected = (id) => selectedIds.value.includes(id)

const toggleSelection = (item) => {
  const index = selectedItems.value.findIndex(i => i.id === item.id)
  if (index > -1) {
    selectedItems.value.splice(index, 1)
  } else {
    selectedItems.value.push(item)
  }
}

const handleSelectAll = () => {
  if (selectedItems.value.length === filteredItems.value.length) {
    selectedItems.value = []
  } else {
    selectedItems.value = [...filteredItems.value]
  }
}

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
          rawProduct: sp
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
  fetchItems()
}, 300)

const filteredItems = computed(() => {
  let result = items.value
  if (activeCategory.value !== 'All') {
    result = result.filter(i => i.categoryId === activeCategory.value)
  }
  return result
})

const openDialog = (product = null) => {
  if (product) {
    isEditMode.value = true
    const sp = product.rawProduct
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

const handleBulkDelete = () => {
    confirmDialog.custom(
        `Xác nhận xóa <b>${selectedIds.value.length}</b> sản phẩm và tất cả kích cỡ liên quan?`,
        'Xóa hàng loạt',
        'Đồng ý'
    ).then(async () => {
        try {
            // Backend endpoint Xóa sản phẩm là xóa ID của sản phẩm chính (productId)
            // Lấy danh sách ID sản phẩm chính duy nhất
            const productIds = [...new Set(selectedItems.value.map(i => i.productId))];
            await Promise.all(productIds.map(id => sanPhamDiKemService.delete(id)));
            notification.success(`Đã xóa ${productIds.length} sản phẩm`);
            selectedItems.value = [];
            fetchItems();
        } catch (error) {
            notification.error('Có lỗi khi xóa hàng loạt');
        }
    }).catch(() => {});
};

const handleDelete = item => {
  confirmDialog.delete('sản phẩm', item.name).then(async () => {
    try {
      await sanPhamDiKemService.delete(item.productId)
      notification.deleteSuccess('sản phẩm')
      fetchItems()
    } catch (error) {
      notification.error('Không thể xóa sản phẩm')
    }
  })
}

onMounted(() => {
  fetchMetadata()
  fetchItems()
})
</script>

<style scoped>
.admin-food-container { 
  overflow: hidden;
}
.food-grid-container {
  flex: 1;
}
.card-image-wrapper { height: 180px; overflow: hidden; position: relative; border-radius: 12px 12px 0 0; background: #eee; }
.size-badge { position: absolute; top: 10px; right: 10px; font-weight: bold; }
.food-card { transition: 0.3s; border: 1px solid #eee; }
.food-card:hover { transform: translateY(-5px); box-shadow: 0 8px 16px rgba(0,0,0,0.08); }
.description-text {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  height: 32px;
}
.selection-checkbox {
  position: absolute;
  top: 10px;
  left: 10px;
  z-index: 10;
  background: white;
  padding: 4px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  line-height: 1;
  border: 1px solid #efefef;
  transition: all 0.2s;
}
.food-card:hover .selection-checkbox {
  border-color: #4f46e5;
  transform: scale(1.05);
}
.selected-card {
  border: 2px solid #4f46e5 !important;
  background-color: #f5f3ff !important;
}
.selected-card .selection-checkbox {
  background: #4f46e5;
  border-color: #4f46e5;
}
.selected-card .selection-checkbox :deep(.el-checkbox__inner) {
  background-color: #4f46e5;
  border-color: #fff;
}
.selected-card .selection-checkbox :deep(.el-checkbox__inner::after) {
  border-color: #fff;
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
  letter-spacing: 0.4px;
  text-transform: uppercase;
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