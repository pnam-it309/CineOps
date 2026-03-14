<template>
  <div class="admin-food-form-page bg-white min-vh-100">
    <!-- Top Progress Bar (Subtle line) -->
    <div class="top-loading-bar" v-if="loading || saving"></div>

    <div class="container-fluid px-5 py-4">
      <!-- Header Section -->
      <div class="d-flex justify-content-between align-items-start mb-5">
        <div>
          <button class="btn-back-link mb-2" @click="goBack">
            <i class="bi bi-arrow-left me-2"></i> Quay lại danh sách
          </button>
          <h2 class="page-title fw-bold text-dark m-0">
            {{ isEditMode ? 'Chỉnh sửa sản phẩm' : 'Thêm sản phẩm mới' }}
          </h2>
        </div>
        <div class="d-flex gap-3">
          <el-button @click="goBack" class="btn-secondary-premium">Hủy bỏ</el-button>
          <el-button type="danger" class="btn-primary-premium shadow-sm" :loading="saving" @click="saveProduct">
            {{ isEditMode ? 'Cập nhật sản phẩm' : 'Tạo sản phẩm mới' }}
          </el-button>
        </div>
      </div>

      <el-form :model="itemForm" label-position="top" class="premium-form" v-loading="loading">
        <el-row :gutter="50">
          <!-- Left Main Column (Basic Info & Variants) -->
          <el-col :span="16">
            <!-- Section 1: Basic Info -->
            <div class="form-section mb-5">
              <div class="section-header d-flex align-items-center mb-4">
                <span class="section-icon me-3"><i class="bi bi-info-circle"></i></span>
                <h5 class="m-0 fw-bold text-dark">Thông tin cơ bản</h5>
              </div>
              <el-row :gutter="25">
                <el-col :span="14">
                  <el-form-item label="Tên sản phẩm *" required>
                    <el-input v-model="itemForm.tenSanPham" placeholder="VD: Bắp Phô Mai, Coca Cola..." size="large" />
                  </el-form-item>
                </el-col>
                <el-col :span="10">
                  <el-form-item label="Mã sản phẩm (Hệ thống)">
                    <el-input v-model="itemIdDisplay" disabled placeholder="Tự động phát sinh" size="large" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-form-item label="Mô tả sản phẩm">
                <el-input type="textarea" v-model="itemForm.moTa" :rows="5" placeholder="Mô tả ngắn gọn về sản phẩm này..." />
              </el-form-item>
            </div>

            <!-- Section 2: Variant Pricing -->
            <div class="form-section">
              <div class="section-header d-flex align-items-center justify-content-between mb-4">
                <div class="d-flex align-items-center">
                  <span class="section-icon me-3"><i class="bi bi-layers-half text-danger"></i></span>
                  <h5 class="m-0 fw-bold text-dark">Quản lý kích cỡ & Giá</h5>
                </div>
                <el-button type="danger" size="default" :icon="Plus" plain round @click="addNewVariantRow">
                  Thêm kích cỡ mới
                </el-button>
              </div>

              <div class="variant-table-container border rounded-3 overflow-hidden shadow-sm">
                <el-table :data="itemForm.variants" border stripe class="premium-table">
                  <el-table-column label="Kích cỡ" min-width="140">
                    <template #default="scope">
                      <el-select v-model="scope.row.idKichCo" size="default" class="w-100">
                        <el-option v-for="s in sizes" :key="s.id" :label="s.tenKichCo" :value="s.id" />
                      </el-select>
                    </template>
                  </el-table-column>

                  <el-table-column label="Định lượng" width="180">
                    <template #default="scope">
                      <div class="d-flex gap-1">
                        <el-input v-model.number="scope.row.giaTriDinhLuong" type="number" size="default" />
                        <el-select v-model="scope.row.idDonViTinh" size="default" style="width: 90px">
                          <el-option v-for="u in units" :key="u.id" :label="u.tenDonViTinh" :value="u.id" />
                        </el-select>
                      </div>
                    </template>
                  </el-table-column>

                  <el-table-column label="Hương vị" min-width="120">
                    <template #default="scope">
                      <el-input v-model="scope.row.huongVi" size="default" placeholder="VD: Cay, Ngọt..." />
                    </template>
                  </el-table-column>

                  <el-table-column label="Giá bán (VND)" width="150">
                    <template #default="scope">
                      <el-input v-model.number="scope.row.giaBan" type="number" size="default">
                        <template #suffix><span class="text-secondary small">₫</span></template>
                      </el-input>
                    </template>
                  </el-table-column>

                  <el-table-column label="Tồn kho" width="100">
                    <template #default="scope">
                      <el-input v-model.number="scope.row.soLuongTon" type="number" size="default" />
                    </template>
                  </el-table-column>

                  <el-table-column width="60" align="center">
                    <template #default="scope">
                      <button class="btn-delete-row" @click="removeVariantRow(scope.$index)">
                        <i class="bi bi-trash"></i>
                      </button>
                    </template>
                  </el-table-column>
                </el-table>
                <div v-if="itemForm.variants.length === 0" class="empty-state-table p-5 text-center bg-light">
                  <i class="bi bi-inboxes fs-1 text-secondary opacity-25 d-block mb-3"></i>
                  <span class="text-secondary">Chưa có phiên bản kích cỡ nào được thêm.</span>
                </div>
              </div>
            </div>
          </el-col>

          <!-- Right Sidebar Column (Config & Image) -->
          <el-col :span="8">
            <div class="sticky-top" style="top: 20px;">
              <!-- Section 3: Configuration -->
              <div class="form-section mb-4 p-4 border rounded-3 bg-light-subtle shadow-sm">
                <div class="section-header d-flex align-items-center mb-4">
                  <span class="section-icon me-3"><i class="bi bi-gear"></i></span>
                  <h5 class="m-0 fw-bold text-dark">Cấu hình & Phân loại</h5>
                </div>
                
                <el-form-item label="Danh mục sản phẩm *" required>
                  <el-select v-model="itemForm.idLoaiSanPham" class="w-100" size="large">
                    <el-option v-for="cat in categories" :key="cat.id" :label="cat.tenLoai" :value="cat.id" />
                  </el-select>
                </el-form-item>

              </div>

              <!-- Section 4: Image & Media -->
              <div class="form-section p-4 border rounded-3 bg-light-subtle shadow-sm">
                <div class="section-header d-flex align-items-center mb-4">
                  <span class="section-icon me-3"><i class="bi bi-image"></i></span>
                  <h5 class="m-0 fw-bold text-dark">Hình ảnh & Media</h5>
                </div>
                
                <el-form-item label="Link hình ảnh URL">
                  <el-input v-model="itemForm.hinhAnh" placeholder="Dán URL hình ảnh..." size="large">
                    <template #prefix><i class="bi bi-link-45deg"></i></template>
                  </el-input>
                </el-form-item>

                <div class="image-preview-wrapper mt-3 border rounded-3 bg-white d-flex align-items-center justify-content-center overflow-hidden" style="height: 250px;">
                  <img v-if="isValidImage" :src="itemForm.hinhAnh" class="w-100 h-100 object-fit-cover" />
                  <div v-else class="text-center text-secondary p-4">
                    <i class="bi bi-card-image fs-1 opacity-25 d-block mb-2"></i>
                    <div class="small">Chưa có ảnh hoặc link không hợp lệ</div>
                  </div>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Plus, Search } from '@element-plus/icons-vue'
