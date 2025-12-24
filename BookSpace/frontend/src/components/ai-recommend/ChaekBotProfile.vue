<template>
  <div 
    :class="containerClass"
    class="mx-auto mb-4 rounded-full bg-gradient-to-br from-blue-100 to-purple-100 dark:from-slate-700 dark:to-slate-600 flex items-center justify-center shadow-lg overflow-hidden"
  >
    <img 
      src="/images/chaekbot.gif" 
      alt="책봇"
      class="w-[85%] h-[85%] object-cover rounded-full"
    >
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  size: {
    type: String,
    default: 'md',
    validator: (value) => ['sm', 'md', 'lg', 'xl'].includes(value)
  }
})

// Size mappings - 원형에 딱 맞게
const sizeMap = {
  sm: 'w-10 h-10',
  md: 'w-16 h-16',
  lg: 'w-20 h-20',
  xl: 'w-24 h-24'
}

const containerClass = computed(() => sizeMap[props.size])
</script>

<style scoped>
/* Additional styles if needed */
</style>