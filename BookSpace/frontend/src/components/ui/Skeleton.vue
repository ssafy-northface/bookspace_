<template>
  <div
    data-slot="skeleton"
    :class="['bg-muted animate-pulse rounded-md', props.class || 'h-4 w-full']"
  />
</template>

<script setup>
const props = defineProps({
  class: {
    type: String,
    default: "",
  },
});
</script>
