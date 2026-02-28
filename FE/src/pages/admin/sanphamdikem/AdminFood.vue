<template>
  <div class="admin-food-container p-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <div class="d-flex align-items-center gap-3">
        <h2 class="fw-bold m-0" style="font-size:18px">Quản Lý Sản Phẩm Đi Kèm</h2>
        <el-button v-if="selectedIds.length" type="danger" plain round :icon="Delete" @click="handleBulkDelete">
          Xóa {{ selectedIds.length }} sản phẩm
        </el-button>
      </div>
      <el-button type="primary" :icon="Plus" @click="openDialog()">
        Thêm sản phẩm mới
      </el-button>
    </div>

    <div class="d-flex justify-content-between align-items-center mb-4 bg-white p-3 rounded shadow-sm">
      <el-radio-group v-model="activeCategory" size="large" @change="handleSearch">
        <el-radio-button value="All">Tất cả</el-radio-button>
        <el-radio-button v-for="cat in categories" :key="cat.id" :value="cat.id">
          {{ cat.tenLoai }}
        </el-radio-button>
      </el-radio-group>

      <el-input
        v-model="searchQuery"
        placeholder="Tìm kiếm sản phẩm..."
        :prefix-icon="Search"
        clearable
        style="width:300px"
      />
    </div>

    <el-row :gutter="20" v-if="filteredItems.length" v-loading="loading">
      <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="item in filteredItems" :key="item.id" class="mb-4">
        <div class="food-card bg-white rounded shadow-sm h-100 d-flex flex-column position-relative" :class="{'selected-card': isSelected(item.id)}">
          <div class="selection-checkbox">
            <el-checkbox :model-value="isSelected(item.id)" @change="toggleSelection(item)" />
          </div>
          <div class="card-image-wrapper">
            <img :src="item.image || 'https://via.placeholder.com/150'" class="w-100 h-100 object-fit-cover" />
            <el-tag effect="dark" class="size-badge" type="warning">Size {{ item.sizeName }}</el-tag>
          </div>
          <div class="p-3 d-flex flex-column flex-grow-1">
            <h6 class="fw-bold mb-1">{{ item.name }}</h6>
            
            <p class="text-muted small mb-2 description-text">
              {{ item.description || 'Chưa có mô tả cho sản phẩm này.' }}
            </p>

            <div class="mt-auto">
              <div class="d-flex justify-content-between align-items-center">
                <span class="text-primary fw-bold fs-5">{{ formatCurrency(item.price) }}</span>
                <span class="text-muted small">{{ item.quantityValue }} {{ item.unitName }}</span>
              </div>
              <div class="text-success small mt-1">Tồn kho: {{ item.stock }}</div>
            </div>
            
            <div class="mt-3 d-flex justify-content-end gap-2">
              <el-button size="small" :icon="Edit" @click="openDialog(item)">Sửa</el-button>
              <el-button size="small" type="danger" :icon="Delete" @click="handleDelete(item)" />
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-empty v-else description="Không có sản phẩm nào" />

    <el-dialog
      v-model="dialogVisible"
      :title="isEditMode ? 'Chỉnh sửa sản phẩm' : 'Thêm sản phẩm mới'"
      width="1000px"
      destroy-on-close
    >
      <el-form :model="itemForm" label-position="top">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="Tên sản phẩm chung">
              <el-input v-model="itemForm.tenSanPham" placeholder="Ví dụ: Bắp Phô Mai" />
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
          <el-input v-model="itemForm.hinhAnh" />
        </el-form-item>

        <hr class="my-4" />

        <div class="d-flex justify-content-between align-items-center mb-3">
          <h6 class="fw-bold m-0">Biến thể & Định lượng</h6>
          <el-button type="success" size="small" :icon="Plus" plain @click="addNewVariantRow">
            Thêm biến thể (Size)
          </el-button>
        </div>

        <el-table :data="itemForm.variants" border stripe>
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
                v-model="scope.row.price"
                size="small"
                :disabled="!scope.row.isEditing"
                style="width: 130px"
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
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">Hủy bỏ</el-button>
          <el-button type="primary" @click="saveProduct">
            {{ isEditMode ? 'Cập nhật' : 'Xác nhận thêm' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Plus, Edit, Delete, Search, Check } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { sanPhamDiKemService } from '@/services/api/admin/sanPhamDiKemService'
import debounce from 'lodash/debounce'

const activeCategory = ref('All')
const searchQuery = ref('')
const dialogVisible = ref(false)
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
    ElMessage.error('Lỗi tải danh sách sản phẩm')
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
  if(!row.idKichCo) return ElMessage.warning('Vui lòng chọn kích cỡ')
  row.isEditing = false
}

const saveProduct = async () => {
  if (!itemForm.value.tenSanPham) return ElMessage.error('Vui lòng nhập tên sản phẩm')
  if (!itemForm.value.idLoaiSanPham) return ElMessage.error('Vui lòng chọn danh mục')
  
  const selected = itemForm.value.variants.filter(v => v.active)
  if (!selected.length) return ElMessage.warning('Vui lòng chọn ít nhất 1 biến thể')

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
      ElMessage.success('Cập nhật sản phẩm thành công')
    } else {
      await sanPhamDiKemService.create(payload)
      ElMessage.success('Thêm sản phẩm thành công')
    }
    dialogVisible.value = false
    fetchItems()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || 'Có lỗi xảy ra khi lưu sản phẩm')
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
            ElMessage.success(`Đã xóa ${productIds.length} sản phẩm`);
            selectedItems.value = [];
            fetchItems();
        } catch (error) {
            ElMessage.error('Có lỗi khi xóa hàng loạt');
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
      ElMessage.success('Đã xóa sản phẩm')
      fetchItems()
    } catch (error) {
      ElMessage.error('Không thể xóa sản phẩm')
    }
  })
}

onMounted(() => {
  fetchMetadata()
  fetchItems()
})
</script>

<style scoped>
.admin-food-container { background: #f8f9fa; min-height: 100vh; }
.card-image-wrapper { height: 180px; overflow: hidden; position: relative; border-radius: 8px 8px 0 0; background: #eee; }
.size-badge { position: absolute; top: 10px; right: 10px; font-weight: bold; }
.food-card { transition: 0.3s; border: 1px solid #eee; }
.food-card:hover { transform: translateY(-5px); box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
.description-text {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  height: 32px; /* Đảm bảo các card đều nhau */
}
.selection-checkbox {
  position: absolute;
  top: 10px;
  left: 10px;
  z-index: 10;
  background: white;
  padding: 2px;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  line-height: 1;
}
.selected-card {
  border: 2px solid #4f46e5 !important;
}
</style>