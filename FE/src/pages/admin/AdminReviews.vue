<script setup>
import { ref } from 'vue';
import { Check, Close, Star, Filter, View, Message } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

const reviews = ref([
  { id: 1, movie: 'Avatar 3', user: 'Alice Johnson', rating: 5, comment: 'Incredible visuals! Must see in IMAX.', date: '2026-02-05', status: 'Pending' },
  { id: 2, movie: 'Spider-Man', user: 'Bob Smith', rating: 4, comment: 'Great action, but a bit long.', date: '2026-02-04', status: 'Approved' },
  { id: 3, movie: 'Batman', user: 'Charlie D.', rating: 1, comment: 'Check out my website for free movies!', date: '2026-02-03', status: 'Rejected' }
]);

const handleApprove = (id) => {
  const r = reviews.value.find(v => v.id === id);
  r.status = 'Approved';
  ElMessage.success('Review approved');
};

const handleReject = (id) => {
  const r = reviews.value.find(v => v.id === id);
  r.status = 'Rejected';
  ElMessage.warning('Review hidden from public');
};

const getStatusType = (status) => {
  if (status === 'Approved') return 'success';
  if (status === 'Rejected') return 'danger';
  return 'info';
};
</script>

<template>
  <div class="admin-reviews container-fluid p-4">
    <div class="d-flex justify-content-between align-items-center mb-4 pt-2">
      <div>
        <h2 class="fs-2 fw-bold text-dark mb-1">Review Moderation</h2>
        <p class="text-secondary small mb-0">Monitor and moderate movie reviews submitted by customers.</p>
      </div>
      <div class="d-flex gap-2">
        <el-button type="success" :icon="Check" plain round>Approve All</el-button>
      </div>
    </div>

    <!-- Review Grid -->
    <div class="row g-4">
      <div v-for="r in reviews" :key="r.id" class="col-md-6 col-xl-4">
        <el-card shadow="hover" class="border-0 shadow-sm rounded-4 h-100 p-0 overflow-hidden">
          <div class="p-4">
            <div class="d-flex justify-content-between align-items-start mb-3">
              <div>
                <h6 class="fw-bold mb-0 text-primary">{{ r.movie }}</h6>
                <div class="small text-secondary">By <strong>{{ r.user }}</strong> on {{ r.date }}</div>
              </div>
              <el-tag :type="getStatusType(r.status)" size="small" effect="light" round>{{ r.status }}</el-tag>
            </div>
            
            <el-rate v-model="r.rating" disabled class="mb-3" />
            
            <div class="p-3 bg-light rounded-3 small mb-4 font-italic text-secondary">
              "{{ r.comment }}"
            </div>
            
            <div class="d-flex gap-2 justify-content-end border-top pt-3 mt-auto">
              <el-button v-if="r.status !== 'Approved'" type="success" :icon="Check" size="small" @click="handleApprove(r.id)">Approve</el-button>
              <el-button v-if="r.status !== 'Rejected'" type="danger" :icon="Close" size="small" plain @click="handleReject(r.id)">Hide</el-button>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<style scoped>
.font-italic { font-style: italic; }
</style>
