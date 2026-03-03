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
        <div class="filter-item flex-grow-1 search-input-wrapper" style="max-width: 400px;">
          <span class="filter-label text-dark small fw-bold mb-1 d-block"></span>
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
      width="1000px"
      :confirmText="isEditMode ? 'Cập nhật' : 'Xác nhận thêm'"
      @confirm="saveProduct"
    >
      <el-form :model="itemForm" label-position="top" class="premium-form">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="Tên sản phẩm chung">
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
          <el-input type="textarea" v-model="itemForm.moTa" :rows="2" placeholder="Nhập mô tả hiển thị trên card..." />
        </el-form-item>

        <el-form-item label="Hình ảnh URL">
          <el-input v-model="itemForm.hinhAnh" :prefix-icon="Search" />
        </el-form-item>

        <div class="variant-section-premium mt-4">
          <div class="d-flex justify-content-between align-items-center mb-3">
            <h6 class="fw-bold m-0 text-primary">Biến thể & Định lượng</h6>
            <el-button type="success" size="small" :icon="Plus" plain @click="addNewVariantRow" class="btn-add-premium">
              Thêm biến thể (Size)
            </el-button>
          </div>

          <el-table :data="itemForm.variants" border stripe class="premium-table-inside">
            <el-table-column width="55" align="center" label="Chọn">
              <template #default="scope">
                <el-checkbox v-model="scope.row.active" :disabled="isEditMode" />
              </template>
            </el-table-column>

            <el-table-column label="Kích cỡ" width="150">
              <template #default="scope">
                <el-select v-model="scope.row.idKichCo" size="small" :disabled="!scope.row.isEditing">
                  <el-option v-for="s in sizes" :key="s.id" :label="s.tenKichCo" :value="s.id" />
                </el-select>
              </template>
            </el-table-column>

            <el-table-column label="Định lượng (Số | Đơn vị)">
              <template #default="scope">
                <div class="d-flex align-items-center gap-1">
                  <el-input-number 
                    v-model="scope.row.giaTriDinhLuong" 
                    size="small" 
                    :disabled="!scope.row.isEditing"
                    :controls="false"
                    style="width: 80px"
                  />
                  <el-select v-model="scope.row.idDonViTinh" size="small" style="width: 90px" :disabled="!scope.row.isEditing">
                    <el-option v-for="u in units" :key="u.id" :label="u.tenDonViTinh" :value="u.id" />
                  </el-select>
                </div>
              </template>
            </el-table-column>

            <el-table-column label="Giá bán (VNĐ)">
              <template #default="scope">
                <el-input-number
                  v-model="scope.row.giaBan"
                  size="small"
                  :disabled="!scope.row.isEditing"
                  style="width: 130px"
                  :min="1000"
                  :step="1000"
                />
              </template>
            </el-table-column>

            <el-table-column label="Tồn kho">
              <template #default="scope">
                <el-input-number
                  v-model="scope.row.soLuongTon"
                  size="small"
                  :disabled="!scope.row.isEditing"
                  style="width: 100px"
                />
              </template>
            </el-table-column>

            <el-table-column label="Thao tác" width="120" align="center">
              <template #default="scope">
                <div class="d-flex justify-content-center gap-2">
                  <el-button 
                    v-if="!scope.row.isEditing"
                    type="primary" :icon="Edit" size="small" circle 
                    @click="scope.row.isEditing = true"
                  />
                  <el-button 
                    v-else
                    type="success" :icon="Check" size="small" circle 
                    @click="confirmUpdateVariant(scope.row)"
                  />
                  <el-button
                    type="danger" :icon="Delete" size="small" circle
                    @click="removeVariantRow(scope.$index)"
                  />
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-form>
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
              <div class="val fw-bold text-primary fs-5">{{ formatCurrency(selectedItemDetail.price) }}</div>
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
import BaseModal from '@/components/common/BaseModal.vue'
import debounce from 'lodash/debounce'

