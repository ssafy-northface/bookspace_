<template>
  <textarea
    :class="classes"
    :value="modelValue"
    @input="emit('update:modelValue', $event.target.value)"
    v-bind="$attrs"
  />
</template>

<script setup>
import { computed } from "vue";

const props = defineProps({
  modelValue: {
    type: [String, Number],
    default: "",
  },
  class: {
    type: String,
    default: "",
  },
});

const emit = defineEmits(["update:modelValue"]);

const baseClasses =
  "w-full rounded-md border border-[color:var(--border)] bg-[color:var(--background)] " +
  "text-[color:var(--foreground)] px-3 py-2 text-base md:text-sm shadow-xs transition outline-none " +
  "placeholder:text-[color:var(--muted-foreground)] " +
  "focus-visible:ring-[3px] focus-visible:ring-[color:var(--ring)]/50 focus-visible:border-[color:var(--ring)] min-h-[220px] resize-none leading-relaxed";

const classes = computed(() =>
  [baseClasses, props.class].filter(Boolean).join(" ")
);
</script>
