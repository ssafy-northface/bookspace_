<template>
  <section class="mt-6">
    <!-- 도서 카드 그리드 -->
    <div class="grid gap-x-12 gap-y-12 grid-cols-[repeat(auto-fill,minmax(130px,1fr))]">
      <BookCard v-for="book in props.books || []" :key="book.bookId || book.isbn13" :book="book" />
    </div>
  </section>
</template>

<script setup>
import BookCard from "./BookCard.vue";

const props = defineProps({
  books: {
    type: Array,
    default: () => [],
  },
});
</script>

<style scoped></style>
