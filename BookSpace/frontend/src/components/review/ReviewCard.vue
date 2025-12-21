<template>
  <div class="w-full p-5 border shadow-sm border-border rounded-xl bg-card">
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
    <p class="mt-4 text-sm whitespace-pre-line text-foreground">
      {{ review.reviewContent }}
    </p>
  </div>
</template>

<script setup>
import { useFormattedDate } from "@/composables/useFormattedDate";

const props = defineProps({
  review: {
    type: Object,
    required: true,
  },
});

const formattedDate = useFormattedDate(props.review.reviewDate, {
  dateStyle: "medium",
});
</script>
