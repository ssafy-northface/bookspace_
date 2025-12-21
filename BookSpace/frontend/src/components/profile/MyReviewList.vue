<template>
  <div class="space-y-4">
    <div v-if="loading" class="py-8 text-center text-muted-foreground">
      내 리뷰를 불러오는 중입니다...
    </div>

    <!-- 작성한 리뷰가 없을 때 -->
    <div
      v-else-if="!reviews.length"
      class="flex flex-col items-center justify-center gap-3 px-6 py-10 text-center border border-dashed rounded-xl border-border bg-muted/30 text-muted-foreground"
    >
      <p class="text-base font-semibold text-foreground">
        작성한 리뷰가 없어요
      </p>
      <p class="text-sm">책을 읽고 느낀 점을 리뷰로 남겨보세요.</p>
    </div>

    <!-- 리뷰 목록이 있을 때 -->
    <div v-else class="grid grid-cols-1 gap-4">
      <button
        v-for="review in reviews"
        :key="review.reviewId"
        type="button"
        class="flex gap-4 p-4 text-left border shadow-sm rounded-xl border-border bg-card transition hover:-translate-y-[1px] hover:shadow-md"
        @click="emit('open-review', review)"
      >
        <!-- 왼쪽: 책 이미지 -->
        <div class="w-20 overflow-hidden rounded-md shrink-0 h-28 bg-muted">
          <img
            :src="review.bookImageUrl"
            :alt="review.bookTitle || '도서 이미지'"
            class="object-cover w-full h-full"
          />
        </div>

        <!-- 오른쪽: 정보 -->
        <div class="flex-1 space-y-3">
          <div class="flex items-start justify-between gap-3">
            <div class="space-y-1">
              <component
                :is="review.isbn ? RouterLink : 'p'"
                :to="
                  review.isbn
                    ? { name: 'bookDetail', params: { isbn: review.isbn } }
                    : undefined
                "
                class="font-semibold text-foreground hover:underline"
              >
                {{ review.bookTitle }}
              </component>
              <p class="text-sm text-muted-foreground">
                {{ review.bookAuthor }}
              </p>
              <p class="text-xs text-muted-foreground">
                {{ formatDate(review.reviewDate) }}
              </p>
            </div>
            <div
              class="flex items-center gap-1 px-3 py-1 rounded-full bg-amber-50 text-amber-600"
            >
              <span class="text-lg leading-none">★</span>
              <span class="text-lg font-semibold text-foreground">
                {{ review.rating ?? review.reviewRating ?? 0 }}
              </span>
            </div>
          </div>

          <p class="text-sm whitespace-pre-line text-foreground line-clamp-3">
            {{ review.reviewContent || "내용 없음" }}
          </p>
        </div>
      </button>
    </div>
  </div>
</template>

<script setup>
const emit = defineEmits(["open-review"]);

defineProps({
  reviews: {
    type: Array,
    default: () => [],
  },
  loading: {
    type: Boolean,
    default: false,
  },
});

const formatDate = (value) => {
  if (!value) return "날짜 정보 없음";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "날짜 정보 없음";
  return date.toLocaleDateString("ko-KR");
};
</script>
