<template>
  <main class="mx-auto max-w-6xl px-4 py-6">

    <div v-if="loadingDetail" class="py-10 text-sm text-muted-foreground">
      상세 정보를 불러오는 중...
    </div>

    <!-- URL상에서 없는 ISBN 번호로 입력해서 화면이동했을 때 -->
    <div v-else-if="detailError === 'NOT_FOUND'" class="py-16 text-center">
      <p class="text-2xl font-medium font-semibold">도서를 찾을 수 없어요</p>
      <p class="mt-2 text-base text-muted-foreground">
        ISBN이 잘못되었거나 삭제된 도서일 수 있어요.
      </p>
      <div class="mt-6 flex justify-center gap-2">
        <button class="h-9 rounded-md border px-4 py-1.5" @click="$router.back()">이전으로</button>
        <button
          class="h-9 rounded-md bg-primary px-4 py-1.5 text-primary-foreground"
          @click="$router.push('/books')"
        >
        도서 목록으로
        </button>
      </div>
    </div>

    <div v-else-if="detailError" class="py-10 text-sm text-destructive">
      {{ detailError }}
    </div>

    <div v-else-if="book" class="grid gap-6 md:grid-cols-[240px_1fr]">
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

    <div v-else class="py-10 text-sm text-muted-foreground">
      도서 정보를 찾을 수 없습니다.
    </div>
  </main>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { useRoute } from "vue-router";
import { useBookStore } from "@/stores/bookStore";

import BookDetailInfo from "../components/bookDetail/BookDetailInfo.vue";
import BookSideCard from "@/components/bookDetail/BookSideCard.vue";
import ReviewPostTabs from "../components/bookDetail/ReviewPostTabs.vue";

import ReviewSection from "@/components/review/ReviewSection.vue";
import PostSection from "@/components/postInBookDetail/PostSection.vue";


const activeTab = ref("review");

const route = useRoute();
const bookStore = useBookStore();

const book = computed(()=> bookStore.bookDetail);

// BookSideCard에 전달
const isWished = computed(() => !!bookStore.bookDetail?.isWished);
const averageRating = computed(() => bookStore.bookDetail?.averageRating ?? 0.0);
const reviewCount = computed(() => bookStore.bookDetail?.reviewCount ?? 0);
const postCount = computed(() => bookStore.bookDetail?.postCount ?? 0);

const loadingDetail = computed(() => bookStore.loadingDetail);
const detailError = computed(() => bookStore.detailError);

const load = () => {
  const isbn = route.params.isbn;
  if (!isbn) return;
  bookStore.loadBookDetail(isbn);
};

onMounted(load);
// 같은 화면에서 isbn만 바뀌는 경우 대비
watch(() => route.params.isbn, load);

// 아직 wishAPI 연동 전
function toggleWish() {
  if(!bookStore.bookDetail) return;
  bookStore.bookDetail.isWished = !bookStore.bookDetail.isWished;
}
</script>
