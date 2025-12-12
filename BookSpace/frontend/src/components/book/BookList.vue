<template>
    <section class="mt-6">
        <!-- 도서 카드 그리드 -->
        <div
        class="grid gap-x-10 gap-y-10 grid-cols-[repeat(auto-fill,minmax(150px,1fr))]"
        >
        <BookCard
            v-for="book in books"
            :key="book.bookId || book.id"
            :book="book"
        />
        </div>
  </section>
</template>

<script setup>
import BookCard from './BookCard.vue';

const props = defineProps({
  books: {
    type: Array,
    default: () => [],
  },
});
</script>

<style scoped>

</style>