<template>
  <div
    data-slot="slider"
    class="relative flex w-full touch-none select-none items-center gap-3"
    :class="props.class"
  >
    <fwb-range
      v-model="valueProxy"
      :min="min"
      :max="max"
      :steps="step"
      :disabled="disabled"
      :label="label"
      class="w-full"
    />
    <span v-if="label === ''" class="text-sm text-muted-foreground">
      {{ valueProxy }}
    </span>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { FwbRange } from "flowbite-vue";

const props = defineProps({
  modelValue: {
    type: Number,
    default: undefined,
  },
  defaultValue: {
    type: Number,
    default: 0,
  },
  min: {
    type: Number,
    default: 0,
  },
  max: {
    type: Number,
    default: 100,
  },
  step: {
    type: Number,
    default: 1,
  },
  label: {
    type: String,
    default: "",
  },
  disabled: {
    type: Boolean,
    default: false,
  },
  class: {
    type: String,
    default: "",
  },
});

const emit = defineEmits(["update:modelValue", "change"]);

const valueProxy = computed({
  get() {
    return props.modelValue ?? props.defaultValue;
  },
  set(val) {
    emit("update:modelValue", Number(val));
    emit("change", Number(val));
  },
});
</script>
