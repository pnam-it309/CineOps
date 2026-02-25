<template>
  <div class="admin-food-container p-4">
    <!-- Header Section -->
    <div class="d-flex justify-content-between align-items-center mb-4">
      <div>
        <h2 class="fw-bold text-dark mb-1" style="font-size: 18px;">Quản Lý Đồ Ăn & Combo</h2>
      </div>
      <el-button type="primary" :icon="Plus" @click="openDialog()">Thêm sản phẩm mới</el-button>
    </div>

    <!-- Filter & Search -->
    <div class="d-flex justify-content-between align-items-center mb-4 bg-white p-3 rounded shadow-sm">
      <el-radio-group v-model="activeCategory" size="large">
        <el-radio-button label="Tất cả" value="All" />
        <el-radio-button label="Bắp" value="Popcorn" />
        <el-radio-button label="Nước" value="Drink" />
        <el-radio-button label="Combo" value="Combo" />
      </el-radio-group>
      
      <div class="d-flex gap-2" style="width: 300px;">
        <el-input
          v-model="searchQuery"
          placeholder="Tìm kiếm sản phẩm..."
          :prefix-icon="Search"
          clearable
        />
      </div>
    </div>

    <!-- Product Grid -->
    <div class="product-grid" v-if="filteredItems.length > 0">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="item in filteredItems" :key="item.id" class="mb-4">
          <div class="food-card bg-white rounded-3 shadow-sm h-100 d-flex flex-column position-relative overflow-hidden hover-scale">
            <!-- Image & Badge -->
            <div class="card-image-wrapper position-relative">
              <img :src="item.image" :alt="item.name" class="w-100 h-100 object-fit-cover">
              <div class="category-badge position-absolute top-0 start-0 m-2">
                <el-tag :type="getCategoryType(item.category)" effect="dark" size="small">{{ getCategoryLabel(item.category) }}</el-tag>
              </div>
              
              <!-- Hover Actions -->
              <div class="card-actions position-absolute top-50 start-50 translate-middle d-flex gap-2 opacity-0">
                <el-button type="primary" circle :icon="Edit" @click="openDialog(item)" />
                <el-button type="danger" circle :icon="Delete" @click="handleDelete(item)" />
              </div>
            </div>

            <!-- Content -->
            <div class="p-3 d-flex flex-column flex-grow-1">
              <h5 class="fw-bold fs-6 mb-1 text-truncate" :title="item.name">{{ item.name }}</h5>
              <p class="text-secondary x-small mb-3 line-clamp-2">{{ item.description }}</p>
              
              <div class="mt-auto d-flex justify-content-between align-items-end">
                <div>
                  <div class="text-primary fw-bold fs-5">{{ formatCurrency(item.price) }}</div>
                  <div class="x-small" :class="getStockClass(item.stock)">
                    {{ getStockLabel(item.stock) }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
    
    <el-empty v-else description="Không tìm thấy sản phẩm nào" />

    <!-- Add/Edit Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEditMode ? 'Chỉnh sửa sản phẩm' : 'Thêm sản phẩm mới'"
      width="500px"
      class="rounded-3"
      destroy-on-close
    >
      <el-form :model="itemForm" label-position="top" :rules="rules" ref="formRef">
        <div class="row">
          <div class="col-8">
            <el-form-item label="Tên sản phẩm" prop="name">
              <el-input v-model="itemForm.name" placeholder="VD: Bắp Phô Mai" />
            </el-form-item>
          </div>
          <div class="col-4">
            <el-form-item label="Danh mục" prop="category">
              <el-select v-model="itemForm.category" class="w-100">
                <el-option label="Bắp" value="Popcorn" />
                <el-option label="Nước" value="Drink" />
                <el-option label="Combo" value="Combo" />
                <el-option label="Khác" value="Snack" />
              </el-select>
            </el-form-item>
          </div>
        </div>

        <el-form-item label="Mô tả" prop="description">
          <el-input type="textarea" v-model="itemForm.description" :rows="2" placeholder="Mô tả ngắn về sản phẩm..." />
        </el-form-item>

        <div class="row">
          <div class="col-6">
            <el-form-item label="Giá bán (VNĐ)" prop="price">
              <el-input-number v-model="itemForm.price" class="w-100" :min="0" :step="1000" controls-position="right" />
            </el-form-item>
          </div>
          <div class="col-6">
            <el-form-item label="Tồn kho" prop="stock">
              <el-input-number v-model="itemForm.stock" class="w-100" :min="0" controls-position="right" />
            </el-form-item>
          </div>
        </div>

        <el-form-item label="Hình ảnh URL" prop="image">
           <el-input v-model="itemForm.image" placeholder="https://..." :prefix-icon="Picture">
             <template #append>
               <el-button :icon="View" />
             </template>
           </el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">Hủy</el-button>
          <el-button type="primary" @click="saveItem">
            {{ isEditMode ? 'Cập nhật' : 'Thêm mới' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { Plus, Edit, Delete, Search, Picture, View } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';

// --- State ---
const activeCategory = ref('All');
const searchQuery = ref('');
const dialogVisible = ref(false);
const isEditMode = ref(false);
const formRef = ref(null);

const items = ref([
  { 
    id: 1, 
    name: 'Bắp Ngọt (Sweet Popcorn)', 
    category: 'Popcorn', 
    price: 79000, 
    stock: 150, 
    image: 'https://cdn.galaxycine.vn/media/2024/3/29/menuboard-co-a-combo-1_1711690323351.jpg',
    description: 'Bắp nổ hương vị ngọt truyền thống, giòn tan.'
  },
  { 
    id: 2, 
    name: 'Bắp Phô Mai (Cheese Popcorn)', 
    category: 'Popcorn', 
    price: 89000, 
    stock: 85, 
    image: 'https://cdn.galaxycine.vn/media/2024/3/29/menuboard-co-a-combo-2_1711690333219.jpg',
    description: 'Bắp nổ phủ phô mai đậm đà, thơm ngon khó cưỡng.'
  },
  { 
    id: 3, 
    name: 'Coca Cola tươi (L)', 
    category: 'Drink', 
    price: 42000, 
    stock: 500, 
    image: 'https://cdn.galaxycine.vn/media/2023/12/28/nuoc-ngot-ly-32oz_1703734007137.jpg',
    description: 'Nước ngọt có gas giải khát sảng khoái.'
  },
  { 
    id: 4, 
    name: 'iCombo 1 (1 Bắp + 1 Nước)', 
    category: 'Combo', 
    price: 110000, 
    stock: 100, 
    image: 'https://cdn.galaxycine.vn/media/2024/3/29/menuboard-co-a-combo-4_1711690352521.jpg',
    description: 'Combo tiết kiệm dành cho 1 người.'
  },
  { 
    id: 5, 
    name: 'iCombo 2 (1 Bắp + 2 Nước)', 
    category: 'Combo', 
    price: 155000, 
    stock: 60, 
    image: 'https://cdn.galaxycine.vn/media/2024/3/29/menuboard-co-a-combo-5_1711690361184.jpg',
    description: 'Combo hoàn hảo cho cặp đôi.'
  },
  {
      id: 6,
      name: 'Milo Siêu To',
      category: 'Drink',
      price: 55000,
      stock: 30,
      image: 'https://cdn.galaxycine.vn/media/2024/1/12/milo-32_1705052329471.jpg',
      description: 'Thức uống lúa mạch đậm đà năng lượng.'
  }
]);

const itemForm = ref({
  id: null,
  name: '',
  category: 'Popcorn',
  price: 0,
  stock: 100,
  image: '',
  description: ''
});

const rules = {
  name: [{ required: true, message: 'Vui lòng nhập tên sản phẩm', trigger: 'blur' }],
  category: [{ required: true, message: 'Vui lòng chọn danh mục', trigger: 'change' }],
  price: [{ required: true, message: 'Vui lòng nhập giá', trigger: 'blur' }],
};

// --- Computed ---
const filteredItems = computed(() => {
  let result = items.value;
  
  // Filter by Category
  if (activeCategory.value !== 'All') {
    result = result.filter(i => i.category === activeCategory.value);
  }

  // Filter by Search Query
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(i => i.name.toLowerCase().includes(query) || i.description.toLowerCase().includes(query));
  }
  
  return result;
});

// --- Methods ---
const getCategoryLabel = (cat) => {
  const map = { 'Popcorn': 'Bắp', 'Drink': 'Nước', 'Combo': 'Combo', 'Snack': 'Khác' };
  return map[cat] || cat;
};

const getCategoryType = (cat) => {
  const map = { 'Popcorn': 'warning', 'Drink': 'primary', 'Combo': 'danger', 'Snack': 'info' };
  return map[cat] || 'info';
};

const formatCurrency = (value) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};

const getStockLabel = (stock) => {
  if (stock === 0) return 'Hết hàng';
  if (stock < 20) return `Sắp hết (${stock})`;
  return `Còn hàng (${stock})`;
};

const getStockClass = (stock) => {
  if (stock === 0) return 'text-danger fw-bold';
  if (stock < 20) return 'text-warning fw-bold';
  return 'text-success';
};

const openDialog = (item = null) => {
  if (item) {
    isEditMode.value = true;
    itemForm.value = { ...item };
  } else {
    isEditMode.value = false;
    itemForm.value = {
      id: null,
      name: '',
      category: 'Popcorn',
      price: 0,
      stock: 100,
      image: '',
      description: ''
    };
  }
  dialogVisible.value = true;
};

const saveItem = async () => {
    if (!formRef.value) return;
    await formRef.value.validate((valid) => {
        if (valid) {
            if (isEditMode.value) {
                // Update
                const index = items.value.findIndex(i => i.id === itemForm.value.id);
                if (index !== -1) items.value[index] = { ...itemForm.value };
                ElMessage.success('Cập nhật sản phẩm thành công');
            } else {
                // Add
                const newId = Math.max(...items.value.map(i => i.id)) + 1;
                items.value.push({ ...itemForm.value, id: newId });
                ElMessage.success('Thêm sản phẩm mới thành công');
            }
            dialogVisible.value = false;
        }
    });
};

const handleDelete = (item) => {
  ElMessageBox.confirm(
    `Bạn có chắc chắn muốn xóa "${item.name}" không?`,
    'Cảnh báo',
    {
      confirmButtonText: 'Xóa',
      cancelButtonText: 'Hủy',
      type: 'warning',
    }
  ).then(() => {
    items.value = items.value.filter(i => i.id !== item.id);
    ElMessage.success('Đã xóa sản phẩm');
  }).catch(() => {});
};
</script>

<style scoped>
.admin-food-container {
  min-height: 100%;
  background-color: #f8f9fa;
}

.card-image-wrapper {
  height: 180px;
  overflow: hidden;
}

.food-card {
  border: 1px solid #e9ecef;
  transition: all 0.3s ease;
}

.food-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 .5rem 1rem rgba(0,0,0,.15)!important;
  border-color: var(--el-color-primary-light-5);
}

.hover-scale .card-actions {
  transition: opacity 0.3s ease;
}

.food-card:hover .card-actions {
  opacity: 1 !important;
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.x-small {
  font-size: 0.85rem;
}
</style>
