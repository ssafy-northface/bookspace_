<template>
  <div
    data-slot="aspect-ratio"
    :style="{
      position: 'relative',
      width: '100%',
      paddingBottom: paddingValue,
    }"
    class="overflow-hidden"
  >
    <div class="absolute inset-0">
      <slot />
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";

const props = defineProps({
  ratio: {
    type: [Number, String],
    default: 1, // 기본 1:1
  },
});

// ratio = width / height → padding-bottom = (height / width) * 100%
const paddingValue = computed(() => {
  const r = Number(props.ratio);
  if (!r || r <= 0) return "100%";
  return `${(1 / r) * 100}%`;
});
</script>
