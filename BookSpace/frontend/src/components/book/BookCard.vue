<template>
    <div
        style="transform-origin: center;"
    >
        <RouterLink 
            :to="{name: 'bookDetail', params: { bookId: book.bookId}}"
        >
            <!-- 표지 이미지 -->
            <div class="
                w-full aspect-[2/3] overflow-hidden
                transform-gpu transition-transform duration-300 hover:scale-105
            ">
                <img
                    :src="book.bookImageURL"
                    :alt="book.bookTitle"
                    class="w-full h-full object-cover"
                />
            </div>

            <!-- 책 제목 -->
            <p class="mt-2 text-sm font-semibold line-clamp-2">{{ book.bookTitle }}</p>
            <!-- 저자 -->
            <p class="text-xs text-muted-foreground">{{ book.bookAuthor }}</p>

        </RouterLink>
    </div>
</template>

<script setup>
import { RouterLink } from 'vue-router';

const props = defineProps({
  book: {
    type: Object,
    required: true,
  },
});

</script>

<style scoped>

</style>