import { sanPhamDiKemService } from '@/services/api/admin/sanPhamDiKemService'
import notification from '@/utils/notifications'
import confirmDialog from '@/utils/confirm'
import { ROUTES_CONSTANTS } from '@/constants/routeConstants'

const route = useRoute()
const router = useRouter()

const isEditMode = computed(() => !!route.params.id)
const loading = ref(false)
const saving = ref(false)

const categories = ref([])
const sizes = ref([])
const units = ref([])

const isProductActive = ref(true)

const itemForm = ref({
  id: null,
  tenSanPham: '',
  idLoaiSanPham: '',
  moTa: '',
  hinhAnh: '',
  variants: []
})

const itemIdDisplay = computed(() => {
  if (!itemForm.value.id) return ''
  return `SP-${itemForm.value.id.substring(0, 8).toUpperCase()}`
})

const isValidImage = computed(() => {
  if (!itemForm.value.hinhAnh) return false
  const imgRegex = /^(https?:\/\/).+\.(jpg|jpeg|png|webp|gif|svg)$/i;
  return imgRegex.test(itemForm.value.hinhAnh)
})

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

const fetchProductData = async (id) => {
  loading.value = true
  try {
    const res = await sanPhamDiKemService.getById(id)
    const sp = res.data.data
    
    if (sp) {
      itemForm.value = {
        id: sp.id,
        tenSanPham: sp.tenSanPham,
        idLoaiSanPham: sp.idLoaiSanPham,
        moTa: sp.moTa,
        hinhAnh: sp.hinhAnh,
        variants: sp.variants.map(v => ({
          idChiTiet: v.idChiTiet,
          idKichCo: v.idKichCo,
          idDonViTinh: v.idDonViTinh,
          giaTriDinhLuong: v.giaTriDinhLuong,
          huongVi: v.huongVi,
          giaBan: v.giaBan,
          soLuongTon: v.soLuongTon
        }))
      }
      isProductActive.value = sp.trangThai === 1
    } else {
      notification.error('Không tìm thấy thông tin sản phẩm')
      goBack()
    }
  } catch (error) {
    notification.error('Lỗi tải thông tin sản phẩm')
  } finally {
    loading.value = false
  }
}

