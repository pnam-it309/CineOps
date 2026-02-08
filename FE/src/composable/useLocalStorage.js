import { ref, watch } from 'vue';

/**
 * Reactive localStorage wrapper
 * Syncs a ref with localStorage
 * @param {string} key - localStorage key
 * @param {*} defaultValue - Default value if key doesn't exist
 */
export function useLocalStorage(key, defaultValue) {
  const storedValue = localStorage.getItem(key);
  
  const value = ref(
    storedValue ? JSON.parse(storedValue) : defaultValue
  );

  // Watch for changes and sync to localStorage
  watch(value, (newValue) => {
    if (newValue === null || newValue === undefined) {
      localStorage.removeItem(key);
    } else {
      localStorage.setItem(key, JSON.stringify(newValue));
    }
  }, { deep: true });

  return value;
}
