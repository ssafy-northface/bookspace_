<template>
  <div class="space-y-4">
    <!-- 책표지 -->
    <div class="overflow-hidden border border-input bg-muted relative aspect-[2/3]">
      <img 
        v-if="imageLoaded"
        :key="book?.isbn || book?.isbn13"
        :src="displayImageSrc" 
        :alt="book?.title || 'cover'" 
        class="w-full h-full object-cover"
      />
      <!-- 로딩 중 플레이스홀더 -->
      <div 
        v-if="!imageLoaded"
        class="absolute inset-0 bg-muted flex items-center justify-center"
      >
        <div class="text-muted-foreground text-sm">로딩 중...</div>
      </div>
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
import { computed, ref, watch } from "vue";

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
  return `${r.toFixed(1)}`;
});

// 이미지 로드 상태 관리
const imageLoaded = ref(false);
const displayImageSrc = ref('');

// 이미지 미리 로드 함수
const preloadImage = (imageSrc) => {
  imageLoaded.value = false;
  const img = new Image();
  img.onload = () => {
    // 이미지 로드 완료 후에만 src 변경
    displayImageSrc.value = imageSrc;
    imageLoaded.value = true;
  };
  img.onerror = () => {
    // 에러가 나도 fallback 이미지로 표시
    displayImageSrc.value = fallbackCover;
    imageLoaded.value = true;
  };
  img.src = imageSrc;
};

// book이 변경될 때 이미지를 미리 로드하고, 로드 완료 후에만 표시
watch(() => props.book?.isbn || props.book?.isbn13, (newIsbn) => {
  if (newIsbn) {
    const newImageSrc = props.book?.cover || fallbackCover;
    preloadImage(newImageSrc);
  }
}, { immediate: true });
</script>