const addNewVariantRow = () => {
  itemForm.value.variants.push({
    idKichCo: sizes.value[0]?.id,
    idDonViTinh: units.value[0]?.id,
    giaTriDinhLuong: 1,
    huongVi: '',
    giaBan: 10000,
    soLuongTon: 100
  })
}

const removeVariantRow = (index) => {
  itemForm.value.variants.splice(index, 1)
}

const saveProduct = async () => {
  if (!itemForm.value.tenSanPham) return notification.error('Vui lòng nhập tên sản phẩm')
  if (!itemForm.value.idLoaiSanPham) return notification.error('Vui lòng chọn danh mục')

  if (itemForm.value.hinhAnh && !isValidImage.value) {
    return notification.error('URL hình ảnh không hợp lệ')
  }

  try {
    if (isEditMode.value) {
      await confirmDialog.update('sản phẩm');
    } else {
      await confirmDialog.add('sản phẩm');
    }
  } catch { return; }

  saving.value = true
  try {
    const payload = {
      tenSanPham: itemForm.value.tenSanPham,
      idLoaiSanPham: itemForm.value.idLoaiSanPham,
      moTa: itemForm.value.moTa,
      hinhAnh: itemForm.value.hinhAnh,
      trangThai: isProductActive.value ? 1 : 0,
      variants: itemForm.value.variants.map(v => ({
        idChiTiet: v.idChiTiet,
        idKichCo: v.idKichCo,
        idDonViTinh: v.idDonViTinh,
        giaTriDinhLuong: v.giaTriDinhLuong,
        huongVi: v.huongVi,
        giaBan: v.giaBan,
        soLuongTon: v.soLuongTon
      }))
    }

    if (isEditMode.value) {
      await sanPhamDiKemService.update(itemForm.value.id, payload)
      notification.updateSuccess('sản phẩm')
    } else {
      await sanPhamDiKemService.create(payload)
      notification.addSuccess('sản phẩm và các biến thể')
    }
    goBack()
  } catch (error) {
    notification.error(error.response?.data?.message || 'Có lỗi xảy ra khi lưu sản phẩm')
  } finally {
    saving.value = false
  }
}

const goBack = () => {
  router.push({ name: ROUTES_CONSTANTS.ADMIN.children.FOOD.name })
}

onMounted(async () => {
  await fetchMetadata()
  if (isEditMode.value) {
    await fetchProductData(route.params.id)
  } else {
    itemForm.value.idLoaiSanPham = categories.value[0]?.id
    addNewVariantRow()
  }
})
</script>

<style scoped>
.admin-food-form-page {
  position: relative;
  overflow-x: hidden;
}

.top-loading-bar {
  position: absolute;
  top: 0;
  left: 0;
  height: 3px;
  background: var(--el-color-danger);
  width: 100%;
  animation: loading-pulse 2s infinite ease-in-out;
  z-index: 1000;
}

@keyframes loading-pulse {
  0% { opacity: 0.3; }
  50% { opacity: 1; }
  100% { opacity: 0.3; }
}

.btn-back-link {
  background: none;
  border: none;
  color: #64748b;
  font-size: 14px;
  font-weight: 500;
  padding: 0;
  transition: all 0.2s;
}

.btn-back-link:hover {
  color: var(--el-color-primary);
  transform: translateX(-4px);
}

.page-title {
  font-size: 28px;
  letter-spacing: -0.5px;
}

.btn-secondary-premium {
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 10px 25px;
  font-weight: 600;
  color: #64748b;
  background: #f8fafc;
}

.btn-primary-premium {
  border-radius: 8px;
  padding: 10px 30px;
  font-weight: 700;
}

.form-section {
  position: relative;
}

.section-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f1f5f9;
  border-radius: 10px;
  color: #475569;
  font-size: 18px;
}

.section-icon i.text-danger {
  background: #fff1f2;
  color: var(--el-color-danger) !important;
  width: 100%;
  height: 100%;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

:deep(.el-form-item__label) {
  font-weight: 600;
  color: #475569;
  margin-bottom: 8px;
}

:deep(.el-input__wrapper), :deep(.el-textarea__inner) {
  box-shadow: 0 0 0 1px #e2e8f0 inset !important;
  border-radius: 8px;
}

:deep(.el-input__wrapper.is-focus), :deep(.el-textarea__inner:focus) {
  box-shadow: 0 0 0 1px var(--el-color-primary) inset !important;
}

.variant-table-container {
  border: 1px solid #e2e8f0;
}

.premium-table :deep(th.el-table__cell) {
  background-color: #f8fafc !important;
  color: #1e293b !important;
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
}

.btn-delete-row {
  background: none;
  border: none;
  color: #94a3b8;
  padding: 5px;
  transition: all 0.2s;
}

.btn-delete-row:hover {
  color: var(--el-color-danger);
  transform: scale(1.1);
}

.image-preview-wrapper {
  background: #f8fafc;
}

.sticky-top {
  z-index: 10;
}
</style>
