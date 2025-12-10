<template>
  <div data-slot="spinner" class="inline-flex items-center justify-center">
    <FwbSpinner :size="size" :color="color" :class="class" />
  </div>
</template>

<script setup>
import { FwbSpinner } from "flowbite-vue";

const props = defineProps({
  size: {
    type: String,
    default: "md",
  },
  color: {
    type: String,
    default: "primary",
  },
  class: {
    type: String,
    default: "",
  },
});
</script>
