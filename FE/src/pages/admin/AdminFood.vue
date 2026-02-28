<template>
  <div class="admin-food-container p-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h2 class="fw-bold" style="font-size:18px">Quản Lý Sản Phẩm Đi Kèm</h2>
      <el-button type="primary" :icon="Plus" @click="openDialog()">
        Thêm sản phẩm mới
      </el-button>
    </div>

    <div class="d-flex justify-content-between align-items-center mb-4 bg-white p-3 rounded shadow-sm">
      <el-radio-group v-model="activeCategory" size="large">
        <el-radio-button label="Tất cả" value="All" />
        <el-radio-button label="Bắp" value="Popcorn" />
        <el-radio-button label="Nước" value="Drink" />
      </el-radio-group>

      <el-input
        v-model="searchQuery"
        placeholder="Tìm kiếm sản phẩm..."
        :prefix-icon="Search"
        clearable
        style="width:300px"
      />
    </div>

    <el-row :gutter="20" v-if="filteredItems.length">
      <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="item in filteredItems" :key="item.id" class="mb-4">
        <div class="food-card bg-white rounded shadow-sm h-100 d-flex flex-column">
          <div class="card-image-wrapper">
            <img :src="item.image" class="w-100 h-100 object-fit-cover" />
            <el-tag effect="dark" class="size-badge" type="warning">Size {{ item.size }}</el-tag>
          </div>
          <div class="p-3 d-flex flex-column flex-grow-1">
            <h6 class="fw-bold mb-1">{{ item.name }}</h6>
            
            <p class="text-muted small mb-2 description-text">
              {{ item.description || 'Chưa có mô tả cho sản phẩm này.' }}
            </p>

            <div class="mt-auto">
              <div class="d-flex justify-content-between align-items-center">
                <span class="text-primary fw-bold fs-5">{{ formatCurrency(item.price) }}</span>
                <span class="text-muted small">{{ item.quantityValue }}{{ item.unit }}</span>
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
              <el-input v-model="itemForm.name" placeholder="Ví dụ: Bắp Phô Mai" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Danh mục">
              <el-select v-model="itemForm.category" class="w-100" @change="handleCategoryChange">
                <el-option label="Bắp" value="Popcorn" />
                <el-option label="Nước" value="Drink" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="Mô tả sản phẩm">
          <el-input type="textarea" v-model="itemForm.description" rows="2" placeholder="Nhập mô tả hiển thị trên card..." />
        </el-form-item>

        <el-form-item label="Hình ảnh URL">
          <el-input v-model="itemForm.image" />
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

          <el-table-column label="Kích cỡ" width="120">
            <template #default="scope">
              <el-input v-model="scope.row.size" placeholder="M, L, XL..." size="small" :disabled="!scope.row.isEditing" />
            </template>
          </el-table-column>

          <el-table-column label="Định lượng (Số | Đơn vị)">
            <template #default="scope">
              <div class="d-flex align-items-center gap-1">
                <el-input-number 
                  v-model="scope.row.quantityValue" 
                  size="small" 
                  :disabled="!scope.row.isEditing"
                  :controls="false"
                  style="width: 80px"
                />
                <el-input 
                  v-model="scope.row.unit" 
                  size="small" 
                  style="width: 70px"
                  :disabled="!scope.row.isEditing"
                />
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
                v-model="scope.row.stock"
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
import { ref, computed } from 'vue'
import { Plus, Edit, Delete, Search, Check } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeCategory = ref('All')
const searchQuery = ref('')
const dialogVisible = ref(false)
const isEditMode = ref(false)

const items = ref([
  {
    id: 1,
    name: 'Bắp Phô Mai',
    size: 'M',
    category: 'Popcorn',
    description: 'Bắp nổ nóng hổi phủ lớp bột phô mai thơm nồng, đậm đà.',
    image: 'https://cdn.galaxycine.vn/media/2024/3/29/menuboard-co-a-combo-2_1711690333219.jpg',
    quantityValue: 100,
    unit: 'g',
    price: 79000,
    stock: 150
  }
])

