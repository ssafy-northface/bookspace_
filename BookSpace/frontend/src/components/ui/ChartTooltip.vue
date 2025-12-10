<template>
  <div
    v-if="active && payload && payload.length"
    class="rounded-md border border-border bg-popover text-popover-foreground px-3 py-2 shadow-md text-xs"
  >
    <div
      v-if="!hideLabel"
      class="font-medium mb-1 text-foreground"
    >
      {{ formattedLabel }}
    </div>

    <div class="flex flex-col gap-1">
      <div
        v-for="(item, index) in payload"
        :key="index"
        class="flex items-center gap-2"
      >
        <span
          class="h-3 w-3 rounded-full"
          :style="{ backgroundColor: item.color }"
        ></span>
        <span class="text-xs text-muted-foreground">
          {{ item.name }}
        </span>
        <span class="ml-auto text-xs font-medium text-foreground">
          {{ item.value }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";

const props = defineProps({
  active: {
    type: Boolean,
    default: false,
  },
  payload: {
    type: Array,
    default: () => [],
  },
  label: {
    type: [String, Number],
    default: "",
  },
  hideLabel: {
    type: Boolean,
    default: false,
  },
  formatter: {
    type: Function,
    default: null,
  },
});

const formattedLabel = computed(() => {
  if (props.formatter) {
    return props.formatter(props.label);
  }
  return props.label;
});
</script>
