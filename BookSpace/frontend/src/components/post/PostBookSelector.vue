<!-- 
# PostBookSelector
 Commuityview에서 post 작성/수정 폼에서 책 검색(BookSearchInput) -> 결과 표시(BookSearchResults) -> 책 선택(composable/useInlineBookSearch) 
- BookSearchInput: 유저가 검색어 입력 -> emit("search", { query, type }) -> handleSearch
- BookSearchResults: useInlineBookSearch().search({query, type}) 결과 전달

[사용]
  <PostBookSelector
    v-model:isbn="isbn"
    :titleOnly="true"
    label="책 선택"
    placeholder="책 제목을 입력하세요"
    @select="onSelectBook"
  />
-->

<template>
  <div class="space-y-2">
    <div v-if="showLabel" class="flex items-center justify-between">
      <label class="text-sm font-semibold text-foreground">
        {{ label }}
      </label>
    </div>

    <!-- 책 검색 입력 인풋 -->
    <BookSearchInput
      :initial-query="initialQuery"
      :initial-type="titleOnly ? 'title' : initialType"
      :show-type-selector="!titleOnly"
      :placeholder="placeholder"
      @search="handleSearch"
    />

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
  titleOnly: {
    type: Boolean,
    default: false,
  },
  initialQuery: {
    type: String,
    default: "",
  },
  initialType: {
    type: String,
    default: "title",
  },
  label: {
    type: String,
    default: "책 선택",
  },
  showLabel: {
    type: Boolean,
    default: true,
  },
  placeholder: {
    type: String,
    default: "책 제목을 입력하세요",
  },
});

const emit = defineEmits(["update:isbn", "select", "clear"]);

const { results, loading, error, lastQuery, search } = useInlineBookSearch();
const showResults = ref(true);

const handleSearch = ({ query, type }) => {
  const trimmed = (query ?? "").trim();

  // 인풋이 비워지면 필터 해제
  if (!trimmed) {
    if (props.isbn) {
      emit("update:isbn", "");
      emit("Clear");
    }
    showResults.value = false;
    return;
  }
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
