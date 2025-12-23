<template>
  <button 
    @click="handleClick"
    class="p-2 -ml-2 rounded-full transition"
    :aria-label="ariaLabel"
  >
    <svg 
      class="w-5 h-5 text-foreground" 
      fill="none" 
      stroke="currentColor" 
      stroke-width="2" 
      viewBox="0 0 24 24"
    >
      <path d="M19 12H5M12 19l-7-7 7-7"/>
    </svg>
  </button>
</template>

<script setup>
import { useRouter } from 'vue-router'

const props = defineProps({
  to: {
    type: [String, Object],
    default: null
  },
  ariaLabel: {
    type: String,
    default: '뒤로 가기'
  }
})

const router = useRouter()

const handleClick = () => {
  if (props.to) {
    // Navigate to specific route if provided
    router.push(props.to)
  } else {
    // Default behavior: go back in history
    router.back()
  }
}
</script>

<style scoped>
/* Additional styles can be added here if needed */
</style>
