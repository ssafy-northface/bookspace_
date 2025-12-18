<template>
  <div v-if="hasQuery" class="p-3 mt-2 border rounded-lg border-border bg-card">
    <div v-if="loading" class="text-xs text-muted-foreground">검색 중...</div>
    <div v-else-if="error" class="text-xs text-destructive">
      {{ error }}
    </div>
    <div v-else-if="results.length" class="divide-y divide-border">
      <RouterLink
        v-for="book in results"
        :key="book.bookId || book.isbn13"
        :to="{ name: 'bookDetail', params: { isbn: book.isbn13 } }"
        class="flex gap-3 py-2"
      >
        <div class="w-10 overflow-hidden rounded h-14 shrink-0 bg-muted">
          <img
            :src="book.cover"
            :alt="book.title"
            class="object-cover w-full h-full"
          />
        </div>
        <div class="min-w-0 space-y-1">
          <p class="text-sm font-semibold text-foreground line-clamp-1">
            {{ book.title }}
          </p>
          <p class="text-xs text-muted-foreground line-clamp-1">
            {{ book.author || "저자 정보 없음" }}
            <span v-if="book.publisher">· {{ book.publisher }}</span>
          </p>
        </div>
      </RouterLink>
    </div>
    <div v-else class="text-xs text-muted-foreground">
      검색 결과가 없습니다.
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { RouterLink } from "vue-router";

const props = defineProps({
  query: {
    type: String,
    default: "",
  },
  results: {
    type: Array,
    default: () => [],
  },
  loading: {
    type: Boolean,
    default: false,
  },
  error: {
    type: String,
    default: "",
  },
});

const hasQuery = computed(() => props.query.trim().length > 0);
</script>