const itemForm = ref({
  id: null, name: '', category: 'Popcorn', description: '', image: '', variants: []
})

const formatCurrency = v => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(v)

const filteredItems = computed(() => {
  let result = items.value
  if (activeCategory.value !== 'All') result = result.filter(i => i.category === activeCategory.value)
  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase()
    result = result.filter(i => i.name.toLowerCase().includes(q))
  }
  return result
})

// Xử lý đổi đơn vị tự động khi chọn danh mục
const handleCategoryChange = (val) => {
  const newUnit = val === 'Popcorn' ? 'g' : 'ml'
  itemForm.value.variants.forEach(v => {
    v.unit = newUnit
  })
}

const openDialog = (product = null) => {
  if (product) {
    isEditMode.value = true
    itemForm.value = {
      ...JSON.parse(JSON.stringify(product)),
      variants: [{
        size: product.size,
        quantityValue: product.quantityValue,
        unit: product.unit,
        price: product.price,
        stock: product.stock,
        active: true,
        isEditing: false
      }]
    }
  } else {
    isEditMode.value = false
    itemForm.value = {
      id: null, name: '', category: 'Popcorn', description: '', image: '',
      variants: [
        { size: 'M', quantityValue: 0, unit: 'g', price: 0, stock: 0, active: true, isEditing: false },
        { size: 'L', quantityValue: 0, unit: 'g', price: 0, stock: 0, active: true, isEditing: false }
      ]
    }
  }
  dialogVisible.value = true
}

// THÊM BIẾN THỂ MỚI
const addNewVariantRow = () => {
  const currentUnit = itemForm.value.category === 'Popcorn' ? 'g' : 'ml'
  itemForm.value.variants.push({
    size: '',
    quantityValue: 0,
    unit: currentUnit,
    price: 0,
    stock: 0,
    active: true,
    isEditing: true // Dòng mới thêm cho sửa ngay
  })
}

const removeVariantRow = (index) => {
  itemForm.value.variants.splice(index, 1)
}

const confirmUpdateVariant = (row) => {
  if(!row.size) return ElMessage.warning('Vui lòng nhập tên kích cỡ')
  ElMessageBox.confirm(`Xác nhận lưu biến thể size ${row.size}?`, 'Xác nhận', {
    confirmButtonText: 'Lưu',
    cancelButtonText: 'Hủy',
    type: 'success'
  }).then(() => {
    row.isEditing = false
    ElMessage.success('Đã lưu biến thể thành công')
  })
}

const saveProduct = () => {
  if (!itemForm.value.name) return ElMessage.error('Vui lòng nhập tên sản phẩm')
  const selected = itemForm.value.variants.filter(v => v.active)
  if (!selected.length) return ElMessage.warning('Vui lòng chọn/nhập ít nhất 1 biến thể')

  if (isEditMode.value) {
    const v = selected[0]
    const idx = items.value.findIndex(i => i.id === itemForm.value.id)
    items.value[idx] = { ...itemForm.value, size: v.size, price: v.price, stock: v.stock, quantityValue: v.quantityValue, unit: v.unit }
    ElMessage.success('Cập nhật sản phẩm thành công')
  } else {
    selected.forEach(v => {
      items.value.push({
        ...JSON.parse(JSON.stringify(itemForm.value)),
        id: Date.now() + Math.random(),
        size: v.size, price: v.price, stock: v.stock, quantityValue: v.quantityValue, unit: v.unit
      })
    })
    ElMessage.success(`Đã thêm thành công ${selected.length} thẻ sản phẩm`)
  }
  dialogVisible.value = false
}

const handleDelete = item => {
  ElMessageBox.confirm(`Bạn có chắc muốn xóa ${item.name} size ${item.size}?`, 'Cảnh báo', { type: 'warning' }).then(() => {
    items.value = items.value.filter(i => i.id !== item.id)
    ElMessage.success('Đã xóa sản phẩm')
  })
}
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
</style>