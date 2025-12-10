<template>
  <FwbToggle
    :model-value="modelValue"
    :label="label"
    :disabled="disabled"
    @update:model-value="onChange"
    class="relative flex items-center gap-2"
  >
    <!-- Flowbite의 커스텀 API 중 slot 사용: switch / label slot -->
    <template #switch="{ checked }">
      <div
        class="relative inline-flex shrink-0 h-[1.15rem] w-8 rounded-full border border-transparent shadow-xs transition-all"
        :class="[
          checked ? 'bg-primary' : 'bg-input dark:bg-input/80',
          focusClass,
          trackClass,
        ]"
      >
        <span
          class="absolute top-0.5 left-0.5 size-4 rounded-full transition-transform bg-background dark:bg-foreground pointer-events-none"
          :class="[
            checked ? 'translate-x-[calc(100%-2px)]' : 'translate-x-0',
            thumbClass,
          ]"
        />
      </div>
    </template>

    <template #label>
      <span class="text-foreground">{{ label }}</span>
    </template>
  </FwbToggle>
</template>

<script setup>
import { FwbToggle } from "flowbite-vue";

const props = defineProps({
  modelValue: Boolean,
  label: String,
  disabled: Boolean,

  trackClass: { type: String, default: "" },
  thumbClass: { type: String, default: "" },
  focusClass: { type: String, default: "" },
  class: { type: String, default: "" },
});

const emit = defineEmits(["update:modelValue"]);
const onChange = (value) => emit("update:modelValue", value);
</script>
