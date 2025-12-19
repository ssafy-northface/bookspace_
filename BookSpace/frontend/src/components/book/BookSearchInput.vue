<!--
# SearchBar (공통 컴포넌트)
- CommunityView, BooksView 검색 UI
- Select(검색 기준) + Input (검색 입력) + Button
- 입력값/타입 변경 & 엔터/버튼 클릭시 300ms emit("search") 발생
- api 호출, 검색 결과 렌더링은 부모 컴포넌트에서 수행

[props]
- initialQuery: 검색 인풋 초기값
- initialType: 셀러터 초기값
- showTypeSelector: 검색 기준 노출 여부 (post검색에서는 검색 기준 x -> 책 제목만임)
- placholder: input placeholder

[사용]
<BookSearchInput
  :initialQuery="route.query.q ?? ''"
  :initialType="route.query.type ?? 'title'"
  :showTypeSelector="true"
  placeholder="책 제목/저자/출판사로 검색"
  @search="handleSearch"
/>

-->
<template>
  <!-- 검색기준 + 검색창  -->
  <section class="flex items-center gap-2 mb-4">
    <!-- 책제목, 저자, 출판사 선택 -->
    <select
      v-if="showTypeSelector"
      v-model="type"
      class="h-9 rounded-md border border-input bg-background px-2 text-xs md:text-sm focus-visible:ring-[3px] focus-visible:ring-ring/50 focus-visible:border-ring"
    >
      <option value="title">제목</option>
      <option value="author">저자</option>
      <option value="publisher">출판사</option>
    </select>

    <!-- 검색창 -->
    <SearchInput
      v-model="searchQuery"
      :placeholder="placeholder"
      @search="onSearch"
    />

    <Button class="w-16 h-5 font-extrabold text-white" @click="onSearch"
      >검색</Button
    >
  </section>
</template>

<script setup>
import { ref, watch } from "vue";
import SearchInput from "../common/SearchInput.vue";
import Button from "../ui/Button.vue";

// 초기값
const props = defineProps({
  initialQuery: { type: String, default: "" },
  initialType: { type: String, default: "title" },
  showTypeSelector: { type: Boolean, default: true },
  placeholder: { type: String, default: "검색어를 입력하세요" },
});

// 부모 BooksView에게 전달할 이벤트들
const emit = defineEmits(["search"]);

// UI 상태 (임시 입력값)
const searchQuery = ref(props.initialQuery);
const type = ref(props.initialType);

// 디바운스 타이머
let debounceTimer = null;
const DEBOUNCE_DELAY = 300;

// 입력값이 비워있는지 체크
const wasNonEmpty = ref(searchQuery.value.trim().length > 0);

// 디바운스
watch([searchQuery, type], ([q, t]) => {
  const trimmed = q.trim();

  // 이전 디바운스 취소
  if (debounceTimer) {
    clearTimeout(debounceTimer);
    debounceTimer = null;
  }

  // 입력이 비어있으면 자동 검색 x
  if (!trimmed) {
    if (wasNonEmpty.value) {
      emit("search", {
        query: "",
        type: t,
      });
    }
    wasNonEmpty.value = false;
    return;
  }

  // 검색어가 있을 경우
  wasNonEmpty.value = true;
  //  debounce
  debounceTimer = setTimeout(() => {
    emit("search", {
      query: trimmed,
      type: type.value,
    });
  }, DEBOUNCE_DELAY);
});

// 검색 확정 : 엔터/버튼 시 부모로 전달
const onSearch = () => {
  if (debounceTimer) {
    clearTimeout(debounceTimer);
    debounceTimer = null;
  }

  emit("search", {
    query: searchQuery.value.trim(), // 빈 값 허용
    type: type.value,
  });
};

// 뒤로가기/새로고침 등으로 URL이 바뀌면 input도 동기화
watch(
  () => props.initialQuery,
  (v) => {
    searchQuery.value = v ?? "";
    wasNonEmpty.value = searchQuery.value.trim().length > 0;
  }
);
watch(
  () => props.initialType,
  (v) => (type.value = v ?? "title")
);
</script>

<style scoped></style>
