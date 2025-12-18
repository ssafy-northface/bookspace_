<template>
  <!-- 도서목록 클릭하면 초기화되면서 메인 도서 목록으로 돌아오도록 설정 -->
  <RouterLink :to="{ path: '/books' }">
    <ViewHeader
      title="도서 목록"
      description="다양한 책들을 탐색해보세요"
      align="left"
      size="lg"
    />
  </RouterLink>

  <div>
    <!-- 검색창  -->
    <section class="mb-8">
      <BookSearchInput
        :initialQuery="query"
        :initialType="type"
        @search="search"
      ></BookSearchInput>
    </section>

    <!-- 정렬기준 선택 (검색모드일때만 보임)-->
    <section v-if="isSearchMode" class="mb-8">
      <BookSortBar :sort="sort" @sort-change="changeSort"></BookSortBar>
    </section>
    <!-- 로딩 -->
    <!-- <div v-if="loading" class="mt-6 text-sm text-muted-foreground">로딩중...</div> -->

    <!-- 에러 -->
    <!-- <div v-else-if="error" class="mt-6 text-sm text-red-500">
      {{ error }}
    </div> -->

    <!-- 도서 목록 -->
    <section>
      <!-- 베스트셀러 제목 (검색 아닐 때만) -->
      <h2 v-if="!isSearchMode" class="mb-8 text-2xl font-semibold">
        인기 도서
      </h2>
      <BookList
        v-if="!loading && !error && books.length"
        :books="books"
      ></BookList>

      <!-- 결과 없음 -->
      <div
        v-else
        class="flex min-h-[160px] flex-col items-center justify-center rounded-xl border border-dashed text-sm text-muted-foreground mt-6"
      >
        {{ isSearchMode ? "검색 결과가 없습니다." : "표시할 도서가 없습니다." }}
        <span class="mt-1"
          >검색어를 바꿔보거나 다른 조건으로 시도해보세요.</span
        >
      </div>
    </section>
  </div>
</template>

<script setup>
import ViewHeader from "../components/common/ViewHeader.vue";
import BookList from "../components/book/BookList.vue";
import BookSearchInput from "../components/book/BookSearchInput.vue";
import BookSortBar from "../components/book/BookSortBar.vue";

import { useBookSearch } from "@/composables/useBookSearch";
import { RouterLink } from "vue-router";

const {
  query,
  type,
  sort,
  isSearchMode,
  books,
  loading,
  error,
  search,
  changeSort,
} = useBookSearch();
</script>

<style scoped></style>
