<template>
  <div class="w-full p-5 border border-border rounded-xl bg-card shadow-sm">
    <!-- 상단: 닉네임, 작성일자, 평점 -->
    <div class="flex items-start justify-between gap-4">
      <div>
        <p class="font-semibold text-foreground">
          {{ review.nickname }}
        </p>
        <p class="text-sm text-muted-foreground">
          {{ formattedDate }}
        </p>
      </div>

      <div class="flex items-center gap-1">
        <span class="relative -top-[2px] text-yellow-400 leading-none">★</span>
        <span class="font-bold text-foreground">
          {{ review.reviewRating }}
        </span>
      </div>
    </div>

    <!-- 리뷰 내용-->
    <p class="mt-4 text-sm text-foreground whitespace-pre-line">
      {{ review.reviewContent }}
    </p>
  </div>
</template>

<script setup>
import { computed } from "vue";

const props = defineProps({
  review: {
    type: Object,
    required: true,
  },
});

const formattedDate = computed(() => {
  if (!props.review.reviewDate) return "";
  const date = new Date(props.review.reviewDate);
  return date.toLocaleDateString("ko-KR");
});
</script>
