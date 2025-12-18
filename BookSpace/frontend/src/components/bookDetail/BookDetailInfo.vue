<template>
  <section class="space-y-6">
    <!-- Meta header -->
    <div class="space-y-2">
      <!-- 제목 -->
      <h1 class="text-3xl font-bold leading-tight">
        {{ book?.title || "제목 없음" }}
      </h1>

      <!-- 저자 · 출판사 -->
      <p class="text-base text-muted-foreground">
        <span>{{ book?.author || "저자 정보 없음" }}</span>
        <span v-if="book?.publisher" class="ml-2">· {{ book.publisher }}</span>
      </p>

      <!-- 출간일 -->
      <p v-if="book?.pubDate" class="text-sm text-muted-foreground">출간일: {{ book.pubDate }}</p>
    </div>

    <!-- 책 소개 -->
    <div v-if="book?.description" class="space-y-2">
      <h2 class="text-xl font-semibold">책 소개</h2>
      <p class="whitespace-pre-line text-sm leading-relaxed text-muted-foreground">
        {{ decodedDescription }}
      </p>
    </div>

    <!-- 소개 없을 때 -->
    <div v-else class="rounded-xl border border-input bg-card p-4 text-sm text-muted-foreground">책 소개 정보가 없습니다.</div>
  </section>
</template>

<script setup>
import { computed } from "vue";

const props = defineProps({
  book: {
    type: Object,
    required: true,
  },
});


const decodeHtml = (str = "") => {
  const el = document.createElement("textarea");
  el.innerHTML = str;
  return el.value;
}

// html entity 디코딩 처리
const decodedDescription = computed(()=>
  decodeHtml(props.book?.description ?? "")
);
</script>
