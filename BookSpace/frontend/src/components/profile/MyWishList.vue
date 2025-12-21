<!-- 
# 찜한 도서 목록을 보여주는 컴포넌트 
-->
<template>
  <div class="space-y-4">
    <!-- 로딩 상태 -->
    <div v-if="loading" class="py-8 text-center text-muted-foreground">
      찜한 도서를 불러오는 중입니다...
    </div>

    <!-- 빈 상태 -->
    <div
      v-else-if="!items.length"
      class="flex flex-col items-center justify-center gap-3 px-6 py-10 text-center border border-dashed rounded-xl border-border bg-muted/30 text-muted-foreground"
    >
      <p class="text-base font-semibold text-foreground">
        아직 찜한 도서가 없어요
      </p>
      <p class="text-sm">마음에 드는 책을 발견하면 찜으로 담아보세요.</p>
    </div>

    <!-- 찜 목록 -->
    <div
      v-else
      class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4"
    >
      <!-- 넷플릭스 포스터 느낌의 카드 -->
      <button
        v-for="item in items"
        :key="item.wishId"
        type="button"
        @click="emit('open-book', item)"
        class="poster-card group relative aspect-[2/3] overflow-hidden rounded-lg border border-border/40 bg-neutral-950 shadow-lg"
      >
        <img
          :src="item.bookImageUrl"
          :alt="item.bookTitle"
          class="absolute inset-0 h-full w-full object-cover transition duration-300 group-hover:scale-105 group-hover:brightness-80"
        />
        <div
          class="absolute inset-0 bg-gradient-to-t from-black/85 via-black/55 to-transparent"
        ></div>
        <div
          class="absolute bottom-0 left-0 right-0 p-4 space-y-1.5 backdrop-blur-[2px] bg-gradient-to-t from-black/70 via-black/30 to-transparent"
        >
          <p
            class="text-base font-semibold leading-snug text-white line-clamp-2 drop-shadow-sm"
          >
            {{ item.bookTitle }}
          </p>
          <p class="text-sm text-gray-200 line-clamp-1">
            {{ item.bookAuthor || "작가 미상" }}
          </p>
        </div>
      </button>
    </div>
  </div>
</template>

<script setup>
const emit = defineEmits(["open-book"]);

defineProps({
  items: {
    type: Array,
    default: () => [],
  },
  loading: {
    type: Boolean,
    default: false,
  },
});
</script>

<style scoped>
.poster-card {
  transform: scale(0.97);
  transition: transform 240ms ease, box-shadow 240ms ease;
}
.poster-card:hover {
  transform: translateY(-6px) scale(1.02);
  box-shadow: 0 16px 26px -12px rgba(0, 0, 0, 0.45);
}
</style>
