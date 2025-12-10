<template>
  <div
    data-slot="scroll-area"
    class="relative h-full w-full overflow-auto rounded-md"
    :class="props.class"
  >
    <div
      data-slot="scroll-area-viewport"
      class="focus-visible:ring-ring/50 min-h-full min-w-full rounded-[inherit] outline-none focus-visible:outline-1 focus-visible:ring-[3px] transition-[color,box-shadow]"
    >
      <slot />
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  class: {
    type: String,
    default: "",
  },
});
</script>
