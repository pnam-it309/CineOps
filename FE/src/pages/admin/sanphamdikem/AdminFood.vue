<script setup>
import { ref, computed } from 'vue';
import { Plus, Edit, Delete, Search, Picture, View } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import AdminTableLayout from '@/components/AdminTableLayout.vue';

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

const currentPage = ref(1);
const pageSize = ref(12);

// --- Computed ---
const filteredItems = computed(() => {
  let result = items.value;
  if (activeCategory.value !== 'All') {
    result = result.filter(i => i.category === activeCategory.value);
  }
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(i => i.name.toLowerCase().includes(query) || i.description.toLowerCase().includes(query));
  }
  return result;
});

const pagedItems = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  return filteredItems.value.slice(start, start + pageSize.value);
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
                const index = items.value.findIndex(i => i.id === itemForm.value.id);
                if (index !== -1) items.value[index] = { ...itemForm.value };
                ElMessage.success('Cập nhật sản phẩm thành công');
            } else {
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

<template>
  <div class="admin-food-page">
    <AdminTableLayout
      title="Quản Lý Đồ Ăn & Combo"
      subtitle="Quản lý kho sản phẩm và combo rạp phim"
      titleIcon="bi bi-basket3-fill"
      addButtonLabel="Thêm sản phẩm"
      :data="pagedItems"
      :total="filteredItems.length"
      v-model:currentPage="currentPage"
      v-model:pageSize="pageSize"
      @add-click="openDialog()"
      @reset-filter="() => { activeCategory = 'All'; searchQuery = ''; }"
    >
      <template #filters>
        <div class="filter-item">
          <el-radio-group v-model="activeCategory" size="default">
            <el-radio-button label="Tất cả" value="All" />
            <el-radio-button label="Bắp" value="Popcorn" />
            <el-radio-button label="Nước" value="Drink" />
            <el-radio-button label="Combo" value="Combo" />
          </el-radio-group>
        </div>
        <div class="filter-item" style="width: 320px;">
          <el-input
            v-model="searchQuery"
            placeholder="Tìm kiếm sản phẩm..."
            :prefix-icon="Search"
            clearable
            size="default"
          />
        </div>
      </template>

      <template #content>
        <div class="product-grid p-1" v-if="pagedItems.length > 0">
          <el-row :gutter="20">
            <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="item in pagedItems" :key="item.id" class="mb-4">
              <div class="food-card bg-white rounded-4 border shadow-sm h-100 d-flex flex-column position-relative overflow-hidden hover-scale">
                <div class="card-image-wrapper position-relative">
                  <img :src="item.image" :alt="item.name" class="w-100 h-100 object-fit-cover">
                  <div class="category-badge position-absolute top-0 start-0 m-3">
                    <el-tag :type="getCategoryType(item.category)" effect="dark" size="small" class="rounded-pill border-0 shadow-sm">{{ getCategoryLabel(item.category) }}</el-tag>
                  </div>
                  <div class="card-actions position-absolute top-50 start-50 translate-middle d-flex gap-2 opacity-0">
                    <el-tooltip content="Chỉnh sửa" placement="top">
                      <button class="btn-action-icon btn-action-edit shadow-lg" @click="openDialog(item)">
                        <i class="bi bi-pencil"></i>
                      </button>
                    </el-tooltip>
                    <el-tooltip content="Xóa" placement="top">
                      <button class="btn-action-icon btn-action-delete shadow-lg" @click="handleDelete(item)">
                        <i class="bi bi-trash"></i>
                      </button>
                    </el-tooltip>
                  </div>
                </div>
                <div class="p-3 d-flex flex-column flex-grow-1">
                  <h5 class="fw-bold fs-6 mb-1 text-truncate text-dark" :title="item.name">{{ item.name }}</h5>
                  <p class="text-secondary small mb-3 line-clamp-2" style="font-size: 12px;">{{ item.description }}</p>
                  <div class="mt-auto d-flex justify-content-between align-items-end pt-2 border-top border-light">
                    <div>
                      <div class="text-primary fw-bold" style="font-size: 1.1rem;">{{ formatCurrency(item.price) }}</div>
                      <div class="small mt-1" :class="getStockClass(item.stock)" style="font-size: 11px;">
                        <i class="bi bi-box me-1"></i>{{ getStockLabel(item.stock) }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </el-col>
          </el-row>
        </div>
        <el-empty v-else description="Không tìm thấy sản phẩm nào" />
      </template>
    </AdminTableLayout>

    <!-- Add/Edit Dialog -->
    <el-dialog
      v-model="dialogVisible"
      width="600px"
      class="premium-dialog"
      destroy-on-close
    >
      <template #header>
        <div class="premium-header">
          <div class="premium-header-content">
            <div class="header-icon-box">
              <i :class="isEditMode ? 'bi bi-pencil-square' : 'bi bi-plus-lg'"></i>
            </div>
            <div class="header-text">
              <h5 class="title">{{ isEditMode ? 'Chỉnh sửa Sản phẩm' : 'Thêm Sản phẩm mới' }}</h5>
              <p class="subtitle opacity-75">Cập nhật thông tin thực đơn rạp phim</p>
            </div>
          </div>
        </div>
      </template>

      <el-form :model="itemForm" label-position="top" :rules="rules" ref="formRef" class="premium-form">
        <div class="row g-3">
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
        <div class="row g-3">
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
        <div class="d-flex gap-2 justify-content-end">
          <el-button @click="dialogVisible = false" class="btn-premium-secondary">HỦY BỎ</el-button>
          <el-button @click="saveItem" class="btn-premium-primary">
            {{ isEditMode ? 'CẬP NHẬT' : 'THÊM MỚI' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.card-image-wrapper {
  height: 200px;
  overflow: hidden;
}

.food-card {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.food-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04) !important;
}

.card-actions {
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(4px);
  padding: 10px;
  border-radius: 50px;
}

.food-card:hover .card-actions {
  opacity: 1 !important;
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-clamp: 2;
  overflow: hidden;
}
</style>
