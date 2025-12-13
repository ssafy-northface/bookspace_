<template>
  <input
    :type="type"
    :value="modelValue"
    :placeholder="placeholder"
    :class="computedClasses"
    @input="onInput"
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
  type: {
    type: String,
    default: "text",
  },
  placeholder: {
    type: String,
    default: "",
  },
  className: {
    type: String,
    default: "",
  },
});

const emit = defineEmits(["update:modelValue"]);

const baseClasses =
  "w-full rounded-md border border-[color:var(--border)] bg-[color:var(--background)] " +
  "text-[color:var(--foreground)] px-3 py-2 text-sm shadow-xs transition outline-none " +
  "placeholder:text-[color:var(--muted-foreground)] " +
  "focus-visible:ring-[3px] focus-visible:ring-[color:var(--ring)]/50 focus-visible:border-[color:var(--ring)]";

const computedClasses = computed(() =>
  [baseClasses, props.className].filter(Boolean).join(" ")
);

const onInput = (event) => {
  emit("update:modelValue", event.target.value);
};
</script>
