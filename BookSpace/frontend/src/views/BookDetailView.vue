<template>
  <main class="mx-auto max-w-6xl px-4 py-6">
    <div class="grid gap-6 md:grid-cols-[240px_1fr]">
      <!-- LEFT -->
      <aside class="lg:sticky lg:top-6 h-fit">
        <BookSideCard :book="book" :is-wished="isWished" :average-rating="averageRating" :review-count="reviewCount" :post-count="postCount" @toggle-wish="toggleWish" />
      </aside>

      <!-- RIGHT -->
      <section class="space-y-10">
        <!-- 책 상세정보 -->
        <BookDetailInfo :book="book" />

        <!-- 리뷰 / 게시글 탭 -->
        <ReviewPostTabs v-model="activeTab" :review-count="reviewCount" :post-count="postCount">
          <!-- ReviewSection -->
          <template #review>
            <ReviewSection />
          </template>

          <!-- PostSection -->
          <template #post>
            <PostSection />
          </template>
        </ReviewPostTabs>
      </section>
    </div>
  </main>
</template>

<script setup>
import { ref } from "vue";
import BookDetailInfo from "../components/bookDetail/BookDetailInfo.vue";
import BookSideCard from "@/components/bookDetail/BookSideCard.vue";
import ReviewPostTabs from "../components/bookDetail/ReviewPostTabs.vue";

import ReviewSection from "@/components/review/ReviewSection.vue";
import PostSection from "@/components/postInBookDetail/PostSection.vue";

const activeTab = ref("review");

/**
 * 화면 확인용 더미 데이터
 * (BookSearchResponseDto 형태 그대로)
 */
const book = ref({
  title: "최소한의 삼국지 - 최태성의 삼국지 고전 특강",
  author: "최태성 (지은이), 이성원 (감수)",
  publisher: "프런트페이지",
  pubDate: "2025-11-18",
  isbn13: "9791193401583",
  cover: "https://image.aladin.co.kr/product/37773/21/cover200/k002033562_2.jpg",
  description: "인생의 필독서로 손꼽히는 ‘삼국지’를 한 권으로 정리한 입문서입니다.",
});

// UI용 더미 상태
const isWished = ref(false);
const averageRating = ref(4.8);
const reviewCount = ref(12);
const postCount = ref(3);

function toggleWish() {
  isWished.value = !isWished.value;
}
</script>
