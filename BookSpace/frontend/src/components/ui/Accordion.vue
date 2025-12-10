<template>
  <div class="accordion" data-slot="accordion">
    <slot />
  </div>
</template>

<script setup>
const props = defineProps({
  multiple: {
    type: Boolean,
    default: false, // 여러 아이템을 동시에 열 수 있는지 여부
  },
});
</script>

<style scoped>
.accordion {
  border: 1px solid var(--border);
  border-radius: 8px;
  overflow: hidden;
  background-color: var(--background);
}
</style>