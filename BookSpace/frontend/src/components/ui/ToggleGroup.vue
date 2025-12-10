<template>
  <div
    data-slot="toggle-group"
    :data-variant="variant"
    :data-size="size"
    class="group/toggle-group flex w-fit items-center rounded-md"
    :class="variant === 'outline' ? 'shadow-xs' : ''"
  >
    <slot />
  </div>
</template>

<script setup>
import { computed, ref, watch, provide } from "vue";

const props = defineProps({
  modelValue: {
    // single: string | null, multiple: string[]
    type: [String, Array],
    default: undefined,
  },
  defaultValue: {
    type: [String, Array],
    default: undefined,
  },
  type: {
    type: String,
    default: "single", // "single" | "multiple"
  },
  disabled: {
    type: Boolean,
    default: false,
  },
  variant: {
    type: String,
    default: "default", // "default" | "outline"
  },
  size: {
    type: String,
    default: "default", // "default" | "sm" | "lg"
  },
});

const emit = defineEmits(["update:modelValue", "change"]);

const isControlled = computed(() => props.modelValue !== undefined);

const internalValue = ref(
  props.modelValue !== undefined
    ? props.modelValue
    : props.defaultValue !== undefined
    ? props.defaultValue
    : props.type === "single"
    ? null
    : []
);

watch(
  () => props.modelValue,
  (value) => {
    if (value !== undefined) {
      internalValue.value = value;
    }
  }
);

const currentValue = computed(() =>
  isControlled.value ? props.modelValue : internalValue.value
);

function toggleItem(value) {
  if (props.disabled) return;

  let next;

  if (props.type === "single") {
    next = currentValue.value === value ? null : value;
  } else {
    const arr = Array.isArray(currentValue.value)
      ? [...currentValue.value]
      : [];
    const idx = arr.indexOf(value);
    if (idx >= 0) {
      arr.splice(idx, 1);
    } else {
      arr.push(value);
    }
    next = arr;
  }

  if (!isControlled.value) {
    internalValue.value = next;
  }

  emit("update:modelValue", next);
  emit("change", next);
}

provide("toggleGroup", {
  type: computed(() => props.type),
  disabled: computed(() => props.disabled),
  variant: computed(() => props.variant),
  size: computed(() => props.size),
  currentValue,
  toggleItem,
});
</script>
