<template>
  <div data-slot="collapsible" class="w-full">
    <button
      data-slot="collapsible-trigger"
      type="button"
      @click="toggle"
      :aria-expanded="isOpen"
      class="flex w-full items-center justify-between rounded-md border border-border bg-secondary px-4 py-2 text-sm font-medium text-secondary-foreground hover:bg-muted hover:text-muted-foreground transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 focus-visible:ring-offset-background"
    >
      <slot name="trigger" />
      <!-- 열림/닫힘 표시용 간단한 아이콘 -->
      <span
        class="ml-2 inline-block text-xs transition-transform duration-200"
        :class="isOpen ? 'rotate-90' : ''"
      >
        ▸
      </span>
    </button>

    <div
      v-show="isOpen"
      data-slot="collapsible-content"
      class="mt-2 rounded-md border border-border bg-card px-4 py-3 text-sm text-card-foreground transition-all duration-300"
    >
      <slot name="content" />
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from "vue";

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(["update:modelValue"]);

const isOpen = ref(props.modelValue);

// 부모에서 v-model 값을 바꿨을 때도 반영되도록
watch(
  () => props.modelValue,
  (value) => {
    isOpen.value = value;
  }
);

const toggle = () => {
  const next = !isOpen.value;
  isOpen.value = next;
  emit("update:modelValue", next);
};
</script>

<style scoped>
/* 색상/테마는 전부 global.css + Tailwind 유틸로 처리하니까 여기선 추가 스타일 없음 */
</style>
