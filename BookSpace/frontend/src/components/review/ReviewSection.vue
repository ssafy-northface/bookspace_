<template>
  <div class="space-y-6">
    <!-- 리뷰 작성 폼 -->
    <ReviewCreate :book-id="bookId" :isbn="isbn"></ReviewCreate>

    <!-- 리뷰 목록 -->
    <ReviewList :reviews="reviews"></ReviewList>
  </div>
</template>

<script setup>
import { computed, onMounted, watch } from "vue";
import { useReviewStore } from "@/stores/reviewStore";
import ReviewCreate from "./ReviewCreate.vue";
import ReviewList from "./ReviewList.vue";

const props = defineProps({
  bookId: { type: Number, required: true },
  isbn: { type: String, required: true },
});

const reviewStore = useReviewStore();
const reviews = computed(() => reviewStore.reviewsByBook);

const fetch = async () => {
  // 일단 sort는 latest로 고정
  await reviewStore.fetchReviewsByBookId(props.bookId, "latest");
};

onMounted(fetch);

// bookId가 바뀌는 경우(다른 상세로 이동) 대비
watch(
  () => props.bookId,
  async (newVal, oldVal) => {
    if (!newVal || newVal === oldVal) return;
    await fetch();
  }
);

</script>

<style scoped></style>
