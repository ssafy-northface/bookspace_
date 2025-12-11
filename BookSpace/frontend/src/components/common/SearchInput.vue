<!-- 
 TODO 쓰로틀링 or 디바운스 적용 고려
 TODO api 검색 결과 표시 컴포넌트와연동 고려
-->
<!-- 
 # SearchInput component 
 검색어가 바뀌면 자동 반영되, 엔터 키 눌렀을 때 검색 이벤트 발생 
 - 검색 로직은 사용하는 컴포넌트에서 onSearch 이벤트 핸들러로 구현
 
 [사용]
<SearchInput 
  v-model="searchQuery" 
  placeholder="검색어를 입력하세요..." 
  @search="onSearch"
  /> 
-->

<template>
  <div class="relative w-full">
    <!-- 검색 아이콘 -->
    <svg
      class="absolute w-4 h-4 -translate-y-1/2 left-3 top-1/2 text-muted-foreground"
      fill="none"
      stroke="currentColor"
      stroke-width="2"
      viewBox="0 0 24 24"
    >
      <circle cx="11" cy="11" r="8" />
      <path d="M21 21l-4.3-4.3" />
    </svg>

    <!-- 검색 입력 필드 -->
    <input
      :value="modelValue"
      @input="onInput"
      @keyup.enter="$emit('search', modelValue)"
      type="text"
      :placeholder="placeholder"
      class="w-full rounded-md border border-input bg-background pl-9 pr-3 py-2 text-sm shadow-xs outline-none transition focus-visible:ring-[3px] focus-visible:ring-ring/50 focus-visible:border-ring disabled:opacity-50 disabled:cursor-not-allowed"
    />
  </div>
</template>

<script setup>
const props = defineProps({
  modelValue: {
    type: String,
    default: "",
  },
  placeholder: {
    type: String,
    default: "검색어를 입력하세요...",
  },
});

const emit = defineEmits(["update:modelValue", "search"]);

const onInput = (event) => {
  emit("update:modelValue", event.target.value);
};
</script>
