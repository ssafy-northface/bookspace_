<template>
  <div class="space-y-4">
    <div class="flex gap-4">
      <div class="w-24 h-32 overflow-hidden rounded-md bg-muted shrink-0">
        <img
          :src="review.bookImageUrl"
          :alt="review.bookTitle || '도서 이미지'"
          class="object-cover w-full h-full"
        />
      </div>
      <div class="flex-1 space-y-1">
        <p class="text-sm text-muted-foreground">내 리뷰</p>
        <h2 class="text-lg font-semibold text-foreground">
          {{ review.bookTitle || "제목 없음" }}
        </h2>
        <p class="text-sm text-muted-foreground">
          {{ review.bookAuthor || "작가 미상" }}
        </p>
        <p class="text-xs text-muted-foreground">
          {{ formattedDate }}
        </p>
        <div class="inline-flex items-center gap-2 rounded-full bg-amber-50 px-3 py-1 text-amber-700">
          <span class="text-base leading-none">★</span>
          <span class="text-base font-semibold">
            {{ review.rating ?? review.reviewRating ?? 0 }}
          </span>
        </div>
      </div>
    </div>

    <div class="rounded-xl border border-border bg-card p-4 text-sm whitespace-pre-line text-foreground min-h-[120px]">
      {{ review.reviewContent || "내용 없음" }}
    </div>
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
