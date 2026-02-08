import { ref, watch } from 'vue';

/**
 * Debounced value composable
 * Useful for search inputs and performance optimization
 * @param {Ref} value - Value to debounce
 * @param {number} delay - Delay in milliseconds (default: 300)
 */
export function useDebounce(value, delay = 300) {
  const debouncedValue = ref(value.value);
  let timeout = null;

  watch(value, (newValue) => {
    if (timeout) {
      clearTimeout(timeout);
    }
    
    timeout = setTimeout(() => {
      debouncedValue.value = newValue;
    }, delay);
  });

  return debouncedValue;
}
