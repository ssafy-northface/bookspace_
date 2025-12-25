<template>
  <div v-if="visible" class="suggested-questions">
    <div class="flex gap-2 overflow-x-auto pb-2 scrollbar-hide">
      <button
        v-for="(question, index) in questions"
        :key="index"
        @click="$emit('select', question)"
        class="flex-shrink-0 px-4 py-2.5 bg-gradient-to-r from-blue-500/10 to-purple-500/10 dark:from-blue-500/20 dark:to-purple-500/20 border border-blue-300 dark:border-blue-500/50 rounded-full text-sm text-blue-600 dark:text-blue-400 font-medium hover:from-blue-500 hover:to-purple-600 hover:text-white hover:border-transparent transition-all duration-200 shadow-sm hover:shadow-md whitespace-nowrap"
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
      "오늘 기분에 맞는 책을 추천해줘",
      "최근 베스트셀러 알려줘",
      "자기계발서 추천해줘",
      "감동적인 소설 찾아줘",
    ],
  },
  visible: {
    type: Boolean,
    default: true,
  },
});

defineEmits(["select"]);
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

</style>
