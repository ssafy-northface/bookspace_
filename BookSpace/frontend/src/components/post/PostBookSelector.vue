<template>
  <div class="space-y-2">
    <div class="flex items-center justify-between">
      <label class="text-sm font-semibold text-foreground">책 선택</label>
    </div>

    <!-- 책 검색 입력 인풋 -->
    <BookSearchInput @search="handleSearch" />

    <!-- 검색 결과  -->
    <BookSearchResults
      v-if="showResults"
      :query="lastQuery"
      :results="results"
      :loading="loading"
      :error="error"
      :selected-isbn="isbn"
      :max-results="0"
      max-height="16rem"
      @select="handleSelect"
    />
  </div>
</template>

<script setup>
import { ref } from "vue";
import BookSearchInput from "@/components/book/BookSearchInput.vue";
import BookSearchResults from "@/components/book/BookSearchResults.vue";
import { useInlineBookSearch } from "@/composables/useInlineBookSearch";

const props = defineProps({
  isbn: {
    type: String,
    default: "",
  },
});

const emit = defineEmits(["update:isbn", "select"]);

const { results, loading, error, lastQuery, search } = useInlineBookSearch();
const showResults = ref(true);

const handleSearch = ({ query, type }) => {
  showResults.value = true;
  search({ query, type });
};

const handleSelect = (book) => {
  const isbn13 = book?.isbn13 ?? "";
  emit("update:isbn", isbn13);
  emit("select", book);
  showResults.value = false;
};
</script>
