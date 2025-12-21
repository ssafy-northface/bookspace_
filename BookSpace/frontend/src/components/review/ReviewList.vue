<template>
  <div class="space-y-4">
    <!-- 리뷰 없음 -->
    <div 
      v-if="!hasBookId || (!reviews?.length && !hasMyReview)" 
      class="rounded-xl border border-dashed border-border bg-card p-10 text-center text-sm text-muted-foreground"
    >
      첫 번째 리뷰를 작성해보세요!
    </div>

    <!-- 리뷰 목록 -->
    <ReviewCard v-else v-for="review in reviews" :key="review.reviewId ?? review.id" :review="review" />
  </div>
</template>

<script setup>
import ReviewCard from "./ReviewCard.vue";

defineProps({
  reviews: {
    type: Array,
    default: () => [],
  },
  hasBookId: {
    type: Boolean,
    default: false,
  },
  hasMyReview: {
    type: Boolean,
    default: false,
  },
});
</script>
