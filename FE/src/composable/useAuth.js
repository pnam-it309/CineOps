import { computed } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';

/**
 * Composable wrapper around the auth store
 * Provides convenient auth-related helpers
 */
export function useAuth() {
  const authStore = useAuthStore();
  const router = useRouter();

  const isLoggedIn = computed(() => authStore.isAuthenticated);
  const currentUser = computed(() => authStore.user);
  const userRoles = computed(() => authStore.roles);
  const isAdmin = computed(() => authStore.isAdmin);
  const isStaff = computed(() => authStore.isStaff);

  /**
   * Check if user has a specific role
   * @param {string} role - Role to check
   * @returns {boolean}
   */
  const hasRole = (role) => {
    return userRoles.value.includes(role);
  };

  /**
   * Logout user and redirect to login
   */
  const logout = async () => {
    await authStore.logout();
    router.push({ name: 'Login' });
  };

  return {
    isLoggedIn,
    currentUser,
    userRoles,
    isAdmin,
    isStaff,
    hasRole,
    logout
  };
}
