<template>
  <div v-if="visible" class="suggested-questions">
    <div class="flex gap-2 overflow-x-auto pb-2 scrollbar-hide">
      <button
        v-for="(question, index) in questions"
        :key="index"
        @click="$emit('select', question)"
        class="flex-shrink-0 px-4 py-2 bg-card border border-border rounded-full text-sm text-foreground hover:bg-accent hover:border-border transition shadow-sm whitespace-nowrap"
      >
        {{ question }}
      </button>
    </div>
  </div>
</template>

<script setup>

const props = defineProps({
  questions: {
    type: Array,
    default: () => [
      '오늘 기분에 맞는 책을 추천해줘',
      '최근 베스트셀러 알려줘',
      '자기계발서 추천해줘',
      '감동적인 소설 찾아줘'
    ]
  },
  visible: {
    type: Boolean,
    default: true
  }
})

defineEmits(['select'])
</script>

<style scoped>
.suggested-questions {
  animation: slideUp 0.3s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Hide scrollbar but keep functionality */
.scrollbar-hide {
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.scrollbar-hide::-webkit-scrollbar {
  display: none;
}
</style>