const activeCategory = ref('All')
const searchQuery = ref('')
const dialogVisible = ref(false)
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
  id: null, tenSanPham: '', idLoaiSanPham: '', moTa: '', hinhAnh: '', variants: []
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
      hinhAnh: sp.hinhAnh,
      variants: sp.variants.map(v => ({
        idChiTiet: v.idChiTiet,
        idKichCo: v.idKichCo || sizes.value.find(s => s.tenKichCo === v.tenKichCo)?.id,
        idDonViTinh: v.idDonViTinh || units.value.find(u => u.tenDonViTinh === v.tenDonViTinh)?.id,
        giaTriDinhLuong: v.giaTriDinhLuong,
        giaBan: v.giaBan,
        soLuongTon: v.soLuongTon,
        active: true,
        isEditing: false
      }))
    }
  } else {
    isEditMode.value = false
    itemForm.value = {
      id: null, tenSanPham: '', idLoaiSanPham: categories.value[0]?.id, moTa: '', hinhAnh: '',
      variants: [
        { idKichCo: sizes.value[0]?.id, idDonViTinh: units.value[0]?.id, giaTriDinhLuong: 0, giaBan: 0, soLuongTon: 0, active: true, isEditing: true }
      ]
    }
  }
  dialogVisible.value = true
}

const addNewVariantRow = () => {
  itemForm.value.variants.push({
    idKichCo: sizes.value[0]?.id,
    idDonViTinh: units.value[0]?.id,
    giaTriDinhLuong: 0,
    giaBan: 0,
    soLuongTon: 0,
    active: true,
    isEditing: true
  })
}

const removeVariantRow = (index) => {
  itemForm.value.variants.splice(index, 1)
}

const confirmUpdateVariant = (row) => {
  if(!row.idKichCo) return notification.warning('Vui lòng chọn kích cỡ')
  row.isEditing = false
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

  const selected = itemForm.value.variants.filter(v => v.active)
  if (!selected.length) return notification.warning('Vui lòng chọn ít nhất 1 biến thể')

  for (const v of selected) {
    if (!v.giaBan || v.giaBan <= 0) {
      return notification.error('Giá bán của các biến thể phải lớn hơn 0')
    }
  }

  try {
    const payload = {
      tenSanPham: itemForm.value.tenSanPham,
      idLoaiSanPham: itemForm.value.idLoaiSanPham,
      moTa: itemForm.value.moTa,
      hinhAnh: itemForm.value.hinhAnh,
      trangThai: 1,
      variants: selected.map(v => ({
        idKichCo: v.idKichCo,
        idDonViTinh: v.idDonViTinh,
        giaTriDinhLuong: v.giaTriDinhLuong,
        giaBan: v.giaBan,
        soLuongTon: v.soLuongTon
      }))
    }

    if (isEditMode.value) {
      await sanPhamDiKemService.update(itemForm.value.id, payload)
      notification.updateSuccess('sản phẩm')
    } else {
      await sanPhamDiKemService.create(payload)
      notification.addSuccess('sản phẩm')
    }
    dialogVisible.value = false
    fetchItems()
  } catch (error) {
    notification.error(error.response?.data?.message || 'Có lỗi xảy ra khi lưu sản phẩm')
  }
}

const handleBulkDelete = () => {
    ElMessageBox.confirm(
        `Xác nhận xóa <b>${selectedIds.value.length}</b> sản phẩm và tất cả kích cỡ liên quan?`,
        'Xóa hàng loạt',
        {
            dangerouslyUseHTMLString: true,
            confirmButtonText: 'Đồng ý',
            cancelButtonText: 'Hủy',
            type: 'warning'
        }
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
  ElMessageBox.confirm(`Bạn có chắc muốn xóa TOÀN BỘ sản phẩm "${item.name}" và các kích cỡ liên quan?`, 'Cảnh báo', { 
    type: 'warning',
    confirmButtonText: 'Xác nhận xóa',
    cancelButtonText: 'Hủy'
  }).then(async () => {
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
</style>