<template>
  <!-- 검색어 있으면 결과 영역 -->
  <div v-if="hasQuery" class="p-3 mt-2 border rounded-lg border-border bg-card">
    <div v-if="loading" class="p-3 text-xs text-muted-foreground">
      검색 중...
    </div>
    <div v-else-if="error" class="text-xs text-destructive">
      {{ error }}
    </div>
    <!-- 검색 결과 목록 -->
    <div
      v-else-if="limitedResults.length"
      class="divide-y divide-border"
      :class="containerClass"
      :style="containerStyle"
    >
      <!-- 각 책을 버튼으로 렌더링 -> 클릭 시 부모로 select 이벤트 전달 -->
      <button
        v-for="book in limitedResults"
        :key="book.bookId || book.isbn13"
        type="button"
        class="flex w-full gap-3 p-4 text-left transition rounded-md hover:bg-muted/60 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
        :class="{
          'bg-muted/60 ring-1 ring-ring': book.isbn13 === selectedIsbn,
        }"
        @click="emit('select', book)"
      >
        <div class="w-10 overflow-hidden rounded h-14 shrink-0 bg-muted">
          <img
            :src="book.cover"
            :alt="book.title"
            class="object-cover w-full h-full"
          />
        </div>
        <!-- 책 정보 -->
        <div class="min-w-0 space-y-1">
          <p class="text-sm font-semibold text-foreground line-clamp-1">
            {{ book.title }}
          </p>
          <p class="text-xs text-muted-foreground line-clamp-1">
            {{ book.author || "저자 정보 없음" }}
            <span v-if="book.publisher">· {{ book.publisher }}</span>
          </p>
        </div>
      </button>
    </div>
    <!-- 검색 결과가 없을 경우 -->
    <div v-else class="text-xs text-muted-foreground">
      검색 결과가 없습니다.
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";

const emit = defineEmits(["select"]);

// 현재 검색어 (공백이면 결과ui 숨기기)
const props = defineProps({
  // 검색어
  query: {
    type: String,
    default: "",
  },
  // 서버애서 받아온 검색 결과
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
  maxResults: {
    type: Number,
    default: 0,
  },
  selectedIsbn: {
    type: String,
    default: "",
  },
  maxHeight: {
    type: String,
    default: "",
  },
});

// 검색어가 존재하는지
const hasQuery = computed(() => props.query.trim().length > 0);

// 최대 개수 제한 있으면 slice
const limitedResults = computed(() =>
  props.maxResults > 0
    ? props.results.slice(0, props.maxResults)
    : props.results
);

// maxHeight -> 스크롤 허용
const containerClass = computed(() =>
  props.maxHeight ? "overflow-y-auto pr-1" : ""
);

// maxHeight (inline style)
const containerStyle = computed(() =>
  props.maxHeight ? { maxHeight: props.maxHeight } : undefined
);
</script>
