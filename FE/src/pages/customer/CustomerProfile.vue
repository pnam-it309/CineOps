<template>
  <div class="container-fluid p-4">
    <!-- Profile Header -->
    <el-card shadow="never" class="mb-4 bg-dark text-white border-0 rounded-4 overflow-hidden position-relative">
      <div class="position-absolute top-0 end-0 p-4 opacity-10">
        <el-icon size="200"><Postcard /></el-icon>
      </div>
      
      <div class="d-flex flex-wrap align-items-center gap-4 position-relative z-1">
        <el-upload
          class="position-relative"
          action="#"
          :show-file-list="false"
          :auto-upload="false"
          @change="handleAvatarChange"
        >
          <el-avatar :size="120" :src="profileForm.avatar" class="border border-4 border-primary">
            <el-icon><User /></el-icon>
          </el-avatar>
          <div class="position-absolute bottom-0 end-0 bg-primary rounded-circle d-flex align-items-center justify-content-center text-white p-2">
            <el-icon><Camera /></el-icon>
          </div>
        </el-upload>
        
        <div class="flex-grow-1">
          <div class="d-flex align-items-center gap-2 mb-1">
            <h2 class="mb-0 fs-2 fw-bold">{{ profileForm.fullName }}</h2>
            <el-tag type="warning" effect="dark" round size="small">GOLD MEMBER</el-tag>
          </div>
          <p class="opacity-75 mb-3">{{ profileForm.email }}</p>
          
          <div class="mw-400">
            <div class="d-flex justify-content-between small mb-1">
              <span>Points to Platinum</span>
              <span>{{ stats.points }}/1000</span>
            </div>
            <el-progress :percentage="42" :show-text="false" color="#ffc107" />
          </div>
        </div>
        
        <div class="d-flex gap-4 ms-auto bg-white bg-opacity-10 p-3 rounded-4 backdrop-blur">
          <div class="text-center">
            <h3 class="fs-4 fw-bold text-white mb-0">{{ stats.totalTickets }}</h3>
            <p class="opacity-75 small mb-0">Tickets</p>
          </div>
          <div class="vr opacity-25"></div>
          <div class="text-center">
            <h3 class="fs-4 fw-bold text-white mb-0">{{ stats.points }}</h3>
            <p class="opacity-75 small mb-0">Points</p>
          </div>
        </div>
      </div>
    </el-card>

    <!-- Profile Content Tabs -->
    <el-card shadow="never">
      <el-tabs v-model="activeTab" type="card">
        <!-- Personal Information Tab -->
        <el-tab-pane label="Personal Information" name="personal">
          <el-form :model="profileForm" label-width="140px" class="py-3 mw-800">
            <el-row :gutter="20">
              <el-col :xs="24" :md="12">
                <el-form-item label="Full Name">
                  <el-input v-model="profileForm.fullName" placeholder="Enter your full name" />
                </el-form-item>
              </el-col>
              
              <el-col :xs="24" :md="12">
                <el-form-item label="Username">
                  <el-input v-model="profileForm.username" placeholder="Enter username" disabled />
                </el-form-item>
              </el-col>
              
              <el-col :xs="24" :md="12">
                <el-form-item label="Email">
                  <el-input v-model="profileForm.email" type="email" placeholder="Enter email" />
                </el-form-item>
              </el-col>
              
              <el-col :xs="24" :md="12">
                <el-form-item label="Phone">
                  <el-input v-model="profileForm.phone" placeholder="Enter phone number" />
                </el-form-item>
              </el-col>
              
              <el-col :xs="24" :md="12">
                <el-form-item label="Date of Birth">
                  <el-date-picker
                    v-model="profileForm.birthDate"
                    type="date"
                    placeholder="Select date"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              
              <el-col :xs="24" :md="12">
                <el-form-item label="Gender">
                  <el-select v-model="profileForm.gender" placeholder="Select gender" style="width: 100%">
                    <el-option label="Male" value="male" />
                    <el-option label="Female" value="female" />
                    <el-option label="Other" value="other" />
                  </el-select>
                </el-form-item>
              </el-col>
              
              <el-col :span="24">
                <el-form-item label="Address">
                  <el-input
                    v-model="profileForm.address"
                    type="textarea"
                    :rows="3"
                    placeholder="Enter your address"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-form-item>
              <el-button type="primary" :icon="Check" @click="handleSaveProfile">
                Save Changes
              </el-button>
              <el-button :icon="Refresh" @click="handleResetForm">
                Reset
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- Security Tab -->
        <el-tab-pane label="Security" name="security">
          <el-form :model="passwordForm" label-width="160px" class="py-3 mw-800">
            <el-form-item label="Current Password">
              <el-input
                v-model="passwordForm.currentPassword"
                type="password"
                placeholder="Enter current password"
                show-password
              />
            </el-form-item>
            
            <el-form-item label="New Password">
              <el-input
                v-model="passwordForm.newPassword"
                type="password"
                placeholder="Enter new password"
                show-password
              />
            </el-form-item>
            
            <el-form-item label="Confirm Password">
              <el-input
                v-model="passwordForm.confirmPassword"
                type="password"
                placeholder="Confirm new password"
                show-password
              />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" :icon="Lock" @click="handleChangePassword">
                Change Password
              </el-button>
            </el-form-item>
          </el-form>
          
          <el-divider />
          
          <div class="mw-600">
            <h3 class="fs-5 mb-4">Security Options</h3>
            <div class="d-flex justify-content-between align-items-center py-3 border-bottom">
              <div>
                <h4 class="fs-6 fw-medium mb-1">Two-Factor Authentication</h4>
                <p class="text-secondary small mb-0">Add an extra layer of security to your account</p>
              </div>
              <el-switch v-model="twoFactorEnabled" />
            </div>
            
            <div class="d-flex justify-content-between align-items-center py-3">
              <div>
                <h4 class="fs-6 fw-medium mb-1">Email Notifications</h4>
                <p class="text-secondary small mb-0">Receive email alerts for account activities</p>
              </div>
              <el-switch v-model="emailNotifications" />
            </div>
          </div>
        </el-tab-pane>

        <!-- Preferences Tab -->
        <el-tab-pane label="Preferences" name="preferences">
          <el-form label-width="180px" class="py-3 mw-800">
            <el-form-item label="Language">
              <el-select v-model="preferences.language" style="width: 200px">
                <el-option label="English" value="en" />
                <el-option label="Tiếng Việt" value="vi" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="Preferred Cinema">
              <el-select v-model="preferences.preferredCinema" style="width: 300px">
                <el-option label="CineOps Central" value="central" />
                <el-option label="CineOps Downtown" value="downtown" />
                <el-option label="CineOps Mall" value="mall" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="Newsletter">
              <el-switch v-model="preferences.newsletter" active-text="Subscribe" inactive-text="Unsubscribe" />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="handleSavePreferences">
                Save Preferences
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { User, Camera, Check, Refresh, Lock, Postcard } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

