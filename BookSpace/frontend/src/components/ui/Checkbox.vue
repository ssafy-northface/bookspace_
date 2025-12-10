<template>
  <div
    :class="[
      // 베이스 스타일
      'peer border-input bg-background dark:bg-input/30',
      'size-4 shrink-0 rounded-[4px] border shadow-xs transition-shadow',
      'outline-none focus-visible:ring-[3px] focus-visible:ring-ring/50',
      'disabled:cursor-not-allowed disabled:opacity-50',
      // 상태에 따른 스타일
      isChecked
        ? 'bg-primary text-primary-foreground border-primary'
        : '',
      isInvalid
        ? 'ring-destructive/20 border-destructive'
        : '',
      className,
    ]"
    role="checkbox"
    :aria-checked="isChecked"
    :aria-invalid="isInvalid"
    tabindex="0"
    @click="toggleCheck"
    @keydown.enter.prevent="toggleCheck"
    @keydown.space.prevent="toggleCheck"
  >
    <div
      v-if="isChecked"
      class="flex h-full w-full items-center justify-center text-current transition-none"
    >
      <CheckIcon class="w-3.5 h-3.5" />
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { CheckIcon } from "lucide-vue-next";

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },
  isInvalid: {
    type: Boolean,
    default: false,
  },
  className: {
    type: String,
    default: "",
  },
});

const emit = defineEmits(["update:modelValue"]);

// 부모 v-model과 항상 동기화 되도록 computed 사용
const isChecked = computed({
  get: () => props.modelValue,
  set: (val) => emit("update:modelValue", val),
});

const toggleCheck = () => {
  isChecked.value = !isChecked.value;
};
</script>

<style scoped>
/* Tailwind에서 size-4, size-3.5 같은 유틸을 안 쓰고 있다면
   여기서 직접 쓰고 싶은 고정값만 정의해도 됨.
   하지만 지금은 w-/h- 클래스를 사용했으므로 비워둬도 무방함. */
</style>
