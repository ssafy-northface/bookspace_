<template>
  <InputText
    v-model="model"
    :type="type"
    :placeholder="placeholder"
    :class="computedClasses"
    v-bind="attrs"
  />
</template>

<script setup>
import { computed, useAttrs } from "vue";
import InputText from "primevue/inputtext";

const props = defineProps({
  modelValue: {
    type: [String, Number],
    default: "",
  },
  type: {
    type: String,
    default: "text",
  },
  placeholder: {
    type: String,
    default: "",
  },
  class: {
    type: String,
    default: "",
  },
});

const emit = defineEmits(["update:modelValue"]);

// v-model 연결
const model = computed({
  get: () => props.modelValue,
  set: (val) => emit("update:modelValue", val),
});

// Tailwind + PrimeVue 조합 스타일
const computedClasses = computed(() => {
  return [
    "w-full rounded-md px-3 py-2 text-sm",
    "border border-[color:var(--border)] bg-[var(--background)] text-[var(--foreground)]",
    "placeholder:text-[color:var(--muted-foreground)]",
    "focus-visible:ring-[3px] focus-visible:ring-[color:var(--ring)]/50 focus-visible:border-[color:var(--ring)]",
    "disabled:opacity-50 disabled:cursor-not-allowed",
    props.class,
  ];
});

// 나머지 속성 모두 전달
const attrs = useAttrs();
</script>
