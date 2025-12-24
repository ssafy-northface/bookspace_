<template>
  <Transition name="fade-slide">
    <button
      v-if="shouldShow"
      type="button"
      aria-label="상단으로 이동"
      @click="scrollToTop"
      class="fixed z-40 bottom-44 right-6 w-12 h-12 rounded-full 
             bg-white dark:bg-slate-700 
             border-2 border-gray-200 dark:border-slate-600
             shadow-lg hover:shadow-xl 
             hover:scale-110 hover:border-blue-400 dark:hover:border-blue-500
             transition-all duration-300 
             flex items-center justify-center 
             text-gray-500 dark:text-gray-300 hover:text-blue-500 dark:hover:text-blue-400
             group"
    >
      <ArrowUpIcon class="w-5 h-5 transition-transform group-hover:-translate-y-0.5" />
    </button>
  </Transition>
</template>

<script setup>
import { ArrowUpIcon } from "@heroicons/vue/24/solid";
import { computed, onBeforeUnmount, onMounted, ref } from "vue";

const props = defineProps({
  /**
   * 외부에서 제어하고 싶을 때 사용 (true/false).
   * null 이면 스크롤 위치를 기준으로 자동 표시.
   */
  visible: {
    type: Boolean,
    default: null,
  },
  /**
   * 자동 표시 시 스크롤 위치 임계값(px)
   */
  threshold: {
    type: Number,
    default: 200,
  },
});

const autoVisible = ref(false);

const handleScroll = () => {
  autoVisible.value = window.scrollY > props.threshold;
};

onMounted(() => {
  handleScroll();
  window.addEventListener("scroll", handleScroll, { passive: true });
});

onBeforeUnmount(() => {
  window.removeEventListener("scroll", handleScroll);
});

const shouldShow = computed(() =>
  props.visible === null ? autoVisible.value : props.visible
);

const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: "smooth" });
};
</script>

<style scoped>
/* 나타나기/사라지기 애니메이션 */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s ease;
}

.fade-slide-enter-from,
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(20px);
}
</style>
