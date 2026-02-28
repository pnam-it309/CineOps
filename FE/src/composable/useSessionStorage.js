import { ref, watch } from 'vue';

/**
 * Reactive sessionStorage wrapper
 * Syncs a ref with sessionStorage
 * @param {string} key - sessionStorage key
 * @param {*} defaultValue - Default value if key doesn't exist
 */
export function useSessionStorage(key, defaultValue) {
  const storedValue = sessionStorage.getItem(key);
  
  const value = ref(
    storedValue ? JSON.parse(storedValue) : defaultValue
  );

  // Watch for changes and sync to sessionStorage
  watch(value, (newValue) => {
    if (newValue === null || newValue === undefined) {
      sessionStorage.removeItem(key);
    } else {
      sessionStorage.setItem(key, JSON.stringify(newValue));
    }
  }, { deep: true });

  return value;
}
