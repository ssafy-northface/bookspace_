<template>
  <div v-if="hasBook" style="transform-origin: center">
    <RouterLink :to="{ name: 'bookDetail', params: { isbn: book.isbn13 } }">
      <!-- 표지 이미지 -->
      <div class="w-full aspect-[2/3] overflow-hidden transform-gpu transition-transform duration-300 hover:scale-105">
        <img :src="book.cover" :alt="book.title" class="w-full h-full object-cover" />
      </div>

      <!-- 책 제목 -->
      <p class="mt-2 text-sm font-semibold line-clamp-2">{{ book.title }}</p>
      <!-- 저자 -->
      <p class="text-xs text-muted-foreground">{{ book.author }}</p>
    </RouterLink>
  </div>
</template>

<script setup>
import { RouterLink } from "vue-router";
import { computed } from "vue";

const book = computed(() => props.book);

const props = defineProps({
  book: {
    type: Object,
    default: () => ({}),
  },
});

const hasBook = computed(() => !!props.book?.isbn13);
</script>

<style scoped></style>