const authStore = useAuthStore();
const activeTab = ref('personal');

const profileForm = ref({
  username: authStore.user?.username || 'customer_user',
  email: authStore.user?.email || 'customer@example.com',
  fullName: 'Nguyen Van A',
  phone: '0912345678',
  birthDate: new Date('1990-01-01'),
  gender: 'male',
  address: '123 Main Street, District 1, Ho Chi Minh City',
  avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
});

const passwordForm = ref({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
});

const stats = ref({
  totalTickets: 42,
  totalSpent: 840,
  points: 420
});

const twoFactorEnabled = ref(false);
const emailNotifications = ref(true);

const preferences = ref({
  language: 'vi',
  preferredCinema: 'central',
  newsletter: true
});

const handleAvatarChange = (file) => {
  const reader = new FileReader();
  reader.onload = (e) => {
    profileForm.value.avatar = e.target.result;
  };
  reader.readAsDataURL(file.raw);
};

const handleSaveProfile = () => {
  ElMessage.success('Profile updated successfully');
};

const handleResetForm = () => {
  ElMessage.info('Form reset');
};

const handleChangePassword = () => {
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    ElMessage.error('Passwords do not match');
    return;
  }
  ElMessage.success('Password changed successfully');
  passwordForm.value = {
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  };
};

const handleSavePreferences = () => {
  ElMessage.success('Preferences saved successfully');
};
</script>

<style scoped>
.min-w-200 {
  min-width: 200px;
}
.mw-800 {
  max-width: 800px;
}
.mw-600 {
  max-width: 600px;
}
</style>
