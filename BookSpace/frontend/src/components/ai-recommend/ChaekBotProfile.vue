<template>
  <div 
    :class="containerClass"
    class="mx-auto mb-4 rounded-full bg-foreground flex items-center justify-center shadow-lg overflow-hidden"
  >
    <img 
      src="/images/chaekbot.png" 
      alt="책봇"
      :style="imageStyle"
      class="object-contain"
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

// Size mappings
const sizeMap = {
  sm: { container: 'w-10 h-10', imageScale: 0.7 },
  md: { container: 'w-16 h-16', imageScale: 0.7 },
  lg: { container: 'w-20 h-20', imageScale: 0.7 },
  xl: { container: 'w-24 h-24', imageScale: 0.7 }
}

const containerClass = computed(() => sizeMap[props.size].container)
const imageStyle = computed(() => ({
  width: `${sizeMap[props.size].imageScale * 100}%`,
  height: `${sizeMap[props.size].imageScale * 100}%`
}))
</script>

<style scoped>
/* Additional styles if needed */
</style>