<template>
  <section class="flex justify-end gap-4 mb-4 text-sm">
    <button
      type="button"
      :class="[
        'transition',
        sort === 'latest'
          ? 'font-semibold text-primary'
          : 'text-muted-foreground hover:text-foreground',
      ]"
      @click="emit('sort-change', 'latest')"
    >
      최신순
    </button>
    <button
      type="button"
      :class="[
        'transition',
        sort === 'comments'
          ? 'font-semibold text-primary'
          : 'text-muted-foreground hover:text-foreground',
      ]"
      @click="emit('sort-change', 'comments')"
    >
      인기순
    </button>
  </section>
</template>

<script setup>
defineProps({
  sort: {
    type: String,
    default: "latest",
  },
});

const emit = defineEmits(["sort-change"]);
</script>
