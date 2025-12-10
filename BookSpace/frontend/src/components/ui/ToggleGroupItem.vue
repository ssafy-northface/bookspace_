<template>
  <Toggle
    :model-value="isPressed"
    :variant="effectiveVariant"
    :size="effectiveSize"
    :disabled="isDisabled"
    :class="extraClasses"
    data-slot="toggle-group-item"
    :data-variant="effectiveVariant"
    :data-size="effectiveSize"
    @update:modelValue="handleToggle"
  >
    <slot />
  </Toggle>
</template>

<script setup>
import { inject, computed } from "vue";
import Toggle from "@/components/ui/Toggle.vue";

const props = defineProps({
  value: {
    type: String,
    required: true,
  },
  disabled: {
    type: Boolean,
    default: false,
  },
  variant: {
    type: String,
    default: undefined, // 그룹 기본값 override 하고 싶을 때만 사용
  },
  size: {
    type: String,
    default: undefined,
  },
  class: {
    type: String,
    default: "",
  },
});

const group = inject("toggleGroup", null);

const effectiveVariant = computed(() => {
  if (group && group.variant && group.variant.value) return group.variant.value;
  if (props.variant) return props.variant;
  return "default";
});

const effectiveSize = computed(() => {
  if (group && group.size && group.size.value) return group.size.value;
  if (props.size) return props.size;
  return "default";
});

const isDisabled = computed(() => {
  if (group && group.disabled && group.disabled.value) return true;
  return props.disabled;
});

const isPressed = computed(() => {
  if (!group) return false;

  const current = group.currentValue.value;

  if (group.type.value === "single") {
    return current === props.value;
  }

  return Array.isArray(current) && current.includes(props.value);
});

function handleToggle() {
  if (!group) return;
  group.toggleItem(props.value);
}

const extraClasses = computed(() =>
  [
    "min-w-0 flex-1 shrink-0 rounded-none shadow-none",
    "first:rounded-l-md last:rounded-r-md",
    "focus:z-10 focus-visible:z-10",
    "data-[variant=outline]:border-l-0 data-[variant=outline]:first:border-l",
    props.class,
  ].join(" ")
);
</script>
