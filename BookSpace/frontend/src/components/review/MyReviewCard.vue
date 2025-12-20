<!-- 로그인 유저가 작성한 리뷰 카드 (수정, 삭제 버튼이 포함되어 있음) -->
<template>
  <div class="flex flex-col w-full p-5 border border-border rounded-xl bg-card shadow-sm">
    <!-- 상단: 닉네임 / 날짜 / 평점 -->
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

    <!-- 리뷰 내용 -->
    <p class="mt-4 text-sm text-foreground whitespace-pre-line">
      {{ review.reviewContent }}
    </p>

    <!-- 수정/삭제 버튼 -->
    <div class="mt-4 flex justify-end gap-2">
        <button
            type="button"
            class="h-9 rounded-md px-4 text-sm font-semibold border border-input bg-background hover:bg-muted disabled:opacity-60"
            :disabled="reviewStore.submitting"
            @click="onDelete"
        >
        삭제
        </button>

        <button
            type="button"
            class="h-9 rounded-md px-4 text-sm font-semibold text-white bg-primary hover:bg-primary/90 disabled:opacity-60"
            :disabled="reviewStore.submitting"
            @click="$emit('edit')"
        >
        수정
        </button>
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { useReviewStore } from "@/stores/reviewStore";

const props = defineProps({
  review: {
    type: Object,
    required: true,
  },
});

const emit = defineEmits(["edit", "deleted"]);

const reviewStore = useReviewStore();

const formattedDate = computed(() => {
  if (!props.review.reviewDate) return "";
  const date = new Date(props.review.reviewDate);
  return date.toLocaleDateString("ko-KR");
});

async function onDelete() {
  const reviewId = props.review?.reviewId;
  if (!reviewId) return;

  const ok = confirm("리뷰를 삭제할까요?");
  if (!ok) return;

  try {
    await reviewStore.deleteReview(reviewId);
    alert("리뷰가 삭제되었습니다.");
    emit("deleted");
  } catch (e) {
    const status = e?.response?.status;

    if (status === 401) {
      alert("로그인 후 이용 가능한 서비스입니다");
    } else if (status === 403) {
      alert("작성자만 수정/삭제할 수 있습니다");
    } else {
      alert(e?.response?.data?.message ?? "리뷰 삭제에 실패했습니다");
    }
  }
}
</script>
