<template>
  <div class="space-y-6">
    <ReviewCreate></ReviewCreate>
    <ReviewList :reviews="reviews"></ReviewList>
  </div>
</template>

<script setup>
import { ref } from "vue";
import ReviewCreate from "./ReviewCreate.vue";
import ReviewList from "./ReviewList.vue";

// defineProps({
//   reviews: { type: Array, default: () => [] },
// });

const reviews = ref([
  {
    reviewId: 1,
    nickname: "독서왕",
    reviewDate: "2025-12-17T10:23:11",
    reviewRating: 4.5,
    reviewContent: "생각보다 훨씬 몰입해서 읽었어요.\n삼국지를 처음 접하는 사람에게 정말 좋은 입문서라고 느꼈습니다.",
  },
  {
    reviewId: 2,
    nickname: "책벌레",
    reviewDate: "2025-12-15T21:10:03",
    reviewRating: 4.0,
    reviewContent: "내용 정리가 잘 되어 있어서 읽기 편했어요.\n다만 이미 삼국지를 알고 있다면 조금 단순하게 느껴질 수도 있을 것 같아요.",
  },
  {
    reviewId: 3,
    nickname: "초보독자",
    reviewDate: "2025-12-14T08:42:55",
    reviewRating: 5.0,
    reviewContent: "삼국지 이름만 알고 있었는데 이 책 덕분에 전체 흐름을 이해할 수 있었어요!\n강의 듣는 느낌이라 재밌었습니다.",
  },
]);
</script>

<style scoped></style>
