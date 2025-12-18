<template>
  <div class="space-y-4">
    <!-- 책표지 -->
    <div class="overflow-hidden border border-input bg-muted">
      <img :src="book?.cover || fallbackCover" :alt="book?.title || 'cover'" class="aspect-[2/3] w-full object-cover" loading="lazy" />
    </div>

    <!-- 찜 버튼 -->
     <!-- 로딩 중 버튼 비활성화 -->
    <div class="flex">
      <button
        type="button"
        class="flex-1 h-10 inline-flex items-center justify-center gap-2 rounded-md font-extrabold text-base transition-colors"
        :disabled="isWishLoading"
        :class="[
          isWished
            ? 'bg-gray-400 text-primary-foreground cursor-default'
            : 'bg-primary text-primary-foreground hover:bg-primary/90',
          isWishLoading ? 'opacity-60 cursor-not-allowed' : ''
        ]"
        @click.stop="!isWishLoading && $emit('toggle-wish')"
      >
        <span class="text-base leading-none">
          {{ isWished ? "♥" : "♡" }}
        </span>
        {{ isWishLoading ? "처리중..." : (isWished ? "찜완료" : "찜하기") }}
      </button>
    </div>

    <!-- 평점, 리뷰, 게시글 정보 -->
    <div class="bg-card rounded-xl border border-input p-4 shadow-sm">
      <div class="space-y-2 text-sm">
        <div class="flex items-center justify-between">
          <span>평점</span>
          <span class="flex items-center gap-1 font-semibold">
            <span class="relative -top-[2px] text-yellow-400 text-base leading-none">★</span>
            <span class="leading-none">{{ averageRatingText }}</span>
          </span>
        </div>

        <div class="flex items-center justify-between">
          <span>리뷰</span>
          <span class="font-semibold">{{ reviewCount }}개</span>
        </div>

        <div class="flex items-center justify-between">
          <span>게시글</span>
          <span class="font-semibold">{{ postCount }}개</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";

const props = defineProps({
  book: {
    type: Object,
    required: true, // BookSearchResponseDto 그대로
  },
  isWished: {
    type: Boolean,
    default: false,
  },
  isWishLoading: {
    type: Boolean,
    default: false,
  },
  averageRating: {
    type: Number,
    default: 0,
  },
  reviewCount: {
    type: Number,
    default: 0,
  },
  postCount: {
    type: Number,
    default: 0,
  },
});

defineEmits(["toggle-wish"]);

/** 표지 없을 때 fallback */
const fallbackCover =
  "data:image/svg+xml;charset=UTF-8," +
  encodeURIComponent(
    `<svg xmlns="http://www.w3.org/2000/svg" width="400" height="600">
      <rect width="100%" height="100%" fill="#eee"/>
      <text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle"
        fill="#999" font-size="20" font-family="Arial">No Cover</text>
    </svg>`
  );

/** 평점 표시용 텍스트 */
const averageRatingText = computed(() => {
  const r = Number(props.averageRating || 0);
  return `${r.toFixed(1)} (${props.reviewCount})`;
});
</script>
