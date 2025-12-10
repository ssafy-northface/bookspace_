<template>
  <button
    type="button"
    :aria-pressed="isPressed"
    :data-state="isPressed ? 'on' : 'off'"
    :disabled="disabled"
    :class="buttonClasses"
    @click="onToggle"
  >
    <slot />
  </button>
</template>

<script setup>
import { computed, ref, watch } from "vue";

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: undefined,
  },
  defaultPressed: {
    type: Boolean,
    default: false,
  },
  disabled: {
    type: Boolean,
    default: false,
  },
  variant: {
    type: String,
    default: "default",
    validator: (value) => ["default", "outline"].includes(value),
  },
  size: {
    type: String,
    default: "default",
    validator: (value) => ["default", "sm", "lg"].includes(value),
  },
});

const emit = defineEmits(["update:modelValue", "change"]);

const internalPressed = ref(props.modelValue ?? props.defaultPressed);

watch(
  () => props.modelValue,
  (value) => {
    if (value !== undefined) internalPressed.value = value;
  }
);

const isControlled = computed(() => props.modelValue !== undefined);
const isPressed = computed(() =>
  isControlled.value ? Boolean(props.modelValue) : internalPressed.value
);

const baseClasses =
  "inline-flex items-center justify-center gap-2 rounded-md text-sm font-medium hover:bg-muted hover:text-muted-foreground disabled:pointer-events-none disabled:opacity-50 focus-visible:outline-ring/50 focus-visible:ring-ring/50 focus-visible:ring-[3px] outline-none transition-[color,box-shadow] whitespace-nowrap";

const variantClasses = {
  default: "bg-transparent",
  outline:
    "border border-input bg-transparent hover:bg-accent hover:text-accent-foreground",
};

const sizeClasses = {
  default: "h-9 px-2 min-w-[2.25rem]",
  sm: "h-8 px-1.5 min-w-[2rem]",
  lg: "h-10 px-2.5 min-w-[2.5rem]",
};

const buttonClasses = computed(() => {
  const pressedClasses = isPressed.value
    ? "bg-accent text-accent-foreground"
    : "text-foreground";

  return [
    baseClasses,
    variantClasses[props.variant] ?? variantClasses.default,
    sizeClasses[props.size] ?? sizeClasses.default,
    pressedClasses,
  ].join(" ");
});

function onToggle() {
  if (props.disabled) return;

  const next = !isPressed.value;
  if (!isControlled.value) {
    internalPressed.value = next;
  }

  emit("update:modelValue", next);
  emit("change", next);
}
</script>
