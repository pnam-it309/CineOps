<script setup>
import { ref } from 'vue';
import { Plus, Edit, Delete, Coffee, IceTea, IceCream, Star, Setting } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

const items = ref([
  { id: 1, name: 'Salty Popcorn (L)', category: 'Snack', price: 65000, stock: 154, image: 'https://dummyimage.com/150?text=Popcorn' },
  { id: 2, name: 'Caramel Popcorn (L)', category: 'Snack', price: 75000, stock: 89, image: 'https://dummyimage.com/150?text=Caramel' },
  { id: 3, name: 'Coca Cola (XL)', category: 'Drink', price: 45000, stock: 420, image: 'https://dummyimage.com/150?text=Coke' },
  { id: 4, name: 'Combo Solo', category: 'Combo', price: 95000, stock: 'N/A', image: 'https://dummyimage.com/150?text=Solo+Combo' },
  { id: 5, name: 'Combo Couple', category: 'Combo', price: 175000, stock: 'N/A', image: 'https://dummyimage.com/150?text=Couple+Combo' }
]);

const activeCategory = ref('Tất cả');
const categories = ['Tất cả', 'Đồ ăn', 'Đồ uống', 'Combo'];

const filteredItems = computed(() => {
  if (activeCategory.value === 'All') return items.value;
  return items.value.filter(i => i.category === activeCategory.value);
});

const dialogVisible = ref(false);
const itemForm = ref({
  name: '',
  category: 'Snack',
  price: 0,
  stock: 0,
  image: ''
});
</script>

<script>
// Computed needed in separate script or inside setup with ref
import { computed } from 'vue';
</script>

<template>
  <div class="admin-food w-100 h-100 d-flex flex-column overflow-hidden no-scroll">
    <div class="d-flex justify-content-between align-items-center mb-3 pt-2 flex-shrink-0">
      <div>
        <h2 class="fs-2 fw-bold text-dark mb-1">Đồ ăn & Combo</h2>
      </div>
      <el-button type="primary" size="default" :icon="Plus" round @click="dialogVisible = true">Thêm sản phẩm</el-button>
    </div>

    <!-- Category Tabs -->
    <div class="mb-3 flex-shrink-0">
      <el-radio-group v-model="activeCategory" size="default">
        <el-radio-button v-for="cat in categories" :key="cat" :value="cat" />
      </el-radio-group>
    </div>

    <!-- Menu Grid Container -->
    <div class="flex-grow-1 overflow-auto no-scroll p-1">
      <div class="row g-3">
        <div v-for="item in filteredItems" :key="item.id" class="col-sm-6 col-md-4 col-xl-3">
          <el-card shadow="hover" class="border-black shadow-sm rounded-4 overflow-hidden h-100 p-0 text-center">
            <img :src="item.image" class="w-100 object-fit-cover mb-1" style="height: 140px;">
            <div class="p-2">
              <el-tag size="small" effect="light" class="mb-1">{{ item.category }}</el-tag>
              <h5 class="fw-bold x-small mb-1 text-truncate">{{ item.name }}</h5>
              <div class="text-primary fw-bold small mb-1">{{ item.price.toLocaleString() }}đ</div>
              <div class="d-flex justify-content-between align-items-center mt-2 pt-2 border-top">
                <div class="x-small text-secondary">Tồn kho: <span :class="item.stock < 50 ? 'text-danger fw-bold' : ''">{{
                    item.stock }}</span></div>
                <el-button-group>
                  <el-button :icon="Edit" size="small" circle />
                  <el-button :icon="Delete" size="small" type="danger" plain circle />
                </el-button-group>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </div>

    <!-- Add Item Dialog -->
    <el-dialog v-model="dialogVisible" title="Thêm món mới vào thực đơn" width="450px" class="rounded-4">
      <el-form :model="itemForm" label-position="top">
        <el-form-item label="Tên sản phẩm" required>
          <el-input v-model="itemForm.name" placeholder="VD: Bắp rang lớn" />
        </el-form-item>
        <el-form-item label="Danh mục">
          <el-select v-model="itemForm.category" class="w-100">
            <el-option label="Đồ ăn" value="Snack" />
            <el-option label="Đồ uống" value="Drink" />
            <el-option label="Combo" value="Combo" />
          </el-select>
        </el-form-item>
        <div class="row g-2">
          <div class="col-6">
            <el-form-item label="Giá bán (VNĐ)">
              <el-input-number v-model="itemForm.price" class="w-100" />
            </el-form-item>
          </div>
          <div class="col-6">
            <el-form-item label="Tồn kho ban đầu">
              <el-input-number v-model="itemForm.stock" class="w-100" />
            </el-form-item>
          </div>
        </div>
        <el-form-item label="Link ảnh sản phẩm">
          <el-input v-model="itemForm.image" placeholder="https://..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="d-flex gap-2 justify-content-end">
          <el-button @click="dialogVisible = false">Hủy</el-button>
          <el-button type="primary" class="px-4" @click="dialogVisible = false">Lưu sản phẩm</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.admin-food {
  height: calc(100vh - 84px);
}

:deep(.el-card) {
  border: 1px solid #000 !important;
  border-radius: 12px !important;
  overflow: hidden !important;
}

.object-fit-cover {
  object-fit: cover;
}

.no-scroll {
  scrollbar-width: none !important;
  -ms-overflow-style: none !important;
  overflow: hidden !important;
}

.no-scroll::-webkit-scrollbar {
  display: none !important;
}

.overflow-auto.no-scroll {
  overflow-y: auto !important;
}

.x-small {
  font-size: 0.7rem;
}
</style>
