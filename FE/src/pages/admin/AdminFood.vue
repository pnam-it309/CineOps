<script setup>
import { ref } from 'vue';
import { Plus, Edit, Delete, Coffee, IceTea, IceCream, Star, Setting } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

const items = ref([
  { id: 1, name: 'Salty Popcorn (L)', category: 'Snack', price: 65000, stock: 154, image: 'https://via.placeholder.com/150?text=Popcorn' },
  { id: 2, name: 'Caramel Popcorn (L)', category: 'Snack', price: 75000, stock: 89, image: 'https://via.placeholder.com/150?text=Caramel' },
  { id: 3, name: 'Coca Cola (XL)', category: 'Drink', price: 45000, stock: 420, image: 'https://via.placeholder.com/150?text=Coke' },
  { id: 4, name: 'Combo Solo', category: 'Combo', price: 95000, stock: 'N/A', image: 'https://via.placeholder.com/150?text=Solo+Combo' },
  { id: 5, name: 'Combo Couple', category: 'Combo', price: 175000, stock: 'N/A', image: 'https://via.placeholder.com/150?text=Couple+Combo' }
]);

const activeCategory = ref('All');
const categories = ['All', 'Snack', 'Drink', 'Combo'];

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
  <div class="admin-food container-fluid p-4">
    <div class="d-flex justify-content-between align-items-center mb-4 pt-2">
      <div>
        <h2 class="fs-2 fw-bold text-dark mb-1">Concessions & Combos</h2>
        <p class="text-secondary small mb-0">Manage the cinema menu, inventory, and special snack packages.</p>
      </div>
      <el-button type="primary" size="large" :icon="Plus" round @click="dialogVisible = true">Add Menu Item</el-button>
    </div>

    <!-- Category Tabs -->
    <div class="mb-4">
      <el-radio-group v-model="activeCategory" size="large">
        <el-radio-button v-for="cat in categories" :key="cat" :label="cat" />
      </el-radio-group>
    </div>

    <!-- Menu Grid -->
    <div class="row g-4">
      <div v-for="item in filteredItems" :key="item.id" class="col-sm-6 col-md-4 col-xl-3">
        <el-card shadow="hover" class="border-0 shadow-sm rounded-4 overflow-hidden h-100 p-0 text-center">
          <img :src="item.image" class="w-100 object-fit-cover mb-2" style="height: 160px;">
          <div class="p-3">
            <el-tag size="small" effect="light" class="mb-2">{{ item.category }}</el-tag>
            <h5 class="fw-bold fs-6 mb-1">{{ item.name }}</h5>
            <div class="text-primary fw-bold fs-5 mb-2">{{ item.price.toLocaleString() }}đ</div>
            <div class="d-flex justify-content-between align-items-center mt-3 pt-3 border-top">
              <div class="small text-secondary">Stock: <span :class="item.stock < 50 ? 'text-danger fw-bold' : ''">{{ item.stock }}</span></div>
              <el-button-group>
                <el-button :icon="Edit" size="small" circle />
                <el-button :icon="Delete" size="small" type="danger" plain circle />
              </el-button-group>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- Add Item Dialog -->
    <el-dialog v-model="dialogVisible" title="Add Menu Item" width="450px" class="rounded-4">
      <el-form :model="itemForm" label-position="top">
        <el-form-item label="Item Name" required>
          <el-input v-model="itemForm.name" placeholder="e.g. Popcorn Large" />
        </el-form-item>
        <el-form-item label="Category">
          <el-select v-model="itemForm.category" class="w-100">
            <el-option label="Snack" value="Snack" />
            <el-option label="Drink" value="Drink" />
            <el-option label="Combo" value="Combo" />
          </el-select>
        </el-form-item>
        <div class="row g-2">
          <div class="col-6">
            <el-form-item label="Price (VND)">
              <el-input-number v-model="itemForm.price" class="w-100" />
            </el-form-item>
          </div>
          <div class="col-6">
            <el-form-item label="Initial Stock">
              <el-input-number v-model="itemForm.stock" class="w-100" />
            </el-form-item>
          </div>
        </div>
        <el-form-item label="Image URL">
          <el-input v-model="itemForm.image" placeholder="https://..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="d-flex gap-2 justify-content-end">
          <el-button @click="dialogVisible = false">Cancel</el-button>
          <el-button type="primary" class="px-4" @click="dialogVisible = false">Save Item</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.object-fit-cover {
  object-fit: cover;
}
</style>
