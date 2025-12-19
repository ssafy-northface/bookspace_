<template>
  <!-- 검색기준 + 검색창  -->
  <section class="flex items-center gap-2 mb-4">
    <!-- 책제목, 저자, 출판사 선택 -->
    <select
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
      placeholder="검색어를 입력하세요"
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
