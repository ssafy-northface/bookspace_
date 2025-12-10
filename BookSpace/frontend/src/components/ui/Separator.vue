<!-- 
# 구분선 컴포넌트 
가로구분선 <Separator />
세로구분선 <Separator isVertical/>
-->
<template>
  <div
    role="separator"
    :aria-orientation="isVertical ? 'vertical' : 'horizontal'"
    :class="[
      'bg-border shrink-0',
      isVertical ? 'mx-2 w-px h-full' : 'my-2 h-px w-full',
    ]"
  />
</template>

<script setup>
const props = defineProps({
  isVertical: {
    type: Boolean,
    default: false, // false면 horizontal
  },
});
</script>
