<template>
  <!-- 도서 정렬기준 선택 영역 -->
  <section class="mt-6 mb-6 flex justify-end gap-3">
    <span @click="emit('sort-change', 'latest')" :class="['cursor-pointer text-sm', sort === 'latest' ? 'font-extrabold text-primary' : 'font-normal']">출간일순</span>

    <span @click="emit('sort-change', 'accuracy')" :class="['cursor-pointer text-sm', sort === 'accuracy' ? 'font-extrabold text-primary' : 'font-normal']">정확도순</span>

    <span @click="emit('sort-change', 'popular')" :class="['cursor-pointer text-sm', sort === 'popular' ? 'font-extrabold text-primary' : 'font-normal']">인기순</span>
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

<style scoped></style>
